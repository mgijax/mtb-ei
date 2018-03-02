/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/LVDBeanListCellRenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.renderers;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import org.jax.mgi.mtb.utils.LabelValueDataBean;

/**
 * Renders the label attribute of a <code>LabelValueDataBean</code> in the cell
 * of a <code>JList</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:51:00
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/LVDBeanListCellRenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 */
public class LVDBeanListCellRenderer<L,V,D> extends JLabel
                                    implements ListCellRenderer {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private Border border = null;


    // ----------------------------------------------------------- Constructors

    /**
     * Constructor
     */
    public LVDBeanListCellRenderer() {
        super(); // ADDED NEW 11/09/2006
        setOpaque(true);
        setVerticalAlignment(CENTER);
        this.border = new EmptyBorder(1,1,1,1);
    }


    // --------------------------------------------------------- Public Methods

    /**
     * This method is called to get the component that will render the data in
     * the list at the specified index.
     *
     * @param list the list
     * @param value the value of the cell
     * @param index the index
     * @param isSelected whether or not the cell is selected
     * @param cellHasFocus whether or not the cell has focus
     * @return the component to render
     */
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

        setBackground(isSelected ? list.getSelectionBackground() :
                                   list.getBackground());
        setForeground(isSelected ? list.getSelectionForeground() :
                                   list.getForeground());

        if (value != null) {
            LabelValueDataBean<L,V,D> bean = (LabelValueDataBean)value;
            setText(bean.getLabel()+"");
        }

        setBorder(border);

        return this;
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
