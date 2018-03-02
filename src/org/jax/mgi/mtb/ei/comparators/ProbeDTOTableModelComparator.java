/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/ProbeDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:41 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.ProbeDTO;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for <code>ProbeDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:41
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/ProbeDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:41 mjv Exp
 */
public class ProbeDTOTableModelComparator<T extends ProbeDTO>
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
    public ProbeDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public ProbeDTOTableModelComparator(int nColumn, boolean bReverse) {
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
                // probe key
                nReturn = Utils.compareLong(dto1.getProbeKey(),
                                            dto2.getProbeKey());
                break;
            case 1:
                // probe name
                nReturn = StringUtils.compareIgnoreCase(dto1.getName(),
                                                        dto2.getName());
                break;
            case 2:
                // target
                nReturn = StringUtils.compareIgnoreCase(dto1.getTarget(),
                                                        dto2.getTarget());
                break;
            case 3:
                // counterstain
                nReturn = StringUtils.compareIgnoreCase(
                        dto1.getCounterstain(), dto2.getCounterstain());
                break;
            case 4:
                // url
                nReturn = StringUtils.compareIgnoreCase(dto1.getUrl(),
                                                        dto2.getUrl());
                break;
            case 5:
                // type
                nReturn = StringUtils.compareIgnoreCase(dto1.getType(),
                                                        dto2.getType());
                break;
            case 6:
                // supplier name
                nReturn = StringUtils.compareIgnoreCase(
                        dto1.getSupplierName(), dto2.getSupplierName());
                break;
            case 7:
                // supplier address
                nReturn = StringUtils.compareIgnoreCase(
                        dto1.getSupplierAddress(), dto2.getSupplierAddress());
                break;
            case 8:
                // supplier url
                nReturn = StringUtils.compareIgnoreCase(dto1.getSupplierUrl(),
                                                        dto2.getSupplierUrl());
                break;
            case 9:
                // notes
                nReturn = StringUtils.compareIgnoreCase(dto1.getNotes(),
                                                        dto2.getNotes());
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
