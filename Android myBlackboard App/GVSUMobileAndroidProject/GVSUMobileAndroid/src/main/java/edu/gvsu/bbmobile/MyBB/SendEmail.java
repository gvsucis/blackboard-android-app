package edu.gvsu.bbmobile.MyBB;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.helpers.BbCookies;
import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.BbCourse;
import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.BbCourses;
import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.OnFetchCoursesCompleted;

public class SendEmail extends ListActivity implements OnFetchCoursesCompleted {

    private static EmailArrayAdapter adapter;
    private static BbCourses courses;
    private static List<BbCourse> currCourses;
    private String strUsername;

    private CookieManager cookies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currCourses = new ArrayList<BbCourse>();

        CookieSyncManager.getInstance().startSync();
        cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);

        //get bundle
        this.strUsername = getIntent().getExtras().getBundle("bundle").getString("username");

        courses = new BbCourses(this);
        courses.fetchAllCourses(strUsername);
        adapter = new EmailArrayAdapter(this.getBaseContext(), currCourses);
        adapter.setStrUserName(strUsername);


        setListAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_email, menu);
        return true;
    }

    @Override
    public void onFetchCoursesCompleted(Boolean isDone) {
        if(isDone && courses.isLastFetchWorked()){
            currCourses.clear();
            currCourses.addAll(courses.getCourses());
            adapter.notifyDataSetChanged();
            for(Cookie c : courses.getCookies()){
                cookies.setCookie(MyGlobal.cookieDomain, (new BbCookies()).getCookieString(c));
            }

        }
        else{
            Toast.makeText(this.getBaseContext(), getString(R.string.tst_no_email_list), Toast.LENGTH_LONG).show();
        }
    }
}
