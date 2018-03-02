/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jax.mgi.mtb.ei.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDTO;
import org.jax.mgi.mtb.utils.DataBean;


/**
 * 
 * @author sbn
 * Parses the MTB triage URL which should contain a list of References, with the J# in the first tab separated field.
 * 
 */
public class TriageParser {

 
  public ArrayList<ReferenceDTO> getReferences(String url) {
    
    String  s = readHttpUrl(url); 

   ArrayList<ReferenceDTO> list = new ArrayList<ReferenceDTO>();
    
    Scanner pageScanner = new Scanner(s);
    pageScanner.useDelimiter("\n");
    while (pageScanner.hasNext()) {
     ReferenceDTO ref =  ReferenceDAO.getInstance().createReferenceDTO();
      String line = pageScanner.next();
      String jnum ="";
      try{
          
          String pubMedID = "";
          Scanner lineScanner = new Scanner(line);
          lineScanner.useDelimiter("\t");
         jnum = lineScanner.next();
          pubMedID = lineScanner.next();//pubmed ID
          
          DataBean bean = new DataBean();
            bean.put("JNum", jnum);
            bean.put("pubMedID",pubMedID);
            ref.setDataBean(bean);
            
            ref.setTitle(fixNulls(lineScanner.next()));       
            ref.setAuthors(fixNulls(lineScanner.next()));
            ref.setPrimaryAuthor(fixNulls(lineScanner.next()));
            ref.setCitation(fixNulls(lineScanner.next()));
            ref.setShortCitation(fixNulls(lineScanner.next()));
            ref.setJournal(fixNulls(lineScanner.next()));
            ref.setVolume(fixNulls(lineScanner.next()));
            ref.setIssue(fixNulls(lineScanner.next()));
            ref.setPages(fixNulls(lineScanner.next()));
            ref.setYear(fixNulls(lineScanner.next()));
            ref.setReviewStatus(fixNulls(lineScanner.next()));
            ref.setAbstractText(fixNulls(lineScanner.next()));
            ref.setMTBDataStatusKey(10L);  // 10 = "NEEDS REVIEW"
            ref.setPriority(null);
            
            list.add(ref);
            
      }catch(Exception e){
          Utils.log(e);
          Utils.log("\nError parsing triage file for "+jnum);
      }
    }
    return list;
  }

  private String fixNulls(String in){
      if("null".equalsIgnoreCase(in)){
          in = "";
      }
      return in;
  }
  

  private String readHttpUrl(String urlStr) {
    StringBuffer buf = new StringBuffer();
    try {
      URL url = new URL(urlStr);
      BufferedReader in = new BufferedReader(
              new InputStreamReader(
              url.openStream()));

      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        buf.append(inputLine);
        buf.append("\n");
      }
      in.close();
    } catch (Exception e) {
      Utils.log("Exception in reading triage URL...");
          Utils.log(e);
    }
    String data = buf.toString();
    return data;
  }
  
}
