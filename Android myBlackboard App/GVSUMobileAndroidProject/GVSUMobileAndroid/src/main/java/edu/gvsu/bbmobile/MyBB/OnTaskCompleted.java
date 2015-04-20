package edu.gvsu.bbmobile.MyBB;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by romeroj on 9/27/13.
 */

public interface OnTaskCompleted{
    void onTaskCompleted(Boolean isAuth, List<Cookie> cookies);
    void onTaskCompleted(Boolean isAuth, List<Cookie> cookies, JSONArray jsonArray);

}
