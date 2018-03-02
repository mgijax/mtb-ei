/*
 * MainMenuRollupPanel.java
 *
 * Created on December 13, 2005, 6:30 AM
 */

package org.jax.mgi.mtb.ei.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import org.jax.mgi.mtb.ei.gui.MainFrame;
import org.jax.mgi.mtb.gui.MXHyperlink;
import us.jawsoft.gui.JawRollupPanel;
import us.jawsoft.gui.RollupPanel.RollupPanelBar;
import us.jawsoft.gui.layout.JawVerticalFlowLayout;

/**
 *
 * @author  mjv
 */
public class MainMenuRollupPanel extends javax.swing.JPanel {

    private MainFrame mainFrame = null;

    /** Creates new form MainMenuRollupPanel */
    public MainMenuRollupPanel(MainFrame mf) {
        this.mainFrame = mf;
        initComponents();
        initCustom();
    }

    private void initCustom() {
        try{
            RollupPanelBar bar = new RollupPanelBar();
            bar.setLayout(new JawVerticalFlowLayout(10, 3));

            ImageIcon iconNew = new ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Add16.png"));
            ImageIcon iconEdit = new ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Edit16.png"));
            ImageIcon iconSearch = new ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/search16x16.png"));
            ImageIcon iconAbout = new ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/About16.png"));

            ///////////////////////////////////////////////////////////////////
            // Strain Panel
            ///////////////////////////////////////////////////////////////////
            JawRollupPanel pnlStrain = new JawRollupPanel("Strains");

            MXHyperlink btnStrainSearch = new MXHyperlink("Search Strains", iconSearch, null);
            btnStrainSearch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchStrainSearchWindow();
                }
            });
            pnlStrain.add(btnStrainSearch);

            MXHyperlink btnStrainNew = new MXHyperlink("New Strain", iconNew, null);
            btnStrainNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchStrainAddWindow();
                }
            });
            pnlStrain.add(btnStrainNew);

            pnlStrain.setTheme(JawRollupPanel.THEME_BLUE);

            bar.add(pnlStrain);

            ///////////////////////////////////////////////////////////////////
            // Tumor Panel
            ///////////////////////////////////////////////////////////////////
            JawRollupPanel pnlTumor = new JawRollupPanel("Tumors");

            MXHyperlink btnTumorSearch = new MXHyperlink("Search Tumors", iconSearch, null);
            btnTumorSearch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchTumorFrequencySearchWindow();
                }
            });
            pnlTumor.add(btnTumorSearch);

            MXHyperlink btnTumorNew = new MXHyperlink("New Tumor", iconNew, null);
            btnTumorNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchTumorFrequencyAddWindow();
                }
            });
            pnlTumor.add(btnTumorNew);

            pnlTumor.setTheme(JawRollupPanel.THEME_BLUE);

            bar.add(pnlTumor);

            ///////////////////////////////////////////////////////////////////
            // Reference Panel
            ///////////////////////////////////////////////////////////////////
            JawRollupPanel pnlReference = new JawRollupPanel("Reference");

            MXHyperlink btnReferenceSearch = new MXHyperlink("Search References", iconSearch, null);
            btnReferenceSearch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchReferenceSearchWindow();
                }
            });
            pnlReference.add(btnReferenceSearch);

            MXHyperlink btnReferenceAdd = new MXHyperlink("Add Reference", iconNew, null);
            btnReferenceAdd.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchReferenceAddWindow();
                }
            });
            pnlReference.add(btnReferenceAdd);

            pnlReference.setTheme(JawRollupPanel.THEME_BLUE);

            bar.add(pnlReference);

            ///////////////////////////////////////////////////////////////////
            // Pathology Panel
            ///////////////////////////////////////////////////////////////////
            JawRollupPanel pnlPathology = new JawRollupPanel("Pathology");

            MXHyperlink btnPathologySearch = new MXHyperlink("Search Pathology Report", iconSearch, null);
            btnPathologySearch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchPathologySearchWindow();
                }
            });
            pnlPathology.add(btnPathologySearch);

            MXHyperlink btnPathologyNew = new MXHyperlink("New Pathology Report", iconNew, null);
            btnPathologyNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchPathologyAddWindow();
                }
            });
            pnlPathology.add(btnPathologyNew);

            pnlPathology.setTheme(JawRollupPanel.THEME_BLUE);

            bar.add(pnlPathology);

            ///////////////////////////////////////////////////////////////////
            // Genotypes Panel
            ///////////////////////////////////////////////////////////////////
            JawRollupPanel pnlGenotype = new JawRollupPanel("Genotypes");

            MXHyperlink btnGenotypesMarkerSearch = new MXHyperlink("Search Markers", iconSearch, null);
            btnGenotypesMarkerSearch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchMarkerSearchWindow();
                }
            });
            pnlGenotype.add(btnGenotypesMarkerSearch);

            MXHyperlink btnGenotypesMarkerNew = new MXHyperlink("New Marker", iconNew, null);
            btnGenotypesMarkerNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchMarkerAddWindow();
                }
            });
            pnlGenotype.add(btnGenotypesMarkerNew);

            MXHyperlink btnGenotypesAlleleSearch = new MXHyperlink("Search Alleles", iconSearch, null);
            btnGenotypesAlleleSearch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchAlleleSearchWindow();
                }
            });
            pnlGenotype.add(btnGenotypesAlleleSearch);

            MXHyperlink btnGenotypesAlleleNew = new MXHyperlink("New Allele", iconNew, null);
            btnGenotypesAlleleNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchAlleleAddWindow();
                }
            });
            pnlGenotype.add(btnGenotypesAlleleNew);

            MXHyperlink btnGenotypesAllelePairSearch = new MXHyperlink("Search Allele Pairs", iconSearch, null);
            btnGenotypesAllelePairSearch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchGenotypeSearchWindow();
                }
            });
            pnlGenotype.add(btnGenotypesAllelePairSearch);

            MXHyperlink btnGenotypesAllelePairsNew = new MXHyperlink("New Allele Pair", iconNew, null);
            btnGenotypesAllelePairsNew.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchGenotypeAddWindow();
                }
            });
            pnlGenotype.add(btnGenotypesAllelePairsNew);

            pnlGenotype.setTheme(JawRollupPanel.THEME_BLUE);

            bar.add(pnlGenotype);

            ///////////////////////////////////////////////////////////////////
            // Controlled Vocabularies Panel
            ///////////////////////////////////////////////////////////////////
            JawRollupPanel pnlVocabs = new JawRollupPanel("Controlled Vocabularies");

            MXHyperlink btnAgentsEdit = new MXHyperlink("Agents", iconEdit, null);
            btnAgentsEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchAgentWindow();
                }
            });
            pnlVocabs.add(btnAgentsEdit);

            MXHyperlink btnAgentTypesEdit = new MXHyperlink("Agent Types", iconEdit, null);
            btnAgentTypesEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchAgentTypeWindow();
                }
            });
            pnlVocabs.add(btnAgentTypesEdit);

            MXHyperlink btnAnatomicalSystemsEdit = new MXHyperlink("Anatomical Systems", iconEdit, null);
            btnAnatomicalSystemsEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchAnatomicalSystemWindow();
                }
            });
            pnlVocabs.add(btnAnatomicalSystemsEdit);

            MXHyperlink btnOrgansEdit = new MXHyperlink("Organs", iconEdit, null);
            btnOrgansEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchOrganWindow();
                }
            });
            pnlVocabs.add(btnOrgansEdit);

            MXHyperlink btnProbesEdit = new MXHyperlink("Probes", iconEdit, null);
            btnProbesEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchProbeWindow();
                }
            });
            pnlVocabs.add(btnProbesEdit);

            MXHyperlink btnStrainFamiliesEdit = new MXHyperlink("Strain Families", iconEdit, null);
            btnStrainFamiliesEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchStrainFamilyEditWindow();
                }
            });
            pnlVocabs.add(btnStrainFamiliesEdit);

            MXHyperlink btnStrainTypesEdit = new MXHyperlink("Strain Types", iconEdit, null);
            btnStrainTypesEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchStrainTypeWindow();
                }
            });
            pnlVocabs.add(btnStrainTypesEdit);

            MXHyperlink btnTumorClassificationsEdit = new MXHyperlink("Tumor Classifications", iconEdit, null);
            btnTumorClassificationsEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchTumorClassificationWindow();
                }
            });
            pnlVocabs.add(btnTumorClassificationsEdit);

            MXHyperlink btnTumorTypesEdit = new MXHyperlink("Tumor Names", iconEdit, null);
            btnTumorTypesEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchTumorTypeWindow();
                }
            });
            pnlVocabs.add(btnTumorTypesEdit);

            pnlVocabs.setTheme(JawRollupPanel.THEME_BLUE);
            pnlVocabs.save();

            bar.add(pnlVocabs);


            ///////////////////////////////////////////////////////////////////
            // Misc. Panel
            ///////////////////////////////////////////////////////////////////
            JawRollupPanel pnlMisc = new JawRollupPanel("Miscellaneous");
            
            MXHyperlink btnSeriesEdit = new MXHyperlink("Series", iconEdit, null);
            btnSeriesEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchSeriesWindow();
                }
            });
            pnlMisc.add(btnSeriesEdit);
            
            MXHyperlink btnSampleAssocEdit = new MXHyperlink("Samples", iconEdit, null);
            btnSampleAssocEdit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchSampleAssocWindow(null);
                }
            });
            pnlMisc.add(btnSampleAssocEdit);
            
            
            MXHyperlink btnSyncAlleles = new MXHyperlink("Sync Alleles", iconSearch, null);
            btnSyncAlleles.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchAlleleSyncWindow();
                }
            });
            pnlMisc.add(btnSyncAlleles);
            
            
            MXHyperlink btnSyncMarkers = new MXHyperlink("Sync Markers", iconSearch, null);
            btnSyncMarkers.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchMarkerSyncWindow();
                }
            });
            pnlMisc.add(btnSyncMarkers);
            
            
            MXHyperlink btnStatistics = new MXHyperlink("Statistics", iconSearch, null);
            btnStatistics.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchStatisticsWindow();
                }
            });
            pnlMisc.add(btnStatistics);
            

            MXHyperlink btnSqlWindow = new MXHyperlink("SQL", iconEdit, null);
            btnSqlWindow.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainFrame.launchSqlWindow();
                }
            });
            pnlMisc.add(btnSqlWindow);
            pnlMisc.setTheme(JawRollupPanel.THEME_BLUE);
            pnlMisc.save();

            bar.add(pnlMisc);

            ///////////////////////////////////////////////////////////////////

            bar.setBackground(new Color(100,100,255));

            JScrollPane sp = new JScrollPane(bar);

            sp.setBorder(null);
            sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            this.add(sp, BorderLayout.CENTER);

        } catch (Exception e) {
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    setLayout(new java.awt.BorderLayout());
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables

}
