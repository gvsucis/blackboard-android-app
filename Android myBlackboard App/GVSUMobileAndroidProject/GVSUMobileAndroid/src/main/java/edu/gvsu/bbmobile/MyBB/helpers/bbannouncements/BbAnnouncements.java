package edu.gvsu.bbmobile.MyBB.helpers.bbannouncements;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;
import edu.gvsu.bbmobile.MyBB.OnFetchCompleted;
import edu.gvsu.bbmobile.MyBB.OnTaskCompleted;
import edu.gvsu.bbmobile.MyBB.helpers.FetchFromBb;
import edu.gvsu.bbmobile.MyBB.helpers.FetchStringFromBb;


/**
 * Created by romeroj on 9/30/13.
 */
public class BbAnnouncements implements OnTaskCompleted{
    private List<Cookie> cookies = null;
    private OnFetchCompleted listener;
    private List<BbAnnouncement> anns = new ArrayList<BbAnnouncement>();
    private boolean lastFetchWorked;
    private int fetchType;
    private int fetchDataType;
    private FetchAnnXMLFromBb fetchXML;
    private FetchStringFromBb fetchString;
    private String fetchResults;


    private static final String FETCH_AL = MyGlobal.bbBaseUrl + "getAnnForUser.jsp";
    private static final String FETCH_ALL_XML = MyGlobal.bbBaseUrl + "getAnnForUserWithDesc.jsp";
    private static final String FETCH_ONE = MyGlobal.bbBaseUrl + "getAnnById.jsp";
    private static final String FETCH_ONE_BODY = MyGlobal.bbBaseUrl + "getAnnBodyById.jsp";
    private static final String FETCH_DASH = MyGlobal.bbBaseUrl + "getRecentAnn.jsp";

    private static final int FETCH_ONE_BODY_TYPE = 201;

    public static final int DASH_TYPE = 0;
    public static final int COMPLETE_TYPE = 1;
    public static final int SINGLE_TYPE = 2;
    public static final int XML_DATA = 10;
    public static final int JSON_DATA = 11;
    public static final int HTML_DATA = 12;

    public static int ANN_TYPE = 101;
    public static String ANN_BODY_URL = MyGlobal.bbBaseUrl + "getAnnBodyById.jsp?ann_id=";

    public BbAnnouncements(OnFetchCompleted l){
        listener = l;

    }

