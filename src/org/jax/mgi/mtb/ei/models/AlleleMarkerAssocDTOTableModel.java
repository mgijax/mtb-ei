/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/AlleleMarkerAssocDTOTableModel.java,v 1.1 2007/04/30 15:50:48 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleMarkerAssocDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>AlleleMarkerAssocDTO</code> objects and ancillary data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:48
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/AlleleMarkerAssocDTOTableModel.java,v 1.1 2007/04/30 15:50:48 mjv Exp
 */
public class AlleleMarkerAssocDTOTableModel<T extends AlleleMarkerAssocDTO> extends DTOTableModel<T> {
    
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
    public AlleleMarkerAssocDTOTableModel(List<T> arr, List<String> columnNames) {
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
        DataBean dtoS = dto.getDataBean();
        MarkerDTO dtoM = (MarkerDTO)dtoS.get(EIConstants.MARKER_DTO);
        LabelValueBean<String,Long> lvb = 
                (LabelValueBean<String,Long>)dtoS.get(
                        EIConstants.ALLELE_MARKER_ASSOC_TYPE_BEAN);
        
        switch (column) {
            case 0:
                return dto.getMarkerKey();
            case 1:
                return dtoM.getSymbol();
            case 2:
                return dtoM.getName();
            case 3:
                return lvb == null ? "" : lvb.getLabel();
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
        // not editable
        if (arr == null) {
            return;
        }
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
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
