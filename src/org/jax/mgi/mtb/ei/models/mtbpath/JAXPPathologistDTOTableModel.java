/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/mtbpath/JAXPPathologistDTOTableModel.java,v 1.1 2007/04/30 15:50:52 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models.mtbpath;

import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtbpath.JAXPPathologistDTO;
import org.jax.mgi.mtb.ei.comparators.mtbpath.JAXPPathologistDTOTableModelComparator;
import org.jax.mgi.mtb.ei.models.DTOTableModel;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>JAXP_Pathologist</code> objects and ancillary data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:52
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/mtbpath/JAXPPathologistDTOTableModel.java,v 1.1 2007/04/30 15:50:52 mjv Exp
 */
public class JAXPPathologistDTOTableModel<T extends JAXPPathologistDTO> extends DTOTableModel<T> {

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
    public JAXPPathologistDTOTableModel(List<T> arr,
                                        List<String> columnNames) {
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
                return dto.getPathologistKey();
            case 1:
                return dto.getUserid();
            case 2:
                return dto.getPassword();
            case 3:
                return dto.getName();
            case 4:
                return dto.getInstitution();
            case 5:
                return dto.getAddress1();
            case 6:
                return dto.getAddress2();
            case 7:
                return dto.getCity();
            case 8:
                return dto.getStateprov();
            case 9:
                return dto.getPostalcode();
            case 10:
                return dto.getPhone();
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
                dto.setPathologistKey((Long)aValue);
                break;
            case 1:
                dto.setUserid((String)aValue);
                break;
            case 2:
                dto.setPassword((String)aValue);
                break;
            case 3:
                dto.setName((String)aValue);
                break;
            case 4:
                dto.setInstitution((String)aValue);
                break;
            case 5:
                dto.setAddress1((String)aValue);
                break;
            case 6:
                dto.setAddress2((String)aValue);
                break;
            case 7:
                dto.setCity((String)aValue);
                break;
            case 8:
                dto.setStateprov((String)aValue);
                break;
            case 9:
                dto.setPostalcode((String)aValue);
                break;
            case 10:
                dto.setPhone((String)aValue);
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
                   new JAXPPathologistDTOTableModelComparator(col, ascending));
        }
    }
    
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
