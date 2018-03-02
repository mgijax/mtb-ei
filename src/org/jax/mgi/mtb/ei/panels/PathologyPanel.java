/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/PathologyPanel.java,v 1.1 2007/04/30 15:50:55 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.dao.gen.mtb.ImagesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ImagesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyDTO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesProbesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesProbesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.dialogs.PathologyImageDetailDialog;
import org.jax.mgi.mtb.ei.models.DTOTableModel;
import org.jax.mgi.mtb.ei.models.PathologyImageDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.ImageCellRenderer;
import org.jax.mgi.mtb.ei.renderers.TextAreaRenderer;
import org.jax.mgi.mtb.ei.util.SFTP;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.gui.menu.MXHeaderMenuItem;
import org.jax.mgi.mtb.gui.menu.MXHtmlMenuItem;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;

/**
 * For inserting or updating <b>Pathology</b> information and the associated
 * data in the database.
 *
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/PathologyPanel.java,v 1.1 2007/04/30 15:50:55 mjv Exp
 */
public class PathologyPanel extends CustomPanel
        implements ActionListener {

    // -------------------------------------------------------------- Constants
    /**
     * Used in the constructor to specify this is a new pathology record.
     */
    public static final int PATHOLOGY_PANEL_NEW = 0;
    /**
     * Used in the constructor to specify this is an old pathology record.
     */
    public static final int PATHOLOGY_PANEL_EDIT = 1;
    // simple constants to identify an action event
    private final String ACTION_COMMAND_NEW = "new";
    private final String ACTION_COMMAND_EDIT = "edit";
    private final String ACTION_COMMAND_DELETE = "delete";
    // ----------------------------------------------------- Instance Variables
    // the PathologyDTO object
    private PathologyDTO dtoPathology = null;
    // the type of panel
    private int nType = PATHOLOGY_PANEL_NEW;
    // custom JTables for sorting purposes
    private MXTable fxtblImages = null;
    // progress monitor
    private MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors
    /**
     * Creates a new PathologyPanel.
     * <p>
     *
     * @param nType the type of panel, which is either
     *        <code>PATHOLOGY_PANEL_ADD</code> or <code>PATHOLOGY_PANEL_EDIT</code>
     */
    public PathologyPanel(int nType) {
        this.nType = nType;
        initComponents();
        initCustom();
    }

    // --------------------------------------------------------- Public Methods
    /**
     * Set the pathology key for the panel.  This should only be called when the
     * type is of <code>PATHOLOGY_PANEL_EDIT</code>, otherwise unknown behavior
     * will occur.
     * <p>
     
     *
     * @param lKey the pathology key to be looked up in the database
     */
    public void setKey(final long lKey) {
        Runnable runnable = new Runnable() {

            public void run() {
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                progressMonitor.start("Loading Pathology: " + lKey);
                try {
                    lookupData(lKey);
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
            // pathologist
            if (dtoPathology.getPathologistKey() != null) {
                String pJNum = txtPathologist.getText();
                long pKey = EIGlobals.getInstance().getRefByAcc(pJNum);

                if (dtoPathology.getPathologistKey().longValue() != pKey) {
                    return true;
                }
            }

            // contributor
            if (dtoPathology.getContributorKey() != null) {
                String cJNum = txtContributor.getText();
                long cKey = EIGlobals.getInstance().getRefByAcc(cJNum);

                if (dtoPathology.getContributorKey().longValue() != cKey) {
                    return true;
                }
            }

            // age at necropsy
            if (!StringUtils.equals(txtAgeNecropsy.getText(), StringUtils.nvl(dtoPathology.getAgeAtNecropsy(), ""))) {
                return true;
            }


            // diagnosis
            if (!StringUtils.equals(txtareaDiagnosis.getText(), StringUtils.nvl(dtoPathology.getDescription(), ""))) {
                return true;
            }

            // notes
            if (!StringUtils.equals(txtareaNotes.getText(), StringUtils.nvl(dtoPathology.getNote(), ""))) {
                return true;
            }

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
     * Lookup all related information in the database.
     *
     * @param lKey the key to be looked up in the database
     */
    private void lookupData(long lKey) {
        PathologyDAO daoPath = PathologyDAO.getInstance();
        PathologyImagesDAO daoPImages = PathologyImagesDAO.getInstance();
        ImagesDAO daoImages = ImagesDAO.getInstance();
     
        try {
            ///////////////////////////////////////////////////////////////////
            // get the pathology information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading pathology data...");

            dtoPathology = daoPath.loadByPrimaryKey(new Long(lKey));

            txtPathologyKey.setText(dtoPathology.getPathologyKey() + "");
            txtAgeNecropsy.setText(dtoPathology.getAgeAtNecropsy());
            txtareaNotes.setText(dtoPathology.getNote());
            txtareaDiagnosis.setText(dtoPathology.getDescription());

            long lPathologistKey = dtoPathology.getPathologistKey().longValue();
            String pJNum = EIGlobals.getInstance().getJNumByRef(lPathologistKey);
            txtPathologist.setText(pJNum);
            lookupPathologist();

            if (dtoPathology.getContributorKey() != null) {
                long lContributorKey = dtoPathology.getContributorKey().longValue();
                String cJNum = EIGlobals.getInstance().getJNumByRef(lContributorKey);
                txtContributor.setText(cJNum);
                lookupContributor();
            }

            updateProgress("Pathology data loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the image information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading images...");

            List<PathologyImagesDTO> listPathologyImages =
                    daoPImages.loadByPathologyKey(dtoPathology.getPathologyKey());

            for (PathologyImagesDTO dtoPI : listPathologyImages) {
                ImagesDTO dtoImage = daoImages.loadByPrimaryKey(dtoPI.getImagesKey());
                dtoPI.getDataBean().put(EIConstants.IMAGE_DTO, dtoImage);
            }

            if (listPathologyImages.size() > 0) {
                ((PathologyImageDTOTableModel<PathologyImagesDTO>) fxtblImages.getModel()).setData(listPathologyImages);
            }

            updateProgress("Images loaded!");

        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Initialize the MXTable for pathology images.
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
        List arrImages = new ArrayList();
        PathologyImageDTOTableModel<PathologyImagesDTO> tblmdlPathImages =
                new PathologyImageDTOTableModel<PathologyImagesDTO>(arrImages, arrHeaders);
        fxtblImages = new MXTable(tblmdlPathImages);
        fxtblImages.setModel(tblmdlPathImages);

        fxtblImages.setDoHightlights(false);

        // set the table options
        fxtblImages.getColumnModel().getColumn(0).setCellRenderer(new ImageCellRenderer());
        fxtblImages.getColumnModel().getColumn(1).setCellRenderer(new DTORenderer());
        fxtblImages.getColumnModel().getColumn(2).setCellRenderer(new TextAreaRenderer(100));
        fxtblImages.getColumnModel().getColumn(3).setCellRenderer(new TextAreaRenderer(100));
        fxtblImages.setColumnSizes(new int[]{100, 0, 0, 0});
        fxtblImages.setRowHeight(100);


        fxtblImages.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblImages.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblImages.enableToolTip(0, false);
        fxtblImages.enableToolTip(1, true);

        final JPopupMenu popupMenu = new JPopupMenu();
        MXHeaderMenuItem header = new MXHeaderMenuItem("Pathology Images Menu");
        popupMenu.add(header);
        MXHtmlMenuItem itemNew = new MXHtmlMenuItem("Add New Pathology Image...");
        itemNew.setIcon(new ImageIcon(getClass().getResource(EIConstants.ICO_NEW_16)));
        itemNew.setActionCommand(ACTION_COMMAND_NEW);
        itemNew.addActionListener(this);
        popupMenu.add(itemNew);
        MXHtmlMenuItem itemEdit = new MXHtmlMenuItem("Edit Pathology Image...");
        itemEdit.setIcon(new ImageIcon(getClass().getResource(EIConstants.ICO_EDIT_16)));
        itemEdit.setActionCommand(ACTION_COMMAND_EDIT);
        itemEdit.addActionListener(this);
        popupMenu.add(itemEdit);
        MXHtmlMenuItem itemDelete = new MXHtmlMenuItem("Delete Pathology Image");
        itemDelete.setIcon(new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));
        itemDelete.setActionCommand(ACTION_COMMAND_DELETE);
        itemDelete.addActionListener(this);
        popupMenu.add(itemDelete);

        // Set the component to show the popup popupMenu
        fxtblImages.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    Point pt = new Point(evt.getX(), evt.getY());
                    int nRow = fxtblImages.rowAtPoint(pt);
                    if (nRow >= 0) {
                        fxtblImages.setRowSelectionInterval(nRow, nRow);
                    }
                }
            }

            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    Point pt = new Point(evt.getX(), evt.getY());
                    int nRow = fxtblImages.rowAtPoint(pt);
                    if (nRow >= 0) {
                        fxtblImages.setRowSelectionInterval(nRow, nRow);
                    }
                }
            }
        });

        // create the note delete button
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
        // make it so the following fields accept numeric input only
        Utils.setNumericFilter(txtPathologyKey);

        // adjust components as needed
        if (nType == PATHOLOGY_PANEL_EDIT) {
            txtPathologyKey.setEditable(false);
            checkboxAutoAssign.setEnabled(false);
        }

        lblPathologistPreview.setText("");
        lblContributorPreview.setText("");

        // create the pathology dto
        dtoPathology = PathologyDAO.getInstance().createPathologyDTO();

        initImages();
    }

    /**
     * Add an image to the images table.
     */
    private void addImage() {
        try {
            PathologyImageDetailDialog dlg = new PathologyImageDetailDialog(null, true, PathologyImageDetailDialog.IMAGE_ADD);


            Utils.centerComponentonScreen(dlg);

            dlg.setVisible(true);
            if (dlg.shouldSave()) {
                ImagesDTO dtoImage = dlg.getImagesDTO();
                PathologyImagesDTO dtoPathImage = dlg.getPathologyImagesDTO();
                ProbeDTO[] probes = dlg.getProbeDTOs();

                File fMedRes = null;
                File fLowRes = null;
                File fHighRes = null;
                File fZoomify = null;

                Object obj = dtoImage.getDataBean().get(EIConstants.LOCAL_IMAGE);
                fMedRes = (obj == null) ? null : (File) obj;

                obj = dtoImage.getDataBean().get(EIConstants.LOCAL_IMAGE_HIGHRES);
                fHighRes = (obj == null) ? null : (File) obj;

                obj = dtoImage.getDataBean().get(EIConstants.LOCAL_IMAGE_THUMB);
                fLowRes = (obj == null) ? null : (File) obj;

                obj = dtoImage.getDataBean().get(EIConstants.LOCAL_IMAGE_ZOOMIFY);
                fZoomify = (obj == null) ? null : (File) obj;

                dtoPathImage.getDataBean().put(EIConstants.IMAGE_DTO, dtoImage);
                dtoPathImage.getDataBean().put(EIConstants.LOCAL_IMAGE, fMedRes);
                dtoPathImage.getDataBean().put(EIConstants.LOCAL_IMAGE_THUMB, fLowRes);
                dtoPathImage.getDataBean().put(EIConstants.IMAGE_PROBES_ARR_DTO, probes);


                PathologyImageDTOTableModel<PathologyImagesDTO> tblmdlImages =
                        (PathologyImageDTOTableModel<PathologyImagesDTO>) fxtblImages.getModel();

                tblmdlImages.addRow(dtoPathImage);
            }

            dlg.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: Bug in editing images...must note edit already edited or newly added images for now
     */
    public void editImage() {
       Utils.log("EDIT CLICKED");
        try {


            final int nRow = fxtblImages.getSelectedRow();
            final PathologyImageDTOTableModel<PathologyImagesDTO> tblmdlImage =
                    (PathologyImageDTOTableModel<PathologyImagesDTO>) fxtblImages.getModel();

            if (nRow >= 0) {
                PathologyImageDetailDialog dlg = new PathologyImageDetailDialog(null, true, PathologyImageDetailDialog.IMAGE_EDIT);
                PathologyImagesDTO dtoPathologyImage =
                        (PathologyImagesDTO) tblmdlImage.getDTO(nRow);
                dlg.setKey(dtoPathology.getPathologyKey().longValue(), dtoPathologyImage.getImagesKey().longValue());
                Utils.log("edit dialog componet created");
                
                Utils.centerComponentonScreen(dlg);
                dlg.setVisible(true);

                if (dlg.shouldSave()) {
                    ImagesDTO dtoImage = dlg.getImagesDTO();
                    PathologyImagesDTO dtoPathImage = dlg.getPathologyImagesDTO();
                    ProbeDTO[] probes = dlg.getProbeDTOs();

                    File fMedRes = null;
                    File fLowRes = null;
                    File fHighRes = null;
                    File fZoomify = null;

                    Object obj = dtoImage.getDataBean().get(EIConstants.LOCAL_IMAGE);
                    fMedRes = (obj == null) ? null : (File) obj;

                    obj = dtoImage.getDataBean().get(EIConstants.LOCAL_IMAGE_HIGHRES);
                    fHighRes = (obj == null) ? null : (File) obj;

                    obj = dtoImage.getDataBean().get(EIConstants.LOCAL_IMAGE_THUMB);
                    fLowRes = (obj == null) ? null : (File) obj;

                    obj = dtoImage.getDataBean().get(EIConstants.LOCAL_IMAGE_ZOOMIFY);
                    fZoomify = (obj == null) ? null : (File) obj;

                    dtoPathImage.getDataBean().put(EIConstants.IMAGE_DTO, dtoImage);
                    dtoPathImage.getDataBean().put(EIConstants.LOCAL_IMAGE, fMedRes);
                    dtoPathImage.getDataBean().put(EIConstants.LOCAL_IMAGE_THUMB, fLowRes);
                    dtoPathImage.getDataBean().put(EIConstants.IMAGE_PROBES_ARR_DTO, probes);
                    tblmdlImage.setRow(nRow, dtoPathImage);
                    dlg.dispose();

                }
            }


        } catch (Exception e) {
            Utils.log(e);
        }
    }

    public void removeImage() {
        int nRow = fxtblImages.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel tblmdlImages =
                    (MXDefaultTableModel) fxtblImages.getModel();
            tblmdlImages.removeRow(nRow);
            
           
        }
        updated = true;
    }

    /**
     * Insert the pathology information and associated data in the database.
     * <p>
     * This is an all or nothing insert.  Either everything the user has
     * filled in gets comitted to the database or nothing does.
     */
    private void insertData() {
        PathologyDAO daoPathology = PathologyDAO.getInstance();
        ImagesDAO daoImages = ImagesDAO.getInstance();
        PathologyImagesDAO daoPathologyImages = PathologyImagesDAO.getInstance();
        PathologyImagesProbesDAO daoPathologyImagesProbes = PathologyImagesProbesDAO.getInstance();

        boolean commit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the pathology record
            ///////////////////////////////////////////////////////////////////
            // populate the pathology record
            // pathology key
            dtoPathology = daoPathology.createPathologyDTO();

            boolean auto = checkboxAutoAssign.isSelected();
            String keyText = txtPathologyKey.getText();
            long key = -1;

            if (auto) {
            } else {
                key = Long.parseLong(keyText);
                dtoPathology.setPathologyKey(key);
            }

            // pathologist
            String pJNum = txtPathologist.getText();
            long pKey = EIGlobals.getInstance().getRefByAcc(pJNum);
            dtoPathology.setPathologistKey(pKey);

            // contributor
            String cJNum = txtContributor.getText();
            long cKey = EIGlobals.getInstance().getRefByAcc(cJNum);
            dtoPathology.setContributorKey(cKey);

            // age at necropsy
            dtoPathology.setAgeAtNecropsy(txtAgeNecropsy.getText().trim());

            // description
            dtoPathology.setDescription(txtareaDiagnosis.getText().trim());

            // note
            dtoPathology.setNote(txtareaNotes.getText().trim());

            // add the audit trail
            dtoPathology.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoPathology.setCreateDate(new Date());
            dtoPathology.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoPathology.setUpdateDate(new Date());

            Utils.log(dtoPathology.toXML());
            Utils.log("Saving Pathology...");
            dtoPathology = daoPathology.save(dtoPathology);
            Utils.log("Pathology saved!");
            Utils.log(dtoPathology.toXML());

            ///////////////////////////////////////////////////////////////////
            // save the images and then the pathology images
            ///////////////////////////////////////////////////////////////////
            PathologyImageDTOTableModel<PathologyImagesDTO> modelImages = (PathologyImageDTOTableModel<PathologyImagesDTO>) fxtblImages.getModel();
            List images = modelImages.getAllData();
            if (images != null) {
                PathologyImagesDTO[] arrTemp = (PathologyImagesDTO[]) images.toArray(new PathologyImagesDTO[images.size()]);
                for (int i = 0; i < arrTemp.length; i++) {
                    ImagesDTO dtoI = (ImagesDTO) arrTemp[i].getDataBean().get(EIConstants.IMAGE_DTO);

                    // add the audit trail for the image
                    dtoI.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                    dtoI.setCreateDate(new Date());
                    dtoI.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                    dtoI.setUpdateDate(new Date());

                    dtoI = daoImages.save(dtoI);

                    arrTemp[i].setPathologyKey(dtoPathology.getPathologyKey());
                    arrTemp[i].setImagesKey(dtoI.getImagesKey());

                    // add the audit trail for the pathology image
                    arrTemp[i].setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                    arrTemp[i].setCreateDate(new Date());
                    arrTemp[i].setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                    arrTemp[i].setUpdateDate(new Date());

                    arrTemp[i] = daoPathologyImages.save(arrTemp[i]);

                    if (arrTemp[i].getDataBean().get(EIConstants.IMAGE_PROBES_ARR_DTO) != null) {
                        ProbeDTO[] arrProbes = (ProbeDTO[]) arrTemp[i].getDataBean().get(EIConstants.IMAGE_PROBES_ARR_DTO);

                        for (int j = 0; j < arrProbes.length; j++) {
                            PathologyImagesProbesDTO dtoPIP = daoPathologyImagesProbes.createPathologyImagesProbesDTO();

                            dtoPIP.setPathologyKey(dtoPathology.getPathologyKey());
                            dtoPIP.setImagesKey(dtoI.getImagesKey());
                            dtoPIP.setProbeKey(arrProbes[j].getProbeKey());

                            // add the audit trail for the pathology image
                            dtoPIP.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                            dtoPIP.setCreateDate(new Date());
                            dtoPIP.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                            dtoPIP.setUpdateDate(new Date());

                            Utils.log(dtoPIP.toXML());

                            dtoPIP = daoPathologyImagesProbes.save(dtoPIP);
                        }
                    }

                    ///////////////////////////////////////////////////////////
                    // UPLOAD new images to server
                    ///////////////////////////////////////////////////////////

                    if ((dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE) != null)
                            || (dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_HIGHRES) != null)
                            || (dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_THUMB) != null)
                            || (dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_ZOOMIFY) != null)) {

                        Utils.log("UPLOADING new images to server: " + dtoI.getServer());


                        SFTP sftp = new SFTP(dtoI.getServer(), SFTP.PORT);


                        sftp.login(EIConstants.FTP_USER, EIConstants.FTP_PASSWORD);

                        Utils.log("Logging in to: " + dtoI.getServer());


                        File f = (File) dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE);
                        File fThumb = (File) dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_THUMB);
                        File fHighRes = (File) dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_HIGHRES);
                        File fZoomify = (File) dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_ZOOMIFY);

                        if (f != null) {
                            String serverPath = dtoI.getServerPath();
                            Utils.log(f.toString() + " ---> " + serverPath);
                            sftp.send(f.toString(), serverPath);
                            Utils.log("file uploaded successfullty");
                        }

                        if (fThumb != null) {
                            String serverPathThumb = dtoI.getServerPath();
                            Utils.log(fThumb.toString() + " ---> " + serverPathThumb);
                            sftp.send(fThumb.toString(), serverPathThumb);
                            Utils.log("thumbnail file uploaded successfullty");
                        }

                        if (fHighRes != null) {
                            String highRes = dtoI.getServerPath();
                            sftp.send(fHighRes.toString(), highRes);
                            Utils.log("highRes file uploaded successfullty");
                        }

                        if (fZoomify != null) {
                            List<File> arr = new ArrayList<File>();
                            visitAllDirsAndFiles(arr, fZoomify);

                            for (int z = 0; z < arr.size(); z++) {
                                File fTemp = (File) arr.get(z);

                                    //we need to check if the dir exists otherwise the is just a failure
                                    // if remote dir exists alert the EI user
                                if (fTemp.isDirectory()) {
                                    String dirToMake = fTemp.toString();
                                    dirToMake = dirToMake.substring(fZoomify.toString().lastIndexOf(File.separatorChar) + 1);
                                    dirToMake = dtoI.getServerPath() + "/" + dirToMake;
                                    dirToMake = dirToMake.replace('\\', '/');
                                    System.out.println("CREATING DIR:" + dirToMake);
                                    sftp.mkdir(dirToMake);
                                } else {
                                    String fileToMake = fTemp.toString();
                                    fileToMake = fileToMake.substring(fZoomify.toString().lastIndexOf(File.separatorChar) + 1);
                                    fileToMake = dtoI.getServerPath() + "/" + fileToMake;
                                    fileToMake = fileToMake.replace('\\', '/');
                                    System.out.println("UPLOADING:" + fileToMake);
                                    sftp.send(fTemp.toString(), fileToMake);
                                }
                            }
                        }
                    }
                }
            }
            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("All pathology data saved!");
            commit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog(e.getMessage(), e);
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to add Pathology.", e2);
            }
            if (commit) {
                switchFromAddToEdit();
            } else {
                Utils.showErrorDialog("Unable to add Pathology.");
            }
        }
    }

    /**
     * Update the pathology information and associated data in the database.
     * <p>
     * This is an all or nothing update.  Either everything the user has
     * updated gets committed to the database or nothing does.
     * 
     * No Code to delete records!!
     */
    private void updateData() {
        PathologyDAO daoPathology = PathologyDAO.getInstance();
        ImagesDAO daoImages = ImagesDAO.getInstance();
        PathologyImagesDAO daoPathologyImages = PathologyImagesDAO.getInstance();
        PathologyImagesProbesDAO daoPathologyImagesProbes = PathologyImagesProbesDAO.getInstance();

        boolean commit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the images and then the pathology images
            ///////////////////////////////////////////////////////////////////
            PathologyImageDTOTableModel<PathologyImagesDTO> modelImages = (PathologyImageDTOTableModel<PathologyImagesDTO>) fxtblImages.getModel();
            List<PathologyImagesDTO> images = modelImages.getAllData();
            if (images != null) {
                PathologyImagesDTO[] arrTemp = (PathologyImagesDTO[]) images.toArray(new PathologyImagesDTO[images.size()]);
                for (int i = 0; i < arrTemp.length; i++) {
                    ImagesDTO dtoI = (ImagesDTO) arrTemp[i].getDataBean().get(EIConstants.IMAGE_DTO);
                     if(arrTemp[i].isOld()){
                         //need to delete all associated data
                            // delete the probes
                             if (arrTemp[i].getDataBean().get(EIConstants.IMAGE_PROBES_ARR_DTO) != null) {
                                    // delete the current pathology images probes, so we can insert the new ones
                                    PathologyImagesProbesDTO dtoPIPToDelete = daoPathologyImagesProbes.createPathologyImagesProbesDTO();
                                    dtoPIPToDelete.setPathologyKey(dtoPathology.getPathologyKey());
                                    dtoPIPToDelete.setImagesKey(dtoI.getImagesKey());
                                    daoPathologyImagesProbes.deleteUsingTemplate(dtoPIPToDelete);
                             }
                             // the pathology images record
                             PathologyImagesDTO piDTO = daoPathologyImages.createPathologyImagesDTO();
                             piDTO.setPathologyKey(dtoPathology.getPathologyKey());
                             piDTO.setImagesKey(dtoI.getImagesKey());
                             daoPathologyImages.deleteUsingTemplate(piDTO);
                             
                             // the image
                             daoImages.deleteByPrimaryKey(dtoI.getImagesKey());
                             
                     }else{
                        // add the audit trail for the image
                        dtoI.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                        dtoI.setUpdateDate(new Date());

                        dtoI = daoImages.save(dtoI);

                        arrTemp[i].setPathologyKey(dtoPathology.getPathologyKey());
                        arrTemp[i].setImagesKey(dtoI.getImagesKey());

                        // add the audit trail for the pathology image
                        arrTemp[i].setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                        arrTemp[i].setUpdateDate(new Date());

                        arrTemp[i] = daoPathologyImages.save(arrTemp[i]);

                        if (arrTemp[i].getDataBean().get(EIConstants.IMAGE_PROBES_ARR_DTO) != null) {
                            ProbeDTO[] arrProbes = (ProbeDTO[]) arrTemp[i].getDataBean().get(EIConstants.IMAGE_PROBES_ARR_DTO);

                            // delete the current pathology images probes, so we can insert the new ones
                            PathologyImagesProbesDTO dtoPIPToDelete = daoPathologyImagesProbes.createPathologyImagesProbesDTO();
                            dtoPIPToDelete.setPathologyKey(dtoPathology.getPathologyKey());
                            dtoPIPToDelete.setImagesKey(dtoI.getImagesKey());
                            daoPathologyImagesProbes.deleteUsingTemplate(dtoPIPToDelete);

                            for (int j = 0; j < arrProbes.length; j++) {
                                PathologyImagesProbesDTO dtoPIP = daoPathologyImagesProbes.createPathologyImagesProbesDTO();

                                dtoPIP.setPathologyKey(dtoPathology.getPathologyKey());
                                dtoPIP.setImagesKey(dtoI.getImagesKey());
                                dtoPIP.setProbeKey(arrProbes[j].getProbeKey());

                                // add the audit trail for the pathology image
                                dtoPIP.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                                dtoPIP.setCreateDate(new Date());
                                dtoPIP.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                                dtoPIP.setUpdateDate(new Date());

                                Utils.log(dtoPIP.toXML());

                                dtoPIP = daoPathologyImagesProbes.save(dtoPIP);
                            }
                        }

                        ///////////////////////////////////////////////////////////
                        // UPLOAD new images to server
                        ///////////////////////////////////////////////////////////


                        if ((dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE) != null)
                                || (dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_HIGHRES) != null)
                                || (dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_THUMB) != null)
                                || (dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_ZOOMIFY) != null)) {

                            Utils.log("UPLOADING new images to server: " + dtoI.getServer());


                            SFTP sftp = new SFTP(dtoI.getServer(), SFTP.PORT);

                            sftp.login(EIConstants.FTP_USER, EIConstants.FTP_PASSWORD);

                            Utils.log("SFTPing in to: " + dtoI.getServer() + " as " + EIConstants.FTP_USER);



                            File f = (File) dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE);
                            File fThumb = (File) dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_THUMB);
                            File fHighRes = (File) dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_HIGHRES);
                            File fZoomify = (File) dtoI.getDataBean().get(EIConstants.LOCAL_IMAGE_ZOOMIFY);

                            if (f != null) {
                                String serverPath = dtoI.getServerPath();
                                Utils.log(f.toString() + " ---> " + serverPath);
                                sftp.send(f.toString(), serverPath);
                                Utils.log("file uploaded successfully");
                            }

                            if (fThumb != null) {
                                String serverPathThumb = dtoI.getServerPath();
                                Utils.log(fThumb.toString() + " ---> " + serverPathThumb);
                                sftp.send(fThumb.toString(), serverPathThumb);
                                Utils.log("thumbnail file uploaded successfullty");
                            }

                            if (fHighRes != null) {
                                String highRes = dtoI.getServerPath();
                                sftp.send(fHighRes.toString(), highRes);
                                Utils.log("highRes file uploaded successfullty");
                            }

                            if (fZoomify != null) {
                                List<File> arr = new ArrayList<File>();
                                visitAllDirsAndFiles(arr, fZoomify);

                                for (int z = 0; z < arr.size(); z++) {
                                    File fTemp = (File) arr.get(z);

                                    if (fTemp.isDirectory()) {
                                        String dirToMake = fTemp.toString();
                                        dirToMake = dirToMake.substring(fZoomify.toString().lastIndexOf(File.separatorChar) + 1);
                                        dirToMake = dtoI.getServerPath() + "/" + dirToMake;
                                        dirToMake = dirToMake.replace('\\', '/');
                                        sftp.mkdir(dirToMake);
                                        Utils.log("CREATED DIR:" + dirToMake);
                                    } else {
                                        String fileToMake = fTemp.getParent();

                                        fileToMake = fileToMake.substring(fZoomify.toString().lastIndexOf(File.separatorChar) + 1);
                                        fileToMake = dtoI.getServerPath() + "/" + fileToMake;
                                        fileToMake = fileToMake.replace('\\', '/');
                                        sftp.send(fTemp.toString(), fileToMake);
                                        Utils.log("UPLOADED:" + fileToMake + "/" + fTemp.toString());
                                    }
                                }
                            }
                        }
                    }// end else
                }
            
                // remove any deleted pathology images
            for(PathologyImagesDTO piDTO : images){
                if(piDTO.isOld()){
                    images.remove(piDTO);
                }
            }
            // update the images and the cell renderer to removed any deleted images
              modelImages.setData(images);
             fxtblImages.getColumnModel().getColumn(0).setCellRenderer(new ImageCellRenderer());
                modelImages.fireTableDataChanged();
            }

            ///////////////////////////////////////////////////////////////////
            // save the pathology
            ///////////////////////////////////////////////////////////////////
            // pathologist
            String pJNum = txtPathologist.getText();
            long pKey = EIGlobals.getInstance().getRefByAcc(pJNum);
            dtoPathology.setPathologistKey(pKey);

            // contributor
            String cJNum = txtContributor.getText();
            long cKey = EIGlobals.getInstance().getRefByAcc(cJNum);
            dtoPathology.setContributorKey(cKey);

            // age at necropsy
            dtoPathology.setAgeAtNecropsy(txtAgeNecropsy.getText().trim());

            // description
            dtoPathology.setDescription(txtareaDiagnosis.getText().trim());

            // note
            dtoPathology.setNote(txtareaNotes.getText().trim());

            // add the audit trail
            dtoPathology.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoPathology.setUpdateDate(new Date());

            dtoPathology = daoPathology.save(dtoPathology);

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            commit = true;
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showErrorDialog(e.getMessage(), e);
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to Pathology.",
                        e2);
            }
            if (commit) {
                this.setKey(dtoPathology.getPathologyKey().longValue());
            } else {
                Utils.showErrorDialog("Unable to save changes to Pathology.");
            }
        }
    }

    /**
     * Save the pathology information.
     * <p>
    
     */
    public void save() {
        // The following code saves the current value in the cell being edited
        // and stops the editing process:
        if (fxtblImages.getCellEditor() != null) {
            fxtblImages.getCellEditor().stopCellEditing();
        }

        Runnable runnable = new Runnable() {

            public void run() {
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try {
                    if (nType == PATHOLOGY_PANEL_NEW) {
                        progressMonitor.start("Inserting Pathology...");
                        insertData();
                    } else if (nType == PATHOLOGY_PANEL_EDIT) {
                        progressMonitor.start("Updating Pathology...");
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

    /**
     * Simple method to close the add form and switch to the edit form.  The
     * window location is tracked to make it seamless to the end user.
     */
    private void switchFromAddToEdit() {
        customInternalFrame.dispose();
        EIGlobals.getInstance().getMainFrame().launchPathologyEditWindow(
                dtoPathology.getPathologyKey().longValue(),
                customInternalFrame.getLocation());
    }

    private void loadPathologist() {
        String text = txtPathologist.getText();

        if (StringUtils.hasValue(text)) {
            long refKey = EIGlobals.getInstance().getRefByAcc(text);

            if (refKey != -1) {
                try {
                    ReferenceDAO daoRef = ReferenceDAO.getInstance();
                    ReferenceDTO dtoRef =
                            daoRef.loadByPrimaryKey(new Long(refKey));

                    lblPathologistPreview.setText(dtoRef.getShortCitation());
                } catch (Exception e) {
                    Utils.log(e);
                }
            }
        }
    }

    private void lookupPathologist() {
        loadPathologist();
    }

    private void loadContributor() {
        String text = txtContributor.getText();

        if (StringUtils.hasValue(text)) {
            long refKey = EIGlobals.getInstance().getRefByAcc(text);

            if (refKey != -1) {
                try {
                    ReferenceDAO daoRef = ReferenceDAO.getInstance();
                    ReferenceDTO dtoRef =
                            daoRef.loadByPrimaryKey(new Long(refKey));

                    lblContributorPreview.setText(dtoRef.getShortCitation());
                } catch (Exception e) {
                    Utils.log(e);
                }
            }
        }
    }

    private void lookupContributor() {
        loadContributor();
    }

    private void visitAllDirsAndFiles(List<File> arr, File f) {
        arr.add(f);
        if (f.isDirectory()) {
            String[] children = f.list();
            for (int i = 0; i < children.length; i++) {
                visitAllDirsAndFiles(arr, new File(f, children[i]));
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

        pnlPathologyInformation = new javax.swing.JPanel();
        lblPathologyKey = new javax.swing.JLabel();
        lblPathologist = new javax.swing.JLabel();
        txtPathologyKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        txtPathologist = new javax.swing.JTextField();
        lblContributor = new javax.swing.JLabel();
        lblAgeAtNecropsy = new javax.swing.JLabel();
        headerPanelPathology = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblPathologistPreview = new javax.swing.JLabel();
        txtContributor = new javax.swing.JTextField();
        lblContributorPreview = new javax.swing.JLabel();
        btnLookupPathologist = new javax.swing.JButton();
        btnLookupContributor = new javax.swing.JButton();
        txtAgeNecropsy = new javax.swing.JTextField();
        lblDiagnosis = new javax.swing.JLabel();
        jspDiagnosis = new javax.swing.JScrollPane();
        txtareaDiagnosis = new javax.swing.JTextArea();
        jspNotes = new javax.swing.JScrollPane();
        txtareaNotes = new javax.swing.JTextArea();
        lblNotes = new javax.swing.JLabel();
        lblMTBID = new javax.swing.JLabel();
        txtMTBID = new javax.swing.JTextField();
        checkboxAutoAssignMTBID = new javax.swing.JCheckBox();
        pnlImages = new javax.swing.JPanel();
        btnImageAdd = new javax.swing.JButton();
        jspImages = new javax.swing.JScrollPane();
        tblImages = new javax.swing.JTable();
        headerPanelImages = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        btnImagesEdit = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        pnlPathologyInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblPathologyKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblPathologyKey.setText("Pathology Key");

        lblPathologist.setText("Pathologist");

        txtPathologyKey.setColumns(10);
        txtPathologyKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        txtPathologist.setColumns(10);
        txtPathologist.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPathologistFocusLost(evt);
            }
        });

        lblContributor.setText("Contributor");

        lblAgeAtNecropsy.setText("Age at Necropsy");

        headerPanelPathology.setDrawSeparatorUnderneath(true);
        headerPanelPathology.setText("Pathology Information");

        lblPathologistPreview.setText(" ");

        txtContributor.setColumns(10);
        txtContributor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtContributorFocusLost(evt);
            }
        });

        lblContributorPreview.setText(" ");

        btnLookupPathologist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
        btnLookupPathologist.setText("Lookup");
        btnLookupPathologist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupPathologistActionPerformed(evt);
            }
        });

        btnLookupContributor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
        btnLookupContributor.setText("Lookup");
        btnLookupContributor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupContributorActionPerformed(evt);
            }
        });

        lblDiagnosis.setText("Diagnosis");

        txtareaDiagnosis.setColumns(20);
        txtareaDiagnosis.setLineWrap(true);
        txtareaDiagnosis.setRows(3);
        txtareaDiagnosis.setWrapStyleWord(true);
        jspDiagnosis.setViewportView(txtareaDiagnosis);

        txtareaNotes.setColumns(20);
        txtareaNotes.setLineWrap(true);
        txtareaNotes.setRows(3);
        txtareaNotes.setWrapStyleWord(true);
        jspNotes.setViewportView(txtareaNotes);

        lblNotes.setText("Notes");

        lblMTBID.setText("MTB ID");

        txtMTBID.setColumns(10);
        txtMTBID.setEditable(false);

        checkboxAutoAssignMTBID.setSelected(true);
        checkboxAutoAssignMTBID.setText("Auto Assign");
        checkboxAutoAssignMTBID.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssignMTBID.setEnabled(false);
        checkboxAutoAssignMTBID.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout pnlPathologyInformationLayout = new org.jdesktop.layout.GroupLayout(pnlPathologyInformation);
        pnlPathologyInformation.setLayout(pnlPathologyInformationLayout);
        pnlPathologyInformationLayout.setHorizontalGroup(
            pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelPathology, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
            .add(pnlPathologyInformationLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblDiagnosis)
                    .add(lblMTBID)
                    .add(lblPathologyKey)
                    .add(lblPathologist)
                    .add(lblContributor)
                    .add(lblAgeAtNecropsy)
                    .add(lblNotes))
                .add(6, 6, 6)
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlPathologyInformationLayout.createSequentialGroup()
                        .add(jspNotes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(pnlPathologyInformationLayout.createSequentialGroup()
                        .add(jspDiagnosis, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(pnlPathologyInformationLayout.createSequentialGroup()
                        .add(lblContributorPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(pnlPathologyInformationLayout.createSequentialGroup()
                        .add(lblPathologistPreview, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 352, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(pnlPathologyInformationLayout.createSequentialGroup()
                        .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txtPathologist)
                            .add(txtContributor)
                            .add(txtPathologyKey)
                            .add(txtMTBID))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(checkboxAutoAssignMTBID)
                            .add(checkboxAutoAssign)
                            .add(btnLookupPathologist)
                            .add(btnLookupContributor))
                        .add(143, 143, 143))
                    .add(pnlPathologyInformationLayout.createSequentialGroup()
                        .add(txtAgeNecropsy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(203, Short.MAX_VALUE))))
        );
        pnlPathologyInformationLayout.setVerticalGroup(
            pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPathologyInformationLayout.createSequentialGroup()
                .add(headerPanelPathology, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPathologyKey)
                    .add(txtPathologyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMTBID)
                    .add(txtMTBID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssignMTBID))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPathologist)
                    .add(txtPathologist, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnLookupPathologist))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblPathologistPreview)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblContributor)
                    .add(txtContributor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnLookupContributor))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblContributorPreview)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtAgeNecropsy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblAgeAtNecropsy))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblDiagnosis)
                    .add(jspDiagnosis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jspNotes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblNotes))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
        headerPanelImages.setText("Pathology Images");

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
            .add(headerPanelImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlImagesLayout.createSequentialGroup()
                .addContainerGap(354, Short.MAX_VALUE)
                .add(btnImageAdd)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnImagesEdit)
                .addContainerGap())
            .add(pnlImagesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jspImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
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
                .add(jspImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
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
                    .add(layout.createSequentialGroup()
                        .add(pnlPathologyInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
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
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlPathologyInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(pnlImages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(btnCancel)
                            .add(btnSave))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnImagesEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImagesEditActionPerformed
        editImage();
    }//GEN-LAST:event_btnImagesEditActionPerformed

    private void txtContributorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtContributorFocusLost
        Utils.fixJNumber(txtContributor);
    }//GEN-LAST:event_txtContributorFocusLost

    private void txtPathologistFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPathologistFocusLost
        Utils.fixJNumber(txtPathologist);
    }//GEN-LAST:event_txtPathologistFocusLost

    private void btnLookupPathologistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupPathologistActionPerformed
        lookupPathologist();
    }//GEN-LAST:event_btnLookupPathologistActionPerformed

    private void btnLookupContributorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupContributorActionPerformed
        lookupContributor();
    }//GEN-LAST:event_btnLookupContributorActionPerformed

    private void checkboxAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignActionPerformed
        if (checkboxAutoAssign.isSelected()) {
            txtPathologyKey.setEditable(false);
            txtPathologyKey.setText("");
        } else {
            txtPathologyKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

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
    private javax.swing.JButton btnLookupContributor;
    private javax.swing.JButton btnLookupPathologist;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private javax.swing.JCheckBox checkboxAutoAssignMTBID;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelImages;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelPathology;
    private javax.swing.JScrollPane jspDiagnosis;
    private javax.swing.JScrollPane jspImages;
    private javax.swing.JScrollPane jspNotes;
    private javax.swing.JLabel lblAgeAtNecropsy;
    private javax.swing.JLabel lblContributor;
    private javax.swing.JLabel lblContributorPreview;
    private javax.swing.JLabel lblDiagnosis;
    private javax.swing.JLabel lblMTBID;
    private javax.swing.JLabel lblNotes;
    private javax.swing.JLabel lblPathologist;
    private javax.swing.JLabel lblPathologistPreview;
    private javax.swing.JLabel lblPathologyKey;
    private javax.swing.JPanel pnlImages;
    private javax.swing.JPanel pnlPathologyInformation;
    private javax.swing.JTable tblImages;
    private javax.swing.JTextField txtAgeNecropsy;
    private javax.swing.JTextField txtContributor;
    private javax.swing.JTextField txtMTBID;
    private javax.swing.JTextField txtPathologist;
    private javax.swing.JTextField txtPathologyKey;
    private javax.swing.JTextArea txtareaDiagnosis;
    private javax.swing.JTextArea txtareaNotes;
    // End of variables declaration//GEN-END:variables
}
