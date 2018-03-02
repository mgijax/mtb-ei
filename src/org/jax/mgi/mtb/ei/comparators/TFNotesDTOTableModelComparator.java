/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TFNotesDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:42 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyNotesDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>TFNotesDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:42
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TFNotesDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:42 mjv Exp
 */
public class TFNotesDTOTableModelComparator<T extends TumorFrequencyNotesDTO>
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
    public TFNotesDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public TFNotesDTOTableModelComparator(int nColumn,
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
                // J Number
                String strJNumber1 =
                        (String)dto1.getDataBean().get(EIConstants.JNUM);
                String strJNumber2 =
                        (String)dto2.getDataBean().get(EIConstants.JNUM);

                nReturn = Utils.compareJNumbers(strJNumber1, strJNumber2);
                break;
            case 1:
                // note
                nReturn = StringUtils.compare(dto1.getNote(), dto2.getNote());
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
