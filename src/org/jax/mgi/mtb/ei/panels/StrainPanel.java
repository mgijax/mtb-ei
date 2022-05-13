/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/StrainPanel.java,v 1.1 2007/04/30 15:50:58 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainDetailDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainGeneticsDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionMaxDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionMaxDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainNotesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainNotesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainReferencesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainReferencesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainSynonymsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainSynonymsDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeAssocDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.editors.JNumberCellEditor;
import org.jax.mgi.mtb.ei.editors.LVBeanCellEditor;
import org.jax.mgi.mtb.ei.handlers.LVBeanTransferHandler;
import org.jax.mgi.mtb.ei.listeners.LVBeanListListener;
import org.jax.mgi.mtb.ei.models.DTOTableModel;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.models.StrainAccessionDTOTableModel;
import org.jax.mgi.mtb.ei.models.StrainGeneticsDTOTableModel;
import org.jax.mgi.mtb.ei.models.StrainNotesDTOTableModel;
import org.jax.mgi.mtb.ei.models.StrainReferencesDTOTableModel;
import org.jax.mgi.mtb.ei.models.StrainSynonymsDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.LabelValueBeanComparator;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.gui.menu.MXHeaderMenuItem;
import org.jax.mgi.mtb.gui.menu.MXHtmlMenuItem;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;

/**
 * For inserting or updating <b>Strain</b> information and the associated
 * data in the database.
 * 
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/StrainPanel.java,v 1.1 2007/04/30 15:50:58 mjv Exp
 * @date 2007/04/30 15:50:58
 */
public class StrainPanel extends CustomPanel implements ActionListener {

    // -------------------------------------------------------------- Constants

    /**
     * Used in the constructor to specify this is a new strain.
     */
    public static final int STRAIN_PANEL_ADD = 1;

    /**
     * Used in the constructor to specify this is an old strain.
     */
    public static final int STRAIN_PANEL_EDIT = 2;

    // simple constant to identify an action event
    private final String ACTION_COMMAND_EDIT = "edit";


    // ----------------------------------------------------- Instance Variables

    // the StrainDTO object
    private StrainDTO dtoStrain = null;

    // detail strain information, reuse from WI
    private MTBStrainDetailDTO dtoStrainDetail = null;

    // keeps track of the strain types
    private List<StrainTypeDTO> arrDTOStrainTypes = null;

    // keeps track of the strain accession identifier
    private AccessionDTO dtoAccession = null;

    // for drag 'n drop
    private LVBeanTransferHandler transferHandlerLVBean;

    // the type of panel
    private int nType = STRAIN_PANEL_ADD;

