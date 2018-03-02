/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/LVBeanCellRenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.renderers;

import java.awt.Component;
import javax.swing.JTable;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * Renders the label attribute of a <code>LabelValueBean</code> in the cell of
 * a <code>JTable</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:51:00
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/LVBeanCellRenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 */
public class LVBeanCellRenderer<L,V> extends DTORenderer {
    
    // -------------------------------------------------------------- Constants
    // none
    
    // ----------------------------------------------------- Instance Variables
    // none

    // ----------------------------------------------------------- Constructors
    // none
    
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
    
        if (value != null) {
            LabelValueBean<L,V> bean = (LabelValueBean<L,V>)value;
            this.setText(bean.getLabel()+"");
        }

        return this;
    }
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none    
}
