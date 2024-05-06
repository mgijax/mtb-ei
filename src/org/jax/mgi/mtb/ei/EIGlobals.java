/**
 * Header: /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/EIGlobals.java,v 1.1 2007/04/30 15:50:39 mjv Exp
 * Author: mjv
 */
package org.jax.mgi.mtb.ei;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jax.mgi.mtb.ei.gui.MainFrame;
import org.jax.mgi.mtb.dao.custom.mtb.MTBPathologyImageUtilDAO;
import org.jax.mgi.mtb.dao.custom.mtb.MTBReferenceUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentComparator;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.AgentTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleGroupTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleGroupTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleGroupTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleMarkerAssocTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleMarkerAssocTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleMarkerAssocTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AlleleTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AnatomicalSystemComparator;
import org.jax.mgi.mtb.dao.gen.mtb.AnatomicalSystemDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AnatomicalSystemDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AssayTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.AssayTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AssayTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ChromosomeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.ChromosomeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ChromosomeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.FixativeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.FixativeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.FixativeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.LabelTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.LabelTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.LabelTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBInfoDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBInfoDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MTBUsersDTO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.MarkerTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganComparator;
import org.jax.mgi.mtb.dao.gen.mtb.OrganDAO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganDTO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganismComparator;
import org.jax.mgi.mtb.dao.gen.mtb.OrganismDAO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganismDTO;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ProbeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.SiteInfoComparator;
import org.jax.mgi.mtb.dao.gen.mtb.SiteInfoDAO;
import org.jax.mgi.mtb.dao.gen.mtb.SiteInfoDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainFamilyComparator;
import org.jax.mgi.mtb.dao.gen.mtb.StrainFamilyDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainFamilyDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorClassificationComparator;
import org.jax.mgi.mtb.dao.gen.mtb.TumorClassificationDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorClassificationDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionTypeComparator;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionTypeDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.utils.Base64;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.LabelValueDataBean;

/**
 * Used to hold global variables used in the EI.
 *
 * @author mjv
 * @date 2007/04/30 15:50:39
 * @version 1.1
 * @cvsheader /mgi/cvsroot/mgi/mtb/mtbei/src/org/jax/mgi/mtb/ei/EIGlobals.java,v 1.1 2007/04/30 15:50:39 mjv Exp
 */
public class EIGlobals {

  // -------------------------------------------------------------- Constants
  public final static int TYPE_MARKER = 1;
  public final static int TYPE_REFERENCE = 2;
  private static Properties properties = null;
  private final String BUILD_TIME = "build.time";
  private final String BUILD_NUMBER = "build.number";
  private final String JDBC_DRIVER = "jdbc.driver";
  private final String JDBC_PASSWORD = "jdbc.password";
  private final String JDBC_URL = "jdbc.url";
  private final String JDBC_USER = "jdbc.user";
  private final String JDBC_MGI_DRIVER = "jdbc.mgi.driver";
  private final String JDBC_MGI_PASSWORD = "jdbc.mgi.password";
  private final String JDBC_MGI_URL = "jdbc.mgi.url";
  private final String JDBC_MGI_USER = "jdbc.mgi.user";
  private final String IMAGE_SERVER = "image.server";
  private final String IMAGE_SERVER_PATH = "image.server.path";
  private final String IMAGE_URL = "image.url";
  private final String IMAGE_URL_PATH = "image.url.path";
  private final String ASSAY_IMAGE_SERVER = "assay.image.server";
  private final String ASSAY_IMAGE_SERVER_PATH = "assay.image.server.path";
  private final String ASSAY_IMAGE_URL = "assay.image.url";
  private final String ASSAY_IMAGE_URL_PATH = "assay.image.url.path";
  private final String FTP_USER = "ftp.user";
  private final String FTP_PASSWORD = "ftp.password";  
  private final String TRIAGE_URL="triage.url";
  private final String MGI_API_URL="mgi.api.url";
  private final String MGI_API_TOKEN = "mgi.api.token";
  
