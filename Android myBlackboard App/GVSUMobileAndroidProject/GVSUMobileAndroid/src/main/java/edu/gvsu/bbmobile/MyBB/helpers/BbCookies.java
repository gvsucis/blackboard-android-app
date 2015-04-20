package edu.gvsu.bbmobile.MyBB.helpers;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.MyGlobal;

/**
 * Created by romeroj on 9/30/13.
 */
public class BbCookies {

    public String getCookieString(Cookie cookie){
        String setCookie = new StringBuilder(cookie.toString())
                .append("; domain=").append(cookie.getDomain())
                .append("; path=").append(cookie.getPath())
                .toString();
        return setCookie;
    }

    public List<Cookie> getCookieList(String strCookies){

        List<Cookie> lst = new ArrayList<Cookie>();
        String[] keyValueSets = null;
        if(strCookies != null && strCookies.contains(";")){
            keyValueSets = strCookies.split(";");
        }
        else if(strCookies.length() > 0){
            keyValueSets[0] = strCookies;
        }
        for(String cookie : keyValueSets)
        {
            cookie = cookie.trim();
            if(cookie.charAt(0) == '['){
                String[] items = cookie.split("]");
                String name = "";
                String value = "";
                String domain = "";
                String path = "";
                for(String item : items){
                    item = item.trim();
                    item = item.substring(1);
                    String[] keyVals = item.split(":");

                    if(keyVals[0].trim().equals("name")){
                        name = keyVals[1].trim();
                    }
                    else if (keyVals[0].trim().equals("value")){
                        value = keyVals[1].trim();
                    }
                    else if (keyVals[0].trim().equals("path")){
                        path = keyVals[1].trim();
                    }
                    else if (keyVals[0].trim().equals("domain")){
                        domain = keyVals[1].trim();
                    }

                }
                BasicClientCookie currCookie = new BasicClientCookie(name, value);
                currCookie.setDomain(domain);
                currCookie.setPath(path);
                if(!currCookie.getName().startsWith("__utm")){
                    lst.add(currCookie);
                }
            }
            else{
                String[] keyValue = cookie.split("=");
                String key = keyValue[0];
                String value = "";
                if(keyValue.length>1) value = keyValue[1];
                BasicClientCookie currCookie = new BasicClientCookie(key, value);
                currCookie.setDomain(MyGlobal.cookieDomain);
                if(!currCookie.getName().startsWith("__utm")){
                    lst.add(currCookie);
                }
            }
        }


        return lst;
    }

    public String transformToCookieHeader(String strCookies){
        String strHeader = "";



        String[] cookies = strCookies.split(";");
        int addedCount = 0;
        for(int i = 0; i < cookies.length; i++){

            Integer nameStart = cookies[i].indexOf("name:") + 6;
            Integer nameEnd = cookies[i].indexOf("]", nameStart);
            Integer valueStart = cookies[i].indexOf("value:") + 7;
            Integer valueEnd = cookies[i].indexOf("]", valueStart);
            String strName ="";
            String strValue = "";
            if(nameStart > 6 && nameEnd > nameStart && valueStart > 7 && valueEnd > valueStart){
                strName = cookies[i].substring(nameStart, nameEnd).trim();
                strValue = cookies[i].substring(valueStart, valueEnd).trim();
            }
            else{
                if(addedCount > 0){ strHeader += "; "; }
                strHeader += cookies[i];
                addedCount++;
            }


            if(!strName.equals("") && !strName.equals("")){
                if(addedCount > 0 ){ strHeader += "; "; }
                strHeader += strName + "=\"" + strValue + "\"";
                addedCount++;
            }
        }


        return strHeader;

    }

    public String[] transformToCookieHeader(Cookie cookie){
        String[] theRet = new String[2];
        theRet[0] = "";
        theRet[1] = cookie.getDomain();
        theRet[0] += cookie.getName() + "=\"" + cookie.getValue() + "\"";
        if(cookie.getExpiryDate() != null){
            theRet[0] += "; " + cookie.getExpiryDate().toString();
        }

        return theRet;
    }



    public String getCookieSetString(List<Cookie> cookies){
        String strCookies = "";
        for (int i = 0; i < cookies.size(); i++){
            if(i > 0){strCookies += "; ";}
            strCookies += cookies.get(i).getName() + "=\"" + cookies.get(i).getValue() + "\"";
        }
        return strCookies;
    }

}
