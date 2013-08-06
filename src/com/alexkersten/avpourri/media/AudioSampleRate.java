/*
 Project: AVpourri
 File: AudioSampleRate.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

/**
 *
 * @author Alex Kersten
 */
public enum AudioSampleRate {

    U44100(44100);

    private int rate;

    AudioSampleRate(int hz) {
        this.rate = hz;
    }

    /**
     * @return the rate
     */
    public int getRate() {
        return rate;
    }
}
