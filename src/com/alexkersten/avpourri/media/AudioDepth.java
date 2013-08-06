/*
 Project: AVpourri
 File: AudioDepth.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

/**
 *
 * @author Alex Kersten
 */
public enum AudioDepth {

    U16((byte) 16);

    private byte depth;

    private int ceil;

    AudioDepth(byte depth) {
        this.depth = depth;
        this.ceil = (int) Math.pow(2, depth);
    }

    /**
     * @return the depth
     */
    public byte getDepth() {
        return depth;
    }

    /**
     * @return the ceil
     */
    public int getCeil() {
        return ceil;
    }
}
