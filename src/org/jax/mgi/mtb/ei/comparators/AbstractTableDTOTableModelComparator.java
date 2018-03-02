/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/AbstractTableDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import java.util.Comparator;
import org.jax.mgi.mtb.dao.gen.TableDTO;

/**
 * Abstract <code>Comparator</code> class for <code>TableDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:40
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/AbstractTableDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 */
public abstract class AbstractTableDTOTableModelComparator<T extends TableDTO>
       implements Comparator<T> {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables
    // none

    /**
     * Holds the field on which the comparison is performed.
     */
    protected int nColumn;

    /**
     * Value that will contain the information about the order of the sort:
     * normal or reversal.
     */
    protected boolean bReverse = false;

    // ----------------------------------------------------------- Constructors

    /**
     * Constructor class for AbstractTableDTOTableModelComparator.
     *
     *
     * @param nCol the field from which you want to sort
     */
    public AbstractTableDTOTableModelComparator(int nCol) {
        this(nCol, false);
    }

    /**
     * Constructor class for AbstractTableDTOTableModelComparator.
     *
     *
     * @param nCol the field from which you want to sort
     * @param bRev <code>true</code for bReverse order
     */
    public AbstractTableDTOTableModelComparator(int nCol, boolean bRev) {
        this.nColumn = nCol;
        this.bReverse = bRev;
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
    public abstract int compare(T pObj1, T pObj2);

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
