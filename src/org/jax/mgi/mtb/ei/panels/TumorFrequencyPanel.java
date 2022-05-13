/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/TumorFrequencyPanel.java,v 1.1 2007/04/30 15:50:59 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.jax.mgi.mtb.ei.gui.CustomInternalFrame;
import org.jax.mgi.mtb.dao.custom.SearchResults;
import org.jax.mgi.mtb.dao.custom.mtb.MTBPathologySearchDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBPathologyUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainDetailDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorFrequencyDetailDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorGeneticChangesDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorGeneticChangesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionMaxDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AccessionMaxDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AllelePairDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AllelePairDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.BreedingStatusDAO;
import org.jax.mgi.mtb.dao.gen.mtb.BreedingStatusDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ChromosomeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.ChromosomeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ChromosomeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDTO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganDAO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganDTO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyDAO;
import org.jax.mgi.mtb.dao.gen.mtb.PathologyDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainSynonymsDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TmrGntcCngAssayImageAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyNotesDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyNotesDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencySynonymsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencySynonymsDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyTreatmentsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyTreatmentsDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorGeneticsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorGeneticsDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorPathologyAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorPathologyAssocDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.editors.JNumberCellEditor;
import org.jax.mgi.mtb.ei.listeners.LVBeanComboListener;
import org.jax.mgi.mtb.ei.listeners.LVDBeanComboListener;
import org.jax.mgi.mtb.ei.models.LVBeanListModel;
import org.jax.mgi.mtb.ei.models.LVDBeanListModel;
import org.jax.mgi.mtb.ei.models.TFNotesDTOTableModel;
import org.jax.mgi.mtb.ei.models.TFProgressionDTOTableModel;
import org.jax.mgi.mtb.ei.models.TFSynonymsDTOTableModel;
import org.jax.mgi.mtb.ei.models.TFTreatmentsDTOTableModel;
import org.jax.mgi.mtb.ei.models.MTBTumorGeneticChangesDTOTableModel;
import org.jax.mgi.mtb.ei.models.TumorGeneticsAllelePairDTOTableModel;
import org.jax.mgi.mtb.ei.models.TumorPathologyAssocDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.renderers.LVBeanListCellRenderer;
import org.jax.mgi.mtb.ei.renderers.LVDBeanListCellRenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.DataBean;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.LabelValueDataBean;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.gui.menu.MXHeaderMenuItem;
import org.jax.mgi.mtb.gui.menu.MXHtmlMenuItem;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;

/**
 * For inserting or updating <b>TumorFrequency</b> information and the
 * associated data in the database.
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/TumorFrequencyPanel.java,v 1.1 2007/04/30 15:50:59 mjv Exp
 */
public class TumorFrequencyPanel extends CustomPanel implements ActionListener {

