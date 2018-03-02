/*
 * PowerEditor.java
 *
 * Created on September 15, 2005, 1:54 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jax.mgi.mtb.ei.panels;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import org.jax.mgi.mtb.ei.EIGlobals;

/**
 * <dl>
 * <dt><b>Creation date :</b></dt>
 * <dd> 8 oct. 2003 </dd>
 * </dl>
 *
 * @author Pierre LE LANNIC
 */
public class PowerEditor extends JPanel {
        private ArrayList theSet;
        private WordMenuWindow theWordMenu;
        private JTextComponent theTextComponent;
        private Window theOwner;

        private static final char[] WORD_SEPARATORS =
                      //  {' ', '\n', '\t', '.', ',', ';', '!', '?', '\'', '(', ')', '[', ']', '\"', '{', '}', '/', '\\', '<', '>'};
                  {' ', '\n', '\t', ';', '!', '?', '\'', '\"', '{', '}', '/', '\\'};

        private Word theCurrentWord;

        private class Word {
                private int theWordStart;
                private int theWordLength;

                public Word() {
                        theWordStart = -1;
                        theWordLength = 0;
                }

                public void setBounds(int aStart, int aLength) {
                        theWordStart = Math.max(-1, aStart);
                        theWordLength = Math.max(0, aLength);
                        if (theWordStart == -1) theWordLength = 0;
                        if (theWordLength == 0) theWordStart = -1;
                }

                public void increaseLength(int newCharLength) {
                        int max = theTextComponent.getText().length() - theWordStart;
                        theWordLength = Math.min(max, theWordLength + newCharLength);
                        if (theWordLength == 0) theWordStart = -1;
                }

                public void decreaseLength(int removedCharLength) {
                        theWordLength = Math.max(0, theWordLength - removedCharLength);
                        if (theWordLength == 0) theWordStart = -1;
                }

                public int getStart() {
                        return theWordStart;
                }

                public int getLength() {
                        return theWordLength;
                }

                public int getEnd() {
                        return theWordStart + theWordLength;
                }

                public String toString() {
                        String toReturn = null;
                        try {
                                toReturn = theTextComponent.getText(theWordStart, theWordLength);
                        } catch (BadLocationException e) {
                        }
                        if (toReturn == null) toReturn = "";
                        return toReturn;
                }
        }

        private class WordMenuWindow extends JWindow {
                private JList theList;
                private DefaultListModel theModel;
                private Point theRelativePosition;

                private class WordMenuKeyListener extends KeyAdapter {
                        public void keyPressed(KeyEvent e) {
                                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                        onSelected();
                                }
                        }
                }

