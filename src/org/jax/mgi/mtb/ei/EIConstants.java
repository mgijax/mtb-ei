/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/EIConstants.java,v 1.1 2007/04/30 15:50:39 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei;

import java.awt.Color;

/**
 * Used to hold constants used in the EI.
 *
 * @author mjv
 * @date 2007/04/30 15:50:39
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/EIConstants.java,v 1.1 2007/04/30 15:50:39 mjv Exp
 */
public class EIConstants {

    // -------------------------------------------------------------- Constants

    public static final long MTB_TYPE_STRAIN = 1l;
    public static final long MTB_TYPE_MARKER = 2l;
    public static final long MTB_TYPE_ALLELE = 3l;
    public static final long MTB_TYPE_TUMOR_FREQUENCY = 5l;
    public static final long MTB_TYPE_REFERENCE = 6l;

    public static final long SITE_INFO_MGI = 1l;
    
    public static final long SITE_INFO_ENTREZ_GENE = 48L;
    
    public static final String MARKER_LABEL_TYPE_SYMBOL = "MS";
    public static final String MARKER_LABEL_TYPE_NAME = "MN";
    
    public static final String MARKER_LABEL_NAME = "Marker Name";
    public static final String MARKER_LABEL_SYMBOL = "Marker Symbol";

    public static final String USER_HOME = System.getProperty("user.home");
    public static final String PROPERTIES_FILE = "mtbei.properties";
    public static final String PROPERTIES_FILE_PATH = //USER_HOME +
                                                      //File.separatorChar +
                                                      PROPERTIES_FILE;
    public static final String EOL = System.getProperty("line.separator");

    public static final String AGENT = "AGENT";
    public static final String AGENT_TYPE = "AGENT_TYPE";
    public static final String AGENT_TYPE_BEAN = "AGENT_TYPE_BEAN";
    public static final String ALLELE1_KEY = "ALLELE1_KEY";
    public static final String ALLELE1_SYMBOL = "ALLELE1_SYMBOL";
    public static final String ALLELE1_TYPE = "ALLELE1_TYPE";
    public static final String ALLELE2_KEY = "ALLELE2_KEY";
    public static final String ALLELE2_SYMBOL = "ALLELE2_SYMBOL";
    public static final String ALLELE2_TYPE = "ALLELE2_TYPE";
    public static final String ALLELE_MARKER_ASSOC_TYPE_BEAN =
            "ALLELE_MARKER_ASSOC_TYPE_BEAN";

    public static String ANATOMICAL_SYSTEM_BEAN =
            "ANATOMICAL_SYSTEM_BEAN";

    public static String APP_NAME_TEXT =
            "Mouse Tumor Biology Editorial Interface";

    public static String APP_BUILD_TEXT = "";
    public static String ASSAY_IMAGE_COUNT = "ASSAY_IMAGE_COUNT";
    public static String ASSAY_NAME="ASSAY_NAME";
    public static String ASSAY_IMAGE_TGC_KEY = "ASSAY_IMAGE_TGC_KEY";
    public static String CHANGE = "CHANGE";
    public static String CHANGE_TYPE = "CHANGE_TYPE";
    public static String CHANGE_CHROMOSOMES = "CHANGE_CHROMOSOMES";

    public static final String IMAGE_DTO = "IMAGE_DTO";
    public static final String JNUM = "JNUM";
    public static final String ORGAN = "ORGAN";
    public static final String ORGAN_BEAN = "ORGAN_BEAN";
    public static final String ORGANISM_CHROMOSOME_BEAN =
            "ORGANISM_CHROMOSOME_BEAN";

