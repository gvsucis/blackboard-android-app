package edu.gvsu.bbmobile.MyBB;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;

import java.util.List;

import edu.gvsu.bbmobile.MyBB.helpers.BbCookies;
import edu.gvsu.bbmobile.MyBB.helpers.BbLogin;
import edu.gvsu.bbmobile.MyBB.helpers.CheckAuth;
import edu.gvsu.bbmobile.MyBB.helpers.OnCheckAuthCompleted;
import edu.gvsu.bbmobile.MyBB.helpers.bbannouncements.BbAnnouncements;

public class ViewAnn extends Activity implements OnFetchCompleted, OnTaskCompleted, OnCheckAuthCompleted {

    private static final int LAUNCH_AUTH = 10001;
    private TextView tvAnnTitle;
    private TextView tvAnnCrs;
    private TextView tvAnnDate;
    private WebView wvAnnDesc;

    private String strUsername;
    private String strAnnId;
    private String strAnnCrs;
    private String strAnnLabel;
    private String strAnnDate;
    private BbAnnouncements anns = new BbAnnouncements(this);
    private CookieManager cookies;

    private boolean fromAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_ann);

        fromAuth = getIntent().getExtras().getBundle("bundle").getBoolean("fromAuth");

        CookieSyncManager.getInstance().startSync();
        cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);

        setupViews();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_ann, menu);
        return true;
    }

    private void setupViews(){
        tvAnnTitle = (TextView) findViewById(R.id.tv_ann_title);
        tvAnnCrs = (TextView) findViewById(R.id.tv_ann_crs_name);
        tvAnnDate = (TextView) findViewById(R.id.tv_ann_date);
        wvAnnDesc = (WebView) findViewById(R.id.wv_ann_desc);

        this.strUsername = getIntent().getExtras().getBundle("bundle").getString("username");
        this.strAnnId = getIntent().getExtras().getBundle("bundle").getString("ann_id");
        this.strAnnCrs = getIntent().getExtras().getBundle("bundle").getString("ann_crs_name");
        this.strAnnDate = getIntent().getExtras().getBundle("bundle").getString("ann_date");
        this.strAnnLabel = getIntent().getExtras().getBundle("bundle").getString("ann_label");

        tvAnnTitle.setText("");
        tvAnnDate.setText("");
        tvAnnCrs.setText("");

        if(strAnnLabel != null){
            tvAnnTitle.setText(strAnnLabel);
        }
        if(strAnnCrs != null){
            tvAnnCrs.setText(strAnnCrs);
        }
        if(strAnnDate != null){
            tvAnnDate.setText(strAnnDate);
        }
        if(strAnnId != null){

            BbCookies bc = new BbCookies();

            SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);

            String tmpCookies = cookies.getCookie(MyGlobal.cookieDomain);
            List<Cookie> lstCookies = bc.getCookieList(tmpCookies);

            for(Cookie ck : lstCookies){
                cookies.setCookie(MyGlobal.cookieDomain, bc.transformToCookieHeader(ck)[0]);
            }
            String tmpSessionCookie = settings.getString(MyGlobal.APP_SETTINGS_COOKIE_KEY, "");
            cookies.setCookie(MyGlobal.cookieDomain, tmpSessionCookie);

            wvAnnDesc.getSettings().setJavaScriptEnabled(true);

            wvAnnDesc.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(url.contains(MyGlobal.cookieDomain)){
                        if(!url.endsWith("_1")){
                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            BbCookies bc = new BbCookies();
                            String nowCookies = bc.transformToCookieHeader(cookies.getCookie(MyGlobal.cookieDomain));
                            request.addRequestHeader("Cookie", bc.transformToCookieHeader(cookies.getCookie(MyGlobal.cookieDomain)));
                            dm.enqueue(request);
                            //Toast.makeText(view.getContext(), R.string.toast_file_download_success, Toast.LENGTH_LONG).show();
                        }
                        return false;

                    }
                    else{
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(i);
                    }
                    return true;
                }
            });

            wvAnnDesc.loadUrl(anns.ANN_BODY_URL + strAnnId);

        }
    }

    @Override
    public void onFetchCompleted(Boolean isDone) {
        if(isDone && this.anns.isLastFetchWorked()){
            if(anns.getAnns().size() == 1){

            }
            else{
                Toast.makeText(this, getString(R.string.tst_no_ann), Toast.LENGTH_LONG).show();
            }

            for(Cookie c : this.anns.getCookies()){
                this.cookies.setCookie(MyGlobal.cookieDomain, (new BbCookies()).getCookieString(c));
            }
        }
        else{
            Toast.makeText(this, getString(R.string.tst_no_ann), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskCompleted(Boolean isAuth, List<Cookie> cookies) {

    }

    @Override
    public void onTaskCompleted(Boolean isAuth, List<Cookie> cookies, JSONArray jsonArray) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        CookieSyncManager.getInstance().sync();
        CheckAuth checkAuth = new CheckAuth(this);
        checkAuth.execute(BbLogin.BB_ISAUTH);
    }

    @Override
    protected void onPause(){
        super.onPause();
        CookieSyncManager.getInstance().stopSync();

    }

    /**
     * Menu options
     * ================================================
     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            Intent resultIntent = new Intent();
            if(getParent() == null){
                setResult(DashBoard.RESULT_LOGOUT, resultIntent);
            }
            else{
                getParent().setResult(DashBoard.RESULT_LOGOUT, resultIntent);
            }

            finish();

        }
        return super.onOptionsItemSelected(item);
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


    @Override
    public void OnCheckAuthCompleted(Boolean isDone) {
        if(!isDone){
            Intent resultIntent = new Intent();
            if(getParent() == null){
                setResult(DashBoard.RESULT_LOGOUT, resultIntent);
            }
            else{
                getParent().setResult(DashBoard.RESULT_LOGOUT, resultIntent);
            }

            finish();
        }
    }
}
