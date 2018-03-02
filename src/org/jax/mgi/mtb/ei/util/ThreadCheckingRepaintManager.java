/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/util/ThreadCheckingRepaintManager.java,v 1.1 2007/04/30 15:51:24 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.util;

import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * A class to check when painting is occuring and if it is doing so on the
 * wrong thread.
 *
 * @author mjv
 * @date 2007/04/30 15:51:24
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/util/ThreadCheckingRepaintManager.java,v 1.1 2007/04/30 15:51:24 mjv Exp
 */

public class ThreadCheckingRepaintManager extends RepaintManager {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private int nTabCount = 0;
    private boolean bShowing = false;


    // ----------------------------------------------------------- Constructors

    /**
     * Default constructor.
     */
    public ThreadCheckingRepaintManager() {
        super();
    }

    /**
     * Constructor.
     *
     * @param bShowing to check if the component is showing
     */
    public ThreadCheckingRepaintManager(boolean bShowing) {
        super();
        this.bShowing = bShowing;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Add an invalid component.
     *
     * @param jComponent the <code>JComponent</code>
     */
    public synchronized void addInvalidComponent(JComponent jComponent) {
        checkThread(jComponent);
        super.addInvalidComponent(jComponent);
    }

    /**
     * Add an area of a component as a "dirty" region.
     *
     * @param jComponent the <code>JComponent</code>
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h the height
     */
    public synchronized void addDirtyRegion(JComponent jComponent,
                                            int x, int y, int w, int h) {
        checkThread(jComponent);
        super.addDirtyRegion(jComponent, x, y, w, h);
    }

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Check the current thread to see if painting is occurring on the
     * incorrect thread.
     *
     * @param jComponent the <code>JComponent</code>
     */
    private void checkThread(JComponent c) {
        if (!SwingUtilities.isEventDispatchThread() && checkIsShowing(c)) {
            System.out.println("----------Wrong Thread START");
            System.out.println(getStracktraceAsString(new Exception()));
            dumpComponentTree(c);
            System.out.println("----------Wrong Thread END");
        }
    }

    /**
     * Get an <code>Exception</code>'s stack trace as a string.
     *
     * @param e the <code>Exception</code>
     * @return the stack trace as a string
     */
    private String getStracktraceAsString(Exception e) {
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        e.printStackTrace(printStream);
        printStream.flush();
        return byteArrayOutputStream.toString();
    }

    /**
     * Check if the specified component is showing or not.
     *
     * @param jComponent the <code>JComponent</code>
     * @return <code>true</code> if the component is visible,
     *         <code>false</code> otherwise
     */

    private boolean checkIsShowing(JComponent c) {
        if (this.bShowing == false) {
            return true;
        } else {
            return c.isShowing();
        }
    }

    /**
     * Display the component tree.
     *
     * @param c the <code>Component</code>
     */
    private void dumpComponentTree(Component c) {
        System.out.println("----------Component Tree");
        resetTabCount();
        for (; c != null; c = c.getParent()) {
            printTabIndent();
            System.out.println(c.toString());
            printTabIndent();
            System.out.println("Showing:" + c.isShowing() +
                               " Visible: " + c.isVisible());
            incrementTabCount();
        }
    }

    /**
     * Resets the tab count.
     */
    private void resetTabCount() {
        this.nTabCount = 0;
    }

    /**
     * Increment the tab count.
     */
    private void incrementTabCount() {
        this.nTabCount++;
    }

    /**
     * Print the indentation for display purposes with tabs.
     */
    private void printTabIndent() {
        for (int i = 0; i < this.nTabCount; i++) {
            System.out.print("\t");
        }
    }
}
