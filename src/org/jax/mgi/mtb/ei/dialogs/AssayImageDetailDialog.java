/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/PathologyImageDetailDialog.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AssayImagesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AssayImagesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.models.AssayImageDTOTableModel;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXImageViewerPreviewPanel;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * A custom <code>JDialog</code> used to display assay information.
 *
 * @author sbn
 * @date 2007/04/30 15:50:45
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/AssayImageDetailDialog.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * @see javax.swing.JDialog
 */
public class AssayImageDetailDialog extends javax.swing.JDialog {

  // -------------------------------------------------------------- Constants
  public static int IMAGE_ADD = 0;
  public static int IMAGE_EDIT = 1;
  private int nType = IMAGE_ADD;
  private AssayImagesDTO dtoAssayImage = null;
  private ReferenceDTO dtoRef = null;
  private File fileHighRes = null;
  private File fileLowRes = null;
  private boolean bSavedClicked = false;
  private MXTable fxtblAssayImages = null;
  private String directoryStr = null;

  // ----------------------------------------------------------- Constructors
  /**
   * Creates new form AssayImageDetailDialog
   */
  public AssayImageDetailDialog(Frame parent, boolean modal) {
    this(parent, modal, IMAGE_ADD);
  }

  /**
   * Creates new form AssayImageDetailDialog
   */
  public AssayImageDetailDialog(Frame parent, boolean modal, int nType) {
    super(parent, modal);
    this.nType = nType;
    initComponents();
    initCustom();
  }
  // --------------------------------------------------------- Public Methods
  /**
   * Determine whether or not this form should be saved or not.
   *
   * @return <code>true</code> if this from should be saved,
   *         <code>false</code> otherwise
   */
  public boolean shouldSave() {
    return this.bSavedClicked;
  }

  /**
   * Return the <code>AssayImagesDTO</code> object.
   *
   * @return the <code>AssayImagesDTO</code> object
   */
  public AssayImagesDTO getImagesDTO() {
    return dtoAssayImage;
  }

  /**
   * Set the DTO for the assay image 
   *
   * @param dtoAssayImageIn AssayImagesDTO
   */
  public void setDTO(AssayImagesDTO dtoAssayImageIn) {
    this.setTitle("Edit Image Key: " + dtoAssayImageIn.getAssayImagesKey() + "");
    this.dtoAssayImage = dtoAssayImageIn;
    loadImageData(dtoAssayImageIn);


  }

  /**
   * Update the image preview.
   *
   * @param f the <code>File</code> object of the local image
   */
  public void updateImage(File f) {
    try {
      updateImage(f.toURL());
    } catch (Exception e) {
      Utils.log(e);
    }
  }

  /**
   * Update the image preview.
   *
   * @param f the <code>File</code> object of the local image
   */
  public void updateImage(URL url) {
    imageViewer.setImage(url);
    try {
      imageViewer.refresh();
    } catch (Exception e) {
      Utils.log(e);
    }
  }
  // ------------------------------------------------------ Protected Methods
  // none

  // -------------------------------------------------------- Private Methods
  /**
   * Construct and/or update the <code>AssayImagesDTO</code>
   */
  private void add() {
    // validate the data being entered
    String strTemp = txtLowResName.getText();
    if (!StringUtils.hasValue(strTemp)) {
      Utils.showErrorDialog("Please select a thumbnail image.");
      txtLowResName.requestFocus();
      return;
    }

    strTemp = txtHighResName.getText();
    if (!StringUtils.hasValue(strTemp)) {
      Utils.showErrorDialog("Please select a high resolution image.");
      txtHighResName.requestFocus();
      return;
    }

    if (!loadReference()) {
      Utils.showErrorDialog("Please enter a valid reference JNumber");
      this.txtReference.requestFocus();
      return;
    }

    MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();

    if (dtoAssayImage == null) {
    
      dtoAssayImage = AssayImagesDAO.getInstance().createAssayImagesDTO();
      dtoAssayImage.setCreateDate(new java.util.Date());
      dtoAssayImage.setCreateUser(dtoUser.getUserName());
    }


    // update the image dto info
    dtoAssayImage.setNote(this.txtareaNotes.getText());
    dtoAssayImage.setCaption(this.txtareaCaption.getText());
    dtoAssayImage.setCopyright(this.txtareaCopyright.getText());
    dtoAssayImage.setReferenceKey(this.dtoRef.getReferenceKey());

    dtoAssayImage.setHighResName(txtHighResName.getText());
    if (this.fileHighRes != null) {
      this.dtoAssayImage.getDataBean().put(EIConstants.LOCAL_IMAGE_HIGHRES, this.fileHighRes);
    }

    dtoAssayImage.setLowResName(txtLowResName.getText());
    if (this.fileLowRes != null) {
      this.dtoAssayImage.getDataBean().put(EIConstants.LOCAL_IMAGE_THUMB, this.fileLowRes);

    }

    if (checkboxPrivate.isSelected()) {
      dtoAssayImage.setPrivateFlag(1l);
    } else {
      dtoAssayImage.setPrivateFlag(0l);
    }
    if (dtoAssayImage.isModified()) {
    
      dtoAssayImage.setUpdateDate(new java.util.Date());
      dtoAssayImage.setUpdateUser(dtoUser.getUserName());
    }

    this.bSavedClicked = true;

    this.setVisible(false);

  }

