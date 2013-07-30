/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri.media;

/**
 * An enumeration of supported media containers and human-readable names for
 * them. Also contains information important to identifying these containers
 * like the first few bytes of the header, for example.
 *
 * @author Alex Kersten
 */
public enum MediaContainerType {

    MSIAVI("MSI Afterburner AVI", new byte[]{'R', 'I', 'F', 'F'});

    //The display name of this MediaContainer - what a user should see when any
    //aspect of the UI queries this media type
    private String name;

    //The first few bytes of the header of this container, which get verified
    //automatically upon the first call to initialize() by anything that
    //inherits MediaContainer (well, the inherited initialize() calls should
    //call super.initialize(), and that will verify against these bytes).
    private byte[] header;

    private MediaContainerType(String name, byte[] header) {
        this.name = name;
        this.header = header;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the header
     */
    public byte[] getHeader() {
        return header;
    }
}
