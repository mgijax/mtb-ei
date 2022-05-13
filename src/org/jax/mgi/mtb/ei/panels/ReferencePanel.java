/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/ReferencePanel.java,v 1.1 2007/04/30 15:50:57 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.apache.log4j.Logger;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBSynchronizationUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.HumanMarkerReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.HumanMarkerReferenceDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceTumorTypeAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceTumorTypeAssocDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.MGIReferenceAPIUtil;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.LabelValueDataBean;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * For displaying  <b>Reference</b> data.
 * 
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/ReferencePanel.java,v 1.1 2007/04/30 15:50:57 mjv Exp
 */
public class ReferencePanel extends CustomPanel {

  private static final Logger log =
          Logger.getLogger(ReferencePanel.class.getName());
  private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); 
  private static final long MTB_DATA_STATUS_INDEXED = 130;
  // -------------------------------------------------------------- Constants
  // put this here or get it from somewhere else
  private static final String PUBMED_LINK = "http://www-ncbi-nlm-nih-gov.ezproxy.jax.org/pubmed/"; 
  
  // ----------------------------------------------------- Instance Variables
  // progress monitor
  MXProgressMonitor progressMonitor = null;
  ReferenceDTO dtoReference = null;
  private MXTable fxtblSearchResults = null;
  private MXTable fxtblTTSearchResults = null;
  private ReferenceSearchPanel rsPanel = null;
  
  private boolean tcsInitalized = false;
  private boolean organsInitalized =false;
  
  private static final Color BG_NORMAL = new Color(255,255,255);
  private static final Color BG_CHANGED = new Color(0,200,0);
  
  private int originalPriorityComboIndex  = 0;
  // ----------------------------------------------------------- Constructors
  /**
   * Creates a new form ReferencePanel.
   */
  public ReferencePanel(boolean editable) {
    initComponents();
    initCustom(editable);
  }

  // --------------------------------------------------------- Public Methods
  /**
   * Set the reference key for the panel.
   *
   * @param lKey the reference key to be looked up in the database
   */
  public void setKey(final long lKey) {
    
    this.jTextFieldCodedBy.setBackground(BG_NORMAL);
    this.jTextFieldCodedByDate.setBackground(BG_NORMAL);
    this.txtNote.setBackground(BG_NORMAL);
    
    Runnable runnable = new Runnable() {

      public void run() {

        try {
          loadReferenceByKey(lKey);
        } catch (Exception e) {
          Utils.log(e);
        } finally {
        }
      }
    };

    new Thread(runnable).start();
  }
  
  // reference to the opening search panel used for next and prev buttions
  public void setSearchPanel(ReferenceSearchPanel in) {
    this.rsPanel = in;
  }

  private void previous() {
    try {
      int nRow = this.rsPanel.fxtblSearchResults.getSelectedRow();
      nRow--;
      this.rsPanel.fxtblSearchResults.getRowCount();
      if (nRow >=0) {
        this.rsPanel.fxtblSearchResults.setRowSelectionInterval((nRow ), (nRow));
        this.rsPanel.fxtblSearchResults.scrollRectToVisible(this.rsPanel.fxtblSearchResults.getCellRect(nRow, 1, false));
        this.jLabelPN.setText("");
      } else {
        this.jLabelPN.setText("At first record");
      }
      MXDefaultTableModel tm =
              (MXDefaultTableModel) this.rsPanel.fxtblSearchResults.getModel();
      int key = ((Integer) tm.getValueAt(nRow, 1)).intValue();

      this.setKey(key);
    } catch (Exception e) {
    }// null pointer if search panel is closed -- do nothing
  }

  private void next() {
    try {
      int nRow = this.rsPanel.fxtblSearchResults.getSelectedRow();
      nRow++;
      this.rsPanel.fxtblSearchResults.getRowCount();
      if (nRow < this.rsPanel.fxtblSearchResults.getRowCount()) {
        this.rsPanel.fxtblSearchResults.setRowSelectionInterval((nRow ), (nRow ));
        this.rsPanel.fxtblSearchResults.scrollRectToVisible(this.rsPanel.fxtblSearchResults.getCellRect(nRow , 1, false));
        this.jLabelPN.setText("");
      } else {
        this.jLabelPN.setText("At last record");
      }

      MXDefaultTableModel tm =
              (MXDefaultTableModel) this.rsPanel.fxtblSearchResults.getModel();
      int key = ((Integer) tm.getValueAt(nRow, 1)).intValue();

      this.setKey(key);

    } catch (Exception e) {
    }// null pointer if search panel is closed -- do nothing

  }

  
  private void loadReferenceByKey(long lKey){
    
     ReferenceDAO daoReference = ReferenceDAO.getInstance();

    try {

      dtoReference = daoReference.loadByPrimaryKey(new Long(lKey));

      String strAccID = EIGlobals.getInstance().getJNumByRef(lKey);
      
      showReferenceData(dtoReference, strAccID);
    }catch(Exception e){}
  }

  /**
   * Lookup all reference related information in the database.
   *
   * @param lKey the reference key to be looked up in the database
   */
  private void showReferenceData(ReferenceDTO dtoReference, String strAccID) {
    
    try {

     
      if (dtoReference != null) {
        txtReferenceKey.setText(dtoReference.getReferenceKey() + "");
        txtTitle.setText(dtoReference.getTitle());
        txtTitle2.setText(dtoReference.getTitle2());
        txtAuthors.setText(dtoReference.getAuthors());
        txtAuthors2.setText(dtoReference.getAuthors2());
        txtPrimaryAuthor.setText(dtoReference.getPrimaryAuthor());
        txtJournal.setText(dtoReference.getJournal());
        txtCitation.setText(dtoReference.getCitation());
        txtShortCitation.setText(dtoReference.getShortCitation());
        txtVolume.setText(dtoReference.getVolume());
        txtIssue.setText(dtoReference.getIssue());
        txtPages.setText(dtoReference.getPages());
        txtYear.setText(dtoReference.getYear());
        txtareaAbstract.setText(dtoReference.getAbstractText());
        if(dtoReference.getNote() != null){
          txtNote.setText(dtoReference.getNote());
        }else{
          txtNote.setText("");
        }
        jTextFieldCodedBy.setText(dtoReference.getCodedBy());
        jTextAreaShortCitation.setText(dtoReference.getShortCitation());
        jTextAreaAuthors.setText(dtoReference.getAuthors());
        this.jTextTitle.setText(dtoReference.getTitle());
        this.jTextJNumber.setText(strAccID);

        if (dtoReference.getCodedByDate() != null) {
          jTextFieldCodedByDate.setText(sdf.format(dtoReference.getCodedByDate()));
        }else{
          jTextFieldCodedByDate.setText("");
        }

        Long priority = dtoReference.getPriority();
        if (priority == null) {
          jComboBoxPriority.setSelectedIndex(0);
        } else {
          for (int i = 1; i < this.jComboBoxPriority.getItemCount(); i++) {
            LabelValueBean<String,String> bean = (LabelValueBean<String, String>) jComboBoxPriority.getItemAt(i);

            if (priority.equals(new Long(bean.getValue()))) {
              jComboBoxPriority.setSelectedIndex(i);
              originalPriorityComboIndex =i;
            }
          }

        }

        long lKey = dtoReference.getReferenceKey();
        txtareaAbstract2.setText(dtoReference.getAbstractText());
        setMarkers(lKey);
        setTumorTypes(lKey);
        initOrgans();
        initTumorClassifications();
        
        jComboBoxPriority.setBackground(BG_NORMAL);
      }

      txtJNumber.setText(strAccID);
     
      MTBSynchronizationUtilDAO syncDAO = MTBSynchronizationUtilDAO.getInstance();
      syncDAO.setMGIInfo(EIGlobals.getInstance().getMGIUser(),
              EIGlobals.getInstance().getMGIPassword(),
              EIGlobals.getInstance().getMGIDriver(),
              EIGlobals.getInstance().getMGIUrl());

      String pubMedID = syncDAO.getPubmedIDFromJNum(strAccID);
      if (pubMedID == null) {
        // if this was a recently loaded reference it may not be in the adhoc db so check MTB
          // older references don't have pubmed ids in MTB thus we check MGD first
          pubMedID = MTBReferenceUtilDAO.getInstance().getPubMedByReference(dtoReference.getReferenceKey());
      
      }
      
      if (pubMedID != null) {

        jLabelPubMedLink.setText(PUBMED_LINK + pubMedID);
      } else {
        jLabelPubMedLink.setText("No PubMed link available");
      }
      

    } catch (Exception e) {
      Utils.log(e);
    }
    
  }

  private void openLink() {
    if (jLabelPubMedLink.getText() != null && jLabelPubMedLink.getText().contains("http:")) {
      try {
        // Lookup the javax.jnlp.BasicService object
        BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
       
        bs.showDocument(new URL(jLabelPubMedLink.getText()));
      } catch (Exception ue) {
        // Service is not supported
        log.debug(ue);
      }
    }

  }

  private void setCodedToday() {
    Date today = new Date();
    jTextFieldCodedByDate.setText(sdf.format(today));
    
    checkCodedDate(null);

    
    MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();    
    this.jTextFieldCodedBy.setText(dtoUser.getUserName());
    codedByChange(null);
    
    
  
    
  }

  private void setMarkers(long lKey) {
    ArrayList<MarkerDTO> markers = MTBReferenceUtilDAO.getInstance().getHumanMarkers(lKey);
    Vector data = new Vector();
    for (MarkerDTO dto : markers) {
      Vector v = new Vector();
      v.add(StringUtils.nvl((String) dto.getDataBean().get(MTBReferenceUtilDAO.EGID), ""));
      v.add(dto.getMarkerKey());
      v.add(StringUtils.nvl(dto.getSymbol(), ""));
      v.add(StringUtils.nvl(dto.getName(), ""));
      v.add((String) dto.getDataBean().get(MTBReferenceUtilDAO.ORG));
      v.add((String) dto.getDataBean().get(MTBReferenceUtilDAO.CHROM));
      data.add(v);
    }
    configureMarkersTable(data);
  }

  private void setTumorTypes(long refKey) {

    Vector data = new Vector();



    try {
      ArrayList<LabelValueDataBean<String, String, Long>> dtoRTTList = MTBReferenceUtilDAO.getInstance().getReferenceTumorTypes(refKey);

      for (LabelValueDataBean<String, String, Long> lvb : dtoRTTList) {

        Vector v = new Vector();
        v.add(lvb.getData() + "");
        v.add(lvb.getLabel());
        v.add(lvb.getValue());

        data.add(v);
      }

    } catch (Exception e) {
      log.error(e);
    }

    configureTumorTypesTable(data);

  }

  private void update() {
    
     final ReferenceDAO daoReference = ReferenceDAO.getInstance();

    dtoReference.setTitle(txtTitle.getText());
    dtoReference.setTitle2(txtTitle2.getText());
    dtoReference.setAuthors(txtAuthors.getText());
    dtoReference.setAuthors2(txtAuthors2.getText());
    dtoReference.setPrimaryAuthor(txtPrimaryAuthor.getText());
    dtoReference.setJournal(txtJournal.getText());
    dtoReference.setCitation(txtCitation.getText());
    dtoReference.setShortCitation(txtShortCitation.getText());
    dtoReference.setVolume(txtVolume.getText());   
    dtoReference.setIssue(txtIssue.getText());
    dtoReference.setPages(txtPages.getText());
    dtoReference.setYear(txtYear.getText());
    dtoReference.setAbstractText(txtareaAbstract.getText());
    dtoReference.setNote(txtNote.getText());

    dtoReference.setCodedBy(jTextFieldCodedBy.getText());
    if (jTextFieldCodedByDate.getText() != null && jTextFieldCodedByDate.getText().trim().length() > 0) {
      try {
        dtoReference.setCodedByDate(sdf.parse(jTextFieldCodedByDate.getText()));
      } catch (Exception e) {
        // need to enter a correctly formatted date
        jTextFieldCodedByDate.setText("dd/mm/yyyy");
        JOptionPane.showMessageDialog(this, "Coded by Date is not a valid date. Format should be ##/##/####  (day/month/year)");
        return;
      }
    }else{
     
      // cant set an existing date to null in the DTO object need to copy all other fields
      ReferenceDTO newDTOReference = daoReference.createReferenceDTO();
      newDTOReference.isNew(false);
      	newDTOReference.setReferenceKey(dtoReference.getReferenceKey());
        newDTOReference.setTitle(dtoReference.getTitle());
        newDTOReference.setTitle2(dtoReference.getTitle2());
        newDTOReference.setAuthors(dtoReference.getAuthors());
        newDTOReference.setAuthors2(dtoReference.getAuthors2());
        newDTOReference.setPrimaryAuthor(dtoReference.getPrimaryAuthor());
        newDTOReference.setCitation(dtoReference.getCitation());
        newDTOReference.setShortCitation(dtoReference.getShortCitation());
        newDTOReference.setJournal(dtoReference.getJournal());
        newDTOReference.setVolume(dtoReference.getVolume());
        newDTOReference.setIssue(dtoReference.getIssue());
        newDTOReference.setPages(dtoReference.getPages());
        newDTOReference.setYear(dtoReference.getYear());
        newDTOReference.setIsReviewArticle(dtoReference.getIsReviewArticle());
        newDTOReference.setReviewStatus(dtoReference.getReviewStatus());
        newDTOReference.setPerson(dtoReference.getPerson());
        newDTOReference.setInstitution(dtoReference.getInstitution());
        newDTOReference.setDepartment(dtoReference.getDepartment());
        newDTOReference.setAddress1(dtoReference.getAddress1());
        newDTOReference.setAddress2(dtoReference.getAddress2());
        newDTOReference.setCity(dtoReference.getCity());
        newDTOReference.setStateProv(dtoReference.getStateProv());
        newDTOReference.setPostalCode(dtoReference.getPostalCode());
        newDTOReference.setCountry(dtoReference.getCountry());
        newDTOReference.setUrl(dtoReference.getUrl());
        newDTOReference.setEmail(dtoReference.getEmail());
        newDTOReference.setCodedBy(dtoReference.getCodedBy());
        newDTOReference.setCodedByDate(null);  // mark field as modified
        newDTOReference.setCheckedBy(dtoReference.getCheckedBy());
        newDTOReference.setCheckedByDate(dtoReference.getCheckedByDate());
      	newDTOReference.setPersonalCommunication(dtoReference.getPersonalCommunication());
        newDTOReference.setNote(dtoReference.getNote());
        newDTOReference.setMTBDataStatusKey(dtoReference.getMTBDataStatusKey());
        newDTOReference.setCreateUser(dtoReference.getCreateUser());
        newDTOReference.setCreateDate(dtoReference.getCreateDate());
        newDTOReference.setReferenceDate(dtoReference.getReferenceDate());
        newDTOReference.setAbstractText(dtoReference.getAbstractText());
      
      
      dtoReference = newDTOReference;
      
      
      
    }

    Long priority = null;
    try{
    
      priority = new Long(((LabelValueBean<String, String>) jComboBoxPriority.getSelectedItem()).getValue());
    
    }catch(Exception ignore){}
  
  
    dtoReference.setPriority(priority);
  
    

    MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
    Date dNow = new Date();

    dtoReference.setUpdateUser(dtoUser.getUserName());
    dtoReference.setUpdateDate(dNow);

   

    Runnable runnable = new Runnable() {

      public void run() {
        progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
        progressMonitor.start("Updating Reference: " + dtoReference.getReferenceKey());
        try {
          daoReference.save(dtoReference);
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
    
    if(this.jTextFieldCodedByDate.getBackground().equals(BG_CHANGED)){
        MGIReferenceAPIUtil apiUtil = new MGIReferenceAPIUtil();
        if(!apiUtil.updateReferenceFullCoded(this.txtJNumber.getText(), dtoUser.getUserName())){
            
            JOptionPane.showMessageDialog(this, "Unable to update reference status to Full Coded" );
            
        }
    }
    
    this.jTextFieldCodedBy.setBackground(BG_NORMAL);
    this.jTextFieldCodedByDate.setBackground(BG_NORMAL);
    this.txtNote.setBackground(BG_NORMAL);
    this.jComboBoxPriority.setBackground(this.BG_NORMAL);
    
    
    
    
    

  }

  private void associateMarker() {

    String refKey = txtReferenceKey.getText();
    String markerKey = txtMarkerKey.getText();
    Date now = new Date(System.currentTimeMillis());
    String user = EIGlobals.getInstance().getMTBUsersDTO().getUserName();
    HumanMarkerReferenceDAO dao = HumanMarkerReferenceDAO.getInstance();
    HumanMarkerReferenceDTO dto = dao.createHumanMarkerReferenceDTO();
    dto.setMarkerKey(new Long(markerKey));
    dto.setReferenceKey(new Long(refKey));


    try {

      List<HumanMarkerReferenceDTO> l = dao.loadUsingTemplate(dto);
      if (l.size() > 0) {

        JOptionPane.showMessageDialog(this, "Marker " + markerKey + " is allready associated with this reference");

      } else {

        dto.setCreateDate(now);
        dto.setUpdateDate(now);
        dto.setCreateUser(user);
        dto.setUpdateUser(user);

        dao.save(dto);

        setMarkers(new Long(refKey).longValue());

      }
      txtMarkerKey.setText("");

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Unable to add marker. " + markerKey + " may not be marker key");
      Utils.log(e);
    }


  }

  // get confirmation, delete assocation and update gui
  private void removeMarker() {

    Long markerKey = null;
    final int nRow = fxtblSearchResults.getSelectedRow();

    if (nRow >= 0) {
      final MXDefaultTableModel tm =
              (MXDefaultTableModel) fxtblSearchResults.getModel();
      markerKey = (Long) tm.getValueAt(nRow, 1);

      String strMessage = "Are you sure you would like to permanently " +
              "delete Marker Key " + markerKey +
              " from the markers associated with this reference?";
      int nAnswer =
              JOptionPane.showConfirmDialog(this, strMessage, "Warning",
              JOptionPane.YES_NO_OPTION);
      if (nAnswer == JOptionPane.YES_OPTION) {
        if (deleteMarker(markerKey)) {
          tm.removeRow(nRow);
        }
      } else if (nAnswer == JOptionPane.NO_OPTION) {
        // do nothing
        return;
      }


    } else {
      String strMessage = "No marker is selected to delete";
      JOptionPane.showConfirmDialog(this, strMessage, "Warning",
              JOptionPane.YES_NO_OPTION);

    }


  }
  // remove marker reference association from the database
  private boolean deleteMarker(Long markerKey) {

    boolean deleted = false;
    String refKey = txtReferenceKey.getText();
    HumanMarkerReferenceDAO dao = HumanMarkerReferenceDAO.getInstance();
    HumanMarkerReferenceDTO dto = dao.createHumanMarkerReferenceDTO();
    dto.setMarkerKey(markerKey);
    dto.setReferenceKey(new Long(refKey));
    try {
      dao.deleteUsingTemplate(dto);
      deleted = true;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Unable to remove marker " + markerKey);
      Utils.log(e);
    }
    return deleted;
  }

  private void removeTumorType() {

    Long rttaKey = null;
    final int nRow = fxtblTTSearchResults.getSelectedRow();

    if (nRow >= 0) {
      final MXDefaultTableModel tm =
              (MXDefaultTableModel) this.fxtblTTSearchResults.getModel();
      try {
        String keyStr = (String) tm.getValueAt(nRow, 0);
        rttaKey = new Long(keyStr);
      } catch (ClassCastException e) {
        log.error(e);
      }
      if(rttaKey.longValue()== 0){
        JOptionPane.showMessageDialog(this, "Curated tumor types can not be deleted");
        
        return;
      }
      
      String strMessage = "Are you sure you would like to permanently " +
              "delete tumor type "+ tm.getValueAt(nRow,1) +
              " from the tumor types associated with this reference?";
      int nAnswer =
              JOptionPane.showConfirmDialog(this, strMessage, "Warning",
              JOptionPane.YES_NO_OPTION);
      if (nAnswer == JOptionPane.YES_OPTION) {
        if (deleteTumorType(rttaKey)) {
          tm.removeRow(nRow);
        }
      } else if (nAnswer == JOptionPane.NO_OPTION) {
        // do nothing
        return;
      }


    } else {
      String strMessage = "No tumor type is selected to delete";
      JOptionPane.showMessageDialog(this, strMessage, "Warning",
              JOptionPane.NO_OPTION);

    }


  }
  // remove tumor type association from the database
  private boolean deleteTumorType(Long key) {

    boolean deleted = false;

    ReferenceTumorTypeAssocDAO dao = ReferenceTumorTypeAssocDAO.getInstance();

    try {
      dao.deleteByPrimaryKey(key);

      deleted = true;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Unable to remove tumor type " + key);
      Utils.log(e);
    }
    return deleted;
  }

  private void configureMarkersTable(Vector data) {
    // column headers
    Vector headers = new Vector();
    headers.add("EntrezGene ID");
    headers.add("Key");
    headers.add("Symbol");
    headers.add("Name");
    headers.add("Organism");
    headers.add("Chromosome");


    MXDefaultTableModel rsdtm =
            new MXDefaultTableModel(data, headers);
    fxtblSearchResults = new MXTable(data, headers);
    fxtblSearchResults.setModel(rsdtm);

    fxtblSearchResults.setColumnSizes(new int[]{85, 75, 75, 75, 75, 75, 30});

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

    jspMarkers.setViewportView(fxtblSearchResults);
    pnlHumanMarkers.revalidate();
  }
 

  private void configureTumorTypesTable(Vector data) {
    // column headers
    Vector headers = new Vector();
    headers.add("Key");
    headers.add("Organ - Tumor Classification");
    headers.add("Create User");


    MXDefaultTableModel tttm =
            new MXDefaultTableModel(data, headers);
    fxtblTTSearchResults = new MXTable(data, headers);
    fxtblTTSearchResults.setModel(tttm);
   
    fxtblTTSearchResults.setColumnSizes(new int[]{350, 10, 10});

    fxtblTTSearchResults.makeUneditable();
    fxtblTTSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    fxtblTTSearchResults.setAlternateRowHighlight(true);
    fxtblTTSearchResults.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
    fxtblTTSearchResults.setAlternateRowHighlightCount(2);
    fxtblTTSearchResults.setStartHighlightRow(1);
    fxtblTTSearchResults.setSelectionBackground(
            EIConstants.COLOR_RESULTS_SELECTION_BG);
    fxtblTTSearchResults.setSelectionForeground(
            EIConstants.COLOR_RESULTS_SELECTION_FG);

    fxtblTTSearchResults.enableToolTip(0, false);
    fxtblTTSearchResults.enableToolTip(1, false);
 
     // hide the key column
    fxtblTTSearchResults.getColumnModel().removeColumn(fxtblTTSearchResults.getColumnModel().getColumn(0));

    // create the tumor type association delete button
    JButton ttDelete =
            new JButton(new ImageIcon(
            getClass().getResource(EIConstants.ICO_DELETE_16)));
    ttDelete.setIconTextGap(0);
    ttDelete.setMargin(new Insets(0, 0, 0, 0));
    ttDelete.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent evt) {
        removeTumorType();
      }
    });

    // update the JScrollPane
    jspTumorType.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jspTumorType.setCorner(JScrollPane.UPPER_RIGHT_CORNER, ttDelete);




    jspTumorType.setViewportView(fxtblTTSearchResults);
    pnlTumorType.revalidate();
  }

  private void initOrgans() {
    if(!organsInitalized){
      
      
      final Map<Long, LabelValueBean<String, Long>> mapOrgans = EIGlobals.getInstance().getOrgansUnfiltered();
      List<LabelValueBean<String, Long>> arrOrgans = new ArrayList<LabelValueBean<String, Long>>(mapOrgans.values());
      arrOrgans.add(0, new LabelValueBean<String, Long>("--Select--", -1L));
      comboOrgan.setModel(new LVBeanListModel<String, Long>(arrOrgans));
      comboOrgan.setRenderer(new LVBeanListCellRenderer<String, Long>());
      comboOrgan.addKeyListener(new LVBeanComboListener<String, Long>());
      comboOrgan.setSelectedIndex(0);
      organsInitalized = true;
    }
  }

  private void initTumorClassifications() {
    if(!tcsInitalized){
      
    final Map<Long, LabelValueBean<String, Long>> mapTumorClassifications =
            EIGlobals.getInstance().getTumorClassifications();
    List<LabelValueBean<String, Long>> arrTumorClassifications =
            new ArrayList<LabelValueBean<String, Long>>(mapTumorClassifications.values());
    arrTumorClassifications.add(0, new LabelValueBean<String, Long>("--Select--", -1L));
    comboTumorClassification.setModel(
            new LVBeanListModel<String, Long>(arrTumorClassifications));
    comboTumorClassification.setRenderer(new LVBeanListCellRenderer<String, Long>());
    comboTumorClassification.addKeyListener(new LVBeanComboListener<String, Long>());
    comboTumorClassification.setSelectedIndex(0);
    
    tcsInitalized = true;
    }
  
  }
  
  private void addTumorType() {

    try {
      Long tumorClassificationKey = new Long(0);
      Long organKey = new Long(0);

      if (comboOrgan.getSelectedIndex() >= 0) {
        LVBeanListModel<String, Long> modelOrganTissueOrigin = (LVBeanListModel<String, Long>) comboOrgan.getModel();
        LabelValueBean<String, Long> beanOrganTissueOrigin = modelOrganTissueOrigin.getElementAt(comboOrgan.getSelectedIndex());
        organKey = new Long(beanOrganTissueOrigin.getValue());
      }

      if (comboTumorClassification.getSelectedIndex() >= 0) {
        LVBeanListModel<String, Long> modelTC = (LVBeanListModel<String, Long>) comboTumorClassification.getModel();
        LabelValueBean<String, Long> beanTC = modelTC.getElementAt(comboTumorClassification.getSelectedIndex());
        tumorClassificationKey = new Long(beanTC.getValue());
      }

      // tumor type
      TumorTypeDAO daoTumorType = TumorTypeDAO.getInstance();
      TumorTypeDTO dtoTumorType = daoTumorType.createTumorTypeDTO();
      dtoTumorType.setOrganKey(organKey);
      dtoTumorType.setTumorClassificationKey(tumorClassificationKey);
      List<TumorTypeDTO> dtoTumorTypes = daoTumorType.loadUsingTemplate(dtoTumorType);



      if (dtoTumorTypes.size() == 0) {
        // add the tumor type
        dtoTumorType.setOrganKey(organKey);
        dtoTumorType.setTumorClassificationKey(tumorClassificationKey);
        dtoTumorType.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
        dtoTumorType.setCreateDate(new java.util.Date());
        dtoTumorType.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
        dtoTumorType.setUpdateDate(new java.util.Date());
        dtoTumorType = daoTumorType.save(dtoTumorType);

        // reload the new TumorType
      
        associateTumorType(dtoTumorType);
      } else {
        associateTumorType(dtoTumorTypes.get(0));
      }

    } catch (Exception e) {
      // unable to create TumorType
      log.error(e);
    }
  }

  private void associateTumorType(TumorTypeDTO ttDTO) throws Exception {
    ReferenceTumorTypeAssocDAO rttaDAO = ReferenceTumorTypeAssocDAO.getInstance();
    ReferenceTumorTypeAssocDTO rttaDTO = rttaDAO.createReferenceTumorTypeAssocDTO();
    Date now = new Date();
    String user = EIGlobals.getInstance().getMTBUsersDTO().getUserName();
    rttaDTO.setReferenceKey(this.dtoReference.getReferenceKey());
    rttaDTO.setTumorTypeKey(ttDTO.getTumorTypeKey());
    rttaDTO.setMTBDataStatusKey(MTB_DATA_STATUS_INDEXED);
    rttaDTO.setCreateDate(now);
    rttaDTO.setUpdateDate(now);
    rttaDTO.setCreateUser(user);
    rttaDTO.setUpdateUser(user);

    rttaDAO.save(rttaDTO);

    // update the tumorTypes list
    setTumorTypes(this.dtoReference.getReferenceKey());
    
    // if coded by date is null update status in MGI to Indexed
   
    if(this.jTextFieldCodedByDate.getText()== null || this.jTextFieldCodedByDate.getText().trim().length()==0){
     MGIReferenceAPIUtil apiUtil = new MGIReferenceAPIUtil();
     if(!apiUtil.updateReferenceIndexed(this.txtJNumber.getText(), user)){
      
         JOptionPane.showMessageDialog(this, "Unable to update reference status to Indexed" );
     }
     
    }

  }

 

  /**
   * Perform any custom initialization needed.
   */
  private void initCustom(boolean editable) {

   // Utils.setTextLimit(this.txtNote, 500);

    this.txtReferenceKey.setEditable(false);
    this.txtJNumber.setEditable(false);
    this.jButtonUpdate.setVisible(true);
    this.jButtonUpdate.setEnabled(true);

    List<LabelValueBean<String, String>> priority = EIGlobals.getInstance().getReferencePriority();
    
    jComboBoxPriority.setModel(new LVBeanListModel<String, String>(priority, false));
    jComboBoxPriority.setRenderer(new LVBeanListCellRenderer<String, String>());
    jComboBoxPriority.addKeyListener(new LVBeanComboListener<String, String>());
    jComboBoxPriority.setSelectedIndex(0);  

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

    jTextField2 = new javax.swing.JTextField();
    tabPanel = new javax.swing.JTabbedPane();
    pnlReferenceInformation = new javax.swing.JPanel();
    lblTitle = new javax.swing.JLabel();
    lblTitle2 = new javax.swing.JLabel();
    lblAuthors = new javax.swing.JLabel();
    lblAuthors2 = new javax.swing.JLabel();
    lblPrimaryAuthor = new javax.swing.JLabel();
    lblJournal = new javax.swing.JLabel();
    lblCitation = new javax.swing.JLabel();
    lblShortCitation = new javax.swing.JLabel();
    lblVolume = new javax.swing.JLabel();
    lblAbstract = new javax.swing.JLabel();
    lblReferenceKey = new javax.swing.JLabel();
    jspAbstract = new javax.swing.JScrollPane();
    txtareaAbstract = new javax.swing.JTextArea();
    txtVolume = new javax.swing.JTextField();
    txtReferenceKey = new javax.swing.JTextField();
    lblJNumber = new javax.swing.JLabel();
    txtJNumber = new javax.swing.JTextField();
    lblIssue = new javax.swing.JLabel();
    txtIssue = new javax.swing.JTextField();
    lblPages = new javax.swing.JLabel();
    txtPages = new javax.swing.JTextField();
    lblYear = new javax.swing.JLabel();
    txtYear = new javax.swing.JTextField();
    txtTitle = new javax.swing.JTextField();
    txtTitle2 = new javax.swing.JTextField();
    txtAuthors = new javax.swing.JTextField();
    txtAuthors2 = new javax.swing.JTextField();
    txtPrimaryAuthor = new javax.swing.JTextField();
    txtJournal = new javax.swing.JTextField();
    txtCitation = new javax.swing.JTextField();
    txtShortCitation = new javax.swing.JTextField();
    headerPanelReference = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    pnlTumorType = new javax.swing.JPanel();
    jspTumorType = new javax.swing.JScrollPane();
    jTable2 = new javax.swing.JTable();
    btnAddTumorType = new javax.swing.JButton();
    headerPanelReference2 = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    comboOrgan = new javax.swing.JComboBox();
    jLabel1 = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    comboTumorClassification = new javax.swing.JComboBox();
    jspAbstract1 = new javax.swing.JScrollPane();
    txtareaAbstract2 = new javax.swing.JTextArea();
    lblAbstract1 = new javax.swing.JLabel();
    jLabelPubMedLink = new javax.swing.JLabel();
    jButtonPrev = new javax.swing.JButton();
    jButtonNext = new javax.swing.JButton();
    jLabel7 = new javax.swing.JLabel();
    jLabelPN = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    txtNote = new javax.swing.JTextArea();
    lblNote = new javax.swing.JLabel();
    jTextFieldCodedBy = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    jTextFieldCodedByDate = new javax.swing.JTextField();
    jButtonToday = new javax.swing.JButton();
    jLabel3 = new javax.swing.JLabel();
    jComboBoxPriority = new javax.swing.JComboBox();
    jButtonUpdate = new javax.swing.JButton();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    jTextAreaShortCitation = new javax.swing.JTextArea();
    jScrollPane3 = new javax.swing.JScrollPane();
    jTextAreaAuthors = new javax.swing.JTextArea();
    jLabel8 = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    jTextTitle = new javax.swing.JTextField();
    jLabel10 = new javax.swing.JLabel();
    jTextJNumber = new javax.swing.JTextField();
    pnlHumanMarkers = new javax.swing.JPanel();
    jspMarkers = new javax.swing.JScrollPane();
    jTable1 = new javax.swing.JTable();
    txtMarkerKey = new javax.swing.JTextField();
    lblMarkerKey = new javax.swing.JLabel();
    btnAddMarker = new javax.swing.JButton();
    btnRemoveMarker = new javax.swing.JButton();
    headerPanelReference1 = new org.jax.mgi.mtb.gui.MXHeaderPanel();

    jTextField2.setText("jTextField2");

    pnlReferenceInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblTitle.setText("Title");

    lblTitle2.setText("Title 2");

    lblAuthors.setText("Authors");

    lblAuthors2.setText("Authors 2");

    lblPrimaryAuthor.setText("Primary Author");

    lblJournal.setText("Journal");

    lblCitation.setText("Citation");

    lblShortCitation.setText("Short Citation");

    lblVolume.setText("Volume");

    lblAbstract.setText("Abstract");

    lblReferenceKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
    lblReferenceKey.setText("Reference Key");

    txtareaAbstract.setColumns(20);
    txtareaAbstract.setLineWrap(true);
    txtareaAbstract.setRows(5);
    txtareaAbstract.setWrapStyleWord(true);
    jspAbstract.setViewportView(txtareaAbstract);

    txtVolume.setColumns(10);

    txtReferenceKey.setColumns(10);

    lblJNumber.setText("JNumber");

    txtJNumber.setColumns(10);

    lblIssue.setText("Issue");

    txtIssue.setColumns(10);

    lblPages.setText("Pages");

    txtPages.setColumns(10);

    lblYear.setText("Year");

    txtYear.setColumns(10);

    headerPanelReference.setText("Reference Information");

    org.jdesktop.layout.GroupLayout pnlReferenceInformationLayout = new org.jdesktop.layout.GroupLayout(pnlReferenceInformation);
    pnlReferenceInformation.setLayout(pnlReferenceInformationLayout);
    pnlReferenceInformationLayout.setHorizontalGroup(
      pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlReferenceInformationLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(lblTitle)
          .add(lblTitle2)
          .add(lblAuthors)
          .add(lblAuthors2)
          .add(lblPrimaryAuthor)
          .add(lblJournal)
          .add(lblCitation)
          .add(lblShortCitation)
          .add(lblVolume)
          .add(lblAbstract)
          .add(lblReferenceKey))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(jspAbstract, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlReferenceInformationLayout.createSequentialGroup()
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(txtVolume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(txtReferenceKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlReferenceInformationLayout.createSequentialGroup()
                .add(lblJNumber)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
              .add(pnlReferenceInformationLayout.createSequentialGroup()
                .add(36, 36, 36)
                .add(lblIssue)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtIssue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(52, 52, 52)
                .add(lblPages)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtPages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 141, Short.MAX_VALUE)
                .add(lblYear)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtYear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
          .add(org.jdesktop.layout.GroupLayout.LEADING, txtTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, txtTitle2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, txtAuthors, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, txtAuthors2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, txtPrimaryAuthor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, txtJournal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, txtCitation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, txtShortCitation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE))
        .addContainerGap())
      .add(headerPanelReference, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
    );
    pnlReferenceInformationLayout.setVerticalGroup(
      pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlReferenceInformationLayout.createSequentialGroup()
        .add(headerPanelReference, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(21, 21, 21)
        .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlReferenceInformationLayout.createSequentialGroup()
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblJNumber)
              .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblTitle)
              .add(txtTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblTitle2)
              .add(txtTitle2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblAuthors)
              .add(txtAuthors, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblAuthors2)
              .add(txtAuthors2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblPrimaryAuthor)
              .add(txtPrimaryAuthor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblJournal)
              .add(txtJournal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblCitation)
              .add(txtCitation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblShortCitation)
              .add(txtShortCitation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
              .add(lblVolume)
              .add(txtVolume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(lblIssue)
              .add(txtIssue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(lblPages)
              .add(txtPages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(lblYear)
              .add(txtYear, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
          .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
            .add(txtReferenceKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(lblReferenceKey)))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlReferenceInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblAbstract)
          .add(jspAbstract, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .add(121, 121, 121))
    );

    tabPanel.addTab("Details", pnlReferenceInformation);

    jTable2.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {},
        {},
        {},
        {}
      },
      new String [] {

      }
    ));
    jspTumorType.setViewportView(jTable2);

    btnAddTumorType.setText("Add Tumor Type");
    btnAddTumorType.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        bntAddTumorTypeActionPerformed(evt);
      }
    });

    headerPanelReference2.setText("Tumor Types");

    comboOrgan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel1.setText("Organ / Tissue");

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel6.setText("Tumor Classification");

    comboTumorClassification.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

    txtareaAbstract2.setColumns(20);
    txtareaAbstract2.setEditable(false);
    txtareaAbstract2.setLineWrap(true);
    txtareaAbstract2.setRows(5);
    txtareaAbstract2.setWrapStyleWord(true);
    jspAbstract1.setViewportView(txtareaAbstract2);

    lblAbstract1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    lblAbstract1.setText("Abstract");

    jLabelPubMedLink.setForeground(new java.awt.Color(51, 102, 255));
    jLabelPubMedLink.setText("Pub med link");
    jLabelPubMedLink.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    jLabelPubMedLink.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        linkClickedHandler(evt);
      }
    });

    jButtonPrev.setText("Previous");
    jButtonPrev.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnPrevActionPerformed(evt);
      }
    });

    jButtonNext.setText("Next");
    jButtonNext.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnNextActionPerformed(evt);
      }
    });

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel7.setText("Link to PubMed");

    jLabelPN.setText("  ");

    txtNote.setColumns(20);
    txtNote.setLineWrap(true);
    txtNote.setRows(5);
    txtNote.setWrapStyleWord(true);
    txtNote.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        checkNoteChanged(evt);
      }
    });
    jScrollPane1.setViewportView(txtNote);

    lblNote.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    lblNote.setText("Note");

    jTextFieldCodedBy.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        codedByChange(evt);
      }
    });

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel2.setText("Coded By");

    jTextFieldCodedByDate.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        checkCodedDate(evt);
      }
    });

    jButtonToday.setText("Today");
    jButtonToday.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jButtonTodayActionPerformed(evt);
      }
    });

    jLabel3.setText("Coded Date");

    jComboBoxPriority.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    jComboBoxPriority.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        priorityChangeHandler(evt);
      }
    });

    jButtonUpdate.setText("Update");
    jButtonUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jButtonUpdateActionPerformed(evt);
      }
    });

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel4.setText("Priority");

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel5.setText("Tumor Types");

    jTextAreaShortCitation.setColumns(20);
    jTextAreaShortCitation.setEditable(false);
    jTextAreaShortCitation.setRows(1);
    jScrollPane2.setViewportView(jTextAreaShortCitation);

    jTextAreaAuthors.setColumns(20);
    jTextAreaAuthors.setEditable(false);
    jTextAreaAuthors.setRows(1);
    jScrollPane3.setViewportView(jTextAreaAuthors);

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel8.setText("ShortCitation");

    jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel9.setText("Authors");

    jTextTitle.setEditable(false);

    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    jLabel10.setText("Title");

    jTextJNumber.setEditable(false);
    jTextJNumber.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextJNumberActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout pnlTumorTypeLayout = new org.jdesktop.layout.GroupLayout(pnlTumorType);
    pnlTumorType.setLayout(pnlTumorTypeLayout);
    pnlTumorTypeLayout.setHorizontalGroup(
      pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(headerPanelReference2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 801, Short.MAX_VALUE)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTumorTypeLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlTumorTypeLayout.createSequentialGroup()
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5)))
          .add(jLabel10)
          .add(jLabel8)
          .add(jLabel7)
          .add(lblAbstract1))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jspAbstract1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
          .add(pnlTumorTypeLayout.createSequentialGroup()
            .add(jButtonPrev)
            .add(151, 151, 151)
            .add(jLabelPN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 280, Short.MAX_VALUE)
            .add(jButtonNext))
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jspTumorType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
          .add(pnlTumorTypeLayout.createSequentialGroup()
            .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(org.jdesktop.layout.GroupLayout.LEADING, pnlTumorTypeLayout.createSequentialGroup()
                .add(jTextFieldCodedBy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldCodedByDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonToday)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxPriority, 0, 162, Short.MAX_VALUE))
              .add(org.jdesktop.layout.GroupLayout.LEADING, comboTumorClassification, 0, 539, Short.MAX_VALUE)
              .add(org.jdesktop.layout.GroupLayout.LEADING, comboOrgan, 0, 539, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
              .add(jButtonUpdate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .add(btnAddTumorType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)))
          .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTumorTypeLayout.createSequentialGroup()
            .add(jLabelPubMedLink, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
            .add(106, 106, 106)
            .add(jTextJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
          .add(pnlTumorTypeLayout.createSequentialGroup()
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(jTextTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
              .add(pnlTumorTypeLayout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)))))
        .add(20, 20, 20))
    );
    pnlTumorTypeLayout.setVerticalGroup(
      pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlTumorTypeLayout.createSequentialGroup()
        .add(headerPanelReference2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(jButtonPrev)
          .add(jLabelPN)
          .add(jButtonNext))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(jTextJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabelPubMedLink, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabel7))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
            .add(jScrollPane2)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
          .add(jLabel9)
          .add(jLabel8))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(jTextTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabel10))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jspAbstract1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblAbstract1))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblNote))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(jButtonUpdate)
          .add(jComboBoxPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabel4)
          .add(jButtonToday)
          .add(jTextFieldCodedByDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabel2)
          .add(jTextFieldCodedBy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabel3))
        .add(8, 8, 8)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(comboOrgan, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabel1))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(comboTumorClassification, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnAddTumorType)
          .add(jLabel6))
        .add(pnlTumorTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlTumorTypeLayout.createSequentialGroup()
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jspTumorType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
          .add(pnlTumorTypeLayout.createSequentialGroup()
            .add(16, 16, 16)
            .add(jLabel5)))
        .addContainerGap())
    );

    tabPanel.addTab("Indexing", pnlTumorType);

    jTable1.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {},
        {},
        {},
        {}
      },
      new String [] {

      }
    ));
    jspMarkers.setViewportView(jTable1);

    txtMarkerKey.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        txtMarkerKeyActionPerformed(evt);
      }
    });

    lblMarkerKey.setText("Marker Key");

    btnAddMarker.setText("Add Marker");
    btnAddMarker.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAddMarkerActionPerformed(evt);
      }
    });

    btnRemoveMarker.setText("Remove Marker");
    btnRemoveMarker.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRemoveMarkerActionPerformed(evt);
      }
    });

    headerPanelReference1.setText("Human Markers");

    org.jdesktop.layout.GroupLayout pnlHumanMarkersLayout = new org.jdesktop.layout.GroupLayout(pnlHumanMarkers);
    pnlHumanMarkers.setLayout(pnlHumanMarkersLayout);
    pnlHumanMarkersLayout.setHorizontalGroup(
      pnlHumanMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(headerPanelReference1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 801, Short.MAX_VALUE)
      .add(pnlHumanMarkersLayout.createSequentialGroup()
        .addContainerGap()
        .add(lblMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(txtMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(btnAddMarker)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 430, Short.MAX_VALUE)
        .add(btnRemoveMarker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
      .add(pnlHumanMarkersLayout.createSequentialGroup()
        .addContainerGap()
        .add(jspMarkers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 781, Short.MAX_VALUE)
        .addContainerGap())
    );
    pnlHumanMarkersLayout.setVerticalGroup(
      pnlHumanMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlHumanMarkersLayout.createSequentialGroup()
        .add(headerPanelReference1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
        .add(pnlHumanMarkersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(btnRemoveMarker)
          .add(lblMarkerKey)
          .add(txtMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnAddMarker))
        .add(18, 18, 18)
        .add(jspMarkers, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabPanel.addTab("Human Markers", pnlHumanMarkers);

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(tabPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, tabPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents

private void txtMarkerKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMarkerKeyActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_txtMarkerKeyActionPerformed

private void btnAddMarkerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMarkerActionPerformed
  associateMarker();
}//GEN-LAST:event_btnAddMarkerActionPerformed

private void btnRemoveMarkerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveMarkerActionPerformed
  removeMarker();
}//GEN-LAST:event_btnRemoveMarkerActionPerformed

private void bntAddTumorTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntAddTumorTypeActionPerformed
  addTumorType();
}//GEN-LAST:event_bntAddTumorTypeActionPerformed

private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
  previous();
}//GEN-LAST:event_btnPrevActionPerformed

private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
  next();
}//GEN-LAST:event_btnNextActionPerformed

private void linkClickedHandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkClickedHandler
  openLink();

}//GEN-LAST:event_linkClickedHandler

private void jButtonTodayActionPerformed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTodayActionPerformed
  setCodedToday();
}//GEN-LAST:event_jButtonTodayActionPerformed

