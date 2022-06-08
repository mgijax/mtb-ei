/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/handlers/LVBeanTransferHandler.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.handlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import org.apache.logging.log4j.Logger;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * Drag and drop support for a <code>LabelValueBean</code>.
 *
 * @author mjv
 * @date 2007/04/30 15:50:46
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/handlers/LVBeanTransferHandler.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 */
public class LVBeanTransferHandler<L,V> extends TransferHandler {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private final static Logger log =
            org.apache.logging.log4j.LogManager.getLogger(LVBeanTransferHandler.class.getName());
    private JList source = null;
    private DataFlavor localListFlavor;
    private DataFlavor serialListFlavor;
    private String localArrayListType =
            DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.List";
    private List<LabelValueBean<L,V>> items = 
            new ArrayList<LabelValueBean<L,V>>();

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the LabelValueBean Transfer Handler.  This TrasnferHandler
     * is essentially an ArrayList of LabelValueBeans used to move data
     * between JLists with a model of LVBeanListModel.
     */
    public LVBeanTransferHandler() {
        try {
            localListFlavor = new DataFlavor(localArrayListType);
        } catch (ClassNotFoundException e) {
            log.error("ListTransferHandler: unable to create!!!", e);
        }
        serialListFlavor = new DataFlavor(List.class, "List");
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Causes a transfer to a component from a clipboard or a  DND drop
     * operation.  The <code>Transferable</code> represents the data to be
     * imported into the component.
     *
     * @param comp the component to receive the transfer; this argument is
     *             provided to enable sharing of <code>TransferHandler</code>s
     *             by multiple components
     * @param t the data to import
     * @return true if the data was inserted into the component, false
     *         otherwise
     */
    public boolean importData(JComponent c, Transferable t) {
        JList target = null;
        List alist = null;

        if (!canImport(c, t.getTransferDataFlavors())) {
            return false;
        }

        try {
            target = (JList)c;
            if (hasLocalListFlavor(t.getTransferDataFlavors())) {
                alist = (ArrayList)t.getTransferData(localListFlavor);
            } else if (hasSerialListFlavor(t.getTransferDataFlavors())) {
                alist = (ArrayList)t.getTransferData(serialListFlavor);
            } else {
                return false;
            }
        } catch (UnsupportedFlavorException ufe) {
            log.error("importData: unsupported data flavor", ufe);
            return false;
        } catch (IOException ioe) {
            log.error("importData: I/O exception", ioe);
            return false;
        }

        LVBeanListModel<L,V> listModel = (LVBeanListModel<L,V>)target.getModel();
        items = new ArrayList<LabelValueBean<L,V>>();
        for (int i=0; i < alist.size(); i++) {
            listModel.addElement(alist.get(i));
            items.add((LabelValueBean<L,V>)alist.get(i));
        }
        return true;
    }

    /**
     * Indicates whether a component would accept an import of the given
     * set of data flavors prior to actually attempting to import it.
     *
     * @param comp the component to receive the transfer; this argument is
     *             provided to enable sharing of <code>TransferHandlers</code>
     *             by multiple components
     * @param transferFlavors the data formats available
     * @return true if the data can be inserted into the component, false
     *         otherwise
     */
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        if (hasLocalListFlavor(flavors))  { return true; }
        if (hasSerialListFlavor(flavors)) { return true; }
        return false;
    }

    /**
     * Returns the type of transfer actions supported by the source.
     * Some models are not mutable, so a transfer operation of <code>COPY</code>
     * only should be advertised in that case.
     *
     * @param c the component holding the data to be transferred; this argument
     *          is provided to enable sharing of <code>TransferHandler</code>s
     *          by multiple components.
     * @return <code>MOVE</code> always
     */
    public int getSourceActions(JComponent c) {
        return MOVE;
    }


    // ------------------------------------------------------ Protected Methods

    /**
     * Invoked after data has been exported.  This method should remove
     * the data that was transferred if the action was <code>MOVE</code>.
     *
     * @param source the component that was the source of the data
     * @param data  The data that was transferred or possibly null
     *              if the action is <code>NONE</code>.
     * @param action the actual action that was performed
     */
    protected void exportDone(JComponent c, Transferable data, int action) {
        if ((action == MOVE) && (items != null)) {
            LVBeanListModel<L,V> model = (LVBeanListModel<L,V>)source.getModel();

            for (int i = 0; i < items.size(); i++) {
                model.removeElement(items.get(i));
            }
        }
        items = null;
    }

    /**
     * Creates a <code>Transferable</code> to use as the source for a data
     * transfer. Returns the representation of the data to be transferred, or
     * <code>null</code> if the component's property is <code>null</code>
     *
     * @param c the component holding the data to be transferred; this argument
     *          is provided to enable sharing of <code>TransferHandler</code>s
     *          by multiple components
     * @return the representation of the data to be transferred, or
     *         <code>null</code> if the property associated with <code>c</code>
     *         is <code>null</code>
     */
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            source = (JList)c;
            Object[] values = source.getSelectedValues();
            if (values == null || values.length == 0) {
                return null;
            }
            List<Object> alist = new ArrayList<Object>(values.length);
            for (int i = 0; i < values.length; i++) {
                Object o = values[i];
                alist.add(o);
            }
            return new ListTransferable(alist);
        }
        return null;
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Checks to see if the target supports this data transfer.
     *
     * @return true if this flavor is supported, false otherwise
     */
    private boolean hasLocalListFlavor(DataFlavor[] flavors) {
        if (localListFlavor == null) {
            return false;
        }

        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(localListFlavor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the target supports this data transfer.
     *
     * @return true if this flavor is supported, false otherwise
     */
    private boolean hasSerialListFlavor(DataFlavor[] flavors) {
        if (serialListFlavor == null) {
            return false;
        }

        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(serialListFlavor)) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------- Inner Classes

    /**
     * A transferable ArrayList.
     */
    public class ListTransferable implements Transferable {
        List data;

        public ListTransferable(List alist) {
            data = alist;
        }

        public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return data;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { localListFlavor,
                    serialListFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            if (localListFlavor.equals(flavor)) {
                return true;
            }
            if (serialListFlavor.equals(flavor)) {
                return true;
            }
            return false;
        }
    }
}
