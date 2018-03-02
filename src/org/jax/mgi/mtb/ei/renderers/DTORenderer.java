/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/DTORenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.renderers;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import org.jax.mgi.mtb.dao.gen.TableDTO;
import org.jax.mgi.mtb.ei.models.DTOTableModel;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * The default renderer for tables containg DTO data.
 *
 * @author mjv
 * @date 2007/04/30 15:51:00
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/DTORenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 */
public class DTORenderer extends JLabel implements TableCellRenderer {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private Color clrDeleted = new Color(255, 200, 200); // red
    private Color clrModified = new Color(200, 200, 255); // blue
    private Color clrNew = new Color(200, 255, 200); // green
    private Color clrHilite = new Color(50, 50, 200);
    private Color clrOriginal = new Color(255,255,255);
    private Border hBorder = new LineBorder(clrHilite);
    private Border nBorder =
           new LineBorder(UIManager.getDefaults().getColor("Table.gridColor"));
    private Map<Integer,Integer> disabledToolTipColumns =
            new HashMap<Integer,Integer>();


    // ----------------------------------------------------------- Constructors

    /**
     * Constructor
     */
    public DTORenderer() {
        super(); // ADDED NEW 11/09/2006
        this.setOpaque(true);
        this.setBackground(clrOriginal);
        this.setForeground(Color.black);
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Enable or disbale tooltips for a column.
     *
     * @param col The column, -1 for all columns
     * @param enable True to enable tooltips, false to disable tooltips
     */
    public void enableToolTip(int col, boolean enable) {
        try {
            if (col == -1) {
                disabledToolTipColumns = new HashMap<Integer,Integer>();
                return;
            }

            Integer column = Integer.valueOf(col);

            if (enable) {
                disabledToolTipColumns.remove(column);
            } else {
                disabledToolTipColumns.put(column, column);
            }
        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * This method is called to get the component that will render the data in
     * the table at the specified row and column.
     *
     * @param table the table
     * @param value the value of the cell
     * @param isSelected whether or not the cell is selected
     * @param hasFocus whether or not the cell has focus
     * @param rowIndex the row
     * @param vColIndex the column
     * @return the component to render
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int column) {

        try {
            Object obj = ((DTOTableModel)table.getModel()).getDTO(row);

            if (obj == null) {
                this.setBackground(clrOriginal);
            } else {
                TableDTO dto = (TableDTO)obj;

                if (isSelected) {
                    this.setBorder(hBorder);
                } else {
                    this.setBorder(nBorder);
                }

                if (dto.isOld()) {
                    this.setBackground(clrDeleted);
                } else if (dto.isNew()) {
                    this.setBackground(clrNew);
                } else if (dto.isModified()) {
                    this.setBackground(clrModified);
                } else {  
                    this.setBackground(clrOriginal);
                }

                if (value == null) {
                    this.setText("");
                } else {
                    if (value instanceof LabelValueBean) {
                        LabelValueBean bean = (LabelValueBean)value;
                        this.setText(bean.getLabel() == null ? "" : bean.getLabel() + "");
                    } else {
                        this.setText(value.toString());
                    }
                }
            }


        } catch (Exception e) {
            Utils.log(e);
        }

        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // The following methods override the defaults for performance reasons
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Overriden for performance reasons.
     */
    public void validate() {
        // Overriden for performance reasons.
    }

    /**
     * Overriden for performance reasons.
     */
    public void revalidate() {
        // Overriden for performance reasons.
    }

    /**
     * Overriden for performance reasons.
     *
     * @param propertyName the property
     * @param oldValue the old value
     * @param newValue the new value
     */
    public void firePropertyChange(String propertyName, boolean oldValue,
                                   boolean newValue) {
        // Overriden for performance reasons.
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Overriden for performance reasons.
     *
     * @param propertyName the property
     * @param oldValue the old value
     * @param newValue the new value
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
                                      Object newValue) {
        // Overriden for performance reasons.
    }

    // -------------------------------------------------------- Private Methods
    // none
}
