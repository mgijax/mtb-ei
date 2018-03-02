/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/StrainSearchPanel.java,v 1.1 2007/04/30 15:50:58 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.jax.mgi.mtb.dao.custom.SearchResults;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainSearchDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.param.StrainSearchParams;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.GenotypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainNotesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainReferencesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainSynonymsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeAssocDAO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.listeners.LVBeanListListener;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXProgressGlassPane;
import org.jax.mgi.mtb.utils.FieldPrinter;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;



/**
 * For searching <b>Strain</b> data.
 * 
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/StrainSearchPanel.java,v 1.1 2007/04/30 15:50:58 mjv Exp
 */
public class StrainSearchPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private MXTable fxtblSearchResults = null;
    private MXProgressGlassPane progressGlassPane = null;
    private Vector gV = new Vector();


    // ----------------------------------------------------------- Constructors

    /**
     * Creates new StrainSearchPanel.
     */
    public StrainSearchPanel() {
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

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Perform any custom initialization needed.
     */
    private void initCustom() {
        
        // make it so the following fields accept numeric input only
        Utils.setNumericFilter(txtStrainKey);

        // initialize the strain types
        final Map<Long,LabelValueBean<String,Long>> mapTypes = EIGlobals.getInstance().getStrainTypes();
        final List<LabelValueBean<String,Long>> arrTypes = new ArrayList<LabelValueBean<String,Long>>(mapTypes.values());

        // set the model, renderer, and key listener
        listStrainTypes.setModel(new LVBeanListModel<String,Long>(arrTypes));
        listStrainTypes.setCellRenderer(new LVBeanListCellRenderer<String,Long>());
        listStrainTypes.addKeyListener(new LVBeanListListener<String,Long>());

        configureSearchResultsTable();

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    /**
     * Search the database for the <code>Reference</code>s based upon the
     * search criteria.
     *
     * @return the <code>SearchResults</code>
     */
    private SearchResults searchDatabase() throws Exception {
        SearchResults res = (SearchResults)Worker.post(new Task() {
            public Object run() throws Exception {
                // determine parameters
                String strTemp = null;
                Object objTemp = null;
                int nStrainKey = -1;

                strTemp = (String)comboStrainKey.getSelectedItem();
                String strStrainKeyCompare = strTemp;

                strTemp = txtStrainKey.getText();

                if (StringUtils.hasValue(strTemp)) {
                    try {
                        nStrainKey = Integer.parseInt(strTemp);
                    } catch (Exception e) {

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
                List arrStrainTypes = null;
                Object[] arrObjTemp = listStrainTypes.getSelectedValues();
                if (arrObjTemp.length > 0) {
                    arrStrainTypes = new ArrayList();

                    for (int i = 0; i < arrObjTemp.length; i++) {
                        LabelValueBean bean = (LabelValueBean)arrObjTemp[i];
                        arrStrainTypes.add(bean.getValue());
                    }
                }

                // genetic name
                String strGenetics = txtGenetics.getText();

                // accession id = JNumber
                String strJNumber = txtJNumber.getText();

                if (StringUtils.hasValue(strJNumber)) {
                    strJNumber = Utils.fixJNumber(strJNumber);
                }

                long lReferenceKey = 0;

                if (StringUtils.hasValue(strJNumber)) {
                    lReferenceKey =
                           EIGlobals.getInstance().getRefByAcc(strJNumber);
                }

                String strJAXMice = txtJAXMice.getText();

                // create the StrainUtil DAO
                MTBStrainUtilDAO daoStrainUtil = MTBStrainUtilDAO.getInstance();

                // create the collection of matching strains to return
                SearchResults res = null;
                try {
                    // search for the strains
                    StrainSearchParams paramsStrain = new StrainSearchParams();
                    paramsStrain.setStrainKey(nStrainKey);
                    paramsStrain.setStrainKeyComparison(strStrainKeyCompare);
                    paramsStrain.setStrainName(strStrainName);
                    paramsStrain.setStrainNameComparison(strStrainNameCompare);
                    paramsStrain.setStrainTypes(arrStrainTypes);
                    paramsStrain.setGeneticName(strGenetics);
                    paramsStrain.setJAXMiceStockNumber(strJAXMice);
                    paramsStrain.setReferenceKey(lReferenceKey);

                    res = daoStrainUtil.searchStrain(paramsStrain,
                            "name", -1);
                } catch (Exception e) {
                    Utils.log("Error searching for strains");
                    Utils.log(e.getMessage());
                    Utils.log(StringUtils.getStackTrace(e));
                }

                return res;
            }
        });

        return res;
    }

    /**
     * Configure the results table.
     */
    private void configureSearchResultsTable() {
        // column headers
        Vector headers = new Vector();
        headers.add("Strain Key");
        headers.add("Strain Name");
        headers.add("Strain Types");
        headers.add("Description");

        Vector data2 = new Vector();
        MXDefaultTableModel fxtm = new MXDefaultTableModel(data2, headers);

        fxtblSearchResults = new MXTable(data2, headers);
        fxtblSearchResults.setModel(fxtm);

        fxtblSearchResults.setColumnSizes(new int[]{80, 100, 0, 0});
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
            final SearchResults res = searchDatabase();

            progressGlassPane.setMessage("Rendering SearchResults...");
            // construct the new table to display the results
            configureSearchResultsTable();

            if (res != null) {
                Object obj = Worker.post(new Task() {
                    public Object run() throws Exception {
                        final List<MTBStrainSearchDTO> arr = new ArrayList<MTBStrainSearchDTO>(res.getList());
                        for (int i = 0; i < arr.size(); i++) {
                            final int row = i;

                            if ((i % 50) == 0) {
                                Thread.sleep(10);
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        lblStatus.setText("Rendering result " +
                                                          row + " of " +
                                                          arr.size());
                                    }
                                });
                            }

                            MTBStrainSearchDTO dto = arr.get(i);

                            Integer key = new Integer((int)dto.getKey());
                            String name = dto.getName();
                            List types = new ArrayList(dto.getTypes());
                            String desc = dto.getDescription();

                            try {
                                Vector v = new Vector();
                                v.add(key);
                                v.add(name);

                                StringBuffer sb = new StringBuffer("");
                                for (int z = 0; z < types.size(); z++) {
                                    sb.append(types.get(z));

                                    if (z < (types.size() - 1)) {
                                        sb.append(',');
                                    }
                                }
                                v.add(sb.toString());
                                v.add(desc);
                                gV.add(v);
                            } catch (Exception e) {
                            }
                        }
                        return "Done";
                    }
                });
            }

            // enable the UI
            btnSearch.setEnabled(true);
            btnReset.setEnabled(true);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);

            lblStatus.setText("Done Searching!");

            // get the results
            Vector<String> headers = new Vector<String>();
            headers.add("Strain Key");
            headers.add("Strain Name");
            headers.add("Strain Types");
            headers.add("Description");

            MXDefaultTableModel tm = new MXDefaultTableModel(gV, headers);
            fxtblSearchResults.setModel(tm);
            fxtblSearchResults.enableToolTip(0, false);

            final int nSearchResults = res.getList().size();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    lblStatus.setText(nSearchResults + " results found.");
                }
            });
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

            customInternalFrame.adjust();
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

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    EIGlobals.getInstance().getMainFrame().
                            launchStrainEditWindow(
                                 ((Integer)tm.getValueAt(nRow, 0)).intValue());
                }
            });
        }
    }

    /**
     * Handle click for the reset button.
     */
    private void reset() {
        comboStrainKey.setSelectedIndex(0);
        txtStrainKey.setText("");
        comboStrainName.setSelectedIndex(0);
        txtStrainName.setText("");
        listStrainTypes.clearSelection();
        txtJAXMice.setText("");
        txtGenetics.setText("");
        txtJNumber.setText("");
        lblStatus.setText("");
        
        fxtblSearchResults.setModel(new MXDefaultTableModel(
                new Object [][] {
        },
                new String [] {
            "Strain Key", "Strain Name", "Strain Types", "Description"
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
                                "delete Strain Key " + lKey + "?";
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
     * Delete the <code>Strain</code> and associated values from the
     * database.
     *
     * @param lKey the <code>Strain</code> key
     */
    private void delete(long lKey) {
        StrainNotesDAO daoNotes = StrainNotesDAO.getInstance();
        StrainReferencesDAO daoRefs = StrainReferencesDAO.getInstance();
        StrainSynonymsDAO daoSynonyms = StrainSynonymsDAO.getInstance();
        StrainTypeAssocDAO daoStrainTypeAssoc = StrainTypeAssocDAO.getInstance();
        GenotypeDAO daoGenotype = GenotypeDAO.getInstance();
        StrainDAO daoStrain = StrainDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        boolean commit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // delete StrainNotes
            ///////////////////////////////////////////////////////////////////
            daoNotes.deleteByStrainKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete StrainReferences
            ///////////////////////////////////////////////////////////////////
            daoRefs.deleteByStrainKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete StrainSynonyms
            ///////////////////////////////////////////////////////////////////
            daoSynonyms.deleteByStrainKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete StrainTypeAssoc
            ///////////////////////////////////////////////////////////////////
            daoStrainTypeAssoc.deleteByStrainKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete Genotype
            ///////////////////////////////////////////////////////////////////
            daoGenotype.deleteByStrainKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete Strain
            ///////////////////////////////////////////////////////////////////
            daoStrain.deleteByPrimaryKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete Accession
            ///////////////////////////////////////////////////////////////////
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_STRAIN);
            dtoAccession.setObjectKey(lKey);
            daoAccession.deleteUsingTemplate(dtoAccession);

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
                Utils.showErrorDialog("Unable to delete Strain.", e2);
            }
            if (commit) {
                Utils.showSuccessDialog("Strain " + lKey +
                                           " sucessfully deleted.");
            } else {
                Utils.showErrorDialog("Unable to delete Strain.");
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
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlCriteria = new javax.swing.JPanel();
        pnlCriteriaStrain = new javax.swing.JPanel();
        lblStrainKey = new javax.swing.JLabel();
        comboStrainKey = new javax.swing.JComboBox();
        lblStrainName = new javax.swing.JLabel();
        comboStrainName = new javax.swing.JComboBox();
        txtStrainName = new javax.swing.JTextField();
        lblStrainType = new javax.swing.JLabel();
        jspStrainTypes = new javax.swing.JScrollPane();
        listStrainTypes = new javax.swing.JList();
        lblJAXMice = new javax.swing.JLabel();
        txtJAXMice = new javax.swing.JTextField();
        txtStrainKey = new javax.swing.JTextField();
        pnlCriteriaGenetics = new javax.swing.JPanel();
        lblGenetics = new javax.swing.JLabel();
        txtGenetics = new javax.swing.JTextField();
        pnlCriteriaReference = new javax.swing.JPanel();
        lblJNumver = new javax.swing.JLabel();
        txtJNumber = new javax.swing.JTextField();
        btnReset = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        headerPanel1 = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        pnlResults = new javax.swing.JPanel();
        jspResults = new javax.swing.JScrollPane();
        tblResults = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        headerPanelResults = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCriteria.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlCriteriaStrain.setBorder(javax.swing.BorderFactory.createTitledBorder("Strain Information"));
        lblStrainKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblStrainKey.setText("Strain Key");

        comboStrainKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        lblStrainName.setText("Strain Name");

        comboStrainName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contains", "Begins", "Equals" }));

        lblStrainType.setText("Strain Type");

        jspStrainTypes.setViewportView(listStrainTypes);

        lblJAXMice.setText("JAX Mice");

        txtJAXMice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtJAXMiceFocusLost(evt);
            }
        });

        txtStrainKey.setColumns(10);

        org.jdesktop.layout.GroupLayout pnlCriteriaStrainLayout = new org.jdesktop.layout.GroupLayout(pnlCriteriaStrain);
        pnlCriteriaStrain.setLayout(pnlCriteriaStrainLayout);
        pnlCriteriaStrainLayout.setHorizontalGroup(
            pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaStrainLayout.createSequentialGroup()
                .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCriteriaStrainLayout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStrainName)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStrainType)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblJAXMice)))
                    .add(pnlCriteriaStrainLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lblStrainKey)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jspStrainTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                    .add(pnlCriteriaStrainLayout.createSequentialGroup()
                        .add(comboStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnlCriteriaStrainLayout.createSequentialGroup()
                        .add(comboStrainName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtStrainName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                    .add(txtJAXMice, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
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
                    .add(pnlCriteriaStrainLayout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(jspStrainTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJAXMice)
                    .add(txtJAXMice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pnlCriteriaGenetics.setBorder(javax.swing.BorderFactory.createTitledBorder("Genetic Information"));
        lblGenetics.setText("Gene or Allele");

        org.jdesktop.layout.GroupLayout pnlCriteriaGeneticsLayout = new org.jdesktop.layout.GroupLayout(pnlCriteriaGenetics);
        pnlCriteriaGenetics.setLayout(pnlCriteriaGeneticsLayout);
        pnlCriteriaGeneticsLayout.setHorizontalGroup(
            pnlCriteriaGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaGeneticsLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblGenetics)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtGenetics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlCriteriaGeneticsLayout.setVerticalGroup(
            pnlCriteriaGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaGeneticsLayout.createSequentialGroup()
                .add(pnlCriteriaGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblGenetics)
                    .add(txtGenetics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pnlCriteriaReference.setBorder(javax.swing.BorderFactory.createTitledBorder("Reference Information"));
        lblJNumver.setText("J Number");

        txtJNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtJNumberFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlCriteriaReferenceLayout = new org.jdesktop.layout.GroupLayout(pnlCriteriaReference);
        pnlCriteriaReference.setLayout(pnlCriteriaReferenceLayout);
        pnlCriteriaReferenceLayout.setHorizontalGroup(
            pnlCriteriaReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaReferenceLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblJNumver)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtJNumber, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlCriteriaReferenceLayout.setVerticalGroup(
            pnlCriteriaReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaReferenceLayout.createSequentialGroup()
                .add(pnlCriteriaReferenceLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJNumver)
                    .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Refresh16.png")));
        btnReset.setText("Reset");
        btnReset.setPreferredSize(new java.awt.Dimension(95, 25));
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png")));
        btnSearch.setText("Search");
        btnSearch.setPreferredSize(new java.awt.Dimension(95, 25));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        headerPanel1.setDrawSeparatorUnderneath(true);
        headerPanel1.setText("Strain Search Criteria");

        org.jdesktop.layout.GroupLayout pnlCriteriaLayout = new org.jdesktop.layout.GroupLayout(pnlCriteria);
        pnlCriteria.setLayout(pnlCriteriaLayout);
        pnlCriteriaLayout.setHorizontalGroup(
            pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteriaLayout.createSequentialGroup()
                        .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnlCriteriaStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteriaReference, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlCriteriaGenetics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .add(headerPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
        );
        pnlCriteriaLayout.setVerticalGroup(
            pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaLayout.createSequentialGroup()
                .add(headerPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaStrain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaGenetics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaReference, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlResults.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspResults.setViewportView(tblResults);

        lblStatus.setText("No Results Found");

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Delete16.png")));
        btnDelete.setText("Delete");
        btnDelete.setPreferredSize(new java.awt.Dimension(95, 25));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Edit16.png")));
        btnEdit.setText("Edit");
        btnEdit.setPreferredSize(new java.awt.Dimension(95, 25));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        headerPanelResults.setDrawSeparatorUnderneath(true);
        headerPanelResults.setText("Strain Search Results");

        org.jdesktop.layout.GroupLayout pnlResultsLayout = new org.jdesktop.layout.GroupLayout(pnlResults);
        pnlResults.setLayout(pnlResultsLayout);
        pnlResultsLayout.setHorizontalGroup(
            pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlResultsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jspResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                    .add(pnlResultsLayout.createSequentialGroup()
                        .add(lblStatus)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 201, Short.MAX_VALUE)
                        .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(headerPanelResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
        );
        pnlResultsLayout.setVerticalGroup(
            pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlResultsLayout.createSequentialGroup()
                .add(headerPanelResults, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblStatus)
                    .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtJAXMiceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJAXMiceFocusLost
        Utils.fixJaxMiceStockNumber(txtJAXMice);
    }//GEN-LAST:event_txtJAXMiceFocusLost

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
    private javax.swing.JComboBox comboStrainKey;
    private javax.swing.JComboBox comboStrainName;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanel1;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelResults;
    private javax.swing.JScrollPane jspResults;
    private javax.swing.JScrollPane jspStrainTypes;
    private javax.swing.JLabel lblGenetics;
    private javax.swing.JLabel lblJAXMice;
    private javax.swing.JLabel lblJNumver;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStrainKey;
    private javax.swing.JLabel lblStrainName;
    private javax.swing.JLabel lblStrainType;
    private javax.swing.JList listStrainTypes;
    private javax.swing.JPanel pnlCriteria;
    private javax.swing.JPanel pnlCriteriaGenetics;
    private javax.swing.JPanel pnlCriteriaReference;
    private javax.swing.JPanel pnlCriteriaStrain;
    private javax.swing.JPanel pnlResults;
    private javax.swing.JTable tblResults;
    private javax.swing.JTextField txtGenetics;
    private javax.swing.JTextField txtJAXMice;
    private javax.swing.JTextField txtJNumber;
    private javax.swing.JTextField txtStrainKey;
    private javax.swing.JTextField txtStrainName;
    // End of variables declaration//GEN-END:variables

}
