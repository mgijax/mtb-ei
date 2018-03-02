/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/GradientPanel.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.jax.mgi.mtb.ei.gui.*;

/**
 * A fast painting panel with a gradient background.
 *
 * @author mjv
 * @date 2007/04/30 15:50:44
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/GradientPanel.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 */
public class GradientPanel extends JPanel {

    // -------------------------------------------------------------- Constants

    private Color gradientStart1 = new Color(33, 72, 152);
    private Color gradientEnd1 = new Color(110, 138, 198);
    private Color gradientStart2 = new Color(33, 72, 152, 200);
    private Color gradientEnd2 = new Color(110, 138, 198, 255);


    // ----------------------------------------------------- Instance Variables

    private BufferedImage mask;

    // ----------------------------------------------------------- Constructors
    // none


    // --------------------------------------------------------- Public Methods


    /**
     * Overriden to paint the gradient effect.
     *
     * @param g the graphics context
     */
    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D)g;

        BufferedImage img = getMask();
        graphics.drawImage(img, 0, 0, this);
    }

    public void setStartGradient(Color startGradient) {
        this.gradientStart1 = startGradient;

        if (gradientStart1 == null) {
            gradientStart2 = null;
        } else {
            gradientStart2 = new Color(gradientStart1.getRed(),
                    gradientStart1.getGreen(),
                    gradientStart1.getBlue(),
                    200);
        }
    }

    public Color getStartGradient() {
        return this.gradientStart1;
    }

    public void setEndGradient(Color endGradient) {
        this.gradientEnd1 = endGradient;

        if (gradientEnd1 == null) {
            gradientEnd2 = null;
        } else {
            gradientEnd2 = new Color(gradientEnd1.getRed(),
                    gradientEnd1.getGreen(),
                    gradientEnd1.getBlue(),
                    255);
        }
    }

    public Color getEndGradient() {
        return this.gradientEnd1;
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Fast paint and return of a buffered image.
     *
     * @return the <code>BufferedImage</code>
     */
    private synchronized BufferedImage getMask() {
        int width = getWidth();
        int height = getHeight();

        if (mask == null ||
                width != mask.getWidth() ||
                height != mask.getHeight()) {

            Rectangle rec = new Rectangle(width, height);

            mask = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = mask.createGraphics();

            Paint oldPainter = g.getPaint();

            GradientPaint painter = new GradientPaint(0, 0, gradientStart1,
                    0, height, gradientEnd1,
                    true);

            g.setPaint(painter);
            g.fill(rec);

            painter = new GradientPaint(0, 0, gradientEnd2,
                    0, height / 2, gradientStart2, true);
            g.setPaint(painter);
            g.fill(rec);

            painter = new GradientPaint(0, height / 2, gradientStart2,
                    0, height, gradientEnd2,
                    true);
            g.setPaint(painter);
            g.fill(rec);

            g.setPaint(oldPainter);

            g.dispose();
        }

        return mask;
    }
}