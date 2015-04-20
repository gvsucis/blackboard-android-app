package edu.gvsu.bbmobile.MyBB;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.MyBB.dashlist.DashListItem;
import edu.gvsu.bbmobile.MyBB.helpers.dashboard.BbDashBoard;

public class DashBoardList extends ListFragment{

    private static final String LIMIT = "5";

    private static String strUsername;
    private static DashBoardListAdapter adapter;
    private List<DashListItem> listItems = new ArrayList<DashListItem>();
    private List<String> strCookies;
    private DashBoard dbParent;

    private CookieManager cookies;

    private BbDashBoard dashBoard;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CookieSyncManager.getInstance().startSync();

        cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);

        String tmpCookies = cookies.getCookie(MyGlobal.cookieDomain);

        this.strUsername = getArguments().getString("username");

        adapter = new DashBoardListAdapter(this.getActivity().getBaseContext(), listItems);
        adapter.setDbParent(dbParent);
        adapter.setUsername(this.strUsername);
        adapter.setCookies(cookies);
        setListAdapter(adapter);


    }

    public DashBoardListAdapter getDashListAdapter(){
        return adapter;
    }


    public void setDashBoard(BbDashBoard dashBoard) {
        this.dashBoard = dashBoard;
    }

    public void setDbParent(DashBoard dbParent) {
        this.dbParent = dbParent;
    }

    public DashBoard getDbParent() {
        return dbParent;
    }
}
