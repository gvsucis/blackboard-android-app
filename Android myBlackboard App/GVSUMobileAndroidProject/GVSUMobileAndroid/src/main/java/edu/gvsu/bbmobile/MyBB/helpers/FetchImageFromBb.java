package edu.gvsu.bbmobile.MyBB.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;
import edu.gvsu.bbmobile.MyBB.OnTaskCompleted;

/**
 * Created by romeroj on 9/29/13.
 */
public class FetchImageFromBb extends AsyncTask<String, String, String> {

    private String response;
    private List<Cookie> cookies;
    private OnTaskCompleted listener;
    private Bitmap bm;

    public final static int FETCH_IMAGE_TYPE = 1001;

    public FetchImageFromBb(OnTaskCompleted l){

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

            BufferedInputStream buffer = new BufferedInputStream(content);
            bm =BitmapFactory.decodeStream(buffer);

            this.cookies = cookieStore.getCookies();
            BbCookies bc = new BbCookies();
            for(Cookie c : this.cookies){
                cm.setCookie(MyGlobal.cookieDomain, bc.getCookieString(c));
            }
            CookieSyncManager.getInstance().sync();

            buffer.close();
            content.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        this.response = r;
        return r;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        listener.onTaskCompleted(true, this.cookies, null);
    }

    public List<Cookie> getCookies(){
        return this.cookies;
    }

    public String getResponse(){
        return this.response;
    }



}
