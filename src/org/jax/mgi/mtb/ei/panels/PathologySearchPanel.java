/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/PathologySearchPanel.java,v 1.1 2007/04/30 15:50:56 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;


import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.jax.mgi.mtb.dao.custom.SearchResults;
import org.jax.mgi.mtb.dao.custom.mtb.MTBPathologySearchDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBPathologyUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesProbesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorPathologyAssocDAO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.gui.MXProgressGlassPane;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;

/**
 * For searching <b>Pathology</b> data.
 *
 *
 *
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/PathologySearchPanel.java,v 1.1 2007/04/30 15:50:56 mjv Exp
 */
public class PathologySearchPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private MXTable fxtblSearchResults = null;
    private MXProgressGlassPane progressGlassPane = null;
    private Vector gV = new Vector();


    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new PathologySearchPanel.
     */
    public PathologySearchPanel() {
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
        Utils.setNumericFilter(txtPathologyKey);
        Utils.setNumericFilter(txtTumorFrequencyKey);
        Utils.setNumericFilter(txtStrainKey);

        configureSearchResultsTable();

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    /**
     * Search the database for the <code>Pathology</code> records based upon
     * the search criteria.
     *
     * @return the <code>SearchResults</code>
     */
    private SearchResults searchDatabase() throws Exception {
        final SearchResults res = (SearchResults)Worker.post(new Task() {
            public Object run() throws Exception {
                gV = new Vector();

                // determine parameters
                String strTemp = null;
                Object objTemp = null;

                int lPathologyKey = -1;
                strTemp = (String)comboPathologyKey.getSelectedItem();
                String strPathologyKeyCompare = strTemp;

                strTemp = txtPathologyKey.getText();

                if (StringUtils.hasValue(strTemp)) {
                    try {
                        lPathologyKey = Integer.parseInt(strTemp);
                    } catch (Exception e) {

                    }
                }

                // strain key
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

                // tumor frequency key
                int nTumorFrequencyKey = -1;

                strTemp = (String)comboTumorFrequencyKey.getSelectedItem();
                String strTumorFrequencyKeyCompare = strTemp;

                strTemp = txtTumorFrequencyKey.getText();

                if (StringUtils.hasValue(strTemp)) {
                    try {
                        nTumorFrequencyKey = Integer.parseInt(strTemp);
                    } catch (Exception e) {

                    }
                }

                // pathologist and contributor keys = 0 indicate no search criteria
                // -1 is a failed search
                
                // pathologist
                String strPathologist = txtPathologist.getText();
                long lPathologistKey = 0;

                if (StringUtils.hasValue(strPathologist)) {
                    lPathologistKey = -1;
                    lPathologistKey =
                            EIGlobals.getInstance().getRefByAcc(strPathologist);
                }

                // contributor
                String strContributor = txtContributor.getText();
                long lContributorKey = 0;

                if (StringUtils.hasValue(strContributor)) {
                    lContributorKey = -1;
                    lContributorKey =
                            EIGlobals.getInstance().getRefByAcc(strContributor);
                }

                // create the PathologyUtil DAO
                MTBPathologyUtilDAO daoPathologyUtil =
                        MTBPathologyUtilDAO.getInstance();

                // create the collection of matching pathology records
                SearchResults res = null;
                try {
                    res = daoPathologyUtil.searchPathology(lPathologyKey,
                            strPathologyKeyCompare,
                            nStrainKey,
                            strStrainKeyCompare,
                            nTumorFrequencyKey,
                            strTumorFrequencyKeyCompare,
                            (int)lPathologistKey,
                            (int)lContributorKey,
                            null,
                            null,
                            checkboxImages.isSelected(),
                            "name",
                            -1);
                } catch (Exception e) {
                    Utils.log("Error searching for pathology");
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
        Vector<String> headers = new Vector<String>();
        headers.add("Pathology Key");
        headers.add("Pathologist");
        headers.add("Contributor");
        headers.add("Description");
        headers.add("T.F. Key");
        headers.add("Strain Key");
        headers.add("# Images");

        Vector data2 = new Vector();
        MXDefaultTableModel fxtm = new MXDefaultTableModel(data2, headers);

        fxtblSearchResults = new MXTable(data2, headers);
        fxtblSearchResults.setModel(fxtm);

        fxtblSearchResults.setColumnSizes(new int[]{75, 0, 0, 0, 75, 75, 75});
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

        jspSearchResults.setViewportView(fxtblSearchResults);
        pnlSearchResults.revalidate();
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

            Object obj = Worker.post(new Task() {
                public Object run() throws Exception {
                    final List<MTBPathologySearchDTO> arr = new ArrayList<MTBPathologySearchDTO>(res.getList());
                    for (int i = 0; i < arr.size(); i++) {
                        final int row = i;

                        if ((i % 50) == 0) {
                            Thread.sleep(10);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    lblStatus.setText("Rendering result " + row + " of " + arr.size());
                                }
                            });
                        }

                        MTBPathologySearchDTO dtoPathology = arr.get(i);
                        Integer pathologyKey =
                                new Integer(dtoPathology.getPathologyKey());
                        Integer pathologistKey =
                                new Integer(dtoPathology.getPathologistKey());
                        String pathologist = dtoPathology.getPathologist();
                        Integer contributorKey =
                                new Integer(dtoPathology.getContributorKey());
                        String description = dtoPathology.getDescription();
                        String contributor = dtoPathology.getContributor();
                        Integer tfKey = new Integer(dtoPathology.getTfKey());
                        Integer strainKey =
                                new Integer(dtoPathology.getStrainKey());
                        Integer numImages =
                                new Integer(dtoPathology.getNumImages());

                        try {
                            Vector v = new Vector();
                            v.add(pathologyKey);
                            v.add(pathologist);
                            v.add(contributor);
                            v.add(description);
                            v.add(tfKey);
                            v.add(strainKey);
                            v.add(numImages);

                            gV.add(v);
                        } catch (Exception e) {
                        }
                    }
                    return "Done";
                }
            });

            // enable the UI
            btnSearch.setEnabled(true);
            btnReset.setEnabled(true);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);

            lblStatus.setText("Done Searching!");

            // get the results
            Vector<String> headers = new Vector<String>();
            headers.add("Pathology Key");
            headers.add("Pathologist");
            headers.add("Contributor");
            headers.add("Description");
            headers.add("T.F. Key");
            headers.add("Strain Key");
            headers.add("# Images");

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

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    EIGlobals.getInstance().getMainFrame().
                            launchPathologyEditWindow(
                                 ((Integer)tm.getValueAt(nRow, 0)).intValue());
                }
            });
        }
    }

    /**
     * Handle click for the reset button.
     */
    private void reset() {
        String blankText = "";

        comboPathologyKey.setSelectedIndex(0);
        txtPathologyKey.setText(blankText);
        txtPathologist.setText(blankText);
        txtContributor.setText(blankText);
        checkboxImages.setSelected(false);

        comboTumorFrequencyKey.setSelectedIndex(0);
        txtTumorFrequencyKey.setText(blankText);

        comboStrainKey.setSelectedIndex(0);
        txtStrainKey.setText(blankText);

        lblStatus.setText(blankText);

        fxtblSearchResults.setModel(new MXDefaultTableModel(
                new Object [][] {
        },
                new String [] { "Pathology Key", "Pathologist", "Contributor",
                                "Description", "TF Key", "Strain Key", "Images"
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
            int lKey = ((Integer)fxtm.getValueAt(nRow, 0)).intValue();

            // Modal dialog with OK/cancel and a text field
            String strMessage = "Are you sure you would like to permanently " +
                                "delete Pathology Key " + lKey + "?";
            int nAnswer =
                    JOptionPane.showConfirmDialog(this, strMessage, "Warning",
                                                  JOptionPane.YES_NO_OPTION);
            if (nAnswer == JOptionPane.YES_OPTION) {
                delete(lKey);
                fxtm.removeRow(nRow);
            } else if (nAnswer == JOptionPane.NO_OPTION) {
                // do nothing
                return;
            }
        }
    }

    /**
     * Delete the <code>Pathology</code> and associated values from the
     * database.
     *
     * @param lKey the <code>Pathology</code> key
     */
    private void delete(long lKey) {
        PathologyDAO daoPathology = PathologyDAO.getInstance();
        PathologyImagesDAO daoPathologyImages = PathologyImagesDAO.getInstance();
        PathologyImagesProbesDAO daoPathologyImagesProbes = PathologyImagesProbesDAO.getInstance();
        TumorPathologyAssocDAO daoTumorPathologyAssoc = TumorPathologyAssocDAO.getInstance();

        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // delete the tumor pathology association
            ///////////////////////////////////////////////////////////////////
            daoTumorPathologyAssoc.deleteByPathologyKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete the pathology image probes
            ///////////////////////////////////////////////////////////////////
            daoPathologyImagesProbes.deleteByPathologyKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete the pathology images
            ///////////////////////////////////////////////////////////////////
            daoPathologyImages.deleteByPathologyKey(new Long(lKey));
            
            // what about the image files?

            ///////////////////////////////////////////////////////////////////
            // delete the pathology report
            ///////////////////////////////////////////////////////////////////
            daoPathology.deleteByPrimaryKey(lKey);

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
                Utils.showErrorDialog("Unable to delete Pathology Report.", e2);
            }
            if (bCommit) {
                Utils.showSuccessDialog("Pathology Report " + lKey + " sucessfully deleted.");
            } else {
                Utils.showErrorDialog("Unable to delete Pathology Report.");
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
        headerPanelCriteria = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        pnlCriteriaPathology = new javax.swing.JPanel();
        checkboxImages = new javax.swing.JCheckBox();
        lblPathologyKey = new javax.swing.JLabel();
        comboPathologyKey = new javax.swing.JComboBox();
        txtPathologyKey = new javax.swing.JTextField();
        lblPathologist = new javax.swing.JLabel();
        lblContributor = new javax.swing.JLabel();
        txtPathologist = new javax.swing.JTextField();
        txtContributor = new javax.swing.JTextField();
        pnlCriteriaStrain = new javax.swing.JPanel();
        lblStrainKey = new javax.swing.JLabel();
        comboStrainKey = new javax.swing.JComboBox();
        txtStrainKey = new javax.swing.JTextField();
        btnReset = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        pnlCriteriaTumorFrequency = new javax.swing.JPanel();
        lblTumorFrequencyKey = new javax.swing.JLabel();
        comboTumorFrequencyKey = new javax.swing.JComboBox();
        txtTumorFrequencyKey = new javax.swing.JTextField();
        pnlSearchResults = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jspSearchResults = new javax.swing.JScrollPane();
        tblSearchResults = new javax.swing.JTable();
        headerPanelSearchResults = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCriteria.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        headerPanelCriteria.setDrawSeparatorUnderneath(true);
        headerPanelCriteria.setText("Pathology Search Criteria");

        pnlCriteriaPathology.setBorder(javax.swing.BorderFactory.createTitledBorder("Pathology Information"));
        checkboxImages.setText("Must have images");
        checkboxImages.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxImages.setMargin(new java.awt.Insets(0, 0, 0, 0));

        lblPathologyKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblPathologyKey.setText("Pathology Key");

        comboPathologyKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        txtPathologyKey.setColumns(10);

        lblPathologist.setText("Pathologist");

        lblContributor.setText("Contributor");

        txtPathologist.setColumns(10);
        txtPathologist.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPathologistFocusLost(evt);
            }
        });

        txtContributor.setColumns(10);
        txtContributor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtContributorFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlCriteriaPathologyLayout = new org.jdesktop.layout.GroupLayout(pnlCriteriaPathology);
        pnlCriteriaPathology.setLayout(pnlCriteriaPathologyLayout);
        pnlCriteriaPathologyLayout.setHorizontalGroup(
            pnlCriteriaPathologyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaPathologyLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCriteriaPathologyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCriteriaPathologyLayout.createSequentialGroup()
                        .add(pnlCriteriaPathologyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblPathologist)
                            .add(lblPathologyKey)
                            .add(lblContributor))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlCriteriaPathologyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtContributor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pnlCriteriaPathologyLayout.createSequentialGroup()
                                .add(comboPathologyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtPathologyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(txtPathologist, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(checkboxImages))
                .add(109, 109, 109))
        );
        pnlCriteriaPathologyLayout.setVerticalGroup(
            pnlCriteriaPathologyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaPathologyLayout.createSequentialGroup()
                .add(pnlCriteriaPathologyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPathologyKey)
                    .add(comboPathologyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtPathologyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaPathologyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPathologist)
                    .add(txtPathologist, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaPathologyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblContributor)
                    .add(txtContributor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkboxImages)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCriteriaStrain.setBorder(javax.swing.BorderFactory.createTitledBorder("Strain Information"));
        lblStrainKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblStrainKey.setText("Strain Key");

        comboStrainKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        txtStrainKey.setColumns(10);

        org.jdesktop.layout.GroupLayout pnlCriteriaStrainLayout = new org.jdesktop.layout.GroupLayout(pnlCriteriaStrain);
        pnlCriteriaStrain.setLayout(pnlCriteriaStrainLayout);
        pnlCriteriaStrainLayout.setHorizontalGroup(
            pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaStrainLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblStrainKey)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );
        pnlCriteriaStrainLayout.setVerticalGroup(
            pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaStrainLayout.createSequentialGroup()
                .add(pnlCriteriaStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblStrainKey)
                    .add(comboStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
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

        pnlCriteriaTumorFrequency.setBorder(javax.swing.BorderFactory.createTitledBorder("Tumor Frequency Information"));
        lblTumorFrequencyKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblTumorFrequencyKey.setText("Tumor Frequency Key");

        comboTumorFrequencyKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        txtTumorFrequencyKey.setColumns(10);

        org.jdesktop.layout.GroupLayout pnlCriteriaTumorFrequencyLayout = new org.jdesktop.layout.GroupLayout(pnlCriteriaTumorFrequency);
        pnlCriteriaTumorFrequency.setLayout(pnlCriteriaTumorFrequencyLayout);
        pnlCriteriaTumorFrequencyLayout.setHorizontalGroup(
            pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaTumorFrequencyLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblTumorFrequencyKey)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
        );
        pnlCriteriaTumorFrequencyLayout.setVerticalGroup(
            pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaTumorFrequencyLayout.createSequentialGroup()
                .add(pnlCriteriaTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTumorFrequencyKey)
                    .add(comboTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlCriteriaLayout = new org.jdesktop.layout.GroupLayout(pnlCriteria);
        pnlCriteria.setLayout(pnlCriteriaLayout);
        pnlCriteriaLayout.setHorizontalGroup(
            pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
            .add(pnlCriteriaLayout.createSequentialGroup()
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlCriteriaLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCriteriaLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCriteriaStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCriteriaTumorFrequency, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCriteriaPathology, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCriteriaLayout.setVerticalGroup(
            pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaLayout.createSequentialGroup()
                .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaPathology, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaTumorFrequency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCriteriaStrain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlSearchResults.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblStatus.setText("No SearchResults Found");

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Edit16.png")));
        btnEdit.setText("Edit");
        btnEdit.setPreferredSize(new java.awt.Dimension(95, 25));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Delete16.png")));
        btnDelete.setText("Delete");
        btnDelete.setPreferredSize(new java.awt.Dimension(95, 25));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        tblSearchResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspSearchResults.setViewportView(tblSearchResults);

        headerPanelSearchResults.setDrawSeparatorUnderneath(true);
        headerPanelSearchResults.setText("Pathology Search SearchResults");

        org.jdesktop.layout.GroupLayout pnlSearchResultsLayout = new org.jdesktop.layout.GroupLayout(pnlSearchResults);
        pnlSearchResults.setLayout(pnlSearchResultsLayout);
        pnlSearchResultsLayout.setHorizontalGroup(
            pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSearchResultsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jspSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                    .add(pnlSearchResultsLayout.createSequentialGroup()
                        .add(lblStatus)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 308, Short.MAX_VALUE)
                        .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(headerPanelSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
        );
        pnlSearchResultsLayout.setVerticalGroup(
            pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSearchResultsLayout.createSequentialGroup()
                .add(headerPanelSearchResults, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblStatus)
                    .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
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
                .add(pnlSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtContributorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContributorFocusLost
        Utils.fixJNumber(txtContributor);
    }//GEN-LAST:event_txtContributorFocusLost

    private void txtPathologistFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPathologistFocusLost
        Utils.fixJNumber(txtPathologist);
    }//GEN-LAST:event_txtPathologistFocusLost

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
    private javax.swing.JCheckBox checkboxImages;
    private javax.swing.JComboBox comboPathologyKey;
    private javax.swing.JComboBox comboStrainKey;
    private javax.swing.JComboBox comboTumorFrequencyKey;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCriteria;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelSearchResults;
    private javax.swing.JScrollPane jspSearchResults;
    private javax.swing.JLabel lblContributor;
    private javax.swing.JLabel lblPathologist;
    private javax.swing.JLabel lblPathologyKey;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStrainKey;
    private javax.swing.JLabel lblTumorFrequencyKey;
    private javax.swing.JPanel pnlCriteria;
    private javax.swing.JPanel pnlCriteriaPathology;
    private javax.swing.JPanel pnlCriteriaStrain;
    private javax.swing.JPanel pnlCriteriaTumorFrequency;
    private javax.swing.JPanel pnlSearchResults;
    private javax.swing.JTable tblSearchResults;
    private javax.swing.JTextField txtContributor;
    private javax.swing.JTextField txtPathologist;
    private javax.swing.JTextField txtPathologyKey;
    private javax.swing.JTextField txtStrainKey;
    private javax.swing.JTextField txtTumorFrequencyKey;
    // End of variables declaration//GEN-END:variables

}
