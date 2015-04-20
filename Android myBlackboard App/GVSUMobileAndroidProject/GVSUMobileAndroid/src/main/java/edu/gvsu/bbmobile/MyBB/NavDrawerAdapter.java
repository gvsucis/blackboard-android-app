package edu.gvsu.bbmobile.MyBB;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.BbCourse;
import edu.gvsu.bbmobile.MyBB.navdrawer.NavDrawerItem;
import edu.gvsu.bbmobile.MyBB.navdrawer.NavMenuCourseItem;
import edu.gvsu.bbmobile.MyBB.navdrawer.NavMenuHeading;
import edu.gvsu.bbmobile.MyBB.navdrawer.NavMenuItem;

/**
 * Created by romeroj on 10/9/13.
 */
public class NavDrawerAdapter extends BaseAdapter {

    private static final String TAG = "GRADE ARRAY ADAPTER";
    public static final int SECTION_TYPE = 0;
    public static final int ITEM_TYPE = 1 ;
    private final Context context;
    private final List<NavDrawerItem> navItems;
    private View divider;
    private DashBoard myDashboard;

    private NavMenuHeading heading;

    public NavDrawerAdapter(Context context, List<NavDrawerItem> items, DashBoard db) {
        this.context = context;
        this.navItems = items;
        myDashboard = db;
    }

    @Override
    public int getCount() {
        return navItems.size();
    }

    @Override
    public Object getItem(int i) {
        return navItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        long theRet = -1;

        return theRet;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NavDrawerItem currItem = navItems.get(i);
        if(currItem.getType() == NavMenuHeading.HEADING_TYPE){
            heading = (NavMenuHeading) currItem;
            View rowView = inflater.inflate(R.layout.navdrawer_section, viewGroup, false);
            TextView tvHeading = (TextView) rowView.findViewById(R.id.navmenusection_label);
            tvHeading.setText(heading.getLabel());
            return rowView;
        }
        else if(currItem.getType() == NavMenuItem.ITEM_TYPE){
            View rowView = inflater.inflate(R.layout.navdrawer_item, viewGroup, false);
            TextView tvLabel = (TextView) rowView.findViewById(R.id.navmenuitem_label);
            tvLabel.setText(currItem.getLabel());
            divider = (View) rowView.findViewById(R.id.divider);
            if(i > 1){
                divider.setVisibility(View.VISIBLE);
            }


            int imgRes = ((NavMenuItem) currItem).getImageRes();
            ImageView img = (ImageView) rowView.findViewById(R.id.imageView);
            if(imgRes != -1){
                img.setImageDrawable(rowView.getResources().getDrawable(imgRes));
            }
            else{
                img.setVisibility(View.INVISIBLE);
            }

            return rowView;
        }
        else{
            View rowView = inflater.inflate(R.layout.nav_drawer_course_list, viewGroup, false);
            LinearLayout ll = (LinearLayout) rowView.findViewById(R.id.navdrawer_course_list);
            NavMenuCourseItem navMenuCourseItem = (NavMenuCourseItem) currItem;

            View crsView = inflater.inflate(R.layout.nav_drawer_course_item, viewGroup, false);
            TextView crsName = (TextView) crsView.findViewById(R.id.tv_crs_title);
            crsName.setText(context.getString(R.string.view_all_crs));
            ll.addView(crsView);


            crsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences settings = context.getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
                    SharedPreferences.Editor editPref = settings.edit();
                    editPref.putString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");
                    editPref.commit();

                    myDashboard.doFetchForPrefs();
                    myDashboard.updateActionBarTitle(context.getString(R.string.lbl_nav_crs_default));
                    myDashboard.closeDrawer();

                    heading.setLabel(context.getString(R.string.lbl_nav_crs_default));
                    myDashboard.updateNavDrawer();


                }
            });


            int count = 0;
            if(navMenuCourseItem.getCourses() != null && navMenuCourseItem.getCourses().size() > 0){
                for(final BbCourse course : navMenuCourseItem.getCourses()){
                    View courseView = inflater.inflate(R.layout.nav_drawer_course_item, viewGroup, false);
                    TextView courseName = (TextView) courseView.findViewById(R.id.tv_crs_title);
                    courseName.setText(course.crsName);
                    if(course.id.equals(navMenuCourseItem.getActiveCrs())){

                    }

                    ll.addView(courseView);
                    count++;

                    courseView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences settings = context.getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
                            SharedPreferences.Editor editPref = settings.edit();
                            editPref.putString(MyGlobal.DASH_SETTINGS_COURSE_KEY, course.id );
                            editPref.commit();

                            myDashboard.doFetchForPrefs();
                            myDashboard.updateActionBarTitle(course.crsName);
                            myDashboard.closeDrawer();

                            heading.setLabel(course.crsName);
                            myDashboard.updateNavDrawer();
                        }
                    });
                }
            }

            return rowView;
        }
    }

    public void clearCourseList(){
        int i = 0;
        for(NavDrawerItem item : this.navItems){
            if(item.getType() == NavMenuCourseItem.COURSE_ITEM_TYPE){
                this.navItems.remove(i);
            }
            i++;
        }
    }
}