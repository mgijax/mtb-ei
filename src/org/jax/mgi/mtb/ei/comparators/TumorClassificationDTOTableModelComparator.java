/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TumorClassificationDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:42 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.TumorClassificationDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>TumorClassificationDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:42
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TumorClassificationDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:42 mjv Exp
 */
public class TumorClassificationDTOTableModelComparator<T extends TumorClassificationDTO>
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
    public TumorClassificationDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public TumorClassificationDTOTableModelComparator(int nColumn,
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
                // tumor classification key
                nReturn = Utils.compareLong(dto1.getTumorClassificationKey(),
                                            dto2.getTumorClassificationKey());
                break;
            case 1:
                // tumor classification
                nReturn = StringUtils.compareIgnoreCase(dto1.getName(),
                                                        dto2.getName());
                break;
            case 2:
                // parent tc name
                DataBean dtoSimpleData1 = dto1.getDataBean();
                DataBean dtoSimpleData2 = dto2.getDataBean();

                LabelValueBean<String,Long> beanTC1 =
                        (LabelValueBean<String,Long>)dtoSimpleData1.get(
                                EIConstants.TUMOR_CLASSIFICATION_BEAN);

                LabelValueBean<String,Long> beanTC2 =
                        (LabelValueBean<String,Long>)dtoSimpleData2.get(
                                EIConstants.TUMOR_CLASSIFICATION_BEAN);

                if ((beanTC1 == null) && (beanTC2 == null)) {
                    nReturn = 0;
                } else if ((beanTC1 != null) && (beanTC2 == null)) {
                    nReturn = -1;
                } else if ((beanTC1 == null) && (beanTC2 != null)) {
                    nReturn = 1;
                } else {
                    nReturn = StringUtils.compareIgnoreCase(beanTC1.getLabel(),
                                                            beanTC2.getLabel());
                }

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
