package edu.gvsu.bbmobile.MyBB.helpers.bbcontent;

/**
 * Created by romeroj on 10/8/13.
 */
public class BbContentFile {

    public String id;
    public String label;
    public String link;


    public boolean isComplete(){
        return true;
    }

    /**
     * This is helper function for BaseAdapter to get long back for id.
     * @return Long version of id -- must first remove _ ... _1 from the String id
     */
    public Long getLongId(){
        String newId = this.id.substring(1, this.id.indexOf("_", 2));
        return Long.valueOf(newId);

    }



}
