/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/gui/CustomInternalFrame.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.gui;

import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.panels.CustomPanel;

/**
 * A custom <code>JInternalFrame</code> for all frames to extend that are
 * placed on the EI "desktop".
 *
 * @author mjv
 * @date 2007/04/30 15:50:46
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/gui/CustomInternalFrame.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 * @see javax.swing.JInternalFrame
 */
public class CustomInternalFrame
       extends JInternalFrame
       implements InternalFrameListener {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private CustomPanel form = null;


    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form CustomInternalFrame.
     * @param title
     * @param resizable
     * @param closeable
     * @param maximizable
     * @param iconifiable
     */
    public CustomInternalFrame(String title, boolean resizable,
                               boolean closeable, boolean maximizable,
                               boolean iconifiable) {
        super(title, resizable, closeable, maximizable, iconifiable);
        initComponents();
        addInternalFrameListener(this);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Set the <code>CustomPanel</code>.
     *
     *
     *
     * @param f the <code>CustomPanel</code>
     */
    public void setForm(CustomPanel panel) {
        this.form = panel;
        form.setFrame(this);
        JScrollPane jspMain = new JScrollPane(form);
        this.getContentPane().add(jspMain, "Center");
        this.revalidate();
    }

    /**
     * Get the <code>CustomPanel</code>.
     *
     *
     *
     * @return the <code>CustomPanel</code>
     */
    public CustomPanel getCustomPanel() {
        return this.form;
    }

    /**
     * Dispose.
     */
    public void dispose() {
        try {
            form.clear();
            form = null;
        } catch(Exception e) {

        }
        super.dispose();
    }

    /**
     * Handle internal frame activation.
     *
     * @param e the event
     */
    public void internalFrameActivated(InternalFrameEvent e) {
    }

    /**
     * Handle internal frame closed.
     *
     * @param e the event
     */
    public void internalFrameClosed(InternalFrameEvent e) {
    }

    /**
     * Handle internal frame closing.
     *
     * @param e the event
     */
    public void internalFrameClosing(InternalFrameEvent e) {
        if (form != null) {
            if (form.isUpdated()) {
                String message = "This form has been updated.\n\n" +
                                 "Click \"OK\" to save your changes.";

                // Modal dialog with OK/cancel and a text field
                int answer =
                   JOptionPane.showConfirmDialog(this, message, "Warning",
                                                 JOptionPane.OK_CANCEL_OPTION);

                if (answer == JOptionPane.OK_OPTION) {
                    form.save();
                    this.dispose();
                    return;
                } else if (answer == JOptionPane.CANCEL_OPTION) {
                    // do nothing
                    return;
                }
            }
        }

        this.dispose();
    }

    /**
     * Handle internal frame deactivation.
     *
     * @param e the event
     */
    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

    /**
     * Handle internal frame deiconified.
     *
     * @param e the event
     */
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    /**
     * Handle internal frame iconfied.
     *
     * @param e the event
     */
    public void internalFrameIconified(InternalFrameEvent e) {
    }

    /**
     * Handle frame opening.
     *
     * @param e the event
     */
    public void internalFrameOpened(InternalFrameEvent e) {
    }

    /**
     * Adjust the size of the frame to accomodate the main size of the
     * application.
     */
    public void adjust() {
        MainFrame mf = EIGlobals.getInstance().getMainFrame();
        Dimension internalDesktopSize = mf.getDesktopSize();
        Dimension formSize = form.getSize();
        Dimension thisSize = this.getSize();

        /*
        EIGlobals.getInstance().log("Init...");
        EIGlobals.getInstance().log("internal dekstop size = " +
                                     internalDesktopSize.toString());
        EIGlobals.getInstance().log("form size = " + formSize.toString());
        EIGlobals.getInstance().log("frame size = " + thisSize.toString());
        */

        // adjust internal frame to fit panel
        if (formSize.width > thisSize.width) {
            //EIGlobals.getInstance().log("adjusting width");
            thisSize.width = formSize.width;
        }

        if (formSize.height > thisSize.height) {
            //EIGlobals.getInstance().log("adjusting height");
            thisSize.height = formSize.height;
        }
        /*
        EIGlobals.getInstance().log("Middle...");
        EIGlobals.getInstance().log("internal dekstop size = " +
                                    internalDesktopSize.toString());
        EIGlobals.getInstance().log("form size = " + formSize.toString());
        EIGlobals.getInstance().log("frame size = " + thisSize.toString());
        */

        // adjust internal frame to fit desktop
        if (thisSize.width > internalDesktopSize.width) {
            //EIGlobals.getInstance().log("adjusting frame width");
            thisSize.width = internalDesktopSize.width;
            thisSize.height = internalDesktopSize.height - 15;
        }

        if (thisSize.height > internalDesktopSize.height) {
            //EIGlobals.getInstance().log("adjusting frame height");
            thisSize.height = internalDesktopSize.height;
            thisSize.width = internalDesktopSize.width - 15;
        }

        /*
        EIGlobals.getInstance().log("End...");
        EIGlobals.getInstance().log("internal dekstop size = " +
                                    internalDesktopSize.toString());
        EIGlobals.getInstance().log("form size = " + formSize.toString());
        EIGlobals.getInstance().log("frame size = " + thisSize.toString());
        */

        this.setSize(thisSize);
    }

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none

    // ------------------------------------------------------------------------
    // ------------------------------------------------ NetBeans Generated Code
    // ------------------------------------------------------------------------
    // TAKE EXTREME CARE MODIFYING CODE BELOW THIS LINE


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        pack();
    }
    // </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
