/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/gui/MainFrame.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.apache.logging.log4j.Logger;
import org.jax.mgi.mtb.ei.EIGlobals;
import org.jax.mgi.mtb.ei.dialogs.AboutDialog;
import org.jax.mgi.mtb.ei.gui.WatermarkDesktopPane;
import org.jax.mgi.mtb.ei.panels.AgentTypesPanel;
import org.jax.mgi.mtb.ei.panels.AgentsPanel;
import org.jax.mgi.mtb.ei.panels.AlleleMGISyncPanel;
import org.jax.mgi.mtb.ei.panels.AllelePairPanel;
import org.jax.mgi.mtb.ei.panels.AllelePairSearchPanel;
import org.jax.mgi.mtb.ei.panels.AllelePanel;
import org.jax.mgi.mtb.ei.panels.AlleleSearchPanel;
import org.jax.mgi.mtb.ei.panels.AnatomicalSystemsPanel;
import org.jax.mgi.mtb.ei.panels.SampleAssocPanel;
import org.jax.mgi.mtb.ei.panels.AssayImagePanel;
import org.jax.mgi.mtb.ei.panels.CustomPanel;
import org.jax.mgi.mtb.ei.panels.MTBUserPropertiesPanel;
import org.jax.mgi.mtb.ei.panels.MainMenuRollupPanel;
import org.jax.mgi.mtb.ei.panels.MarkerPanel;
import org.jax.mgi.mtb.ei.panels.MarkerSearchPanel;
import org.jax.mgi.mtb.ei.panels.PathologyPanel;
import org.jax.mgi.mtb.ei.panels.ReferenceMGIPanel;
import org.jax.mgi.mtb.ei.panels.StrainFamilyPanel;
import org.jax.mgi.mtb.ei.panels.TumorFrequencyPanel;
import org.jax.mgi.mtb.ei.panels.TumorFrequencySearchPanel;
import org.jax.mgi.mtb.ei.panels.OrgansPanel;
import org.jax.mgi.mtb.ei.panels.PathologySearchPanel;
import org.jax.mgi.mtb.ei.panels.ProbesPanel;
import org.jax.mgi.mtb.ei.panels.ReferencePanel;
import org.jax.mgi.mtb.ei.panels.ReferenceSearchPanel;
import org.jax.mgi.mtb.ei.panels.StrainPanel;
import org.jax.mgi.mtb.ei.panels.StrainSearchPanel;
import org.jax.mgi.mtb.ei.panels.StrainTypesPanel;
import org.jax.mgi.mtb.ei.panels.TumorClassificationPanel;
import org.jax.mgi.mtb.ei.panels.TumorTypesPanel;
import org.jax.mgi.mtb.ei.panels.ConsolePanel;
import org.jax.mgi.mtb.ei.panels.SeriesPanel;
import org.jax.mgi.mtb.ei.panels.MarkerMGISyncPanel;
import org.jax.mgi.mtb.ei.panels.StatisticsPanel;
import org.jax.mgi.mtb.ei.util.Utils;
import org.jax.mgi.mtb.gui.progress.MXProgressMonitor;
import org.jax.mgi.mtb.gui.progress.MXProgressUtil;
import org.jax.mgi.mtb.jSql.jSql;
import org.jax.mgi.mtb.utils.FieldPrinter;
import org.jdesktop.swingworker.SwingWorker;


/**
 * The main <code>JFrame</code> of the application.
 *
 * @author mjv
 * @date 2007/04/30 15:50:46
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/gui/MainFrame.java,v 1.1 2007/04/30 15:50:46 mjv Exp
 * @see javax.swing.JFrame
 */
public class MainFrame extends JFrame {

    // -------------------------------------------------------------- Constants

    private final int TILE_HORIZONTALLY = 2;
    private final int TILE_VERTICALLY = 3;

