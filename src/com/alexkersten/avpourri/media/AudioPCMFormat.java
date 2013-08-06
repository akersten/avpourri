/*
 Project: AVpourri
 File: AudioPCMFormat.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

/**
 * Internally we represent all audio data with a giant PCM array. This will
 * determine the exact representation of that array in the AudioStream.
 *
 * @author Alex Kersten
 */
public enum AudioPCMFormat {

    S16LE_44100_Stereo(44100, 16, 2, true, false, "2Ch/S-PCM-16-LE/44100kHz");

    private String description;

    private int rate, depth, channels;

    private boolean signed, bigEndian;

    AudioPCMFormat(int rate, int depth, int channels, boolean signed,
                   boolean bigEndian, String description) {

        this.rate = rate;
        this.depth = depth;
        this.channels = channels;
        this.signed = signed;
        this.bigEndian = bigEndian;
        this.description = description;
    }

    /**
     * @return the rate
     */
    public int getRate() {
        return rate;
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @return the channels
     */
    public int getChannels() {
        return channels;
    }

    /**
     * @return the signed
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * @return the bigEndian
     */
    public boolean isBigEndian() {
        return bigEndian;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
