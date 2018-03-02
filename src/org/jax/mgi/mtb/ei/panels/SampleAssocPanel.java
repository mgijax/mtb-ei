/*
 * SampleAssocPanel.java
 *
 * Created on May 6, 2010, 9:47 AM
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.String;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import org.jax.mgi.mtb.dao.custom.mtb.MTBSeriesSampleUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.SampleAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.SampleAssocDTO;
import org.jax.mgi.mtb.dao.gen.mtb.SampleDAO;
import org.jax.mgi.mtb.dao.gen.mtb.SampleDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.listeners.LVDBeanComboListener;
import org.jax.mgi.mtb.ei.models.SampleAssocDTOTableModel;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.models.LVDBeanListModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.renderers.LVDBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.LabelValueDataBean;

/**
 *
 * @author  sbn
 */
public class SampleAssocPanel extends CustomPanel {

  private final static Logger log =
          Logger.getLogger(SampleAssocPanel.class.getName());
  private MTBSeriesSampleUtilDAO ssUtilDAO = MTBSeriesSampleUtilDAO.getInstance();
  private SampleDTO sampleDTO = null;

  /** Creates new SampleAssocPanel */
  public SampleAssocPanel() {
    initComponents();
    initCustom();
  }

  /** Creates new SampleAssocPanel loads it using the id  */
  public SampleAssocPanel(String id) {
    initComponents();
    initCustom();
    if (id != null) {
      txtSeriesId.setText(id);
      lookupSample();
    }
  }

  private void removeAssoc() {

    int row = fxtblAssocs.getSelectedRow();
    SampleAssocDTOTableModel tblMdlSampleAssoc =
            (SampleAssocDTOTableModel) fxtblAssocs.getModel();
    tblMdlSampleAssoc.removeRow(row);

  }

