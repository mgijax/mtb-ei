/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AllelePanel.java,v 1.1 2007/04/30 15:50:53 mjv Exp
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
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.jax.mgi.mtb.dao.custom.SearchResults;
import org.jax.mgi.mtb.dao.custom.mtb.MTBGeneticsUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBSynchronizationUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleMarkerAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleMarkerAssocDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.models.AlleleMarkerAssocDTOTableModel;
import org.jax.mgi.mtb.ei.models.DTOTableModel;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.MXTable;

/**
 * For inserting or updating <b>Allele</b> information and the associated
 * data in the database.
 *
 *
 *
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AllelePanel.java,v 1.1 2007/04/30 15:50:53 mjv Exp
 * @date 2007/04/30 15:50:53
 */
public class AllelePanel extends CustomPanel {

    // -------------------------------------------------------------- Constants

    /**
     * Used in the constructor to specify this is a read only panel.
     */
    public static int ALLELE_PANEL_READONLY = 0;

    /**
     * Used in the constructor to specify this is a new allele.
     */
    public static int ALLELE_PANEL_ADD = 1;

    /**
     * Used in the constructor to specify this is an old allele.
     */
    public static int ALLELE_PANEL_EDIT = 2;


    // ----------------------------------------------------- Instance Variables

    private final static Logger log =
            Logger.getLogger(AllelePanel.class.getName());

    // the allele dto
    private AlleleDTO dtoAllele = null;

    // the type of panel
    private int nType = ALLELE_PANEL_READONLY;

    // custom JTables for sorting purposes
    private MXTable fxtblMarkers = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;


    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new AllelePanel (READ ONLY).
     */
    public AllelePanel() {
        this(ALLELE_PANEL_READONLY);
    }

    /**
     * Creates a new AllelePanel.
     * <p>
     * If <code>nType = ALLELE_PANEL_ADD/code> an insert to the database of
     * the allele object is necessary.  Otherwise, the strain object already
     * exists in the database.
     *
     * @param nType the type of panel, which is either
     *        <code>ALLELE_PANEL_READONLY</code, <code>ALLELE_PANEL_ADD</code>
     *        or <code>ALLELE_PANEL_EDIT</code>
     */
    public AllelePanel(int nType) {
        this.nType = nType;
        initComponents();
        initCustom();
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Set the allele key for the panel.  This should only be called when the
     * type is of <code>ALLELE_PANEL_EDIT</code>, otherwise unknown behavior
     * will occur.
     * <p>
     * <code>Allele</code> data and associated data is retrieved from the
     * database during this method.
     *
     * @param lKey the allele key to be looked up in the database
     */
    public void setKey(final long lKey) {
        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                progressMonitor.start("Loading Allele: " + lKey);
                try{
                    lookupData(lKey);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    // to ensure that progress dlg is closed in case of
                    // any exception
                    progressMonitor.setCurrent("Done!", progressMonitor.getTotal());
                }
            }
        };

