 /* Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/ReferenceSearchPanel.java,v 1.1 2007/04/30 15:50:57 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;


import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.jax.mgi.mtb.dao.custom.SearchResults;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceSearchDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.param.ReferenceSearchParams;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.gui.CustomInternalFrame;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.models.MTBReferenceSearchDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.MGIReferenceAPIUtil;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXProgressGlassPane;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.utils.LabelValueBean;

/**
 * For searching <b>Reference</b> data.
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/ReferenceSearchPanel.java,v 1.1 2007/04/30 15:50:57 mjv Exp
 */
public class ReferenceSearchPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none
    // ----------------------------------------------------- Instance Variables
    // custom JTable for sorting
    protected MXTable fxtblSearchResults = null;  // for searching effect
    private MXProgressGlassPane progressGlassPane = null;
    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new ReferenceSearchPanel.
     */
    public ReferenceSearchPanel() {
        initComponents();
        initCustom();
    }
    // --------------------------------------------------------- Public Methods

    /**
     * Clear the results from the form.
     */
    public void clear() {
        try {
            fxtblSearchResults.removeAll();
            fxtblSearchResults = null;
        } catch (Exception e) {
            Utils.log(e);
        }
    }

    // ------------------------------------------------------ Protected Methods
    // none
    // -------------------------------------------------------- Private Methods
    /**
     * Perform any custom initialization needed.
     */
    private void initCustom() {
        // make it so the following fields accept numeric input only
        Utils.setNumericFilter(txtReferenceKey);

        configureSearchResultsTable();

        configureOrganCombo();
        configureTCCombo();


        List<LabelValueBean<String, String>> priority = EIGlobals.getInstance().getReferencePriority();

        jComboBoxPriority.setModel(new LVBeanListModel<String, String>(priority, false));
        jComboBoxPriority.setRenderer(new LVBeanListCellRenderer<String, String>());
        jComboBoxPriority.addKeyListener(new LVBeanComboListener<String, String>());
        jComboBoxPriority.setSelectedIndex(0);

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

      //  jTextFieldTriageURL.setText(EIConstants.TRIAGE_URL);
    }

    private void configureOrganCombo() {

        List<LabelValueBean<String, Long>> organs = new ArrayList<LabelValueBean<String, Long>>(EIGlobals.getInstance().getOrgansUnfiltered().values());
        organs.add(0, new LabelValueBean<String, Long>("--ANY--", -1L));
        jComboBoxOrgan.setModel(
                new LVBeanListModel<String, Long>(organs));
        jComboBoxOrgan.setRenderer(new LVBeanListCellRenderer<String, Long>());
        jComboBoxOrgan.addKeyListener(new LVBeanComboListener<String, Long>());
        jComboBoxOrgan.setSelectedIndex(0);

    }

    private void configureTCCombo() {

        List<LabelValueBean<String, Long>> tumorClassifications = new ArrayList<LabelValueBean<String, Long>>(EIGlobals.getInstance().getTumorClassifications().values());
        tumorClassifications.add(0, new LabelValueBean<String, Long>("--ANY--", -1L));
        jComboBoxTumorClass.setModel(
                new LVBeanListModel<String, Long>(tumorClassifications));
        jComboBoxTumorClass.setRenderer(new LVBeanListCellRenderer<String, Long>());
        jComboBoxTumorClass.addKeyListener(new LVBeanComboListener<String, Long>());
        jComboBoxTumorClass.setSelectedIndex(0);

    }

    private SearchResults loadFromTriage() throws Exception {
       
                MGIReferenceAPIUtil apiUtil = new MGIReferenceAPIUtil();
    
                ArrayList<ReferenceDTO> refs = apiUtil.getReferences();
                // need to check if any of these are in MTB

                SearchResults<MTBReferenceSearchDTO> res = new SearchResults<MTBReferenceSearchDTO>();
                ArrayList<MTBReferenceSearchDTO> newList = new ArrayList<MTBReferenceSearchDTO>();

             
                ReferenceSearchParams params = new ReferenceSearchParams();
                MTBReferenceUtilDAO dao = MTBReferenceUtilDAO.getInstance();

                AccessionDAO daoAccession = AccessionDAO.getInstance();

                ReferenceDAO daoReference = ReferenceDAO.getInstance();

                for (ReferenceDTO dto : refs) {
                    params.setReferenceAccessionId((String)dto.getDataBean().get("JNum"));
                    params.setIncludeRejected(true);
                    SearchResults mtbRes = dao.searchReference(params);
                    if (mtbRes.getList().size() == 0) {

                        try {
                            
                            MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
                            Date dNow = new Date();

                            dto.setCreateUser("triage");
                            dto.setCreateDate(dNow);
                            dto.setUpdateUser(dtoUser.getUserName());
                            dto.setUpdateDate(dNow);

                            ///////////////////////////////////////////////////////////////////
                            // save the reference
                            ///////////////////////////////////////////////////////////////////

                            dto = daoReference.save(dto);


                            ///////////////////////////////////////////////////////////////////
                            // save the accession information
                            ///////////////////////////////////////////////////////////////////
                            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
                            String strJNumber = (String)dto.getDataBean().get("JNum");
                            long lNumericPart = Utils.parseJNumber(strJNumber);

                            dtoAccession.setAccID(strJNumber);
                            dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_REFERENCE);
                            dtoAccession.setObjectKey(dto.getReferenceKey());
                            dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
                            dtoAccession.setPrefixPart("J:");
                            dtoAccession.setNumericPart(lNumericPart);
                            dtoAccession.setCreateUser("triage");
                            dtoAccession.setCreateDate(dNow);
                            dtoAccession.setUpdateUser(dtoUser.getUserName());
                            dtoAccession.setUpdateDate(dNow);


                            dtoAccession = daoAccession.save(dtoAccession);
                            
                            Utils.log("Saved reference "+strJNumber);
                            
                            
                            try{
                                
                                String strPubMedID = (String)dto.getDataBean().get("pubMedID");
                            
                                long pmIDNum = (Long.parseLong(strPubMedID));
                            
                                dtoAccession = daoAccession.createAccessionDTO();

                                dtoAccession.setAccID(strPubMedID);
                                dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_REFERENCE);
                                dtoAccession.setObjectKey(dto.getReferenceKey());
                                dtoAccession.setSiteInfoKey(29);
                                dtoAccession.setPrefixPart("");
                                dtoAccession.setNumericPart(pmIDNum);
                                dtoAccession.setCreateUser("triage");
                                dtoAccession.setCreateDate(dNow);
                                dtoAccession.setUpdateUser(dtoUser.getUserName());
                                dtoAccession.setUpdateDate(dNow);


                                dtoAccession = daoAccession.save(dtoAccession);
                                
                            }catch(NumberFormatException e){
                                // no PubMed ID for this reference
                                // that is OK carry on
                            }
                            
                            MTBReferenceSearchDTO searchDTO = new MTBReferenceSearchDTO();
                            searchDTO.setAccId(strJNumber);
                            searchDTO.setAuthors(dto.getAuthors());
                            searchDTO.setFirstAuthor(dto.getPrimaryAuthor());
                            searchDTO.setKey(dto.getReferenceKey().intValue());
                            searchDTO.setCitation(dto.getCitation());
                            searchDTO.setPriority(null);

                            newList.add(searchDTO);

                        } catch (Exception e) {
                            Utils.log(e);
                            Utils.showErrorDialog("Can't load Reference "+ dto.getDataBean().get("JNum"));
                        }

                    }
                }

                res.setList(newList);
                res.setTotal(newList.size());

              


        return res;
    }
    
    
    
    

    /**
     * Search the database for the <code>Reference</code>s based upon the
     * search criteria.
     *
     * @return the <code>SearchResults</code>
     */
    private SearchResults searchDatabase() throws Exception {
      
                // determine parameters
                String strTemp = null;
                Object objTemp = null;
                int nRefKey = 0;

                strTemp = txtReferenceKey.getText();

                if (StringUtils.hasValue(strTemp)) {
                    try {
                        nRefKey = Integer.parseInt(strTemp);
                    } catch (Exception e) {
                        nRefKey = 0;
                    }
                }

                // reference key comparison
                String strKeyCompare = null;

                objTemp = comboReferenceKey.getSelectedItem();
                if (objTemp != null) {
                    strKeyCompare = (String) objTemp;
                }

                // accession id
                String strAccID = txtJNumber.getText();



                // first author
                String strFirstAuthor = txtFirstAuthor.getText();

                // first author comparison
                String strFirstAuthorCompare = null;

                objTemp = comboFirstAuthor.getSelectedItem();
                if (objTemp != null) {
                    strFirstAuthorCompare = (String) objTemp;
                }

                // authors
                String strAuthors = txtAuthor.getText();

                // author comparison
                String strAuthorsCompare = "contains";

                // journal
                String strJournal = txtJournal.getText();

                // journal comparison
                String strJournalCompare = "contains";

                // title
                String strTitle = txtTitle.getText();

                // title comparison
                String strTitleCompare = "contains";

                // create a ReferenceUtilDAO
                MTBReferenceUtilDAO daoRefUtil =
                        MTBReferenceUtilDAO.getInstance();

                SearchResults res = null;
                int coded = -1;

                int isIndexed = -1;
                if (jCheckBoxCoded.isSelected()) {
                    coded = 1;
                }
                if (jCheckBoxNotCoded.isSelected()) {
                    if (coded != 1) {
                        coded = 0;
                    } else {
                        coded = -1;
                    }
                }




                if (jCheckBoxIndexed.isSelected()) {
                    isIndexed = 1;
                }
                if (jCheckBoxNotIndexed.isSelected()) {
                    if (isIndexed != 1) {
                        isIndexed = 0;
                    } else {
                        isIndexed = -1;
                    }
                }

                String codedBy = jTextFieldCodedBy.getText();

                String codedByDateComparison = (String) jComboCodedByDateComparison.getSelectedItem();
                String codedByDateStr = jTextCodedByDate.getText().trim();
                Date codedByDate = null;

                if ((codedByDateStr != null) && (codedByDateStr.length() > 0)) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        sdf.setLenient(false);
                        codedByDate = sdf.parse(codedByDateStr);

                    } catch (Exception e) {
                        Utils.showErrorDialog("Invaid date. Format is MM/DD/YYYY");

                        jTextCodedByDate.setText("");
                        return res;
                    }


                }

                String pubYearComparison = (String) jComboPubYearComparison.getSelectedItem();
                int pubYear = 0;

                String pYear = jTextPubYear.getText();
                if (pYear != null && pYear.length() > 0) {

                    try {
                        pubYear = new Integer(pYear).intValue();

                    } catch (NumberFormatException npe) {
                        Utils.showErrorDialog("Invaid publication year. Format is a four digit year");
                        jTextPubYear.setText("");
                        return res;
                    }
                }

                String createDateComparison = (String) jComboDateComparison.getSelectedItem();
                String createDateStr = jTextCreatedDate.getText().trim();
                Date createDate = null;

                if ((createDateStr != null) && (createDateStr.length() > 0)) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        sdf.setLenient(false);
                        createDate = sdf.parse(createDateStr);

                    } catch (Exception e) {
                        Utils.showErrorDialog("Invaid date. Format is MM/DD/YYYY");

                        jTextCreatedDate.setText("");
                        return res;
                    }

                }

                long organKey = ((LabelValueBean<String, Long>) jComboBoxOrgan.getSelectedItem()).getValue().longValue();

                long tcKey = ((LabelValueBean<String, Long>) jComboBoxTumorClass.getSelectedItem()).getValue().longValue();

                Long priority = null;

                try {

                    priority = new Long(((LabelValueBean<String, String>) jComboBoxPriority.getSelectedItem()).getValue());

                } catch (Exception ignore) {
                }

                try {
                    // search for references
                    ReferenceSearchParams params = new ReferenceSearchParams();
                    params.setReferenceKey(nRefKey);
                    params.setReferenceKeyComparison(strKeyCompare);
                    params.setReferenceAccessionId(strAccID);
                    params.setFirstAuthor(strFirstAuthor);
                    params.setFirstAuthorComparison(strFirstAuthorCompare);
                    params.setAuthors(strAuthors);
                    params.setAuthorsComparison(strAuthorsCompare);
                    params.setJournal(strJournal);
                    params.setJournalComparison(strJournalCompare);
                    params.setTitle(strTitle);
                    params.setTitleComparison(strTitleCompare);
                    params.setCodedBy(codedBy);
                    params.setCodedByDate(codedByDate);
                    params.setCodedByDateComparison(codedByDateComparison);
                    params.setCoded(coded);
                    params.setOrgan(organKey);
                    params.setTumorClassification(tcKey);
                    if (priority != null) {
                        params.setPriority(priority.longValue());
                    }
                    params.setIndexed(isIndexed);
                    params.setOrderBy(params.ORDER_BY_JNUMBER);
                    params.setMaxItems(-1);
                    if (createDate != null) {
                        params.setCreateDate(createDate);
                        params.setCreateDateComparison(createDateComparison);
                    }

                    if (pubYear > 0) {
                        params.setPubYear(pubYear);
                        params.setPubYearComparison(pubYearComparison);
                    }
                    params.setIncludeRejected(true);

                    res = daoRefUtil.searchReference(params);
                } catch (Exception e) {
                    Utils.log("Error searching for references");
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
        List<String> arrHeaders = new ArrayList<String>(4);
        arrHeaders.add("JNumber");
        arrHeaders.add("Ref Key");
        arrHeaders.add("Priority");
        arrHeaders.add("Coded By");
        arrHeaders.add("Coded Date");

        arrHeaders.add("First Author");
        arrHeaders.add("Short Citation");

        List data = new ArrayList();
        MTBReferenceSearchDTOTableModel<MTBReferenceSearchDTO> rsdtm =
                new MTBReferenceSearchDTOTableModel<MTBReferenceSearchDTO>(data, arrHeaders);

        fxtblSearchResults = new MXTable(data, arrHeaders);
        fxtblSearchResults.setModel(rsdtm);

        fxtblSearchResults.setColumnSizes(new int[]{80, 70, 50, 70, 80, 0, 0});
        fxtblSearchResults.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    edit(false);
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
    private void getReferences(boolean fromTriage) {

        String action = "Searching";
        if (fromTriage) {
            action = "Loading";
        }
        // disable the UI
        btnSearch.setEnabled(false);
        btnReset.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        //Color fg = lblStatus.getForeground();
        lblStatus.setForeground(new Color(255, 100, 100));
        lblStatus.setText(action + "...");
        Component compGlassPane = customInternalFrame.getGlassPane();
        progressGlassPane =
                new MXProgressGlassPane(customInternalFrame.getRootPane());
        customInternalFrame.setGlassPane(progressGlassPane);
        progressGlassPane.setVisible(true);
        progressGlassPane.setMessage(action + "...");


        try {
            // perform the search
            SearchResults res = null;
            if (fromTriage) {
                res = loadFromTriage();
            } else {

                res = searchDatabase();

            }

            if (res == null) {
                lblStatus.setText("");
                btnSearch.setEnabled(true);
                btnReset.setEnabled(true);
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);


                return;
            }
            progressGlassPane.setMessage("Rendering Results...");
            // construct the new table to display the results
            configureSearchResultsTable();

            // enable the UI
            btnSearch.setEnabled(true);
            btnReset.setEnabled(true);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);

            lblStatus.setText("Done " + action);

            // get the results
            List arr = new ArrayList(res.getList());
            List<String> arrHeaders = new ArrayList<String>(9);
            arrHeaders.add("JNumber");
            arrHeaders.add("Ref Key");
            arrHeaders.add("Priority");
            arrHeaders.add("Coded By");
            arrHeaders.add("Coded Date");
            arrHeaders.add("First Author");
            arrHeaders.add("Short Citation");
            MTBReferenceSearchDTOTableModel<MTBReferenceSearchDTO> tm =
                    new MTBReferenceSearchDTOTableModel<MTBReferenceSearchDTO>(arr, arrHeaders);
            fxtblSearchResults.setModel(tm);
            fxtblSearchResults.enableToolTip(0, false);

            final int nSearchResults = res.getList().size();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    lblStatus.setText(nSearchResults + " results found.");
                }
            });

            customInternalFrame.adjust();
        } catch (Exception x) {
            lblStatus.setText("No results found.");
            Utils.log("===================================================");
            Utils.log(x.getMessage());
            Utils.log(StringUtils.getStackTrace(x));
            Utils.log("===================================================");
        } finally {
            progressGlassPane.setVisible(false);
            customInternalFrame.setGlassPane(compGlassPane);
            progressGlassPane = null;
        }
    }

    /**
     * Handle click for the edit button.
     */
    private void edit(boolean editable) {
        final int nRow = fxtblSearchResults.getSelectedRow();
        final boolean feditable = editable;
        if (nRow >= 0) {
            final MXDefaultTableModel tm =
                    (MXDefaultTableModel) fxtblSearchResults.getModel();

            CustomInternalFrame cif = null;

            int key = ((Integer) tm.getValueAt(nRow, 1)).intValue();
            if (key != 0) {
                cif = EIGlobals.getInstance().getMainFrame().
                        launchReferenceEditWindow(key, feditable);
                ReferencePanel rp = (ReferencePanel) cif.getCustomPanel();

                rp.setSearchPanel(this);

            }

        }
    }

    /**
     * Handle click for the reset button.
     */
    private void reset() {
        comboReferenceKey.setSelectedIndex(0);
        txtReferenceKey.setText("");
        txtJNumber.setText("");
        comboFirstAuthor.setSelectedIndex(0);
        txtFirstAuthor.setText("");
        txtAuthor.setText("");
        txtJournal.setText("");
        txtTitle.setText("");
        lblStatus.setText("");

        jCheckBoxCoded.setSelected(false);
        jCheckBoxNotCoded.setSelected(false);

        jCheckBoxIndexed.setSelected(false);
        jCheckBoxNotIndexed.setSelected(false);
        jTextFieldCodedBy.setText("");
        jComboBoxOrgan.setSelectedIndex(0);
        jComboBoxTumorClass.setSelectedIndex(0);
        jComboBoxPriority.setSelectedIndex(0);
        this.jComboDateComparison.setSelectedIndex(0);
        this.jTextCodedByDate.setText("");
        this.jTextCreatedDate.setText("");
        this.jTextPubYear.setText("");
        this.jComboPubYearComparison.setSelectedIndex(0);
        this.jComboCodedByDateComparison.setSelectedIndex(0);


        fxtblSearchResults.setModel(new MXDefaultTableModel(
                new Object[][]{},
                new String[]{
                    "JNumber", "Ref Key", "Priority", "Coded By", "Coded Date", "First Author", "Short Citation"
                }));
        btnSearch.setEnabled(true);
        btnReset.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

      //  jTextFieldTriageURL.setText(EIConstants.TRIAGE_URL);
    }

    /**
     * Handle click for the delete button.
     */
    private void delete() {
        int nRow = fxtblSearchResults.getSelectedRow();

        if (nRow >= 0) {
            MTBReferenceSearchDTOTableModel fxtm =
                    (MTBReferenceSearchDTOTableModel) fxtblSearchResults.getModel();
            String sKey = ((Integer) fxtm.getValueAt(nRow, 1)).toString();

            // Modal dialog with OK/cancel and a text field
            String strMessage = "Are you sure you would like to permanently "
                    + "delete Reference Key " + sKey + "?";
            int nAnswer =
                    JOptionPane.showConfirmDialog(this, strMessage, "Warning",
                    JOptionPane.YES_NO_OPTION);
            if (nAnswer == JOptionPane.YES_OPTION) {
                delete(sKey);
            } else if (nAnswer == JOptionPane.NO_OPTION) {
                // do nothing
                return;
            }
        }
    }

    /**
     * Delete the <code>Reference</code> and associated values from the
     * database.
     *
     * @param lKey the <code>Reference</code> key
     */
    private void delete(String sKey) {
        ReferenceDAO daoReference = ReferenceDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // delete Reference
            ///////////////////////////////////////////////////////////////////
            daoReference.deleteByPrimaryKey(new Long(sKey));

            ///////////////////////////////////////////////////////////////////
            // delete Accession
            ///////////////////////////////////////////////////////////////////
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_REFERENCE);
            dtoAccession.setObjectKey(new Long(sKey));
            daoAccession.deleteUsingTemplate(dtoAccession);

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            bCommit = true;
        } catch (Exception e) {
            Utils.showErrorDialog(e.getMessage(), e);
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(bCommit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to delete Reference.", e2);
            }
            if (bCommit) {
                Utils.showSuccessDialog("Reference " + sKey
                        + " sucessfully deleted.");
            } else {
                Utils.showErrorDialog("Unable to delete Reference.");
            }
        }
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
        lblReferenceKey = new javax.swing.JLabel();
        comboReferenceKey = new javax.swing.JComboBox();
        lblJNumber = new javax.swing.JLabel();
        lblFirstAuthor = new javax.swing.JLabel();
        lblAuthor = new javax.swing.JLabel();
        lblJournal = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblAuthorContains = new javax.swing.JLabel();
        lblJournalContains = new javax.swing.JLabel();
        lblTitleContains = new javax.swing.JLabel();
        comboFirstAuthor = new javax.swing.JComboBox();
        txtAuthor = new javax.swing.JTextField();
        txtJournal = new javax.swing.JTextField();
        txtTitle = new javax.swing.JTextField();
        txtFirstAuthor = new javax.swing.JTextField();
        btnReset = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        txtReferenceKey = new javax.swing.JTextField();
        txtJNumber = new javax.swing.JTextField();
        headerPanelCriteria = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCheckBoxCoded = new javax.swing.JCheckBox();
        jCheckBoxNotCoded = new javax.swing.JCheckBox();
        jComboBoxOrgan = new javax.swing.JComboBox();
        jTextFieldCodedBy = new javax.swing.JTextField();
        lblJournalContains1 = new javax.swing.JLabel();
        jComboBoxTumorClass = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxPriority = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jCheckBoxIndexed = new javax.swing.JCheckBox();
        jCheckBoxNotIndexed = new javax.swing.JCheckBox();
        jButtonTriage = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jComboDateComparison = new javax.swing.JComboBox();
        jTextCreatedDate = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jTextCodedByDate = new javax.swing.JTextField();
        jComboCodedByDateComparison = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jComboPubYearComparison = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jTextPubYear = new javax.swing.JTextField();
        pnlResults = new javax.swing.JPanel();
        jspResults = new javax.swing.JScrollPane();
        tblResults = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        headerPanelResults = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCriteria.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblReferenceKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblReferenceKey.setText("Reference Key");

        comboReferenceKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        lblJNumber.setText("JNumber");

        lblFirstAuthor.setText("First Author");

        lblAuthor.setText("Author");

        lblJournal.setText("Journal");

        lblTitle.setText("Title");

        lblAuthorContains.setFont(new java.awt.Font("MS Sans Serif", 2, 11)); // NOI18N
        lblAuthorContains.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAuthorContains.setText("contains");

        lblJournalContains.setFont(new java.awt.Font("MS Sans Serif", 2, 11)); // NOI18N
        lblJournalContains.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblJournalContains.setText("contains");

        lblTitleContains.setFont(new java.awt.Font("MS Sans Serif", 2, 11)); // NOI18N
        lblTitleContains.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitleContains.setText("contains");

        comboFirstAuthor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contains", "Equals" }));

        btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Refresh16.png"))); // NOI18N
        btnReset.setText("Reset");
        btnReset.setIconTextGap(2);
        btnReset.setPreferredSize(new java.awt.Dimension(95, 25));
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
        btnSearch.setText("Search");
        btnSearch.setIconTextGap(2);
        btnSearch.setPreferredSize(new java.awt.Dimension(95, 25));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        txtReferenceKey.setColumns(10);

        txtJNumber.setColumns(10);
        txtJNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJNumberActionPerformed(evt);
            }
        });
        txtJNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtJNumberFocusLost(evt);
            }
        });

        headerPanelCriteria.setDrawSeparatorUnderneath(true);
        headerPanelCriteria.setText("Reference Search Criteria");

        jLabel1.setText("Coded By");

        jLabel2.setText("Organ");

        jCheckBoxCoded.setText("Coded");

        jCheckBoxNotCoded.setText("Not Coded");

        jComboBoxOrgan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxOrgan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOrganActionPerformed(evt);
            }
        });

        lblJournalContains1.setFont(new java.awt.Font("MS Sans Serif", 2, 11)); // NOI18N
        lblJournalContains1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblJournalContains1.setText("equals");

        jComboBoxTumorClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Tumor Classification");

        jComboBoxPriority.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Priority");

        jCheckBoxIndexed.setText("Indexed");
        jCheckBoxIndexed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxIndexedActionPerformed(evt);
            }
        });

        jCheckBoxNotIndexed.setText("Not Indexed");

        jButtonTriage.setText("Import References selected for Tumor from MGI");
        jButtonTriage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTriageActionPerformed(evt);
            }
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Created Date");

        jComboDateComparison.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        jComboCodedByDateComparison.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("Coded By Date");

        jComboPubYearComparison.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("Publication Year");

        org.jdesktop.layout.GroupLayout pnlCriteriaLayout = new org.jdesktop.layout.GroupLayout(pnlCriteria);
        pnlCriteria.setLayout(pnlCriteriaLayout);
        pnlCriteriaLayout.setHorizontalGroup(
            pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
            .add(pnlCriteriaLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblJNumber)
                    .add(lblReferenceKey)
                    .add(lblFirstAuthor)
                    .add(lblAuthor)
                    .add(lblJournal)
                    .add(lblTitle)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlCriteriaLayout.createSequentialGroup()
                        .add(jComboBoxPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 129, Short.MAX_VALUE)
                        .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCriteriaLayout.createSequentialGroup()
                        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(pnlCriteriaLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 1, Short.MAX_VALUE)
                                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblJournalContains)
                                    .add(comboReferenceKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(comboFirstAuthor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lblAuthorContains)
                                    .add(lblTitleContains)))
                            .add(lblJournalContains1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtFirstAuthor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                            .add(txtAuthor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                            .add(txtJournal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                            .add(txtTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                            .add(pnlCriteriaLayout.createSequentialGroup()
                                .add(txtReferenceKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jCheckBoxNotIndexed, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                                    .add(jCheckBoxIndexed, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jCheckBoxCoded, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                                    .add(jCheckBoxNotCoded)))
                            .add(pnlCriteriaLayout.createSequentialGroup()
                                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(pnlCriteriaLayout.createSequentialGroup()
                                        .add(jTextFieldCodedBy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                    .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jComboPubYearComparison, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jComboDateComparison, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jComboCodedByDateComparison, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTextCodedByDate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTextCreatedDate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                    .add(jTextPubYear, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)))))
                    .add(jComboBoxTumorClass, 0, 448, Short.MAX_VALUE)
                    .add(jComboBoxOrgan, 0, 448, Short.MAX_VALUE))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteriaLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButtonTriage)
                .add(149, 149, 149))
        );

        pnlCriteriaLayout.linkSize(new java.awt.Component[] {comboFirstAuthor, comboReferenceKey, lblAuthorContains, lblJournalContains, lblTitleContains}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnlCriteriaLayout.setVerticalGroup(
            pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaLayout.createSequentialGroup()
                .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJNumber)
                    .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jCheckBoxIndexed)
                    .add(jCheckBoxCoded))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comboReferenceKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblReferenceKey)
                    .add(txtReferenceKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jCheckBoxNotCoded)
                    .add(jCheckBoxNotIndexed))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblFirstAuthor)
                    .add(comboFirstAuthor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtFirstAuthor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAuthor)
                    .add(lblAuthorContains)
                    .add(txtAuthor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJournal)
                    .add(lblJournalContains)
                    .add(txtJournal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTitle)
                    .add(lblTitleContains)
                    .add(txtTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboPubYearComparison, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8)
                    .add(jTextPubYear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboDateComparison, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(jTextCreatedDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextCodedByDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboCodedByDateComparison, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7)
                    .add(jTextFieldCodedBy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblJournalContains1)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBoxOrgan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlCriteriaLayout.createSequentialGroup()
                        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jComboBoxTumorClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel3))
                        .add(40, 40, 40))
                    .add(pnlCriteriaLayout.createSequentialGroup()
                        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jComboBoxPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButtonTriage)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlResults.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspResults.setViewportView(tblResults);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Delete16.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setPreferredSize(new java.awt.Dimension(95, 25));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Edit16.png"))); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.setPreferredSize(new java.awt.Dimension(95, 25));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        lblStatus.setText("No Results Found");

        headerPanelResults.setDrawSeparatorUnderneath(true);
        headerPanelResults.setText("Reference Search Results");

        org.jdesktop.layout.GroupLayout pnlResultsLayout = new org.jdesktop.layout.GroupLayout(pnlResults);
        pnlResults.setLayout(pnlResultsLayout);
        pnlResultsLayout.setHorizontalGroup(
            pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlResultsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jspResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                    .add(pnlResultsLayout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(lblStatus)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 227, Short.MAX_VALUE)
                        .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(headerPanelResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
        );

        pnlResultsLayout.linkSize(new java.awt.Component[] {btnDelete, btnEdit}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

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
                .add(jspResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
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
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        edit(true);
    }//GEN-LAST:event_btnEditActionPerformed

    private void jButtonTriageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTriageActionPerformed
        getReferences(true);
    }//GEN-LAST:event_jButtonTriageActionPerformed

    private void jCheckBoxIndexedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxIndexedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxIndexedActionPerformed

    private void jComboBoxOrganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxOrganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxOrganActionPerformed

    private void txtJNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberFocusLost
        Utils.fixJNumber(txtJNumber);
    }//GEN-LAST:event_txtJNumberFocusLost

    private void txtJNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJNumberActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        getReferences(false);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        reset();
    }//GEN-LAST:event_btnResetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox comboFirstAuthor;
    private javax.swing.JComboBox comboReferenceKey;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCriteria;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelResults;
    private javax.swing.JButton jButtonTriage;
    private javax.swing.JCheckBox jCheckBoxCoded;
    private javax.swing.JCheckBox jCheckBoxIndexed;
    private javax.swing.JCheckBox jCheckBoxNotCoded;
    private javax.swing.JCheckBox jCheckBoxNotIndexed;
    private javax.swing.JComboBox jComboBoxOrgan;
    private javax.swing.JComboBox jComboBoxPriority;
    private javax.swing.JComboBox jComboBoxTumorClass;
    private javax.swing.JComboBox jComboCodedByDateComparison;
    private javax.swing.JComboBox jComboDateComparison;
    private javax.swing.JComboBox jComboPubYearComparison;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextCodedByDate;
    private javax.swing.JTextField jTextCreatedDate;
    private javax.swing.JTextField jTextFieldCodedBy;
    private javax.swing.JTextField jTextPubYear;
    private javax.swing.JScrollPane jspResults;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblAuthorContains;
    private javax.swing.JLabel lblFirstAuthor;
    private javax.swing.JLabel lblJNumber;
    private javax.swing.JLabel lblJournal;
    private javax.swing.JLabel lblJournalContains;
    private javax.swing.JLabel lblJournalContains1;
    private javax.swing.JLabel lblReferenceKey;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitleContains;
    private javax.swing.JPanel pnlCriteria;
    private javax.swing.JPanel pnlResults;
    private javax.swing.JTable tblResults;
    private javax.swing.JTextField txtAuthor;
    private javax.swing.JTextField txtFirstAuthor;
    private javax.swing.JTextField txtJNumber;
    private javax.swing.JTextField txtJournal;
    private javax.swing.JTextField txtReferenceKey;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
}
