package edu.gvsu.bbmobile.MyBB.navdrawer;

import java.util.List;

import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.BbCourse;

/**
 * Created by romeroj on 10/9/13.
 */
public class NavMenuCourseItem implements NavDrawerItem {

    public static final int COURSE_ITEM_TYPE = 5 ;

    private String id ;
    private boolean updateActionBarTitle ;
    private List<BbCourse> courses;
    private String activeCrs;

    private NavMenuCourseItem() {
    }

    public static NavMenuCourseItem create( String id, List<BbCourse> c, String activeCourse) {
        NavMenuCourseItem item = new NavMenuCourseItem();
        item.setId(id);
        item.setCourses(c);
        item.setActiveCrs(activeCourse);
        return item;
    }

    @Override
    public int getType() {
        return COURSE_ITEM_TYPE;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return "Courses";
    }

    public void setId(String id) {
        this.id = id;
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

    public List<BbCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<BbCourse> courses) {
        this.courses = courses;
    }

    public String getActiveCrs() {
        return activeCrs;
    }

    public void setActiveCrs(String activeCrs) {
        this.activeCrs = activeCrs;
    }
}