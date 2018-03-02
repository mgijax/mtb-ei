/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/TumorClassificationPanel.java,v 1.1 2007/04/30 15:50:59 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorClassificationDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorClassificationDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.editors.LVBeanCellEditor;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.models.TumorClassificationDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
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
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/TumorClassificationPanel.java,v 1.1 2007/04/30 15:50:59 mjv Exp
 */
public class TumorClassificationPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    // custom JTable for sorting purposes
    private MXTable fxtblTumorClassification = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form for <b>TumorClassification</b> data.
     */
    public TumorClassificationPanel() {
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
            // Tumor Classifications
            TumorClassificationDTOTableModel<TumorClassificationDTO> tblmdlTumorClassification =
                    (TumorClassificationDTOTableModel<TumorClassificationDTO>)fxtblTumorClassification.getModel();

            if (tblmdlTumorClassification.hasBeenUpdated()) {
                return true;
            }

            return false;
        }
        StringUtils.out("Super changed...");
        return true;
    }

    /**
     * Save the tumor classification information.
     * <p>
     * This is performed in a seperate thread since this could potentially be
     * a lengthy operation. A <code>MXProgressMonitor</code> is used to display
     * visual feedback to the user.
     */
    public void save() {
        // The following code saves the current value in the cell being edited
        // and stops the editing process:
        if (fxtblTumorClassification.getCellEditor() != null) {
            fxtblTumorClassification.getCellEditor().stopCellEditing();
        }

        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try {
                    progressMonitor.start("Saving Tumor Classification data...");
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
            Utils.setNumericFilter(txtTumorClassificationKey);
            
            ///////////////////////////////////////////////////////////////////
            // init parent tumor classifications
            ///////////////////////////////////////////////////////////////////
            Map<Long,LabelValueBean<String,Long>> mapParentTC = 
                    EIGlobals.getInstance().getTumorClassifications();
            Map<Long,LabelValueBean<String,Long>> mapTempTC = new HashMap<Long,LabelValueBean<String,Long>>();
            mapTempTC.put(-1L, new LabelValueBean<String,Long>(" ", -1L));
            mapTempTC.putAll(mapParentTC);
            List<LabelValueBean<String,Long>> arrParentTC =
                    new ArrayList<LabelValueBean<String,Long>>(mapParentTC.values());
            arrParentTC.add(new LabelValueBean<String,Long>("-Select-", -1L));
            comboParentTumorClass.setModel(new LVBeanListModel<String,Long>(arrParentTC));
            comboParentTumorClass.setRenderer(new LVBeanListCellRenderer<String,Long>());
            comboParentTumorClass.addKeyListener(new LVBeanComboListener<String,Long>());
            comboParentTumorClass.setSelectedIndex(0);

            ///////////////////////////////////////////////////////////////////
            // Tumor Classifications
            ///////////////////////////////////////////////////////////////////
            TumorClassificationDAO daoTumorClassification = TumorClassificationDAO.getInstance();
            List<TumorClassificationDTO> arrTumorClassifications = daoTumorClassification.loadAll();

            for (TumorClassificationDTO dto : arrTumorClassifications) {
                DataBean dtoSimple = dto.getDataBean();
                try{
                    LabelValueBean<String,Long> beanParentOrgan =
                            mapParentTC.get(dto.getTCParentKey());

                    dtoSimple.put(EIConstants.TUMOR_CLASSIFICATION_BEAN, beanParentOrgan);
                } catch (Exception e) {
                    Utils.log(e);
                }
                dto.setDataBean(dtoSimple);
            }

            List<String> arrHeaders = new ArrayList<String>(3);
            arrHeaders.add("Key");
            arrHeaders.add("Tumor Classification");
            arrHeaders.add("Parent Tumor Class.");

            List data = new ArrayList();
            fxtblTumorClassification = new MXTable(data, arrHeaders);

            TumorClassificationDTOTableModel<TumorClassificationDTO> tblmdlTumorClassification = new TumorClassificationDTOTableModel<TumorClassificationDTO>(arrTumorClassifications, arrHeaders);
            fxtblTumorClassification.setModel(tblmdlTumorClassification);
            fxtblTumorClassification.setColumnSizes(new int[]{75, 0, 0});
            fxtblTumorClassification.setDefaultRenderer(Object.class, new DTORenderer());
            fxtblTumorClassification.getColumnModel().getColumn(2).setCellEditor(new LVBeanCellEditor<String,Long>(mapTempTC));
            fxtblTumorClassification.enableToolTips(false);

            JButton btnDelete = new JButton(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
            btnDelete.setIconTextGap(0);
            btnDelete.setMargin(new java.awt.Insets(0, 0, 0, 0));
            btnDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    removeTumorClassification();
                }
            });
            jspData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jspData.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelete);
            jspData.setViewportView(fxtblTumorClassification);
            pnlEdit.revalidate();

        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Remove the current highlighted row from the table.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void removeTumorClassification() {
        int nRow = fxtblTumorClassification.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel fxtm =
                    (MXDefaultTableModel)fxtblTumorClassification.getModel();
            fxtm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Add a new <code>TumorClassification</code> to the datamodel.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void addTumorClassification() {
        boolean bAutoAssign = checkboxAutoAssign.isSelected();
        String strTumorClassificationKey = txtTumorClassificationKey.getText().trim();
        String strTumorClassification = txtTumorClassification.getText().trim();
        long lTumorClassificationKey = -1;
        LabelValueBean<String,Long> beanParentTC =
                (LabelValueBean<String,Long>)comboParentTumorClass.getSelectedItem();
        Long lTCParent = beanParentTC.getValue();

        // validate that an Tumor Classification key has been entered or will be auto
        // assigned
        if ((!bAutoAssign) && (!StringUtils.hasValue(strTumorClassificationKey))) {
            Utils.showErrorDialog("Please either enter an Tumor Classification Key " +
                    "value or select Auto Assign.");
            txtTumorClassificationKey.requestFocus();
            return;
        }

        // validate that an Tumor Classification has been entered
        if (!StringUtils.hasValue(strTumorClassification)) {
            Utils.showErrorDialog("Please enter an Tumor Classification.");
            txtTumorClassification.requestFocus();
            return;
        }

        // get the table model
        TumorClassificationDTOTableModel<TumorClassificationDTO> tblmdlTumorClassification =
                (TumorClassificationDTOTableModel<TumorClassificationDTO>)fxtblTumorClassification.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        TumorClassificationDTO dtoTumorClassification =
                TumorClassificationDAO.getInstance().createTumorClassificationDTO();

        if (!bAutoAssign) {
            try {
                lTumorClassificationKey = Long.parseLong(strTumorClassificationKey);
            } catch (Exception e) {
                Utils.log(e);
                return;
            }
            dtoTumorClassification.setTumorClassificationKey(new Long(lTumorClassificationKey));
        }

        dtoTumorClassification.setName(strTumorClassification);
        dtoTumorClassification.setCreateDate(dNow);
        dtoTumorClassification.setCreateUser(dtoUser.getUserName());
        dtoTumorClassification.setUpdateDate(dNow);
        dtoTumorClassification.setUpdateUser(dtoUser.getUserName());

        if (-1 == lTCParent) {
             Utils.showSuccessDialog("This tumor classification will be its own parent");
       
            LabelValueBean<String,Long> selfParent = new LabelValueBean<String,Long>();
            selfParent.setLabel(strTumorClassification);
            selfParent.setValue(-1L);
            dtoTumorClassification.getDataBean().put(EIConstants.TUMOR_CLASSIFICATION_BEAN, selfParent);
            dtoTumorClassification.setTCParentKey(-1L);
        } else {
            dtoTumorClassification.setTCParentKey(new Long(lTCParent));
            
            // set the custom data for the data model to display the correct 
            // data
            dtoTumorClassification.getDataBean().put(EIConstants.TUMOR_CLASSIFICATION_BEAN,
                                                     beanParentTC);
        }
        
        // add it to the table
        tblmdlTumorClassification.addRow(dtoTumorClassification);

        Utils.scrollToVisible(fxtblTumorClassification,
                fxtblTumorClassification.getRowCount() - 1, 0);
    }

    /**
     * Update the database with the changes made to the datamodel.  Proper
     * handling of transaction occurs (commit, rollback).
     */
    private void updateData() {
        boolean commit = false;
        TumorClassificationDAO dao = TumorClassificationDAO.getInstance();

        try {
            updateProgress("Parsing Tumor Classification data...");

            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            // audit trail information
            MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
            Date dNow = new Date();

            ///////////////////////////////////////////////////////////////////
            // save the Tumor Classifications
            ///////////////////////////////////////////////////////////////////
            TumorClassificationDTOTableModel<TumorClassificationDTO> tblmdlTumorClassification =
                    (TumorClassificationDTOTableModel<TumorClassificationDTO>)fxtblTumorClassification.getModel();

            updateProgress("Saving Tumor Classification data...");

            for (int i = 0; i < tblmdlTumorClassification.getRowCount(); i++) {
                TumorClassificationDTO dto = (TumorClassificationDTO)tblmdlTumorClassification.getDTO(i);

                if(dto.getTCParentKey().longValue() == -1l) {
                    // it will be its own parent
                    // save the object so we can get a key assigned
                    dto.setTCParentKey(null);
                    dto = dao.save(dto);
                    
                    // set its parent to itself
                    dto.setTCParentKey(dto.getTumorClassificationKey());
                }
                
                if (dto.isModified()) {
                    dto.setUpdateUser(dtoUser.getUserName());
                    dto.setUpdateDate(dNow);
                }

                dao.save(dto);
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("Tumor Classification data saved!");
            commit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog("Unable to save changes to Tumor Classifications.\n\n" + e.getMessage(), e);
            return;
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to Tumor Classifications.\n\n" + e2.getMessage(), e2);
                return;
            }
        }

        if (commit) {
            // make sure the globals are updated
            EIGlobals.getInstance().fetchTumorClassifications();
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
        lblTumorClassificationKey = new javax.swing.JLabel();
        txtTumorClassificationKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        lblTumorClassification = new javax.swing.JLabel();
        txtTumorClassification = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        headerPanelCreate = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblParentTumorClass = new javax.swing.JLabel();
        comboParentTumorClass = new javax.swing.JComboBox();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlEdit = new javax.swing.JPanel();
        jspData = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        headerPanelEdit = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCreate.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblTumorClassificationKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblTumorClassificationKey.setText("Tumor Classification Key");

        txtTumorClassificationKey.setColumns(10);
        txtTumorClassificationKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        lblTumorClassification.setText("Tumor Classification");

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png")));
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        headerPanelCreate.setDrawSeparatorUnderneath(true);
        headerPanelCreate.setText("Create Tumor Classification");

        lblParentTumorClass.setText("Tumor Class. Parent");

        org.jdesktop.layout.GroupLayout pnlCreateLayout = new org.jdesktop.layout.GroupLayout(pnlCreate);
        pnlCreate.setLayout(pnlCreateLayout);
        pnlCreateLayout.setHorizontalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
            .add(pnlCreateLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblParentTumorClass)
                    .add(lblTumorClassificationKey)
                    .add(lblTumorClassification))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCreateLayout.createSequentialGroup()
                        .add(txtTumorClassificationKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(checkboxAutoAssign)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAdd))
                    .add(txtTumorClassification, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                    .add(comboParentTumorClass, 0, 402, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlCreateLayout.setVerticalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCreateLayout.createSequentialGroup()
                .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTumorClassificationKey)
                    .add(txtTumorClassificationKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(btnAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTumorClassification)
                    .add(txtTumorClassification, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblParentTumorClass)
                    .add(comboParentTumorClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
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
        headerPanelEdit.setText("Edit Tumor Classifications");

        org.jdesktop.layout.GroupLayout pnlEditLayout = new org.jdesktop.layout.GroupLayout(pnlEdit);
        pnlEdit.setLayout(pnlEditLayout);
        pnlEditLayout.setHorizontalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                .add(10, 10, 10))
            .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
        );
        pnlEditLayout.setVerticalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
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
            txtTumorClassificationKey.setEditable(false);
            txtTumorClassificationKey.setText("");
        } else {
            txtTumorClassificationKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addTumorClassification();
    }//GEN-LAST:event_btnAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private javax.swing.JComboBox comboParentTumorClass;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCreate;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelEdit;
    private javax.swing.JScrollPane jspData;
    private javax.swing.JLabel lblParentTumorClass;
    private javax.swing.JLabel lblTumorClassification;
    private javax.swing.JLabel lblTumorClassificationKey;
    private javax.swing.JPanel pnlCreate;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtTumorClassification;
    private javax.swing.JTextField txtTumorClassificationKey;
    // End of variables declaration//GEN-END:variables

}
