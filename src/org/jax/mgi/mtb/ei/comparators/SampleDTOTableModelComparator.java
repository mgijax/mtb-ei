/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.SampleDTO;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 *
 * @author sbn
 */
public class SampleDTOTableModelComparator <T extends SampleDTO>
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
    public SampleDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public SampleDTOTableModelComparator(int nColumn, boolean bReverse) {
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
                // id changed this with move to postgres used to use key
                nReturn = StringUtils.compareIgnoreCase(dto1.getId(),
                                            dto2.getId());
                break;
            case 1:
                // title
      
                nReturn = StringUtils.compareIgnoreCase(dto1.getTitle(),
                                                        dto2.getTitle());
                break;
            case 2:
                // summary
                nReturn = StringUtils.compareIgnoreCase(dto1.getSummary(),
                                                        dto2.getSummary());
                break;
            default:
                throw new IllegalArgumentException("Column not supported");
        }

        return bReverse ? (-1 * nReturn) : nReturn;
    }

}
