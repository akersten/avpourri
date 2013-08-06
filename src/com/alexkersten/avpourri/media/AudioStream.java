/*
 Project: AVpourri
 File: AudioStream.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

import java.io.IOException;

/**
 * An AudioStream is comprised of many AudioSeconds.
 *
 * @author Alex Kersten
 */
public abstract class AudioStream extends MediaStream {

    private final AudioChannel channels;

    private final AudioDepth depth;

    private final AudioSampleRate rate;

    public AudioStream(MediaContainer container, String title,
                       AudioChannel channels, AudioDepth depth,
                       AudioSampleRate rate) {

        super(container, title);

        this.channels = channels;
        this.depth = depth;
        this.rate = rate;


    }

    @Override
    public abstract AudioSecond getNthFrame(int n);

    @Override
    public abstract boolean startStream() throws IOException;

    @Override
    public abstract AudioSecond getNextFrame() throws IOException;

    /**
     * @return the channels
     */
    public AudioChannel getChannels() {
        return channels;
    }

    /**
     * @return the depth
     */
    public AudioDepth getDepth() {
        return depth;
    }

    /**
     * @return the rate
     */
    public AudioSampleRate getRate() {
        return rate;
    }
}
