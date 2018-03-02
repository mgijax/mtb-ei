/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/ProbeDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeDTO;
import org.jax.mgi.mtb.ei.comparators.ProbeDTOTableModelComparator;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and is
 * used to model <code>ProbeDTO</code> objects and ancillary data.
 * 
 * @author mjv
 * @date 2007/04/30 15:50:49
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/ProbeDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 */
public class ProbeDTOTableModel<T extends ProbeDTO> extends DTOTableModel<T> {
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
  public ProbeDTOTableModel(List<T> arr, List<String> columnNames) {
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
        return dto.getProbeKey();
      case 1:
        return dto.getName();
      case 2:
        return dto.getTarget();
      case 3:
        return dto.getCounterstain();
      case 4:
        return dto.getUrl();
      case 5:
        return dto.getType();
      case 6:
        return dto.getSupplierName();
      case 7:
        return dto.getSupplierAddress();
      case 8:
        return dto.getSupplierUrl();
      case 9:
        return dto.getNotes();
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
        dto.setProbeKey((Long) aValue);
        break;
      case 1:
        dto.setName((String) aValue);
        break;
      case 2:
        dto.setTarget((String) aValue);
        break;
      case 3:
        dto.setCounterstain((String) aValue);
        break;
      case 4:
        dto.setUrl((String) aValue);
        break;
      case 5:
        dto.setType((String) aValue);
        break;
      case 6:
        dto.setSupplierName((String) aValue);
        break;
      case 7:
        dto.setSupplierAddress((String) aValue);
        break;
      case 8:
        dto.setSupplierUrl((String) aValue);
        break;
      case 9:
        dto.setNotes((String) aValue);
        break;
      default:
        break;
    }

    arr.set(row, dto);

    // This is much faster than fireTableDataChanged() because the event 
    // fires for all rows.  Using fireTableRowsUpdated(row, row) only fires 
    // for the row that changed, if we don't do this, our pick list won't be
    // updated
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
              new ProbeDTOTableModelComparator<T>(col, ascending));
    }
  }

  
  // ------------------------------------------------------ Protected Methods
  // none

  // -------------------------------------------------------- Private Methods
  // none
}
