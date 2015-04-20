package edu.gvsu.bbmobile.MyBB.dashlist;

import edu.gvsu.bbmobile.MyBB.helpers.BbObjectType;
import edu.gvsu.bbmobile.MyBB.helpers.bbgrades.BbGrade;

/**
 * Created by romeroj on 10/9/13.
 */
public class DashListGradeItem implements DashListItem {

    public static final int TYPE = BbObjectType.GRADE;
    private String id;
    private String label;
    private BbGrade grade;
    private int count;
    private boolean hasHeader;


    public static DashListGradeItem create(String id, String label, BbGrade gr ) {
        DashListGradeItem section = new DashListGradeItem();
        section.setLabel(label);
        section.setGrade(gr);
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

    public BbGrade getGrade() {
        return grade;
    }

    public void setGrade(BbGrade grade) {
        this.grade = grade;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }
}
