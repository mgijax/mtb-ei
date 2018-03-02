/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/MarkerLabelDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerLabelDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.comparators.MarkerLabelDTOTableModelComparator;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>MarkerLabelDTO</code> objects and ancillary data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:49
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/MarkerLabelDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 */
public class MarkerLabelDTOTableModel<T extends MarkerLabelDTO> extends DTOTableModel<T> {

    // -------------------------------------------------------------- Constants
    // none
    
    // ----------------------------------------------------- Instance Variables
    // none

    // ----------------------------------------------------------- Constructors
    
    /**
     * Constructor.
     *
     * @param arr the data
     * @param columnNames the names of the columns
     */
    public MarkerLabelDTOTableModel(List<T> arr, List<String> columnNames) {
        super(arr, columnNames);
    }
    
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Get the value at the specified cell (row, column).
     *
     * @param row the row
     * @param column the column
     * @return the value at the specified column
     */
    public Object getValueAt(int row, int column) {
        if (arr == null) {
            return null;
        }
        
        T dto = arr.get(row);
        
        switch (column) {
            case 0:
                return dto.getLabel();
            case 1:
                // type
                LabelValueBean beanType = 
                        (LabelValueBean)dto.getDataBean().get(
                                                 EIConstants.LABEL_TYPE_BEAN);
                return beanType;
            default:
                return "";
        }
    }
    
    /**
     * Set the value at the specified cell (row, column).
     * <b>This model is not editable.</b>
     *
     * @param aValue the value to set
     * @param row the row
     * @param column the column
     */
    public void setValueAt(Object aValue, int row, int column) {
        // read only
    }
    
    /**
     * Overriden to make a read only table model.
     *
     * @param row the row
     * @param column the column
     */
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    /**
     * Sort the specified column in the specified order.
     *
     * @param col The column index
     * @param ascending Set to <code>true</code> if the column should be sorted
     * in ascending order, <code>false</code> otherwise
     */
    public void sortColumn(int col, boolean ascending) {
        if (arr != null) {
            Collections.sort(arr, 
                           new MarkerLabelDTOTableModelComparator<T>(col, 
                                    ascending));
        }
    }
    
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
