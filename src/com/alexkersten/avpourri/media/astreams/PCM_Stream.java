/*
 Project: AVpourri
 File: PCM_Stream.java (com.alexkersten.avpourri.media.astreams)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.astreams;

import com.alexkersten.avpourri.media.AudioChannel;
import com.alexkersten.avpourri.media.AudioDepth;
import com.alexkersten.avpourri.media.AudioSampleRate;
import com.alexkersten.avpourri.media.AudioSecond;
import com.alexkersten.avpourri.media.AudioStream;
import com.alexkersten.avpourri.media.MediaContainer;
import java.io.IOException;

/**
 * This will be fairly straightforward since the Audio subsystem expects PCM
 * data anyway.
 *
 * @author Alex Kersten
 */
public class PCM_Stream extends AudioStream {

    //2D array of channels/samples (inner array is PCM data of a single channel,
    //sampled at the given sampling rate).
    private final int streamData[][];

    //In stream mode, where we are
    private int streamLocation = 0;

    public PCM_Stream(MediaContainer container, String title,
                      AudioChannel channels, AudioDepth depth,
                      AudioSampleRate rate, int streamData[][]) {

        super(container, title, channels, depth, rate);
        this.streamData = streamData;
    }

    @Override
    public AudioSecond getNthFrame(int n) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean startStream() throws IOException {
        streamLocation = 0;
        return true;
    }

    @Override
    public AudioSecond getNextFrame() throws IOException {
        int secondData[][] = new int[this.getChannels().getNum()][this.getRate().getRate()];

        //TODO: More than stereo support - copy the stream data into the second -- also, this is pretty slow -- maybe ditch AudioSecond objects and just play from the stream? Not sure why we have them in the first place.
        System.arraycopy(streamData[0], streamLocation * this.getRate().getRate(), secondData[0], 0, this.getRate().getRate());
        System.arraycopy(streamData[1], streamLocation * this.getRate().getRate(), secondData[1], 0, this.getRate().getRate());

        streamLocation++;
        return new AudioSecond(this.getChannels(), this.getDepth(), this.getRate(), secondData);

    }
}
