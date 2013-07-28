/*
 Project: AVpourri
 File: MediaFile.java (com.alexkersten.avpourri.media)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media;

import java.nio.file.Path;

/**
 * A class which helps us determine what kind of file something is before we
 * attempt to open it. MediaFile is essentially a wrapper around an actual file
 * that we've located on the disk, but contains some extra information and
 * metadata about it.
 *
 * MediaFiles will have a container component - once we identify what type a
 * file is, we can assign it a more specific MediaContainer type, which will
 * then be able to see into the file and extract MediaStreams.
 *
 * @author Alex Kersten
 */
public class MediaFile {

    public MediaFile(Path p) {
    }
}
