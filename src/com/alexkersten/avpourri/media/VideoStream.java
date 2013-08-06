/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri.media;

import java.io.IOException;

/**
 * A stream with video-specific things like width and height parameters.
 *
 * @author akersten
 */
public abstract class VideoStream extends MediaStream {

    private int width, height;

    public abstract MediaFrame getNthFrame(int n);

    /**
     * Grabs the next frame stored in the internal stream buffer, and causes the
     * stream to advance further in the file if necessary. Returns null if the
     * stream hasn't been started or if we've reached the end of the file.
     *
     * @return The next frame in the buffer
     */
    public abstract MediaFrame getNextFrame() throws IOException;

    public VideoStream(MediaContainer container, String name) {
        super(container, name);
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
