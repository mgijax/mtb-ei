/*
 * AlleleMGISyncPanel.java
 *
 * Created on July 13, 2009, 11:15 AM
 */
package org.jax.mgi.mtb.ei.panels;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JLabel;
import org.jax.mgi.mtb.dao.custom.mtb.MTBSynchronizationUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleDTO;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXProgressGlassPane;

/**
 * Panel to synchronize Alleles between MGD and MTB
 * 
 * @author  sbn
 */
public class AlleleMGISyncPanel extends CustomPanel {
  private int dtoIndex = 0;
  private ArrayList<ArrayList<AlleleDTO>> dtos = null;
  
  /** Creates new form AlleleMGISyncPanel */
  public AlleleMGISyncPanel() {
    initComponents();
  }

  /**
   * Get all the pairs of alleles that don't match between MGD and MTB
   * This takes a while so block the frame with a progress pane
   * and use a worker thread so the EI doesn't lock up for the 
   * whole query.
   */
  private void loadAlleles(){
    Component compGlassPane = customInternalFrame.getGlassPane();
    MXProgressGlassPane progressGlassPane =
          new MXProgressGlassPane(customInternalFrame.getRootPane());
    customInternalFrame.setGlassPane(progressGlassPane);
    progressGlassPane.setVisible(true);
    progressGlassPane.setMessage("Comparing MTB alleles to MGI");

    final MTBSynchronizationUtilDAO dao = MTBSynchronizationUtilDAO.getInstance();

    dao.setMGIInfo(EIGlobals.getInstance().getMGIUser(),
          EIGlobals.getInstance().getMGIPassword(),
          EIGlobals.getInstance().getMGIDriver(),
          EIGlobals.getInstance().getMGIUrl());
    
    dtos = dao.getAllelesToSync();
               
    

    this.lblTotal.setText(dtos.size() + " non matching Alleles");

    progressGlassPane.setVisible(false);
    customInternalFrame.setGlassPane(compGlassPane);
    progressGlassPane = null;

    customInternalFrame.adjust();

    dtoIndex = 0;
    showAlleles(); 
  }
  
  /**
   * Update the allele dto from the fields in the EI
   * Update the database with the new Allle
   */
  private void update(){
     if(dtos != null){
     ArrayList<AlleleDTO> alleles = dtos.get(dtoIndex);

     AlleleDTO mtbAllele = alleles.get(1);
     mtbAllele.setName(txtMtbName.getText());
     mtbAllele.setNote(txtMtbNote.getText());
     mtbAllele.setSymbol(txtMtbSymbol.getText());
     mtbAllele.isNew(false);
     mtbAllele.setUpdateDate(new Date());
     mtbAllele.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
     AlleleDAO aDao = AlleleDAO.getInstance();
     try{
      aDao.save(mtbAllele);
     }catch(Exception e){
      Utils.showErrorDialog("Unable to update Allele.", e);
     }
    }
  }
  
