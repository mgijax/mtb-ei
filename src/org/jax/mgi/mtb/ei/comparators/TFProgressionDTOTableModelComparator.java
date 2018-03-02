/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TFProgressionDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:42 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>TFProgressionDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:42
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TFProgressionDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:42 mjv Exp
 */
public class TFProgressionDTOTableModelComparator<T extends TumorProgressionDTO>
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
    public TFProgressionDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public TFProgressionDTOTableModelComparator(int nColumn,
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
                // child tumor frequency key
                nReturn = Utils.compareLong(dto1.getChildKey(),
                                            dto2.getChildKey());
                break;
            case 1:
                // progression type
                String strType1 =
                        (String)dto1.getDataBean().get(EIConstants.PROGRESSION);
                String strType2 =
                        (String)dto2.getDataBean().get(EIConstants.PROGRESSION);

                nReturn = StringUtils.compare(strType1, strType2);
                break;
            case 2:
                String strOrgan1 =
                        (String)dto1.getDataBean().get(EIConstants.ORGAN);
                String strOrgan2 =
                        (String)dto2.getDataBean().get(EIConstants.ORGAN);

                nReturn = StringUtils.compare(strOrgan1, strOrgan2);
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
