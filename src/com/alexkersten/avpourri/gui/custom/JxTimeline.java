/*
 Project: AVpourri
 File: JxTimeline.java (com.alexkersten.avpourri.gui.custom)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.gui.custom;

import java.awt.Color;
import javax.swing.JList;

/**
 * A Timeline is responsible for displaying a list of Tracks.
 *
 * @author Alex Kersten
 */
public class JxTimeline<JxTrack> extends JList<JxTrack> {

    private static final long serialVersionUID = 1L;

    public JxTimeline() {
        this.setLayoutOrientation(JList.VERTICAL);
        this.setBackground(Color.DARK_GRAY);

    }
}
