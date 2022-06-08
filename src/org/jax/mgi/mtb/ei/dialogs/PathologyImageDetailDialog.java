/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/PathologyImageDetailDialog.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.Frame;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import org.apache.logging.log4j.Logger;
import org.jax.mgi.mtb.dao.gen.mtb.ImagesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ImagesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesProbesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyImagesProbesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.listeners.LVBeanListListener;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXImageViewerPreviewPanel;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * A custom <code>JDialog</code> used to display pathology information.
 *
 * @author mjv
 * @date 2007/04/30 15:50:45
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/PathologyImageDetailDialog.java,v 1.1 2007/04/30 15:50:45 mjv Exp
 * @see javax.swing.JDialog
 */
public class PathologyImageDetailDialog extends javax.swing.JDialog {

    // -------------------------------------------------------------- Constants

    public static int IMAGE_ADD = 0;
    public static int IMAGE_EDIT = 1;


    // ----------------------------------------------------- Instance Variables

    private final static Logger log =
            org.apache.logging.log4j.LogManager.getLogger(PathologyImageDetailDialog.class.getName());
    private int nType = IMAGE_ADD;
    private ImagesDTO dtoImage = null;
    private ImagesDTO dtoImageOriginal = null;
    private PathologyImagesDTO dtoPathologyImage = null;
    private PathologyImagesDTO dtoPathologyImageOriginal = null;
    private ProbeDTO[] dtoProbes = null;
    private File fileHighRes = null;
    private File fileMediumRes = null;
    private File fileLowRes = null;
    private File fileZoomifyPath = null;
    private boolean bSavedClicked = false;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form PathologyImageDetailDialog
     */
    public PathologyImageDetailDialog(Frame parent, boolean modal) {
        this(parent, modal, IMAGE_ADD);
    }