  private static EIGlobals instance = null;
  private String JDBCDriver;
  private String JDBCUrl;
  private String JDBCUser;
  private String JDBCPassword; 
  // cache the last browsed to directory
  private String directoryStr;
  // Q: Why use a map here instead of a hashtable?
  // A: Because a LinkedHashMap keeps the order of insertion
  private Map<Long, LabelValueBean<String, Long>> allAgentTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allAlleleGroupTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allAlleleMarkerAssocType = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allAlleleTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allAnatomicalSystems = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allAssayTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allMouseChromosomes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allFixatives = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allImageContributors = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allMarkerTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allOrganisms = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allOrgans = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allOrgansUnfiltered = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allProbes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allSiteInfo = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allStrainFamilies = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allStrainTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allTumorClassifications = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueBean<String, Long>> allTumorProgressionTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();
  private Map<Long, LabelValueDataBean<String, Long, Long>> allAgents = new LinkedHashMap<Long, LabelValueDataBean<String, Long, Long>>();
  private Map<Long, LabelValueDataBean<String, Long, Long>> allOrganismsChromosomes = new LinkedHashMap<Long, LabelValueDataBean<String, Long, Long>>();
  private Map<Long, List<LabelValueDataBean<String, Long, Long>>> allChromosomes = new LinkedHashMap<Long, List<LabelValueDataBean<String, Long, Long>>>();
  private Map<String, LabelValueBean<String, String>> allLabelTypes = new LinkedHashMap<String, LabelValueBean<String, String>>();
  private Map<String, LabelValueBean<String, String>> allMethods = new LinkedHashMap<String, LabelValueBean<String, String>>();
  private final String DB_VERSION = "DB_VERSION";
  private final String DB_LAST_UPDATE_DATE = "DB_LAST_UPDATE_DATE";
  private final String DB_LAST_UPDATE_TIME = "DB_LAST_UPDATE_TIME";
  private String dbType = "na";
  private String dbVersion = "na";
  private String dbLastUpdateDate = "na";
  private String dbLastUpdateTime = "na";
  private MainFrame mf = null;
  private MTBUsersDTO dtoUser = null;
  private String buildNumber = null;
  private String buildTime = null;
  private String mgi_user;
  private String mgi_password;
  private String mgi_driver;
  private String mgi_url;
  private String geoPath = "";
  
  // ----------------------------------------------------------- Constructors
  /**
   * Prevents class instantiation and instead use the getInstance() method.
   */
  private EIGlobals() {
    // nothing here, singleton pattern
    }
  // --------------------------------------------------------- Public Methods
  /**
   * Get an instance of the <code>EIGlobals</code> class.
   *
   * @return an <code>EIGlobals</code> instance
   */
  public static EIGlobals getInstance() {
    if (instance == null) {
      instance = new EIGlobals();
    }
    return instance;
  }

  /**
   * Get the database type.
   *
   * @return the database type
   */
  public String getDBType() {
    return this.dbType;
  }

  /**
   * Reinitialize the <b>Agent</b> data from the database.
   */
  public void fetchAgents() {
    // instantiate a new agents map
    allAgents = new LinkedHashMap<Long, LabelValueDataBean<String, Long, Long>>();

    // get the agents
    initAgents();
  }

  /**
   * Reinitialize the <b>StrainType</b> data from the database.
   */
  public void fetchStrainTypes() {
    // instantiate a new agents map
    allStrainTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();

    // get the strain types
    initStrainTypes();
  }

  /**
   * Reinitialize the <b>AgentTypes</b> data from the database.
   */
  public void fetchAgentTypes() {
    // instantiate a new agent types map
    allAgentTypes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();

    // get the agent types
    initAgentTypes();
  }

  /**
   * Reinitialize the <b>AnatomicalSystem</b> data from the database.
   */
  public void fetchAnatomicalSystems() {
    // instantiate a new anatomical system map
    allAnatomicalSystems = new LinkedHashMap<Long, LabelValueBean<String, Long>>();

    // get the anatomical systems
    initAnatomicalSystems();
  }

  /**
   * Reinitialize the <b>Organ</b> data from the database.  This is used in
   * the WI.
   */
  public void fetchOrgans() {
    // instantiate a new organs map
    allOrgans = new LinkedHashMap<Long, LabelValueBean<String, Long>>();

    // get the organs
    initOrgans();
  }

  /**
   * Reinitialize the <b>Organ</b> data from the database.  This is used in
   * the EI.
   */
  public void fetchOrgansUnfiltered() {
    // instantiate a new organs map
    allOrgansUnfiltered = new LinkedHashMap<Long, LabelValueBean<String, Long>>(); 

    // get the organs
    initOrgansUnfiltered();
  }

  /**
   * Reinitialize the <b>Organ</b> data from the database.  This is used in
   * the EI.
   */
  public void fetchProbes() {
    // instantiate a new probes map
    allProbes = new LinkedHashMap<Long, LabelValueBean<String, Long>>();

    // get the probes
    initProbes();
  }

  /**
   * Reinitialize the <b>TumorClassification</b> data from the database.
   */
  public void fetchTumorClassifications() {
    // instantiate a new agent types map
    allTumorClassifications = new LinkedHashMap<Long, LabelValueBean<String, Long>>();

    // get the agent types
    initTumorClassifications();
  }
  
 


  /**
   * Reinitialize the <b>StrainFamily</b> data from the database.
   */
  public void fetchStrainFamilies() {
    // instantiate a new strain families map
    allStrainFamilies = new LinkedHashMap<Long, LabelValueBean<String, Long>>();

    // get the strain families
    initStrainFamilies();
  }

  /**
   * Get the build number.
   *
   * @return the build number
   */
  public String getBuildNumber() {
    return this.buildNumber;
  }

  /**
   * Get the build time.
   *
   * @return the build time
   */
  public String getBuildTime() {
    return this.buildTime;
  }

