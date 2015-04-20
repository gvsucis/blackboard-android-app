package edu.gvsu.bbmobile.MyBB.helpers.bbcourses;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;
import edu.gvsu.bbmobile.MyBB.OnTaskCompleted;
import edu.gvsu.bbmobile.MyBB.helpers.FetchFromBb;

/**
 * Created by romeroj on 10/9/13.
 */
public class BbCourses implements OnTaskCompleted {

    private int requestId;

    private List<Cookie> cookies = null;
    private OnFetchCoursesCompleted listener;
    private List<BbCourse> courses;


    private boolean lastFetchWorked;


    private static final String BB_COURSES_FETCH_ALL = MyGlobal.bbBaseUrl + "getAvailableCoursesForUser.jsp";


    public BbCourses(OnFetchCoursesCompleted l){
        listener = l;
        courses = new ArrayList<BbCourse>();
    }

    public void fetchAllCourses(String userName){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(BB_COURSES_FETCH_ALL, userName);
    }

    public void fetchAllCourses(String userName, int callnum){
        requestId = callnum;
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(BB_COURSES_FETCH_ALL, userName);
    }

    public boolean isLastFetchWorked(){
        return this.lastFetchWorked;
    }



    @Override
    public void onTaskCompleted(Boolean isAuth, List<Cookie> cookies) {

    }

    @Override
    public void onTaskCompleted(Boolean isGood, List<Cookie> cookies, JSONArray jsonArray) {
        this.cookies = cookies;
        if(isGood){
            this.courses.clear();
            this.lastFetchWorked = true;
            for(int i = 0; i < jsonArray.length(); i++){
                BbCourse bbcourse = new BbCourse();
                try {
                    JSONObject currCrs = jsonArray.getJSONObject(i);

                    bbcourse.id = currCrs.getString("crs_id");
                    bbcourse.crsName = currCrs.getString("crs_name");
                    bbcourse.crsId = currCrs.getString("crs_batch_uid");

                    //TODO: look at checking for a good grade item and handling if not one. can use build in functions of bbgrade



                } catch (JSONException e) {
                    // TODO: logging for error
                    e.printStackTrace();
                }
                this.courses.add(bbcourse);


            }
            listener.onFetchCoursesCompleted(true);

        }
        else{
            this.lastFetchWorked = false;
            listener.onFetchCoursesCompleted(false);
        }
    }

    public List<BbCourse> getCourses() {
        return courses;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }


    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int r) {
        requestId = r;
    }
}
