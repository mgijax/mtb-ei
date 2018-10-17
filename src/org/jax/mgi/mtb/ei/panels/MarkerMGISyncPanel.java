/*
 * markerMGISynch.java
 *
 * Created on July 13, 2009, 11:15 AM
 */
package org.jax.mgi.mtb.ei.panels;

import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import org.jax.mgi.mtb.dao.custom.mtb.MTBSynchronizationUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerDTO;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXProgressGlassPane;

/**
 *
 * @author sbn
 */
public class MarkerMGISyncPanel extends CustomPanel {
    
    private int dtoIndex = 0;
    private ArrayList<ArrayList<MarkerDTO>> dtos = null;

   
    /**
     * Creates new markerMGISynch panel
     */
    public MarkerMGISyncPanel() {
        initComponents();
    }

    /**
     * Get all the markers that don't match MGD
     */
    private void loadMarkers() {
        
        Component compGlassPane = customInternalFrame.getGlassPane();
        MXProgressGlassPane progressGlassPane
                = new MXProgressGlassPane(customInternalFrame.getRootPane());
        customInternalFrame.setGlassPane(progressGlassPane);
        progressGlassPane.setVisible(true);
        progressGlassPane.setMessage("Comparing MTB Markers to MGI");
        
        final MTBSynchronizationUtilDAO dao = MTBSynchronizationUtilDAO.getInstance();
        
        dao.setMGIInfo(EIGlobals.getInstance().getMGIUser(),
                EIGlobals.getInstance().getMGIPassword(),
                EIGlobals.getInstance().getMGIDriver(),
                EIGlobals.getInstance().getMGIUrl());
        try {
            Object obj = Worker.post(new Task() {
                
                public Object run() throws Exception {
                    dtos = dao.getMarkersToSync();
                    return "Done";
                }
            });
        } catch (Exception e) {
        }
        
        this.lblTotal.setText(dtos.size() + " non matching Markers");
        
        progressGlassPane.setVisible(false);
        customInternalFrame.setGlassPane(compGlassPane);
        progressGlassPane = null;
        
        customInternalFrame.adjust();
        
        dtoIndex = 0;
        showMarkers();
    }

    /**
     * Update the dto to reflect the fields in the ei Save the updated marker
     */
    private void update() {
        if (dtos != null) {
            ArrayList<MarkerDTO> markers = dtos.get(dtoIndex);
            
            MarkerDTO mtbMarker = markers.get(1);
            mtbMarker.setName(txtMtbName.getText());
            mtbMarker.setSymbol(txtMtbSymbol.getText());
            mtbMarker.isNew(false);
            mtbMarker.setUpdateDate(new Date());
            mtbMarker.setUpdateUser(EIGlobals.getInstance().getMTBUsersDTO().getUserName());
            MarkerDAO mDao = MarkerDAO.getInstance();
            try {
                mDao.save(mtbMarker);
            } catch (Exception e) {
                Utils.showErrorDialog("Unable to update marker.", e);
            }
            
            // update labels
            // what about duplicates? unless there is a wholesale remove and replace we need to find existing labels
            // remove all then add new? what about created_by updated_by?
            if(!MTBSynchronizationUtilDAO.getInstance().updateMarkerLabels(mtbMarker)){
                
                Utils.showErrorDialog("There was an error updating the marker labels");
            }
        }
    }

    /**
     * Increment the counter and show the corresponding markers
     */
    private void next() {
        if (dtos != null) {
            dtoIndex++;
            if (dtoIndex >= dtos.size()) {
                dtoIndex = 0;
            }
            showMarkers();
        }
    }

    /**
     * Decrement the counter and show the corresponding pair of markers
     */
    private void prev() {
        if (dtos != null) {
            dtoIndex--;
            if (dtoIndex < 0) {
                dtoIndex = dtos.size() - 1;
            }
            showMarkers();
        }
    }

