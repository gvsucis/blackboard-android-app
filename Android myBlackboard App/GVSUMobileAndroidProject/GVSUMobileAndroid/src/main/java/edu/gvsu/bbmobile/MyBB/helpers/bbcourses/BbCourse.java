package edu.gvsu.bbmobile.MyBB.helpers.bbcourses;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;

/**
 * Created by romeroj on 10/9/13.
 */
public class BbCourse {

    public String id;
    public String crsId;
    public String crsName;
    public boolean isAvailable;
    public String startDate;
    public String endDate;
    public boolean isActive;

    /**
     * This is helper function for BaseAdapter to get long back for id.
     * @return Long version of id -- must first remove _ ... _1 from the String id
     */
    public Long getLongId(){
        String newId = this.id.substring(1, this.id.indexOf("_", 2));
        return Long.valueOf(newId);
    }

    public static BbCourse create(String id, String crsId, String crsName){
        BbCourse crs = new BbCourse();
        crs.crsId = crsId;
        crs.crsName = crsName;
        crs.id = id;
        crs.isActive = false;
        return crs;
    }

    public static BbCourse create(String id, String crsId, String crsName, boolean active){
        BbCourse crs = new BbCourse();
        crs.crsId = crsId;
        crs.crsName = crsName;
        crs.id = id;
        crs.isActive = active;
        return crs;
    }
    public JSONObject buildJSONObject(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", this.id);
            obj.put("crs_id", this.crsId);
            obj.put("crs_batch_uid", this.crsName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static BbCourse buildCourse(JSONObject jsonAnn){

        BbCourse bbcourse = new BbCourse();
        try{
            bbcourse.id = jsonAnn.getString("id");
            bbcourse.crsId = jsonAnn.getString("crs_id");
            bbcourse.crsName = jsonAnn.getString("crs_batch_uid");
        }
        catch(Exception ex){

        }
        return bbcourse;

    }

    public void cache(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        String currCourse = settings.getString(key, "");
        boolean isFound = false;
        List<BbCourse> currCourses = new ArrayList<BbCourse>();
        if(!currCourse.equals("")){
            try {
                JSONArray jsonArr = new JSONArray(currCourse);
                for(int i = 0; i < jsonArr.length(); i++){
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    BbCourse bbCourse = BbCourse.buildCourse(jsonObj);

                    if(this.id != null && bbCourse.id == this.id){
                        isFound = true;
                    }
                    currCourses.add(bbCourse);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(!isFound){
            currCourses.add(this);
        }

        JSONArray newArr = new JSONArray();
        for(int i = 0; i < currCourses.size() && i < MyGlobal.CACHE_LIMIT; i++){
            BbCourse course = currCourses.get(i);
            JSONObject obj = course.buildJSONObject();
            newArr.put(obj);
        }
        String tmp = newArr.toString();
        editor.putString(MyGlobal.DASH_CACHE_COURSE_KEY, newArr.toString());
        editor.commit();

    }

}
