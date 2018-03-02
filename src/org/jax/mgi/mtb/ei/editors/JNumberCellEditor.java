/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/editors/JNumberCellEditor.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.editors;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import org.jax.mgi.mtb.ei.EIGlobals;

/**
 * A <code>TableCellEditor</code> specifically for parsing and validating
 * JNumbers.
 *
 * @author mjv
 * @date 2007/04/30 15:50:45
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/editors/JNumberCellEditor.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * @see javax.swing.AbstractCellEditor
 * @see javax.swing.table.TableCellEditor
 */
public class JNumberCellEditor
       extends AbstractCellEditor
       implements TableCellEditor {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    /**
     * This is the component that will handle the editing of the cell value
     */
    private JComponent component = new JTextField();


    // ----------------------------------------------------------- Constructors
    // none

    // --------------------------------------------------------- Public Methods

    /**
     * This method is called when a cell value is edited by the user.  The
     * purpose is to retrieve the component for editing data in the specified
     * cell.
     *
     * @param table the table
     * @param value the value of the cell
     * @param isSelected whether or not the cell is selected
     * @param rowIndex the row
     * @param vColIndex the column
     * @return the component to render
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int rowIndex, int vColIndex) {

        // Configure the component with the specified value
        ((JTextField)component).setText((String)value);

        // Return the configured component
        return component;
    }

    /**
     * This method is called when editing is completed. It must return the new
     * value to be stored in the cell.
     *
     * @return the new value to store
     */
    public Object getCellEditorValue() {
        return ((JTextField)component).getText();
    }

    /**
     * This method is called just before the cell value is saved. If the value
     * is not valid, false should be returned.
     *
     * @return <code>true</code> if the value is valid, <code>false</code>
     *         otherwise
     */
    public boolean stopCellEditing() {
        return isValid((String)getCellEditorValue()) ? super.stopCellEditing() 
                                                     : false;
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * This method is called when a cell value is edited by the user.
     *
     * @param s the text entered for a JNumber
     * @return <code>true</code> if the JNumber is valid, <code>false</code>
     *         otherwise
     */
    private boolean isValid(String s) {
        boolean bRet = true;
        long key = EIGlobals.getInstance().getRefByAcc(s);

        if (key < 0) {
            JOptionPane.showMessageDialog(null,
                                          s + " is not a valid JNumber.");
            bRet = false;
        }

        return bRet;
    }
}