  /**
   * Initialize the custom components.
   *
   */
  private void initCustom() {


    initAssayImages();

    if (this.nType == IMAGE_ADD) {
      this.setTitle("Add Image");
      this.dtoAssayImage = AssayImagesDAO.getInstance().createAssayImagesDTO();
    } else {
      this.setTitle("Edit Image");
      this.btnAdd.setText("Update");
    }
  }

  /**
   * Browse for the thumbnail image file.
   */
  private void browseLowResFile() {
    JFileChooser chooser = new JFileChooser();
    String dirString = EIGlobals.getInstance().getDirectoryStr();
    if(dirString != null){  
      File currentDir = new File(dirString);
      chooser.setCurrentDirectory(currentDir);
    }
    MXImageViewerPreviewPanel preview = new MXImageViewerPreviewPanel();
    chooser.setAccessory(preview);
    chooser.addPropertyChangeListener(preview);
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setDialogTitle("Select Thumbnail File");
    chooser.setMultiSelectionEnabled(false);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    // Show open dialog; this method does not return until the dialog is closed
    if (chooser.showDialog(null, "Select") != JFileChooser.APPROVE_OPTION) {
      return; // only when user select valid dir, it can return approve_option
    }

    File f = chooser.getSelectedFile();

    if (f != null) {
      EIGlobals.getInstance().setDirectoryStr(f.getParent());
      this.fileLowRes = f;
      dtoAssayImage.setLowResName(f.getName());
      dtoAssayImage.getDataBean().put(EIConstants.LOCAL_IMAGE_THUMB, f);
      this.txtLowResName.setText(f.getName());
      updateImage(f);

    }




  }

  /**
   * Browse for the high res image file.
   */
  private void browseHighResFile() {
    JFileChooser chooser = new JFileChooser();
    
    String dirString = EIGlobals.getInstance().getDirectoryStr();
    if(dirString != null){  
      File currentDir = new File(dirString);
      chooser.setCurrentDirectory(currentDir);
    }
    MXImageViewerPreviewPanel preview = new MXImageViewerPreviewPanel();
    chooser.setAccessory(preview);
    chooser.addPropertyChangeListener(preview);
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setDialogTitle("Select High Resolution File");
    chooser.setMultiSelectionEnabled(false);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    // Show open dialog; this method does not return until the dialog is closed
    if (chooser.showDialog(null, "Select") != JFileChooser.APPROVE_OPTION) {
      return; // only when user select valid dir, it can return approve_option
    }

    File f = chooser.getSelectedFile();

    if (f != null) {
      EIGlobals.getInstance().setDirectoryStr(f.getParent());
      this.fileHighRes = f;
      txtHighResName.setText(f.getName());
      dtoAssayImage.setHighResName(f.getName());
      dtoAssayImage.getDataBean().put(EIConstants.LOCAL_IMAGE_HIGHRES, f);
    }
  }

