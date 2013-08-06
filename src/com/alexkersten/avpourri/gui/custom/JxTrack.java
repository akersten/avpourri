/*
 Project: AVpourri
 File: JxTrack.java (com.alexkersten.avpourri.gui.custom)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.gui.custom;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 *
 * @author Alex Kersten
 */
@SuppressWarnings("serial")
public class JxTrack extends JList {

    //Color for the top of the gradient of the track
    private Color trackBackgroundColor1 = new Color(80, 80, 80);

    //Color for the bottom of the gradient of the track
    private Color trackBackgroundColor2 = new Color(40, 40, 40);

    //How tall the track should be.
    private int trackHeight = 96;

    //The title of this track
    private String title = "!Track";

    //The clips that this track contains
    private ArrayList<JxClip> clips = new ArrayList<JxClip>();

    public JxTrack(String title) {
        this.title = title;

        DefaultListModel<JxClip> m = new DefaultListModel<>();
        m.add(0, new JxClip("Test 1: " + title));
        m.add(0, new JxClip("Test 2: " + title));
        this.setModel(m);
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the clips
     */
    public ArrayList<JxClip> getClips() {
        return clips;
    }
}