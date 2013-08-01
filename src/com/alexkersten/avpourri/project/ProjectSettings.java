/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri.project;

import java.io.Serializable;

/**
 * Keeps track of the project settings - serializable so we can just save a copy
 * of this object and reconstitute it when loading project settings.
 *
 * @author Alex Kersten
 */
public class ProjectSettings implements Serializable {

    //The project hosting this project settings instance - in case we need to
    //reach out and update something when one of our settings gets changed (like
    //if microsecondsPerFrame is changed, we'll want to notify the timeline so
    //it can be updated, etc.), so go reference the Project's Runtime.
    private transient Project project;

    //How many microseconds each frame should take. The default is 30FPS, so
    //33,333 micros. This is a project-wide setting: clips which have a
    //different speed will be scaled to match. For example, a clip at 15FPS
    //(16666 micros/frame) inserted into a project at 30FPS will play at twice
    //the speed (as if it were actually a 30FPS clip). This can be avoided by
    //simply changing the time-scale on a per-clip basis (in clip properties)
    //while editing.
    private long microsecondsPerFrame = 33333;

    public ProjectSettings(Project project) {
        this.project = project;
    }

    /**
     * @return the microsecondsPerFrame
     */
    public long getMicrosecondsPerFrame() {
        return microsecondsPerFrame;
    }

    /**
     * @param microsecondsPerFrame the microsecondsPerFrame to set
     */
    public void setMicrosecondsPerFrame(long microsecondsPerFrame) {
        this.microsecondsPerFrame = microsecondsPerFrame;
    }
}