private void jButtonUpdateActionPerformed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
  update();
}//GEN-LAST:event_jButtonUpdateActionPerformed

private void codedByChange(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codedByChange
 if(dtoReference != null){
   String a = dtoReference.getCodedBy();
   String b = this.jTextFieldCodedBy.getText();
   if(equalStrings(a,b)){
     jTextFieldCodedBy.setBackground(BG_NORMAL);
   }else{
  jTextFieldCodedBy.setBackground(BG_CHANGED);   
   }
   
   
 }
  
}//GEN-LAST:event_codedByChange

private void checkCodedDate(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_checkCodedDate
if(dtoReference != null  ){
   String a = null;
   if(dtoReference.getCodedByDate() != null){
     a = sdf.format(dtoReference.getCodedByDate());
   }
   String b = this.jTextFieldCodedByDate.getText();
   if(equalStrings(a,b)){
     jTextFieldCodedByDate.setBackground(BG_NORMAL);
   }else{
  jTextFieldCodedByDate.setBackground(BG_CHANGED);   
   }
   
   
 }
}//GEN-LAST:event_checkCodedDate

private void checkNoteChanged(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_checkNoteChanged
if(dtoReference != null){
   String a = dtoReference.getNote();
   String b = this.txtNote.getText();
   if(equalStrings(a,b)){
     txtNote.setBackground(BG_NORMAL);
   }else{
   txtNote.setBackground(BG_CHANGED);   
   }
   
   
 }
}//GEN-LAST:event_checkNoteChanged

