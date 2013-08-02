/*
 Project: AVpourri
 File: JxTrack.java (com.alexkersten.avpourri.gui.custom)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.gui.custom;

import javax.swing.JList;

/**
 * A Track is a sequential list of Clips.
 * @author Alex Kersten
 */
public class JxTrack<JxClip> extends JList<JxClip> {
    
    

    String getTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

/**
 * These need a custom renderer to draw their contained objects, I think.
 * 
 */