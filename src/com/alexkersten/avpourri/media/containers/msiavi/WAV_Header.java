/*
 Project: AVpourri
 File: WAV_Header.java (com.alexkersten.avpourri.media.containers.msiavi)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.containers.msiavi;

import com.alexkersten.avpourri.middleware.AVUtils;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Reference: https://ccrma.stanford.edu/courses/422/projects/WaveFormat/
 *
 * @author Alex Kersten
 */
public class WAV_Header {

    public int dwSubchunk1ID, dwSubchunk1Size;

    public short wAudioFormat, wNumChannels;

    public int dwSampleRate, dwByteRate;

    public short wBlockAlign, wBitsPerSample;

    /**
     * Do some verification and populate the header fields from a stream into a
     * WAV file on disk.
     *
     * @param dis A data input stream aligned at the header chunk length field
     * of a WAV file.
     * @return If the file passed rudimentary verification.
     */
    public boolean createFromStream(DataInputStream dis) throws IOException {
        //Make sure the first 8 bytes we see are "WAVEfmt ".

        long test = 0x57415645666D7420L;

        if (dis.readLong() != test) {
            System.err.println("WAV file header did not start w/ 'WAVEfmt '");
            return false;
        }

        //We already read the dwSubchunk1ID - set it, because we know it.
        dwSubchunk1ID = 0x666D7420;

        dwSubchunk1Size = AVUtils.readLittleEndianInt(dis);

        //Verify that everything looks right with the length.. Should be 18 (16
        //bytes of info and 2 for byte-align - but that's not present in some, so
//allow either and compensate later)
        if (dwSubchunk1Size != 18 && dwSubchunk1Size != 16) {
            System.err.println("WAV file header length confusion. ("
                               + dwSubchunk1Size + ")");
            return false;
        }

        wAudioFormat = AVUtils.readLittleEndianShort(dis);
        wNumChannels = AVUtils.readLittleEndianShort(dis);
        dwSampleRate = AVUtils.readLittleEndianInt(dis);
        dwByteRate = AVUtils.readLittleEndianInt(dis);
        wBlockAlign = AVUtils.readLittleEndianShort(dis);
        wBitsPerSample = AVUtils.readLittleEndianShort(dis);

        //If we had extra padding (as opposed to 16), skip the next two 0 bytes
        if (dwSubchunk1Size == 18) {
            dis.skip(2);
        }

        return true;
    }
}
