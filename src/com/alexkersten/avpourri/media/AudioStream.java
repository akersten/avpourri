/*
 Project: AVpourri
 File: AudioStream.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * An AudioStream is comprised of a bunch of PCM data. The format of PCM data is
 * a uni-dimensional array with interleaved channels.
 *
 * We'll store this as a byte array (because that's what the Java7 audio layer
 * expects) and cast it any which way if we need it somewhere else.
 *
 * The exact details of this byte array are described by the AudioPCMFormat
 * enum.
 *
 * @author Alex Kersten
 */
public abstract class AudioStream extends MediaStream {

    //Stores information about the exact PCM representation.
    private AudioPCMFormat format;

    //The raw PCM data for this stream.
    private final byte[] data;

    //The current sample that we're looking at.
    private int streamPosition = 0;

    public AudioStream(MediaContainer container, String title,
                       AudioPCMFormat format, byte[] data) {

        super(container, title);
        this.format = format;
        this.data = data;
    }

    /**
     * For these, we can provide an abstract way of starting the stream and
     * returning the data, since most audio has some sense of a start and end...
     *
     * @return
     * @throws IOException
     */
    @Override
    public boolean setStream(int frame) throws IOException {
        streamPosition = frame;
        return true;
    }

    /**
     * Play and don't return until done playing. DEBUG ONLY, remove this...
     *
     * Whatever, it only plays the left channel for now, will muck around with
     * PCM directy to the sound interface later...
     */
    public void playSync() {
        try {
            AudioFormat fmt = new AudioFormat(format.getRate(), format.getDepth(), format.getChannels(), format.isSigned(), format.isBigEndian());
            SourceDataLine line = AudioSystem.getSourceDataLine(fmt);
            line.open(fmt);
            line.start();

            line.write(data, 0, data.length);

            line.drain();
            line.close();
            System.out.println("Played!~");


        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
}
