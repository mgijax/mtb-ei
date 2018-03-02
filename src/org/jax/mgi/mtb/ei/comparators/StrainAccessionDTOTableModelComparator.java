/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/StrainAccessionDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:41 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>StrainAccessionDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:41
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/StrainAccessionDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:41 mjv Exp
 */
public class StrainAccessionDTOTableModelComparator<T extends AccessionDTO>
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
    public StrainAccessionDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public StrainAccessionDTOTableModelComparator(int nColumn,
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
                // accession site
                LabelValueBean<String,Long> bean1 =
                        (LabelValueBean<String,Long>)dto1.getDataBean().get(
                                EIConstants.SITE_INFO);
                LabelValueBean<String,Long> bean2 =
                        (LabelValueBean<String,Long>)dto2.getDataBean().get(
                                EIConstants.SITE_INFO);

                nReturn = StringUtils.compare(bean1.getLabel(),
                                              bean2.getLabel());

                break;
            case 1:
                // accession id, will contain other data than just J Numbers
                nReturn = StringUtils.compare(dto1.getAccID(),
                                              dto2.getAccID());
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
