/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/AgentsPanel.java,v 1.1 2007/04/30 15:50:52 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jax.mgi.mtb.dao.custom.SearchResults;
import org.jax.mgi.mtb.dao.custom.mtb.MTBStrainTumorSummaryDTO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.param.StrainSearchParams;
import org.jax.mgi.mtb.dao.custom.mtb.param.TumorFrequencySearchParams;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.ei.util.Utils;


/*
 * AllelePairs not associated w/ a strain or tumor frequency 
 * Alleles not assciated w/ an AllelePair
 * References w/o accession ids?
 * 
 * 
 *                     Is there a way to do some sort of search that would
                    spit back a report when a TF has a particular strain
                    and reference and that reference does not appear on
                    the strain info?

                    

                    Conversely is there a way to get a report of
                    references listed for a strain that do not appear in
                    any TF (or assoicated) records? 

 * 
 * 
 */
 
public class StatisticsPanel extends CustomPanel {

  // -------------------------------------------------------------- Constants
  // none

  // ----------------------------------------------------- Instance Variables


  // ----------------------------------------------------------- Constructors
  /**
   * Creates new form for <b>Statistics</b> data.
   */
  public StatisticsPanel() {
    initComponents();
    initCustom();
  }

  // --------------------------------------------------------- Public Methods
  // ------------------------------------------------------ Protected Methods
  // none

  // -------------------------------------------------------- Private Methods
  /**
   * Custom initilization of all GUI objects.
   */
  private void initCustom() {

    try {
    } catch (Exception e) {
      Utils.log(e);
    }
  }

