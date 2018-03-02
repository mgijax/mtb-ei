/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/MTBReferenceSearchDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.models;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceSearchDTO;
import org.jax.mgi.mtb.ei.comparators.MTBReferenceSearchDTOTableModelComparator;

/**
 * A <code>TableModel</code> which extends <code>DTOTableModel</code> and
 * is used to model <code>MTBReferenceSearchDTO</code> objects and ancillary 
 * data.
 *
 * @author mjv
 * @date 2007/04/30 15:50:49
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/models/MTBReferenceSearchDTOTableModel.java,v 1.1 2007/04/30 15:50:49 mjv Exp
 */
public class MTBReferenceSearchDTOTableModel<T extends MTBReferenceSearchDTO> extends DTOTableModel<T> {

  private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
  // -------------------------------------------------------------- Constants
  // none
  // ----------------------------------------------------- Instance Variables
  // none

  // ----------------------------------------------------------- Constructors
  /**
   * Constructor.
   *
   * @param data the data
   * @param columns the names of the columns
   */
  public MTBReferenceSearchDTOTableModel(List<T> data, List<String> columns) {
    super(data, columns);
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
        return dto.getAccId();
      case 1:
        return new Integer(dto.getKey());
      case 2:
        if(dto.getPriority() == null){
          return "";
        }
        switch (dto.getPriority().intValue()) {
          case 5:
            return "Top";
          case 4:
            return "High";
          case 3:
            return "Medium";
          case 2:
            return "Low";
          case 1:
              return "Review";
          case 0:
              return "Editorial";
          case -1:
            return "Rejected";
          default:
             return "";

        }
      case 3:
        return dto.getCodedBy();
      case 4:
        if (dto.getCodedByDate() != null) {
          return sdf.format(dto.getCodedByDate());
        } else {
          return "";
        }
      case 5:
        return dto.getFirstAuthor();
      case 6:
        return dto.getCitation();
      default:
        return "";
    }
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
              new MTBReferenceSearchDTOTableModelComparator<T>(col,
              ascending));
    }
  }  // ------------------------------------------------------ Protected Methods
  // none

  // -------------------------------------------------------- Private Methods
  // none
}
