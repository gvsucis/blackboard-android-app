package edu.gvsu.bbmobile.MyBB.helpers;

/**
 * Created by romeroj on 9/18/13.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import edu.gvsu.bbmobile.MyBB.MyGlobal;
import edu.gvsu.bbmobile.MyBB.OnTaskCompleted;

/**
 * Created by romeroj on 6/28/13.
 * Attempts login using standard blackboard login fields.
 */
public class BbLogin extends AsyncTask<String, String, String> {

    private static final String TAG = "BBLEARN LOGIN";
    private static List<Cookie> cookies = null;

    private static String strCookies = "";
    private static String sessionCookie = "";

    public static final String BB_ISAUTH = MyGlobal.bbBaseUrl + "isAuth.jsp";
    private OnTaskCompleted listener;

    public BbLogin(OnTaskCompleted l){
        this.listener = l;
    }

    public String getSessionCookie() {
        return sessionCookie;
    }

    @Override
    protected String doInBackground(String... uri) {

        URL url;
        String strReturn = "";
        try {


            CookieManager cm = CookieManager.getInstance();
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            CookieStore cookieStore = new BasicCookieStore();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            X509HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };



            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
            HttpPost httpPost = new HttpPost(uri[0]);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("user_id", uri[1]));
            nameValuePairs.add(new BasicNameValuePair("password", uri[2]));
            nameValuePairs.add(new BasicNameValuePair("action", "login"));
            nameValuePairs.add(new BasicNameValuePair("login", "Login"));
            nameValuePairs.add(new BasicNameValuePair("new_loc", ""));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);

            HttpResponse response = httpClient.execute(httpPost, localContext);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https",SSLSocketFactory.getSocketFactory(), 443));


            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8096);
            String tmpString = "";
            while((tmpString = br.readLine()) != null){
                strReturn += tmpString + "\n";
            }

            this.cookies = cookieStore.getCookies();

            BbCookies bc = new BbCookies();
            for(Cookie c : this.cookies){
                if(c.getName().equalsIgnoreCase("jsessionid")){
                    this.sessionCookie = c.getName() + "=\"" + c.getValue() + "\"";
                }
                cm.setCookie(MyGlobal.cookieDomain, bc.getCookieString(c));
            }
            CookieSyncManager.getInstance().sync();

            this.strCookies = bc.getCookieSetString(cookies);

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();

            String page = "";
            BufferedReader in = null;

            request.addHeader("Cookie", this.strCookies);
            request.setURI(new URI(BB_ISAUTH));
            response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            strReturn = sb.toString();
        } catch (MalformedURLException e) {
            //TODO: handle eror
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        } catch (ProtocolException e) {
            //TODO: handle eror
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            //TODO: handle eror
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            //TODO: handle erorr
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
        catch(Exception e){
            //TODO: handle error
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        Log.d(TAG, "Before Return: " + strReturn);
        return strReturn;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.trim().equals("true")){
            listener.onTaskCompleted(true, cookies);
        }
        else{
            listener.onTaskCompleted(false, null);
        }

    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

