/*
 Project: AVpourri
 File: CustomTheme.java (com.alexkersten.avpourri.gui)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.gui;

import java.awt.Color;
import java.io.Serializable;

/**
 * Because we want to be able to change things on the fly and have people share
 * theme files, this will encapsulate all the UI theming into one class - UI
 * components should make calls into here to get their style information.
 *
 * @author Alex Kersten
 */
public class CustomTheme implements Serializable {

    private static final long serialVersionUID = 1L;

    //The default colors.
    private Color labelColor = new Color(210, 210, 210), inputColor = new Color(229, 229, 229), farBackgroundColor, nearBackgroundColor;

    public CustomTheme() {
    }
}
