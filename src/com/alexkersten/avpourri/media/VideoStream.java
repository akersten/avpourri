/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri.media;

/**
 * A stream with video-specific things like width and height parameters.
 *
 * @author akersten
 */
public abstract class VideoStream extends MediaStream {

    private int width, height;

    @Override
    public abstract VideoFrame getNthFrame(int n);

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
