/*
 * UlliReport.java
 *
 * Created on May 5, 2006, 12:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jax.mgi.mtb.ei;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jax.mgi.mtb.dao.custom.mtb.MTBTumorUtilDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentDTO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.AgentTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganDAO;
import org.jax.mgi.mtb.dao.gen.mtb.OrganDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainDTO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeAssocDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.StrainTypeDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorClassificationDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorClassificationDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyComparator;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencySynonymsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencySynonymsDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyTreatmentsDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorFrequencyTreatmentsDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorProgressionDTO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDAO;
import org.jax.mgi.mtb.dao.gen.mtb.TumorTypeDTO;
import org.jax.mgi.mtb.dao.mtb.DAOManagerMTB;
import org.jax.mgi.mtb.utils.LabelValueBean;
import org.jax.mgi.mtb.utils.StringUtils;

/**
 *
 * @author mjv
 */
public class UlliReport {

    /** Creates a new instance of UlliReport */
    public UlliReport() {
    }

    public static void run() {

        /*
         *
MTB_id
Tumor-name
Tumor-synonym
*Strain_id
Strain-name
Strain Types
Strain General note
Treatment-type
Treatment-agent
*Organ_id
Organ-affected
Metastasizes_to
MTB_ids of corresponding metastases
Sex
Reproductive status
Tumor Frequency
Age of Onset
Age of Detection
Reference(s)
         *
         *
         *
         */
        char DELIM = '\t';
        char INNER_DELIM = ',';
        char NL = '\n';
        char Q = '[';
        char Q2 = ']';

        TumorFrequencyDAO daoTF = TumorFrequencyDAO.getInstance();
        TumorFrequencySynonymsDAO daoTFSynonyms = TumorFrequencySynonymsDAO.getInstance();
        OrganDAO daoOrgan = OrganDAO.getInstance();
        TumorClassificationDAO daoTC = TumorClassificationDAO.getInstance();
        TumorTypeDAO daoTumorType = TumorTypeDAO.getInstance();
        StrainDAO daoStrain = StrainDAO.getInstance();
        StrainTypeAssocDAO daoStrainTypeAssoc = StrainTypeAssocDAO.getInstance();
        StrainTypeDAO daoStrainType = StrainTypeDAO.getInstance();
        TumorFrequencyTreatmentsDAO daoTFTreatments = TumorFrequencyTreatmentsDAO.getInstance();
        AgentDAO daoAgent = AgentDAO.getInstance();
        AgentTypeDAO daoAgentType = AgentTypeDAO.getInstance();
        TumorProgressionDAO daoTumorProgression = TumorProgressionDAO.getInstance();
        MTBTumorUtilDAO daoTumorUtil = MTBTumorUtilDAO.getInstance();
        List<TumorFrequencyDTO> dtoTF;

        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter("C:/ullireport.txt"));;
            StringBuffer sbH = new StringBuffer();
            sbH.append(Q).append("MTBID").append(Q2).append(DELIM);
            sbH.append(Q).append("TUMOR NAME").append(Q2).append(DELIM);
            sbH.append(Q).append("TUMOR SYNONYMS").append(Q2).append(DELIM);
            sbH.append(Q).append("STRAIN NAME").append(Q2).append(DELIM);
            sbH.append(Q).append("STRAIN TYPES").append(Q2).append(DELIM);
            sbH.append(Q).append("$$$STRAIN GENERAL NOTE$$$").append(Q2).append(DELIM);
            sbH.append(Q).append("TREATMENT TYPE").append(Q2).append(DELIM);
            sbH.append(Q).append("TREATMENT AGENTS").append(Q2).append(DELIM);
            sbH.append(Q).append("ORGAN AFFECTED").append(Q2).append(DELIM);
            sbH.append(Q).append("METASTASIZES TO").append(Q2).append(DELIM);
            sbH.append(Q).append("MTBIDS OF CORRESPONDING METASTASES").append(Q2).append(DELIM);
            sbH.append(Q).append("SEX").append(Q2).append(DELIM);
            sbH.append(Q).append("REPRODUCTIVE STATUS").append(Q2).append(DELIM);
            sbH.append(Q).append("TUMOR FREQUENCY").append(Q2).append(DELIM);
            sbH.append(Q).append("AGE OF ONSET").append(Q2).append(DELIM);
            sbH.append(Q).append("AGE OF DETECTION").append(Q2).append(DELIM);
            sbH.append(Q).append("REFERENCES").append(Q2).append(NL);
            out.write(sbH.toString());

            dtoTF = daoTF.loadAll();

            Collections.sort(dtoTF, new TumorFrequencyComparator(TumorFrequencyDAO.ID__TUMORFREQUENCY_KEY));

