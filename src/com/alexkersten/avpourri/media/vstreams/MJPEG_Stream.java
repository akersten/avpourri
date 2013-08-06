/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri.media.vstreams;

import com.alexkersten.avpourri.media.MediaContainer;
import com.alexkersten.avpourri.media.MediaFrame;
import com.alexkersten.avpourri.media.VideoFrame;
import com.alexkersten.avpourri.media.VideoStream;
import java.io.IOException;

/**
 * A general Stream object for decoding JFIF information - if a container has
 * frames encoded as a bunch of different JFIFs (like MJPG), this can be used to
 * turn them into our general Stream format. This should work out of the box
 * with the MSIAVI format by just passing the correct image offsets into the
 * cache - with other formats like MJPEG inside a .mov, the container object is
 * responsible for extracting the stream information from the container and
 * passing it on to this object.
 *
 * @author akersten
 */
public class MJPEG_Stream extends VideoStream {

    public MJPEG_Stream(MediaContainer container, String name) {
        super(container, name);
    }

    @Override
    public VideoFrame getNthFrame(int n) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean setStream(int frame) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MediaFrame getNextFrame() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
