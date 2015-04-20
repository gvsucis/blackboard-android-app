package edu.gvsu.bbmobile.MyBB.helpers.bbgrades;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;

/**
 * Created by romeroj on 9/30/13.
 */
public class BbGrade {

    public String id;
    public String label;
    public String crsId;
    public String crsName;
    public String pointsPossible;
    public String grade;
    public String colType;
    public String pos;
    public String scoreId;
    public String postDate;

    public boolean isCrsTitle;

    public boolean isComplete(){
        if(id == null || label == null || crsId == null || crsName == null
                || pointsPossible == null || grade == null || colType == null
                || pos == null || scoreId == null){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * This is helper function for BaseAdapter to get long back for id.
     * @return Long version of id -- must first remove _ ... _1 from the String id
     */
    public Long getLongId(){
        if(this.isCrsTitle = true){
            return Long.valueOf(-1);
        }
        else if(this.id != null && !(this.id.equals(""))){
            String newId = this.id.substring(1, this.id.indexOf("_", 2));
            return Long.valueOf(newId);
        }

        return null;
    }

    public JSONObject buildJSONObject(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", this.id);
            obj.put("crs_id", this.crsId);
            obj.put("crs_name", this.crsName);
            obj.put("label", this.label);
            obj.put("pos", this.pos);
            obj.put("col_type", this.colType);
            obj.put("points_poss", this.pointsPossible);
            obj.put("grade", this.grade);
            obj.put("score_id", this.scoreId);
            obj.put("date", this.postDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static BbGrade buildGrade(JSONObject jsonAnn){

        BbGrade bbgrade = new BbGrade();
        try{
            bbgrade.id = jsonAnn.getString("id");
            bbgrade.crsId = jsonAnn.getString("crs_id");
            bbgrade.crsName = jsonAnn.getString("crs_name");
            bbgrade.label = jsonAnn.getString("label");
            bbgrade.pos = jsonAnn.getString("pos");
            bbgrade.colType = jsonAnn.getString("col_type");
            bbgrade.pointsPossible = jsonAnn.getString("points_poss");
            bbgrade.grade = jsonAnn.getString("grade");
            bbgrade.scoreId = jsonAnn.getString("score_id");
            bbgrade.postDate = jsonAnn.getString("date");
        }
        catch(Exception ex){

        }
        return bbgrade;

    }

    public void cache(Context context){
        SharedPreferences settings = context.getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        String currGrade = settings.getString(MyGlobal.DASH_CACHE_GRADE_KEY, "");
        boolean isFound = false;
        List<BbGrade> currGrades = new ArrayList<BbGrade>();
        if(!currGrade.equals("")){
            try {
                JSONArray jsonArr = new JSONArray(currGrade);
                for(int i = 0; i < jsonArr.length(); i++){
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    BbGrade grade = BbGrade.buildGrade(jsonObj);

                    if(this.id != null && grade.id == this.id){
                        isFound = true;
                    }
                    currGrades.add(grade);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(!isFound){
            currGrades.add(this);
        }

        Collections.sort(currGrades, new OrderGrades());
        JSONArray newArr = new JSONArray();
        for(int i = 0; i < currGrades.size() && i < MyGlobal.CACHE_LIMIT; i++){
            BbGrade myGrade = currGrades.get(i);
            JSONObject obj = myGrade.buildJSONObject();
            newArr.put(obj);
        }
        String tmp = newArr.toString();
        editor.putString(MyGlobal.DASH_CACHE_GRADE_KEY, newArr.toString());
        editor.commit();

    }



}