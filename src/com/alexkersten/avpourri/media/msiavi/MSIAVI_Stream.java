/*
 Project: AVpourri
 File: MSIAVI_Stream.java (com.alexkersten.avpourri.media.msiavi)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.msiavi;

import com.alexkersten.avpourri.media.MediaContainer;
import com.alexkersten.avpourri.media.MediaFrame;
import com.alexkersten.avpourri.media.MediaStream;
import java.io.IOException;

/**
 * A stream object for keeping track of AVI-specific stream data.
 *
 * @author Alex Kersten
 */
public class MSIAVI_Stream extends MediaStream {

    public MSIAVI_Stream(MediaContainer container, String name) {
        super(container, name);
    }

    @Override
    public MediaFrame getNthFrame(int n) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean startStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MediaFrame getNextFrame() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