            for (TumorFrequencyDTO dto : dtoTF) {
                // get the tumor name
                TumorTypeDTO dtoTumorType = daoTumorType.loadByPrimaryKey(dto.getTumorTypeKey());
                TumorClassificationDTO dtoTC = daoTC.loadByPrimaryKey(dtoTumorType.getTumorClassificationKey());
                OrganDTO dtoOrgan = daoOrgan.loadByPrimaryKey(dtoTumorType.getOrganKey());
                String strTumorName = StringUtils.nvl(dtoOrgan.getName(), "") +
                                      ' ' +
                                      StringUtils.nvl(dtoTC.getName(), "");

                // get the organ affected
                OrganDTO dtoOrganAffected = daoOrgan.loadByPrimaryKey(dto.getOrganAffectedKey());

                //tumor synonyms
                List<TumorFrequencySynonymsDTO> dtoTFSynonyms = daoTFSynonyms.loadByTumorFrequencyKey(dto.getTumorFrequencyKey());
                List<String> arrTFSynonyms = new ArrayList<String>();

                for (TumorFrequencySynonymsDTO dtoTFS : dtoTFSynonyms) {
                    arrTFSynonyms.add(dtoTFS.getName());
                }

                String strTFSynonyms = StringUtils.collectionToString(arrTFSynonyms, "|", "");

                // get the strain
                StrainDTO dtoStrain = daoStrain.loadByPrimaryKey(dto.getStrainKey());

                // get the strain types
                List<StrainTypeDTO> dtoStrainTypes = daoStrain.loadStrainTypeViaStrainTypeAssoc(dtoStrain);
                List<String> arrStrainTypes = new ArrayList<String>();

                for (StrainTypeDTO dtoST : dtoStrainTypes) {
                    arrStrainTypes.add(dtoST.getType());
                }

                String strStrainTypes = StringUtils.collectionToString(arrStrainTypes, "|", "");

                // get the treatments
                List<TumorFrequencyTreatmentsDTO> dtoTFTreatments = daoTFTreatments.loadByTumorFrequencyKey(dto.getTumorFrequencyKey());
                String strTreatment = "None (Spontaneous)";
                String strAgents = "";
                List<String> arrAgents = new ArrayList<String>();

                // load the agents
                if (dtoTFTreatments.size() > 0) {
                    for (TumorFrequencyTreatmentsDTO dtoTFTr : dtoTFTreatments) {
                        AgentDTO dtoAgent = daoAgent.loadByPrimaryKey(dtoTFTr.getAgentKey());
                        AgentTypeDTO dtoAgentType = daoAgentType.loadByPrimaryKey(dtoAgent.getAgentTypeKey());
                        strTreatment = dtoAgentType.getName();

                        arrAgents.add(dtoAgent.getName());
                    }
                    strAgents = StringUtils.collectionToString(arrAgents, "|", "");
                }

                // load the mets
                List<TumorProgressionDTO> dtoTumorProgression = daoTumorProgression.loadByParentKey(dto.getTumorFrequencyKey());

                List<Long> arrMets = new ArrayList<Long>();
                List<String> arrMetsOrgans = new ArrayList<String>();

                for (TumorProgressionDTO dtoTP : dtoTumorProgression) {
                    arrMets.add(dtoTP.getChildKey());

                    // look up the tf
                    TumorFrequencyDTO dtoTFTemp = daoTF.loadByPrimaryKey(dtoTP.getChildKey());
                    //look up the organ
                    OrganDTO dtoOrganMets = daoOrgan.loadByPrimaryKey(dtoTFTemp.getOrganAffectedKey());

                    arrMetsOrgans.add(dtoOrganMets.getName());
                }

                String strTFMets = StringUtils.collectionToString(arrMets, "|", "");
                String strMetsOrgans = StringUtils.collectionToString(arrMetsOrgans, "|", "");

                // load the references
                List<LabelValueBean<String,Long>> arrRefs = daoTumorUtil.getTumorRefs(dto.getTumorFrequencyKey().longValue());
                List<String> arrJNums = new ArrayList<String>();

                for (LabelValueBean<String,Long> lvb : arrRefs) {
                    arrJNums.add(lvb.getLabel());
                }

                String strJNums = StringUtils.collectionToString(arrJNums, "|", "");

                StringBuffer sb = new StringBuffer();
                sb.append(Q).append(dto.getTumorFrequencyKey()).append(Q2).append(DELIM);
                sb.append(Q).append(strTumorName).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(strTFSynonyms, "")).append(Q2).append(DELIM);
                sb.append(Q).append(dtoStrain.getStrainKey()).append(Q2).append(DELIM); // new
                sb.append(Q).append(dtoStrain.getName()).append(Q2).append(DELIM);
                sb.append(Q).append(strStrainTypes).append(Q2).append(DELIM);
                sb.append(Q).append("$$$").append(StringUtils.nvl(StringUtils.replace(StringUtils.nvl(dtoStrain.getDescription(), ""), "\n", " "), "")).append("$$$").append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(strTreatment, "")).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(strAgents, "")).append(Q2).append(DELIM);
                sb.append(Q).append(dtoOrganAffected.getOrganKey()).append(Q2).append(DELIM); // new
                sb.append(Q).append(dtoOrganAffected.getName()).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(strMetsOrgans, "")).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(strTFMets, "")).append(Q2).append(DELIM);
                sb.append(Q).append(dto.getSexKey()).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(dto.getBreedingStatus(), "")).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(dto.getIncidence(), "")).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(dto.getAgeOnset(), "")).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(dto.getAgeDetection(), "")).append(Q2).append(DELIM);
                sb.append(Q).append(StringUtils.nvl(strJNums, "")).append(Q2).append(NL);

                out.write(sb.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            DAOManagerMTB.getInstance().setJdbcDriver("");
            DAOManagerMTB.getInstance().setJdbcUrl("");
            DAOManagerMTB.getInstance().setJdbcUsername("");
            DAOManagerMTB.getInstance().setJdbcPassword("");
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
