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
 * and content, imported and generated clips, render settings, etc.
 *
 * @author Alex Kersten
 */
public class Project {

    //The AVPRuntime hosting this Project.
    private AVPRuntime runtime;

    //The collection of media files used in this project - represented visually
    //on the left-hand hierarchy of files in the editor.
    private ArrayList<MediaFile> projectMedia;

    public Project(AVPRuntime runtime) {
        this.runtime = runtime;
    }

    /**
     * @return the projectMedia
     */
    public ArrayList<MediaFile> getProjectMedia() {
        return projectMedia;
    }
}
