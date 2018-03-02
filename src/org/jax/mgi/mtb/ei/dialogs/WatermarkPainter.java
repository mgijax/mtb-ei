/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/WatermarkPainter.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.net.URL;
import javax.imageio.ImageIO;
import org.jax.mgi.mtb.ei.util.Utils;

/**
 * This class is the abstract base class for objects that know how to paint a
 * <code>WatermarkViewport</code>. Minimally, subclasses must override the
 * <code>paint</code> method.
 * <p>
 * Note: <code>WatermarkPainter</code>s are not designed to be shared between
 * viewports.
 * <p>
 * <i>Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.</i>
 *
 * @author mjv
 * @author Shannon Hickey
 * @date 2007/04/30 15:50:45
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/WatermarkPainter.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 */
public abstract class WatermarkPainter
       implements ActionListener, HierarchyListener {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>WatermarkViewport</code> that this painter is associated with.
     */
    private WatermarkViewport paintComp;


    // ----------------------------------------------------------- Constructors
    // none


    // --------------------------------------------------------- Public Methods

    /**
     * Called when this painter is registered with a viewport, to notify us of
     * the component that we'll be painting on. This will replace the previous
     * component if non-null.
     *
     * @param comp the viewport that this painter is associated with, or null
     *             to simply remove the existing component
     */
    final public void setComponent(WatermarkViewport comp) {
        if (paintComp != null) {
            paintComp.removeHierarchyListener(this);
            stop();
        }

        paintComp = comp;

        if (paintComp != null) {
            if (paintComp.isShowing()) {
                start();
            }
            paintComp.addHierarchyListener(this);
        }
    }

    /**
     * Return the list of commands that can be sent to this painter. These
     * commands will be sent in the form of an actionCommand on an
     * <code>java.awt.ActionEvent</code> sent to <code>actionPerformed</code>.
     *
     * @return the commands which this painter understands
     */
    public String[] getCommands() {
        return null;
    };

    /**
     * Perform the action given by the actionCommand of the specified
     * <code>ActionEvent</code>. Should support the command list returned by
     * <code>getCommands</code>.
     *
     * @param ae the <code>ActionEvent</code> containing a command to perform
     */
    public void actionPerformed(ActionEvent ae) {
    }

    /**
     * Listens for hierarchy events on the current viewport to start or stop
     * this painter when the component's showing state changes.
     */
    public void hierarchyChanged(HierarchyEvent he) {
        if ((he.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) !=
            HierarchyEvent.SHOWING_CHANGED) {
            return;
        }

        if (paintComp.isShowing()) {
            start();
        } else {
            stop();
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the viewport associated with this painter.
     *
     * @return the viewport associated with this painter
     */
    protected final WatermarkViewport getComponent() {
        return paintComp;
    }

    /**
     * Starts any animation that this painter might perform. This is called
     * when this painter is registered with anew viewport, if the viewport's
     * showing status is true. It will then be called every time that
     * component's status changes to true.
     */
    protected void start() {
    }

    /**
     * Stops any animation that this painter might be performing. This is
     * called when this painter is un-registered, or when the showing status of
     * the current viewport changes to false.
     */
    protected void stop() {
    }

    /**
     * Paint onto the graphics object indicated by the parameter. This method
     * can query the component returned by <code>getComponent</code> for size
     * information. The component returned is guaranteed to be non-null as
     * <code>setComponent</code> will always be called with a non-null
     * component before any painting is done.
     *
     * @param g the graphics object on which to paint
     */
    protected abstract void paint(Graphics g);

    /**
     * Convenience method to load an image from the given URL. This
     * implementation uses <CODE>ImageIO</CODE> to load the image and thus
     * returns <CODE>BufferedImages</CODE>.
     *
     * @param imageURL the URL to an image
     * @return the image or null if the image couldn't be loaded
     */
    protected static Image getImage(URL imageURL) {
        Image image = null;

        try {
            // use ImageIO to read in the image
            image = ImageIO.read(imageURL);
        } catch (Exception ioe) {
            Utils.log(ioe);
        }

        return image;
    }


    // -------------------------------------------------------- Private Methods
    // none
}
