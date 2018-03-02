/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/MarkerPanel.java,v 1.1 2007/04/30 15:50:54 mjv Exp
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
import org.jax.mgi.mtb.dao.custom.SearchResults;
import org.jax.mgi.mtb.dao.custom.mtb.MTBGeneticsUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBSynchronizationUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerLabelDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerLabelDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.editors.LVBeanCellEditor;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.listeners.LVDBeanComboListener;
import org.jax.mgi.mtb.ei.models.MarkerLabelDTOTableModel;
import org.jax.mgi.mtb.ei.models.DTOTableModel;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.models.LVDBeanListModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.renderers.LVDBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.LabelValueDataBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.MXTable;

/**
 * For inserting or updating <b>Marker</b> information and the associated
 * data in the database.
 * 
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/MarkerPanel.java,v 1.1 2007/04/30 15:50:54 mjv Exp
 */
public class MarkerPanel extends CustomPanel {

  // -------------------------------------------------------------- Constants
  /**
   * Used in the constructor to specify this is a new allele.
   */
  public static int MARKER_PANEL_ADD = 1;
  /**
   * Used in the constructor to specify this is an old allele.
   */
  public static int MARKER_PANEL_EDIT = 2;  // ----------------------------------------------------- Instance Variables

  // the allele dto
  private MarkerDTO dtoMarker = null;  // the type of panel
  private int nType = MARKER_PANEL_ADD;  // custom JTables for sorting purposes
  private MXTable fxtblMarkerLabels = null;  // progress monitor
  private MXProgressMonitor progressMonitor = null;
  // ----------------------------------------------------------- Constructors
  /**
   * Creates a new MarkerPanel.
   * <p>
   * If <code>nType = MARKER_PANEL_ADD/code> an insert to the database of
   * the allele object is necessary.  Otherwise, the strain object already
   * exists in the database.
   *
   * @param nType the type of panel, which is either
   *        <code>MARKER_PANEL_ADD</code> or <code>MARKER_PANEL_EDIT</code>
   */
  public MarkerPanel(int nType) {
    this.nType = nType;
    initComponents();
    initCustom();
  }
  // --------------------------------------------------------- Public Methods
  /**
   * Set the allele key for the panel.  This should only be called when the
   * type is of <code>MARKER_PANEL_EDIT</code>, otherwise unknown behavior
   * will occur.
   * <p>
   * <code>Marker</code> data and associated data is retrieved from the
   * database during this method.
   *
   * @param lKey the allele key to be looked up in the database
   */
  public void setKey(final long lKey) {
    Runnable runnable = new Runnable() {

      public void run() {
        progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
        progressMonitor.start("Loading Marker: " + lKey);
        try {
          lookupData(lKey);
        } catch (Exception e) {
          Utils.log(e);
        } finally {
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
      // marker name
      if (!StringUtils.equals(StringUtils.nvl(dtoMarker.getName(), ""), txtName.getText())) {
        System.out.println("marker name changed");
        return true;
      }

      // marker symbol
      if (!StringUtils.equals(StringUtils.nvl(dtoMarker.getSymbol(), ""), txtSymbol.getText())) {
        System.out.println("marker symbol changed");
        return true;
      }

      // marker type
      LabelValueBean<String, Long> beanType = (LabelValueBean<String, Long>) comboMarkerType.getSelectedItem();
      try {
        if (dtoMarker.getMarkerTypeKey().longValue() != beanType.getValue().longValue()) {
          System.out.println("marker type key changed");
          return true;
        }
      } catch (NullPointerException npe) {
      }

      // orgainsm / chromsome
      LabelValueDataBean<String, Long, Long> beanOrganismChromosome =
              (LabelValueDataBean<String, Long, Long>) comboOrgansimChromosome.getSelectedItem();
      try {
        if (dtoMarker.getChromosomeKey().longValue() != beanOrganismChromosome.getValue().longValue()) {
          System.out.println("organism / chromosome changed");
          return true;
        }
      } catch (NullPointerException npe) {
      }

      // marker labels
      if (((DTOTableModel) fxtblMarkerLabels.getModel()).hasBeenUpdated()) {
        System.out.println("marker labels changed");
        return true;
      }

      return false;
    }

    return true;
  }

  /**
   * Save the marker information.
   * <p>
   * Depending upon the type, the marker information will either be updated
   * or inserted.  This is performed in a seperate thread since this could
   * potentially be a lengthy operation. A <code>MXProgressMonitor</code> is
   * used to display visual feedback to the user.
   */
  public void save() {
    // The following code saves the current value in the cell being edited
    // and stops the editing process:
    if (fxtblMarkerLabels.getCellEditor() != null) {
      fxtblMarkerLabels.getCellEditor().stopCellEditing();
    }
    String entrezGene = txtEntrezGene.getText();
    if (entrezGene != null && entrezGene.length() > 0) {

      Long markerKey = entrezGeneExists(entrezGene);
      if ((markerKey != null) && (!markerKey.equals(this.dtoMarker.getMarkerKey()))) {
        Utils.showErrorDialog("Entrez Gene:" + entrezGene + " allready exits for marker with key " + markerKey + ".");
        return;
      }
    }

    Runnable runnable = new Runnable() {

      public void run() {
        progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
        try {
          if (nType == MARKER_PANEL_ADD) {
            progressMonitor.start("Inserting Marker...");
            insertData();
          } else if (nType == MARKER_PANEL_EDIT) {
            progressMonitor.start("Updating Marker...");
            updateData();
          }
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

  private Long entrezGeneExists(String id) {
    Long markerKey = null;
    AccessionDTO dto = AccessionDAO.getInstance().createAccessionDTO();
    dto.setAccID(id);
    dto.setMTBTypesKey(EIConstants.MTB_TYPE_MARKER);
    dto.setSiteInfoKey(EIConstants.SITE_INFO_ENTREZ_GENE);
    try {
      List<AccessionDTO> list = AccessionDAO.getInstance().loadUsingTemplate(dto);
      if (list.size() > 0) {
        markerKey = list.get(0).getObjectKey();
      }
    } catch (Exception e) {
      Utils.log(e);
    }
    return markerKey;

  }

  /**
   * Lookup all allele related information in the database.
   *
   * @param lKey the strain key to be looked up in the database
   */
  private void lookupData(long lKey) {
    MarkerDAO daoMarker = MarkerDAO.getInstance();
    dtoMarker = null;

    try {
      dtoMarker = daoMarker.loadByPrimaryKey(new Long(lKey));
    } catch (Exception e) {
      Utils.log(e);
    }

    txtMarkerKey.setText(dtoMarker.getMarkerKey() + "");
    txtName.setText(dtoMarker.getName());
    txtSymbol.setText(dtoMarker.getSymbol());

    // set the marker type
    for (int i = 0; i < comboMarkerType.getItemCount(); i++) {
      LabelValueBean<String, Long> bean = (LabelValueBean<String, Long>) comboMarkerType.getItemAt(i);

      if (dtoMarker.getMarkerTypeKey().longValue() == bean.getValue().longValue()) {
        comboMarkerType.setSelectedIndex(i);
      }
    }

    // set the organism / chromosome
    for (int i = 0; i < comboOrgansimChromosome.getItemCount(); i++) {
      LabelValueBean<String, Long> bean = (LabelValueBean<String, Long>) comboOrgansimChromosome.getItemAt(i);

      if (dtoMarker.getChromosomeKey().longValue() == bean.getValue().longValue()) {
        comboOrgansimChromosome.setSelectedIndex(i);
      }
    }

    // set the accession info
    AccessionDAO daoAccession = AccessionDAO.getInstance();
    AccessionDTO dtoAcc = null;

    try {
      dtoAcc = daoAccession.createAccessionDTO();
      dtoAcc.setMTBTypesKey(EIConstants.MTB_TYPE_MARKER);
      dtoAcc.setObjectKey(dtoMarker.getMarkerKey());
      dtoAcc.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
      dtoAcc = daoAccession.loadUniqueUsingTemplate(dtoAcc);
      if (dtoAcc != null && dtoAcc.getAccID() != null) {
        txtMGIID.setText(dtoAcc.getAccID());
        dtoMarker.getDataBean().put(MTBSynchronizationUtilDAO.MGI_MARKER_ID, dtoAcc.getAccID());
      }
      // for a possible Entrez Gene ID
      AccessionDTO dtoAcc2 = daoAccession.createAccessionDTO();
      dtoAcc2.setMTBTypesKey(EIConstants.MTB_TYPE_MARKER);
      dtoAcc2.setObjectKey(dtoMarker.getMarkerKey());
      dtoAcc2.setSiteInfoKey(EIConstants.SITE_INFO_ENTREZ_GENE);
      dtoAcc2 = daoAccession.loadUniqueUsingTemplate(dtoAcc2);
      if (dtoAcc2 != null && dtoAcc2.getAccID() != null) {
        txtEntrezGene.setText(dtoAcc2.getAccID());
      }

    } catch (Exception e) {
      Utils.log(e);
    }

    ///////////////////////////////////////////////////////////////////
    // get the marker labels
    ///////////////////////////////////////////////////////////////////
    try {
      MarkerLabelDAO daoMarkerLabel = MarkerLabelDAO.getInstance();
      List<MarkerLabelDTO> arrLabels = daoMarkerLabel.loadByMarkerKey(dtoMarker.getMarkerKey());
      Map<String, LabelValueBean<String, String>> mapMarkerLabelTypes =
              EIGlobals.getInstance().getLabelTypes();

      for (MarkerLabelDTO dto : arrLabels) {
        DataBean sDTO = dto.getDataBean();
        try {
          LabelValueBean<String, String> beanMarkerLabelType =
                  (LabelValueBean<String, String>) mapMarkerLabelTypes.get(
                  dto.getLabelTypeKey());

          sDTO.put(EIConstants.LABEL_TYPE_BEAN, beanMarkerLabelType);
        } catch (Exception e) {
          Utils.log(e);
        }
        dto.setDataBean(sDTO);
      }
      ((MarkerLabelDTOTableModel<MarkerLabelDTO>) fxtblMarkerLabels.getModel()).setData(arrLabels);
    } catch (Exception e) {
      Utils.log(e);
    }
  }

  /**
   * Initialize the MXTable for associated markers.
   * <p>
   * A MXTable is used to provide sorting capabilities.  A
   * <code>DTORenderer</code> is used as the default renderer to provide
   * visual feedback of the state of the data.
   */
  private void initMarkerLabels() {
    Map<String, LabelValueBean<String, String>> mapMarkerLabelTypes =
            EIGlobals.getInstance().getLabelTypes();

    // create the table model
    List<String> vHeader = new ArrayList<String>(2);
    vHeader.add("Label");
    vHeader.add("Label Type");
    List arrLabels = new ArrayList();
    MarkerLabelDTOTableModel<MarkerLabelDTO> tblmdlMarkerLabels =
            new MarkerLabelDTOTableModel<MarkerLabelDTO>(arrLabels, vHeader);
    fxtblMarkerLabels = new MXTable(tblmdlMarkerLabels);
    fxtblMarkerLabels.setModel(tblmdlMarkerLabels);

    // set the table options
    fxtblMarkerLabels.setDefaultRenderer(Object.class, new DTORenderer());
    fxtblMarkerLabels.getColumnModel().getColumn(1).setCellEditor(new LVBeanCellEditor(mapMarkerLabelTypes));
    fxtblMarkerLabels.setAlternateRowHighlight(true);
    fxtblMarkerLabels.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
    fxtblMarkerLabels.setAlternateRowHighlightCount(2);
    fxtblMarkerLabels.setStartHighlightRow(1);
    fxtblMarkerLabels.setSelectionBackground(
            EIConstants.COLOR_RESULTS_SELECTION_BG);
    fxtblMarkerLabels.setSelectionForeground(
            EIConstants.COLOR_RESULTS_SELECTION_FG);
    fxtblMarkerLabels.enableToolTip(0, false);

    // create the synonym delete button
    JButton btnDelete =
            new JButton(new ImageIcon(
            getClass().getResource(EIConstants.ICO_DELETE_16)));
    btnDelete.setIconTextGap(0);
    btnDelete.setMargin(new Insets(0, 0, 0, 0));
    btnDelete.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent evt) {
        removeMarkerLabel();
      }
    });

    // update the JScrollPane
    jspMarkerLabels.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jspMarkerLabels.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelete);
    jspMarkerLabels.setViewportView(fxtblMarkerLabels);

    // revalidate the panel
    pnlMarkerLabels.revalidate();
  }

  /**
   * Perform any custom initialization needed.
   */
  private void initCustom() {
    // make it so the following fields accept numeric input only
    Utils.setNumericFilter(txtMarkerKey);

    // adjust components as needed

    // configure properly
    if (this.nType == MARKER_PANEL_EDIT) {
      txtMarkerKey.setEditable(false);
      checkboxAutoAssign.setEnabled(false);
    } else {
      txtMarkerKey.setEditable(true);
      checkboxAutoAssign.setSelected(true);
    }

    // create the marker dto
    dtoMarker = MarkerDAO.getInstance().createMarkerDTO();

    ///////////////////////////////////////////////////////////////////////
    // marker type
    ///////////////////////////////////////////////////////////////////////
    final Map<Long, LabelValueBean<String, Long>> mapMarkerTypes = EIGlobals.getInstance().getMarkerTypes();
    List<LabelValueBean<String, Long>> arrMarkerTypes = new ArrayList<LabelValueBean<String, Long>>(mapMarkerTypes.values());
    arrMarkerTypes.add(0, new LabelValueBean<String, Long>("--Select--", -1L));
    comboMarkerType.setModel(new LVBeanListModel<String, Long>(arrMarkerTypes));
    comboMarkerType.setRenderer(new LVBeanListCellRenderer<String, Long>());
    comboMarkerType.addKeyListener(new LVBeanComboListener<String, Long>());
    comboMarkerType.setSelectedIndex(0);

    ///////////////////////////////////////////////////////////////////////
    // chromosome
    ///////////////////////////////////////////////////////////////////////
    Map<Long, LabelValueDataBean<String, Long, Long>> mapOrganismChromosomes =
            EIGlobals.getInstance().getOrganismChromosomes();
    List<LabelValueDataBean<String, Long, Long>> arrOrganismChromosomes =
            new ArrayList<LabelValueDataBean<String, Long, Long>>(mapOrganismChromosomes.values());
    arrOrganismChromosomes.add(0,
            new LabelValueDataBean<String, Long, Long>("--Select--", -1L, -1L));
    comboOrgansimChromosome.setModel(
            new LVDBeanListModel<String, Long, Long>(arrOrganismChromosomes));
    comboOrgansimChromosome.setRenderer(new LVDBeanListCellRenderer<String, Long, Long>());
    comboOrgansimChromosome.addKeyListener(new LVDBeanComboListener<String, Long, Long>());
    comboOrgansimChromosome.setSelectedIndex(0);

    ///////////////////////////////////////////////////////////////////////
    // label type
    ///////////////////////////////////////////////////////////////////////
    Map<String, LabelValueBean<String, String>> mapLabelTypes = EIGlobals.getInstance().getLabelTypes();
    List<LabelValueBean<String, String>> arrLabelTypes = new ArrayList<LabelValueBean<String, String>>(mapLabelTypes.values());
    arrLabelTypes.add(0, new LabelValueBean<String, String>("--Select--", "-1"));
    comboMarkerLabelType.setModel(new LVBeanListModel<String, String>(arrLabelTypes));
    comboMarkerLabelType.setRenderer(new LVBeanListCellRenderer<String, String>());
    comboMarkerLabelType.addKeyListener(new LVBeanComboListener<String, String>());
    comboMarkerLabelType.setSelectedIndex(0);


    initMarkerLabels();
  }

  /**
   * Load data from MGI into this panel.
   */
  private void lookupMGI() {
    Runnable runnable = new Runnable() {

      public void run() {
        progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
        try {
          progressMonitor.start("Loading Marker from MGI");
          loadMarkerFromMGI();
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
  }

  private void loadMarkerFromMGI() {
    String mgiid = txtMGIID.getText().trim();

    if (!StringUtils.hasValue(mgiid)) {
      Utils.showErrorDialog("Please enter a MGI ID to retrieve.");
      return;
    }


    MTBGeneticsUtilDAO daoGeneticsUtil =
            MTBGeneticsUtilDAO.getInstance();

    SearchResults res = null;

    try {
      // check for exising marker
      res = daoGeneticsUtil.searchMarker(mgiid, 0,
              null, null, null, null,
              new Long(0), null, "name", -1);

      if ((res.getList() != null) && (res.getList().size() > 0)) {
        MarkerDTO marker = (MarkerDTO) res.getList().get(0);

        Utils.showErrorDialog("Marker " + mgiid + " " + marker.getSymbol() + "\nis in MTB with key " + marker.getMarkerKey());
        return;


      }
    } catch (Exception e) {
      e.printStackTrace();
    }



    try {
      MTBSynchronizationUtilDAO dao = MTBSynchronizationUtilDAO.getInstance();

      dao.setMGIInfo(EIGlobals.getInstance().getMGIUser(),
              EIGlobals.getInstance().getMGIPassword(),
              EIGlobals.getInstance().getMGIDriver(),
              EIGlobals.getInstance().getMGIUrl());



      MarkerDTO mgiDtoMarker = dao.getMarkerFromMGI(mgiid);
      if (mgiDtoMarker == null) {
        Utils.showErrorDialog("Unable to retrieve: " + mgiid);
        return;
      }
      // if editing an existing marker
      // just update the name and symbol from MGI
      if (nType == MARKER_PANEL_EDIT) {
        dtoMarker.setSymbol(mgiDtoMarker.getSymbol());
        dtoMarker.setName(mgiDtoMarker.getName());
        // show the MGI marker type and chrom-orgnaism but don't update the dto
        lblMGIMarkerType.setText((String) mgiDtoMarker.getDataBean().get(MTBSynchronizationUtilDAO.MGI_MARKER_TYPE));
      //      lblMGIChromosome.setText((String)mgiDtoMarker.getDataBean().get(MTBSynchronizationUtilDAO.MGI_CHROMOSOME_ORGANISM));
      } else {
        dtoMarker = mgiDtoMarker;
        lblMGIMarkerType.setText((String) dtoMarker.getDataBean().get(MTBSynchronizationUtilDAO.MGI_MARKER_TYPE));
      //     lblMGIChromosome.setText((String)dtoMarker.getDataBean().get(MTBSynchronizationUtilDAO.MGI_CHROMOSOME_ORGANISM));
      }



      txtName.setText(dtoMarker.getName());
      txtSymbol.setText(dtoMarker.getSymbol());

    } catch (Exception e) {
      Utils.log(e);
      return;
    }
  }

  /**
   * Add a MarkerLabel to the marker label table.
   */
  private void addMarkerLabel() {
    String strMarkerLabel = txtMarkerLabel.getText().trim();
    LabelValueBean beanLabelType =
            (LabelValueBean) comboMarkerLabelType.getSelectedItem();

    // validate that a marker label has been entered
    if (!StringUtils.hasValue(strMarkerLabel)) {
      Utils.showErrorDialog("Please enter a marker label.");
      txtMarkerLabel.requestFocus();
      return;
    }

    // validate that a marker label type has been selected
    if (comboMarkerLabelType.getSelectedIndex() <= 0) {
      Utils.showErrorDialog("Please select a valid marker label type.");
      comboMarkerLabelType.requestFocus();
      return;
    }
    makeLabel(strMarkerLabel, beanLabelType);

  }

  private void createSymbolLabel() {
    MarkerLabelDTOTableModel<MarkerLabelDTO> tblmdlMarkerLabel =
            (MarkerLabelDTOTableModel<MarkerLabelDTO>) fxtblMarkerLabels.getModel();


    List<MarkerLabelDTO> dtos = tblmdlMarkerLabel.getAllData();
    for (MarkerLabelDTO dto : dtos) {
      if (dto.getLabelTypeKey().equals(EIConstants.MARKER_LABEL_TYPE_SYMBOL)) {
        dto.setLabel(txtSymbol.getText());
        fxtblMarkerLabels.repaint();
        return;
      }
    }
    LabelValueBean<String, String> labelType = new LabelValueBean<String, String>();
    labelType.setValue(EIConstants.MARKER_LABEL_TYPE_SYMBOL);
    labelType.setLabel(EIConstants.MARKER_LABEL_SYMBOL);
    makeLabel(txtSymbol.getText(), labelType);

  }

  private void createNameLabel() {
    MarkerLabelDTOTableModel<MarkerLabelDTO> tblmdlMarkerLabel =
            (MarkerLabelDTOTableModel<MarkerLabelDTO>) fxtblMarkerLabels.getModel();


    List<MarkerLabelDTO> dtos = tblmdlMarkerLabel.getAllData();
    for (MarkerLabelDTO dto : dtos) {
      if (dto.getLabelTypeKey().equals(EIConstants.MARKER_LABEL_TYPE_NAME)) {
        dto.setLabel(txtName.getText());
        fxtblMarkerLabels.repaint();
        return;
      }
    }


    LabelValueBean<String, String> labelType = new LabelValueBean<String, String>();
    labelType.setValue(EIConstants.MARKER_LABEL_TYPE_NAME);
    labelType.setLabel(EIConstants.MARKER_LABEL_NAME);
    makeLabel(txtName.getText(), labelType);

  }

  private void makeLabel(String label, LabelValueBean labelType) {

    // get the table model
    MarkerLabelDTOTableModel<MarkerLabelDTO> tblmdlMarkerLabel =
            (MarkerLabelDTOTableModel<MarkerLabelDTO>) fxtblMarkerLabels.getModel();

    // audit trail information
    MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
    Date dNow = new Date();

    // create the dto
    MarkerLabelDTO dtoMarkerLabel =
            MarkerLabelDAO.getInstance().createMarkerLabelDTO();

    dtoMarkerLabel.setMarkerKey(dtoMarker.getMarkerKey());
    dtoMarkerLabel.setLabel(label);
    dtoMarkerLabel.setLabelTypeKey((String) labelType.getValue());
    dtoMarkerLabel.setLabelStatusKey(1l);
    dtoMarkerLabel.setCreateUser(dtoUser.getUserName());
    dtoMarkerLabel.setCreateDate(dNow);
    dtoMarkerLabel.setUpdateUser(dtoUser.getUserName());
    dtoMarkerLabel.setUpdateDate(dNow);

    // set the custom data for the data model to display the correct data
    dtoMarkerLabel.getDataBean().put(EIConstants.LABEL_TYPE_BEAN, labelType);

    // add it to the table
    tblmdlMarkerLabel.addRow(dtoMarkerLabel);

    Utils.scrollToVisible(fxtblMarkerLabels,
            fxtblMarkerLabels.getRowCount() - 1, 0);
  }

  /**
   * Insert the marker information and associated data in the database.
   * <p>
   * This is an all or nothing insert.  Either everything the user has
   * filled in gets committed to the database or nothing does.
   */
  private void insertData() {
    MarkerDAO daoMarker = MarkerDAO.getInstance();
    MarkerLabelDAO daoMarkerLabel = MarkerLabelDAO.getInstance();
    AccessionDAO daoAccession = AccessionDAO.getInstance();

    boolean bCommit = false;

    try {
      ///////////////////////////////////////////////////////////////////
      // Start the Transaction
      ///////////////////////////////////////////////////////////////////
      DAOManagerMTB.getInstance().beginTransaction();

      // audit trail information
      MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
      Date dNow = new Date();

      ///////////////////////////////////////////////////////////////////
      // save the marker
      ///////////////////////////////////////////////////////////////////
      updateProgress("Parsing marker data...");

      boolean bAutoAssign = checkboxAutoAssign.isSelected();
      String strMarkerKeyText = txtMarkerKey.getText();
      long lMarkerKey = -1;

      dtoMarker = daoMarker.createMarkerDTO();

      if (!bAutoAssign) {
        lMarkerKey = Long.parseLong(strMarkerKeyText);
        dtoMarker.setMarkerKey(lMarkerKey);
      }

      dtoMarker.setName(txtName.getText());
      dtoMarker.setSymbol(txtSymbol.getText());

      LabelValueBean<String, Long> beanType = (LabelValueBean<String, Long>) comboMarkerType.getSelectedItem();
      dtoMarker.setMarkerTypeKey(new Long(beanType.getValue()));

      LabelValueDataBean<String, Long, Long> beanOrgansimChromosome =
              (LabelValueDataBean<String, Long, Long>) comboOrgansimChromosome.getSelectedItem();
      dtoMarker.setChromosomeKey(beanOrgansimChromosome.getValue());

      // add the audit trail
      dtoMarker.setCreateUser(dtoUser.getUserName());
      dtoMarker.setCreateDate(dNow);
      dtoMarker.setUpdateUser(dtoUser.getUserName());
      dtoMarker.setUpdateDate(dNow);

      updateProgress("Saving marker data...");
      dtoMarker = daoMarker.save(dtoMarker);
      updateProgress("Marker data saved!");

      ///////////////////////////////////////////////////////////////////
      // save marker labels
      ///////////////////////////////////////////////////////////////////
      updateProgress("Saving marker label data...");

      MarkerLabelDTOTableModel<MarkerLabelDTO> modelMarkerLabels = (MarkerLabelDTOTableModel<MarkerLabelDTO>) fxtblMarkerLabels.getModel();
      List<MarkerLabelDTO> arrMarkerLabels = modelMarkerLabels.getAllData();
      if (arrMarkerLabels != null) {
        for (MarkerLabelDTO dtoTemp : arrMarkerLabels) {
          dtoTemp.setMarkerKey(dtoMarker.getMarkerKey());
        }
        daoMarkerLabel.save(arrMarkerLabels);
      }

      updateProgress("Marker label data saved!");

      ///////////////////////////////////////////////////////////////////
      // save accession information
      ///////////////////////////////////////////////////////////////////
      String strTemp = txtMGIID.getText().trim();

      if (StringUtils.hasValue(strTemp)) {
        updateProgress("Saving marker accession data...");

        AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
        long numericPart = Utils.parseMGIID(strTemp);

        dtoAccession.setAccID(strTemp);
        dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_MARKER);
        dtoAccession.setObjectKey(dtoMarker.getMarkerKey());
        dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
        dtoAccession.setPrefixPart("MGI:");
        dtoAccession.setNumericPart(numericPart);
        dtoAccession.setCreateUser(dtoUser.getUserName());
        dtoAccession.setCreateDate(dNow);
        dtoAccession.setUpdateUser(dtoUser.getUserName());
        dtoAccession.setUpdateDate(dNow);

        dtoAccession = daoAccession.save(dtoAccession);

      }

      // save the Entrez Gene id if there is one
      String entrezGene = this.txtEntrezGene.getText();


      if (entrezGene != null && entrezGene.length() > 0) {

        AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
        long numericPart = new Long(entrezGene).longValue();

        dtoAccession.setAccID(entrezGene);
        dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_MARKER);
        dtoAccession.setObjectKey(dtoMarker.getMarkerKey());
        dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_ENTREZ_GENE);

        dtoAccession.setNumericPart(numericPart);
        dtoAccession.setCreateUser(dtoUser.getUserName());
        dtoAccession.setCreateDate(dNow);
        dtoAccession.setUpdateUser(dtoUser.getUserName());
        dtoAccession.setUpdateDate(dNow);

        dtoAccession = daoAccession.save(dtoAccession);




      }

      updateProgress("Marker accession data saved!");


      ///////////////////////////////////////////////////////////////////
      // COMMIT point reached
      ///////////////////////////////////////////////////////////////////
      updateProgress("All marker data saved!");
      bCommit = true;
    } catch (Exception e) {
      Utils.log(e);
      Utils.showErrorDialog(e.getMessage(), e);
    } finally {
      try {
        ///////////////////////////////////////////////////////////////
        // End the Transaction
        ///////////////////////////////////////////////////////////////
        DAOManagerMTB.getInstance().endTransaction(bCommit);
      } catch (Exception e2) {
        Utils.showErrorDialog("Unable to add Marker.", e2);
      }
      if (bCommit) {
        switchFromAddToEdit();
      } else {
        Utils.showErrorDialog("Unable to add Marker.");
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
    MarkerDAO daoMarker = MarkerDAO.getInstance();
    MarkerLabelDAO daoMarkerLabel = MarkerLabelDAO.getInstance();
    AccessionDAO daoAccession = AccessionDAO.getInstance();

    boolean bCommit = false;

    try {
      ///////////////////////////////////////////////////////////////////
      // Start the Transaction
      ///////////////////////////////////////////////////////////////////
      DAOManagerMTB.getInstance().beginTransaction();

      // audit trail information
      MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
      Date dNow = new Date();

      ///////////////////////////////////////////////////////////////////
      // save the marker label data
      ///////////////////////////////////////////////////////////////////
      updateProgress("Saving marker label data...");

      MarkerLabelDTOTableModel<MarkerLabelDTO> modelMarkerLabels = (MarkerLabelDTOTableModel<MarkerLabelDTO>) fxtblMarkerLabels.getModel();
      List<MarkerLabelDTO> arrMarkerLabels = modelMarkerLabels.getAllData();
      daoMarkerLabel.save(arrMarkerLabels);
      updateProgress("Marker labael data saved!");

      ///////////////////////////////////////////////////////////////////
      // save the marker
      ///////////////////////////////////////////////////////////////////
      updateProgress("Saving marker data...");

      dtoMarker.setName(txtName.getText());
      dtoMarker.setSymbol(txtSymbol.getText());

      LabelValueBean<String, Long> beanType = (LabelValueBean<String, Long>) comboMarkerType.getSelectedItem();
      dtoMarker.setMarkerTypeKey(new Long(beanType.getValue()));

      LabelValueDataBean<String, Long, Long> beanOrgansimChromosome =
              (LabelValueDataBean<String, Long, Long>) comboOrgansimChromosome.getSelectedItem();
      dtoMarker.setChromosomeKey(beanOrgansimChromosome.getValue());

      dtoMarker.setUpdateUser(dtoUser.getUserName());
      dtoMarker.setUpdateDate(dNow);

      daoMarker.save(dtoMarker);

      updateProgress("Marker data saved!");

      ///////////////////////////////////////////////////////////////////
      // save accession information
      ///////////////////////////////////////////////////////////////////
      updateProgress("Saving marker accession data...");

      AccessionDTO dtoAccession = daoAccession.createAccessionDTO();

      // delete the accession info for this allele
      dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_MARKER);
      dtoAccession.setObjectKey(dtoMarker.getMarkerKey());
      dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
      dtoAccession.setPrefixPart("MGI:");

      daoAccession.deleteUsingTemplate(dtoAccession);

      String strTemp = txtMGIID.getText().trim();

      if (StringUtils.hasValue(strTemp)) {
        // create the new accesion info
        dtoAccession = daoAccession.createAccessionDTO();

        long numericPart = Utils.parseMGIID(strTemp);

        dtoAccession.setAccID(strTemp);
        dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_MARKER);
        dtoAccession.setObjectKey(dtoMarker.getMarkerKey());
        dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_MGI);
        dtoAccession.setPrefixPart("MGI:");
        dtoAccession.setNumericPart(numericPart);
        dtoAccession.setCreateUser(dtoUser.getUserName());
        dtoAccession.setCreateDate(dNow);
        dtoAccession.setUpdateUser(dtoUser.getUserName());
        dtoAccession.setUpdateDate(dNow);

        dtoAccession = daoAccession.save(dtoAccession);

      }
      // save the Entrez Gene id if there is one
      String entrezGene = this.txtEntrezGene.getText();


      if (entrezGene != null && entrezGene.length() > 0) {

        dtoAccession = daoAccession.createAccessionDTO();
        long numericPart = new Long(entrezGene).longValue();

        dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_MARKER);
        dtoAccession.setObjectKey(dtoMarker.getMarkerKey());
        dtoAccession.setSiteInfoKey(EIConstants.SITE_INFO_ENTREZ_GENE);

        // remove any existing EntrezGeneID
        daoAccession.deleteUsingTemplate(dtoAccession);

        dtoAccession.setAccID(entrezGene);


        dtoAccession.setNumericPart(numericPart);
        dtoAccession.setCreateUser(dtoUser.getUserName());
        dtoAccession.setCreateDate(dNow);
        dtoAccession.setUpdateUser(dtoUser.getUserName());
        dtoAccession.setUpdateDate(dNow);

        dtoAccession = daoAccession.save(dtoAccession);




      }






      updateProgress("Marker accession data saved!");

      ///////////////////////////////////////////////////////////////////
      // COMMIT point reached
      ///////////////////////////////////////////////////////////////////
      updateProgress("All marker data saved!");
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
        Utils.showErrorDialog("Unable to save changes to Marker.", e2);
      }
      if (bCommit) {
        this.setKey(dtoMarker.getMarkerKey().longValue());
      } else {
        Utils.showErrorDialog("Unable to save changes to Marker.");
      }
    }
  }

  /**
   * Mark a marker from the allele marker association table as to be deleted.
   * <p>
   * The actual association will not be removed until the allele has been
   * saved.
   */
  private void removeMarkerLabel() {
    int nRow = fxtblMarkerLabels.getSelectedRow();

    if (nRow >= 0) {
      MarkerLabelDTOTableModel<MarkerLabelDTO> fxtblLabels =
              (MarkerLabelDTOTableModel<MarkerLabelDTO>) fxtblMarkerLabels.getModel();
      fxtblLabels.removeRow(nRow);
      updated = true;
    }
  }

  /**
   * Simple method to close the add form and switch to the edit form.  The
   * window location is tracked to make it seemless to the end user.
   */
  private void switchFromAddToEdit() {
    customInternalFrame.dispose();
    EIGlobals.getInstance().getMainFrame().launchMarkerEditWindow(
            dtoMarker.getMarkerKey().longValue(),
            customInternalFrame.getLocation());
  }

  private void chromosomeAction() {
    LabelValueDataBean<String, Long, Long> beanOrgansimChromosome =
            (LabelValueDataBean<String, Long, Long>) comboOrgansimChromosome.getSelectedItem();

    if (beanOrgansimChromosome.getData() == 2) {

      lblEntrezGene.setVisible(true);
      txtEntrezGene.setVisible(true);

    } else {
      lblEntrezGene.setVisible(false);
      txtEntrezGene.setVisible(false);
      txtEntrezGene.setText("");
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

    pnlMarkerInformation = new javax.swing.JPanel();
    lblMarkerKey = new javax.swing.JLabel();
    lblMarkerType = new javax.swing.JLabel();
    txtMarkerKey = new javax.swing.JTextField();
    checkboxAutoAssign = new javax.swing.JCheckBox();
    lblMGIMarkerType = new javax.swing.JLabel();
    lblSymbol = new javax.swing.JLabel();
    headerPanelMarker = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    lblMGIID = new javax.swing.JLabel();
    txtMGIID = new javax.swing.JTextField();
    btnLookup = new javax.swing.JButton();
    comboMarkerType = new javax.swing.JComboBox();
    txtSymbol = new javax.swing.JTextField();
    lblName = new javax.swing.JLabel();
    txtName = new javax.swing.JTextField();
    lblChromosome = new javax.swing.JLabel();
    comboOrgansimChromosome = new javax.swing.JComboBox();
    lblEntrezGene = new javax.swing.JLabel();
    txtEntrezGene = new javax.swing.JTextField();
    pnlMarkerLabels = new javax.swing.JPanel();
    lblMarkerLabel = new javax.swing.JLabel();
    txtMarkerLabel = new javax.swing.JTextField();
    lblMarkerLabelType = new javax.swing.JLabel();
    btnAddLabel = new javax.swing.JButton();
    jspMarkerLabels = new javax.swing.JScrollPane();
    tblMarkerLabels = new javax.swing.JTable();
    headerPanelMarkerLabels = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    comboMarkerLabelType = new javax.swing.JComboBox();
    btnCancel = new javax.swing.JButton();
    btnSave = new javax.swing.JButton();

    pnlMarkerInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblMarkerKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
    lblMarkerKey.setText("Marker Key");

    lblMarkerType.setText("Marker Type");

    txtMarkerKey.setColumns(10);
    txtMarkerKey.setEditable(false);

    checkboxAutoAssign.setSelected(true);
    checkboxAutoAssign.setText("Auto Assign");
    checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkboxAutoAssignActionPerformed(evt);
      }
    });

    lblMGIMarkerType.setText(" ");

    lblSymbol.setText("Symbol");

    headerPanelMarker.setDrawSeparatorUnderneath(true);
    headerPanelMarker.setText("Marker Information");

    lblMGIID.setText("MGI ID");

    txtMGIID.setColumns(10);
    txtMGIID.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtMGIIDFocusLost(evt);
      }
    });

    btnLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
    btnLookup.setText("Lookup");
    btnLookup.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnLookupActionPerformed(evt);
      }
    });

    txtSymbol.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtSymbolFocusLost(evt);
      }
    });

    lblName.setText("Name");

    txtName.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtNameFocusLost(evt);
      }
    });

    lblChromosome.setText("Organism / Chrom.");

    comboOrgansimChromosome.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        comboOrgansimChromosomeActionPerformed(evt);
      }
    });

    lblEntrezGene.setText("EntrezGene ID");

    org.jdesktop.layout.GroupLayout pnlMarkerInformationLayout = new org.jdesktop.layout.GroupLayout(pnlMarkerInformation);
    pnlMarkerInformation.setLayout(pnlMarkerInformationLayout);
    pnlMarkerInformationLayout.setHorizontalGroup(
      pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(headerPanelMarker, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
      .add(pnlMarkerInformationLayout.createSequentialGroup()
        .add(10, 10, 10)
        .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(lblEntrezGene)
          .add(lblChromosome)
          .add(lblName)
          .add(lblSymbol)
          .add(lblMarkerType)
          .add(lblMarkerKey))
        .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlMarkerInformationLayout.createSequentialGroup()
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlMarkerInformationLayout.createSequentialGroup()
                .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                  .add(lblMGIMarkerType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                  .add(pnlMarkerInformationLayout.createSequentialGroup()
                    .add(txtMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(4, 4, 4)
                    .add(checkboxAutoAssign)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 83, Short.MAX_VALUE)
                    .add(lblMGIID)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(txtMGIID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(btnLookup)
                    .add(23, 23, 23)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
              .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, txtSymbol)
                .add(org.jdesktop.layout.GroupLayout.LEADING, txtName)
                .add(org.jdesktop.layout.GroupLayout.LEADING, comboOrgansimChromosome, 0, 473, Short.MAX_VALUE))
              .add(comboMarkerType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 472, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
          .add(pnlMarkerInformationLayout.createSequentialGroup()
            .add(4, 4, 4)
            .add(txtEntrezGene, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 158, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    pnlMarkerInformationLayout.setVerticalGroup(
      pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlMarkerInformationLayout.createSequentialGroup()
        .add(headerPanelMarker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(0, 0, 0)
        .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblMarkerKey)
          .add(txtMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(checkboxAutoAssign)
          .add(lblMGIID)
          .add(txtMGIID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnLookup))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblMarkerType)
          .add(comboMarkerType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(lblMGIMarkerType)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblSymbol)
          .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblName)
          .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblChromosome)
          .add(comboOrgansimChromosome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 22, Short.MAX_VALUE)
        .add(pnlMarkerInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblEntrezGene)
          .add(txtEntrezGene, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pnlMarkerLabels.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblMarkerLabel.setText("Marker Label");

    lblMarkerLabelType.setText("Label Type");

    btnAddLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
    btnAddLabel.setText("Add");
    btnAddLabel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAddLabelActionPerformed(evt);
      }
    });

    tblMarkerLabels.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jspMarkerLabels.setViewportView(tblMarkerLabels);

    headerPanelMarkerLabels.setDrawSeparatorUnderneath(true);
    headerPanelMarkerLabels.setText("Marker Labels");

    comboMarkerLabelType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

    org.jdesktop.layout.GroupLayout pnlMarkerLabelsLayout = new org.jdesktop.layout.GroupLayout(pnlMarkerLabels);
    pnlMarkerLabels.setLayout(pnlMarkerLabelsLayout);
    pnlMarkerLabelsLayout.setHorizontalGroup(
      pnlMarkerLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlMarkerLabelsLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlMarkerLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(org.jdesktop.layout.GroupLayout.LEADING, jspMarkerLabels, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
          .add(pnlMarkerLabelsLayout.createSequentialGroup()
            .add(pnlMarkerLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(lblMarkerLabel)
              .add(lblMarkerLabelType))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlMarkerLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlMarkerLabelsLayout.createSequentialGroup()
                .add(comboMarkerLabelType, 0, 441, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnAddLabel))
              .add(txtMarkerLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE))))
        .addContainerGap())
      .add(headerPanelMarkerLabels, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
    );
    pnlMarkerLabelsLayout.setVerticalGroup(
      pnlMarkerLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlMarkerLabelsLayout.createSequentialGroup()
        .add(headerPanelMarkerLabels, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlMarkerLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblMarkerLabel)
          .add(txtMarkerLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlMarkerLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblMarkerLabelType)
          .add(btnAddLabel)
          .add(comboMarkerLabelType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jspMarkerLabels, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        .addContainerGap())
    );

    btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png"))); // NOI18N
    btnCancel.setText("Cancel");
    btnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCancelActionPerformed(evt);
      }
    });

    btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Save16.png"))); // NOI18N
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
      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMarkerInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMarkerLabels, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        .add(pnlMarkerInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlMarkerLabels, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 30, Short.MAX_VALUE)
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
        txtMarkerKey.setEditable(false);
        txtMarkerKey.setText("");
      } else {
        txtMarkerKey.setEditable(true);
      }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnAddLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLabelActionPerformed
      addMarkerLabel();
    }//GEN-LAST:event_btnAddLabelActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
      customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
      save();
    }//GEN-LAST:event_btnSaveActionPerformed

