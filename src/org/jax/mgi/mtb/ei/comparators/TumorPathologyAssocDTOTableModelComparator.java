/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TumorPathologyAssocDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:43 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.custom.mtb.MTBPathologySearchDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorPathologyAssocDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>TumorPathologyAssocDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:43
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TumorPathologyAssocDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:43 mjv Exp
 */
public class TumorPathologyAssocDTOTableModelComparator<T extends TumorPathologyAssocDTO>
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
    public TumorPathologyAssocDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public TumorPathologyAssocDTOTableModelComparator(int nColumn,
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
        MTBPathologySearchDTO dtoMTBSearch1 =
                (MTBPathologySearchDTO)dto1.getDataBean().get(
                        EIConstants.MTB_PATHOLOGY_SEARCH_DTO);
        MTBPathologySearchDTO dtoMTBSearch2 =
                (MTBPathologySearchDTO)dto2.getDataBean().get(
                        EIConstants.MTB_PATHOLOGY_SEARCH_DTO);
        int nReturn = 0;
        switch(nColumn) {
            case 0:
                // pathology key
                nReturn = Utils.compareLong(dto1.getPathologyKey(),
                                            dto2.getPathologyKey());
                break;
            case 1:
                // pathologist
                nReturn = StringUtils.compare(dtoMTBSearch1.getPathologist(),
                                              dtoMTBSearch2.getPathologist());
                break;
            case 2:
                // contributor
                nReturn = StringUtils.compare(dtoMTBSearch1.getContributor(),
                                              dtoMTBSearch2.getContributor());
                break;
            case 3:
                // description
                nReturn = StringUtils.compare(dtoMTBSearch1.getDescription(),
                                              dtoMTBSearch2.getDescription());
                break;
            case 4:
                // number of images
                Integer iNumImages1 = 
                        Integer.valueOf(dtoMTBSearch1.getNumImages());
                Integer iNumImages2 =
                        Integer.valueOf(dtoMTBSearch2.getNumImages());

                nReturn = iNumImages1.compareTo(iNumImages2);
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
