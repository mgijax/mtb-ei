/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/Main.java,v 1.1 2007/04/30 15:50:39 mjv Exp
 * Author: mjv
 */

package org.jax.mgi.mtb.ei;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jax.mgi.mtb.ei.gui.MainFrame;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.ei.actions.LoginAction;
import org.jax.mgi.mtb.ei.dialogs.LoginDialog;
import org.jax.mgi.mtb.gui.MXInfoDialog;
import org.jax.mgi.mtb.gui.progress.MXActiveWindowTracker;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.utils.StringUtils;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

/**
 * The main class of the Editorial Interface.
 *
 * @author mjv
 * @date 2007/04/30 15:50:39
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/Main.java,v 1.1 2007/04/30 15:50:39 mjv Exp
 */
public class Main {

    // -------------------------------------------------------------- Constants
    // none

    // ----------------------------------------------------- Instance Variables

    private boolean userAuthenticated = false;
    private MainFrame mainFrame = null;
    private String propFile = EIConstants.PROPERTIES_FILE_PATH;
    private LoginDialog dlg = null;
    private MTBUsersDTO user = null;


    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new instance of Main.
     */
    public Main() {
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Set the look and feel of the EI.
     */
    public void installLookAndFeel() {
        
        
        String OSName = System.getProperty("os.name").toLowerCase();
        boolean MAC = OSName.startsWith("mac");

        if (MAC) {
            // get the native look and feel class name
            String nativeLF = UIManager.getSystemLookAndFeelClassName();
            try {
                UIManager.setLookAndFeel(nativeLF);
            } catch (InstantiationException ex) {
              ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
              ex.printStackTrace();
            } catch (UnsupportedLookAndFeelException ex) {
              ex.printStackTrace();
            } catch (IllegalAccessException ex) {
              ex.printStackTrace();
            }
        } else {
            try {
                
                // try loading the jgoodies
                com.jgoodies.looks.plastic.PlasticXPLookAndFeel pxplaf = new com.jgoodies.looks.plastic.PlasticXPLookAndFeel();
               
           
                UIManager.put("ClassLoader",pxplaf.getClass().getClassLoader());
                
                //String looks ="com.jgoodies.looks.plastic.PlasticXPLookAndFeel";
               // UIManager.setLookAndFeel(looks);
                
                UIManager.setLookAndFeel(pxplaf);
            } catch (Exception e) {
                e.printStackTrace();

                // do nothing, no big deal that the look and feel cannot be
                // used get the native look and feel class name
                String nativeLF = UIManager.getSystemLookAndFeelClassName();

                // Install the look and feel
                try {
                    UIManager.setLookAndFeel(nativeLF);
                } catch (InstantiationException ex) {
                } catch (ClassNotFoundException ex) {
                } catch (UnsupportedLookAndFeelException ex) {
                } catch (IllegalAccessException ex) {
                }
            }
        }
    }

    /**
     * Initialize the application.
     *
     * @param args the command line arguments
     */
    public void initializeApplication(String[] args) {
       
        // create Options object
        Options options = new Options();

        // add u(ser) option
        Option optUser = OptionBuilder.withArgName("user")
                                   .hasArg()
                                   .withDescription("MTB User" )
                                   .create("u");
        optUser.setRequired(false);

        // add p(assword) option
        Option optPassword = OptionBuilder.withArgName("password")
                                   .hasArg()
                                   .withDescription("MTB Password" )
                                   .create("p");
        optPassword.setRequired(false);

        // add prop(erties) option
        Option optProperties = OptionBuilder.withArgName("file")
                                   .hasArg()
                                   .withDescription("MTB Properties file" )
                                   .create("prop");
        optProperties.setRequired(false);

        options.addOption(optUser);
        options.addOption(optPassword);
        options.addOption(optProperties);

        String strUser = null;
        String strPassword = null;

        // create the parser
        CommandLineParser parser = new PosixParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args, true);
            if(line.hasOption("u")) {
                strUser = line.getOptionValue("u");
            }

            if(line.hasOption("p")) {
                strPassword = line.getOptionValue("p");
            }
        } catch(ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            exp.printStackTrace();
        }

      

        initProperties();

        if (StringUtils.hasValue(strUser) &&
            StringUtils.hasValue(strPassword)) {

            LoginAction la = new LoginAction();
            try {
                if (la.login(strUser, strPassword)) {
                    this.user = la.getUser();
                    EIGlobals.getInstance().setMTBUsersDTO(this.user);
                    this.userAuthenticated = true;
                } else {
                    MXInfoDialog.showDialog(null, "Authentication Error",
                                           "<html><body>Unable toauthenticate with command line parameters.</body></html>");
                    System.exit(-1);
                }
            } catch (Exception e) {
                MXInfoDialog.showDialog(null, "Authentication Error",
                                              "<html><body>Unable toauthenticate with command line parameters.</body></html>", e);
                System.exit(-1);
            }
        }
    }

    /**
     * Attempt to authenticate the user in the database.
     */
    public void authenticateUser() {
        dlg = new LoginDialog(null, true);
        dlg.pack();
        Utils.centerComponentonScreen(dlg);
        dlg.setVisible(true);
        dlg.toFront();

        if (dlg.isSuccessful()) {
            this.userAuthenticated = true;
            this.user = dlg.getUser();
            EIGlobals.getInstance().setMTBUsersDTO(this.user);
        }

        dlg.dispose();
        dlg = null;
    }

    /**
     * Show the interface.
     */
    public void runApplication() {
        EIGlobals.getInstance().initDB();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
    }

    /**
     * Check to see if the user is authenticated or not.
     *
     * @return <code>true</code> if the user was successfully authenticated,
     *         <code>false</code> otherwise
     */
    public boolean isUserAuthenticated() {
        return this.userAuthenticated;
    }

    /**
     * Get the authenticated <code>MTBUsersDTO</code> object.
     *
     * @return the <code>MTBUsersDTO</code> object
     */
    public MTBUsersDTO getUser() {
        return this.user;
    }

    /**
     * The MAIN method of the Editorial Interface.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MXActiveWindowTracker.findActiveWindow();

        Main app = new Main();

        app.initializeApplication(args);

        app.installLookAndFeel();

        if ((app.getUser() == null) ||
            (app.getUser().getUserName().length() == 0)) {
            app.authenticateUser();
        }

        if (app.isUserAuthenticated()) {
            app.runApplication();
        }
    }

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Create the GUI components and display the main screen.
     */
    private void createAndShowGUI() {
        mainFrame = new MainFrame();
        mainFrame.setExtendedState(mainFrame.getExtendedState() |
                                   mainFrame.MAXIMIZED_BOTH);
        // Center the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width - 200, screenSize.height - 200);
        Utils.centerComponentonScreen(mainFrame);
        mainFrame.setVisible(true);
        //Globals.getInstance().setMainFrame(mainFrame);
    }

    /**
     * Initialize the global variables used in the EI.
     */
    private void initProperties() {
        /**
         * ENHANCEMENT: Might be nice to automatically download a generic file
         * from the web or somewhere if the file cannot be found.
         */
        try {
            System.out.println("Loading props...");
            // load the global properties
            //Properties props = Utils.loadProperties(propFile);
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/" + propFile));

            System.out.println("Setting props...");
            EIGlobals.getInstance().setProperties(props);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showErrorDialog(e.getMessage(), e);
            System.exit(-1);
        }
    }
}
