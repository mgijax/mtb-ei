/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/mtbpath/JAXPPathologistDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:43 mjv Exp
 * Author: mjv
 */

package org.jax.mgi.mtb.ei.comparators.mtbpath;

import org.jax.mgi.mtb.dao.gen.mtbpath.JAXPPathologistDTO;
import org.jax.mgi.mtb.ei.comparators.AbstractTableDTOTableModelComparator;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for 
 * <code>TumorClassificationDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:43
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/mtbpath/JAXPPathologistDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:43 mjv Exp
 */
public class JAXPPathologistDTOTableModelComparator<T extends JAXPPathologistDTO>
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
    public JAXPPathologistDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     * 
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public JAXPPathologistDTOTableModelComparator(int nColumn, 
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
                nReturn = Utils.compareLong(dto1.getPathologistKey(),
                                            dto2.getPathologistKey());
                break;
            case 1:
                nReturn = StringUtils.compareIgnoreCase(dto1.getUserid(), 
                                                        dto2.getUserid());
                break;
            case 2:
                nReturn = StringUtils.compareIgnoreCase(dto1.getPassword(), 
                                                        dto2.getPassword());
                break;
            case 3:
                nReturn = StringUtils.compareIgnoreCase(dto1.getName(), 
                                                        dto2.getName());
                break;
            case 4:
                nReturn = StringUtils.compareIgnoreCase(dto1.getInstitution(), 
                                                        dto2.getInstitution());
                break;
            case 5:
                nReturn = StringUtils.compareIgnoreCase(dto1.getAddress1(), 
                                                        dto2.getAddress1());
                break;
            case 6:
                nReturn = StringUtils.compareIgnoreCase(dto1.getAddress2(), 
                                                        dto2.getAddress2());
                break;
            case 7:
                nReturn = StringUtils.compareIgnoreCase(dto1.getCity(), 
                                                        dto2.getCity());
                break;
            case 8:
                nReturn = StringUtils.compareIgnoreCase(dto1.getStateprov(), 
                                                        dto2.getStateprov());
                break;
            case 9:
                nReturn = StringUtils.compareIgnoreCase(dto1.getPostalcode(), 
                                                        dto2.getPostalcode());
                break;
            case 10:
                nReturn = StringUtils.compareIgnoreCase(dto1.getPhone(), 
                                                        dto2.getPhone());
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
