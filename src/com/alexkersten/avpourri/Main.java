/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class for AVpourri - loads the runtime instance and gets things rolling.
 * Also sets the system window style, since this needs to be done before any
 * windows are created.
 *
 * @author Alex Kersten
 */
public class Main {

    //The singleton runtime instance which ties everything together.
    private static AVPRuntime runtime;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {

            System.err.println("Couldn't set the system look and feel.");
        }

        runtime = new AVPRuntime();
    }
}
