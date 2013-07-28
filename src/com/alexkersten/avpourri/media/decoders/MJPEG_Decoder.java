/*
 Project: AVpourri
 File: MJPEG_Decoder.java (com.alexkersten.avpourri.media.decoders)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.decoders;

import com.alexkersten.avpourri.media.extractors.msiavi.AVIRIFF_MJPEG_Extractor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.imageio.ImageIO;

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

    //The container extractor which took care of finding the stream start
    //position for us.
    private AVIRIFF_MJPEG_Extractor extractor;

    //In stream-read mode, where are we? Set by startStream() to the first
    //SOI before the first JFIF it finds to avoid finding "SOI"'s earlier in the
    //AVI file which are probably just coincidence.
    private long streamPosition = -1;

    //The common stream file channel to be used while in stream-read mode.
    private FileChannel stream;

    //The byte buffer to use in stream-read mode.
    private ByteBuffer streamBuff;

    /**
     * In case we use the same class for MJPEG2000, we'll want to make a
     * separate constructor.
     */
    public MJPEG_Decoder(AVIRIFF_MJPEG_Extractor extractor) {
        super(extractor);
        this.extractor = extractor;
    }

    @Override
    public VideoFrame getNthFrame(int n) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * (Re)Sets the streamPosition to the beginning of the JFIF stream which was
     * found by the extractor earlier and opens the file channel.
     *
     * @return boolean If the stream was successfully initiated (a JFIF was
     * found in the file and its position was queued for use of nextFrame()).
     * @throws IOException If something horrible happens while trying to open
     * the file channel.
     */
    @Override
    public boolean startStream() throws IOException {
        streamPosition = extractor.getStreamStartPosition();

        if (getStreamPosition() == -1) {
            return false;
        }

        stream = FileChannel.open(extractor.getFileOnDisk());
        streamBuff = ByteBuffer.allocate(STREAM_BUFFER_SIZE);

        return true;
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
    public VideoFrame getNextFrame() throws IOException {
        //We should have a next frame set by now...
        if (getStreamPosition() == -1) {
            //End of stream or not set up yet.
            return null;
        }


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

        //But we'll just do it the proper way actually.
        //However, we definitely need to build a cache/lookup table once we've
        //gone through it once... Can probably save this as a file and read it 
        //back after multiple project closings/reopenings but will ahve to be
        //careful when parsing it.

        int bytesRead = 0;
        VideoFrame frameToReturn = null;

        //If we know we're at the start of a JPEG already... The JFIF objects
        //inside an MJPG (at least the MSI Afterburner-created ones) are pretty 
        //much... just JPEG files... and ImageIO can read them, and is smart
        //enough to stop when it finds an EOI. Now, if only it could tell us
        //where that EOI is, but I digress.
        /*FileInputStream is = new FileInputStream(extractor.getFileOnDisk().toFile());
         is.getChannel().position(streamPosition);
        
         BufferedImage thisFrame = ImageIO.read(is);
        
         frameToReturn = new VideoFrame(thisFrame); 
         frameToReturn.setDebugInfo("" + streamPosition);
        
         is.close();

         //Starting after the image header we  just read...
         streamPosition += 4;
         stream.position(streamPosition);
         */
        stream.position(getStreamPosition());
        //...find the next header.
        while ((bytesRead = stream.read(streamBuff)) != -1) {
            streamBuff.flip();

            //Temporary: grab the image from the bytes in the array here and
            //just hope that the whole thing made it... It probably did if our
            //buffer is large enough
            //TODO/BUG: make this more robust and able to get frames that are
            //larger than our buffer or that start near the end of a buffer.
            InputStream in = new ByteArrayInputStream(streamBuff.array());
            Image tmpImg = ImageIO.read(in);
            BufferedImage bi = null;
            if (tmpImg != null) {
                bi = new BufferedImage(96, 54, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = bi.createGraphics();
                g.drawImage(tmpImg, 0, 0, 96, 54, null);
                g.dispose();
            } else {
                System.err.println("NULL");
            }

            in.close();

            frameToReturn = new VideoFrame(bi);
            frameToReturn.setDebugInfo("" + getStreamPosition());

            //Set i to 4 to avoid looping on the current header...
            for (int i = 4; i < bytesRead; i++) {

                //If the end sequence is in the header, get out
                //BUG: If the end sequence is split across two buffers, we're
                //out of luck and we'll lose this frame; hopefully it won't be 
                //(fix is just checking while we're near the end but that's far
                //too much work).
                if (i + 10 > STREAM_BUFFER_SIZE) {
                    //We're near the end of the buffer - don't check for new
                    //frames here because we'll overrun it when we reach out and
                    //look at bytes. This is a naive solution and we'll miss a
                    //frame here and there but good enough for now.
                    //FIXME: Don't do this.
                    break;
                }

                try {
                    if (streamBuff.getInt(i) == 0xFFD8FFE0) {
                        //Make SURE it's a JFIF because this pattern _CAN_ randomly
                        //appear in the stream.  (Yes, I encountered this while
                        //debugging).
                        if (streamBuff.getInt(i + 6) != 0x4A464946) {
                            //Get outta here!
                            continue;
                        }

                        //We need to set the stream position back a little bit,
                        //since we found where we want to be in part of the file that
                        //we had already read.
                        streamPosition = stream.position() + i - bytesRead;
                        System.out.println("" + getStreamPosition());

                        //if we find it, return early with the frame
                        streamBuff.clear();
                        return frameToReturn;
                    }
                } catch (IndexOutOfBoundsException iboob) {
                    //FIXME: This shouldn't be happening and this is only here
                    //for debug...
                    System.err.println("IOOB: stream/buffer position: " + stream.position() + " / " + streamBuff.position() + " buffer offset: " + i);
                    return null;
                }
            }
            streamBuff.clear();
        }

        //Didn't find anything, so stream position is -1 and we're done.
        streamPosition = -1;
        stream.close();
        return frameToReturn;
    }

    /**
     * @return the streamPosition
     */
    public long getStreamPosition() {
        return streamPosition;
    }
}
