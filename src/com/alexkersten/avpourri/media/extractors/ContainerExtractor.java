/*
 Project: AVpourri
 File: ContainerExtracter.java (com.alexkersten.avpourri.media.extracters)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.extractors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Media is stored in a container file - be it .avi, .mkv, .mov, etc.
 *
 * We need to identify and separate the streams from these files before decoding
 * them into BufferedImages or the like. This interface provides for the common
 * methods we need.
 *
 * 
 *TODO: Is this file necessary? If we're going to have things lke MSIAVI_container
 * @author Alex Kersten
 */
public abstract class ContainerExtractor {

    private Path fileOnDisk;

    /**
     * Every container must be a reference to some file on the disk. This
     * superclass is only responsible for keeping track of which file that is.
     *
     * @param p The path to a container file, probably. Will be checked later
     * with downstream implementations of setExtractionParametersAndValidate().
     */
    public ContainerExtractor(Path p) {
        if (Files.isDirectory(p)) {
            throw new RuntimeException(
                    "Container is a directory: " + p.toString());
        }

        this.fileOnDisk = p;
    }

    /**
     * Checks if this container is a valid container of whatever type the
     * corresponding implementing class purports to be. For example, if a class
     * named AVI_MJPG_Extractor implemented this interface, it could be expected
     * to return true iff the header of the file in question matched the AVI
     * container format, and MJPG + JFIF FourCCs were found inside.
     *
     * @return Whether the constructed extractor is operating on what we presume
     * to be a valid container file.
     */
    public abstract boolean setExtractionParametersAndValidate() throws IOException;

    /**
     * @return the fileOnDisk
     */
    public Path getFileOnDisk() {
        return fileOnDisk;
    }
    
    /**
     * Returns how many streams we found in this container.
     * @return The number of streams of this type in this container.
     */
    public abstract int getStreamCount();
}
