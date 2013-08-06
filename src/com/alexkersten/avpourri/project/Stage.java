/*
 Project: AVpourri
 File: Stage.java (com.alexkersten.avpourri.project)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.project;

/**
 *
 * @author Alex
 */
public abstract class Stage {

    
    /**
     * Invoked when the frame is advanced during normal playback and it wasn't
     * because of a jump somewhere else.
     *
     * For video streams, this should process all visible next frames and
     * display them - for audio streams, it should simply keep playing the audio
     * (that is, _not_ stopping it) because it'll be playing via the Java7 audio
     * system.
     *
     * When _rendering_, the video will be calculated with this method, but the
     * PCM audio will just be copied out - because we can't really get a frame-
     * by-frame on audio. Hopefully it doesn't skew too badly - it shouldn't, if
     * the sampling rate is correct.
     */
    public abstract void playbackCursorAdvanced();
}
