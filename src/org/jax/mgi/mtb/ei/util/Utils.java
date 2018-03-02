/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/util/Utils.java,v 1.1 2007/04/30 15:51:25 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.filters.DocumentSizeFilter;
import org.jax.mgi.mtb.ei.filters.IntegerDocumentFilter;
import org.jax.mgi.mtb.gui.MXInfoDialog;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.LabelValueBeanComparator;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * A collection of static utility methods used in the EI.
 *
 * @author mjv
 * @date 2007/04/30 15:51:25
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/util/Utils.java,v 1.1 2007/04/30 15:51:25 mjv Exp
 */
public class Utils {

    // -------------------------------------------------------------- Constants

    public static final String OK_ICON_PATH =
            "org/mgi/mtb/ei/reosources/img/ok.png";

    public static final String WARNING_ICON_PATH =
            "org/mgi/mtb/ei/reosources/img/warning.png";

    public static final String ERROR_ICON_PATH =
            "org/mgi/mtb/ei/reosources/img/error.png";


    // ----------------------------------------------------- Instance Variables
    // none

    // ----------------------------------------------------------- Constructors
    // none

    // --------------------------------------------------------- Public Methods

    /**
     * This is a quick fix to make sure that data being entered somewhere is
     * in the form of J:[NUMBER].
     *
     * @param jnumber The <code>String</code> to alter
     * @return A <code>String</code> in the form of J:[NUMBER]
     */
    public static String fixJNumber(final String jnumber) {
        final String ret = "J:";
        String temp = null;

        if (StringUtils.hasValue(jnumber)) {
            temp = jnumber.trim();
        } else {
            return "";
        }

        if (jnumber.indexOf('J') == 0) {
            if (jnumber.indexOf(':') == 1) {
                temp = jnumber.substring(2);
            } else {
                temp = jnumber.substring(1);
            }
        }

        return ret + temp;
    }

    /**
     * This is a quick fix to make sure that data being entered somewhere is
     * in the form of J:[NUMBER].
     *
     * @param txtJNumber The <code>JTectField</code> to alter
     */
    public static void fixJNumber(final JTextField txtJNumber) {
        txtJNumber.setText(fixJNumber(txtJNumber.getText().trim()));
    }

    /**
     * This is a quick fix to make sure that data being entered somewhere is
     * in the form of ######.
     *
     * @param stockNumber The <code>String</code> to alter
     * @return A <code>String</code> in the form of ######
     */
    public static String fixJaxMiceStockNumber(final String stockNumber) {
        String temp = null;

        if (StringUtils.hasValue(stockNumber)) {
            temp = stockNumber.trim();
        } else {
            return "";
        }

        temp = StringUtils.padLeft(temp, 6, '0');

        return temp;
    }

    /**
     * This is a quick fix to make sure that data being entered somewhere is
     * in the form of ######.
     *
     * @param txtJNumber The <code>JTectField</code> to alter
     */
    public static void fixJaxMiceStockNumber(final JTextField txtJNumber) {
        txtJNumber.setText(fixJaxMiceStockNumber(txtJNumber.getText().trim()));
    }

    /**
     * This is a quick fix to make sure that data being entered somewhere is
     * in the form of MGI:[NUMBER].
     *
     * @param mgiID The <code>String</code> to alter
     * @return A <code>String</code> in the form of MGI:[NUMBER]
     */
    public static String fixMGIID(final String mgiID) {
        final String ret = "MGI:";
        String temp = null;

        if (StringUtils.hasValue(mgiID)) {
            temp = mgiID.trim();
        } else {
            return "";
        }

        if (mgiID.indexOf("MGI") == 0) {
            if (mgiID.indexOf(':') == 3) {
                temp = mgiID.substring(4);
            } else {
                temp = mgiID.substring(3);
            }
        }

        return ret + temp;
    }

    /**
     * This is a quick parse to get the numeric part of the JNumber.
     *
     * @param jnumber The <code>String</code> to parse
     * @return A <code>long</code> representing the numeric part
     */
    public static long parseJNumber(final String jnumber) {
        long ret = -1l;
        String temp = null;

        if (StringUtils.hasValue(jnumber)) {
            temp = fixJNumber(jnumber);
        } else {
            return ret;
        }

        if (jnumber.indexOf('J') == 0) {
            if (jnumber.indexOf(':') == 1) {
                temp = jnumber.substring(2);
            } else {
                temp = jnumber.substring(1);
            }
        }

        try {
            ret = Long.parseLong(temp);
        } catch (Exception e) {
            Utils.log(e);
        }

        return ret;
    }

    /**
     * This is a quick parse to get the numeric part of the MGI ID.
     *
     * @param mgiID The <code>String</code> to parse
     * @return A <code>long</code> representing the numeric part
     */
    public static long parseMGIID(final String mgiID) {
        long ret = -1l;
        String temp = null;

        if (StringUtils.hasValue(mgiID)) {
            temp = fixJNumber(mgiID);
        } else {
            return ret;
        }

        if (mgiID.indexOf("MGI") == 0) {
            if (mgiID.indexOf(':') == 3) {
                temp = mgiID.substring(4);
            } else {
                temp = mgiID.substring(3);
            }
        }

        try {
            ret = Long.parseLong(temp);
        } catch (Exception e) {
            Utils.log(e);
        }

        return ret;
    }


