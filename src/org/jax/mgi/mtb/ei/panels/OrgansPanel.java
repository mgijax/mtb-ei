/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/OrgansPanel.java,v 1.1 2007/04/30 15:50:55 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Insets;
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
import org.jax.mgi.mtb.dao.gen.mtb.OrganDAO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.editors.LVBeanCellEditor;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.models.DTOTableModel;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.models.OrgansDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;

/**
 * For editing <b>Organ</b> data.
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/OrgansPanel.java,v 1.1 2007/04/30 15:50:55 mjv Exp
 */
public class OrgansPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none
    // ----------------------------------------------------- Instance Variables
    // custom JTable for sorting purposes
    private MXTable fxtblOrgans = null;
    private OrgansDTOTableModel<OrganDTO> tblmdlOrgans = null;
    private OrgansDTOTableModel<OrganDTO> tblmdlOrgansFiltered = null;
    Map<Long, LabelValueBean<String, Long>> mapTempOrgans = new HashMap();
    Map<Long, LabelValueBean<String, Long>> mapAnatomicalSystems =
            EIGlobals.getInstance().getAnatomicalSystems();
    // progress monitor
    private MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors
    /**
     * Creates new form for <b>TumorClassification</b> data.
     */
    public OrgansPanel() {
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
            // organs
            if (((DTOTableModel) fxtblOrgans.getModel()).hasBeenUpdated()) {
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
        if (fxtblOrgans.getCellEditor() != null) {
            fxtblOrgans.getCellEditor().stopCellEditing();
        }

        Runnable runnable = new Runnable() {

            public void run() {
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try {
                    progressMonitor.start("Saving Organ data...");
                    updateData();
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
            Utils.setNumericFilter(txtOrganKey);

            ///////////////////////////////////////////////////////////////////
            // init anatomical systems
            ///////////////////////////////////////////////////////////////////

            List<LabelValueBean<String, Long>> arrAnatomicalSystems =
                    new ArrayList(mapAnatomicalSystems.values());
            arrAnatomicalSystems.add(0, new LabelValueBean<String, Long>("-Select-", -1L));
            comboAnatomicalSystem.setModel(
                    new LVBeanListModel<String, Long>(arrAnatomicalSystems));
            comboAnatomicalSystem.setRenderer(new LVBeanListCellRenderer<String, Long>());
            comboAnatomicalSystem.addKeyListener(new LVBeanComboListener<String, Long>());
            comboAnatomicalSystem.setSelectedIndex(0);

            ///////////////////////////////////////////////////////////////////
            // init anatomical systems (for organ filter)
            ///////////////////////////////////////////////////////////////////
            List<LabelValueBean<String, Long>> arrAnatomicalSystemsFilter =
                    new ArrayList<LabelValueBean<String, Long>>(mapAnatomicalSystems.values());
            arrAnatomicalSystemsFilter.add(
                    new LabelValueBean<String, Long>("-Select-", -1L));
            comboAnatomicalSystemFilter.setModel(
                    new LVBeanListModel<String, Long>(arrAnatomicalSystemsFilter));
            comboAnatomicalSystemFilter.setRenderer(
                    new LVBeanListCellRenderer<String, Long>());
            comboAnatomicalSystemFilter.addKeyListener(
                    new LVBeanComboListener<String, Long>());
            comboAnatomicalSystemFilter.setSelectedIndex(0);

            ///////////////////////////////////////////////////////////////////
            // init parent organs
            ///////////////////////////////////////////////////////////////////
            Map<Long, LabelValueBean<String, Long>> mapParentOrgans =
                    EIGlobals.getInstance().getOrgans();

            mapTempOrgans.put(-1L, new LabelValueBean<String, Long>(" ", -1L));
            mapTempOrgans.putAll(mapParentOrgans);
            List<LabelValueBean<String, Long>> arrParentOrgans =
                    new ArrayList<LabelValueBean<String, Long>>(mapParentOrgans.values());
            arrParentOrgans.add(new LabelValueBean<String, Long>("-Select-", -1L));
            comboParentOrgan.setModel(new LVBeanListModel<String, Long>(arrParentOrgans));
            comboParentOrgan.setRenderer(new LVBeanListCellRenderer<String, Long>());
            comboParentOrgan.addKeyListener(new LVBeanComboListener<String, Long>());
            comboParentOrgan.setSelectedIndex(0);

            ///////////////////////////////////////////////////////////////////
            // Organs
            ///////////////////////////////////////////////////////////////////
            OrganDAO daoOrgan = OrganDAO.getInstance();
            List<OrganDTO> arrOrgans = daoOrgan.loadAll();

            for (OrganDTO dtoO : arrOrgans) {
                DataBean dtoSimple = dtoO.getDataBean();
                try {
                    LabelValueBean<String, Long> beanAnatomicalSystem =
                            mapAnatomicalSystems.get(dtoO.getAnatomicalSystemKey());
                    dtoSimple.put(EIConstants.ANATOMICAL_SYSTEM_BEAN,
                            beanAnatomicalSystem);

                    LabelValueBean<String, Long> beanParentOrgan =
                            mapParentOrgans.get(dtoO.getOrganParentKey());

                    dtoSimple.put(EIConstants.ORGAN_BEAN, beanParentOrgan);
                } catch (Exception e) {
                    Utils.log(e);
                }
                dtoO.setDataBean(dtoSimple);
            }

            List<String> arrHeaders = new ArrayList<String>(3);
            arrHeaders.add("Key");
            arrHeaders.add("Anatomical System");
            arrHeaders.add("Organ");
            arrHeaders.add("Parent Organ");

            List data = new ArrayList();
            fxtblOrgans = new MXTable(data, arrHeaders);

            tblmdlOrgans = new OrgansDTOTableModel<OrganDTO>(arrOrgans, arrHeaders);
            fxtblOrgans.setModel(tblmdlOrgans);
            fxtblOrgans.setColumnSizes(new int[]{75, 0, 0, 0});
            fxtblOrgans.setDefaultRenderer(Object.class, new DTORenderer());
            fxtblOrgans.getColumnModel().getColumn(1).setCellEditor(new LVBeanCellEditor<String, Long>(mapAnatomicalSystems));
            fxtblOrgans.getColumnModel().getColumn(3).setCellEditor(new LVBeanCellEditor<String, Long>(mapTempOrgans));
            fxtblOrgans.enableToolTip(0, false);
            fxtblOrgans.enableToolTip(1, false);
            fxtblOrgans.enableToolTip(4, false);

            JButton btnDelete = new JButton(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
            btnDelete.setIconTextGap(0);
            btnDelete.setMargin(new Insets(0, 0, 0, 0));
            btnDelete.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    removeOrgan();
                }
            });
            jspData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jspData.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelete);
            jspData.setViewportView(fxtblOrgans);
            pnlEdit.revalidate();

        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Remove the current highlighted row from the table.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void removeOrgan() {
        int nRow = fxtblOrgans.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel fxtm =
                    (MXDefaultTableModel) fxtblOrgans.getModel();
            fxtm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Add a new <code>Organ</code> to the datamodel.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void addOrgan() {
        boolean bAutoAssign = checkboxAutoAssign.isSelected();
        String strOrganKey = txtOrganKey.getText().trim();
        LabelValueBean<String, Long> beanAnatomicalSystem =
                (LabelValueBean<String, Long>) comboAnatomicalSystem.getSelectedItem();
        LabelValueBean<String, Long> beanParentOrgan =
                (LabelValueBean<String, Long>) comboParentOrgan.getSelectedItem();
        Long lAnantomicalSystem = beanAnatomicalSystem.getValue();
        Long lOrganParent = beanParentOrgan.getValue();
        String strOrgan = txtOrgan.getText().trim();
        long lOrganKey = -1;

        // validate that an Organ key has been entered or will be auto
        // assigned
        if ((!bAutoAssign) && (!StringUtils.hasValue(strOrganKey))) {
            Utils.showErrorDialog("Please either enter an Organ Key "
                    + "value or select Auto Assign.");
            txtOrganKey.requestFocus();
            return;
        }

        // validate that a Anatomical Sysem has been selected
        if (comboAnatomicalSystem.getSelectedIndex() <= 0) {
            Utils.showErrorDialog("Please select an Anatomical System.");
            comboAnatomicalSystem.requestFocus();
            return;
        }

        // validate that an organ name has been entered
        if (!StringUtils.hasValue(strOrgan)) {
            Utils.showErrorDialog("Please enter an Organ name.");
            txtOrgan.requestFocus();
            return;
        }

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        OrganDTO dtoOrgan = OrganDAO.getInstance().createOrganDTO();

        if (!bAutoAssign) {
            try {
                lOrganKey = Long.parseLong(strOrganKey);
            } catch (Exception e) {
                Utils.log(e);
                return;
            }
            dtoOrgan.setOrganKey(new Long(lOrganKey));
        }

        dtoOrgan.setAnatomicalSystemKey(lAnantomicalSystem);
        dtoOrgan.setName(strOrgan);
        dtoOrgan.setCreateUser(dtoUser.getUserName());
        dtoOrgan.setCreateDate(dNow);
        dtoOrgan.setUpdateUser(dtoUser.getUserName());
        dtoOrgan.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoOrgan.getDataBean().put(EIConstants.ANATOMICAL_SYSTEM_BEAN,
                beanAnatomicalSystem);

        if (-1 == beanParentOrgan.getValue()) {
            Utils.showSuccessDialog("This organ will be its own parent");
       
            LabelValueBean<String,Long> selfParent = new LabelValueBean<String,Long>();
            selfParent.setLabel(strOrgan);
            selfParent.setValue(-1L);
                    
            dtoOrgan.getDataBean().put(EIConstants.ORGAN_BEAN,
                   selfParent);
            dtoOrgan.setOrganParentKey(-1L);
        } else {
            dtoOrgan.setOrganParentKey(lOrganParent);

            // set the custom data for the data model to display the correct 
            // data
            dtoOrgan.getDataBean().put(EIConstants.ORGAN_BEAN,
                    beanParentOrgan);
        }

        // add it to the table
        tblmdlOrgans.addRow(dtoOrgan);

        Utils.scrollToVisible(fxtblOrgans,
                fxtblOrgans.getRowCount() - 1, 0);
    }

    /**
     * Update the database with the changes made to the datamodel.  Proper
     * handling of transaction occurs (commit, rollback).
     */
    private void updateData() {
        boolean bCommit = false;
        OrganDAO daoOrgan = OrganDAO.getInstance();

        try {
            updateProgress("Parsing Organ data...");

            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            // audit trail information
            MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
            Date dNow = new Date();

            ///////////////////////////////////////////////////////////////////
            // save the Organs
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving Organ data...");

            for (int i = 0; i < tblmdlOrgans.getRowCount(); i++) {
                OrganDTO dtoOrgan = (OrganDTO) tblmdlOrgans.getDTO(i);

                //if the parent key is null or == -1 then the parent key should be set to the organ key
               
                if ((dtoOrgan.getOrganParentKey() == null)
                        ||((dtoOrgan.getOrganParentKey() != null) && (dtoOrgan.getOrganParentKey().longValue() == -1l))) {
                    dtoOrgan.setOrganParentKey(null);
                    // need to save the object so that it is given an organkey
                    dtoOrgan = daoOrgan.save(dtoOrgan);
                    //set the parent key to be the organ key
                    dtoOrgan.setOrganParentKey(dtoOrgan.getOrganKey());
                }


                if (dtoOrgan.isModified() ||  dtoOrgan.isOld()) {
                    dtoOrgan.setUpdateUser(dtoUser.getUserName());
                    dtoOrgan.setUpdateDate(dNow);
                    
                      daoOrgan.save(dtoOrgan);
                }

              
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("Organ data saved!");
            bCommit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog("Unable to save changes to Organs.\n\n" + e.getMessage(), e);
            return;
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(bCommit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to Organs.\n\n" + e2.getMessage(), e2);
                return;
            }
        }

        if (bCommit) {
            // make sure the globals are updated
            EIGlobals.getInstance().fetchOrgans();
            EIGlobals.getInstance().fetchOrgansUnfiltered();
            initCustom();
        }

        setUpdated(false);
    }

    /**
     * Set the filter settings.
     */
    public void setOrganFilter(boolean bContains, String strName, long lAnatomicalSystemKey) {
        //EIGlobals.getInstance().log("setting filter ");
        //EIGlobals.getInstance().log("contains="+bContains);
        //EIGlobals.getInstance().log("name="+strName);
        //EIGlobals.getInstance().log("anatomicalSystemKey="+lAnatomicalSystemKey);

        List<String> arrHeaders = new ArrayList<String>(4);
        arrHeaders.add("Key");
        arrHeaders.add("Anatomical System");
        arrHeaders.add("Organ");
        arrHeaders.add("Parent Organ");

        List data = new ArrayList();

        tblmdlOrgansFiltered = new OrgansDTOTableModel<OrganDTO>(data, arrHeaders);

        for (int i = 0; i < tblmdlOrgans.getRowCount(); i++) {
            OrganDTO dto = (OrganDTO) tblmdlOrgans.getDTO(i);
            boolean matchName = false;
            boolean matchType = false;

            if (StringUtils.hasValue(strName)) {
                //EIGlobals.getInstance().log("comparing [" + strName + "] to [" + dto.getName() + "]");

                if (bContains) {
                    // check for 'contains'
                    if (dto.getName().toLowerCase().indexOf(strName.toLowerCase()) >= 0) {
                        // matches condition
                        matchName = true;
                    } else {
                        // no match
                        matchName = false;
                    }
                } else {
                    // check for 'equals'
                    if (StringUtils.equals(dto.getName().toLowerCase(), strName.toLowerCase())) {
                        // matches condition
                        matchName = true;
                    } else {
                        // no match
                        matchName = false;
                    }
                }



            } else {
                matchName = true;
            }

            if (lAnatomicalSystemKey > 0) {
                long key = dto.getAnatomicalSystemKey().longValue();

                //EIGlobals.getInstance().log("comparing [" + lAnatomicalSystemKey + "] to [" + key + "]");
                if (key == lAnatomicalSystemKey) {
                    matchType = true;
                } else {
                    matchType = false;
                }
            } else {
                matchType = true;
            }


            if ((matchName) && (matchType)) {
                //EIGlobals.getInstance().log("ADDING : " + dto.toString());
                tblmdlOrgansFiltered.addRow(dto);
            }
        }

        fxtblOrgans.setModel(tblmdlOrgansFiltered);

        fxtblOrgans.getColumnModel().getColumn(1).setCellEditor(new LVBeanCellEditor<String, Long>(mapAnatomicalSystems));
        fxtblOrgans.getColumnModel().getColumn(3).setCellEditor(new LVBeanCellEditor<String, Long>(mapTempOrgans));
        fxtblOrgans.enableToolTip(0, false);
        fxtblOrgans.enableToolTip(1, false);
        fxtblOrgans.enableToolTip(4, false);
        pnlEdit.revalidate();
    }

    /**
     * Clear the filter settings.
     */
    private void clearFilter() {
        comboOrganNameFilter.setSelectedIndex(0);
        txtOrganNameFilter.setText("");
        comboAnatomicalSystemFilter.setSelectedIndex(0);
        fxtblOrgans.setModel(tblmdlOrgans);
        fxtblOrgans.getColumnModel().getColumn(1).setCellEditor(new LVBeanCellEditor<String, Long>(mapAnatomicalSystems));
        fxtblOrgans.getColumnModel().getColumn(3).setCellEditor(new LVBeanCellEditor<String, Long>(mapTempOrgans));
        fxtblOrgans.enableToolTip(0, false);
        fxtblOrgans.enableToolTip(1, false);
        fxtblOrgans.enableToolTip(4, false);

        pnlEdit.revalidate();
    }

    /**
     * Apply the filter settings.
     */
    private void applyFilter() {
        String strOrganName = txtOrganNameFilter.getText();
        String strCompare = (String) comboOrganNameFilter.getSelectedItem();
        long lAnatomicalSystemKey = -1;

        try {
            Object objAnatomicalSystem =
                    comboAnatomicalSystemFilter.getSelectedItem();

            if (comboAnatomicalSystemFilter.getSelectedIndex() > 0) {
                LabelValueBean<String, Long> beanAnatomicalSystem =
                        (LabelValueBean<String, Long>) objAnatomicalSystem;
                lAnatomicalSystemKey = beanAnatomicalSystem.getValue();
            }
        } catch (Exception e) {
            Utils.log(e);
        }

        boolean bContains = true;

        if (strCompare.charAt(0) == 'E') {
            bContains = false;
        }

        //EIGlobals.getInstance().log("From OrganFilterPanel: ");
        //EIGlobals.getInstance().log("contains="+bContains);
        //EIGlobals.getInstance().log("name="+strOrganName);
        //EIGlobals.getInstance().log("anatomicalSystemKey="+lAnatomicalSystemKey);

        setOrganFilter(bContains, strOrganName, lAnatomicalSystemKey);
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
        lblOrganKey = new javax.swing.JLabel();
        txtOrganKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        lblAnatomicalSystem = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        headerPanelCreate = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        comboAnatomicalSystem = new javax.swing.JComboBox();
        lblOrgan = new javax.swing.JLabel();
        txtOrgan = new javax.swing.JTextField();
        lblParentOrgan = new javax.swing.JLabel();
        comboParentOrgan = new javax.swing.JComboBox();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlEdit = new javax.swing.JPanel();
        jspData = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        headerPanelEdit = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        pnlOrganFilter = new javax.swing.JPanel();
        lblOrganNameFIlter = new javax.swing.JLabel();
        lblAnatomicalSystemFilter = new javax.swing.JLabel();
        comboOrganNameFilter = new javax.swing.JComboBox();
        comboAnatomicalSystemFilter = new javax.swing.JComboBox();
        txtOrganNameFilter = new javax.swing.JTextField();
        btnApplyFilter = new javax.swing.JButton();
        btnClearFilter = new javax.swing.JButton();

        pnlCreate.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblOrganKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblOrganKey.setText("Organ Key");

        txtOrganKey.setColumns(10);
        txtOrganKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        lblAnatomicalSystem.setText("Anatomical System");

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png")));
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        headerPanelCreate.setDrawSeparatorUnderneath(true);
        headerPanelCreate.setText("Create Organ");

        lblOrgan.setText("Organ Name");

        lblParentOrgan.setText("Parent Organ");

        org.jdesktop.layout.GroupLayout pnlCreateLayout = new org.jdesktop.layout.GroupLayout(pnlCreate);
        pnlCreate.setLayout(pnlCreateLayout);
        pnlCreateLayout.setHorizontalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
            .add(pnlCreateLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblOrgan)
                    .add(lblOrganKey)
                    .add(lblAnatomicalSystem)
                    .add(lblParentOrgan))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCreateLayout.createSequentialGroup()
                        .add(txtOrganKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(checkboxAutoAssign)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAdd))
                    .add(comboAnatomicalSystem, 0, 500, Short.MAX_VALUE)
                    .add(txtOrgan, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .add(comboParentOrgan, 0, 500, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlCreateLayout.setVerticalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCreateLayout.createSequentialGroup()
                .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOrganKey)
                    .add(txtOrganKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(btnAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAnatomicalSystem)
                    .add(comboAnatomicalSystem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOrgan)
                    .add(txtOrgan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblParentOrgan)
                    .add(comboParentOrgan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
        headerPanelEdit.setText("Edit Organ");

        pnlOrganFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Organ Filter"));
        lblOrganNameFIlter.setText("Organ Name");

        lblAnatomicalSystemFilter.setText("Anatomical System");

        comboOrganNameFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contains", "Equals" }));

        btnApplyFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/TableEdit16.png")));
        btnApplyFilter.setText("Apply Filter");
        btnApplyFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyFilterActionPerformed(evt);
            }
        });

        btnClearFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Refresh16.png")));
        btnClearFilter.setText("Clear Filter");
        btnClearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFilterActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlOrganFilterLayout = new org.jdesktop.layout.GroupLayout(pnlOrganFilter);
        pnlOrganFilter.setLayout(pnlOrganFilterLayout);
        pnlOrganFilterLayout.setHorizontalGroup(
            pnlOrganFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlOrganFilterLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlOrganFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblAnatomicalSystemFilter)
                    .add(lblOrganNameFIlter))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOrganFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlOrganFilterLayout.createSequentialGroup()
                        .add(comboOrganNameFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtOrganNameFilter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))
                    .add(comboAnatomicalSystemFilter, 0, 335, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOrganFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(btnApplyFilter)
                    .add(btnClearFilter))
                .addContainerGap())
        );

        pnlOrganFilterLayout.linkSize(new java.awt.Component[] {btnApplyFilter, btnClearFilter}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnlOrganFilterLayout.setVerticalGroup(
            pnlOrganFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlOrganFilterLayout.createSequentialGroup()
                .add(pnlOrganFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOrganNameFIlter)
                    .add(comboOrganNameFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnApplyFilter)
                    .add(txtOrganNameFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOrganFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAnatomicalSystemFilter)
                    .add(btnClearFilter)
                    .add(comboAnatomicalSystemFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlEditLayout = new org.jdesktop.layout.GroupLayout(pnlEdit);
        pnlEdit.setLayout(pnlEditLayout);
        pnlEditLayout.setHorizontalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
            .add(pnlEditLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlOrganFilter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(pnlEditLayout.createSequentialGroup()
                .addContainerGap()
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlEditLayout.setVerticalGroup(
            pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditLayout.createSequentialGroup()
                .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOrganFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
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

    private void btnClearFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFilterActionPerformed
        clearFilter();
    }//GEN-LAST:event_btnClearFilterActionPerformed

    private void btnApplyFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyFilterActionPerformed
        applyFilter();
    }//GEN-LAST:event_btnApplyFilterActionPerformed

    private void checkboxAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignActionPerformed
        if (checkboxAutoAssign.isSelected()) {
            txtOrganKey.setEditable(false);
            txtOrganKey.setText("");
        } else {
            txtOrganKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addOrgan();
    }//GEN-LAST:event_btnAddActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnApplyFilter;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private javax.swing.JComboBox comboAnatomicalSystem;
    private javax.swing.JComboBox comboAnatomicalSystemFilter;
    private javax.swing.JComboBox comboOrganNameFilter;
    private javax.swing.JComboBox comboParentOrgan;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCreate;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelEdit;
    private javax.swing.JScrollPane jspData;
    private javax.swing.JLabel lblAnatomicalSystem;
    private javax.swing.JLabel lblAnatomicalSystemFilter;
    private javax.swing.JLabel lblOrgan;
    private javax.swing.JLabel lblOrganKey;
    private javax.swing.JLabel lblOrganNameFIlter;
    private javax.swing.JLabel lblParentOrgan;
    private javax.swing.JPanel pnlCreate;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JPanel pnlOrganFilter;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtOrgan;
    private javax.swing.JTextField txtOrganKey;
    private javax.swing.JTextField txtOrganNameFilter;
    // End of variables declaration//GEN-END:variables
}