                private class WordMenuMouseListener extends MouseAdapter {
                        public void mouseClicked(MouseEvent e) {
                                if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
                                        onSelected();
                                }
                        }
                }

                public WordMenuWindow() {
                        super(theOwner);
                        theModel = new DefaultListModel();
                        theRelativePosition = new Point(0, 0);
                        loadUIElements();
                        setEventManagement();
                }

                private void loadUIElements() {
                        theList = new JList(theModel) {
                                public int getVisibleRowCount() {
                                        return Math.min(theModel.getSize(), 10);
                                }
                        };
                        theList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        theList.setBackground(new Color(235, 244, 254));
                        JScrollPane scrollPane = new JScrollPane(theList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        scrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                        setContentPane(scrollPane);
                }

                private void setEventManagement() {
                        theList.addKeyListener(new WordMenuKeyListener());
                        theList.addMouseListener(new WordMenuMouseListener());
                }

                private void onSelected() {
                        String word = (String)theList.getSelectedValue();
                        setCurrentTypedWord(word);
                }

                public void display(Point aPoint) {
                        theRelativePosition = aPoint;
                        Point p = theTextComponent.getLocationOnScreen();
                        setLocation(new Point(p.x + aPoint.x, p.y + aPoint.y));
                        setVisible(true);
                }

                public void move() {
                        if (theRelativePosition != null) {
                                Point p = theTextComponent.getLocationOnScreen();
                                setLocation(new Point(p.x + theRelativePosition.x, p.y + theRelativePosition.y));
                        }
                }

                public void setWords(String[] someWords) {
                        theModel.clear();
                        if ((someWords == null) || (someWords.length == 0)) {
                                setVisible(false);
                                return;
                        }
                        for (int i = 0; i < someWords.length; i++) {
                                theModel.addElement(someWords[i]);
                        }
                        pack();
                        pack();
                }

                public void moveDown() {
                        if (theModel.getSize() < 1) return;
                        int current = theList.getSelectedIndex();
                        int newIndex = Math.min(theModel.getSize() - 1, current + 1);
                        theList.setSelectionInterval(newIndex, newIndex);
                        theList.scrollRectToVisible(theList.getCellBounds(newIndex, newIndex));
                }

                public void moveUp() {
                        if (theModel.getSize() < 1) return;
                        int current = theList.getSelectedIndex();
                        int newIndex = Math.max(0, current - 1);
                        theList.setSelectionInterval(newIndex, newIndex);
                        theList.scrollRectToVisible(theList.getCellBounds(newIndex, newIndex));
                }

                public void moveStart() {
                        if (theModel.getSize() < 1) return;
                        theList.setSelectionInterval(0, 0);
                        theList.scrollRectToVisible(theList.getCellBounds(0, 0));
                }

                public void moveEnd() {
                        if (theModel.getSize() < 1) return;
                        int endIndex = theModel.getSize() - 1;
                        theList.setSelectionInterval(endIndex, endIndex);
                        theList.scrollRectToVisible(theList.getCellBounds(endIndex, endIndex));
                }

                public void movePageUp() {
                        if (theModel.getSize() < 1) return;
                        int current = theList.getSelectedIndex();
                        int newIndex = Math.max(0, current - Math.max(0, theList.getVisibleRowCount() - 1));
                        theList.setSelectionInterval(newIndex, newIndex);
                        theList.scrollRectToVisible(theList.getCellBounds(newIndex, newIndex));
                }

                public void movePageDown() {
                        if (theModel.getSize() < 1) return;
                        int current = theList.getSelectedIndex();
                        int newIndex = Math.min(theModel.getSize() - 1, current + Math.max(0, theList.getVisibleRowCount() - 1));
                        theList.setSelectionInterval(newIndex, newIndex);
                        theList.scrollRectToVisible(theList.getCellBounds(newIndex, newIndex));
                }
        }

        public PowerEditor(ArrayList aLexiconSet, JFrame anOwner, JTextComponent aTextComponent) {
                super(new BorderLayout());
                theOwner = anOwner;
                theTextComponent = aTextComponent;
                theWordMenu = new WordMenuWindow();
                theSet = aLexiconSet;
                theCurrentWord = new Word();
                loadUIElements();
                setEventManagement();
        }

        public JTextComponent getTextComponent() {
                return theTextComponent;
        }

        private void loadUIElements() {
                add(theTextComponent, BorderLayout.CENTER);
        }

        private void setEventManagement() {
                theTextComponent.addFocusListener(new FocusAdapter() {
                        public void focusLost(FocusEvent e) {
                                theTextComponent.requestFocus();
                        }
                });
                theTextComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK), "controlEspace");
                theTextComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.CTRL_MASK), "home");
                theTextComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.CTRL_MASK), "end");
                theTextComponent.getActionMap().put("controlEspace", new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                                onControlSpace();
                        }
                });
                theTextComponent.getActionMap().put("home", new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                                theWordMenu.moveStart();
                        }
                });
                theTextComponent.getActionMap().put("end", new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                                theWordMenu.moveEnd();
                        }
                });
                theTextComponent.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                                super.mouseClicked(e);
                                if (theWordMenu.isVisible()) {
                                        theWordMenu.setVisible(false);
                                }
                        }
                });
                theTextComponent.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent e) {
                                onControlSpace();
                                if (e.isConsumed()) return;
                                if (theWordMenu.isVisible()) {
                                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                                theWordMenu.onSelected();
                                                e.consume();
                                        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                                                theWordMenu.moveDown();
                                                e.consume();
                                        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                                                theWordMenu.moveUp();
                                                e.consume();
                                        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                                                theWordMenu.movePageDown();
                                                e.consume();
                                        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                                                theWordMenu.movePageUp();
                                                e.consume();
                                        }
                                }
                        }
                });
                theOwner.addComponentListener(new ComponentAdapter() {
                        public void componentHidden(ComponentEvent e) {
                                theWordMenu.setVisible(false);
                        }

                        public void componentMoved(ComponentEvent e) {
                                if (theWordMenu.isVisible()) {
                                        theWordMenu.move();
                                }
                        }
                });
                theTextComponent.getDocument().addDocumentListener(new DocumentListener() {
                        public void insertUpdate(DocumentEvent e) {
                                if (theWordMenu.isVisible()) {
                                        int beginIndex = e.getOffset();
                                        int endIndex = beginIndex + e.getLength();
                                        String newCharacters = theTextComponent.getText().substring(beginIndex, endIndex);
                                        for (int i = 0; i < WORD_SEPARATORS.length; i++) {
                                                if (newCharacters.indexOf(WORD_SEPARATORS[i]) != -1) {
                                                        theCurrentWord.setBounds(-1, 0);
                                                        theWordMenu.setWords(null);
                                                        theWordMenu.setVisible(false);
                                                        return;
                                                }
                                        }
                                        theCurrentWord.increaseLength(e.getLength());
                                        updateMenu();
                                }
                        }

                        public void removeUpdate(DocumentEvent e) {
                                if (theWordMenu.isVisible()) {
                                        theCurrentWord.decreaseLength(e.getLength());
                                        if (theCurrentWord.getLength() == 0) {
                                                theWordMenu.setWords(null);
                                                theWordMenu.setVisible(false);
                                                return;
                                        }
                                        updateMenu();
                                }
                        }

                        public void changedUpdate(DocumentEvent e) {
                        }
                });
        }

        private String[] getWords(String aWord) {
                EIGlobals.getInstance().log(aWord);
                aWord = aWord.trim().toLowerCase();
                ArrayList returnSet = new ArrayList();
                
                for (int i = 0; i < theSet.size(); i++) {
                //for (Iterator iterator = theSet.iterator(); iterator.hasNext();) {
                        String string = (String)theSet.get(i);
                        if (string.startsWith(aWord)) {
                                returnSet.add(string);
                                EIGlobals.getInstance().log("Adding " + string);
                        }
                }
                return (String[])returnSet.toArray(new String[0]);
        }

        private static boolean isWordSeparator(char aChar) {
                for (int i = 0; i < WORD_SEPARATORS.length; i++) {
                        if (aChar == WORD_SEPARATORS[i]) return true;
                }
                return false;
        }

        private void onControlSpace() {
                theCurrentWord = getCurrentTypedWord();
                if (theCurrentWord.getLength() == 0) return;
                int index = theCurrentWord.getStart();
                Rectangle rect = null;
                try {
                        rect = theTextComponent.getUI().modelToView(theTextComponent, index);
                } catch (BadLocationException e) {
                }
                if (rect == null) return;
                theWordMenu.display(new Point(rect.x, rect.y + rect.height));
                updateMenu();
                theTextComponent.requestFocus();
        }

        private void updateMenu() {
                if (theCurrentWord.getLength() == 0) return;
                String[] words = getWords(theCurrentWord.toString());
                theWordMenu.setWords(words);
        }

        private Word getCurrentTypedWord() {
                Word word = new Word();
                int position = theTextComponent.getCaretPosition();
                if (position == 0) return word;
                int index = position - 1;
                boolean found = false;
                while ((index > 0) && (!found)) {
                        char current = theTextComponent.getText().charAt(index);
                        if (isWordSeparator(current)) {
                                found = true;
                                index++;
                        } else {
                                index--;
                        }
                }
                word.setBounds(index, position - index);
                return word;
        }

        private void setCurrentTypedWord(String aWord) {
                theWordMenu.setVisible(false);
                if (aWord != null) {
                        if (aWord.length() > theCurrentWord.getLength()) {
                                String newLetters = aWord.substring(theCurrentWord.getLength());
                                try {
                                        theTextComponent.getDocument().insertString(theCurrentWord.getEnd(), newLetters, null);
                                } catch (BadLocationException e) {
                                }
                                theCurrentWord.increaseLength(newLetters.length());
                        }
                }
                theTextComponent.requestFocus();
                theTextComponent.setCaretPosition(theCurrentWord.getEnd());
                theCurrentWord.setBounds(-1, 0);
        }

        private static ArrayList loadLexiconFromFile(File aFile) throws IOException {
                ArrayList returnSet = new ArrayList();
                BufferedReader reader = new BufferedReader(new FileReader(aFile));
                String line = reader.readLine();
                while (line != null) {
                        returnSet.add(line);
                        line = reader.readLine();
                }
                reader.close();
                return returnSet;
        }

        public static void main(String[] args) {
                try {
                        File lexiconFile = new File("./lexicon.txt");
                        ArrayList lexicon =  loadLexiconFromFile(lexiconFile);
                        final JFrame frame = new JFrame("Test");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JTextPane textArea = new JTextPane();
                        PowerEditor powerEditor = new PowerEditor(lexicon, frame, textArea);
                        JScrollPane scrollpane = new JScrollPane(powerEditor);
                        scrollpane.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
                                                                                                        BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
                        frame.addWindowListener(new WindowAdapter() {
                                public void windowClosing(WindowEvent e) {
                                        System.exit(0);
                                }
                        });
                        frame.setContentPane(scrollpane);
                        SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                        frame.setSize(500, 500);
                                        frame.setVisible(true);
                                }
                        });
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}
