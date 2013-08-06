/*
 Project: AVpourri
 File: MediaContainer.java (com.alexkersten.avpourri.media.extractors)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

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

    //The expected type of this MediaContainer. This gets verified automatically
    //by this class by looking at the first few bytes of whatever container
    //type this supposedly is - a simple verification. See MediaContainerType
    //for the actual bytes that get checked.
    private final MediaContainerType type;

    public MediaContainer(MediaFile mediaFile, MediaContainerType type) {
        this.mediaFile = mediaFile;
        this.type = type;
    }

    /**
     * Opens and validates the container; this causes the file to become open
     * and populates fields of the Container object with information about the
     * contained streams. Also, makes sure this file is actually a valid
     * container of this type. The superclass implementation in MediaContainer
     * does rudimentary header verification against known file headers of the
     * claimed type, and leaves more intensive verification to subclasses.
     *
     * This needs to be called first before any of the other operations will
     * work - information about the streams usually depends on a method like
     * this being invoked (so the streams can be found and infrastructure set up
     * to extract/decode them).
     *
     * For example, an implementation of this might check a FOURCC and then
     * populate some fields with framerate information and stream offsets.
     *
     * The superclass method (in MediaContainer) will open up a FileChannel into
     * the file with an associated ByteBuffer, and subclasses are responsible
     * for allocating a ByteBuffer defined in MediaFile of the appropriate size,
     * if the default of 8KiB is insufficient.
     *
     * @return true iff this is a valid container of this type with an
     * associated MediaFile, false otherwise
     */
    public boolean initialize() throws IOException, FileNotFoundException {
        //Open file and set up default byte buffer
        mediaFile.setFileChannel(FileChannel.open(mediaFile.getFileOnDisk()));
        mediaFile.setFileBuffer(
                ByteBuffer.allocate(MediaFile.DEFAULT_BUFFER_SIZE));

        //TODO: Check header against bytes in MediaContainerType


        return true;
    }

    //
    //Info methods about this container follow.
    //
    /**
     * Gets the size of the container - this size should be loaded from the file
     * metadata itself if at all possible. If not, using builtin file size
     * methods.
     *
     * @return The size of the container file in bytes.
     */
    public abstract long getContainerSize();

    /**
     * @return the mediaFile
     */
    public MediaFile getMediaFile() {
        return mediaFile;
    }

    /**
     * Streams that this container contains. Implementation depends on the
     * specific container format.
     *
     * @return A list of the streams that this container contains.
     */
    public abstract List<? extends MediaStream> getStreams();
}
