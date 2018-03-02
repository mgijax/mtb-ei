/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/GenotypeStrainsDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.GenotypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for
 * <code>GenotypeStrainsDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:40
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/GenotypeStrainsDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 */
public class GenotypeStrainsDTOTableModelComparator<T extends GenotypeDTO>
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
    public GenotypeStrainsDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public GenotypeStrainsDTOTableModelComparator(int nColumn,
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
        StrainDTO dtoStrain1 =
                (StrainDTO)dto1.getDataBean().get(
                        EIConstants.STRAIN_DTO);
        StrainDTO dtoStrain2 =
                (StrainDTO)dto2.getDataBean().get(
                        EIConstants.STRAIN_DTO);

        int nReturn = 0;
        switch(nColumn) {
            case 0:
                // strain key
                nReturn = Utils.compareLong(dtoStrain1.getStrainKey(),
                                            dtoStrain2.getStrainKey());
                break;
            case 1:
                // strain name
                nReturn = StringUtils.compare(dtoStrain1.getName(),
                                              dtoStrain2.getName());
                break;
            case 2:
                // strain description
                nReturn = StringUtils.compare(dtoStrain1.getDescription(),
                                              dtoStrain2.getDescription());
                break;
            case 3:
                // jnumber
                String strJNumber1 =
                        (String)dto1.getDataBean().get(
                                EIConstants.JNUM);
                String strJNumber2 =
                        (String)dto2.getDataBean().get(
                                EIConstants.JNUM);

                nReturn = Utils.compareJNumbers(strJNumber1, strJNumber2);
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
