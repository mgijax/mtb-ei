/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/GradientPainter.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import org.jax.mgi.mtb.ei.gui.*;

/**
 * An extension of <code>WatermarkPainter</code> that paints the entire
 * component with a gradient.
 * <p>
 * <i>Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.</i>
 *
 * @author mjv
 * @author Shannon Hickey
 * @date 2007/04/30 15:50:44
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/GradientPainter.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 */
public class GradientPainter extends WatermarkPainter {

    // -------------------------------------------------------------- Constants

    /**
     * The first color to use in the gradient
     */
    private Color colorStart = Color.WHITE;

    /**
     * The second color to use in the gradient
     */
    private Color colorFinish = Color.WHITE;

    // ----------------------------------------------------- Instance Variables
    // none

    // ----------------------------------------------------------- Constructors

    /**
     * Constructor.
     *
     * @param colorStart the start color
     * @param colorFinish the finish color
     */

    public GradientPainter(Color colorStart, Color colorFinish) {
        super();

        this.colorStart = colorStart;
        this.colorFinish = colorFinish;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Paint the component with a background gradient.
     *
     * @param g the <code>Graphics</code> context
     */
    public void paint(Graphics g) {
        int width = getComponent().getWidth();
        int height = getComponent().getHeight();

        // create the gradient paint
        GradientPaint paint = new GradientPaint(0, 0, colorStart,
                                                width, height, colorFinish,
                                                true);

        // we need to cast to Graphics2D for this operation
        Graphics2D g2d = (Graphics2D)g;

        // save the old paint
        Paint oldPaint = g2d.getPaint();

        // set the paint to use for this operation
        g2d.setPaint(paint);

        // fill the background using the paint
        g2d.fillRect(0, 0, width, height);

        // restore the original paint
        g2d.setPaint(oldPaint);
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
