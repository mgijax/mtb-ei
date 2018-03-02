/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/PathologyImageDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.io.File;
import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtb.ImagesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesDTO;
import org.jax.mgi.mtb.ei.EIConstants;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>PathologyImagesDTO</code> objects and ancillary data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:49
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/PathologyImageDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 */
public class PathologyImageDTOTableModel<T extends PathologyImagesDTO> extends DTOTableModel<T> {

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
    public PathologyImageDTOTableModel(List<T> arr, List<String> columnNames) {
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
        ImagesDTO dtoI = (ImagesDTO)dto.getDataBean().get(EIConstants.IMAGE_DTO);
        
        switch (column) {
            case 0:
                if (dto.getDataBean().get(EIConstants.LOCAL_IMAGE) != null) {
                    return ((File)dto.getDataBean().get(EIConstants.LOCAL_IMAGE)).toString();
                } else {
                    return dtoI.getUrl() + "/" + dtoI.getUrlPath() + "/" + dtoI.getMediumResName();
                }
            case 1:
                return dtoI.getImagesKey();
            case 2:
                return dto.getCaption();
            case 3:
                return dto.getNotes();
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
