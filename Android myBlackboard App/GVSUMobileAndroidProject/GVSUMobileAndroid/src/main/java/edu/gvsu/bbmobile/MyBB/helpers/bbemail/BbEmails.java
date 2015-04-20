package edu.gvsu.bbmobile.MyBB.helpers.bbemail;

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

/**
 * Created by romeroj on 9/29/13.
 */
public class BbEmails implements OnTaskCompleted {

    private int requestId;

    private List<Cookie> cookies = null;
    private OnFetchCompleted listener;
    private List<BbEmail> bbEmails = new ArrayList<BbEmail>();

    private boolean lastFetchWorked;


    private static final String BB_FETCH_ALL_INST_EMAILS = MyGlobal.bbBaseUrl + "getInstEmailsForCourse.jsp";


    public BbEmails(OnFetchCompleted l){
        listener = l;
    }

    public void fetchInstEmails(String userName, String crsId){

        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(BB_FETCH_ALL_INST_EMAILS + "?crs_id=" + crsId, userName);

    }





    @Override
    public void onTaskCompleted(Boolean isAuth, List<Cookie> cookies) {

    }

    @Override
    public void onTaskCompleted(Boolean isGood, List<Cookie> cookies, JSONArray jsonArray) {
        this.cookies = cookies;
        if(isGood){
            this.lastFetchWorked = true;
            for(int i = 0; i < jsonArray.length(); i++){
                BbEmail bbEmail = new BbEmail();
                try {
                    JSONObject currEmail = jsonArray.getJSONObject(i);

                    bbEmail.email = currEmail.getString("email");
                    bbEmail.firstName = currEmail.getString("fname");
                    bbEmail.lastName  = currEmail.getString("lname");

                    //TODO: look at checking for a good grade item and handling if not one. can use build in functions of bbgrade



                } catch (JSONException e) {
                    // TODO: logging for error
                    e.printStackTrace();
                }
                this.bbEmails.add(bbEmail);


            }
            listener.onFetchCompleted(true);

        }
        else{
            this.lastFetchWorked = false;
            listener.onFetchCompleted(false);
        }
    }


    public boolean isLastFetchWorked() {
        return lastFetchWorked;
    }

    public List<BbEmail> getBbEmails() {
        return bbEmails;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}