    /**
     * Creates new form PathologyImageDetailDialog
     */
    public PathologyImageDetailDialog(Frame parent, boolean modal, int nType) {
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
     * Return the <code>PathologyImagesDTO</code> object.
     *
     * @return the <code>PathologyImagesDTO</code> object
     */
    public PathologyImagesDTO getPathologyImagesDTO() {
        return dtoPathologyImage;
    }

    /**
     * Return the <code>ImagesDTO</code> object.
     *
     * @return the <code>ImagesDTO</code> object
     */
    public ImagesDTO getImagesDTO() {
        return dtoImage;
    }

    /**
     * Return an array of <code>ProbeDTO</code> objects.
     *
     * @return an array of <code>ProbeDTO</code> objects
     */
    public ProbeDTO[] getProbeDTOs() {
        return dtoProbes;
    }

    /**
     * Set the keys for the pathology image to perform the database lookup on
     * those keys.
     *
     * @param lPathologyKey the pathology key
     * @param lImageKey the image key
     */
    public void setKey(long lPathologyKey, long lImageKey) {
        this.setTitle("Edit Image Key: " + lImageKey);
        PathologyImagesDAO daoPathologyImages =
                                              PathologyImagesDAO.getInstance();

        ImagesDAO daoImages = ImagesDAO.getInstance();
        PathologyImagesProbesDAO daoPathologyImageProbes =
                                        PathologyImagesProbesDAO.getInstance();

        try {
            dtoPathologyImage =
                   daoPathologyImages.loadByPrimaryKey(new Long(lImageKey),
                                                       new Long(lPathologyKey));

            dtoPathologyImageOriginal = daoPathologyImages.createPathologyImagesDTO();
            dtoPathologyImageOriginal.copy(dtoPathologyImage);

            dtoImage = daoImages.loadByPrimaryKey(new Long(lImageKey));

            dtoImageOriginal = daoImages.createImagesDTO();
            dtoImageOriginal.copy(dtoImage);

            txtImageKey.setText(dtoImage.getImagesKey() + "");
            txtHighResName.setText(dtoImage.getHighResName());
            txtMediumResName.setText(dtoImage.getMediumResName());
            txtLowResName.setText(dtoImage.getLowResName());
            txtZoomifyPath.setText(dtoImage.getZoomifyPath());
            txtServer.setText(dtoImage.getServer());
            txtPath.setText(dtoImage.getServerPath());
            txtURL.setText(dtoImage.getUrl());
            txtURLPath.setText(dtoImage.getUrlPath());

            if (dtoPathologyImage.getFixativeKey() != null) {
                LVBeanListModel<String,Long> modelFixative =
                        (LVBeanListModel<String,Long>)comboFixative.getModel();

                for (int i = 0; i < modelFixative.getSize(); i++) {
                    LabelValueBean<String,Long> bean =
                            (LabelValueBean<String,Long>)modelFixative.getElementAt(i);
                    if (dtoPathologyImage.getFixativeKey().equals(bean.getValue())) {
                        comboFixative.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                comboFixative.setSelectedIndex(0);
            }

            if (dtoImage.getPrivateFlag().longValue() == 1) {
                checkboxPrivate.setSelected(true);
            } else {
                checkboxPrivate.setSelected(false);
            }

            txtareaCaption.setText(
                    StringUtils.nvl(dtoPathologyImage.getCaption(), ""));

            txtareaNotes.setText(
                    StringUtils.nvl(dtoPathologyImage.getNotes(), ""));

            txtMagnification.setText(
                    StringUtils.nvl(dtoPathologyImage.getMagnification(), ""));

            txtareaCopyright.setText(
                    StringUtils.nvl(dtoPathologyImage.getCopyright(), ""));

            txtStain.setText(
                    StringUtils.nvl(dtoPathologyImage.getStain(), ""));

            PathologyImagesProbesDTO dtoTemplate =
                      daoPathologyImageProbes.createPathologyImagesProbesDTO();

            dtoTemplate.setPathologyKey(lPathologyKey);
            dtoTemplate.setImagesKey(lImageKey);
            List<PathologyImagesProbesDTO> arrProbes =
                        daoPathologyImageProbes.loadUsingTemplate(dtoTemplate);

            LVBeanListModel<String,Long> modelProbes =
                    (LVBeanListModel<String,Long>)listProbes.getModel();

            for (int i = 0; i < arrProbes.size(); i++) {
                for (int j = 0; j < modelProbes.getSize(); j++) {
                    LabelValueBean<String,Long> bean = modelProbes.getElementAt(j);
                    Long l = new Long(bean.getValue());
                    if (arrProbes.get(i).getProbeKey().longValue() == l.longValue()) {
                        listProbes.addSelectionInterval(j, j);
                    }
                }
            }

            URL url = null;

            try {
                url = new URL(dtoImage.getUrl() + "/" +
                              dtoImage.getUrlPath() + "/" +
                              dtoImage.getMediumResName());
                if (log.isDebugEnabled()) {
                    log.debug("url=" + url.toString());
                }
            } catch (MalformedURLException mfue) {
                log.error("Unable to create URL.", mfue);
            }

            updateImage(url);
        } catch (Exception e) {
            log.error("Error retrieving pathology images.");
            log.error("pathologyKey=" + lPathologyKey);
            log.error("imageKey=" + lImageKey);
            log.error(e.getMessage(), e);
        }
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
            
        }
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Construct and/or update the <code>PathologyImagesDTO</code>,
     * <code>ImagesDTO</code>, and <code>ProbeDTO[]</code> objects.
     */
    private void save() {
        // validate the data being entered
        String strTemp = txtMediumResName.getText();
        if (!StringUtils.hasValue(strTemp)) {
            Utils.showErrorDialog("Please select an image.");
            txtMediumResName.requestFocus();
            return;
        }

        strTemp = txtLowResName.getText();
        if (!StringUtils.hasValue(strTemp)) {
            Utils.showErrorDialog("Please select a thumbnail image.");
            txtLowResName.requestFocus();
            return;
        }

        strTemp = txtServer.getText();
        if (!StringUtils.hasValue(strTemp)) {
            Utils.showErrorDialog("Please enter a value for the server.");
            txtServer.requestFocus();
            return;
        }

        strTemp = txtPath.getText();
        if (!StringUtils.hasValue(strTemp)) {
            Utils.showErrorDialog("Please enter a value for server path.");
            txtPath.requestFocus();
            return;
        }

        strTemp = txtURL.getText();
        if (!StringUtils.hasValue(strTemp)) {
            Utils.showErrorDialog("Please enter a value for the url.");
            txtURL.requestFocus();
            return;
        }

        strTemp = txtURLPath.getText();
        if (!StringUtils.hasValue(strTemp)) {
            Utils.showErrorDialog("Please enter a value for the url path.");
            txtURLPath.requestFocus();
            return;
        }
        
        strTemp = txtZoomifyPath.getText();
        if((strTemp != null) && (strTemp.indexOf("+")>-1)){
            Utils.showErrorDialog("Zoomify path can not contain '+'.");
            txtZoomifyPath.requestFocus();
            return;
        }

        this.bSavedClicked = true;

        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();

        // save image dto info
        //dtoImage.setImagesKey();
        dtoImage.setDescription("");
        dtoImage.setHighResName(txtHighResName.getText());
        dtoImage.setMediumResName(txtMediumResName.getText());
        dtoImage.setLowResName(txtLowResName.getText());
       
         dtoImage.setZoomifyPath(txtZoomifyPath.getText());
        
        dtoImage.setServer(txtServer.getText());
        dtoImage.setServerPath(txtPath.getText());
        dtoImage.setUrl(txtURL.getText());
        dtoImage.setUrlPath(txtURLPath.getText());

        if (checkboxPrivate.isSelected()) {
            dtoImage.setPrivateFlag(1l);
        } else {
            dtoImage.setPrivateFlag(0l);
        }

        dtoImage.setUpdateDate(new java.util.Date());
        dtoImage.setUpdateUser(dtoUser.getUserName());

        if (this.nType == IMAGE_ADD) {
            dtoImage.setCreateDate(new java.util.Date());
            dtoImage.setCreateUser(dtoUser.getUserName());
        }

        if (fileHighRes != null) {
            dtoImage.getDataBean().put(EIConstants.LOCAL_IMAGE_HIGHRES, fileHighRes);
        }

        if (fileMediumRes != null) {
            dtoImage.getDataBean().put(EIConstants.LOCAL_IMAGE, fileMediumRes);
        }

        if (fileLowRes != null) {
            dtoImage.getDataBean().put(EIConstants.LOCAL_IMAGE_THUMB, fileLowRes);
        }

        // allow zoomify path to be set to null
         dtoImage.getDataBean().put(EIConstants.LOCAL_IMAGE_ZOOMIFY, fileZoomifyPath);
        

        // save pathology image info
        //dtoPathologyImage.setPathologyKey();
        //dtoPathologyImage.setImagesKey();
        dtoPathologyImage.setCaption(txtareaCaption.getText());
        dtoPathologyImage.setCopyright(txtareaCopyright.getText());
        dtoPathologyImage.setMagnification(txtMagnification.getText());
        dtoPathologyImage.setNotes(txtareaNotes.getText());
        dtoPathologyImage.setStain(txtStain.getText().trim());
        dtoPathologyImage.setUpdateDate(new java.util.Date());
        dtoPathologyImage.setUpdateUser(dtoUser.getUserName());

        // fixative
        LVBeanListModel<String,Long> modelFixative =
                (LVBeanListModel<String,Long>)comboFixative.getModel();
        LabelValueBean<String,Long> beanFixative =
                (LabelValueBean<String,Long>)modelFixative.getElementAt(
                comboFixative.getSelectedIndex());

        if (dtoPathologyImage.getFixativeKey() != null) {
            if (!dtoPathologyImage.getFixativeKey().equals(beanFixative.getValue())) {
                dtoPathologyImage.setFixativeKey(beanFixative.getValue());
            }
        } else {
            if (comboFixative.getSelectedIndex() > 0) {
                dtoPathologyImage.setFixativeKey(
                        new Long(beanFixative.getValue()));
            }
        }

        if (this.nType == IMAGE_ADD) {
            dtoPathologyImage.setCreateDate(new java.util.Date());
            dtoPathologyImage.setCreateUser(dtoUser.getUserName());
        }

        // save pathology image probe info
        int indices[] = listProbes.getSelectedIndices();
        LVBeanListModel<String,Long> modelProbes = (LVBeanListModel<String,Long>)listProbes.getModel();
        dtoProbes = new ProbeDTO[indices.length];

        for (int i = 0; i < indices.length; i++) {
            LabelValueBean<String,Long> bean = modelProbes.getElementAt(indices[i]);
            log.debug(bean.toString());
            ProbeDTO dto = ProbeDAO.getInstance().createProbeDTO();
            dto.setProbeKey(bean.getValue());
            dtoProbes[i] = dto;
            log.debug(dtoProbes[i].toString());
        }

        this.setVisible(false);
    }

    /**
     * Initialize the custom components.
     *
     * @param type the type of dialog:
     *             <code>PathologyImageDetailDialog.IMAGE_ADD</code> or
     *             <code>PathologyImageDetailDialog.IMAGE_EDIT</code>
     */
    private void initCustom() {
        txtServer.setText(EIConstants.IMAGE_SERVER);
        txtPath.setText(EIConstants.IMAGE_SERVER_PATH);
        txtURL.setText(EIConstants.IMAGE_URL);
        txtURLPath.setText(EIConstants.IMAGE_URL_PATH);

        dtoImage = ImagesDAO.getInstance().createImagesDTO();
        dtoPathologyImage =
                   PathologyImagesDAO.getInstance().createPathologyImagesDTO();
        dtoProbes = new ProbeDTO[0];

        ///////////////////////////////////////////////////////////////////////
        // probes
        ///////////////////////////////////////////////////////////////////////
        final Map<Long,LabelValueBean<String,Long>> probes = EIGlobals.getInstance().getProbes();
        final List<LabelValueBean<String,Long>> arrProbes = new ArrayList<LabelValueBean<String,Long>>(probes.values());
        final LVBeanListModel<String,Long> modelProbes = new LVBeanListModel<String,Long>(arrProbes);
        final LVBeanListCellRenderer<String,Long> rendProbes = new LVBeanListCellRenderer<String,Long>();
        final LVBeanListListener<String,Long> listenerProbes = new LVBeanListListener<String,Long>();
        listProbes.addKeyListener(listenerProbes);
        listProbes.setModel(modelProbes);
        listProbes.setCellRenderer(rendProbes);

        ///////////////////////////////////////////////////////////////////////
        // fixatives
        ///////////////////////////////////////////////////////////////////////
        final Map<Long,LabelValueBean<String,Long>> mapFixatives = EIGlobals.getInstance().getFixatives();
        List<LabelValueBean<String,Long>> arrFixatives = new ArrayList<LabelValueBean<String,Long>>(mapFixatives.values());
        arrFixatives.add(0, new LabelValueBean<String,Long>("-- Select --", -1L));
        comboFixative.setModel(new LVBeanListModel<String,Long>(arrFixatives));
        comboFixative.setRenderer(new LVBeanListCellRenderer<String,Long>());
        comboFixative.setSelectedIndex(0);

        if (this.nType == IMAGE_ADD) {
            this.setTitle("Add Image");
        } else {
            this.setTitle("Edit Image");
        }
    }

    /**
     * Browse for the image file.
     */
    private void browseMediumResFile() {
        JFileChooser chooser = new JFileChooser();
        MXImageViewerPreviewPanel preview = new MXImageViewerPreviewPanel();
        chooser.setAccessory(preview);
        chooser.addPropertyChangeListener(preview);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Select Medium Resolution File");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Show open dialog; this method does not return until the dialog is closed
        if (chooser.showDialog(null, "Select") != JFileChooser.APPROVE_OPTION) {
            return; // only when user select valid dir, it can return approve_option
        }

        File f = chooser.getSelectedFile();

        if (f != null) {
            if (log.isDebugEnabled()) {
                log.debug("File Selected: " + f.toString());
            }
            fileMediumRes = f;
            txtMediumResName.setText(f.getName());
            updateImage(f);
        }
    }

    /**
     * Browse for the thumbnail image file.
     */
    private void browseLowResFile() {
        JFileChooser chooser = new JFileChooser();
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
            fileLowRes = f;
            txtLowResName.setText(f.getName());
        }
    }

    /**
     * Browse for the high res image file.
     */
    private void browseHighResFile() {
        JFileChooser chooser = new JFileChooser();
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
            fileHighRes = f;
            txtHighResName.setText(f.getName());
        }
    }

    /**
     * Browse for the high res image file.
     */
    private void browseZoomify() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Select Zoomify File's Directory");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show open dialog; this method does not return until the dialog is closed
        if (chooser.showDialog(null, "Select") != JFileChooser.APPROVE_OPTION) {
            return; // only when user select valid dir, it can return approve_option
        }

        File f = chooser.getSelectedFile();

        if (f != null) {
            fileZoomifyPath = f;
            txtZoomifyPath.setText(f.getName());
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

        pnlDetails = new javax.swing.JPanel();
        headerPanelDetails = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblImageKey = new javax.swing.JLabel();
        lblHighResName = new javax.swing.JLabel();
        lblMediumResName = new javax.swing.JLabel();
        lblLowResName = new javax.swing.JLabel();
        lblZoomifyPath = new javax.swing.JLabel();
        lblServer = new javax.swing.JLabel();
        lblPath = new javax.swing.JLabel();
        lblURL = new javax.swing.JLabel();
        lblURLPath = new javax.swing.JLabel();
        txtImageKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        txtHighResName = new javax.swing.JTextField();
        txtMediumResName = new javax.swing.JTextField();
        txtLowResName = new javax.swing.JTextField();
        btnBrowseHighRes = new javax.swing.JButton();
        btnBrowseMediumRes = new javax.swing.JButton();
        btnBrowseLowRes = new javax.swing.JButton();
        txtZoomifyPath = new javax.swing.JTextField();
        txtServer = new javax.swing.JTextField();
        txtPath = new javax.swing.JTextField();
        txtURL = new javax.swing.JTextField();
        txtURLPath = new javax.swing.JTextField();
        btnBrowseZoomify = new javax.swing.JButton();
        checkboxPrivate = new javax.swing.JCheckBox();
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
        lblMagnification = new javax.swing.JLabel();
        txtMagnification = new javax.swing.JTextField();
        lblStain = new javax.swing.JLabel();
        txtStain = new javax.swing.JTextField();
        lblProbes = new javax.swing.JLabel();
        jspProbes = new javax.swing.JScrollPane();
        listProbes = new javax.swing.JList();
        lblFixative = new javax.swing.JLabel();
        comboFixative = new javax.swing.JComboBox();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlDetails.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        headerPanelDetails.setDrawSeparatorUnderneath(true);
        headerPanelDetails.setText("Image Details");

        lblImageKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblImageKey.setText("Image Key");

        lblHighResName.setText("Optional TIFF");

        lblMediumResName.setText("Medium Res Name");

        lblLowResName.setText("Low Res Name");

        lblZoomifyPath.setText("Zoomify Path");

        lblServer.setText("Server");

        lblPath.setText("Path");

        lblURL.setText("URL");

        lblURLPath.setText("URL Path");

        txtImageKey.setColumns(10);
        txtImageKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        btnBrowseHighRes.setText("Browse");
        btnBrowseHighRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseHighResActionPerformed(evt);
            }
        });

        btnBrowseMediumRes.setText("Browse");
        btnBrowseMediumRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseMediumResActionPerformed(evt);
            }
        });

        btnBrowseLowRes.setText("Browse");
        btnBrowseLowRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseLowResActionPerformed(evt);
            }
        });

        btnBrowseZoomify.setText("Browse");
        btnBrowseZoomify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseZoomifyActionPerformed(evt);
            }
        });

        checkboxPrivate.setText("Private");
        checkboxPrivate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxPrivate.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout pnlDetailsLayout = new org.jdesktop.layout.GroupLayout(pnlDetails);
        pnlDetails.setLayout(pnlDetailsLayout);
        pnlDetailsLayout.setHorizontalGroup(
            pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblURLPath)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblImageKey)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblURL)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblPath)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblServer)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblZoomifyPath)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblLowResName)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblMediumResName)
                    .add(pnlDetailsLayout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(lblHighResName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtServer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                    .add(txtPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                    .add(txtURL, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                    .add(txtURLPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlDetailsLayout.createSequentialGroup()
                        .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(pnlDetailsLayout.createSequentialGroup()
                                    .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(txtLowResName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                                        .add(txtMediumResName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, txtHighResName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE))
                                    .add(6, 6, 6))
                                .add(pnlDetailsLayout.createSequentialGroup()
                                    .add(txtZoomifyPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                            .add(pnlDetailsLayout.createSequentialGroup()
                                .add(txtImageKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(checkboxAutoAssign)
                                .add(76, 76, 76)))
                        .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(checkboxPrivate)
                            .add(btnBrowseZoomify)
                            .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(btnBrowseMediumRes)
                                .add(btnBrowseHighRes)
                                .add(btnBrowseLowRes)))))
                .addContainerGap())
            .add(headerPanelDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
        );
        pnlDetailsLayout.setVerticalGroup(
            pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDetailsLayout.createSequentialGroup()
                .add(headerPanelDetails, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblImageKey)
                    .add(txtImageKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(checkboxPrivate))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnBrowseHighRes)
                    .add(lblHighResName)
                    .add(txtHighResName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMediumResName)
                    .add(btnBrowseMediumRes)
                    .add(txtMediumResName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblLowResName)
                    .add(btnBrowseLowRes)
                    .add(txtLowResName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblZoomifyPath)
                    .add(txtZoomifyPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnBrowseZoomify))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblServer)
                    .add(txtServer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPath)
                    .add(txtPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblURL)
                    .add(txtURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDetailsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblURLPath)
                    .add(txtURLPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pnlPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        headerPanelPreview.setDrawSeparatorUnderneath(true);
        headerPanelPreview.setText("Image Preview");

        imageViewer.setShowToolbar(false);

        org.jdesktop.layout.GroupLayout pnlPreviewLayout = new org.jdesktop.layout.GroupLayout(pnlPreview);
        pnlPreview.setLayout(pnlPreviewLayout);
        pnlPreviewLayout.setHorizontalGroup(
            pnlPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
            .add(pnlPreviewLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(imageViewer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlPreviewLayout.setVerticalGroup(
            pnlPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPreviewLayout.createSequentialGroup()
                .add(headerPanelPreview, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(imageViewer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addContainerGap())
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

        lblNotes.setText("Notes");

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

        lblMagnification.setText("Magnification");

        lblStain.setText("Stain");

        lblProbes.setText("Probes");

        jspProbes.setViewportView(listProbes);

        lblFixative.setText("Fixative");

        org.jdesktop.layout.GroupLayout pnlAdditionalLayout = new org.jdesktop.layout.GroupLayout(pnlAdditional);
        pnlAdditional.setLayout(pnlAdditionalLayout);
        pnlAdditionalLayout.setHorizontalGroup(
            pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelAdditional, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE)
            .add(pnlAdditionalLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblProbes)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStain)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblMagnification)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblCopyright)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblNotes)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblCaption)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblFixative))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jspProbes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                    .add(txtMagnification, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                    .add(jspCopyright, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                    .add(comboFixative, 0, 723, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtStain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                    .add(jspCaption, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jspNotes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE))
                .addContainerGap())
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMagnification)
                    .add(txtMagnification, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblStain)
                    .add(txtStain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblFixative)
                    .add(comboFixative, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlAdditionalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblProbes)
                    .add(jspProbes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnSave.setText("Update");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlAdditional, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(pnlDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlPreview, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnlPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAdditional, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void checkboxAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignActionPerformed
        if (checkboxAutoAssign.isSelected()) {
            txtImageKey.setEditable(false);
            txtImageKey.setText("");
        } else {
            txtImageKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnBrowseZoomifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseZoomifyActionPerformed
        browseZoomify();
    }//GEN-LAST:event_btnBrowseZoomifyActionPerformed

    private void btnBrowseLowResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseLowResActionPerformed
        browseLowResFile();
    }//GEN-LAST:event_btnBrowseLowResActionPerformed

    private void btnBrowseMediumResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseMediumResActionPerformed
        browseMediumResFile();
    }//GEN-LAST:event_btnBrowseMediumResActionPerformed

    private void btnBrowseHighResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseHighResActionPerformed
        browseHighResFile();
    }//GEN-LAST:event_btnBrowseHighResActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseHighRes;
    private javax.swing.JButton btnBrowseLowRes;
    private javax.swing.JButton btnBrowseMediumRes;
    private javax.swing.JButton btnBrowseZoomify;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private javax.swing.JCheckBox checkboxPrivate;
    private javax.swing.JComboBox comboFixative;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelAdditional;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelDetails;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelPreview;
    private org.jax.mgi.mtb.gui.MXImageViewer imageViewer;
    private javax.swing.JScrollPane jspCaption;
    private javax.swing.JScrollPane jspCopyright;
    private javax.swing.JScrollPane jspNotes;
    private javax.swing.JScrollPane jspProbes;
    private javax.swing.JLabel lblCaption;
    private javax.swing.JLabel lblCopyright;
    private javax.swing.JLabel lblFixative;
    private javax.swing.JLabel lblHighResName;
    private javax.swing.JLabel lblImageKey;
    private javax.swing.JLabel lblLowResName;
    private javax.swing.JLabel lblMagnification;
    private javax.swing.JLabel lblMediumResName;
    private javax.swing.JLabel lblNotes;
    private javax.swing.JLabel lblPath;
    private javax.swing.JLabel lblProbes;
    private javax.swing.JLabel lblServer;
    private javax.swing.JLabel lblStain;
    private javax.swing.JLabel lblURL;
    private javax.swing.JLabel lblURLPath;
    private javax.swing.JLabel lblZoomifyPath;
    private javax.swing.JList listProbes;
    private javax.swing.JPanel pnlAdditional;
    private javax.swing.JPanel pnlDetails;
    private javax.swing.JPanel pnlPreview;
    private javax.swing.JTextField txtHighResName;
    private javax.swing.JTextField txtImageKey;
    private javax.swing.JTextField txtLowResName;
    private javax.swing.JTextField txtMagnification;
    private javax.swing.JTextField txtMediumResName;
    private javax.swing.JTextField txtPath;
    private javax.swing.JTextField txtServer;
    private javax.swing.JTextField txtStain;
    private javax.swing.JTextField txtURL;
    private javax.swing.JTextField txtURLPath;
    private javax.swing.JTextField txtZoomifyPath;
    private javax.swing.JTextArea txtareaCaption;
    private javax.swing.JTextArea txtareaCopyright;
    private javax.swing.JTextArea txtareaNotes;
    // End of variables declaration//GEN-END:variables
}
