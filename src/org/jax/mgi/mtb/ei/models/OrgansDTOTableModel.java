/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/OrgansDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtb.OrganDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.comparators.OrgansDTOTableModelComparator;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>OrganDTO</code> objects and ancillary data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:49
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/OrgansDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 */
public class OrgansDTOTableModel<T extends OrganDTO> extends DTOTableModel<T> {

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
    public OrgansDTOTableModel(List<T> arr, List<String> columnNames) {
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
                return dto.getOrganKey();
            case 1:
                return (LabelValueBean)dto.getDataBean().get(
                                           EIConstants.ANATOMICAL_SYSTEM_BEAN);
            case 2:
                return dto.getName();
            case 3:
                return (LabelValueBean)dto.getDataBean().get(
                                           EIConstants.ORGAN_BEAN);
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
                dto.setOrganKey((Long)aValue);
                break;
            case 1:
                if (aValue != null) {
                    LabelValueBean<String,Long> bean = (LabelValueBean<String,Long>)aValue;
                    dto.setAnatomicalSystemKey(new Long(bean.getValue()));
                    dto.getDataBean().put(EIConstants.ANATOMICAL_SYSTEM_BEAN, bean);
                }
                break;
            case 2:
                dto.setName((String)aValue);
                break;
            case 3:
                if (aValue != null) {
                    LabelValueBean<String,Long> bean = (LabelValueBean<String,Long>)aValue;
                    dto.setOrganParentKey(new Long(bean.getValue()));
                    dto.getDataBean().put(EIConstants.ORGAN_BEAN, bean);
                }
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
                             new OrgansDTOTableModelComparator<T>(col, 
                                    ascending));
        }
    }
    
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
