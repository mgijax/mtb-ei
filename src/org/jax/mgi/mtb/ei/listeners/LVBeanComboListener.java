/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/listeners/LVBeanComboListener.java,v 1.1 2007/04/30 15:50:47 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComboBox;
import javax.swing.text.Position;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * Listens for <code>KeyEvent</code>s for <code>JComboBox</code>s that have a
 * custom model of <code>LabelValueBean</code>s.
 *
 * @author mjv
 * @date 2007/04/30 15:50:47
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/listeners/LVBeanComboListener.java,v 1.1 2007/04/30 15:50:47 mjv Exp
 * @see java.awt.event.KeyListener
 */
public class LVBeanComboListener<L,V> implements KeyListener {
    
    // -------------------------------------------------------------- Constants
    // none
    
    // ----------------------------------------------------- Instance Variables

    private String prefix = "";
    private long lastTime = 0L;
    
    // ----------------------------------------------------------- Constructors
    // none
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Invoked when a key has been typed.
     *
     * Moves the keyboard focus to the first element whose first letter matches
     * the alphanumeric key pressed by the user. Subsequent same key presses
     * move the keyboard focus to the next object that starts with the same 
     * letter.
     *
     * @param e The KeyEvent
     */
    public void keyTyped(KeyEvent e) {
        JComboBox src = (JComboBox)e.getSource();
        
        LVBeanListModel<L,V> model = (LVBeanListModel<L,V>)src.getModel();
        
        if (model.getSize() == 0 || e.isAltDown() || 
            e.isControlDown() || e.isMetaDown()) {
            // Nothing to select
            return;
        }
        boolean startingFromSelection = true;
        
        char c = e.getKeyChar();
        
        long time = e.getWhen();
        int startIndex;
        if (time - lastTime < 1000L && 
                (prefix.length() != 1 || c != prefix.charAt(0))) {
            prefix += c;
            startIndex = src.getSelectedIndex();
        } else {
            prefix = "" + c;
            startIndex = src.getSelectedIndex() + 1;
        }
        lastTime = time;
        
        if (startIndex < 0 || startIndex >= model.getSize()) {
            startingFromSelection = false;
            startIndex = 0;
        }
        
        int index = getNextMatch(model, prefix, startIndex, 
                                 Position.Bias.Forward);
        
        if (index >= 0) {
            src.setSelectedIndex(index);
        } else if (startingFromSelection) { 
            // wrap
            index = getNextMatch(model, prefix, 0, Position.Bias.Forward);
            if (index >= 0) {
                src.setSelectedIndex(index);
            }
        }
    }
    
    /**
     * Invoked when a key has been pressed.
     *
     * @param e The KeyEvent
     */
    public void keyPressed(KeyEvent e) {
    }
    
    /**
     * Invoked when a key has been released. See the class description for a 
     * definition of a key released event.
     *
     * @param e The KeyEvent
     */
    public void keyReleased(KeyEvent e) {
    }
    
    /**
     * Returns the next list element that starts with a prefix.
     *
     * @param model the LVBeanListModel
     * @param prefix the string to test for a match
     * @param startIndex the index for starting the search
     * @param bias the search direction, either Position.Bias.Forward or 
     *        Position.Bias.Backward.
     * @return the index of the next list element that starts with the prefix;
     *          otherwise -1
     * @exception IllegalArgumentException if prefix is null or startIndex is 
     *            out of bounds
     */
    public int getNextMatch(LVBeanListModel<L,V> model, String prefix, 
                            int startIndex, Position.Bias bias) {
        int max = model.getSize();
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        if (startIndex < 0 || startIndex >= max) {
            throw new IllegalArgumentException();
        }
        prefix = prefix.toUpperCase();
        
        // start search from the next element after the selected element
        int increment = (bias == Position.Bias.Forward) ? 1 : -1;
        int index = startIndex;
        do {
            Object o = model.getElementAt(index);
            
            if (o != null) {
                LabelValueBean bean = (LabelValueBean)o;
                String string = bean.getLabel() + "";

                if (string != null) {
                    string = string.toUpperCase();
                }
                
                if (string != null && string.startsWith(prefix)) {
                    return index;
                }
            }
            index = (index + increment + max) % max;
        } while (index != startIndex);
        return -1;
    }
    
    // ------------------------------------------------------ Protected Methods
    // none    
    
    // -------------------------------------------------------- Private Methods
    // none    
}
