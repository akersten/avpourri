/*
 Project: AVpourri
 File: Project.java (com.alexkersten.avpourri.project)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.project;

import com.alexkersten.avpourri.AVPRuntime;
import com.alexkersten.avpourri.media.MediaFile;
import java.util.ArrayList;

/**
 * The giant Project class will contain all the configuration and media info of
 * our project, including clips currently on the timeline, timeline positions
 * and content, imported and generated clips, render settings, project settings,
 * etc. This file itself isn't serializable but
 *
 * @author Alex Kersten
 */
public class Project {

    //The AVPRuntime hosting this Project.
    private final AVPRuntime runtime;

    //Whether there are unsaved changes or not.
    private boolean projectDirty = false;

    //The collection of media files used in this project - represented visually
    //on the left-hand hierarchy of files in the editor.
    private ArrayList<MediaFile> projectMedia;

    //The local settings for this project, separated into a serializable
    //container.
    private ProjectSettings projectSettings;

    public Project(AVPRuntime runtime) {
        this.runtime = runtime;
    }

    /**
     * @return the projectMedia
     */
    public ArrayList<MediaFile> getProjectMedia() {
        return projectMedia;
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

    /**
     * @return the runtime
     */
    public AVPRuntime getRuntime() {
        return runtime;
    }
}
