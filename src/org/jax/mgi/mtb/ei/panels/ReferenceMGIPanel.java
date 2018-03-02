/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/ReferenceMGIPanel.java,v 1.1 2007/04/30 15:50:56 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Point;
import java.util.Date;
import org.apache.log4j.Logger;
import org.jax.mgi.mtb.dao.custom.mtb.MTBSynchronizationUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.util.MGIReferenceAPIUtil;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * For looking up JNumbers in MGI and adding them to MTB.
 *
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel
 * @cvsheader
 * /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/ReferenceMGIPanel.java,v
 * 1.1 2007/04/30 15:50:56 mjv Exp
 */
public class ReferenceMGIPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none
    // ----------------------------------------------------- Instance Variables
    private final static Logger log
            = Logger.getLogger(ReferenceMGIPanel.class.getName());

    private ReferenceDTO dtoReference = null;

    // progress monitor
    MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors
    /**
     * Creates a new form ReferencePanel.
     */
    public ReferenceMGIPanel() {
        initComponents();
        initCustom();
    }

    // --------------------------------------------------------- Public Methods
    /**
     * Set the reference key for the panel.
     */
    public void loadData() {
        Runnable runnable = new Runnable() {
            public void run() {
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                progressMonitor.start("Loading JNumber: "
                        + txtJNumber.getText());
                try {
                    lookupData();
                } catch (Exception e) {
                    log.error("Unable to load JNumber: " + txtJNumber.getText(), e);
                } finally {
                    // to ensure that progress dlg is closed in case of
                    // any exception
                    progressMonitor.setCurrent("Done!",
                            progressMonitor.getTotal());
                }
            }
        };

        new Thread(runnable).start();
    }

    // ------------------------------------------------------ Protected Methods
    // none
    // -------------------------------------------------------- Private Methods
    /**
     * Provide visual feedback to the end user.
     *
     * @param strMessage the message to display
     */
    private void updateProgress(String strMessage) {
        if (StringUtils.hasValue(strMessage)) {
            if (progressMonitor != null) {
                progressMonitor.setCurrent(strMessage,
                        progressMonitor.getCurrent());
            }
        }
    }

    /**
     * Lookup the JNumber in MGI.
     */
    private void lookupData() {
        String strJNumber = txtJNumber.getText();

        if (!StringUtils.hasValue(strJNumber)) {
            Utils.showErrorDialog("Please enter a JNumber to retrieve.");
            return;
        }

        updateProgress("Loading JNumber from MGI...");

        try {
            MTBSynchronizationUtilDAO daoSynch
                    = MTBSynchronizationUtilDAO.getInstance();

            daoSynch.setMGIInfo(EIGlobals.getInstance().getMGIUser(),
                    EIGlobals.getInstance().getMGIPassword(),
                    EIGlobals.getInstance().getMGIDriver(),
                    EIGlobals.getInstance().getMGIUrl());

            String refKey = daoSynch.getReferenceKeyFromMGI(strJNumber);
            MGIReferenceAPIUtil apiUtil = new MGIReferenceAPIUtil();
            dtoReference = apiUtil.getReferenceByKey(refKey);

            if (dtoReference == null) {
                Utils.showErrorDialog("Unable to retrieve JNumber: "
                        + strJNumber);
                return;
            }

            txtTitle.setText(dtoReference.getTitle());

            txtAuthors.setText(dtoReference.getAuthors());

            txtPrimaryAuthor.setText(dtoReference.getPrimaryAuthor());
            txtJournal.setText(dtoReference.getJournal());
            txtCitation.setText(dtoReference.getCitation());
            txtShortCitation.setText(dtoReference.getShortCitation());
            txtVolume.setText(dtoReference.getVolume());
            txtIssue.setText(dtoReference.getIssue());
            txtPages.setText(dtoReference.getPages());
            txtYear.setText(dtoReference.getYear());
            txtareaAbstract.setText(dtoReference.getAbstractText());

            updateProgress("JNumber loaded from MGI.");

        } catch (Exception e) {
            Utils.log(e);
            return;
        }
    }

    /**
     * Perform any custom initialization needed.
     */
    private void initCustom() {
        ; // no-op'd
    }

    /**
     * Insert the reference information and associated data in the database.
     * <p>
     * This is an all or nothing insert. Either everything the user has filled
     * in gets committed to the database or nothing does.
     */
    private void insertData() {
        ReferenceDAO daoReference = ReferenceDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            updateProgress("Parsing reference data...");

            // add the audit trail
            MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
            Date dNow = new Date();

            dtoReference.setCreateUser(dtoUser.getUserName());
            dtoReference.setCreateDate(dNow);
            dtoReference.setUpdateUser(dtoUser.getUserName());
            dtoReference.setUpdateDate(dNow);

            ///////////////////////////////////////////////////////////////////
            // save the reference
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving Reference data...");
            dtoReference = daoReference.save(dtoReference);
            updateProgress("Reference data saved!");

            ///////////////////////////////////////////////////////////////////
            // save the accession information
            ///////////////////////////////////////////////////////////////////
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            String strJNumber = txtJNumber.getText();
            long lNumericPart = Utils.parseJNumber(strJNumber);

            dtoAccession.setAccID(txtJNumber.getText().trim());
            dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_REFERENCE);
            dtoAccession.setObjectKey(dtoReference.getReferenceKey());
            dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
            dtoAccession.setPrefixPart("J:");
            dtoAccession.setNumericPart(lNumericPart);
            dtoAccession.setCreateUser(dtoUser.getUserName());
            dtoAccession.setCreateDate(dNow);
            dtoAccession.setUpdateUser(dtoUser.getUserName());
            dtoAccession.setUpdateDate(dNow);

            updateProgress("Saving J#...");
            dtoAccession = daoAccession.save(dtoAccession);
            String strPubMedID = (String) dtoReference.getDataBean().get("pubMedID");

            long pmIDNum = (Long.parseLong(strPubMedID));

            dtoAccession = daoAccession.createAccessionDTO();

            dtoAccession.setAccID(strPubMedID);
            dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_REFERENCE);
            dtoAccession.setObjectKey(dtoReference.getReferenceKey());
            dtoAccession.setSiteInfoKey(29);
            dtoAccession.setPrefixPart("");
            dtoAccession.setNumericPart(pmIDNum);
            dtoAccession.setCreateUser(dtoUser.getUserName());
            dtoAccession.setCreateDate(dNow);
            dtoAccession.setUpdateUser(dtoUser.getUserName());
            dtoAccession.setUpdateDate(dNow);

            updateProgress("Saving PubMed ID...");
            dtoAccession = daoAccession.save(dtoAccession);
            updateProgress("Accession data saved!");

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("JNumber loaded from MGI into MTB.");
            bCommit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog(e.getMessage(), e);
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(bCommit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to add Reference.", e2);
            }
            if (bCommit) {
                switchFromAddToEdit();
            } else {
                Utils.showErrorDialog("Unable to add Reference.");
            }
        }
    }

    /**
     * Simple method to close the add form and switch to the edit form. The
     * window location is tracked to make it seemless to the end user.
     */
    private void switchFromAddToEdit() {
        Point point = customInternalFrame.getLocation();
        customInternalFrame.dispose();
        EIGlobals.getInstance().getMainFrame().launchReferenceEditWindow(
                dtoReference.getReferenceKey().longValue(), point, true);
    }

    /**
     * Save the reference information.
     * <p>
     * The reference information will be inserted. This is performed in a
     * seperate thread since this could potentially be a lengthy operation. A
     * <code>MXProgressMonitor</code> is used to display visual feedback to the
     * user.
     */
    public void save() {
        if (dtoReference == null) {
            Utils.showErrorDialog("Please load an MGI JNumber first.");
            return;
        }

        String strJNumber = txtJNumber.getText();

        if (!StringUtils.hasValue(strJNumber)) {
            Utils.showErrorDialog("Please enter a JNumber.");
            return;
        }

        long lReferenceKey = EIGlobals.getInstance().getRefByAcc(strJNumber);

        if (lReferenceKey > 0) {
            Utils.showErrorDialog("JNumber " + strJNumber + " "
                    + "already exists with Reference Key: "
                    + lReferenceKey);
            return;
        }

        Runnable runnable = new Runnable() {
            public void run() {
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try {
                    progressMonitor.start("Inserting reference data...");
                    insertData();
                } catch (Exception e) {
                    Utils.log(e);
                } finally {
                    // to ensure that progress dlg is closed in case of
                    // any exception
                    progressMonitor.setCurrent("Done!",
                            progressMonitor.getTotal());
                }
            }
        };

        new Thread(runnable).start();

        setUpdated(false);
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------ NetBeans Generated Code
    // ------------------------------------------------------------------------
    // TAKE EXTREME CARE MODIFYING CODE BELOW THIS LINE
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlReference = new javax.swing.JPanel();
        headerPanelReference = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblTitle = new javax.swing.JLabel();
        lblTitle2 = new javax.swing.JLabel();
        lblAuthors = new javax.swing.JLabel();
        lblAuthors2 = new javax.swing.JLabel();
        lblPrimaryAuthor = new javax.swing.JLabel();
        lblJournal = new javax.swing.JLabel();
        lblCitation = new javax.swing.JLabel();
        lblShortCitation = new javax.swing.JLabel();
        lblVolume = new javax.swing.JLabel();
        lblAbstract = new javax.swing.JLabel();
        lblJNumber = new javax.swing.JLabel();
        jspAbstract = new javax.swing.JScrollPane();
        txtareaAbstract = new javax.swing.JTextArea();
        txtVolume = new javax.swing.JTextField();
        txtJNumber = new javax.swing.JTextField();
        lblIssue = new javax.swing.JLabel();
        txtIssue = new javax.swing.JTextField();
        lblPages = new javax.swing.JLabel();
        txtPages = new javax.swing.JTextField();
        lblYear = new javax.swing.JLabel();
        txtYear = new javax.swing.JTextField();
        btnLookup = new javax.swing.JButton();
        txtTitle = new javax.swing.JTextField();
        txtTitle2 = new javax.swing.JTextField();
        txtAuthors = new javax.swing.JTextField();
        txtAuthors2 = new javax.swing.JTextField();
        txtPrimaryAuthor = new javax.swing.JTextField();
        txtJournal = new javax.swing.JTextField();
        txtCitation = new javax.swing.JTextField();
        txtShortCitation = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        pnlReference.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        headerPanelReference.setDrawSeparatorUnderneath(true);
        headerPanelReference.setText("Add Reference");

        lblTitle.setText("Title");

        lblTitle2.setText("Title 2");

        lblAuthors.setText("Authors");

        lblAuthors2.setText("Authors 2");

        lblPrimaryAuthor.setText("Primary Author");

        lblJournal.setText("Journal");

        lblCitation.setText("Citation");

        lblShortCitation.setText("Short Citation");

        lblVolume.setText("Volume");

        lblAbstract.setText("Abstract");

        lblJNumber.setText("J Number");

        txtareaAbstract.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
        txtareaAbstract.setColumns(20);
        txtareaAbstract.setEditable(false);
        txtareaAbstract.setLineWrap(true);
        txtareaAbstract.setRows(5);
        txtareaAbstract.setWrapStyleWord(true);
        jspAbstract.setViewportView(txtareaAbstract);

        txtVolume.setColumns(10);
        txtVolume.setEditable(false);

        txtJNumber.setColumns(10);
        txtJNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtJNumberFocusLost(evt);
            }
        });

        lblIssue.setText("Issue");

        txtIssue.setColumns(10);
        txtIssue.setEditable(false);

        lblPages.setText("Pages");

        txtPages.setColumns(10);
        txtPages.setEditable(false);

        lblYear.setText("Year");

        txtYear.setColumns(10);
        txtYear.setEditable(false);

        btnLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png")));
        btnLookup.setText("Lookup");
        btnLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupActionPerformed(evt);
            }
        });

        txtTitle.setEditable(false);

        txtTitle2.setEditable(false);

        txtAuthors.setEditable(false);

        txtAuthors2.setEditable(false);

        txtPrimaryAuthor.setEditable(false);

        txtJournal.setEditable(false);

        txtCitation.setEditable(false);

        txtShortCitation.setEditable(false);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Save16.png")));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png")));
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlReferenceLayout = new org.jdesktop.layout.GroupLayout(pnlReference);
        pnlReference.setLayout(pnlReferenceLayout);
        pnlReferenceLayout.setHorizontalGroup(
            pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelReference, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
            .add(pnlReferenceLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlReferenceLayout.createSequentialGroup()
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblTitle)
                            .add(lblTitle2)
                            .add(lblAuthors)
                            .add(lblAuthors2)
                            .add(lblPrimaryAuthor)
                            .add(lblJournal)
                            .add(lblCitation)
                            .add(lblShortCitation)
                            .add(lblVolume)
                            .add(lblAbstract)
                            .add(lblJNumber))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jspAbstract, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .add(pnlReferenceLayout.createSequentialGroup()
                                .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(txtVolume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(pnlReferenceLayout.createSequentialGroup()
                                        .add(36, 36, 36)
                                        .add(lblIssue)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(txtIssue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(52, 52, 52)
                                        .add(lblPages)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(txtPages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(lblYear)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(txtYear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(pnlReferenceLayout.createSequentialGroup()
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(btnLookup))))
                            .add(txtTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .add(txtTitle2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .add(txtAuthors, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .add(txtAuthors2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .add(txtPrimaryAuthor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .add(txtJournal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .add(txtCitation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .add(txtShortCitation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)))
                    .add(pnlReferenceLayout.createSequentialGroup()
                        .add(btnSave)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel)))
                .addContainerGap())
        );

        pnlReferenceLayout.linkSize(new java.awt.Component[] {btnCancel, btnSave}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnlReferenceLayout.setVerticalGroup(
            pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlReferenceLayout.createSequentialGroup()
                .add(headerPanelReference, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlReferenceLayout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblTitle)
                            .add(txtTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblTitle2)
                            .add(txtTitle2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAuthors)
                            .add(txtAuthors, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAuthors2)
                            .add(txtAuthors2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblPrimaryAuthor)
                            .add(txtPrimaryAuthor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblJournal)
                            .add(txtJournal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblCitation)
                            .add(txtCitation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblShortCitation)
                            .add(txtShortCitation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblVolume)
                            .add(txtVolume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblIssue)
                            .add(txtIssue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblPages)
                            .add(txtPages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblYear)
                            .add(txtYear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jspAbstract, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                            .add(lblAbstract)))
                    .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(btnLookup)
                        .add(lblJNumber)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlReference, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlReference, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupActionPerformed
        loadData();
    }//GEN-LAST:event_btnLookupActionPerformed

    private void txtJNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberFocusLost
        Utils.fixJNumber(txtJNumber);
    }//GEN-LAST:event_txtJNumberFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLookup;
    private javax.swing.JButton btnSave;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelReference;
    private javax.swing.JScrollPane jspAbstract;
    private javax.swing.JLabel lblAbstract;
    private javax.swing.JLabel lblAuthors;
    private javax.swing.JLabel lblAuthors2;
    private javax.swing.JLabel lblCitation;
    private javax.swing.JLabel lblIssue;
    private javax.swing.JLabel lblJNumber;
    private javax.swing.JLabel lblJournal;
    private javax.swing.JLabel lblPages;
    private javax.swing.JLabel lblPrimaryAuthor;
    private javax.swing.JLabel lblShortCitation;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitle2;
    private javax.swing.JLabel lblVolume;
    private javax.swing.JLabel lblYear;
    private javax.swing.JPanel pnlReference;
    private javax.swing.JTextField txtAuthors;
    private javax.swing.JTextField txtAuthors2;
    private javax.swing.JTextField txtCitation;
    private javax.swing.JTextField txtIssue;
    private javax.swing.JTextField txtJNumber;
    private javax.swing.JTextField txtJournal;
    private javax.swing.JTextField txtPages;
    private javax.swing.JTextField txtPrimaryAuthor;
    private javax.swing.JTextField txtShortCitation;
    private javax.swing.JTextField txtTitle;
    private javax.swing.JTextField txtTitle2;
    private javax.swing.JTextField txtVolume;
    private javax.swing.JTextField txtYear;
    private javax.swing.JTextArea txtareaAbstract;
    // End of variables declaration//GEN-END:variables

}
