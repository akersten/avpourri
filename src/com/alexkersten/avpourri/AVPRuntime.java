/*
 Project: AVpourri
 File: AVPRuntime.java (com.alexkersten.avpourri)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri;

import com.alexkersten.avpourri.gui.CustomTheme;
import com.alexkersten.avpourri.gui.DebugConsoleFrame;
import com.alexkersten.avpourri.gui.WorkspaceFrame;
import com.alexkersten.avpourri.project.Project;

/**
 * The "central class" runtime for AVpourri.
 *
 * @author Alex Kersten
 */
public class AVPRuntime {

    //The theme for the program.
    private CustomTheme customTheme;

    //The currently open project.
    private Project project;

    public AVPRuntime() {
        customTheme = new CustomTheme();

        //  DebugConsoleFrame f = new DebugConsoleFrame();
        // f.setVisible(true);
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
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
