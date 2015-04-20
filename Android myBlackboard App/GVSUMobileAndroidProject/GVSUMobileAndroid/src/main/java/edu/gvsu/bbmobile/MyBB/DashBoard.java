package edu.gvsu.bbmobile.MyBB;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.analytics.tracking.android.EasyTracker;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.dashlist.DashListAnnItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListContentItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListEmptyListItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListGradeItem;
import edu.gvsu.bbmobile.MyBB.dashlist.DashListItem;
import edu.gvsu.bbmobile.MyBB.helpers.BbCookies;
import edu.gvsu.bbmobile.MyBB.helpers.BbLogin;
import edu.gvsu.bbmobile.MyBB.helpers.CheckAuth;
import edu.gvsu.bbmobile.MyBB.helpers.OnCheckAuthCompleted;
import edu.gvsu.bbmobile.MyBB.helpers.bbannouncements.BbAnnouncement;
import edu.gvsu.bbmobile.MyBB.helpers.bbcontent.BbContent;
import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.BbCourse;
import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.BbCourses;
import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.OnFetchCoursesCompleted;
import edu.gvsu.bbmobile.MyBB.helpers.bbgrades.BbGrade;
import edu.gvsu.bbmobile.MyBB.helpers.dashboard.BbDashBoard;
import edu.gvsu.bbmobile.MyBB.navdrawer.NavDrawerItem;
import edu.gvsu.bbmobile.MyBB.navdrawer.NavMenuCourseItem;
import edu.gvsu.bbmobile.MyBB.navdrawer.NavMenuHeading;
import edu.gvsu.bbmobile.MyBB.navdrawer.NavMenuItem;

public class DashBoard extends ActionBarActivity implements OnFetchCompleted, OnFetchCoursesCompleted, OnCheckAuthCompleted {

    private static final int LAUNCH_EMAIL = 100;
    public static final int RESULT_LOGOUT = 10;
    private static int FETCH_COURSES_ID = 1;
    private static int FETCH_DASH_ITEMS = 2;
    public static int START_ANN_VIEW = 1000;
    public static int START_GRADEABLE_VIEW = 1001;
    private int mLastFetchType = 0;

    private List<NavDrawerItem> navItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBar actionBar;
    private NavDrawerAdapter navDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private ProgressBar progressBar;

    private static BbCourses bbCourses;
    private static List<BbCourse> currCourses;
    private CharSequence mTitle;
    private int currentFragmentId;

    private static String strUsername;
    private static DashBoardListAdapter adapter;
    private List<DashListItem> listItems = new ArrayList<DashListItem>();

    private CookieManager cookies;
    private BbDashBoard bbDashBoard;
    private DashBoardList mFragment = new DashBoardList();
    private ActionBarMenuAdapter menuAdapter;
    private List<String> menuItems = new ArrayList<String>();
    private List<String> strCookies;
    private boolean fromAuth;
    private int numTries;
    private boolean checkingAuth;
    private int numCourseTries;

    private String actionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        numTries = 0;
        numCourseTries = 0;

        fromAuth = getIntent().getExtras().getBundle("bundle").getBoolean("fromAuth");
        checkingAuth = false;

        currCourses = new ArrayList<BbCourse>();

        CookieSyncManager.getInstance().startSync();
        cookies = CookieManager.getInstance();

        String tmpCookies = cookies.getCookie(MyGlobal.cookieDomain);

        cookies.setAcceptCookie(true);

        //get bundle
        this.strUsername = getIntent().getExtras().getBundle("bundle").getString("username");

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
            actionBarTitle = settings.getString(MyGlobal.DASH_ACTION_BAR_TITLE_KEY, getString(R.string.lbl_nav_crs_default));

