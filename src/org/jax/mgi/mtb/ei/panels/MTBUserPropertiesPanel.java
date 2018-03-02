/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/MTBUserPropertiesPanel.java,v 1.1 2007/04/30 15:50:55 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.panels;

import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 * For editing <b>MTBUsers</b> data.
 * 
 * 
 * 
 * @author mjv
 * @version 1.1
 * @see org.jax.mgi.mtb.ei.panels.CustomPanel
 * @CustomPanel 
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/panels/MTBUserPropertiesPanel.java,v 1.1 2007/04/30 15:50:55 mjv Exp
 */
public class MTBUserPropertiesPanel extends CustomPanel {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private MTBUsersDTO dtoUser = null;

    // progress monitor
    private MXProgressMonitor progressMonitor = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new form MTBUserPropertiesPanel.
     */
    public MTBUserPropertiesPanel() {
        initComponents();
        initCustom();
    }

    // --------------------------------------------------------- Public Methods

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
            if (!StringUtils.equals(dtoUser.getFullName(), txtUserName.getText())) {
                return true;
            }
            if (!StringUtils.equals(dtoUser.getEmail(), txtEmail.getText())) {
                return true;
            }
            if (!StringUtils.equals(dtoUser.getTelephone(), txtTelephone.getText())) {
                return true;
            }
            return false;
        }
        StringUtils.out("Super changed...");
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
     * Perform any custom initialization needed.
     */
    private void initCustom() {
        dtoUser = EIGlobals.getInstance().getMTBUsersDTO();

        txtUserID.setText(dtoUser.getUserName());
        txtUserName.setText(dtoUser.getFullName());
        txtEmail.setText(dtoUser.getEmail());
        txtTelephone.setText(dtoUser.getTelephone());
    }

    /**
     * Update the user information in the database.
     * <p>
     * This is an all or nothing insert.  Either everything the user has
     * filled in gets comitted to the database or nothing does.
     */
    private void updateData() {
        boolean commit = false;
        MTBUsersDAO daoUser = MTBUsersDAO.getInstance();

        try {
            ///////////////////////////////////////////////////////////////////
            // Start the Transaction
            ///////////////////////////////////////////////////////////////////
            DAOManagerMTB.getInstance().beginTransaction();

            ///////////////////////////////////////////////////////////////////
            // save the user
            ///////////////////////////////////////////////////////////////////
            dtoUser.setPassword(new String(passwdNew.getPassword()));
            dtoUser.setFullName(txtUserName.getText());
            dtoUser.setEmail(txtEmail.getText());
            dtoUser.setTelephone(txtTelephone.getText());

            dtoUser = daoUser.save(dtoUser);

            ///////////////////////////////////////////////////////////////////
            // COMMIT point reached
            ///////////////////////////////////////////////////////////////////
            commit = true;
        } catch (Exception e) {
            Utils.log(e);
            Utils.showErrorDialog("Unable to save changes to MTB User.", e);
            return;
        } finally {
            try {
                ///////////////////////////////////////////////////////////////
                // End the Transaction
                ///////////////////////////////////////////////////////////////
                DAOManagerMTB.getInstance().endTransaction(commit);
            } catch (Exception e2) {
                Utils.showErrorDialog("Unable to save changes to MTB User.", e2);
                return;
            }
        }

        if (commit) {
            // make sure the globals are updated
            EIGlobals.getInstance().setMTBUsersDTO(dtoUser);
            initCustom();
            Utils.showSuccessDialog("MTB User information saved.");
        }
    }

    /**
     * Save the reference information.
     * <p>
     * The reference information will be inserted.  This is performed in a
     * seperate thread since this could potentially be a lengthy operation.
     * A <code>MXProgressMonitor</code> is used to display visual feedback to the
     * user.
     */
    public void save() {
        String strNew = new String(passwdNew.getPassword());
        String strConfirm = new String(passwdNew.getPassword());

        if (!StringUtils.equals(strNew, strConfirm)) {
            Utils.showErrorDialog("Passwords don't match.  Please try again.");
            return;
        }

        Runnable runnable = new Runnable(){
            public void run(){
                progressMonitor = MXProgressUtil.createModalProgressMonitor(1, true);
                try {
                    progressMonitor.start("Updating user data...");
                    updateData();
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


    // ------------------------------------------------------------------------
    // ------------------------------------------------ NetBeans Generated Code
    // ------------------------------------------------------------------------
    // TAKE EXTREME CARE MODIFYING CODE BELOW THIS LINE

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlProperties = new javax.swing.JPanel();
        headerPanelProperties = new org.jax.mgi.mtb.gui.MXHeaderPanel();
        lblNew = new javax.swing.JLabel();
        lblConfirm = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblTelephone = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblUserID = new javax.swing.JLabel();
        txtUserID = new javax.swing.JTextField();
        txtTelephone = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtUserName = new javax.swing.JTextField();
        passwdNew = new javax.swing.JPasswordField();
        passwdConfirm = new javax.swing.JPasswordField();

        pnlProperties.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        headerPanelProperties.setDrawSeparatorUnderneath(true);
        headerPanelProperties.setText("MTB User Properties");

        lblNew.setText("New Password");

        lblConfirm.setText("Confirm Password");

        lblUserName.setText("User Name");

        lblEmail.setText("E-mail");

        lblTelephone.setText("Telephone");

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Save16.png")));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png")));
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        lblUserID.setText("User ID");

        txtUserID.setEditable(false);

        org.jdesktop.layout.GroupLayout pnlPropertiesLayout = new org.jdesktop.layout.GroupLayout(pnlProperties);
        pnlProperties.setLayout(pnlPropertiesLayout);
        pnlPropertiesLayout.setHorizontalGroup(
            pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanelProperties, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
            .add(pnlPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblTelephone)
                    .add(lblEmail)
                    .add(lblUserName)
                    .add(lblConfirm)
                    .add(lblNew)
                    .add(lblUserID))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtTelephone, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(txtUserID, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(txtEmail, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(txtUserName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(passwdNew, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(passwdConfirm, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlPropertiesLayout.createSequentialGroup()
                .addContainerGap(303, Short.MAX_VALUE)
                .add(btnSave)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnCancel)
                .addContainerGap())
        );

        pnlPropertiesLayout.linkSize(new java.awt.Component[] {btnCancel, btnSave}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnlPropertiesLayout.setVerticalGroup(
            pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPropertiesLayout.createSequentialGroup()
                .add(headerPanelProperties, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblUserID)
                    .add(txtUserID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblNew)
                    .add(passwdNew, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblConfirm)
                    .add(passwdConfirm, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblUserName)
                    .add(txtUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblEmail)
                    .add(txtEmail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTelephone)
                    .add(txtTelephone, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnSave))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlProperties, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlProperties, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        customInternalFrame.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private org.jax.mgi.mtb.gui.MXHeaderPanel headerPanelProperties;
    private javax.swing.JLabel lblConfirm;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblNew;
    private javax.swing.JLabel lblTelephone;
    private javax.swing.JLabel lblUserID;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPasswordField passwdConfirm;
    private javax.swing.JPasswordField passwdNew;
    private javax.swing.JPanel pnlProperties;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtTelephone;
    private javax.swing.JTextField txtUserID;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables

}
