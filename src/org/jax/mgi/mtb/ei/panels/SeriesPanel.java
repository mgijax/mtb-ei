/*
 * DataSetAssocPanel.java
 *
 * Created on May 6, 2010, 9:47 AM
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import org.jax.mgi.mtb.dao.gen.mtb.SampleDAO;
import org.jax.mgi.mtb.dao.gen.mtb.SampleDTO;
import org.jax.mgi.mtb.dao.gen.mtb.SeriesSampleAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.SeriesSampleAssocDTO;
import org.jax.mgi.mtb.dao.gen.mtb.SeriesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.SeriesDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.listeners.LVDBeanComboListener;
import org.jax.mgi.mtb.ei.models.SampleDTOTableModel;
import org.jax.mgi.mtb.ei.models.LVDBeanListModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVDBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueDataBean;

/**
 *
 * @author  sbn
 */
public class SeriesPanel extends CustomPanel {

  private final static Logger log =
          Logger.getLogger(SeriesPanel.class.getName());
  private SeriesDTO seriesDTO = null;  
  
  private MTBSeriesSampleUtilDAO ssUtilDAO = MTBSeriesSampleUtilDAO.getInstance();

  /** Creates new form SeriesPanel */
  public SeriesPanel() {
    initComponents();
    initCustom();
  }

  private void removeSampleAssoc() {

    int row = fxtblAssocs.getSelectedRow();
    SampleDTOTableModel tblMdlSamples =
            (SampleDTOTableModel) fxtblAssocs.getModel();
    SampleDTO dto = (SampleDTO) tblMdlSamples.getDTO(row);
   
    tblMdlSamples.removeRow(row);

  }

