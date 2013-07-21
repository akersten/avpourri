/*
 Project: AVpourri
 File: MJPG_Decoder.java (com.alexkersten.avpourri.media.decoders)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.decoders;

/**
 * From an MJPEG stream, we'll look for the individual JPEG headers and extract
 * the single images - we'll call each image a frame and that should be good
 * enough.
 *
 * @author Alex Kersten
 */
public class MJPG_Decoder implements StreamDecoder {

    @Override
    public VideoFrame getNthFrame(int n) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
