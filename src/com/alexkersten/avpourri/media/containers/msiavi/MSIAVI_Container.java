/*
 Project: AVpourri
 File: MSIAVI_Container.java (com.alexkersten.avpourri.media.extractors.msiavi)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.containers.msiavi;

import com.alexkersten.avpourri.media.AudioPCMFormat;
import com.alexkersten.avpourri.media.MediaContainer;
import com.alexkersten.avpourri.media.MediaContainerType;
import com.alexkersten.avpourri.media.MediaFile;
import com.alexkersten.avpourri.media.MediaStream;
import com.alexkersten.avpourri.media.astreams.PCM_Stream;
import com.alexkersten.avpourri.media.vstreams.MJPEG_Stream;
import com.alexkersten.avpourri.middleware.AVUtils;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Why is this file named MSIAVI container, why not just make a general AVI
 * extractor?
 *
 * Let me preface this with: the AVI file format is _very bad_, and I'm not sure
 * why _anyone_ would target it when developing software. Why not use a better
 * container format like MKV?
 *
 * A few of the things that are wrong with AVI:
 *
 * 1) The info header containing content offsets, which are _real useful_ for a
 * non-linear video editing program is at the _end_ of the file. Not, like, at
 * the beginning, where you'd expect a header to be. What in the world?
 *
 * 2) This header is actually completely optional - you might be stuck having to
 * scan through the entire file and find content streams yourself!
 *
 * 3) If the header _is_ present, there's multiple versions! One called
 * "AVIOLDINDEX" and one called "AVIMETAINDEX" in which they've apparently tried
 * to outdo themselves and create a header for... separate headers within the
 * data stream? I'll admit I didn't spend too much time looking at this because
 * thankfully the files we're working with use the "old" header... Maybe? It
 * looks like they also have an indx FOURCC at the beginning, which is
 * interesting... We'll see where it goes...
 *
 * 4) Within the (old) header, there's a field named `dwOffset` - exactly the
 * kind of thing we're looking for, an offset into the stream to find exactly
 * where content is. But it's not that simple! A quote from the MSDN
 * documentation: "dwOffset: Specifies the location of the data chunk in the
 * file. The value should be specified as an offset, in bytes, from the start of
 * the 'movi' list; however, in some AVI files it is given as an offset from the
 * start of the file." Great! No discernable way to tell which format any given
 * AVI uses, either!
 *
 * I could go on, but I won't. But I could. Anyway, that's why we're
 * specifically targeting AVI files produced by MSI Afterburner, and any others
 * that happen to work are just extra points. I'd prefer to spend as little time
 * as possible working with the AVI format. Ideally this will be general-ish but
 * no promises.
 *
 * @author Alex Kersten
 */
public class MSIAVI_Container extends MediaContainer {

    //The length that the header of the AVI file claims, in bytes, of the data
    //that comes after the first FOURCC and length declaration (this + 8 bytes
    //should be the size of the file in bytes).
    private long claimedLength;

    //The object representing the AVI global header.
    private MSIAVI_MainHeader header;

    //The streams that this container contains - populated after we initialize.
    //I'll set the generic to VideoStream because for all we know, something
    //other than MJPEG could be in here - although we're really writing this
    //as an MJPEG tool so...
    private final ArrayList<MediaStream> streams = new ArrayList<>();

    public MSIAVI_Container(MediaFile mediaFile) {
        super(mediaFile, MediaContainerType.MSIAVI);
    }

    private static void checkRead(int expected, int read) throws IOException {
        if (expected != read) {
            throw new IOException("Expected to read more bytes! E: "
                                  + expected + " R: " + read);
        }
    }
    //These are just scaffolding until we get a proper list reader set up...
    private int[][] tmpAudioStreamSampleLocations;

    private int tmpNastyInt = 0;

    private int tmpStartOfMovieList = 0;

