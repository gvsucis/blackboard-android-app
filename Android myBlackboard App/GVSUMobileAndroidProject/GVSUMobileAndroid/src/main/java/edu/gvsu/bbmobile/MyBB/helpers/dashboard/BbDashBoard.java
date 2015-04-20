package edu.gvsu.bbmobile.MyBB.helpers.dashboard;

import android.content.Context;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;
import edu.gvsu.bbmobile.MyBB.OnFetchCompleted;
import edu.gvsu.bbmobile.MyBB.OnTaskCompleted;
import edu.gvsu.bbmobile.MyBB.R;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListAnnItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListContentItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListGradeItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListGradeableItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListItem;
import edu.gvsu.bbmobile.MyBB.helpers.BbObjectType;
import edu.gvsu.bbmobile.MyBB.helpers.FetchFromBb;
import edu.gvsu.bbmobile.MyBB.helpers.bbannouncements.BbAnnouncement;
import edu.gvsu.bbmobile.MyBB.helpers.bbcontent.BbContent;
import edu.gvsu.bbmobile.MyBB.helpers.bbgrades.BbGrade;

/**
 * Created by romeroj on 10/10/13.
 */
public class BbDashBoard implements OnTaskCompleted {

    private int requestId;

    private List<Cookie> cookies = null;
    private OnFetchCompleted listener;
    private List<DashListItem> items = new ArrayList<DashListItem>();
    private String limit;
    private String crsId;
    private Context context;

    private int numTries;

    private Integer totalCount;

    private boolean lastFetchWorked;
    private boolean lastIsAuth;


    private static final String FETCH_ALL_TYPES = MyGlobal.bbBaseUrl + "getDashList.jsp";
    private static final String FETCH_ANNOUNCEMENTS = MyGlobal.bbBaseUrl + "getRecentAnn.jsp";
    private static final String FETCH_GRADES = MyGlobal.bbBaseUrl + "getGradesForUser.jsp";
    private static final String FETCH_CONTENTS = MyGlobal.bbBaseUrl + "getRecentContents.jsp";
    private static final String FETCH_GRADEABLES = MyGlobal.bbBaseUrl + "getRecentGradeable.jsp";

    public BbDashBoard(OnFetchCompleted l){
        listener = l;
    }

