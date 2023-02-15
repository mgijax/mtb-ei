  /**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/TumorFrequencySearchPanel.java,v 1.1 2007/04/30 15:50:59 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.jax.mgi.mtb.dao.custom.SearchResults;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainTumorDetailsDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.param.StrainSearchParams;
import org.jax.mgi.mtb.dao.custom.mtb.param.TumorFrequencySearchParams;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyNotesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencySynonymsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyTreatmentsDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorGeneticChangesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorGeneticsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorPathologyAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.gui.CustomInternalFrame;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.listeners.LVBeanListListener;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXProgressGlassPane;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;

/**
 * For searching <b>TumorFrequency</b> data.
 *
 * @author mjv
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/TumorFrequencySearchPanel.java,v 1.1 2007/04/30 15:50:59 mjv Exp
 * @date 2007/04/30 15:50:59
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 */
public class TumorFrequencySearchPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

   protected MXTable fxtblSearchResults = null;
   private MXProgressGlassPane progressGlassPane = null;
   private Vector gV = new Vector();


    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new TumorFrequencySearchPanel
     */
    public TumorFrequencySearchPanel() {
        initComponents();
        initCustom();
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Clear.
     */
    public void clear() {
        try {
            fxtblSearchResults.removeAll();
            fxtblSearchResults = null;
        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Select the appropriate organ of origin and tumor classification from
     * the <code>TumorTypeDTO</code>.
     *
     * @param dtoTumorType a classification of organ and tumor class
     */
    public void setTumorType(TumorTypeDTO dtoTumorType) {
        Map<Long,LabelValueBean<String,Long>> mapOrgans =
                EIGlobals.getInstance().getOrgansUnfiltered();
        Map<Long,LabelValueBean<String,Long>> mapTumorClass =
                EIGlobals.getInstance().getTumorClassifications();

        LabelValueBean<String,Long> beanOrgan =
                mapOrgans.get(dtoTumorType.getOrganKey());

        LabelValueBean<String,Long> beanTumorClass =
                mapTumorClass.get(dtoTumorType.getTumorClassificationKey());

        listOrganTissueOrigin.setSelectedValue(beanOrgan, true);
        listTumorClassification.setSelectedValue(beanTumorClass, true);
    }

    /**
     * Performs a search based upon the selected criteria.
     */
    public void performSearch() {
        search();
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Perform any custom initialization needed.
     */
    private void initCustom() {

        // make it so the following fields accept numeric input only
        Utils.setNumericFilter(txtTumorFrequencyKey);
        Utils.setNumericFilter(txtStrainKey);

        // organ/tissue of origin
        final Map<Long,LabelValueBean<String,Long>> mapOrgans = EIGlobals.getInstance().getOrgansUnfiltered();
        final List<LabelValueBean<String,Long>> arrOrgansOrigin = new ArrayList<LabelValueBean<String,Long>>(mapOrgans.values());
        listOrganTissueOrigin.addKeyListener(new LVBeanListListener<String,Long>());
        listOrganTissueOrigin.setModel(new LVBeanListModel<String,Long>(arrOrgansOrigin));
        listOrganTissueOrigin.setCellRenderer(new LVBeanListCellRenderer<String,Long>());

        // tumor classification
        final Map<Long,LabelValueBean<String,Long>> mapTumorClass =
                EIGlobals.getInstance().getTumorClassifications();
        final List<LabelValueBean<String,Long>> arrTumorClass = new ArrayList<LabelValueBean<String,Long>>(mapTumorClass.values());
        listTumorClassification.addKeyListener(new LVBeanListListener<String,Long>());
        listTumorClassification.setModel(new LVBeanListModel<String,Long>(arrTumorClass));
        listTumorClassification.setCellRenderer(new LVBeanListCellRenderer<String,Long>());

        // metastatic
        final List<LabelValueBean<String,Long>> arrOrgansAffected = new ArrayList<LabelValueBean<String,Long>>(mapOrgans.values());
        listOrganTissueAffected.addKeyListener(new LVBeanListListener<String,Long>());
        listOrganTissueAffected.setModel(new LVBeanListModel<String,Long>(arrOrgansAffected));
        listOrganTissueAffected.setCellRenderer(new LVBeanListCellRenderer<String,Long>());

        // strain types
        final Map<Long,LabelValueBean<String,Long>> mapStrainTypes = EIGlobals.getInstance().getStrainTypes();
        final List<LabelValueBean<String,Long>> arrStrainTypes = new ArrayList<LabelValueBean<String,Long>>(mapStrainTypes.values());
        listStrainTypes.addKeyListener(new LVBeanListListener<String,Long>());
        listStrainTypes.setModel(new LVBeanListModel<String,Long>(arrStrainTypes));
        listStrainTypes.setCellRenderer(new LVBeanListCellRenderer<String,Long>());

        // treatment types / agent
        final Map<Long,LabelValueBean<String,Long>> mapAgentTypes = EIGlobals.getInstance().getAgentTypes();
        List<LabelValueBean<String,Long>> arrAgentTypes = new ArrayList<LabelValueBean<String,Long>>(mapAgentTypes.values());
        arrAgentTypes.add(0, new LabelValueBean<String,Long>("-- Select --", -1L));
        comboTreatmentType.addKeyListener(new LVBeanComboListener<String,Long>());
        comboTreatmentType.setModel(new LVBeanListModel<String,Long>(arrAgentTypes));
        comboTreatmentType.setRenderer(new LVBeanListCellRenderer<String,Long>());
        comboTreatmentType.setSelectedIndex(0);

        configureSearchResultsTable();

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    
    // for java 11 Search threat runs as SecondaryLoop
    // blocks UI durring search.
    // what are the values of loop and res over time?
    // chances for NPEs or stale data?
    // also lost the incrementing counts as results are loaded (do we care?)
    SecondaryLoop loop;
    SearchResults res;
    
    class SearchThread extends Thread{
        public void run() {
            try{
            res = searchDatabase();
            }catch(Exception e){}
            loop.exit();
        }
        
    }

    /**
     * Search the database for the <code>Reference</code>s based upon the
     * search criteria.
     *
     * @return the <code>SearchResults</code>
     */
    
    
    
    private SearchResults searchDatabase() throws Exception {
        
                gV = new Vector();

                // determine parameters
                String strTemp = null;
                Object objTemp = null;

                ///////////////////////////////////////////////////////////
                // accession
                ///////////////////////////////////////////////////////////
                strTemp = txtJNumber.getText();
                int lRefKey = 0;
                if (StringUtils.hasValue(strTemp)) {
                    lRefKey = (int)EIGlobals.getInstance().getRefByAcc(strTemp);

                    
                }

                ///////////////////////////////////////////////////////////
                // tumor information
                ///////////////////////////////////////////////////////////
                int lTumorFrequencyKey = -1;
                strTemp = (String)comboTumorFrequencyKey.getSelectedItem();
                String strTumorKeyCompare = strTemp;

                strTemp = txtTumorFrequencyKey.getText();

                if (StringUtils.hasValue(strTemp)) {
                    try {
                        lTumorFrequencyKey = Integer.parseInt(strTemp);
                    } catch (Exception e) {
                        Utils.log(e);
                    }
                }

                // organ tissue of origin
                List<String> arrOrgansOrigin = null;
                Object[] arrSelectedOrgansOrigin =
                        listOrganTissueOrigin.getSelectedValues();
                if (arrSelectedOrgansOrigin.length > 0) {
                    arrOrgansOrigin = new ArrayList<String>();

                    for (int i = 0; i < arrSelectedOrgansOrigin.length; i++) {
                        LabelValueBean bean =
                                (LabelValueBean)arrSelectedOrgansOrigin[i];
                        arrOrgansOrigin.add(bean.getValue()+"");
                    }
                }

                // tumor class of origin
                List<String> arrTumorClass = null;
                Object[] arrSelectedTumorClass =
                        listTumorClassification.getSelectedValues();

                if (arrSelectedTumorClass.length > 0) {
                    arrTumorClass = new ArrayList<String>();

                    for (int i = 0; i < arrSelectedTumorClass.length; i++) {
                        LabelValueBean bean =
                                (LabelValueBean)arrSelectedTumorClass[i];
                        arrTumorClass.add(bean.getValue()+"");
                    }
                }

                // tumor name
                String strTumorName = txtTumorName.getText();

                // agent type (treatment type)
                long lTreatmentTypeKey = -1l;

                if (comboTreatmentType.getSelectedIndex() > 0) {
                    LabelValueBean<String,Long> bean =
                            (LabelValueBean<String,Long>)comboTreatmentType.getSelectedItem();
                    lTreatmentTypeKey = bean.getValue();
                }

                // agent name
                String strAgentName = txtTreatmentAgent.getText();

                // tumor name comparison
                objTemp = comboTumorName.getSelectedItem();
                if (objTemp != null) {
                    strTemp = (String)objTemp;
                }
         
                boolean bMetastasis = checkboxMetastasis.isSelected();

                // metastatic
                List<String> arrOrgansAffected = null;
                Object[] arrSelectedOrgansAffected =
                        listOrganTissueAffected.getSelectedValues();
                if (arrSelectedOrgansAffected.length > 0) {
                    arrOrgansAffected = new ArrayList<String>();

                    for (int i = 0; i < arrSelectedOrgansAffected.length; i++) {
                        LabelValueBean bean =
                                (LabelValueBean)arrSelectedOrgansAffected[i];
                        arrOrgansAffected.add(bean.getValue()+"");
                    }
                }

                boolean bImages = checkboxPathologyImages.isSelected();

                ///////////////////////////////////////////////////////////
                // strain info
                ///////////////////////////////////////////////////////////
                int lStrainKey = -1;
                strTemp = (String)comboStrainKey.getSelectedItem();
                String strStrainKeyCompare = strTemp;

                strTemp = txtStrainKey.getText();

                if (StringUtils.hasValue(strTemp)) {
                    try {
                        lStrainKey = Integer.parseInt(strTemp);
                    } catch (Exception e) {
                        Utils.log(e);
                    }
                }

                // strain name
                String strStrainName = txtStrainName.getText();

                // strain name comparison
                objTemp = comboStrainName.getSelectedItem();
                if (objTemp != null) {
                    strTemp = (String)objTemp;
                }
                String strStrainNameCompare = strTemp;

                // strain types
                List<String> arrStrainTypes = new ArrayList<String>();
                Object[] arrSelectedStrainTypes =
                        listStrainTypes.getSelectedValues();
                if (arrSelectedStrainTypes.length > 0) {
                    for (int i = 0; i < arrSelectedStrainTypes.length; i++) {
                        LabelValueBean bean =
                                (LabelValueBean)arrSelectedStrainTypes[i];
                        arrStrainTypes.add(bean.getValue()+"");
                    }
                }

                // create the TumorUtil DAO
                MTBTumorUtilDAO daoTumorUtil = MTBTumorUtilDAO.getInstance();

                // create the collection of matching strains to return
                SearchResults res = null;
                try {
                    // search for the strains
                    TumorFrequencySearchParams tfParams = 
                            new TumorFrequencySearchParams();
                    tfParams.setTumorFrequencyKey(lTumorFrequencyKey);
                    tfParams.setTfKeyComparison(strTumorKeyCompare);
                    tfParams.setOrgansAffected(arrOrgansAffected);
                    tfParams.setOrgansOrigin(arrOrgansOrigin);
                    tfParams.setTumorClassifications(arrTumorClass);
                    tfParams.setAgent(strAgentName);
                    tfParams.setAgentTypeKey(lTreatmentTypeKey);
                  
                    tfParams.setRestrictToMetastasis(bMetastasis);
                    tfParams.setMustHaveImages(bImages);
                    tfParams.setTumorName(strTumorName);
                    tfParams.setReferenceKey(lRefKey);

                    StrainSearchParams sParams = new StrainSearchParams();
                    sParams.setStrainKey(lStrainKey);
                    sParams.setStrainKeyComparison(strStrainKeyCompare);
                    sParams.setStrainName(strStrainName);
                    sParams.setStrainNameComparison(strStrainNameCompare);
                    sParams.setStrainTypes(arrStrainTypes);
                    sParams.setExactStrainTypes(false);
                    boolean includePrivateImages = true;
                    Date startDate = null;
                    Date endDate = null;
                    
                  
                    res = daoTumorUtil.searchNewDetail(tfParams, sParams, "", -1
                            , includePrivateImages, startDate, endDate);
                    
                    
                    
                } catch (Exception e) {
                    Utils.log("Error searching for tumor frequency");
                    Utils.log(e.getMessage());
                    Utils.log(StringUtils.getStackTrace(e));
                }

                return res;
            
    }

    /**
     * Configure the results table.
     */
    private void configureSearchResultsTable() {
        // column headers
        Vector headers = new Vector();
        headers.add("T.F. Key");
        headers.add("Tumor Name");
        headers.add("Organ Affected");
        headers.add("Treatment Type");
        headers.add("Agents");
        headers.add("Strain Name");
        headers.add("Sex");
        headers.add("Freq.");

        Vector data2 = new Vector();
        MXDefaultTableModel fxtm = new MXDefaultTableModel(data2, headers);

        fxtblSearchResults = new MXTable(data2, headers);
        fxtblSearchResults.setModel(fxtm);

        fxtblSearchResults.setColumnSizes(new int[]{75, 0, 0, 0, 0, 0, 50, 50});
        fxtblSearchResults.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    edit();
                }
            }
        });
        fxtblSearchResults.makeUneditable();
        fxtblSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fxtblSearchResults.setAlternateRowHighlight(true);
        fxtblSearchResults.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblSearchResults.setAlternateRowHighlightCount(2);
        fxtblSearchResults.setStartHighlightRow(1);
        fxtblSearchResults.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblSearchResults.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);

        fxtblSearchResults.enableToolTip(0, false);
        fxtblSearchResults.enableToolTip(1, false);

        jspResults.setViewportView(fxtblSearchResults);
        pnlResults.revalidate();
    }

    
    /**
     * Handle click for the search button.
     */
    private void search() {
        
        res = null;
        gV = new Vector();

        // disable the UI
        btnSearch.setEnabled(false);
        btnReset.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        //Color fg = lblStatus.getForeground();
        lblStatus.setForeground(new Color(255, 100, 100));
        lblStatus.setText("Searching...");
        Component compGlassPane = customInternalFrame.getGlassPane();
        progressGlassPane =
                new MXProgressGlassPane(customInternalFrame.getRootPane());
        customInternalFrame.setGlassPane(progressGlassPane);
        progressGlassPane.setVisible(true);
        progressGlassPane.setMessage("Searching...");

        try {
            // perform the search
            //final SearchResults res = searchDatabase();
           
           // for java 11 need to stop using foxtrop and use secondayLoop for event blocking
           // experimental
           Toolkit tk = Toolkit.getDefaultToolkit();
           EventQueue eq = tk.getSystemEventQueue();
           loop = eq.createSecondaryLoop();
           Thread worker = new SearchThread();
           worker.start();
           loop.enter();

            progressGlassPane.setMessage("Rendering SearchResults...");
            // construct the new table to display the results
            configureSearchResultsTable();

            
                    final List<MTBStrainTumorDetailsDTO> arr = new ArrayList<MTBStrainTumorDetailsDTO>(res.getList());
                    for (int i = 0; i < arr.size(); i++) {
                        final int row = i;

                       

                        try {
                            MTBStrainTumorDetailsDTO dto = arr.get(i);

                            Vector v = new Vector();
                            v.add(new Integer(dto.getTumorFrequencyKey()));
                            v.add(dto.getTumorName());
                            v.add(dto.getOrganAffectedName());
                            v.add(dto.getTreatmentType());
                            v.add(agentsAsString(dto.getAgentCollection()));
                            v.add(dto.getStrainName());
                            v.add(dto.getSex());
                            v.add(dto.getFrequency());

                            gV.add(v);
                        } catch (Exception e) {
                            Utils.log(e);
                        }
                    }
                   

            // enable the UI
            btnSearch.setEnabled(true);
            btnReset.setEnabled(true);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);

            lblStatus.setText("Done Searching!");

            // get the results
            Vector<String> headers = new Vector<String>();
            headers.add("T.F. Key");
            headers.add("Tumor Name");
            headers.add("Organ Affected");
            headers.add("Treatment Type");
            headers.add("Agents");
            headers.add("Strain Name");
            headers.add("Sex");
            headers.add("Freq.");

            MXDefaultTableModel tm = new MXDefaultTableModel(gV, headers);
            fxtblSearchResults.setModel(tm);
            fxtblSearchResults.enableToolTip(0, false);

            final int nSearchResults = res.getList().size();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lblStatus.setText(nSearchResults + " results found.");
                }
            });
            progressGlassPane.setVisible(false);
            customInternalFrame.setGlassPane(compGlassPane);
            progressGlassPane = null;

            customInternalFrame.adjust();
        } catch (Exception x) {
            lblStatus.setText("No results found.");
            Utils.log("===================================================");
            Utils.log(x.getMessage());
            Utils.log(StringUtils.getStackTrace(x));
            Utils.log("===================================================");
        } finally {
            ;
        }
    }

    /**
     * Handle click for the edit button.
     */
    private void edit() {
        final int nRow = fxtblSearchResults.getSelectedRow();

        if (nRow >= 0) {
            final MXDefaultTableModel tm =
                    (MXDefaultTableModel)fxtblSearchResults.getModel();

                 CustomInternalFrame cif = null;
                
                   cif = EIGlobals.getInstance().getMainFrame().
                            launchTumorFrequencyEditWindow(
                                 ((Integer)tm.getValueAt(nRow, 0)).intValue());
                   TumorFrequencyPanel tfp = (TumorFrequencyPanel) cif.getCustomPanel();
                   tfp.setSearchPanel(this);
             
        }
    }

    /**
     * Handle click for the reset button.
     */
    private void reset() {
        String blankText = "";
        comboTumorFrequencyKey.setSelectedIndex(0);
        txtTumorFrequencyKey.setText(blankText);
        txtJNumber.setText(blankText);

        listOrganTissueOrigin.clearSelection();
        listTumorClassification.clearSelection();

        comboTumorName.setSelectedIndex(0);
        txtTumorName.setText(blankText);

        comboStrainKey.setSelectedIndex(0);
        txtStrainKey.setText(blankText);
        comboStrainName.setSelectedIndex(0);
        txtStrainName.setText(blankText);

        listStrainTypes.clearSelection();

        checkboxMetastasis.setSelected(false);
        listOrganTissueAffected.clearSelection();

        checkboxPathologyImages.setSelected(false);

        lblStatus.setText(blankText);
        
        this.comboTreatmentType.setSelectedIndex(0);
        txtTreatmentAgent.setText(blankText);

        fxtblSearchResults.setModel(new MXDefaultTableModel(
                new Object [][] {
        },
                new String [] { "T.F. Key", "Tumor Name", "Organ Affected",
                                "Treatmnent Type", "Strain Name", "Sex",
                                "Freq."
        }
        ));

        btnSearch.setEnabled(true);
        btnReset.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    /**
     * Handle click for the delete button.
     */
    private void delete() {
        int nRow = fxtblSearchResults.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel fxtm =
                    (MXDefaultTableModel)fxtblSearchResults.getModel();
            long lKey = ((Integer)fxtm.getValueAt(nRow, 0)).longValue();

            // Modal dialog with OK/cancel and a text field
            String strMessage = "Are you sure you would like to permanently " +
                                "delete Tumor Frequency Key " + lKey + "?";
            int nAnswer =
                    JOptionPane.showConfirmDialog(this, strMessage, "Warning",
                                                  JOptionPane.YES_NO_OPTION);
            if (nAnswer == JOptionPane.YES_OPTION) {
                delete(lKey);
            } else if (nAnswer == JOptionPane.NO_OPTION) {
                // do nothing
                return;
            }
            
        }
    }

    /**
     * Delete the <code>TumorFrequency</code> and associated values from the
     * database.
     *
     * @param lKey the <code>Strain</code> key
     */
    private void delete(long lKey) {
        TumorFrequencyDAO daoTF = TumorFrequencyDAO.getInstance();
        TumorFrequencyNotesDAO daoTFNotes = TumorFrequencyNotesDAO.getInstance();
        TumorFrequencySynonymsDAO daoTFSynonyms = TumorFrequencySynonymsDAO.getInstance();
        TumorFrequencyTreatmentsDAO daoTFTreatments = TumorFrequencyTreatmentsDAO.getInstance();
        TumorPathologyAssocDAO daoTumorPathologyAssoc = TumorPathologyAssocDAO.getInstance();
        MTBTumorGeneticChangesDAO daoTGC = MTBTumorGeneticChangesDAO.getInstance();
        TumorGeneticsDAO daoTG = TumorGeneticsDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        boolean commit = false;

        try {
            Long tfKey = new Long(lKey);
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // delete the tumor pathology association
            ///////////////////////////////////////////////////////////////////
            daoTumorPathologyAssoc.deleteByTumorFrequencyKey(tfKey);

            ///////////////////////////////////////////////////////////////////
            // delete the tumor frequency treatments
            ///////////////////////////////////////////////////////////////////
            daoTFTreatments.deleteByTumorFrequencyKey(tfKey);

            ///////////////////////////////////////////////////////////////////
            // delete the tumor frequency synonyms
            ///////////////////////////////////////////////////////////////////
            daoTFSynonyms.deleteByTumorFrequencyKey(tfKey);

            ///////////////////////////////////////////////////////////////////
            // delete the tumor frequency notes
            ///////////////////////////////////////////////////////////////////
            daoTFNotes.deleteByTumorFrequencyKey(tfKey);
            
            ///////////////////////////////////////////////////////////////////
            // delete tumor genetics
            ///////////////////////////////////////////////////////////////////
            daoTG.deleteByTumorFrequencyKey(tfKey);
            
            
            ///////////////////////////////////////////////////////////////////
            // delete tumor genetic changes
            ///////////////////////////////////////////////////////////////////
            daoTGC.deleteByTumorFrequencyKey(tfKey);
            
            ///////////////////////////////////////////////////////////////////
            // delete accession
            ///////////////////////////////////////////////////////////////////
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setObjectKey(tfKey);
            dtoAccession.setSiteInfoKey(0);
            dtoAccession.setMTBTypesKey(5);
            dtoAccession.setPrefixPart("MTB:");
            daoAccession.deleteUsingTemplate(dtoAccession);
            
            ///////////////////////////////////////////////////////////////////
            // delete tumor frequency
            ///////////////////////////////////////////////////////////////////
            daoTF.deleteByPrimaryKey(tfKey);

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            commit = true;

        } catch (Exception e) {
            Utils.showErrorDialog(e.getMessage(), e);
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to delete Tumor Frequency.", e2);
            }
            if (commit) {
                Utils.showSuccessDialog("Tumor Frequency " + lKey + " sucessfully deleted.");
            } else {
                Utils.showErrorDialog("Unable to delete Tumor Frequency.");
            }
        }
    }
    
    private String agentsAsString(Collection<String> agents){
      StringBuffer sb = new StringBuffer("");
      for(String agent : agents){
        if((agent != null)  && (!agent.equals("null"))){
          sb.append(agent+", ");
        }
      }
      if( sb.length() < 2){
              return "";
      }
      return sb.substring(0, sb.length()-2);
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
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    pnlCriteria = new javax.swing.JPanel();
    headerPanelCriteria = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    pnlCriteriaTumorFrequency = new javax.swing.JPanel();
    lblTumorFrequencyKey = new javax.swing.JLabel();
    comboTumorFrequencyKey = new javax.swing.JComboBox();
    txtTumorFrequencyKey = new javax.swing.JTextField();
    txtJNumber = new javax.swing.JTextField();
    lblJNumber = new javax.swing.JLabel();
    lblOrganTissueOrigin = new javax.swing.JLabel();
    lblTumorClassification = new javax.swing.JLabel();
    lblTumorName = new javax.swing.JLabel();
    lblTreatmentType = new javax.swing.JLabel();
    jspOrganTissueOrigin = new javax.swing.JScrollPane();
    listOrganTissueOrigin = new javax.swing.JList();
    jspTumorClassification = new javax.swing.JScrollPane();
    listTumorClassification = new javax.swing.JList();
    txtTumorName = new javax.swing.JTextField();
    comboTumorName = new javax.swing.JComboBox();
    comboTreatmentType = new javax.swing.JComboBox();
    txtTreatmentAgent = new javax.swing.JTextField();
    lblTreatmentAgent = new javax.swing.JLabel();
    checkboxMetastasis = new javax.swing.JCheckBox();
    jspOrganTissueAffected = new javax.swing.JScrollPane();
    listOrganTissueAffected = new javax.swing.JList();
    lblMetastasis = new javax.swing.JLabel();
    checkboxPathologyImages = new javax.swing.JCheckBox();
    pnlCriteriaStrain = new javax.swing.JPanel();
    lblStrainKey = new javax.swing.JLabel();
    lblStrainName = new javax.swing.JLabel();
    lblStrainType = new javax.swing.JLabel();
    comboStrainKey = new javax.swing.JComboBox();
    comboStrainName = new javax.swing.JComboBox();
    jspStrainTypes = new javax.swing.JScrollPane();
    listStrainTypes = new javax.swing.JList();
    txtStrainKey = new javax.swing.JTextField();
    txtStrainName = new javax.swing.JTextField();
    btnReset = new javax.swing.JButton();
    btnSearch = new javax.swing.JButton();
    pnlResults = new javax.swing.JPanel();
    lblStatus = new javax.swing.JLabel();
    btnEdit = new javax.swing.JButton();
    btnDelete = new javax.swing.JButton();
    jspResults = new javax.swing.JScrollPane();
    tblResults = new javax.swing.JTable();
    headerPanelResults = new org.jax.mgi.mtb.gui.MXHeaderPanel();

    pnlCriteria.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    headerPanelCriteria.setDrawSeparatorUnderneath(true);
    headerPanelCriteria.setText("Tumor Search Criteria");

    pnlCriteriaTumorFrequency.setBorder(javax.swing.BorderFactory.createTitledBorder("Tumor Frequency Information"));

    lblTumorFrequencyKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
    lblTumorFrequencyKey.setText("Tumor Frequency Key");

    comboTumorFrequencyKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=" }));

    txtTumorFrequencyKey.setColumns(10);

    txtJNumber.setColumns(10);
    txtJNumber.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtJNumberFocusLost(evt);
      }
    });

    lblJNumber.setText("JNumber");

    lblOrganTissueOrigin.setText("Organ/Tissue of Origin");

    lblTumorClassification.setText("Tumor Classification");

    lblTumorName.setText("Tumor Name");

    lblTreatmentType.setText("Treatment Type");

    listOrganTissueOrigin.setVisibleRowCount(4);
    jspOrganTissueOrigin.setViewportView(listOrganTissueOrigin);

    listTumorClassification.setVisibleRowCount(4);
    jspTumorClassification.setViewportView(listTumorClassification);

    comboTumorName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contains", "Begins", "Equals" }));

    lblTreatmentAgent.setText("Treatment Agent");

    checkboxMetastasis.setText("Restrict search to metastasis tumors only");
    checkboxMetastasis.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkboxMetastasis.setMargin(new java.awt.Insets(0, 0, 0, 0));

    listOrganTissueAffected.setVisibleRowCount(4);
    jspOrganTissueAffected.setViewportView(listOrganTissueAffected);

    lblMetastasis.setText("Metastasizes to the");

    checkboxPathologyImages.setText("Restrict search to entries with associated pathology images");
    checkboxPathologyImages.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkboxPathologyImages.setMargin(new java.awt.Insets(0, 0, 0, 0));

    org.jdesktop.layout.GroupLayout pnlCriteriaTumorFrequencyLayout = new org.jdesktop.layout.GroupLayout(pnlCriteriaTumorFrequency);
    pnlCriteriaTumorFrequency.setLayout(pnlCriteriaTumorFrequencyLayout);
    pnlCriteriaTumorFrequencyLayout.setHorizontalGroup(
      pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlCriteriaTumorFrequencyLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlCriteriaTumorFrequencyLayout.createSequentialGroup()
            .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblMetastasis)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblTreatmentType)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblTumorName)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblTumorClassification)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblOrganTissueOrigin)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblTumorFrequencyKey))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(pnlCriteriaTumorFrequencyLayout.createSequentialGroup()
                .add(comboTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 185, Short.MAX_VALUE)
                .add(lblJNumber)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
              .add(pnlCriteriaTumorFrequencyLayout.createSequentialGroup()
                .add(comboTumorName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTumorName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
              .add(pnlCriteriaTumorFrequencyLayout.createSequentialGroup()
                .add(comboTreatmentType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 197, Short.MAX_VALUE)
                .add(lblTreatmentAgent)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTreatmentAgent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 175, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
              .add(org.jdesktop.layout.GroupLayout.TRAILING, jspOrganTissueAffected, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
              .add(jspTumorClassification, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
              .add(jspOrganTissueOrigin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)))
          .add(checkboxPathologyImages)
          .add(checkboxMetastasis))
        .addContainerGap())
    );

    pnlCriteriaTumorFrequencyLayout.linkSize(new java.awt.Component[] {comboTumorFrequencyKey, comboTumorName}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    pnlCriteriaTumorFrequencyLayout.setVerticalGroup(
      pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlCriteriaTumorFrequencyLayout.createSequentialGroup()
        .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblTumorFrequencyKey)
          .add(comboTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblJNumber))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblOrganTissueOrigin)
          .add(jspOrganTissueOrigin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblTumorClassification)
          .add(jspTumorClassification, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblTumorName)
          .add(comboTumorName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtTumorName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblTreatmentType)
          .add(comboTreatmentType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblTreatmentAgent)
          .add(txtTreatmentAgent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(checkboxMetastasis)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblMetastasis)
          .add(jspOrganTissueAffected, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
        .add(checkboxPathologyImages)
        .addContainerGap())
    );

    pnlCriteriaStrain.setBorder(javax.swing.BorderFactory.createTitledBorder("Strain Information"));

    lblStrainKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
    lblStrainKey.setText("Strain Key");

    lblStrainName.setText("Strain Name");

    lblStrainType.setText("Strain Type");

    comboStrainKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

    comboStrainName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contains", "Begins", "Equals" }));

    listStrainTypes.setVisibleRowCount(4);
    jspStrainTypes.setViewportView(listStrainTypes);

    txtStrainKey.setColumns(10);

    org.jdesktop.layout.GroupLayout pnlCriteriaStrainLayout = new org.jdesktop.layout.GroupLayout(pnlCriteriaStrain);
    pnlCriteriaStrain.setLayout(pnlCriteriaStrainLayout);
    pnlCriteriaStrainLayout.setHorizontalGroup(
      pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlCriteriaStrainLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStrainKey)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStrainName)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStrainType))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlCriteriaStrainLayout.createSequentialGroup()
            .add(comboStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
          .add(pnlCriteriaStrainLayout.createSequentialGroup()
            .add(comboStrainName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(txtStrainName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE))
          .add(jspStrainTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE))
        .addContainerGap())
    );

    pnlCriteriaStrainLayout.linkSize(new java.awt.Component[] {comboStrainKey, comboStrainName}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    pnlCriteriaStrainLayout.setVerticalGroup(
      pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlCriteriaStrainLayout.createSequentialGroup()
        .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblStrainKey)
          .add(comboStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblStrainName)
          .add(comboStrainName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtStrainName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblStrainType)
          .add(jspStrainTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(16, Short.MAX_VALUE))
    );

    btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Refresh16.png"))); // NOI18N
    btnReset.setText("Reset");
    btnReset.setPreferredSize(new java.awt.Dimension(95, 25));
    btnReset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnResetActionPerformed(evt);
      }
    });

    btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
    btnSearch.setText("Search");
    btnSearch.setPreferredSize(new java.awt.Dimension(95, 25));
    btnSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSearchActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout pnlCriteriaLayout = new org.jdesktop.layout.GroupLayout(pnlCriteria);
    pnlCriteria.setLayout(pnlCriteriaLayout);
    pnlCriteriaLayout.setHorizontalGroup(
      pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteriaLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCriteriaStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCriteriaTumorFrequency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
      .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteriaLayout.createSequentialGroup()
        .addContainerGap(464, Short.MAX_VALUE)
        .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    pnlCriteriaLayout.setVerticalGroup(
      pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlCriteriaLayout.createSequentialGroup()
        .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaTumorFrequency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaStrain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 39, Short.MAX_VALUE)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pnlResults.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblStatus.setText("No Results Found");

    btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Edit16.png"))); // NOI18N
    btnEdit.setText("Edit");
    btnEdit.setPreferredSize(new java.awt.Dimension(95, 25));
    btnEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnEditActionPerformed(evt);
      }
    });

    btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Delete16.png"))); // NOI18N
    btnDelete.setText("Delete");
    btnDelete.setPreferredSize(new java.awt.Dimension(95, 25));
    btnDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnDeleteActionPerformed(evt);
      }
    });

    tblResults.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jspResults.setViewportView(tblResults);

    headerPanelResults.setDrawSeparatorUnderneath(true);
    headerPanelResults.setText("Tumor Frequency Search Results");

    org.jdesktop.layout.GroupLayout pnlResultsLayout = new org.jdesktop.layout.GroupLayout(pnlResults);
    pnlResults.setLayout(pnlResultsLayout);
    pnlResultsLayout.setHorizontalGroup(
      pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlResultsLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jspResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
          .add(pnlResultsLayout.createSequentialGroup()
            .add(lblStatus)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 203, Short.MAX_VALUE)
            .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
      .add(headerPanelResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
    );
    pnlResultsLayout.setVerticalGroup(
      pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlResultsLayout.createSequentialGroup()
        .add(headerPanelResults, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblStatus)
          .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jspResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
        .addContainerGap())
    );

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(layout.createSequentialGroup()
        .addContainerGap()
        .add(pnlCriteria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

    private void txtJNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberFocusLost
        Utils.fixJNumber(txtJNumber);
    }//GEN-LAST:event_txtJNumberFocusLost

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        edit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        reset();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnDelete;
  private javax.swing.JButton btnEdit;
  private javax.swing.JButton btnReset;
  private javax.swing.JButton btnSearch;
  private javax.swing.JCheckBox checkboxMetastasis;
  private javax.swing.JCheckBox checkboxPathologyImages;
  private javax.swing.JComboBox comboStrainKey;
  private javax.swing.JComboBox comboStrainName;
  private javax.swing.JComboBox comboTreatmentType;
  private javax.swing.JComboBox comboTumorFrequencyKey;
  private javax.swing.JComboBox comboTumorName;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCriteria;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelResults;
  private javax.swing.JScrollPane jspOrganTissueAffected;
  private javax.swing.JScrollPane jspOrganTissueOrigin;
  private javax.swing.JScrollPane jspResults;
  private javax.swing.JScrollPane jspStrainTypes;
  private javax.swing.JScrollPane jspTumorClassification;
  private javax.swing.JLabel lblJNumber;
  private javax.swing.JLabel lblMetastasis;
  private javax.swing.JLabel lblOrganTissueOrigin;
  private javax.swing.JLabel lblStatus;
  private javax.swing.JLabel lblStrainKey;
  private javax.swing.JLabel lblStrainName;
  private javax.swing.JLabel lblStrainType;
  private javax.swing.JLabel lblTreatmentAgent;
  private javax.swing.JLabel lblTreatmentType;
  private javax.swing.JLabel lblTumorClassification;
  private javax.swing.JLabel lblTumorFrequencyKey;
  private javax.swing.JLabel lblTumorName;
  private javax.swing.JList listOrganTissueAffected;
  private javax.swing.JList listOrganTissueOrigin;
  private javax.swing.JList listStrainTypes;
  private javax.swing.JList listTumorClassification;
  private javax.swing.JPanel pnlCriteria;
  private javax.swing.JPanel pnlCriteriaStrain;
  private javax.swing.JPanel pnlCriteriaTumorFrequency;
  private javax.swing.JPanel pnlResults;
  private javax.swing.JTable tblResults;
  private javax.swing.JTextField txtJNumber;
  private javax.swing.JTextField txtStrainKey;
  private javax.swing.JTextField txtStrainName;
  private javax.swing.JTextField txtTreatmentAgent;
  private javax.swing.JTextField txtTumorFrequencyKey;
  private javax.swing.JTextField txtTumorName;
  // End of variables declaration//GEN-END:variables

}