  private void searchByReference() {


    String strAccID = txtJNumber.getText();
    long nRefKey = 0;
    if (StringUtils.hasValue(strAccID)) {
      MTBReferenceUtilDAO daoRefUtil = MTBReferenceUtilDAO.getInstance();
      nRefKey = daoRefUtil.getReferenceKeyByAccession(strAccID);
    }

    List<AssayImagesDTO> assayImages = null;
    try {
      assayImages = AssayImagesDAO.getInstance().loadByReferenceKey(nRefKey);
    
    } catch (Exception e) {
      e.printStackTrace();
    }

    ((AssayImageDTOTableModel) fxtblAssayImages.getModel()).setData(assayImages);

  }

  private boolean loadReference() {
    boolean validReference = false;
    String text = txtReference.getText();

    if (StringUtils.hasValue(text)) {
      long refKey = EIGlobals.getInstance().getRefByAcc(text);

      if (refKey != -1) {
        try {
          ReferenceDAO daoRef = ReferenceDAO.getInstance();
          dtoRef = daoRef.loadByPrimaryKey(new Long(refKey));

          lblReferenceDetails.setText(dtoRef.getShortCitation());
          if (dtoRef.getReferenceKey() != null) {
            validReference = true;
          }
        } catch (Exception e) {
          Utils.log(e);
        }
      } else {
        lblReferenceDetails.setText("");
      }
    }
    return validReference;
  }

  private void loadImageData(AssayImagesDTO dto) {
    try {
      dtoAssayImage = dto;
      this.dtoRef = ReferenceDAO.getInstance().loadByPrimaryKey(dtoAssayImage.getReferenceKey());

    } catch (Exception e) {
      Utils.log(e);

    }

    this.txtImageKey.setText(dtoAssayImage.getAssayImagesKey() + "");
    this.txtHighResName.setText(dtoAssayImage.getHighResName());
    this.txtLowResName.setText(dtoAssayImage.getLowResName());


    if (dtoAssayImage.getPrivateFlag().longValue() == 1) {
      checkboxPrivate.setSelected(true);
    } else {
      checkboxPrivate.setSelected(false);
    }

    this.txtReference.setText(EIGlobals.getInstance().getJNumByRef(dtoRef.getReferenceKey()));
    this.lblReferenceDetails.setText(dtoRef.getShortCitation());
    this.txtareaCaption.setText(dtoAssayImage.getCaption());
    this.txtareaCopyright.setText(dtoAssayImage.getCopyright());
    this.txtareaNotes.setText(dtoAssayImage.getNote());

    File lowRes = null;
    try {
      lowRes = (File) dtoAssayImage.getDataBean().get(EIConstants.LOCAL_IMAGE_THUMB);
    } catch (NullPointerException e) {
      Utils.log(e);
    }

    if (lowRes != null) {
      updateImage(lowRes);
    } else {
      try {
        updateImage(new URL(EIConstants.ASSAY_IMAGE_URL + "/" + EIConstants.ASSAY_IMAGE_URL_PATH + "/" + dtoAssayImage.getLowResName()));
      } catch (MalformedURLException e) {
        Utils.log(e);

      }
    }
  }