    // -------------------------------------------------------------- Constants
    /**
     * Used in the constructor to specify this is a new tumor frequency.
     */
    public static final int TUMOR_FREQUENCY_PANEL_ADD = 1;
    /**
     * Used in the constructor to specify this is an old tumor frequency.
     */
    public static final int TUMOR_FREQUENCY_PANEL_EDIT = 2;    // simple constant to identify an action event
    private final String ACTION_COMMAND_EDIT = "edit";
    private final String ACTION_COMMAND_ADD = "add";
    private final String ACTION_COMMAND_DELETE = "delete";    // organism key for mouse in chromosome table
    private static final Long mouseChromosome = new Long(1L);    // ----------------------------------------------------- Instance Variables
    // the type of panel
    private int nType = TUMOR_FREQUENCY_PANEL_ADD;    // custom JTables for sorting and rendering purposes
    private MXTable fxtblSynonyms = null;
    private MXTable fxtblNotes = null;
    private MXTable fxtblTreatements = null;
    private MXTable fxtblProgression = null;
    private MXTable fxtblPathology = null;
    private MXTable fxtblGenetics = null;
    private MXTable fxtblGeneticChanges = null;    // progress monitor
    MXProgressMonitor progressMonitor = null;
    private TumorFrequencyDTO dtoTF = null;
    private TumorTypeDTO dtoTumorType = null;
    private MTBStrainDetailDTO dtoStrainDetail = null;
    private boolean bDuplicate = false;
    private Long lProgressionTypeKeyForDup = 0l;
    private Map<Long, String> mapChangesKeyToChangesName = null;
    private Map<Long, String> mapAssayTypeKeyToAssayTypeName = null;
    private TumorFrequencySearchPanel tfsp = null;
    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new TumorFrequencyPanel.
     * <p>
     * If <code>nType = TUMOR_FREQUENCY_PANEL_ADD/code> an insert to the
     * database of the tumor frequency object is necessary.  Otherwise, the
     * tumor frequency object already exists in the database.
     *
     * @param nType the type of panel, which is either
     *        <code>TUMOR_FREQUENCY_PANEL_ADD</code> or
     *        <code>TUMOR_FREQUENCY_PANEL_EDIT</code>
     */
    public TumorFrequencyPanel(int nType) {
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
        setKey(lKey, false);
    }

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
    public void setKey(final long lKey, final boolean bDuplicate) {
        Runnable runnable = new Runnable() {

            public void run() {
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true, 10);
                progressMonitor.start("Loading Tumor Frequency: " + lKey);
                try {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            lookupData(lKey, bDuplicate);
                        }
                    ;
                } );
        } catch (Exception e) {
                    Utils.log(e);
                    progressMonitor.setCurrent("Error!",
                            progressMonitor.getTotal());
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
        try {
            if (!super.isUpdated()) {
                // tumor frequency key
                // not checking for tf key change

                // jnumber
                Utils.log("checking jnumber...");
                String strTemp = txtJNumber.getText().trim();

                if (StringUtils.hasValue(strTemp)) {
                    long lRefKey = EIGlobals.getInstance().getRefByAcc(strTemp);

                    if (dtoTF.getReferenceKey() != null) {
                        if (dtoTF.getReferenceKey().longValue() != lRefKey) {
                            Utils.log("jnumber changed...");
                            return true;
                        }
                    } else {
                        Utils.log("jnumber changed...");
                        return true;
                    }
                } else {
                    if (dtoTF.getReferenceKey() != null) {
                        Utils.log("jnumber changed...");
                        return true;
                    }
                }

                // organ/tissue of origin
                Utils.log("checking organ tissue of origin...");
                if (dtoTumorType != null) {
                    // organ of origin
                    if (dtoTumorType.getOrganKey() != null) {
                        LVBeanListModel<String, Long> model2 = (LVBeanListModel<String, Long>) comboOrganTissueOrigin.getModel();
                        LabelValueBean<String, Long> bean = (LabelValueBean<String, Long>) model2.getElementAt(comboOrganTissueOrigin.getSelectedIndex());
                        if (!dtoTumorType.getOrganKey().equals(bean.getValue())) {
                            Utils.log("Organ of origin changed...");
                            return true;
                        }
                    } else {
                        if (comboOrganTissueOrigin.getSelectedIndex() > 0) {
                            Utils.log("Organ of origin changed...");
                            return true;
                        }
                    }
                } else {
                    if (comboOrganTissueOrigin.getSelectedIndex() > 0) {
                        Utils.log("Organ of origin changed...");
                        return true;
                    }
                }

                // tumor classification
                Utils.log("checking tumor classification...");
                if (dtoTumorType != null) {
                    if (dtoTumorType.getTumorClassificationKey() != null) {
                        LVBeanListModel<String, Long> model2 = (LVBeanListModel<String, Long>) comboTumorClassification.getModel();
                        LabelValueBean<String, Long> bean = (LabelValueBean<String, Long>) model2.getElementAt(comboTumorClassification.getSelectedIndex());
                        if (!dtoTumorType.getTumorClassificationKey().equals(bean.getValue())) {
                            Utils.log("Tumor classification changed...");
                            return true;
                        }
                    } else {
                        if (comboTumorClassification.getSelectedIndex() > 0) {
                            Utils.log("Tumor classification changed...");
                            return true;
                        }
                    }
                } else {
                    if (comboTumorClassification.getSelectedIndex() > 0) {
                        Utils.log("Tumor classification changed...");
                        return true;
                    }
                }

                // organ/tissue affected
                Utils.log("checking organ affected...");
                if (dtoTF.getOrganAffectedKey() != null) {
                    LVBeanListModel<String, Long> model2 = (LVBeanListModel<String, Long>) comboOrganTissueAffected.getModel();
                    LabelValueBean<String, Long> bean = (LabelValueBean<String, Long>) model2.getElementAt(comboOrganTissueAffected.getSelectedIndex());
                    if (!dtoTF.getOrganAffectedKey().equals(bean.getValue())) {
                        Utils.log("organ affected changed...");
                        return true;
                    }
                } else {
                    if (comboOrganTissueAffected.getSelectedIndex() > 0) {
                        Utils.log("organ affected changed...");
                        return true;
                    }
                }

                // age onset
                Utils.log("checking age onset...");
                if (!StringUtils.equals(txtAgeOnset.getText(), StringUtils.nvl(dtoTF.getAgeOnset(), ""))) {
                    Utils.log("age onset changed...");
                    return true;
                }

                // age detection
                Utils.log("checking age detection...");
                if (!StringUtils.equals(txtAgeDetection.getText(), StringUtils.nvl(dtoTF.getAgeDetection(), ""))) {
                    Utils.log("age detection changed...");
                    return true;
                }

                // breeding status
                Utils.log("checking breeding status...");
                if (!StringUtils.equals((String) comboBreedingStatus.getSelectedItem(), StringUtils.nvl(dtoTF.getBreedingStatus(), "--Select--"))) {
                    Utils.log("breeding status changed...");
                    return true;
                }

                // infection status
                Utils.log("checking infection status...");
                if (!StringUtils.equals(txtInfectionStatus.getText(), StringUtils.nvl(dtoTF.getInfectionStatus(), ""))) {
                    Utils.log("infection status changed...");
                    return true;
                }

                // incidence
                Utils.log("checking incidence...");
                Utils.log("comparing [" + txtIncidence.getText() + "] to [" + StringUtils.nvl(dtoTF.getIncidence(), "") + "]");
                if (!StringUtils.equals(txtIncidence.getText(), StringUtils.nvl(dtoTF.getIncidence(), ""))) {
                    Utils.log("incidence changed...");
                    return true;
                }

                // frequency
                Utils.log("checking frequency (sort equivalent)...");
                if (dtoTF.getFreqNum() != null) {
                    if (!StringUtils.equals(txtSortEquivalent.getText(), dtoTF.getFreqNum() + "")) {
                        StringUtils.out("frequency changed...");
                        return true;
                    }
                } else {
                    if (StringUtils.hasValue(txtSortEquivalent.getText())) {
                        StringUtils.out("frequency changed...");
                        return true;
                    }
                }

                // colony size
                Utils.log("checking colony size...");
                if (dtoTF.getColonySize() != null) {
                    if (!StringUtils.equals(txtColonySize.getText(), dtoTF.getColonySize() + "")) {
                        Utils.log("colony size changed...");
                        return true;
                    }
                } else {
                    if (StringUtils.hasValue(txtColonySize.getText())) {
                        Utils.log("colony size changed...");
                        return true;
                    }
                }

                // num mice affected
                Utils.log("checking num mice affected...");
                if (dtoTF.getNumMiceAffected() != null) {
                    if (!StringUtils.equals(txtNumMiceAffected.getText(), dtoTF.getNumMiceAffected() + "")) {
                        Utils.log("num mice affected changed...");
                        return true;
                    }
                } else {
                    if (StringUtils.hasValue(txtNumMiceAffected.getText())) {
                        Utils.log("num mice affected changed...");
                        return true;
                    }
                }

                // main note
                Utils.log("checking main note...");
                if (!StringUtils.equals(txtareaMainNote.getText(), StringUtils.nvl(dtoTF.getNote(), ""))) {
                    Utils.log("main note changed...");
                    return true;
                }

                // synonyms
                Utils.log("checking tumor frequency synonyms...");
                TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO> tblmdlSynonyms =
                        (TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO>) fxtblSynonyms.getModel();
                if (tblmdlSynonyms.hasBeenUpdated()) {
                    Utils.log("tumor frequency synonyms changed...");
                    return true;
                }

                // notes
                Utils.log("checking tumor frequency notes...");
                TFNotesDTOTableModel<TumorFrequencyNotesDTO> tblmdlNotes =
                        (TFNotesDTOTableModel<TumorFrequencyNotesDTO>) fxtblNotes.getModel();
                if (tblmdlNotes.hasBeenUpdated()) {
                    Utils.log("tumor frequency notes changed...");
                    return true;
                }

                // strain
                if (dtoStrainDetail != null) {
                    if (!StringUtils.equals(txtStrainKey.getText(), dtoStrainDetail.getStrainKey() + "")) {
                        Utils.log("Strain Key changed...");
                        return true;
                    }
                }

                // strain sex
                if (dtoTF.getSexKey() != null) {

                    /*
                    F Female
                    M Male
                    U Not specified
                    X Mixed population
                     */
                    String s = (String) comboStrainSex.getSelectedItem();

                    if (StringUtils.equals(s, "Mixed population")) {
                        s = "X";
                    } else if (StringUtils.equals(s, "Not specified")) {
                        s = "U";
                    } else {
                        s = s.substring(0, 1);
                    }

                    if (!dtoTF.getSexKey().equals(s)) {
                        Utils.log("Sex changed...");
                        return true;
                    }
                }

                // progression info, parent

                // progression info, child
                Utils.log("checking tumor progression info (child)...");
                TFProgressionDTOTableModel<TumorProgressionDTO> tblmdlProgression =
                        (TFProgressionDTOTableModel<TumorProgressionDTO>) fxtblProgression.getModel();
                if (tblmdlProgression.hasBeenUpdated()) {
                    Utils.log("tumor progression info (child) changed...");
                    return true;
                }


                // treatment info
                Utils.log("checking tumor treatment info...");
                TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO> tblmdlTreatment =
                        (TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO>) fxtblTreatements.getModel();
                if (tblmdlTreatment.hasBeenUpdated()) {
                    Utils.log("tumor treatment info changed...");
                    return true;
                }

                // pathology info
                Utils.log("checking tumor pathology info...");
                TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> tblmdlPathology =
                        (TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>) fxtblPathology.getModel();
                if (tblmdlPathology.hasBeenUpdated()) {
                    Utils.log("tumor pathology info changed...");
                    return true;
                }

                Utils.log("Nothing changed...");
                return false;
            }

            Utils.log("Super changed...");
            return true;
        } catch (Exception e) {
            Utils.showErrorDialog("ERROR", e);
            return true;
        }
    }

    /**
     * Handles all <code>ActionEvent</code>s that occur.
     * <p>
     * In this case, a right click on the pathology table will trigger a popup
     * menu to appear.
     *
     * @param evt the <code>ActionEvent</code>
     */
    public void actionPerformed(ActionEvent e) {
        int nRow = fxtblPathology.getSelectedRow();

        if (e.getActionCommand().compareTo(ACTION_COMMAND_ADD) == 0) {
            CustomInternalFrame cif = EIGlobals.getInstance().getMainFrame().launchPathologyAddWindow();
            PathologyPanel pp = (PathologyPanel) cif.getCustomPanel();
        } else if (e.getActionCommand().compareTo(ACTION_COMMAND_EDIT) == 0) {
            if (nRow >= 0) {
                TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> tm = (TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>) fxtblPathology.getModel();
                TumorPathologyAssocDTO dtoP = (TumorPathologyAssocDTO) tm.getDTO(nRow);
                EIGlobals.getInstance().getMainFrame().launchPathologyEditWindow(dtoP.getPathologyKey().longValue());
                //refreshPathology();
            }
        } else if (e.getActionCommand().compareTo(ACTION_COMMAND_DELETE) == 0) {
            if (nRow >= 0) {
                TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> tm = (TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>) fxtblPathology.getModel();
                tm.removeRow(nRow);
                updated = true;
            }
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
     * Lookup all tumor related information in the database.
     *
     * @param lKey the tumor frequency key to be looked up in the database
     */
    private void lookupData(long lKey) {
        lookupData(lKey, false);
    }

    /**
     * Lookup all tumor related information in the database.
     *
     * @param lKey the tumor frequency key to be looked up in the database
     */
    private void lookupData(long lKey, boolean bDuplicate) {
        this.bDuplicate = bDuplicate;
        TumorFrequencyDAO daoTF = TumorFrequencyDAO.getInstance();
        TumorFrequencyNotesDAO daoTFNotes =
                TumorFrequencyNotesDAO.getInstance();
        TumorFrequencySynonymsDAO daoTFSynonyms =
                TumorFrequencySynonymsDAO.getInstance();
        TumorFrequencyTreatmentsDAO daoTFTreatments =
                TumorFrequencyTreatmentsDAO.getInstance();
        TumorGeneticsDAO daoGenetics = TumorGeneticsDAO.getInstance();
        MTBTumorGeneticChangesDAO daoGeneticChanges = MTBTumorGeneticChangesDAO.getInstance();
        TumorProgressionDAO daoProgression = TumorProgressionDAO.getInstance();
        TumorPathologyAssocDAO daoTumorPathologyAssoc =
                TumorPathologyAssocDAO.getInstance();
        MTBReferenceUtilDAO daoReferenceUtil = MTBReferenceUtilDAO.getInstance();
        MTBPathologyUtilDAO daoPathologyUtil = MTBPathologyUtilDAO.getInstance();
        AgentDAO daoAgent = AgentDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        if (bDuplicate) {
            // change the type to do an insert rather than update
            this.nType = TUMOR_FREQUENCY_PANEL_ADD;
        }

        try {
            ///////////////////////////////////////////////////////////////////
            // get the tumor freq. record
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading tumor frequency data...");
            dtoTF = daoTF.loadByPrimaryKey(new Long(lKey));
            txtAgeOnset.setText(dtoTF.getAgeOnset());
            txtAgeDetection.setText(dtoTF.getAgeDetection());
            if (dtoTF.getColonySize() == null) {
                txtColonySize.setText("");
            } else {
                txtColonySize.setText(dtoTF.getColonySize() + "");
            }

            if (dtoTF.getNumMiceAffected() == null) {
                txtNumMiceAffected.setText("");
            } else {
                txtNumMiceAffected.setText(dtoTF.getNumMiceAffected() + "");
            }

            txtIncidence.setText(dtoTF.getIncidence());

            if (dtoTF.getFreqNum() == null) {
                txtSortEquivalent.setText("");
            } else {
                txtSortEquivalent.setText(dtoTF.getFreqNum() + "");
            }


            comboBreedingStatus.setSelectedItem(dtoTF.getBreedingStatus());
            txtInfectionStatus.setText(dtoTF.getInfectionStatus());
            txtJNumber.setText(
                    daoReferenceUtil.getJNumByReference(
                    dtoTF.getReferenceKey().longValue()));
            txtareaMainNote.setText(dtoTF.getNote());

            ///////////////////////////////////////////////////////////////////
            // load the accession info
            ///////////////////////////////////////////////////////////////////
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setObjectKey(dtoTF.getTumorFrequencyKey());
            dtoAccession.setSiteInfoKey(0);
            dtoAccession.setMTBTypesKey(5);
            dtoAccession.setPrefixPart("MTB:");
            dtoAccession = daoAccession.loadUniqueUsingTemplate(dtoAccession);
            if (bDuplicate) {
                txtAccession.setText("");
            } else {
                txtAccession.setText(dtoAccession.getAccID());
            }


            ///////////////////////////////////////////////////////////////////
            // get the tumor type, class, and organ
            ///////////////////////////////////////////////////////////////////
            dtoTumorType = daoTF.getTumorTypeDTO(dtoTF);
            LVBeanListModel<String, Long> modelOrgan =
                    (LVBeanListModel<String, Long>) comboOrganTissueOrigin.getModel();

            Long lOrganKey = dtoTumorType.getOrganKey();

            for (int i = 0; i < modelOrgan.getSize(); i++) {
                LabelValueBean<String, Long> bean = modelOrgan.getElementAt(i);
                if (lOrganKey.equals(bean.getValue())) {
                    comboOrganTissueOrigin.setSelectedIndex(i);
                    break;
                }
            }

            LVBeanListModel<String, Long> modelTumorClassification =
                    (LVBeanListModel<String, Long>) comboTumorClassification.getModel();
            Long lTumorClassificationKey =
                    dtoTumorType.getTumorClassificationKey();

            for (int i = 0; i < modelTumorClassification.getSize(); i++) {
                LabelValueBean<String, Long> bean =
                        modelTumorClassification.getElementAt(i);
                if (lTumorClassificationKey.equals(bean.getValue())) {
                    comboTumorClassification.setSelectedIndex(i);
                    break;
                }
            }

            LVBeanListModel<String, Long> modelOrganAffected =
                    (LVBeanListModel<String, Long>) comboOrganTissueAffected.getModel();
            Long organAffectedKey = dtoTF.getOrganAffectedKey();

            for (int i = 0; i < modelOrganAffected.getSize(); i++) {
                LabelValueBean<String, Long> bean =
                        modelOrganAffected.getElementAt(i);
                if (organAffectedKey.equals(bean.getValue())) {
                    comboOrganTissueAffected.setSelectedIndex(i);
                    break;
                }
            }
            updateProgress("Tumor frequency data loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the synonyms
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading tumor frequency synonym data...");

            List<TumorFrequencySynonymsDTO> arrTFSynonyms =
                    daoTFSynonyms.loadByTumorFrequencyKey(
                    dtoTF.getTumorFrequencyKey());

            List<TumorFrequencySynonymsDTO> arrTFSynonymsWorking =
                    new ArrayList<TumorFrequencySynonymsDTO>();

            for (TumorFrequencySynonymsDTO dtoTFS : arrTFSynonyms) {
                DataBean dtoSimple = dtoTFS.getDataBean();
                try {
                    dtoSimple.put(EIConstants.JNUM,
                            daoReferenceUtil.getJNumByReference(
                            dtoTFS.getReferenceKey().longValue()));
                } catch (Exception e) {
                    Utils.log(e);
                }
                TumorFrequencySynonymsDTO dtoWorking = daoTFSynonyms.createTumorFrequencySynonymsDTO();

                if (bDuplicate) {
                    dtoWorking.setTumorFrequencySynonymsKey(null);
                    dtoWorking.setTumorFrequencyKey(null);
                    dtoWorking.isNew(true);
                    dtoWorking.setReferenceKey(dtoTFS.getReferenceKey());
                    dtoWorking.setName(dtoTFS.getName());
                    dtoWorking.setCreateDate(dtoTFS.getCreateDate());
                    dtoWorking.setCreateUser(dtoTFS.getCreateUser());
                    dtoWorking.setUpdateDate(dtoTFS.getUpdateDate());
                    dtoWorking.setUpdateUser(dtoTFS.getUpdateUser());
                } else {
                    dtoWorking = dtoTFS;
                }
                dtoWorking.setDataBean(dtoSimple);
                arrTFSynonymsWorking.add(dtoWorking);

            }
            TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO> tblmdlSynonyms =
                    (TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO>) fxtblSynonyms.getModel();

            tblmdlSynonyms.setData(arrTFSynonymsWorking);
            txtJNumberSynonym.setText(txtJNumber.getText());

            updateProgress("Tumor frequency synonym data loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the notes
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading tumor frequency note data...");

            List<TumorFrequencyNotesDTO> arrTFNotes =
                    daoTFNotes.loadByTumorFrequencyKey(
                    dtoTF.getTumorFrequencyKey());

            List<TumorFrequencyNotesDTO> arrTFNotesWorking = new ArrayList<TumorFrequencyNotesDTO>();

            for (TumorFrequencyNotesDTO dtoTFN : arrTFNotes) {
                DataBean dtoSimple = dtoTFN.getDataBean();
                try {
                    dtoSimple.put(EIConstants.JNUM,
                            daoReferenceUtil.getJNumByReference(
                            dtoTFN.getReferenceKey().longValue()));
                } catch (Exception e) {
                    Utils.log(e);
                }

                TumorFrequencyNotesDTO dtoWorking = daoTFNotes.createTumorFrequencyNotesDTO();

                if (bDuplicate) {
                    dtoWorking.setTumorFrequencyNotesKey(null);
                    dtoWorking.setTumorFrequencyKey(null);
                    dtoWorking.isNew(true);
                    dtoWorking.setReferenceKey(dtoTFN.getReferenceKey());
                    dtoWorking.setNote(dtoTFN.getNote());
                    dtoWorking.setCreateDate(dtoTFN.getCreateDate());
                    dtoWorking.setCreateUser(dtoTFN.getCreateUser());
                    dtoWorking.setUpdateDate(dtoTFN.getUpdateDate());
                    dtoWorking.setUpdateUser(dtoTFN.getUpdateUser());
                } else {
                    dtoWorking = dtoTFN;
                }
                dtoWorking.setDataBean(dtoSimple);
                arrTFNotesWorking.add(dtoWorking);

            }


            TFNotesDTOTableModel<TumorFrequencyNotesDTO> tblmdlNotes = (TFNotesDTOTableModel<TumorFrequencyNotesDTO>) fxtblNotes.getModel();
            tblmdlNotes.setData(arrTFNotesWorking);
            txtJNumberNote.setText(txtJNumber.getText());

            updateProgress("Tumor frequency note data loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the treatments
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading tumor frequency treatment data...");

            List<TumorFrequencyTreatmentsDTO> arrTFTreatments =
                    daoTFTreatments.loadByTumorFrequencyKey(
                    dtoTF.getTumorFrequencyKey());

            List<TumorFrequencyTreatmentsDTO> arrTFTreatmentsWorking =
                    new ArrayList<TumorFrequencyTreatmentsDTO>();

            Map<Long, LabelValueBean<String, Long>> mapAgentTypes = EIGlobals.getInstance().getAgentTypes();
            //DefaultListModel modelRO =
            //        (DefaultListModel)listTreatmentTypeReadOnly.getModel();
            //modelRO.clear();

            for (TumorFrequencyTreatmentsDTO dtoTFT : arrTFTreatments) {
                DataBean dtoSimple = dtoTFT.getDataBean();
                try {
                    AgentDTO dtoAgent =
                            daoAgent.loadByPrimaryKey(dtoTFT.getAgentKey());

                    LabelValueBean<String, Long> bean =
                            mapAgentTypes.get(dtoAgent.getAgentTypeKey());

                    dtoSimple.put(EIConstants.AGENT, dtoAgent.getName());
                    //modelRO.addElement(bean.getLabel() + "-" +
                    //                 dtoAgent.getName());
                    dtoSimple.put(EIConstants.AGENT_TYPE, bean.getLabel());
                } catch (Exception e) {
                    Utils.log(e);
                }
                TumorFrequencyTreatmentsDTO dtoWorking = daoTFTreatments.createTumorFrequencyTreatmentsDTO();

                if (bDuplicate) {
                    dtoWorking.setTumorFrequencyTreatmentsKey(null);
                    dtoWorking.setTumorFrequencyKey(null);
                    dtoWorking.isNew(true);
                    dtoWorking.setAge(dtoTFT.getAge());
                    dtoWorking.setAgentKey(dtoTFT.getAgentKey());
                    dtoWorking.setDose(dtoTFT.getDose());
                    dtoWorking.setCreateDate(dtoTFT.getCreateDate());
                    dtoWorking.setCreateUser(dtoTFT.getCreateUser());
                    dtoWorking.setUpdateDate(dtoTFT.getUpdateDate());
                    dtoWorking.setUpdateUser(dtoTFT.getUpdateUser());
                } else {
                    dtoWorking = dtoTFT;
                }
                dtoWorking.setDataBean(dtoSimple);
                arrTFTreatmentsWorking.add(dtoWorking);

            }

            TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO> tblmdlTreatments =
                    (TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO>) fxtblTreatements.getModel();
            tblmdlTreatments.setData(arrTFTreatmentsWorking);
//            if (modelRO.getSize() == 0) {
            //              modelRO.addElement("None (spontaneous)");
            //        }

            updateProgress("Tumor frequency treatment data loaded!");

            ///////////////////////////////////////////////////////////////////
            // get the progression info, parent (no progressions for duplicates)
            ///////////////////////////////////////////////////////////////////
            if (!bDuplicate) {
                updateProgress("Loading tumor frequency progression data...");

                List<TumorProgressionDTO> arrTFProgressionParents =
                        daoProgression.loadByChildKey(
                        dtoTF.getTumorFrequencyKey());

                if ((arrTFProgressionParents != null)
                        && (arrTFProgressionParents.size() > 0)) {

                    txtTumorFrequencyKeyProgressionParent.setText(
                            (arrTFProgressionParents.get(0)).getParentKey() + "");

                    lProgressionTypeKeyForDup =
                            (arrTFProgressionParents.get(0)).getTumorProgressionTypeKey();

                    lookupProgressionParent();
                }

                ///////////////////////////////////////////////////////////////////
                // get the progression info, child (no progressions for duplicates)
                ///////////////////////////////////////////////////////////////////

                List<TumorProgressionDTO> arrTFProgressionChildren =
                        daoProgression.loadByParentKey(
                        dtoTF.getTumorFrequencyKey());

                if ((arrTFProgressionChildren != null)
                        && (arrTFProgressionChildren.size() > 0)) {

                    for (int i = 0; i < arrTFProgressionChildren.size(); i++) {
                        TumorProgressionDTO dtoTumorProgression =
                                arrTFProgressionChildren.get(i);

                        DataBean dtoSimple = dtoTumorProgression.getDataBean();
                        try {
                            TumorFrequencyDTO dtoTumorFrequencyTemp =
                                    TumorFrequencyDAO.getInstance().
                                    createTumorFrequencyDTO();

                            dtoTumorFrequencyTemp.setTumorFrequencyKey(
                                    dtoTumorProgression.getChildKey());

                            dtoTumorFrequencyTemp =
                                    TumorFrequencyDAO.getInstance().
                                    loadUniqueUsingTemplate(
                                    dtoTumorFrequencyTemp);

                            OrganDTO organTempDTO2 =
                                    OrganDAO.getInstance().loadByPrimaryKey(
                                    dtoTumorFrequencyTemp.getOrganAffectedKey());

                            TumorProgressionTypeDTO dtoTumorProgressionType =
                                    TumorProgressionTypeDAO.getInstance().
                                    loadByPrimaryKey(
                                    dtoTumorProgression.getTumorProgressionTypeKey());

                            dtoSimple.put(EIConstants.PROGRESSION,
                                    dtoTumorProgressionType.getName());
                            dtoSimple.put(EIConstants.ORGAN,
                                    organTempDTO2.getName());
                        } catch (Exception e) {
                            Utils.log(e);
                        }
                        dtoTumorProgression.setDataBean(dtoSimple);
                        arrTFProgressionChildren.set(i, dtoTumorProgression);
                    }
                }
                TFProgressionDTOTableModel<TumorProgressionDTO> tblmdlTumorProgression =
                        (TFProgressionDTOTableModel<TumorProgressionDTO>) fxtblProgression.getModel();
                tblmdlTumorProgression.setData(arrTFProgressionChildren);

                updateProgress("Tumor frequency progression data loaded!");
            }

            ///////////////////////////////////////////////////////////////////
            // get the pathology information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading tumor pathology associated data...");

            List<TumorPathologyAssocDTO> arrTumorPathologyAssoc =
                    daoTumorPathologyAssoc.loadByTumorFrequencyKey(
                    dtoTF.getTumorFrequencyKey());

            List<TumorPathologyAssocDTO> arrTumorPathologyAssocWorking =
                    new ArrayList<TumorPathologyAssocDTO>();

            for (TumorPathologyAssocDTO dtoTPA : arrTumorPathologyAssoc) {
                DataBean dtoSimple = dtoTPA.getDataBean();
                try {
                    SearchResults wrap =
                            daoPathologyUtil.searchPathology(
                            (int) dtoTPA.getPathologyKey().
                            longValue());
                    Collection c = wrap.getList();
                    List<MTBPathologySearchDTO> a = new ArrayList<MTBPathologySearchDTO>(c);
                    MTBPathologySearchDTO psDTO = a.get(0);
                    dtoSimple.put(EIConstants.MTB_PATHOLOGY_SEARCH_DTO, psDTO);
                } catch (Exception e) {
                    Utils.log(e);
                }
                TumorPathologyAssocDTO dtoWorking = daoTumorPathologyAssoc.createTumorPathologyAssocDTO();

                if (bDuplicate) {
                    dtoWorking.setTumorFrequencyKey(null);
                    dtoWorking.isNew(true);
                    dtoWorking.setPathologyKey(dtoTPA.getPathologyKey());
                    dtoWorking.setCreateDate(dtoTPA.getCreateDate());
                    dtoWorking.setCreateUser(dtoTPA.getCreateUser());
                    dtoWorking.setUpdateDate(dtoTPA.getUpdateDate());
                    dtoWorking.setUpdateUser(dtoTPA.getUpdateUser());
                } else {
                    dtoWorking = dtoTPA;
                }
                dtoWorking.setDataBean(dtoSimple);
                arrTumorPathologyAssocWorking.add(dtoWorking);
            }
            TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> tblmdlPathology =
                    (TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>) fxtblPathology.getModel();
            tblmdlPathology.setData(arrTumorPathologyAssocWorking);

            updateProgress("Tumor pathology associated data loaded!");

            // don't get genetic info for duplicated records
            if (!bDuplicate) {


                ///////////////////////////////////////////////////////////////////
                // get the genetic information
                ///////////////////////////////////////////////////////////////////
                updateProgress("Loading tumor genetic data...");

                List<TumorGeneticsDTO> arrTumorGenetics =
                        daoGenetics.loadByTumorFrequencyKey(
                        dtoTF.getTumorFrequencyKey());

                List<TumorGeneticsDTO> arrTumorGeneticsWorking =
                        new ArrayList<TumorGeneticsDTO>();

                // large numbers of tumor genetics records results
                // in too many new connections if not in a transaction
                // no connection pooling other than this approach.
                DAOManagerMTB.getInstance().beginTransaction();
                for (TumorGeneticsDTO dtoG : arrTumorGenetics) {
                    DataBean dtoSimple = dtoG.getDataBean();
                    try {
                        // lookup the allele pair and allele information
                        AllelePairDTO dtoAP = lookupAllelePair(dtoG.getAllelePairKey());
                        AlleleDTO dto1 = lookupAllele(dtoAP.getAllele1Key());
                        AlleleDTO dto2 = lookupAllele(dtoAP.getAllele2Key());

                        // get the marker information for allele 1
                        MarkerDTO dtoM = null;
                        try {
                            List<MarkerDTO> listMarkers = AlleleDAO.getInstance().loadMarkerViaAlleleMarkerAssoc(dto1);
                            if (listMarkers != null) {
                                dtoM = listMarkers.get(0);
                            }
                        } catch (SQLException sqle) {
                            // ignore
                        }

                        AlleleTypeDTO dtoAT = null;
                        try {

                            dtoAT = AlleleTypeDAO.getInstance().loadByPrimaryKey(dto1.getAlleleTypeKey());

                        } catch (SQLException sqle) {
                            // ignore
                        }

                        // set the custom data for the data model to display the correct data
                        dtoSimple.put(EIConstants.ALLELE1_KEY, dto1.getAlleleKey() + "");
                        dtoSimple.put(EIConstants.ALLELE1_SYMBOL, dto1.getSymbol());
                        if (dtoAT != null) {
                            dtoSimple.put(EIConstants.ALLELE1_TYPE, dtoAT.getType());
                        }
                        if (dto2 != null) {
                            dtoAT = null;
                            try {

                                dtoAT = AlleleTypeDAO.getInstance().loadByPrimaryKey(dto2.getAlleleTypeKey());

                            } catch (SQLException sqle) {
                                // ignore
                            }

                            dtoSimple.put(EIConstants.ALLELE2_KEY, dto2.getAlleleKey() + "");
                            dtoSimple.put(EIConstants.ALLELE2_SYMBOL, dto2.getSymbol());
                            if (dtoAT != null) {
                                dtoSimple.put(EIConstants.ALLELE2_TYPE, dtoAT.getType());
                            }
                        }

                        if (dtoM != null) {
                            dtoSimple.put(EIConstants.MARKER_SYMBOL, dtoM.getSymbol());
                        }
                    } catch (Exception e) {
                        Utils.log(e);
                    }

                    TumorGeneticsDTO dtoWorking = daoGenetics.createTumorGeneticsDTO();
                    dtoWorking = dtoG;
                    dtoWorking.setDataBean(dtoSimple);
                    arrTumorGeneticsWorking.add(dtoWorking);
                }

                DAOManagerMTB.getInstance().endTransaction(true);


                TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO> tblmdlGenetics =
                        (TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO>) fxtblGenetics.getModel();
                tblmdlGenetics.setData(arrTumorGeneticsWorking);

                updateProgress("Tumor genetics data loaded!");

                ///////////////////////////////////////////////////////////////////
                // get the genetic change information
                ///////////////////////////////////////////////////////////////////
                updateProgress("Loading tumor genetic change data...");

                List<MTBTumorGeneticChangesDTO> arrTumorGeneticChanges =
                        daoGeneticChanges.loadByTumorFrequencyKey(
                        dtoTF.getTumorFrequencyKey());

                List<MTBTumorGeneticChangesDTO> arrTumorGeneticChangesWorking =
                        new ArrayList<MTBTumorGeneticChangesDTO>();

                for (MTBTumorGeneticChangesDTO dtoG : arrTumorGeneticChanges) {
                    DataBean dtoSimple = dtoG.getDataBean();

                    // set the custom data for the data model to display the correct data
                    String name = "";
                    try {
                        name = mapChangesKeyToChangesName.get(dtoG.getAlleleTypeKey());
                    } catch (NullPointerException e) {
                    }

                    dtoSimple.put(EIConstants.CHANGE, name);

                    name = "";
                    try {
                        name = mapAssayTypeKeyToAssayTypeName.get(dtoG.getAssayTypeKey());
                    } catch (NullPointerException e) {
                    }

                    dtoSimple.put(EIConstants.ASSAY_NAME, name);


                    MTBTumorGeneticChangesDTO dtoWorking = daoGeneticChanges.createTumorGeneticChangesDTO();
                    dtoWorking = dtoG;
                    dtoWorking.setDataBean(dtoSimple);
                    // sort the list of associated chromosomes to be displayed
                    List chroms = daoGeneticChanges.loadChromosomeViaGeneticChangeChromosomeAssoc(dtoG);
                    Collections.sort(chroms, new ChromosomeComparator(ChromosomeDAO.ID_ORDERNUM));
                    dtoWorking.setChromosomes(chroms);
                    dtoWorking.setChromosomesModified(false);

                    // assayImages
                    // wow a sql call in a loop could be slow
                    int size = 0;
                    try {
                        List imageList = TmrGntcCngAssayImageAssocDAO.getInstance().loadByTumorGeneticChangesKey(dtoWorking.getTumorGeneticChangesKey());
                        size = imageList.size();

                    } catch (Exception e) {
                    }
                    dtoWorking.getDataBean().put(EIConstants.ASSAY_IMAGE_COUNT, new Integer(size));

                    arrTumorGeneticChangesWorking.add(dtoWorking);
                }
                MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO> tblmdlGeneticChanges =
                        (MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO>) fxtblGeneticChanges.getModel();
                tblmdlGeneticChanges.setData(arrTumorGeneticChangesWorking);

                updateProgress("Tumor genetic change data loaded!");
            }
            ////////////////////////////////////////////////////////////////////

            if (bDuplicate) {
                TumorFrequencyDTO dtoTFTemp = daoTF.createTumorFrequencyDTO();
                dtoTFTemp.setTumorFrequencyKey(null);
                dtoTFTemp.isNew(true);
                dtoTFTemp.setTumorTypeKey(dtoTF.getTumorTypeKey());
                dtoTFTemp.setStrainKey(dtoTF.getStrainKey());
                dtoTFTemp.setSexKey(dtoTF.getSexKey());
                dtoTFTemp.setReferenceKey(dtoTF.getReferenceKey());
                dtoTFTemp.setOrganAffectedKey(dtoTF.getOrganAffectedKey());
                dtoTFTemp.setNote(dtoTF.getNote());
                dtoTFTemp.setColonySize(dtoTF.getColonySize());
                dtoTFTemp.setAgeOnset(dtoTF.getAgeOnset());
                dtoTFTemp.setAgeDetection(dtoTF.getAgeDetection());
                dtoTFTemp.setNumMiceAffected(dtoTF.getNumMiceAffected());
                dtoTFTemp.setIncidence(dtoTF.getIncidence());
                dtoTFTemp.setFreqNum(dtoTF.getFreqNum());
                dtoTFTemp.setBreedingStatus(dtoTF.getBreedingStatus());
                dtoTFTemp.setInfectionStatus(dtoTF.getInfectionStatus());
                dtoTFTemp.setCreateDate(dtoTF.getCreateDate());
                dtoTFTemp.setCreateUser(dtoTF.getCreateUser());
                dtoTFTemp.setUpdateDate(dtoTF.getUpdateDate());
                dtoTFTemp.setUpdateUser(dtoTF.getUpdateUser());
                dtoTF = dtoTFTemp;
                txtTumorFrequencyKey.setText("");
            } else {
                txtTumorFrequencyKey.setText(dtoTF.getTumorFrequencyKey() + "");
            }


            ///////////////////////////////////////////////////////////////////
            // get the strain information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Loading strain information...");

            txtStrainKey.setText(dtoTF.getStrainKey() + "");
            lookupStrain();

            /*
            F Female
            M Male
            U Not specified
            X Mixed population
             */
            String strSexString = "Mixed population";
            String strSexKey = dtoTF.getSexKey();
            if (StringUtils.equals(strSexKey, "M")) {
                strSexString = "Male";
            } else if (StringUtils.equals(strSexKey, "F")) {
                strSexString = "Female";
            } else if (StringUtils.equals(strSexKey, "U")) {
                strSexString = "Not specified";
            }

            for (int i = 0; i < comboStrainSex.getItemCount(); i++) {
                String strSexLookup = (String) comboStrainSex.getItemAt(i);

                if (StringUtils.equals(strSexLookup, strSexString)) {
                    comboStrainSex.setSelectedIndex(i);
                }
            }

            updateProgress("Strain information loaded!");

            updateProgress("Tumor frequency loaded!");

            progressMonitor.setCurrent("Done!", progressMonitor.getTotal());
        } catch (Exception e) {
            Utils.log("Error retrieving tumor information: " + lKey);
            Utils.log(e.getMessage());
            Utils.log(StringUtils.getStackTrace(e));
            progressMonitor.setCurrent("Error!", progressMonitor.getTotal());
        }
    }

    private void initChangeKeyToChangesNameMap() {
        mapChangesKeyToChangesName = new HashMap<Long, String>();
        Collection<LabelValueBean<String, Long>> lvb = EIGlobals.getInstance().getAlleleTypes().values();

        for (LabelValueBean<String, Long> bean : lvb) {
            mapChangesKeyToChangesName.put(bean.getValue(), bean.getLabel());

        }
    }

    private void initAssayTypeKeyToAssayTypeNameMap() {
        mapAssayTypeKeyToAssayTypeName = new HashMap<Long, String>();
        Collection<LabelValueBean<String, Long>> lvb = EIGlobals.getInstance().getAssayTypes().values();

        for (LabelValueBean<String, Long> bean : lvb) {
            mapAssayTypeKeyToAssayTypeName.put(bean.getValue(), bean.getLabel());

        }
    }

    /**
     * Initialize the JList for organs and tissues of origin.
     * <p>
     * A custom <code>ComboBoxModel</code>, <code>ListCellRenderer</code>, and
     * <code>KeyListener</code> are used.
     */
    private void initOrgansTissuesOrigin() {
        final Map<Long, LabelValueBean<String, Long>> mapOrgans = EIGlobals.getInstance().getOrgansUnfiltered();
        List<LabelValueBean<String, Long>> arrOrgans = new ArrayList<LabelValueBean<String, Long>>(mapOrgans.values());
        arrOrgans.add(0, new LabelValueBean<String, Long>("--Select--", -1L));
        comboOrganTissueOrigin.setModel(new LVBeanListModel<String, Long>(arrOrgans));
        comboOrganTissueOrigin.setRenderer(new LVBeanListCellRenderer<String, Long>());
        comboOrganTissueOrigin.addKeyListener(new LVBeanComboListener<String, Long>());
        comboOrganTissueOrigin.setSelectedIndex(0);
    }

    /**
     * Initialize the JList for tumor classifications.
     * <p>
     * A custom <code>ComboBoxModel</code>, <code>ListCellRenderer</code>, and
     * <code>KeyListener</code> are used.
     */
    private void initTumorClassifications() {
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
    }

    /**
     * Initialize the JList for organisms.
     * <p>
     * A custom <code>ComboBoxModel</code>, <code>ListCellRenderer</code>, and
     * <code>KeyListener</code> are used.
     */
    /*
    private void initOrganisms() {
    final Map<Long,LabelValueBean<String,Long>> mapOrganisms = EIGlobals.getInstance().getOrganisms();
    List<LabelValueBean<String,Long>> arrOrganisms = new ArrayList<LabelValueBean<String,Long>>(mapOrganisms.values());
    arrOrganisms.add(0, new LabelValueBean<String,Long>("--Select--", -1L));
    comboCytogeneticOrganisms.setModel(new LVBeanListModel<String,Long>(arrOrganisms));
    comboCytogeneticOrganisms.setRenderer(new LVBeanListCellRenderer<String,Long>());
    comboCytogeneticOrganisms.addKeyListener(new LVBeanComboListener<String,Long>());
    comboCytogeneticOrganisms.setSelectedIndex(0);
    }
     */
    /**
     * Initialize the JList for organs and tissues affected.
     * <p>
     * A custom <code>ComboBoxModel</code>, <code>ListCellRenderer</code>, and
     * <code>KeyListener</code> are used.
     */
    private void initOrgansTissuesAffected() {
        final Map<Long, LabelValueBean<String, Long>> mapOrgans = EIGlobals.getInstance().getOrgansUnfiltered();
        List<LabelValueBean<String, Long>> arrOrgansAffected = new ArrayList<LabelValueBean<String, Long>>(mapOrgans.values());
        arrOrgansAffected.add(0, new LabelValueBean<String, Long>("--Select--", -1L));
        comboOrganTissueAffected.setModel(
                new LVBeanListModel<String, Long>(arrOrgansAffected));
        comboOrganTissueAffected.setRenderer(new LVBeanListCellRenderer<String, Long>());
        comboOrganTissueAffected.addKeyListener(new LVBeanComboListener<String, Long>());
        comboOrganTissueAffected.setSelectedIndex(0);
    }

    /**
     * Initialize the JComboBox for breeding status.
     * <p>
     * A custom <code>ComboBoxModel</code> and <code>ListCellRenderer</code>
     * are used.
     */
    private void initBreedingStatus() {


        try {
            BreedingStatusDAO daoBreedingStatus =
                    BreedingStatusDAO.getInstance();

            List<BreedingStatusDTO> arrBs = daoBreedingStatus.loadAll();

            java.util.Collections.sort(arrBs, new java.util.Comparator<BreedingStatusDTO>() {

                public int compare(BreedingStatusDTO o1, BreedingStatusDTO o2) {
                    return o1.getText().compareTo(o2.getText());
                }
            });

            for (BreedingStatusDTO dto : arrBs) {
                comboBreedingStatus.addItem(dto.getText());
                if (dto.getText().indexOf("not specified") != -1) {
                    comboBreedingStatus.setSelectedItem(dto.getText());
                }
            }


        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Initialize the JList for chromosomes.
     * <p>
     * A custom <code>ComboBoxModel</code>, <code>ListCellRenderer</code>, and
     * <code>KeyListener</code> are used.
     *
     * @param lOrganismKey the organism for which chromsomes to display
     */
    private void initChromosomes(long lOrganismKey) {

        List chromos = null;
        ArrayList<LabelValueBean<String, Long>> arrChromosomes = new ArrayList<LabelValueBean<String, Long>>();
        ChromosomeDAO cDAO = ChromosomeDAO.getInstance();
        try {
            chromos = cDAO.loadByOrganismKey(lOrganismKey);
            Collections.sort(chromos, new ChromosomeComparator(ChromosomeDAO.ID_ORDERNUM));
            Iterator it = chromos.iterator();
            while (it.hasNext()) {
                ChromosomeDTO dto = (ChromosomeDTO) it.next();
                LabelValueBean<String, Long> lvb = new LabelValueBean();
                lvb.setLabel(dto.getChromosome());
                lvb.setValue(dto.getChromosomeKey());
                arrChromosomes.add(lvb);
            }
        } catch (SQLException e) {
            Utils.log(e);
        }


        // remove the key listeners so there are no duplicates
        KeyListener keylisteners[] = listCytogeneticChromosomes.getKeyListeners();
        for (int i = 0; i < keylisteners.length; i++) {
            listCytogeneticChromosomes.removeKeyListener(keylisteners[i]);
        }


        listCytogeneticChromosomes.setModel(new LVBeanListModel<String, Long>(arrChromosomes, false));
        listCytogeneticChromosomes.setCellRenderer(new LVBeanListCellRenderer<String, Long>());
        listCytogeneticChromosomes.addKeyListener(new LVBeanComboListener<String, Long>());
        listCytogeneticChromosomes.setSelectedIndex(0);
        listCytogeneticChromosomes.clearSelection();




    }

    /**
     * Initialize the JList for  Allele Types
     * <p>
     * A custom <code>ComboBoxModel</code>, <code>ListCellRenderer</code>, and
     * <code>KeyListener</code> are used.
     */
    private void initAlleleTypes() {

        Collection lvb = EIGlobals.getInstance().getAlleleTypes().values();
        Iterator it = lvb.iterator();
        List<LabelValueBean<String, Long>> arrChanges = new ArrayList<LabelValueBean<String, Long>>();
        while (it.hasNext()) {
            arrChanges.add((LabelValueBean<String, Long>) it.next());

        }


        arrChanges.add(0, new LabelValueBean<String, Long>("--Select--", -1L));
        comboCytogeneticChange.setModel(new LVBeanListModel<String, Long>(arrChanges));
        comboCytogeneticChange.setRenderer(new LVBeanListCellRenderer<String, Long>());
        comboCytogeneticChange.addKeyListener(new LVBeanComboListener<String, Long>());
        comboCytogeneticChange.setSelectedIndex(0);


    }

    private void initAssayTypes() {

        List<LabelValueBean<String, Long>> arrAssayTypes = new ArrayList<LabelValueBean<String, Long>>();

        Collection lvb = EIGlobals.getInstance().getAssayTypes().values();
        Iterator it = lvb.iterator();

        while (it.hasNext()) {
            arrAssayTypes.add((LabelValueBean<String, Long>) it.next());

        }



        arrAssayTypes.add(0, new LabelValueBean<String, Long>("--Select--", -1L));
        comboAssayType.setModel(new LVBeanListModel<String, Long>(arrAssayTypes));
        comboAssayType.setRenderer(new LVBeanListCellRenderer<String, Long>());
        comboAssayType.addKeyListener(new LVBeanComboListener<String, Long>());
        comboAssayType.setSelectedIndex(0);
    }

    /**
     * Initialize the JComboBox for treatment types/agents.
     * <p>
     * A custom <code>ComboBoxModel</code> and <code>ListCellRenderer</code>
     * are used.
     */
    private void initAgents() {
        final Map<Long, LabelValueDataBean<String, Long, Long>> mapAgents = EIGlobals.getInstance().getAgents();
        final List<LabelValueDataBean<String, Long, Long>> arrAgents = new ArrayList<LabelValueDataBean<String, Long, Long>>(mapAgents.values());
        arrAgents.add(new LabelValueDataBean<String, Long, Long>("--Select--", -1L, -1L));
        comboAgent.setModel(new LVDBeanListModel<String, Long, Long>(arrAgents));
        comboAgent.setRenderer(new LVDBeanListCellRenderer<String, Long, Long>());
        comboAgent.setSelectedIndex(0);

        // remove the key listeners so there are no duplicates
        KeyListener keylisteners[] = comboAgent.getKeyListeners();
        for (int i = 0; i < keylisteners.length; i++) {
            comboAgent.removeKeyListener(keylisteners[i]);
        }

        // install the new key listener
        comboAgent.addKeyListener(new LVDBeanComboListener());

        // for the filter
        final Map<Long, LabelValueBean<String, Long>> mapAgentTypes = EIGlobals.getInstance().getAgentTypes();
        final List<LabelValueBean<String, Long>> arrAgentTypes = new ArrayList<LabelValueBean<String, Long>>(mapAgentTypes.values());
        arrAgentTypes.add(new LabelValueBean<String, Long>("--Select--", -1L));
        comboAgentTypeFilter.setModel(new LVBeanListModel<String, Long>(arrAgentTypes));
        comboAgentTypeFilter.setRenderer(new LVBeanListCellRenderer<String, Long>());
        comboAgentTypeFilter.addKeyListener(new LVBeanComboListener<String, Long>());
        comboAgentTypeFilter.setSelectedIndex(0);
    }

    /**
     * Initialize the tumor progression panel.
     * <p>
     * Initialize the MXTable for tumor progression.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initTumorFrequencyProgression() {
        ///////////////////////////////////////////////////////////////////////
        // progression type (parent)
        ///////////////////////////////////////////////////////////////////////
        // nothing for now

        ///////////////////////////////////////////////////////////////////////
        // progression type (child)
        ///////////////////////////////////////////////////////////////////////
        final Map<Long, LabelValueBean<String, Long>> mapProgressionTypes =
                EIGlobals.getInstance().getTumorProgressionTypes();
        final List<LabelValueBean<String, Long>> arrProgressionTypesChildren =
                new ArrayList<LabelValueBean<String, Long>>(mapProgressionTypes.values());
        arrProgressionTypesChildren.add(0,
                new LabelValueBean<String, Long>("--Select--", -1L));
        comboProgressionTypeProgressionChild.setModel(
                new LVBeanListModel<String, Long>(arrProgressionTypesChildren));
        comboProgressionTypeProgressionChild.setRenderer(
                new LVBeanListCellRenderer<String, Long>());
        comboProgressionTypeProgressionChild.setSelectedIndex(0);

        // create the table model
        List<String> arrHeaders = new ArrayList<String>(3);
        arrHeaders.add("Child Tumor Frequency Key");
        arrHeaders.add("Tumor Progression Type");
        arrHeaders.add("Organ Affected");
        List arrTFProgressionChildren = new ArrayList();
        TFProgressionDTOTableModel<TumorProgressionDTO> tblmdlTumorProgression =
                new TFProgressionDTOTableModel<TumorProgressionDTO>(arrTFProgressionChildren,
                arrHeaders);
        fxtblProgression = new MXTable(tblmdlTumorProgression);
        fxtblProgression.setModel(tblmdlTumorProgression);

        // set the table options
        fxtblProgression.setDefaultRenderer(Object.class, new DTORenderer());
        //fxtblProgression.setColumnSizes(new int[]{100, 0, 0});
        fxtblProgression.setAlternateRowHighlight(true);
        fxtblProgression.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblProgression.setAlternateRowHighlightCount(2);
        fxtblProgression.setStartHighlightRow(1);
        fxtblProgression.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblProgression.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblProgression.enableToolTip(0, false);
        fxtblProgression.enableToolTip(1, false);

        // create the synonym delete button
        JButton btnDelTFProgression =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));
        btnDelTFProgression.setIconTextGap(0);
        btnDelTFProgression.setMargin(new Insets(0, 0, 0, 0));
        btnDelTFProgression.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                removeProgression();
            }
        });

        // update the JScrollPane
        jspProgressionChildren.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspProgressionChildren.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                btnDelTFProgression);
        jspProgressionChildren.setViewportView(fxtblProgression);

        // revalidate the panel
        pnlProgressionChild.revalidate();
    }

    /**
     * Initialize the MXTable for tumor frequency synonyms.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initTumorFrequencySynonyms() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(2);
        arrHeaders.add("J Number");
        arrHeaders.add("Synonym");
        List arrTFSynonyms = new ArrayList();
        TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO> tblmdlSynonyms =
                new TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO>(arrTFSynonyms, arrHeaders);
        fxtblSynonyms = new MXTable(tblmdlSynonyms);
        fxtblSynonyms.setModel(tblmdlSynonyms);

        // set the table options
        fxtblSynonyms.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblSynonyms.getColumnModel().getColumn(0).setCellEditor(
                new JNumberCellEditor());
        fxtblSynonyms.setColumnSizes(new int[]{75, 0});
        fxtblSynonyms.setAlternateRowHighlight(true);
        fxtblSynonyms.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblSynonyms.setAlternateRowHighlightCount(2);
        fxtblSynonyms.setStartHighlightRow(1);
        fxtblSynonyms.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblSynonyms.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblSynonyms.enableToolTip(0, false);
        fxtblSynonyms.enableToolTip(1, true);

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
        pnlTumorFrequencySynonyms.revalidate();
    }

    /**
     * Initialize the MXTable for tumor frequency notes.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initTumorFrequencyNotes() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(2);
        arrHeaders.add("J Number");
        arrHeaders.add("Note");
        List arrTFNotes = new ArrayList();
        TFNotesDTOTableModel<TumorFrequencyNotesDTO> tblmdlNotes =
                new TFNotesDTOTableModel<TumorFrequencyNotesDTO>(arrTFNotes, arrHeaders);
        fxtblNotes = new MXTable(tblmdlNotes);
        fxtblNotes.setModel(tblmdlNotes);

        // set the table options
        fxtblNotes.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblNotes.getColumnModel().getColumn(0).setCellEditor(
                new JNumberCellEditor());
        fxtblNotes.setColumnSizes(new int[]{75, 0});
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
        pnlTumorFrequencyNotes.revalidate();
    }

    /**
     * Initialize the MXTable for tumor frequency treatments.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initTumorFrequencyTreatments() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(3);
        arrHeaders.add("Agent Key");
        arrHeaders.add("Agent");
        arrHeaders.add("Agent Type");
        List arrTFTreatments = new ArrayList();
        TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO> tblmdlTreatments =
                new TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO>(arrTFTreatments, arrHeaders);
        fxtblTreatements = new MXTable(tblmdlTreatments);
        fxtblTreatements.setModel(tblmdlTreatments);

        // set the table options
        fxtblTreatements.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblTreatements.getColumnModel().getColumn(0).setCellEditor(
                new JNumberCellEditor());
        fxtblTreatements.setColumnSizes(new int[]{75, 0, 0});
        fxtblTreatements.setAlternateRowHighlight(true);
        fxtblTreatements.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblTreatements.setAlternateRowHighlightCount(2);
        fxtblTreatements.setStartHighlightRow(1);
        fxtblTreatements.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblTreatements.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblTreatements.enableToolTip(0, false);
        fxtblTreatements.enableToolTip(1, true);

        // create the treatment delete button
        JButton btnDelTreatment =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));
        btnDelTreatment.setIconTextGap(0);
        btnDelTreatment.setMargin(new Insets(0, 0, 0, 0));
        btnDelTreatment.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                removeAgent();
            }
        });

        // update the JScrollPane
        jspTreatmentTypes.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspTreatmentTypes.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                btnDelTreatment);
        jspTreatmentTypes.setViewportView(fxtblTreatements);

        // revalidate the panel
        //pnlTreatments.revalidate();
    }

    /**
     * Initialize the MXTable for tumor frequency pathology.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initTumorFrequencyPathology() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(5);
        arrHeaders.add("Pathology Key");
        arrHeaders.add("Pathologist");
        arrHeaders.add("Contributor");
        arrHeaders.add("Description");
        arrHeaders.add("# Images");
        List arrTumorPathologyAssoc = new ArrayList();
        TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> tblmdlPathology =
                new TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>(arrTumorPathologyAssoc,
                arrHeaders);
        fxtblPathology = new MXTable(tblmdlPathology);
        fxtblPathology.setModel(tblmdlPathology);

        // set the table options
        fxtblPathology.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblPathology.setColumnSizes(new int[]{100, 0, 0, 0, 75});
        fxtblPathology.setAlternateRowHighlight(true);
        fxtblPathology.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblPathology.setAlternateRowHighlightCount(2);
        fxtblPathology.setStartHighlightRow(1);
        fxtblPathology.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblPathology.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblPathology.enableToolTip(0, false);
        fxtblPathology.enableToolTip(1, true);

        // create the treatment delete button
        JButton btnDelPathology =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));
        btnDelPathology.setIconTextGap(0);
        btnDelPathology.setMargin(new Insets(0, 0, 0, 0));
        btnDelPathology.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                removePathology();
            }
        });

        final JPopupMenu popupMenu = new JPopupMenu();
        MXHeaderMenuItem headerItem = new MXHeaderMenuItem("Tumor Pathology Menu");
        popupMenu.add(headerItem);

        MXHtmlMenuItem itemNew = new MXHtmlMenuItem("Add New Pathology Report...");
        itemNew.setActionCommand(ACTION_COMMAND_ADD);
        itemNew.addActionListener(this);
        itemNew.setIcon(
                new ImageIcon(getClass().getResource(EIConstants.ICO_NEW_16)));

        popupMenu.add(itemNew);

        MXHtmlMenuItem itemEdit = new MXHtmlMenuItem("Edit Pathology Report...");
        itemEdit.setActionCommand(ACTION_COMMAND_EDIT);
        itemEdit.addActionListener(this);
        itemEdit.setIcon(
                new ImageIcon(getClass().getResource(EIConstants.ICO_EDIT_16)));

        popupMenu.add(itemEdit);

        MXHtmlMenuItem itemDelete = new MXHtmlMenuItem("Delete Pathology Report");
        itemDelete.setActionCommand(ACTION_COMMAND_DELETE);
        itemDelete.addActionListener(this);
        itemDelete.setIcon(
                new ImageIcon(getClass().getResource(EIConstants.ICO_DELETE_16)));

        popupMenu.add(itemDelete);

        // Set the component to show the popup menu
        fxtblPathology.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    Point pt = new Point(evt.getX(), evt.getY());
                    int nRow = fxtblPathology.rowAtPoint(pt);
                    if (nRow >= 0) {
                        fxtblPathology.setRowSelectionInterval(nRow, nRow);
                    }
                }
            }

            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    Point pt = new Point(evt.getX(), evt.getY());
                    int nRow = fxtblPathology.rowAtPoint(pt);
                    if (nRow >= 0) {
                        fxtblPathology.setRowSelectionInterval(nRow, nRow);
                    }
                }
            }
        });

        // update the JScrollPane
        jspPathology.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspPathology.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                btnDelPathology);
        jspPathology.setViewportView(fxtblPathology);

        // revalidate the panel
        pnlPathologyDetail.revalidate();
    }

    /**
     * Perform any custom initialization needed.
     */
    private void initCustom() {
        // create the factory

        // make it so the following fields accept numeric input only
        Utils.setNumericFilter(txtTumorFrequencyKey);
        Utils.setNumericFilter(txtStrainKey);
        Utils.setNumericFilter(txtTumorFrequencyKeyProgressionChild);
        Utils.setNumericFilter(txtPathologyKey);
        Utils.setNumericFilter(txtAllelePairKey);

        // make it so the following fields accept up to 255 characters
        //Utils.setTextLimit(txtareaMainNote, 255);
        //Utils.setTextLimit(txtNote, 255);
        //Utils.setTextLimit(txtareaCytogeneticNote, 255);


        // adjust components as needed
        if (nType == TUMOR_FREQUENCY_PANEL_ADD) {
        } else if (nType == TUMOR_FREQUENCY_PANEL_EDIT) {
            txtTumorFrequencyKey.setEditable(false);
            checkboxAutoAssign.setEnabled(false);
        }

        //DefaultListModel modelRO = new DefaultListModel();
        //listTreatmentTypeReadOnly.setModel(modelRO);

        DefaultListModel model = new DefaultListModel();
        listStrainSynonyms.setModel(model);

        bDuplicate = false;
        lProgressionTypeKeyForDup = 0l;

        ///////////////////////////////////////////////////////////////////////
        // tumor frequency
        ///////////////////////////////////////////////////////////////////////
        dtoTF = TumorFrequencyDAO.getInstance().createTumorFrequencyDTO();

        initOrgansTissuesOrigin();
        initTumorClassifications();
        initOrgansTissuesAffected();
        initBreedingStatus();
        initAgents();
        initAssayTypes();
        initTumorFrequencyProgression();
        initTumorFrequencySynonyms();
        initTumorFrequencyNotes();
        initTumorFrequencyTreatments();
        initTumorFrequencyPathology();
        //initOrganisms();
        initAlleleTypes();
        initChangeKeyToChangesNameMap();
        initAssayTypeKeyToAssayTypeNameMap();
        initChromosomes(mouseChromosome);
        initTumorFrequencyGenetics();
        initTumorFrequencyGeneticChanges();
    }

    /**
     * Insert the tumor frequency information and associated data in the
     * database.
     * <p>
     * This is an all or nothing insert.  Either everything the user has
     * filled in gets comitted to the database or nothing does.
     */
    private void insertData() {
        TumorFrequencyDAO daoTF = TumorFrequencyDAO.getInstance();
        TumorFrequencyNotesDAO daoTFNotes = TumorFrequencyNotesDAO.getInstance();
        TumorFrequencySynonymsDAO daoTFSynonyms = TumorFrequencySynonymsDAO.getInstance();
        TumorFrequencyTreatmentsDAO daoTFTreatments = TumorFrequencyTreatmentsDAO.getInstance();
        TumorTypeDAO daoTumorType = TumorTypeDAO.getInstance();
        TumorPathologyAssocDAO daoTumorPathologyAssoc = TumorPathologyAssocDAO.getInstance();
        TumorProgressionDAO daoTumorProgression = TumorProgressionDAO.getInstance();
        TumorGeneticsDAO daoTumorGenetics = TumorGeneticsDAO.getInstance();
        MTBTumorGeneticChangesDAO daoTumorGeneticChanges = MTBTumorGeneticChangesDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        boolean commit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the tumor frequency
            ///////////////////////////////////////////////////////////////////
            // tumor frequency key
            boolean bAutoAssign = checkboxAutoAssign.isSelected();
            String strTumorFrequency = txtTumorFrequencyKey.getText();
            long lTumorFrequencyKey = -1;

            dtoTF = daoTF.createTumorFrequencyDTO();

            if (!bAutoAssign) {
                try {
                    lTumorFrequencyKey = Long.parseLong(strTumorFrequency);
                } catch (Exception e) {
                    Utils.log(e);
                    return;
                }
                dtoTF.setTumorFrequencyKey(lTumorFrequencyKey);
            }


            // age onset
            String strTemp = txtAgeOnset.getText();
            dtoTF.setAgeOnset(StringUtils.hasValue(strTemp) ? strTemp : null);

            // age detection
            strTemp = txtAgeDetection.getText();
            dtoTF.setAgeDetection(StringUtils.hasValue(strTemp) ? strTemp : null);

            // colony size
            strTemp = txtColonySize.getText();
            dtoTF.setColonySize(StringUtils.hasValue(strTemp) ? new Long(strTemp) : null);

            // num mice affected
            strTemp = txtNumMiceAffected.getText();
            dtoTF.setNumMiceAffected(StringUtils.hasValue(strTemp) ? new Long(strTemp) : null);

            // incidence
            strTemp = txtIncidence.getText();
            long inc = 0;
            try {
                Long l = Long.parseLong(strTemp);
                inc = l.longValue();
            } catch (Exception e) {
                // do nothing incidence can be non numeric text
            }
            if (inc > 100) {
                throw new Exception("Incidence is greater than 100");
            }
            if (strTemp == null || strTemp.length() == 0) {
                throw new Exception("Please enter a value for incidence");
            }

            dtoTF.setIncidence(StringUtils.hasValue(strTemp) ? strTemp : null);

            // frequency (sort equivalent)
            strTemp = txtSortEquivalent.getText();
            dtoTF.setFreqNum(StringUtils.hasValue(strTemp) ? new java.math.BigDecimal(strTemp) : null);

            // breeding status
            strTemp = (String) comboBreedingStatus.getSelectedItem();
            if (StringUtils.equals(strTemp, "--Select--")) {
                strTemp = null;
            }
            dtoTF.setBreedingStatus(StringUtils.hasValue(strTemp) ? strTemp : null);

            // infection status
            strTemp = txtInfectionStatus.getText();
            dtoTF.setInfectionStatus(StringUtils.hasValue(strTemp) ? strTemp : null);

            // note
            strTemp = txtareaMainNote.getText();
            dtoTF.setNote(StringUtils.hasValue(strTemp) ? strTemp : null);

            // reference
            strTemp = txtJNumber.getText();
            if (StringUtils.hasValue(strTemp)) {
                strTemp = Utils.fixJNumber(strTemp);
                txtJNumber.setText(strTemp);
                try {
                    long lRefKey = EIGlobals.getInstance().getRefByAcc(strTemp);
                    dtoTF.setReferenceKey(lRefKey);
                } catch (Exception e) {
                    Utils.log(e);
                    throw e;
                }
            } else {
                throw new Exception("You must enter a JNumber.");
            }

            // strain key
            strTemp = txtStrainKey.getText();
            dtoTF.setStrainKey(StringUtils.hasValue(strTemp) ? new Long(strTemp) : null);

            // strain sex
     /*
            F Female
            M Male
            U Not specified
            X Mixed population
             */
            strTemp = (String) comboStrainSex.getSelectedItem();

            if (StringUtils.equals(strTemp, "Mixed population")) {
                strTemp = "X";
            } else if (StringUtils.equals(strTemp, "Not specified")) {
                strTemp = "U";
            } else {
                strTemp = strTemp.substring(0, 1);
            }
            dtoTF.setSexKey(strTemp);

            // organ/tissue affectd
            if (comboOrganTissueAffected.getSelectedIndex() >= 0) {
                LVBeanListModel<String, Long> modelOA = (LVBeanListModel<String, Long>) comboOrganTissueAffected.getModel();
                LabelValueBean<String, Long> beanOA = modelOA.getElementAt(comboOrganTissueAffected.getSelectedIndex());
                dtoTF.setOrganAffectedKey(new Long(beanOA.getValue()));
            }

            Long lOrganTissueOriginKey = new Long(-1);
            Long lTumorClassificationKey = new Long(-1);

            // organ of origin
            if (comboOrganTissueOrigin.getSelectedIndex() >= 0) {
                LVBeanListModel<String, Long> modelOrganTissueOrigin = (LVBeanListModel<String, Long>) comboOrganTissueOrigin.getModel();
                LabelValueBean<String, Long> beanOrganTissueOrigin = modelOrganTissueOrigin.getElementAt(comboOrganTissueOrigin.getSelectedIndex());
                lOrganTissueOriginKey = new Long(beanOrganTissueOrigin.getValue());
            }

            // tumor class
            if (comboTumorClassification.getSelectedIndex() >= 0) {
                LVBeanListModel<String, Long> modelTC = (LVBeanListModel<String, Long>) comboTumorClassification.getModel();
                LabelValueBean<String, Long> beanTC = modelTC.getElementAt(comboTumorClassification.getSelectedIndex());
                lTumorClassificationKey = new Long(beanTC.getValue());
            }

            // tumor type
            TumorTypeDTO dtoTumorType = daoTumorType.createTumorTypeDTO();
            dtoTumorType.setOrganKey(lOrganTissueOriginKey);
            dtoTumorType.setTumorClassificationKey(lTumorClassificationKey);
            List<TumorTypeDTO> dtoTumorTypes = daoTumorType.loadUsingTemplate(dtoTumorType);

            //EIGlobals.getInstance().log("organ origin key = " + ooKey);
            //EIGlobals.getInstance().log("tumor classification key = " + tcKey);
            //EIGlobals.getInstance().log("dtoTumorTypes.length = " + dtoTumorTypes.length);

            if (dtoTumorTypes.size() == 0) {
                // add the tumor type
                dtoTumorType.setOrganKey(lOrganTissueOriginKey);
                dtoTumorType.setTumorClassificationKey(lTumorClassificationKey);
                dtoTumorType.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                dtoTumorType.setCreateDate(new java.util.Date());
                dtoTumorType.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                dtoTumorType.setUpdateDate(new java.util.Date());
                dtoTumorType = daoTumorType.save(dtoTumorType);
                dtoTF.setTumorTypeKey(dtoTumorType.getTumorTypeKey());
            } else {
                dtoTF.setTumorTypeKey(dtoTumorTypes.get(0).getTumorTypeKey());
            }

            // add the audit information
            dtoTF.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoTF.setCreateDate(new java.util.Date());
            dtoTF.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoTF.setUpdateDate(new java.util.Date());

            dtoTF = daoTF.save(dtoTF);

            ///////////////////////////////////////////////////////////////////
            // save the accession information
            ///////////////////////////////////////////////////////////////////

            AccessionMaxDAO amaxDAO = AccessionMaxDAO.getInstance();
            List<AccessionMaxDTO> maxList = amaxDAO.loadAll();
            AccessionMaxDTO maxDTO = maxList.get(0);
            Long max = maxDTO.getMaxNumericPart();
            max++;
            maxDTO.setMaxNumericPart(max);
            amaxDAO.save(maxDTO);
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setAccID("MTB:" + max);
            dtoAccession.setObjectKey(dtoTF.getTumorFrequencyKey());
            dtoAccession.setSiteInfoKey(0);
            dtoAccession.setMTBTypesKey(5);
            dtoAccession.setPrefixPart("MTB:");
            dtoAccession.setNumericPart(max);
            dtoAccession.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoAccession.setCreateDate(new java.util.Date());
            dtoAccession.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoAccession.setUpdateDate(new java.util.Date());
            daoAccession.save(dtoAccession);

            ///////////////////////////////////////////////////////////////////
            // save the notes
            ///////////////////////////////////////////////////////////////////
            TFNotesDTOTableModel<TumorFrequencyNotesDTO> modelNotes = (TFNotesDTOTableModel<TumorFrequencyNotesDTO>) fxtblNotes.getModel();
            List<TumorFrequencyNotesDTO> arrTFNotes = modelNotes.getAllData();
            if (arrTFNotes != null) {
                TumorFrequencyNotesDTO[] arrNoteTemp = (TumorFrequencyNotesDTO[]) arrTFNotes.toArray(new TumorFrequencyNotesDTO[arrTFNotes.size()]);
                for (TumorFrequencyNotesDTO dtoTemp : arrTFNotes) {
                    dtoTemp.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
                }
                daoTFNotes.save(arrTFNotes);
            }

            ///////////////////////////////////////////////////////////////////
            // save the synonyms
            ///////////////////////////////////////////////////////////////////
            TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO> modelSynonyms = (TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO>) fxtblSynonyms.getModel();
            List<TumorFrequencySynonymsDTO> arrTFSynonyms = modelSynonyms.getAllData();
            if (arrTFSynonyms != null) {
                for (TumorFrequencySynonymsDTO dtoTemp : arrTFSynonyms) {
                    dtoTemp.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
                }
                daoTFSynonyms.save(arrTFSynonyms);
            }

            ///////////////////////////////////////////////////////////////////
            // save the treatments
            ///////////////////////////////////////////////////////////////////
            TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO> modelTreatments = (TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO>) fxtblTreatements.getModel();
            List<TumorFrequencyTreatmentsDTO> arrTFTreatments = modelTreatments.getAllData();
            if (arrTFTreatments != null) {
                for (TumorFrequencyTreatmentsDTO dtoTemp : arrTFTreatments) {
                    dtoTemp.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
                }
                daoTFTreatments.save(arrTFTreatments);
            }

            ///////////////////////////////////////////////////////////////////
            // save the tumor genetic changes
            ///////////////////////////////////////////////////////////////////
            MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO> modelGeneticChanges = (MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO>) fxtblGeneticChanges.getModel();
            List<MTBTumorGeneticChangesDTO> arrTumorGeneticChanges = modelGeneticChanges.getAllData();
            if (arrTumorGeneticChanges != null) {
                for (MTBTumorGeneticChangesDTO dtoTemp : arrTumorGeneticChanges) {
                    dtoTemp.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
                }
                daoTumorGeneticChanges.save(arrTumorGeneticChanges);
            }

            ///////////////////////////////////////////////////////////////////
            // save the tumor genetics
            ///////////////////////////////////////////////////////////////////
            TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO> modelGenetics = (TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO>) fxtblGenetics.getModel();
            List<TumorGeneticsDTO> arrTumorGenetics = modelGenetics.getAllData();
            if (arrTumorGenetics != null) {
                for (TumorGeneticsDTO dtoTemp : arrTumorGenetics) {
                    dtoTemp.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
                }
                daoTumorGenetics.save(arrTumorGenetics);
            }

            ///////////////////////////////////////////////////////////////////
            // save the pathology association
            ///////////////////////////////////////////////////////////////////
            TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> modelPathology = (TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>) fxtblPathology.getModel();
            List<TumorPathologyAssocDTO> arrTumorPathologyAssoc = modelPathology.getAllData();
            if (arrTumorPathologyAssoc != null) {
                for (TumorPathologyAssocDTO dtoTemp : arrTumorPathologyAssoc) {
                    dtoTemp.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
                }
                daoTumorPathologyAssoc.save(arrTumorPathologyAssoc);
            }

            // check to make sure 2 progression children do not have the same parent
            checkTFPDuplication();

            ///////////////////////////////////////////////////////////////////
            // save the tumor progression information (parent)
            ///////////////////////////////////////////////////////////////////
            if (bDuplicate) {
                String temp = txtTumorFrequencyKeyProgressionParent.getText();
                if (StringUtils.hasValue(temp)) {
                    long tfPKey = Long.parseLong(temp);
                    TumorProgressionDTO dtoPTempInsert = daoTumorProgression.createTumorProgressionDTO();
                    dtoPTempInsert.setParentKey(new Long(tfPKey));
                    dtoPTempInsert.setChildKey(dtoTF.getTumorFrequencyKey());
                    dtoPTempInsert.setTumorProgressionTypeKey(lProgressionTypeKeyForDup);
                    dtoPTempInsert.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                    dtoPTempInsert.setCreateDate(new Date());
                    dtoPTempInsert.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                    dtoPTempInsert.setUpdateDate(new Date());
                    daoTumorProgression.save(dtoPTempInsert);
                }
            }

            ///////////////////////////////////////////////////////////////////
            // save the tumor progression information (children)
            ///////////////////////////////////////////////////////////////////
            TFProgressionDTOTableModel<TumorProgressionDTO> modelProgression = (TFProgressionDTOTableModel<TumorProgressionDTO>) fxtblProgression.getModel();
            List<TumorProgressionDTO> arrTFProgressionChildren = modelProgression.getAllData();

            if (arrTFProgressionChildren != null) {
                for (TumorProgressionDTO dtoTemp : arrTFProgressionChildren) {
                    dtoTemp.setParentKey(dtoTF.getTumorFrequencyKey());
                }

                daoTumorProgression.save(arrTFProgressionChildren);
            }


            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            commit = true;
        } catch (Exception e) {
            progressMonitor.setCurrent("Exception!",
                    progressMonitor.getTotal());
            Utils.showErrorDialog(e.getMessage(), e);
        } finally {
            try {
                progressMonitor.setCurrent("Done",
                        progressMonitor.getTotal());
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to add Tumor Frequency.", e2);
            }
            if (commit) {
                switchFromAddToEdit();
            } else {
                Utils.showErrorDialog("Unable to add Tumor Frequency.");
            }
        }
    }

    /**
     * Update the tumor frequency information and associated data in the
     * database.
     * <p>
     * This is an all or nothing update.  Either everything the user has
     * updated gets comitted to the database or nothing does.
     */
    private void updateData() {
        TumorFrequencyDAO daoTF = TumorFrequencyDAO.getInstance();
        TumorFrequencyNotesDAO daoTFNotes = TumorFrequencyNotesDAO.getInstance();
        TumorFrequencySynonymsDAO daoTFSynonyms = TumorFrequencySynonymsDAO.getInstance();
        TumorFrequencyTreatmentsDAO daoTFTreatments = TumorFrequencyTreatmentsDAO.getInstance();
        TumorTypeDAO daoTumorType = TumorTypeDAO.getInstance();
        TumorPathologyAssocDAO daoTumorPathologyAssoc = TumorPathologyAssocDAO.getInstance();
        TumorProgressionDAO daoTumorProgression = TumorProgressionDAO.getInstance();
        TumorGeneticsDAO daoTumorGenetics = TumorGeneticsDAO.getInstance();
        MTBTumorGeneticChangesDAO daoTumorGeneticChanges = MTBTumorGeneticChangesDAO.getInstance();
        AccessionDAO daoAccession = AccessionDAO.getInstance();

        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the tumor frequency
            ///////////////////////////////////////////////////////////////////
            // age onset
            String strTemp = txtAgeOnset.getText();
            dtoTF.setAgeOnset(StringUtils.hasValue(strTemp) ? strTemp : null);

            // age detection
            strTemp = txtAgeDetection.getText();
            dtoTF.setAgeDetection(StringUtils.hasValue(strTemp) ? strTemp : null);

            // colony size
            strTemp = txtColonySize.getText();
            dtoTF.setColonySize(StringUtils.hasValue(strTemp) ? new Long(strTemp) : null);

            // num mice affected
            strTemp = txtNumMiceAffected.getText();
            dtoTF.setNumMiceAffected(StringUtils.hasValue(strTemp) ? new Long(strTemp) : null);

            // incidence
            strTemp = txtIncidence.getText();
            
            if (strTemp.trim().length()==0) {
                throw new Exception("Incidence must be entered.");
            }
            
            long inc = 0;
            try {
                Long l = Long.parseLong(strTemp);
                inc = l.longValue();
            } catch (Exception e) {
                // do nothing incidence can be non numeric text
            }
            if (inc > 100) {
                throw new Exception("Incidence is greater than 100");
            }

            dtoTF.setIncidence(StringUtils.hasValue(strTemp) ? strTemp : null);

            // frequency
            strTemp = txtSortEquivalent.getText();
            dtoTF.setFreqNum(StringUtils.hasValue(strTemp) ? new java.math.BigDecimal(strTemp) : null);

            // breeding status
            strTemp = (String) comboBreedingStatus.getSelectedItem();
            if (StringUtils.equals(strTemp, "--Select --")) {
                strTemp = null;
            }
            dtoTF.setBreedingStatus(StringUtils.hasValue(strTemp) ? strTemp : null);

            // infection status
            strTemp = txtInfectionStatus.getText();
            dtoTF.setInfectionStatus(StringUtils.hasValue(strTemp) ? strTemp : null);

            // note
            strTemp = txtareaMainNote.getText();
            dtoTF.setNote(StringUtils.hasValue(strTemp) ? strTemp : null);

            // reference
            strTemp = txtJNumber.getText();
            if (StringUtils.hasValue(strTemp)) {
                strTemp = Utils.fixJNumber(strTemp);
                txtJNumber.setText(strTemp);
                try {
                    long refKey = EIGlobals.getInstance().getRefByAcc(strTemp);
                    if(refKey<1){
                        throw new Exception("You must enter a vaild JNumber");
                    }
                    dtoTF.setReferenceKey(refKey);
                } catch (Exception e) {
                    Utils.log(e);
                    throw e;
                }
            } else {
                throw new Exception("You must enter a JNumber.");
            }

            // strain key
            strTemp = txtStrainKey.getText();
            dtoTF.setStrainKey(StringUtils.hasValue(strTemp) ? new Long(strTemp) : null);

            // strain sex
            /*
            F Female
            M Male
            U Not specified
            X Mixed population
             */
            strTemp = (String) comboStrainSex.getSelectedItem();

            if (StringUtils.equals(strTemp, "Mixed population")) {
                strTemp = "X";
            } else if (StringUtils.equals(strTemp, "Not specified")) {
                strTemp = "U";
            } else {
                strTemp = strTemp.substring(0, 1);
            }
            dtoTF.setSexKey(strTemp);

            // organ/tissue affectd
            if (comboOrganTissueAffected.getSelectedIndex() >= 0) {
                LVBeanListModel<String, Long> modelOA = (LVBeanListModel<String, Long>) comboOrganTissueAffected.getModel();
                LabelValueBean<String, Long> beanOA = modelOA.getElementAt(comboOrganTissueAffected.getSelectedIndex());
                dtoTF.setOrganAffectedKey(new Long(beanOA.getValue()));
            }

            Long lOrganTissueOriginKey = new Long(-1);
            Long lTumorClassificationKey = new Long(-1);

            // organ of origin
            if (comboOrganTissueOrigin.getSelectedIndex() >= 0) {
                LVBeanListModel<String, Long> modelOrganTissueOrigin = (LVBeanListModel<String, Long>) comboOrganTissueOrigin.getModel();
                LabelValueBean<String, Long> beanOrganTissueOrigin = modelOrganTissueOrigin.getElementAt(comboOrganTissueOrigin.getSelectedIndex());
                lOrganTissueOriginKey = new Long(beanOrganTissueOrigin.getValue());
            }

            // tumor class
            if (comboTumorClassification.getSelectedIndex() >= 0) {
                LVBeanListModel<String, Long> modelTC = (LVBeanListModel<String, Long>) comboTumorClassification.getModel();
                LabelValueBean<String, Long> beanTC = modelTC.getElementAt(comboTumorClassification.getSelectedIndex());
                lTumorClassificationKey = new Long(beanTC.getValue());
            }

            // tumor type
            TumorTypeDTO dtoTumorType = daoTumorType.createTumorTypeDTO();
            dtoTumorType.setOrganKey(lOrganTissueOriginKey);
            dtoTumorType.setTumorClassificationKey(lTumorClassificationKey);
            List<TumorTypeDTO> dtoTumorTypes = daoTumorType.loadUsingTemplate(dtoTumorType);

            //EIGlobals.getInstance().log("organ origin key = " + lOrganTissueOriginKey);
            //EIGlobals.getInstance().log("tumor classification key = " + lTumorClassificationKey);
            //EIGlobals.getInstance().log("dtoTumorTypes.length = " + dtoTumorTypes.length);

            if (dtoTumorTypes.size() == 0) {
                // add the tumor type
                dtoTumorType.setOrganKey(lOrganTissueOriginKey);
                dtoTumorType.setTumorClassificationKey(lTumorClassificationKey);
                dtoTumorType.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                dtoTumorType.setCreateDate(new java.util.Date());
                dtoTumorType.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
                dtoTumorType.setUpdateDate(new java.util.Date());
                dtoTumorType = daoTumorType.save(dtoTumorType);
                dtoTF.setTumorTypeKey(dtoTumorType.getTumorTypeKey());
            } else {
                dtoTF.setTumorTypeKey(dtoTumorTypes.get(0).getTumorTypeKey());
            }
            
            dtoTF.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoTF.setUpdateDate(new java.util.Date());

            ////////////////
            daoTF.save(dtoTF);

            ///////////////////////////////////////////////////////////////////
            // why would we save the accession information? it allready exists this is an update
            ///////////////////////////////////////////////////////////////////
            /*
            AccessionDTO dtoAccession = daoAccession.createAccessionDTO();
            dtoAccession.setObjectKey(dtoTF.getTumorFrequencyKey());
            dtoAccession.setSiteInfoKey(0);
            dtoAccession.setMTBTypesKey(5);
            dtoAccession.setPrefixPart("MTB:");
            dtoAccession.setNumericPart(dtoTF.getTumorFrequencyKey());
            daoAccession.loadUniqueUsingTemplate(dtoAccession);
            dtoAccession.setUpdateDate(new java.util.Date());
            daoAccession.save(dtoAccession);
            */
            
            ///////////////////////////////////////////////////////////////////
            // save the synonyms
            ///////////////////////////////////////////////////////////////////
            TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO> modelSynonyms = (TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO>) fxtblSynonyms.getModel();
            List<TumorFrequencySynonymsDTO> arrTFSynonyms = modelSynonyms.getAllData();
            if (arrTFSynonyms != null) {
                daoTFSynonyms.save(arrTFSynonyms);
            }

            ///////////////////////////////////////////////////////////////////
            // save the notes
            ///////////////////////////////////////////////////////////////////
            TFNotesDTOTableModel<TumorFrequencyNotesDTO> modelNotes = (TFNotesDTOTableModel<TumorFrequencyNotesDTO>) fxtblNotes.getModel();
            List<TumorFrequencyNotesDTO> arrTFNotes = modelNotes.getAllData();
            if (arrTFNotes != null) {
                daoTFNotes.save(arrTFNotes);
            }

            ///////////////////////////////////////////////////////////////////
            // save the treatments
            ///////////////////////////////////////////////////////////////////
            TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO> modelTreatments =
                    (TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO>) fxtblTreatements.getModel();
            List<TumorFrequencyTreatmentsDTO> arrTFTreatments = modelTreatments.getAllData();
            if (arrTFTreatments != null) {
                daoTFTreatments.save(arrTFTreatments);
            }

            ///////////////////////////////////////////////////////////////////
            // save the tumor genetic changes
            ///////////////////////////////////////////////////////////////////
            MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO> modelGeneticChanges =
                    (MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO>) fxtblGeneticChanges.getModel();
            List<MTBTumorGeneticChangesDTO> arrGeneticChanges = modelGeneticChanges.getAllData();
            if (arrGeneticChanges != null) {
                daoTumorGeneticChanges.save(arrGeneticChanges);
            }

            ///////////////////////////////////////////////////////////////////
            // save the tumor genetics
            ///////////////////////////////////////////////////////////////////
            TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO> modelGenetics =
                    (TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO>) fxtblGenetics.getModel();
            List<TumorGeneticsDTO> arrGenetics = modelGenetics.getAllData();
            if (arrGenetics != null) {
                daoTumorGenetics.save(arrGenetics);
            }

            ///////////////////////////////////////////////////////////////////
            // save the pathology associations
            ///////////////////////////////////////////////////////////////////
            TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> modelPathology = (TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>) fxtblPathology.getModel();
            List<TumorPathologyAssocDTO> arrTumorPathologyAssoc = modelPathology.getAllData();
            if (arrTumorPathologyAssoc != null) {
                daoTumorPathologyAssoc.save(arrTumorPathologyAssoc);
            }

            // check to make sure 2 progression children do not have the same parent
            checkTFPDuplication();

            ///////////////////////////////////////////////////////////////////
            // save the tumor progression information (children)
            ///////////////////////////////////////////////////////////////////
            TFProgressionDTOTableModel<TumorProgressionDTO> modelProgression = (TFProgressionDTOTableModel<TumorProgressionDTO>) fxtblProgression.getModel();
            List<TumorProgressionDTO> arrTFProgressionChildren = modelProgression.getAllData();
            if (arrTFProgressionChildren != null) {
                daoTumorProgression.save(arrTFProgressionChildren);
            }

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            bCommit = true;
        } catch (Exception e) {
            progressMonitor.setCurrent("Exception!",
                    progressMonitor.getTotal());
            Utils.showErrorDialog(e.getMessage(), e);
        } finally {
            try {
                progressMonitor.setCurrent("Exception!",
                        progressMonitor.getTotal());
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(bCommit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to update Tumor Frequency.", e2);
            }
            if (bCommit) {
                setKey(dtoTF.getTumorFrequencyKey().longValue());
                JTextField tempTF = new JTextField();
                txtStrainKey.setBackground(tempTF.getBackground());
            } else {
                Utils.showErrorDialog("Unable to update Tumor Frequency.");
            }
        }
    }

    /**
     * Save the tumor frequency information.
     * <p>
     * Depending upon the type, the tumor frequency information will either be
     * updated or inserted. A <code>MXProgressMonitor</code>
     * is used to display visual feedback to the user.
     */
    public void save() {
        // The following code saves the current value in the cell being edited
        // and stops the editing process:
        if (fxtblNotes.getCellEditor() != null) {
            fxtblNotes.getCellEditor().stopCellEditing();
        }

        if (fxtblPathology.getCellEditor() != null) {
            fxtblPathology.getCellEditor().stopCellEditing();
        }

        if (fxtblProgression.getCellEditor() != null) {
            fxtblProgression.getCellEditor().stopCellEditing();
        }

        if (fxtblSynonyms.getCellEditor() != null) {
            fxtblSynonyms.getCellEditor().stopCellEditing();
        }

        if (fxtblTreatements.getCellEditor() != null) {
            fxtblTreatements.getCellEditor().stopCellEditing();
        }

        TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO> modelSynonyms = (TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO>) fxtblSynonyms.getModel();
        List<TumorFrequencySynonymsDTO> arrTFSynonyms = modelSynonyms.getAllData();

        boolean save = false;
        if (arrTFSynonyms != null || arrTFSynonyms.size() > 0) {

            for (TumorFrequencySynonymsDTO dto : arrTFSynonyms) {
                if (!dto.isOld()) {
                    save = true;
                }
            }
        }
        if (!save) {
            Utils.showErrorDialog("A synonym is required");
            return;

        }


        progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
        try {
            if (nType == TUMOR_FREQUENCY_PANEL_ADD) {
                progressMonitor.start("Inserting Tumor Frequency...");
                insertData();
            } else if (nType == TUMOR_FREQUENCY_PANEL_EDIT) {
                progressMonitor.start("Updating Tumor Frequency...");
                updateData();
            }

            progressMonitor.setCurrent("Done!",
                    progressMonitor.getTotal());
        } catch (Exception e) {
            progressMonitor.setCurrent("Exception!",
                    progressMonitor.getTotal());
            Utils.log(e);
        } finally {
            // to ensure that progress dlg is closed in case of
            // any exception
            progressMonitor.setCurrent("Done!",
                    progressMonitor.getTotal());

        }

        setUpdated(false);

    }

    /**
     * Duplicate the tumor frequency information.
     */
    public void duplicate() {
        EIGlobals.getInstance().getMainFrame().launchTumorFrequencyDuplicateWindow(
                dtoTF.getTumorFrequencyKey().longValue());
    }

    /**
     * Simple method to close the add form and switch to the edit form.  The
     * window location is tracked to make it seemless to the end user.
     */
    private void switchFromAddToEdit() {
        customInternalFrame.dispose();
        EIGlobals.getInstance().getMainFrame().launchTumorFrequencyEditWindow(
                dtoTF.getTumorFrequencyKey().longValue(),
                customInternalFrame.getLocation());
    }

    /**
     * Add a note to the tumor frequency notes table provided a note has been
     * filled in.  The JNumber must have a value, be in a valid format, and
     * exist in the database.
     */
    private void addNote() {
        String strNote = txtNote.getText().trim();
        String strJNumber = txtJNumberNote.getText().trim();
        long lRefKey = -1;

        // validate that a note has been entered
        if (!StringUtils.hasValue(strNote)) {
            Utils.showErrorDialog("Please enter a note.");
            txtNote.grabFocus();
            return;
        }

        // validate that a JNumber has been entered
        if (!StringUtils.hasValue(strJNumber)) {
            Utils.showErrorDialog("Please enter a JNumber for a note.");
            txtJNumberNote.grabFocus();
            return;
        }

        // validate that the JNumber is valid
        try {
            lRefKey = EIGlobals.getInstance().getRefByAcc(strJNumber);

            if (lRefKey <= 0) {
                JOptionPane.showMessageDialog(null,
                        strJNumber
                        + " is not a valid JNumber.");
                txtJNumberNote.requestFocus();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, strJNumber
                    + " is not a valid JNumber.");
            txtJNumberNote.requestFocus();
            return;
        }

        // get the table model
        TFNotesDTOTableModel<TumorFrequencyNotesDTO> tblmdlNotes =
                (TFNotesDTOTableModel<TumorFrequencyNotesDTO>) fxtblNotes.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        TumorFrequencyNotesDTO dtoNote =
                TumorFrequencyNotesDAO.getInstance().createTumorFrequencyNotesDTO();

        dtoNote.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
        dtoNote.setReferenceKey(lRefKey);
        dtoNote.setNote(strNote);
        dtoNote.setCreateUser(dtoUser.getUserName());
        dtoNote.setCreateDate(dNow);
        dtoNote.setUpdateUser(dtoUser.getUserName());
        dtoNote.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoNote.getDataBean().put(EIConstants.JNUM, strJNumber);

        // add it to the table
        tblmdlNotes.addRow(dtoNote);

        Utils.scrollToVisible(fxtblNotes, fxtblNotes.getRowCount() - 1, 0);
    }

    /**
     * Add a note to the tumor frequency synonyms table provided a synonym has
     * been  filled in.  The JNumber must have a value, be in a valid format,
     * and exist in the database.
     */
    private void addSynonym() {
        String strSynonym = txtSynonym.getText().trim();
        String strJNumber = txtJNumberSynonym.getText().trim();
        long lRefKey = -1;

        // validate that a note has been entered
        if (!StringUtils.hasValue(strSynonym)) {
            Utils.showErrorDialog("Please enter a note.");
            txtSynonym.grabFocus();
            return;
        }

        // validate that a JNumber has been entered
        if (!StringUtils.hasValue(strJNumber)) {
            Utils.showErrorDialog("Please enter a JNumber for a note.");
            txtJNumberSynonym.grabFocus();
            return;
        }

        // validate that the JNumber is valid
        try {
            lRefKey = EIGlobals.getInstance().getRefByAcc(strJNumber);

            if (lRefKey <= 0) {
                JOptionPane.showMessageDialog(null,
                        strJNumber
                        + " is not a valid JNumber.");
                txtJNumberSynonym.requestFocus();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, strJNumber
                    + " is not a valid JNumber.");
            txtJNumberSynonym.requestFocus();
            return;
        }

        // get the table model
        TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO> tblmdlSynonyms =
                (TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO>) fxtblSynonyms.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        TumorFrequencySynonymsDTO dtoSynonym =
                TumorFrequencySynonymsDAO.getInstance().
                createTumorFrequencySynonymsDTO();

        dtoSynonym.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
        dtoSynonym.setName(strSynonym);
        dtoSynonym.setReferenceKey(lRefKey);
        dtoSynonym.setCreateUser(dtoUser.getUserName());
        dtoSynonym.setCreateDate(dNow);
        dtoSynonym.setUpdateUser(dtoUser.getUserName());
        dtoSynonym.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoSynonym.getDataBean().put(EIConstants.JNUM, strJNumber);

        // add it to the table
        tblmdlSynonyms.addRow(dtoSynonym);

        Utils.scrollToVisible(fxtblSynonyms,
                fxtblSynonyms.getRowCount() - 1, 0);
    }

    /**
     * Add a progression record the tumor progression table provided a tumor
     * frequency key has been filled in.  Also, the progression type must also
     * have a value.
     */
    private void addProgression() {
        int nSelected = comboProgressionTypeProgressionChild.getSelectedIndex();
        LVBeanListModel<String, Long> modelProgressionType =
                (LVBeanListModel<String, Long>) comboProgressionTypeProgressionChild.getModel();
        LabelValueBean<String, Long> bean = modelProgressionType.getElementAt(nSelected);
        Long lProgressionTypeKey = new Long(bean.getValue());

        if (lProgressionTypeKey == -1) {
            Utils.showErrorDialog("Please select a progression type");
            return;
        }

        String strProgressionType = bean.getLabel();

        String strChildKey = txtTumorFrequencyKeyProgressionChild.getText();
        long lChildKey = -1;


        try {
            lChildKey = Long.parseLong(strChildKey);
        } catch (Exception e) {
            Utils.showErrorDialog(strChildKey + " is not a valid Tumor "
                    + "Frequency Key.\n\nPlease enter a "
                    + "valid Tumor Frequency Key.");
            txtTumorFrequencyKeyProgressionChild.grabFocus();
            return;
        }
        MTBTumorFrequencyDetailDTO dtoTumorFrequency = null;
        try {
            // validate the child key exists
            dtoTumorFrequency =
                    MTBTumorUtilDAO.getInstance().getTumorFrequencyDetail(lChildKey);
        } catch (Exception e) {
        }

        if ((dtoTumorFrequency == null) || (dtoTumorFrequency.getTumorFrequencyKey() <= 0)) {
            Utils.showErrorDialog(strChildKey + " is not a valid Tumor "
                    + "Frequency Key.\n\nPlease enter a valid "
                    + "Tumor Frequency Key.");
            txtTumorFrequencyKeyProgressionChild.grabFocus();
            return;
        }

        if (!((dtoTumorFrequency.getStrainKey() + "").equals(txtStrainKey.getText().trim()))) {
            Utils.showErrorDialog("Child has different strain.");
            txtTumorFrequencyKeyProgressionChild.setText("");
            txtTumorFrequencyKeyProgressionChild.grabFocus();
            return;
        }

        // get the table model
        TFProgressionDTOTableModel<TumorProgressionDTO> tblmdlTumorProgression =
                (TFProgressionDTOTableModel<TumorProgressionDTO>) fxtblProgression.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        TumorProgressionDTO dtoTumorProgression =
                TumorProgressionDAO.getInstance().createTumorProgressionDTO();

        dtoTumorProgression.setParentKey(dtoTF.getTumorFrequencyKey());
        dtoTumorProgression.setChildKey(lChildKey);
        dtoTumorProgression.setTumorProgressionTypeKey(lProgressionTypeKey);
        dtoTumorProgression.setCreateUser(dtoUser.getUserName());
        dtoTumorProgression.setCreateDate(dNow);
        dtoTumorProgression.setUpdateUser(dtoUser.getUserName());
        dtoTumorProgression.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoTumorProgression.getDataBean().put(EIConstants.PROGRESSION,
                strProgressionType);
        dtoTumorProgression.getDataBean().put(EIConstants.ORGAN,
                dtoTumorFrequency.getOrganAffected());

        // add it to the table
        tblmdlTumorProgression.addRow(dtoTumorProgression);

        Utils.scrollToVisible(fxtblProgression,
                fxtblProgression.getRowCount() - 1, 0);

    }

    /**
     * Add a treatment record the tumor frequency treatments table provided an
     * agent has been selected.
     */
    private void addAgent() {
        int nSelected = comboAgent.getSelectedIndex();
        LVDBeanListModel modelAgents = (LVDBeanListModel) comboAgent.getModel();
        LabelValueDataBean<String, Long, Long> beane =
                (LabelValueDataBean<String, Long, Long>) (modelAgents.getElementAt(nSelected));
        Long lAgentKey = beane.getValue();
        String strAgentName = beane.getLabel() + "";

        // validate the the agent is real
        if (nSelected <= 0) {
            Utils.showErrorDialog("Please select an Agent.");
            comboAgent.grabFocus();
            return;
        }

        Map<Long, LabelValueBean<String, Long>> mapAgentTypes =
                EIGlobals.getInstance().getAgentTypes();
        LabelValueBean<String, Long> beanAgentType =
                mapAgentTypes.get(beane.getData());

        // get the table model
        TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO> tblmdlTreatments =
                (TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO>) fxtblTreatements.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        TumorFrequencyTreatmentsDTO dtoTreatment =
                TumorFrequencyTreatmentsDAO.getInstance().
                createTumorFrequencyTreatmentsDTO();

        dtoTreatment.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
        dtoTreatment.setAgentKey(lAgentKey);
        dtoTreatment.setCreateUser(dtoUser.getUserName());
        dtoTreatment.setCreateDate(dNow);
        dtoTreatment.setUpdateUser(dtoUser.getUserName());
        dtoTreatment.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoTreatment.getDataBean().put(EIConstants.AGENT, strAgentName);
        dtoTreatment.getDataBean().put(EIConstants.AGENT_TYPE,
                beanAgentType.getLabel());

        // add it to the table
        tblmdlTreatments.addRow(dtoTreatment);

        Utils.scrollToVisible(fxtblTreatements,
                fxtblTreatements.getRowCount() - 1, 0);
    }

    /**
     * Add a pathology record to the tumor pathology assoc table provided a
     * valid JNumber has been filled in.
     */
    private void addPathology() {
        String strPathologyKey = txtPathologyKey.getText();

        // validate that the pathology key is valid
        if (!StringUtils.hasValue(strPathologyKey)) {
            Utils.showErrorDialog("Please enter a Pathology Report.");
            txtPathologyKey.grabFocus();
            return;
        }

        try {
            PathologyDAO daoPathology = PathologyDAO.getInstance();
            TumorPathologyAssocDAO daoTumorPathologyAssoc = TumorPathologyAssocDAO.getInstance();
            PathologyDTO dtoPathology = daoPathology.loadByPrimaryKey(new Long(strPathologyKey));
            TumorPathologyAssocDTO dtoTumorPathologyAssoc = daoTumorPathologyAssoc.createTumorPathologyAssocDTO();
            MTBPathologyUtilDAO daoPathologyUtil = MTBPathologyUtilDAO.getInstance();

            if ((dtoPathology == null) || (dtoPathology.getPathologyKey().longValue() <= 0)) {
                JOptionPane.showMessageDialog(null, strPathologyKey + " is not a valid pathology key.");
                txtPathologyKey.requestFocus();
                return;
            }

            dtoTumorPathologyAssoc.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
            dtoTumorPathologyAssoc.setPathologyKey(dtoPathology.getPathologyKey());

            MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
            Date dNow = new Date();

            dtoTumorPathologyAssoc.setCreateUser(dtoUser.getUserName());
            dtoTumorPathologyAssoc.setCreateDate(dNow);
            dtoTumorPathologyAssoc.setUpdateUser(dtoUser.getUserName());
            dtoTumorPathologyAssoc.setUpdateDate(dNow);

            DataBean sDTO = dtoTumorPathologyAssoc.getDataBean();
            try {
                SearchResults wrap = daoPathologyUtil.searchPathology((int) dtoPathology.getPathologyKey().longValue());
                Collection c = wrap.getList();
                List<MTBPathologySearchDTO> a = new ArrayList<MTBPathologySearchDTO>(c);
                MTBPathologySearchDTO psDTO = a.get(0);
                sDTO.put(EIConstants.MTB_PATHOLOGY_SEARCH_DTO, psDTO);
            } catch (Exception e) {
                Utils.log(e);
            }

            dtoTumorPathologyAssoc.setDataBean(sDTO);
            TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> tm = (TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>) fxtblPathology.getModel();
            tm.addRow(dtoTumorPathologyAssoc);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, strPathologyKey + " is not a valid pathology key.");
            txtPathologyKey.requestFocus();
            return;
        }


        Utils.scrollToVisible(fxtblPathology, fxtblPathology.getRowCount() - 1, 0);
    }

    /**
     * Mark a synonym from the tumor frequency synonyms table as to be deleted.
     * <p>
     * The actual synonym will not be removed until the tumor frequency has
     * been saved.
     */
    private void removeSynonym() {
        int nRow = fxtblSynonyms.getSelectedRow();

        if (nRow >= 0) {
            TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO> tblmdlSynonyms =
                    (TFSynonymsDTOTableModel<TumorFrequencySynonymsDTO>) fxtblSynonyms.getModel();
            tblmdlSynonyms.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark a note from the tumor frequency notes table as to be deleted.
     * <p>
     * The actual note will not be removed until the tumor frequency has been
     * saved.
     */
    private void removeNote() {
        int nRow = fxtblNotes.getSelectedRow();

        if (nRow >= 0) {
            TFNotesDTOTableModel<TumorFrequencyNotesDTO> tblmdlNotes =
                    (TFNotesDTOTableModel<TumorFrequencyNotesDTO>) fxtblNotes.getModel();
            tblmdlNotes.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark a tumor progression from the tumor progression table as to be
     * deleted.
     * <p>
     * The actual progression record will not be removed until the tumor
     * frequency has been saved.
     */
    private void removeProgression() {
        int nRow = fxtblProgression.getSelectedRow();

        if (nRow >= 0) {
            TFProgressionDTOTableModel<TumorProgressionDTO> tblmdlProgression =
                    (TFProgressionDTOTableModel<TumorProgressionDTO>) fxtblProgression.getModel();
            tblmdlProgression.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark a treatment from the tumor frequency treatments table as to be
     * deleted.
     * <p>
     * The actual tumor frequency treatment record will not be removed until
     * the tumor frequency has been saved.
     */
    private void removeAgent() {
        int nRow = fxtblTreatements.getSelectedRow();

        if (nRow >= 0) {
            TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO> tblmdlTreatments =
                    (TFTreatmentsDTOTableModel<TumorFrequencyTreatmentsDTO>) fxtblTreatements.getModel();
            tblmdlTreatments.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark a genetic record from the tumor genetics table as to be deleted.
     */
    private void removeTumorGenetic() {
        int nRow = fxtblGenetics.getSelectedRow();

        if (nRow >= 0) {
            TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO> tblmdlGenetics =
                    (TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO>) fxtblGenetics.getModel();
            tblmdlGenetics.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark a genetic record from the tumor genetics table as to be deleted.
     */
    private void removeTumorGeneticChanges() {
        int nRow = fxtblGeneticChanges.getSelectedRow();

        if (nRow >= 0) {
            MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO> tblmdlGeneticChanges =
                    (MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO>) fxtblGeneticChanges.getModel();
            tblmdlGeneticChanges.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Mark a pathology record from the tumor pathology assoc table as to be
     * deleted.
     * <p>
     * The actual pathology record will not be removed until the tumor
     * frequency has been saved.
     */
    private void removePathology() {
        int nRow = fxtblPathology.getSelectedRow();

        if (nRow >= 0) {
            TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO> tblmdlPathology =
                    (TumorPathologyAssocDTOTableModel<TumorPathologyAssocDTO>) fxtblPathology.getModel();
            tblmdlPathology.removeRow(nRow);
            updated = true;
        }
    }

    /**
     * Clear the data in the strain panel.
     */
    private void clearFormStrain() {
        String strBlank = "";
        txtStrainKey.setText(strBlank);
        txtStrainName.setText(strBlank);
        txtareaStrainDescription.setText(strBlank);
        listStrainSynonyms.removeAll();
        listStrainSynonyms.clearSelection();
        txtStrainTypes.setText(strBlank);
        DefaultListModel modelSynonyms =
                (DefaultListModel) listStrainSynonyms.getModel();
        modelSynonyms.clear();
    }

    /**
     * Populate the Strain panel with the strain data from the database.
     */
    private void updateFormStrain(MTBStrainDetailDTO dtoStrainDetail) {
        clearFormStrain();

        txtStrainKey.setText(dtoStrainDetail.getStrainKey() + "");
        txtStrainName.setText(dtoStrainDetail.getName());
        txtareaStrainDescription.setText(dtoStrainDetail.getDescription());

        // strain synonyms
        Collection colSynonyms = dtoStrainDetail.getSynonyms();
        DefaultListModel modelSynonyms =
                (DefaultListModel) listStrainSynonyms.getModel();

        if (colSynonyms != null) {
            for (Iterator it = colSynonyms.iterator(); it.hasNext();) {
                StrainSynonymsDTO dtoSynonym = (StrainSynonymsDTO) it.next();
                modelSynonyms.addElement(dtoSynonym.getName());
            }
        }
        listStrainSynonyms.setModel(modelSynonyms);

        // strain types
        Collection colTypes = dtoStrainDetail.getTypes();
        StringBuffer sbTypes = new StringBuffer("");
        if (colTypes != null) {
            for (Iterator it = colTypes.iterator(); it.hasNext();) {
                StrainTypeDTO dtoType = (StrainTypeDTO) it.next();
                sbTypes.append(dtoType.getType());
                if (it.hasNext()) {
                    sbTypes.append(", ");
                }
            }
        }
        txtStrainTypes.setText(sbTypes.toString());
    }

    /**
     * Lookup the strain data from the database.
     */
    private void lookupStrain() {
        MTBStrainUtilDAO mtbStrainDAO = MTBStrainUtilDAO.getInstance();
        long lStrainKey = -1;

        try {
            lStrainKey = Long.parseLong(txtStrainKey.getText().trim());
        } catch (Exception e) {
            Utils.log(e);
            clearFormStrain();
            return;
        }

        MTBStrainDetailDTO dtoStrainDetail = mtbStrainDAO.getStrain(lStrainKey);
        updateFormStrain(dtoStrainDetail);
        txtStrainKey.setBackground(new Color(200, 200, 255));
    }

    /**
     * Lookup the TumorFrequency record and populate the lookup fields for the
     * parent part.
     */
    private void lookupProgressionParent() {
        String strParentKey =
                txtTumorFrequencyKeyProgressionParent.getText().trim();
        long lParentKey = -1;

        if (StringUtils.hasValue(strParentKey)) {
            try {
                lParentKey = Long.parseLong(strParentKey);
            } catch (Exception e) {
                Utils.showErrorDialog(strParentKey
                        + " is not a valid Tumor Frequency "
                        + "Key.\n\nPlease enter a valid Tumor "
                        + "Frequency Key.");
                txtTumorFrequencyKeyProgressionParent.requestFocus();
                return;
            }
        }

        MTBTumorFrequencyDetailDTO dtoTumorFrequencyDetail =
                MTBTumorUtilDAO.getInstance().getTumorFrequencyDetail(lParentKey);

        if (dtoTumorFrequencyDetail.getTumorFrequencyKey() <= 0) {
            Utils.showErrorDialog(strParentKey + " is not a valid Tumor "
                    + "Frequency Key.\n\nPlease enter a valid "
                    + "Tumor Frequency Key.");
            txtTumorFrequencyKeyProgressionParent.requestFocus();
            return;
        }

    }

    /**
     * Clear the filter for the Agent Filter panel and reset the combo box for
     * the agents back to the original values.
     */
    private void clearFilter() {
        comboAgentNameFilter.setSelectedIndex(0);
        txtAgentNameFilter.setText("");
        comboAgentTypeFilter.setSelectedIndex(0);
        initAgents();
    }

    /**
     * Apply the selected values in the Agent Filter panel to reduce the
     * number of agents in the combo box.
     */
    private void applyFilter() {
        String strAgentNameFilter = txtAgentNameFilter.getText();
        String strAgentNameCompareFilter =
                (String) comboAgentNameFilter.getSelectedItem();

        long lAgentTypeKey = -1;

        try {
            Object objAgentType = comboAgentTypeFilter.getSelectedItem();
            LabelValueBean<String, Long> bean = (LabelValueBean<String, Long>) objAgentType;
            lAgentTypeKey = bean.getValue();
        } catch (Exception e) {
            Utils.log(e);
        }

        boolean bContains = true;

        if (strAgentNameCompareFilter.charAt(0) == 'E') {
            bContains = false;
        }

        Map<Long, LabelValueDataBean<String, Long, Long>> mapAgents = EIGlobals.getInstance().getAgents();
        List<LabelValueDataBean<String, Long, Long>> arrAgents = new ArrayList<LabelValueDataBean<String, Long, Long>>(mapAgents.values());
        List<LabelValueDataBean<String, Long, Long>> arrAgentsFilter = new ArrayList<LabelValueDataBean<String, Long, Long>>();

        for (int i = 0; i < arrAgents.size(); i++) {
            LabelValueDataBean<String, Long, Long> bean = (LabelValueDataBean<String, Long, Long>) arrAgents.get(i);
            boolean bMatchName = false;
            boolean bMatchType = false;

            if (StringUtils.hasValue(strAgentNameFilter)) {

                if (bContains) {
                    // check for 'contains'
                    if (bean.getLabel().indexOf(strAgentNameFilter) >= 0) {
                        // matches condition
                        bMatchName = true;
                    } else {
                        // no match
                        bMatchName = false;
                    }
                } else {
                    // check for 'equals'
                    if (StringUtils.equals(bean.getLabel() + "",
                            strAgentNameFilter)) {
                        // matches condition
                        bMatchName = true;
                    } else {
                        // no match
                        bMatchName = false;
                    }
                }
            } else {
                bMatchName = true;
            }

            if (lAgentTypeKey > 0) {
                long lKey = bean.getData();

                if (lKey == lAgentTypeKey) {
                    bMatchType = true;
                } else {
                    bMatchType = false;
                }
            } else {
                bMatchType = true;
            }


            if ((bMatchName) && (bMatchType)) {
                arrAgentsFilter.add(bean);
            }
        }

        comboAgent.setModel(new LVDBeanListModel(arrAgentsFilter));
        comboAgent.setRenderer(new LVDBeanListCellRenderer());

        if (comboAgent.getItemCount() > 0) {
            comboAgent.setSelectedIndex(0);
        }

        // remove the key listeners so there are no duplicates
        KeyListener keylisteners[] = comboAgent.getKeyListeners();
        for (int i = 0; i < keylisteners.length; i++) {
            comboAgent.removeKeyListener(keylisteners[i]);
        }

        // install the new key listener
        comboAgent.addKeyListener(new LVDBeanComboListener());
    }

    /**
     * If there are values for colony size and # mice affected it's
     * (#mice/colony size) x 100. If there's a number entered for incidence
     * with no colony size and # mice, then it equals the # in incidence.
     * If incidence is a text value, then we were using those assigned values
     *
     * very high = 81
     * high = 51
     * moderate = 31
     * low = 19
     * very low = 9
     * sporadic = 0.9
     * observed = 0.1
     */
    private void autoCalculateFrequency() {
        try {
            String strColonySize = txtColonySize.getText();
            String strNumMiceAffected = txtNumMiceAffected.getText();
            String strIncidence = txtIncidence.getText();

            if (StringUtils.hasValue(strColonySize)
                    && StringUtils.hasValue(strNumMiceAffected)
                    && !StringUtils.hasValue(strIncidence)) {
                int nColony = Integer.parseInt(strColonySize);
                int nMiceAffected = Integer.parseInt(strNumMiceAffected);
                double dPercentage = ((double) nMiceAffected / nColony) * 100;
                NumberFormat numberFormat = new DecimalFormat("0.00");
                String strText = numberFormat.format(dPercentage);
                txtSortEquivalent.setText(strText);
            } else {
                if (StringUtils.hasValue(strIncidence)) {
                    String strTemp = strIncidence.toLowerCase();
                    double dFreqNum = 0.0;
                    if (StringUtils.equals(strTemp, "very high")) {
                        dFreqNum = 81.00;
                    } else if (StringUtils.equals(strTemp, "high")) {
                        dFreqNum = 51.00;
                    } else if (StringUtils.equals(strTemp, "moderate")) {
                        dFreqNum = 31.00;
                    } else if (StringUtils.equals(strTemp, "low")) {
                        dFreqNum = 19.00;
                    } else if (StringUtils.equals(strTemp, "very low")) {
                        dFreqNum = 9.00;
                    } else if (StringUtils.equals(strTemp, "sporadic")) {
                        dFreqNum = 0.90;
                    } else if (StringUtils.equals(strTemp, "observed")) {
                        dFreqNum = 0.10;
                    } else if (StringUtils.equals(strTemp, "not applicable")) {
                        dFreqNum = -1;


                    } else {
                        if (strTemp.indexOf(" to ") != -1) {
                            String[] tnf = strTemp.split(" to ");
                            double frm = Double.parseDouble(tnf[0]);
                            double too = Double.parseDouble(tnf[1]);
                            if (too > frm) {
                                strTemp = tnf[1];
                            }
                        }
                        // punt on any other chars
                        if (strTemp.startsWith(">=")) {
                            strTemp = strTemp.replace(">=", "");
                        } else if (strTemp.startsWith("<=")) {
                            strTemp = strTemp.replace("<=", "");
                        } else if (strTemp.startsWith(">")) {
                            strTemp = strTemp.replace(">", "");
                        } else if (strTemp.startsWith("<")) {
                            strTemp = strTemp.replace("<", "");
                        } else if (strTemp.startsWith("~")) {
                            strTemp = strTemp.replace("~", "");
                        }
                        if (strTemp.endsWith("%")) {
                            strTemp = strTemp.replace("%", "");
                        }


                        dFreqNum = Double.parseDouble(strTemp);
                    }

                    NumberFormat numberFormatter = new DecimalFormat("0.00");
                    String strText = numberFormatter.format(dFreqNum);
                    if (dFreqNum <= 100) {
                        txtSortEquivalent.setText(strText);
                    } else {
                        txtSortEquivalent.setText("ERR");
                    }
                } else {
                    txtSortEquivalent.setText("ERR");
                }
            }
        } catch (Exception e) {
            txtSortEquivalent.setText("ERR");

        }
    }

    /**
     * Initialize the MXTable for tumor frequency genetics.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initTumorFrequencyGeneticChanges() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(6);
        arrHeaders.add("Change");
        arrHeaders.add("Assay Type");
        arrHeaders.add("Name");
        arrHeaders.add("Note");
        arrHeaders.add("Chromosomes");
        arrHeaders.add("Images");
        List arrGeneticChanges = new ArrayList();
        MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO> tblmdlGeneticChanges =
                new MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO>(arrGeneticChanges, arrHeaders);
        fxtblGeneticChanges = new MXTable(tblmdlGeneticChanges);
        fxtblGeneticChanges.setModel(tblmdlGeneticChanges);

        // set the table options
        fxtblGeneticChanges.setDefaultRenderer(Object.class, new DTORenderer());
        //fxtblGeneticChanges.getColumnModel().getColumn(0).setCellEditor(
        //                                            new JNumberCellEditor());
        fxtblGeneticChanges.setColumnSizes(new int[]{0, 0, 0, 0, 0, 0});
        fxtblGeneticChanges.setAlternateRowHighlight(true);
        fxtblGeneticChanges.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblGeneticChanges.setAlternateRowHighlightCount(2);
        fxtblGeneticChanges.setStartHighlightRow(1);
        fxtblGeneticChanges.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblGeneticChanges.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblGeneticChanges.enableToolTip(0, false);


        // create the treatment delete button
        JButton btnDelGeneticChanges =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));
        btnDelGeneticChanges.setIconTextGap(0);
        btnDelGeneticChanges.setMargin(new Insets(0, 0, 0, 0));
        btnDelGeneticChanges.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                removeTumorGeneticChanges();
            }
        });




        // update the JScrollPane
        jspGeneticChanges.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspGeneticChanges.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                btnDelGeneticChanges);
        jspGeneticChanges.setViewportView(fxtblGeneticChanges);

        // revalidate the panel
        pnlGeneticChanges.revalidate();
    }

    /**
     * Initialize the MXTable for tumor frequency genetics.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initTumorFrequencyGenetics() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(3);
        arrHeaders.add("Key");
        arrHeaders.add("Allele1 Key");
        arrHeaders.add("Allele1 Symbol");
        arrHeaders.add("Allele1 Type");
        arrHeaders.add("Allele2 Key");
        arrHeaders.add("Allele2 Symbol");
        arrHeaders.add("Allele2 Type");
        arrHeaders.add("Marker Symbol");
        List arrGenetics = new ArrayList();
        TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO> tblmdlGenetics =
                new TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO>(arrGenetics, arrHeaders);
        fxtblGenetics = new MXTable(tblmdlGenetics);
        fxtblGenetics.setModel(tblmdlGenetics);

        // set the table options
        fxtblGenetics.setDefaultRenderer(Object.class, new DTORenderer());
        //fxtblGenetics.getColumnModel().getColumn(0).setCellEditor(
        //                                            new JNumberCellEditor());
        fxtblGenetics.setColumnSizes(new int[]{50, 0, 0, 0, 0, 0, 0, 0});
        fxtblGenetics.setAlternateRowHighlight(true);
        fxtblGenetics.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblGenetics.setAlternateRowHighlightCount(2);
        fxtblGenetics.setStartHighlightRow(1);
        fxtblGenetics.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblGenetics.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblGenetics.enableToolTip(0, false);
        fxtblGenetics.enableToolTip(1, true);

        // create the treatment delete button
        JButton btnDelGenetics =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));
        btnDelGenetics.setIconTextGap(0);
        btnDelGenetics.setMargin(new Insets(0, 0, 0, 0));
        btnDelGenetics.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                removeTumorGenetic();
            }
        });

        // update the JScrollPane
        jspGenetics.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspGenetics.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
                btnDelGenetics);
        jspGenetics.setViewportView(fxtblGenetics);

        // revalidate the panel
        pnlGenetics.revalidate();
    }

    private void addGeneticChange() {
        // get tumor change, which will tell us the type too
        int nSelected = comboCytogeneticChange.getSelectedIndex();
        if (nSelected <= 0) {
            String message =
                    "Please select a genetic change type.\n\n";

            // Modal dialog 

            JOptionPane.showConfirmDialog(this, message, "Warning",
                    JOptionPane.DEFAULT_OPTION);

            return;

        }
        LVBeanListModel<String, Long> modelType =
                (LVBeanListModel<String, Long>) comboCytogeneticChange.getModel();
        LabelValueBean<String, Long> bean = modelType.getElementAt(nSelected);
        Long lChange = new Long(bean.getValue());
        String strChange = bean.getLabel();

        nSelected = comboAssayType.getSelectedIndex();
        modelType = (LVBeanListModel<String, Long>) comboAssayType.getModel();
        bean = modelType.getElementAt(nSelected);
        Long assayType = new Long(bean.getValue());
        String assayName = bean.getLabel();
        if (assayType.longValue() <= 0) {
            //--Selected-- was selected
            assayType = new Long(0L);
            assayName = "";
        }


        String strChangeType = "Cytogenetic";

        // get the table model
        MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO> tblmdlGeneticChanges =
                (MTBTumorGeneticChangesDTOTableModel<MTBTumorGeneticChangesDTO>) fxtblGeneticChanges.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // get the chromosome data

        Object[] chroms = listCytogeneticChromosomes.getSelectedValues();

        ChromosomeDAO cDAO = ChromosomeDAO.getInstance();
        Long mouse = new Long(1L);
        Collection<ChromosomeDTO> cDTOs = new ArrayList<ChromosomeDTO>();
        for (int i = 0; i < chroms.length; i++) {
            LabelValueBean<String, Long> lvb = (LabelValueBean<String, Long>) chroms[i];
            ChromosomeDTO dto = cDAO.createChromosomeDTO();
            dto.setChromosome(lvb.getLabel());
            dto.setChromosomeKey(lvb.getValue());
            cDTOs.add(dto);

        }


        // create the dto
        MTBTumorGeneticChangesDTO dtoTumorGeneticChanges =
                MTBTumorGeneticChangesDAO.getInstance().createTumorGeneticChangesDTO();

        dtoTumorGeneticChanges.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());

        dtoTumorGeneticChanges.setAlleleTypeKey(lChange);
        dtoTumorGeneticChanges.setAssayTypeKey(assayType);
        dtoTumorGeneticChanges.setName(txtCytogeneticName.getText());
        dtoTumorGeneticChanges.setNotes(txtareaCytogeneticNote.getText());
        dtoTumorGeneticChanges.setChromosomes(cDTOs);
        dtoTumorGeneticChanges.setCreateUser(dtoUser.getUserName());
        dtoTumorGeneticChanges.setCreateDate(dNow);
        dtoTumorGeneticChanges.setUpdateUser(dtoUser.getUserName());
        dtoTumorGeneticChanges.setUpdateDate(dNow);

        dtoTumorGeneticChanges.getDataBean().put(EIConstants.CHANGE, strChange);
        // add the change type
        dtoTumorGeneticChanges.getDataBean().put(EIConstants.CHANGE_TYPE, strChangeType);

        dtoTumorGeneticChanges.getDataBean().put(EIConstants.ASSAY_NAME, assayName);

        // add it to the table
        tblmdlGeneticChanges.addRow(dtoTumorGeneticChanges);

        Utils.scrollToVisible(fxtblGeneticChanges,
                fxtblGeneticChanges.getRowCount() - 1, 0);

    }

    private void showImages() {
        try {

            MTBTumorGeneticChangesDTOTableModel tgcModel =
                    (MTBTumorGeneticChangesDTOTableModel) fxtblGeneticChanges.getModel();
            if (fxtblGeneticChanges.getSelectedRow() != -1) {
                MTBTumorGeneticChangesDTO tgcDTO = (MTBTumorGeneticChangesDTO) tgcModel.getDTO(fxtblGeneticChanges.getSelectedRow());

                if ((tgcDTO.getTumorGeneticChangesKey() == null)
                        || (tgcDTO.getTumorGeneticChangesKey() == 0)) {
                    String message =
                            "Please save the cytogenetic record before adding images.\n\n";

                    // Modal dialog 

                    JOptionPane.showConfirmDialog(this, message, "Warning",
                            JOptionPane.DEFAULT_OPTION);

                    return;
                }

                CustomInternalFrame cif =
                        EIGlobals.getInstance().getMainFrame().launchAssayImageWindow();
                AssayImagePanel aip = (AssayImagePanel) cif.getCustomPanel();
                aip.setDTO(tgcDTO);

                //allow the aip to update this panel's table model to display the new
                // number of associated images
                aip.setTableModel(tgcModel, fxtblGeneticChanges.getSelectedRow());

                // need to update images column for selected row after aip panel closes

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private AlleleDTO lookupAllele(Long key) {
        if (key == null) {
            return null;
        }

        AlleleDAO daoA = AlleleDAO.getInstance();
        AlleleDTO dtoA = null;

        try {
            dtoA = daoA.loadByPrimaryKey(new Long(key));
        } catch (Exception e) {
            Utils.showErrorDialog(e.getMessage());
            return null;
        }

        return dtoA;
    }

    private AllelePairDTO lookupAllelePair(long key) {
        AllelePairDAO daoA = AllelePairDAO.getInstance();
        AllelePairDTO dtoA = null;

        try {
            dtoA = daoA.loadByPrimaryKey(new Long(key));
        } catch (Exception e) {
            Utils.showErrorDialog(e.getMessage());
            return null;
        }

        return dtoA;
    }

    private void addAllelePair() {
        // get the allele pair key
        System.out.println("Adding allele pair");
        String strAllelePairText = txtAllelePairKey.getText();
        long lAllelePairKey = -1l;

        // convert it to a number
        if (StringUtils.hasValue(strAllelePairText)) {
            try {
                lAllelePairKey = Long.parseLong(strAllelePairText);
            } catch (NumberFormatException nfe) {
                Utils.showErrorDialog("Please enetr a numeric Allele Pair Key.");
                txtAllelePairKey.grabFocus();
                return;
            }
        }

        // lookup the allele pair and make sure it is valid
        AllelePairDTO dtoAP = lookupAllelePair(lAllelePairKey);

        if ((dtoAP == null) || (dtoAP.getAllelePairKey() <= 0)) {
            Utils.showErrorDialog("Please enetr a valid Allele Pair Key.");
            txtAllelePairKey.grabFocus();
            return;
        }

        System.out.println("Allele Pair Key: " + dtoAP.getAllelePairKey());

        // get the table model
        TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO> tblmdlGenetics =
                (TumorGeneticsAllelePairDTOTableModel<TumorGeneticsDTO>) fxtblGenetics.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dto
        TumorGeneticsDTO dtoTumorGenetics =
                TumorGeneticsDAO.getInstance().createTumorGeneticsDTO();

        dtoTumorGenetics.setTumorFrequencyKey(dtoTF.getTumorFrequencyKey());
        dtoTumorGenetics.setAllelePairKey(dtoAP.getAllelePairKey());
        dtoTumorGenetics.setCreateUser(dtoUser.getUserName());
        dtoTumorGenetics.setCreateDate(dNow);
        dtoTumorGenetics.setUpdateUser(dtoUser.getUserName());
        dtoTumorGenetics.setUpdateDate(dNow);

        // get the allele information
        Map<Long, LabelValueBean<String, Long>> atypes = EIGlobals.getInstance().getAlleleTypes();

        AlleleDTO dto1 = lookupAllele(dtoAP.getAllele1Key());
        AlleleDTO dto2 = lookupAllele(dtoAP.getAllele2Key());

        // get the marker information for allele 1

        MarkerDTO dtoM = null;

        try {
            List<MarkerDTO> listMarkers = AlleleDAO.getInstance().loadMarkerViaAlleleMarkerAssoc(dto1);


            if (listMarkers != null) {
                dtoM = listMarkers.get(0);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            // ignore
        }

        // set the custom data for the data model to display the correct data
        dtoTumorGenetics.getDataBean().put(EIConstants.ALLELE1_KEY, dto1.getAlleleKey() + "");
        dtoTumorGenetics.getDataBean().put(EIConstants.ALLELE1_SYMBOL, dto1.getSymbol());
        dtoTumorGenetics.getDataBean().put(EIConstants.ALLELE1_TYPE, atypes.get(dto1.getAlleleTypeKey()).getLabel());

        if (dto2 != null) {
            dtoTumorGenetics.getDataBean().put(EIConstants.ALLELE2_KEY, dto2.getAlleleKey() + "");
            dtoTumorGenetics.getDataBean().put(EIConstants.ALLELE2_SYMBOL, dto2.getSymbol());
            dtoTumorGenetics.getDataBean().put(EIConstants.ALLELE2_TYPE, atypes.get(dto2.getAlleleTypeKey()).getLabel());
        }

        if (dtoM != null) {
            dtoTumorGenetics.getDataBean().put(EIConstants.MARKER_SYMBOL, dtoM.getSymbol());
        }


        // add it to the table
        tblmdlGenetics.addRow(dtoTumorGenetics);

        Utils.scrollToVisible(fxtblGenetics,
                fxtblGenetics.getRowCount() - 1, 0);

    }

    private void lookupAllelePair() {
        String strAllelePair = txtAllelePairKey.getText();

        long allelePairKey = -1;

        try {
            allelePairKey = Long.parseLong(strAllelePair);
        } catch (Exception e) {
            Utils.showErrorDialog("Please enter a numeric Allele Pair Key");
            txtAllele1Key.setText("");
            txtAllele1Symbol.setText("");
            txtAllele1Type.setText("");
            textareaAllele1Note.setText("");
            txtAllele2Key.setText("");
            txtAllele2Symbol.setText("");
            txtAllele2Type.setText("");
            textareaAllele2Note.setText("");
            txtMarkerSymbol.setText("");
            txtAllelePairKey.requestFocus();
            return;
        }

        AllelePairDTO dto = lookupAllelePair(allelePairKey);

        if (dto == null) {
            Utils.showErrorDialog("Allele Pair Key " + allelePairKey + " does not exist!");
            txtAllelePairKey.requestFocus();
            return;
        }

        AlleleDTO dto1 = lookupAllele(dto.getAllele1Key());

        if (dto1 != null) {
            txtAllele1Key.setText(dto1.getAlleleKey() + "");
            txtAllele1Symbol.setText(dto1.getSymbol());
            txtAllele1Type.setText(EIGlobals.getInstance().getAlleleTypes().get(dto1.getAlleleTypeKey()).getLabel());
            textareaAllele1Note.setText(dto1.getNote());
        }

        AlleleDTO dto2 = lookupAllele(dto.getAllele2Key());

        if (dto2 != null) {
            txtAllele2Key.setText(dto2.getAlleleKey() + "");
            txtAllele2Symbol.setText(dto2.getSymbol());
            txtAllele2Type.setText(EIGlobals.getInstance().getAlleleTypes().get(dto2.getAlleleTypeKey()).getLabel());
            textareaAllele2Note.setText(dto2.getNote());
        } else {
            txtAllele2Key.setText("");
            txtAllele2Symbol.setText("");
            txtAllele2Type.setText("");
            textareaAllele2Note.setText("");

        }

        MarkerDTO dtoM = null;

        try {
            List<MarkerDTO> listMarkers = AlleleDAO.getInstance().loadMarkerViaAlleleMarkerAssoc(dto1);


            if (listMarkers != null) {
                dtoM = listMarkers.get(0);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            // ignore
        }

        if (dtoM != null) {
            txtMarkerSymbol.setText(dtoM.getSymbol());
        }

    }

    /**
     * Check to make sure 2 progression children do not have the same parent.
     */
    private void checkTFPDuplication() throws Exception {
        if ((dtoTF != null) && (dtoTF.getTumorFrequencyKey() != null)
                && (dtoTF.getTumorFrequencyKey() > -1)) {

            TumorProgressionDAO daoTP = TumorProgressionDAO.getInstance();
            TumorProgressionDTO dtoTP = daoTP.createTumorProgressionDTO();
            dtoTP.setChildKey(dtoTF.getTumorFrequencyKey());
            if (daoTP.countUsingTemplate(dtoTP) > 1) {
                txtTumorFrequencyKeyProgressionParent.requestFocus();
                throw new Exception("A child frequency record can only have "
                        + "1 parent frequency record.");
            }
        }
    }

    private void checkParent() {
        if ((dtoTF != null) && (dtoTF.getTumorFrequencyKey() != null)
                && (dtoTF.getTumorFrequencyKey() > -1)) {
            String currentKey = dtoTF.getTumorFrequencyKey().toString();
            String child = this.txtTumorFrequencyKeyProgressionParent.getText();
            if (currentKey.equals(child)) {
                Utils.showErrorDialog(currentKey + " can not be the parent progression key.");
                this.txtTumorFrequencyKeyProgressionParent.setText("");
            }
        }
    }

    private void checkChild() {
        if ((dtoTF != null) && (dtoTF.getTumorFrequencyKey() != null)
                && (dtoTF.getTumorFrequencyKey() > -1)) {
            String currentKey = dtoTF.getTumorFrequencyKey().toString();
            String child = this.txtTumorFrequencyKeyProgressionChild.getText();
            if (currentKey.equals(child)) {
                Utils.showErrorDialog(currentKey + " can not be the child progression key.");
                this.txtTumorFrequencyKeyProgressionChild.setText("");
            }
        }

    }

    protected void setSearchPanel(TumorFrequencySearchPanel p) {
        this.tfsp = p;
        // need to display previous / next buttons
        this.jButtonNext.setEnabled(true);

        this.jButtonPrevious.setEnabled(true);

        this.jLabelPN.setEnabled(true);
    }

    private void previous() {
        try {
            int nRow = this.tfsp.fxtblSearchResults.getSelectedRow();
            nRow--;
            this.tfsp.fxtblSearchResults.getRowCount();
            if (nRow >= 0) {
                this.tfsp.fxtblSearchResults.setRowSelectionInterval((nRow), (nRow));
                this.tfsp.fxtblSearchResults.scrollRectToVisible(this.tfsp.fxtblSearchResults.getCellRect(nRow, 1, false));
                this.jLabelPN.setText("");
            } else {
                this.jLabelPN.setText("At first record");
            }
            MXDefaultTableModel tm =
                    (MXDefaultTableModel) this.tfsp.fxtblSearchResults.getModel();
            int key = ((Integer) tm.getValueAt(nRow, 0)).intValue();

            CustomInternalFrame cif = null;

            cif = EIGlobals.getInstance().getMainFrame().
                    launchTumorFrequencyEditWindow(
                    ((Integer) tm.getValueAt(nRow, 0)).intValue());
            TumorFrequencyPanel tfp = (TumorFrequencyPanel) cif.getCustomPanel();
            tfp.setSearchPanel(this.tfsp);

            this.customInternalFrame.dispose();



            this.customInternalFrame.setTitle("Tumor Frequency Edit Form: " + key);

        } catch (Exception e) {
        }// null pointer if search panel is closed -- do nothing
    }

    private void next() {
        try {
            int nRow = this.tfsp.fxtblSearchResults.getSelectedRow();
            nRow++;
            this.tfsp.fxtblSearchResults.getRowCount();
            if (nRow < this.tfsp.fxtblSearchResults.getRowCount()) {
                this.tfsp.fxtblSearchResults.setRowSelectionInterval((nRow), (nRow));
                this.tfsp.fxtblSearchResults.scrollRectToVisible(this.tfsp.fxtblSearchResults.getCellRect(nRow, 1, false));
                this.jLabelPN.setText("");
            } else {
                this.jLabelPN.setText("At last record");
            }

            MXDefaultTableModel tm =
                    (MXDefaultTableModel) this.tfsp.fxtblSearchResults.getModel();
            int key = ((Integer) tm.getValueAt(nRow, 0)).intValue();

            CustomInternalFrame cif = null;

            cif = EIGlobals.getInstance().getMainFrame().
                    launchTumorFrequencyEditWindow(
                    ((Integer) tm.getValueAt(nRow, 0)).intValue());
            TumorFrequencyPanel tfp = (TumorFrequencyPanel) cif.getCustomPanel();
            tfp.setSearchPanel(this.tfsp);

            this.customInternalFrame.dispose();



        } catch (Exception e) {
          
        }// null pointer if search panel is closed -- do nothing

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

        tabbedpaneData = new javax.swing.JTabbedPane();
        pnllTumorFrequency = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblStrainKey = new javax.swing.JLabel();
        lblStrainSex = new javax.swing.JLabel();
        comboStrainSex = new javax.swing.JComboBox();
        txtStrainKey = new javax.swing.JTextField();
        btnStrainLookup = new javax.swing.JButton();
        headerPanelStrain = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblStrainName = new javax.swing.JLabel();
        lblStrainTypes = new javax.swing.JLabel();
        txtStrainTypes = new javax.swing.JTextField();
        txtStrainName = new javax.swing.JTextField();
        lblStrainDescription = new javax.swing.JLabel();
        jspStrainDescription = new javax.swing.JScrollPane();
        txtareaStrainDescription = new javax.swing.JTextArea();
        lblStrainSynonyms = new javax.swing.JLabel();
        jspStrainSynonyms = new javax.swing.JScrollPane();
        listStrainSynonyms = new javax.swing.JList();
        jPanel4 = new javax.swing.JPanel();
        pnlTumorFrequencyInformation = new javax.swing.JPanel();
        lblTumorFrequencyKey = new javax.swing.JLabel();
        txtTumorFrequencyKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        txtJNumber = new javax.swing.JTextField();
        lblJNumber = new javax.swing.JLabel();
        lblOrganTissueOrigin = new javax.swing.JLabel();
        comboOrganTissueOrigin = new javax.swing.JComboBox();
        lblTumorClassification = new javax.swing.JLabel();
        comboTumorClassification = new javax.swing.JComboBox();
        lblOrganTissueAffected = new javax.swing.JLabel();
        comboOrganTissueAffected = new javax.swing.JComboBox();
        lblAgeOnset = new javax.swing.JLabel();
        txtAgeOnset = new javax.swing.JTextField();
        txtAgeDetection = new javax.swing.JTextField();
        lblAgeDetection = new javax.swing.JLabel();
        lblColonySize = new javax.swing.JLabel();
        lblNumMiceAffected = new javax.swing.JLabel();
        lblIncidence = new javax.swing.JLabel();
        lblBreedingStatus = new javax.swing.JLabel();
        lblSortEquivalent = new javax.swing.JLabel();
        lblInfectionStatus = new javax.swing.JLabel();
        txtNumMiceAffected = new javax.swing.JTextField();
        txtSortEquivalent = new javax.swing.JTextField();
        txtInfectionStatus = new javax.swing.JTextField();
        txtColonySize = new javax.swing.JTextField();
        txtIncidence = new javax.swing.JTextField();
        lblMainNote = new javax.swing.JLabel();
        jspMainNote = new javax.swing.JScrollPane();
        txtareaMainNote = new javax.swing.JTextArea();
        comboBreedingStatus = new javax.swing.JComboBox();
        headerPanelTumorFrequencyInformation = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblAccession = new javax.swing.JLabel();
        txtAccession = new javax.swing.JTextField();
        checkboxAutoAssignAccession = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        pnlTumorFrequencyNotes = new javax.swing.JPanel();
        lblJNumberNote = new javax.swing.JLabel();
        txtJNumberNote = new javax.swing.JTextField();
        txtNote = new javax.swing.JTextField();
        lblNote = new javax.swing.JLabel();
        jspNotes = new javax.swing.JScrollPane();
        tblNotes = new javax.swing.JTable();
        btnNoteAdd = new javax.swing.JButton();
        headerPanelNotes = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        pnlTumorFrequencySynonyms = new javax.swing.JPanel();
        lblJNumberSynonym = new javax.swing.JLabel();
        txtJNumberSynonym = new javax.swing.JTextField();
        txtSynonym = new javax.swing.JTextField();
        lblSynonym = new javax.swing.JLabel();
        btnSynonymAdd = new javax.swing.JButton();
        jspSynonyms = new javax.swing.JScrollPane();
        tblSynonyms = new javax.swing.JTable();
        headerPanelSynonyms = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlProgressionChild = new javax.swing.JPanel();
        lbTumorFrequencyKeyProgressionChild = new javax.swing.JLabel();
        txtTumorFrequencyKeyProgressionChild = new javax.swing.JTextField();
        lblProgressionTypeProgressionChild = new javax.swing.JLabel();
        comboProgressionTypeProgressionChild = new javax.swing.JComboBox();
        separatorTopProgressionChild = new javax.swing.JSeparator();
        jspProgressionChildren = new javax.swing.JScrollPane();
        tblProgressionChildren = new javax.swing.JTable();
        btnProgressionAdd = new javax.swing.JButton();
        headerPanel3 = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblTumorFrequencyKeyProgressionParent = new javax.swing.JLabel();
        txtTumorFrequencyKeyProgressionParent = new javax.swing.JTextField();
        pnlTreatmentsInner = new javax.swing.JPanel();
        lblAgent = new javax.swing.JLabel();
        comboAgent = new javax.swing.JComboBox();
        btnAgentAdd = new javax.swing.JButton();
        pnlFilterAgents = new javax.swing.JPanel();
        lblAgentNameFilter = new javax.swing.JLabel();
        lblAgentTypeFilter = new javax.swing.JLabel();
        comboAgentNameFilter = new javax.swing.JComboBox();
        txtAgentNameFilter = new javax.swing.JTextField();
        comboAgentTypeFilter = new javax.swing.JComboBox();
        btnFilterApply = new javax.swing.JButton();
        btnFilterClear = new javax.swing.JButton();
        jspTreatmentTypes = new javax.swing.JScrollPane();
        tblTreatmentTypes = new javax.swing.JTable();
        headerPanelTreatments = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        pnlPathologyDetail = new javax.swing.JPanel();
        lblPathologyKey = new javax.swing.JLabel();
        txtPathologyKey = new javax.swing.JTextField();
        btnPathologyAdd = new javax.swing.JButton();
        jspPathology = new javax.swing.JScrollPane();
        tblPathology = new javax.swing.JTable();
        headerPanel1 = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        pnlAllGenetics = new javax.swing.JPanel();
        pnlGenetics = new javax.swing.JPanel();
        headerPanelGenetics = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblAllelePairKey = new javax.swing.JLabel();
        txtAllelePairKey = new javax.swing.JTextField();
        btnAddAllelePair = new javax.swing.JButton();
        jspGenetics = new javax.swing.JScrollPane();
        tblGeneticChanges = new javax.swing.JTable();
        btnAllelePairLookup = new javax.swing.JButton();
        separatorGenetics = new javax.swing.JSeparator();
        lblAllele1Key = new javax.swing.JLabel();
        lblAllele1Symbol = new javax.swing.JLabel();
        lblAllele1Type = new javax.swing.JLabel();
        lblAllele2Key = new javax.swing.JLabel();
        lblAllele2Symbol = new javax.swing.JLabel();
        lblAllele2Type = new javax.swing.JLabel();
        lblMarkerSymbol = new javax.swing.JLabel();
        txtAllele1Key = new javax.swing.JTextField();
        txtAllele1Symbol = new javax.swing.JTextField();
        txtAllele2Key = new javax.swing.JTextField();
        txtAllele1Type = new javax.swing.JTextField();
        txtAllele2Symbol = new javax.swing.JTextField();
        txtMarkerSymbol = new javax.swing.JTextField();
        txtAllele2Type = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textareaAllele1Note = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textareaAllele2Note = new javax.swing.JTextArea();
        pnlGeneticChanges = new javax.swing.JPanel();
        headerPanelCytogenetics = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblCytogeneticChange = new javax.swing.JLabel();
        lblCytogeneticNote = new javax.swing.JLabel();
        jspCytogeneticNote = new javax.swing.JScrollPane();
        txtareaCytogeneticNote = new javax.swing.JTextArea();
        jspCytogeneticChromosomes = new javax.swing.JScrollPane();
        listCytogeneticChromosomes = new javax.swing.JList();
        lblCytogeneticChromosome = new javax.swing.JLabel();
        jspGeneticChanges = new javax.swing.JScrollPane();
        tblCytogenetics = new javax.swing.JTable();
        comboCytogeneticChange = new javax.swing.JComboBox();
        btnGeneticChange = new javax.swing.JButton();
        lblCytogeneticName = new javax.swing.JLabel();
        txtCytogeneticName = new javax.swing.JTextField();
        comboAssayType = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        btnAssayImages = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnDuplicate = new javax.swing.JButton();
        jButtonPrevious = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jLabelPN = new javax.swing.JTextField();

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblStrainKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblStrainKey.setText("Strain Key");

        lblStrainSex.setText("Strain Sex");

        comboStrainSex.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Select --", "Female", "Male", "Mixed population", "Not specified" }));

        txtStrainKey.setColumns(10);

        btnStrainLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
        btnStrainLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStrainLookupActionPerformed(evt);
            }
        });

        headerPanelStrain.setDrawSeparatorUnderneath(true);
        headerPanelStrain.setText("Strain Information");

        lblStrainName.setText("Strain Name");

        lblStrainTypes.setText("Strain Types");

        txtStrainTypes.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        txtStrainName.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        lblStrainDescription.setText("Description");

        txtareaStrainDescription.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        txtareaStrainDescription.setColumns(20);
        txtareaStrainDescription.setRows(3);
        jspStrainDescription.setViewportView(txtareaStrainDescription);

        lblStrainSynonyms.setText("Synonyms");

        listStrainSynonyms.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        listStrainSynonyms.setVisibleRowCount(3);
        jspStrainSynonyms.setViewportView(listStrainSynonyms);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblStrainSex)
                    .add(lblStrainKey))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comboStrainSex, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 107, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnStrainLookup, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(29, 29, 29)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblStrainName)
                    .add(lblStrainTypes))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtStrainName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtStrainTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblStrainDescription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspStrainDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 253, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblStrainSynonyms)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspStrainSynonyms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 241, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(143, 143, 143))
            .add(headerPanelStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1236, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(headerPanelStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jspStrainSynonyms, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(lblStrainSynonyms)
                        .addContainerGap())
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblStrainKey)
                            .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblStrainSex)
                            .add(comboStrainSex, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(jspStrainDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblStrainName)
                            .add(txtStrainName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblStrainDescription))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblStrainTypes)
                            .add(txtStrainTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(btnStrainLookup)))
        );

        pnlTumorFrequencyInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTumorFrequencyKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblTumorFrequencyKey.setText("Tumor Frequency Key");

        txtTumorFrequencyKey.setColumns(10);
        txtTumorFrequencyKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setEnabled(false);
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        txtJNumber.setColumns(10);
        txtJNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtJNumberFocusLost(evt);
            }
        });

        lblJNumber.setText("J Number");

        lblOrganTissueOrigin.setText("Organ/Tissue of Origin");

        lblTumorClassification.setText("Tumor Classification");

        lblOrganTissueAffected.setText("Organ/Tissue Affected");

        lblAgeOnset.setText("Age Onset");

        txtAgeOnset.setColumns(15);

        txtAgeDetection.setColumns(15);

        lblAgeDetection.setText("Age Detection");

        lblColonySize.setText("Colony Size");

        lblNumMiceAffected.setText("# Mice Affected");

        lblIncidence.setText("Incidence");

        lblBreedingStatus.setText("Breeding Status");

        lblSortEquivalent.setText("Sort Equivalent");

        lblInfectionStatus.setText("Infection Status");

        txtNumMiceAffected.setColumns(15);
        txtNumMiceAffected.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNumMiceAffectedFocusLost(evt);
            }
        });

        txtSortEquivalent.setColumns(15);
        txtSortEquivalent.setEditable(false);

        txtInfectionStatus.setColumns(15);

        txtColonySize.setColumns(15);
        txtColonySize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtColonySizeFocusLost(evt);
            }
        });

        txtIncidence.setColumns(15);
        txtIncidence.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIncidenceFocusLost(evt);
            }
        });

        lblMainNote.setText("Treatment Note");

        txtareaMainNote.setColumns(20);
        txtareaMainNote.setLineWrap(true);
        txtareaMainNote.setRows(2);
        txtareaMainNote.setWrapStyleWord(true);
        jspMainNote.setViewportView(txtareaMainNote);

        headerPanelTumorFrequencyInformation.setDrawSeparatorUnderneath(true);
        headerPanelTumorFrequencyInformation.setText("Tumor Frequency Information");

        lblAccession.setText("Accession ID");

        txtAccession.setColumns(10);
        txtAccession.setEditable(false);

        checkboxAutoAssignAccession.setSelected(true);
        checkboxAutoAssignAccession.setText("Auto Assign");
        checkboxAutoAssignAccession.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssignAccession.setEnabled(false);
        checkboxAutoAssignAccession.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssignAccession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignAccessionActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlTumorFrequencyInformationLayout = new org.jdesktop.layout.GroupLayout(pnlTumorFrequencyInformation);
        pnlTumorFrequencyInformation.setLayout(pnlTumorFrequencyInformationLayout);
        pnlTumorFrequencyInformationLayout.setHorizontalGroup(
            pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelTumorFrequencyInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
            .add(pnlTumorFrequencyInformationLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblJNumber)
                    .add(lblTumorFrequencyKey))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlTumorFrequencyInformationLayout.createSequentialGroup()
                        .add(txtTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(checkboxAutoAssign)
                        .add(41, 41, 41)
                        .add(lblAccession)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtAccession, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(checkboxAutoAssignAccession))
                    .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .add(pnlTumorFrequencyInformationLayout.createSequentialGroup()
                .add(21, 21, 21)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblIncidence)
                    .add(lblAgeOnset)
                    .add(lblMainNote)
                    .add(lblTumorClassification)
                    .add(lblOrganTissueOrigin)
                    .add(lblOrganTissueAffected)
                    .add(lblColonySize)
                    .add(lblBreedingStatus))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTumorFrequencyInformationLayout.createSequentialGroup()
                        .add(comboBreedingStatus, 0, 288, Short.MAX_VALUE)
                        .add(88, 88, 88)
                        .add(lblInfectionStatus)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtInfectionStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jspMainNote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                    .add(comboOrganTissueOrigin, 0, 583, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, comboTumorClassification, 0, 583, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, comboOrganTissueAffected, 0, 583, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTumorFrequencyInformationLayout.createSequentialGroup()
                        .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(pnlTumorFrequencyInformationLayout.createSequentialGroup()
                                .add(txtIncidence, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 254, Short.MAX_VALUE)
                                .add(lblSortEquivalent))
                            .add(pnlTumorFrequencyInformationLayout.createSequentialGroup()
                                .add(txtColonySize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 250, Short.MAX_VALUE)
                                .add(lblNumMiceAffected))
                            .add(pnlTumorFrequencyInformationLayout.createSequentialGroup()
                                .add(txtAgeOnset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 259, Short.MAX_VALUE)
                                .add(lblAgeDetection)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtSortEquivalent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txtNumMiceAffected, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txtAgeDetection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        pnlTumorFrequencyInformationLayout.setVerticalGroup(
            pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTumorFrequencyInformationLayout.createSequentialGroup()
                .add(headerPanelTumorFrequencyInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTumorFrequencyKey)
                    .add(txtTumorFrequencyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign)
                    .add(checkboxAutoAssignAccession)
                    .add(lblAccession)
                    .add(txtAccession, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJNumber)
                    .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comboOrganTissueOrigin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblOrganTissueOrigin))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comboTumorClassification, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblTumorClassification))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comboOrganTissueAffected, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblOrganTissueAffected))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAgeOnset)
                    .add(txtAgeOnset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtAgeDetection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblAgeDetection))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblColonySize)
                    .add(txtColonySize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblNumMiceAffected)
                    .add(txtNumMiceAffected, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblIncidence)
                    .add(txtIncidence, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblSortEquivalent)
                    .add(txtSortEquivalent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblBreedingStatus)
                    .add(comboBreedingStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtInfectionStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblInfectionStatus))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblMainNote)
                    .add(jspMainNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel2.setLayout(new java.awt.BorderLayout());

        pnlTumorFrequencyNotes.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblJNumberNote.setText("J Number");

        txtJNumberNote.setColumns(10);
        txtJNumberNote.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtJNumberNoteFocusLost(evt);
            }
        });

        lblNote.setText("Note");

        tblNotes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspNotes.setViewportView(tblNotes);

        btnNoteAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnNoteAdd.setText("Add");
        btnNoteAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoteAddActionPerformed(evt);
            }
        });

        headerPanelNotes.setDrawSeparatorUnderneath(true);
        headerPanelNotes.setText("Tumor Frequency Notes");

        org.jdesktop.layout.GroupLayout pnlTumorFrequencyNotesLayout = new org.jdesktop.layout.GroupLayout(pnlTumorFrequencyNotes);
        pnlTumorFrequencyNotes.setLayout(pnlTumorFrequencyNotesLayout);
        pnlTumorFrequencyNotesLayout.setHorizontalGroup(
            pnlTumorFrequencyNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTumorFrequencyNotesLayout.createSequentialGroup()
                .add(138, 138, 138)
                .add(lblNote)
                .addContainerGap(338, Short.MAX_VALUE))
            .add(headerPanelNotes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
            .add(pnlTumorFrequencyNotesLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblJNumberNote)
                .addContainerGap(444, Short.MAX_VALUE))
            .add(pnlTumorFrequencyNotesLayout.createSequentialGroup()
                .addContainerGap()
                .add(txtJNumberNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtNote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                .add(12, 12, 12)
                .add(btnNoteAdd)
                .addContainerGap())
            .add(pnlTumorFrequencyNotesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jspNotes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTumorFrequencyNotesLayout.setVerticalGroup(
            pnlTumorFrequencyNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTumorFrequencyNotesLayout.createSequentialGroup()
                .add(headerPanelNotes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJNumberNote)
                    .add(lblNote))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencyNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtJNumberNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnNoteAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspNotes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(pnlTumorFrequencyNotes, java.awt.BorderLayout.CENTER);

        pnlTumorFrequencySynonyms.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
        headerPanelSynonyms.setText("Tumor Frequency Synonyms");

        org.jdesktop.layout.GroupLayout pnlTumorFrequencySynonymsLayout = new org.jdesktop.layout.GroupLayout(pnlTumorFrequencySynonyms);
        pnlTumorFrequencySynonyms.setLayout(pnlTumorFrequencySynonymsLayout);
        pnlTumorFrequencySynonymsLayout.setHorizontalGroup(
            pnlTumorFrequencySynonymsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelSynonyms, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlTumorFrequencySynonymsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlTumorFrequencySynonymsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jspSynonyms, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                    .add(pnlTumorFrequencySynonymsLayout.createSequentialGroup()
                        .add(pnlTumorFrequencySynonymsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblJNumberSynonym)
                            .add(lblSynonym))
                        .add(pnlTumorFrequencySynonymsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlTumorFrequencySynonymsLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtJNumberSynonym, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnlTumorFrequencySynonymsLayout.createSequentialGroup()
                                .add(11, 11, 11)
                                .add(txtSynonym, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnSynonymAdd)))
                .addContainerGap())
        );
        pnlTumorFrequencySynonymsLayout.setVerticalGroup(
            pnlTumorFrequencySynonymsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTumorFrequencySynonymsLayout.createSequentialGroup()
                .add(headerPanelSynonyms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencySynonymsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblJNumberSynonym)
                    .add(txtJNumberSynonym, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTumorFrequencySynonymsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSynonym)
                    .add(btnSynonymAdd)
                    .add(txtSynonym, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(13, 13, 13)
                .add(jspSynonyms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(pnlTumorFrequencySynonyms, java.awt.BorderLayout.NORTH);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(pnlTumorFrequencyInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pnlTumorFrequencyInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.setLayout(new java.awt.BorderLayout());

        pnlProgressionChild.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbTumorFrequencyKeyProgressionChild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lbTumorFrequencyKeyProgressionChild.setText("Child Tumor Frequency Key");

        txtTumorFrequencyKeyProgressionChild.setColumns(10);
        txtTumorFrequencyKeyProgressionChild.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTumorFrequencyKeyProgressionChildFocusLost(evt);
            }
        });

        lblProgressionTypeProgressionChild.setText("Tumor Progression Type");

        tblProgressionChildren.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspProgressionChildren.setViewportView(tblProgressionChildren);

        btnProgressionAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnProgressionAdd.setText("Add");
        btnProgressionAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProgressionAddActionPerformed(evt);
            }
        });

        headerPanel3.setDrawSeparatorUnderneath(true);
        headerPanel3.setText("Tumor Progression Information");

        lblTumorFrequencyKeyProgressionParent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblTumorFrequencyKeyProgressionParent.setText("Parent Tumor Frequency Key");

        txtTumorFrequencyKeyProgressionParent.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        txtTumorFrequencyKeyProgressionParent.setColumns(10);
        txtTumorFrequencyKeyProgressionParent.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTumorFrequencyKeyProgressionParentFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlProgressionChildLayout = new org.jdesktop.layout.GroupLayout(pnlProgressionChild);
        pnlProgressionChild.setLayout(pnlProgressionChildLayout);
        pnlProgressionChildLayout.setHorizontalGroup(
            pnlProgressionChildLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
            .add(pnlProgressionChildLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlProgressionChildLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jspProgressionChildren, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                    .add(pnlProgressionChildLayout.createSequentialGroup()
                        .add(pnlProgressionChildLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblProgressionTypeProgressionChild)
                            .add(lbTumorFrequencyKeyProgressionChild))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlProgressionChildLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txtTumorFrequencyKeyProgressionChild)
                            .add(comboProgressionTypeProgressionChild, 0, 176, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnProgressionAdd))
                    .add(pnlProgressionChildLayout.createSequentialGroup()
                        .add(lblTumorFrequencyKeyProgressionParent)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtTumorFrequencyKeyProgressionParent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(separatorTopProgressionChild, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlProgressionChildLayout.setVerticalGroup(
            pnlProgressionChildLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlProgressionChildLayout.createSequentialGroup()
                .add(headerPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlProgressionChildLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTumorFrequencyKeyProgressionParent)
                    .add(txtTumorFrequencyKeyProgressionParent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(separatorTopProgressionChild, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlProgressionChildLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbTumorFrequencyKeyProgressionChild)
                    .add(txtTumorFrequencyKeyProgressionChild, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlProgressionChildLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblProgressionTypeProgressionChild)
                    .add(btnProgressionAdd)
                    .add(comboProgressionTypeProgressionChild, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspProgressionChildren, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(pnlProgressionChild, java.awt.BorderLayout.CENTER);

        pnlTreatmentsInner.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblAgent.setText("Agent");

        btnAgentAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnAgentAdd.setText("Add");
        btnAgentAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgentAddActionPerformed(evt);
            }
        });

        pnlFilterAgents.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10), javax.swing.BorderFactory.createTitledBorder("Agent Filter")));

        lblAgentNameFilter.setText("Agent Name");

        lblAgentTypeFilter.setText("Agent Type");

        comboAgentNameFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contains", "Equals" }));

        btnFilterApply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/TableEdit16.png"))); // NOI18N
        btnFilterApply.setText("Apply Filter");
        btnFilterApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterApplyActionPerformed(evt);
            }
        });

        btnFilterClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Refresh16.png"))); // NOI18N
        btnFilterClear.setText("Clear Filter");
        btnFilterClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterClearActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlFilterAgentsLayout = new org.jdesktop.layout.GroupLayout(pnlFilterAgents);
        pnlFilterAgents.setLayout(pnlFilterAgentsLayout);
        pnlFilterAgentsLayout.setHorizontalGroup(
            pnlFilterAgentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFilterAgentsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlFilterAgentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblAgentNameFilter)
                    .add(lblAgentTypeFilter))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlFilterAgentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnlFilterAgentsLayout.createSequentialGroup()
                        .add(comboAgentNameFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtAgentNameFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(comboAgentTypeFilter, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlFilterAgentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnFilterApply)
                    .add(btnFilterClear))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFilterAgentsLayout.linkSize(new java.awt.Component[] {btnFilterApply, btnFilterClear}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnlFilterAgentsLayout.setVerticalGroup(
            pnlFilterAgentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFilterAgentsLayout.createSequentialGroup()
                .add(pnlFilterAgentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comboAgentNameFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnFilterApply)
                    .add(txtAgentNameFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblAgentNameFilter))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlFilterAgentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comboAgentTypeFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnFilterClear)
                    .add(lblAgentTypeFilter))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblTreatmentTypes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspTreatmentTypes.setViewportView(tblTreatmentTypes);

        headerPanelTreatments.setDrawSeparatorUnderneath(true);
        headerPanelTreatments.setText("Tumor Frequency Treatments");

        org.jdesktop.layout.GroupLayout pnlTreatmentsInnerLayout = new org.jdesktop.layout.GroupLayout(pnlTreatmentsInner);
        pnlTreatmentsInner.setLayout(pnlTreatmentsInnerLayout);
        pnlTreatmentsInnerLayout.setHorizontalGroup(
            pnlTreatmentsInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelTreatments, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
            .add(pnlTreatmentsInnerLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlTreatmentsInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jspTreatmentTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                    .add(pnlTreatmentsInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, pnlFilterAgents, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, pnlTreatmentsInnerLayout.createSequentialGroup()
                            .add(lblAgent)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(comboAgent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(btnAgentAdd))))
                .addContainerGap())
        );
        pnlTreatmentsInnerLayout.setVerticalGroup(
            pnlTreatmentsInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTreatmentsInnerLayout.createSequentialGroup()
                .add(headerPanelTreatments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTreatmentsInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAgent)
                    .add(comboAgent, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnAgentAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlFilterAgents, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspTreatmentTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(pnlTreatmentsInner, java.awt.BorderLayout.WEST);

        pnlPathologyDetail.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblPathologyKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblPathologyKey.setText("Pathology Key");

        txtPathologyKey.setColumns(10);

        btnPathologyAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnPathologyAdd.setText("Add");
        btnPathologyAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPathologyAddActionPerformed(evt);
            }
        });

        tblPathology.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspPathology.setViewportView(tblPathology);

        headerPanel1.setDrawSeparatorUnderneath(true);
        headerPanel1.setText("Tumor Frequency Pathology Association");

        org.jdesktop.layout.GroupLayout pnlPathologyDetailLayout = new org.jdesktop.layout.GroupLayout(pnlPathologyDetail);
        pnlPathologyDetail.setLayout(pnlPathologyDetailLayout);
        pnlPathologyDetailLayout.setHorizontalGroup(
            pnlPathologyDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1236, Short.MAX_VALUE)
            .add(pnlPathologyDetailLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlPathologyDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jspPathology, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1216, Short.MAX_VALUE)
                    .add(pnlPathologyDetailLayout.createSequentialGroup()
                        .add(lblPathologyKey)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtPathologyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnPathologyAdd)))
                .addContainerGap())
        );
        pnlPathologyDetailLayout.setVerticalGroup(
            pnlPathologyDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPathologyDetailLayout.createSequentialGroup()
                .add(headerPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPathologyKey)
                    .add(txtPathologyKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnPathologyAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspPathology, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout pnllTumorFrequencyLayout = new org.jdesktop.layout.GroupLayout(pnllTumorFrequency);
        pnllTumorFrequency.setLayout(pnllTumorFrequencyLayout);
        pnllTumorFrequencyLayout.setHorizontalGroup(
            pnllTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1240, Short.MAX_VALUE)
            .add(pnlPathologyDetail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnllTumorFrequencyLayout.setVerticalGroup(
            pnllTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnllTumorFrequencyLayout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPathologyDetail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        tabbedpaneData.addTab("Tumor Frequency", pnllTumorFrequency);

        pnlGenetics.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        headerPanelGenetics.setDrawSeparatorUnderneath(true);
        headerPanelGenetics.setText("Genetics");

        lblAllelePairKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblAllelePairKey.setText("Allele Pair Key");

        txtAllelePairKey.setColumns(10);

        btnAddAllelePair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnAddAllelePair.setText("Add");
        btnAddAllelePair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAllelePairActionPerformed(evt);
            }
        });

        tblGeneticChanges.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspGenetics.setViewportView(tblGeneticChanges);

        btnAllelePairLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
        btnAllelePairLookup.setText("Lookup");
        btnAllelePairLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllelePairLookupActionPerformed(evt);
            }
        });

        lblAllele1Key.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblAllele1Key.setText("Allele 1 Key");

        lblAllele1Symbol.setText("Allele 1 Symbol");

        lblAllele1Type.setText("Allele 1 Type");

        lblAllele2Key.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblAllele2Key.setText("Allele 2 Key");

        lblAllele2Symbol.setText("Allele 2 Symbol");

        lblAllele2Type.setText("Allele 2 Type");

        lblMarkerSymbol.setText("Marker Symbol");

        txtAllele1Key.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        txtAllele1Symbol.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        txtAllele2Key.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        txtAllele1Type.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        txtAllele2Symbol.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        txtMarkerSymbol.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        txtAllele2Type.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));

        jLabel1.setText("Allele 1 Note");

        textareaAllele1Note.setColumns(20);
        textareaAllele1Note.setRows(4);
        jScrollPane1.setViewportView(textareaAllele1Note);

        jLabel2.setText("Allele 2 Note");

        textareaAllele2Note.setColumns(20);
        textareaAllele2Note.setRows(4);
        jScrollPane2.setViewportView(textareaAllele2Note);

        org.jdesktop.layout.GroupLayout pnlGeneticsLayout = new org.jdesktop.layout.GroupLayout(pnlGenetics);
        pnlGenetics.setLayout(pnlGeneticsLayout);
        pnlGeneticsLayout.setHorizontalGroup(
            pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlGeneticsLayout.createSequentialGroup()
                .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, headerPanelGenetics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1206, Short.MAX_VALUE)
                    .add(pnlGeneticsLayout.createSequentialGroup()
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlGeneticsLayout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(lblAllelePairKey)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtAllelePairKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnAllelePairLookup)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btnAddAllelePair))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlGeneticsLayout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(separatorGenetics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 427, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlGeneticsLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(lblAllele2Type)
                                        .add(lblAllele1Key)
                                        .add(lblAllele1Type)
                                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(lblAllele1Symbol)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1))
                                        .add(jLabel2)
                                        .add(lblAllele2Symbol))
                                    .add(lblAllele2Key)
                                    .add(lblMarkerSymbol))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtAllele2Symbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtAllele2Key, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                    .add(txtMarkerSymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtAllele1Type, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtAllele1Symbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtAllele1Key, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                    .add(txtAllele2Type, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jspGenetics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 733, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlGeneticsLayout.setVerticalGroup(
            pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlGeneticsLayout.createSequentialGroup()
                .add(headerPanelGenetics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlGeneticsLayout.createSequentialGroup()
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAllelePairKey)
                            .add(btnAllelePairLookup)
                            .add(btnAddAllelePair)
                            .add(txtAllelePairKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(separatorGenetics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAllele1Key)
                            .add(txtAllele1Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAllele1Symbol)
                            .add(txtAllele1Symbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtAllele1Type, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblAllele1Type))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(pnlGeneticsLayout.createSequentialGroup()
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(17, 17, 17)
                                .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(lblAllele2Key)
                                    .add(txtAllele2Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAllele2Symbol)
                            .add(txtAllele2Symbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAllele2Type)
                            .add(txtAllele2Type, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtMarkerSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblMarkerSymbol)))
                    .add(pnlGeneticsLayout.createSequentialGroup()
                        .add(jspGenetics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pnlGeneticChanges.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        headerPanelCytogenetics.setDrawSeparatorUnderneath(true);
        headerPanelCytogenetics.setText("Cytogenetics");

        lblCytogeneticChange.setText("Genetic Change");

        lblCytogeneticNote.setText("Note");

        txtareaCytogeneticNote.setColumns(20);
        txtareaCytogeneticNote.setRows(4);
        jspCytogeneticNote.setViewportView(txtareaCytogeneticNote);

        listCytogeneticChromosomes.setVisibleRowCount(4);
        jspCytogeneticChromosomes.setViewportView(listCytogeneticChromosomes);

        lblCytogeneticChromosome.setText("Chromosome");

        tblCytogenetics.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspGeneticChanges.setViewportView(tblCytogenetics);

        comboCytogeneticChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboCytogeneticChangeActionPerformed(evt);
            }
        });

        btnGeneticChange.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnGeneticChange.setText("Add");
        btnGeneticChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGeneticChangeActionPerformed(evt);
            }
        });

        lblCytogeneticName.setText("Name");

        comboAssayType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAssayTypeActionPerformed(evt);
            }
        });

        jLabel3.setText("Assay Type");

        btnAssayImages.setText("Add / Edit Assay Images");
        btnAssayImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssayImagesActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlGeneticChangesLayout = new org.jdesktop.layout.GroupLayout(pnlGeneticChanges);
        pnlGeneticChanges.setLayout(pnlGeneticChangesLayout);
        pnlGeneticChangesLayout.setHorizontalGroup(
            pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, headerPanelCytogenetics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1216, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlGeneticChangesLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlGeneticChangesLayout.createSequentialGroup()
                        .add(14, 14, 14)
                        .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(pnlGeneticChangesLayout.createSequentialGroup()
                                .add(lblCytogeneticNote)
                                .add(4, 4, 4))
                            .add(lblCytogeneticChromosome)
                            .add(lblCytogeneticName)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jspCytogeneticNote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, comboAssayType, 0, 263, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtCytogeneticName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .add(jspCytogeneticChromosomes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnGeneticChange)
                    .add(pnlGeneticChangesLayout.createSequentialGroup()
                        .add(lblCytogeneticChange)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comboCytogeneticChange, 0, 263, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlGeneticChangesLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 682, Short.MAX_VALUE)
                        .add(btnAssayImages)
                        .add(32, 32, 32))
                    .add(pnlGeneticChangesLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jspGeneticChanges, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 821, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        pnlGeneticChangesLayout.setVerticalGroup(
            pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlGeneticChangesLayout.createSequentialGroup()
                .add(headerPanelCytogenetics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnGeneticChange)
                    .add(btnAssayImages))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlGeneticChangesLayout.createSequentialGroup()
                        .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(comboCytogeneticChange, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblCytogeneticChange))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(comboAssayType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtCytogeneticName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lblCytogeneticName))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblCytogeneticNote)
                            .add(jspCytogeneticNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(9, 9, 9)
                        .add(pnlGeneticChangesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblCytogeneticChromosome)
                            .add(jspCytogeneticChromosomes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jspGeneticChanges, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 230, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlAllGeneticsLayout = new org.jdesktop.layout.GroupLayout(pnlAllGenetics);
        pnlAllGenetics.setLayout(pnlAllGeneticsLayout);
        pnlAllGeneticsLayout.setHorizontalGroup(
            pnlAllGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlAllGeneticsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlAllGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlGeneticChanges, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlGenetics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlAllGeneticsLayout.setVerticalGroup(
            pnlAllGeneticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAllGeneticsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlGenetics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlGeneticChanges, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedpaneData.addTab("Genetics", pnlAllGenetics);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Save16.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnDuplicate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Jetfire16.png"))); // NOI18N
        btnDuplicate.setText("Duplicate");
        btnDuplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDuplicateActionPerformed(evt);
            }
        });

        jButtonPrevious.setText("Previous");
        jButtonPrevious.setEnabled(false);
        jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousActionPerformed(evt);
            }
        });

        jButtonNext.setText("Next");
        jButtonNext.setEnabled(false);
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });

        jLabelPN.setEditable(false);
        jLabelPN.setBorder(null);
        jLabelPN.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, tabbedpaneData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1245, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jButtonPrevious)
                        .add(18, 18, 18)
                        .add(jButtonNext)
                        .add(32, 32, 32)
                        .add(jLabelPN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 591, Short.MAX_VALUE)
                        .add(btnDuplicate)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnSave)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(tabbedpaneData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave)
                    .add(btnDuplicate)
                    .add(jButtonPrevious)
                    .add(jButtonNext)
                    .add(jLabelPN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkboxAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignActionPerformed
        if (checkboxAutoAssign.isSelected()) {
            txtTumorFrequencyKey.setEditable(false);
            txtTumorFrequencyKey.setText("");
        } else {
            txtTumorFrequencyKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void txtJNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberFocusLost
        Utils.fixJNumber(txtJNumber);
        txtJNumberNote.setText(txtJNumber.getText());
        txtJNumberSynonym.setText(txtJNumber.getText());
    }//GEN-LAST:event_txtJNumberFocusLost

    private void txtNumMiceAffectedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNumMiceAffectedFocusLost
        autoCalculateFrequency();
    }//GEN-LAST:event_txtNumMiceAffectedFocusLost

    private void txtColonySizeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtColonySizeFocusLost
        autoCalculateFrequency();
    }//GEN-LAST:event_txtColonySizeFocusLost

    private void txtIncidenceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIncidenceFocusLost
        autoCalculateFrequency();
    }//GEN-LAST:event_txtIncidenceFocusLost

    private void checkboxAutoAssignAccessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignAccessionActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_checkboxAutoAssignAccessionActionPerformed

    private void btnStrainLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStrainLookupActionPerformed
        lookupStrain();
    }//GEN-LAST:event_btnStrainLookupActionPerformed

    private void txtJNumberNoteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberNoteFocusLost
        Utils.fixJNumber(txtJNumberNote);
    }//GEN-LAST:event_txtJNumberNoteFocusLost

    private void btnNoteAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoteAddActionPerformed
        addNote();
    }//GEN-LAST:event_btnNoteAddActionPerformed

    private void txtJNumberSynonymFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberSynonymFocusLost
        Utils.fixJNumber(txtJNumberSynonym);
    }//GEN-LAST:event_txtJNumberSynonymFocusLost

    private void btnSynonymAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSynonymAddActionPerformed
        addSynonym();
    }//GEN-LAST:event_btnSynonymAddActionPerformed

    private void btnPathologyAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPathologyAddActionPerformed
        addPathology();
    }//GEN-LAST:event_btnPathologyAddActionPerformed

    private void btnProgressionAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProgressionAddActionPerformed
        addProgression();
    }//GEN-LAST:event_btnProgressionAddActionPerformed

    private void btnAgentAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgentAddActionPerformed
        addAgent();
    }//GEN-LAST:event_btnAgentAddActionPerformed

    private void btnFilterApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterApplyActionPerformed
        applyFilter();
    }//GEN-LAST:event_btnFilterApplyActionPerformed

    private void btnFilterClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterClearActionPerformed
        clearFilter();
    }//GEN-LAST:event_btnFilterClearActionPerformed

    private void btnAddAllelePairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAllelePairActionPerformed
        addAllelePair();
    }//GEN-LAST:event_btnAddAllelePairActionPerformed

    private void btnAllelePairLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllelePairLookupActionPerformed
        lookupAllelePair();
    }//GEN-LAST:event_btnAllelePairLookupActionPerformed

    private void btnGeneticChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGeneticChangeActionPerformed
        addGeneticChange();
    }//GEN-LAST:event_btnGeneticChangeActionPerformed

    private void btnDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDuplicateActionPerformed
        duplicate();
    }//GEN-LAST:event_btnDuplicateActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

