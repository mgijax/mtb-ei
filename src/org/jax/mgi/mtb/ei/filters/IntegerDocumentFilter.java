/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/filters/IntegerDocumentFilter.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.filters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * <p>A <code>DocumentFilter</code> used to allow only integer values to be
 * entered into a JComponent.</p>
 *
 * <p>The following code illustrates usage of this class with a
 * <code>JTextField</code></p>
 *
 * <p><code>
 * JTextField txtNumeric = new JTextFiled();
 * Document textDocOne = txtNumeric.getDocument();
 * DocumentFilter filterOne = new IntegerDocumentFilter();
 * ((AbstractDocument)textDocOne).setDocumentFilter(filterOne);
 * txtNumeric.setDocument(textDocOne);
 * </code></p>
 *
 * @author mjv
 * @date 2007/04/30 15:50:46
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/filters/IntegerDocumentFilter.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 * @see javax.swing.text.DocumentFilter
 */
public class IntegerDocumentFilter extends DocumentFilter {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables
    // none

    // ----------------------------------------------------------- Constructors
    // none

    // --------------------------------------------------------- Public Methods

    /**
     * Invoked prior to insertion of text into the specified Document.
     *
     * @param fb FilterBypass that can be used to mutate Document
     * @param nOffset the nOffset into the document to insert the content >= 0.
     *        All positions that track change at or after the given location
     *        will move.
     * @param strText the strText to insert
     * @param attr  the attributes to associate with the inserted content.
     *        This may be null if there are no attributes.
     * @exception BadLocationException the given insert position is not a
     *            valid position within the document
     */
    public void insertString(DocumentFilter.FilterBypass fb, int nOffset,
                             String strText, AttributeSet attr)
            throws BadLocationException {

        if (strText == null) {
            return;
        } else {
            replace(fb, nOffset, 0, strText, attr);
        }
    }

    /**
     * Removes the specified region of text, bypassing the DocumentFilter.
     *
     * @param fb FilterBypass that can be used to mutate Document
     * @param nOffset the nOffset from the beginning >= 0
     * @param nLength the number of characters to remove >= 0
     * @exception BadLocationException some portion of the removal range was
     *            not a valid part of the document.  The location in the
     *            exception is the first bad position encountered.
     */
    public void remove(DocumentFilter.FilterBypass fb,
            int nOffset, int nLength)
            throws BadLocationException {

        replace(fb, nOffset, nLength, "", null);
    }

    /**
     * Deletes the region of strText from <code>nOffset</code> to
     * <code>nOffset + nLength</code>, and replaces it with
     * <code>strText</code>.
     *
     * @param nOffset Location in Document
     * @param nLength Length of strText to delete
     * @param string Text to insert, null indicates no strText to insert
     * @param attrs AttributeSet indicating attributes of inserted strText,
     *              null is legal.
     * @exception BadLocationException the given insert is not a valid position
     *            within the document
     */
    public void replace(DocumentFilter.FilterBypass fb,
            int nOffset, int nLength, String strText, AttributeSet attrs)
            throws BadLocationException {

        Document doc = fb.getDocument();
        int nCurrentLength = doc.getLength();
        String currentContent = doc.getText(0, nCurrentLength);
        String strBefore = currentContent.substring(0, nOffset);
        String strAfter = currentContent.substring(nLength + nOffset,
                                                   nCurrentLength);
        String strNewValue = strBefore +
                (strText == null ? "" : strText) + strAfter;
        checkInput(strNewValue, nOffset);
        fb.replace(nOffset, nLength, strText, attrs);
    }

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Make sure that the value entered is indeed numeric.
     *
     * @param strProposedValue the text to check
     * @param nOffset the position where the bad text is located
     * @return the integer value
     */
    private int checkInput(String strProposedValue, int nOffset)
        throws BadLocationException {

        int nNewValue = 0;
        if (strProposedValue.length() > 0) {
            try {
                nNewValue = Integer.parseInt(strProposedValue);
            } catch (NumberFormatException e) {
                throw new BadLocationException(strProposedValue, nOffset);
            }
        }
        return nNewValue;
    }
}