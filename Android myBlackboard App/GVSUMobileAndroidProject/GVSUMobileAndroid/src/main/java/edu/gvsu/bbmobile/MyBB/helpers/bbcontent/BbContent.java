package edu.gvsu.bbmobile.MyBB.helpers.bbcontent;

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
 * Created by romeroj on 10/8/13.
 */
public class BbContent {

    public String id;
    public String label;
    public String crsId;
    public String crsName;
    public String pointsPossible;
    public String handle;
    public String pos;
    public String startDate;
    public String endDate;
    public String dueDate;
    public String description;
    public boolean isAvailable;
    public String numFiles;
    public String date;
    public List<BbContentFile> files = new ArrayList<BbContentFile>();

    public boolean isCrsTitle;

    public boolean isComplete(){
        if(id == null || label == null || crsId == null || crsName == null
                || pointsPossible == null || handle == null
                || pos == null || startDate == null || endDate == null){
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

    public void cache(Context context){
        SharedPreferences settings = context.getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        String currCnt = settings.getString(MyGlobal.DASH_CACHE_GRADEABLE_KEY, "");
        boolean isFound = false;
        List<BbContent> currCnts = new ArrayList<BbContent>();
        if(!currCnt.equals("")){
            try {
                JSONArray jsonCnts = new JSONArray(currCnt);
                for(int i = 0; i < jsonCnts.length(); i++){
                    JSONObject jsonCnt = jsonCnts.getJSONObject(i);

                    BbContent bbcnt = new BbContent();
                    bbcnt.id = jsonCnt.getString("cnt_id");
                    bbcnt.label = jsonCnt.getString("cnt_label");
                    bbcnt.pos = jsonCnt.getString("cnt_pos");
                    bbcnt.handle = jsonCnt.getString("cnt_handle");
                    bbcnt.endDate = jsonCnt.getString("cnt_end");
                    bbcnt.startDate = jsonCnt.getString("cnt_start");
                    bbcnt.dueDate = jsonCnt.getString("due_date");
                    if(jsonCnt.getString("cnt_available").equals("Y")){
                        bbcnt.isAvailable = true;
                    }
                    else{
                        bbcnt.isAvailable = false;
                    }
                    bbcnt.crsId = jsonCnt.getString("crs_id");
                    bbcnt.crsName = jsonCnt.getString("crs_name");
                    bbcnt.description = jsonCnt.getString("desc_preview");
                    bbcnt.numFiles = jsonCnt.getString("num_files");
                    bbcnt.date = jsonCnt.getString("cnt_recent_date");

                    if(this.id != null && bbcnt.id == this.id){
                        isFound = true;
                    }
                    currCnts.add(bbcnt);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(!isFound){
            currCnts.add(this);
        }

        Collections.sort(currCnts, new OrderGradeables());
        JSONArray newCnts = new JSONArray();
        for(int i = 0; i < currCnts.size() && i < MyGlobal.CACHE_LIMIT; i++){
            BbContent myCnt = currCnts.get(i);
            JSONObject obj = myCnt.buildJSONObject();
            newCnts.put(obj);
        }
        String tmp = newCnts.toString();
        editor.putString(MyGlobal.DASH_CACHE_GRADEABLE_KEY, newCnts.toString());
        editor.commit();

    }



    public JSONObject buildJSONObject(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("cnt_id", this.id);
            obj.put("cnt_label", this.label);
            obj.put("cnt_pos", this.pos);
            obj.put("cnt_handle", this.handle);
            obj.put("cnt_end", this.endDate);
            obj.put("cnt_start", this.startDate);
            obj.put("due_date", this.dueDate);
            obj.put("cnt_comp_due", this.dueDate);
            if(this.isAvailable){
                obj.put("cnt_available", "Y");
            }
            else{
                obj.put("cnt_available", "N");
            }
            obj.put("crs_id", this.crsId);
            obj.put("crs_name", this.crsName);
            obj.put("desc_preview", this.description);
            obj.put("num_files", this.numFiles);
            obj.put("cnt_recent_date", this.date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static BbContent buildContent(JSONObject jsonCnt){


        BbContent bbcnt = new BbContent();
        try{
            bbcnt.id = jsonCnt.getString("cnt_id");
            bbcnt.label = jsonCnt.getString("cnt_label");
            bbcnt.pos = jsonCnt.getString("cnt_pos");
            bbcnt.handle = jsonCnt.getString("cnt_handle");
            bbcnt.endDate = jsonCnt.getString("cnt_end");
            bbcnt.startDate = jsonCnt.getString("cnt_start");
            bbcnt.dueDate = jsonCnt.getString("due_date");
            if(jsonCnt.getString("cnt_available").equals("Y")){
                bbcnt.isAvailable = true;
            }
            else{
                bbcnt.isAvailable = false;
            }
            bbcnt.crsId = jsonCnt.getString("crs_id");
            bbcnt.crsName = jsonCnt.getString("crs_name");
            bbcnt.description = jsonCnt.getString("desc_preview");
            bbcnt.numFiles = jsonCnt.getString("num_files");
            bbcnt.date = jsonCnt.getString("cnt_recent_date");
        }catch(Exception ex){

        }
        return bbcnt;
    }


}
