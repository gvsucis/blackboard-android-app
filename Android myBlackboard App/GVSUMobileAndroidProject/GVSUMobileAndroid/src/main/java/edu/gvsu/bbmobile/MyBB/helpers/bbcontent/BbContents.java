package edu.gvsu.bbmobile.MyBB.helpers.bbcontent;

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
 * Created by romeroj on 10/8/13.
 */
public class BbContents implements OnTaskCompleted {

    private List<Cookie> cookies = null;
    private OnFetchCompleted listener;
    private List<BbContent> cnts = new ArrayList<BbContent>();

    private boolean lastFetchWorked;


    private static final String BB_CONTENTS_FETCH_ALL = MyGlobal.bbBaseUrl + "getCntForUser.jsp";
    private static final String BB_CONTENTS_FETCH_ONE = MyGlobal.bbBaseUrl + "getGradeableById.jsp";


    public static String FETCH_CONTENT_BODY = MyGlobal.bbBaseUrl + "getGradeableBodyById.jsp";

    public BbContents(OnFetchCompleted l){
        listener = l;
    }

    public void fetchAllContent(String userName){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(BB_CONTENTS_FETCH_ALL, userName);
    }

    public void fetchOneContent(String cntId, String userName){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(BB_CONTENTS_FETCH_ONE + "?cnt_id=" + cntId);
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
                BbContent bbcontent = new BbContent();
                try {
                    JSONObject currCnt = jsonArray.getJSONObject(i);

                    bbcontent.id = currCnt.getString("cnt_id");
                    bbcontent.crsId = currCnt.getString("crs_id");

                    bbcontent.crsName = currCnt.getString("crs_title");

                    bbcontent.label = currCnt.getString("cnt_label");
                    bbcontent.pos = currCnt.getString("cnt_pos");
                    bbcontent.handle = currCnt.getString("cnt_handle");
                    if(currCnt.has("cnt_start")){
                        bbcontent.startDate = currCnt.getString("cnt_start");
                    }
                    else{
                        bbcontent.startDate = "";
                    }
                    if(currCnt.has("cnt_end")){
                        bbcontent.endDate = currCnt.getString("cnt_end");
                    }
                    else{
                        bbcontent.endDate = "";
                    }
                    if(currCnt.has("due_date")){
                        bbcontent.dueDate = currCnt.getString("due_date");
                    }
                    else{
                        bbcontent.dueDate = "";
                    }

                    bbcontent.numFiles = currCnt.getString("num_files");
                    List<BbContentFile> bbContentFiles = new ArrayList<BbContentFile>();

                    if(bbcontent.numFiles != null && Integer.valueOf(bbcontent.numFiles) > 0 ){
                        JSONObject jsonFiles = currCnt.getJSONObject("files");
                        for(int k = 0; k < jsonFiles.length(); k++){
                            JSONObject jsonFile = jsonFiles.getJSONObject(String.valueOf(k));
                            BbContentFile currFile = new BbContentFile();
                            currFile.id = jsonFile.getString("id");
                            currFile.label = jsonFile.getString("name");
                            currFile.link = jsonFile.getString("link");
                            bbContentFiles.add(currFile);
                        }
                        String bob = "bob";
                    }
                    bbcontent.files = bbContentFiles;


                    //TODO: look at checking for a good grade item and handling if not one. can use build in functions of bbgrade



                } catch (JSONException e) {
                    // TODO: logging for error
                    e.printStackTrace();
                }
                this.cnts.add(bbcontent);


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

    public List<BbContent> getContents() {
        return cnts;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setupCrsSeperators(){
        List<BbContent> newCnt = new ArrayList<BbContent>();
        String currCourse = "";
        String previousCourse = "";
        for(BbContent c : this.cnts){
            currCourse = c.crsId;
            if(!currCourse.equals(previousCourse)){

                BbContent cnt = new BbContent();
                cnt.isCrsTitle = true;
                cnt.crsName = c.crsName;
                newCnt.add(cnt);
                newCnt.add(c);
                previousCourse = currCourse;
            }
            else{
                newCnt.add(c);
            }


        }
        if(newCnt.size() > this.cnts.size()){
            this.cnts = newCnt;
        }
    }

}
