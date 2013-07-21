/*
 Project: AVpourri
 File: VideoFrame.java (com.alexkersten.avpourri.media.decoders)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.decoders;

import java.awt.image.BufferedImage;

/**
 * As we walk down the ladder of abstraction, we need to solidify our internal
 * data representation - video frames need to be able to be quickly inserted
 * into some format that the Java runtime recognizes (like shoving it into a
 * VolatileImage for presentation). If the stream that generates these frames
 * isn't a nice format which has pre-separated video frames (like MJPEG), it's
 * the onus of the decoder to provide an efficient implementation to grab frames
 * out of the stream - or cache them as necessary. So just a note for the
 * future: that work goes in the stream decoders, not here - here the data
 * representation should be known.
 *
 * @author Alex Kersten
 */
public class VideoFrame extends MediaFrame {

    private BufferedImage image;

    private String debugInfo;

    public VideoFrame(BufferedImage image) {
        this.image = image;
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @return the debugInfo
     */
    public String getDebugInfo() {
        return debugInfo;
    }

    /**
     * @param debugInfo the debugInfo to set
     */
    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }
}
