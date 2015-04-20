package edu.gvsu.bbmobile.MyBB.helpers.bbgrades;

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
public class BbGrades implements OnTaskCompleted {

    private int requestId;

    private List<Cookie> cookies = null;
    private OnFetchCompleted listener;
    private List<BbGrade> grades = new ArrayList<BbGrade>();
    private List<String> courses;

    private boolean lastFetchWorked;


    private static final String BB_GRADES_FETCH_ALL = MyGlobal.bbBaseUrl + "getGradesForUser.jsp";


    public BbGrades(OnFetchCompleted l){
        listener = l;
        this.courses = new ArrayList<String>();

    }

    public void fetchAllGrades(String userName){

        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(BB_GRADES_FETCH_ALL, userName);

    }

    public void fetchAllGrades(String userName, int callNum){

        requestId = callNum;
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(BB_GRADES_FETCH_ALL, userName);
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
                BbGrade bbgrade = new BbGrade();
                try {
                    JSONObject currGrade = jsonArray.getJSONObject(i);

                    bbgrade.id = currGrade.getString("id");
                    bbgrade.crsId = currGrade.getString("crs_id");
                    bbgrade.colType = currGrade.getString("col_type");
                    bbgrade.crsName = currGrade.getString("crs_name");
                    bbgrade.label = currGrade.getString("label");
                    bbgrade.pointsPossible = currGrade.getString("points_poss");
                    bbgrade.grade = currGrade.getString("grade");
                    bbgrade.scoreId = currGrade.getString("score_id");
                    bbgrade.pos = currGrade.getString("pos");

                    //TODO: look at checking for a good grade item and handling if not one. can use build in functions of bbgrade



                } catch (JSONException e) {
                    // TODO: logging for error
                    e.printStackTrace();
                }
                this.grades.add(bbgrade);


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

    public List<BbGrade> getGrades() {
        return grades;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setupCrsSeperators(){
        List<BbGrade> newGrades = new ArrayList<BbGrade>();
        String currCourse = "";
        String previousCourse = "";
        for(BbGrade g : this.grades){
            currCourse = g.crsId;
            if(!currCourse.equals(previousCourse)){

                BbGrade gCrs = new BbGrade();
                gCrs.isCrsTitle = true;
                gCrs.crsName = g.crsName;
                newGrades.add(gCrs);
                newGrades.add(g);
                previousCourse = currCourse;
            }
            else{
                newGrades.add(g);
            }


        }
        if(newGrades.size() > this.grades.size()){
            this.grades = newGrades;
        }
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}


