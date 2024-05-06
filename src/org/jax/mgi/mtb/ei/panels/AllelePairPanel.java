/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AllelePairPanel.java,v 1.1 2007/04/30 15:50:52 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AllelePairDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AllelePairDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorGeneticsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorGeneticsDTO;
import org.jax.mgi.mtb.dao.gen.mtb.GenotypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.GenotypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.models.TumorGeneticsDTOTableModel;
import org.jax.mgi.mtb.ei.models.DTOTableModel;
import org.jax.mgi.mtb.ei.models.GenotypeStrainsDTOTableModel;
import org.jax.mgi.mtb.ei.renderers.DTORenderer;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jax.mgi.mtb.gui.table.MXDefaultTableModel;
import org.jax.mgi.mtb.gui.MXTable;

/**
 * For editing <b>AllelePair</b> data.
 *
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AllelePairPanel.java,v 1.1 2007/04/30 15:50:52 mjv Exp
 * @date 2007/04/30 15:50:52
 */
public class AllelePairPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants

    public static final int ALLELE_PAIR_ADD = 0;
    public static final int ALLELE_PAIR_EDIT = 1;

    // ----------------------------------------------------- Instance Variables

  

    // the AllelePairDTO object
    private AllelePairDTO dtoAP = null;
    private AlleleDTO dtoAllele1 = null;
    private AlleleDTO dtoAllele2 = null;

    // the type of panel
    private int nType = ALLELE_PAIR_ADD;

    // custom JTables for sorting purposes
    private MXTable fxtblStrain = null;
    private MXTable fxtblTumorFrequency = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;


    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new AllelePairPanel.
     */
    public AllelePairPanel(int nType) {
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
                progressMonitor.start("Loading Allele Pair: " + lKey);
                try{
                    lookupData(lKey);
                } catch (Exception e) {
                     EIGlobals.getInstance().getMainFrame().log("Unable to lookup allele pair data for " + lKey);
                      EIGlobals.getInstance().getMainFrame().log(e);
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
            // allele 1
            if (dtoAllele1 != null) {
                if ((dtoAP.getAllele1Key()!= null) &&  dtoAP.getAllele1Key().longValue() !=
                    dtoAllele1.getAlleleKey().longValue()) {
                    return true;
                }
            }

            // allele 2
            if (dtoAllele2 != null) {
                if ((dtoAP.getAllele2Key() != null) && dtoAP.getAllele2Key().longValue() !=
                    dtoAllele2.getAlleleKey().longValue()) {
                    return true;
                }
            }

            // genotype
            if (((DTOTableModel)fxtblStrain.getModel()).hasBeenUpdated()) {
                return true;
            }

            // tumor frequency
            if (((DTOTableModel)fxtblTumorFrequency.getModel()).hasBeenUpdated()) {
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
     * Lookup all allele pair related information in the database.
     *
     * @param lKey the allele pair lKey to be looked up in the database
     */
    private void lookupData(long lKey) {
        updateProgress("Loading allele pair data...");
        txtAllelePairKey.setText(lKey+"");

        AllelePairDAO daoAP = AllelePairDAO.getInstance();
        AlleleDAO daoAllele = AlleleDAO.getInstance();

        try {
            // get the allele pair
            dtoAP = daoAP.loadByPrimaryKey(new Long(lKey));

            // get allele 1
            updateProgress("Loading allele 1 data...");
            dtoAllele1 = daoAllele.loadByPrimaryKey(dtoAP.getAllele1Key());

            Long a2Key = dtoAP.getAllele2Key();

            if (a2Key != null) {
                updateProgress("Loading allele 2 data...");
                // get allele 2
                dtoAllele2 = daoAllele.loadByPrimaryKey(a2Key);
            }
        } catch (Exception e) {
            
            Utils.showErrorDialog("Error retrieving allele pair: " + lKey, e);
        }

        updateForm();
    }

    /**
     * Initialize the MXTable for allele pair and tumor frequency
     * associations.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initTumorFrequencyAssociation() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>();
        arrHeaders.add("T.F. Key");
        List arrTumorFrequency = new ArrayList();
        TumorGeneticsDTOTableModel<TumorGeneticsDTO> tblmdlTumorFrequency =
                new TumorGeneticsDTOTableModel<TumorGeneticsDTO>(arrTumorFrequency, arrHeaders);
        fxtblTumorFrequency = new MXTable(tblmdlTumorFrequency);
        fxtblTumorFrequency.setModel(tblmdlTumorFrequency);

        // set the table options
        fxtblTumorFrequency.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblTumorFrequency.setAlternateRowHighlight(true);
        fxtblTumorFrequency.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblTumorFrequency.setAlternateRowHighlightCount(2);
        fxtblTumorFrequency.setStartHighlightRow(1);
        fxtblTumorFrequency.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblTumorFrequency.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblTumorFrequency.enableToolTips(false);

     

        // update the JScrollPane
        jspTumorFrequency.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      
        jspTumorFrequency.setViewportView(fxtblTumorFrequency);

        // revalidate the panel
        pnlTumorFrequency.revalidate();
    }

    /**
     * Initialize the MXTable for strain association information.
     * <p>
     * A MXTable is used to provide sorting capabilities.  A
     * <code>DTORenderer</code> is used as the default renderer to provide
     * visual feedback of the state of the data.
     */
    private void initStrainAssociation() {
        // create the table model
        List<String> arrHeaders = new ArrayList<String>(4);
        arrHeaders.add("Key");
        arrHeaders.add("Name");
        arrHeaders.add("Description");
        arrHeaders.add("JNumber");
        List arrAccession = new ArrayList();
        GenotypeStrainsDTOTableModel<GenotypeDTO> tblmdlGenotype =
                new GenotypeStrainsDTOTableModel<GenotypeDTO>(arrAccession, arrHeaders);
        fxtblStrain = new MXTable(tblmdlGenotype);
        fxtblStrain.setModel(tblmdlGenotype);

        // set the table options
        fxtblStrain.setDefaultRenderer(Object.class, new DTORenderer());
        fxtblStrain.setColumnSizes(new int[]{100, 0, 0, 100});
        fxtblStrain.setAlternateRowHighlight(true);
        fxtblStrain.setRowHiliteColor(EIConstants.COLOR_RESULTS_HILITE);
        fxtblStrain.setAlternateRowHighlightCount(2);
        fxtblStrain.setStartHighlightRow(1);
        fxtblStrain.setSelectionBackground(
                EIConstants.COLOR_RESULTS_SELECTION_BG);
        fxtblStrain.setSelectionForeground(
                EIConstants.COLOR_RESULTS_SELECTION_FG);
        fxtblStrain.enableToolTip(0, false);
        fxtblStrain.enableToolTip(1, false);

        // create the genotype delete button
        JButton btnDelGenotype =
                new JButton(new ImageIcon(
                getClass().getResource(EIConstants.ICO_DELETE_16)));

        btnDelGenotype.setIconTextGap(0);
        btnDelGenotype.setMargin(new Insets(0, 0, 0, 0));
        btnDelGenotype.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeStrain();
            }
        });

        // update the JScrollPane
        jspStrain.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspStrain.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btnDelGenotype);
        jspStrain.setViewportView(fxtblStrain);

        // revalidate the panel
        pnlStrain.revalidate();
    }

    /**
     * Perform any custom initialization needed.
     */
    private void initCustom() {
        // make it so the following fields accept numeric input only
        Utils.setNumericFilter(txtAllelePairKey);
        Utils.setNumericFilter(txtAllele1Key);
        Utils.setNumericFilter(txtAllele2Key);
        Utils.setNumericFilter(txtStrainKey);

        // adjust components as needed
        if (nType == ALLELE_PAIR_EDIT) {
            txtAllelePairKey.setEditable(false);
            checkboxAutoAssign.setEnabled(false);
        }

        // create the allele pair dto
        dtoAP = AllelePairDAO.getInstance().createAllelePairDTO();
        dtoAllele1 = AlleleDAO.getInstance().createAlleleDTO();
        dtoAllele2 = AlleleDAO.getInstance().createAlleleDTO();

        initStrainAssociation();

        initTumorFrequencyAssociation();
    }

    /**
     * Add a strain to the genotype table only if strain strStrainKey and JNumber have
     * a value, and exist in the database.
     */
    private void addStrain() {
        String strStrainKey = txtStrainKey.getText().trim();
        String strJNumber = txtJNumber.getText().trim();
        long lStrainKey = -1;
        long lReferenceKey = -1;

        // validate that a strain key has been entered
        if (!StringUtils.hasValue(strStrainKey)) {
            Utils.showErrorDialog("Please enter a value for the Strain Key.");
            txtStrainKey.requestFocus();
            return;
        }

        // validate that the strain key is numeric
        try {
            lStrainKey = Long.parseLong(strStrainKey);
        } catch (Exception e) {
            Utils.showErrorDialog("Please enter a numeric value for the Strain Key.");
            txtStrainKey.requestFocus();
            return;
        }

        // validate that the strain key exists
        StrainDAO daoStrain = StrainDAO.getInstance();
        StrainDTO dtoStrain = null;

        try {
            dtoStrain = daoStrain.loadByPrimaryKey(new Long(lStrainKey));
        } catch (Exception e) {

        }

        if (dtoStrain == null) {
            Utils.showErrorDialog("Please enter a valid Strain Key.");
            txtStrainKey.requestFocus();
            return;
        }

        // validate that a JNumber has been entered
        if (!StringUtils.hasValue(strJNumber)) {
            Utils.showErrorDialog("Please enter a value for the J Number.");
            txtJNumber.requestFocus();
            return;
        }

        // validate that the JNumber exists
        try {
            lReferenceKey = EIGlobals.getInstance().getRefByAcc(strJNumber);

            if (lReferenceKey <= 0) {
                Utils.showErrorDialog("Please enter a valid JNumber.");
                txtJNumber.requestFocus();
                return;
            }
        } catch (Exception e) {
            Utils.showErrorDialog("Please enter a valid JNumber.");
            txtJNumber.requestFocus();
            return;
        }

        // get the table model
        GenotypeStrainsDTOTableModel<GenotypeDTO> tblmdlGenotype =
                (GenotypeStrainsDTOTableModel<GenotypeDTO>)fxtblStrain.getModel();

        // audit trail information
        MTBUsersDTO dtoUser = EIGlobals.getInstance().getMTBUsersDTO();
        Date dNow = new Date();

        // create the dtoGenotype
        GenotypeDTO dtoGenotype = GenotypeDAO.getInstance().createGenotypeDTO();

        dtoGenotype.setAllelePairKey(dtoAP.getAllelePairKey());
        dtoGenotype.setStrainKey(dtoStrain.getStrainKey());
        dtoGenotype.setReferenceKey(lReferenceKey);
        dtoGenotype.setCreateUser(dtoUser.getUserName());
        dtoGenotype.setCreateDate(dNow);
        dtoGenotype.setUpdateUser(dtoUser.getUserName());
        dtoGenotype.setUpdateDate(dNow);

        // set the custom data for the data model to display the correct data
        dtoGenotype.getDataBean().put(EIConstants.STRAIN_DTO, dtoStrain);
        dtoGenotype.getDataBean().put(EIConstants.JNUM, strJNumber);

        // add it to the table
        tblmdlGenotype.addRow(dtoGenotype);

        Utils.scrollToVisible(fxtblStrain, fxtblStrain.getRowCount() - 1, 0);
    }

    
    
   

    private void loadGenotype() {
        System.out.println("loading genotype");
        GenotypeDAO daoG = GenotypeDAO.getInstance();
        StrainDAO daoS = StrainDAO.getInstance();
        List<GenotypeDTO> listGenotypes = null;

        try {
            listGenotypes = daoG.loadByAllelePairKey(dtoAP.getAllelePairKey());

            for (GenotypeDTO dtoG : listGenotypes) {
                StrainDTO dtoS = daoS.loadByPrimaryKey(dtoG.getStrainKey());
                dtoG.getDataBean().put(EIConstants.STRAIN_DTO, dtoS);

                try {
                    String jnum = EIGlobals.getInstance().getJNumByRef(dtoG.getReferenceKey().longValue());
                    dtoG.getDataBean().put(EIConstants.JNUM, jnum);
                } catch (Exception e) {
                    Utils.log(e);
                }
            }
        } catch (Exception e) {
        }

        List<String> arrHeaders = new ArrayList<String>(4);
        arrHeaders.add("Key");
        arrHeaders.add("Name");
        arrHeaders.add("Description");
        arrHeaders.add("JNumber");

        GenotypeStrainsDTOTableModel<GenotypeDTO> sdtm = new GenotypeStrainsDTOTableModel<GenotypeDTO>(listGenotypes, arrHeaders);
        fxtblStrain.setModel(sdtm);
        pnlStrain.revalidate();
    }

    private void loadTumorAssoc() {
        TumorGeneticsDAO daoA = TumorGeneticsDAO.getInstance();
        List<TumorGeneticsDTO> arrTFAssoc = new ArrayList<TumorGeneticsDTO>();

        try {
            arrTFAssoc = daoA.loadByAllelePairKey(dtoAP.getAllelePairKey());
        } catch (Exception e) {
        }

        List<String> arrHeaders = new ArrayList<String>();
        arrHeaders.add("T.F. KEY");

        TumorGeneticsDTOTableModel<TumorGeneticsDTO> sdtmA = new TumorGeneticsDTOTableModel<TumorGeneticsDTO>(arrTFAssoc, arrHeaders);
        fxtblTumorFrequency.setModel(sdtmA);
        pnlTumorFrequency.revalidate();
    }


    /**
     * Insert the strain information and associated data in the database.
     * <p>
     * This is an all or nothing insert.  Either everything the user has
     * filled in gets comitted to the database or nothing does.
     */
    private void insertData() {
        AllelePairDAO daoAllelePair = AllelePairDAO.getInstance();
        GenotypeDAO daoGenotype = GenotypeDAO.getInstance();
        TumorGeneticsDAO daoTumorGenetics = TumorGeneticsDAO.getInstance();

        boolean commit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the allele pair
            ///////////////////////////////////////////////////////////////////
            // populate the strain
            // strain key
            boolean auto = checkboxAutoAssign.isSelected();
            String keyText = txtAllelePairKey.getText();
            long key = -1;


            dtoAP = daoAllelePair.createAllelePairDTO();
            
            // TODO check for existing pair with same order or reversed order of alleles


            if (auto) {
                // get the max value
            } else {
                key = Long.parseLong(keyText);
                dtoAP.setAllelePairKey(key);
            }


            // allele 1
            dtoAP.setAllele1Key(dtoAllele1.getAlleleKey());

            // allele2
            if (dtoAllele2 != null) {
                dtoAP.setAllele2Key(dtoAllele2.getAlleleKey());
            }

            // set the sequence
            dtoAP.setSequence(1);

            // add the audit trail
            dtoAP.setCreateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoAP.setCreateDate(new Date());
            dtoAP.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            dtoAP.setUpdateDate(new Date());

            dtoAP = daoAllelePair.save(dtoAP);

            ///////////////////////////////////////////////////////////////////
            // save the associated strain information
            ///////////////////////////////////////////////////////////////////
            GenotypeStrainsDTOTableModel<GenotypeDTO> modelG = (GenotypeStrainsDTOTableModel<GenotypeDTO>)fxtblStrain.getModel();
            List<GenotypeDTO> arrGenotypes = modelG.getAllData();
            if (arrGenotypes != null) {
                for (GenotypeDTO dto : arrGenotypes) {
                    dto.setAllelePairKey(dtoAP.getAllelePairKey());
                }
                daoGenotype.save(arrGenotypes);
            }

          
            
            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            commit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog(e.getMessage(), e);
            Utils.log(e);
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to add Allele Pair.", e2);
                e2.printStackTrace();
            }
            if (commit) {
                switchFromAddToEdit();
            } else {
                Utils.showErrorDialog("Unable to add Allele Pair.");
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
        AllelePairDAO daoAP = AllelePairDAO.getInstance();
        GenotypeDAO daoGenotype = GenotypeDAO.getInstance();
        TumorGeneticsDAO daoAssoc =
                TumorGeneticsDAO.getInstance();

        boolean bCommit = false;

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the associated strain information
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving associated strain information...");
            GenotypeStrainsDTOTableModel<GenotypeDTO> tblmdlGenotype =
                    (GenotypeStrainsDTOTableModel<GenotypeDTO>)fxtblStrain.getModel();
            List<GenotypeDTO> arrGenotypes = tblmdlGenotype.getAllData();
            daoGenotype.save(arrGenotypes);
            System.out.println("Associated strain information saved!");

          
            
            ///////////////////////////////////////////////////////////////////
            // save the allele pair
            ///////////////////////////////////////////////////////////////////
            updateProgress("Saving allele pair data...");
            String tempS = txtAllelePairKey.getText();

            // allele pair
            dtoAP.setAllele1Key(dtoAllele1.getAlleleKey());

            if (dtoAllele2 != null) {
                dtoAP.setAllele2Key(dtoAllele2.getAlleleKey());
            }

            // audit trail information
            MTBUsersDTO user = EIGlobals.getInstance().getMTBUsersDTO();
            dtoAP.setUpdateUser(user.getUserName());
            dtoAP.setUpdateDate(new Date());

            daoAP.save(dtoAP);
            updateProgress("Allele pair data saved!");

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            updateProgress("All allele pair information saved!");
            bCommit = true;

            //customInternalFrame.dispose();
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
                e2.printStackTrace();
                Utils.showErrorDialog("Unable to save changes to Allele Pair.", e2);
            }
            if (bCommit) {
                this.setKey(dtoAP.getAllelePairKey().longValue());
            } else {
                Utils.showErrorDialog("Unable to save changes to Allele Pair.");
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
        if (fxtblStrain.getCellEditor() != null) {
            fxtblStrain.getCellEditor().stopCellEditing();
        }

        if (fxtblTumorFrequency.getCellEditor() != null) {
            fxtblTumorFrequency.getCellEditor().stopCellEditing();
        }

        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try{
                    if (nType == ALLELE_PAIR_ADD) {
                        progressMonitor.start("Inserting Allele Pair...");
                        insertData();
                    } else if (nType == ALLELE_PAIR_EDIT) {
                        progressMonitor.start("Updating Allele Pair...");
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
    private void removeStrain() {
        int nRow = fxtblStrain.getSelectedRow();

        if (nRow >= 0) {
            MXDefaultTableModel tm =
                    (MXDefaultTableModel)fxtblStrain.getModel();
            tm.removeRow(nRow);
            updated = true;
        }
    }

   

    /**
     * Simple method to close the add form and switch to the edit form.  The
     * window location is tracked to make it seemless to the end user.
     */
    private void switchFromAddToEdit() {
        customInternalFrame.dispose();
        EIGlobals.getInstance().getMainFrame().launchGenotypeEditWindow(
                dtoAP.getAllelePairKey().longValue(),
                customInternalFrame.getLocation());
    }

    private void updateForm() {
        try {
            allelePanel1.setAlleleDTO(dtoAllele1);
        } catch (Exception e) {
            Utils.log(e);
        }
        try {
            allelePanel2.setAlleleDTO(dtoAllele2);
        } catch (Exception e) {
            Utils.log(e);
        }
        try {
            loadGenotype();
        } catch (Exception e) {
            Utils.log(e);
        }
        try {
            loadTumorAssoc();
        } catch (Exception e) {
            Utils.log(e);
        }
    }

    private AlleleDTO lookupAllele(long key) {
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

    private void checkGenes() {

    }


    private void lookupAllele1() {
        String alleleKeyText = txtAllele1Key.getText();

        long alleleKey = -1;

        try {
            alleleKey = Long.parseLong(alleleKeyText);
        } catch (Exception e) {
            Utils.showErrorDialog("Please enter a numeric Allele Key");
            txtAllele1Key.setText("");
            dtoAllele1 = null;
            txtAllele1Key.requestFocus();
            updateForm();
            return;
        }

        AlleleDTO dto = lookupAllele(alleleKey);

        if (dto == null) {
            Utils.showErrorDialog("Allele Key " + alleleKey + " does not exist!");
            txtAllele1Key.requestFocus();
            return;
        }

        checkGenes();

        this.dtoAllele1 = dto;
        updateForm();

    }

    private void lookupAllele2() {
        String alleleKeyText = txtAllele2Key.getText();

        long alleleKey = -1;

        try {
            alleleKey = Long.parseLong(alleleKeyText);
        } catch (Exception e) {
            Utils.showErrorDialog("Please enter a numeric Allele Key");
            txtAllele2Key.setText("");
            dtoAllele2 = null;
            txtAllele2Key.requestFocus();
            updateForm();
            return;
        }

        AlleleDTO dto = lookupAllele(alleleKey);

        if (dto == null) {
            Utils.showErrorDialog("Allele Key " + alleleKey + " does not exist!");
            txtAllele2Key.requestFocus();
            return;
        }

        checkGenes();

        this.dtoAllele2 = dto;
        updateForm();

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

        pnlAllelePair = new javax.swing.JPanel();
        headerPanelAllelePair = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblAllelePairKey = new javax.swing.JLabel();
        txtAllelePairKey = new javax.swing.JTextField();
        checkboxAutoAssign = new javax.swing.JCheckBox();
        allelePanel1 = new org.jax.mgi.mtb.ei.panels.AllelePanel();
        allelePanel2 = new org.jax.mgi.mtb.ei.panels.AllelePanel();
        lblAllele1Key = new javax.swing.JLabel();
        lblAllele2Key = new javax.swing.JLabel();
        txtAllele1Key = new javax.swing.JTextField();
        txtAllele2Key = new javax.swing.JTextField();
        separatorEdit = new javax.swing.JSeparator();
        btnLookupAllele2 = new javax.swing.JButton();
        btnLookupAllele1 = new javax.swing.JButton();
        pnlStrain = new javax.swing.JPanel();
        headerPanelStrain = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblStrainKey = new javax.swing.JLabel();
        txtStrainKey = new javax.swing.JTextField();
        txtJNumber = new javax.swing.JTextField();
        lblJNumber = new javax.swing.JLabel();
        jspStrain = new javax.swing.JScrollPane();
        tblStrain = new javax.swing.JTable();
        btnAddStrain = new javax.swing.JButton();
        pnlTumorFrequency = new javax.swing.JPanel();
        headerPanelTumorFrequency = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        jspTumorFrequency = new javax.swing.JScrollPane();
        tblTumorFrequency = new javax.swing.JTable();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        pnlAllelePair.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        headerPanelAllelePair.setDrawSeparatorUnderneath(true);
        headerPanelAllelePair.setText("Allele Pair Information");

        lblAllelePairKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblAllelePairKey.setText("Allele Pair Key");

        txtAllelePairKey.setColumns(10);
        txtAllelePairKey.setEditable(false);

        checkboxAutoAssign.setSelected(true);
        checkboxAutoAssign.setText("Auto Assign");
        checkboxAutoAssign.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkboxAutoAssign.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkboxAutoAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxAutoAssignActionPerformed(evt);
            }
        });

        allelePanel1.setTitle("Allele 1 Information");

        allelePanel2.setTitle("Allele 2 Information");

        lblAllele1Key.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblAllele1Key.setText("Allele 1 Key");

        lblAllele2Key.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblAllele2Key.setText("Allele 2 Key");

        txtAllele1Key.setColumns(10);

        txtAllele2Key.setColumns(10);

        btnLookupAllele2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
        btnLookupAllele2.setText("Lookup");
        btnLookupAllele2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupAllele2ActionPerformed(evt);
            }
        });

        btnLookupAllele1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"))); // NOI18N
        btnLookupAllele1.setText("Lookup");
        btnLookupAllele1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupAllele1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlAllelePairLayout = new org.jdesktop.layout.GroupLayout(pnlAllelePair);
        pnlAllelePair.setLayout(pnlAllelePairLayout);
        pnlAllelePairLayout.setHorizontalGroup(
            pnlAllelePairLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAllelePairLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlAllelePairLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlAllelePairLayout.createSequentialGroup()
                        .add(lblAllele1Key)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtAllele1Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnLookupAllele1))
                    .add(allelePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 418, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAllelePairLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlAllelePairLayout.createSequentialGroup()
                        .add(lblAllele2Key)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtAllele2Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnLookupAllele2)
                        .addContainerGap(217, Short.MAX_VALUE))
                    .add(allelePanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)))
            .add(headerPanelAllelePair, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
            .add(pnlAllelePairLayout.createSequentialGroup()
                .addContainerGap()
                .add(separatorEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 894, Short.MAX_VALUE)
                .addContainerGap())
            .add(pnlAllelePairLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblAllelePairKey)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtAllelePairKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkboxAutoAssign)
                .addContainerGap(641, Short.MAX_VALUE))
        );
        pnlAllelePairLayout.setVerticalGroup(
            pnlAllelePairLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAllelePairLayout.createSequentialGroup()
                .add(headerPanelAllelePair, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAllelePairLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblAllelePairKey)
                    .add(txtAllelePairKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkboxAutoAssign))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(separatorEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAllelePairLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlAllelePairLayout.createSequentialGroup()
                        .add(pnlAllelePairLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAllele1Key)
                            .add(txtAllele1Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(btnLookupAllele1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(allelePanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlAllelePairLayout.createSequentialGroup()
                        .add(pnlAllelePairLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblAllele2Key)
                            .add(txtAllele2Key, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(btnLookupAllele2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(allelePanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnlStrain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        headerPanelStrain.setDrawSeparatorUnderneath(true);
        headerPanelStrain.setText("Strain Associations");

        lblStrainKey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
        lblStrainKey.setText("Strain Key");

        txtStrainKey.setColumns(10);

        txtJNumber.setColumns(10);
        txtJNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtJNumberFocusLost(evt);
            }
        });

        lblJNumber.setText("J Number");

        tblStrain.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspStrain.setViewportView(tblStrain);

        btnAddStrain.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"))); // NOI18N
        btnAddStrain.setText("Add");
        btnAddStrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStrainActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlStrainLayout = new org.jdesktop.layout.GroupLayout(pnlStrain);
        pnlStrain.setLayout(pnlStrainLayout);
        pnlStrainLayout.setHorizontalGroup(
            pnlStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
            .add(pnlStrainLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblStrainKey))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblJNumber)
                    .add(pnlStrainLayout.createSequentialGroup()
                        .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAddStrain)))
                .addContainerGap(254, Short.MAX_VALUE))
            .add(pnlStrainLayout.createSequentialGroup()
                .addContainerGap()
                .add(jspStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlStrainLayout.setVerticalGroup(
            pnlStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStrainLayout.createSequentialGroup()
                .add(headerPanelStrain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblStrainKey)
                    .add(lblJNumber))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStrainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtStrainKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtJNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnAddStrain))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspStrain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTumorFrequency.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        headerPanelTumorFrequency.setDrawSeparatorUnderneath(true);
        headerPanelTumorFrequency.setText("Tumor Frequency Associations");

        tblTumorFrequency.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jspTumorFrequency.setViewportView(tblTumorFrequency);

        org.jdesktop.layout.GroupLayout pnlTumorFrequencyLayout = new org.jdesktop.layout.GroupLayout(pnlTumorFrequency);
        pnlTumorFrequency.setLayout(pnlTumorFrequencyLayout);
        pnlTumorFrequencyLayout.setHorizontalGroup(
            pnlTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelTumorFrequency, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
            .add(pnlTumorFrequencyLayout.createSequentialGroup()
                .addContainerGap()
                .add(jspTumorFrequency, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTumorFrequencyLayout.setVerticalGroup(
            pnlTumorFrequencyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTumorFrequencyLayout.createSequentialGroup()
                .add(headerPanelTumorFrequency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(59, 59, 59)
                .add(jspTumorFrequency, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addContainerGap())
        );

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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlAllelePair, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(btnSave)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(pnlTumorFrequency, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(pnlStrain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlAllelePair, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(pnlStrain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlTumorFrequency, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkboxAutoAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxAutoAssignActionPerformed
        if (checkboxAutoAssign.isSelected()) {
            txtAllelePairKey.setEditable(false);
            txtAllelePairKey.setText("");
        } else {
            txtAllelePairKey.setEditable(true);
        }
    }//GEN-LAST:event_checkboxAutoAssignActionPerformed

    private void btnLookupAllele2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupAllele2ActionPerformed
        lookupAllele2();
    }//GEN-LAST:event_btnLookupAllele2ActionPerformed

    private void btnLookupAllele1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupAllele1ActionPerformed
        lookupAllele1();
    }//GEN-LAST:event_btnLookupAllele1ActionPerformed

    private void btnAddStrainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStrainActionPerformed
        addStrain();
    }//GEN-LAST:event_btnAddStrainActionPerformed

    private void txtJNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJNumberFocusLost
        Utils.fixJNumber(txtJNumber);
    }//GEN-LAST:event_txtJNumberFocusLost

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jax.mgi.mtb.ei.panels.AllelePanel allelePanel1;
    private org.jax.mgi.mtb.ei.panels.AllelePanel allelePanel2;
    private javax.swing.JButton btnAddStrain;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLookupAllele1;
    private javax.swing.JButton btnLookupAllele2;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox checkboxAutoAssign;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelAllelePair;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelStrain;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelTumorFrequency;
    private javax.swing.JScrollPane jspStrain;
    private javax.swing.JScrollPane jspTumorFrequency;
    private javax.swing.JLabel lblAllele1Key;
    private javax.swing.JLabel lblAllele2Key;
    private javax.swing.JLabel lblAllelePairKey;
    private javax.swing.JLabel lblJNumber;
    private javax.swing.JLabel lblStrainKey;
    private javax.swing.JPanel pnlAllelePair;
    private javax.swing.JPanel pnlStrain;
    private javax.swing.JPanel pnlTumorFrequency;
    private javax.swing.JSeparator separatorEdit;
    private javax.swing.JTable tblStrain;
    private javax.swing.JTable tblTumorFrequency;
    private javax.swing.JTextField txtAllele1Key;
    private javax.swing.JTextField txtAllele2Key;
    private javax.swing.JTextField txtAllelePairKey;
    private javax.swing.JTextField txtJNumber;
    private javax.swing.JTextField txtStrainKey;
    // End of variables declaration//GEN-END:variables

}
