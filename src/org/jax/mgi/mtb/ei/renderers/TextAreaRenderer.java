/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/TextAreaRenderer.java,v 1.1 2007/04/30 15:51:01 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.renderers;

import java.awt.Component;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Renders an multiline <code>JTextArea</code> component in the cell of a
 * <code>JTable</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:51:01
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/TextAreaRenderer.java,v 1.1 2007/04/30 15:51:01 mjv Exp
 */
public class TextAreaRenderer extends JTextArea implements TableCellRenderer {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private int minimumHeight = -1;
    private final DefaultTableCellRenderer adaptee =
            new DefaultTableCellRenderer();
    /**
     * Map from table to map of rows to map of column heights
     */
    private final Map<JTable,Map<Integer,Map<Integer,Integer>>> cellSizes =
            new HashMap<JTable,Map<Integer,Map<Integer,Integer>>>();


    // ----------------------------------------------------------- Constructors

    /**
     * Constructor
     */
    public TextAreaRenderer() {
        this(-1);
    }

    /**
     * Constructor
     *
     * @param minimumHeight the minimum heaight of this component
     */
     public TextAreaRenderer(int minimumHeight) {
        super(); // ADDED NEW 11/09/2006
        this.minimumHeight = minimumHeight;
        setLineWrap(true);
        setWrapStyleWord(true);
    }


    // --------------------------------------------------------- Public Methods

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
        // set the colours, etc. using the standard for that platform
        adaptee.getTableCellRendererComponent(table, value,
                                              isSelected, hasFocus,
                                              row, column);
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());

        // This line was very important to get it working with JDK1.4
        TableColumnModel columnModel = table.getColumnModel();
        setSize(columnModel.getColumn(column).getWidth(), 100000);

        int height_wanted = (int) getPreferredSize().getHeight();
        addSize(table, row, column, Math.max(this.minimumHeight,
                                             height_wanted));

        height_wanted = findTotalMaximumRowSize(table, row);

        if (height_wanted != table.getRowHeight(row)) {
            table.setRowHeight(row, height_wanted);
        }
        return this;
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Monitor the row height.
     *
     * @param table the table
     * @param row the row
     * @param column the column
     * @param height the height of the row
     */
    private void addSize(JTable table, int row, int column, int height) {
        Map<Integer,Map<Integer,Integer>> rows = cellSizes.get(table);

        if (rows == null) {
            cellSizes.put(table, rows = new HashMap<Integer,Map<Integer,Integer>>());
        }

        Map<Integer,Integer> rowheights = rows.get(Integer.valueOf(row));

        if (rowheights == null) {
            rows.put(Integer.valueOf(row), rowheights = new HashMap<Integer,Integer>());
        }

        rowheights.put(Integer.valueOf(column), Integer.valueOf(height));
    }

    /**
     * Look through all columns and get the renderer.  If it is the same class
     * as this, we look at the maximum height in its hash table for this row.
     *
     * @param table the table
     * @param row the row
     * @return the maximum row height
     */
    private int findTotalMaximumRowSize(JTable table, int row) {
        int maximum_height = 0;
        Enumeration columns = table.getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn tc = (TableColumn) columns.nextElement();
            TableCellRenderer cellRenderer = tc.getCellRenderer();

            if (cellRenderer instanceof TextAreaRenderer) {
                TextAreaRenderer tar = (TextAreaRenderer) cellRenderer;
                maximum_height =
                        Math.max(maximum_height,
                                 tar.findMaximumRowSize(table, row));
            }
        }

        return maximum_height;
    }

    /**
     * Look through the hash of row heights.
     *
     * @param table the table
     * @param row the row
     * @return the maximum row height
     */
    private int findMaximumRowSize(JTable table, int row) {
        int maximum_height = 0;

        Map<Integer,Map<Integer,Integer>> rows = cellSizes.get(table);

        if (rows != null) {
            Map<Integer,Integer> rowheights = rows.get(Integer.valueOf(row));

            if (rowheights != null) {
                for (Iterator it = rowheights.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    int cellHeight = ((Integer) entry.getValue()).intValue();
                    maximum_height = Math.max(maximum_height, cellHeight);
                }
            }
        }

        return maximum_height;
    }
}