  /**
   * Clear any highlighted fields
   * Get the pair of alleles and display their fields
   * Highlight any filds that don't match
   */
  private void showAlleles() {

    this.txtMgiName.setBackground(Color.WHITE);
    this.txtMtbName.setBackground(Color.WHITE);
    this.txtMgiSymbol.setBackground(Color.WHITE);
    this.txtMtbSymbol.setBackground(Color.WHITE);
    this.txtMgiNote.setBackground(Color.WHITE);
    this.txtMtbNote.setBackground(Color.WHITE);
    
    this.lblMGIAlleleStatus.setText("");
    
    
    if(dtos == null || dtos.size() == 0){
      return;
    }

    ArrayList<AlleleDTO> alleles = dtos.get(dtoIndex);
    AlleleDTO mgiAllele = alleles.get(0);
    AlleleDTO mtbAllele = alleles.get(1);

    this.lblAlleleKey.setText("  Key:"+mtbAllele.getAlleleKey().toString());
    this.lblMGIAlleleId.setText((String)mgiAllele.getDataBean().get(MTBSynchronizationUtilDAO.MGI_ALLELE_ID));


    if (!mgiAllele.getName().equals(mtbAllele.getName())) {
      this.txtMgiName.setBackground(Color.RED);
      this.txtMtbName.setBackground(Color.RED);
    }
    this.txtMgiName.setText(mgiAllele.getName());
    this.txtMtbName.setText(mtbAllele.getName());

    if (!mtbAllele.getSymbol().equals(
            MTBSynchronizationUtilDAO.fixSymbol(mgiAllele.getSymbol()))) {
      this.txtMgiSymbol.setBackground(Color.RED);
      this.txtMtbSymbol.setBackground(Color.RED);
    }

    this.txtMgiSymbol.setText(mgiAllele.getSymbol());
    this.txtMtbSymbol.setText(mtbAllele.getSymbol());

    if (mgiAllele.getNote() != null &&
            mgiAllele.getNote().trim().length() != 0 &&
            !mgiAllele.getNote().trim().equals(mtbAllele.getNote().trim())) {


      this.txtMgiNote.setBackground(Color.RED);
      this.txtMtbNote.setBackground(Color.RED);

    }
    
    this.txtMgiNote.setText(mgiAllele.getNote());
    this.txtMtbNote.setText(mtbAllele.getNote());

    this.lblCount.setHorizontalAlignment(JLabel.CENTER);
    this.lblCount.setText((dtoIndex + 1) + " of " + dtos.size());
    
    Boolean status = (Boolean) mgiAllele.getDataBean().get(MTBSynchronizationUtilDAO.MGI_ALLELE_STATUS_DELETED);
    if(status.booleanValue() == true){
      this.lblMGIAlleleStatus.setText("This allele is marked as deleted in MGI");
    }
  }
  
  /**
   * Decrement the index and display
   * the corresponding pair of alleles
   */
  private void prev(){
    if(dtos != null){
      dtoIndex--;
      // wrap the index from first to last
      if (dtoIndex < 0) {
        dtoIndex = dtos.size() - 1;
      }
      showAlleles();
    }
  }
  
