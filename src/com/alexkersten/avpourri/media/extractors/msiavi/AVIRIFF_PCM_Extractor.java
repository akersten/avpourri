/*
 Project: AVpourri
 File: AVIRIFF_PCM_Extractor.java (com.alexkersten.avpourri.media.extractors.aviriff)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.extractors.msiavi;

import com.alexkersten.avpourri.media.extractors.ContainerExtractor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Alex Kersten
 */
public class AVIRIFF_PCM_Extractor extends ContainerExtractor {

    //"2d" arraylist containing the streams, with each stream containing integer
    //stereo data in 16-bit signed interleaved form -- TODO that should not
    //be the case, we need an abstract container for audio.
    private ArrayList<ArrayList<Integer>> streams = new ArrayList<>();

    //TODO: Enum for different types of PCM, now we'll just do Stereo 16-bit signed
    //or just stream copy the bits out.
    public AVIRIFF_PCM_Extractor(Path p) {
        super(p);
    }
    
    @Override
    public boolean setExtractionParametersAndValidate() throws IOException {
        FileInputStream fis = new FileInputStream(getFileOnDisk().toFile());
        FileChannel c = FileChannel.open(getFileOnDisk());

        //Look for streams listed in the header (before 8192 bytes)
        //LISTs should be word-aligned
        byte[] wordBuffer = new byte[4];
        byte[] LIST_MAGIC = {'L', 'I', 'S', 'T'};
        
        int initinto = 0;
        
        while (fis.read(wordBuffer, 0, 4) != -1 && initinto <= 8192) {
            if (Arrays.equals(wordBuffer, LIST_MAGIC)) {
                //initinto points to the location of where the list item is
                //figure out what it's listing, if it's audio, add another stream
                //TODO: abstract this list finding one level up - we should have a
                //class that finds lists and can give us an overview of all the lists in
                //an avi (as part of the framework that finds streams contained within a
                //media file)
                
                streams.add(new ArrayList<Integer>());
                
            }
            
            initinto += 4;
        }
        
        return true;
    }
    
    @Override
    public int getStreamCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