  private void initAssayImages() {
    // create the table model
    List<String> arrHeaders = new ArrayList<String>(4);

    arrHeaders.add("Image");
    arrHeaders.add("Key");
    arrHeaders.add("Caption");
    arrHeaders.add("Note");


    List arrAssayImages = new ArrayList();
    AssayImageDTOTableModel<AssayImagesDTO> tblmdlAssayImages =
            new AssayImageDTOTableModel<AssayImagesDTO>(arrAssayImages, arrHeaders);
    fxtblAssayImages = new MXTable(tblmdlAssayImages);
    fxtblAssayImages.setModel(tblmdlAssayImages);

    // set the table options

    fxtblAssayImages.setColumnSizes(new int[]{0, 0, 0, 0});
    fxtblAssayImages.setAlternateRowHighlight(true);
    fxtblAssayImages.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
    fxtblAssayImages.setAlternateRowHighlightCount(2);
    fxtblAssayImages.setStartHighlightRow(1);
    fxtblAssayImages.setSelectionBackground(
            EIConstants.COLOR_RESULTS_SELECTION_BG);
    fxtblAssayImages.setSelectionForeground(
            EIConstants.COLOR_RESULTS_SELECTION_FG);
    fxtblAssayImages.enableToolTip(1, false);
    fxtblAssayImages.setRowSelectionAllowed(true);
    fxtblAssayImages.addMouseListener(new MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int row = fxtblAssayImages.getSelectedRow();
          AssayImageDTOTableModel model = (AssayImageDTOTableModel) fxtblAssayImages.getModel();
          loadImageData((AssayImagesDTO) model.getDTO(row));
        }
      }
    });
    fxtblAssayImages.makeUneditable();
    fxtblAssayImages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    fxtblAssayImages.doLayout();

    // update the JScrollPane1
    jScrollPane1.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    jScrollPane1.setViewportView(fxtblAssayImages);

    //revalidate the panel
    jPanel2.revalidate();
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

    pnlDetails = new javax.swing.JPanel();
    lblImageKey = new javax.swing.JLabel();
    lblHighResName = new javax.swing.JLabel();
    lblLowResName = new javax.swing.JLabel();
    txtImageKey = new javax.swing.JTextField();
    txtHighResName = new javax.swing.JTextField();
    txtLowResName = new javax.swing.JTextField();
    btnBrowseHighRes = new javax.swing.JButton();
    btnBrowseLowRes = new javax.swing.JButton();
    checkboxPrivate = new javax.swing.JCheckBox();
    headerPanelDetails = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    txtReference = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    btnReferenceLookup = new javax.swing.JButton();
    lblReferenceDetails = new javax.swing.JLabel();
    pnlPreview = new javax.swing.JPanel();
    headerPanelPreview = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    imageViewer = new org.jax.mgi.mtb.gui.MXImageViewer();
    pnlAdditional = new javax.swing.JPanel();
    headerPanelAdditional = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    lblCaption = new javax.swing.JLabel();
    jspCaption = new javax.swing.JScrollPane();
    txtareaCaption = new javax.swing.JTextArea();
    lblNotes = new javax.swing.JLabel();
    jspNotes = new javax.swing.JScrollPane();
    txtareaNotes = new javax.swing.JTextArea();
    lblCopyright = new javax.swing.JLabel();
    jspCopyright = new javax.swing.JScrollPane();
    txtareaCopyright = new javax.swing.JTextArea();
    btnCancel = new javax.swing.JButton();
    jPanel1 = new javax.swing.JPanel();
    txtJNumber = new javax.swing.JTextField();
    jLabel1 = new javax.swing.JLabel();
    btnSearchJNumber = new javax.swing.JButton();
    headerPanelDetails1 = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jTable1 = new javax.swing.JTable();
    btnAdd = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    pnlDetails.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblImageKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
    lblImageKey.setText("Image Key");

    lblHighResName.setText("High Res Name");

    lblLowResName.setText("Low Res Name");

    txtImageKey.setColumns(10);
    txtImageKey.setEditable(false);

    btnBrowseHighRes.setText("Browse");
    btnBrowseHighRes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnBrowseHighResActionPerformed(evt);
      }
    });

    btnBrowseLowRes.setText("Browse");
    btnBrowseLowRes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnBrowseLowResActionPerformed(evt);
      }
    });

    checkboxPrivate.setText("Private");
    checkboxPrivate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkboxPrivate.setMargin(new java.awt.Insets(0, 0, 0, 0));

    headerPanelDetails.setDrawSeparatorUnderneath(true);
    headerPanelDetails.setText("Image Details");

    txtReference.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        txtReferenceActionPerformed(evt);
      }
    });
    txtReference.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtReferenceFocusLost(evt);
      }
    });

    jLabel2.setText("JNumber");

    btnReferenceLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
    btnReferenceLookup.setText("Lookup");
    btnReferenceLookup.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnReferenceLookupActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout pnlDetailsLayout = new org.jdesktop.layout.GroupLayout(pnlDetails);
    pnlDetails.setLayout(pnlDetailsLayout);
    pnlDetailsLayout.setHorizontalGroup(
      pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, headerPanelDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
      .add(pnlDetailsLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(pnlDetailsLayout.createSequentialGroup()
            .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(pnlDetailsLayout.createSequentialGroup()
                .add(lblImageKey)
                .add(1, 1, 1))
              .add(jLabel2))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblReferenceDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
              .add(pnlDetailsLayout.createSequentialGroup()
                .add(txtImageKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 109, Short.MAX_VALUE)
                .add(checkboxPrivate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
              .add(txtReference, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)))
          .add(pnlDetailsLayout.createSequentialGroup()
            .add(lblHighResName)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(txtHighResName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))
          .add(pnlDetailsLayout.createSequentialGroup()
            .add(lblLowResName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(txtLowResName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
          .add(btnBrowseLowRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(btnBrowseHighRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(btnReferenceLookup, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .add(38, 38, 38))
    );
    pnlDetailsLayout.setVerticalGroup(
      pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlDetailsLayout.createSequentialGroup()
        .add(headerPanelDetails, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(25, 25, 25)
        .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblImageKey)
          .add(txtImageKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(checkboxPrivate))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
        .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(txtReference, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(jLabel2)
          .add(btnReferenceLookup))
        .add(3, 3, 3)
        .add(lblReferenceDetails, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(27, 27, 27)
        .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(txtHighResName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblHighResName)
          .add(btnBrowseHighRes))
        .add(18, 18, 18)
        .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(txtLowResName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblLowResName)
          .add(btnBrowseLowRes))
        .addContainerGap())
    );

    pnlPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    headerPanelPreview.setDrawSeparatorUnderneath(true);
    headerPanelPreview.setText("Image Preview");

    imageViewer.setShowToolbar(false);

    org.jdesktop.layout.GroupLayout pnlPreviewLayout = new org.jdesktop.layout.GroupLayout(pnlPreview);
    pnlPreview.setLayout(pnlPreviewLayout);
    pnlPreviewLayout.setHorizontalGroup(
      pnlPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, headerPanelPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
      .add(imageViewer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
    );
    pnlPreviewLayout.setVerticalGroup(
      pnlPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlPreviewLayout.createSequentialGroup()
        .add(headerPanelPreview, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 8, Short.MAX_VALUE)
        .add(imageViewer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );

    pnlAdditional.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    headerPanelAdditional.setDrawSeparatorUnderneath(true);
    headerPanelAdditional.setText("Additional Image Information");

    lblCaption.setText("Caption");

    txtareaCaption.setColumns(20);
    txtareaCaption.setLineWrap(true);
    txtareaCaption.setRows(2);
    txtareaCaption.setWrapStyleWord(true);
    jspCaption.setViewportView(txtareaCaption);

    lblNotes.setText("Editor's Note");

    txtareaNotes.setColumns(20);
    txtareaNotes.setLineWrap(true);
    txtareaNotes.setRows(2);
    txtareaNotes.setWrapStyleWord(true);
    jspNotes.setViewportView(txtareaNotes);

    lblCopyright.setText("Copyright");

    txtareaCopyright.setColumns(20);
    txtareaCopyright.setLineWrap(true);
    txtareaCopyright.setRows(2);
    txtareaCopyright.setWrapStyleWord(true);
    jspCopyright.setViewportView(txtareaCopyright);

    org.jdesktop.layout.GroupLayout pnlAdditionalLayout = new org.jdesktop.layout.GroupLayout(pnlAdditional);
    pnlAdditional.setLayout(pnlAdditionalLayout);
    pnlAdditionalLayout.setHorizontalGroup(
      pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlAdditionalLayout.createSequentialGroup()
        .add(28, 28, 28)
        .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, lblCopyright)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, lblNotes)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, lblCaption))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jspCopyright, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
          .add(jspCaption, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, jspNotes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE))
        .addContainerGap())
      .add(headerPanelAdditional, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
    );
    pnlAdditionalLayout.setVerticalGroup(
      pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlAdditionalLayout.createSequentialGroup()
        .add(headerPanelAdditional, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblCaption)
          .add(jspCaption, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblNotes)
          .add(jspNotes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblCopyright)
          .add(jspCopyright, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(36, Short.MAX_VALUE))
    );

    btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png"))); // NOI18N
    btnCancel.setText("Cancel");
    btnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCancelActionPerformed(evt);
      }
    });

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    txtJNumber.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        JNumberFocusLost(evt);
      }
    });

    jLabel1.setText("JNumber");

    btnSearchJNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
    btnSearchJNumber.setText("Search");
    btnSearchJNumber.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSearchJNumberActionPerformed(evt);
      }
    });

    headerPanelDetails1.setDrawSeparatorUnderneath(true);
    headerPanelDetails1.setText("Search Images");

    org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(headerPanelDetails1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .add(btnSearchJNumber))
          .add(jPanel1Layout.createSequentialGroup()
            .add(25, 25, 25)
            .add(jLabel1)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 158, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        .add(140, 140, 140))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(jPanel1Layout.createSequentialGroup()
        .add(headerPanelDetails1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(31, 31, 31)
        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(jLabel1)
          .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(btnSearchJNumber)
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jTable1.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jScrollPane1.setViewportView(jTable1);

    org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
    );

    btnAdd.setIcon(new javax.swing.ImageIcon("C:\\Documents and Settings\\sbn\\My Documents\\NetBeansProjects\\mgi\\mtb\\mtbei\\src\\org\\jax\\mgi\\mtb\\ei\\resources\\img\\Add16.png")); // NOI18N
    btnAdd.setText("Add");
    btnAdd.setMaximumSize(new java.awt.Dimension(85, 25));
    btnAdd.setMinimumSize(new java.awt.Dimension(85, 25));
    btnAdd.setPreferredSize(new java.awt.Dimension(85, 25));
    btnAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAddActionPerformed(evt);
      }
    });

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(layout.createSequentialGroup()
            .add(btnAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(btnCancel)
            .add(4, 4, 4))
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlAdditional, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(layout.createSequentialGroup()
            .add(pnlDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlPreview, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
          .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 254, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
          .add(pnlPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(pnlDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlAdditional, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(btnCancel)
          .add(btnAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
      dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnBrowseLowResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseLowResActionPerformed
      browseLowResFile();
    }//GEN-LAST:event_btnBrowseLowResActionPerformed

    private void btnBrowseHighResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseHighResActionPerformed
      browseHighResFile();
    }//GEN-LAST:event_btnBrowseHighResActionPerformed

private void txtReferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReferenceActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_txtReferenceActionPerformed

private void btnSearchJNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchJNumberActionPerformed
  searchByReference();
}//GEN-LAST:event_btnSearchJNumberActionPerformed

private void JNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_JNumberFocusLost
  Utils.fixJNumber(txtJNumber);
}//GEN-LAST:event_JNumberFocusLost

private void btnReferenceLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReferenceLookupActionPerformed
  loadReference();
}//GEN-LAST:event_btnReferenceLookupActionPerformed

private void txtReferenceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReferenceFocusLost
  Utils.fixJNumber(txtReference);
}//GEN-LAST:event_txtReferenceFocusLost