        new Thread(runnable).start();
    }

    /**
     * Check to see if any data has been updated since the form has been loaded
     * and/or saved.  If any data has been modified, return <code>true</code.
     * Otherwise, return <code>false</code>.
     *
     * @return <code>true</code>if the form has been updated,
     *         <code>false</code> otherwise
     */
    public boolean isUpdated() {
        if (!super.isUpdated()) {
            // allele name
            if (!StringUtils.equals(StringUtils.nvl(dtoAllele.getName(), ""), txtName.getText())) {
                System.out.println("allele name changed");
                return true;
            }

            // allele symbol
            if (!StringUtils.equals(StringUtils.nvl(dtoAllele.getSymbol(), ""), txtSymbol.getText())) {
                System.out.println("allele symbol changed");
                return true;
            }

            // allele note
            if (!StringUtils.equals(StringUtils.nvl(dtoAllele.getNote(), ""), txtareaNote.getText())) {
                System.out.println("allele note changed");
                return true;
            }

            // allele type
            LabelValueBean<String,Long> beanType = (LabelValueBean<String,Long>)comboAlleleType.getSelectedItem();
            if (dtoAllele.getAlleleTypeKey() != null && dtoAllele.getAlleleTypeKey().longValue() != (new Long(beanType.getValue())).longValue()) {
                System.out.println("allele type key changed");
                return true;
            }

            // allele marker associations
            if (((DTOTableModel)fxtblMarkers.getModel()).hasBeenUpdated()) {
                System.out.println("markers changed");
                return true;
            }

            return false;
        }

        return true;
    }

    /**
     * Set the allele DTO.
     */
    public void setAlleleDTO(AlleleDTO dto) {
        this.dtoAllele = dto;
        if (dto != null) {
            txtAlleleKey.setText(dtoAllele.getAlleleKey()+"");
            txtName.setText(dtoAllele.getName());
            txtSymbol.setText(dtoAllele.getSymbol());
            txtareaNote.setText(dtoAllele.getNote());

            // set the allele type
            for (int i = 0; i < comboAlleleType.getItemCount(); i++) {
                LabelValueBean<String,Long> bean = (LabelValueBean<String,Long>)comboAlleleType.getItemAt(i);

                if (dtoAllele.getAlleleTypeKey().longValue() == bean.getValue().longValue()) {
                    comboAlleleType.setSelectedIndex(i);
                }
            }

            // set the accession info
            AccessionDAO daoAccession = AccessionDAO.getInstance();
            AccessionDTO dtoAcc = null;

            try {
                dtoAcc = daoAccession.createAccessionDTO();
                dtoAcc.setMTBTypesKey(EIConstants.MTB_TYPE_ALLELE);
                dtoAcc.setObjectKey(dtoAllele.getAlleleKey());
                dtoAcc.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
                dtoAcc = daoAccession.loadUniqueUsingTemplate(dtoAcc);
                if (dtoAcc == null) {
                    txtMGIID.setText("");
                } else {
                    txtMGIID.setText(dtoAcc.getAccID());
                }
            } catch (Exception e) {
                Utils.log(e);
            }

            
            setMarkerDTOs();
        }
    }

    /**
     * Set the marker DTOs
     */
    public void setMarkerDTOs() {
        AlleleMarkerAssocDAO daoAMA = AlleleMarkerAssocDAO.getInstance();
        MarkerDAO daoMarker = MarkerDAO.getInstance();
        Map<Long,LabelValueBean<String,Long>> mapAssocTypes = EIGlobals.getInstance().getAlleleMarkerAssocTypes();
        List<AlleleMarkerAssocDTO> arrMarkers = new ArrayList<AlleleMarkerAssocDTO>();
        List<AlleleMarkerAssocDTO> arrMarkersNew = new ArrayList<AlleleMarkerAssocDTO>();

        try {
            // get association information
            arrMarkers = daoAMA.loadByAlleleKey(dtoAllele.getAlleleKey());

            // set the allele marker assoc
            for (int i = 0; i < arrMarkers.size(); i++) {
            //for (AlleleMarkerAssocDTO dto : arrMarkers) {
                AlleleMarkerAssocDTO dto = arrMarkers.get(i);
                MarkerDTO dtoMarker = daoMarker.loadByPrimaryKey(dto.getMarkerKey());
                dto.getDataBean().put(EIConstants.MARKER_DTO, dtoMarker);
                LabelValueBean<String,Long> beanType = mapAssocTypes.get(dto.getAlleleMarkerAssocTypeKey());
                dto.getDataBean().put(EIConstants.ALLELE_MARKER_ASSOC_TYPE_BEAN, beanType);
                arrMarkersNew.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }

        AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO> tblmdlAMA =
                (AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO>)fxtblMarkers.getModel();

        tblmdlAMA.setData(arrMarkersNew);
    }


    /**
     * Save the strain information.
     * <p>
     * Depending upon the type, the strain information will either be updated
     * or inserted.  This is performed in a seperate thread since this could
     * potentially be a lengthy operation. A <code>MXProgressMonitor</code> is
     * used to display visual feedback to the user.
     */
    public void save() {
      
        //don't allow a save if there is no marker assoc
        AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO> modelAssoc = (AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO>)fxtblMarkers.getModel();
        List<AlleleMarkerAssocDTO> arrMarkers = modelAssoc.getAllData();
        boolean hasMarkers = false;
        for(AlleleMarkerAssocDTO ama : arrMarkers){
          if(!ama.isOld()){
            hasMarkers = true;
          }
        }
        if(!hasMarkers){
           Utils.showErrorDialog("Alleles must be associated with a marker.");      
           return;
        }
                
        // The following code saves the current value in the cell being edited
        // and stops the editing process:
        if (fxtblMarkers.getCellEditor() != null) {
            fxtblMarkers.getCellEditor().stopCellEditing();
        }

        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try{
                    if (nType == ALLELE_PANEL_ADD) {
                        progressMonitor.start("Inserting Allele...");
                        insertData();
                    } else if (nType == ALLELE_PANEL_EDIT) {
                        progressMonitor.start("Updating Allele...");
                        updateData();
                    }
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

    /**
     * Set the title header text.
     *
     * @param strTitle the title text
     */
    public void setTitle(String strTitle) {
        this.headerPanelAllele.setText(strTitle);
    }

    /**
     * Get the title header text.
     */
    public String getTitle() {
        return this.headerPanelAllele.getText();
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
     * Lookup all allele related information in the database.
     *
     * @param lKey the strain key to be looked up in the database
     */
    private void lookupData(long lKey) {
        AlleleDAO daoAllele = AlleleDAO.getInstance();
        AlleleDTO dtoAllele = null;

        try {
            dtoAllele = daoAllele.loadByPrimaryKey(new Long(lKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setAlleleDTO(dtoAllele);
    }

    /**
     * Initialize the MXTable for associated markers.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initAlleleMarkerAssoc() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(4);
        arrHeaders.add("Key");
        arrHeaders.add("Symbol");
        arrHeaders.add("Name");
        arrHeaders.add("Association");
        List arrMarkers = new ArrayList();
        AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO> tblmdlMarkerAssoc =
                new AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO>(arrMarkers, arrHeaders);
        fxtblMarkers = new MXTable(tblmdlMarkerAssoc);
        fxtblMarkers.setModel(tblmdlMarkerAssoc);

        // set the table options
        fxtblMarkers.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblMarkers.setColumnSizes(new int[]{75, 0, 0, 0});
        fxtblMarkers.setAlternateRowHighlight(true);
        fxtblMarkers.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblMarkers.setAlternateRowHighlightCount(2);
        fxtblMarkers.setStartHighlightRow(1);
        fxtblMarkers.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblMarkers.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblMarkers.enableToolTip(0, false);

        // create the synonym delete button
        JButton btnDelete =
                new JButton(new ImageIcon(
                        getClass().getResource(EIConstants.ICO_DELETE_16)));
        btnDelete.setIconTextGap(0);
        btnDelete.setMargin(new Insets(0, 0, 0, 0));
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeMarker();
            }
        });

        // update the JScrollPane
        jspMarkers.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspMarkers.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelete);
        jspMarkers.setViewportView(fxtblMarkers);

        // revalidate the panel
        pnlMarkers.revalidate();
    }

    /**
     * Perform any custom initialization needed.
     */
    private void initCustom() {
        // make it so the following fields accept numeric input only
        Utils.setNumericFilter(txtAlleleKey);
        Utils.setNumericFilter(txtMarkerKey);

        // create the allele dto
        dtoAllele = AlleleDAO.getInstance().createAlleleDTO();

        ///////////////////////////////////////////////////////////////////////
        // allele type
        ///////////////////////////////////////////////////////////////////////
        final Map<Long,LabelValueBean<String,Long>> alleleTypes = EIGlobals.getInstance().getAlleleTypes();
        List<LabelValueBean<String,Long>> arrAlleleTypes = new ArrayList<LabelValueBean<String,Long>>(alleleTypes.values());
        arrAlleleTypes.add(0, new LabelValueBean<String,Long>("--Select--", -1L));
        comboAlleleType.setModel(new LVBeanListModel<String,Long>(arrAlleleTypes));
        comboAlleleType.setRenderer(new LVBeanListCellRenderer<String,Long>());
        comboAlleleType.addKeyListener(new LVBeanComboListener<String,Long>());
        comboAlleleType.setSelectedIndex(0);

        ///////////////////////////////////////////////////////////////////////
        // allele/marker association
        ///////////////////////////////////////////////////////////////////////
        final Map<Long,LabelValueBean<String,Long>> assocTypes = EIGlobals.getInstance().getAlleleMarkerAssocTypes();
        List<LabelValueBean<String,Long>> arrAssocTypes = new ArrayList<LabelValueBean<String,Long>>(assocTypes.values());
        arrAssocTypes.add(0, new LabelValueBean<String,Long>("--Select--", -1L));
        comboMarkerAssociationType.setModel(new LVBeanListModel<String,Long>(arrAssocTypes));
        comboMarkerAssociationType.setRenderer(new LVBeanListCellRenderer<String,Long>());
        comboMarkerAssociationType.addKeyListener(new LVBeanComboListener<String,Long>());
        comboMarkerAssociationType.setSelectedIndex(0);

        initAlleleMarkerAssoc();

        // adjust components as needed

        // configure properly
        if (this.nType == ALLELE_PANEL_READONLY) {
            // essentially remove the following items
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            checkboxAutoAssign.setVisible(false);
            btnLookup.setVisible(false);
            lblMGIAlleleType.setVisible(false);
            btnAddMarker.setVisible(false);
            comboMarkerAssociationType.setVisible(false);
            lblMarkerKey.setVisible(false);
            lblAssociationType.setVisible(false);
            txtMarkerKey.setVisible(false);

            // disbale editing of the following items
            txtAlleleKey.setEditable(false);
            txtMGIID.setEditable(false);
            txtSymbol.setEditable(false);
            txtName.setEditable(false);
            txtareaNote.setEditable(false);
            txtareaNote.setBackground(
                    UIManager.getDefaults().getColor("TextField.inactiveBackground"));
            txtareaNote.setForeground(
                    UIManager.getDefaults().getColor("TextField.inactiveForeground"));
            comboAlleleType.setBackground(
                    UIManager.getDefaults().getColor("TextField.inactiveBackground"));
            comboAlleleType.setForeground(
                    UIManager.getDefaults().getColor("TextField.inactiveForeground"));
            fxtblMarkers.setBackground(
                    UIManager.getDefaults().getColor("TextField.inactiveBackground"));
            fxtblMarkers.setForeground(
                    UIManager.getDefaults().getColor("TextField.inactiveForeground"));
        } else if (this.nType == ALLELE_PANEL_EDIT) {
            txtAlleleKey.setEditable(false);
            checkboxAutoAssign.setEnabled(false);
        } else {
            txtAlleleKey.setEditable(true);
            checkboxAutoAssign.setSelected(true);
        }
    }

    /**
     * Load data from MGI into this panel.
     */
    private void lookupMGI() {
        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try{
                    progressMonitor.start("Loading Allele from MGI");
                    loadAlleleFromMGI();
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
    }

    private void loadAlleleFromMGI() {
        String mgiid = txtMGIID.getText().trim();
        AlleleDTO dtoAllele = null;

        if (!StringUtils.hasValue(mgiid)) {
            Utils.showErrorDialog("Please enter a MGI ID to retrieve.");
            return;
        }

        try {
          
         MTBGeneticsUtilDAO daoGeneticsUtil =
                        MTBGeneticsUtilDAO.getInstance();

                SearchResults res = null;

                try {
                    // search existing for allele
                    
                    res = daoGeneticsUtil.searchAllele(mgiid, 0,null, null, null,"name", -1);
                    if((res.getList()!= null) && (res.getList().size() > 0)){
                      dtoAllele = (AlleleDTO)res.getList().get(0);
                      
                      Utils.showErrorDialog("Allele "+mgiid+ " "+dtoAllele.getSymbol()+"\nis in MTB with key "+dtoAllele.getAlleleKey());
                      dtoAllele = null;
                      return;
                    }
                } catch (Exception e) {
                }
          
            MTBSynchronizationUtilDAO dao = MTBSynchronizationUtilDAO.getInstance();

            dao.setMGIInfo(EIGlobals.getInstance().getMGIUser(),
                           EIGlobals.getInstance().getMGIPassword(),
                           EIGlobals.getInstance().getMGIDriver(),
                           EIGlobals.getInstance().getMGIUrl());

            dtoAllele = dao.getAlleleFromMGI(mgiid);

            if (dtoAllele == null) {
                Utils.showErrorDialog("Unable to retrieve: " + mgiid);
                return;
            }
            
            txtName.setText(dtoAllele.getName());
            txtSymbol.setText(MTBSynchronizationUtilDAO.fixSymbol(dtoAllele.getSymbol()));
            lblMGIAlleleType.setText((String)dtoAllele.getDataBean().get(MTBSynchronizationUtilDAO.MGI_ALLELE_TYPE));
            txtareaNote.setText(dtoAllele.getNote());
        } catch (Exception e) {
            log.error("Unable to load allele from MGI.", e);
        }
    }


    /**
     * Add a Marker to the association table.
     */
    private void addMarker() {
        String strMarkerKey = txtMarkerKey.getText().trim();

        long lMarkerKey = -1;

        // validate that a marker key has been entered
        try {
            lMarkerKey = Long.parseLong(strMarkerKey);
        } catch (Exception e) {
            Utils.showErrorDialog("Please enter a value for the Marker Key.");
            txtMarkerKey.requestFocus();
            return;
        }

        // validate that an association type has been selected
        if (comboMarkerAssociationType.getSelectedIndex() <= 0) {
            Utils.showErrorDialog("Please select an association.");
            comboMarkerAssociationType.requestFocus();
            return;
        }

        LabelValueBean<String,Long> beanAssociationType =
                (LabelValueBean<String,Long>)comboMarkerAssociationType.getSelectedItem();

        // validate that the marker key exists
        MarkerDAO daoMarker = MarkerDAO.getInstance();
        MarkerDTO dtoMarker = null;

        try {
            dtoMarker = daoMarker.loadByPrimaryKey(new Long(lMarkerKey));
        } catch (Exception e) {
            Utils.log(e);
        }

        if (dtoMarker == null) {
            Utils.showErrorDialog("Please enter a valid Marker Key.");
            txtMarkerKey.requestFocus();
            return;
        }

        // get the table model
        AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO> tblmdlAMA =
                (AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO>)fxtblMarkers.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        AlleleMarkerAssocDTO dtoAMA =
                AlleleMarkerAssocDAO.getInstance().createAlleleMarkerAssocDTO();

        dtoAMA.setAlleleKey(dtoAllele.getAlleleKey());
        dtoAMA.setMarkerKey(dtoMarker.getMarkerKey());
        dtoAMA.setAlleleMarkerAssocTypeKey(
                new Long(beanAssociationType.getValue()));
        dtoAMA.setCreateUser(dtoUser.getUserName());
        dtoAMA.setCreateDate(dNow);
        dtoAMA.setUpdateUser(dtoUser.getUserName());
        dtoAMA.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoAMA.getDataBean().put(EIConstants.MARKER_DTO, dtoMarker);
        dtoAMA.getDataBean().put(EIConstants.ALLELE_MARKER_ASSOC_TYPE_BEAN,
                                  beanAssociationType);

        // add it to the table
        tblmdlAMA.addRow(dtoAMA);

        Utils.scrollToVisible(fxtblMarkers,
                                 fxtblMarkers.getRowCount() - 1, 0);
    }

    /**
     * Insert the allele information and associated data in the database.
     * <p>
     * This is an all or nothing insert.  Either everything the user has
     * filled in gets comitted to the database or nothing does.
     */
    private void insertData() {
        AlleleDAO daoAllele = AlleleDAO.getInstance();
        AlleleMarkerAssocDAO daoAMA = AlleleMarkerAssocDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the allele
            ///////////////////////////////////////////////////////////////////
            updateProgress("Parsing allele data...");

            boolean bAutoAssign = checkboxAutoAssign.isSelected();
            String strAlleleKeyText = txtAlleleKey.getText();
            long lAlleleKey = -1;

            dtoAllele = daoAllele.createAlleleDTO();

            if (!bAutoAssign) {
                lAlleleKey = Long.parseLong(strAlleleKeyText);
                dtoAllele.setAlleleKey(lAlleleKey);
            }

            dtoAllele.setName(txtName.getText());
            dtoAllele.setNote(txtareaNote.getText());
            dtoAllele.setSymbol(txtSymbol.getText());

            LabelValueBean<String,Long> beanType = (LabelValueBean<String,Long>)comboAlleleType.getSelectedItem();
            dtoAllele.setAlleleTypeKey(new Long(beanType.getValue()));

            // TODO: determine how this should be implemented
            dtoAllele.setSomaticInd(3);

            // add the audit trail
            dtoAllele.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoAllele.setCreateDate(new Date());
            dtoAllele.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoAllele.setUpdateDate(new Date());

            updateProgress("Saving allele data...");
            dtoAllele = daoAllele.save(dtoAllele);
            updateProgress("Allele data saved!");

            ///////////////////////////////////////////////////////////////////
            // save allele marker association
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving allele marker association data...");

            AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO> modelAssoc = (AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO>)fxtblMarkers.getModel();
            List<AlleleMarkerAssocDTO> arrMarkers = modelAssoc.getAllData();
            if (arrMarkers != null) {
                for (AlleleMarkerAssocDTO dto : arrMarkers) {
                    dto.setAlleleKey(dtoAllele.getAlleleKey());
                }
                daoAMA.save(arrMarkers);
            }

            updateProgress("Allele marker association data saved!");

            ///////////////////////////////////////////////////////////////////
            // save accession information
            ///////////////////////////////////////////////////////////////////
            String strTemp = txtMGIID.getText().trim();

            if (StringUtils.hasValue(strTemp)) {
                updateProgress("Saving allele accession data...");

                AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
                long numericPart = Utils.parseMGIID(strTemp);

                dtoAccession.setAccID(strTemp);
                dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_ALLELE);
                dtoAccession.setObjectKey(dtoAllele.getAlleleKey());
                dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
                dtoAccession.setPrefixPart("MGI:");
                dtoAccession.setNumericPart(numericPart);
                dtoAccession.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                dtoAccession.setCreateDate(new Date());
                dtoAccession.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                dtoAccession.setUpdateDate(new Date());

                dtoAccession = daoAccession.save(dtoAccession);

                updateProgress("Allele accession data saved!");
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("All allele data saved!");
            bCommit = true;
        } catch (Exception e) {
            log.error("Unable to insert allele.", e);
            Utils.showErrorDialog("Unable to insert allele.", e);
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(bCommit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to add Allele.", e2);
            }
            if (bCommit) {
                switchFromAddToEdit();
            } else {
                Utils.showErrorDialog("Unable to add Allele.");
            }
        }
    }

    /**
     * Update the allele information and associated data in the database.
     * <p>
     * This is an all or nothing update.  Either everything the user has
     * updated gets comitted to the database or nothing does.
     */
    private void updateData() {
        AlleleDAO daoAllele = AlleleDAO.getInstance();
        AlleleMarkerAssocDAO daoAMA = AlleleMarkerAssocDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the allele marker associations
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving allele marker association data...");

            AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO> modelAssoc = (AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO>)fxtblMarkers.getModel();
            List<AlleleMarkerAssocDTO> arrMarkers = modelAssoc.getAllData();
            daoAMA.save(arrMarkers);
            updateProgress("Allele marker association data saved!");

            ///////////////////////////////////////////////////////////////////
            // save the allele
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving allele data...");

            dtoAllele.setName(txtName.getText());
            dtoAllele.setNote(txtareaNote.getText());
            dtoAllele.setSymbol(txtSymbol.getText());

            LabelValueBean<String,Long> beanType = (LabelValueBean<String,Long>)comboAlleleType.getSelectedItem();
            dtoAllele.setAlleleTypeKey(new Long(beanType.getValue()));

            // TODO: determine how this should be implemented
            dtoAllele.setSomaticInd(3);

            // audit trail information
            MTBUsersDTO user = EIGlobals.getInstance().getMTBUsersDTO();
            dtoAllele.setUpdateUser(user.getUserName());
            dtoAllele.setUpdateDate(new Date());

            daoAllele.save(dtoAllele);

            updateProgress("Allele data saved!");

            ///////////////////////////////////////////////////////////////////
            // save accession information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving allele accession data...");

            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();

            // delete the accession info for this allele
            dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_ALLELE);
            dtoAccession.setObjectKey(dtoAllele.getAlleleKey());
            dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
            dtoAccession.setPrefixPart("MGI:");

            daoAccession.deleteUsingTemplate(dtoAccession);

            String strTemp = txtMGIID.getText().trim();

            if (StringUtils.hasValue(strTemp)) {
                // create the new accesion info
                dtoAccession = daoAccession.createAccessionDTO();

                long numericPart = Utils.parseMGIID(strTemp);

                dtoAccession.setAccID(strTemp);
                dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_ALLELE);
                dtoAccession.setObjectKey(dtoAllele.getAlleleKey());
                dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
                dtoAccession.setPrefixPart("MGI:");
                dtoAccession.setNumericPart(numericPart);
                dtoAccession.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                dtoAccession.setCreateDate(new Date());
                dtoAccession.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                dtoAccession.setUpdateDate(new Date());

                dtoAccession = daoAccession.save(dtoAccession);
            }

            updateProgress("Allele accession data saved!");

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("All allele data saved!");
            bCommit = true;
        } catch (Exception e) {
            log.error("Unable to update allele.", e);
            Utils.showErrorDialog(e.getMessage(), e);
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(bCommit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to Allele.", e2);
            }
            if (bCommit) {
                this.setKey(dtoAllele.getAlleleKey().longValue());
            } else {
                Utils.showErrorDialog("Unable to save changes to Allele.");
            }
        }
    }


    /**
     * Mark a marker from the allele marker association table as to be deleted.
     * <p>
     * The actual association will not be removed until the allele has been
     * saved.
     */
    private void removeMarker() {
        int nRow = fxtblMarkers.getSelectedRow();

        if (nRow >= 0) {
            AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO> modelAssoc =
                    (AlleleMarkerAssocDTOTableModel<AlleleMarkerAssocDTO>)fxtblMarkers.getModel();
            modelAssoc.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Simple method to close the add form and switch to the edit form.  The
     * window location is tracked to make it seemless to the end user.
     */
    private void switchFromAddToEdit() {
        customInternalFrame.dispose();
        EIGlobals.getInstance().getMainFrame().launchAlleleEditWindow(
                dtoAllele.getAlleleKey().longValue(),
                customInternalFrame.getLocation());
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
        pnlStrainInformation = new javax.swing.JPanel();
        lblAlleleKey = new javax.swing.JLabel();
        lblAlleleType = new javax.swing.JLabel();
        txtAlleleKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        lblMGIAlleleType = new javax.swing.JLabel();
        lblSymbol = new javax.swing.JLabel();
        jspDescription = new javax.swing.JScrollPane();
        txtareaNote = new javax.swing.JTextArea();
        headerPanelAllele = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblMGIID = new javax.swing.JLabel();
        txtMGIID = new javax.swing.JTextField();
        btnLookup = new javax.swing.JButton();
        comboAlleleType = new javax.swing.JComboBox();
        txtSymbol = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblNote = new javax.swing.JLabel();
        pnlMarkers = new javax.swing.JPanel();
        lblMarkerKey = new javax.swing.JLabel();
        txtMarkerKey = new javax.swing.JTextField();
        lblAssociationType = new javax.swing.JLabel();
        btnAddMarker = new javax.swing.JButton();
        jspMarkers = new javax.swing.JScrollPane();
        tblMarkers = new javax.swing.JTable();
        headerPanelMarkers = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        comboMarkerAssociationType = new javax.swing.JComboBox();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        pnlStrainInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblAlleleKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblAlleleKey.setText("Allele Key");

        lblAlleleType.setText("Allele Type");

        txtAlleleKey.setColumns(10);
        txtAlleleKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        lblMGIAlleleType.setText(" ");

        lblSymbol.setText("Symbol");

        txtareaNote.setColumns(20);
        txtareaNote.setLineWrap(true);
        txtareaNote.setRows(3);
        txtareaNote.setWrapStyleWord(true);
        jspDescription.setViewportView(txtareaNote);

        headerPanelAllele.setDrawSeparatorUnderneath(true);
        headerPanelAllele.setText("Allele Information");

        lblMGIID.setText("MGI ID");

        txtMGIID.setColumns(10);
        txtMGIID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMGIIDFocusLost(evt);
            }
        });

        btnLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png")));
        btnLookup.setText("Lookup");
        btnLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupActionPerformed(evt);
            }
        });

        lblName.setText("Name");

        lblNote.setText("Note");

        org.jdesktop.layout.GroupLayout pnlStrainInformationLayout = new org.jdesktop.layout.GroupLayout(pnlStrainInformation);
        pnlStrainInformation.setLayout(pnlStrainInformationLayout);
        pnlStrainInformationLayout.setHorizontalGroup(
            pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelAllele, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .add(pnlStrainInformationLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblNote)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblName)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblSymbol)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblAlleleType)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblAlleleKey))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(comboAlleleType, 0, 508, Short.MAX_VALUE)
                    .add(txtSymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                    .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                    .add(jspDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlStrainInformationLayout.createSequentialGroup()
                        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblMGIAlleleType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .add(pnlStrainInformationLayout.createSequentialGroup()
                                .add(txtAlleleKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(4, 4, 4)
                                .add(checkboxAutoAssign)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 117, Short.MAX_VALUE)
                                .add(lblMGIID)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtMGIID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnLookup)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .addContainerGap())
        );
        pnlStrainInformationLayout.setVerticalGroup(
            pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStrainInformationLayout.createSequentialGroup()
                .add(headerPanelAllele, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAlleleKey)
                    .add(txtAlleleKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(lblMGIID)
                    .add(txtMGIID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnLookup))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAlleleType)
                    .add(comboAlleleType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblMGIAlleleType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSymbol)
                    .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName)
                    .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblNote)
                    .add(jspDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlMarkers.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblMarkerKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblMarkerKey.setText("Marker Key");

        txtMarkerKey.setColumns(10);

        lblAssociationType.setText("Association Type");

        btnAddMarker.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png")));
        btnAddMarker.setText("Add");
        btnAddMarker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMarkerActionPerformed(evt);
            }
        });

        tblMarkers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspMarkers.setViewportView(tblMarkers);

        headerPanelMarkers.setDrawSeparatorUnderneath(true);
        headerPanelMarkers.setText("Associated Markers");

        org.jdesktop.layout.GroupLayout pnlMarkersLayout = new org.jdesktop.layout.GroupLayout(pnlMarkers);
        pnlMarkers.setLayout(pnlMarkersLayout);
        pnlMarkersLayout.setHorizontalGroup(
            pnlMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelMarkers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlMarkersLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jspMarkers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .add(pnlMarkersLayout.createSequentialGroup()
                        .add(pnlMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblMarkerKey))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblAssociationType)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlMarkersLayout.createSequentialGroup()
                                .add(comboMarkerAssociationType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 385, Short.MAX_VALUE)
                                .add(btnAddMarker)))))
                .addContainerGap())
        );
        pnlMarkersLayout.setVerticalGroup(
            pnlMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlMarkersLayout.createSequentialGroup()
                .add(headerPanelMarkers, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMarkerKey)
                    .add(lblAssociationType))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnAddMarker)
                    .add(comboMarkerAssociationType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspMarkers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png")));
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Save16.png")));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlStrainInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(btnSave)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel))
                    .add(pnlMarkers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnCancel, btnSave}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlStrainInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlMarkers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtMGIIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMGIIDFocusLost
        Utils.fixMGIID(txtMGIID);
    }//GEN-LAST:event_txtMGIIDFocusLost

    private void btnLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupActionPerformed
        lookupMGI();
    }//GEN-LAST:event_btnLookupActionPerformed

    private void checkboxAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignActionPerformed
        if (checkboxAutoAssign.isSelected()) {
            txtAlleleKey.setEditable(false);
            txtAlleleKey.setText("");
        } else {
            txtAlleleKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnAddMarkerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMarkerActionPerformed
        addMarker();
    }//GEN-LAST:event_btnAddMarkerActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMarker;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLookup;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private javax.swing.JComboBox comboAlleleType;
    private javax.swing.JComboBox comboMarkerAssociationType;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelAllele;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelMarkers;
    private javax.swing.JScrollPane jspDescription;
    private javax.swing.JScrollPane jspMarkers;
    private javax.swing.JLabel lblAlleleKey;
    private javax.swing.JLabel lblAlleleType;
    private javax.swing.JLabel lblAssociationType;
    private javax.swing.JLabel lblMGIAlleleType;
    private javax.swing.JLabel lblMGIID;
    private javax.swing.JLabel lblMarkerKey;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblSymbol;
    private javax.swing.JPanel pnlMarkers;
    private javax.swing.JPanel pnlStrainInformation;
    private javax.swing.JTable tblMarkers;
    private javax.swing.JTextField txtAlleleKey;
    private javax.swing.JTextField txtMGIID;
    private javax.swing.JTextField txtMarkerKey;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSymbol;
    private javax.swing.JTextArea txtareaNote;
    // End of variables declaration//GEN-END:variables

}
