/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/ImageCellRenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.Timer;

/**
 * Renders an image in the cell of a <code>JTable</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:51:00
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/renderers/ImageCellRenderer.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 */
public class ImageCellRenderer extends JComponent implements TableCellRenderer {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables
    
    private String value = "";
    private BufferedImage bufImage = null;
    private Map<String,BufferedImage> hs = new HashMap<String,BufferedImage>();

    // ----------------------------------------------------------- Constructors

    /**
     * Constructor
     */
    public ImageCellRenderer() {
        super(); // ADDED NEW 11/09/2006
        Dimension d = new Dimension(100, 100);
        this.setSize(d);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setMaximumSize(d);
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

          // the entire reason for the HashMap is to not render the image more
          // than necessary
          BufferedImage prev = hs.get("row"+row);

          // only retrieve the file and create the buffer once
          if (prev == null) {
              if (value != null) {
                  this.value = value.toString();

                  try {
                      Timer timer = new Timer();
                      timer.start();
                      if (this.value.startsWith("http://")) {
                          // for an image of the net
                          bufImage = ImageIO.read(new URL(this.value));
                      } else {
                          // for a local image
                          bufImage = ImageIO.read(new File(this.value));
                      }
                  } catch (Exception e) {
                      Utils.log(e);
                  }
              }
          } else {
              bufImage = prev;
          }

          hs.put("row"+row, bufImage);

          if (isSelected) {
              // do something here like change the color
              this.setForeground(Color.white);
              this.setBackground(table.getBackground()) ;
          } else {
              this.setForeground(table.getForeground()) ;
              this.setBackground(table.getBackground()) ;
          }

          if (hasFocus)  {
              this.setBorder(
                      UIManager.getBorder("Table.focusCellHighlightBorder"));

              if (table.isCellEditable(row, column))  {
                  this.setForeground(
                          UIManager.getColor("Table.focusCellForeground"));
                  this.setBackground(
                          UIManager.getColor("Table.focusCellBackground"));
              }
          } else {
              this.setBorder( new EmptyBorder( 1, 2, 1, 2 ) ) ;
          }
          
           this.revalidate();
     

        return this;
    }


    // ------------------------------------------------------ Protected Methods


    protected void paintComponent(Graphics g) {
        g.drawImage(bufImage, 0, 0, 100, 100, null);
    }


    // -------------------------------------------------------- Private Methods
    // none

}