    // custom JTables for sorting purposes
    private MXTable fxtblSynonyms = null;
    private MXTable fxtblGenetics = null;
    private MXTable fxtblReferences = null;
    private MXTable fxtblAccession = null;
    private MXTable fxtblNotes = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;


    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new StrainPanel.
     * <p>
     * If <code>nType = STRAIN_PANEL_ADD/code> an insert to the database of
     * the strain object is necessary.  Otherwise, the strain object already
     * exists in the database.
     *
     * @param nType the type of panel, which is either
     *        <code>STRAIN_PANEL_ADD</code> or <code>STRAIN_PANEL_EDIT</code>
     */
    public StrainPanel(int nType) {
        this.nType = nType;
        initComponents();
        initCustom();
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Set the strain key for the panel.  This should only be called when the
     * type is of <code>STRAIN_PANEL_EDIT</code>, otherwise unknown behavior
     * will occur.
     * <p>
     * <code>Strain</code> sata and associated data is retrieved from the
     * database during this method.
     *
     * @param lKey the strain key to be looked up in the database
     */
    public void setKey(final long lKey) {
        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                progressMonitor.start("Loading Strain: " + lKey);
                try{
                    lookupData(lKey);
                } catch (Exception e) {
                    Utils.log(e);
                } finally{
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
            // strain name
            if (!StringUtils.equals(StringUtils.nvl(dtoStrain.getName(), ""),
                    txtStrainName.getText())) {
                return true;
            }

            // strain description
            if (!StringUtils.equals(StringUtils.nvl(dtoStrain.getDescription(),
                    ""),
                    txtareaDescription.getText())) {

                return true;
            }

            // strain types
            LVBeanListModel<String,Long> modelTypes =
                    (LVBeanListModel<String,Long>)listStrainTypesSelected.getModel();
            LabelValueBean arrTypes[] =
                    new LabelValueBean[modelTypes.getSize()];

            for (int i = 0; i < arrTypes.length; i++) {
                arrTypes[i] = (LabelValueBean)modelTypes.getElementAt(i);
            }

            if (arrDTOStrainTypes != null) {
                LabelValueBean arrBeans[] =
                        new LabelValueBean[arrDTOStrainTypes.size()];
                for (int i = 0; i < arrDTOStrainTypes.size(); i++) {
                    arrBeans[i] =
                            new LabelValueBean(arrDTOStrainTypes.get(i).getType(),
                            arrDTOStrainTypes.get(i).getStrainTypeKey()+"");
                }

                Arrays.sort(arrTypes,
                        new LabelValueBeanComparator(
                        LabelValueBeanComparator.TYPE_VALUE));
                Arrays.sort(arrBeans,
                        new LabelValueBeanComparator(
                        LabelValueBeanComparator.TYPE_VALUE));

                if (!Arrays.equals(arrBeans, arrTypes)) {
                    return true;
                }
            }

            // strain family
            if (dtoStrain.getStrainFamilyKey() != null) {
                LVBeanListModel<String,Long> modelStrainFamily =
                        (LVBeanListModel<String,Long>)comboStrainFamily.getModel();
                LabelValueBean<String,Long> bean =
                        (LabelValueBean<String,Long>)modelStrainFamily.getElementAt(
                        comboStrainFamily.getSelectedIndex());
                Long l = bean.getValue();
                if (!dtoStrain.getStrainFamilyKey().equals(l)) {
                    return true;
                }
            } else {
                if (comboStrainFamily.getSelectedIndex() > 0) {
                    return true;
                }
            }

            // strain genetics
            if (((DTOTableModel)fxtblGenetics.getModel()).hasBeenUpdated()) {
                return true;
            }

            // strain notes
            if (((DTOTableModel)fxtblNotes.getModel()).hasBeenUpdated()) {
                return true;
            }

            // strain references
            if (((DTOTableModel)fxtblReferences.getModel()).hasBeenUpdated()) {
                return true;
            }

            // strain synonyms
            if (((DTOTableModel)fxtblSynonyms.getModel()).hasBeenUpdated()) {
                return true;
            }

            return false;
        }

        return true;
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
     * Lookup all strain related information in the database.
     *
     * @param lKey the strain key to be looked up in the database
     */
    private void lookupData(long lKey) {
        StrainDAO daoStrain = StrainDAO.getInstance();
        StrainNotesDAO daoStrainNotes = StrainNotesDAO.getInstance();
        StrainReferencesDAO daoStrainReferences =
                StrainReferencesDAO.getInstance();
        StrainSynonymsDAO daoStrainSynonyms = StrainSynonymsDAO.getInstance();
        ReferenceDAO daoReference = ReferenceDAO.getInstance();
        MTBReferenceUtilDAO daoReferenceUtil =
                MTBReferenceUtilDAO.getInstance();
        MTBStrainUtilDAO daoStrainUtil = MTBStrainUtilDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();
        Map<Long,LabelValueBean<String,Long>> mapSiteInfo = EIGlobals.getInstance().getSiteInfo();

        try {
            ///////////////////////////////////////////////////////////////////
            // get the strain
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading strain data...");
            dtoStrain = daoStrain.loadByPrimaryKey(new Long(lKey));

            txtStrainKey.setText(dtoStrain.getStrainKey()+"");
            txtStrainName.setText(dtoStrain.getName());
            lblPreview.setText("<html><body>" +
                    dtoStrain.getName() +
                    "</body></html>");
            txtareaDescription.setText(dtoStrain.getDescription());
            if (dtoStrain.getStrainFamilyKey() != null) {
                LVBeanListModel<String,Long> modelStrainFamily =
                        (LVBeanListModel<String,Long>)comboStrainFamily.getModel();

                for (int i = 0; i < modelStrainFamily.getSize(); i++) {
                    LabelValueBean<String,Long> bean =
                            (LabelValueBean<String,Long>)modelStrainFamily.getElementAt(i);
                    Long l = bean.getValue();

                    if (dtoStrain.getStrainFamilyKey().equals(l)) {
                        comboStrainFamily.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                comboStrainFamily.setSelectedIndex(0);
            }
            updateProgress("Strain data loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the strain types
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading strain type data...");

            LVBeanListModel<String,Long> modelSelected =
                    (LVBeanListModel<String,Long>)(listStrainTypesSelected.getModel());
            LVBeanListModel<String,Long> modelAvailable =
                    (LVBeanListModel<String,Long>)(listStrainTypesAvailable.getModel());

            arrDTOStrainTypes =
                    daoStrain.loadStrainTypeViaStrainTypeAssoc(dtoStrain);

            for (StrainTypeDTO dtoST : arrDTOStrainTypes) {
                for (int j = 0; j < modelAvailable.getSize(); j++) {

                    LabelValueBean<String,Long> bean =
                            (LabelValueBean<String,Long>)modelAvailable.getElementAt(j);
                    Long l = new Long(bean.getValue());

                    if (dtoST.getStrainTypeKey().equals(l)) {
                        // add to selected
                        modelSelected.addElement(bean);
                        // remove from available
                        modelAvailable.removeElement(bean);
                    }
                }
            }

            updateProgress("Strain type data loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the synonyms
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading strain synonyms...");

            List<StrainSynonymsDTO> arrSynonyms = 
                    daoStrainSynonyms.loadByStrainKey(dtoStrain.getStrainKey());

            for (StrainSynonymsDTO dtoSS : arrSynonyms) {
                DataBean sDTO = dtoSS.getDataBean();
                try {
                    sDTO.put(EIConstants.JNUM,
                            daoReferenceUtil.getJNumByReference(
                            dtoSS.getReferenceKey().longValue()));
                } catch (Exception e) {
                    Utils.log(e);
                }
                dtoSS.setDataBean(sDTO);
            }
            ((StrainSynonymsDTOTableModel)
            fxtblSynonyms.getModel()).setData(arrSynonyms);

            updateProgress("Strain synonyms loaded...");

            ///////////////////////////////////////////////////////////////////
            // get the notes
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading strain notes...");

            List<StrainNotesDTO> arrNotes =
                    daoStrainNotes.loadByStrainKey(dtoStrain.getStrainKey());

            for (StrainNotesDTO dtoSN : arrNotes) {
                DataBean sDTO = dtoSN.getDataBean();
                try{
                    sDTO.put(EIConstants.JNUM,
                            EIGlobals.getInstance().getJNumByRef(
                            dtoSN.getReferenceKey().longValue()));
                } catch (Exception e) {
                    Utils.log(e);
                }
                dtoSN.setDataBean(sDTO);
            }
            ((StrainNotesDTOTableModel)
            fxtblNotes.getModel()).setData(arrNotes);

            updateProgress("Strain notes loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the references
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading strain references...");

            List<StrainReferencesDTO> arrReferences =
                    daoStrainReferences.loadByStrainKey(dtoStrain.getStrainKey());

            for (StrainReferencesDTO dtoSR : arrReferences) {
                DataBean sDTO = dtoSR.getDataBean();
                try {
                    sDTO.put(EIConstants.JNUM,
                            EIGlobals.getInstance().getJNumByRef(
                            dtoSR.getReferenceKey().longValue()));

                    sDTO.put(EIConstants.REFERENCE,
                            daoReference.loadByPrimaryKey(
                            dtoSR.getReferenceKey()));
                } catch (Exception e) {
                    Utils.log(e);
                }
                dtoSR.setDataBean(sDTO);
            }
            ((StrainReferencesDTOTableModel)
            fxtblReferences.getModel()).setData(arrReferences);

            updateProgress("Strain references loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the accession information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading accession information...");

            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setObjectKey(dtoStrain.getStrainKey());
            dtoAccession.setSiteInfoKey(0);
            dtoAccession.setMTBTypesKey(1);
            dtoAccession.setPrefixPart("MTB:");
            dtoAccession = daoAccession.loadUniqueUsingTemplate(dtoAccession);
            txtMTBID.setText(dtoAccession.getAccID());

            dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_STRAIN);
            dtoAccession.setObjectKey(dtoStrain.getStrainKey());

            List<AccessionDTO> arrAccession =
                    daoAccession.loadUsingTemplate(dtoAccession);

            for (AccessionDTO dtoA : arrAccession) {
                DataBean sDTO = dtoA.getDataBean();

                try {
                    LabelValueBean<String,Long> bean =
                            (LabelValueBean<String,Long>)mapSiteInfo.get(
                            dtoA.getSiteInfoKey());
                    sDTO.put(EIConstants.SITE_INFO, bean);
                } catch (Exception e) {
                    Utils.log(e);
                }

                dtoA.setDataBean(sDTO);
            }
            ((StrainAccessionDTOTableModel)
            fxtblAccession.getModel()).setData(arrAccession);

            updateProgress("Accession information loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the genetics
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading strain genetics...");

            dtoStrainDetail = daoStrainUtil.getStrain(lKey);
            List<MTBStrainGeneticsDTO> arrGeneticsTemp = (ArrayList<MTBStrainGeneticsDTO>)dtoStrainDetail.getGenetics();
            List<MTBStrainGeneticsDTO> arrGenetics = new ArrayList<MTBStrainGeneticsDTO>();
            Map<String,MTBStrainGeneticsDTO> hashMap = new HashMap<String,MTBStrainGeneticsDTO>();

            for (int i = 0; i < arrGeneticsTemp.size(); i++) {
                MTBStrainGeneticsDTO dtoStrainGenetics = arrGeneticsTemp.get(i);
                DataBean dtoS = dtoStrainGenetics.getDataBean();
                try {
                    dtoS.put(EIConstants.JNUM,
                            daoReferenceUtil.getJNumByReference(
                            dtoStrainGenetics.getReferenceId()));
                } catch (Exception e) {
                    Utils.log(e);
                }
                dtoStrainGenetics.setDataBean(dtoS);
                dtoStrainGenetics.isNew(false);

                // TODO: QUERY BRINGINGS BACK DUPLICATES BECAUSE OF MARKER INFO
                //System.out.println(FieldPrinter.getFieldsAsString(tDTO));
                String strTemp = dtoStrainGenetics.getAllelePairId() + "";

                if (!hashMap.containsKey(strTemp)) {
                    //System.out.println("adding " + tempS);
                    hashMap.put(strTemp, dtoStrainGenetics);
                    arrGenetics.add(dtoStrainGenetics);
                }
            }
            ((StrainGeneticsDTOTableModel)
            fxtblGenetics.getModel()).setData(arrGenetics);

            updateProgress("Strain genetics loaded!");
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog("Error retrieving strain: " + lKey, e);
        }
    }

    /**
     * Initialize the JList for strain types that have been selected.
     * <p>
     * A custom <code>ListModel</code>, <code>ListCellRenderer</code>, and
     * <code>KeyListener</code> are used.
     */
    private void initStrainTypesSelected() {
        listStrainTypesSelected.setTransferHandler(transferHandlerLVBean);
        listStrainTypesSelected.setDragEnabled(true);
        listStrainTypesSelected.addKeyListener(new LVBeanListListener());
        listStrainTypesSelected.setModel(new LVBeanListModel<String,Long>(new ArrayList()));
        listStrainTypesSelected.setCellRenderer(new LVBeanListCellRenderer());
        listStrainTypesSelected.clearSelection();
    }

    /**
     * Initialize the JList for strain types that are available.
     * <p>
     * A custom <code>ListModel</code>, <code>CellRenderer</code>, and
     * <code>KeyListener</code> are used.
     */
    private void initStrainTypesAvailable() {
        final Map<Long,LabelValueBean<String,Long>> mapTypes = EIGlobals.getInstance().getStrainTypes();
        final List<LabelValueBean<String,Long>> arrStrainTypes = new ArrayList<LabelValueBean<String,Long>>(mapTypes.values());
        listStrainTypesAvailable.setTransferHandler(transferHandlerLVBean);
        listStrainTypesAvailable.setDragEnabled(true);
        listStrainTypesAvailable.addKeyListener(new LVBeanListListener<String,Long>());
        listStrainTypesAvailable.setModel(new LVBeanListModel<String,Long>(arrStrainTypes));
        listStrainTypesAvailable.setCellRenderer(new LVBeanListCellRenderer<String,Long>());
        listStrainTypesAvailable.setSelectedIndex(0);
        listStrainTypesAvailable.clearSelection();
    }

    /**
     * Initialize the JComboBox for strain families.
     * <p>
     * A custom <code>ComboBoxModel</code> and <code>ListCellRenderer</code>
     * are used.
     */
    private void initStrainFamily() {
        final Map<Long,LabelValueBean<String,Long>> mapFamilies = EIGlobals.getInstance().getStrainFamilies();
        List<LabelValueBean<String,Long>> arrFamilies = new ArrayList<LabelValueBean<String,Long>>(mapFamilies.values());
        arrFamilies.add(0, new LabelValueBean<String,Long>("-- Select --", -1L));
        comboStrainFamily.setModel(new LVBeanListModel<String,Long>(arrFamilies));
        comboStrainFamily.setRenderer(new LVBeanListCellRenderer<String,Long>());
        comboStrainFamily.setSelectedIndex(0);
    }

    /**
     * Initialize the MXTable for strain synonyms.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initStrainSynonyms() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(2);
        arrHeaders.add("JNumber");
        arrHeaders.add("Synonym");
        List arrSynonyms = new ArrayList();
        StrainSynonymsDTOTableModel tblmdlStrainSynonyms =
                new StrainSynonymsDTOTableModel(arrSynonyms, arrHeaders);
        fxtblSynonyms = new MXTable(tblmdlStrainSynonyms);
        fxtblSynonyms.setModel(tblmdlStrainSynonyms);

        // set the table options
        fxtblSynonyms.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblSynonyms.getColumnModel().getColumn(0).setCellEditor(
                new JNumberCellEditor());
        fxtblSynonyms.setColumnSizes(new int[]{100, 0});
        fxtblSynonyms.setAlternateRowHighlight(true);
        fxtblSynonyms.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblSynonyms.setAlternateRowHighlightCount(2);
        fxtblSynonyms.setStartHighlightRow(1);
        fxtblSynonyms.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblSynonyms.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblSynonyms.enableToolTip(0, false);
        fxtblSynonyms.enableToolTip(1, false);

        // create the synonym delete button
        JButton btnDelSynonym =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));
        btnDelSynonym.setIconTextGap(0);
        btnDelSynonym.setMargin(new Insets(0, 0, 0, 0));
        btnDelSynonym.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeSynonym();
            }
        });

        // update the JScrollPane
        jspSynonyms.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspSynonyms.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelSynonym);
        jspSynonyms.setViewportView(fxtblSynonyms);

        // revalidate the panel
        pnlSynonym.revalidate();
    }

    /**
     * Initialize the MXTable for strain notes.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initStrainNotes() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(2);
        arrHeaders.add("JNumber");
        arrHeaders.add("Note");
        List arrNotes = new ArrayList();
        StrainNotesDTOTableModel tblmdlStrainNotes =
                new StrainNotesDTOTableModel(arrNotes, arrHeaders);
        fxtblNotes = new MXTable(tblmdlStrainNotes);
        fxtblNotes.setModel(tblmdlStrainNotes);

        // set the table options
        fxtblNotes.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblNotes.getColumnModel().getColumn(0).setCellEditor(
                new JNumberCellEditor());
        fxtblNotes.setColumnSizes(new int[]{100, 0});
        fxtblNotes.setAlternateRowHighlight(true);
        fxtblNotes.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblNotes.setAlternateRowHighlightCount(2);
        fxtblNotes.setStartHighlightRow(1);
        fxtblNotes.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblNotes.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblNotes.enableToolTip(0, false);
        fxtblNotes.enableToolTip(1, true);

        // create the note delete button
        JButton btnDelNote =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));
        btnDelNote.setIconTextGap(0);
        btnDelNote.setMargin(new Insets(0, 0, 0, 0));
        btnDelNote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeNote();
            }
        });

        // update the JScrollPane
        jspNotes.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspNotes.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelNote);
        jspNotes.setViewportView(fxtblNotes);

        // revalidate the panel
        pnlNotes.revalidate();
    }

    /**
     * Initialize the MXTable for strain references.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initStrainReferences() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(2);
        arrHeaders.add("JNumber");
        arrHeaders.add("Short Citation");
        List arrReferences = new ArrayList();
        StrainReferencesDTOTableModel tblmdlStrainReferences =
                new StrainReferencesDTOTableModel(arrReferences, arrHeaders);
        fxtblReferences = new MXTable(tblmdlStrainReferences);
        fxtblReferences.setModel(tblmdlStrainReferences);

        // set the table options
        fxtblReferences.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblReferences.getColumnModel().getColumn(0).setCellEditor(
                new JNumberCellEditor());
        fxtblReferences.setColumnSizes(new int[]{100, 0});
        fxtblReferences.setAlternateRowHighlight(true);
        fxtblReferences.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblReferences.setAlternateRowHighlightCount(2);
        fxtblReferences.setStartHighlightRow(1);
        fxtblReferences.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblReferences.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblReferences.enableToolTip(0, false);
        fxtblReferences.enableToolTip(1, false);

        // create the note delete button
        JButton btnDelReference =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));

        btnDelReference.setIconTextGap(0);
        btnDelReference.setMargin(new Insets(0, 0, 0, 0));
        btnDelReference.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeReference();
            }
        });

        // update the JScrollPane
        jspReferences.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspReferences.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                btnDelReference);
        jspReferences.setViewportView(fxtblReferences);

        // revalidate the panel
        pnlReferences.revalidate();
    }

    /**
     * Initialize the MXTable for strain references.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     * <p>
     * A <code>MouseListener</code> is added to the table for a popup menu.
     */
    private void initStrainGenetics() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(4);
        arrHeaders.add("Allele Pair Key");
        arrHeaders.add("Allele1");
        arrHeaders.add("Allele2");
        arrHeaders.add("JNumber");
        List arrGenetics = new ArrayList();
        StrainGeneticsDTOTableModel tblmdlStrainGenetics
                = new StrainGeneticsDTOTableModel(arrGenetics, arrHeaders);

        fxtblGenetics = new MXTable(tblmdlStrainGenetics);
        fxtblGenetics.setModel(tblmdlStrainGenetics);

        // set the table options
        fxtblGenetics.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblGenetics.setAlternateRowHighlight(true);
        fxtblGenetics.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblGenetics.setAlternateRowHighlightCount(2);
        fxtblGenetics.setStartHighlightRow(1);
        fxtblGenetics.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblGenetics.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblReferences.enableToolTips(false);

        // update the JScrollPane
        jspGenetics.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspGenetics.setViewportView(fxtblGenetics);

        // create the popup menu
        final JPopupMenu popupMenu = new JPopupMenu();
        MXHeaderMenuItem header = new MXHeaderMenuItem("Strain Genetics Menu");
        popupMenu.add(header);
        MXHtmlMenuItem itemEdit = new MXHtmlMenuItem("Edit Allele Pair...");
        itemEdit.setIcon(
                new ImageIcon(getClass().getResource(EIConstants.ICO_EDIT_16)));
        itemEdit.setActionCommand(ACTION_COMMAND_EDIT);
        itemEdit.addActionListener(this);
        popupMenu.add(itemEdit);

        // create a popup menu for the strain genetics table
        fxtblGenetics.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    Point pt = new Point(evt.getX(), evt.getY());
                    int row = fxtblGenetics.rowAtPoint(pt);
                    if (row >= 0) {
                        fxtblGenetics.setRowSelectionInterval(row, row);
                    }
                }
            }
            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    Point pt = new Point(evt.getX(), evt.getY());
                    int row = fxtblGenetics.rowAtPoint(pt);
                    if (row >= 0) {
                        fxtblGenetics.setRowSelectionInterval(row, row);
                    }
                }
            }
        });
    }

