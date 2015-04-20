package edu.gvsu.bbmobile.MyBB.helpers.bbannouncements;

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
public class BbAnnouncement {

    public String id;
    public String crsId;
    public String crsName;
    public String label;
    public String description;
    public String pos;
    public String post_date;
    public boolean isCrsTitle;
    public List<String> emImageLinks = new ArrayList<String>();


    public boolean isComplete(){
        if(id == null || crsId == null || crsName == null || label == null
                || description == null || pos == null){
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
        if(this.isCrsTitle == true){
            return Long.valueOf(-1);
        }
        else if(this.id != null && !(this.id.equals(""))){
            String newId = this.id.substring(1, this.id.indexOf("_", 2));
            return Long.valueOf(newId);
        }

        return null;
    }

    public void cacheAnn(Context context){
        SharedPreferences settings = context.getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        String currAnn = settings.getString(MyGlobal.DASH_CACHE_ANN_KEY, "");
        boolean isFound = false;
        List<BbAnnouncement> currAnns = new ArrayList<BbAnnouncement>();
        if(!currAnn.equals("")){
            try {
                JSONArray jsonAnns = new JSONArray(currAnn);
                for(int i = 0; i < jsonAnns.length(); i++){
                    JSONObject jsonAnn = jsonAnns.getJSONObject(i);

                    BbAnnouncement bbann = new BbAnnouncement();
                    bbann.id = jsonAnn.getString("id");
                    bbann.crsId = jsonAnn.getString("crs_id");
                    bbann.crsName = jsonAnn.getString("crs_name");
                    bbann.label = jsonAnn.getString("label");
                    bbann.description = jsonAnn.getString("desc_preview");
                    bbann.pos = jsonAnn.getString("pos");
                    bbann.post_date = jsonAnn.getString("post_date");
                    bbann.isCrsTitle = false;

                    if(this.id != null && bbann.id == this.id){
                        isFound = true;
                    }
                    currAnns.add(bbann);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(!isFound){
            currAnns.add(this);
        }

        Collections.sort(currAnns, new OrderAnn());
        JSONArray newAnns = new JSONArray();
        for(int i = 0; i < currAnns.size() && i < MyGlobal.CACHE_LIMIT; i++){
            BbAnnouncement myAnn = currAnns.get(i);
            JSONObject obj = myAnn.buildJSONObject();
            newAnns.put(obj);
        }
        String tmp = newAnns.toString();
        editor.putString(MyGlobal.DASH_CACHE_ANN_KEY, newAnns.toString());
        editor.commit();

    }

    public JSONObject buildJSONObject(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", this.id);
            obj.put("crs_id", this.crsId);
            obj.put("crs_name", this.crsName);
            obj.put("label", this.label);
            obj.put("desc_preview", this.description);
            obj.put("pos", this.pos);
            obj.put("post_date", this.post_date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static BbAnnouncement buildAnn(JSONObject jsonAnn){

        BbAnnouncement bbann = new BbAnnouncement();
        try{
            bbann.id = jsonAnn.getString("id");
            bbann.crsId = jsonAnn.getString("crs_id");
            bbann.crsName = jsonAnn.getString("crs_name");
            bbann.label = jsonAnn.getString("label");
            bbann.description = jsonAnn.getString("desc_preview");
            bbann.pos = jsonAnn.getString("pos");
            bbann.post_date = jsonAnn.getString("post_date");
            bbann.isCrsTitle = false;
        }
        catch(Exception ex){

        }
        return bbann;

    }

}
