/*
 Project: AVpourri
 File: WAVDebugger.java (com.alexkersten.avpourri.gui)
 Author: Alex Kersten
 */

/*
 * WAVDebugger.java
 *
 * Created on Aug 6, 2013, 12:06:36 AM
 */
package com.alexkersten.avpourri.gui;

import com.alexkersten.avpourri.media.AudioStream;
import com.alexkersten.avpourri.media.MediaFile;
import com.alexkersten.avpourri.media.containers.msiavi.WAV_Container;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Alex
 */
@SuppressWarnings("serial")
public class WAVDebugger extends javax.swing.JFrame {

    /**
     * Creates new form WAVDebugger
     */
    public WAVDebugger() {
        initComponents();
        try {
            setIconImage(ImageIO.read(
                    this.getClass().getResource("icons/trash16.png")));
        } catch (Exception e) {
            System.err.println("Can't load frame icon.");
        }

        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("Play");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(337, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(266, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private AudioStream as = null;

    private WAV_Container c = null;

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser jf = new JFileChooser();
        int result = jf.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            if (as == null) {
                MediaFile f = new MediaFile(jf.getSelectedFile().toPath());
                c = new WAV_Container(f);
            }
            try {
                if (as == null) {
                    c.initialize();


                    as = (AudioStream) c.getStreams().get(0);
                }
                as.setStream(0);
                as.playSync();


            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "problem", "fffff", 0);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables

}
