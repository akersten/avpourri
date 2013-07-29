/*
 Project: AVpourri
 File: MediaFile.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * A class representing a handle into a file on disk, which will very likely be
 * open by our program. Contains all the file-level stuff like seeking and other
 * junk.
 *
 * @author Alex Kersten
 */
public class MediaFile {

    //Default buffer size for the fileBuffer, should be changed by the
    //implementation of initialize in a MediaContainer subclass if the format
    //itself hints to another size (like the recommended buffer size field in
    //AVI files). Default size is 8KiB
    public static final int DEFAULT_BUFFER_SIZE = 0x2000;

    //The file that this MediaFile referrs to.
    private final Path fileOnDisk;

    //The channel into this file.
    private FileChannel fileChannel;

    //Buffer into which fileChannel should read.
    private ByteBuffer fileBuffer;

    public MediaFile(Path fileOnDisk) {
        this.fileOnDisk = fileOnDisk;

    }

    /**
     * @return the fileOnDisk
     */
    public Path getFileOnDisk() {
        return fileOnDisk;
    }

    /**
     * @return the fileChannel
     */
    public FileChannel getFileChannel() {
        return fileChannel;
    }

    /**
     * @return the fileBuffer
     */
    public ByteBuffer getFileBuffer() {
        return fileBuffer;
    }

    /**
     * Called by the initialize method in the abstract MediaContainer, which
     * should also be invoked by overriding methods. Sets the local fileChannel
     * reference to a FileChannel which has hopefully been opened.
     *
     * @param fileChannel the fileChannel
     */
    void setFileChannel(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }

    /**
     * Same rationale as setFileChannel. Individual container decoders can
     * change the default buffer size if they want.
     *
     * @param fileBuffer the fileBuffer to set
     */
    public void setFileBuffer(ByteBuffer fileBuffer) {
        this.fileBuffer = fileBuffer;
    }
}
