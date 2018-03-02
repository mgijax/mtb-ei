/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/DTOTableModel.java,v 1.1 2007/04/30 15:50:48 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.jax.mgi.mtb.dao.gen.TableDTO;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.table.MXColumnComparator;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>TumorTypeDTO</code> objects and ancillary data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:48
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/DTOTableModel.java,v 1.1 2007/04/30 15:50:48 mjv Exp
 */
public class DTOTableModel<T extends TableDTO> extends MXDefaultTableModel {

    // -------------------------------------------------------------- Constants
    // none
    
    // ----------------------------------------------------- Instance Variables

    protected List<T> arr = null;
    protected boolean updated;
    
    
    // ----------------------------------------------------------- Constructors
    
    /**
     * Constructor.
     *
     * @param arr the data
     * @param columns the names of the columns
     */
    public DTOTableModel(List<T> data, Vector<String> columns) {
        if (data != null) {
            this.arr = data;
        }
        setColumnIdentifiers(columns);
        this.updated = false;
    }
    
    /**
     * Constructor.
     *
     * @param arr the data
     * @param columns the names of the columns
     */
    public DTOTableModel(List<T> data, List<String> columns) {
        if (data != null) {
            this.arr = data;
        }
        setColumnIdentifiers(new Vector<String>(columns));
        this.updated = false;
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Get the value at the specified row.
     *
     * @param row the row
     * @return the value at the specified row
     */
    public T getDTO(int row) {
        if (arr == null) {
            return null;
        }
        
        return arr.get(row);
    }
    
    /**
     * Get the number of rows in the model.
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return (arr == null) ? 0 : arr.size();
    }
    
    /**
     * Remove the specified row from the model.
     *
     * @param r the row to remove
     */
    public void removeRow(int r) {
        T dto = arr.get(r);
        
        if (dto.isNew()) {
            arr.remove(r);
            this.fireTableDataChanged();
        } else {
            dto.isOld(true);
            arr.set(r, dto);
            this.fireTableDataChanged();
        }
    }
    
    /**
     * Add the DTO to the model.
     *
     * @param dto the DTO to add
     */
    public void addRow(T dto) {
        if (arr == null) {
            arr = new ArrayList<T>();
        }
        arr.add(dto);
        this.fireTableDataChanged();
    }
    
    /**
     * Set the DTO at the specified row in the  model.
     *
     * @param row the row
     * @param dto the DTO
     */
    public void setRow(int row, T dto) {
        if (arr != null) {
            if (arr.size() > row) {
                arr.set(row, dto);
            }
        }
        this.fireTableDataChanged();
    }
    
    /**
     * Set the data for the model.
     *
     * @param arr an <code>ArrayList</code> of DTOs
     */
    public void setData(List<T> data) {
        this.arr = data;
        this.fireTableDataChanged();
    }
    
    /**
     * Get the data for the model.
     *
     * @return an <code>ArrayList</code> of DTOs
     */
    public final List<T> getAllData() {
        return this.arr;
    }
    
    /**
     * Determine if any row in the model has been updated.
     *
     * @return <code>true</code> if any row in the model has been updated,
     *         <code>false</code> otherwise
     */
    public boolean hasBeenUpdated() {
        if (arr == null) {
            return false;
        }
        
        int size = arr.size() - 1;
        
        try {
            for (int i = size; i >= 0; i--) {
                T dto = getDTO(i);

                if (dto != null) {
                    if (dto.isOld()) {
                        return true;
                    } else if (dto.isNew()) {
                        return true;
                    } else if (dto.isModified()) {
                        return true;
                    } 
                }
            }
        } catch (Exception e) {
            Utils.log(e);
        }
        
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
            Collections.sort(arr, new MXColumnComparator(col, ascending));
        }
    }
    
    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