            actionBar.setTitle(actionBarTitle);
        }

        bbCourses = new BbCourses(this);
        bbCourses.fetchAllCourses(strUsername);

        if (savedInstanceState == null) {

            currentFragmentId = 0;
            SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
            SharedPreferences.Editor editPref = settings.edit();
            editPref.putString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");
            editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, MyGlobal.NAV_ALL);
            editPref.commit();

            bbDashBoard = new BbDashBoard(this);
            bbDashBoard.setContext(getBaseContext());

            // update the main content by replacing fragments
            mFragment = new DashBoardList();
            mFragment.setDbParent(this);
            mFragment.setDashBoard(bbDashBoard);

            Bundle args = new Bundle();
            args.putString("username", this.strUsername);
            mFragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).commit();

            adapter = mFragment.getDashListAdapter();
            if(adapter != null){
                adapter.setDbParent(this);
            }
        }


        setContentView(R.layout.dashboard_layout);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);

        navItems = new ArrayList<NavDrawerItem>();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
                String crsTitle = settings.getString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");
                if(crsTitle.equals("_ALL_")){
                    crsTitle = getString(R.string.lbl_nav_crs_default);
                }
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle("");
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        CookieSyncManager.getInstance().startSync();
        cookies.setAcceptCookie(true);

        navItems.add(NavMenuHeading.create(getString(R.string.lbl_nav_crs_default)));
        navItems.add(NavMenuItem.create(MyGlobal.NAV_ALL, getString(R.string.view_all_cnt), -1));
        navItems.add(NavMenuItem.create(MyGlobal.NAV_ANNOUNCEMENTS, getString(R.string.nav_ann_lbl), R.drawable.ann_icon_blue_small));
        navItems.add(NavMenuItem.create(MyGlobal.NAV_GRADES, getString(R.string.nav_grades_lbl), R.drawable.grade_icon_blue_small));
        navItems.add(NavMenuItem.create(MyGlobal.NAV_DUE, getString(R.string.nav_gradeables_lbl), R.drawable.gradeable_icon_blue_small));
        navItems.add(NavMenuItem.create(MyGlobal.NAV_EMAIL, getString(R.string.nav_send_email_lbl), R.drawable.email_icon_white_small));
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        String strCrs = settings.getString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");
        navItems.add(NavMenuCourseItem.create("", bbCourses.getCourses(), strCrs));

        navDrawerAdapter = new NavDrawerAdapter(this, navItems, this);

        mDrawerList.setAdapter(navDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        if(savedInstanceState == null){
            selectItem(0);
        }
    }


    @Override
    public void onFetchCompleted(Boolean isDone) {
        progressBar.setVisibility(View.GONE);
        if(isDone){

            listItems.clear();
            listItems.addAll(bbDashBoard.getItems());

            adapter = mFragment.getDashListAdapter();
            if(adapter != null){
                adapter.setDbParent(this);
            }
            adapter.replaceList(listItems);
            adapter.notifyDataSetChanged();

            if(bbDashBoard.getCookies() != null){
                for(Cookie c : bbDashBoard.getCookies()){
                    cookies.setCookie(MyGlobal.cookieDomain, (new BbCookies()).getCookieString(c));
                }
            }
        }
        else{
            doFetchForPrefsCached();
        }
    }

    @Override
    public void onFetchCoursesCompleted(Boolean isDone) {
        if(isDone){

            navItems.remove(navItems.size() - 1);

            SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
            String strCrs = settings.getString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");

            navItems.add(NavMenuCourseItem.create("", bbCourses.getCourses(), strCrs));
            navDrawerAdapter.notifyDataSetChanged();

            if(bbCourses != null && bbCourses.getCookies() != null){
                for(Cookie c : bbCourses.getCookies()){
                    cookies.setCookie(MyGlobal.cookieDomain, (new BbCookies()).getCookieString(c));
                }
            }

        }
        else{
            doFetchCourseCached();
        }
    }

    protected void doFetchCourseCached(){
        List<BbCourse> courses = getCachedCourses();
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        String strCrs = settings.getString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");
        if(courses.size() > 0){
            navItems.remove(navItems.size() - 1);
            navItems.add(NavMenuCourseItem.create("", courses, strCrs));
            navDrawerAdapter.notifyDataSetChanged();
        }
        else{
            if(numCourseTries < 2){
                numCourseTries++;
                bbCourses.fetchAllCourses(strUsername);
            }
            else{

                numCourseTries = 0;

            }
        }
    }

    private List<DashListItem> getCachedAnn(){
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        List<DashListItem> items = new ArrayList<DashListItem>();
        String strAnnJson = settings.getString(MyGlobal.DASH_CACHE_ANN_KEY, "");
        if(!strAnnJson.equals("")){
            try {
                JSONArray jsonAnns = new JSONArray(strAnnJson);
                if(jsonAnns != null && jsonAnns.length() > 0){
                    for(int i = 0; i < jsonAnns.length(); i++){
                        BbAnnouncement currAnn = BbAnnouncement.buildAnn(jsonAnns.getJSONObject(i));
                        items.add(DashListAnnItem.create("", "Announcements", currAnn));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    private List<DashListItem> getCachedAnn(String crsId){
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        List<DashListItem> items = new ArrayList<DashListItem>();
        String strAnnJson = settings.getString(MyGlobal.DASH_CACHE_ANN_KEY, "");
        if(!strAnnJson.equals("")){
            try {
                JSONArray jsonAnns = new JSONArray(strAnnJson);
                if(jsonAnns != null && jsonAnns.length() > 0){
                    for(int i = 0; i < jsonAnns.length(); i++){
                        BbAnnouncement currAnn = BbAnnouncement.buildAnn(jsonAnns.getJSONObject(i));
                        if(currAnn.crsId == crsId){
                            items.add(DashListAnnItem.create("", "Announcements", currAnn));
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    private List<DashListItem> getCachedGrades(){
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        List<DashListItem> items = new ArrayList<DashListItem>();
        String strJson = settings.getString(MyGlobal.DASH_CACHE_GRADE_KEY, "");
        if(!strJson.equals("")){
            try {
                JSONArray jsonArr = new JSONArray(strJson);
                if(jsonArr != null && jsonArr.length() > 0){
                    for(int i = 0; i < jsonArr.length(); i++){
                        BbGrade curr = BbGrade.buildGrade(jsonArr.getJSONObject(i));
                        items.add(DashListGradeItem.create("", "Grades", curr));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
    private List<DashListItem> getCachedGrades(String crsId){
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        List<DashListItem> items = new ArrayList<DashListItem>();
        String strJson = settings.getString(MyGlobal.DASH_CACHE_GRADE_KEY, "");
        if(!strJson.equals("")){
            try {
                JSONArray jsonArr = new JSONArray(strJson);
                if(jsonArr != null && jsonArr.length() > 0){
                    for(int i = 0; i < jsonArr.length(); i++){
                        BbGrade curr = BbGrade.buildGrade(jsonArr.getJSONObject(i));
                        if(curr.crsId == crsId){
                            items.add(DashListGradeItem.create("", "Grades", curr));
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    private List<DashListItem> getCachedGradeables(){
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        List<DashListItem> items = new ArrayList<DashListItem>();
        String strJson = settings.getString(MyGlobal.DASH_CACHE_GRADEABLE_KEY, "");
        if(!strJson.equals("")){
            try {
                JSONArray jsonArr = new JSONArray(strJson);
                if(jsonArr != null && jsonArr.length() > 0){
                    for(int i = 0; i < jsonArr.length(); i++){
                        BbContent curr = BbContent.buildContent(jsonArr.getJSONObject(i));
                        items.add(DashListContentItem.create("", "Due Soon", curr));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
    private List<DashListItem> getCachedGradeables(String crsId){
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        List<DashListItem> items = new ArrayList<DashListItem>();
        String strJson = settings.getString(MyGlobal.DASH_CACHE_GRADEABLE_KEY, "");
        if(!strJson.equals("")){
            try {
                JSONArray jsonArr = new JSONArray(strJson);
                if(jsonArr != null && jsonArr.length() > 0){
                    for(int i = 0; i < jsonArr.length(); i++){
                        BbContent curr = BbContent.buildContent(jsonArr.getJSONObject(i));
                        if(curr.crsId == crsId){
                            items.add(DashListContentItem.create("", "Due Soon", curr));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    private List<BbCourse> getCachedCourses(){
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        List<BbCourse> items = new ArrayList<BbCourse>();
        String strJson = settings.getString(MyGlobal.DASH_CACHE_COURSE_KEY, "");
        if(!strJson.equals("")){
            try {
                JSONArray jsonArr = new JSONArray(strJson);
                if(jsonArr != null && jsonArr.length() > 0){
                    for(int i = 0; i < jsonArr.length(); i++){
                        BbCourse curr = BbCourse.buildCourse(jsonArr.getJSONObject(i));
                        items.add(curr);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    protected void doFetchForPrefsCached(){
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        String strCrs = settings.getString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");
        String strType = settings.getString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, MyGlobal.NAV_ALL);

        progressBar.setVisibility(View.VISIBLE);

        adapter = (DashBoardListAdapter) mFragment.getListAdapter();
        if(adapter != null){
            adapter.setDbParent(this);
        }
        bbDashBoard = new BbDashBoard(this);
        bbDashBoard.setContext(getBaseContext());

        List<DashListItem> items = new ArrayList<DashListItem>();
        if(strType.equals(MyGlobal.NAV_ALL)){
            if(strCrs.equals("_ALL_")){
                items.addAll(getCachedAnn());
                items.addAll(getCachedGrades());
                items.addAll(getCachedGradeables());
            }
            else{
                items.addAll(getCachedAnn(strCrs));
                items.addAll(getCachedGrades(strCrs));
                items.addAll(getCachedGradeables(strCrs));
            }
        }
        else if(strType.equals(MyGlobal.NAV_ANNOUNCEMENTS)){
            if(strCrs.equals("_ALL_")){
                items.addAll(getCachedAnn());
            }
            else{
                items.addAll(getCachedAnn(strCrs));
            }
        }
        else if(strType.equals(MyGlobal.NAV_GRADES)){
            if(strCrs.equals("_ALL_")){
                items.addAll(getCachedGrades());
            }
            else{
                items.addAll(getCachedGrades(strCrs));
            }
        }
        else if(strType.equals(MyGlobal.NAV_ITEMS)){
            if(strCrs.equals("_ALL_")){
                //
            }
            else{
                //
            }
        }
        else if(strType.equals(MyGlobal.NAV_DUE)){
            if(strCrs.equals("_ALL_")){
                items.addAll(getCachedGradeables());
            }
            else{
                items.addAll(getCachedGradeables(strCrs));
            }
        }

        if(items.size() > 0){
            listItems.clear();
            listItems.addAll(items);

            adapter = mFragment.getDashListAdapter();
            if(adapter != null){
                adapter.setDbParent(this);
            }
            adapter.replaceList(listItems);
            adapter.notifyDataSetChanged();
        }
        else{
            progressBar.setVisibility(View.GONE);
            listItems.clear();
            listItems.add(new DashListEmptyListItem().create());

            adapter = mFragment.getDashListAdapter();
            if(adapter != null){
                adapter.setDbParent(this);
            }
            adapter.replaceList(listItems);
            adapter.notifyDataSetChanged();

        }
        if(numTries < 2){
            numTries++;
            doFetchForPrefs();
        }
        else{
            progressBar.setVisibility(View.GONE);
            numTries = 0;
            listItems.clear();
            listItems.add(new DashListEmptyListItem().create());

            adapter = mFragment.getDashListAdapter();
            if(adapter != null){
                adapter.setDbParent(this);
            }
            adapter.replaceList(listItems);
            adapter.notifyDataSetChanged();

            if(bbDashBoard.getCookies() != null){
                for(Cookie c : bbDashBoard.getCookies()){
                    cookies.setCookie(MyGlobal.cookieDomain, (new BbCookies()).getCookieString(c));
                }
            }
            if(checkingAuth){
                checkingAuth = false;
            }
            else{
                checkingAuth = true;
                CheckAuth checkAuth = new CheckAuth(this);
                checkAuth.execute(BbLogin.BB_ISAUTH);
            }
        }


    }


    protected void doFetchForPrefs(){


        //get prefs
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        String strCrs = settings.getString(MyGlobal.DASH_SETTINGS_COURSE_KEY, "_ALL_");
        String strType = settings.getString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, MyGlobal.NAV_ALL);

        progressBar.setVisibility(View.VISIBLE);

        adapter = (DashBoardListAdapter) mFragment.getListAdapter();
        if(adapter != null){
            adapter.setDbParent(this);
        }
        bbDashBoard = new BbDashBoard(this);
        bbDashBoard.setContext(getBaseContext());

        if(strType.equals(MyGlobal.NAV_ALL)){
            if(strCrs.equals("_ALL_")){
                bbDashBoard.fetchAllTypes(strUsername, MyGlobal.DASH_ALL_LIMIT);
            }
            else{
                bbDashBoard.fetchAllTypes(strUsername, MyGlobal.DASH_ALL_LIMIT, strCrs);
            }
        }
        else if(strType.equals(MyGlobal.NAV_ANNOUNCEMENTS)){
            if(strCrs.equals("_ALL_")){
                adapter = (DashBoardListAdapter) mFragment.getListAdapter();
                bbDashBoard.fetchAnnouncements(strUsername, MyGlobal.DASH_SINGLE_TYPE_LIMIT);
            }
            else{
                adapter = (DashBoardListAdapter) mFragment.getListAdapter();
                bbDashBoard.fetchAnnouncements(strUsername, MyGlobal.DASH_SINGLE_TYPE_SINGLE_CRS_LIMIT, strCrs);
            }
        }
        else if(strType.equals(MyGlobal.NAV_GRADES)){
            if(strCrs.equals("_ALL_")){
                adapter = (DashBoardListAdapter) mFragment.getListAdapter();
                bbDashBoard.fetchGrades(strUsername, MyGlobal.DASH_NO_LIMIT);
            }
            else{
                adapter = (DashBoardListAdapter) mFragment.getListAdapter();
                bbDashBoard.fetchGrades(strUsername, MyGlobal.DASH_NO_LIMIT, strCrs);
            }
        }
        else if(strType.equals(MyGlobal.NAV_ITEMS)){
            if(strCrs.equals("_ALL_")){
                adapter = (DashBoardListAdapter) mFragment.getListAdapter();
                bbDashBoard.fetchContens(strUsername, MyGlobal.DASH_SINGLE_TYPE_LIMIT);
            }
            else{
                adapter = (DashBoardListAdapter) mFragment.getListAdapter();
                bbDashBoard.fetchContens(strUsername, MyGlobal.DASH_SINGLE_TYPE_SINGLE_CRS_LIMIT, strCrs);
            }
        }
        else if(strType.equals(MyGlobal.NAV_DUE)){
            if(strCrs.equals("_ALL_")){
                adapter = (DashBoardListAdapter) mFragment.getListAdapter();
                bbDashBoard.setFetchGradeables(strUsername, MyGlobal.DASH_SINGLE_TYPE_LIMIT);
            }
            else{
                adapter = (DashBoardListAdapter) mFragment.getListAdapter();
                bbDashBoard.setFetchGradeables(strUsername, MyGlobal.DASH_SINGLE_TYPE_SINGLE_CRS_LIMIT, strCrs);
            }
        }
        if(adapter != null){
            adapter.setDbParent(this);
        }
        bbCourses.fetchAllCourses(strUsername);

    }

    @Override
    public void OnCheckAuthCompleted(Boolean isDone) {
        if(isDone){
            doFetchForPrefs();
        }
        else{
            Intent data = new Intent();
            data.putExtra(MyGlobal.RESULT_BAD_AUTH_KEY, MyGlobal.RESULT_BAD_AUTH);
            setResult(RESULT_OK, data);
            finish();
        }
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * When select from nav Drawer
     * will get item clicked and then fetch and update adapter in fragment.
     * @param position
     */
    private void selectItem(int position) {

        //get nav items
        NavDrawerItem navItem = navItems.get(position);
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);

        if(navItem.getId().equals(MyGlobal.NAV_ALL)){
            SharedPreferences.Editor editPref = settings.edit();
            editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, navItem.getId());
            editPref.commit();
        }
        else if(navItem.getId().equals(MyGlobal.NAV_ANNOUNCEMENTS)){
            SharedPreferences.Editor editPref = settings.edit();
            editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, navItem.getId());
            editPref.commit();

        }
        else if(navItem.getId().equals(MyGlobal.NAV_DUE)){
            SharedPreferences.Editor editPref = settings.edit();
            editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, navItem.getId());
            editPref.commit();
        }
        else if(navItem.getId().equals(MyGlobal.NAV_GRADES)){
            SharedPreferences.Editor editPref = settings.edit();
            editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, navItem.getId());
            editPref.commit();
        }
        else if(navItem.getId().equals(MyGlobal.NAV_ITEMS)){
            SharedPreferences.Editor editPref = settings.edit();
            editPref.putString(MyGlobal.DASH_SETTINGS_VIEW_TYPE, navItem.getId());
            editPref.commit();
        }
        else if(navItem.getId().equals(MyGlobal.NAV_EMAIL)){
            Bundle bundle = new Bundle();
            bundle.putString("username", strUsername);
            Intent intent = new Intent(getBaseContext(), SendEmail.class);
            intent.putExtra("bundle", bundle);
            startActivityForResult(intent, LAUNCH_EMAIL);
        }



        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(navItems.get(position).getLabel());
        mDrawerLayout.closeDrawers();


        doFetchForPrefs();
    }

    @Override
    public void setTitle(CharSequence title) {
        //mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void updateActionBarTitle(String title){
        actionBar.setTitle(title);
        actionBarTitle = title;
    }

    public void updateNavDrawer(){
        navDrawerAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume(){
        super.onResume();
        CookieSyncManager.getInstance().sync();
        actionBar.setTitle(actionBarTitle);
        if(fromAuth){
            doFetchForPrefsCached();
            fromAuth = false;
        }
        else{
            CheckAuth checkAuth = new CheckAuth(this);
            checkAuth.execute(BbLogin.BB_ISAUTH);
        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MyGlobal.DASH_ACTION_BAR_TITLE_KEY, actionBarTitle);
        editor.commit();
        CookieSyncManager.getInstance().stopSync();

    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        final boolean[] goback = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                logout();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        builder.setMessage(R.string.do_logout).setTitle(R.string.dialog_title);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Menu options
     * ================================================
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            logout();
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    protected void logout() {

        cookies.removeAllCookie();
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MyGlobal.APP_SETTINGS_COOKIE_KEY, "");
        editor.clear();
        editor.commit();

        editor.putString(MyGlobal.APP_SETTINGS_USERNAME_KEY, strUsername);
        editor.commit();
        finish();
    }

    protected void launchActivity(Intent intent, int request){
        startActivityForResult(intent, request);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == START_ANN_VIEW || requestCode == START_GRADEABLE_VIEW) {
            if (resultCode == RESULT_LOGOUT) {
                logout();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }



}