  private void initCustom() {

    ArrayList<LabelValueDataBean<String, String, Long>> sites = ssUtilDAO.getSitesList();

    comboSite.setModel(new LVDBeanListModel<String, String, Long>(sites));
    comboSite.setRenderer(new LVDBeanListCellRenderer<String, String, Long>());
    comboSite.addKeyListener(new LVDBeanComboListener<String, String, Long>());
    comboSite.setSelectedIndex(0);

    Vector<String> arrHeaders = new Vector<String>(3);
    arrHeaders.add("Sample ID");
    arrHeaders.add("Title");
    arrHeaders.add("Summary");

    List data = new ArrayList<SampleDTO>();

    SampleDTOTableModel tblMdlSample =
            new SampleDTOTableModel(data, arrHeaders);

    fxtblAssocs = new MXTable(data, arrHeaders);
    fxtblAssocs.setModel(tblMdlSample);
    fxtblAssocs.setDefaultRenderer(Object.class, new DTORenderer());
    fxtblAssocs.enableToolTip(0, false);
    fxtblAssocs.enableToolTip(1, false);

    fxtblAssocs.addMouseListener(new MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          openSample();
        }
      }
    });

    JButton btnDelAgent = new JButton(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
    btnDelAgent.setIconTextGap(0);
    btnDelAgent.setMargin(new Insets(0, 0, 0, 0));
    btnDelAgent.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent evt) {
        removeSampleAssoc();
      }
    });

    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelAgent);
    jScrollPane1.setViewportView(fxtblAssocs);

    tablePanel.revalidate();

  }

  private void openSample() {
    final int nRow = fxtblAssocs.getSelectedRow();

    if (nRow >= 0) {
      final SampleDTOTableModel tm =
              (SampleDTOTableModel) fxtblAssocs.getModel();

      EventQueue.invokeLater(new Runnable() {

        public void run() {
          EIGlobals.getInstance().getMainFrame().
                  launchSampleAssocWindow(
                  ((String) tm.getValueAt(nRow, 0)));
        }
      });
    }
  }

  private void addSampleAssoc() {

    if (seriesDTO == null || seriesDTO.getSeriesKey() == null) {
      Utils.showErrorDialog("Please load an Series before adding Samples");
      return;
    }

    String sampleId = this.txtSampleId.getText();

    SampleDAO aDAO = SampleDAO.getInstance();
    SampleDTO dto = aDAO.createSampleDTO();
    dto.setId(sampleId);
    try {
      dto = aDAO.loadUniqueUsingTemplate(dto);  // this may not work in case sensitive searches (postgres)
    } catch (Exception e) {
    }

    if (dto == null) {
      Utils.showErrorDialog("Unable to find Sample with id " + sampleId + ". Search is case sensitive.");
      return;
    }

    dto.isNew(true);


    ((SampleDTOTableModel) fxtblAssocs.getModel()).addRow(dto);

  }

  
  private void saveSampleAssocs() {
    Date now = new Date(System.currentTimeMillis());
    String user = EIGlobals.getInstance().getMTBUsersDTO().getUserName();

    SeriesSampleAssocDAO dao = SeriesSampleAssocDAO.getInstance();

    SampleDTOTableModel model = (SampleDTOTableModel) fxtblAssocs.getModel();
    try {
      List<SampleDTO> data = model.getAllData();
      for (SampleDTO sDTO : data) {
        if (sDTO.isNew()) {
          SeriesSampleAssocDTO ssADTO = dao.createSeriesSampleAssocDTO();
          ssADTO.setSampleKey(sDTO.getSampleKey());
          ssADTO.setSeriesKey(seriesDTO.getSeriesKey());
          ssADTO.setCreateDate(now);
          ssADTO.setUpdateDate(now);
          ssADTO.setCreateUser(user);
          ssADTO.setUpdateUser(user);

          dao.save(ssADTO);

        } else if (sDTO.isOld()) {
          System.out.println("deleting " + sDTO.getId());
          dao.deleteByPrimaryKey((Long) sDTO.getDataBean().get("assocKey"));
        }
      }


    } catch (Exception e) {
      Utils.showErrorDialog("Unable to save Sample associations", e);
    }

    lookupSeries();

  }

  private void lookupSeries() {
    seriesDTO = SeriesDAO.getInstance().createSeriesDTO();
    seriesDTO.setId(txtSeriesId.getText().toUpperCase());
    try {
      seriesDTO = SeriesDAO.getInstance().loadUniqueUsingTemplate(seriesDTO);  // this may not work in case sensitive searches (postgres)
      if (seriesDTO == null) {
        int nAnswer =
                JOptionPane.showConfirmDialog(this, "Do you want to try to load " + txtSeriesId.getText().toUpperCase() + " from GEO", "Series not in MTB",
                JOptionPane.YES_NO_OPTION);

        if (nAnswer == JOptionPane.YES_OPTION) {
          loadFromGEO(txtSeriesId.getText().toUpperCase());
        }

        return;
      }
      txtSeriesId.setText(seriesDTO.getId());
      txtAreaSummary.setText(seriesDTO.getSummary());
      txtAreaReference.setText(seriesDTO.getReference());
      txtTitle.setText(seriesDTO.getTitle());


      for (int i = 0; i < comboSite.getItemCount(); i++) {
        LabelValueDataBean<String, String, Long> bean =
                (LabelValueDataBean<String, String, Long>) comboSite.getItemAt(i);
        if (bean.getData().equals(seriesDTO.getSiteinfoKey())) {
          comboSite.setSelectedIndex(i);
          this.txtFieldLink.setText(bean.getValue() + seriesDTO.getId());
        }
      }
      SeriesSampleAssocDAO ssADAO = SeriesSampleAssocDAO.getInstance();
      SampleDAO sDAO = SampleDAO.getInstance();

      List<SeriesSampleAssocDTO> assocDTOs =
              ssADAO.loadBySeriesKey(seriesDTO.getSeriesKey());
      List<SampleDTO> samples = new ArrayList<SampleDTO>();

      for (SeriesSampleAssocDTO dto : assocDTOs) {
        SampleDTO sDTO = sDAO.loadByPrimaryKey(dto.getSampleKey());
        DataBean bean = new DataBean();
        bean.put("assocKey", new Long(dto.getSeriessampleassocKey()));
        sDTO.setDataBean(bean);
     
        sDTO.isNew(false); //  don't try to save this
        samples.add(sDTO);

      }

      ((SampleDTOTableModel) fxtblAssocs.getModel()).setData(samples);

    } catch (Exception e) {
      txtAreaSummary.setText("");
      txtAreaReference.setText("");
      txtFieldLink.setText("");
      fxtblAssocs.removeAll();
      ((SampleDTOTableModel) fxtblAssocs.getModel()).setData(new ArrayList<SampleDTO>());
      ((SampleDTOTableModel) fxtblAssocs.getModel()).fireTableDataChanged();

      Utils.showErrorDialog("Unable to lookup Samples for id " + txtSeriesId.getText(), e);
    }

  }

  private void addUpdateSeries() {
    SeriesDAO sDAO = SeriesDAO.getInstance();
    Date now = new Date(System.currentTimeMillis());
    String user = EIGlobals.getInstance().getMTBUsersDTO().getUserName();

    if (seriesDTO == null) {
      seriesDTO = sDAO.createSeriesDTO();
      seriesDTO.setCreateDate(now);
      seriesDTO.setCreateUser(user);

    }

    seriesDTO.setUpdateDate(now);
    seriesDTO.setUpdateUser(user);
    seriesDTO.setId(this.txtSeriesId.getText());
    seriesDTO.setSummary(txtAreaSummary.getText());
    seriesDTO.setReference(txtAreaReference.getText());
    seriesDTO.setTitle(txtTitle.getText());

    LVDBeanListModel<String, String, Long> model = (LVDBeanListModel<String, String, Long>) comboSite.getModel();
    seriesDTO.setSiteinfoKey(model.getSelectedItem().getData());

    try {
      sDAO.save(seriesDTO);
      txtFieldLink.setText(model.getSelectedItem().getValue() + seriesDTO.getId());
    } catch (Exception e) {
      Utils.showErrorDialog("Unable to save Series ", e);
    }

  }

  private void loadFromGEO(String id) {

    String geoPath = EIGlobals.getInstance().getGeoPath();
    geoPath = JOptionPane.showInputDialog(this, "Enter the path and file name for the GEO .sqlite file", geoPath);

    EIGlobals.getInstance().setGeoPath(geoPath);

    if (geoPath != null && geoPath.length() > 1) {


      try {
        ssUtilDAO.loadSeriesFromGEO(geoPath, id);
        lookupSeries();
      } catch (Exception e) {
        Utils.showErrorDialog(e.getMessage());
      }

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

    long[] sampleKeys = new long[0];
    SeriesDAO sDAO = SeriesDAO.getInstance();
    try {

      sDAO.getManager().beginTransaction();
      SeriesSampleAssocDAO ssaDAO = SeriesSampleAssocDAO.getInstance();
      SeriesSampleAssocDTO ssaDTO = ssaDAO.createSeriesSampleAssocDTO();
      ssaDTO.setSeriesKey(seriesDTO.getSeriesKey());
     
      // need to get a list of all assoicated samples to possibly delete
      List<SeriesSampleAssocDTO> assocs = ssaDAO.loadUsingTemplate(ssaDTO);
      sampleKeys = new long[assocs.size()];
      int i = 0;
      for(SeriesSampleAssocDTO dto : assocs){
          sampleKeys[i] = dto.getSampleKey();
          i++;
      }
      
      ssaDAO.deleteUsingTemplate(ssaDTO);
      sDAO.deleteByPrimaryKey(seriesDTO.getSeriesKey());

      sDAO.getManager().endTransaction(true);

      clearForm();

    } catch (Exception e) {
      try {
        log.error(e);
        sDAO.getManager().endTransaction(false);
      } catch (Exception e2) {
        log.error(e2);
      }
      Utils.showErrorDialog("Unable to delete Series and Sample assocations", e);
    }
    // remove any orphaned samples
    ssUtilDAO.cleanUpSamples(sampleKeys);
  }

  private void clearForm() {

    seriesDTO = null;
    txtSeriesId.setText("");
    txtAreaSummary.setText("");
    txtTitle.setText("");
    txtAreaReference.setText("");
    txtFieldLink.setText("");
    txtSampleId.setText("");
    fxtblAssocs.removeAll();
    ((SampleDTOTableModel) fxtblAssocs.getModel()).setData(new ArrayList<SampleDTO>());
    ((SampleDTOTableModel) fxtblAssocs.getModel()).fireTableDataChanged();

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
        txtSampleId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnAddSample = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fxtblAssocs = new org.jax.mgi.mtb.gui.MXTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaSummary = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        comboSite = new javax.swing.JComboBox();
        btnAddUpdate = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaReference = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtFieldLink = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        txtTitle = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDeleteAssoc = new javax.swing.JButton();

        mXHeaderPanel1.setText("Series");

        jLabel1.setText("Series Id");

        btnLookup.setText("Lookup");
        btnLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupActionPerformed(evt);
            }
        });

        jLabel3.setText("Sample Id");

        btnAddSample.setText("Add");
        btnAddSample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSampleActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(fxtblAssocs);

        org.jdesktop.layout.GroupLayout tablePanelLayout = new org.jdesktop.layout.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
        );

        txtAreaSummary.setColumns(20);
        txtAreaSummary.setRows(5);
        jScrollPane2.setViewportView(txtAreaSummary);

        jLabel4.setText("Summary");

        jLabel5.setText("Site");

        btnAddUpdate.setText("Add / Update");
        btnAddUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUpdateActionPerformed(evt);
            }
        });

        txtAreaReference.setColumns(20);
        txtAreaReference.setRows(5);
        jScrollPane3.setViewportView(txtAreaReference);

        jLabel6.setText("Reference");

        jLabel8.setText("Link");

        txtFieldLink.setEditable(false);
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

        jLabel7.setText("Title");

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDeleteAssoc.setText("Delete Assoc");
        btnDeleteAssoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteAssocActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mXHeaderPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtSampleId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(btnAddSample)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 332, Short.MAX_VALUE)
                .add(btnDeleteAssoc)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(txtSeriesId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnLookup)
                        .add(31, 31, 31)
                        .add(btnAddUpdate)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 149, Short.MAX_VALUE)
                        .add(btnClear)
                        .add(18, 18, 18)
                        .add(btnDelete))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(comboSite, 0, 161, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(txtFieldLink, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 382, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE))
                .addContainerGap())
            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(544, Short.MAX_VALUE)
                .add(btnSave)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnCancel)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(mXHeaderPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(11, 11, 11)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnAddUpdate)
                    .add(btnLookup)
                    .add(txtSeriesId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnDelete)
                    .add(btnClear)
                    .add(jLabel1))
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtFieldLink, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel8)
                        .add(comboSite, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel5)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel6)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnAddSample)
                    .add(txtSampleId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(btnDeleteAssoc))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnSave)
                    .add(btnCancel)))
        );
    }// </editor-fold>//GEN-END:initComponents

private void btnAddSampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSampleActionPerformed
  addSampleAssoc();
}//GEN-LAST:event_btnAddSampleActionPerformed

private void btnLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupActionPerformed
  lookupSeries();
}//GEN-LAST:event_btnLookupActionPerformed

private void btnAddUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUpdateActionPerformed
  addUpdateSeries();
}//GEN-LAST:event_btnAddUpdateActionPerformed

private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
  saveSampleAssocs();
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

    private void btnDeleteAssocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteAssocActionPerformed
     removeSampleAssoc();
    }//GEN-LAST:event_btnDeleteAssocActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSample;
    private javax.swing.JButton btnAddUpdate;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteAssoc;
    private javax.swing.JButton btnLookup;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox comboSite;
    private org.jax.mgi.mtb.gui.MXTable fxtblAssocs;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JTextArea txtAreaReference;
    private javax.swing.JTextArea txtAreaSummary;
    private javax.swing.JTextField txtFieldLink;
    private javax.swing.JTextField txtSampleId;
    private javax.swing.JTextField txtSeriesId;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
}
