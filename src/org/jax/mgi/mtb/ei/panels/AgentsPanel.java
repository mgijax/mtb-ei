/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AgentsPanel.java,v 1.1 2007/04/30 15:50:52 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.editors.LVBeanCellEditor;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.models.AgentsDTOTableModel;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;
import org.jdesktop.swingworker.SwingWorker;

/**
 * For editing <b>Agent</b> data.
 *
 *
 *
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AgentsPanel.java,v 1.1 2007/04/30 15:50:52 mjv Exp
 * @date 2007/04/30 15:50:52
 */
public class AgentsPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    // custom JTable for sorting purposes
    private MXTable fxtblAgents = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form for <b>AgentType</b> data.
     */
    public AgentsPanel() {
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
            // agent types
            AgentsDTOTableModel<AgentDTO> tblmdlAgentType =
                    (AgentsDTOTableModel<AgentDTO>)fxtblAgents.getModel();

            if (tblmdlAgentType.hasBeenUpdated()) {
                return true;
            }

            return false;
        }
        return true;
    }

    /**
     * Save the agent information.
     * <p>
     * This is performed in a seperate thread since this could potentially be
     * a lengthy operation. A <code>MXProgressMonitor</code> is used to display
     * visual feedback to the user.
     */
    public void save() {
        // The following code saves the current value in the cell being edited
        // and stops the editing process:
        if (fxtblAgents.getCellEditor() != null) {
            fxtblAgents.getCellEditor().stopCellEditing();
        }

        SwingWorker<String, Object> sw = new SwingWorker<String, Object>() {
            protected String doInBackground() throws Exception {
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try {
                    progressMonitor.start("Saving Agent data...");
                    updateData();
                } catch (Exception e) {
                    Utils.log(e);
                }
                return "";
            }
            protected void done() {
                // to ensure that progress dlg is closed in case of
                // any exception
                progressMonitor.setCurrent("Done!",
                                           progressMonitor.getTotal());
            }

        };
        sw.execute();

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
        // initialize all objects related to agent data
        try {
            //comboAgentTypes = new javax.swing.JComboBox();
            //comboAgentTypes.removeAllItems();

            // make it so the agent key field is numeric only
            Utils.setNumericFilter(txtAgentKey);

            ///////////////////////////////////////////////////////////////////
            // agent types
            ///////////////////////////////////////////////////////////////////
            Map<Long,LabelValueBean<String,Long>> mapAgentTypes =
                    EIGlobals.getInstance().getAgentTypes();

            List<LabelValueBean<String,Long>> arrAgentTypes =
                    new ArrayList<LabelValueBean<String,Long>>(mapAgentTypes.values());
            arrAgentTypes.add(0, new LabelValueBean<String,Long>("-Select-", -1L));

            comboAgentTypes.setModel(new LVBeanListModel<String,Long>(arrAgentTypes));
            comboAgentTypes.setRenderer(new LVBeanListCellRenderer<String,Long>());
            comboAgentTypes.setSelectedIndex(0);

            ///////////////////////////////////////////////////////////////////
            // agents
            ///////////////////////////////////////////////////////////////////
            AgentDAO daoAgent = AgentDAO.getInstance();
            List<AgentDTO> listAgents = daoAgent.loadAll();

            for (AgentDTO dto : listAgents) {
                DataBean dtoSimple = dto.getDataBean();
                try{
                    LabelValueBean<String,Long> beanAgentType =
                            mapAgentTypes.get(dto.getAgentTypeKey());
                    dtoSimple.put(EIConstants.AGENT_TYPE_BEAN, beanAgentType);
                } catch (Exception e) {
                    Utils.log(e);
                }
                dto.setDataBean(dtoSimple);
            }

        
            List<String> arrHeaders = new ArrayList<String>(3);
            arrHeaders.add("Key");
            arrHeaders.add("Agent Type");
            arrHeaders.add("Agent");
            AgentsDTOTableModel<AgentDTO> tblmdlAgents = new AgentsDTOTableModel<AgentDTO>(listAgents, arrHeaders);
            List data = new ArrayList();
            fxtblAgents = new MXTable(data, arrHeaders);
            fxtblAgents.setColumnSizes(new int[]{75, 150, 0});
            fxtblAgents.setModel(tblmdlAgents);
            fxtblAgents.setDefaultRenderer(Object.class, new DTORenderer());
            fxtblAgents.getColumnModel().getColumn(1).setCellEditor(new LVBeanCellEditor(mapAgentTypes));
            fxtblAgents.enableToolTip(0, false);
            fxtblAgents.enableToolTip(1, false);

            JButton btnDelAgent = new JButton(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
            btnDelAgent.setIconTextGap(0);
            btnDelAgent.setMargin(new Insets(0, 0, 0, 0));
            btnDelAgent.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    removeAgent();
                }
            });
            jspData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jspData.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelAgent);
            jspData.setViewportView(fxtblAgents);

            pnlEdit.revalidate();
        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Remove the current highlighted row from the table.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void removeAgent() {
        int nRow = fxtblAgents.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel fxtm =
                    (MXDefaultTableModel)fxtblAgents.getModel();
            fxtm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Add a new <code>Agent</code> to the datamodel.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void addAgent() {
        boolean bAutoAssign = checkboxAutoAssign.isSelected();
        String strAgentKey = txtAgentKey.getText().trim();
        LabelValueBean<String,Long> bean =
                (LabelValueBean<String,Long>)comboAgentTypes.getSelectedItem();
        Long lAgentType = bean.getValue();
        String strAgent = txtAgent.getText().trim();
        long lAgentKey = -1;

        // validate that an agent key has been entered or will be auto assigned
        if ((!bAutoAssign) && (!StringUtils.hasValue(strAgentKey))) {
            Utils.showErrorDialog("Please either enter an Agent Key value or select Auto Assign.");
            txtAgentKey.requestFocus();
            return;
        }

        // validate that an agent type has been selected
        if (comboAgentTypes.getSelectedIndex() <= 0) {
            Utils.showErrorDialog("Please select an Agent Type.");
            comboAgentTypes.requestFocus();
            return;
        }

        // validate that an agent has been entered
        if (!StringUtils.hasValue(strAgent)) {
            Utils.showErrorDialog("Please enter an Agent value.");
            txtAgent.requestFocus();
            return;
        }

        // get the table model
        AgentsDTOTableModel<AgentDTO> tblmdlAgent =
                (AgentsDTOTableModel<AgentDTO>)fxtblAgents.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        AgentDTO dtoAgent = AgentDAO.getInstance().createAgentDTO();

        if (!bAutoAssign) {
            try {
                lAgentKey = Long.parseLong(strAgentKey);
            } catch (Exception e) {
                Utils.log(e);
                return;
            }
            dtoAgent.setAgentKey(new Long(lAgentKey));
        }

        dtoAgent.setAgentTypeKey(lAgentType);
        dtoAgent.setName(strAgent);
        dtoAgent.setCreateUser(dtoUser.getUserName());
        dtoAgent.setCreateDate(dNow);
        dtoAgent.setUpdateUser(dtoUser.getUserName());
        dtoAgent.setUpdateDate(dNow);
        dtoAgent.getDataBean().put(EIConstants.AGENT_TYPE_BEAN, bean);

        // add it to the table
        tblmdlAgent.addRow(dtoAgent);

        Utils.scrollToVisible(fxtblAgents,
                                 fxtblAgents.getRowCount() - 1, 0);
    }

    /**
     * Update the database with the changes made to the datamodel.  Proper
     * handling of transaction occurs (commit, rollback).
     */
    private void updateData() {
        boolean commit = false;
        AgentDAO daoAgent = AgentDAO.getInstance();

        try {
            updateProgress("Parsing agent data...");

            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            // audit trail information
            MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
            Date dNow = new Date();

            ///////////////////////////////////////////////////////////////////
            // save the agent types
            ///////////////////////////////////////////////////////////////////
            AgentsDTOTableModel<AgentDTO> tblmdlAgent =
                    (AgentsDTOTableModel<AgentDTO>)fxtblAgents.getModel();

            updateProgress("Saving agent data...");

            for (int i = 0; i < tblmdlAgent.getRowCount(); i++) {
                AgentDTO dtoAgent = (AgentDTO)tblmdlAgent.getDTO(i);

                if (dtoAgent.isModified()) {
                    dtoAgent.setUpdateUser(dtoUser.getUserName());
                    dtoAgent.setUpdateDate(dNow);
                }

                daoAgent.save(dtoAgent);
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("Agent data saved!");
            commit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog("Unable to save changes to Agents.\n\n" + e.getMessage(), e);
            return;
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to Agents.\n\n" + e2.getMessage(), e2);
                return;
            }
        }

        if (commit) {
            // make sure the globals are updated
            EIGlobals.getInstance().fetchAgents();
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
        lblAgentKey = new javax.swing.JLabel();
        txtAgentKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        lblAgent = new javax.swing.JLabel();
        txtAgent = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        headerPanelCreate = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblAgentType = new javax.swing.JLabel();
        comboAgentTypes = new javax.swing.JComboBox();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlEdit = new javax.swing.JPanel();
        jspData = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        headerPanelEdit = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCreate.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblAgentKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblAgentKey.setText("Agent Key");

        txtAgentKey.setColumns(10);
        txtAgentKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        lblAgent.setText("Agent ");

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png")));
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        headerPanelCreate.setDrawSeparatorUnderneath(true);
        headerPanelCreate.setText("Create Agent");

        lblAgentType.setText("Agent Type");

        org.jdesktop.layout.GroupLayout pnlCreateLayout = new org.jdesktop.layout.GroupLayout(pnlCreate);
        pnlCreate.setLayout(pnlCreateLayout);
        pnlCreateLayout.setHorizontalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
            .add(pnlCreateLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblAgent)
                    .add(lblAgentType)
                    .add(lblAgentKey))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCreateLayout.createSequentialGroup()
                        .add(txtAgentKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(checkboxAutoAssign)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAdd))
                    .add(txtAgent, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                    .add(comboAgentTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlCreateLayout.setVerticalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCreateLayout.createSequentialGroup()
                .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAgentKey)
                    .add(txtAgentKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(btnAdd))
                .add(6, 6, 6)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAgentType)
                    .add(comboAgentTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAgent)
                    .add(txtAgent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
        headerPanelEdit.setText("Edit Agents");

        org.jdesktop.layout.GroupLayout pnlEditLayout = new org.jdesktop.layout.GroupLayout(pnlEdit);
        pnlEdit.setLayout(pnlEditLayout);
        pnlEditLayout.setHorizontalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .add(10, 10, 10))
            .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
        );
        pnlEditLayout.setVerticalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
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
            txtAgentKey.setEditable(false);
            txtAgentKey.setText("");
        } else {
            txtAgentKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addAgent();
    }//GEN-LAST:event_btnAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private javax.swing.JComboBox comboAgentTypes;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCreate;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelEdit;
    private javax.swing.JScrollPane jspData;
    private javax.swing.JLabel lblAgent;
    private javax.swing.JLabel lblAgentKey;
    private javax.swing.JLabel lblAgentType;
    private javax.swing.JPanel pnlCreate;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtAgent;
    private javax.swing.JTextField txtAgentKey;
    // End of variables declaration//GEN-END:variables

}
