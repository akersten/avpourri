/*
 Project: AVpourri
 File: StreamDecoder.java (com.alexkersten.avpourri.media.decoders)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.decoders;

import com.alexkersten.avpourri.media.extractors.ContainerExtractor;
import java.io.IOException;

/**
 * The purpose of the decoder interface is to provide a common way to access
 * segments of media which may be in many different formats. However, both audio
 * and video can be abstracted to the concept of 'frames', and we can look into
 * an audio or video stream and pick a certain frame to start playing from - and
 * we should be agnostic about what codec encodes it.
 *
 * There are exceptions to this - especially for formats like MP3 in which there
 * are few/no keyframes or time markers; I don't think MP3 is a frame-based
 * format (can't just jump arbitrarily into the file without "playing"/computing
 * diffs up to that point). Luckily, the codecs that we care about should behave
 * nicely.
 *
 * For video streams, the way we'll render and present the video to the user
 * dictates how we want our decoder to behave. For our purposes, we'll need to
 * simply be able to pull a bunch of frames out of the media and convert them to
 * image or audio data. If it's video, we'll pipe this data into a VolatileImage
 * (or similar) and present it to the user - when we go to render, this image
 * and any modifications made will just be played back and the final baked image
 * will be sent off to the encoder.
 *
 * Audio is a bit trickier and I haven't thought about it yet.
 *
 * Anyway, these decoders are kind of in the middle of the chain of abstraction:
 * you'll need to give them a ContainerExtractor object from which it can
 * request a stream to decode. Then you'll be able to pull individual frames.
 *
 * This can get nasty if we're pulling a bunch of frames from a file and calling
 * getNthFrame for every single one of them (random access). We'll have to
 * devise a caching solution. The best option so far is to kind of open the file
 * as a stream, then read sequentially - do this as often as possible, rarely do
 * we want to call getNthFrame a bunch, because that'll require seeking to
 * random parts of the file - unless implementations of StreamDecoder keep cue
 * sheets of where in the file each frame appeared. Which could be good, but
 * it's still random-ish access. So we'll try to streamline it by providing a
 * few methods that start, resume, and stop a stream. (startStream,
 * getNextFrame).
 *
 * @author Alex Kersten
 */
public abstract class StreamDecoder {

    private ContainerExtractor extractor;

    public StreamDecoder(ContainerExtractor extractor) {
        this.extractor = extractor;
    }

    public abstract MediaFrame getNthFrame(int n);

    //Stream-read methods. These will be better performant and less heavy on
    //disk IO since they're not as random access as getNthFrame.
    /**
     * Starts a stream-reading profile for this decoder. In the general case, it
     * should create a buffer for storing junk read in from the file, and begin
     * sorting out as many frames as it can into a cache/internal buffer.
     *
     * @return boolean Status of the stream initialization.
     */
    public abstract boolean startStream() throws IOException;

    /**
     * Grabs the next frame stored in the internal stream buffer, and causes the
     * stream to advance further in the file if necessary. Returns null if the
     * stream hasn't been started or if we've reached the end of the file.
     *
     * @return The next frame in the buffer
     */
    public abstract MediaFrame getNextFrame() throws IOException;

    //End of stream-read methods.
    /**
     * @return the container
     */
    public ContainerExtractor getExtractor() {
        return extractor;
    }
}
