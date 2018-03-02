/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/StrainTypesPanel.java,v 1.1 2007/04/30 15:50:59 mjv Exp
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
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.models.StrainTypesDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;

/**
 * For editing <b>StrainType</b> data.
 * 
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/StrainTypesPanel.java,v 1.1 2007/04/30 15:50:59 mjv Exp
 */
public class StrainTypesPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    // custom JTable for sorting purposes
    private MXTable fxtblStrainTypes = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form for <b>StrainType</b> data.
     */
    public StrainTypesPanel() {
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
            // Strain Types
            StrainTypesDTOTableModel<StrainTypeDTO> tblmdlStrainType =
                    (StrainTypesDTOTableModel<StrainTypeDTO>)fxtblStrainTypes.getModel();

            if (tblmdlStrainType.hasBeenUpdated()) {
                return true;
            }

            return false;
        }
        StringUtils.out("Super changed...");
        return true;
    }

    /**
     * Save the strain type information.
     * <p>
     * This is performed in a seperate thread since this could potentially be
     * a lengthy operation. A <code>MXProgressMonitor</code> is used to display
     * visual feedback to the user.
     */
    public void save() {
        // The following code saves the current value in the cell being edited
        // and stops the editing process:
        if (fxtblStrainTypes.getCellEditor() != null) {
            fxtblStrainTypes.getCellEditor().stopCellEditing();
        }

        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try {
                    progressMonitor.start("Saving Strain Type data...");
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
        // initialize all objects related to Strain Type data
        try {
            // make it so the following fields accept numeric input only
            Utils.setNumericFilter(txtStrainTypeKey);

            ///////////////////////////////////////////////////////////////////
            // Strain Types
            ///////////////////////////////////////////////////////////////////
            StrainTypeDAO daoStrainType = StrainTypeDAO.getInstance();
            List<StrainTypeDTO> arrStrainTypeDTO = daoStrainType.loadAll();
            List<String> arrHeaders = new ArrayList<String>(2);
            arrHeaders.add("Key");
            arrHeaders.add("Strain Type");

            List data = new ArrayList();
            fxtblStrainTypes = new MXTable(data, arrHeaders);

            StrainTypesDTOTableModel<StrainTypeDTO> tblmdlStrainType = new StrainTypesDTOTableModel<StrainTypeDTO>(arrStrainTypeDTO, arrHeaders);
            fxtblStrainTypes.setModel(tblmdlStrainType);
            fxtblStrainTypes.setColumnSizes(new int[]{75, 0});
            fxtblStrainTypes.setDefaultRenderer(Object.class, new DTORenderer());
            fxtblStrainTypes.enableToolTips(false);

            JButton btnDelete = new JButton(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
            btnDelete.setIconTextGap(0);
            btnDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
            btnDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    removeStrainType();
                }
            });
            jspData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jspData.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelete);
            jspData.setViewportView(fxtblStrainTypes);
            pnlEdit.revalidate();

        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Remove the current highlighted row from the table.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void removeStrainType() {
        int nRow = fxtblStrainTypes.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel fxtm =
                    (MXDefaultTableModel)fxtblStrainTypes.getModel();
            fxtm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Add a new <code>StrainType</code> to the datamodel.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void addStrainType() {
        boolean bAutoAssign = checkboxAutoAssign.isSelected();
        String strStrainTypeKey = txtStrainTypeKey.getText().trim();
        String strStrainType = txtStrainType.getText().trim();
        long lStrainTypeKey = -1;

        // validate that an Strain Type key has been entered or will be auto
        // assigned
        if ((!bAutoAssign) && (!StringUtils.hasValue(strStrainTypeKey))) {
            Utils.showErrorDialog("Please either enter an Strain Type Key " +
                    "value or select Auto Assign.");
            txtStrainTypeKey.requestFocus();
            return;
        }

        // validate that an Strain Type has been entered
        if (!StringUtils.hasValue(strStrainType)) {
            Utils.showErrorDialog("Please enter an Strain Type.");
            txtStrainType.requestFocus();
            return;
        }

        // get the table model
        StrainTypesDTOTableModel<StrainTypeDTO> tblmdlStrainType =
                (StrainTypesDTOTableModel<StrainTypeDTO>)fxtblStrainTypes.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        StrainTypeDTO dtoStrainType =
                StrainTypeDAO.getInstance().createStrainTypeDTO();

        if (!bAutoAssign) {
            try {
                lStrainTypeKey = Long.parseLong(strStrainTypeKey);
            } catch (Exception e) {
                Utils.log(e);
                return;
            }
            dtoStrainType.setStrainTypeKey(new Long(lStrainTypeKey));
        }

        dtoStrainType.setType(strStrainType);
        dtoStrainType.setCreateDate(dNow);
        dtoStrainType.setCreateUser(dtoUser.getUserName());
        dtoStrainType.setUpdateDate(dNow);
        dtoStrainType.setUpdateUser(dtoUser.getUserName());

        // add it to the table
        tblmdlStrainType.addRow(dtoStrainType);

        Utils.scrollToVisible(fxtblStrainTypes,
                fxtblStrainTypes.getRowCount() - 1, 0);
    }

    /**
     * Update the database with the changes made to the datamodel.  Proper
     * handling of transaction occurs (commit, rollback).
     */
    private void updateData() {
        boolean commit = false;
        StrainTypeDAO dao = StrainTypeDAO.getInstance();

        try {
            updateProgress("Parsing Strain Type data...");

            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            // audit trail information
            MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
            Date dNow = new Date();

            ///////////////////////////////////////////////////////////////////
            // save the Strain Types
            ///////////////////////////////////////////////////////////////////
            StrainTypesDTOTableModel<StrainTypeDTO> tblmdlStrainType =
                    (StrainTypesDTOTableModel<StrainTypeDTO>)fxtblStrainTypes.getModel();

            updateProgress("Saving Strain Type data...");

            for (int i = 0; i < tblmdlStrainType.getRowCount(); i++) {
                StrainTypeDTO dto = (StrainTypeDTO)tblmdlStrainType.getDTO(i);

                if (dto.isModified()) {
                    dto.setUpdateUser(dtoUser.getUserName());
                    dto.setUpdateDate(dNow);
                }

                dao.save(dto);
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("Strain Type data saved!");
            commit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog("Unable to save changes to Strain Types.\n\n" + e.getMessage(), e);
            return;
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to Strain Types.\n\n" + e2.getMessage(), e2);
                return;
            }
        }

        if (commit) {
            // make sure the globals are updated
            EIGlobals.getInstance().fetchStrainTypes();
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
        lblStrainTypeKey = new javax.swing.JLabel();
        txtStrainTypeKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        lblStrainType = new javax.swing.JLabel();
        txtStrainType = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        headerPanelCreate = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlEdit = new javax.swing.JPanel();
        jspData = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        headerPanelEdit = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCreate.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblStrainTypeKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblStrainTypeKey.setText("Strain Type Key");

        txtStrainTypeKey.setColumns(10);
        txtStrainTypeKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        lblStrainType.setText("Strain Type");

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png")));
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        headerPanelCreate.setDrawSeparatorUnderneath(true);
        headerPanelCreate.setText("Create Strain Type");

        org.jdesktop.layout.GroupLayout pnlCreateLayout = new org.jdesktop.layout.GroupLayout(pnlCreate);
        pnlCreate.setLayout(pnlCreateLayout);
        pnlCreateLayout.setHorizontalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCreateLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblStrainTypeKey)
                    .add(lblStrainType))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCreateLayout.createSequentialGroup()
                        .add(txtStrainTypeKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(checkboxAutoAssign)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAdd))
                    .add(txtStrainType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
                .addContainerGap())
            .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
        );
        pnlCreateLayout.setVerticalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCreateLayout.createSequentialGroup()
                .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblStrainTypeKey)
                    .add(txtStrainTypeKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(btnAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblStrainType)
                    .add(txtStrainType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
        headerPanelEdit.setText("Edit Strain Types");

        org.jdesktop.layout.GroupLayout pnlEditLayout = new org.jdesktop.layout.GroupLayout(pnlEdit);
        pnlEdit.setLayout(pnlEditLayout);
        pnlEditLayout.setHorizontalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                .add(10, 10, 10))
            .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
        );
        pnlEditLayout.setVerticalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
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
            .add(layout.createSequentialGroup()
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
            txtStrainTypeKey.setEditable(false);
            txtStrainTypeKey.setText("");
        } else {
            txtStrainTypeKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addStrainType();
    }//GEN-LAST:event_btnAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCreate;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelEdit;
    private javax.swing.JScrollPane jspData;
    private javax.swing.JLabel lblStrainType;
    private javax.swing.JLabel lblStrainTypeKey;
    private javax.swing.JPanel pnlCreate;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtStrainType;
    private javax.swing.JTextField txtStrainTypeKey;
    // End of variables declaration//GEN-END:variables

}