  /**
   * Get the main frame of the EI.
   *
   * @return the <code>MainFrame</code> of the EI.
   * @see org.jax.mgi.mtb.ei.gui.MainFrame
   */
  public MainFrame getMainFrame() {
    return this.mf;
  }

  /**
   * Set the main frame of the EI.
   *
   * @param mainFrame the <code>MainFrame</code> of the EI
   * @see org.jax.mgi.mtb.ei.gui.MainFrame
   */
  public void setMainFrame(MainFrame mainFrame) {
    this.mf = mainFrame;
  }

  /**
   * Get the EI user.
   *
   * @return the <code>MTBUsersDTO</code> object
   * @see org.jax.mgi.mtb.dao.gen.MTBUsersDTO
   */
  public MTBUsersDTO getMTBUsersDTO() {
    return this.dtoUser;
  }

  /**
   * Set the EI user.
   *
   * @param dto the <code>MTBUsersDTO</code> object
   * @see org.jax.mgi.mtb.dao.gen.MTBUsersDTO
   */
  public void setMTBUsersDTO(MTBUsersDTO dto) {
    this.dtoUser = dto;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueDataBean</code> objects
   * representing <b>Agent</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueDataBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueDataBean
   */
  public Map<Long, LabelValueDataBean<String, Long, Long>> getAgents() {
    return this.allAgents;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>AgentType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getAgentTypes() {
    return this.allAgentTypes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>AlleleGroupType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getAlleleGroupTypes() {
    return this.allAlleleGroupTypes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>AssayType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getAssayTypes() {
    return this.allAssayTypes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>LabelType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<String, LabelValueBean<String, String>> getLabelTypes() {
    return this.allLabelTypes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>MarkerType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getMarkerTypes() {
    return this.allMarkerTypes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>AlleleMarkerAssocType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getAlleleMarkerAssocTypes() {
    return this.allAlleleMarkerAssocType;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>AlleleType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getAlleleTypes() {
    return this.allAlleleTypes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>AnatomicalSystem</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getAnatomicalSystems() {
    return this.allAnatomicalSystems;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>Chromosome</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, List<LabelValueDataBean<String, Long, Long>>> getChromosomes() {
    return this.allChromosomes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>Chromosome</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getMouseChromosomes() {
    return this.allMouseChromosomes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>Fixative</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getFixatives() {
    return this.allFixatives;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>Organism</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getOrganisms() {
    return this.allOrganisms;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueDataBean</code> objects
   * representing <b>Organism</b> and <b>Chromosome</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueDataBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueDataBean<String, Long, Long>> getOrganismChromosomes() {
    return this.allOrganismsChromosomes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>Organ</b> data.  This is used in the WI.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getOrgans() {
    return this.allOrgans;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>Organ</b> data. This is used in the EI.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getOrgansUnfiltered() {
    return this.allOrgansUnfiltered;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>Probe</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getProbes() {
    return this.allProbes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>SiteInfo</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getSiteInfo() {
    return this.allSiteInfo;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>StrainFamilies</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getStrainFamilies() {
    return this.allStrainFamilies;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>StrainType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getStrainTypes() {
    return this.allStrainTypes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>TumorClassifications</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getTumorClassifications() {
    return this.allTumorClassifications;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>TumorProgressionType</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getTumorProgressionTypes() {
    return this.allTumorProgressionTypes;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>ImageContributors</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<Long, LabelValueBean<String, Long>> getImageContributors() {
    return this.allImageContributors;
  }
 

  /**
   * Return the JDBC driver used for the connection.
   *
   * @return the JDBC driver
   */
  public String getJdbcDriver() {
    return this.JDBCDriver;
  }

  /**
   * Return the JDBC URL used for the connection.
   *
   * @return the JDBC URL
   */
  public String getJdbcUrl() {
    return this.JDBCUrl;
  }

  /**
   * Return the JDBC user used for the connection.
   *
   * @return the JDBC user
   */
  public String getJdbcUser() {
    return this.JDBCUser;
  }

  /**
   * Return the JDBC password used for the connection.
   *
   * @return the JDBC password
   */
  public String getJdbcPassword() {
    return this.JDBCPassword;
  }

  /**
   * Returns a <code>Map</code> of <code>LabelValueBean</code> objects
   * representing <b>Probe</b> data.
   *
   * @return a <code>Map</code> of <code>LabelValueBean</code> objects
   * @see org.jax.mgi.mtb.utils.LabelValueBean
   */
  public Map<String, LabelValueBean<String, String>> getMethods() {
    return this.allMethods;
  }

  /**
   * Get the version of the database.
   *
   * @return the version of the database
   */
  public final String getDBVersion() {
    return this.dbVersion;
  }

  /**
   * Get the last date the database was updated.
   *
   * @return a <code>String</code> of the last date the database was updated
   * @see #getDBLastUpdateTime()
   */
  public final String getDBLastUpdateDate() {
    return this.dbLastUpdateDate;
  }

  /**
   * Get the last time the database was updated.
   *
   * @return a <code>String</code> of the last time the database was updated
   * @see #getDBLastUpdateDate()
   */
  public final String getDBLastUpdateTime() {
    return this.dbLastUpdateTime;
  }

  /**
   * Initialize the global variables from the database.
   */
  public void initDB() {
    try {
      initMouseChromosomes();
      initAllChromosomes();
      initAlleleTypes();
      initAlleleGroupTypes();
      initAlleleMarkerAssocTypes();
      initAssayTypes();
      initMarkerTypes();
      initAgentTypes();
      initAgents();
      initAnatomicalSystems();
      initFixatives();
      initOrganisms();
      initOrganismChromosomes();
      initOrgans();
      initOrgansUnfiltered();
      initLabelTypes();
      initProbes();
      initSiteInfo();
      initStrainFamilies();
      initStrainTypes();
      initTumorClassifications();
      initTumorProgressionTypes();
      initImageContributors();
      initMethods();
      initDatabaseInfo();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  
   public void reloadDB() {
    try {
 //     fetchMouseChromosomes();
  //  fetchAllChromosomes();
  //    fetchAlleleTypes();
   //   fetchAlleleGroupTypes();
    ///  fetchAlleleMarkerAssocTypes();
     // fetchAssayTypes();
    //  fetchMarkerTypes();
      fetchAgentTypes();
      fetchAgents();
      fetchAnatomicalSystems();
    //  fetchFixatives();
     // fetchOrganisms();
    // fetchOrganismChromosomes();
      fetchOrgans();
      fetchOrgansUnfiltered();
      //fetchLabelTypes();
      fetchProbes();
     // fetchSiteInfo();
     fetchStrainFamilies();
      fetchStrainTypes();
      fetchTumorClassifications();
   //  fetchTumorProgressionTypes();
   //  fetchImageContributors();
    //  fetchMethods();

   
    } catch (Exception e) {
        e.printStackTrace();
    }
  }

  public String getMGIUser() {
    return this.mgi_user;
  }

  public String getMGIPassword() {
    return this.mgi_password;
  }

  public String getMGIUrl() {
    return this.mgi_url;
  }

  public String getMGIDriver() {
    return this.mgi_driver;
  }

  public String getDirectoryStr() {
    return this.directoryStr;
  }

  public void setDirectoryStr(String dirIn) {
    this.directoryStr = dirIn;
  }

  /**
   *
   * @param props
   */
  public void setProperties(Properties props) {
    //wiVersion = props.getProperty(WI_VERSION);
    properties = props;
    //DAOFactoryMTB.setProperties(props);
    try {
      this.JDBCDriver = properties.getProperty(JDBC_DRIVER);
      this.JDBCUrl = properties.getProperty(JDBC_URL);
      this.JDBCUser = properties.getProperty(JDBC_USER);
      this.JDBCPassword = properties.getProperty(JDBC_PASSWORD);

      DAOManagerMTB.getInstance().setJdbcDriver(this.JDBCDriver);
      DAOManagerMTB.getInstance().setJdbcUrl(this.JDBCUrl);
      DAOManagerMTB.getInstance().setJdbcUsername(this.JDBCUser);
      DAOManagerMTB.getInstance().setJdbcPassword(this.JDBCPassword);

      mgi_user = properties.getProperty(JDBC_MGI_USER);
      mgi_password = properties.getProperty(JDBC_MGI_PASSWORD);
      mgi_driver = properties.getProperty(JDBC_MGI_DRIVER);
      mgi_url = properties.getProperty(JDBC_MGI_URL);

      buildTime = properties.getProperty(BUILD_TIME);
      buildNumber = properties.getProperty(BUILD_NUMBER);

      EIConstants.IMAGE_SERVER = properties.getProperty(IMAGE_SERVER);
      EIConstants.IMAGE_SERVER_PATH = properties.getProperty(IMAGE_SERVER_PATH);
      EIConstants.IMAGE_URL = properties.getProperty(IMAGE_URL);
      EIConstants.IMAGE_URL_PATH = properties.getProperty(IMAGE_URL_PATH);

      EIConstants.ASSAY_IMAGE_SERVER = properties.getProperty(ASSAY_IMAGE_SERVER);
      EIConstants.ASSAY_IMAGE_SERVER_PATH = properties.getProperty(ASSAY_IMAGE_SERVER_PATH);
      EIConstants.ASSAY_IMAGE_URL = properties.getProperty(ASSAY_IMAGE_URL);
      EIConstants.ASSAY_IMAGE_URL_PATH = properties.getProperty(ASSAY_IMAGE_URL_PATH);


      EIConstants.FTP_USER = properties.getProperty(FTP_USER);
      EIConstants.FTP_PASSWORD = Base64.decode(properties.getProperty(FTP_PASSWORD));
      
      EIConstants.MGI_API_URL = properties.getProperty(MGI_API_URL);
      EIConstants.MGI_API_TOKEN = properties.getProperty(MGI_API_TOKEN);
      
      
      EIConstants.TRIAGE_URL = properties.getProperty(TRIAGE_URL);

      // enumerate all properties
      Enumeration e = props.propertyNames();
      while (e.hasMoreElements()) {
        // Get property name
        String propName = (String) e.nextElement();

        // Get property value
        String propValue = (String) props.get(propName);

        
      }

      dbType = properties.getProperty("app.version");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public long getRefByAcc(String acc) {
    MTBReferenceUtilDAO dao = MTBReferenceUtilDAO.getInstance();
    return dao.getReferenceKeyByAccession(acc);
  }

  public String getJNumByRef(long key) {
    MTBReferenceUtilDAO dao = MTBReferenceUtilDAO.getInstance();
    return dao.getJNumByReference(key);
  }

  /**
   * Determine if debugging is on or off.
   *
   * @return <code>true</code> if debugging is turned on, <code>false</code>
   * otherwise
   */
  public boolean debugging() {
    return true;
  }

  // ------------------------------------------------------ Protected Methods
  // none

  // -------------------------------------------------------- Private Methods
  /**
   * Load and sort the agents from the database.
   */
  private void initAgents() {
    try {
      AgentDAO agentDAO = AgentDAO.getInstance();
      List<AgentDTO> listAgents = agentDAO.loadAll();

      Collections.sort(listAgents,
              new AgentComparator(AgentDAO.ID_NAME));

      for (AgentDTO dto : listAgents) {
        allAgents.put(dto.getAgentKey(),
                new LabelValueDataBean<String, Long, Long>(dto.getName(),
                dto.getAgentKey(), dto.getAgentTypeKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the agent types from the database.
   */
  private void initAgentTypes() {
    Map<Long, LabelValueBean<String, Long>> agentTypes =
            new LinkedHashMap<Long, LabelValueBean<String, Long>>();

    try {
      AgentTypeDAO agentTypeDAO = AgentTypeDAO.getInstance();
      List<AgentTypeDTO> listAgentTypes = agentTypeDAO.loadAll();

      Collections.sort(listAgentTypes,
              new AgentTypeComparator(AgentTypeDAO.ID_NAME));

      for (AgentTypeDTO dto : listAgentTypes) {
        agentTypes.put(dto.getAgentTypeKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getAgentTypeKey()));
      }

      allAgentTypes.put(0L,
              new LabelValueBean<String, Long>("None (spontaneous)", 0L));
      allAgentTypes.putAll(agentTypes);

    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort the allele group types from the database.
   */
  private void initAlleleGroupTypes() {
    try {
      AlleleGroupTypeDAO alleleGroupTypeDAO =
              AlleleGroupTypeDAO.getInstance();
      List<AlleleGroupTypeDTO> listAllelGroupTypes =
              alleleGroupTypeDAO.loadAll();

      Collections.sort(listAllelGroupTypes,
              new AlleleGroupTypeComparator(AlleleGroupTypeDAO.ID_NAME));

      for (AlleleGroupTypeDTO dto : listAllelGroupTypes) {
        allAlleleGroupTypes.put(dto.getAlleleGroupTypeKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getAlleleGroupTypeKey()));
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort the allele types from the database.
   */
  private void initAlleleTypes() {
    try {
      AlleleTypeDAO alleleTypeDAO = AlleleTypeDAO.getInstance();
      List<AlleleTypeDTO> listAlleleTypes = alleleTypeDAO.loadAll();

      Collections.sort(listAlleleTypes,
              new AlleleTypeComparator(AlleleTypeDAO.ID_SORTORDER));

      for (AlleleTypeDTO dto : listAlleleTypes) {
        allAlleleTypes.put(dto.getAlleleTypeKey(),
                new LabelValueBean<String, Long>(dto.getType(),
                dto.getAlleleTypeKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the allele types from the database.
   */
  private void initAssayTypes() {
    try {
      AssayTypeDAO assayTypeDAO = AssayTypeDAO.getInstance();
      List<AssayTypeDTO> listAssayTypes = assayTypeDAO.loadAll();

      Collections.sort(listAssayTypes,
              new AssayTypeComparator(AssayTypeDAO.ID_NAME));

      for (AssayTypeDTO dto : listAssayTypes) {
        allAssayTypes.put(dto.getAssayTypeKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getAssayTypeKey()));
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort the marker types from the database.
   */
  private void initMarkerTypes() {
    try {
      MarkerTypeDAO markerTypeDAO = MarkerTypeDAO.getInstance();
      List<MarkerTypeDTO> listMarkerTypes = markerTypeDAO.loadAll();

      Collections.sort(listMarkerTypes,
              new MarkerTypeComparator(MarkerTypeDAO.ID_NAME));

      for (MarkerTypeDTO dto : listMarkerTypes) {
        allMarkerTypes.put(dto.getMarkerTypeKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getMarkerTypeKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the label types from the database.
   */
  private void initLabelTypes() {
    try {
      LabelTypeDAO labelTypeDAO = LabelTypeDAO.getInstance();
      List<LabelTypeDTO> listLabelTypes = labelTypeDAO.loadAll();

      Collections.sort(listLabelTypes,
              new LabelTypeComparator(LabelTypeDAO.ID_TYPE));

      for (LabelTypeDTO dto : listLabelTypes) {
        allLabelTypes.put(dto.getLabelTypeKey(),
                new LabelValueBean<String, String>(dto.getType(),
                dto.getLabelTypeKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the allele marker association types from the database.
   */
  private void initAlleleMarkerAssocTypes() {
    try {
      AlleleMarkerAssocTypeDAO assocTypeDAO =
              AlleleMarkerAssocTypeDAO.getInstance();
      List<AlleleMarkerAssocTypeDTO> listTypes = assocTypeDAO.loadAll();

      Collections.sort(listTypes,
              new AlleleMarkerAssocTypeComparator(
              AlleleMarkerAssocTypeDAO.ID_NAME));

      for (AlleleMarkerAssocTypeDTO dto : listTypes) {
        allAlleleMarkerAssocType.put(dto.getAlleleMarkerAssocTypeKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getAlleleMarkerAssocTypeKey()));
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort the anatomical systems from the database.
   */
  private void initAnatomicalSystems() {
    try {
      AnatomicalSystemDAO anatomicalSystemDAO =
              AnatomicalSystemDAO.getInstance();
      List<AnatomicalSystemDTO> listSystems =
              anatomicalSystemDAO.loadAll();

      Collections.sort(listSystems,
              new AnatomicalSystemComparator(
              AnatomicalSystemDAO.ID_NAME));

      for (AnatomicalSystemDTO dto : listSystems) {
        allAnatomicalSystems.put(dto.getAnatomicalSystemKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getAnatomicalSystemKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the chromosomes and organisms from the database.
   */
  private void initOrganismChromosomes() {
    try {
      ChromosomeDAO chromosomeDAO = ChromosomeDAO.getInstance();
      List<ChromosomeDTO> listChrom = chromosomeDAO.loadAll();

      Collections.sort(listChrom,
              new ChromosomeComparator(ChromosomeDAO.ID_ORDERNUM));

      for (ChromosomeDTO dto : listChrom) {
        LabelValueBean<String, Long> bean =
                allOrganisms.get(dto.getOrganismKey());
        if (bean == null) {
         
        } else {
          LabelValueDataBean<String, Long, Long> newBean =
                  new LabelValueDataBean<String, Long, Long>();
          newBean.setLabel(bean.getLabel() + " - " +
                  dto.getChromosome());
          newBean.setValue(dto.getChromosomeKey());
          newBean.setData(bean.getValue());
          allOrganismsChromosomes.put(dto.getChromosomeKey(),
                  newBean);
        }
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort all the mouse chromosomes from the database.
   */
  private void initMouseChromosomes() {
    try {
      ChromosomeDAO chromosomeDAO = ChromosomeDAO.getInstance();
      List<ChromosomeDTO> listChrom =
              chromosomeDAO.loadByOrganismKey(new Long(1)); // 1 = mouse

      Collections.sort(listChrom,
              new ChromosomeComparator(ChromosomeDAO.ID_ORDERNUM));

      for (ChromosomeDTO dto : listChrom) {
        allMouseChromosomes.put(dto.getChromosomeKey(),
                new LabelValueBean<String, Long>(dto.getChromosome(),
                dto.getChromosomeKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort all the chromosomes from the database.
   */
  private void initAllChromosomes() {
    try {
      ChromosomeDAO chromosomeDAO = ChromosomeDAO.getInstance();
      List<ChromosomeDTO> listChrom = chromosomeDAO.loadAll();

      Collections.sort(listChrom, new ChromosomeComparator(ChromosomeDAO.ID_ORDERNUM));

      for (ChromosomeDTO dto : listChrom) {
        List<LabelValueDataBean<String, Long, Long>> chroms = allChromosomes.get(dto.getOrganismKey());
        if ((chroms == null) || (chroms.size() == 0)) {
          chroms = new ArrayList<LabelValueDataBean<String, Long, Long>>();
        }
        LabelValueDataBean<String, Long, Long> lvd = new LabelValueDataBean<String, Long, Long>();
        lvd.setLabel(dto.getChromosome());
        lvd.setValue(dto.getChromosomeKey());
        lvd.setData(dto.getOrganismKey());
        chroms.add(lvd);
        allChromosomes.put(dto.getOrganismKey(), chroms);
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load the database information from the database.
   */
  private void initDatabaseInfo() {
    try {
      MTBInfoDAO dao = MTBInfoDAO.getInstance();
      List<MTBInfoDTO> listInfo = dao.loadAll();

      for (MTBInfoDTO dto : listInfo) {
        if (dto.getMTBProperty().equals(DB_VERSION)) {
          dbVersion = dto.getMTBValue();
        } else if (dto.getMTBProperty().equals(DB_LAST_UPDATE_DATE)) {
          dbLastUpdateDate = dto.getMTBValue();
        } else if (dto.getMTBProperty().equals(DB_LAST_UPDATE_TIME)) {
          dbLastUpdateTime = dto.getMTBValue();
        }
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort the fixatives from the database.
   */
  private void initFixatives() {
    try {
      FixativeDAO fixativeDAO = FixativeDAO.getInstance();
      List<FixativeDTO> listFixatives = fixativeDAO.loadAll();

      Collections.sort(listFixatives,
              new FixativeComparator(FixativeDAO.ID_NAME));

      for (FixativeDTO dto : listFixatives) {
        allFixatives.put(dto.getFixativeKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getFixativeKey()));
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load the image contributors from the database.
   */
  private void initImageContributors() {
    try {
      MTBPathologyImageUtilDAO pathUtilDAO =
              MTBPathologyImageUtilDAO.getInstance();
      List<LabelValueBean<String, Long>> list =
              pathUtilDAO.getImageContributors();

      for (LabelValueBean<String, Long> dto : list) {
        allImageContributors.put(dto.getValue(), dto);
      }
    } catch (Exception e) {
      
    }
  }

  /**
   * Load and sort the stains/methods from the database.
   */
  private void initMethods() {
    try {
      MTBPathologyImageUtilDAO pathUtilDAO =
              MTBPathologyImageUtilDAO.getInstance();
      List<LabelValueBean<String, String>> l = pathUtilDAO.getMethods();

      for (LabelValueBean<String, String> dto : l) {
        allMethods.put(dto.getValue(),
                new LabelValueBean<String, String>(dto.getLabel(),
                dto.getValue()));
      }
    } catch (Exception e) {
      
    }
  }

  /**
   * Load and sort the organisms from the database.
   */
  private void initOrganisms() {
    try {
      OrganismDAO organismDAO = OrganismDAO.getInstance();
      List<OrganismDTO> listOrganisms = organismDAO.loadAll();

      Collections.sort(listOrganisms,
              new OrganismComparator(OrganismDAO.ID_COMMONNAME));

      for (OrganismDTO dto : listOrganisms) {
        allOrganisms.put(dto.getOrganismKey(),
                new LabelValueBean<String, Long>(dto.getCommonName(),
                dto.getOrganismKey()));
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort the organs from the database.
   */
  private void initOrgans() {
    try {
      OrganDAO organDAO = OrganDAO.getInstance();
      List<OrganDTO> listOrgans = organDAO.loadAll();

      Collections.sort(listOrgans, new OrganComparator(OrganDAO.ID_NAME));

      for (OrganDTO dto : listOrgans) {
        // not adding the "(Unspecified organ)" entries
        if (dto.getName().indexOf("Unspecified") == -1) {
          allOrgans.put(dto.getOrganKey(),
                  new LabelValueBean<String, Long>(dto.getName(),
                  dto.getOrganKey()));
        }
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort all the organs from the database.
   */
  private void initOrgansUnfiltered() {
    try {
      OrganDAO organDAO = OrganDAO.getInstance();
      List<OrganDTO> listOrgans = organDAO.loadAll();

      Collections.sort(listOrgans, new OrganComparator(OrganDAO.ID_NAME));

      Map<Long, LabelValueBean<String, Long>> unspecified =
              new LinkedHashMap<Long, LabelValueBean<String, Long>>();

      for (OrganDTO dto : listOrgans) {
        // not adding the "(Unspecified organ)" entries in order
        if (dto.getName().indexOf("Unspecified") == -1) {
          allOrgansUnfiltered.put(dto.getOrganKey(),
                  new LabelValueBean<String, Long>(dto.getName(),
                  dto.getOrganKey()));
        } else {
          LabelValueBean<String, Long> anaBean =
                  allAnatomicalSystems.get(
                  dto.getAnatomicalSystemKey());
          unspecified.put(dto.getOrganKey(),
                  new LabelValueBean<String, Long>(dto.getName() +
                  " - " +
                  anaBean.getLabel(),
                  dto.getOrganKey()));
        }
      }

      allOrgansUnfiltered.putAll(unspecified);
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the probes from the database.
   */
  private void initProbes() {
    try {
      ProbeDAO probeDAO = ProbeDAO.getInstance();
      List<ProbeDTO> listProbes = probeDAO.loadAll();

      Collections.sort(listProbes, new ProbeComparator(ProbeDAO.ID_NAME));

      for (ProbeDTO dto : listProbes) {
        allProbes.put(dto.getProbeKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getProbeKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the site information from the database.
   */
  private void initSiteInfo() {
    try {
      SiteInfoDAO siteInfoDAO = SiteInfoDAO.getInstance();
      List<SiteInfoDTO> listSiteInfo = siteInfoDAO.loadAll();

      Collections.sort(listSiteInfo, new SiteInfoComparator(SiteInfoDAO.ID_NAME));

      for (SiteInfoDTO dto : listSiteInfo) {
        allSiteInfo.put(dto.getSiteInfoKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getSiteInfoKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the strain families from the database.
   */
  private void initStrainFamilies() {
    try {
      StrainFamilyDAO strainFamilyDAO = StrainFamilyDAO.getInstance();
      List<StrainFamilyDTO> listStrainFamilies =
              strainFamilyDAO.loadAll();

      Collections.sort(listStrainFamilies,
              new StrainFamilyComparator(StrainFamilyDAO.ID_FAMILY));

      for (StrainFamilyDTO dto : listStrainFamilies) {
        allStrainFamilies.put(dto.getStrainFamilyKey(),
                new LabelValueBean<String, Long>(dto.getFamily(),
                dto.getStrainFamilyKey()));
      }
    } catch (SQLException sqle) {
    }
  }

  /**
   * Load and sort the strain types from the database.
   */
  private void initStrainTypes() {
    try {
      StrainTypeDAO strainTypeDAO = StrainTypeDAO.getInstance();
      List<StrainTypeDTO> listStrainTypes = strainTypeDAO.loadAll();

      Collections.sort(listStrainTypes,
              new StrainTypeComparator(StrainTypeDAO.ID_TYPE));

      for (StrainTypeDTO dto : listStrainTypes) {
        allStrainTypes.put(dto.getStrainTypeKey(),
                new LabelValueBean<String, Long>(dto.getType(),
                dto.getStrainTypeKey()));
      }
    } catch (SQLException sqle) {
     
    }
  }

  /**
   * Load and sort the tumor classifications from the database.
   */
  private void initTumorClassifications() {
    try {
      TumorClassificationDAO tumorClassificationDAO =
              TumorClassificationDAO.getInstance();
      List<TumorClassificationDTO> listTumorClassifications =
              tumorClassificationDAO.loadAll();

      Collections.sort(listTumorClassifications,
              new TumorClassificationComparator(
              TumorClassificationDAO.ID_NAME));

      for (TumorClassificationDTO dto : listTumorClassifications) {
        allTumorClassifications.put(dto.getTumorClassificationKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getTumorClassificationKey()));
      }
    } catch (SQLException sqle) {
      
    }
  }

  /**
   * Load and sort the tumor progression types from the database.
   */
  private void initTumorProgressionTypes() {
    try {
      TumorProgressionTypeDAO progDAO =
              TumorProgressionTypeDAO.getInstance();
      List<TumorProgressionTypeDTO> listTumorProgressionTypes =
              progDAO.loadAll();

      Collections.sort(listTumorProgressionTypes,
              new TumorProgressionTypeComparator(progDAO.ID_NAME));

      for (TumorProgressionTypeDTO dto : listTumorProgressionTypes) {
        allTumorProgressionTypes.put(dto.getTumorProgressionTypeKey(),
                new LabelValueBean<String, Long>(dto.getName(),
                dto.getTumorProgressionTypeKey()));
      }
    } catch (SQLException sqle) {
      
    }
  }
  
 
   
   public ArrayList<LabelValueBean<String, String>> getReferencePriority(){
     
     ArrayList<LabelValueBean<String,String>> priority = new ArrayList<LabelValueBean<String,String>>();
     
     LabelValueBean<String,String> bean0 = new LabelValueBean<String,String>();
     bean0.setLabel("--SELECT--");
     bean0.setValue("null");
     priority.add(bean0);
     
     LabelValueBean<String,String> bean = new LabelValueBean<String,String>();
     bean.setLabel("Top");
     bean.setValue("5");
     priority.add(bean);
     
      LabelValueBean<String,String> bean1 = new LabelValueBean<String,String>();
     bean1.setLabel("High");
     bean1.setValue("4");
     priority.add(bean1);
     
      LabelValueBean<String,String> bean2 = new LabelValueBean<String,String>();
     bean2.setLabel("Medium");
     bean2.setValue("3");
     priority.add(bean2);
     
      LabelValueBean<String,String> bean3 = new LabelValueBean<String,String>();
     bean3.setLabel("Low");
     bean3.setValue("2");
     priority.add(bean3);
     
      LabelValueBean<String,String> bean4 = new LabelValueBean<String,String>();
     bean4.setLabel("Review");
     bean4.setValue("1");
     priority.add(bean4);
     
     
      LabelValueBean<String,String> bean5 = new LabelValueBean<String,String>();
     bean5.setLabel("Editorial");
     bean5.setValue("0");
     priority.add(bean5);
     
      LabelValueBean<String,String> bean6 = new LabelValueBean<String,String>();
     bean6.setLabel("Rejected");
     bean6.setValue("-1");
     priority.add(bean6);
     
     
     
     return priority;
   }
   

          
          
   // used to access the getMetaDB sqlite database. Path will vary for EI users
  // set the first time the path is provied by the user


  public String getGeoPath() {
    return geoPath;
  }

  public void setGeoPath(String geoPath) {
    this.geoPath = geoPath;
  }
}
