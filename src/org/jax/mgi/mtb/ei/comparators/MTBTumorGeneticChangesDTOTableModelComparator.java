/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TumorGeneticChangesDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:42 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;


import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorGeneticChangesComparator;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorGeneticChangesDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorGeneticChangesDTO;
import org.jax.mgi.mtb.ei.EIConstants;



/**
 * <code>Comparator</code> implementation for
 * <code>TumorPathologyAssocDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:42
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/TumorGeneticChangesDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:42 mjv Exp
 */
public class MTBTumorGeneticChangesDTOTableModelComparator<T extends MTBTumorGeneticChangesDTO>
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
    public MTBTumorGeneticChangesDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public MTBTumorGeneticChangesDTOTableModelComparator(int nColumn,
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
        MTBTumorGeneticChangesComparator compy = null;
        
        switch(nColumn){
          case 0:
                nReturn = ((String)dto1.getDataBean().get(EIConstants.CHANGE))
                        .compareTo((String)dto2.getDataBean().get(EIConstants.CHANGE));
          break;
          
          case 1: 
                nReturn = ((String)dto1.getDataBean().get(EIConstants.ASSAY_NAME))
                        .compareTo((String)dto2.getDataBean().get(EIConstants.ASSAY_NAME));
          break;
          
          case 2:
               compy = new MTBTumorGeneticChangesComparator(MTBTumorGeneticChangesDAO.ID_NAME);
                nReturn = compy.compare(dto1, dto2);
          break;  
          
          case 3:
               compy = new MTBTumorGeneticChangesComparator(MTBTumorGeneticChangesDAO.ID_NOTES);
                nReturn = compy.compare(dto1, dto2);
          break;
          
          case 4:
                compy = new MTBTumorGeneticChangesComparator(MTBTumorGeneticChangesDAO.ID_DISPLAY_CHROMOSOMES);
                nReturn = compy.compare(dto1, dto2);
          break;
          
          case 5:
                nReturn = ((Integer)dto1.getDataBean().get(EIConstants.ASSAY_IMAGE_COUNT))
                        .compareTo((Integer)dto2.getDataBean().get(EIConstants.ASSAY_IMAGE_COUNT));
           
        }
       

        return bReverse ? (-1 * nReturn) : nReturn;
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
