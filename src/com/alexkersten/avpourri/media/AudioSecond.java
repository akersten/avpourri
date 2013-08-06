/*
 Project: AVpourri
 File: AudioSecond.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Because we don't want to create a frame object for every single sample in the
 * stream (duh), we'll have per-second containers that encapsulate the
 * underlying PCM data in one-second intervals based on the sample rate.
 *
 * @author Alex Kersten
 */
public class AudioSecond extends MediaFrame {

    //The maximum value before clipping (2^depth)
    private final int ceil;

    //How many channels are there?
    private final byte channels;

    //How many samples per second?
    private final int sampleRate;

    //The raw data for this second of audio.
    public final int[][] data;

    public AudioSecond(AudioChannel channels, AudioDepth depth, AudioSampleRate rate,
                       int[][] data) {
        this.channels = channels.getNum();
        this.sampleRate = rate.getRate();
        this.ceil = depth.getCeil();

        this.data = data;
        if (data.length != this.channels) {
            System.err.println("Data/channels mismatch");
        }
        if (data[0].length != this.sampleRate) {
            System.err.println("Data/rate mismatch");
        }
    }

    /**
     * Plays this frame.
     */
    public void playAsync() {
    }

    /**
     * Play and don't return until done playing. DEBUG ONLY, remove this...
     *
     * Whatever, it only plays the left channel for now, will muck around with
     * PCM directy to the sound interface later...
     */
    public void playSync() {
        try {
            AudioFormat fmt = new AudioFormat(this.sampleRate, 16, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(fmt);
            line.open(fmt);
            line.start();

            line.write(toByteArray(data[0]), 0, data[0].length);
            
            line.drain();
            line.close();
            System.out.println("Played!~");


        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private byte[] toByteArray(int[] x) {
        byte[] b = new byte[x.length];
        for (int i = 0; i < x.length; i++) {
            b[i] = (byte) x[i];
        }

        return b;
    }
}