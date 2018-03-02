/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AllelePairSearchPanel.java,v 1.1 2007/04/30 15:50:53 mjv Exp
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
import org.jax.mgi.mtb.dao.custom.mtb.MTBGeneticsAllelePairSearchDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBGeneticsUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AllelePairDAO;
import org.jax.mgi.mtb.dao.gen.mtb.GenotypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorGeneticsDAO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXProgressGlassPane;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;

/**
 * For searching <b>TumorFrequency</b> data.
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AllelePairSearchPanel.java,v 1.1 2007/04/30 15:50:53 mjv Exp
 * @date 2007/04/30 15:50:53
 */
public class AllelePairSearchPanel extends CustomPanel {

  // -------------------------------------------------------------- Constants
  // none

  // ----------------------------------------------------- Instance Variables
  private MXTable fxtblSearchResults = null;
  private MXProgressGlassPane progressGlassPane = null;
  private Vector gV = new Vector();
  // ----------------------------------------------------------- Constructors
  /**
   * Creates a new TumorFrequencySearchPanel
   */
  public AllelePairSearchPanel() {
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
    Utils.setNumericFilter(txtAllelePairKey);
    Utils.setNumericFilter(txtAllele1Key);
    Utils.setNumericFilter(txtAllele2Key);

    // allele types
    final Map<Long, LabelValueBean<String, Long>> mapAlleleTypes =
            EIGlobals.getInstance().getAlleleTypes();
    final List<LabelValueBean<String, Long>> arrAlleleTypes =
            new ArrayList<LabelValueBean<String, Long>>(mapAlleleTypes.values());
    listAlleleTypes.setModel(new LVBeanListModel<String, Long>(arrAlleleTypes));
    listAlleleTypes.setCellRenderer(new LVBeanListCellRenderer<String, Long>());

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
    final SearchResults res = (SearchResults) Worker.post(new Task() {

      public Object run() throws Exception {
        gV = new Vector();

        // determine parameters
        String strTemp = null;
        Object objTemp = null;
        int nAllelePairKey = -1;

        strTemp = (String) comboAllelePairKey.getSelectedItem();
        String strAllelePairKeyCompare = strTemp;

        strTemp = txtAllelePairKey.getText();

        if (StringUtils.hasValue(strTemp)) {
          try {
            nAllelePairKey = Integer.parseInt(strTemp);
          } catch (Exception e) {
          }
        }

        // allele 1
        int nAllele1Key = -1;

        strTemp = (String) comboAllele1Key.getSelectedItem();
        String strAllele1KeyCompare = strTemp;

        strTemp = txtAllele1Key.getText();

        if (StringUtils.hasValue(strTemp)) {
          try {
            nAllele1Key = Integer.parseInt(strTemp);
          } catch (Exception e) {
          }
        }

        // allele 2
        int nAllele2Key = -1;

        strTemp = (String) comboAllele2Key.getSelectedItem();
        String strAllele2KeyCompare = strTemp;

        strTemp = txtAllele2Key.getText();

        if (StringUtils.hasValue(strTemp)) {
          try {
            nAllele2Key = Integer.parseInt(strTemp);
          } catch (Exception e) {
          }
        }

        // allele marker name
        String strAlleleMarkerName = txtAlleleMarkerName.getText();

        // allele marker name comparison
        objTemp = comboAlleleMarkerName.getSelectedItem();
        if (objTemp != null) {
          strTemp = (String) objTemp;
        }
        String strAlleleMarkerNameCompare = strTemp;

        // allele types
        List<String> arrAlleleTypes = null;
        Object[] arrAlleleTypesSelected =
                listAlleleTypes.getSelectedValues();
        if (arrAlleleTypesSelected.length > 0) {
          arrAlleleTypes = new ArrayList<String>();

          for (int i = 0; i < arrAlleleTypesSelected.length; i++) {
            LabelValueBean<String, Long> beanAlleleType =
                    (LabelValueBean<String, Long>) arrAlleleTypesSelected[i];
            arrAlleleTypes.add(beanAlleleType.getValue() + "");
          }
        }

        // create a GeneticsUtil DAO
        MTBGeneticsUtilDAO daoGeneticsUtil =
                MTBGeneticsUtilDAO.getInstance();

        // create the collection of matching allele pairs to return
        SearchResults res = null;

        try {
          // search for allele pairs
          res = daoGeneticsUtil.searchAllelePair(
                  nAllelePairKey, strAllelePairKeyCompare,
                  nAllele1Key, strAllele1KeyCompare,
                  nAllele2Key, strAllele2KeyCompare,
                  strAlleleMarkerName, arrAlleleTypes,
                  checkboxStrain.isSelected(),
                  checkboxTumorFrequency.isSelected(),
                  "name", -1);
        } catch (Exception e) {
          Utils.log("Error searching for allele pairs");
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
    headers.add("Allele Pair Key");
    headers.add("Allele 1 Key");
    headers.add("Allele 1 Symbol");
    headers.add("Allele 1 Type");
    headers.add("Allele 2 Key");
    headers.add("Allele 2 Symbol");
    headers.add("Allele 2 Type");

    Vector data2 = new Vector();
    MXDefaultTableModel fxtm = new MXDefaultTableModel(data2, headers);

    fxtblSearchResults = new MXTable(data2, headers);
    fxtblSearchResults.setModel(fxtm);

    fxtblSearchResults.setColumnSizes(new int[]{90, 70, 0, 0, 70, 0, 0});
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
    fxtblSearchResults.enableToolTip(4, false);

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
          final List<MTBGeneticsAllelePairSearchDTO> arr = new ArrayList<MTBGeneticsAllelePairSearchDTO>(res.getList());
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

            MTBGeneticsAllelePairSearchDTO dtoAllelePair = arr.get(i);

            try {
              Vector v = new Vector();
              v.add(new Integer(
                      dtoAllelePair.getAllelePairKey()));
              v.add(new Integer(dtoAllelePair.getAllele1Key()));
              v.add(StringUtils.nvl(
                      dtoAllelePair.getAllele1Symbol(), ""));
              v.add(StringUtils.nvl(
                      dtoAllelePair.getAllele1Type(), ""));

              Integer iTemp =
                      new Integer(dtoAllelePair.getAllele2Key());

              if (iTemp.intValue() == 0) {
                v.add(null);
              } else {
                v.add(iTemp);
              }

              v.add(StringUtils.nvl(
                      dtoAllelePair.getAllele2Symbol(), ""));
              v.add(StringUtils.nvl(
                      dtoAllelePair.getAllele2Type(), ""));

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
      headers.add("Allele Pair Key");
      headers.add("Allele 1 Key");
      headers.add("Allele 1 Symbol");
      headers.add("Allele 1 Type");
      headers.add("Allele 2 Key");
      headers.add("Allele 2 Symbol");
      headers.add("Allele 2 Type");

      MXDefaultTableModel tm = new MXDefaultTableModel(gV, headers);
      fxtblSearchResults.setModel(tm);

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
      if (progressGlassPane != null) {
        progressGlassPane.setVisible(false);
        progressGlassPane = null;
      }
      customInternalFrame.setGlassPane(compGlassPane);

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
              (MXDefaultTableModel) fxtblSearchResults.getModel();

      EventQueue.invokeLater(new Runnable() {

        public void run() {
          EIGlobals.getInstance().getMainFrame().
                  launchGenotypeEditWindow(
                  ((Integer) tm.getValueAt(nRow, 0)).intValue());
        }
      });
    }
  }

  /**
   * Handle click for the reset button.
   */
  private void reset() {
    String blankText = "";
    comboAllelePairKey.setSelectedIndex(0);
    txtAllelePairKey.setText(blankText);

    comboAllele1Key.setSelectedIndex(0);
    txtAllele1Key.setText(blankText);
    comboAllele2Key.setSelectedIndex(0);
    txtAllele2Key.setText(blankText);

    comboAlleleMarkerName.setSelectedIndex(0);
    txtAlleleMarkerName.setText(blankText);
    listAlleleTypes.clearSelection();

    checkboxStrain.setSelected(false);
    checkboxTumorFrequency.setSelected(false);

    lblStatus.setText(blankText);

    fxtblSearchResults.setModel(new MXDefaultTableModel(
            new Object[][]{},
            new String[]{"Allele Pair Key", "Allele 1 Key",
              "Allele 1 Symbol", "Allele 1 Type",
              "Allele 2 Key", "Allele 2 Symbol",
              "Allele 2 Type"
            }));

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
              (MXDefaultTableModel) fxtblSearchResults.getModel();
      long lKey = 0l;
      try {
        lKey = ((Integer) fxtm.getValueAt(nRow, 0)).longValue();
      } catch (Exception e) {
        Utils.showErrorDialog(e.getMessage(), e);
      }
      // Modal dialog with OK/cancel and a text field
      String strMessage = "Are you sure you would like to permanently " +
              "delete Allele Pair Key " + lKey + "?";
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
    AllelePairDAO daoAllelePair = AllelePairDAO.getInstance();

    GenotypeDAO daoGenotype = GenotypeDAO.getInstance();
    TumorGeneticsDAO daoTumorGenetics =
            TumorGeneticsDAO.getInstance();

    boolean bCommit = false;

    try {
      ///////////////////////////////////////////////////////////////////
      // Start the Transaction
      ///////////////////////////////////////////////////////////////////
      DAOManagerMTB.getInstance().beginTransaction();

      ///////////////////////////////////////////////////////////////////
      // delete allele pair and strain association (genotype)
      ///////////////////////////////////////////////////////////////////
      daoGenotype.deleteByAllelePairKey(new Long(lKey));

      ///////////////////////////////////////////////////////////////////
      // delete the tumor genetic changes
      ///////////////////////////////////////////////////////////////////
      daoTumorGenetics.deleteByAllelePairKey(new Long(lKey));

      ///////////////////////////////////////////////////////////////////
      // delete the allele pair
      ///////////////////////////////////////////////////////////////////
      daoAllelePair.deleteByPrimaryKey(new Long(lKey));

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
        Utils.showErrorDialog("Unable to delete Allele Pair.", e2);
      }
      if (bCommit) {
        Utils.showSuccessDialog("Allele Pair" + lKey + " sucessfully deleted.");
      } else {
        Utils.showErrorDialog("Unable to delete Allele Pair.");
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
        btnReset = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        pnlAllelePairInformation = new javax.swing.JPanel();
        lblAllelePairKey = new javax.swing.JLabel();
        comboAllelePairKey = new javax.swing.JComboBox();
        txtAllelePairKey = new javax.swing.JTextField();
        pnlAlleleInformation = new javax.swing.JPanel();
        lblAllele1Key = new javax.swing.JLabel();
        lblAllele2Key = new javax.swing.JLabel();
        comboAllele1Key = new javax.swing.JComboBox();
        comboAllele2Key = new javax.swing.JComboBox();
        txtAllele1Key = new javax.swing.JTextField();
        txtAllele2Key = new javax.swing.JTextField();
        pnlGeneticInformation = new javax.swing.JPanel();
        lblAlleleMarkerName = new javax.swing.JLabel();
        lblAlleleType = new javax.swing.JLabel();
        comboAlleleMarkerName = new javax.swing.JComboBox();
        jspAlleleTypes = new javax.swing.JScrollPane();
        listAlleleTypes = new javax.swing.JList();
        txtAlleleMarkerName = new javax.swing.JTextField();
        pnlAssociations = new javax.swing.JPanel();
        checkboxStrain = new javax.swing.JCheckBox();
        checkboxTumorFrequency = new javax.swing.JCheckBox();
        pnlSearchResults = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jspSearchResults = new javax.swing.JScrollPane();
        tblSearchResults = new javax.swing.JTable();
        headerPanelSearchResults = new org.jax.mgi.mtb.gui.MXHeaderPanel();

        pnlCriteria.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        headerPanelCriteria.setDrawSeparatorUnderneath(true);
        headerPanelCriteria.setText("Allele Pair Search Criteria");

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

        pnlAllelePairInformation.setBorder(javax.swing.BorderFactory.createTitledBorder("Allele Pair Information"));
        lblAllelePairKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblAllelePairKey.setText("Allele Pair Key");

        comboAllelePairKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        txtAllelePairKey.setColumns(10);

        org.jdesktop.layout.GroupLayout pnlAllelePairInformationLayout = new org.jdesktop.layout.GroupLayout(pnlAllelePairInformation);
        pnlAllelePairInformation.setLayout(pnlAllelePairInformationLayout);
        pnlAllelePairInformationLayout.setHorizontalGroup(
            pnlAllelePairInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAllelePairInformationLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblAllelePairKey)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboAllelePairKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtAllelePairKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(215, Short.MAX_VALUE))
        );
        pnlAllelePairInformationLayout.setVerticalGroup(
            pnlAllelePairInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAllelePairInformationLayout.createSequentialGroup()
                .add(pnlAllelePairInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAllelePairKey)
                    .add(comboAllelePairKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtAllelePairKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlAlleleInformation.setBorder(javax.swing.BorderFactory.createTitledBorder("Allele Information"));
        lblAllele1Key.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblAllele1Key.setText("Allele 1 Key");

        lblAllele2Key.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png")));
        lblAllele2Key.setText("Allele 2 Key");

        comboAllele1Key.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        comboAllele2Key.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", "<", ">" }));

        txtAllele1Key.setColumns(10);

        txtAllele2Key.setColumns(10);

        org.jdesktop.layout.GroupLayout pnlAlleleInformationLayout = new org.jdesktop.layout.GroupLayout(pnlAlleleInformation);
        pnlAlleleInformation.setLayout(pnlAlleleInformationLayout);
        pnlAlleleInformationLayout.setHorizontalGroup(
            pnlAlleleInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAlleleInformationLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlAlleleInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblAllele2Key)
                    .add(lblAllele1Key))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAlleleInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlAlleleInformationLayout.createSequentialGroup()
                        .add(comboAllele1Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtAllele1Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnlAlleleInformationLayout.createSequentialGroup()
                        .add(comboAllele2Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtAllele2Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(227, Short.MAX_VALUE))
        );
        pnlAlleleInformationLayout.setVerticalGroup(
            pnlAlleleInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAlleleInformationLayout.createSequentialGroup()
                .add(pnlAlleleInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAllele1Key)
                    .add(comboAllele1Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtAllele1Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAlleleInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAllele2Key)
                    .add(comboAllele2Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtAllele2Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnlGeneticInformation.setBorder(javax.swing.BorderFactory.createTitledBorder("Genetic Information"));
        lblAlleleMarkerName.setText("Allele / Marker");

        lblAlleleType.setText("Allele Type");

        comboAlleleMarkerName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contains", "Begins", "Equals" }));

        listAlleleTypes.setVisibleRowCount(4);
        jspAlleleTypes.setViewportView(listAlleleTypes);

        org.jdesktop.layout.GroupLayout pnlGeneticInformationLayout = new org.jdesktop.layout.GroupLayout(pnlGeneticInformation);
        pnlGeneticInformation.setLayout(pnlGeneticInformationLayout);
        pnlGeneticInformationLayout.setHorizontalGroup(
            pnlGeneticInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlGeneticInformationLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlGeneticInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblAlleleType)
                    .add(lblAlleleMarkerName))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlGeneticInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlGeneticInformationLayout.createSequentialGroup()
                        .add(comboAlleleMarkerName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtAlleleMarkerName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                    .add(jspAlleleTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGeneticInformationLayout.setVerticalGroup(
            pnlGeneticInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlGeneticInformationLayout.createSequentialGroup()
                .add(pnlGeneticInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAlleleMarkerName)
                    .add(comboAlleleMarkerName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtAlleleMarkerName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlGeneticInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblAlleleType)
                    .add(jspAlleleTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pnlAssociations.setBorder(javax.swing.BorderFactory.createTitledBorder("Strain & Tumor Frequency Associations"));
        checkboxStrain.setText("Restrict search to Allele Pairs associated to Strain records");
        checkboxStrain.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxStrain.setMargin(new java.awt.Insets(0, 0, 0, 0));

        checkboxTumorFrequency.setText("Restrict search to Allele Pairs associated to Tumor Frequency records");
        checkboxTumorFrequency.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxTumorFrequency.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout pnlAssociationsLayout = new org.jdesktop.layout.GroupLayout(pnlAssociations);
        pnlAssociations.setLayout(pnlAssociationsLayout);
        pnlAssociationsLayout.setHorizontalGroup(
            pnlAssociationsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAssociationsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlAssociationsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(checkboxStrain)
                    .add(checkboxTumorFrequency))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        pnlAssociationsLayout.setVerticalGroup(
            pnlAssociationsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAssociationsLayout.createSequentialGroup()
                .add(checkboxStrain)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkboxTumorFrequency)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlCriteriaLayout = new org.jdesktop.layout.GroupLayout(pnlCriteria);
        pnlCriteria.setLayout(pnlCriteriaLayout);
        pnlCriteriaLayout.setHorizontalGroup(
            pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlCriteriaLayout.createSequentialGroup()
                .addContainerGap(275, Short.MAX_VALUE)
                .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnReset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(pnlCriteriaLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlGeneticInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlAssociations, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlAlleleInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlAllelePairInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlCriteriaLayout.setVerticalGroup(
            pnlCriteriaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCriteriaLayout.createSequentialGroup()
                .add(headerPanelCriteria, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAllelePairInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAlleleInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlGeneticInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAssociations, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 11, Short.MAX_VALUE)
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
        headerPanelSearchResults.setText("Allele Pair Search SearchResults");

        org.jdesktop.layout.GroupLayout pnlSearchResultsLayout = new org.jdesktop.layout.GroupLayout(pnlSearchResults);
        pnlSearchResults.setLayout(pnlSearchResultsLayout);
        pnlSearchResultsLayout.setHorizontalGroup(
            pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSearchResultsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSearchResultsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jspSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                    .add(pnlSearchResultsLayout.createSequentialGroup()
                        .add(lblStatus)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 397, Short.MAX_VALUE)
                        .add(btnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(headerPanelSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
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
                .add(jspSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
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
                .add(pnlSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JCheckBox checkboxStrain;
    private javax.swing.JCheckBox checkboxTumorFrequency;
    private javax.swing.JComboBox comboAllele1Key;
    private javax.swing.JComboBox comboAllele2Key;
    private javax.swing.JComboBox comboAlleleMarkerName;
    private javax.swing.JComboBox comboAllelePairKey;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCriteria;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelSearchResults;
    private javax.swing.JScrollPane jspAlleleTypes;
    private javax.swing.JScrollPane jspSearchResults;
    private javax.swing.JLabel lblAllele1Key;
    private javax.swing.JLabel lblAllele2Key;
    private javax.swing.JLabel lblAlleleMarkerName;
    private javax.swing.JLabel lblAllelePairKey;
    private javax.swing.JLabel lblAlleleType;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JList listAlleleTypes;
    private javax.swing.JPanel pnlAlleleInformation;
    private javax.swing.JPanel pnlAllelePairInformation;
    private javax.swing.JPanel pnlAssociations;
    private javax.swing.JPanel pnlCriteria;
    private javax.swing.JPanel pnlGeneticInformation;
    private javax.swing.JPanel pnlSearchResults;
    private javax.swing.JTable tblSearchResults;
    private javax.swing.JTextField txtAllele1Key;
    private javax.swing.JTextField txtAllele2Key;
    private javax.swing.JTextField txtAlleleMarkerName;
    private javax.swing.JTextField txtAllelePairKey;
    // End of variables declaration//GEN-END:variables
}