  /**
   * Increment the index and display
   * the correspoding pair of alleles
   */
  private void next(){
    if(dtos != null){
      dtoIndex++;
      // wrap the index from last to first
      if (dtoIndex >= dtos.size()) {
        dtoIndex = 0;
      }
      showAlleles();
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanelAllele = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        btnCheck = new javax.swing.JButton();
        txtMgiName = new javax.swing.JTextField();
        txtMtbName = new javax.swing.JTextField();
        txtMgiSymbol = new javax.swing.JTextField();
        txtMtbSymbol = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        lblSymbol = new javax.swing.JLabel();
        lblNote = new javax.swing.JLabel();
        lblMgiAllele = new javax.swing.JLabel();
        lblMtbAllele = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtMgiNote = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtMtbNote = new javax.swing.JTextArea();
        btnUpdate = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        btnNext = new javax.swing.JButton();
        btnCopyName = new javax.swing.JButton();
        btnCopySymbol = new javax.swing.JButton();
        btnCopyNote = new javax.swing.JButton();
        lblAlleleKey = new javax.swing.JLabel();
        btnPrev = new javax.swing.JButton();
        lblCount = new javax.swing.JLabel();
        lblMGIAlleleId = new javax.swing.JLabel();
        lblMGIAlleleStatus = new javax.swing.JLabel();

        headerPanelAllele.setDrawSeparatorUnderneath(true);
        headerPanelAllele.setText("Allele MGI Synchronization");

        btnCheck.setText("Check Alleles");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAlleles(evt);
            }
        });

        txtMgiName.setEditable(false);

        txtMgiSymbol.setEditable(false);

        lblName.setText("Name");

        lblSymbol.setText("Symbol");

        lblNote.setText("Note");

        lblMgiAllele.setText("MGI Allele");

        lblMtbAllele.setText("MTB Allele");

        txtMgiNote.setColumns(20);
        txtMgiNote.setEditable(false);
        txtMgiNote.setLineWrap(true);
        txtMgiNote.setRows(5);
        jScrollPane3.setViewportView(txtMgiNote);

        txtMtbNote.setColumns(20);
        txtMtbNote.setLineWrap(true);
        txtMtbNote.setRows(5);
        jScrollPane4.setViewportView(txtMtbNote);

        btnUpdate.setText("Update MTB Allele");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateAllele(evt);
            }
        });

        btnNext.setText("Next");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextAllele(evt);
            }
        });

        btnCopyName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/StepForward16.png"))); // NOI18N
        btnCopyName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyName(evt);
            }
        });

        btnCopySymbol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/StepForward16.png"))); // NOI18N
        btnCopySymbol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySymbol(evt);
            }
        });

        btnCopyNote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/StepForward16.png"))); // NOI18N
        btnCopyNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyNote(evt);
            }
        });

        btnPrev.setText("Prev");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevAllele(evt);
            }
        });

        lblCount.setText(" ");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(btnCheck)
                        .add(18, 18, 18)
                        .add(lblTotal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(btnPrev))
                    .add(layout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblName)
                            .add(lblSymbol)
                            .add(lblNote))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblMGIAlleleStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtMgiName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtMgiSymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(lblMgiAllele)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblMGIAlleleId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(lblCount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, btnCopyNote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, btnCopySymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, btnCopyName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtMtbName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtMtbSymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnNext)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(lblMtbAllele)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblAlleleKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnUpdate)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
                .add(122, 122, 122))
            .add(headerPanelAllele, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 816, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(headerPanelAllele, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnNext)
                    .add(btnPrev)
                    .add(lblCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnCheck)
                    .add(lblTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMtbAllele)
                    .add(lblMgiAllele)
                    .add(lblAlleleKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblMGIAlleleId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName)
                    .add(txtMtbName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtMgiName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnCopyName))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSymbol)
                    .add(txtMtbSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtMgiSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnCopySymbol))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblNote))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(btnUpdate)
                            .add(lblMGIAlleleStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(97, 97, 97))
                    .add(layout.createSequentialGroup()
                        .add(btnCopyNote)
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents
  
private void checkAlleles(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAlleles
  loadAlleles();
}//GEN-LAST:event_checkAlleles

private void nextAllele(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextAllele
  next();
}//GEN-LAST:event_nextAllele

private void copyName(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyName
  this.txtMtbName.setText(this.txtMgiName.getText());
  this.txtMgiName.setBackground(Color.WHITE);
  this.txtMtbName.setBackground(Color.WHITE);

}//GEN-LAST:event_copyName

private void copySymbol(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copySymbol
  this.txtMtbSymbol.setText(
          MTBSynchronizationUtilDAO.fixSymbol(this.txtMgiSymbol.getText()));
  this.txtMgiSymbol.setBackground(Color.WHITE);
  this.txtMtbSymbol.setBackground(Color.WHITE);

}//GEN-LAST:event_copySymbol

private void copyNote(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyNote
  this.txtMtbNote.setText(this.txtMgiNote.getText());
  this.txtMgiNote.setBackground(Color.WHITE);
  this.txtMtbNote.setBackground(Color.WHITE);
}//GEN-LAST:event_copyNote

private void updateAllele(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateAllele
   update();
}//GEN-LAST:event_updateAllele

private void prevAllele(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevAllele
  prev();
}//GEN-LAST:event_prevAllele
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnCopyName;
    private javax.swing.JButton btnCopyNote;
    private javax.swing.JButton btnCopySymbol;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnUpdate;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelAllele;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblAlleleKey;
    private javax.swing.JLabel lblCount;
    private javax.swing.JLabel lblMGIAlleleId;
    private javax.swing.JLabel lblMGIAlleleStatus;
    private javax.swing.JLabel lblMgiAllele;
    private javax.swing.JLabel lblMtbAllele;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblSymbol;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTextField txtMgiName;
    private javax.swing.JTextArea txtMgiNote;
    private javax.swing.JTextField txtMgiSymbol;
    private javax.swing.JTextField txtMtbName;
    private javax.swing.JTextArea txtMtbNote;
    private javax.swing.JTextField txtMtbSymbol;
    // End of variables declaration//GEN-END:variables
}
