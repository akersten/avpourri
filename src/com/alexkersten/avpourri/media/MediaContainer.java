/*
 Project: AVpourri
 File: MediaContainer.java (com.alexkersten.avpourri.media.extractors)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

/**
 * A generic class which represents an A/V container on disk. Containers consist
 * of audio and/or video streams, and we need to be able to do things like
 * identify what type they are, their length, and other properties.
 *
 * @author Alex Kersten
 */
public abstract class MediaContainer {

    //The MediaFile object that corresponds to this container.
    private final MediaFile mediaFile;

    public MediaContainer(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    /**
     * Verifies if the file corresponding to this container is actually a valid
     * container format of this type.
     *
     * @return true iff this is a valid container with an associated MediaFile,
     * false otherwise
     */
    public abstract boolean isValidContainer();

    /**
     * @return the mediaFile
     */
    public MediaFile getMediaFile() {
        return mediaFile;
    }
}