    /**
     * Initialize the strain sites JComboBox.
     */
    private void initStrainSites() {
        Map<Long,LabelValueBean<String,Long>> mapSiteInfo = EIGlobals.getInstance().getSiteInfo();
        List<LabelValueBean<String,Long>> arrSiteInfo = new ArrayList(mapSiteInfo.values());
        arrSiteInfo.add(0, new LabelValueBean<String,Long>("-- Select --", -1L));
        comboSites.setModel(new LVBeanListModel<String,Long>(arrSiteInfo));
        comboSites.setRenderer(new LVBeanListCellRenderer());
        comboSites.setSelectedIndex(0);
    }

    /**
     * Initialize the MXTable for strain accession information.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initStrainAccessionInfo() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(2);
        arrHeaders.add("Site");
        arrHeaders.add("Accession ID");
        List arrAccession = new ArrayList();
        StrainAccessionDTOTableModel tblmdlStrainAccession =
                new StrainAccessionDTOTableModel(arrAccession, arrHeaders);
        fxtblAccession = new MXTable(tblmdlStrainAccession);
        fxtblAccession.setModel(tblmdlStrainAccession);

        // set the table options
        fxtblAccession.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblAccession.getColumnModel().getColumn(0).setCellEditor(
                new LVBeanCellEditor(EIGlobals.getInstance().getSiteInfo()));

        fxtblAccession.setColumnSizes(new int[]{100, 0});
        fxtblAccession.setAlternateRowHighlight(true);
        fxtblAccession.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblAccession.setAlternateRowHighlightCount(2);
        fxtblAccession.setStartHighlightRow(1);
        fxtblAccession.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblAccession.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblAccession.enableToolTip(0, false);
        fxtblAccession.enableToolTip(1, false);

        // create the note delete button
        JButton btnDelAccession =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));

        btnDelAccession.setIconTextGap(0);
        btnDelAccession.setMargin(new Insets(0, 0, 0, 0));
        btnDelAccession.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeAccession();
            }
        });

        // update the JScrollPane
        jspAccession.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspAccession.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelAccession);
        jspAccession.setViewportView(fxtblAccession);

        // revalidate the panel
        pnlAccession.revalidate();
    }

    /**
     * Perform any custom initialization needed.
     */
    private void initCustom() {
        // create the factory

        // make it so the following fields accept numeric input only
        Utils.setNumericFilter(txtStrainKey);

        // make it so the following fields accept up to 255 characters
        //Utils.setTextLimit(txtareaDescription, 255);

        // adjust components as needed
        if (nType == STRAIN_PANEL_ADD) {
            ;
        } else if (nType == STRAIN_PANEL_EDIT) {
            txtStrainKey.setEditable(false);
            checkboxAutoAssign.setEnabled(false);
        }

        lblPreview.setText("");

        // create a handler for drag and drop of strain type data
        transferHandlerLVBean = new LVBeanTransferHandler();

        // create the strain dto
        dtoStrain = StrainDAO.getInstance().createStrainDTO();

        initStrainTypesAvailable();
        initStrainTypesSelected();
        initStrainFamily();
        initStrainSynonyms();
        initStrainNotes();
        initStrainReferences();
        initStrainGenetics();
        initStrainSites();
        initStrainAccessionInfo();
    }

    /**
     * Add a JNumber to the reference table only if the JNumber has a value,
     * is in a valid format, and exists in the database.
     */
    private void addReference() {
        String strJNumber = txtJNumberReference.getText().trim();
        long lRefKey = -1;

        // validate that a JNumber has been entered
        if (!StringUtils.hasValue(strJNumber)) {
            Utils.showErrorDialog("Please enter a reference.");
            txtJNumberReference.requestFocus();
            return;
        }

        // validate that the JNumber is valid
        try {
            lRefKey = EIGlobals.getInstance().getRefByAcc(strJNumber);

            if (lRefKey <= 0) {
                JOptionPane.showMessageDialog(null,
                        strJNumber +
                        " is not a valid JNumber.");
                txtJNumberReference.requestFocus();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, strJNumber +
                    " is not a valid JNumber.");
            txtJNumberReference.requestFocus();
            return;
        }

        // get the table model
        StrainReferencesDTOTableModel tblmdlStrainReferences =
                (StrainReferencesDTOTableModel)fxtblReferences.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();


        // attempt to pull the reference data from the database so the short
        // citation will be displayed
        ReferenceDTO dtoRef = null;
        try {
            ReferenceDAO daoRef = ReferenceDAO.getInstance();
            dtoRef = daoRef.loadByPrimaryKey(new Long(lRefKey));
        } catch (Exception e) {
            Utils.showErrorDialog(
                    "The JNumber you entered does not exist.\n\n" +
                    e.getMessage(), e);
            return;
        }

        // create the dto
        StrainReferencesDTO dtoStrainReference =
                StrainReferencesDAO.getInstance().createStrainReferencesDTO();

        dtoStrainReference.setStrainKey(dtoStrain.getStrainKey());
        dtoStrainReference.setReferenceKey(dtoRef.getReferenceKey());
        dtoStrainReference.setCreateUser(dtoUser.getUserName());
        dtoStrainReference.setCreateDate(dNow);
        dtoStrainReference.setUpdateUser(dtoUser.getUserName());
        dtoStrainReference.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoStrainReference.getDataBean().put(EIConstants.JNUM, strJNumber);
        dtoStrainReference.getDataBean().put(EIConstants.REFERENCE, dtoRef);

        // add it to the table
        tblmdlStrainReferences.addRow(dtoStrainReference);

        Utils.scrollToVisible(fxtblReferences,
                fxtblReferences.getRowCount() - 1, 0);
    }

