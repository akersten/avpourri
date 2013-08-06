/*
 Project: AVpourri
 File: JxTimeline.java (com.alexkersten.avpourri.gui.custom)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.gui.custom;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

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
@SuppressWarnings("serial")
public class JxTimeline extends JList<JxTrack> {

    //The parent JScrollPane that contains this timeline
    private final JScrollPane scrollPane;

    //How tall the trackbar should be, in pixels
    private int trackbarHeight = 24;

    //The color for the trackbar background
    private Color trackbarBackgroundColor1 = new Color(25, 25, 25);

    private Color trackbarBackgroundColor2 = new Color(15, 15, 15);

    //The color for the trackbar foreground (ticks, numbers, etc.)
    private Color trackbarForegroundColor1 = new Color(90, 90, 90);

    private Color trackbarForegroundColor2 = new Color(110, 110, 110);

    private Color trackbarForegroundColor3 = new Color(150, 140, 150);

    //Colors for the track cursors.
    private Color cursorAColor = new Color(200, 10, 10);

    private Color cursorBColor = new Color(10, 10, 200);

    //Which frame the beginning of the timeline is looking at (leftmost frame)
    private int currentFrame = 0;

    //How long each frame should be displayed, in microseconds - used in
    //calculations for where to draw the tickmarks.
    private long microsecondsPerFrame = 33333;

    //How many seconds apart the tickmarks should be drawn
    private int secondsPerMajorTick = 15;

    //The current temporal "resolution" of the Timeline
    private int framesPerPixel = 1;

    //Frame position of cursor A
    int cursorA = 0;

    //Frame position of cursor B
    int cursorB = 0;

    public JxTimeline(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;

        if (scrollPane == null) {
            System.err.println("Can't have a JxTimeline without a parent scroll pane!");
            return;
        }


        //This isn't really a selectable list, doesn't make sense to be able to
        //select elements except in our own special way - the JxTrack items
        //that we add to this list will have a callback to allow us to set the
        //"selected" property on them and highlight them, but this won't be done
        //through the regular Swing way because the regular mouse/motionlistener
        //will make this a giant mess, so remove them.

        MouseListener[] listeners = this.getMouseListeners();
        MouseMotionListener[] listeners2 = this.getMouseMotionListeners();
        for (MouseListener l : listeners) {
            //Don't need those where we're going, full custom stack baby
            //       this.removeMouseListener(l);
        }
        for (MouseMotionListener l : listeners2) {
            this.removeMouseMotionListener(l);
        }





        DefaultListModel<JxTrack> m = new DefaultListModel<>();


        //TODO: DEBUG TEST, REMOVE
        for (int i = 0; i < 2; i++) {


            m.add(i, new JxTrack("track " + i));
        }

        this.setModel(m);

        this.setLayoutOrientation(JList.VERTICAL);
        this.setBackground(Color.DARK_GRAY);

        this.addMouseListener(new TLML());
        this.addMouseMotionListener(new TLMML(this));
        this.setCellRenderer(new TimelineCellRenderer());

    }

    /**
     * We do a lot in this method: the main thing is that we need a trackbar
     * across the top to draw the cursors and timeline ticks.
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        //Translate down by the height of the trackbar we insert, so no elements
        //get hidden under it.
        //TODO: Remember this when we eventually handle mouse events and pass
        //them on to child objects - need to pass the correct position.
        //TODO: Instead of this (kind of nasty) hack we might look into a custom
        //cell renderer or listmodel renderer or whatever the API calls them
        g.translate(0, trackbarHeight);
        super.paintComponent(g);

        g.translate(0, -trackbarHeight);

        //Since we're expecting to be in a scroll-pane, any relative drawing on
        //top of this component needs to be with respect to where we've 
        //scrolled.
        int xRel = 0; //TODO: horizontal scroll offset
        int yRel = scrollPane.getVerticalScrollBar().getValue();

        //On top of anything that's there, draw the trackbar.
        GradientPaint grad = new GradientPaint(0, yRel,
                                               trackbarBackgroundColor2, 0,
                                               trackbarHeight + yRel,
                                               trackbarBackgroundColor1);

        //Draw a background for the trackbar.
        ((Graphics2D) g).setPaint(grad);
        g.fillRect(xRel, yRel, this.getWidth(), getTrackbarHeight());

        //Draw a border around the bottom
        g.setColor(trackbarForegroundColor1);
        g.drawLine(xRel, yRel + getTrackbarHeight(),
                   this.getWidth() + xRel, yRel + getTrackbarHeight());

        //Draw ticks on the trackbar based on the framerate - draw a tick every
        //second, and a large tick every 15.
        //TODO: Offset based on horizontal scroll position (by modifying the
        //start x parameter) - will probably need to set to xRef and take into
        //account that we're calculating from 0 (not every x position is a frame
        //for example) - more invovled than other parts where we're just changing
        //0 to xRef

        //Maybe xRef shouldn't change -just use the horizontal scroll and only
        //render what's in the current viewport?

        //Units of increment are pixels/second (how many pixels to move / sec)
        for (int x = 0; x < this.getWidth();
             x += (int) (1000.0 * 1000)
                  / (double) (microsecondsPerFrame * framesPerPixel)) {

            //Units of modulus comparison are pixels by seconds (between mticks)
            if (x % (secondsPerMajorTick * secondsPerMajorTick) == 0) {
                //Large tick
                g.setColor(trackbarForegroundColor2);
                g.drawLine(x, yRel + getTrackbarHeight() / 2, x, yRel + getTrackbarHeight());
                g.setColor(trackbarForegroundColor1);
            } else {
                //Small tick
                g.drawLine(x, yRel + 3 * getTrackbarHeight() / 4, x, yRel + getTrackbarHeight());
            }

        }

        //Draw cursor A
        g.setColor(cursorAColor);
        g.drawRect(xRel + cursorA, yRel, 1, getHeight());

        //Cursor B
        g.setColor(cursorBColor);
        g.drawRect(xRel + cursorB, yRel, 1, getHeight());

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
     * @return the trackbarForegroundColor
     */
    public Color getTrackbarForegroundColor() {
        return trackbarForegroundColor1;
    }

    /**
     * @param trackbarForegroundColor the trackbarForegroundColor to set
     */
    public void setTrackbarForegroundColor(Color trackbarForegroundColor) {
        this.trackbarForegroundColor1 = trackbarForegroundColor;
    }
}

class TLMML implements MouseMotionListener {

    JxTimeline tl;

    TLMML(JxTimeline tl) {
        this.tl = tl;
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (SwingUtilities.isLeftMouseButton(me)) {

            tl.cursorA = me.getX();
        } else if (SwingUtilities.isRightMouseButton(me)) {
            tl.cursorB = me.getX();
        }

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

/**
 * This renderer will draw the tracks as horizontal rows by providing a JPanel
 * for them to exist in. Each "track" is just a JPanel to contain the JxTracks
 * which are JLists containing JxClips (which are really just JLabels).
 *
 * @author Alex Kersten
 */
@SuppressWarnings("serial")
class TimelineCellRenderer extends JPanel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        @SuppressWarnings("unchecked")
        JxTrack track = (JxTrack) value;
        this.add(track);
        this.setBorder(BorderFactory.createLoweredSoftBevelBorder());

        /*        //Get every clip inside these and render those
         for (JxClip clip : track.getClips()) {
         //setPreferredSize(new Dimension(40, 40));
         this.add(clip);

         }*/

        this.setPreferredSize(new Dimension(60, 90));

        this.setBackground(Color.MAGENTA);

        return this;
    }
}