package edu.gvsu.bbmobile.MyBB.dashlist;

import edu.gvsu.bbmobile.MyBB.helpers.BbObjectType;
import edu.gvsu.bbmobile.MyBB.helpers.bbcontent.BbContent;

/**
 * Created by romeroj on 10/9/13.
 */
public class DashListContentItem implements DashListItem {

    public static final int TYPE = BbObjectType.CONTENT_ITEM;
    private String id;
    private String label;
    private BbContent item;
    private int count;
    private boolean hasHeader;


    public static DashListContentItem create(String id, String label, BbContent item ) {
        DashListContentItem section = new DashListContentItem();
        section.setLabel(label);
        section.setItem(item);
        section.setHasHeader(false);
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

    public BbContent getItem() {
        return item;
    }

    public void setItem(BbContent item) {
        this.item = item;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }
}
