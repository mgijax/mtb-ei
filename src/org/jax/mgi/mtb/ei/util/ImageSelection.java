/*
 * ImageSelection.java
 *
 * Created on March 1, 2006, 4:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jax.mgi.mtb.ei.util;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

// This class is used to hold an image while on the clipboard.
public class ImageSelection implements Transferable {
    private Image image;

    public ImageSelection(Image image) {
        this.image = image;
    }

    // Returns supported flavors
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    // Returns true if flavor is supported
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    // Returns image
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!DataFlavor.imageFlavor.equals(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return image;
    }
}