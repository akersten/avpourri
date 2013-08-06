/*
 Project: AVpourri
 File: AudioChannel.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

/**
 *
 * @author Alex
 */
public enum AudioChannel {

    STEREO((byte) 2);

    private byte num;

    AudioChannel(byte num) {
        this.num = num;
    }

    /**
     * @return the num
     */
    public byte getNum() {
        return num;
    }
}
