/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/LoginDialog.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.ei.actions.LoginAction;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;
import org.jdesktop.swingworker.SwingWorker;

/**
 * A custom <code>JDialog</code> used for end user login.
 *
 * @author mjv
 * @date 2007/04/30 15:50:44
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/LoginDialog.java,v 1.1 2007/04/30 15:50:44 mjv Exp
 * @see javax.swing.JDialog
 */
public class LoginDialog extends JDialog implements FadeListener  {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private JComponent contentPane;
    private boolean successful = false;
    private MTBUsersDTO user = null;
    private Timer animation;
    private FadingPanel glassPane;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form LoginDialog.
     *
     * @param parent the parent frame
     * @param modal whether the dialog should be modal or not
     */
    public LoginDialog(Frame parent, boolean modal) {
        super(parent, modal);
        buildContentPane();
        initComponents();
        initCustom();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Handle the fade in.
     *
     * @see org.jax.mgi.mtb.ei.widgets.FadeListener
     */
    public void fadeInFinished() {
        glassPane.setVisible(false);
    }

    /**
     * Handle the fade out.
     *
     * @see org.jax.mgi.mtb.ei.widgets.FadeListener
     */
    public void fadeOutFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                contentPane = new CurvesPanel();
                contentPane.setLayout(new BorderLayout());
                WaitAnimation waitAnimation = new WaitAnimation();
                contentPane.add(waitAnimation, BorderLayout.CENTER);
                setContentPane(contentPane);
                validate();
                glassPane.switchDirection();
            }
        });
    }

    /**
     * Determines if the user successfully logged in or not.
     *
     * @return <code>true</code> if the logged in to the system,
     *         <code>false</code> otherwise
     */
    public boolean isSuccessful() {
        return this.successful;
    }

    /**
     * Get the <code>MTBUsersDTO</code> object representing the user.
     *
     * @return the <code>MTBUsersDTO</code> object
     */
    public MTBUsersDTO getUser() {
        return this.user;
    }

    /**
     * Attempt to login to the system.
     */
    public void doLogin() {
        if (!StringUtils.hasValue(txtUserName.getText())) {
            Utils.showErrorDialog("Please enter your User Name.");
            return;
        }

        if (!StringUtils.hasValue(new String(pwdPassword.getPassword()))) {
            Utils.showErrorDialog("Please enter your Password.");
            return;
        }

        glassPane.setVisible(true);

        LoginAction action = new LoginAction();

        String user = txtUserName.getText();
        char[] pwd = pwdPassword.getPassword();

        if (pwd.length == 0) {
            return;
        }

        String password = new String(pwd);

        try {
            if (action.login(user, password)) {
                this.successful = true;
                this.user = action.getUser();

                SwingWorker worker = new SwingWorker() {
                    public Object doInBackground() {
                        try {
                            // sleep a couple of seconds just so the user can
                            // see the effects
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return "Done";
                    }
                    public void done() {
                        setVisible(false);
                    }
                };

                worker.execute();
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        glassPane.setVisible(false);
                    }
                });
                Utils.showErrorDialog("Unable to authenticate.\n\n" +
                                         "You have entered an incorrect " +
                                         "userid and password combination." +
                                         "\n\nPlease try again.");
            }
        } catch (Exception e) {
            Utils.log("Unable to authenticate user!!!");
        }
    }

    /**
     * Override dispose.
     */
    public void dispose() {
        user = null;
        animation = null;
        glassPane = null;

        super.dispose();
    }

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Initialize custom components.
     */
    private void initCustom() {
        glassPane = new FadingPanel(this);
        setGlassPane(glassPane);
        startAnimation();
        setResizable(false);
    }

    /**
     * Build the content pane with curves.
     */
    private void buildContentPane() {
        contentPane = new CurvesPanel();
        //contentPane = new DnaPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
    }

    /**
     * Start the animation.
     */
    private void startAnimation() {
        animation = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                contentPane.repaint();
            }
        });
        animation.start();
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

    pnlEntireLogin = new javax.swing.JPanel();
    pnlTop = new javax.swing.JPanel();
    lblTitle = new javax.swing.JLabel();
    pnlMiddle = new javax.swing.JPanel();
    lblCuratorLogin = new javax.swing.JLabel();
    pnlBoxHolder = new javax.swing.JPanel();
    pnlBox = new javax.swing.JPanel();
    pnlInnerBox = new javax.swing.JPanel();
    pnlUserName = new javax.swing.JPanel();
    lblUserName = new javax.swing.JLabel();
    txtUserName = new javax.swing.JTextField();
    pnlPassword = new javax.swing.JPanel();
    lblPassword = new javax.swing.JLabel();
    pwdPassword = new javax.swing.JPasswordField();
    pnlLoginButton = new javax.swing.JPanel();
    btnLogin = new javax.swing.JButton();
    pnlBottom = new javax.swing.JPanel();
    pnlHelp = new javax.swing.JPanel();
    pnlHelpButtons = new javax.swing.JPanel();
    btnExit = new javax.swing.JButton();
    btnHelp = new javax.swing.JButton();
    lblEmail = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Mouse Tumor Biology EI Login");
    setBackground(new java.awt.Color(220, 220, 220));
    setResizable(false);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(java.awt.event.WindowEvent evt) {
        formWindowClosed(evt);
      }
    });

    pnlEntireLogin.setBackground(new java.awt.Color(33, 72, 152));
    pnlEntireLogin.setOpaque(false);
    pnlEntireLogin.setLayout(new java.awt.BorderLayout());

    pnlTop.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 1, 30));
    pnlTop.setOpaque(false);
    pnlTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    lblTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/LoginTitle2.png"))); // NOI18N
    pnlTop.add(lblTitle);

    pnlEntireLogin.add(pnlTop, java.awt.BorderLayout.NORTH);

    pnlMiddle.setOpaque(false);
    pnlMiddle.setLayout(new java.awt.BorderLayout());

    lblCuratorLogin.setFont(new java.awt.Font("MS Sans Serif", 1, 11)); // NOI18N
    lblCuratorLogin.setForeground(new java.awt.Color(255, 255, 255));
    lblCuratorLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lblCuratorLogin.setText("Curator Login");
    pnlMiddle.add(lblCuratorLogin, java.awt.BorderLayout.NORTH);

    pnlBoxHolder.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 150, 10, 150));
    pnlBoxHolder.setOpaque(false);
    pnlBoxHolder.setLayout(new java.awt.BorderLayout());

    pnlBox.setBackground(new java.awt.Color(103, 145, 228));
    pnlBox.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

    pnlInnerBox.setBackground(new java.awt.Color(103, 145, 228));
    pnlInnerBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    pnlInnerBox.setLayout(new java.awt.BorderLayout(0, 10));

    pnlUserName.setOpaque(false);
    pnlUserName.setLayout(new java.awt.BorderLayout(10, 0));

    lblUserName.setFont(new java.awt.Font("MS Sans Serif", 1, 11)); // NOI18N
    lblUserName.setForeground(new java.awt.Color(255, 255, 255));
    lblUserName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblUserName.setText("User Name:");
    lblUserName.setMaximumSize(new java.awt.Dimension(80, 15));
    lblUserName.setMinimumSize(new java.awt.Dimension(80, 15));
    lblUserName.setPreferredSize(new java.awt.Dimension(80, 15));
    pnlUserName.add(lblUserName, java.awt.BorderLayout.WEST);

    txtUserName.setPreferredSize(new java.awt.Dimension(200, 20));
    pnlUserName.add(txtUserName, java.awt.BorderLayout.CENTER);

    pnlInnerBox.add(pnlUserName, java.awt.BorderLayout.NORTH);

    pnlPassword.setOpaque(false);
    pnlPassword.setLayout(new java.awt.BorderLayout(10, 0));

    lblPassword.setFont(new java.awt.Font("MS Sans Serif", 1, 11)); // NOI18N
    lblPassword.setForeground(new java.awt.Color(255, 255, 255));
    lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lblPassword.setText("Password:");
    lblPassword.setMaximumSize(new java.awt.Dimension(80, 15));
    lblPassword.setMinimumSize(new java.awt.Dimension(80, 15));
    lblPassword.setPreferredSize(new java.awt.Dimension(80, 15));
    pnlPassword.add(lblPassword, java.awt.BorderLayout.WEST);

    pwdPassword.setNextFocusableComponent(btnLogin);
    pwdPassword.setPreferredSize(new java.awt.Dimension(200, 20));
    pnlPassword.add(pwdPassword, java.awt.BorderLayout.CENTER);

    pnlInnerBox.add(pnlPassword, java.awt.BorderLayout.CENTER);

    pnlLoginButton.setOpaque(false);
    pnlLoginButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

    btnLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Key16.png"))); // NOI18N
    btnLogin.setText("Login");
    btnLogin.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnLoginActionPerformed(evt);
      }
    });
    pnlLoginButton.add(btnLogin);

    pnlInnerBox.add(pnlLoginButton, java.awt.BorderLayout.SOUTH);

    pnlBox.add(pnlInnerBox);

    pnlBoxHolder.add(pnlBox, java.awt.BorderLayout.NORTH);

    pnlMiddle.add(pnlBoxHolder, java.awt.BorderLayout.CENTER);

    pnlEntireLogin.add(pnlMiddle, java.awt.BorderLayout.CENTER);

    pnlBottom.setMaximumSize(new java.awt.Dimension(10, 50));
    pnlBottom.setMinimumSize(new java.awt.Dimension(10, 50));
    pnlBottom.setOpaque(false);
    pnlBottom.setPreferredSize(new java.awt.Dimension(10, 50));
    pnlEntireLogin.add(pnlBottom, java.awt.BorderLayout.SOUTH);

    getContentPane().add(pnlEntireLogin, java.awt.BorderLayout.CENTER);

    pnlHelp.setBackground(new java.awt.Color(0, 0, 0));
    pnlHelp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
    pnlHelp.setLayout(new java.awt.BorderLayout());

    pnlHelpButtons.setOpaque(false);

    btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Close16.png"))); // NOI18N
    btnExit.setText("Exit");
    btnExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnExitActionPerformed(evt);
      }
    });
    pnlHelpButtons.add(btnExit);

    btnHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Information16.png"))); // NOI18N
    btnHelp.setText("Help");
    btnHelp.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnHelpActionPerformed(evt);
      }
    });
    pnlHelpButtons.add(btnHelp);

    pnlHelp.add(pnlHelpButtons, java.awt.BorderLayout.WEST);

    lblEmail.setFont(new java.awt.Font("MS Sans Serif", 1, 11)); // NOI18N
    lblEmail.setForeground(new java.awt.Color(255, 255, 255));
    lblEmail.setText("Email: mjv@informatics.jax.org");
    lblEmail.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    pnlHelp.add(lblEmail, java.awt.BorderLayout.EAST);

    getContentPane().add(pnlHelp, java.awt.BorderLayout.SOUTH);

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void btnHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHelpActionPerformed
        Utils.showErrorDialog("Help System not implemented.");
    }//GEN-LAST:event_btnHelpActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        if (!this.successful) {
            System.exit(1);
        }

        this.setVisible(false);
    }//GEN-LAST:event_formWindowClosed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(1);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        doLogin();
    }//GEN-LAST:event_btnLoginActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnExit;
  private javax.swing.JButton btnHelp;
  private javax.swing.JButton btnLogin;
  private javax.swing.JLabel lblCuratorLogin;
  private javax.swing.JLabel lblEmail;
  private javax.swing.JLabel lblPassword;
  private javax.swing.JLabel lblTitle;
  private javax.swing.JLabel lblUserName;
  private javax.swing.JPanel pnlBottom;
  private javax.swing.JPanel pnlBox;
  private javax.swing.JPanel pnlBoxHolder;
  private javax.swing.JPanel pnlEntireLogin;
  private javax.swing.JPanel pnlHelp;
  private javax.swing.JPanel pnlHelpButtons;
  private javax.swing.JPanel pnlInnerBox;
  private javax.swing.JPanel pnlLoginButton;
  private javax.swing.JPanel pnlMiddle;
  private javax.swing.JPanel pnlPassword;
  private javax.swing.JPanel pnlTop;
  private javax.swing.JPanel pnlUserName;
  private javax.swing.JPasswordField pwdPassword;
  private javax.swing.JTextField txtUserName;
  // End of variables declaration//GEN-END:variables

}
