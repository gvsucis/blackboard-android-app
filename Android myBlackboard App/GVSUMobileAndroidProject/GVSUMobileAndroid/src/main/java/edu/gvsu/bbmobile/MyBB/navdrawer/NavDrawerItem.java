package edu.gvsu.bbmobile.MyBB.navdrawer;

/**
 * Created by romeroj on 10/9/13.
 */
public interface NavDrawerItem {
    public String getId();
    public String getLabel();
    public int getType();
    public boolean isEnabled();
    public boolean updateActionBarTitle();
}
