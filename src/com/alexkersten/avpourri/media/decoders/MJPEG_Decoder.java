/*
 Project: AVpourri
 File: MJPEG_Decoder.java (com.alexkersten.avpourri.media.decoders)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.decoders;

import com.alexkersten.avpourri.media.extractors.AVI_MJPEG_Extractor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * From an MJPEG stream, we'll look for the individual JPEG headers and extract
 * the single images - we'll call each image a frame and that should be good
 * enough.
 *
 * @author Alex Kersten
 */
public class MJPEG_Decoder extends StreamDecoder {

    //For stream-read mode, this is how long the read-buffer will be.
    private static int STREAM_BUFFER_SIZE = 1048576;

    //How big do we think a JFIF could ever be? Probably will need to be updated
    //once we start considering exotic resolutions... Let's go with 4MiB for
    //now...
    private static int JFIF_BUFFER_SIZE = 4194304;

    //The container extractor which took care of finding the stream start
    //position for us.
    private AVI_MJPEG_Extractor extractor;

    //In stream-read mode, where are we? Set by startStream() to the first
    //SOI before the first JFIF it finds to avoid finding "SOI"'s earlier in the
    //AVI file which are probably just coincidence.
    private long streamPosition = -1;

    //The common stream file channel to be used while in stream-read mode.
    private FileChannel stream;

    //The byte buffer to use in stream-read mode.
    private ByteBuffer streamBuff;

    /**
     * In case we don't use the same class for MJPEG2000, we'll want to make a
     * separate constructor.
     */
    public MJPEG_Decoder(AVI_MJPEG_Extractor extractor) {
        super(extractor);
        this.extractor = extractor;
    }

    @Override
    public VideoFrame getNthFrame(int n) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * (Re)Sets the streamPosition to the beginning of the JFIF stream which was
     * found by the extractor earlier.
     *
     * @return boolean If the stream was successfully initiated (a JFIF was
     * found in the file and its position was queued for use of nextFrame()).
     */
    @Override
    public boolean startStream() throws IOException {
        streamPosition = extractor.getStreamStartPosition();
        return (streamPosition != -1);
    }

    /**
     * This method will stream through the file looking for more SOI/EOI pairs
     * and putting the data into a VideoFrame (which will use Java's native JPEG
     * encoder to transform that data).
     *
     * @return A VideoFrame containing simpler data for reconstructing a single
     * frame of this stream, or null if there is no more stream.
     *
     * @throws IOException If something horrible happens while reading the
     * stream.
     */
    @Override
    public MediaFrame getNextFrame() throws IOException {
        //We should have a next frame set by now...
        if (streamPosition == -1) {
            //End of stream or not set up yet.
            return null;
        }

        return null;

        //We need to reconstruct an entire JFIF which may or may not be entirely
        //inside the buffer...


        //Because of the way we've set things up, the location will always be in
        //the buffer.


        //...and then find the next JFIF and point nextJFIFLocation to it, or -1
        //if there is no more.


        //Anyway, start from the first SOI and read 4 bytes at a time into the
        //MediaFrame. From my quick analysis, SOI bytes always start on an even
        //offset from the first one so this should be okay. The pattern we're 
        //looking for is SOI (4 bytes) + 2 bytes + "JFIF" (4) so we just look
        //ahead in the stream when we find the SOI (0xFFD8FFE0) which doesn't
        //occur in the compression. Anyway, these all looked aligned on 4th
        //bytes (maybe even on 8ths or 16ths but thsoe dont' matter for this
        //pattern) so read in 4 bytes at once starting from the stream position
        //and pipe it into a VideoFrame.

        //Sadly we don't know the size of these ahead of time, at least in
        //the format I looked at. Oh well.
        //BUG: If these are not 4-aligned, this could be bad.
        
        //tl;dr: Start at the streamPosition and read from SOI to EOI, put that
        //into a MediaFrame, advance streamPosition to the next SOI.
        
    }
}
