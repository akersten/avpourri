/*
 Project: AVpourri
 File: AVIRIFF_MJPEG_Extractor.java (com.alexkersten.avpourri.media.extractors)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.extractors.msiavi;

import com.alexkersten.avpourri.media.extractors.ContainerExtractor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Unfortunately, the original MJPG specification (before MJPEG2000) doesn't
 * exist, as far as I can tell - so there are many different ways of encoding
 * it. Luckily, my usage case is very small: decode MJPEG AVI files produced by
 * MSI Afterburner.
 *
 * After poking around with a hex editor and `strings`, I've decided that the
 * MJPEG stream in an AVI produced by MSI Afterburner is pretty straightforward.
 * Basically, there are a few parts to the file:
 *
 * 1) The AVI header junk, just make sure it has the RIFF FourCC at the
 * beginning and it should be valid.
 *
 * 2) An MJPG FourCC denotes one or more MJPEG streams - for now, we'll just
 * assume that everything has one stream, although this could get messy if
 * someone feeds us something with more. I haven't looked at any files that _do_
 * have more than one stream, so it'll be interesting to see if the frames get
 * interleaved/mixed out of order or if the streams are sequential. We'll have
 * to find one with multiple streams and then open it up with the Frame
 * Extractor tool (which I've also written as part of AVpourri) to find out.
 *
 * 3) Many, many, many JPEG images denoted by the JFIF FourCC - find each one
 * and extract it, and hopefully it's a straight-up JPEG that we can read,
 *
 * @author Alex Kersten
 */
public class AVIRIFF_MJPEG_Extractor extends ContainerExtractor {

    //The size of the buffer to use for initial parsing of the container file.
    private static final int PARSE_BUFFER_SIZE = 1024;

    //For now we'll just support one stream. This is all the extraction of
    //MJPEG from AVI requires is a pointer into the file for where the stream
    //starts - from there we can parse out the individual JFIFs.
    private long streamStartPosition = -1;

    public AVIRIFF_MJPEG_Extractor(Path p) {
        super(p);
    }

    /**
     * The method for checking this is mentioned in the description for this
     * class, but to be brief, we'll look for the following strings: RIFF, MJPG,
     * JFIF (a lot of them).
     *
     * Also, builds the JFIF lookup table... make sure the container is checked
     * as being valid before streaming from it!
     *
     * @return Whether this container has an MJPEG stream worth extracting.
     */
    @Override
    public boolean setExtractionParametersAndValidate() throws IOException {
        FileChannel fc = FileChannel.open(getFileOnDisk());
        ByteBuffer smallBuff = ByteBuffer.allocate(PARSE_BUFFER_SIZE);

        //We'll read the file for a while and make sure we see RIFF and MJPG
        //early on - keep reading until we see a few JFIF's and we'll be golden.

        //Initial examination of the files produced by MSI Afterburner seemed to
        //indicate that the MJPG string happens early on (within the first
        //hundred or so bytes of the file) and the JFIF sequences began around
        //the 8200th byte of the file).
        boolean seenRIFF = false, seenMJPG = false;

        //Temporary to keep track of the position in the file for first offset
        //reading, need to figure out why reading fc.position() gives us an
        //IO error...
        int xx = 0;

        while (fc.read(smallBuff) != -1) {
            //The first thing we should check is the RIFF, so if it hasn't been
            //seen already, it must be the first four bytes - otherwise return
            //false.
            smallBuff.flip();
            if (!seenRIFF) {
                if (smallBuff.getInt(0) != 0x52494646) {
                    fc.close();
                    return false;
                }
                seenRIFF = true;
            }

            //Alright... Look and see if this buffer has any MJPG or JFIF items
            //in it...
            //We could use KMP here for string searching but it's not worth it..
            //BUG: I suppose this will break if the MJPG stream tag is split
            //between the buffer (like it's at position 1022 or something) but
            //we're banking on the fact that that would be a reaaally big AVI
            //prologue and won't happen...
            for (int i = 0; i < PARSE_BUFFER_SIZE - 4; i++) {
                if (smallBuff.getInt(i) == 0x4D4A5047) {
                    seenMJPG = true;
                }

                //SOI (2 bytes) + something that isn't specified as an SOI but
                //looks like it's necessary to specify in the header because the
                //2-byte "SOI" sometimes shows up in the compressed data;
                //probably just a documentation versionitis issue but it's
                //present in all the JFIF headers in the files I've seen.
                if (smallBuff.getInt(i) == 0xFFD8FFE0) {
                    fc.close();
                    //We're valid if we saw this and an earlier MJPG FourCC.
                    //Set the start of the stream to this location.
                    streamStartPosition = xx * PARSE_BUFFER_SIZE + i;
                    return seenMJPG;
                }
            }

            smallBuff.clear();
            xx++;
        }

        fc.close();
        return false;
    }

    /**
     * @return the streamStartPosition
     */
    public long getStreamStartPosition() {
        return streamStartPosition;
    }

    @Override
    public int getStreamCount() {
        //TODO: Support for multiple video streams? Somewhere I read that
        //multiple video streams wasn't supported in MJPEG in RIFF AVI but that
        //could be wrong... Will have to look into how it would be implemented.
        return 1;
    }
}
