/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/MTBReferenceSearchDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import java.util.Date;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceSearchDTO;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>MTBReferenceSearchDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:40
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/MTBReferenceSearchDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 */
public class MTBReferenceSearchDTOTableModelComparator<T extends MTBReferenceSearchDTO>
        extends AbstractTableDTOTableModelComparator<T> {

  // -------------------------------------------------------------- Constants
  // none

  // ----------------------------------------------------- Instance Variables
  // none

  // ----------------------------------------------------------- Constructors
  /**
   * Constructor
   *
   * @param nColumn the field from which you want to sort
   */
  public MTBReferenceSearchDTOTableModelComparator(int nColumn) {
    this(nColumn, false);
  }

  /**
   * Constructor
   *
   * @param nColumn the field from which you want to sort
   * @param bReverse <code>true</code for reverse order
   */
  public MTBReferenceSearchDTOTableModelComparator(int nColumn,
          boolean bReverse) {
    super(nColumn, bReverse);
  }
  // --------------------------------------------------------- Public Methods
  /**
   * Implementation of the compare method.
   *
   * @param pObj1 the first object to compare
   * @param pObj2 the second object to compare
   * @return 0 if the two objects are equal, 1 if the first object is
   *  greater than the second object, -1 if the second object is greater than
   *  the first object
   */
  public int compare(T dto1, T dto2) {
    int nReturn = 0;
    switch (nColumn) {
      case 0:
        // jnumber
        nReturn = Utils.compareJNumbers(dto1.getAccId(),
                dto2.getAccId());
        break;
      case 1:
        // reference key
        Integer ni1 = new Integer( dto1.getKey());
        Integer ni2 = new Integer(dto2.getKey());
        
        nReturn = ni1.compareTo(ni2);

        break;
      case 2:
        // priority
        ni1 = -3;
        ni2 = -3;
        try{
         ni1 = new Integer( dto1.getPriority().intValue());
        }catch(Exception ignore){}
        try{
          ni2 = new Integer( dto2.getPriority().intValue());
           }catch(Exception ignore){}
        nReturn = ni1.compareTo(ni2);
        break;
        
      case 3:
        // coded by
        nReturn = StringUtils.compareIgnoreCase(dto1.getCodedBy(), dto2.getCodedBy());
        break;
        
      case 4:
        // coded by date
        Date d1 = dto1.getCodedByDate();
        Date d2 = dto2.getCodedByDate();
        
        
        if(d1 == null){
          d1 = new Date();
          d1.setTime(0); 
        }
        
        if(d2 == null){
          d2 = new Date();
          d2.setTime(0);
        }
        nReturn = d1.compareTo(d2);
        break;
         
      case 5:
        // first author
        nReturn = StringUtils.compareIgnoreCase(dto1.getFirstAuthor(),
                dto2.getFirstAuthor());
        break;
        
      case 6:
        // citation
        nReturn = StringUtils.compareIgnoreCase(dto1.getCitation(),
                dto2.getCitation());
        break;
      default:
        throw new IllegalArgumentException("Column not supported");
    }

    return bReverse ? (-1 * nReturn) : nReturn;
  }  // ------------------------------------------------------ Protected Methods
  // none

  // -------------------------------------------------------- Private Methods
  // none
}
