/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/AgentDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.comparators;

import org.jax.mgi.mtb.dao.gen.mtb.AgentDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * <code>Comparator</code> implementation for <code>AgentDTOTableModel</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:40
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/comparators/AgentDTOTableModelComparator.java,v 1.1 2007/04/30 15:50:40 mjv Exp
 */
public class AgentDTOTableModelComparator<T extends AgentDTO>
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
    public AgentDTOTableModelComparator(int nColumn) {
        this(nColumn, false);
    }

    /**
     * Constructor
     *
     * @param nColumn the field from which you want to sort
     * @param bReverse <code>true</code for reverse order
     */
    public AgentDTOTableModelComparator(int nColumn, boolean bReverse) {
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
                // agent key
                nReturn = Utils.compareLong(dto1.getAgentKey(),
                                            dto2.getAgentKey());
                break;
            case 1:
                // agent type
                final DataBean dtoSimple1 = dto1.getDataBean();
                final DataBean dtoSimple2 = dto2.getDataBean();

                final LabelValueBean<String,Long> bean1 =
                        (LabelValueBean<String,Long>)dtoSimple1.get(
                                EIConstants.AGENT_TYPE_BEAN);

                final LabelValueBean<String,Long> bean2 =
                        (LabelValueBean<String,Long>)dtoSimple2.get(
                                EIConstants.AGENT_TYPE_BEAN);

                nReturn = StringUtils.compareIgnoreCase(bean1.getLabel(),
                                                        bean2.getLabel());
                break;
            case 2:
                // agent name
                nReturn = StringUtils.compareIgnoreCase(dto1.getName(),
                                                        dto2.getName());
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