    /**
     * Add accession information to the accession table.  A site and an
     * accession id must be filled in.
     */
    private void addAccession() {
        String strAccID = txtAccessionID.getText().trim();
        long lSiteInfoKey = -1;

        // validate that a site has been selected
        if (comboSites.getSelectedIndex() <= 0) {
            Utils.showErrorDialog("Please select a Site.");
            comboSites.requestFocus();
            return;
        }

        // validate that an accession ID has been entered
        if (!StringUtils.hasValue(strAccID)) {
            Utils.showErrorDialog("Please enter an Accession ID.");
            txtAccessionID.requestFocus();
            return;
        }

        // retrieve the value to use in the database from the combo box
        LVBeanListModel<String,Long> modelSites = (LVBeanListModel<String,Long>)comboSites.getModel();
        LabelValueBean<String,Long> bean = modelSites.getElementAt(comboSites.getSelectedIndex());
        lSiteInfoKey = (new Long(bean.getValue())).longValue();

        // get the model
        StrainAccessionDTOTableModel tblmdlStrainAccession =
                (StrainAccessionDTOTableModel)fxtblAccession.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        AccessionDTO dtoAccession =
                AccessionDAO.getInstance().createAccessionDTO();

        dtoAccession.setAccID(strAccID);
        dtoAccession.setObjectKey(dtoStrain.getStrainKey());
        dtoAccession.setMTBTypesKey(EIConstants.MTB_TYPE_STRAIN);
        dtoAccession.setSiteInfoKey(lSiteInfoKey);
        dtoAccession.setCreateUser(dtoUser.getUserName());
        dtoAccession.setCreateDate(dNow);
        dtoAccession.setUpdateUser(dtoUser.getUserName());
        dtoAccession.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoAccession.getDataBean().put(EIConstants.SITE_INFO, bean);

        // add it to the table
        tblmdlStrainAccession.addRow(dtoAccession);

        Utils.scrollToVisible(fxtblAccession,
                fxtblAccession.getRowCount() - 1, 0);
    }

    /**
     * Add a note to the strain notes table provided a note has been filled in.
     * The JNumber must have a value, be in a valid format, and  exist in the
     * database.
     */
    private void addNote() {
        String strNote = txtNote.getText().trim();
        String strJNumber = txtJNumberNote.getText().trim();
        long lRefKey = -1;

        // validate that a note has been entered
        if (!StringUtils.hasValue(strNote)) {
            Utils.showErrorDialog("Please enter a note.");
            txtNote.requestFocus();
            return;
        }

        // validate that a JNumber has been entered
        if (!StringUtils.hasValue(strJNumber)) {
            Utils.showErrorDialog("Please enter a JNumber for a note.");
            txtJNumberNote.requestFocus();
            return;
        }

        // validate that the JNumber is valid
        try {
            lRefKey = EIGlobals.getInstance().getRefByAcc(strJNumber);

            if (lRefKey <= 0) {
                JOptionPane.showMessageDialog(null,
                        strJNumber +
                        " is not a valid JNumber.");
                txtJNumberNote.requestFocus();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, strJNumber +
                    " is not a valid JNumber.");
            txtJNumberNote.requestFocus();
            return;
        }

        // get the table model
        StrainNotesDTOTableModel tblmdlStrainNotes =
                (StrainNotesDTOTableModel)fxtblNotes.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        StrainNotesDTO dtoStrainNotes =
                StrainNotesDAO.getInstance().createStrainNotesDTO();

        dtoStrainNotes.setStrainKey(dtoStrain.getStrainKey());
        dtoStrainNotes.setReferenceKey(lRefKey);
        dtoStrainNotes.setNote(strNote);
        dtoStrainNotes.setCreateUser(dtoUser.getUserName());
        dtoStrainNotes.setCreateDate(dNow);
        dtoStrainNotes.setUpdateUser(dtoUser.getUserName());
        dtoStrainNotes.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoStrainNotes.getDataBean().put(EIConstants.JNUM, strJNumber);

        // add it to the table
        tblmdlStrainNotes.addRow(dtoStrainNotes);

        Utils.scrollToVisible(fxtblNotes, fxtblNotes.getRowCount() - 1, 0);
    }

    /**
     * Add a synonym to the strain synonyms table provided a synonym has been
     * filled in.  The JNumber must have a value, be in a valid format, and
     * exist in the database.
     */
    private void addSynonym() {
        String strSynonym = txtSynonym.getText().trim();
        String strJNumber = txtJNumberSynonym.getText().trim();
        long lRefKey = -1;

        // validate the a synonym has been entered
        if (!StringUtils.hasValue(strSynonym)) {
            Utils.showErrorDialog("Please enter a synonym.");
            txtSynonym.requestFocus();
            return;
        }

        // validate the a JNumber has been entered
        if (!StringUtils.hasValue(strJNumber)) {
            Utils.showErrorDialog("Please enter a JNumber for a synonym.");
            txtJNumberSynonym.requestFocus();
            return;
        }

        // validate that the JNumber is valid
        try {
            lRefKey = EIGlobals.getInstance().getRefByAcc(strJNumber);

            if (lRefKey <= 0) {
                JOptionPane.showMessageDialog(null, strJNumber +
                        " is not a valid JNumber.");
                txtJNumberReference.requestFocus();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, strJNumber +
                    " is not a valid JNumber.");
            txtJNumberSynonym.requestFocus();
            return;
        }

        // get the table model
        StrainSynonymsDTOTableModel tblmdlStrainSynonyms =
                (StrainSynonymsDTOTableModel)fxtblSynonyms.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        StrainSynonymsDTO dtoStrainSynonym =
                StrainSynonymsDAO.getInstance().createStrainSynonymsDTO();

        dtoStrainSynonym.setStrainKey(dtoStrain.getStrainKey());
        dtoStrainSynonym.setName(strSynonym);
        dtoStrainSynonym.setReferenceKey(lRefKey);
        dtoStrainSynonym.setCreateUser(dtoUser.getUserName());
        dtoStrainSynonym.setCreateDate(dNow);
        dtoStrainSynonym.setUpdateUser(dtoUser.getUserName());
        dtoStrainSynonym.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoStrainSynonym.getDataBean().put(EIConstants.JNUM, strJNumber);

        // add it to the table
        tblmdlStrainSynonyms.addRow(dtoStrainSynonym);

        Utils.scrollToVisible(fxtblSynonyms,
                fxtblSynonyms.getRowCount() - 1, 0);
    }

