/*
 Project: AVpourri
 File: JxTimeline.java (com.alexkersten.avpourri.gui.custom)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.gui.custom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * A Timeline is responsible for displaying a list of Tracks.
 *
 * @author Alex Kersten
 */
public class JxTimeline<JxTrack> extends JList<JxTrack> {

    private static final long serialVersionUID = 1L;

    //Which frame the beginning of the timeline is looking at (leftmost frame)
    private int currentFrame = 0;

    //The current temporal "resolution" of the Timeline
    private int framesPerPixel = 1;

    //Frame position of cursor A
    private int cursorA = 25;

    //Frame position of cursor B
    private int cursorB = 25;

    public JxTimeline() {
        DefaultListModel m = new DefaultListModel();
        m.add(0, "test element");

        this.setModel(m);


        this.setLayoutOrientation(JList.VERTICAL);
        this.setBackground(Color.DARK_GRAY);

        this.addMouseMotionListener(new TLMML(this));

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Paint the cursors and cursor bars.

        int mouseX = 10;
        int mouseY = 10;
        Point mPos = this.getMousePosition();
        if (mPos != null) {
            mouseX = mPos.x;
            mouseY = mPos.y;
        }

        g.setColor(Color.YELLOW);
        g.drawOval(mouseX - 1, mouseY - 1, 2, 2);

    }
}

class TLMML implements MouseMotionListener {

    JxTimeline tl;

    TLMML(JxTimeline tl) {
        this.tl = tl;
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
}

class TLML implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}
