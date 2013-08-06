/*
 Project: AVpourri
 File: AVUtils.java (com.alexkersten.avpourri.middleware)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.middleware;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * These are pretty self-explanatory, so commenting in this file will be sparse.
 * One note about the readLittleEndian* methods - if the value you read in is
 * too big (like you read in an unsigned value that's greater than the signed
 * maximum) then you'll have to manually bitmask out what you want from it - it
 * was either this, or changing the return types of these methods to one higher
 * than their corresponding value, but I thought that was silly.
 *
 * @author Alex Kersten
 */
public abstract class AVUtils {

    public static short readLittleEndianShort(DataInputStream dis) throws IOException {
        short rtn = 0;

        for (int i = 0; i < 2; i++) {
            rtn += dis.readUnsignedByte() << (8 * i);
        }

        return rtn;
    }

    public static int readLittleEndianInt(DataInputStream dis) throws IOException {
        int rtn = 0;

        for (int i = 0; i < 4; i++) {
            rtn += dis.readUnsignedByte() << (8 * i);
        }

        return rtn;
    }
}