    /**
     * Insert the strain information and associated data in the database.
     * <p>
     * This is an all or nothing insert.  Either everything the user has
     * filled in gets comitted to the database or nothing does.
     */
    private void insertData() {
        StrainDAO daoStrain = StrainDAO.getInstance();
        StrainTypeAssocDAO daoStrainTypeAssoc =
                StrainTypeAssocDAO.getInstance();
        StrainNotesDAO daoStrainNotes = StrainNotesDAO.getInstance();
        StrainReferencesDAO daoStrainReferences =
                StrainReferencesDAO.getInstance();
        StrainSynonymsDAO daoStrainSynonyms = StrainSynonymsDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();
        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the strain
            ///////////////////////////////////////////////////////////////////
            dtoStrain = daoStrain.createStrainDTO();

            updateProgress("Parsing strain data...");

            // strain key
            boolean bAutoGenerate = checkboxAutoAssign.isSelected();
            String strStrainKey = txtStrainKey.getText();
            long lStrainKey = -1;

            if (!bAutoGenerate) {
                lStrainKey = Long.parseLong(strStrainKey);
                dtoStrain.setStrainKey(lStrainKey);
            }

            // strain name
            String strTemp = txtStrainName.getText();
            dtoStrain.setName(StringUtils.hasValue(strTemp) ? strTemp : null);

            // strain description
            strTemp = txtareaDescription.getText();
            dtoStrain.setDescription(
                    StringUtils.hasValue(strTemp) ? strTemp : null);

            // strain family
            LVBeanListModel<String,Long> modelStrainFamily =
                    (LVBeanListModel<String,Long>)comboStrainFamily.getModel();
            LabelValueBean<String,Long> beanStrainFamily =
                    modelStrainFamily.getElementAt(
                            comboStrainFamily.getSelectedIndex());

            if (dtoStrain.getStrainFamilyKey() != null) {
                Long l = new Long(beanStrainFamily.getValue());
                if (!dtoStrain.getStrainFamilyKey().equals(l)) {
                    dtoStrain.setStrainFamilyKey(l);
                }
            } else {
                if (comboStrainFamily.getSelectedIndex() > 0) {
                    dtoStrain.setStrainFamilyKey(
                            new Long(beanStrainFamily.getValue()));
                }
            }

            // add the audit trail
            dtoStrain.setCreateUser(dtoUser.getUserName());
            dtoStrain.setCreateDate(dNow);
            dtoStrain.setUpdateUser(dtoUser.getUserName());
            dtoStrain.setUpdateDate(dNow);

            updateProgress("Saving strain data...");
            dtoStrain = daoStrain.save(dtoStrain);
            
            if ((dtoStrain == null) || (dtoStrain.getStrainKey() == null)) {
                throw new Exception("Unable to determine Strain key.");
            }
            updateProgress("Strain data saved!");

            
            ///////////////////////////////////////////////////////////////////
            // save the associated strain types
            ///////////////////////////////////////////////////////////////////
            updateProgress("Parsing strain type data...");

            LVBeanListModel<String,Long> modelTypes =
                    (LVBeanListModel<String,Long>)listStrainTypesSelected.getModel();
            LabelValueBean<String,Long> arrTypes[] =
                    new LabelValueBean[modelTypes.getSize()];

            for (int i = 0; i < modelTypes.getSize(); i++) {
                arrTypes[i] = (LabelValueBean)modelTypes.getElementAt(i);
            }

            if (arrTypes.length > 0) {
                daoStrainTypeAssoc.deleteByStrainKey(dtoStrain.getStrainKey());

                List<StrainTypeAssocDTO> arrAssoc = new ArrayList<StrainTypeAssocDTO>();

                for (int i = 0; i < arrTypes.length; i++) {
                    StrainTypeAssocDTO dtoTemp =
                            StrainTypeAssocDAO.getInstance().
                            createStrainTypeAssocDTO();
                    dtoTemp.setStrainKey(dtoStrain.getStrainKey());
                    dtoTemp.setStrainTypeKey(arrTypes[i].getValue());
                    dtoTemp.setCreateUser(dtoUser.getUserName());
                    dtoTemp.setCreateDate(dNow);
                    dtoTemp.setUpdateUser(dtoUser.getUserName());
                    dtoTemp.setUpdateDate(dNow);
                    arrAssoc.add(dtoTemp);
                }

                updateProgress("Saving strain type data...");
                daoStrainTypeAssoc.save(arrAssoc);
                updateProgress("Strain type data saved!");
            }

            ///////////////////////////////////////////////////////////////////
            // save the strain synonyms
            ///////////////////////////////////////////////////////////////////
            updateProgress("Parsing strain synonym data...");

            StrainSynonymsDTOTableModel modelSynonyms =
                    (StrainSynonymsDTOTableModel)fxtblSynonyms.getModel();
            List<StrainSynonymsDTO> arrSynonyms = modelSynonyms.getAllData();
            if (arrSynonyms != null) {
                for (StrainSynonymsDTO dtoSS : arrSynonyms) {
                    dtoSS.setStrainKey(dtoStrain.getStrainKey());
                }

                updateProgress("Saving strain synonym data...");
                daoStrainSynonyms.save(arrSynonyms);
                updateProgress("Strain synonym data saved!");

            }

            ///////////////////////////////////////////////////////////////////
            // save the strain notes
            ///////////////////////////////////////////////////////////////////
            updateProgress("Parsing strain note data...");

            StrainNotesDTOTableModel modelNotes =
                    (StrainNotesDTOTableModel)fxtblNotes.getModel();
            List<StrainNotesDTO> arrNotes = modelNotes.getAllData();
            if (arrNotes != null) {
                for (StrainNotesDTO dtoSN : arrNotes) {
                    dtoSN.setStrainKey(dtoStrain.getStrainKey());
                }

                updateProgress("Saving strain synonym data...");
                daoStrainNotes.save(arrNotes);
                updateProgress("Strain synonym data saved!");
            }

            ///////////////////////////////////////////////////////////////////
            // save the strain references
            ///////////////////////////////////////////////////////////////////
            updateProgress("Parsing strain reference data...");

            StrainReferencesDTOTableModel modelReferences =
                    (StrainReferencesDTOTableModel)fxtblReferences.getModel();
            List<StrainReferencesDTO> arrReferences = modelReferences.getAllData();
            if (arrReferences != null) {
                for (StrainReferencesDTO dtoSR : arrReferences) {
                    dtoSR.setStrainKey(dtoStrain.getStrainKey());
                }

                updateProgress("Saving strain reference data...");
                daoStrainReferences.save(arrReferences);
                updateProgress("Strain reference data saved!");
            }

            ///////////////////////////////////////////////////////////////////
            // save the strain accession information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Parsing strain accession data...");
            
            AccessionMaxDAO amaxDAO = AccessionMaxDAO.getInstance();
            List<AccessionMaxDTO> maxList = amaxDAO.loadAll();
            AccessionMaxDTO maxDTO = maxList.get(0);
            Long max = maxDTO.getMaxNumericPart();
            max++;
            maxDTO.setMaxNumericPart(max);
            amaxDAO.save(maxDTO);

            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setAccID("MTB:"+max); 
            dtoAccession.setObjectKey(dtoStrain.getStrainKey());
            dtoAccession.setSiteInfoKey(0);
            dtoAccession.setMTBTypesKey(1);
            dtoAccession.setPrefixPart("MTB:");
            dtoAccession.setNumericPart(max);
            dtoAccession.setCreateUser(dtoUser.getUserName());
            dtoAccession.setCreateDate(dNow);
            dtoAccession.setUpdateUser(dtoUser.getUserName());
            dtoAccession.setUpdateDate(dNow);
            daoAccession.save(dtoAccession);

            StrainAccessionDTOTableModel modelAcc =
                    (StrainAccessionDTOTableModel)fxtblAccession.getModel();
            List<AccessionDTO> arrAccession = modelAcc.getAllData();
            if (arrAccession!= null) {
                for (AccessionDTO dtoA : arrAccession) {
                    dtoA.setObjectKey(dtoStrain.getStrainKey());
                }

                updateProgress("Saving strain accession data...");
                daoAccession.save(arrAccession);
                updateProgress("Strain accession data saved!");
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("All strain data saved!");
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
                Utils.showErrorDialog("Unable to add Strain.", e2);
            }
            if (bCommit) {
                switchFromAddToEdit();
            } else {
                Utils.showErrorDialog("Unable to add Strain.");
            }
        }
    }

    /**
     * Update the strain information and associated data in the database.
     * <p>
     * This is an all or nothing update.  Either everything the user has
     * updated gets comitted to the database or nothing does.
     */
    private void updateData() {
        StrainDAO daoStrain = StrainDAO.getInstance();
        StrainTypeAssocDAO daoStrainTypeAssoc =
                StrainTypeAssocDAO.getInstance();
        StrainNotesDAO daoStrainNotes = StrainNotesDAO.getInstance();
        StrainReferencesDAO daoStrainReferences =
                StrainReferencesDAO.getInstance();
        StrainSynonymsDAO daoStrainSynonyms = StrainSynonymsDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();
        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the associated strain types
            ///////////////////////////////////////////////////////////////////
            updateProgress("Parsing strain type data...");

            LVBeanListModel<String,Long> modelTypes =
                    (LVBeanListModel<String,Long>)listStrainTypesSelected.getModel();
            LabelValueBean<String,Long> arrBeans[] =
                    new LabelValueBean[modelTypes.getSize()];

            for (int i = 0; i < arrBeans.length; i++) {
                arrBeans[i] = (LabelValueBean)modelTypes.getElementAt(i);
            }

            LabelValueBean<String,Long> arrTypes[] =
                    new LabelValueBean[arrDTOStrainTypes.size()];

            for (int i = 0; i < arrDTOStrainTypes.size(); i++) {
                arrTypes[i] =
                        new LabelValueBean(arrDTOStrainTypes.get(i).getType(),
                        arrDTOStrainTypes.get(i).getStrainTypeKey()+"");
            }

            Arrays.sort(arrBeans,
                    new LabelValueBeanComparator(
                    LabelValueBeanComparator.TYPE_VALUE));
            Arrays.sort(arrTypes,
                    new LabelValueBeanComparator(
                    LabelValueBeanComparator.TYPE_VALUE));

            if (!Arrays.equals(arrTypes, arrBeans)) {
                if (arrBeans.length > 0) {
                    daoStrainTypeAssoc.deleteByStrainKey(
                            dtoStrain.getStrainKey());

                    List<StrainTypeAssocDTO> arrAssoc = new ArrayList<StrainTypeAssocDTO>();

                    for (int i = 0; i < arrBeans.length; i++) {
                        StrainTypeAssocDTO dtoTemp =
                                StrainTypeAssocDAO.getInstance().
                                createStrainTypeAssocDTO();
                        dtoTemp.setStrainKey(dtoStrain.getStrainKey());
                        dtoTemp.setStrainTypeKey(arrBeans[i].getValue());
                        dtoTemp.setCreateUser(dtoUser.getUserName());
                        dtoTemp.setCreateDate(dNow);
                        dtoTemp.setUpdateUser(dtoUser.getUserName());
                        dtoTemp.setUpdateDate(dNow);
                        arrAssoc.add(dtoTemp);
                    }

                    updateProgress("Saving strain type data...");
                    daoStrainTypeAssoc.save(arrAssoc);
                    updateProgress("Strain type data saved!");
                }
            }

            ///////////////////////////////////////////////////////////////////
            // save the strain synonyms
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving strain synonym data...");

            StrainSynonymsDTOTableModel modelSynonyms =
                    (StrainSynonymsDTOTableModel)fxtblSynonyms.getModel();
            List<StrainSynonymsDTO> arrSynonyms = modelSynonyms.getAllData();
            daoStrainSynonyms.save(arrSynonyms);

            updateProgress("Strain synonym data saved!");

            ///////////////////////////////////////////////////////////////////
            // save the strain notes
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving strain note data...");

            StrainNotesDTOTableModel modelNotes =
                    (StrainNotesDTOTableModel)fxtblNotes.getModel();
            List<StrainNotesDTO> arrNotes = modelNotes.getAllData();
            daoStrainNotes.save(arrNotes);

            updateProgress("Strain note data saved!");

            ///////////////////////////////////////////////////////////////////
            // save the strain references
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving strain reference data...");

            StrainReferencesDTOTableModel modelReferences =
                    (StrainReferencesDTOTableModel)fxtblReferences.getModel();
            List<StrainReferencesDTO> arrReferences = modelReferences.getAllData();
            daoStrainReferences.save(arrReferences);

            updateProgress("Strain reference data saved!");

            ///////////////////////////////////////////////////////////////////
            // save the strain accession information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving strain accession data...");

            ///////////////////////////////////////////////////////////////////
            // save the accession information
            ///////////////////////////////////////////////////////////////////
/*
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setObjectKey(dtoStrain.getStrainKey());
            dtoAccession.setSiteInfoKey(0);
            dtoAccession.setMTBTypesKey(5);
            dtoAccession.setPrefixPart("MTB:");
            dtoAccession.setNumericPart(dtoStrain.getStrainKey());
            daoAccession.loadUniqueUsingTemplate(dtoAccession);

            if (!StringUtils.equals(txtMTBID.getText(), dtoAccession.getAccID())) {
                dtoAccession.setAccID(txtAccessionID.getText());
                dtoAccession.setUpdateUser(dtoUser.getUserName());
                dtoAccession.setUpdateDate(dNow);
                daoAccession.save(dtoAccession);
            }
*/
            StrainAccessionDTOTableModel modelAcc =
                    (StrainAccessionDTOTableModel)fxtblAccession.getModel();
            List<AccessionDTO> arrAccession = modelAcc.getAllData();
            daoAccession.save(arrAccession);

            updateProgress("Strain accession data saved!");

            ///////////////////////////////////////////////////////////////////
            // save the strain
            ///////////////////////////////////////////////////////////////////
            updateProgress("Parsing strain data...");

            // strain key
            String strTemp = txtStrainKey.getText();

            // strain name
            strTemp = txtStrainName.getText();
            dtoStrain.setName(StringUtils.hasValue(strTemp) ? strTemp : null);

            // strain description
            strTemp = txtareaDescription.getText();
            dtoStrain.setDescription(
                    StringUtils.hasValue(strTemp) ? strTemp : null);

            // strain family
            LVBeanListModel<String,Long> modelStrainFamily =
                    (LVBeanListModel<String,Long>)comboStrainFamily.getModel();
            LabelValueBean<String,Long> beanFamily =
                    modelStrainFamily.getElementAt(
                            comboStrainFamily.getSelectedIndex());

            if (dtoStrain.getStrainFamilyKey() != null) {
                Long l = new Long(beanFamily.getValue());
                if (!dtoStrain.getStrainFamilyKey().equals(l)) {
                    dtoStrain.setStrainFamilyKey(l);
                }
            } else {
                if (comboStrainFamily.getSelectedIndex() > 0) {
                    dtoStrain.setStrainFamilyKey(
                            new Long(beanFamily.getValue()));
                }
            }

            updateProgress("Saving strain data...");
            daoStrain.save(dtoStrain);
            updateProgress("Strain data saved!");

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
                Utils.showErrorDialog("Unable to save changes to Strain.",
                        e2);
            }
            if (bCommit) {
                this.setKey(dtoStrain.getStrainKey().longValue());
            } else {
                Utils.showErrorDialog("Unable to save changes to Strain.");
            }
        }
    }

    /**
     * Save the strain information.
     * <p>
     * Depending upon the type, the strain information will either be updated
     * or inserted.  This is performed in a seperate thread since this could
     * potentially be a lengthy operation. A <code>MXProgressMonitor</code> is
     * used to display visual feedback to the user.
     */
    public void save() {
        // The following code saves the current value in the cell being edited
        // and stops the editing process:
        if (fxtblSynonyms.getCellEditor() != null) {
            fxtblSynonyms.getCellEditor().stopCellEditing();
        }

        if (fxtblReferences.getCellEditor() != null) {
            fxtblReferences.getCellEditor().stopCellEditing();
        }

        if (fxtblNotes.getCellEditor() != null) {
            fxtblNotes.getCellEditor().stopCellEditing();
        }

        if (fxtblAccession.getCellEditor() != null) {
            fxtblAccession.getCellEditor().stopCellEditing();
        }

        // make sure that at least 1 strain type is selected
        int nSize = listStrainTypesSelected.getModel().getSize();
        if (nSize <= 0) {
            Utils.showErrorDialog("Please associate at least 1 strain type.");
            return;
        }

        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try{
                    if (nType == STRAIN_PANEL_ADD) {
                        progressMonitor.start("Inserting Strain...");
                        insertData();
                    } else if (nType == STRAIN_PANEL_EDIT) {
                        progressMonitor.start("Updating Strain...");
                        updateData();
                    }
                } catch (Exception e) {
                    Utils.log(e);
                } finally{
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
     * Mark a reference from the strain reference table as to be deleted.
     * <p>
     * The actual reference will not be removed until the strain has been
     * saved.
     */
    private void removeReference() {
        int nRow = fxtblReferences.getSelectedRow();

        if (nRow >= 0) {
            StrainReferencesDTOTableModel tm =
                    (StrainReferencesDTOTableModel)fxtblReferences.getModel();
            tm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark a synonym from the strain synonym table as to be deleted.
     * <p>
     * The actual synonym will not be removed until the strain has been saved.
     */
    private void removeSynonym() {
        int nRow = fxtblSynonyms.getSelectedRow();

        if (nRow >= 0) {
            StrainSynonymsDTOTableModel tm =
                    (StrainSynonymsDTOTableModel)fxtblSynonyms.getModel();
            tm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark a note from the strain notes table as to be deleted.
     * <p>
     * The actual note will not be removed until the strain has been saved.
     */
    private void removeNote() {
        int nRow = fxtblNotes.getSelectedRow();

        if (nRow >= 0) {
            StrainNotesDTOTableModel tm =
                    (StrainNotesDTOTableModel)fxtblNotes.getModel();
            tm.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark an accession record from the strain accession table as to be
     * deleted.
     * <p>
     * The actual accession record will not be removed until the strain has
     * been saved.
     */
    private void removeAccession() {
        int nRow = fxtblAccession.getSelectedRow();

        if (nRow >= 0) {
            StrainAccessionDTOTableModel tm =
                    (StrainAccessionDTOTableModel)fxtblAccession.getModel();
            tm.removeRow(nRow);
            updated = true;
        }
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
        int nRow = fxtblGenetics.getSelectedRow();

        if (evt.getActionCommand().compareTo(ACTION_COMMAND_EDIT) == 0) {
            if (nRow >= 0) {
                final StrainGeneticsDTOTableModel tm =
                        (StrainGeneticsDTOTableModel)fxtblGenetics.getModel();
                final MTBStrainGeneticsDTO dtoSG =
                        (MTBStrainGeneticsDTO)tm.getDTO(nRow);

                Runnable runnable = new Runnable(){
                    public void run(){
                        progressMonitor =
                                MXProgressUtil.createModalProgressMonitor(1, true);
                        try{
                            progressMonitor.start("Loading Allele Pair: " +
                                    dtoSG.getAllelePairId() +
                                    "...");
                            EIGlobals.getInstance().getMainFrame().
                                    launchGenotypeEditWindow(
                                    dtoSG.getAllelePairId());
                        } catch (Exception e) {
                            Utils.log(e);
                        } finally{
                            // to ensure that progress dlg is closed in case of
                            // any exception
                            progressMonitor.setCurrent(
                                    "Done!", progressMonitor.getTotal());
                        }
                    }
                };

                new Thread(runnable).start();
            }
        }
    }

    /**
     * Simple method to close the add form and switch to the edit form.  The
     * window location is tracked to make it seemless to the end user.
     */
    private void switchFromAddToEdit() {
        customInternalFrame.dispose();
        EIGlobals.getInstance().getMainFrame().launchStrainEditWindow(
                dtoStrain.getStrainKey().longValue(),
                customInternalFrame.getLocation());
    }

    /**
     * Move a strain type from the available list to the selected list.
     * <p>
     * This is the event handler for when the [&gt;] button is pressed.
     */
    private void addStrainType() {
        LVBeanListModel<String,Long> selectedModel =
                (LVBeanListModel<String,Long>)listStrainTypesSelected.getModel();
        LVBeanListModel<String,Long> availableModel =
                (LVBeanListModel<String,Long>)listStrainTypesAvailable.getModel();

        Object[] arrBeans = listStrainTypesAvailable.getSelectedValues();

        for (int i = 0; i < arrBeans.length; i++) {
            LabelValueBean<String,Long> bean = (LabelValueBean<String,Long>)arrBeans[i];
            availableModel.removeElement(bean);
            selectedModel.addElement(bean);
        }

        listStrainTypesAvailable.clearSelection();
        listStrainTypesSelected.clearSelection();
    }

    /**
     * Move a strain type from the selected list to the available list.
     * <p>
     * This is the event handler for when the [&lt;] button is pressed.
     */
    private void removeStrainType() {
        LVBeanListModel<String,Long> selectedModel =
                (LVBeanListModel<String,Long>)listStrainTypesSelected.getModel();
        LVBeanListModel<String,Long> availableModel =
                (LVBeanListModel<String,Long>)listStrainTypesAvailable.getModel();

        Object[] arrBeans = listStrainTypesSelected.getSelectedValues();

        for (int i = 0; i < arrBeans.length; i++) {
            LabelValueBean<String,Long> bean = (LabelValueBean<String,Long>)arrBeans[i];
            availableModel.addElement(bean);
            selectedModel.removeElement(bean);
        }

        listStrainTypesAvailable.clearSelection();
        listStrainTypesSelected.clearSelection();
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

    pnlStrainInformation = new javax.swing.JPanel();
    lblStrainKey = new javax.swing.JLabel();
    lblStrainName = new javax.swing.JLabel();
    txtStrainKey = new javax.swing.JTextField();
    checkboxAutoAssign = new javax.swing.JCheckBox();
    txtStrainName = new javax.swing.JTextField();
    lblPreviewLabel = new javax.swing.JLabel();
    lblPreview = new javax.swing.JLabel();
    lblDescription = new javax.swing.JLabel();
    lblStrainFamily = new javax.swing.JLabel();
    comboStrainFamily = new javax.swing.JComboBox();
    lblStrainTypes = new javax.swing.JLabel();
    jspStrainTypesAvailable = new javax.swing.JScrollPane();
    listStrainTypesAvailable = new javax.swing.JList();
    jspDescription = new javax.swing.JScrollPane();
    txtareaDescription = new javax.swing.JTextArea();
    jspStrainTypesSelected = new javax.swing.JScrollPane();
    listStrainTypesSelected = new javax.swing.JList();
    btnStrainTypeAdd = new javax.swing.JButton();
    btnStrainTypeRemove = new javax.swing.JButton();
    lblAvailable = new javax.swing.JLabel();
    lblSelected = new javax.swing.JLabel();
    headerPanelStrain = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    checkboxAutoAssignMTBID = new javax.swing.JCheckBox();
    txtMTBID = new javax.swing.JTextField();
    lblMTBID = new javax.swing.JLabel();
    pnlSynonym = new javax.swing.JPanel();
    lblJNumberSynonym = new javax.swing.JLabel();
    txtJNumberSynonym = new javax.swing.JTextField();
    lblSynonym = new javax.swing.JLabel();
    txtSynonym = new javax.swing.JTextField();
    btnSynonymAdd = new javax.swing.JButton();
    jspSynonyms = new javax.swing.JScrollPane();
    tblSynonyms = new javax.swing.JTable();
    headerPanelSynonyms = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    ponlAdditionalInfo = new javax.swing.JPanel();
    headerPanelAdditionalInfo = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    tabbedpaneData = new javax.swing.JTabbedPane();
    pnlGenetics = new javax.swing.JPanel();
    jspGenetics = new javax.swing.JScrollPane();
    tblGenetics = new javax.swing.JTable();
    pnlNotes = new javax.swing.JPanel();
    lblJNumberNote = new javax.swing.JLabel();
    txtJNumberNote = new javax.swing.JTextField();
    txtNote = new javax.swing.JTextField();
    lblNote = new javax.swing.JLabel();
    btnNoteAdd = new javax.swing.JButton();
    jspNotes = new javax.swing.JScrollPane();
    tblNotes = new javax.swing.JTable();
    pnlReferences = new javax.swing.JPanel();
    lblJNumberReference = new javax.swing.JLabel();
    txtJNumberReference = new javax.swing.JTextField();
    btnReferenceAdd = new javax.swing.JButton();
    jspReferences = new javax.swing.JScrollPane();
    tblReferences = new javax.swing.JTable();
    pnlAccession = new javax.swing.JPanel();
    lblSite = new javax.swing.JLabel();
    comboSites = new javax.swing.JComboBox();
    txtAccessionID = new javax.swing.JTextField();
    btnAccessionAdd = new javax.swing.JButton();
    lblAccessionID = new javax.swing.JLabel();
    jspAccession = new javax.swing.JScrollPane();
    tblAccession = new javax.swing.JTable();
    btnCancel = new javax.swing.JButton();
    btnSave = new javax.swing.JButton();

    pnlStrainInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblStrainKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
    lblStrainKey.setText("Strain Key");

    lblStrainName.setText("Strain Name");

    txtStrainKey.setColumns(10);
    txtStrainKey.setEditable(false);

    checkboxAutoAssign.setSelected(true);
    checkboxAutoAssign.setText("Auto Assign");
    checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkboxAutoAssignActionPerformed(evt);
      }
    });

    txtStrainName.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        txtStrainNameKeyReleased(evt);
      }
    });

    lblPreviewLabel.setText("Preview");

    lblPreview.setText("Preview Goes Here");

    lblDescription.setText("Description");

    lblStrainFamily.setText("Strain Family");

    lblStrainTypes.setText("Strain Types");

    listStrainTypesAvailable.setVisibleRowCount(4);
    jspStrainTypesAvailable.setViewportView(listStrainTypesAvailable);

    txtareaDescription.setColumns(20);
    txtareaDescription.setLineWrap(true);
    txtareaDescription.setRows(3);
    txtareaDescription.setWrapStyleWord(true);
    jspDescription.setViewportView(txtareaDescription);

    listStrainTypesSelected.setVisibleRowCount(4);
    jspStrainTypesSelected.setViewportView(listStrainTypesSelected);

    btnStrainTypeAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/StepForward16.png"))); // NOI18N
    btnStrainTypeAdd.setMargin(new java.awt.Insets(5, 5, 5, 5));
    btnStrainTypeAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnStrainTypeAddActionPerformed(evt);
      }
    });

    btnStrainTypeRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/StepBack16.png"))); // NOI18N
    btnStrainTypeRemove.setMargin(new java.awt.Insets(5, 5, 5, 5));
    btnStrainTypeRemove.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnStrainTypeRemoveActionPerformed(evt);
      }
    });

    lblAvailable.setFont(new java.awt.Font("MS Sans Serif", 2, 11)); // NOI18N
    lblAvailable.setText("Available");

    lblSelected.setFont(new java.awt.Font("MS Sans Serif", 2, 11)); // NOI18N
    lblSelected.setText("Selected");

    headerPanelStrain.setDrawSeparatorUnderneath(true);
    headerPanelStrain.setText("Strain Information");

    checkboxAutoAssignMTBID.setSelected(true);
    checkboxAutoAssignMTBID.setText("Auto Assign");
    checkboxAutoAssignMTBID.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkboxAutoAssignMTBID.setEnabled(false);
    checkboxAutoAssignMTBID.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkboxAutoAssignMTBID.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkboxAutoAssignMTBIDActionPerformed(evt);
      }
    });

    txtMTBID.setColumns(10);
    txtMTBID.setEditable(false);

    lblMTBID.setText("MTB ID");

    org.jdesktop.layout.GroupLayout pnlStrainInformationLayout = new org.jdesktop.layout.GroupLayout(pnlStrainInformation);
    pnlStrainInformation.setLayout(pnlStrainInformationLayout);
    pnlStrainInformationLayout.setHorizontalGroup(
      pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlStrainInformationLayout.createSequentialGroup()
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlStrainInformationLayout.createSequentialGroup()
            .add(19, 19, 19)
            .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStrainTypes)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStrainFamily)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblDescription)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblPreviewLabel)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, lblStrainName)))
          .add(pnlStrainInformationLayout.createSequentialGroup()
            .addContainerGap()
            .add(lblStrainKey)))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
          .add(jspDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
          .add(comboStrainFamily, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(pnlStrainInformationLayout.createSequentialGroup()
            .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(lblAvailable)
              .add(jspStrainTypesAvailable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 235, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
              .add(btnStrainTypeAdd)
              .add(btnStrainTypeRemove))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(pnlStrainInformationLayout.createSequentialGroup()
                .add(lblSelected)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 284, Short.MAX_VALUE))
              .add(jspStrainTypesSelected, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)))
          .add(pnlStrainInformationLayout.createSequentialGroup()
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(txtStrainName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
              .add(pnlStrainInformationLayout.createSequentialGroup()
                .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkboxAutoAssign)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 223, Short.MAX_VALUE)
                .add(lblMTBID)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtMTBID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkboxAutoAssignMTBID)))))
        .addContainerGap())
      .add(org.jdesktop.layout.GroupLayout.TRAILING, headerPanelStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
    );
    pnlStrainInformationLayout.setVerticalGroup(
      pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlStrainInformationLayout.createSequentialGroup()
        .add(headerPanelStrain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblStrainKey)
          .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(checkboxAutoAssign)
          .add(checkboxAutoAssignMTBID)
          .add(txtMTBID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblMTBID))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblStrainName)
          .add(txtStrainName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblPreviewLabel)
          .add(lblPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblDescription)
          .add(jspDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(comboStrainFamily, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(lblStrainFamily))
        .add(8, 8, 8)
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblStrainTypes)
          .add(lblAvailable)
          .add(lblSelected))
        .add(4, 4, 4)
        .add(pnlStrainInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(pnlStrainInformationLayout.createSequentialGroup()
            .add(btnStrainTypeAdd)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 9, Short.MAX_VALUE)
            .add(btnStrainTypeRemove))
          .add(jspStrainTypesSelected, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
          .add(jspStrainTypesAvailable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
        .addContainerGap())
    );

    pnlSynonym.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblJNumberSynonym.setText("J Number");

    txtJNumberSynonym.setColumns(10);
    txtJNumberSynonym.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtJNumberSynonymFocusLost(evt);
      }
    });

    lblSynonym.setText("Synonym");

    btnSynonymAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
    btnSynonymAdd.setText("Add");
    btnSynonymAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSynonymAddActionPerformed(evt);
      }
    });

    tblSynonyms.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jspSynonyms.setViewportView(tblSynonyms);

    headerPanelSynonyms.setDrawSeparatorUnderneath(true);
    headerPanelSynonyms.setText("Strain Synonyms");

    org.jdesktop.layout.GroupLayout pnlSynonymLayout = new org.jdesktop.layout.GroupLayout(pnlSynonym);
    pnlSynonym.setLayout(pnlSynonymLayout);
    pnlSynonymLayout.setHorizontalGroup(
      pnlSynonymLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlSynonymLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlSynonymLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(org.jdesktop.layout.GroupLayout.LEADING, jspSynonyms, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
          .add(pnlSynonymLayout.createSequentialGroup()
            .add(pnlSynonymLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(txtJNumberSynonym, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(lblJNumberSynonym))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlSynonymLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(lblSynonym)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSynonymLayout.createSequentialGroup()
                .add(txtSynonym, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnSynonymAdd)))))
        .addContainerGap())
      .add(headerPanelSynonyms, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
    );
    pnlSynonymLayout.setVerticalGroup(
      pnlSynonymLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlSynonymLayout.createSequentialGroup()
        .add(headerPanelSynonyms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlSynonymLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblJNumberSynonym)
          .add(lblSynonym))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlSynonymLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(txtJNumberSynonym, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnSynonymAdd)
          .add(txtSynonym, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jspSynonyms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    ponlAdditionalInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    headerPanelAdditionalInfo.setDrawSeparatorUnderneath(true);
    headerPanelAdditionalInfo.setText("Additional Strain Information");

    tblGenetics.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jspGenetics.setViewportView(tblGenetics);

    org.jdesktop.layout.GroupLayout pnlGeneticsLayout = new org.jdesktop.layout.GroupLayout(pnlGenetics);
    pnlGenetics.setLayout(pnlGeneticsLayout);
    pnlGeneticsLayout.setHorizontalGroup(
      pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlGeneticsLayout.createSequentialGroup()
        .addContainerGap()
        .add(jspGenetics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
        .addContainerGap())
    );
    pnlGeneticsLayout.setVerticalGroup(
      pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlGeneticsLayout.createSequentialGroup()
        .addContainerGap()
        .add(jspGenetics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabbedpaneData.addTab("Strain Genetics", pnlGenetics);

    lblJNumberNote.setText("J Number");

    txtJNumberNote.setColumns(10);
    txtJNumberNote.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtJNumberNoteFocusLost(evt);
      }
    });

    lblNote.setText("Note");

    btnNoteAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
    btnNoteAdd.setText("Add");
    btnNoteAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnNoteAddActionPerformed(evt);
      }
    });

    tblNotes.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jspNotes.setViewportView(tblNotes);

    org.jdesktop.layout.GroupLayout pnlNotesLayout = new org.jdesktop.layout.GroupLayout(pnlNotes);
    pnlNotes.setLayout(pnlNotesLayout);
    pnlNotesLayout.setHorizontalGroup(
      pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlNotesLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(org.jdesktop.layout.GroupLayout.LEADING, jspNotes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
          .add(pnlNotesLayout.createSequentialGroup()
            .add(pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(txtJNumberNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(lblJNumberNote))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(lblNote)
              .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlNotesLayout.createSequentialGroup()
                .add(txtNote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnNoteAdd)))))
        .addContainerGap())
    );
    pnlNotesLayout.setVerticalGroup(
      pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlNotesLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblJNumberNote)
          .add(lblNote))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(txtJNumberNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnNoteAdd)
          .add(txtNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jspNotes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabbedpaneData.addTab("Strain Notes", pnlNotes);

    lblJNumberReference.setText("J Number");

    txtJNumberReference.setColumns(10);
    txtJNumberReference.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtJNumberReferenceFocusLost(evt);
      }
    });

    btnReferenceAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
    btnReferenceAdd.setText("Add");
    btnReferenceAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnReferenceAddActionPerformed(evt);
      }
    });

    tblReferences.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jspReferences.setViewportView(tblReferences);

    org.jdesktop.layout.GroupLayout pnlReferencesLayout = new org.jdesktop.layout.GroupLayout(pnlReferences);
    pnlReferences.setLayout(pnlReferencesLayout);
    pnlReferencesLayout.setHorizontalGroup(
      pnlReferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlReferencesLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlReferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jspReferences, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
          .add(lblJNumberReference)
          .add(pnlReferencesLayout.createSequentialGroup()
            .add(txtJNumberReference, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(btnReferenceAdd)))
        .addContainerGap())
    );
    pnlReferencesLayout.setVerticalGroup(
      pnlReferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlReferencesLayout.createSequentialGroup()
        .addContainerGap()
        .add(lblJNumberReference)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlReferencesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(txtJNumberReference, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnReferenceAdd))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jspReferences, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabbedpaneData.addTab("Strain References", pnlReferences);

    lblSite.setText("Site");

    txtAccessionID.setColumns(20);

    btnAccessionAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
    btnAccessionAdd.setText("Add");
    btnAccessionAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAccessionAddActionPerformed(evt);
      }
    });

    lblAccessionID.setText("Accession ID");

    tblAccession.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {

      }
    ));
    jspAccession.setViewportView(tblAccession);

    org.jdesktop.layout.GroupLayout pnlAccessionLayout = new org.jdesktop.layout.GroupLayout(pnlAccession);
    pnlAccession.setLayout(pnlAccessionLayout);
    pnlAccessionLayout.setHorizontalGroup(
      pnlAccessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlAccessionLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlAccessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(jspAccession, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
          .add(pnlAccessionLayout.createSequentialGroup()
            .add(pnlAccessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(comboSites, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
              .add(lblSite))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pnlAccessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
              .add(pnlAccessionLayout.createSequentialGroup()
                .add(txtAccessionID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(btnAccessionAdd))
              .add(lblAccessionID))))
        .addContainerGap())
    );
    pnlAccessionLayout.setVerticalGroup(
      pnlAccessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlAccessionLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlAccessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblSite)
          .add(lblAccessionID))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlAccessionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(comboSites, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtAccessionID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(btnAccessionAdd))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(jspAccession, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabbedpaneData.addTab("Strain Accession Information", pnlAccession);

    org.jdesktop.layout.GroupLayout ponlAdditionalInfoLayout = new org.jdesktop.layout.GroupLayout(ponlAdditionalInfo);
    ponlAdditionalInfo.setLayout(ponlAdditionalInfoLayout);
    ponlAdditionalInfoLayout.setHorizontalGroup(
      ponlAdditionalInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(headerPanelAdditionalInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
      .add(ponlAdditionalInfoLayout.createSequentialGroup()
        .add(10, 10, 10)
        .add(tabbedpaneData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
        .add(10, 10, 10))
    );
    ponlAdditionalInfoLayout.setVerticalGroup(
      ponlAdditionalInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(ponlAdditionalInfoLayout.createSequentialGroup()
        .add(headerPanelAdditionalInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(tabbedpaneData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
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
          .add(org.jdesktop.layout.GroupLayout.TRAILING, ponlAdditionalInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(pnlSynonym, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlStrainInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
            .add(btnSave)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(btnCancel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
        .addContainerGap())
    );

    layout.linkSize(new java.awt.Component[] {btnCancel, btnSave}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(layout.createSequentialGroup()
        .addContainerGap()
        .add(pnlStrainInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlSynonym, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(ponlAdditionalInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(btnSave)
          .add(btnCancel))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

    private void checkboxAutoAssignMTBIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignMTBIDActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_checkboxAutoAssignMTBIDActionPerformed

    private void txtStrainNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStrainNameKeyReleased
        String strStart = "<html><body>";
        String strEnd = "</body></html>";
        String strName = txtStrainName.getText();
        lblPreview.setText(strStart + strName + strEnd);
    }//GEN-LAST:event_txtStrainNameKeyReleased

    private void btnStrainTypeRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStrainTypeRemoveActionPerformed
        removeStrainType();
    }//GEN-LAST:event_btnStrainTypeRemoveActionPerformed

    private void btnStrainTypeAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStrainTypeAddActionPerformed
        addStrainType();
    }//GEN-LAST:event_btnStrainTypeAddActionPerformed

    private void txtJNumberSynonymFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberSynonymFocusLost
        Utils.fixJNumber(txtJNumberSynonym);
    }//GEN-LAST:event_txtJNumberSynonymFocusLost

    private void txtJNumberReferenceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberReferenceFocusLost
        Utils.fixJNumber(txtJNumberReference);
    }//GEN-LAST:event_txtJNumberReferenceFocusLost

    private void txtJNumberNoteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberNoteFocusLost
        Utils.fixJNumber(txtJNumberNote);
    }//GEN-LAST:event_txtJNumberNoteFocusLost

    private void btnNoteAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoteAddActionPerformed
        addNote();
    }//GEN-LAST:event_btnNoteAddActionPerformed

    private void btnReferenceAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReferenceAddActionPerformed
        addReference();
    }//GEN-LAST:event_btnReferenceAddActionPerformed

    private void btnAccessionAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccessionAddActionPerformed
        addAccession();
    }//GEN-LAST:event_btnAccessionAddActionPerformed

    private void checkboxAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignActionPerformed
        if (checkboxAutoAssign.isSelected()) {
            txtStrainKey.setEditable(false);
            txtStrainKey.setText("");
        } else {
            txtStrainKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnSynonymAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSynonymAddActionPerformed
        addSynonym();
    }//GEN-LAST:event_btnSynonymAddActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAccessionAdd;
  private javax.swing.JButton btnCancel;
  private javax.swing.JButton btnNoteAdd;
  private javax.swing.JButton btnReferenceAdd;
  private javax.swing.JButton btnSave;
  private javax.swing.JButton btnStrainTypeAdd;
  private javax.swing.JButton btnStrainTypeRemove;
  private javax.swing.JButton btnSynonymAdd;
  private javax.swing.JCheckBox checkboxAutoAssign;
  private javax.swing.JCheckBox checkboxAutoAssignMTBID;
  private javax.swing.JComboBox comboSites;
  private javax.swing.JComboBox comboStrainFamily;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelAdditionalInfo;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelStrain;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelSynonyms;
  private javax.swing.JScrollPane jspAccession;
  private javax.swing.JScrollPane jspDescription;
  private javax.swing.JScrollPane jspGenetics;
  private javax.swing.JScrollPane jspNotes;
  private javax.swing.JScrollPane jspReferences;
  private javax.swing.JScrollPane jspStrainTypesAvailable;
  private javax.swing.JScrollPane jspStrainTypesSelected;
  private javax.swing.JScrollPane jspSynonyms;
  private javax.swing.JLabel lblAccessionID;
  private javax.swing.JLabel lblAvailable;
  private javax.swing.JLabel lblDescription;
  private javax.swing.JLabel lblJNumberNote;
  private javax.swing.JLabel lblJNumberReference;
  private javax.swing.JLabel lblJNumberSynonym;
  private javax.swing.JLabel lblMTBID;
  private javax.swing.JLabel lblNote;
  private javax.swing.JLabel lblPreview;
  private javax.swing.JLabel lblPreviewLabel;
  private javax.swing.JLabel lblSelected;
  private javax.swing.JLabel lblSite;
  private javax.swing.JLabel lblStrainFamily;
  private javax.swing.JLabel lblStrainKey;
  private javax.swing.JLabel lblStrainName;
  private javax.swing.JLabel lblStrainTypes;
  private javax.swing.JLabel lblSynonym;
  private javax.swing.JList listStrainTypesAvailable;
  private javax.swing.JList listStrainTypesSelected;
  private javax.swing.JPanel pnlAccession;
  private javax.swing.JPanel pnlGenetics;
  private javax.swing.JPanel pnlNotes;
  private javax.swing.JPanel pnlReferences;
  private javax.swing.JPanel pnlStrainInformation;
  private javax.swing.JPanel pnlSynonym;
  private javax.swing.JPanel ponlAdditionalInfo;
  private javax.swing.JTabbedPane tabbedpaneData;
  private javax.swing.JTable tblAccession;
  private javax.swing.JTable tblGenetics;
  private javax.swing.JTable tblNotes;
  private javax.swing.JTable tblReferences;
  private javax.swing.JTable tblSynonyms;
  private javax.swing.JTextField txtAccessionID;
  private javax.swing.JTextField txtJNumberNote;
  private javax.swing.JTextField txtJNumberReference;
  private javax.swing.JTextField txtJNumberSynonym;
  private javax.swing.JTextField txtMTBID;
  private javax.swing.JTextField txtNote;
  private javax.swing.JTextField txtStrainKey;
  private javax.swing.JTextField txtStrainName;
  private javax.swing.JTextField txtSynonym;
  private javax.swing.JTextArea txtareaDescription;
  // End of variables declaration//GEN-END:variables

}
