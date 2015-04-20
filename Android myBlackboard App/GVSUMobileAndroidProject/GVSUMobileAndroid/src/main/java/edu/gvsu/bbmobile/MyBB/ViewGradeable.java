package edu.gvsu.bbmobile.MyBB;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import org.apache.http.cookie.Cookie;

import java.util.List;

import edu.gvsu.bbmobile.MyBB.helpers.BbCookies;
import edu.gvsu.bbmobile.MyBB.helpers.BbLogin;
import edu.gvsu.bbmobile.MyBB.helpers.CheckAuth;
import edu.gvsu.bbmobile.MyBB.helpers.OnCheckAuthCompleted;
import edu.gvsu.bbmobile.MyBB.helpers.bbcontent.BbContent;
import edu.gvsu.bbmobile.MyBB.helpers.bbcontent.BbContentFile;
import edu.gvsu.bbmobile.MyBB.helpers.bbcontent.BbContents;

public class ViewGradeable extends Activity implements OnFetchCompleted, OnCheckAuthCompleted {

    private CookieManager cookies;
    private TextView tvTitle;
    private TextView tvCrs;
    private TextView tvDate;
    private WebView wvDesc;
    private TextView tvAttachmentsLabel;
    private LinearLayout llAttachments;

    private String strCntId;
    private String strCntCrs;
    private String strUsername;
    private String strCntDate;
    private String strCntLabel;

    private BbContents bbContents;
    private BbContent bbContent;

    private boolean fromAuth;

    private DownloadManager dm;
    private long enqueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gradeable);
        bbContents = new BbContents(this);
        fromAuth = getIntent().getExtras().getBundle("bundle").getBoolean("fromAuth");


        CookieSyncManager.getInstance().startSync();
        cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);

        setupViews();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_gradeable, menu);
        return true;
    }

    private void setupViews(){
        tvTitle = (TextView) this.findViewById(R.id.tv_gradeable_title);
        tvCrs = (TextView) this.findViewById(R.id.tv_gradeable_crs_name);
        tvDate = (TextView) this.findViewById(R.id.tv_gradeable_date);
        wvDesc = (WebView) this.findViewById(R.id.wv_gradeable_desc);
        llAttachments = (LinearLayout) this.findViewById(R.id.ll_attachments);
        tvAttachmentsLabel = (TextView) this.findViewById(R.id.tv_attachment_list_label);

        this.strUsername = getIntent().getExtras().getBundle("bundle").getString("username");
        this.strCntId = getIntent().getExtras().getBundle("bundle").getString("cnt_id");
        this.strCntCrs = getIntent().getExtras().getBundle("bundle").getString("cnt_crs_name");
        this.strCntDate = getIntent().getExtras().getBundle("bundle").getString("cnt_date");
        this.strCntLabel = getIntent().getExtras().getBundle("bundle").getString("cnt_label");

        tvTitle.setText(strCntLabel);
        tvCrs.setText(strCntCrs);
        tvDate.setText(strCntDate);

        if(strCntId != null && !strCntId.equals("")){
            bbContents.fetchOneContent(strCntId, strUsername);
        }


        BbCookies bc = new BbCookies();

        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);

        String tmpCookies = cookies.getCookie(MyGlobal.cookieDomain);
        List<Cookie> lstCookies = bc.getCookieList(tmpCookies);

        for(Cookie ck : lstCookies){
            cookies.setCookie(MyGlobal.cookieDomain, bc.transformToCookieHeader(ck)[0]);
        }
        String tmpSessionCookie = settings.getString(MyGlobal.APP_SETTINGS_COOKIE_KEY, "");
        cookies.setCookie(MyGlobal.cookieDomain, tmpSessionCookie);

        wvDesc.getSettings().setJavaScriptEnabled(true);

        wvDesc.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains(MyGlobal.cookieDomain)){
                    if(!url.endsWith("_1")){
                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        BbCookies bc = new BbCookies();
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
        wvDesc.loadUrl(bbContents.FETCH_CONTENT_BODY + "?cnt_id=" + strCntId );
    }

    @Override
    public void onFetchCompleted(Boolean isDone) {
        if(isDone){
            List<BbContent> contents = bbContents.getContents();
            if(contents != null && contents.size() == 1){
                bbContent = contents.get(0);
                if(bbContent != null && bbContent.files.size() > 0){
                    llAttachments.setVisibility(View.VISIBLE);
                    tvAttachmentsLabel.setVisibility(View.VISIBLE);

                    for(final BbContentFile currFile : bbContent.files){
                        View rowView = this.getLayoutInflater().inflate(R.layout.view_gradeable_attachment_item, null);
                        TextView tvAttLabel = (TextView) rowView.findViewById(R.id.tv_attachment_label);
                        final WebView wvAnnDesc = (WebView) rowView.findViewById(R.id.webView);
                        tvAttLabel.setText(currFile.label);
                        final String downloadUrl = currFile.link;

                        rowView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                BbCookies bc = new BbCookies();

                                SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);

                                String tmpCookies = cookies.getCookie(MyGlobal.cookieDomain);
                                List<Cookie> lstCookies = bc.getCookieList(tmpCookies);

                                for(Cookie ck : lstCookies){
                                    cookies.setCookie(MyGlobal.cookieDomain, bc.transformToCookieHeader(ck)[0]);
                                }
                                String tmpSessionCookie = settings.getString(MyGlobal.APP_SETTINGS_COOKIE_KEY, "");
                                cookies.setCookie(MyGlobal.cookieDomain, tmpSessionCookie);

                                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setTitle(currFile.label);

                                request.addRequestHeader("Cookie", bc.transformToCookieHeader(cookies.getCookie(MyGlobal.cookieDomain)));
                                enqueue = dm.enqueue(request);


                                //Toast.makeText(v.getContext(), R.string.toast_file_download_success, Toast.LENGTH_LONG).show();

                            }
                        });
                        llAttachments.addView(rowView);
                    }

                }


            }
        }
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