    // ----------------------------------------------------- Instance Variables

 
    private MainMenuRollupPanel menuPanel = null;
    private Icon iconSearch = null;
    private Icon iconAdd = null;
    private Icon iconEdit = null;
    private ConsolePanel scratchPad = null;
    private ConsolePanel consolePanel = null;
    private boolean scratchPadShowing = false;
    private boolean consoleShowing = false;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates new form MainFrame.
     */
    public MainFrame() {
        initComponents();
        initCustom();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Get the <code>Image</code> to be used for the application icon.
     */
    public Image getIconImage() {
        String iconPath = "/org/jax/mgi/mtb/ei/resources/img/mtb.png";
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        return icon.getImage();
    }

    /**
     * Show the debug console.
     */
    public void showConsole() {
        if (consoleShowing) {
            pnlConsoles.remove(consolePanel);
        } else {
            pnlConsoles.add(consolePanel);
        }

        consoleShowing = !consoleShowing;
        pnlBottom.revalidate();
    }

    /**
     * Show the scratchpad console.
     */
    public void showScratchpad() {
        if (scratchPadShowing) {
            pnlConsoles.remove(scratchPad);
        } else {
            pnlConsoles.add(scratchPad);
        }

        scratchPadShowing = !scratchPadShowing;
        pnlBottom.revalidate();
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                  Controlled Vocabularies
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Launch an agent edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchAgentWindow() {
        AgentsPanel form = new AgentsPanel();
        return launch(form, "Edit Agents", iconEdit);
    }

    /**
     * Launch an agent types edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchAgentTypeWindow() {
        AgentTypesPanel form = new AgentTypesPanel();
        return launch(form, "Edit Agent Types", iconEdit);
    }

    /**
     * Launch an anatomical system edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchAnatomicalSystemWindow() {
        AnatomicalSystemsPanel form = new AnatomicalSystemsPanel();
        return launch(form, "Edit Anatomical Systems", iconEdit);
    }

    /**
     * Launch an organ edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchOrganWindow() {
        OrgansPanel form = new OrgansPanel();
        return launch(form, "Edit Organs", iconEdit);
    }

    /**
     * Launch a probe edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchProbeWindow() {
        ProbesPanel form = new ProbesPanel();
        return launch(form, "Edit Probes", iconEdit);
    }

    /**
     * Launch a strain family edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchStrainFamilyEditWindow() {
        StrainFamilyPanel form = new StrainFamilyPanel();
        return launch(form, "Edit Strain Families", iconSearch);
    }

    /**
     * Launch a strain type edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchStrainTypeWindow() {
        StrainTypesPanel form = new StrainTypesPanel();
        return launch(form, "Edit Strain Types", iconEdit);
    }

    /**
     * Launch a tumor classification edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchTumorClassificationWindow() {
        TumorClassificationPanel form = new TumorClassificationPanel();
        return launch(form, "Tumor Classification Edit Form", iconEdit);
    }

    /**
     * Launch a tumor type edit window.
     */
    public void launchTumorTypeWindow() {
        SwingWorker swingWorker = new SwingWorker() {
            public Object doInBackground() {
                MXProgressMonitor monitor =
                        MXProgressUtil.createModalProgressMonitor(1, true);
                monitor.start("Loading Tumor Name information");
                TumorTypesPanel form = new TumorTypesPanel();
                launch(form, "Tumor Names Edit Form", iconEdit);
                return monitor;
            }

            public void done() {
                try {
                    MXProgressMonitor monitor = (MXProgressMonitor)get();
                    monitor.setCurrent("Done!", monitor.getTotal());
                } catch (Exception e) {
                    log("Unable to launch tumor type window.");
                    log(e);
                }
            }
        };

        swingWorker.execute();
    }
    
    
     /**
     * Launch a series window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchSeriesWindow() {
        SeriesPanel form = new SeriesPanel();
        return launch(form, "Series Form", iconEdit);
    }
    
    
    
     /**
     * Launch a sample assoc window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchSampleAssocWindow(String id) {
        SampleAssocPanel form = new SampleAssocPanel(id);
        return launch(form, "Sample Association Form", iconEdit);
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                       Strain Information
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Launch a strain search window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchStrainSearchWindow() {
        StrainSearchPanel form = new StrainSearchPanel();
        return launch(form, "Strain Search Form", iconSearch);
    }

    /**
     * Launch a strain add window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchStrainAddWindow() {
        StrainPanel form = new StrainPanel(StrainPanel.STRAIN_PANEL_ADD);
        return launch(form, "Strain Add Form", iconAdd);
    }

    /**
     * Launch a strain edit window.
     *
     * @param key the strain key
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchStrainEditWindow(long key) {
        return launchStrainEditWindow(key, null);
    }

    /**
     * Launch a strain edit window at a specified location.
     *
     * @param key the strain key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchStrainEditWindow(final long key,
                                                      final Point point) {
        StrainPanel form =
                new StrainPanel(StrainPanel.STRAIN_PANEL_EDIT);
        CustomInternalFrame f = launch(form, "Strain Edit Form: " + key,
                                       iconEdit, point);
        form.setKey(key);
        return f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                    Pathology Information
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Launch a pathology search window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchPathologySearchWindow() {
        PathologySearchPanel form = new PathologySearchPanel();
        return launch(form, "Pathology Search Form", iconSearch);
    }

    /**
     * Launch a pathology report add window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchPathologyAddWindow() {
        PathologyPanel form = new PathologyPanel(PathologyPanel.PATHOLOGY_PANEL_NEW);
        return launch(form, "Pathology Add Form", iconAdd);
    }
    
    
    public CustomInternalFrame launchAssayImageWindow() {
        AssayImagePanel form = new AssayImagePanel(AssayImagePanel.ASSAY_IMAGE_PANEL_NEW);
        return launch(form, "Assay Image Add Form", iconAdd);
    }

    /**
     * Launch a pathology report edit window.
     *
     * @param key the pathology key
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchPathologyEditWindow(long key) {
        return launchPathologyEditWindow(key, null);
    }

    /**
     * Launch a pathology report edit window.
     *
     * @param key the pathology key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchPathologyEditWindow(final long key,
                                                         final Point point) {
        PathologyPanel form = new PathologyPanel(PathologyPanel.PATHOLOGY_PANEL_EDIT);
        CustomInternalFrame f = launch(form, "Pathology Edit Form: " + key,
                                       iconEdit, point);
        form.setKey(key);
        return f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                    Reference Information
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Launch a reference search window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchReferenceSearchWindow() {
        //ReferenceSearchPanel form = new ReferenceSearchPanel();
        ReferenceSearchPanel form = new ReferenceSearchPanel();
        return launch(form, "Reference Search Form", iconSearch);
    }

    /**
     * Launch a reference add window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchReferenceAddWindow() {
        ReferenceMGIPanel form = new ReferenceMGIPanel();
        return launch(form, "Reference Add Form", iconAdd);
    }

    /**
     * Launch a reference edit window.
     *
     * @param key the reference key
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchReferenceEditWindow(long key, boolean editable) {
        return launchReferenceEditWindow(key, null, editable);
    }
    
    
     public CustomInternalFrame launchReferenceEditWindow(boolean editable) {
        ReferencePanel form = new ReferencePanel(editable);
        CustomInternalFrame f = launch(form, "Reference Edit Form",iconEdit);
       
        return f;

    }

    /**
     * Launch a reference edit window.
     *
     * @param key the reference key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchReferenceEditWindow(long key,
                                                         final Point point, boolean editable) {
        ReferencePanel form = new ReferencePanel(editable);
        CustomInternalFrame f = launch(form, "Reference Edit Form",
                                       iconEdit, point);
        
        form.setKey(key);
        return f;


    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                        Tumor Information
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Launch a tumor frequency search window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchTumorFrequencySearchWindow() {
        TumorFrequencySearchPanel form = new TumorFrequencySearchPanel();
        return launch(form, "Tumor Frequency Search Form", iconSearch);
    }

    /**
     * Launch a tumor frequency add window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchTumorFrequencyAddWindow() {
        TumorFrequencyPanel form =
                new TumorFrequencyPanel(
                        TumorFrequencyPanel.TUMOR_FREQUENCY_PANEL_ADD);
        return launch(form, "Tumor Frequency Add Form", iconAdd);
    }

    /**
     * Launch a tumor frequency edit window.
     *
     * @param key the tumor frequency key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchTumorFrequencyDuplicateWindow(long key) {
        TumorFrequencyPanel form =
                new TumorFrequencyPanel(
                        TumorFrequencyPanel.TUMOR_FREQUENCY_PANEL_ADD);
        CustomInternalFrame f =
                launch(form, "Tumor Frequency Duplicate Form: " + key, iconEdit);
        form.setKey(key, true);
        return f;
    }
    
    /**
     * Launch a tumor frequency edit window.
     *
     * @param key the tumor frequency key
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchTumorFrequencyEditWindow(long key) {
        return launchTumorFrequencyEditWindow(key, null);
    }

    /**
     * Launch a tumor frequency edit window.
     *
     * @param key the tumor frequency key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame
            launchTumorFrequencyEditWindow(final long key, final Point point) {
        TumorFrequencyPanel form =
                new TumorFrequencyPanel(
                        TumorFrequencyPanel.TUMOR_FREQUENCY_PANEL_EDIT);
        CustomInternalFrame f =
                launch(form, "Tumor Frequency Edit Form: " + key, iconEdit);
        form.setKey(key);
        return f;
    }

    
    ///////////////////////////////////////////////////////////////////////////
    //                                                       Allele Information
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Launch an allele search window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchAlleleSearchWindow() {
        AlleleSearchPanel form = new AlleleSearchPanel();
        return launch(form, "Allele Search Form", iconSearch);
    }
    
    
     public CustomInternalFrame launchAlleleSyncWindow() {
        AlleleMGISyncPanel form = new AlleleMGISyncPanel();
        return launch(form, "Allele Sync Form", iconSearch);
    }
     
     
     public CustomInternalFrame launchMarkerSyncWindow() {
        MarkerMGISyncPanel form = new MarkerMGISyncPanel();
        return launch(form, "Marker Sync Form", iconSearch);
    }

    /**
     * Launch an allele add window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchAlleleAddWindow() {
        AllelePanel form = new AllelePanel(AllelePanel.ALLELE_PANEL_ADD);
        return launch(form, "Allele Add Form", iconAdd);
    }

    /**
     * Launch an allele edit window.
     *
     * @param key the allele key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchAlleleEditWindow(long key) {
        return launchAlleleEditWindow(key, null);
    }

    /**
     * Launch an allele edit window.
     *
     * @param key the allele key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchAlleleEditWindow(long key, Point point) {
        AllelePanel form = new AllelePanel(AllelePanel.ALLELE_PANEL_EDIT);
        CustomInternalFrame f = launch(form, "Allele Edit Form: " + key,
                                       iconEdit, point);
        form.setKey(key);
        return f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                       Marker Information
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Launch a marker search window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchMarkerSearchWindow() {
        MarkerSearchPanel form = new MarkerSearchPanel();
        return launch(form, "Marker Search Form", iconSearch);
    }

    /**
     * Launch a marker add window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchMarkerAddWindow() {
        MarkerPanel form = new MarkerPanel(MarkerPanel.MARKER_PANEL_ADD);
        return launch(form, "Marker Add Form", iconAdd);
    }

    /**
     * Launch a marker edit window.
     *
     * @param key the marker key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchMarkerEditWindow(long key) {
        return launchMarkerEditWindow(key, null);
    }

    /**
     * Launch a marker edit window.
     *
     * @param key the marker key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchMarkerEditWindow(long key, Point point) {
        MarkerPanel form = new MarkerPanel(MarkerPanel.MARKER_PANEL_EDIT);
        CustomInternalFrame f = launch(form, "Marker Edit Form: " + key,
                                       iconEdit, point);
        form.setKey(key);
        return f;
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                     Genotype Information
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Launch a genotype search window.
     *
     * @param key the gentotype key
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchGenotypeSearchWindow() {
        AllelePairSearchPanel form = new AllelePairSearchPanel();
        return launch(form, "Allele Pair Search Form", iconSearch);
    }

    /**
     * Launch a genotype add window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchGenotypeAddWindow() {
        AllelePairPanel form =
                new AllelePairPanel(AllelePairPanel.ALLELE_PAIR_ADD);
        return launch(form, "Allele Pair Add Form", iconAdd);
    }

    /**
     * Launch a genotype edit window at a specified location.
     *
     * @param key the gentotype key
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchGenotypeEditWindow(long key) {
        return launchGenotypeEditWindow(key, null);
    }

    /**
     * Launch a genotype edit window at a specified location.
     *
     * @param key the gentotype key
     * @param point the location to place the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchGenotypeEditWindow(final long key,
                                                        final Point point) {
        AllelePairPanel form =
                new AllelePairPanel(AllelePairPanel.ALLELE_PAIR_EDIT);

        CustomInternalFrame f = launch(form, "Allele Pair Edit Form: " + key,
                                       iconEdit, point);
        form.setKey(key);
        return f;
    }

    /**
     * Launch a mtb user edit window.
     *
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launchMTBUserEditWindow() {
        MTBUserPropertiesPanel form = new MTBUserPropertiesPanel();

        CustomInternalFrame f = launch(form, "MTB User Edit Form",
                                       iconEdit, null);
        return f;
    }
    
    public CustomInternalFrame launchStatisticsWindow(){
      StatisticsPanel form = new StatisticsPanel();
         return launch(form, "Statistics Form", iconSearch);
    }

    /**
     * Generic creation and display of a <code>CustomPanel</code>.
     *
     *
     *
     * @param p the <code>CustomPanel</code>
     * @param title the window title
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launch(CustomPanel p, String title) {
        return launch(p, title, null, null);
    }

    /**
     * Generic creation and display of a <code>CustomPanel</code>.
     *
     *
     *
     * @param p the <code>CustomPanel</code>
     * @param title the window title
     * @param i the icon for the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launch(CustomPanel p, String title, Icon i) {
        return launch(p, title, i,  null);
    }

    /**
     * Generic creation and display of a <code>CustomPanel</code>.
     *
     *
     *
     * @param p the <code>CustomPanel</code>
     * @param title the window title
     * @param i the icon for the window
     * @param point the location of the window
     * @return the <code>CustomInternalFrame</code> object
     */
    public CustomInternalFrame launch(CustomPanel p, String title,
                                      Icon i, Point point) {
        boolean resizable = true;
        boolean closeable = true;
        boolean maximizable  = true;
        boolean iconifiable = true;

        CustomInternalFrame f = new CustomInternalFrame(title, resizable,
                                                        closeable, maximizable,
                                                        iconifiable);

        if (i != null) {
            f.setFrameIcon(i);
        }

        // Add components to internal frame...
        f.setForm(p);
        p.setFrame(f);

        // By default, internal frames are not visible; make it visible
        f.setVisible(true);

        // add to desktop
        desktopPaneMain.add(f);
        
        // bring it to the front
        f.pack();


        // adjust the size to fit in the desktop pane
        int buffer = 15;

        try {
            buffer = Integer.parseInt(UIManager.get("ScrollBar.width")+"");
        } catch (Exception e) {
            Utils.log(e);
        }

        buffer = (buffer == 0) ? 15 : buffer;

        Dimension internalFrameSize = f.getSize();
        Dimension desktopPaneSize = desktopPaneMain.getSize();
        Dimension internalFrameNewSize = f.getSize();
        internalFrameNewSize.width += buffer;
        internalFrameNewSize.height += buffer;

        if (internalFrameSize.width > desktopPaneSize.width) {
            internalFrameNewSize.width = desktopPaneSize.width;
            internalFrameNewSize.height = internalFrameNewSize.height + buffer;
        }

        if (internalFrameSize.height > desktopPaneSize.height) {
            internalFrameNewSize.height = desktopPaneSize.height;
            internalFrameNewSize.width = internalFrameNewSize.width + buffer;
        }

        if (!internalFrameSize.equals(internalFrameNewSize)) {
            f.setSize(internalFrameNewSize);
            f.revalidate();
        }

        // show the window
        f.show();

        if (point != null) {
            f.setLocation(point);
        }

        try {
            f.setSelected(true);
        } catch (Exception e) {
            Utils.log(e);
        }

        return f;
    }

    public void launchSqlWindow() {
        jSql f = new jSql(false);

        try {
            f.addNewConnection("MTB",
                               EIGlobals.getInstance().getJdbcDriver(),
                               EIGlobals.getInstance().getJdbcUrl(),
                               EIGlobals.getInstance().getJdbcUser(),
                               EIGlobals.getInstance().getJdbcPassword());
            f.setActiveConnectionId("MTB");
        } catch (Exception e) {
            log("Unable to instantiate jSQL!!!");
            log(e);
        }


        // By default, internal frames are not visible; make it visible
        f.setVisible(true);


        // add to desktop
        desktopPaneMain.add(f);

        // bring it to the front
        f.pack();

        // adjust the size to fit in the desktop pane
        int buffer = 15;

        try {
            buffer = Integer.parseInt(UIManager.get("ScrollBar.width")+"");
        } catch (Exception e) {
            Utils.log(e);
        }

        buffer = (buffer == 0) ? 15 : buffer;

        Dimension internalFrameSize = f.getSize();
        Dimension desktopPaneSize = desktopPaneMain.getSize();
        Dimension internalFrameNewSize = f.getSize();
        internalFrameNewSize.width += buffer;
        internalFrameNewSize.height += buffer;

        if (internalFrameSize.width > desktopPaneSize.width) {
            internalFrameNewSize.width = desktopPaneSize.width;
            internalFrameNewSize.height = internalFrameNewSize.height + buffer;
        }

        if (internalFrameSize.height > desktopPaneSize.height) {
            internalFrameNewSize.height = desktopPaneSize.height;
            internalFrameNewSize.width = internalFrameNewSize.width + buffer;
        }

        if (!internalFrameSize.equals(internalFrameNewSize)) {
            f.setSize(internalFrameNewSize);
            f.revalidate();
        }

        // show the window
        f.show();

        try {
            f.setSelected(true);
        } catch (Exception e) {
            Utils.log(e);
        }
    }

    /**
     * Log text to the console panel.
     *
     * @param str the text
     */
    public void log(String str) {
        consolePanel.append(str);
    }

    /**
     * Log text to the console panel.
     *
     * @param str the text
     */
    public void log(StringBuffer buf) {
        consolePanel.append(buf);
    }

    /**
     * Log an exception to the console panel.
     *
     * @param e the <code>Exception</code>
     */
    public void log(Exception e) {
        consolePanel.append(e);
    }

    /**
     * Get the size of the desktop.
     *
     * @return the size of the desktop
     */
    public Dimension getDesktopSize() {
        return this.desktopPaneMain.getSize();
    }


    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods

    /**
     * Initialize the custom components.
     */
    private void initCustom() {
        String watermark = "/org/jax/mgi/mtb/ei/resources/img/watermark.png";
        String search = "/org/jax/mgi/mtb/ei/resources/img/search16x16.png";
        String add = "/org/jax/mgi/mtb/ei/resources/img/Add16.png";
        String edit = "/org/jax/mgi/mtb/ei/resources/img/Edit16.png";

        String databaseType = EIGlobals.getInstance().getDBType();

        setTitle(getTitle() + " [" + databaseType + "]");

        splitpaneMain.remove(lblHolder);

        menuPanel = new MainMenuRollupPanel(this);
        splitpaneMain.setLeftComponent(menuPanel);

        ImageIcon icon = new ImageIcon(getClass().getResource(watermark));

        splitpaneMain.remove(desktopPaneMain);
        desktopPaneMain = new WatermarkDesktopPane(icon.getImage());
        splitpaneMain.setRightComponent(desktopPaneMain);

        EIGlobals.getInstance().setMainFrame(this);

        iconSearch = new ImageIcon(getClass().getResource(search));
        iconAdd = new ImageIcon(getClass().getResource(add));
        iconEdit = new ImageIcon(getClass().getResource(edit));

        consolePanel = new ConsolePanel("Console");
        scratchPad = new ConsolePanel("Scratchpad");
    }

    /**
     * Handle the close of the top most window.
     */
    public void handleClose() {
        JInternalFrame internalFrame = desktopPaneMain.getSelectedFrame();
        try {
            if (internalFrame != null) {
                internalFrame.setClosed(true);
            }
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handle the close of this frame by first closing all the internal
     * windows.
     */
    private void handleCloseAll() {
        JInternalFrame frames[] = desktopPaneMain.getAllFrames();

        for (int i = (frames.length - 1); i >= 0; i--) {
            try {
                frames[i].moveToFront();
                frames[i].setClosed(true);
            } catch (Exception e) {
                Utils.log(e);
            }
        }
    }

    /**
     * Show the about dialog.
     */
    private void showAboutDialog() {
        AboutDialog dlg = new AboutDialog(null, true);
        dlg.pack();
        Utils.centerComponentonScreen(dlg);
        dlg.setVisible(true);
    }


    /**
     *
     */
    public void cascade(Container container, int xOffset, int yOffset,
                        int xSize, int ySize) {
        Component components[] = container.getComponents();

        if(components != null && components.length > 0) {

            for(int k = 0; k < components.length; k++) {
                if((components[k] instanceof JInternalFrame) && !((JInternalFrame)components[k]).isIcon()) {
                    components[k].setLocation(xOffset * k, yOffset * k);
                    components[k].setSize(xSize, ySize);
                    try {
                        ((JInternalFrame)components[k]).setSelected(true);
                    }
                    catch(PropertyVetoException pve) {
                        pve.printStackTrace();
                    }
                }
            }
        }
        container.validate();
        container.repaint(1000L);
    }

    public void cascade(Container container) {
        cascade(container, 30, 30, container.getWidth() - 200, container.getHeight() - 200);
    }

    public void tileHorizontally(Container container) {
        Component components[] = container.getComponents();
        Rectangle rectangle = container.getBounds();
        double height = rectangle.getHeight();
        double width = rectangle.getWidth();
        double x = rectangle.getX();
        double y = rectangle.getY();
        int nonIconifiedComponents = 0;
        if(components != null && components.length > 0) {
            for(int k = 0; k < components.length; k++)
                if((components[k] instanceof JInternalFrame) && !((JInternalFrame)components[k]).isIcon())
                    nonIconifiedComponents++;

        }
        int heightOfEachComponent = (int)height;
        int widthOfEachComponent = (int)width / nonIconifiedComponents;
        Rectangle tempRect = null;
        if(components != null && components.length > 0) {
            int k = 0;
            int counter = 0;
            for(; k < components.length; k++) {
                if((components[k] instanceof JInternalFrame) && !((JInternalFrame)components[k]).isIcon()) {
                    tempRect = new Rectangle(widthOfEachComponent * counter++, (int)y, widthOfEachComponent, heightOfEachComponent);
                    components[k].setBounds(tempRect);
                }
            }

        }
        container.validate();
        container.repaint(1000L);
    }

    public void tile(Container container, int alignment) {
        Component components[] = container.getComponents();
        Rectangle rectangle = container.getBounds();
        double height = rectangle.getHeight();
        double width = rectangle.getWidth();
        double x = rectangle.getX();
        double y = rectangle.getY();
        int nonIconifiedComponents = 0;
        if(components != null && components.length > 0) {
            for(int k = 0; k < components.length; k++)
                if((components[k] instanceof JInternalFrame) && !((JInternalFrame)components[k]).isIcon())
                    nonIconifiedComponents++;

        }
        int heightOfEachComponent = 0;
        int widthOfEachComponent = 0;
        int noOfComponentsInX = (int)Math.sqrt(nonIconifiedComponents);
        if(alignment == 1) {
            heightOfEachComponent = (int)height / (nonIconifiedComponents / noOfComponentsInX);
            widthOfEachComponent = (int)width / noOfComponentsInX;
        } else
            if(alignment == 3) {
            heightOfEachComponent = (int)height;
            widthOfEachComponent = (int)width / nonIconifiedComponents;
            } else
                if(alignment == 2) {
            heightOfEachComponent = (int)height / nonIconifiedComponents;
            widthOfEachComponent = (int)width;
                }
        Rectangle tempRect = null;
        if(components != null && components.length > 0) {
            int k = 0;
            int counter = 0;
            int yLocation = 0;
            int xLocation = 0;
            for(; k < components.length; k++)
                if((components[k] instanceof JInternalFrame) && !((JInternalFrame)components[k]).isIcon()) {
                if(alignment == 1) {
                    if(k != 0 && k % noOfComponentsInX == 0) {
                        yLocation++;
                        xLocation = 0;
                    }
                    tempRect = new Rectangle(widthOfEachComponent * xLocation++, heightOfEachComponent * yLocation, widthOfEachComponent, heightOfEachComponent - (int)y);
                } else
                    if(alignment == 3)
                        tempRect = new Rectangle(widthOfEachComponent * counter++, 0, widthOfEachComponent, heightOfEachComponent - (int)y);
                    else
                        if(alignment == 2)
                            tempRect = new Rectangle(0, heightOfEachComponent * counter++, widthOfEachComponent, heightOfEachComponent);
                components[k].setBounds(tempRect);
                }

        }
        container.validate();
        container.repaint(1000L);
    }

    private void takeWindowScreenshot() {
        try{
            // get the window size
            Rectangle rectangle = new Rectangle(this.getBounds());
            rectangle = SwingUtilities.convertRectangle(this, rectangle, this);

            BufferedImage bufferedImage = Utils.takeScreenshot(rectangle);

            Utils.setClipboard(bufferedImage);

            Utils.showSuccessDialog("Image copied to clipboard.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void copyWindowAsText() {
        JInternalFrame internalFrame = desktopPaneMain.getSelectedFrame();
        if (internalFrame != null) {
            CustomInternalFrame customInternalFrame =
                    (CustomInternalFrame)internalFrame;
            String str = FieldPrinter.getFieldsAsString(customInternalFrame.getCustomPanel());
            Utils.setClipboard(str);
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
        splitpaneMain = new javax.swing.JSplitPane();
        lblHolder = new javax.swing.JLabel();
        desktopPaneMain = new javax.swing.JDesktopPane();
        pnlBottom = new javax.swing.JPanel();
        pnlStatus = new javax.swing.JPanel();
        btnConsole = new javax.swing.JButton();
        btnScratchpad = new javax.swing.JButton();
        pnlConsoles = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemExit = new javax.swing.JMenuItem();
        menuWindow = new javax.swing.JMenu();
        menuItemCascadeWindows = new javax.swing.JMenuItem();
        menuItemTileWindowsHorizontally = new javax.swing.JMenuItem();
        menuItemTileWindowsVertically = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        menuItemCloseWindow = new javax.swing.JMenuItem();
        menuItemCloseAllWindows = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        menuItemScreenshot = new javax.swing.JMenuItem();
        menuItemCopyText = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemUserInformation = new javax.swing.JMenuItem();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mouse Tumor Biology Editorial Interface");
        setIconImage(getIconImage());
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                formComponentRemoved(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        splitpaneMain.setDividerLocation(200);
        splitpaneMain.setLastDividerLocation(200);
        splitpaneMain.setOneTouchExpandable(true);
        lblHolder.setText("jLabel1");
        splitpaneMain.setLeftComponent(lblHolder);

        desktopPaneMain.setDoubleBuffered(true);
        splitpaneMain.setRightComponent(desktopPaneMain);

        getContentPane().add(splitpaneMain, java.awt.BorderLayout.CENTER);

        pnlBottom.setLayout(new java.awt.BorderLayout());

        pnlBottom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        btnConsole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/Console16.png")));
        btnConsole.setText("Console");
        btnConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsoleActionPerformed(evt);
            }
        });

        pnlStatus.add(btnConsole);

        btnScratchpad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/ConsoleLight16.png")));
        btnScratchpad.setText("Scratchpad");
        btnScratchpad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScratchpadActionPerformed(evt);
            }
        });

        pnlStatus.add(btnScratchpad);

        pnlBottom.add(pnlStatus, java.awt.BorderLayout.NORTH);

        pnlConsoles.setLayout(new javax.swing.BoxLayout(pnlConsoles, javax.swing.BoxLayout.X_AXIS));

        pnlBottom.add(pnlConsoles, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlBottom, java.awt.BorderLayout.SOUTH);

        menuFile.setText("File");
        menuItemExit.setText("Exit");
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });

        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuWindow.setText("Window");
        menuItemCascadeWindows.setText("Cascade");
        menuItemCascadeWindows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCascadeWindowsActionPerformed(evt);
            }
        });

        menuWindow.add(menuItemCascadeWindows);

        menuItemTileWindowsHorizontally.setText("Tile Horizontally");
        menuItemTileWindowsHorizontally.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTileWindowsHorizontallyActionPerformed(evt);
            }
        });

        menuWindow.add(menuItemTileWindowsHorizontally);

        menuItemTileWindowsVertically.setText("Tile Vertically");
        menuItemTileWindowsVertically.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTileWindowsVerticallyActionPerformed(evt);
            }
        });

        menuWindow.add(menuItemTileWindowsVertically);

        menuWindow.add(jSeparator1);

        menuItemCloseWindow.setText("Close");
        menuItemCloseWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCloseWindowActionPerformed(evt);
            }
        });

        menuWindow.add(menuItemCloseWindow);

        menuItemCloseAllWindows.setText("Close All");
        menuItemCloseAllWindows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCloseAllWindowsActionPerformed(evt);
            }
        });

        menuWindow.add(menuItemCloseAllWindows);

        menuWindow.add(jSeparator2);

        menuItemScreenshot.setText("Take Screenshot");
        menuItemScreenshot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemScreenshotActionPerformed(evt);
            }
        });

        menuWindow.add(menuItemScreenshot);

        menuItemCopyText.setText("Copy as text");
        menuItemCopyText.setRolloverEnabled(true);
        menuItemCopyText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCopyTextActionPerformed(evt);
            }
        });

        menuWindow.add(menuItemCopyText);

        menuBar.add(menuWindow);

        menuHelp.setText("Help");
        menuItemUserInformation.setText("Update User Information");
        menuItemUserInformation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemUserInformationActionPerformed(evt);
            }
        });

        menuHelp.add(menuItemUserInformation);

        menuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jax/mgi/mtb/ei/resources/img/About16.png")));
        menuItemAbout.setText("About");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });

        menuHelp.add(menuItemAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
// TODO add your handling code here:
    }//GEN-LAST:event_formComponentResized

    private void formComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentRemoved
