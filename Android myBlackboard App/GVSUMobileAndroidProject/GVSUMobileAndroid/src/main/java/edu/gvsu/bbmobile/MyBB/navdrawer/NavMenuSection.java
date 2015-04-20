package edu.gvsu.bbmobile.MyBB.navdrawer;

/**
 * Created by romeroj on 10/9/13.
 */
public class NavMenuSection implements NavDrawerItem {

    public static final int SECTION_TYPE = 0;
    private String id;
    private String label;

    private NavMenuSection() {
    }

    public static NavMenuSection create( String id, String label ) {
        NavMenuSection section = new NavMenuSection();
        section.setLabel(label);
        return section;
    }

    @Override
    public int getType() {
        return SECTION_TYPE;
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