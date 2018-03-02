/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/filters/DocumentSizeFilter.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.filters;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

/**
 * <p>A <code>DocumentFilter</code> used to allow only a certain number of
 * specified characters to be entered into a JComponent.</p>
 *
 * <p>The following code illustrates usage of this class with a
 * <code>JTextField</code></p>
 *
 * <p><code>
 * JTextField txtLimit = new JTextFiled();
 * Document textDocOne = txtLimit.getDocument();
 * DocumentFilter filterOne = new DocumentSizeFilter();
 * ((AbstractDocument)textDocOne).setDocumentFilter(filterOne);
 * txtLimit.setDocument(textDocOne);
 * </code></p>
 *
 * @author mjv
 * @date 2007/04/30 15:50:45
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/filters/DocumentSizeFilter.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * @see javax.swing.text.DocumentFilter
 */

public class DocumentSizeFilter extends DocumentFilter {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private int maxCharacters;

    // ----------------------------------------------------------- Constructors

    public DocumentSizeFilter(int maxChars) {
        super();
        maxCharacters = maxChars;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This rejects the entire insertion if it would make the contents too 
     * long. Another option would be to truncate the inserted string so the 
     * contents would be exactly maxCharacters in length.
     */
    public void insertString(FilterBypass fb, int offs,
            String str, AttributeSet a)
            throws BadLocationException {

        if ((fb.getDocument().getLength() + str.length()) <= maxCharacters) {
            super.insertString(fb, offs, str, a);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * This rejects the entire insertion if it would make the contents too 
     * long. Another option would be to truncate the inserted string so the 
     * contents would be exactly maxCharacters in length.
     */
    public void replace(FilterBypass fb, int offs,
            int length,
            String str, AttributeSet a)
            throws BadLocationException {
        if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters) {
            super.replace(fb, offs, length, str, a);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}