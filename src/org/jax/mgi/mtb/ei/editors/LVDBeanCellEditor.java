/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/editors/LVDBeanCellEditor.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.editors;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.jax.mgi.mtb.ei.renderers.LVDBeanListCellRenderer;
import org.jax.mgi.mtb.ei.models.LVDBeanListModel;
import org.jax.mgi.mtb.utils.LabelValueDataBean;

/**
 * A <code>TableCellEditor</code> specifically for displaying a
 * <code>JComboBox</code> of specified values that are all
 * <code>LabelValueDataBean</code>s.
 *
 * @author mjv
 * @date 2007/04/30 15:50:45
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/editors/LVDBeanCellEditor.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * @see javax.swing.AbstractCellEditor
 * @see javax.swing.table.TableCellEditor
 */
public class LVDBeanCellEditor<L,V,D>
       extends AbstractCellEditor
       implements TableCellEditor {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private JComboBox box;


    // ----------------------------------------------------------- Constructors

    /**
     * Constructor.
     *
     * @param beans a <code>Map</code> of <code>LabelValueDataBean</code>s.
     */
    public LVDBeanCellEditor(Map<L,LabelValueDataBean<L,V,D>> beans) {
        box = new JComboBox();
        List<LabelValueDataBean<L,V,D>> arr = new ArrayList<LabelValueDataBean<L,V,D>>(beans.values());
        LVDBeanListModel<L,V,D> model = new LVDBeanListModel<L,V,D>(arr);
        LVDBeanListCellRenderer<L,V,D> renderer = new LVDBeanListCellRenderer<L,V,D>();
        box.setModel(model);
        box.setRenderer(renderer);
    }


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
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        if (value != null) {
            box.setSelectedItem((LabelValueDataBean<L,V,D>)value);
        }

        // Return the configured component
        return box;
    }

    /**
     * This method is called when editing is completed. It must return the new
     * value to be stored in the cell.
     *
     * @return the new value to store
     */
    public Object getCellEditorValue() {
        return box.getSelectedItem();
    }

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
