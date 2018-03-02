/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/AboutDialog.java,v 1.1 2007/04/30 15:50:43 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.MXTable;
import org.jax.mgi.mtb.gui.MXTypeWriterPanel;
import org.jax.mgi.mtb.utils.LabelValueBean;
import us.jawsoft.gui.JawButtonBar;
import us.jawsoft.gui.plaf.blue.BlueishButtonBarUI;

/**
 * A custom <code>JDialog</code> used to display general information about the
 * EI.
 *
 * @author mjv
 * @date 2007/04/30 15:50:43
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/dialogs/AboutDialog.java,v 1.1 2007/04/30 15:50:43 mjv Exp
 * @see javax.swing.JDialog
 */
public class AboutDialog extends JDialog {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private Component currentComponent;


    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form AboutDialog.
     *
     * @param parent the parent frame
     * @param modal whether the dialog should be modal or not
     */
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initCustom();
    }


    // --------------------------------------------------------- Public Methods
    // none

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Initialize custom components.
     */
    private void initCustom() {
        // get the notes
        try {
            URL url = getClass().getResource(EIConstants.RELEASE_NOTES_PATH);
            textPaneNotes.setPage(url);
        } catch (Exception e) {
            Utils.log(e);
        }

        // get all system properties
        Properties props = System.getProperties();

        lblProductText.setText(EIConstants.APP_NAME_TEXT);
        lblBuildText.setText(EIConstants.APP_BUILD_TEXT);
        lblJavaVMText.setText(props.getProperty("java.vm.name"));
        lblJavaVersionText.setText(props.getProperty("java.vm.version"));
        lblJavaVendorText.setText(props.getProperty("java.vendor"));
        lblOSNameText.setText(props.getProperty("os.name"));
        lblOSArchitectureText.setText(props.getProperty("os.arch"));
        lblOSVersionText.setText(props.getProperty("os.version"));
        lblBuildTime.setText(EIGlobals.getInstance().getBuildTime());
        lblBuildNumber.setText(EIGlobals.getInstance().getBuildNumber());

        List<LabelValueBean<String,String>> arr = Utils.getSystemProperties();
        List<List<String>> rows = new ArrayList<List<String>>();

        for (LabelValueBean<String,String> bean : arr) {
            List<String> row = new ArrayList<String>();
            row.add(bean.getLabel());
            row.add(bean.getValue());
            rows.add(row);
        }
        List<String> row = new ArrayList<String>();
        row.add("Free Memory");
        row.add(Runtime.getRuntime().freeMemory()+"");
        rows.add(row);
        
        List<String> row2 = new ArrayList<String>();
        row2.add("Max Memory");
        row2.add(Runtime.getRuntime().maxMemory()+"");
        rows.add(row2);
        
        List<String> row3 = new ArrayList<String>();
        row3.add("Total Memory");
        row3.add(Runtime.getRuntime().totalMemory()+"");
        rows.add(row3);

        // construct the animated typewriter component
        MXTypeWriterPanel tw = new MXTypeWriterPanel(350, 150);
        tw.startAnimation();
        tw.setAlignmentY(javax.swing.SwingConstants.CENTER);
        pnlType.add(tw, BorderLayout.CENTER);
        pnlType.revalidate();

        List<String> columns = new ArrayList<String>();
        columns.add("Property");
        columns.add("Value");
        MXTable tbl = new MXTable(rows, columns);

        pnlProperties.remove(jspProperties);
        jspProperties = new JScrollPane(tbl);
        pnlProperties.add(jspProperties);
        pnlProperties.revalidate();

        // construct the menu
        JawButtonBar menuBar = new JawButtonBar(JawButtonBar.VERTICAL);
        menuBar.setUI(new BlueishButtonBarUI(true));
        ButtonGroup menuGroup = new ButtonGroup();

        addButton("About", EIConstants.ICO_ABOUT_24, pnlAbout,
                menuBar, menuGroup);

        addButton("History", EIConstants.ICO_HISTORY_24, pnlNotes,
                menuBar, menuGroup);

        addButton("Build", EIConstants.ICO_DATABASE_24, pnlBuildInfoTab,
                menuBar, menuGroup);

        addButton("Properties", EIConstants.ICO_PREFERENCES_24, pnlProperties,
                menuBar, menuGroup);

        show(pnlAbout);

        pnlMain.add("West", menuBar);
    }

    /**
     * Show the selected component.
     *
     * @param component the component to show
     */
    private void show(Component component) {
        if (currentComponent != null) {
            pnlMain.remove(currentComponent);
        }
        pnlMain.add("Center", currentComponent = component);
        pnlMain.revalidate();
        pnlMain.repaint();
    }

    /**
     * Add a button to the menu.
     *
     * @param title the button title
     * @param pathToIcon the icon image path
     * @param component the component to show when selected
     * @param bar the bar to add to
     * @param group the group to add to
     */
    private void addButton(String title, String pathToIcon,
            final Component component,
            JawButtonBar bar, ButtonGroup group) {

        ImageIcon icon = new ImageIcon(getClass().getResource(pathToIcon));

        Action action = new AbstractAction(title, icon) {
            public void actionPerformed(ActionEvent e) {
                show(component);
            }
        };

        JToggleButton button = new JToggleButton(action);
        button.doClick();

        bar.add(button);

        group.add(button);

        if (group.getSelection() == null) {
            button.setSelected(false);
            show(component);
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
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlNotes = new javax.swing.JPanel();
        jspNotes = new javax.swing.JScrollPane();
        textPaneNotes = new javax.swing.JTextPane();
        pnlTitleNotes = new javax.swing.JPanel();
        lblTitleNotes = new javax.swing.JLabel();
        pnlBuildInfoTab = new javax.swing.JPanel();
        pnlBuildMaster = new javax.swing.JPanel();
        pnlProduct = new javax.swing.JPanel();
        lblProduct = new javax.swing.JLabel();
        lblProductText = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblBuildNumber = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblBuildTime = new javax.swing.JLabel();
        pnlBuild = new javax.swing.JPanel();
        lblBuild = new javax.swing.JLabel();
        lblBuildText = new javax.swing.JLabel();
        pnlJavaVM = new javax.swing.JPanel();
        lblJavaVM = new javax.swing.JLabel();
        lblJavaVMText = new javax.swing.JLabel();
        pnlJavaVersion = new javax.swing.JPanel();
        lblJavaVersion = new javax.swing.JLabel();
        lblJavaVersionText = new javax.swing.JLabel();
        pnlJavaVendor = new javax.swing.JPanel();
        lblJavaVendor = new javax.swing.JLabel();
        lblJavaVendorText = new javax.swing.JLabel();
        pnlOSName = new javax.swing.JPanel();
        lblOSName = new javax.swing.JLabel();
        lblOSNameText = new javax.swing.JLabel();
        pnlOSArchitecture = new javax.swing.JPanel();
        lblOSArchitecture = new javax.swing.JLabel();
        lblOSArchitectureText = new javax.swing.JLabel();
        pnlOSVersion = new javax.swing.JPanel();
        lblOSVersion = new javax.swing.JLabel();
        lblOSVersionText = new javax.swing.JLabel();
        pnlTitleBuild = new javax.swing.JPanel();
        lblTitleBuild = new javax.swing.JLabel();
        pnlProperties = new javax.swing.JPanel();
        jspProperties = new javax.swing.JScrollPane();
        tblProperties = new javax.swing.JTable();
        pnlTitleProperties = new javax.swing.JPanel();
        lblTitleProperties = new javax.swing.JLabel();
        pnlAbout = new javax.swing.JPanel();
        pnlTitleAbout = new javax.swing.JPanel();
        lblTitleAbout = new javax.swing.JLabel();
        pnlMainAbout = new javax.swing.JPanel();
        pnlTopIcon = new javax.swing.JPanel();
        lblTopIcon = new javax.swing.JLabel();
        pnlType = new javax.swing.JPanel();
        pnlMain = new javax.swing.JPanel();

        pnlNotes.setLayout(new java.awt.BorderLayout(0, 5));

        jspNotes.setViewportView(textPaneNotes);

        pnlNotes.add(jspNotes, java.awt.BorderLayout.CENTER);

        pnlTitleNotes.setLayout(new java.awt.BorderLayout());

        pnlTitleNotes.setBackground(new java.awt.Color(33, 72, 152));
        pnlTitleNotes.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        lblTitleNotes.setFont(new java.awt.Font("MS Sans Serif", 1, 14));
        lblTitleNotes.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleNotes.setText("History");
        pnlTitleNotes.add(lblTitleNotes, java.awt.BorderLayout.CENTER);

        pnlNotes.add(pnlTitleNotes, java.awt.BorderLayout.NORTH);

        pnlBuildInfoTab.setLayout(new java.awt.BorderLayout(5, 5));

        us.jawsoft.gui.layout.JawVerticalFlowLayout jawVerticalFlowLayout1 = new us.jawsoft.gui.layout.JawVerticalFlowLayout();
        jawVerticalFlowLayout1.setHorizontalAlignment(3);
        jawVerticalFlowLayout1.setVerticalGap(3);
        pnlBuildMaster.setLayout(jawVerticalFlowLayout1);

        pnlProduct.setLayout(new java.awt.BorderLayout());

        lblProduct.setText("Product:");
        lblProduct.setMaximumSize(new java.awt.Dimension(80, 15));
        lblProduct.setMinimumSize(new java.awt.Dimension(80, 15));
        lblProduct.setPreferredSize(new java.awt.Dimension(80, 15));
        pnlProduct.add(lblProduct, java.awt.BorderLayout.WEST);

        lblProductText.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblProductText.setText("Product Information Goes Here");
        pnlProduct.add(lblProductText, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(pnlProduct);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Build Number:");
        jLabel1.setMaximumSize(new java.awt.Dimension(80, 15));
        jLabel1.setMinimumSize(new java.awt.Dimension(80, 15));
        jLabel1.setPreferredSize(new java.awt.Dimension(80, 15));
        jPanel1.add(jLabel1, java.awt.BorderLayout.WEST);

        lblBuildNumber.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblBuildNumber.setText("Build Number Goes Here");
        jPanel1.add(lblBuildNumber, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Build Time:");
        jLabel2.setMaximumSize(new java.awt.Dimension(80, 15));
        jLabel2.setMinimumSize(new java.awt.Dimension(80, 15));
        jLabel2.setPreferredSize(new java.awt.Dimension(80, 15));
        jPanel2.add(jLabel2, java.awt.BorderLayout.WEST);

        lblBuildTime.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblBuildTime.setText("Build Time Goes Here");
        jPanel2.add(lblBuildTime, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(jPanel2);

        pnlBuild.setLayout(new java.awt.BorderLayout());

        lblBuild.setText("Build:");
        lblBuild.setMaximumSize(new java.awt.Dimension(80, 15));
        lblBuild.setMinimumSize(new java.awt.Dimension(80, 15));
        lblBuild.setPreferredSize(new java.awt.Dimension(80, 15));
        pnlBuild.add(lblBuild, java.awt.BorderLayout.WEST);

        lblBuildText.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblBuildText.setText("Build Information Goes Here");
        pnlBuild.add(lblBuildText, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(pnlBuild);

        pnlJavaVM.setLayout(new java.awt.BorderLayout());

        lblJavaVM.setText("Java VM:");
        lblJavaVM.setMaximumSize(new java.awt.Dimension(80, 15));
        lblJavaVM.setMinimumSize(new java.awt.Dimension(80, 15));
        lblJavaVM.setPreferredSize(new java.awt.Dimension(80, 15));
        pnlJavaVM.add(lblJavaVM, java.awt.BorderLayout.WEST);

        lblJavaVMText.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblJavaVMText.setText("Java VM Information Goes Here");
        pnlJavaVM.add(lblJavaVMText, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(pnlJavaVM);

        pnlJavaVersion.setLayout(new java.awt.BorderLayout());

        lblJavaVersion.setText("Java Version:");
        lblJavaVersion.setMaximumSize(new java.awt.Dimension(80, 15));
        lblJavaVersion.setMinimumSize(new java.awt.Dimension(80, 15));
        lblJavaVersion.setPreferredSize(new java.awt.Dimension(80, 15));
        pnlJavaVersion.add(lblJavaVersion, java.awt.BorderLayout.WEST);

        lblJavaVersionText.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblJavaVersionText.setText("Java Version Information Goes Here");
        pnlJavaVersion.add(lblJavaVersionText, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(pnlJavaVersion);

        pnlJavaVendor.setLayout(new java.awt.BorderLayout());

        lblJavaVendor.setText("Java Vendor:");
        lblJavaVendor.setMaximumSize(new java.awt.Dimension(80, 15));
        lblJavaVendor.setMinimumSize(new java.awt.Dimension(80, 15));
        lblJavaVendor.setPreferredSize(new java.awt.Dimension(80, 15));
        pnlJavaVendor.add(lblJavaVendor, java.awt.BorderLayout.WEST);

        lblJavaVendorText.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblJavaVendorText.setText("Java Vendor Information Goes Here");
        pnlJavaVendor.add(lblJavaVendorText, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(pnlJavaVendor);

        pnlOSName.setLayout(new java.awt.BorderLayout());

        lblOSName.setText("OS Name:");
        lblOSName.setMaximumSize(new java.awt.Dimension(80, 15));
        lblOSName.setMinimumSize(new java.awt.Dimension(80, 15));
        lblOSName.setPreferredSize(new java.awt.Dimension(80, 15));
        pnlOSName.add(lblOSName, java.awt.BorderLayout.WEST);

        lblOSNameText.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblOSNameText.setText("OS Name Information Goes Here");
        pnlOSName.add(lblOSNameText, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(pnlOSName);

        pnlOSArchitecture.setLayout(new java.awt.BorderLayout());

        lblOSArchitecture.setText("OS Arch:");
        lblOSArchitecture.setMaximumSize(new java.awt.Dimension(80, 15));
        lblOSArchitecture.setMinimumSize(new java.awt.Dimension(80, 15));
        lblOSArchitecture.setPreferredSize(new java.awt.Dimension(80, 15));
        pnlOSArchitecture.add(lblOSArchitecture, java.awt.BorderLayout.WEST);

        lblOSArchitectureText.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblOSArchitectureText.setText("OS Architecture Information Goes Here");
        pnlOSArchitecture.add(lblOSArchitectureText, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(pnlOSArchitecture);

        pnlOSVersion.setLayout(new java.awt.BorderLayout());

        lblOSVersion.setText("OS Version:");
        lblOSVersion.setMaximumSize(new java.awt.Dimension(80, 15));
        lblOSVersion.setMinimumSize(new java.awt.Dimension(80, 15));
        lblOSVersion.setPreferredSize(new java.awt.Dimension(80, 15));
        pnlOSVersion.add(lblOSVersion, java.awt.BorderLayout.WEST);

        lblOSVersionText.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        lblOSVersionText.setText("OS Version Information Goes Here");
        pnlOSVersion.add(lblOSVersionText, java.awt.BorderLayout.CENTER);

        pnlBuildMaster.add(pnlOSVersion);

        pnlBuildInfoTab.add(pnlBuildMaster, java.awt.BorderLayout.CENTER);

        pnlTitleBuild.setLayout(new java.awt.BorderLayout());

        pnlTitleBuild.setBackground(new java.awt.Color(33, 72, 152));
        pnlTitleBuild.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        lblTitleBuild.setFont(new java.awt.Font("MS Sans Serif", 1, 14));
        lblTitleBuild.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleBuild.setText("Build");
        pnlTitleBuild.add(lblTitleBuild, java.awt.BorderLayout.CENTER);

        pnlBuildInfoTab.add(pnlTitleBuild, java.awt.BorderLayout.NORTH);

        pnlProperties.setLayout(new java.awt.BorderLayout(5, 5));

        tblProperties.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Property", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jspProperties.setViewportView(tblProperties);

        pnlProperties.add(jspProperties, java.awt.BorderLayout.CENTER);

        pnlTitleProperties.setLayout(new java.awt.BorderLayout());

        pnlTitleProperties.setBackground(new java.awt.Color(33, 72, 152));
        pnlTitleProperties.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        lblTitleProperties.setFont(new java.awt.Font("MS Sans Serif", 1, 14));
        lblTitleProperties.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleProperties.setText("Properties");
        pnlTitleProperties.add(lblTitleProperties, java.awt.BorderLayout.CENTER);

        pnlProperties.add(pnlTitleProperties, java.awt.BorderLayout.NORTH);

        pnlAbout.setLayout(new java.awt.BorderLayout(5, 5));

        pnlTitleAbout.setLayout(new java.awt.BorderLayout());

        pnlTitleAbout.setBackground(new java.awt.Color(33, 72, 152));
        pnlTitleAbout.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        lblTitleAbout.setFont(new java.awt.Font("MS Sans Serif", 1, 14));
        lblTitleAbout.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleAbout.setText("About");
        pnlTitleAbout.add(lblTitleAbout, java.awt.BorderLayout.CENTER);

        pnlAbout.add(pnlTitleAbout, java.awt.BorderLayout.NORTH);

        pnlMainAbout.setLayout(new java.awt.BorderLayout());

        pnlMainAbout.setBackground(new java.awt.Color(33, 72, 152));
        pnlTopIcon.setOpaque(false);
        lblTopIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/LoginTitle.png")));
        pnlTopIcon.add(lblTopIcon);

        pnlMainAbout.add(pnlTopIcon, java.awt.BorderLayout.NORTH);

        pnlType.setLayout(new java.awt.BorderLayout());

        pnlType.setOpaque(false);
        pnlMainAbout.add(pnlType, java.awt.BorderLayout.CENTER);

        pnlAbout.add(pnlMainAbout, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About the MTB Editorial Interface");
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pnlMain.setLayout(new java.awt.BorderLayout(5, 5));

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jspNotes;
    private javax.swing.JScrollPane jspProperties;
    private javax.swing.JLabel lblBuild;
    private javax.swing.JLabel lblBuildNumber;
    private javax.swing.JLabel lblBuildText;
    private javax.swing.JLabel lblBuildTime;
    private javax.swing.JLabel lblJavaVM;
    private javax.swing.JLabel lblJavaVMText;
    private javax.swing.JLabel lblJavaVendor;
    private javax.swing.JLabel lblJavaVendorText;
    private javax.swing.JLabel lblJavaVersion;
    private javax.swing.JLabel lblJavaVersionText;
    private javax.swing.JLabel lblOSArchitecture;
    private javax.swing.JLabel lblOSArchitectureText;
    private javax.swing.JLabel lblOSName;
    private javax.swing.JLabel lblOSNameText;
    private javax.swing.JLabel lblOSVersion;
    private javax.swing.JLabel lblOSVersionText;
    private javax.swing.JLabel lblProduct;
    private javax.swing.JLabel lblProductText;
    private javax.swing.JLabel lblTitleAbout;
    private javax.swing.JLabel lblTitleBuild;
    private javax.swing.JLabel lblTitleNotes;
    private javax.swing.JLabel lblTitleProperties;
    private javax.swing.JLabel lblTopIcon;
    private javax.swing.JPanel pnlAbout;
    private javax.swing.JPanel pnlBuild;
    private javax.swing.JPanel pnlBuildInfoTab;
    private javax.swing.JPanel pnlBuildMaster;
    private javax.swing.JPanel pnlJavaVM;
    private javax.swing.JPanel pnlJavaVendor;
    private javax.swing.JPanel pnlJavaVersion;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlMainAbout;
    private javax.swing.JPanel pnlNotes;
    private javax.swing.JPanel pnlOSArchitecture;
    private javax.swing.JPanel pnlOSName;
    private javax.swing.JPanel pnlOSVersion;
    private javax.swing.JPanel pnlProduct;
    private javax.swing.JPanel pnlProperties;
    private javax.swing.JPanel pnlTitleAbout;
    private javax.swing.JPanel pnlTitleBuild;
    private javax.swing.JPanel pnlTitleNotes;
    private javax.swing.JPanel pnlTitleProperties;
    private javax.swing.JPanel pnlTopIcon;
    private javax.swing.JPanel pnlType;
    private javax.swing.JTable tblProperties;
    private javax.swing.JTextPane textPaneNotes;
    // End of variables declaration//GEN-END:variables
}
