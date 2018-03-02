/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/FadeListener.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import org.jax.mgi.mtb.ei.gui.*;

/**
 * A very simple interface for listneing to fade ins and outs.
 *
 * @author mjv
 * @date 2007/04/30 15:50:44
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/FadeListener.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 */
public interface FadeListener {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables
    // none

    // ----------------------------------------------------------- Constructors
    // none

    // --------------------------------------------------------- Public Methods

    /**
     * Fade in has finished.
     */
    public void fadeInFinished();


    /**
     * Fade out has finished.
     */
    public void fadeOutFinished();


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
