package edu.gvsu.bbmobile.MyBB.dashlist;

/**
 * Created by romeroj on 10/9/13.
 */
public class DashListEmptyListItem implements DashListItem {

    public static final int TYPE = -1;
    private String id;
    private String label;
    private int count;


    public static DashListEmptyListItem create() {
        DashListEmptyListItem section = new DashListEmptyListItem();
        section.setLabel("No Items");
        section.setId("");
        return section;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
