/*
 Project: AVpourri
 File: AVPRuntime.java (com.alexkersten.avpourri)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri;

import com.alexkersten.avpourri.gui.WorkspaceFrame;

/**
 * The "central class" runtime for AVpourri.
 *
 * @author Alex Kersten
 */
public class AVPRuntime {

    public AVPRuntime() {
        WorkspaceFrame w = new WorkspaceFrame();
        w.setVisible(true);
    }
}
