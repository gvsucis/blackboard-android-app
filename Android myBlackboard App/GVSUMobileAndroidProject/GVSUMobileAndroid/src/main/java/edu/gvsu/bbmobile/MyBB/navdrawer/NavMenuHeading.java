package edu.gvsu.bbmobile.MyBB.navdrawer;

/**
 * Created by romeroj on 10/9/13.
 */
public class NavMenuHeading implements NavDrawerItem {

    public static final int HEADING_TYPE = 3;
    private String id;
    private String label;

    private NavMenuHeading() {
    }

    public static NavMenuHeading create(String label ) {
        NavMenuHeading section = new NavMenuHeading();
        section.setLabel(label);
        section.setId("-1");
        return section;
    }

    @Override
    public int getType() {
        return HEADING_TYPE;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean updateActionBarTitle() {
        return false;
    }


}