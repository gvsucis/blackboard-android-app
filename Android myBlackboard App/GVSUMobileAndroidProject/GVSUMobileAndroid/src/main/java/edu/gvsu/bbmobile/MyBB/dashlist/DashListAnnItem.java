package edu.gvsu.bbmobile.MyBB.dashlist;

import edu.gvsu.bbmobile.MyBB.helpers.BbObjectType;
import edu.gvsu.bbmobile.MyBB.helpers.bbannouncements.BbAnnouncement;

/**
 * Created by romeroj on 10/9/13.
 */
public class DashListAnnItem implements DashListItem {

    public static final int TYPE = BbObjectType.ANNOUNCEMENT;
    private String id;
    private String label;
    private BbAnnouncement announcement;
    private int count;
    private boolean hasHeader;


    public static DashListAnnItem create(String id, String label, BbAnnouncement a ) {
        DashListAnnItem section = new DashListAnnItem();
        section.setLabel(label);
        section.setAnnouncement(a);
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

    public BbAnnouncement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(BbAnnouncement announcement) {
        this.announcement = announcement;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }
}
