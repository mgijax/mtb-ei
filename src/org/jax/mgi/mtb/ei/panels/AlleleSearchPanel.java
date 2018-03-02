/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AlleleSearchPanel.java,v 1.1 2007/04/30 15:50:53 mjv Exp
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
import org.jax.mgi.mtb.dao.custom.mtb.MTBGeneticsUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleMarkerAssocDAO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXProgressGlassPane;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * For searching <b>Allele</b> data.
 * 
 * @author mjv
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AlleleSearchPanel.java,v 1.1 2007/04/30 15:50:53 mjv Exp
 * @date 2007/04/30 15:50:53
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 */
public class AlleleSearchPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    // custom JTable for sorting
    private MXTable fxtblSearchResults = null;

    // for searching effect
    private MXProgressGlassPane progressGlassPane = null;
    

    private Vector gV = new Vector();


    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new ReferenceSearchPanel.
     */
    public AlleleSearchPanel() {
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
        Utils.setNumericFilter(txtAlleleKey);

        // allele types
        final Map<Long,LabelValueBean<String,Long>> mapAlleleTypes = EIGlobals.getInstance().getAlleleTypes();
        List<LabelValueBean<String,Long>> arrAlleleTypes = new ArrayList<LabelValueBean<String,Long>>(mapAlleleTypes.values());
        listTypes.setModel(new LVBeanListModel<String,Long>(arrAlleleTypes));
        listTypes.setCellRenderer(new LVBeanListCellRenderer<String,Long>());

        configureSearchResultsTable();

        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    /**
     * Search the database for the <code>Alleles</code>s based upon the
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
                int nAlleleKey = -1;

                String strMGIID = txtMGIID.getText().trim();

                strTemp = (String)comboAlleleKey.getSelectedItem();
                String strAlleleKeyCompare = strTemp;

                strTemp = txtAlleleKey.getText();

                if (StringUtils.hasValue(strTemp)) {
                    try {
                        nAlleleKey = Integer.parseInt(strTemp);
                    } catch (Exception e) {

                    }
                }

                // allele marker  name
                String strName = txtName.getText().trim();

                // allele marker name comparison
                objTemp = comboName.getSelectedItem();
                if (objTemp != null) {
                    strTemp = (String)objTemp;
                }
               

                // allele types
                List arrAlleleTypes = null;
                Object[] arrAlleleTypesSelected = listTypes.getSelectedValues();
                if (arrAlleleTypesSelected.length > 0) {
                    arrAlleleTypes = new ArrayList();

                    for (int i = 0; i < arrAlleleTypesSelected.length; i++) {
                        LabelValueBean beanType =
                                (LabelValueBean)arrAlleleTypesSelected[i];
                        arrAlleleTypes.add(beanType.getValue());
                    }
                }
                
                String markerKeyCompare = (String) comboMarkerKey.getSelectedItem();
                
                String markerKeyStr = txtMarkerKey.getText();
                int markerKey = 0;
                try{
                  markerKey = new Integer(markerKeyStr).intValue();
                }catch(Exception e){}
                
                // create a GeneticsUtilDAO
                MTBGeneticsUtilDAO daoGeneticsUtil =
                        MTBGeneticsUtilDAO.getInstance();

                SearchResults res = null;

                try {
                    // search for alleles
                    res = daoGeneticsUtil.searchAllele(strMGIID, nAlleleKey,
                            strAlleleKeyCompare, strName, arrAlleleTypes,
                            markerKeyCompare, markerKey,
                            "name", -1);
                } catch (Exception e) {
                    Utils.log("Error searching for alleles.");
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
        headers.add("MGI ID");
        headers.add("Key");
        headers.add("Type");
        headers.add("Allele Symbol");
        headers.add("Allele Name");
        headers.add("Marker Symbol");
        headers.add("Marker Key");

        Vector data = new Vector();
        MXDefaultTableModel rsdtm =
                new MXDefaultTableModel(data, headers);
        fxtblSearchResults = new MXTable(data, headers);
        fxtblSearchResults.setModel(rsdtm);

        fxtblSearchResults.setColumnSizes(new int[]{80, 75, 0, 0, 0, 0, 0});
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
        // disable the UI
        gV = new Vector();
        
        btnSearch.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnReset.setEnabled(false);

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
                    final List<AlleleDTO> arr = new ArrayList<AlleleDTO>(res.getList());
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

                        AlleleDTO dto = arr.get(i);

                        try {
                            Vector v = new Vector();
                            v.add((String)dto.getDataBean().get(MTBGeneticsUtilDAO.ACC_ID));
                            v.add(dto.getAlleleKey());
                            v.add((String)dto.getDataBean().get(MTBGeneticsUtilDAO.ALLELE_TYPE_NAME));
                            v.add(StringUtils.nvl(dto.getSymbol(), ""));
                            v.add(StringUtils.nvl(dto.getName(), ""));
                            v.add(StringUtils.nvl((String)dto.getDataBean().get(MTBGeneticsUtilDAO.MARKER_SYMBOL), ""));
                            v.add((Integer)dto.getDataBean().get(MTBGeneticsUtilDAO.MARKER_KEY));
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
            headers.add("MGI ID");
            headers.add("Key");
            headers.add("Type");
            headers.add("Allele Symbol");
            headers.add("Allele Name");
            headers.add("Marker Symbol");
            headers.add("Marker Key");
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
            btnSearch.setEnabled(true);
            btnReset.setEnabled(true);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);

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
                        launchAlleleEditWindow(
                             ((Long)tm.getValueAt(nRow, 1)).intValue());
                }
            });
        }
    }

    /**
     * Handle click for the reset button.
     */
    private void reset() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                txtMGIID.setText("");
                comboAlleleKey.setSelectedIndex(0);
                txtAlleleKey.setText("");
                comboName.setSelectedIndex(0);
                txtName.setText("");
                listTypes.clearSelection();
                lblStatus.setText("");
                
                txtMarkerKey.setText("");
                comboMarkerKey.setSelectedIndex(0);

                fxtblSearchResults.setModel(new MXDefaultTableModel(
                        new Object [][] {
                },
                        new String [] {
                    "MGI ID", "Key", "Type", "Allele Symbol", "Allele Name", "Marker Symbol", "Marker Key"
                }
                ));
                btnSearch.setEnabled(true);
                btnReset.setEnabled(true);
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });
    }

    /**
     * Handle click for the delete button.
     */
    private void delete() {
        int nRow = fxtblSearchResults.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel fxtm =
                    (MXDefaultTableModel)fxtblSearchResults.getModel();
            long lKey = ((Long)fxtm.getValueAt(nRow, 1)).longValue();

            // Modal dialog with OK/cancel and a text field
            String strMessage = "Are you sure you would like to permanently " +
                                "delete Allele Key " + lKey + "?";
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
     * Delete the <code>Allele</code> and associated values from the
     * database.
     *
     * @param lKey the <code>Allele</code> key
     */
    private void delete(long lKey) {
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
            // delete AlleleMarkerAssociations
            ///////////////////////////////////////////////////////////////////
            daoAMA.deleteByAlleleKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete Allele
            ///////////////////////////////////////////////////////////////////
            daoAllele.deleteByPrimaryKey(new Long(lKey));

            ///////////////////////////////////////////////////////////////////
            // delete Accession
            ///////////////////////////////////////////////////////////////////
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setMTBTypesKey(3l);
            dtoAccession.setObjectKey(lKey);
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
                Utils.showErrorDialog("Unable to delete Allele.", e2);
            }
            if (bCommit) {
                Utils.showSuccessDialog("Allele " + lKey + " sucessfully deleted.");
            } else {
                Utils.showErrorDialog("Unable to delete Allele.");
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
    lblAlleleKey = new javax.swing.JLabel();
    comboAlleleKey = new javax.swing.JComboBox();
    lblMGIID = new javax.swing.JLabel();
    lblName = new javax.swing.JLabel();
    lblAlleleType = new javax.swing.JLabel();
    comboName = new javax.swing.JComboBox();
    txtName = new javax.swing.JTextField();
    btnReset = new javax.swing.JButton();
    btnSearch = new javax.swing.JButton();
    txtAlleleKey = new javax.swing.JTextField();
    txtMGIID = new javax.swing.JTextField();
    headerPanelCriteria = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    jspTypes = new javax.swing.JScrollPane();
    listTypes = new javax.swing.JList();
    comboMarkerKey = new javax.swing.JComboBox();
    jLabel1 = new javax.swing.JLabel();
    txtMarkerKey = new javax.swing.JTextField();
    pnlSearchResults = new javax.swing.JPanel();
    jspSearchResults = new javax.swing.JScrollPane();
    tblResults = new javax.swing.JTable();
    btnDelete = new javax.swing.JButton();
    btnEdit = new javax.swing.JButton();
    lblStatus = new javax.swing.JLabel();
    headerPanelResults = new org.jax.mgi.mtb.gui.MXHeaderPanel();

    pnlCriteria.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblAlleleKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
    lblAlleleKey.setText("Allele Key");

    comboAlleleKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));
    comboAlleleKey.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        comboAlleleKeyActionPerformed(evt);
      }
    });

    lblMGIID.setText("MGI ID");

    lblName.setText("Allele / Marker");

    lblAlleleType.setText("Allele Type");

    comboName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contains", "Equals" }));

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

    txtAlleleKey.setColumns(10);

    txtMGIID.setColumns(10);
    txtMGIID.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtMGIIDFocusLost(evt);
      }
    });

    headerPanelCriteria.setDrawSeparatorUnderneath(true);
    headerPanelCriteria.setText("Allele Search Criteria");

    listTypes.setVisibleRowCount(4);
    jspTypes.setViewportView(listTypes);

    comboMarkerKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

    jLabel1.setText("Marker Key");

    org.jdesktop.layout.GroupLayout pnlCriteriaLayout = new org.jdesktop.layout.GroupLayout(pnlCriteria);
    pnlCriteria.setLayout(pnlCriteriaLayout);
    pnlCriteriaLayout.setHorizontalGroup(
      pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteriaLayout.createSequentialGroup()
        .addContainerGap(326, Short.MAX_VALUE)
        .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
        .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(13, 13, 13))
      .add(pnlCriteriaLayout.createSequentialGroup()
        .add(10, 10, 10)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(lblAlleleType)
          .add(lblName)
          .add(lblAlleleKey)
          .add(lblMGIID)
          .add(jLabel1))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlCriteriaLayout.createSequentialGroup()
            .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(comboAlleleKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(comboName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(txtAlleleKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)))
          .add(txtMGIID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jspTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
          .add(pnlCriteriaLayout.createSequentialGroup()
            .add(comboMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(txtMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );

    pnlCriteriaLayout.linkSize(new java.awt.Component[] {comboAlleleKey, comboName}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    pnlCriteriaLayout.linkSize(new java.awt.Component[] {btnReset, btnSearch}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    pnlCriteriaLayout.setVerticalGroup(
      pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlCriteriaLayout.createSequentialGroup()
        .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblMGIID)
          .add(txtMGIID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(comboAlleleKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtAlleleKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblAlleleKey))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblName)
          .add(comboName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblAlleleType)
          .add(jspTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(jLabel1)
          .add(comboMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .add(22, 22, 22)
        .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pnlSearchResults.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    tblResults.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jspSearchResults.setViewportView(tblResults);

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
    headerPanelResults.setText("Allele Search Results");

    org.jdesktop.layout.GroupLayout pnlSearchResultsLayout = new org.jdesktop.layout.GroupLayout(pnlSearchResults);
    pnlSearchResults.setLayout(pnlSearchResultsLayout);
    pnlSearchResultsLayout.setHorizontalGroup(
      pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlSearchResultsLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jspSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
          .add(pnlSearchResultsLayout.createSequentialGroup()
            .add(4, 4, 4)
            .add(lblStatus)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 285, Short.MAX_VALUE)
            .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
      .add(headerPanelResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
    );

    pnlSearchResultsLayout.linkSize(new java.awt.Component[] {btnDelete, btnEdit}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    pnlSearchResultsLayout.setVerticalGroup(
      pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlSearchResultsLayout.createSequentialGroup()
        .add(headerPanelResults, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblStatus)
          .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jspSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
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
      .add(layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

    private void txtMGIIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMGIIDFocusLost
        Utils.fixMGIID(txtMGIID);
    }//GEN-LAST:event_txtMGIIDFocusLost

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

private void comboAlleleKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAlleleKeyActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_comboAlleleKeyActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnDelete;
  private javax.swing.JButton btnEdit;
  private javax.swing.JButton btnReset;
  private javax.swing.JButton btnSearch;
  private javax.swing.JComboBox comboAlleleKey;
  private javax.swing.JComboBox comboMarkerKey;
  private javax.swing.JComboBox comboName;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCriteria;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelResults;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jspSearchResults;
  private javax.swing.JScrollPane jspTypes;
  private javax.swing.JLabel lblAlleleKey;
  private javax.swing.JLabel lblAlleleType;
  private javax.swing.JLabel lblMGIID;
  private javax.swing.JLabel lblName;
  private javax.swing.JLabel lblStatus;
  private javax.swing.JList listTypes;
  private javax.swing.JPanel pnlCriteria;
  private javax.swing.JPanel pnlSearchResults;
  private javax.swing.JTable tblResults;
  private javax.swing.JTextField txtAlleleKey;
  private javax.swing.JTextField txtMGIID;
  private javax.swing.JTextField txtMarkerKey;
  private javax.swing.JTextField txtName;
  // End of variables declaration//GEN-END:variables

}
