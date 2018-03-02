/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/FadingPanel.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.Timer;
import org.jax.mgi.mtb.ei.gui.*;

/**
 * A component which can fade in and out.
 *
 * @author mjv
 * @date 2007/04/30 15:50:44
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/FadingPanel.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 */
public class FadingPanel extends JComponent implements ActionListener {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private Timer ticker = null;
    private int alpha = 0;
    private int step;
    private FadeListener fadeListener;


    // ----------------------------------------------------------- Constructors

    /**
     * Constructor.
     *
     * @param fadeListener the listener
     */
    public FadingPanel(FadeListener fadeListener) {
        this.fadeListener = fadeListener;
}


    // --------------------------------------------------------- Public Methods

    /**
     * Set whether or not the component should be visible.
     *
     * @param visible whether or not the component should be visible
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            if (ticker != null) {
                ticker.stop();
            }
            alpha = 0;
            step = 50;
            ticker = new Timer(1, this);
            ticker.start();
        } else {
            if (ticker != null) {
                ticker.stop();
                ticker = null;
            }
        }
    }

    /**
     * Switch from fade in to fade out and vice-versa.
     */
    public void switchDirection() {
        step = -step;
        ticker.start();
    }

    /**
     * Handles events, in this case the direction of fade (in or out).
     *
     * @param e the event
     */
    public void actionPerformed(ActionEvent e) {
        alpha += step;
        if (alpha >= 255) {
            alpha = 255;
            ticker.stop();
            fadeListener.fadeOutFinished();
        } else if (alpha < 0) {
            alpha = 0;
            ticker.stop();
            fadeListener.fadeInFinished();
        }
        repaint();
    }


    // ------------------------------------------------------ Protected Methods

    /**
     * Overriden to paint the tansparency effect.
     *
     * @param g the graphics context
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(33, 72, 152, alpha));
        Rectangle clip = g.getClipBounds();
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
    }

    // -------------------------------------------------------- Private Methods
    // none
}