    public void fetchAll(String userName){

        this.fetchType = COMPLETE_TYPE;
        this.fetchDataType = JSON_DATA;
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_AL, userName);

    }

    public void fetchAllWithDescriptions(String username){
        this.fetchType = COMPLETE_TYPE;
        this.fetchDataType = XML_DATA;
        fetchXML = new FetchAnnXMLFromBb(this);
        fetchXML.execute(FETCH_ALL_XML);
    }

    public void fetchOne(String strId, String userName){
        this.fetchType = SINGLE_TYPE;
        this.fetchDataType = XML_DATA;
        fetchXML = new FetchAnnXMLFromBb(this);
        fetchXML.execute(FETCH_ONE + "?ann_id=" + strId, userName);

    }
    public void fetchOneBody(String strId, String userName){
        this.fetchDataType = HTML_DATA;
        this.fetchType = FETCH_ONE_BODY_TYPE;
        fetchString = new FetchStringFromBb(this);
        fetchString.execute(FETCH_ONE_BODY + "?ann_id=" + strId, userName);

    }



    public void fetchForDash(String userName, Integer limit){
        this.fetchType = DASH_TYPE;
        this.fetchDataType = JSON_DATA;
        if(limit != null && limit > 0){
            FetchFromBb fetch = new FetchFromBb(this);
            fetch.execute(FETCH_DASH + "?limit=" + String.valueOf(limit), userName);
        }
        else{
            FetchFromBb fetch = new FetchFromBb(this);
            fetch.execute(FETCH_DASH, userName);
        }

    }


    @Override
    public void onTaskCompleted(Boolean isAuth, List<Cookie> cookies) {

    }

    @Override
    public void onTaskCompleted(Boolean isGood, List<Cookie> cookies, JSONArray jsonArray) {
        this.cookies = cookies;
        if(isGood){

            /**
             * IF TYPE SI COMPLETE OR DASH TYPE
             */
            if((fetchType == COMPLETE_TYPE && fetchDataType == JSON_DATA) || (fetchDataType == JSON_DATA && fetchType == DASH_TYPE)){
                this.lastFetchWorked = true;
                for(int i = 0; i < jsonArray.length(); i++){
                    BbAnnouncement bbann = new BbAnnouncement();
                    try {
                        JSONObject currAnn = jsonArray.getJSONObject(i);

                        bbann.id = currAnn.getString("id");
                        bbann.crsId = currAnn.getString("crs_id");
                        bbann.crsName = currAnn.getString("crs_name");
                        bbann.label = currAnn.getString("label");
                        bbann.description = currAnn.getString("desc_preview");
                        bbann.pos = currAnn.getString("pos");
                        bbann.isCrsTitle = false;


                        //for dashboard
                        if(this.fetchType == DASH_TYPE){
                            bbann.post_date = currAnn.getString("post_date");
                        }
                        else{
                            bbann.post_date = "";
                        }
                        //TODO: look at checking for a good grade item and handling if not one. can use build in functions of bbgrade



                    } catch (JSONException e) {
                        // TODO: logging for error
                        e.printStackTrace();
                    }
                    this.anns.add(bbann);
                }
                listener.onFetchCompleted(true);
            }
            /**
             * THIS IF FETCH TYPE SINGLE ANNOUNCEMENT
             * ALSO WILL BE TYPE XML_DATA
             */
            else if(fetchType == SINGLE_TYPE && fetchDataType == XML_DATA){
                this.lastFetchWorked = true;
                anns = fetchXML.getItems();
                if(anns.get(0).emImageLinks.size() > 0){

                    FetchFromBb fetchFromBb = new FetchFromBb(this);

                    //fetchFromBb.execute(anns.get(0).emImageLinks.get(0));
                }
                listener.onFetchCompleted(true);
            }
            else if(fetchType == COMPLETE_TYPE && fetchDataType == XML_DATA){
                this.lastFetchWorked = true;
                anns = fetchXML.getItems();
                listener.onFetchCompleted(true);
            }
            else if(fetchType == FETCH_ONE_BODY_TYPE && fetchDataType == HTML_DATA){
                this.lastFetchWorked = true;
                anns = new ArrayList<BbAnnouncement>();
                this.fetchResults = fetchString.getResponse();
                listener.onFetchCompleted(true);
            }

        }

        /**
         * FAILED LAST FETCH
         */
        else{
            this.lastFetchWorked = false;
            listener.onFetchCompleted(false);
        }
    }


    public boolean isLastFetchWorked() {
        return lastFetchWorked;
    }

    public List<BbAnnouncement> getAnns() {
        return anns;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setupCrsSeperators(){
        List<BbAnnouncement> newAnns = new ArrayList<BbAnnouncement>();
        String currCourse = "";
        String previousCourse = "";
        for(BbAnnouncement a : this.anns){
            currCourse = a.crsId;
            if(!currCourse.equals(previousCourse)){

                BbAnnouncement aCrs = new BbAnnouncement();
                aCrs.isCrsTitle = true;
                aCrs.crsName = a.crsName;
                newAnns.add(aCrs);
                a.isCrsTitle = false;
                newAnns.add(a);
                previousCourse = currCourse;
            }
            else{
                a.isCrsTitle = false;
                newAnns.add(a);
            }


        }
        if(newAnns.size() > this.anns.size()){
            this.anns.clear();
            this.anns.addAll(newAnns);
        }
    }

    public int getFetchType(){
        return this.fetchType;
    }

    public String getFetchResults() {
        return fetchResults;
    }


}