    /**
     * This is a quick fix to make sure that data being entered somewhere is
     * in the form of MGI:[NUMBER].
     *
     * @param txtMGIID The <code>JTextField</code> to alter
     */
    public static void fixMGIID(final JTextField txtMGIID) {
        txtMGIID.setText(fixMGIID(txtMGIID.getText().trim()));
    }

    /**
     * Log to the console.
     *
     * @param str the text to log
     */
    public static void log(final String str) {
        EIGlobals.getInstance().getMainFrame().log(str);
    }

    /**
     * Log to the console.
     *
     * @param buf the text to log
     */
    public static void log(final StringBuffer buf) {
        EIGlobals.getInstance().getMainFrame().log(buf);
    }

    /**
     * Log to the console.
     *
     * @param e the <code>Exception</code> to log
     */
    public static void log(final Exception e) {
        EIGlobals.getInstance().getMainFrame().log(e);
    }

    /**
     * Get the system properties as an <code>ArrayList</code> of
     * <code>LabelValueBean</code>s.
     *
     * @return an <code>ArrayList</code> of <code>LabelValueBean</code>s
     */
    public static List<LabelValueBean<String,String>> getSystemProperties() {
        List<LabelValueBean<String,String>> ret = new ArrayList<LabelValueBean<String,String>>();

        // Get all system properties
        final Properties props = System.getProperties();

        // Enumerate all system properties
        Enumeration e = props.propertyNames();
        for (; e.hasMoreElements(); ) {
            // Get property name
            String propName = (String)e.nextElement();

            // Get property value
            String propValue = (String)props.get(propName);

            LabelValueBean<String,String> bean =
                    new LabelValueBean<String,String>(propName, propValue);
            ret.add(bean);
        }

        // sort the data
        LabelValueBean arrTemp[] =
                (LabelValueBean[])ret.toArray(new LabelValueBean[ret.size()]);
        Arrays.sort(arrTemp,
            new LabelValueBeanComparator(LabelValueBeanComparator.TYPE_LABEL));
        ret = new ArrayList(Arrays.asList(arrTemp));

        return ret;
    }

    /**
     * Convert a byte array to a hexidecimal string.
     *
     * @param b the byte array
     * @return the hexidecimal string representation
     */
    public static String byteArrayToHexString(final byte[] b){
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * Convert a hexidecimal string to a byte array.
     *
     * @param s the hexidecimal string representation
     * @return the byte array
     */
    public static byte[] hexStringToByteArray(final String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }

    /**
     * Load a properties file from disk.
     *
     * @param fileNameAndPath the full path to the properties file
     * @return the <code>Properties</code>
     */
    public static Properties loadProperties(String fileNameAndPath)
        throws IOException, Exception {

        return loadProperties(new File(fileNameAndPath));
    }

    /**
     * Load the properties into memory form the properties file.
     *
     * @param file the <code>File</code> object
     * @return the <code>Properties</code>
     */
    public static Properties loadProperties(File file)
        throws FileNotFoundException, IOException {
        // determine the user's home directory
        File f = loadFile(file.getPath());

        if (!f.exists()) {
            throw new FileNotFoundException("Unable to find file: " + file);
        }

        // Read properties file
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(f));
        } catch (IOException e) {
            throw new IOException("Unable to load properties file: " +
                    f.toString());
        }