private void priorityChangeHandler(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_priorityChangeHandler
 int index = this.jComboBoxPriority.getSelectedIndex();
 if(index != this.originalPriorityComboIndex){
   // highlight combo
   this.jComboBoxPriority.setBackground(this.BG_CHANGED);
   
 }else{
   // un highlight
   this.jComboBoxPriority.setBackground(this.BG_NORMAL);
 }
}//GEN-LAST:event_priorityChangeHandler

private void jTextJNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextJNumberActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextJNumberActionPerformed

private boolean equalStrings(String a, String b){
  if(a == null){
    if(b == null || b.trim().length ()== 0){
     return true;
    }
    return false;
  }
  if(b == null){
    return false;
  }
  if(a.trim().equalsIgnoreCase(b.trim())){
    return true;
  }
  return false;
  
  
}

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAddMarker;
  private javax.swing.JButton btnAddTumorType;
  private javax.swing.JButton btnRemoveMarker;
  private javax.swing.JComboBox comboOrgan;
  private javax.swing.JComboBox comboTumorClassification;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelReference;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelReference1;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelReference2;
  private javax.swing.JButton jButtonNext;
  private javax.swing.JButton jButtonPrev;
  private javax.swing.JButton jButtonToday;
  private javax.swing.JButton jButtonUpdate;
  private javax.swing.JComboBox jComboBoxPriority;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JLabel jLabelPN;
  private javax.swing.JLabel jLabelPubMedLink;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JTable jTable1;
  private javax.swing.JTable jTable2;
  private javax.swing.JTextArea jTextAreaAuthors;
  private javax.swing.JTextArea jTextAreaShortCitation;
  private javax.swing.JTextField jTextField2;
  private javax.swing.JTextField jTextFieldCodedBy;
  private javax.swing.JTextField jTextFieldCodedByDate;
  private javax.swing.JTextField jTextJNumber;
  private javax.swing.JTextField jTextTitle;
  private javax.swing.JScrollPane jspAbstract;
  private javax.swing.JScrollPane jspAbstract1;
  private javax.swing.JScrollPane jspMarkers;
  private javax.swing.JScrollPane jspTumorType;
  private javax.swing.JLabel lblAbstract;
  private javax.swing.JLabel lblAbstract1;
  private javax.swing.JLabel lblAuthors;
  private javax.swing.JLabel lblAuthors2;
  private javax.swing.JLabel lblCitation;
  private javax.swing.JLabel lblIssue;
  private javax.swing.JLabel lblJNumber;
  private javax.swing.JLabel lblJournal;
  private javax.swing.JLabel lblMarkerKey;
  private javax.swing.JLabel lblNote;
  private javax.swing.JLabel lblPages;
  private javax.swing.JLabel lblPrimaryAuthor;
  private javax.swing.JLabel lblReferenceKey;
  private javax.swing.JLabel lblShortCitation;
  private javax.swing.JLabel lblTitle;
  private javax.swing.JLabel lblTitle2;
  private javax.swing.JLabel lblVolume;
  private javax.swing.JLabel lblYear;
  private javax.swing.JPanel pnlHumanMarkers;
  private javax.swing.JPanel pnlReferenceInformation;
  private javax.swing.JPanel pnlTumorType;
  private javax.swing.JTabbedPane tabPanel;
  private javax.swing.JTextField txtAuthors;
  private javax.swing.JTextField txtAuthors2;
  private javax.swing.JTextField txtCitation;
  private javax.swing.JTextField txtIssue;
  private javax.swing.JTextField txtJNumber;
  private javax.swing.JTextField txtJournal;
  private javax.swing.JTextField txtMarkerKey;
  private javax.swing.JTextArea txtNote;
  private javax.swing.JTextField txtPages;
  private javax.swing.JTextField txtPrimaryAuthor;
  private javax.swing.JTextField txtReferenceKey;
  private javax.swing.JTextField txtShortCitation;
  private javax.swing.JTextField txtTitle;
  private javax.swing.JTextField txtTitle2;
  private javax.swing.JTextField txtVolume;
  private javax.swing.JTextField txtYear;
  private javax.swing.JTextArea txtareaAbstract;
  private javax.swing.JTextArea txtareaAbstract2;
  // End of variables declaration//GEN-END:variables
}
