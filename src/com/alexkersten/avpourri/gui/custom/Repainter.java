/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alexkersten.avpourri.gui.custom;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A thread to periodically repaint a Swing component. Since we don't want to be
 * calling repaint on every single mouseMoved event (too much CPU usage), we
 * will do it every x ms instead, by using this thread to target certain
 * components.
 *
 * @author Alex Kersten
 */
public class Repainter implements Runnable {

    //Local thread reference
    private Thread thisThread;

    //Whether this thread is active or not
    private boolean running = false;

    //How often to repaint - default is 10fps.
    private long interval = 100;

    private ArrayList<Component> targets = new ArrayList<>();

    /**
     * Constructs a Repainter and starts it spinning with the default interval.
     * Components can be added and the interval can be changed at any time.
     */
    public Repainter() {
        thisThread = new Thread(this);
        thisThread.setDaemon(true); //Background thread, don't hold up the JVM
        thisThread.start();
    }

    /**
     * Add a Swing component to be subject to interval repaints.
     *
     * @param target The component to add.
     */
    public void addTarget(Component target) {
        synchronized (targets) {
            targets.add(target);
        }
    }

    /**
     * Stop repaiting and let the thread exit. Don't use the object after
     * invoking this method.
     */
    public void stopRunning() {
        running = false;
    }

    
    @Override
    public void run() {
        running = true;

        while (running) {
            
            synchronized (targets) {
                Iterator<Component> itr = targets.iterator();
                while (itr.hasNext()) {
             
                    itr.next().repaint();
                }
            }
           
            try {
                Thread.sleep(getInterval());
            } catch (InterruptedException ie) {
                running = false;
            }
            
            
        }
    }

    /**
     * @return the interval
     */
    public long getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }
}