    /**
     * There are a couple different formats an AVI could be in - the one we're
     * mainly targeting is the type produced by MSI Afterburner, which have an
     * index in the "old" index form at the end of the file. < Have to figure
     * out if this index is absolute offsets or offsets from the movi tag >
     *
     *
     * Ideally, the method would be as follows (for the future):
     *
     * - Read in the header data
     *
     * - Read lists as objects
     *
     * - Find relevant lists with video or audio header data
     *
     * - Use those lists to look into the file and build the stream
     *
     * THIS NEEDS TO BE ENTIRELY RE-WRITTEN ; THIS WAS WHIPPED UP QUICKLY TO
     * DECODE SOME AVI's for a friend.
     *
     * @return
     */
    @Override
    public boolean initialize() throws IOException, FileNotFoundException {
        if (!super.initialize()) {
            return false;
        }

        tmpAudioStreamSampleLocations = new int[2][];

        //We could probably use the NIO stuff here, but we're just reading in
        //the global header for now - we'll check the PaddingGranularity that
        //the AVI file claims later and use that. For now, just read in the
        //global header and check the RIFF header.

        //TODO: Checking this RIFF header is redundant - it's already performed
        //by the MediaContainerType enum's presence in MediaContainer's
        //initialize method.
        DataInputStream dis = new DataInputStream(
                new FileInputStream(getMediaFile().getFileOnDisk().toFile()));

        byte[] dwordbuff = new byte[4];

        checkRead(dis.read(dwordbuff, 0, dwordbuff.length), dwordbuff.length);
        if (!Arrays.equals(dwordbuff, MSIAVI_Constants.FOURCC_RIFF)) {
            System.err.println("RIFF FOURCC not present");
            return false;
        }


        //Next is the length of the file in bytes, little-endian. This is minus
        //the first 8 bytes (file length and FOURCC header).
        for (int i = 0; i < dwordbuff.length; i++) {
            claimedLength += dis.readUnsignedByte() << (8 * i);
        }

        //The next four bytes are "AVI LIST", which contains both the global 
        //header and the list of stream headers... We'll make a buffer
        //containing both... Skip those though
        dis.skip(dwordbuff.length * 2);

        int firstListLength = 0;
        for (int i = 0; i < dwordbuff.length; i++) {
            firstListLength += dis.readUnsignedByte() << (8 * i);
        }

        //NB: Java is stupid and doesn't have unsigned types, so when reading
        //multiple bytes out of this buffer (like when reconstructing a little-
        //endian number, for example), you'll need to &0xFF them and cast to
        //int in order to get their unsigned representation.
        byte[] hdrlBuffer = new byte[firstListLength];
        checkRead(dis.read(hdrlBuffer, 0, hdrlBuffer.length),
                  hdrlBuffer.length);

        //Within this byte array now is both the AVI global header and multiple
        //lists for each stream containing stream info.
        //We'll read the global header out first and create an object for it.

        //The first eight bytes are "hdrlavih". Don't even bother verifying
        //because we'll encounter an error down the line if it's wrong.
        int into = dwordbuff.length * 2;

        //The next four are the length of the avi global header section that we
        //have left to read (excluding these four bytes). We don't really need
        //to keep track of this since the global header is all fixed-length
        //anyway...
        int hdrlHeaderLength = 0;
        for (int i = 0; i < dwordbuff.length; i++) {
            hdrlHeaderLength += (hdrlBuffer[into + i] & 0xFF) << (8 * i);
        }
        into += dwordbuff.length;

        header = new MSIAVI_MainHeader();

        //Read the properties out in order.
        for (int i = 0; i < dwordbuff.length; i++) {
            header.dwMicroSecPerFrame += (hdrlBuffer[into + i] & 0xFF) << (8 * i);
            header.dwMaxBytesPerSec += (hdrlBuffer[into + i + dwordbuff.length] & 0xFF) << (8 * i);
            header.dwPaddingGranularity += (hdrlBuffer[into + i + dwordbuff.length * 2] & 0xFF) << (8 * i);
            header.dwFlags += (hdrlBuffer[into + i + dwordbuff.length * 3] & 0xFF) << (8 * i);
            header.dwTotalFrames += (hdrlBuffer[into + i + dwordbuff.length * 4] & 0xFF) << (8 * i);
            header.dwInitialFrames += (hdrlBuffer[into + i + dwordbuff.length * 5] & 0xFF) << (8 * i);
            header.dwStreams += (hdrlBuffer[into + i + dwordbuff.length * 6] & 0xFF) << (8 * i);
            header.dwSuggestedBufferSize += (hdrlBuffer[into + i + dwordbuff.length * 7] & 0xFF) << (8 * i);
            header.dwWidth += (hdrlBuffer[into + i + dwordbuff.length * 8] & 0xFF) << (8 * i);
            header.dwHeight += (hdrlBuffer[into + i + dwordbuff.length * 9] & 0xFF) << (8 * i);
        }
        into += dwordbuff.length * 10;

        //16 reserved bytes
        into += dwordbuff.length * 4;

        System.out.println("First list length: " + firstListLength);


        //The next thing we'll see are LISTS containing stream info.
        //For each one, we'll see how long it is and count how many bytes we
        //pass until we're done.
        //We don't need to offset into the list - into already measures from
        //the beginning of the list.
        while (into < firstListLength) {
            System.out.println("into: " + into);
            //The size of each stream reported in the file doesn't count the
            //"LIST" fourCC, so skip it. It also doesn't count the length item.
            into += dwordbuff.length;

            int thisListLength = 0;
            for (int i = 0; i < dwordbuff.length; i++) {
                thisListLength += (hdrlBuffer[into + i] & 0xFF) << (8 * i);
            }

            into += dwordbuff.length;

            //We're now looking at the strlstrh header - we know that already though
            //so skip it.
            into += dwordbuff.length * 2;

            //Now, we're looking at a thing called <Stream header>
            //These are defined here: http://msdn.microsoft.com/en-us/library/windows/desktop/dd318183%28v=vs.85%29.aspx
            //This data doesnt' really let us know anything important though;
            //most of it is about starting offsets and audio offsets - if we
            //were a really professional piece of software, we'd care, but for
            //now we just assume all clips start at 0seconds with 0 audio offset
            //and people can scale the clips themselves if it's wrong. For now,
            //just read how long the information is, and skip it.

            //Actually, there is something we care about - this tells us which
            //kind of stream it is in the header, so we can select the correct
            //template later.

            //So, check the format - it's either 'vids' or 'auds' - so we look
            //in at the first member of this structure and compare the first 
            //byte against 'v'; in a properly formatted AVI (which is what we're
            //interested in, this is not a recovery tool), this will be enough
            //info to determine the stream format.

            //QUIRK: If it's not one of these, it's likely we found an extra random 
            //list, and we don't knwo what to do with it for now - since we
            //happen to have one of these in the AVI's produced by msi afterburner,
            //and it allways occurs after the regular lists, just say we're done.

            //However, this stream header could also be "mids" or "txts", for a
            //MIDI or text stream.
            //TODO: Skip those streams - we'll need to advance the `into`
            //variable. Putting this off because MSI afterburner won't generate
            //those kind of streams and you'll rarely find them in the wild.

            int thisStreamHeaderLength = 0;


            for (int i = 0; i < dwordbuff.length; i++) {
                thisStreamHeaderLength += (hdrlBuffer[into + i] & 0xFF) << (8 * i);

            }

            //TEMPORARY SCAFFOLDING FOR OUR BAD ARRAY
            int thisNumSamples = 0;

            for (int i = 0; i < dwordbuff.length; i++) {
                thisNumSamples += (hdrlBuffer[into + i + 9 * 4] & 0xFF) << (8 * i);
            }

            System.out.println("num discrete samples: " + ((char) hdrlBuffer[into + 4]) + thisNumSamples);


            //TEMPORRARY BAD  - look only for the audio sample lengths and use
            //those to build our array which we'll find the samples with later..

            //SCRATCH THAT - number of samples is not equal to number of sample locations!
            //We'lll truncate this array later...
            if (((char) hdrlBuffer[into + 4]) == 'a') {
//                tmpAudioStreamSampleLocations[tmpNastyInt] = new int[thisNumSamples];
                tmpAudioStreamSampleLocations[tmpNastyInt] = new int[thisNumSamples];
                tmpNastyInt++;
            }

            into += dwordbuff.length;

            //Now looking at the fccType
            boolean thisStreamIsVideo = (hdrlBuffer[into] == 'v');
            if (!thisStreamIsVideo) {
                //Check to make sure it's at least audio
                if (hdrlBuffer[into] != 'a') {
                    //It's that `odml dmlh` thing and we're just gonna skip it
                    into = firstListLength;
                    continue;
                }
            }

            //TODO: we might want to look at the dwSuggestedBufferSize for these
            //streams in order to use for playback. we'll see.

            //Skip the rest of the stream header, it's not useful
            into += thisStreamHeaderLength;

            System.out.println("into: " + into);
            //Now we examine the <Stream format> section of a stream list. This
            //is actually not super important for our purposes.
            //Documented here:
            //Video: http://msdn.microsoft.com/en-us/library/windows/desktop/dd318229%28v=vs.85%29.aspx
            //Audio: http://msdn.microsoft.com/en-us/library/windows/desktop/dd390970%28v=vs.85%29.aspx

            //For now, skip the fourCC
            into += dwordbuff.length;

            //Find the size of the format header and skip over it...
            int thisStreamFormatLength = 0;

            for (int i = 0; i < dwordbuff.length; i++) {
                thisStreamFormatLength +=
                (hdrlBuffer[into + i] & 0xFF) << (8 * i);
            }

            //Skip it and the length dword.
            into += thisStreamFormatLength + dwordbuff.length;
            System.out.println("into: " + into);
            //This is where it gets a little weird. While I'm doing this whole
            //thing, I'm cross-referencing the AVI documentation with a sample
            //AVI created with MSI afterburner. In the official documentation,
            //there's no mention of this - but the next fourcc we're looking at
            //is 'indx', which is apparently a newer version of indexing.

            //Looking at the supposed documentation for this, it doesn't say
            //anything about offsets into the file, so not very interesting to
            //us - documentation I think: http://msdn.microsoft.com/en-us/library/windows/desktop/ff625868%28v=vs.85%29.aspx
            //If we look at the length of these though, we can skip right to
            //the next stream if it's there, which begins as another 'LIST'
            //and we continue this until we're done with the global header.

            //Check if this fourcc is a list - begins with 'L' - or something
            //else (the index) with a length.
            //BUG: If more than one optional header is present (like strd followed by strn)
            //this will break. Luckily, again, this isn't what we're programming for.
            if (hdrlBuffer[into] == 'L') {
                System.out.println("Did not find an index");
                continue;
            } else {

                into += dwordbuff.length;

                int thisUselessIndexLength = 0;
                for (int i = 0; i < dwordbuff.length; i++) {
                    thisUselessIndexLength +=
                    (hdrlBuffer[into + i] & 0xFF) << (8 * i);
                }


                into += thisUselessIndexLength + dwordbuff.length;
            }

            System.out.println("into: " + into);
            System.out.println("Added a stream, continuing? " + firstListLength);
            if (thisStreamIsVideo) {
                getStreams().add(new MJPEG_Stream(this, "video"));
            } else {
                //It's an audio stream, find where it's split across the entire
                //file...

                System.out.println("Found an audio stream, will demux later");

                //Demuxing note: There's a hell of a lot of wb01, wb02 interlaced
                //within the file - need to find out how long each sample is...
                //Spoilers; it's 0x16F8 long each = 5880 bytes = 2940 bytes per
                //channel = each sample is 1/15 a second

            }

        } //End of Stream Header/Stream Format discovery in the global header.



        //Skip to end index of audio streams and demux.
        dis.close();

        int skipped = 0;
        dis = new DataInputStream(
                new FileInputStream(getMediaFile().getFileOnDisk().toFile()));

        //RIFF, length of whole file, AVI LIST, skip all that
        dis.skip(16);

        skipped += 16;
        //Read how long the 'hdrl' list is, skip it
        int length = AVUtils.readLittleEndianInt(dis);
        dis.skip(length);

        //Skip JUNK headers and LISTs
        int test = dis.readInt();

        skipped += length + 8;

        //BUG: if no idx1 found, will error out. whole thing needs to be replaced
        //but this will work for an MVP
        while (test == 0x4A554E4B || test == 0x4C495354) {
            int len = AVUtils.readLittleEndianInt(dis);
            dis.skip(len);
            test = dis.readInt();

            skipped += len + 8;

            if (test == 0x4C495354) {
                //Just say the last LIST we find has a movi in it.. very bad
                //temporary magic numbers, but we need an MVP extractor
                tmpStartOfMovieList = skipped + 4;
            }

            //If an index, we're ready for the next step
            if (test == 0x69647831) {
                break;
            }
        }




        System.out.println("STart of movie list: " + tmpStartOfMovieList);


        //We're looking at the index, find any wubs.
        int wublength = AVUtils.readLittleEndianInt(dis);
        int sampleI = 0;
        int sampleJ = 0;

        for (int i = 0; i < wublength / 4; i += 1) {
            dis.read(dwordbuff, 0, dwordbuff.length);
            if (dwordbuff[2] != 'w' || dwordbuff[3] != 'b') {
                continue;
            }


            int stream = Integer.parseInt(
                    ((char) dwordbuff[0]) + "" + ((char) dwordbuff[1]));
            //Skip the dwFlags
            dis.skip(4);

            //
            int offset = AVUtils.readLittleEndianInt(dis);

            System.out.println("Wub found offset " + offset + " stream " + stream + " i = " + sampleI);

            //Load htis offset into the array
            tmpAudioStreamSampleLocations[stream - 1][stream == 1
                                                      ? sampleI++ : sampleJ++] = offset + tmpStartOfMovieList;
            System.out.println("" + sampleI + ":" + sampleJ);
        }

        //Now we know where all the samples are, build from them!
        int tmpAudioStreamSampleLocationsTrunc[][] = new int[2][];

        tmpAudioStreamSampleLocationsTrunc[0] = new int[sampleI];
        tmpAudioStreamSampleLocationsTrunc[1] = new int[sampleJ];

        System.arraycopy(tmpAudioStreamSampleLocations[0], 0, tmpAudioStreamSampleLocationsTrunc[0], 0, sampleI);
        System.arraycopy(tmpAudioStreamSampleLocations[1], 0, tmpAudioStreamSampleLocationsTrunc[1], 0, sampleJ);


        //Open the file for random access
        dis.close();



        FileChannel fc = FileChannel.open(getMediaFile().getFileOnDisk(), StandardOpenOption.READ, StandardOpenOption.WRITE);
        ByteBuffer four = ByteBuffer.allocate(4);

        for (int stream = 0; stream < tmpAudioStreamSampleLocationsTrunc.length; stream++) {
            byte datum[][] = new byte[tmpAudioStreamSampleLocationsTrunc[stream].length][];

            for (int sample = 0; sample < tmpAudioStreamSampleLocationsTrunc[stream].length; sample++) {

                System.out.println("ss " + stream + " " + sample + "= offset @ " + tmpAudioStreamSampleLocationsTrunc[stream][sample]);

                //First four bytes are dumb, but do read the length...
                fc.read(four, tmpAudioStreamSampleLocationsTrunc[stream][sample] + 4);

                //Make a length out of that and a new buffer, then dump that into
                //the pcm stream
                //First need little-endian reading of that length...
                four.flip();
                int len = 0;
                int i = 0;
                while (four.hasRemaining()) {

                    len += (four.get() & 0xFF) << (8 * i);
                    System.out.println("LEN: " + len);
                    i++;
                }

                System.out.println("Sample length, stream " + stream + " : " + len);
                four.flip();

                //copy in and prepare, each sample point gets a thing in datum
                datum[sample] = new byte[len];


                ByteBuffer datumBuffer = ByteBuffer.allocate(len);
                fc.read(datumBuffer, tmpAudioStreamSampleLocationsTrunc[stream][sample]);
                datumBuffer.flip();

                if (!datumBuffer.hasArray()) {
                    throw new RuntimeException("NO BACKING ARRAY");
                }

                for (int ii = 0; ii < len; ii++) {
                    datum[sample][ii] = datumBuffer.array()[ii];
                }


            }

            //combine all the datums into a stream...
            //This will break if the samples aren't all the same length per stream.....
            byte realDatum[] = new byte[datum[0].length * tmpAudioStreamSampleLocationsTrunc[stream].length];
            for (int i = 0; i < realDatum.length; i++) {
                realDatum[i] = datum[i / datum[0].length][i % datum[0].length];
            }

            //add new stream
            PCM_Stream thisStream = new PCM_Stream(this, "audio " + stream, AudioPCMFormat.S16LE_44100_Stereo, realDatum);
            streams.add(thisStream);
        }
        return true;
    }