  private void getTFStatsAction() {

    MTBTumorUtilDAO daoTumorUtil = MTBTumorUtilDAO.getInstance();
    boolean stop = false;

    // create the collection of matching strains to return
    SearchResults<MTBStrainTumorSummaryDTO> res = null;
    try {
      // search for the strains
      TumorFrequencySearchParams tfParams =
              new TumorFrequencySearchParams();

      // these are the default params passed to the query when no
      // search criteria are selected on the tumor search form in the WI
      tfParams.setOrgansAffected(new ArrayList<String>());
      tfParams.setOrgansOrigin(new ArrayList<String>());
      tfParams.setTumorClassifications(new ArrayList<String>());
      tfParams.setAgent("");
      tfParams.setAgentTypeKey(-1);
      tfParams.setRestrictToMetastasis(false);
      tfParams.setMustHaveImages(false);
      tfParams.setExcludeMets(false);
      tfParams.setExcludePlasias(false);
      tfParams.setTumorName("");
      tfParams.setReferenceKey(0);
      tfParams.setAnatomicalSystemOriginName(null);
      tfParams.setOrganOriginName(null);
      tfParams.setOrganOriginNameComparison(2);
      tfParams.setOrganAffectedName(null);
      tfParams.setOrganAffectedNameComparison(2);
      tfParams.setOrganOriginKey(-1);
      tfParams.setOrganOriginParentKey(-1);
      tfParams.setAllelePairKeys(null);
      tfParams.setColonySize(-1);
      tfParams.setColonySizeComparison(2);
      tfParams.setFrequency(-1.0);
      tfParams.setFrequencyComparison(2);

      StrainSearchParams strainParams = new StrainSearchParams();
      strainParams.setStrainKey(-1);
      strainParams.setStrainFamilyKey(-1);
      strainParams.setStrainTypes(new ArrayList<String>());
      strainParams.setExactStrainTypes(false);
      strainParams.setStrainKeyComparison("=");

      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

      if (txtStartDate.getText().equals("MM/DD/YYYY")) {
        txtStartDate.setText("");
      }
      if (txtEndDate.getText().equals("MM/DD/YYYY")) {
        txtEndDate.setText("");
      }

      Date startDate = null;
      Date endDate = null;

      if (txtStartDate.getText().trim().length() > 0) {
        try {
          startDate = sdf.parse(txtStartDate.getText());
        } catch (ParseException pe) {
          stop = true;
          txtStartDate.setText("Invalid date!");
        }
      }

      if (txtEndDate.getText().trim().length() > 0) {
        try {
          endDate = sdf.parse(txtEndDate.getText());
        } catch (ParseException pe) {
          stop = true;
          txtEndDate.setText("Invalid date!");
        }
      }

      if (stop) {
        return;
      }

      res = daoTumorUtil.searchNewSummary(tfParams, strainParams, "organ", -1L, startDate, endDate);

      txtInstances.setText(res.getTotal() + "");
      txtTotal.setText(res.getAncillaryTotal() + "");


    } catch (Exception e) {
      e.printStackTrace();
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

    pnlCreate = new javax.swing.JPanel();
    lblStartDate = new javax.swing.JLabel();
    txtStartDate = new javax.swing.JTextField();
    lblInstances = new javax.swing.JLabel();
    txtInstances = new javax.swing.JTextField();
    btnAdd = new javax.swing.JButton();
    headerPanelCreate = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    lblEndDate = new javax.swing.JLabel();
    txtEndDate = new javax.swing.JTextField();
    lblTotal = new javax.swing.JLabel();
    txtTotal = new javax.swing.JTextField();
    btnCancel = new javax.swing.JButton();
    pnlEdit = new javax.swing.JPanel();
    headerPanelEdit = new org.jax.mgi.mtb.gui.MXHeaderPanel();
    jLabel1 = new javax.swing.JLabel();

    pnlCreate.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    lblStartDate.setText("Start Date");

    txtStartDate.setColumns(10);
    txtStartDate.setText("YYYY-MM-DD");

    lblInstances.setText("Instances");

    txtInstances.setEditable(false);

    btnAdd.setText("Calculate");
    btnAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnGetTFStatsActionPerformed(evt);
      }
    });

    headerPanelCreate.setDrawSeparatorUnderneath(true);
    headerPanelCreate.setText("Tumor Frequency Instances and Totals");

    lblEndDate.setText("End Date");

    txtEndDate.setColumns(10);
    txtEndDate.setText("YYYY-MM-DD");

    lblTotal.setText("Total");

    txtTotal.setEditable(false);

    org.jdesktop.layout.GroupLayout pnlCreateLayout = new org.jdesktop.layout.GroupLayout(pnlCreate);
    pnlCreate.setLayout(pnlCreateLayout);
    pnlCreateLayout.setHorizontalGroup(
      pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
      .add(pnlCreateLayout.createSequentialGroup()
        .addContainerGap()
        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblStartDate)
          .add(lblInstances))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
          .add(txtStartDate)
          .add(txtInstances, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 51, Short.MAX_VALUE)
        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
          .add(lblEndDate)
          .add(lblTotal))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
          .add(txtTotal)
          .add(txtEndDate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
        .add(89, 89, 89)
        .add(btnAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(32, 32, 32))
    );
    pnlCreateLayout.setVerticalGroup(
      pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlCreateLayout.createSequentialGroup()
        .add(headerPanelCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(8, 8, 8)
        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(btnAdd)
          .add(lblEndDate)
          .add(lblStartDate)
          .add(txtStartDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtEndDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(pnlCreateLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
          .add(lblTotal)
          .add(lblInstances)
          .add(txtInstances, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
          .add(txtTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    btnAdd.getAccessibleContext().setAccessibleName("");

    btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png"))); // NOI18N
    btnCancel.setText("Close");
    btnCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCloseActionPerformed(evt);
      }
    });

    pnlEdit.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    headerPanelEdit.setDrawSeparatorUnderneath(true);
    headerPanelEdit.setText("More Stats");

    jLabel1.setText("More to be determined");

    org.jdesktop.layout.GroupLayout pnlEditLayout = new org.jdesktop.layout.GroupLayout(pnlEdit);
    pnlEdit.setLayout(pnlEditLayout);
    pnlEditLayout.setHorizontalGroup(
      pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
      .add(pnlEditLayout.createSequentialGroup()
        .add(30, 30, 30)
        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    pnlEditLayout.setVerticalGroup(
      pnlEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(pnlEditLayout.createSequentialGroup()
        .add(headerPanelEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(18, 18, 18)
        .add(jLabel1)
        .add(222, 222, 222))
    );

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(org.jdesktop.layout.GroupLayout.LEADING, pnlEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .add(btnCancel))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
      .add(layout.createSequentialGroup()
        .addContainerGap()
        .add(pnlCreate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        .add(31, 31, 31)
        .add(pnlEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
        .add(btnCancel)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
      customInternalFrame.dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnGetTFStatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetTFStatsActionPerformed
      getTFStatsAction();
}//GEN-LAST:event_btnGetTFStatsActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAdd;
  private javax.swing.JButton btnCancel;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelCreate;
  private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelEdit;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel lblEndDate;
  private javax.swing.JLabel lblInstances;
  private javax.swing.JLabel lblStartDate;
  private javax.swing.JLabel lblTotal;
  private javax.swing.JPanel pnlCreate;
  private javax.swing.JPanel pnlEdit;
  private javax.swing.JTextField txtEndDate;
  private javax.swing.JTextField txtInstances;
  private javax.swing.JTextField txtStartDate;
  private javax.swing.JTextField txtTotal;
  // End of variables declaration//GEN-END:variables
}