  private void initCustom() {

    ArrayList<LabelValueBean<String, Long>> arrMTBTypes = ssUtilDAO.getMTBTypesList();

    comboMTBTypes.setModel(new LVBeanListModel<String, Long>(arrMTBTypes));
    comboMTBTypes.setRenderer(new LVBeanListCellRenderer<String, Long>());
    comboMTBTypes.addKeyListener(new LVBeanComboListener<String, Long>());
    comboMTBTypes.setSelectedIndex(arrMTBTypes.size()-1);

    ArrayList<LabelValueDataBean<String, String, Long>> sites = ssUtilDAO.getSitesList();

    comboSite.setModel(new LVDBeanListModel<String, String, Long>(sites));
    comboSite.setRenderer(new LVDBeanListCellRenderer<String, String, Long>());
    comboSite.addKeyListener(new LVDBeanComboListener<String, String, Long>());
    comboSite.setSelectedIndex(0);

    Vector<String> arrHeaders = new Vector<String>(3);
    arrHeaders.add("MTB Type");
    arrHeaders.add("MTB Key");
    arrHeaders.add("Detail");


    List data = new ArrayList<SampleAssocDTO>();

    SampleAssocDTOTableModel tblMdlSampleAssoc =
            new SampleAssocDTOTableModel(data, arrHeaders);


    fxtblAssocs = new MXTable(data, arrHeaders);
    fxtblAssocs.setModel(tblMdlSampleAssoc);
    fxtblAssocs.setDefaultRenderer(Object.class, new DTORenderer());
    fxtblAssocs.enableToolTip(0, false);
    fxtblAssocs.enableToolTip(1, false);



    JButton btnDelAgent = new JButton(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
    btnDelAgent.setIconTextGap(0);
    btnDelAgent.setMargin(new Insets(0, 0, 0, 0));
    btnDelAgent.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent evt) {
        removeAssoc();
      }
    });
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelAgent);
    jScrollPane1.setViewportView(fxtblAssocs);

    tablePanel.revalidate();

  }

  private void addAssoc() {

    if (sampleDTO == null || sampleDTO.getSampleKey() == null) {
      Utils.showErrorDialog("Please load a Sample before creating associations");
      return;
    }

    LabelValueBean<String, Long> beanType = (LabelValueBean<String, Long>) comboMTBTypes.getSelectedItem();

    Long key = null;

    try {
      key = new Long(txtMtbKey.getText());
    } catch (NumberFormatException nfe) {
      Utils.showErrorDialog("Unable to find " + beanType.getLabel() + " with key " + key);
      return;
    }

    String detail = ssUtilDAO.getDetail(beanType.getValue(), key);
    if (detail == null) {
      Utils.showErrorDialog("Unable to find " + beanType.getLabel() + " with key " + key);
      return;
    }
    String user = EIGlobals.getInstance().getMTBUsersDTO().getUserName();
    Date now = new Date(System.currentTimeMillis());
    SampleAssocDTO dto = SampleAssocDAO.getInstance().createSampleAssocDTO();

    dto.setSampleKey(sampleDTO.getSampleKey());
    dto.setMTBTypesKey(beanType.getValue());
    dto.setObjectKey(key);
    dto.setUpdateUser(user);
    dto.setCreateUser(user);
    dto.setUpdateDate(now);
    dto.setCreateDate(now);

    DataBean bean = new DataBean();
    dto.setDataBean(bean);
    bean.put("MTBType", beanType.getLabel());

    bean.put("Detail", detail);

    ((SampleAssocDTOTableModel) fxtblAssocs.getModel()).addRow(dto);


  }

  private void saveAssocs() {
    SampleAssocDAO dao = SampleAssocDAO.getInstance();
    SampleAssocDTOTableModel model = (SampleAssocDTOTableModel) fxtblAssocs.getModel();
    try {
      dao.save(model.getAllData());
    } catch (Exception e) {
      Utils.showErrorDialog("Unable to save associations", e);
    }

    lookupSample();

  }

  private void lookupSample() {
    sampleDTO = SampleDAO.getInstance().createSampleDTO();
    sampleDTO.setId(txtSeriesId.getText().toUpperCase());  // this may not work in case sensitive searches (postgres)
    try {
      sampleDTO = SampleDAO.getInstance().loadUniqueUsingTemplate(sampleDTO);
      if (sampleDTO == null) {
        throw new Exception("No matching record for id " + txtSeriesId.getText().toUpperCase());
      }
      txtSeriesId.setText(sampleDTO.getId());
      txtAreaTitle.setText(sampleDTO.getTitle());
      txtAreaSummary.setText(sampleDTO.getSummary());
      txtPlatform.setText(sampleDTO.getPlatform());
      this.chkIsControl.setSelected(sampleDTO.getIsControl());
      for (int i = 0; i < comboSite.getItemCount(); i++) {
        LabelValueDataBean<String, String, Long> bean = (LabelValueDataBean<String, String, Long>) comboSite.getItemAt(i);
        if (bean.getData().equals(sampleDTO.getSiteinfoKey())) {
          comboSite.setSelectedIndex(i);
         
        }
      }
       this.txtFieldLink.setText(sampleDTO.getUrl());
      SampleAssocDAO saDAO = SampleAssocDAO.getInstance();

      SampleAssocDTO saDTO = saDAO.createSampleAssocDTO();
      saDTO.setSampleKey(sampleDTO.getSampleKey());

      List<SampleAssocDTO> assocs = SampleAssocDAO.getInstance().loadUsingTemplate(saDTO);
      for (SampleAssocDTO dto : assocs) {
        DataBean bean = new DataBean();
        bean.put("Detail", ssUtilDAO.getDetail(dto.getMTBTypesKey(), dto.getObjectKey()));
        bean.put("MTBType", ssUtilDAO.getMTBType(dto.getMTBTypesKey()));
        dto.setDataBean(bean);
      }


      ((SampleAssocDTOTableModel) fxtblAssocs.getModel()).setData(assocs);


    } catch (Exception e) {
      txtAreaTitle.setText("");
      txtAreaSummary.setText("");
      txtFieldLink.setText("");
      fxtblAssocs.removeAll();
      ((SampleAssocDTOTableModel) fxtblAssocs.getModel()).setData(new ArrayList<SampleAssocDTO>());
      ((SampleAssocDTOTableModel) fxtblAssocs.getModel()).fireTableDataChanged();


      int nAnswer =
              JOptionPane.showConfirmDialog(this, "Do you want to try to load " 
                                            + txtSeriesId.getText().toUpperCase()
                                            + " from GEO", "Sample not in MTB",
                                              JOptionPane.YES_NO_OPTION);
      
      if (nAnswer == JOptionPane.YES_OPTION) {
        loadFromGEO(txtSeriesId.getText().toUpperCase());
      }

    }

  }


  private void loadFromGEO(String id) {
     String geoPath = EIGlobals.getInstance().getGeoPath();
     geoPath = JOptionPane.showInputDialog(this, "Enter the path to the GEO db", geoPath);
     
     EIGlobals.getInstance().setGeoPath(geoPath);
     
     if(geoPath != null && geoPath.length() > 1){
      try {
        ssUtilDAO.loadSampleFromGEO(geoPath, id);
        lookupSample();
      } catch (Exception e) {
        Utils.showErrorDialog(e.getMessage());
      }
     }


  }

  private void addUpdateSample() {
    SampleDAO sDAO = SampleDAO.getInstance();
    Date now = new Date(System.currentTimeMillis());
    String user = EIGlobals.getInstance().getMTBUsersDTO().getUserName();

    if (sampleDTO == null) {
      sampleDTO = sDAO.createSampleDTO();

      sampleDTO.setCreateDate(now);

      sampleDTO.setCreateUser(user);

    }

    sampleDTO.setUpdateDate(now);
    sampleDTO.setUpdateUser(user);

    sampleDTO.setId(this.txtSeriesId.getText());
    LVDBeanListModel<String, String, Long> model = (LVDBeanListModel<String, String, Long>) comboSite.getModel();

    sampleDTO.setSiteinfoKey(model.getSelectedItem().getData());
    sampleDTO.setTitle(txtAreaTitle.getText());
    sampleDTO.setSummary(txtAreaSummary.getText());
    sampleDTO.setPlatform(this.txtPlatform.getText());
    sampleDTO.setIsControl(this.chkIsControl.isSelected());
    sampleDTO.setUrl(txtFieldLink.getText());
    try {
      sDAO.save(sampleDTO);
      
    } catch (Exception e) {
      Utils.showErrorDialog("Unable to save Sample ", e);
    }

  }

  private void openLink() {
    if (txtFieldLink.getText() != null && txtFieldLink.getText().contains("http:")) {
      try {
        // Lookup the javax.jnlp.BasicService object
        BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
        // Invoke the showDocument method
        bs.showDocument(new URL(txtFieldLink.getText()));
      } catch (Exception ue) {
        // Service is not supported
        log.debug(ue);
      }
    }

  }

  private void delete() {

    SampleDAO sDAO = SampleDAO.getInstance();
    try {

      sDAO.getManager().beginTransaction();
      SampleAssocDAO saDAO = SampleAssocDAO.getInstance();
      SampleAssocDTO saDTO = saDAO.createSampleAssocDTO();
      saDTO.setSampleKey(sampleDTO.getSampleKey());
      // delete all associations
      saDAO.deleteUsingTemplate(saDTO);
      //delete sample
      sDAO.deleteByPrimaryKey(sampleDTO.getSampleKey());

      saDAO.getManager().endTransaction(true);

      clearForm();

    } catch (Exception e) {
      try {
        log.error(e);
        sDAO.getManager().endTransaction(false);
      } catch (Exception e2) {
        log.error(e2);
      }
      Utils.showErrorDialog("Unable to delete Sample. It may belong to a series.", e);
    }
  }

  private void clearForm() {

    sampleDTO = null;
    txtSeriesId.setText("");
    txtAreaTitle.setText("");
    txtAreaSummary.setText("");
    txtFieldLink.setText("");
    txtPlatform.setText("");
    chkIsControl.setSelected(false);
    fxtblAssocs.removeAll();
    ((SampleAssocDTOTableModel) fxtblAssocs.getModel()).setData(new ArrayList<SampleAssocDTO>());
    ((SampleAssocDTOTableModel) fxtblAssocs.getModel()).fireTableDataChanged();

  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    mXHeaderPanel1 = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    jLabel1 = new javax.swing.JLabel();
    txtSeriesId = new javax.swing.JTextField();
    btnLookup = new javax.swing.JButton();
    comboMTBTypes = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();
    txtMtbKey = new javax.swing.JTextField();
    jLabel3 = new javax.swing.JLabel();
    btnAddAssoc = new javax.swing.JButton();
    tablePanel = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    fxtblAssocs = new org.jax.mgi.mtb.gui.MXTable();
    btnSave = new javax.swing.JButton();
    btnCancel = new javax.swing.JButton();
    jScrollPane2 = new javax.swing.JScrollPane();
    txtAreaTitle = new javax.swing.JTextArea();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    comboSite = new javax.swing.JComboBox();
    btnAddUpdate = new javax.swing.JButton();
    jScrollPane3 = new javax.swing.JScrollPane();
    txtAreaSummary = new javax.swing.JTextArea();
    jLabel6 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    txtFieldLink = new javax.swing.JTextField();
    jSeparator1 = new javax.swing.JSeparator();
    btnDelete = new javax.swing.JButton();
    btnClear = new javax.swing.JButton();
    txtPlatform = new javax.swing.JTextField();
    jLabel7 = new javax.swing.JLabel();
    chkIsControl = new javax.swing.JCheckBox();

    mXHeaderPanel1.setText("Samples and Associations");

    jLabel1.setText(" Sample Id");

    btnLookup.setText("Lookup");
    btnLookup.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnLookupActionPerformed(evt);
      }
    });

    comboMTBTypes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Organ", "Reference", "Strain", "Tumor Frequency", "Tumor Type" }));
    comboMTBTypes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        comboMTBTypesActionPerformed(evt);
      }
    });

    jLabel2.setText(" MTB Type");

    jLabel3.setText("MTB Key");

    btnAddAssoc.setText("Add");
    btnAddAssoc.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAddAssocActionPerformed(evt);
      }
    });

    jScrollPane1.setViewportView(fxtblAssocs);

    btnSave.setText("Save");
    btnSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSaveActionPerformed(evt);
      }
    });

    btnCancel.setText("Cancel");
    btnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCancelActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout tablePanelLayout = new org.jdesktop.layout.GroupLayout(tablePanel);
    tablePanel.setLayout(tablePanelLayout);
    tablePanelLayout.setHorizontalGroup(
      tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, tablePanelLayout.createSequentialGroup()
        .addContainerGap(533, Short.MAX_VALUE)
        .add(btnSave)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(btnCancel))
    );
    tablePanelLayout.setVerticalGroup(
      tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, tablePanelLayout.createSequentialGroup()
        .addContainerGap()
        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(btnSave)
          .add(btnCancel))
        .addContainerGap())
    );

    txtAreaTitle.setColumns(20);
    txtAreaTitle.setRows(5);
    jScrollPane2.setViewportView(txtAreaTitle);

    jLabel4.setText("  Title");

    jLabel5.setText("  Site");

    btnAddUpdate.setText("Add / Update");
    btnAddUpdate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAddUpdateActionPerformed(evt);
      }
    });

    txtAreaSummary.setColumns(20);
    txtAreaSummary.setRows(5);
    jScrollPane3.setViewportView(txtAreaSummary);

    jLabel6.setText("  Description");

    jLabel8.setText("Link");

    txtFieldLink.setForeground(new java.awt.Color(51, 51, 255));
    txtFieldLink.setBorder(null);
    txtFieldLink.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        txtFieldLinkMouseClicked(evt);
      }
    });

    btnDelete.setText("Delete");
    btnDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnDeleteActionPerformed(evt);
      }
    });

    btnClear.setText("Clear");
    btnClear.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnClearActionPerformed(evt);
      }
    });

    jLabel7.setText("  Platform");

    chkIsControl.setText("Control");

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(mXHeaderPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
        .add(jLabel2)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
        .add(comboMTBTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 203, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 154, Short.MAX_VALUE)
        .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
        .add(txtMtbKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(18, 18, 18)
        .add(btnAddAssoc)
        .addContainerGap())
      .add(layout.createSequentialGroup()
        .addContainerGap()
        .add(tablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
      .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
          .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
              .add(layout.createSequentialGroup()
                .add(txtSeriesId)
                .add(18, 18, 18)
                .add(btnLookup))
              .add(comboSite, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(layout.createSequentialGroup()
                .add(btnAddUpdate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 148, Short.MAX_VALUE)
                .add(btnClear)
                .add(18, 18, 18)
                .add(btnDelete))
              .add(txtFieldLink, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)))
          .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
          .add(layout.createSequentialGroup()
            .add(txtPlatform, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(chkIsControl)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(layout.createSequentialGroup()
        .add(mXHeaderPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(11, 11, 11)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(btnDelete)
          .add(btnClear)
          .add(btnAddUpdate)
          .add(btnLookup)
          .add(txtSeriesId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabel1))
        .add(18, 18, 18)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
            .add(jLabel8)
            .add(comboSite, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jLabel5))
          .add(txtFieldLink, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .add(15, 15, 15)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(jLabel7)
          .add(chkIsControl)
          .add(txtPlatform, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .add(17, 17, 17)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jLabel4)
          .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
          .add(jLabel6))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
            .add(btnAddAssoc)
            .add(txtMtbKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jLabel3))
          .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
            .add(jLabel2)
            .add(comboMTBTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(tablePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

private void btnAddAssocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAssocActionPerformed
  addAssoc();
}//GEN-LAST:event_btnAddAssocActionPerformed

private void comboMTBTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMTBTypesActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_comboMTBTypesActionPerformed

private void btnLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupActionPerformed
  lookupSample();
}//GEN-LAST:event_btnLookupActionPerformed

private void btnAddUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUpdateActionPerformed
  addUpdateSample();
}//GEN-LAST:event_btnAddUpdateActionPerformed

private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
  saveAssocs();
}//GEN-LAST:event_btnSaveActionPerformed

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
  customInternalFrame.dispose();
}//GEN-LAST:event_btnCancelActionPerformed

