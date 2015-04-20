package edu.gvsu.bbmobile.MyBB.navdrawer;

/**
 * Created by romeroj on 10/9/13.
 */
public class NavMenuItem implements NavDrawerItem {

    public static final int ITEM_TYPE = 1 ;

    private String id ;
    private String label ;
    private boolean updateActionBarTitle ;
    private int imageRes;

    private NavMenuItem() {
    }

    public static NavMenuItem create( String id, String label, int res) {
        NavMenuItem item = new NavMenuItem();
        item.setId(id);
        item.setLabel(label);
        item.setImageRes(res);
        return item;
    }

    @Override
    public int getType() {
        return ITEM_TYPE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean updateActionBarTitle() {
        return this.updateActionBarTitle;
    }

    public void setUpdateActionBarTitle(boolean updateActionBarTitle) {
        this.updateActionBarTitle = updateActionBarTitle;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }
}