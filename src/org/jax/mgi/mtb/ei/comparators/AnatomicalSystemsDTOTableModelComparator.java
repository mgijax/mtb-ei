/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/AnatomicalSystemsDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.AnatomicalSystemDTO;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>AnatomicalSystemsDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:40
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/AnatomicalSystemsDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 */
public class AnatomicalSystemsDTOTableModelComparator<T extends AnatomicalSystemDTO>
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
    public AnatomicalSystemsDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public AnatomicalSystemsDTOTableModelComparator(int nColumn,
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
        switch(nColumn) {
            case 0:
                // anatomical system key
                nReturn = Utils.compareLong(dto1.getAnatomicalSystemKey(),
                                            dto2.getAnatomicalSystemKey());
                break;
            case 1:
                // get anatomical system name
                nReturn = StringUtils.compareIgnoreCase(dto1.getName(),
                                                        dto2.getName());
                break;
            default:
                throw new IllegalArgumentException("Column not supported");
        }

        return bReverse ? (-1 * nReturn) : nReturn;
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
