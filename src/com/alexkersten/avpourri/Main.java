/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri;

import com.alexkersten.avpourri.gui.WorkspaceFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author akersten
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {

            System.err.println("Couldn't set the system look and feel.");
        }

        WorkspaceFrame f = new WorkspaceFrame();
        f.setVisible(true);
    }
}
