/*
 Project: AVpourri
 File: WAV_Container.java (com.alexkersten.avpourri.media.containers.msiavi)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.containers.msiavi;

import com.alexkersten.avpourri.media.AudioPCMFormat;
import com.alexkersten.avpourri.media.AudioStream;
import com.alexkersten.avpourri.media.MediaContainer;
import com.alexkersten.avpourri.media.MediaContainerType;
import com.alexkersten.avpourri.media.MediaFile;
import com.alexkersten.avpourri.media.astreams.PCM_Stream;
import com.alexkersten.avpourri.middleware.AVUtils;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Alex Kersten
 */
public class WAV_Container extends MediaContainer {

    private WAV_Header header;

    private int length;

    private ArrayList<AudioStream> streams = new ArrayList<>();

    public WAV_Container(MediaFile file) {
        super(file, MediaContainerType.WAV);
    }

    @Override
    public boolean initialize() throws IOException {
        DataInputStream dis = new DataInputStream(
                new FileInputStream(getMediaFile().getFileOnDisk().toFile()));

        //The FOURCC has already been verified as RIFF - read in the file size
        //and save it, then create the WAV main header and check its values.
        dis.skip(4);

        byte[] dwordbuff = new byte[4];

        for (int i = 0; i < dwordbuff.length; i++) {
            length += dis.readUnsignedByte() << (8 * i);
        }

        //Should be aligned to the WAV Header now.
        header = new WAV_Header();
        if (!header.createFromStream(dis)) {
            return false;
        }

        //We're now looking at a data stream. Find its length and read in the
        //PCM data.
        //TODO: Make sure this length coincides with the header length we read
        //earlier.
        int test = dis.readInt();
        if (test != 0x64617461) {
            System.err.println("Data field header missing (" + test + ")");
            return false;
        }

        int streamLength = AVUtils.readLittleEndianInt(dis);

        //That's the length of our stream - depending on how many channels we
        //have and bit depth determines how we'll read it in. Veryify that
        //they're PCM streams though, cuz that's all we'll ever support in WAV
        //for the forseeable future.
        if (header.wAudioFormat != 1) {
            System.err.println("Not PCM audio!");
            return false;
        }

        //Create the PCM streams with the set bit depth and sampling rate.
        if (header.wNumChannels < 1) {
            System.err.println("No channels.");
            return false;
        }

        //Only one stream in a WAV file (although it has multiple channels, for L, R, etc.)
        //TODO: We'll see how we want to handle different channels once the code
        //evolves to that point.
        //TODO: do a reverse enum map to map the correct channels etc to the right
        //enums, for nwo just assume 16-bit 44100khz stereo


        //TODO: support more than stereo
        //division by 4 because lenght is in bytes and we want two streams of shorts (contained in ints)\

        //TODO: TEMPORARY - we're reading it in as bytes instead, because Java7 Line does byte reading only...
        byte streamData[] = new byte[streamLength];

        dis.read(streamData, 0, streamLength);

        System.out.println("Read " + streamLength + " bytes");

        //TODO: support for different containers
        PCM_Stream pcm = new PCM_Stream(this, "WAV PCM", AudioPCMFormat.S16LE_44100_Stereo, streamData);

        streams.add(pcm);

        return true;
    }

    @Override
    public long getContainerSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<AudioStream> getStreams() {
        return streams;
    }
}