/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TumorTypeDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:43 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>TumorTypeDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:43
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TumorTypeDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:43 mjv Exp
 */
public class TumorTypeDTOTableModelComparator<T extends TumorTypeDTO>
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
    public TumorTypeDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public TumorTypeDTOTableModelComparator(int nColumn, boolean bReverse) {
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
                nReturn = Utils.compareLong(dto1.getTumorTypeKey(),
                                            dto2.getTumorTypeKey());
                break;
            case 1:
                // tumor classification
                DataBean dtoSimple1 = dto1.getDataBean();
                DataBean dtoSimple2 = dto2.getDataBean();

                LabelValueBean<String,Long> bean1 =
                        (LabelValueBean<String,Long>)dtoSimple1.get(
                                EIConstants.TUMOR_CLASSIFICATION);
                LabelValueBean<String,Long> bean2 =
                        (LabelValueBean<String,Long>)dtoSimple2.get(
                                EIConstants.TUMOR_CLASSIFICATION);

                nReturn = StringUtils.compareIgnoreCase(bean1.getLabel(),
                                                        bean2.getLabel());
                break;
            case 2:
                // organ
                DataBean dtoSimpleA = dto1.getDataBean();
                DataBean dtoSimpleB = dto2.getDataBean();

                LabelValueBean<String,Long> beanA =
                        (LabelValueBean<String,Long>)dtoSimpleA.get(EIConstants.ORGAN);
                LabelValueBean<String,Long> beanB =
                        (LabelValueBean<String,Long>)dtoSimpleB.get(EIConstants.ORGAN);

                nReturn = StringUtils.compareIgnoreCase(beanA.getLabel(),
                                                        beanB.getLabel());
                break;
            case 3:
                // common name
                nReturn = StringUtils.compareIgnoreCase(dto1.getCommonName(),
                                                        dto2.getCommonName());
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