private void comboOrgansimChromosomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboOrgansimChromosomeActionPerformed
  chromosomeAction();
}//GEN-LAST:event_comboOrgansimChromosomeActionPerformed

private void txtSymbolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSymbolFocusLost
  createSymbolLabel();
}//GEN-LAST:event_txtSymbolFocusLost

private void txtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusLost
  createNameLabel();
}//GEN-LAST:event_txtNameFocusLost
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAddLabel;
  private javax.swing.JButton btnCancel;
  private javax.swing.JButton btnLookup;
  private javax.swing.JButton btnSave;
  private javax.swing.JCheckBox checkboxAutoAssign;
  private javax.swing.JComboBox comboMarkerLabelType;
  private javax.swing.JComboBox comboMarkerType;
  private javax.swing.JComboBox comboOrgansimChromosome;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelMarker;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelMarkerLabels;
  private javax.swing.JScrollPane jspMarkerLabels;
  private javax.swing.JLabel lblChromosome;
  private javax.swing.JLabel lblEntrezGene;
  private javax.swing.JLabel lblMGIID;
  private javax.swing.JLabel lblMGIMarkerType;
  private javax.swing.JLabel lblMarkerKey;
  private javax.swing.JLabel lblMarkerLabel;
  private javax.swing.JLabel lblMarkerLabelType;
  private javax.swing.JLabel lblMarkerType;
  private javax.swing.JLabel lblName;
  private javax.swing.JLabel lblSymbol;
  private javax.swing.JPanel pnlMarkerInformation;
  private javax.swing.JPanel pnlMarkerLabels;
  private javax.swing.JTable tblMarkerLabels;
  private javax.swing.JTextField txtEntrezGene;
  private javax.swing.JTextField txtMGIID;
  private javax.swing.JTextField txtMarkerKey;
  private javax.swing.JTextField txtMarkerLabel;
  private javax.swing.JTextField txtName;
  private javax.swing.JTextField txtSymbol;
  // End of variables declaration//GEN-END:variables
}
