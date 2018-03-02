/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/gui/WatermarkDesktopPane.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JDesktopPane;
import org.jax.mgi.mtb.ei.EIGlobals;

/**
 * Essentially a <code>JDesktopPane</code> with an image.
 *
 * @author mjv
 * @date 2007/04/30 15:50:46
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/gui/WatermarkDesktopPane.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 */
public class WatermarkDesktopPane extends JDesktopPane {
    
    // -------------------------------------------------------------- Constants
    // none
    
    // ----------------------------------------------------- Instance Variables
    
    private Image bgImage = null;


    // ----------------------------------------------------------- Constructors
    
    /** 
     * Creates a new instance of WatermarkDesktopPane.
     *
     * @param i the <code>Image</code> to draw on the desktop
     */
    public WatermarkDesktopPane(Image i) {
        super();
        setImage(i);
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Set the image to use.
     *
     * @param i the image
     */
    public void setImage(Image i) {
        this.bgImage = i;
    }
    
    /**
     * Get the image that is being used.
     *
     * @return get the image
     */
    public Image getImage() {
        return this.bgImage;
    }
    
    
    /**
     * Paint the component and the image in the lower right corner.
     *
     * @param g the <code>Graphics</code> context
     */
    public void paintComponent(Graphics g) {
        if (bgImage != null) {
            int width = this.getWidth();
            int height = this.getHeight();
            int imageW = bgImage.getWidth(null);
            int imageH = bgImage.getHeight(null);
            int x = width - imageW - 10;
            int y = height - imageH - 10;
            g.drawImage(bgImage, x, y, this);
            
            y = 10;
            x = width - 200;
            
            g.drawString("Database: " + EIGlobals.getInstance().getDBType(), x, y);
            g.drawString("User: " + EIGlobals.getInstance().getMTBUsersDTO().getFullName(), x, y + 15);
        }
    }
    

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
} 
