package edu.gvsu.bbmobile.MyBB;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.gvsu.bbmobile.MyBB.dashlist.DashListAnnItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListEmptyListItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListGradeItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListGradeableItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListItem;
import edu.gvsu.bbmobile.MyBB.helpers.BbObjectType;
import edu.gvsu.bbmobile.MyBB.helpers.bbannouncements.BbAnnouncement;
import edu.gvsu.bbmobile.MyBB.helpers.bbcontent.BbContent;
import edu.gvsu.bbmobile.MyBB.helpers.bbgrades.BbGrade;

/**
 * Created by romeroj on 10/9/13.
 */
public class DashBoardListAdapter extends BaseAdapter implements OnFetchCompleted {

    private static final String TAG = "Course LIST ADAPTER";
    private final Context context;
    private List<DashListItem> listItems;
    private String cntId;
    private String username;
    private CookieManager cookies;
    private List<String> strCookies;
    private DashBoard dbParent;
    private SharedPreferences settings;


    public DashBoardListAdapter(Context c, List<DashListItem> l){
        this.context = c;
        this.listItems = l;
        CookieSyncManager.getInstance().startSync();
        cookies = CookieManager.getInstance();
        String tmpCookies = cookies.getCookie(MyGlobal.cookieDomain);
        strCookies = new ArrayList<String>();
        settings = c.getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
    }



    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public List<DashListItem> getListItems(){
        return listItems;
    }

    public void replaceList(List<DashListItem> newItems){
        listItems.clear();
        listItems.addAll(newItems);
    }

    public void setCookies(CookieManager cm){
        this.cookies = cm;
    }

    private Calendar getToday(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }

    private Calendar getThisWeek(){
        Calendar thisWeek = getToday();
        thisWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return thisWeek;
    }

    private Calendar getOneWeekPrevious(){
        Calendar dt = getToday();
        dt.setTime(new Date(dt.getTimeInMillis() - MyGlobal.DAY_IN_MS));
        return dt;
    }


    @Override
    public View getView(int i, final View view, ViewGroup viewGroup) {

        Calendar startOfToday = getToday();
        Calendar startOfWeek = getOneWeekPrevious();



        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Integer type = listItems.get(i).getType();

        /**
         * *================================================================================
         * ANNOUNCEMENTS
         *================================================================================
         */
        if(type == BbObjectType.ANNOUNCEMENT){
            boolean inToday = true;
            boolean inWeek = true;
            boolean inOlder = true;

            DashListAnnItem currItem = (DashListAnnItem) listItems.get(i);

            View rowView = inflater.inflate(R.layout.dashboard_ann_item, null);
            View divider = (View) rowView.findViewById(R.id.divider);

            //if add header
            if(currItem.isHasHeader()){
                TextView tvTitle = (TextView) rowView.findViewById(R.id.dashlist_ann_section_title);
                tvTitle.setText(currItem.getLabel());

                // Onclick for announcement
                View.OnClickListener annOnlyClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreferences.Editor editPref = settings.edit();
                        editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, MyGlobal.NAV_ANNOUNCEMENTS);
                        editPref.commit();

                        String strCrs = settings.getString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");
                        dbParent.doFetchForPrefs();
                    }
                };


