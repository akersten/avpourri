/*
 Project: AVpourri
 File: JxClip.java (com.alexkersten.avpourri.gui.custom)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.gui.custom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * A Clip is either a meta-clip (a group of other clips), video, or audio data.
 * For video, we'll have a thumbnail and text; for audio we'll probably generate
 * a visual representation of it.
 *
 * @author Alex Kersten
 */
@SuppressWarnings("serial")
public class JxClip extends JLabel {

    private String title;

    public JxClip(String title) {
        this.title = title;
        this.setText(title);
        this.addMouseListener(new JxClipML());
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, this.getWidth(), this.getHeight());
    }
}

class JxClipML implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        JOptionPane.showMessageDialog(null, "Press", null, 0);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}