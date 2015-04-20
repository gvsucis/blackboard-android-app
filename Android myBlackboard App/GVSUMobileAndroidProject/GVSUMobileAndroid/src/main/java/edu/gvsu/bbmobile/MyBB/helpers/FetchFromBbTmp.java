package edu.gvsu.bbmobile.MyBB.helpers;

import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;
import edu.gvsu.bbmobile.MyBB.OnTaskCompleted;

/**
 * Created by romeroj on 9/29/13.
 */
public class FetchFromBbTmp extends AsyncTask<String, String, String> {

    private String response;
    private List<Cookie> cookies;
    private OnTaskCompleted listener;

    public FetchFromBbTmp(OnTaskCompleted l){

        listener = l;
    }


    @Override
    protected String doInBackground(String... strings) {

        String url = strings[0];


        DefaultHttpClient client = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        CookieSyncManager.getInstance().startSync();
        CookieManager cm = CookieManager.getInstance();

        CookieStore cookieStore = new BasicCookieStore();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        List<Cookie> ck = cookieStore.getCookies();
        String strCookies = cm.getCookie(MyGlobal.loginUrl);

        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("romero.student.m", "student"), "UTF-8", false));


        BbCookies myCookies = new BbCookies();
        List<Cookie> newCookies = myCookies.getCookieList(strCookies);
        String strNewCookies = myCookies.getCookieSetString(newCookies);

        String strSetCookies = myCookies.transformToCookieHeader(strNewCookies);

        httpGet.addHeader("Cookie", myCookies.transformToCookieHeader(strNewCookies));

        String r = "";


        try {
            HttpResponse execute = client.execute(httpGet);
            client.setCookieStore(cookieStore);


            execute.setHeader("Cookie", strCookies);

            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";

            while ((s = buffer.readLine()) != null) {
                r += s;
            }

            this.cookies = cookieStore.getCookies();
            BbCookies bc = new BbCookies();
            for(Cookie c : this.cookies){
                cm.setCookie(MyGlobal.cookieDomain, bc.getCookieString(c));
            }
            CookieSyncManager.getInstance().sync();

        } catch (Exception e) {
            e.printStackTrace();
        }


        this.response = r;

        try{

            HttpResponse execute = client.execute(httpGet);
            execute.setHeader("Cookie", strCookies);

            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";

            while ((s = buffer.readLine()) != null) {
                r += s;
            }

            this.cookies = cookieStore.getCookies();
            BbCookies bc = new BbCookies();
            for(Cookie c : this.cookies){
                cm.setCookie(MyGlobal.cookieDomain, bc.getCookieString(c));
            }
            CookieSyncManager.getInstance().sync();

        }catch(Exception ex){

        }
        return r;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        if(result.trim().equals("not authorized") != true){
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(result);
                if(jsonArray.length() == 0){
                    JSONObject jobject = new JSONObject(result);
                    jsonArray.put(jobject);
                }
                listener.onTaskCompleted(true, this.cookies, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onTaskCompleted(false, this.cookies, null);
            }


        }
        else{
            listener.onTaskCompleted(false, null);
        }
    }

    public List<Cookie> getCookies(){
        return this.cookies;
    }

    public String getResponse(){
        return this.response;
    }



}