        return properties;
    }

    /**
     * Save the properties in memory to disk.
     *
     * @param properties the <code>Properties</code>
     * @param fileNameAndPath the full path to the properties file
     */
    public static void saveProperties(Properties properties,
                                      String fileNameAndPath)
        throws IOException {

        File file = new File(fileNameAndPath);
        saveProperties(properties, file);
    }

    /**
     * Save the properties in memory to disk.
     *
     * @param properties the <code>Properties</code>
     * @param file the <code>File</code> object
     */
    public static void saveProperties(Properties properties, File file)
        throws IOException {

        // make sure the file can be written to
        if (!file.canWrite()) {
            throw new IOException("Insufficient access to write to the " +
                    "following file: " +
                    file.getPath() + File.separatorChar +
                    file.getName());
        }

        // Write properties to disk
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
            throw new IOException("Unable to store properties file: " +
                    file.toString() + "." + e.getMessage());
        }
    }


    /**
     * Load a file from disk.
     *
     * @param fileNameAndPath the full path to the properties file
     * @return the <code>File</code> object
     **/
    public static File loadFile(String fileNameAndPath)
    throws FileNotFoundException, IOException {
        File f = new File(fileNameAndPath);

        // make sure the file exists
        if (!f.exists()) {
            // File or directory does not exist
            throw new FileNotFoundException("Unable to locate the file: " +
                    fileNameAndPath);
        }

        // make sure the file can be read
        if (!f.canRead()) {
            throw new IOException("Unable to read the file: " + fileNameAndPath);
        }

        return f;
    }

    /**
     * Show an error dialog.
     *
     * @param strMessage the mesage
     */
    public static void showErrorDialog(String strMessage) {
        MXInfoDialog.showDialog(null,"Error",strMessage);
    }

    /**
     * Show an error dialog.
     *
     * @param strMessage the strMessage
     * @param exc the <code>Exception</code>
     */
    public static void showErrorDialog(String strMessage, Exception exc) {
        MXInfoDialog.showDialog(null,strMessage,exc);
    }

    /**
     * Show a success dialog.
     *
     * @param strMessage the strMessage
     */
    public static void showSuccessDialog(String strMessage) {
        MXInfoDialog.showDialog(null,"Success",strMessage);
    }

    /**
     * Set the scrolling viewport of a <code>JTable</code> component to be
     * visible at the specified row and column.
     *
     * @param tbl <code>JTable</code>
     * @param nRowIndex the row
     * @param nColIndex the column
     */
    public static void scrollToVisible(JTable tbl,
                                      int nRowIndex, int nColIndex) {
        // assumes tbl is contained in a JScrollPane. Scrolls the
        // cell (nRowIndex, nColIndex) so that it is visible within the
        // viewport
        if (!(tbl.getParent() instanceof JViewport)) {
            return;
        }

        JViewport viewport = (JViewport)tbl.getParent();

        // this rectangle is relative to the tbl where the northwest corner
        // of cell (0,0) is always (0,0).
        Rectangle rect = tbl.getCellRect(nRowIndex, nColIndex, true);

        // the location of the viewport relative to the tbl
        Point pt = viewport.getViewPosition();

        // translate the cell location so that it is relative to the view,
        // assuming the northwest corner of the view is (0,0)
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);

        // scroll the area into view
        viewport.scrollRectToVisible(rect);
    }

    /**
     * Center a <code>Component</code> on the desktop.
     *
     * @param comp the <code>Component</code>
     */
    public static void centerComponentonScreen(Component comp) {
        // center the window on the screen
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();

        Dimension dimComponent = comp.getSize();

        if (dimComponent.height > dimScreen.height) {
            dimComponent.height = dimScreen.height;
        }

        if (dimComponent.width > dimScreen.width) {
            dimComponent.width = dimScreen.width;
        }

        comp.setLocation((dimScreen.width - dimComponent.width) / 2,
                         (dimScreen.height - dimComponent.height) / 2);
    }


    /**
     * Loads an <code>ImageIcon</code> object from this archive.
     *
     * @param pathnamethe name of the icon file
     * @return the icon if loaded, <code>null</code> otherwise
     */
    public static ImageIcon loadIcon(String pathname) {
        URL url = Utils.class.getClassLoader().getResource(pathname);

        if (url != null) {
            return new ImageIcon(url);
        }

        return null;
    }

    /**
     * Set the component to only allow numeric values.
     *
     * @param comp the component
     */
    public static void setNumericFilter(JTextComponent comp) {
        Document textDocOne = comp.getDocument();
        DocumentFilter filterOne = new IntegerDocumentFilter();
        ((AbstractDocument)textDocOne).setDocumentFilter(filterOne);
        comp.setDocument(textDocOne);
    }

    public static void setTextLimit(JTextComponent comp, int size) {
        Document textDocOne = comp.getDocument();
        DocumentFilter filterOne = new DocumentSizeFilter(size);
        ((AbstractDocument)textDocOne).setDocumentFilter(filterOne);
        comp.setDocument(textDocOne);
    }

    public static int compareLong(final Long l1, final Long l2) {
        int nReturn = 0;

        // agent key
        if (l1 == null && l2 != null) {
            nReturn = -1;
        } else if (l1 == null && l2 == null) {
            nReturn = 0;
        } else if (l1 != null && l2 == null) {
            nReturn = 1;
        } else {
            nReturn = l1.compareTo(l2);
        }

        return nReturn;
    }

    public static int compareJNumbers(String strJNumber1, String strJNumber2) {
        int nReturn = 0;

        if (strJNumber1 == null && strJNumber2 != null) {
            nReturn = -1;
        } else if (strJNumber1 == null &&  strJNumber2 == null) {
            nReturn = 0;
        } else if (strJNumber1 != null && strJNumber2 == null) {
            nReturn = 1;
        } else {
            final Integer i1 = Integer.valueOf(strJNumber1.substring(2));
            final Integer i2 = Integer.valueOf(strJNumber2.substring(2));

            nReturn = i1.compareTo(i2);
        }

        return nReturn;

    }

    public static BufferedImage takeScreenshot(Rectangle rect) {
        BufferedImage bufferedImage = null;

        try {
            Robot robot = new Robot();
            bufferedImage = robot.createScreenCapture(rect);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return bufferedImage;
    }

    /**
     * This method writes a image to the system clipboard.
     *
     * @param image the image
     */
    public static void setClipboard(Image image) {
        ImageSelection imgSel = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    /**
     * This method writes a string to the system clipboard.
     *
     * @param str the text to write
     */
    public static void setClipboard(String str) {
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none

}
