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
import javax.swing.JScrollPane;
import javax.swing.ListModel;

/**
 * A Timeline is responsible for displaying a list of Tracks.
 *
 * Don't add any references to AVPRuntime or anything in here - this is a
 * standalone custom component for reusability - if you need to reference
 * something like microsecondsPerFrame to draw time markers, add an internal
 * field to this class and set it from outside.
 *
 * @author Alex Kersten
 */
public class JxTimeline<JxTrack> extends JList<JxTrack> {

    private static final long serialVersionUID = 1L;

    //The parent JScrollPane that contains this timeline
    private final JScrollPane scrollPane;

    //How tall the trackbar should be, in pixels
    private int trackbarHeight = 24;

    //The color for the trackbar background
    private Color trackbarBackgroundColor = new Color(25, 15, 25);

    //The color for the trackbar foreground (ticks, numbers, etc.)
    private Color trackbarForegroundColor = new Color(127, 64, 127);

    //Which frame the beginning of the timeline is looking at (leftmost frame)
    private int currentFrame = 0;

    //How long each frame should be displayed, in microseconds - used in
    //calculations for where to draw the tickmarks.
    private long microsecondsPerFrame = 33333;

    //How many seconds apart the tickmarks should be drawn
    private int secondsPerTick = 15;

    //The current temporal "resolution" of the Timeline
    private int framesPerPixel = 1;

    //Frame position of cursor A
    private int cursorA = 25;

    //Frame position of cursor B
    private int cursorB = 25;

    public JxTimeline(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;

        if (scrollPane == null) {
            System.err.println("Can't have a JxTimeline without a parent scroll pane!");
            return;
        }

        DefaultListModel m = new DefaultListModel();

        for (int i = 0; i < 25; i++) {
            m.add(i, "test element " + i);
        }

        this.setModel(m);

        this.setLayoutOrientation(JList.VERTICAL);
        this.setBackground(Color.DARK_GRAY);

        this.addMouseListener(new TLML());
        this.addMouseMotionListener(new TLMML(this));
    }

    /**
     * Whenever the list model is updated (i.e., a track is added or removed),
     * we need to recalculate the margin at the top of this element. Because
     * we're drawing a trackbar on top of everything, it's liable to cover some
     * components up if we don't.
     *
     * Add some extra space to the top of this list so that we'll always be able
     * to scroll up past any existing elements and reveal anything beneath the
     * trackbar.
     *
     * @param model The ListModel to set.
     */
    @Override
    public void setModel(ListModel model) {

        //First have the regular JList handle setting the ListModel.
        super.setModel(model);

        //From this, we have a new height - add some to it.
        this.setBounds(this.getX(), this.getY() - trackbarHeight, this.getWidth(), this.getY() + trackbarHeight);
    }

    /**
     * We do a lot in this method: the main thing is that we need a trackbar
     * across the top to draw the cursors and timeline ticks.
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //On top of anything that's there, draw the trackbar. We'll need to make
        //sure it follows us as we scroll as well. We already know we're in
        //a scrollpane.

        //Draw a background for the trackbar.
        g.setColor(trackbarBackgroundColor);
        g.fillRect(0, scrollPane.getVerticalScrollBar().getValue(), this.getWidth(), getTrackbarHeight());

        //Draw ticks on the trackbar based on the framerate - draw a tick every
        //10 seconds.
        //TODO: Offset based on horizontal scroll position (by modifying the
        //start x parameter)

        g.setColor(trackbarForegroundColor);
//        for (int x = 0; x < this.getWidth(); x += mic) {
//            
//        }
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

    /**
     * @return the trackbarHeight
     */
    public int getTrackbarHeight() {
        return trackbarHeight;
    }

    /**
     * @param trackbarHeight the trackbarHeight to set
     */
    public void setTrackbarHeight(int trackbarHeight) {
        this.trackbarHeight = trackbarHeight;
    }

    /**
     * @return the trackbarBackgroundColor
     */
    public Color getTrackbarBackgroundColor() {
        return trackbarBackgroundColor;
    }

    /**
     * @param trackbarBackgroundColor the trackbarBackgroundColor to set
     */
    public void setTrackbarBackgroundColor(Color trackbarBackgroundColor) {
        this.trackbarBackgroundColor = trackbarBackgroundColor;
    }

    /**
     * @return the trackbarForegroundColor
     */
    public Color getTrackbarForegroundColor() {
        return trackbarForegroundColor;
    }

    /**
     * @param trackbarForegroundColor the trackbarForegroundColor to set
     */
    public void setTrackbarForegroundColor(Color trackbarForegroundColor) {
        this.trackbarForegroundColor = trackbarForegroundColor;
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