    /**
     * The size of the actual file should be the claimed length plus the eight
     * bytes that it does not account for.
     *
     * @return The size of the file.
     */
    @Override
    public long getContainerSize() {
        return claimedLength + 8;
    }

    @Override
    public ArrayList<MediaStream> getStreams() {
        return streams;
    }

    //
    //AVI specific properties (AVI global header)
    //
    public long getdwMicroSecPerFrame() {
        return header.dwMicroSecPerFrame;
    }

    public long getdwMaxBytesPerSec() {
        return header.dwMaxBytesPerSec;
    }

    public long getdwPaddingGranularity() {
        return header.dwPaddingGranularity;
    }

    public long getdwFlags() {
        return header.dwFlags;
    }

    public long getdwTotalFrames() {
        return header.dwTotalFrames;
    }

    public long getdwInitialFrames() {
        return header.dwInitialFrames;
    }

    public long getdwStreams() {
        return header.dwStreams;
    }

    public long getdwSuggestedBufferSize() {
        return header.dwSuggestedBufferSize;
    }

    public long getdwWidth() {
        return header.dwWidth;
    }

    public long getdwHeight() {
        return header.dwHeight;
    }
}

class MSIAVI_Constants {

    static final byte[] FOURCC_RIFF = {'R', 'I', 'F', 'F'};

}