    /**
     * Reset any higlighted fields Display the two markers and highlight any
     * discrepencies
     */
    private void showMarkers() {
        
        this.txtMgiName.setBackground(Color.WHITE);
        this.txtMtbName.setBackground(Color.WHITE);
        this.txtMgiSymbol.setBackground(Color.WHITE);
        this.txtMtbSymbol.setBackground(Color.WHITE);
        
        if (dtos == null || dtos.size() == 0) {
            return;
        }
        
        ArrayList<MarkerDTO> markers = dtos.get(dtoIndex);
        MarkerDTO mgiMarker = markers.get(0);
        MarkerDTO mtbMarker = markers.get(1);
        
        this.lblMarkerKey.setText(mtbMarker.getMarkerKey().toString());
        this.lblMGIMarkerID.setText((String) mgiMarker.getDataBean().get(MTBSynchronizationUtilDAO.MGI_MARKER_ID));
        
        if (!mgiMarker.getName().equals(mtbMarker.getName())) {
            this.txtMgiName.setBackground(Color.RED);
            this.txtMtbName.setBackground(Color.RED);
        }
        this.txtMgiName.setText(mgiMarker.getName());
        this.txtMtbName.setText(mtbMarker.getName());
        
        if (!mtbMarker.getSymbol().equals(
                MTBSynchronizationUtilDAO.fixSymbol(mgiMarker.getSymbol()))) {
            this.txtMgiSymbol.setBackground(Color.RED);
            this.txtMtbSymbol.setBackground(Color.RED);
        }
        
        this.txtMgiSymbol.setText(mgiMarker.getSymbol());
        this.txtMtbSymbol.setText(mtbMarker.getSymbol());
        
        this.jList1.setSelectionBackground(Color.red);
        
        ArrayList<String> mgiList = (ArrayList<String>) mgiMarker.getDataBean().get("synonyms");
        String[] mgiArray = new String[mgiList.size()];
        this.jList1.setListData(mgiList.toArray(mgiArray));
        
        ArrayList<String> mtbList = (ArrayList<String>) mtbMarker.getDataBean().get("synonyms");
        String[] mtbArray = new String[mtbList.size()];
        this.jList2.setListData(mtbList.toArray(mtbArray));
        
        ArrayList<Integer> newSyns = new ArrayList<>();
        for (int i = 0; i < mgiList.size(); i++) {
            if (!mtbList.contains(mgiList.get(i))) {
                newSyns.add(i);
            }
        }
        int[] newSynsArray = new int[newSyns.size()];
        for (int i = 0; i < newSyns.size(); i++) {
            newSynsArray[i] = newSyns.get(i);
        }
        this.jList1.setSelectedIndices(newSynsArray);
        
        this.lblCount.setHorizontalAlignment(JLabel.CENTER);
        this.lblCount.setText((dtoIndex + 1) + " of " + dtos.size());
        
    }
    
    private void copySynonyms(){
        
        ArrayList<MarkerDTO> markers = dtos.get(dtoIndex);
        MarkerDTO mtbMarker = markers.get(1);
        
        List<String> newSyns = this.jList1.getSelectedValuesList();
        
        ArrayList<String> mtbList = (ArrayList<String>) mtbMarker.getDataBean().get("synonyms");
        mtbList.addAll(newSyns); // this changes the data in the DTO rather than just the list, clicking away and back will show the new (not actual in DB) data with no indication it isn't in the DB
        dedupe(mtbList);
        String[] mtbArray = new String[mtbList.size()];
        this.jList2.setListData(mtbList.toArray(mtbArray));
        this.jList1.clearSelection();
    
       
        
    }
    
    private void addSynonym(String newSyn){
        ArrayList<MarkerDTO> markers = dtos.get(dtoIndex);
        MarkerDTO mtbMarker = markers.get(1);
        ArrayList<String> mtbList = (ArrayList<String>) mtbMarker.getDataBean().get("synonyms");
        mtbList.add(newSyn);
        dedupe(mtbList);
        String[] mtbArray = new String[mtbList.size()];
        this.jList2.setListData(mtbList.toArray(mtbArray));
    }
    
    private void dedupe(ArrayList<String> list){
        Collections.sort(list);
        int i = 0;
        while(i<list.size()-1){
            if(list.get(i).equals(list.get(i+1))){
                list.remove(i);
            }else{
                i++;
            }
            
        }
        
    }
    