private void txtFieldLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFieldLinkMouseClicked
  openLink();
}//GEN-LAST:event_txtFieldLinkMouseClicked

private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
  delete();
}//GEN-LAST:event_btnDeleteActionPerformed

private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
  clearForm();
}//GEN-LAST:event_btnClearActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAddAssoc;
  private javax.swing.JButton btnAddUpdate;
  private javax.swing.JButton btnCancel;
  private javax.swing.JButton btnClear;
  private javax.swing.JButton btnDelete;
  private javax.swing.JButton btnLookup;
  private javax.swing.JButton btnSave;
  private javax.swing.JCheckBox chkIsControl;
  private javax.swing.JComboBox comboMTBTypes;
  private javax.swing.JComboBox comboSite;
  private org.jax.mgi.mtb.gui.MXTable fxtblAssocs;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JSeparator jSeparator1;
  private org.jax.mgi.mtb.gui.MXHeaderPanel mXHeaderPanel1;
  private javax.swing.JPanel tablePanel;
  private javax.swing.JTextArea txtAreaSummary;
  private javax.swing.JTextArea txtAreaTitle;
  private javax.swing.JTextField txtFieldLink;
  private javax.swing.JTextField txtMtbKey;
  private javax.swing.JTextField txtPlatform;
  private javax.swing.JTextField txtSeriesId;
  // End of variables declaration//GEN-END:variables
}
