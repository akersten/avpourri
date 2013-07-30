/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri.media.containers.msiavi;

/**
 * This global header should reflect the structure defined here:
 * http://msdn.microsoft.com/en-us/library/ms779632.aspx
 *
 * We can use some of the values of this as hints to aid our decoding, like the
 * MicroSecPerFrame stuff can be translated to a framerate.
 *
 * @author Alex Kersten
 */
class MSIAVI_MainHeader {

    int dwMicroSecPerFrame = 0;

    int dwMaxBytesPerSec = 0;

    int dwPaddingGranularity = 0;

    int dwFlags = 0;

    int dwTotalFrames = 0;

    int dwInitialFrames = 0;

    int dwStreams = 0;

    int dwSuggestedBufferSize = 0;

    int dwWidth = 0;

    int dwHeight = 0;

}