// TODO add your handling code here:
    }//GEN-LAST:event_formComponentRemoved

    private void menuItemCopyTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCopyTextActionPerformed
        copyWindowAsText();
    }//GEN-LAST:event_menuItemCopyTextActionPerformed

    private void menuItemScreenshotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemScreenshotActionPerformed
        takeWindowScreenshot();
    }//GEN-LAST:event_menuItemScreenshotActionPerformed

    private void menuItemCascadeWindowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCascadeWindowsActionPerformed
        cascade(this.desktopPaneMain);
    }//GEN-LAST:event_menuItemCascadeWindowsActionPerformed

    private void menuItemTileWindowsVerticallyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTileWindowsVerticallyActionPerformed
        tile(this.desktopPaneMain, TILE_VERTICALLY);
    }//GEN-LAST:event_menuItemTileWindowsVerticallyActionPerformed

    private void menuItemTileWindowsHorizontallyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTileWindowsHorizontallyActionPerformed
        tile(this.desktopPaneMain, TILE_HORIZONTALLY);
    }//GEN-LAST:event_menuItemTileWindowsHorizontallyActionPerformed

    private void menuItemCloseAllWindowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCloseAllWindowsActionPerformed
        handleCloseAll();
    }//GEN-LAST:event_menuItemCloseAllWindowsActionPerformed

    private void menuItemCloseWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCloseWindowActionPerformed
        handleClose();
    }//GEN-LAST:event_menuItemCloseWindowActionPerformed

    private void menuItemUserInformationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemUserInformationActionPerformed
        launchMTBUserEditWindow();
    }//GEN-LAST:event_menuItemUserInformationActionPerformed

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
        showAboutDialog();
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
        handleCloseAll();
        dispose();
    }//GEN-LAST:event_menuItemExitActionPerformed

    private void btnScratchpadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScratchpadActionPerformed
        showScratchpad();
    }//GEN-LAST:event_btnScratchpadActionPerformed

    private void btnConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsoleActionPerformed
        showConsole();
    }//GEN-LAST:event_btnConsoleActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        handleClose();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConsole;
    private javax.swing.JButton btnScratchpad;
    private javax.swing.JDesktopPane desktopPaneMain;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblHolder;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemCascadeWindows;
    private javax.swing.JMenuItem menuItemCloseAllWindows;
    private javax.swing.JMenuItem menuItemCloseWindow;
    private javax.swing.JMenuItem menuItemCopyText;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemScreenshot;
    private javax.swing.JMenuItem menuItemTileWindowsHorizontally;
    private javax.swing.JMenuItem menuItemTileWindowsVertically;
    private javax.swing.JMenuItem menuItemUserInformation;
    private javax.swing.JMenu menuWindow;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlConsoles;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JSplitPane splitpaneMain;
    // End of variables declaration//GEN-END:variables
}
