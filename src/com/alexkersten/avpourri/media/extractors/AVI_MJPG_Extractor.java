/*
 Project: AVpourri
 File: AVI_MJPG_Extractor.java (com.alexkersten.avpourri.media.extractors)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.extractors;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * Unfortunately, the original MJPG specification (before MJPEG2000) doesn't
 * exist, as far as I can tell - so there are many different ways of encoding
 * it. Luckily, my usage case is very small: decode MJPEG AVI files produced by
 * MSI Afterburner.
 *
 * After poking around with a hex editor and `strings`, I've decided that the
 * MJPEG stream in an AVI produced by MSI Afterburner is pretty straightforward.
 * Basically, there are a few parts to the file:
 *
 * 1) The AVI header junk, just make sure it has the RIFF FourCC at the
 * beginning and it should be valid.
 *
 * 2) An MJPG FourCC denotes one or more MJPEG streams - for now, we'll just
 * assume that everything has one stream, although this could get messy if
 * someone feeds us something with more. I haven't looked at any files that _do_
 * have more than one stream, so it'll be interesting to see if the frames get
 * interleaved/mixed out of order or if the streams are sequential. We'll have
 * to find one with multiple streams and then open it up with the Frame
 * Extractor tool (which I've also written as part of AVpourri) to find out.
 *
 * 3) Many, many, many JPEG images denoted by the JFIF FourCC - find each one
 * and extract it, and hopefully it's a straight-up JPEG that we can read.
 *
 * @author Alex Kersten
 */
public class AVI_MJPG_Extractor extends ContainerExtractor {

    //The size of the buffer to use for initial parsing of the container file.
    private static final int PARSE_BUFFER_SIZE = 1024;

    public AVI_MJPG_Extractor(Path p) {
        super(p);
    }

    /**
     * The method for checking this is mentioned in the description for this
     * class, but to be brief, we'll look for the following strings: RIFF, MJPG,
     * JFIF (a lot of them).
     *
     * @return Whether this container has an MJPEG stream worth extracting.
     */
    @Override
    public boolean isValidContainer() throws IOException {
        FileChannel fc = FileChannel.open(getFileOnDisk());
        ByteBuffer smallBuff = ByteBuffer.allocate(PARSE_BUFFER_SIZE);

        //We'll read the file for a while and make sure we see RIFF and MJPG
        //early on - keep reading until we see a few JFIF's and we'll be golden.

        //Initial examination of the files produced by MSI Afterburner seemed to
        //indicate that the MJPG string happens early on (within the first
        //hundred or so bytes of the file) and the JFIF sequences began around
        //the 8200th byte of the file).
        boolean seenRIFF = false, seenMJPG = false, seenJFIF = false;

        while (fc.read(smallBuff) != -1) {
            //The first thing we should check is the RIFF, so if it hasn't been
            //seen already, it must be the first four bytes - otherwise return
            //false.
            if (!seenRIFF) {
                if (smallBuff.getInt(0) != 0x52494646) {
                    fc.close();
                    return false;
                }
                seenRIFF = true;
            }

            //Alright... Look and see if this buffer has any MJPG or JFIF items
            //in it...
            //We could use KMP here for string searching but it's not worth it..
            for (int i = 0; i < PARSE_BUFFER_SIZE - "MJPG".length(); i++) {
                if (smallBuff.getInt(i) == 0x4D4A5047) {
                    seenMJPG = true;
                }
                if (smallBuff.getInt(i) == 0x4A464946) {
                    seenJFIF = true;
                }
            }

            if (seenMJPG && seenJFIF) {
                fc.close();
                return true;
            }
        }

        fc.close();
        return false;
    }
}
