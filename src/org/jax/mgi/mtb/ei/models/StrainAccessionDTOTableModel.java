/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/StrainAccessionDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.comparators.StrainAccessionDTOTableModelComparator;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>AccessionDTO</code> objects and ancillary data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:49
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/StrainAccessionDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 */
public class StrainAccessionDTOTableModel<T extends AccessionDTO> extends DTOTableModel<T> {
    
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
    public StrainAccessionDTOTableModel(List<T> arr, List<String> columnNames) {
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
                LabelValueBean bean = 
                        (LabelValueBean)dto.getDataBean().get(
                                                        EIConstants.SITE_INFO);
                return bean;
            case 1:
                return dto.getAccID();
            default:
                return "";
        }
    }
    
    /**
     * Set the value at the specified cell (row, column).
     *
     * @param aValue the value to set
     * @param row the row
     * @param column the column
     */
    public void setValueAt(Object aValue, int row, int column) {
        if (arr == null) {
            return;
        }
        
        T dto = arr.get(row);
        
        switch (column) {
            case 0:
                if (aValue != null) {
                    final LabelValueBean<String,Long> bean = (LabelValueBean<String,Long>)aValue;
                    dto.setSiteInfoKey(new Long(bean.getValue()));
                    dto.getDataBean().put(EIConstants.SITE_INFO, bean);
                }
                break;
            case 1:
                dto.setAccID((String)aValue);
                break;
            default:
                break;
        }
        
        arr.set(row, dto);
        fireTableRowsUpdated(row, row);
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
                             new StrainAccessionDTOTableModelComparator<T>(col, 
                                                                   ascending));
        }
    }

    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
