/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/AgentsDTOTableModel.java,v 1.1 2007/04/30 15:50:47 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.comparators.AgentDTOTableModelComparator;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>AgentDTO</code> objects and ancillary data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:47
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/AgentsDTOTableModel.java,v 1.1 2007/04/30 15:50:47 mjv Exp
 */
public class AgentsDTOTableModel<T extends AgentDTO> extends DTOTableModel<T> {
    
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
    public AgentsDTOTableModel(List<T> arr, List<String> columnNames) {
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
        
        final T dto = arr.get(row);
        
        switch (column) {
            case 0:
                return dto.getAgentKey();
            case 1:
                final LabelValueBean<String,Long> bean = 
                    (LabelValueBean<String,Long>)dto.getDataBean().get(EIConstants.AGENT_TYPE_BEAN);
                return bean;
            case 2:
                return dto.getName();
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
        
        final T dto = arr.get(row);
        
        switch (column) {
            case 0:
                dto.setAgentKey((Long)aValue);
                break;
            case 1:
                if (aValue != null) {
                    final LabelValueBean<String,Long> bean = (LabelValueBean<String,Long>)aValue;
                    dto.setAgentTypeKey(bean.getValue());
                    dto.getDataBean().put(EIConstants.AGENT_TYPE_BEAN, bean);
                }
                break;
            case 2:
                dto.setName((String)aValue);
                break;
            default:
                break;
        }
        
        arr.set(row, dto);
        
        fireTableRowsUpdated(row,row);
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
            Collections.sort(arr, new AgentDTOTableModelComparator<T>(col, 
                                                                   ascending));
        }
    }
    
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
