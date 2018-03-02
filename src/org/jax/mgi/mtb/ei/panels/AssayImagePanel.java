/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/PathologyPanel.java,v 1.1 2007/04/30 15:50:55 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorGeneticChangesDTO;
import org.jax.mgi.mtb.ei.models.MTBTumorGeneticChangesDTOTableModel;
import org.jax.mgi.mtb.dao.gen.mtb.AssayImagesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AssayImagesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TmrGntcCngAssayImageAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TmrGntcCngAssayImageAssocDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.dialogs.AssayImageDetailDialog;
import org.jax.mgi.mtb.ei.models.AssayImageDTOTableModel;
import org.jax.mgi.mtb.ei.models.DTOTableModel;
import org.jax.mgi.mtb.ei.renderers.AssayImageDTOCellRenderer;
import org.jax.mgi.mtb.ei.renderers.ImageCellRenderer;
import org.jax.mgi.mtb.ei.util.SFTP;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.utils.DataBean;

/**
 * For inserting or updating <b>Assay Image</b> information and the associated
 * data in the database.
 *
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AssayImagePanel.java,v 1.1 2007/04/30 15:50:55 mjv Exp
 */
public class AssayImagePanel extends CustomPanel
        implements ActionListener {

  // -------------------------------------------------------------- Constants
  /**
   * Used in the constructor to specify this is a new pathology record.
   */
  public static final int ASSAY_IMAGE_PANEL_NEW = 0;
  /**
   * Used in the constructor to specify this is an old pathology record.
   */
  public static final int ASSAY_IMAGE_PANEL_EDIT = 1;
  private final String ACTION_COMMAND_NEW = "new";
  private final String ACTION_COMMAND_EDIT = "edit";
  private final String ACTION_COMMAND_DELETE = "delete";  
  private MTBTumorGeneticChangesDTO dtoTGC = null;
  private int nType = ASSAY_IMAGE_PANEL_NEW;    
  private MXTable fxtblImages = null;    
  private MXProgressMonitor progressMonitor = null;
  
  private MTBTumorGeneticChangesDTOTableModel tgcTableModel = null;
  private int tgcTableRow = 0;
  
  // ----------------------------------------------------------- Constructors
  /**
   * Creates a new AssayImage Panel.
   * <p>
   * If <code>nType = ASSAY_IMAGE_PANEL_ADD/code> create a new image entry
   * Otherwise, the image already exists in the database.
   *
   * @param nType the type of panel, which is either
   *        <code>ASSAY_IMAGE_PANEL_ADD</code> or <code>ASSAY_IMAGE_PANEL_EDIT</code>
   */
  public AssayImagePanel(int nType) {
    this.nType = nType;
    initComponents();
    initCustom();
  }
  
  public void setTableModel(MTBTumorGeneticChangesDTOTableModel tgcTableModelIn, int row){
    this.tgcTableModel = tgcTableModelIn;
    this.tgcTableRow = row;
    
    
  }
  // --------------------------------------------------------- Public Methods
  /**
   * Set the Tumor Genetic Change DTO for the panel. This should only be called when the
   * type is of <code>ASSAY_IMAGE_PANEL_EDIT</code>
   * @param dtoTGCin the parent tumor genetic change dto for the image
   */
  public void setDTO(final MTBTumorGeneticChangesDTO dtoTGCin) {
    this.dtoTGC = dtoTGCin;
    Runnable runnable = new Runnable() {
    
      public void run() {
        progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
        progressMonitor.start("Loading Images: " + dtoTGC.getTumorGeneticChangesKey());
        try {
          
          lookupData(dtoTGC.getTumorGeneticChangesKey());
        } catch (Exception e) {
          e.printStackTrace();
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

      // images
      if (((DTOTableModel) fxtblImages.getModel()).hasBeenUpdated()) {
        return true;
      }

      return false;
    }

    return true;
  }

  /**
   * Handles all <code>ActionEvent</code>s that occur.
   * <p>
   * In this case, a right click on the genetics table will trigger a popup
   * menu to appear.
   *
   * @param evt the <code>ActionEvent</code>
   */
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().compareTo(ACTION_COMMAND_NEW) == 0) {
      addImage();
    } else if (evt.getActionCommand().compareTo(ACTION_COMMAND_EDIT) == 0) {
      editImage();
    } else if (evt.getActionCommand().compareTo(ACTION_COMMAND_DELETE) == 0) {
      removeImage();
    }
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

  /**
   * 
   *
   * @param lKey the TumorGeneticChange key to be looked up in the database
   */
  private void lookupData(long lKey) {

    AssayImagesDAO daoAssayImages = AssayImagesDAO.getInstance();
    TmrGntcCngAssayImageAssocDAO daoTGCAIa = TmrGntcCngAssayImageAssocDAO.getInstance();
    ArrayList<AssayImagesDTO> aiDTOs = new ArrayList<AssayImagesDTO>();

    try {

      updateProgress("Loading data...");

      List<TmrGntcCngAssayImageAssocDTO> dtos = daoTGCAIa.loadByTumorGeneticChangesKey(lKey);

      for (TmrGntcCngAssayImageAssocDTO dto : dtos) {
        AssayImagesDTO aiDTO =
                daoAssayImages.loadByPrimaryKey(dto.getAssayImagesKey());
        
        // if there is an association keep track of it so it won't be duplicated
        aiDTO.getDataBean().put(EIConstants.ASSAY_IMAGE_TGC_KEY, dto.getTumorGeneticChangesKey());
        aiDTOs.add(aiDTO);
      }

      if (aiDTOs.size() > 0) {
        ((AssayImageDTOTableModel<AssayImagesDTO>) fxtblImages.getModel()).setData(aiDTOs);
      }

      updateProgress("Images loaded!");

    } catch (Exception e) {
      e.printStackTrace();
      Utils.log(e);
    }
  }

  /**
   * Initialize the MXTable for assay images.
   * <p>
   * A MXTable is used to provide sorting capabilities.  A
   * <code>DTORenderer</code> is used as the default renderer to provide
   * visual feedback of the state of the data.
   */
  private void initImages() {
    // create the table model
    List<String> arrHeaders = new ArrayList<String>(4);
    arrHeaders.add("Image");
    arrHeaders.add("Key");
    arrHeaders.add("Caption");
    arrHeaders.add("Note");
    List arrNotes = new ArrayList();
    AssayImageDTOTableModel<AssayImagesDTO> tblmdlAssayImages =
            new AssayImageDTOTableModel<AssayImagesDTO>(arrNotes, arrHeaders);
    
    fxtblImages = new MXTable(tblmdlAssayImages);
    fxtblImages.setModel(tblmdlAssayImages);
    
    // allow the cell renderes manage highlighting
    fxtblImages.setDoHightlights(false);
    
    // set the table options
    fxtblImages.getColumnModel().getColumn(0).setCellRenderer(new ImageCellRenderer());
    fxtblImages.getColumnModel().getColumn(1).setCellRenderer(new AssayImageDTOCellRenderer());
    fxtblImages.getColumnModel().getColumn(2).setCellRenderer(new AssayImageDTOCellRenderer());
    fxtblImages.getColumnModel().getColumn(3).setCellRenderer(new AssayImageDTOCellRenderer());
    fxtblImages.setColumnSizes(new int[]{100, 0, 0, 0});
    fxtblImages.setRowHeight(100);
  
    fxtblImages.setSelectionBackground(
            EIConstants.COLOR_RESULTS_SELECTION_BG);
    fxtblImages.setSelectionForeground(
            EIConstants.COLOR_RESULTS_SELECTION_FG);
    fxtblImages.enableToolTip(0, false);
    fxtblImages.enableToolTip(1, true);


    // create the delete button
    JButton btnDelImage =
            new JButton(new ImageIcon(
            getClass().getResource(EIConstants.ICO_DELETE_16)));
    btnDelImage.setIconTextGap(0);
    btnDelImage.setMargin(new Insets(0, 0, 0, 0));
    btnDelImage.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent evt) {
        removeImage();
      }
    });

    // update the JScrollPane
    jspImages.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jspImages.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelImage);
    jspImages.setViewportView(fxtblImages);

    // revalidate the panel
    pnlImages.revalidate();
  }

  /**
   * Perform any custom initialization needed.
   */
  private void initCustom() {
    initImages();
  }

  /**
   * Add an image to the images table.
   */
  private void addImage() {
    try {
      AssayImageDetailDialog dlg =
              new AssayImageDetailDialog(
              EIGlobals.getInstance().getMainFrame(),
              true, AssayImageDetailDialog.IMAGE_ADD);

     
      Utils.centerComponentonScreen(dlg);
      
      dlg.setVisible(true);
      if (dlg.shouldSave()) {
        AssayImagesDTO dtoImage = dlg.getImagesDTO();

        AssayImageDTOTableModel<AssayImagesDTO> tblmdlImages =
                (AssayImageDTOTableModel<AssayImagesDTO>) fxtblImages.getModel();

        tblmdlImages.addRow(dtoImage);
      }

      dlg.dispose();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   */
  public void editImage() {
    try {
      final int nRow = fxtblImages.getSelectedRow();
      final AssayImageDTOTableModel<AssayImagesDTO> tblmdlImage =
              (AssayImageDTOTableModel<AssayImagesDTO>) fxtblImages.getModel();

      if (nRow >= 0) {


        AssayImageDetailDialog dlg =
                new AssayImageDetailDialog(EIGlobals.getInstance().getMainFrame(),
                true, AssayImageDetailDialog.IMAGE_EDIT);

        AssayImagesDTO dtoAssayImage =
                (AssayImagesDTO) tblmdlImage.getDTO(nRow);
        dlg.setDTO(dtoAssayImage);
        Utils.centerComponentonScreen(dlg);
        dlg.setVisible(true);

        if (dlg.shouldSave()) {
          AssayImagesDTO dtoImage = dlg.getImagesDTO();
          fxtblImages.getColumnModel().getColumn(0).setCellRenderer(new ImageCellRenderer());

          tblmdlImage.setRow(nRow, dtoImage);
          dlg.dispose();
          
          fxtblImages.revalidate();
          
        }
      }


    } catch (Exception e) {
      Utils.log(e);
    }
  }

  /**
   * Marks and image association as old and will remove it from the table
   * the row in the database will be deleted when the save buttion is cliked
   */
  
  public void removeImage() {
    int nRow = fxtblImages.getSelectedRow();

    if (nRow >= 0) {
      DTOTableModel tblmdlImages =
              (DTOTableModel) fxtblImages.getModel();
      tblmdlImages.removeRow(nRow);
    }
  }

  /**
   * 
   * This is an all or nothing update.  Either everything the user has
   * filled in gets comitted to the database or nothing does.
   */
  private void updateData()  {
    
    
    TmrGntcCngAssayImageAssocDAO daoTGCAI = TmrGntcCngAssayImageAssocDAO.getInstance();
    AssayImagesDAO daoAssayImages = AssayImagesDAO.getInstance();
   
    boolean commit = false;

    try {
      ///////////////////////////////////////////////////////////////////
      // Start the Transaction
      ///////////////////////////////////////////////////////////////////
      DAOManagerMTB.getInstance().beginTransaction();

     
      ///////////////////////////////////////////////////////////////////
      // save the images and then the image associations
      ///////////////////////////////////////////////////////////////////
      AssayImageDTOTableModel<AssayImagesDTO> modelImages =
              (AssayImageDTOTableModel<AssayImagesDTO>) fxtblImages.getModel();
      List<AssayImagesDTO> images = modelImages.getAllData();
      // new list to repopulate the table (won't include any deleted image assoc)
      List<AssayImagesDTO> newImages = new ArrayList<AssayImagesDTO>();
      if (images != null) {
        DataBean dBean = null;
        boolean keep;
        for (AssayImagesDTO dtoAI : images) {
          keep = true;
          dBean = dtoAI.getDataBean();
          
          // add the audit trail for the image
          // if the image is being updated it will have a create user
          if( (dtoAI.getCreateUser() == null) || (dtoAI.getCreateUser().length() == 0)){
            
            dtoAI.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoAI.setCreateDate(new Date());
          }
          if(dtoAI.isModified()){
            dtoAI.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoAI.setUpdateDate(new Date());
            
            //save it
            dtoAI = daoAssayImages.save(dtoAI);
          }

          // don't create an association if one allready exists
          if(!dBean.containsKey(EIConstants.ASSAY_IMAGE_TGC_KEY)){

            TmrGntcCngAssayImageAssocDTO dtoTGCAI = daoTGCAI.createTmrGntcCngAssayImageAssocDTO();

            dtoTGCAI.setAssayImagesKey(dtoAI.getAssayImagesKey());
            dtoTGCAI.setTumorGeneticChangesKey(this.dtoTGC.getTumorGeneticChangesKey());

            dtoTGCAI.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoTGCAI.setCreateDate(new Date());
            dtoTGCAI.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoTGCAI.setUpdateDate(new Date());

            daoTGCAI.save(dtoTGCAI);
            
            // add the key for the assocation so this AI won't get
            // reassociated if saved again
            dtoAI.getDataBean().put(EIConstants.ASSAY_IMAGE_TGC_KEY,
                    this.dtoTGC.getTumorGeneticChangesKey());
          }else{
            if(dtoAI.isOld()){
              TmrGntcCngAssayImageAssocDTO dtoX = daoTGCAI.createTmrGntcCngAssayImageAssocDTO();
              dtoX.setAssayImagesKey(dtoAI.getAssayImagesKey());
              dtoX.setTumorGeneticChangesKey(this.dtoTGC.getTumorGeneticChangesKey());
              daoTGCAI.deleteUsingTemplate(dtoX);
              
              // dont add this to the new table model.
              keep = false;
            }
          }
          

          ///////////////////////////////////////////////////////////
          // UPLOAD new images to server
          ///////////////////////////////////////////////////////////
        

          // this prevents images w/o thumbnails and thumbnails w/o images
          // from being loaded but also prevents a single image from being
          // changed once a pair has been created...
          if ((dBean.get(EIConstants.LOCAL_IMAGE_HIGHRES) != null)
            &&
             (dBean.get(EIConstants.LOCAL_IMAGE_THUMB) != null)){
                  

            Utils.log("UPLOADING new images to server: " 
                    + EIConstants.ASSAY_IMAGE_SERVER);

            SFTP sftp = new SFTP(EIConstants.ASSAY_IMAGE_SERVER, SFTP.PORT);
            
            sftp.login(EIConstants.FTP_USER, EIConstants.FTP_PASSWORD);
            
            Utils.log("Logging in to: " + EIConstants.ASSAY_IMAGE_SERVER);

            File fThumb = (File) dBean.get(EIConstants.LOCAL_IMAGE_THUMB);
            File fHighRes = (File) dBean.get(EIConstants.LOCAL_IMAGE_HIGHRES);

            if (fThumb != null) {
              String serverPathThumb = EIConstants.ASSAY_IMAGE_SERVER_PATH;
              Utils.log(fThumb.toString() + " ---> " + serverPathThumb);
              sftp.send(fThumb.toString(), serverPathThumb);
              Utils.log("thumbnail file uploaded successfully");
            }

            if (fHighRes != null) {
              String highRes = EIConstants.ASSAY_IMAGE_SERVER_PATH ;
              sftp.send(fHighRes.toString(), highRes);
              Utils.log("highRes file uploaded successfully");
            }

          }
          // the table will not contain deleted image assocations
          if(keep){
            newImages.add(dtoAI);
          }

         
        }
      }
      ///////////////////////////////////////////////////////////////////
      // COMMIT point reached
      ///////////////////////////////////////////////////////////////////
      updateProgress("All assay image data saved!");
      commit = true;
      
      // replace the ImageCellRenderer so the images are redrawn
      fxtblImages.getColumnModel().getColumn(0).setCellRenderer(new ImageCellRenderer());
      modelImages.setData(newImages);
      modelImages.fireTableDataChanged();
      
      // update the table in the tumorfrequencypanel
     
      MTBTumorGeneticChangesDTO dto = 
              (MTBTumorGeneticChangesDTO) tgcTableModel.getDTO(tgcTableRow);
      dto.getDataBean().put(EIConstants.ASSAY_IMAGE_COUNT, newImages.size());
      tgcTableModel.setRow(tgcTableRow, dto);
      
      
    } catch (Exception e) {
      Utils.log(e);
      Utils.showErrorDialog(e.getMessage(), e);
     
    } finally {
      try {
        ///////////////////////////////////////////////////////////////
        // End the Transaction
        ///////////////////////////////////////////////////////////////
        DAOManagerMTB.getInstance().endTransaction(commit);
        // should the ftp'd images be removed?
      } catch (Exception e2) {
        Utils.showErrorDialog("Unable to add Assay Images.", e2);
     
      }
      if (commit) {
        //switchFromAddToEdit();
      } else {
        Utils.showErrorDialog("Unable to add Assay Images.");
      }
    }
  }

 
  /**
   * Save the images and the associations with the cytognetic records.
   *
   */
   public void save() {
   
    
    if (fxtblImages.getCellEditor() != null) {
      fxtblImages.getCellEditor().stopCellEditing();
    }

    Runnable runnable = new Runnable() {

      public void run() {
        progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
        
        progressMonitor.start("Saving Images: " + dtoTGC.getTumorGeneticChangesKey());
        
        try {
          
            updateData();
         
        } catch (Exception e) {
          Utils.log(e);
        } finally {
          // to ensure that progress dlg is closed in case of
          // any exception
          progressMonitor.setCurrent("Done!",
                  progressMonitor.getTotal());
          
          // close the frame
          customInternalFrame.dispose();
        }
      }
    };

    new Thread(runnable).start();

    setUpdated(false);
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

        pnlImages = new javax.swing.JPanel();
        btnImageAdd = new javax.swing.JButton();
        jspImages = new javax.swing.JScrollPane();
        tblImages = new javax.swing.JTable();
        headerPanelImages = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        btnImagesEdit = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        pnlImages.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnImageAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnImageAdd.setText("Add");
        btnImageAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageAddActionPerformed(evt);
            }
        });

        tblImages.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspImages.setViewportView(tblImages);

        headerPanelImages.setDrawSeparatorUnderneath(true);
        headerPanelImages.setText("Assay Images");

        btnImagesEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Edit16.png"))); // NOI18N
        btnImagesEdit.setText("Edit");
        btnImagesEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImagesEditActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlImagesLayout = new org.jdesktop.layout.GroupLayout(pnlImages);
        pnlImages.setLayout(pnlImagesLayout);
        pnlImagesLayout.setHorizontalGroup(
            pnlImagesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlImagesLayout.createSequentialGroup()
                .addContainerGap(381, Short.MAX_VALUE)
                .add(btnImageAdd)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnImagesEdit)
                .addContainerGap())
            .add(pnlImagesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jspImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlImagesLayout.setVerticalGroup(
            pnlImagesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlImagesLayout.createSequentialGroup()
                .add(headerPanelImages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlImagesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnImagesEdit)
                    .add(btnImageAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
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
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlImages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(btnSave)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {btnCancel, btnSave}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnImagesEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImagesEditActionPerformed
      editImage();
    }//GEN-LAST:event_btnImagesEditActionPerformed

    private void btnImageAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageAddActionPerformed
      addImage();
    }//GEN-LAST:event_btnImageAddActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
      customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
      save();
    }//GEN-LAST:event_btnSaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnImageAdd;
    private javax.swing.JButton btnImagesEdit;
    private javax.swing.JButton btnSave;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelImages;
    private javax.swing.JScrollPane jspImages;
    private javax.swing.JPanel pnlImages;
    private javax.swing.JTable tblImages;
    // End of variables declaration//GEN-END:variables
}
