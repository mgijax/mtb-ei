/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jax.mgi.mtb.ei.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDAO;
import org.jax.mgi.mtb.dao.gen.mtb.ReferenceDTO;
import org.jax.mgi.mtb.ei.EIConstants;
import org.jax.mgi.mtb.utils.DataBean;

public class MGIReferenceAPIUtil {

    private static String BASE_URL = EIConstants.MGI_API_URL;
    
    private static String REFERENCE_URL = BASE_URL+"reference/";
    


    private static String TOKEN = EIConstants.MGI_API_TOKEN;

    private static final String INDEXED = "Indexed";
    private static final String FULLCODED = "Full-coded";

    private boolean testing = false;
    
    
    public static void main(String[] args){
     
        MGIReferenceAPIUtil util = new MGIReferenceAPIUtil();
        
        util.REFERENCE_URL =  "http://bhmgipwi01lp.jax.org:8099/api/reference/";    
      
        util.testing = true;
        util.TOKEN="";

        ArrayList<ReferenceDTO> refs = util.getReferences();
        for(ReferenceDTO dto : refs){
            System.out.println(dto.getAuthors());
        }
        
       util.updateReferenceFullCoded("J:324452", "dab");
                
    }

    public ArrayList<ReferenceDTO> getReferences() {

        ArrayList<ReferenceDTO> list = new ArrayList<>();

        String tumorRouted = "{ \"status_Tumor_Chosen\" : 1 }";

        try {
            JSONArray items = new JSONArray(getJSON(REFERENCE_URL + "search", tumorRouted));

         //   JSONArray items = job.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject refObj = items.getJSONObject(i);
                String refKey = refObj.getString("refsKey");
                list.add(getReferenceByKey(refKey));

            }

        } catch (Exception e) {
            log(e);
            log("Unable to load references from MGI webservice");
        }
        log("Retrieved " + list.size() + " references chosen for Tumor from MGI");
        return list;
    }

    public ReferenceDTO getReferenceByKey(String refKey) throws Exception {
        String detailsString = REFERENCE_URL + refKey;
        JSONObject reference = new JSONObject(getJSON(detailsString, null));//.getJSONArray("items").getJSONObject(0);

        ReferenceDTO ref = ReferenceDAO.getInstance().createReferenceDTO();

        DataBean bean = new DataBean();
        bean.put("JNum", reference.getString("jnumid"));
        ref.setDataBean(bean);
        System.out.println(reference.getString("jnumid"));
        bean.put("pubMedID", reference.getString("pubmedid"));
        ref.setTitle(fixNulls(reference.getString("title")));
        ref.setAuthors(fixNulls(reference.getString("authors")));
        ref.setPrimaryAuthor(fixNulls(reference.getString("primaryAuthor")));
        ref.setCitation(fixNulls(reference.getString("short_citation")));
        ref.setShortCitation(fixNulls(reference.getString("short_citation")));
        ref.setJournal(fixNulls(reference.getString("journal")));
        ref.setVolume(fixNulls(reference.getString("vol")));
        ref.setIssue(fixNulls(reference.getString("issue")));
        ref.setPages(fixNulls(reference.getString("pgs")));
        ref.setYear(fixNulls(reference.getString("year")));
        ref.setIsReviewArticle((fixNulls(reference.getString("isReviewArticle")).contains("Y") ? 1 : 0));
        ref.setAbstractText(fixNulls(reference.getString("referenceAbstract")));
        ref.setMTBDataStatusKey(10L);  // 10 = "NEEDS REVIEW"
        ref.setPriority(null);

        return ref;
    }

    public boolean updateReferenceIndexed(String jNum, String userName) {
        return updateReferenceStatus(jNum, userName, INDEXED);
    }

    public boolean updateReferenceFullCoded(String jNum, String userName) {
        return updateReferenceStatus(jNum, userName, FULLCODED);
    }

    private boolean updateReferenceStatus(String jNum, String userName, String status) {

        boolean success = false;

        String jNum4URL = jNum.replace(":", "%3A");

        String responseSingle = "";
        StringBuffer response = new StringBuffer();

        HttpURLConnection connection = null;
        try {
            URL url = new URL(REFERENCE_URL + "statusUpdate?accid=" + jNum4URL + "&group=tumor&status=" + status);
            connection
                    = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("api_access_token", TOKEN);
            connection.setRequestProperty("username", userName);

            connection.connect();

            if (200 != (connection.getResponseCode())) {
                success = false;
                log("Failed to update " + jNum + " to status " + status);
                log("Response:" + connection.getResponseMessage());
            }
            InputStream in = connection.getInputStream();

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(in));
                while ((responseSingle = rd.readLine()) != null) {
                    response.append(responseSingle);
                }
                rd.close(); //close the reader

                success = true;

            } catch (IOException e) {

                log("Error reading from webservice " + url);
                log(e);

            } finally {
                if (in != null) {
                    in.close();
                }
            }

            JSONObject job = new JSONObject(response.toString());
            if (job.getString("error") != null && !"null".equals(job.getString("error"))) {
                success = false;
                log(job.getString("error") + " " + job.getString("message"));
            }

            log("MGI Reference API " + url + " called with response " + response.toString());

        } catch (Exception e) {
            log(e);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return success;
    }

    private String fixNulls(String in) {
        if ("null".equalsIgnoreCase(in)) {
            in = "";
        }
        return in;
    }

    private String getJSON(String uri, String json) {

        String responseSingle = "";
        StringBuilder response = new StringBuilder();

        HttpURLConnection connection = null;
        try {
            URL url = new URL(uri);
            connection
                    = (HttpURLConnection) url.openConnection();

            if (json != null) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true); // sending stuff
            }
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoInput(true); //we want a response
            connection.setUseCaches(false);

            if (json != null) {
                OutputStream out = connection.getOutputStream();
                try {

                    OutputStreamWriter wr = new OutputStreamWriter(out);
                    wr.write(json);
                    wr.flush();
                    wr.close();
                } catch (IOException e) {

                    log("Error writing to webservice " + uri);
                    log(e);

                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }

            // Open a stream which can read the server response
            InputStream in = connection.getInputStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(in));
                while ((responseSingle = rd.readLine()) != null) {
                    response.append(responseSingle);
                }
                rd.close(); //close the reader

            } catch (IOException e) {

                log("Error reading from webservice " + uri);
                log(e);

            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException e) {
            log("Error connecting to webservice " + uri);
            log(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();

    }

    private void log(String in) {
        if (testing) {
            System.out.println(in);
        } else {
            Utils.log(in);
        }
    }

    private void log(Exception e) {
        if (testing) {
            e.printStackTrace();
        } else {
            Utils.log(e);
        }

    }

}
