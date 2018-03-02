/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/TumorGeneticsAllelePairDTOTableModel.java,v 1.1 2007/04/30 15:50:51 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtb.TumorGeneticsDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.comparators.TumorGeneticsDTOTableModelComparator;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>TumorGenetics</code> objects and ancillary
 * data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:51
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/TumorGeneticsAllelePairDTOTableModel.java,v 1.1 2007/04/30 15:50:51 mjv Exp
 */
public class TumorGeneticsAllelePairDTOTableModel<T extends TumorGeneticsDTO> extends DTOTableModel<T> {

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
    public TumorGeneticsAllelePairDTOTableModel(List<T> arr, List<String> columnNames) {
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
                return dto.getAllelePairKey();
            case 1:
                return dto.getDataBean().get(EIConstants.ALLELE1_KEY);
            case 2:
                return dto.getDataBean().get(EIConstants.ALLELE1_SYMBOL);
            case 3:
                return dto.getDataBean().get(EIConstants.ALLELE1_TYPE);
            case 4:
                return dto.getDataBean().get(EIConstants.ALLELE2_KEY);
            case 5:
                return dto.getDataBean().get(EIConstants.ALLELE2_SYMBOL);
            case 6:
                return dto.getDataBean().get(EIConstants.ALLELE2_TYPE);
            case 7:
                return dto.getDataBean().get(EIConstants.MARKER_SYMBOL);
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
        /*
        if (arr == null) {
            return;
        }
        
        T dto = arr.get(row);
        
        switch (column) {
            case 0:
                try {
                    dto.setAllelePairKey((Long)aValue);
                } catch (Exception e) {
                }
                break;
            case 1:
                dto.getDataBean().put(EIConstants.ALLELE1_KEY, aValue);
                break;
            case 2:
                dto.getDataBean().put(EIConstants.ALLELE1_SYMBOL, aValue);
                break;
            case 3:
                dto.getDataBean().put(EIConstants.ALLELE2_KEY, aValue);
                break;
            case 4:
                dto.getDataBean().put(EIConstants.ALLELE2_SYMBOL, aValue);
                break;
            case 5:
                dto.getDataBean().put(EIConstants.MARKER_SYMBOL, aValue);
                break;
            default:
                break;
        }
        
        arr.set(row, dto);
        fireTableRowsUpdated(row, row);
        */
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
                             new TumorGeneticsDTOTableModelComparator<T>(col, 
                                    ascending));
        }
    }
    
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
