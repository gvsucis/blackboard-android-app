package edu.gvsu.bbmobile.MyBB;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by romeroj on 9/29/13.
 */
public class MyGlobal {

    public static final String DASH_SETTINGS_LIMIT_KEY = "defaultLimit" ;
    public static final String APP_SETTINGS_COOKIE_KEY = "cookies";
    public static final String RESULT_BAD_AUTH_KEY = "authResults";

    public static final int RESULT_BAD_AUTH = 20001;
    public static final String DASH_SINGLE_TYPE_SINGLE_CRS_LIMIT = "31";
    public static final String DASH_NO_LIMIT = "1000";
    public static final int NAV_DRAWER_COURSE_INDEX = 6;
    public static final String DASH_CACHE_ANN_KEY = "cachedAnnouncements";
    public static final String DASH_CACHE_GRADE_KEY = "cachedGrades";
    public static final String DASH_CACHE_GRADEABLE_KEY = "cachedGradeables";
    public static final int CACHE_LIMIT = 20;
    public static final String DASH_CACHE_COURSE_KEY = "cachedCourses";
    public static final String DASH_ACTION_BAR_TITLE_KEY = "actionBarTitle";
    public static String baseUrl = "https:/blackboard.myUniversity.edu/";
    public static String loginSuffix = "webapps/login/";
    public static String bbSuffix = "webapps/gvsu-gvsu_mobile-BBLEARN/";

    public static String cookieDomain = "myUniversity.edu";

    public static String bbBaseUrl = baseUrl + bbSuffix;
    public static String loginUrl = baseUrl + loginSuffix;

    public static String APP_SETTINGS_FILE = "GVSU_MYBB_MOBILE";
    public static String APP_SETTINGS_USERNAME_KEY = "savedUsername";
    public static String DASH_SETTINGS_COURSE_KEY = "courseToView";
    public static String DASH_SETTINGS_VIEW_TYPE = "typeToView";

    public static final String DASH_ALL_LIMIT = "6";
    public static final String DASH_SINGLE_TYPE_LIMIT = "16";

    //for nav menu
    public static String NAV_ALL = "100";
    public static String NAV_ANNOUNCEMENTS = "101";
    public static String NAV_GRADES = "102";
    public static String NAV_ITEMS = "103";
    public static String NAV_DUE = "104";
    public static String NAV_EMAIL = "105";

    public static long DAY_IN_MS = 1000 * 60 * 60 * 24;

    public static String getDateFormat(String sqlDate){
        String s = "";
        try{
            Date dt = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(sqlDate);
            Format formatter = new SimpleDateFormat("MMM d, yyyy hh:mm a");
            s = formatter.format(dt);

        }catch(ParseException e){
            //TODO handle catch

        }
        return s;
    }

    public static String getDisplayDate(String sqlDate){
        String s = "";
        Date dt = new Date();
        try{
            dt = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(sqlDate);

        }catch(Exception e){

        }
        Calendar today = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(dt.getTime());
        if(cal.get(Calendar.DATE) == today.get(Calendar.DATE)){
            Format formater = new SimpleDateFormat("hh:mm a");
            s = formater.format(dt);
        }
        else if(cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            Format formater = new SimpleDateFormat("MMM d hh:mm a");
            s = formater.format(dt);
        }
        else{
            Format formater = new SimpleDateFormat("MMM d, yyyy hh:mm a");
            s = formater.format(dt);
        }
        return s;
    }

    public static String getMonth(String date){
        String s = "";
        try{
            Date dt = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(date);
            Format formatter = new SimpleDateFormat("MMM");
            s = formatter.format(dt);

        }catch(ParseException e){
            //TODO handle catch
            return "";
        }
        return s;
    }

    public static String getDate(String date){
        String s = "";
        try{
            Date dt = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(date);
            Format formatter = new SimpleDateFormat("dd");
            s = formatter.format(dt);

        }catch(ParseException e){
            //TODO handle catch
            return "";
        }
        return s;
    }

    public static String getTime(String date){
        String s = "";
        try{
            Date dt = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(date);
            Format formatter = new SimpleDateFormat("hh:mm aa");
            s = formatter.format(dt);

        }catch(ParseException e){
            //TODO handle catch
            return "";
        }
        return s;
    }



    public static Calendar getCalendarFromBuildingBlock(String date){
        Calendar cal = Calendar.getInstance();
        try{
            Date dt = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(date);

            cal.setTime(dt);


        }catch(ParseException e){
            //TODO handle catch
            return null;
        }
        return cal;
    }

    public static Calendar getCalendarFromItem(String date){

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        try{
            Date dt = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(date);
            new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse(date).getTime();


            cal.setTimeInMillis(dt.getTime());
            return cal;
        }catch(Exception e){
            String error = e.getLocalizedMessage();

        }
        return cal;
    }

    public static String replaceVTBEURLStub(String body){
        return body.replaceAll("@X@EmbeddedFile.requestUrlStub@X@", MyGlobal.baseUrl);
    }



}
