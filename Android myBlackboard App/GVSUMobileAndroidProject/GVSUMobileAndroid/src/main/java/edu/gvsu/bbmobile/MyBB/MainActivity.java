package edu.gvsu.bbmobile.MyBB;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.helpers.BbCookies;
import edu.gvsu.bbmobile.MyBB.helpers.BbLogin;



public class MainActivity extends Activity implements OnTaskCompleted {

    private static final int LAUNCH_DASHBOARD = 2000;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private RelativeLayout progressBar;

    private CookieManager cookies = CookieManager.getInstance();
    private BbLogin bbLogin;
    private String strUsername;
    private String strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        CookieSyncManager.createInstance(this.getBaseContext());
        CookieSyncManager.getInstance().startSync();
        setupLayout();



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strUsername = etUsername.getText().toString();
                strPassword = etPassword.getText().toString();
                etPassword.setText("");
                loginTry(strUsername, strPassword);
            }
        });
    }

    /**
     * Try to login to blackboard
     * @param username
     * @param password
     * @return true if works and false if not
     */
    private void loginTry(String username, String password)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        SharedPreferences.Editor editSettings = settings.edit();
        editSettings.clear();
        editSettings.commit();
        editSettings.putString(MyGlobal.APP_SETTINGS_USERNAME_KEY, username.toString().trim() );
        editSettings.commit();
        etPassword.setText("");
        bbLogin = new BbLogin(this);
        bbLogin.execute(MyGlobal.loginUrl, username, password);
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        CookieSyncManager.getInstance().sync();
    }

    @Override
    protected void onPause(){
        super.onPause();
        CookieSyncManager.getInstance().stopSync();

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



    private void setupLayout()
    {
        SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
        String strSavedUsername = settings.getString(MyGlobal.APP_SETTINGS_USERNAME_KEY, "");

        etUsername = (EditText) findViewById(R.id.etUserName);
        if(!strSavedUsername.equals("")){
            etUsername.setText(strSavedUsername);
        }
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {

                    loginTry(etUsername.getText().toString(), etPassword.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressBar = (RelativeLayout) findViewById(R.id.rl_progress_bar);

    }



    /**
     *
     * * This waits for the login to be done and passes to main springboard activity if it works
     * else it will display a toast for no login.
     * @param isAuth
     * @param strCookies
     */
    @Override
    public void onTaskCompleted(Boolean isAuth, List<Cookie> strCookies) {
        //login good
        if(isAuth){
            BbCookies bc = new BbCookies();
            List<String> transCookies = new ArrayList<String>();

            for(Iterator i = strCookies.iterator(); i.hasNext();){
                Cookie currCookie = (Cookie) i.next();
                cookies.setCookie(currCookie.getDomain(), currCookie.toString());
            }

            String strSetStringCookies = bc.getCookieSetString(bc.getCookieList(cookies.getCookie(MyGlobal.cookieDomain)));
            CookieSyncManager.getInstance().sync();
            SharedPreferences settings = getSharedPreferences(MyGlobal.APP_SETTINGS_FILE, 0);
            SharedPreferences.Editor editSettings = settings.edit();
            editSettings.putString(MyGlobal.APP_SETTINGS_USERNAME_KEY, etUsername.getText().toString().trim() );
            editSettings.putString(MyGlobal.APP_SETTINGS_COOKIE_KEY, bbLogin.getSessionCookie());
            editSettings.commit();

            Bundle bundle = new Bundle();
            bundle.putString("username", etUsername.getText().toString().trim());
            bundle.putBoolean("fromAuth", true);
            Intent intent = new Intent(this, DashBoard.class);
            intent.putExtra("bundle", bundle);

            progressBar.setVisibility(View.GONE);
            startActivityForResult(intent, LAUNCH_DASHBOARD);

        }
        else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this.getBaseContext(),getString(R.string.msg_invalid_login),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskCompleted(Boolean isAuth, List<Cookie> cookies, JSONArray jsonArray) {
        // NO ARRAY HERE.
    }




}
