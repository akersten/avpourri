/*
 Project: AVpourri
 File: StreamDecoder.java (com.alexkersten.avpourri.media.decoders)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.decoders;

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
 * @author Alex Kersten
 */
public interface StreamDecoder {
    public MediaFrame getNthFrame(int n);
}