private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
  add();
}//GEN-LAST:event_btnAddActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAdd;
  private javax.swing.JButton btnBrowseHighRes;
  private javax.swing.JButton btnBrowseLowRes;
  private javax.swing.JButton btnCancel;
  private javax.swing.JButton btnReferenceLookup;
  private javax.swing.JButton btnSearchJNumber;
  private javax.swing.JCheckBox checkboxPrivate;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelAdditional;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelDetails;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelDetails1;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelPreview;
  private org.jax.mgi.mtb.gui.MXImageViewer imageViewer;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable jTable1;
  private javax.swing.JScrollPane jspCaption;
  private javax.swing.JScrollPane jspCopyright;
  private javax.swing.JScrollPane jspNotes;
  private javax.swing.JLabel lblCaption;
  private javax.swing.JLabel lblCopyright;
  private javax.swing.JLabel lblHighResName;
  private javax.swing.JLabel lblImageKey;
  private javax.swing.JLabel lblLowResName;
  private javax.swing.JLabel lblNotes;
  private javax.swing.JLabel lblReferenceDetails;
  private javax.swing.JPanel pnlAdditional;
  private javax.swing.JPanel pnlDetails;
  private javax.swing.JPanel pnlPreview;
  private javax.swing.JTextField txtHighResName;
  private javax.swing.JTextField txtImageKey;
  private javax.swing.JTextField txtJNumber;
  private javax.swing.JTextField txtLowResName;
  private javax.swing.JTextField txtReference;
  private javax.swing.JTextArea txtareaCaption;
  private javax.swing.JTextArea txtareaCopyright;
  private javax.swing.JTextArea txtareaNotes;
  // End of variables declaration//GEN-END:variables
}
