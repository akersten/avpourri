/*
 Project: AVpourri
 File: AVPRuntime.java (com.alexkersten.avpourri)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri;

import com.alexkersten.avpourri.gui.CustomTheme;
import com.alexkersten.avpourri.gui.WorkspaceFrame;

/**
 * The "central class" runtime for AVpourri.
 *
 * @author Alex Kersten
 */
public class AVPRuntime {

    //Whether there are unsaved changes or not.
    private boolean projectDirty = false;

    private CustomTheme customTheme;

    public AVPRuntime() {
        customTheme = new CustomTheme();
        
        WorkspaceFrame w = new WorkspaceFrame(this);
        w.setVisible(true);
    }

    /**
     * @return the customTheme
     */
    public CustomTheme getCustomTheme() {
        return customTheme;
    }

    /**
     * @return the projectDirty
     */
    public boolean isProjectDirty() {
        return projectDirty;
    }

    /**
     * @param projectDirty the projectDirty to set
     */
    public void setProjectDirty(boolean projectDirty) {
        this.projectDirty = projectDirty;
    }
}