    private void removeSynonyms(){
        int[] selected = jList2.getSelectedIndices();
        
        ArrayList<MarkerDTO> markers = dtos.get(dtoIndex);
        MarkerDTO mtbMarker = markers.get(1);
        
        ArrayList<String> mtbList = (ArrayList<String>) mtbMarker.getDataBean().get("synonyms");
        for(int i = selected.length; i > 0; i--){
            mtbList.remove(selected[i-1]);
        }
        
        String[] mtbArray = new String[mtbList.size()];
        this.jList2.setListData(mtbList.toArray(mtbArray));
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        lblMgiAllele = new javax.swing.JLabel();
        lblMtbAllele = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        btnNext = new javax.swing.JButton();
        btnCopyName = new javax.swing.JButton();
        btnCopySymbol = new javax.swing.JButton();
        lblMarkerKey = new javax.swing.JLabel();
        btnPrev = new javax.swing.JButton();
        lblCount = new javax.swing.JLabel();
        lblMGIMarkerID = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        btnCopySynonyms = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();

        headerPanelAllele.setDrawSeparatorUnderneath(true);
        headerPanelAllele.setText("Marker MGI Synchronization");

        btnCheck.setText("Check Markers");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkMarkers(evt);
            }
        });

        txtMgiName.setEditable(false);

        txtMgiSymbol.setEditable(false);

        lblName.setText("Name");

        lblSymbol.setText("Symbol");

        lblMgiAllele.setText("MGI Marker");

        lblMtbAllele.setText("MTB Marker");

        btnUpdate.setText("Update MTB Marker");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateMarker(evt);
            }
        });

        btnNext.setText("Next");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextMarker(evt);
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

        btnPrev.setText("Prev");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevMarker(evt);
            }
        });

        lblCount.setText(" ");

        jScrollPane1.setViewportView(jList1);

        jScrollPane2.setViewportView(jList2);

        jLabel1.setText("Synonyms");

        btnCopySynonyms.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/StepForward16.png"))); // NOI18N
        btnCopySynonyms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopySynonyms(evt);
            }
        });

        jButtonRemove.setText("Remove Selected Synonyms");
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelAllele, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(btnCheck)
                        .add(18, 18, 18)
                        .add(lblTotal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(btnPrev))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblName)
                            .add(lblSymbol)
                            .add(jLabel1))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtMgiName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtMgiSymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(lblMgiAllele)
                                .add(15, 15, 15)
                                .add(lblMGIMarkerID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(0, 0, Short.MAX_VALUE))
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(lblCount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, btnCopySynonyms, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(btnCopySymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(btnCopyName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jButtonRemove)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(btnUpdate)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtMtbName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtMtbSymbol, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, btnNext)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                        .add(lblMtbAllele)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(lblMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .add(0, 0, Short.MAX_VALUE)))
                        .add(122, 122, 122))))
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
                    .add(lblMarkerKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblMGIMarkerID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, btnCopySynonyms))
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 11, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnUpdate)
                    .add(jButtonRemove))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void checkMarkers(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkMarkers
    loadMarkers();
}//GEN-LAST:event_checkMarkers

private void nextMarker(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextMarker
    next();
}//GEN-LAST:event_nextMarker

private void copyName(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyName
    
    addSynonym(this.txtMtbName.getText());
    this.txtMtbName.setText(this.txtMgiName.getText());
    this.txtMgiName.setBackground(Color.WHITE);
    this.txtMtbName.setBackground(Color.WHITE);
    
    
    

}//GEN-LAST:event_copyName

private void copySymbol(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copySymbol
    
    addSynonym(this.txtMtbSymbol.getText());
    this.txtMtbSymbol.setText(
            MTBSynchronizationUtilDAO.fixSymbol(this.txtMgiSymbol.getText()));
    this.txtMgiSymbol.setBackground(Color.WHITE);
    this.txtMtbSymbol.setBackground(Color.WHITE);
    
   
    

}//GEN-LAST:event_copySymbol

private void updateMarker(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateMarker
    update();
}//GEN-LAST:event_updateMarker

private void prevMarker(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevMarker
    prev();
}//GEN-LAST:event_prevMarker

    private void btnCopySynonyms(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopySynonyms
        copySynonyms();
    }//GEN-LAST:event_btnCopySynonyms

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
       removeSynonyms();
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnCopyName;
    private javax.swing.JButton btnCopySymbol;
    private javax.swing.JButton btnCopySynonyms;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnUpdate;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelAllele;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCount;
    private javax.swing.JLabel lblMGIMarkerID;
    private javax.swing.JLabel lblMarkerKey;
    private javax.swing.JLabel lblMgiAllele;
    private javax.swing.JLabel lblMtbAllele;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSymbol;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTextField txtMgiName;
    private javax.swing.JTextField txtMgiSymbol;
    private javax.swing.JTextField txtMtbName;
    private javax.swing.JTextField txtMtbSymbol;
    // End of variables declaration//GEN-END:variables
}
