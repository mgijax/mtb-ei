/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/ProbesPanel.java,v 1.1 2007/04/30 15:50:56 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.models.ProbeDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;

/**
 * For editing <b>TumorClassification</b> data.
 * 
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/ProbesPanel.java,v 1.1 2007/04/30 15:50:56 mjv Exp
 */
public class ProbesPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    // custom JTable for sorting purposes
    private MXTable fxtblProbes = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form for <b>TumorClassification</b> data.
     */
    public ProbesPanel() {
        initComponents();
        initCustom();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Override the <code>CustomPanel</code> <b>isUpdated()</b> method.
     * 
     * 
     * 
     * @see org.jax.mgi.mtb.ei.panels.CustomPanel#isUpdated()
     */
    public boolean isUpdated() {
        if (!super.isUpdated()) {
            // probes
            ProbeDTOTableModel<ProbeDTO> tblmdlProbes =
                    (ProbeDTOTableModel<ProbeDTO>)fxtblProbes.getModel();

            if (tblmdlProbes.hasBeenUpdated()) {
                return true;
            }

            return false;
        }
        StringUtils.out("Super changed...");
        return true;
    }

    /**
     * Save the probe information.
     * <p>
     * This is performed in a seperate thread since this could potentially be
     * a lengthy operation. A <code>MXProgressMonitor</code> is used to display
     * visual feedback to the user.
     */
    public void save() {
        // The following code saves the current value in the cell being edited
        // and stops the editing process:
        if (fxtblProbes.getCellEditor() != null) {
            fxtblProbes.getCellEditor().stopCellEditing();
        }

        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try {
                    progressMonitor.start("Saving Probe data...");
                    updateData();
                } catch (Exception e) {
                    Utils.log(e);
                } finally{
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
     * Custom initilization of all GUI objects.
     */
    private void initCustom() {
        // initialize all objects related to Tumor Classification data
        try {
            // make it so the following fields accept numeric input only
            Utils.setNumericFilter(txtProbeKey);

            ///////////////////////////////////////////////////////////////////
            // probes
            ///////////////////////////////////////////////////////////////////
            ProbeDAO daoProbe = ProbeDAO.getInstance();
            List<ProbeDTO> listProbes = daoProbe.loadAll();
            List<String> arrHeaders = new ArrayList<String>(11);
            arrHeaders.add("Key");
            arrHeaders.add("Probe Name");
            arrHeaders.add("Target");
            arrHeaders.add("Counterstain");
            arrHeaders.add("URL");
            arrHeaders.add("Type");
            arrHeaders.add("Supplier Name");
            arrHeaders.add("Supplier Address");
            arrHeaders.add("Supplier URL");
            arrHeaders.add("Note");

            List data = new ArrayList();
            fxtblProbes = new MXTable(data, arrHeaders);

            ProbeDTOTableModel<ProbeDTO> tblmdlProbes = new ProbeDTOTableModel<ProbeDTO>(listProbes, arrHeaders);
            fxtblProbes.setModel(tblmdlProbes);
            fxtblProbes.setColumnSizes(new int[]{75, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            fxtblProbes.setDefaultRenderer(Object.class, new DTORenderer());
            fxtblProbes.enableToolTips(false);

            JButton btnDelete = new JButton(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
            btnDelete.setIconTextGap(0);
            btnDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
            btnDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    removeProbe();
                }
            });
            jspData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jspData.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelete);
            jspData.setViewportView(fxtblProbes);
            pnlEdit.revalidate();

        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Remove the current highlighted row from the table.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void removeProbe() {
        int nRow = fxtblProbes.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel fxtm =
                    (MXDefaultTableModel)fxtblProbes.getModel();
            fxtm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Add a new <code>Probe</code> to the datamodel.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void addProbe() {
        boolean bAutoAssign = checkboxAutoAssign.isSelected();
        long lProbeKey = -1;
        String strProbeKey = txtProbeKey.getText().trim();
        String strProbe = txtProbeName.getText().trim();
        String strTarget = txtTarget.getText().trim();
        String strType = txtProbeType.getText().trim();
        String strCounterstain = txtCounterstain.getText().trim();
        String strURL = txtURL.getText().trim();
        String strSupplierName = txtSupplierName.getText().trim();
        String strSupplierAddress = txtSupplierAddress.getText().trim();
        String strSupplierURL = txtSupplierURL.getText().trim();
        String strNote = txtareaNote.getText().trim();

        // validate that a Probekey has been entered or will be auto assigned
        if ((!bAutoAssign) && (!StringUtils.hasValue(strProbeKey))) {
            Utils.showErrorDialog("Please either enter a Probe Key " +
                    "value or select Auto Assign.");
            txtProbeKey.requestFocus();
            return;
        }

        // validate that a probe name has been entered
        if (!StringUtils.hasValue(strProbe)) {
            Utils.showErrorDialog("Please enter a Probe Name.");
            txtProbeName.requestFocus();
            return;
        }

        // validate that a target has been entered
        if (!StringUtils.hasValue(strTarget)) {
            Utils.showErrorDialog("Please enter a Target.");
            txtTarget.requestFocus();
            return;
        }

        // validate that a probe type has been entered
        if (!StringUtils.hasValue(strType)) {
            Utils.showErrorDialog("Please enter a Probe Type.");
            txtProbeType.requestFocus();
            return;
        }

        // get the table model
        ProbeDTOTableModel<ProbeDTO> tblmdlProbes =
                (ProbeDTOTableModel<ProbeDTO>)fxtblProbes.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        ProbeDTO dtoProbe = ProbeDAO.getInstance().createProbeDTO();

        if (!bAutoAssign) {
            try {
                lProbeKey = Long.parseLong(strProbeKey);
            } catch (Exception e) {
                Utils.log(e);
                return;
            }
            dtoProbe.setProbeKey(new Long(lProbeKey));
        }

        dtoProbe.setName(strProbe);
        dtoProbe.setTarget(strTarget);
        dtoProbe.setType(strType);

        dtoProbe.setCounterstain(StringUtils.hasValue(strCounterstain) ?
                                 strCounterstain : null);
        dtoProbe.setUrl(StringUtils.hasValue(strURL) ? strURL : null);
        dtoProbe.setSupplierName(StringUtils.hasValue(strSupplierName) ?
                                 strSupplierName : null);
        dtoProbe.setSupplierAddress(StringUtils.hasValue(strSupplierAddress) ?
                                    strSupplierAddress : null);
        dtoProbe.setSupplierUrl(StringUtils.hasValue(strSupplierURL) ?
                                strSupplierURL : null);
        dtoProbe.setNotes(StringUtils.hasValue(strNote) ? strNote : null);
        dtoProbe.setCreateUser(dtoUser.getUserName());
        dtoProbe.setCreateDate(dNow);
        dtoProbe.setUpdateUser(dtoUser.getUserName());
        dtoProbe.setUpdateDate(dNow);

        // add it to the table
        tblmdlProbes.addRow(dtoProbe);

        Utils.scrollToVisible(fxtblProbes,
                fxtblProbes.getRowCount() - 1, 0);
    }

    /**
     * Update the database with the changes made to the datamodel.  Proper
     * handling of transaction occurs (commit, rollback).
     */
    private void updateData() {
        boolean bCommit = false;
        ProbeDAO daoProbe = ProbeDAO.getInstance();

        try {
            updateProgress("Parsing Probe data...");

            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            // audit trail information
            MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
            Date dNow = new Date();

            ///////////////////////////////////////////////////////////////////
            // save the Probes
            ///////////////////////////////////////////////////////////////////
            ProbeDTOTableModel<ProbeDTO> tblmdlProbes =
                    (ProbeDTOTableModel<ProbeDTO>)fxtblProbes.getModel();

            updateProgress("Saving Probe data...");

            for (int i = 0; i < tblmdlProbes.getRowCount(); i++) {
                ProbeDTO dtoProbe = (ProbeDTO)tblmdlProbes.getDTO(i);

                if (dtoProbe.isModified()) {
                    dtoProbe.setUpdateUser(dtoUser.getUserName());
                    dtoProbe.setUpdateDate(dNow);
                }

                daoProbe.save(dtoProbe);
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("Probe data saved!");
            bCommit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog("Unable to save changes to Probes.\n\n" + e.getMessage(), e);
            return;
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(bCommit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to Probes.\n\n" + e2.getMessage(), e2);
                return;
            }
        }

        if (bCommit) {
            // make sure the globals are updated
            EIGlobals.getInstance().fetchProbes();
            initCustom();
        }

        setUpdated(false);
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------ NetBeans Generated Code
    // ------------------------------------------------------------------------
    // TAKE EXTREME CARE MODIFYING CODE BELOW THIS LINE

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlCreate = new javax.swing.JPanel();
        lblProbeKey = new javax.swing.JLabel();
        txtProbeKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        lblProbeName = new javax.swing.JLabel();
        txtProbeName = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        headerPanelCreate = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblCounterstain = new javax.swing.JLabel();
        lblProbeType = new javax.swing.JLabel();
        lblSupplierAddress = new javax.swing.JLabel();
        lblTarget = new javax.swing.JLabel();
        txtTarget = new javax.swing.JTextField();
        lblURL = new javax.swing.JLabel();
        lblSupplierName = new javax.swing.JLabel();
        lblSupplierURL = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        txtSupplierName = new javax.swing.JTextField();
        txtSupplierURL = new javax.swing.JTextField();
        txtCounterstain = new javax.swing.JTextField();
        txtProbeType = new javax.swing.JTextField();
        txtSupplierAddress = new javax.swing.JTextField();
        lblNote = new javax.swing.JLabel();
        jspNote = new javax.swing.JScrollPane();
        txtareaNote = new javax.swing.JTextArea();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlEdit = new javax.swing.JPanel();
        jspData = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        headerPanelEdit = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCreate.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblProbeKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblProbeKey.setText("Probe Key");

        txtProbeKey.setColumns(10);
        txtProbeKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        lblProbeName.setText("Probe Name");

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png")));
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        headerPanelCreate.setDrawSeparatorUnderneath(true);
        headerPanelCreate.setText("Create Probe");

        lblCounterstain.setText("Counterstain");

        lblProbeType.setText("Type");

        lblSupplierAddress.setText("Supplier Address");

        lblTarget.setText("Target");

        lblURL.setText("URL");

        lblSupplierName.setText("Supplier Name");

        lblSupplierURL.setText("Supplier URL");

        lblNote.setText("Note");

        txtareaNote.setColumns(20);
        txtareaNote.setRows(3);
        jspNote.setViewportView(txtareaNote);

        org.jdesktop.layout.GroupLayout pnlCreateLayout = new org.jdesktop.layout.GroupLayout(pnlCreate);
        pnlCreate.setLayout(pnlCreateLayout);
        pnlCreateLayout.setHorizontalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1007, Short.MAX_VALUE)
            .add(pnlCreateLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblNote)
                    .add(lblSupplierAddress)
                    .add(lblProbeType)
                    .add(lblCounterstain)
                    .add(lblProbeKey)
                    .add(lblProbeName))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnlCreateLayout.createSequentialGroup()
                        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(txtSupplierAddress)
                            .add(txtProbeType)
                            .add(txtCounterstain)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCreateLayout.createSequentialGroup()
                                .add(txtProbeKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(checkboxAutoAssign)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnAdd))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtProbeName))
                        .add(94, 94, 94)
                        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblTarget)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblURL)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblSupplierName)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblSupplierURL))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txtSupplierURL, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                            .add(txtSupplierName)
                            .add(txtURL)
                            .add(txtTarget)))
                    .add(jspNote))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pnlCreateLayout.linkSize(new java.awt.Component[] {txtCounterstain, txtProbeName, txtProbeType, txtSupplierAddress, txtSupplierName, txtSupplierURL, txtTarget, txtURL}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnlCreateLayout.setVerticalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCreateLayout.createSequentialGroup()
                .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProbeKey)
                    .add(txtProbeKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(btnAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProbeName)
                    .add(lblTarget)
                    .add(txtProbeName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCounterstain)
                    .add(lblURL)
                    .add(txtCounterstain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProbeType)
                    .add(lblSupplierName)
                    .add(txtProbeType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtSupplierName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSupplierAddress)
                    .add(lblSupplierURL)
                    .add(txtSupplierAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtSupplierURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblNote)
                    .add(jspNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        pnlEdit.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspData.setViewportView(tblData);

        headerPanelEdit.setDrawSeparatorUnderneath(true);
        headerPanelEdit.setText("Edit Probes");

        org.jdesktop.layout.GroupLayout pnlEditLayout = new org.jdesktop.layout.GroupLayout(pnlEdit);
        pnlEdit.setLayout(pnlEditLayout);
        pnlEditLayout.setHorizontalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)
                .add(10, 10, 10))
            .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1007, Short.MAX_VALUE)
        );
        pnlEditLayout.setVerticalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(btnSave)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnCancel, btnSave}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkboxAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignActionPerformed
        if (checkboxAutoAssign.isSelected()) {
            txtProbeKey.setEditable(false);
            txtProbeKey.setText("");
        } else {
            txtProbeKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addProbe();
    }//GEN-LAST:event_btnAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCreate;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelEdit;
    private javax.swing.JScrollPane jspData;
    private javax.swing.JScrollPane jspNote;
    private javax.swing.JLabel lblCounterstain;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblProbeKey;
    private javax.swing.JLabel lblProbeName;
    private javax.swing.JLabel lblProbeType;
    private javax.swing.JLabel lblSupplierAddress;
    private javax.swing.JLabel lblSupplierName;
    private javax.swing.JLabel lblSupplierURL;
    private javax.swing.JLabel lblTarget;
    private javax.swing.JLabel lblURL;
    private javax.swing.JPanel pnlCreate;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtCounterstain;
    private javax.swing.JTextField txtProbeKey;
    private javax.swing.JTextField txtProbeName;
    private javax.swing.JTextField txtProbeType;
    private javax.swing.JTextField txtSupplierAddress;
    private javax.swing.JTextField txtSupplierName;
    private javax.swing.JTextField txtSupplierURL;
    private javax.swing.JTextField txtTarget;
    private javax.swing.JTextField txtURL;
    private javax.swing.JTextArea txtareaNote;
    // End of variables declaration//GEN-END:variables

}