    public void fetchAllTypes(String userName){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_ALL_TYPES, userName);
    }

    public void fetchAllTypes(String userName, String strLimit){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_ALL_TYPES + "?limit=" + strLimit);
    }

    public void fetchAllTypes(String userName, String strLimit, String strCrsId){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_ALL_TYPES + "?limit=" + strLimit + "&crs_id=" + strCrsId);
    }

    public void fetchAnnouncements(String userName){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_ANNOUNCEMENTS, userName);
    }

    public void fetchAnnouncements(String userName, String strLimit){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_ANNOUNCEMENTS + "?limit=" + strLimit, userName);
    }

    public void fetchAnnouncements(String userName, String strLimit, String strCrsId){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_ANNOUNCEMENTS + "?limit=" + strLimit + "&crs_id=" + strCrsId, userName);
    }

    public void fetchGrades(String userName){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_GRADES, userName);
    }

    public void fetchGrades(String userName, String strLimit){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_GRADES + "?limit=" + strLimit, userName);
    }

    public void fetchGrades(String userName, String strLimit, String strCrsId){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_GRADES + "?limit=" + strLimit + "&crs_id=" + strCrsId, userName);
    }

    public void fetchContens(String userName){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_CONTENTS, userName);
    }

    public void fetchContens(String userName, String strLimit){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_CONTENTS + "?limit=" + strLimit, userName);
    }

    public void fetchContens(String userName, String strLimit, String strCrsId){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_CONTENTS + "?limit=" + strLimit + "&crs_id=" + strCrsId, userName);
    }

    public void setFetchGradeables(String userName){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_GRADEABLES, userName);
    }

    public void setFetchGradeables(String userName, String strLimit){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_GRADEABLES + "?limit=" + strLimit, userName);
    }

    public void setFetchGradeables(String userName, String strLimit, String strCrsId){
        FetchFromBb fetch = new FetchFromBb(this);
        fetch.execute(FETCH_GRADEABLES + "?limit=" + strLimit + "&crs_id=" + strCrsId, userName);
    }

    public List<DashListItem> getItems(){
        return items;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }


    @Override
    public void onTaskCompleted(Boolean isAuth, List<Cookie> cookies) {

        if(isAuth == false){
            this.lastIsAuth = this.lastFetchWorked = false;
            listener.onFetchCompleted(false);
        }
    }

    @Override
    public void onTaskCompleted(Boolean isGood, List<Cookie> cookies, JSONArray jsonArray) {

        this.cookies = cookies;
        if(isGood){
            this.lastFetchWorked = true;
            List<BbAnnouncement> anns = new ArrayList<BbAnnouncement>();
            List<BbGrade> grades = new ArrayList<BbGrade>();
            List<BbContent> contents = new ArrayList<BbContent>();
            List<BbContent> gradeables = new ArrayList<BbContent>();

            for(int i = 0; i < jsonArray.length(); i++){

                try {
                    JSONObject currItem = jsonArray.getJSONObject(i);
                    Integer type = currItem.getInt("type");
                    if(type == BbObjectType.ANNOUNCEMENT){
                        BbAnnouncement announcement = new BbAnnouncement();
                        announcement.id = currItem.getString("id");
                        announcement.label = currItem.getString("label");
                        announcement.crsId = currItem.getString("crs_id");
                        announcement.crsName = currItem.getString("crs_name");
                        announcement.pos = currItem.getString("pos");
                        announcement.post_date = currItem.getString("post_date");
                        announcement.description = currItem.getString("desc_preview");
                        anns.add(announcement);
                        announcement.cacheAnn(context);
                    }
                    else if(type == BbObjectType.GRADE){
                        BbGrade bbgrade = new BbGrade();
                        bbgrade.id = currItem.getString("id");
                        bbgrade.crsId = currItem.getString("crs_id");
                        bbgrade.colType = currItem.getString("col_type");
                        bbgrade.crsName = currItem.getString("crs_name");
                        bbgrade.label = currItem.getString("label");
                        bbgrade.pointsPossible = currItem.getString("points_poss");
                        bbgrade.grade = currItem.getString("grade");
                        bbgrade.scoreId = currItem.getString("score_id");
                        bbgrade.pos = currItem.getString("pos");
                        bbgrade.postDate = currItem.getString("date");
                        grades.add(bbgrade);
                        bbgrade.cache(context);
                    }
                    else if(type == BbObjectType.CONTENT_ITEM){
                        BbContent bbcontent = new BbContent();
                        bbcontent.id = currItem.getString("cnt_id");
                        bbcontent.label = currItem.getString("cnt_label");
                        if(currItem.has("cnt_end")){ bbcontent.endDate = currItem.getString("cnt_end"); }
                        if(currItem.has("cnt_start")){ bbcontent.startDate = currItem.getString("cnt_start"); }
                        bbcontent.pos = currItem.getString("cnt_pos");
                        bbcontent.handle = currItem.getString("cnt_handle");
                        bbcontent.crsId = currItem.getString("crs_id");
                        bbcontent.crsName = currItem.getString("crs_title");
                        bbcontent.isAvailable = currItem.getString("cnt_available").equals("Y");
                        bbcontent.cache(context);
                        contents.add(bbcontent);
                    }
                    else if(type == BbObjectType.GRADEABLE){
                        BbContent bbContent = new BbContent();
                        bbContent.id = currItem.getString("cnt_id");
                        bbContent.label = currItem.getString("cnt_label");
                        bbContent.pos = currItem.getString("cnt_pos");
                        bbContent.handle = currItem.getString("cnt_handle");
                        if(currItem.has("cnt_end")){ bbContent.endDate = currItem.getString("cnt_end"); }
                        else{ bbContent.endDate = "";}
                        if(currItem.has("cnt_start")){ bbContent.startDate = currItem.getString("cnt_start"); }
                        else{ bbContent.startDate = ""; }
                        if(currItem.has("due_date")){ bbContent.dueDate = currItem.getString("due_date"); }
                        else if (currItem.has("cnt_comp_due")){ bbContent.dueDate = currItem.getString("cnt_comp_due");}
                        bbContent.isAvailable = currItem.getString("cnt_available").equals("Y");
                        bbContent.crsId = currItem.getString("crs_id");
                        bbContent.crsName = currItem.getString("crs_title");
                        bbContent.description = currItem.getString("desc_preview");
                        bbContent.numFiles = currItem.getString("num_files");
                        if(currItem.has("cnt_recent_date")){ bbContent.date = currItem.getString("cnt_recent_date"); }
                        else{ bbContent.date = "";}
                        gradeables.add(bbContent);
                        bbContent.cache(context);
                    }
                    else if(type == BbObjectType.LIST_COUNT){
                        totalCount = (Integer) currItem.get("total");
                    }

                    //TODO: look at checking for a good grade item and handling if not one. can use build in functions of bbgrade

                } catch (JSONException e) {
                    // TODO: logging for error
                    e.printStackTrace();
                }
            }
            if(anns.size() > 0){
                int i = 0;
                for(BbAnnouncement ann : anns){
                    DashListAnnItem currItem = new DashListAnnItem().create("", context.getString(R.string.lbl_announcements), ann);
                    if(i == 0){
                        currItem.setHasHeader(true);
                    }
                    items.add(currItem);
                    i++;
                }

            }
            if(grades.size() > 0){
                int i = 0;
                for(BbGrade grade : grades){
                    DashListGradeItem currItem = new DashListGradeItem().create("", context.getString(R.string.lbl_grades), grade);
                    if(i == 0){
                        currItem.setHasHeader(true);
                    }
                    items.add(currItem);
                    i++;
                }
            }
            if(gradeables.size() > 0){
                int i = 0;
                for(BbContent gradeable : gradeables){
                    DashListGradeableItem currItem = new DashListGradeableItem().create("", context.getString(R.string.lbl_due_items), gradeable);
                    if(i == 0 ){
                        currItem.setHasHeader(true);
                    }
                    items.add(currItem);
                    i++;
                }
            }
            if(contents.size() > 0){
                int i = 0;
                for(BbContent content : contents){
                    DashListContentItem currItem = new DashListContentItem().create("", context.getString(R.string.lbl_contents), content);
                    if(i == 0){
                        currItem.setHasHeader(true);
                    }
                    items.add(currItem);
                    i++;
                }
            }
            listener.onFetchCompleted(true);
        }

        else{

            this.lastFetchWorked = false;
            listener.onFetchCompleted(false);
        }

    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public boolean isLastIsAuth() {
        return lastIsAuth;
    }

    public int getNumTries() {
        return numTries;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
