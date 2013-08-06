/*
 Project: AVpourri
 File: PCM_Stream.java (com.alexkersten.avpourri.media.astreams)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.astreams;

import com.alexkersten.avpourri.media.AudioPCMFormat;
import com.alexkersten.avpourri.media.AudioStream;
import com.alexkersten.avpourri.media.MediaContainer;

/**
 * This will be fairly straightforward since the Audio subsystem expects PCM
 * data anyway. This is just a wrapper basically that can be created around a
 * raw AudioStream.
 *
 * @author Alex Kersten
 */
public class PCM_Stream extends AudioStream {

    public PCM_Stream(MediaContainer container, String title,
                      AudioPCMFormat format, byte data[]) {

        super(container, title, format, data);
    }
}
