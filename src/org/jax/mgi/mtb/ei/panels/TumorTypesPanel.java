/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/TumorTypesPanel.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.ei.gui.CustomInternalFrame;
import org.jax.mgi.mtb.ei.gui.MainFrame;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.editors.LVBeanCellEditor;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.models.TumorTypeDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.gui.menu.MXHeaderMenuItem;
import org.jax.mgi.mtb.gui.menu.MXHtmlMenuItem;

/**
 * For editing <b>TumorClassification</b> data.
 * 
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/TumorTypesPanel.java,v 1.1 2007/04/30 15:51:00 mjv Exp
 * @date 2007/04/30 15:51:00
 */
public class TumorTypesPanel extends CustomPanel implements ActionListener {

    // -------------------------------------------------------------- Constants

    private static final String ACTION_VIEW = "view";
    private static final String ACTION_DELETE = "delete";


    // ----------------------------------------------------- Instance Variables

    // custom JTable for sorting purposes
    private MXTable fxtblTumorType = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form for <b>TumorClassification</b> data.
     */
    public TumorTypesPanel() {
        initComponents();
        initCustom();
    }

    // --------------------------------------------------------- Public Methods

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo(ACTION_DELETE) == 0) {
            removeTumorType();
        } else if (e.getActionCommand().compareTo(ACTION_VIEW) == 0) {
            MainFrame mainFrame = EIGlobals.getInstance().getMainFrame();
            CustomInternalFrame customInternalFrame =
                    mainFrame.launchTumorFrequencySearchWindow();
            TumorFrequencySearchPanel panelTumorFrequencySearch =
                    (TumorFrequencySearchPanel)customInternalFrame.getCustomPanel();
            TumorTypeDTOTableModel<TumorTypeDTO> tblmdlTumorType =
                    (TumorTypeDTOTableModel<TumorTypeDTO>)fxtblTumorType.getModel();

            int nRow = fxtblTumorType.getSelectedRow();
            panelTumorFrequencySearch.setTumorType(
                    (TumorTypeDTO)tblmdlTumorType.getDTO(nRow));
            fxtblTumorType.setRowSelectionInterval(nRow, nRow);
            panelTumorFrequencySearch.performSearch();
        }
    }

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
            TumorTypeDTOTableModel tblmdlTumorClassification =
                    (TumorTypeDTOTableModel)fxtblTumorType.getModel();

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
        if (fxtblTumorType.getCellEditor() != null) {
            fxtblTumorType.getCellEditor().stopCellEditing();
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
            Utils.setNumericFilter(txtTumorTypeKey);

            ///////////////////////////////////////////////////////////////////
            // init organs
            ///////////////////////////////////////////////////////////////////
            Map<Long,LabelValueBean<String,Long>> mapOrgans = EIGlobals.getInstance().getOrgansUnfiltered();
            List<LabelValueBean<String,Long>> arrOrgans = new ArrayList<LabelValueBean<String,Long>>(mapOrgans.values());
            arrOrgans.add(new LabelValueBean<String,Long>("-Select-", -1L));
            comboOrgan.setModel(new LVBeanListModel<String,Long>(arrOrgans));
            comboOrgan.setRenderer(new LVBeanListCellRenderer<String,Long>());
            comboOrgan.addKeyListener(new LVBeanComboListener<String,Long>());
            comboOrgan.setSelectedIndex(0);

            ///////////////////////////////////////////////////////////////////
            // init tumor classification
            ///////////////////////////////////////////////////////////////////
            Map<Long,LabelValueBean<String,Long>> mapTumorClass =
                    EIGlobals.getInstance().getTumorClassifications();
            List<LabelValueBean<String,Long>> arrTumorClass = new ArrayList<LabelValueBean<String,Long>>(mapTumorClass.values());
            arrTumorClass.add(new LabelValueBean<String,Long>("-Select-", -1L));
            comboTumorClassification.setModel(
                    new LVBeanListModel<String,Long>(arrTumorClass));
            comboTumorClassification.setRenderer(new LVBeanListCellRenderer<String,Long>());
            comboTumorClassification.addKeyListener(new LVBeanComboListener<String,Long>());
            comboTumorClassification.setSelectedIndex(0);

            ///////////////////////////////////////////////////////////////////
            // Tumor Types
            ///////////////////////////////////////////////////////////////////
            TumorTypeDAO daoTumorType = TumorTypeDAO.getInstance();
            List<TumorTypeDTO> arrTumorTypes = daoTumorType.loadAll();

            for (TumorTypeDTO dto : arrTumorTypes) {
                DataBean dtoSimple = dto.getDataBean();
                try{
                    LabelValueBean<String,Long> beanTumorType = mapTumorClass.get(
                                dto.getTumorClassificationKey());
                    dtoSimple.put(EIConstants.TUMOR_CLASSIFICATION, beanTumorType);
                    LabelValueBean beanOrgan =
                            (LabelValueBean)mapOrgans.get(
                                    dto.getOrganKey());
                    dtoSimple.put(EIConstants.ORGAN, beanOrgan);
                } catch (Exception e) {
                    Utils.log(e);
                }
                dto.setDataBean(dtoSimple);
            }

            List<String> arrHeaders = new ArrayList<String>(4);
            arrHeaders.add("Key");
            arrHeaders.add("Tumor Classification");
            arrHeaders.add("Organ");
            arrHeaders.add("Common Name");

            List data = new ArrayList();
            fxtblTumorType = new MXTable(data, arrHeaders);

            TumorTypeDTOTableModel<TumorTypeDTO> tblmdlTumorType = new TumorTypeDTOTableModel<TumorTypeDTO>(arrTumorTypes, arrHeaders);
            fxtblTumorType.setModel(tblmdlTumorType);
            fxtblTumorType.setColumnSizes(new int[]{75, 0, 0, 75});
            fxtblTumorType.setDefaultRenderer(Object.class, new DTORenderer());
            fxtblTumorType.getColumnModel().getColumn(1).setCellEditor(new LVBeanCellEditor(mapTumorClass));
            fxtblTumorType.getColumnModel().getColumn(2).setCellEditor(new LVBeanCellEditor(mapOrgans));
            fxtblTumorType.enableToolTip(0, false);
            fxtblTumorType.enableToolTip(1, false);
            fxtblTumorType.enableToolTip(2, false);

            final JPopupMenu menu = new JPopupMenu();
            MXHeaderMenuItem header = new MXHeaderMenuItem("Tumor Type Menu");
            menu.add(header);
            MXHtmlMenuItem itemView = new MXHtmlMenuItem("View all associated Tumor Frequency Records");
            itemView.setIcon(new ImageIcon(getClass().getResource(EIConstants.ICO_VIEW_DATA_16)));
            itemView.setActionCommand(ACTION_VIEW);
            itemView.addActionListener(this);
            menu.add(itemView);
            MXHtmlMenuItem itemDelete = new MXHtmlMenuItem("Delete this Tumor Type");
            itemDelete.setIcon(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
            itemDelete.setActionCommand(ACTION_DELETE);
            itemDelete.addActionListener(this);
            menu.add(itemDelete);

            // Set the component to show the popup menu
            fxtblTumorType.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent evt) {
                    if (evt.isPopupTrigger()) {
                        menu.show(evt.getComponent(), evt.getX(), evt.getY());
                        Point pt = new Point(evt.getX(), evt.getY());
                        int nRow = fxtblTumorType.rowAtPoint(pt);
                        fxtblTumorType.setRowSelectionInterval(nRow, nRow);

                    }
                }
                public void mouseReleased(MouseEvent evt) {
                    if (evt.isPopupTrigger()) {
                        menu.show(evt.getComponent(), evt.getX(), evt.getY());
                        Point pt = new Point(evt.getX(), evt.getY());
                        int nRow = fxtblTumorType.rowAtPoint(pt);
                        fxtblTumorType.setRowSelectionInterval(nRow, nRow);
                    }
                }
            });

            JButton btnDelete = new JButton(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
            btnDelete.setIconTextGap(0);
            btnDelete.setMargin(new Insets(0, 0, 0, 0));
            btnDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    removeTumorType();
                }
            });
            jspData.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jspData.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelete);
            jspData.setViewportView(fxtblTumorType);
            pnlEdit.revalidate();

        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Remove the current highlighted row from the table.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void removeTumorType() {
        int nRow = fxtblTumorType.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel fxtm =
                    (MXDefaultTableModel)fxtblTumorType.getModel();
            fxtm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Add a new <code>TumorClassification</code> to the datamodel.  When the user
     * clicks the "Save" button, the database will be updated.
     */
    private void addTumorType() {
        boolean bAutoAssign = checkboxAutoAssign.isSelected();
        String strTumorTypeKey = txtTumorTypeKey.getText().trim();
        String strCommonName = txtCommonName.getText().trim();
        LabelValueBean<String,Long> beanTumorClass =
                (LabelValueBean<String,Long>)comboTumorClassification.getSelectedItem();
        LabelValueBean<String,Long> beanOrgan = (LabelValueBean<String,Long>)comboOrgan.getSelectedItem();
        Long lTumorClassification = beanTumorClass.getValue();
        Long lOrgan = beanOrgan.getValue();
        long lTumorTypeKey = -1;

        // validate that an Tumor Type key has been entered or will be auto
        // assigned
        if ((!bAutoAssign) && (!StringUtils.hasValue(strTumorTypeKey))) {
            Utils.showErrorDialog("Please either enter a Tumor Type Key " +
                    "value or select Auto Assign.");
            txtTumorTypeKey.requestFocus();
            return;
        }

        // validate that a Tumor Classification has been selected
        if (comboTumorClassification.getSelectedIndex() <= 0) {
            Utils.showErrorDialog("Please select a Tumor Classification.");
            comboTumorClassification.requestFocus();
            return;
        }

        // validate that an Organ has been selected
        if (comboOrgan.getSelectedIndex() <= 0) {
            Utils.showErrorDialog("Please select an Organ.");
            comboOrgan.requestFocus();
            return;
        }

        // get the table model
        TumorTypeDTOTableModel<TumorTypeDTO> tblmdlTumorType =
                (TumorTypeDTOTableModel<TumorTypeDTO>)fxtblTumorType.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        TumorTypeDTO dtoTumorType =
                TumorTypeDAO.getInstance().createTumorTypeDTO();

        if (!bAutoAssign) {
            try {
                lTumorTypeKey = Long.parseLong(strTumorTypeKey);
            } catch (Exception e) {
                Utils.log(e);
                return;
            }
            dtoTumorType.setTumorTypeKey(new Long(lTumorTypeKey));
        }

        dtoTumorType.setTumorClassificationKey(lTumorClassification);
        dtoTumorType.setOrganKey(lOrgan);
        dtoTumorType.setCommonName(strCommonName);
        dtoTumorType.setCreateUser(dtoUser.getUserName());
        dtoTumorType.setCreateDate(dNow);
        dtoTumorType.setUpdateUser(dtoUser.getUserName());
        dtoTumorType.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoTumorType.getDataBean().put(EIConstants.ORGAN, beanOrgan);
        dtoTumorType.getDataBean().put(EIConstants.TUMOR_CLASSIFICATION,
                                        beanTumorClass);

        // add it to the table
        tblmdlTumorType.addRow(dtoTumorType);

        Utils.scrollToVisible(fxtblTumorType,
                fxtblTumorType.getRowCount() - 1, 0);
    }

    /**
     * Update the database with the changes made to the datamodel.  Proper
     * handling of transaction occurs (commit, rollback).
     */
    private void updateData() {
        boolean bCommit = false;
        TumorTypeDAO daoTumorType = TumorTypeDAO.getInstance();

        try {
            updateProgress("Parsing Tumor Type data...");

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
            TumorTypeDTOTableModel<TumorTypeDTO> tblmdlTumorType =
                    (TumorTypeDTOTableModel<TumorTypeDTO>)fxtblTumorType.getModel();

            updateProgress("Saving Tumor Type data...");

            for (int i = 0; i < tblmdlTumorType.getRowCount(); i++) {
                TumorTypeDTO dto = (TumorTypeDTO)tblmdlTumorType.getDTO(i);

                if (dto.isModified()) {
                    dto.setUpdateUser(dtoUser.getUserName());
                    dto.setUpdateDate(dNow);
                }

                daoTumorType.save(dto);
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("Tumor Type data saved!");
            bCommit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog("Unable to save changes to Tumor Types.\n\n" + e.getMessage(), e);
            return;
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(bCommit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to Tumor Types.\n\n" + e2.getMessage(), e2);
                return;
            }
        }

        if (bCommit) {
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
        lblTumorTypeKey = new javax.swing.JLabel();
        txtTumorTypeKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        lblTumorClassification = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        headerPanelCreate = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        comboTumorClassification = new javax.swing.JComboBox();
        lblOrgan = new javax.swing.JLabel();
        lblCommonName = new javax.swing.JLabel();
        comboOrgan = new javax.swing.JComboBox();
        txtCommonName = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlEdit = new javax.swing.JPanel();
        jspData = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        headerPanelEdit = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCreate.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblTumorTypeKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblTumorTypeKey.setText("Tumor Type Key");

        txtTumorTypeKey.setColumns(10);
        txtTumorTypeKey.setEditable(false);

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
        headerPanelCreate.setText("Create Tumor Type");

        lblOrgan.setText("Organ");

        lblCommonName.setText("Common Name");

        org.jdesktop.layout.GroupLayout pnlCreateLayout = new org.jdesktop.layout.GroupLayout(pnlCreate);
        pnlCreate.setLayout(pnlCreateLayout);
        pnlCreateLayout.setHorizontalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
            .add(pnlCreateLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblCommonName)
                    .add(lblOrgan)
                    .add(lblTumorTypeKey)
                    .add(lblTumorClassification))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCreateLayout.createSequentialGroup()
                        .add(txtTumorTypeKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(checkboxAutoAssign)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAdd))
                    .add(comboTumorClassification, 0, 440, Short.MAX_VALUE)
                    .add(comboOrgan, 0, 440, Short.MAX_VALUE)
                    .add(txtCommonName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlCreateLayout.setVerticalGroup(
            pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCreateLayout.createSequentialGroup()
                .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTumorTypeKey)
                    .add(txtTumorTypeKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(btnAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTumorClassification)
                    .add(comboTumorClassification, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOrgan)
                    .add(comboOrgan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCommonName)
                    .add(txtCommonName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jspData.setViewportView(tblData);

        headerPanelEdit.setDrawSeparatorUnderneath(true);
        headerPanelEdit.setText("Edit Tumor Type");

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
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
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
            txtTumorTypeKey.setEditable(false);
            txtTumorTypeKey.setText("");
        } else {
            txtTumorTypeKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addTumorType();
    }//GEN-LAST:event_btnAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private javax.swing.JComboBox comboOrgan;
    private javax.swing.JComboBox comboTumorClassification;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCreate;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelEdit;
    private javax.swing.JScrollPane jspData;
    private javax.swing.JLabel lblCommonName;
    private javax.swing.JLabel lblOrgan;
    private javax.swing.JLabel lblTumorClassification;
    private javax.swing.JLabel lblTumorTypeKey;
    private javax.swing.JPanel pnlCreate;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtCommonName;
    private javax.swing.JTextField txtTumorTypeKey;
    // End of variables declaration//GEN-END:variables

}