private void comboCytogeneticChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboCytogeneticChangeActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_comboCytogeneticChangeActionPerformed

private void comboAssayTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAssayTypeActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_comboAssayTypeActionPerformed

private void btnAssayImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssayImagesActionPerformed
    showImages();
}//GEN-LAST:event_btnAssayImagesActionPerformed

private void comboStrainSexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboStrainSexActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_comboStrainSexActionPerformed

private void txtTumorFrequencyKeyProgressionParentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTumorFrequencyKeyProgressionParentFocusLost
    checkParent();
}//GEN-LAST:event_txtTumorFrequencyKeyProgressionParentFocusLost

private void txtTumorFrequencyKeyProgressionChildFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTumorFrequencyKeyProgressionChildFocusLost
    checkChild();
}//GEN-LAST:event_txtTumorFrequencyKeyProgressionChildFocusLost

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        next();
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousActionPerformed
        previous();
    }//GEN-LAST:event_jButtonPreviousActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddAllelePair;
    private javax.swing.JButton btnAgentAdd;
    private javax.swing.JButton btnAllelePairLookup;
    private javax.swing.JButton btnAssayImages;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDuplicate;
    private javax.swing.JButton btnFilterApply;
    private javax.swing.JButton btnFilterClear;
    private javax.swing.JButton btnGeneticChange;
    private javax.swing.JButton btnNoteAdd;
    private javax.swing.JButton btnPathologyAdd;
    private javax.swing.JButton btnProgressionAdd;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnStrainLookup;
    private javax.swing.JButton btnSynonymAdd;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private javax.swing.JCheckBox checkboxAutoAssignAccession;
    private javax.swing.JComboBox comboAgent;
    private javax.swing.JComboBox comboAgentNameFilter;
    private javax.swing.JComboBox comboAgentTypeFilter;
    private javax.swing.JComboBox comboAssayType;
    private javax.swing.JComboBox comboBreedingStatus;
    private javax.swing.JComboBox comboCytogeneticChange;
    private javax.swing.JComboBox comboOrganTissueAffected;
    private javax.swing.JComboBox comboOrganTissueOrigin;
    private javax.swing.JComboBox comboProgressionTypeProgressionChild;
    private javax.swing.JComboBox comboStrainSex;
    private javax.swing.JComboBox comboTumorClassification;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanel1;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanel3;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCytogenetics;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelGenetics;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelNotes;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelStrain;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelSynonyms;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelTreatments;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelTumorFrequencyInformation;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jLabelPN;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jspCytogeneticChromosomes;
    private javax.swing.JScrollPane jspCytogeneticNote;
    private javax.swing.JScrollPane jspGeneticChanges;
    private javax.swing.JScrollPane jspGenetics;
    private javax.swing.JScrollPane jspMainNote;
    private javax.swing.JScrollPane jspNotes;
    private javax.swing.JScrollPane jspPathology;
    private javax.swing.JScrollPane jspProgressionChildren;
    private javax.swing.JScrollPane jspStrainDescription;
    private javax.swing.JScrollPane jspStrainSynonyms;
    private javax.swing.JScrollPane jspSynonyms;
    private javax.swing.JScrollPane jspTreatmentTypes;
    private javax.swing.JLabel lbTumorFrequencyKeyProgressionChild;
    private javax.swing.JLabel lblAccession;
    private javax.swing.JLabel lblAgeDetection;
    private javax.swing.JLabel lblAgeOnset;
    private javax.swing.JLabel lblAgent;
    private javax.swing.JLabel lblAgentNameFilter;
    private javax.swing.JLabel lblAgentTypeFilter;
    private javax.swing.JLabel lblAllele1Key;
    private javax.swing.JLabel lblAllele1Symbol;
    private javax.swing.JLabel lblAllele1Type;
    private javax.swing.JLabel lblAllele2Key;
    private javax.swing.JLabel lblAllele2Symbol;
    private javax.swing.JLabel lblAllele2Type;
    private javax.swing.JLabel lblAllelePairKey;
    private javax.swing.JLabel lblBreedingStatus;
    private javax.swing.JLabel lblColonySize;
    private javax.swing.JLabel lblCytogeneticChange;
    private javax.swing.JLabel lblCytogeneticChromosome;
    private javax.swing.JLabel lblCytogeneticName;
    private javax.swing.JLabel lblCytogeneticNote;
    private javax.swing.JLabel lblIncidence;
    private javax.swing.JLabel lblInfectionStatus;
    private javax.swing.JLabel lblJNumber;
    private javax.swing.JLabel lblJNumberNote;
    private javax.swing.JLabel lblJNumberSynonym;
    private javax.swing.JLabel lblMainNote;
    private javax.swing.JLabel lblMarkerSymbol;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblNumMiceAffected;
    private javax.swing.JLabel lblOrganTissueAffected;
    private javax.swing.JLabel lblOrganTissueOrigin;
    private javax.swing.JLabel lblPathologyKey;
    private javax.swing.JLabel lblProgressionTypeProgressionChild;
    private javax.swing.JLabel lblSortEquivalent;
    private javax.swing.JLabel lblStrainDescription;
    private javax.swing.JLabel lblStrainKey;
    private javax.swing.JLabel lblStrainName;
    private javax.swing.JLabel lblStrainSex;
    private javax.swing.JLabel lblStrainSynonyms;
    private javax.swing.JLabel lblStrainTypes;
    private javax.swing.JLabel lblSynonym;
    private javax.swing.JLabel lblTumorClassification;
    private javax.swing.JLabel lblTumorFrequencyKey;
    private javax.swing.JLabel lblTumorFrequencyKeyProgressionParent;
    private javax.swing.JList listCytogeneticChromosomes;
    private javax.swing.JList listStrainSynonyms;
    private javax.swing.JPanel pnlAllGenetics;
    private javax.swing.JPanel pnlFilterAgents;
    private javax.swing.JPanel pnlGeneticChanges;
    private javax.swing.JPanel pnlGenetics;
    private javax.swing.JPanel pnlPathologyDetail;
    private javax.swing.JPanel pnlProgressionChild;
    private javax.swing.JPanel pnlTreatmentsInner;
    private javax.swing.JPanel pnlTumorFrequencyInformation;
    private javax.swing.JPanel pnlTumorFrequencyNotes;
    private javax.swing.JPanel pnlTumorFrequencySynonyms;
    private javax.swing.JPanel pnllTumorFrequency;
    private javax.swing.JSeparator separatorGenetics;
    private javax.swing.JSeparator separatorTopProgressionChild;
    private javax.swing.JTabbedPane tabbedpaneData;
    private javax.swing.JTable tblCytogenetics;
    private javax.swing.JTable tblGeneticChanges;
    private javax.swing.JTable tblNotes;
    private javax.swing.JTable tblPathology;
    private javax.swing.JTable tblProgressionChildren;
    private javax.swing.JTable tblSynonyms;
    private javax.swing.JTable tblTreatmentTypes;
    private javax.swing.JTextArea textareaAllele1Note;
    private javax.swing.JTextArea textareaAllele2Note;
    private javax.swing.JTextField txtAccession;
    private javax.swing.JTextField txtAgeDetection;
    private javax.swing.JTextField txtAgeOnset;
    private javax.swing.JTextField txtAgentNameFilter;
    private javax.swing.JTextField txtAllele1Key;
    private javax.swing.JTextField txtAllele1Symbol;
    private javax.swing.JTextField txtAllele1Type;
    private javax.swing.JTextField txtAllele2Key;
    private javax.swing.JTextField txtAllele2Symbol;
    private javax.swing.JTextField txtAllele2Type;
    private javax.swing.JTextField txtAllelePairKey;
    private javax.swing.JTextField txtColonySize;
    private javax.swing.JTextField txtCytogeneticName;
    private javax.swing.JTextField txtIncidence;
    private javax.swing.JTextField txtInfectionStatus;
    private javax.swing.JTextField txtJNumber;
    private javax.swing.JTextField txtJNumberNote;
    private javax.swing.JTextField txtJNumberSynonym;
    private javax.swing.JTextField txtMarkerSymbol;
    private javax.swing.JTextField txtNote;
    private javax.swing.JTextField txtNumMiceAffected;
    private javax.swing.JTextField txtPathologyKey;
    private javax.swing.JTextField txtSortEquivalent;
    private javax.swing.JTextField txtStrainKey;
    private javax.swing.JTextField txtStrainName;
    private javax.swing.JTextField txtStrainTypes;
    private javax.swing.JTextField txtSynonym;
    private javax.swing.JTextField txtTumorFrequencyKey;
    private javax.swing.JTextField txtTumorFrequencyKeyProgressionChild;
    private javax.swing.JTextField txtTumorFrequencyKeyProgressionParent;
    private javax.swing.JTextArea txtareaCytogeneticNote;
    private javax.swing.JTextArea txtareaMainNote;
    private javax.swing.JTextArea txtareaStrainDescription;
    // End of variables declaration//GEN-END:variables
}