                // add onclick to arrow and header
                ImageView ivArrow = (ImageView) rowView.findViewById(R.id.imageViewArrow);
                if(!settings.getString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, "").equals(MyGlobal.NAV_ANNOUNCEMENTS)){
                    ivArrow.setVisibility(View.VISIBLE);
                    ivArrow.setOnClickListener(annOnlyClick);
                    TextView tvHeader = (TextView) rowView.findViewById(R.id.dashlist_ann_section_title);
                    tvHeader.setOnClickListener(annOnlyClick);
                    ImageView icon = (ImageView) rowView.findViewById(R.id.imageView);
                    icon.setOnClickListener(annOnlyClick);
                }

                RelativeLayout rlHeader = (RelativeLayout) rowView.findViewById(R.id.rv_ann_header);
                rlHeader.setVisibility(View.VISIBLE);

                divider.setVisibility(View.INVISIBLE);

            }

            // setup date header
            final BbAnnouncement currAnn = currItem.getAnnouncement();
            Calendar annCal = MyGlobal.getCalendarFromItem(currAnn.post_date);

            if(inToday && annCal.compareTo(startOfToday) > -1){
                inToday = false;
                TextView tvDateLabel = (TextView) rowView.findViewById(R.id.tv_dash_date_sep_lbl);
                tvDateLabel.setText(context.getString(R.string.lbl_dt_sep_today));
                divider.setVisibility(View.INVISIBLE);
            }
            else if(inWeek && annCal.compareTo(startOfWeek) > -1){
                inToday = false;
                inWeek = false;
                TextView tvDateLabel = (TextView) rowView.findViewById(R.id.tv_dash_date_sep_lbl);
                tvDateLabel.setText(context.getString(R.string.lbl_dt_sep_today));
                divider.setVisibility(View.INVISIBLE);
            }
            else if(inOlder){
                inToday = false;
                inWeek = false;
                inOlder = false;
                TextView tvDateLabel = (TextView) rowView.findViewById(R.id.tv_dash_date_sep_lbl);
                tvDateLabel.setText(context.getString(R.string.lbl_dt_sep_today));
                divider.setVisibility(View.INVISIBLE);
            }

            // setup announcement data
            TextView tvLabel = (TextView) rowView.findViewById(R.id.dashlist_ann_title);
            TextView tvCrsName = (TextView) rowView.findViewById(R.id.dashlist_ann_crs);
            TextView tvPostDate = (TextView) rowView.findViewById(R.id.dashlist_ann_date);
            TextView tvPreview = (TextView) rowView.findViewById(R.id.tv_ann_preview);
            tvLabel.setText(currAnn.label);
            tvCrsName.setText(currAnn.crsName);
            tvPostDate.setText(MyGlobal.getDisplayDate(currAnn.post_date));
            tvPreview.setText(currAnn.description);


            View.OnClickListener annClick = new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("ann_id", currAnn.id);
                    bundle.putString("ann_label", currAnn.label);
                    bundle.putString("ann_crs_name", currAnn.crsName);
                    bundle.putString("ann_date", MyGlobal.getDisplayDate(currAnn.post_date));
                    bundle.putBoolean("fromAuth", true);
                    Intent intent = new Intent(context, ViewAnn.class);
                    intent.putExtra("bundle", bundle);
                    dbParent.launchActivity(intent, DashBoard.START_ANN_VIEW);
                }
            };

            tvLabel.setOnClickListener(annClick);
            return rowView;

        }
        /**
         *================================================================================
         * GRADE VIEW
         *================================================================================
         */
        else if(type == BbObjectType.GRADE){
            DashListGradeItem currItem = (DashListGradeItem) listItems.get(i);

            View rowView = inflater.inflate(R.layout.dashboard_grade_item, null);
            View divider = rowView.findViewById(R.id.divider);

            //setup heading if needed
            if(currItem.isHasHeader()){
                TextView tvTitle = (TextView) rowView.findViewById(R.id.dashlist_grade_section_title);
                tvTitle.setText(currItem.getLabel());

                // Onclick for grade
                View.OnClickListener gradeOnlyClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreferences.Editor editPref = settings.edit();
                        editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, MyGlobal.NAV_GRADES);
                        editPref.commit();
                        dbParent.doFetchForPrefs();
                    }
                };

                ImageView ivGrades = (ImageView) rowView.findViewById(R.id.imageViewArrow);
                RelativeLayout rlHeader = (RelativeLayout) rowView.findViewById(R.id.rl_grade_list_header);
                if(!settings.getString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, "").equals(MyGlobal.NAV_GRADES)){
                    ivGrades.setVisibility(View.VISIBLE);
                    ivGrades.setOnClickListener(gradeOnlyClick);
                    ImageView icon = (ImageView) rowView.findViewById(R.id.imageView);
                    icon.setOnClickListener(gradeOnlyClick);
                    TextView tvHeader = (TextView) rowView.findViewById(R.id.dashlist_grade_section_title);
                    tvHeader.setOnClickListener(gradeOnlyClick);
                }
                rlHeader.setVisibility(View.VISIBLE);
                divider.setVisibility(View.GONE);
            }
            BbGrade currGrade = currItem.getGrade();

            TextView tvScore = (TextView) rowView.findViewById(R.id.tv_dashboard_grade_score);
            TextView tvGradeLabel = (TextView) rowView.findViewById(R.id.dashlist_grade_title);
            TextView tvCrsname = (TextView) rowView.findViewById(R.id.dashlist_grade_crs);
            TextView tvPostDate = (TextView) rowView.findViewById(R.id.dashlist_grade_date);

            tvScore.setText(currGrade.grade);
            tvGradeLabel.setText(currGrade.label);
            tvCrsname.setText(currGrade.crsName);
            tvPostDate.setText(MyGlobal.getDateFormat(currGrade.postDate));

            return rowView;
        }
        /**
         * *================================================================================
         * DUE SOON ITEMS
         * GRADEABLES
         * *================================================================================
         */
        else if(type == BbObjectType.GRADEABLE){
            DashListGradeableItem currItem = (DashListGradeableItem) listItems.get(i);
            View rowView = inflater.inflate(R.layout.dashboard_gradeable_item, null);
            View divider = rowView.findViewById(R.id.divider);


            if(currItem.isHasHeader()){

                TextView tvTitle = (TextView) rowView.findViewById(R.id.dashlist_gradeable_section_title);
                tvTitle.setText(currItem.getLabel());

                // Onclick for grade
                View.OnClickListener dueOnlyClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreferences.Editor editPref = settings.edit();
                        editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, MyGlobal.NAV_DUE);
                        editPref.commit();

                        dbParent.doFetchForPrefs();
                    }
                };

                if(!settings.getString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, "").equals(MyGlobal.NAV_DUE)){
                    ImageView ivDueArrow = (ImageView) rowView.findViewById(R.id.imageViewArrow);
                    ivDueArrow.setVisibility(View.VISIBLE);
                    ivDueArrow.setOnClickListener(dueOnlyClick);
                    TextView tvHeader = (TextView) rowView.findViewById(R.id.dashlist_gradeable_section_title);
                    tvHeader.setOnClickListener(dueOnlyClick);
                    ImageView icon = (ImageView) rowView.findViewById(R.id.imageView);
                    icon.setOnClickListener(dueOnlyClick);
                }

                RelativeLayout rlHeader = (RelativeLayout) rowView.findViewById(R.id.rl_due_list_header);
                rlHeader.setVisibility(View.VISIBLE);
                divider.setVisibility(View.GONE);
            }

            final BbContent content = currItem.getItem();

            TextView tvDueLabel = (TextView) rowView.findViewById(R.id.tv_dash_gradeable_due_label);
            TextView tvDueDate = (TextView) rowView.findViewById(R.id.tv_dash_gradeable_due_date);
            TextView tvCntTitle = (TextView) rowView.findViewById(R.id.dashlist_gradeable_title);
            TextView tvCntCrsName = (TextView) rowView.findViewById(R.id.dashlist_gradeable_crs);
            TextView tvDescription = (TextView) rowView.findViewById(R.id.tv_gradeable_desc);

            if(Integer.valueOf(content.numFiles) > 0){
                LinearLayout attachments = (LinearLayout) rowView.findViewById(R.id.ll_cnt_num_files);
                attachments.setVisibility(View.VISIBLE);
            }

            tvCntCrsName.setText(content.crsName);
            tvCntTitle.setText(content.label);
            tvDescription.setText(content.description);
            String dispDate = "";
            Locale locale = Locale.getDefault();
            if(content.dueDate != null && !content.dueDate.equals("")){
                tvDueLabel.setText("Due");
                tvDueDate.setText(MyGlobal.getDisplayDate(content.dueDate));
                dispDate = MyGlobal.getDateFormat(content.dueDate);
            }
            else if(content.endDate != null && !content.endDate.equals("")){
                tvDueLabel.setText(context.getString(R.string.due_label_end));
                tvDueDate.setText(MyGlobal.getDisplayDate(content.endDate));
                dispDate = MyGlobal.getDateFormat(content.endDate);
            }
            else{
                tvDueLabel.setText(context.getString(R.string.due_label_none) + " ");
                tvDueDate.setText(MyGlobal.getDisplayDate(content.date));
                dispDate = MyGlobal.getDateFormat(content.date);
            }

            final String finalDispDate = dispDate;
            View.OnClickListener gradeableClick = new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("cnt_id", content.id);
                    bundle.putString("cnt_label", content.label);
                    bundle.putString("cnt_crs_name", content.crsName);
                    bundle.putString("cnt_date", finalDispDate);
                    bundle.putBoolean("fromAuth", true);
                    Intent intent = new Intent(v.getContext(), ViewGradeable.class);
                    intent.putExtra("bundle", bundle);
                    dbParent.launchActivity(intent, dbParent.START_GRADEABLE_VIEW);
                }
            };

            tvCntTitle.setOnClickListener(gradeableClick);
            return rowView;
        }
        else if(type == BbObjectType.CONTENT_ITEM){
            //nothing yet
        }
        else if(type == DashListEmptyListItem.TYPE){
            View rowView = inflater.inflate(R.layout.dash_empty_layout, null);
            TextView tvTitle = (TextView) rowView.findViewById(R.id.dashlist_empty_section_title);
            tvTitle.setText(R.string.dash_empty_title);

            Button btnRefresh = (Button) rowView.findViewById(R.id.btn_empty_refresh);
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbParent.doFetchForPrefs();
                }
            });

            return rowView;
        }
        return null;
    }


    public String getCntId() {
        return cntId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStrCookies(List<String> strCookies) {
        this.strCookies = strCookies;
    }

    @Override
    public void onFetchCompleted(Boolean isDone) {

    }

    public DashBoard getDbParent() {
        return dbParent;
    }

    public void setDbParent(DashBoard dbParent) {
        this.dbParent = dbParent;
    }




}
