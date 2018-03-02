/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/ButtonCellRenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.renderers;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * A <code>JButton</code> as a <code>TableCellRenderer</code> component.
 *
 * @author mjv
 * @date 2007/04/30 15:51:00
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/ButtonCellRenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 */
public class ButtonCellRenderer extends JButton implements TableCellRenderer {
    
    // -------------------------------------------------------------- Constants
    // none
    
    // ----------------------------------------------------- Instance Variables
    // none
    
    // ----------------------------------------------------------- Constructors
    
    /**
     * Constructor
     *
     * @param icon the icon for the button
     */
    public ButtonCellRenderer(Icon icon) {
        this(null, icon);
    }
    
    /**
     * Constructor
     *
     * @param text the button text
     */
    public ButtonCellRenderer(String text) {
        this(text, null);
    }
    
    /**
     * Constructor
     *
     * @param text the button text
     * @param icon the icon for the button
     */
    public ButtonCellRenderer(String text, Icon icon) {
        super(text, icon);
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(1,1,1,1));
    }
    

    // --------------------------------------------------------- Public Methods
    
    /**
     * This method is called to get the component that will render the data in
     * the table at the specified row and column.
     *
     * @param table the table
     * @param value the value of the cell
     * @param isSelected whether or not the cell is selected
     * @param hasFocus whether or not the cell has focus
     * @param rowIndex the row
     * @param vColIndex the column
     * @return the component to render
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {
        
        return this;
    }
    
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none    
}
