/*
 Project: AVpourri
 File: MJPGDecoder.java (com.alexkersten.avpourri.media.decoders)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.decoders;

/**
 * This is more of an 'extracter' than a 'decoder' - it will pull JPG frames out
 * of an MJPEG stream.
 * 
 * Unfortunately there isn't a unified specification for MJPEG (only for MJPEG
 * 2000) as far as I can tell, so I'll make the executive decision to target
 * MJPEG files produced by MSI Afterburner (since that's the purpose of writing
 * this project in the first place: to have an editor for those captures).
 * 
 * @author Alex Kersten
 */
public class MJPGDecoder implements AVPDecoder {
    
}