    public static final String LABEL_TYPE_BEAN = "LABEL_TYPE_BEAN";
    public static final String MARKER_DTO = "MARKER_DTO";
    public static final String MARKER_SYMBOL = "MARKER_SYMBOL";
    public static final String MARKER_TYPE_BEAN = "MARKER_TYPE_BEAN";
    public static final String PATHOLOGY_IMAGE_HELPER_DTO =
            "PATHOLOGY_IMAGE_HELPER_DTO";
    public static final String MTB_PATHOLOGY_SEARCH_DTO =
            "MTB_PATHOLOGY_SEARCH_DTO";
    public static final String PATHOLOGY_NUM_IMAGES = "PATHOLOGY_NUM_IMAGES";
    public static final String REF_KEY = "REF_KEY";
    public static final String REFERENCE = "REFERENCE";
    public static final String PROGRESSION = "PROGRESSION";
    public static final String STRAIN_DTO = "STRAIN_DTO";
    public static final String TUMOR_CLASSIFICATION = "TUMOR_CLASSIFICATION";
    public static final String TUMOR_CLASSIFICATION_BEAN = "TUMOR_CLASSIFICATION_BEAN";
    public static final String LOCAL_IMAGE = "LOCAL_IMAGE";
    public static final String LOCAL_IMAGE_THUMB = "LOCAL_IMAGE_THUMB";
    public static final String LOCAL_IMAGE_HIGHRES = "LOCAL_IMAGE_HIGHRES";
    public static final String LOCAL_IMAGE_ZOOMIFY = "LOCAL_IMAGE_ZOOMIFY";
    public static final String IMAGE_PROBES_ARR_DTO = "IMAGE_PROBES_ARR_DTO";
    public static final String SITE_INFO = "SITE_INFO";

    public static String IMAGE_SERVER = "";
    public static String IMAGE_SERVER_PATH = "";
    public static String IMAGE_URL = "";
    public static String IMAGE_URL_PATH = "";
    
    public static String ASSAY_IMAGE_SERVER = "";
    public static String ASSAY_IMAGE_SERVER_PATH = "";
    public static String ASSAY_IMAGE_URL ="";
    public static String ASSAY_IMAGE_URL_PATH = "";


    public static String FTP_USER = "";
    public static String FTP_PASSWORD = "";
    
    public static String MGI_API_URL = "";
    public static String MGI_API_TOKEN = "";

    public static final String RELEASE_NOTES_PATH =
            "/org/jax/mgi/mtb/ei/resources/html/releasenotes.html";

    public static final String ICO_ABOUT_24 =
            "/org/jax/mgi/mtb/ei/resources/img/About24.png";
    public static final String ICO_HISTORY_24 =
            "/org/jax/mgi/mtb/ei/resources/img/History24.png";
    public static final String ICO_DATABASE_24 =
            "/org/jax/mgi/mtb/ei/resources/img/Database24.png";
    public static final String ICO_PREFERENCES_24 =
            "/org/jax/mgi/mtb/ei/resources/img/Preferences24.png";
    public static final String ICO_DELETE_16 =
            "/org/jax/mgi/mtb/ei/resources/img/Delete16.png";
    public static final String ICO_EDIT_16 =
            "/org/jax/mgi/mtb/ei/resources/img/Edit16.png";
    public static final String ICO_NEW_16 =
            "/org/jax/mgi/mtb/ei/resources/img/New16.png";
    public static final String ICO_VIEW_DATA_16 =
            "/org/jax/mgi/mtb/ei/resources/img/ViewData16.png";

    public static Color COLOR_RESULTS_HILITE = new Color(230,230,230);
    public static Color COLOR_RESULTS_SELECTION_FG = new Color(230,230,230);
    public static Color COLOR_RESULTS_SELECTION_BG = new Color(100, 100, 220);
    
    public static  String TRIAGE_URL = "";

    // ----------------------------------------------------- Instance Variables
    // none

    // ----------------------------------------------------------- Constructors

    /**
     * Prevents class instantiation.
     */
    private EIConstants() {
        // no constructor
    }

    // --------------------------------------------------------- Public Methods
    // none

    // ------------------------------------------------------ Protected Methods
    // none

    // -------------------------------------------------------- Private Methods
    // none
}
