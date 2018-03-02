/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/WaitAnimation.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * A component used for waiting.
 *
 * @author mjv
 * @date 2007/04/30 15:50:45
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/WaitAnimation.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 */
public class WaitAnimation extends JComponent implements ActionListener {

    // -------------------------------------------------------------- Constants

    private final String PATH = "/org/jax/mgi/mtb/ei/resources/img/";


    // ----------------------------------------------------- Instance Variables

    private Image[] animation;
    private int index;
    private int direction;


    // ----------------------------------------------------------- Constructors

    /**
     * Default constructor.
     */
    public WaitAnimation() {
        setOpaque(false);

        index = 0;
        direction = 1;

        MediaTracker tracker = new MediaTracker(this);

        animation = new Image[6];

        for (int i = 0; i < 6; i++) {
            animation[i] = readImage("auth_" + String.valueOf(i) + ".png");
            tracker.addImage(animation[i], i);
        }

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
        }

        Timer animationTimer = new Timer(10, this);
        animationTimer.start();
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Paint the component.
     *
     * @param g the graphics context
     */
    public void paintComponent(Graphics g) {
        int x = (int) ((getWidth() - animation[index].getWidth(this)) / 2.0);
        int y = (int) ((getHeight() - animation[index].getHeight(this)) / 2.0);

        g.drawImage(animation[index], x, y, this);
    }

    /**
     * Handles actions that occur to this component.
     *
     * @param e the action
     */
    public void actionPerformed(ActionEvent e) {
        index += direction;

        if (index > 5) {
            index = 5;
            direction = -1;
        } else if (index < 0) {
            index = 0;
            direction = 1;
        }
    }

    /**
     * Read the image.
     *
     * @param fileName the name of the file to read
     * @return the image
     */
    public Image readImage(String fileName) {
        ImageIcon i = new ImageIcon(getClass().getResource(PATH + fileName));
        return i.getImage();
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
