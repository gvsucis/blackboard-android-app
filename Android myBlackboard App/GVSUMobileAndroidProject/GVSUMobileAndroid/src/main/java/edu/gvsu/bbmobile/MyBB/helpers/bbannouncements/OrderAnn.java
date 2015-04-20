package edu.gvsu.bbmobile.MyBB.helpers.bbannouncements;

import java.util.Calendar;
import java.util.Comparator;

import edu.gvsu.bbmobile.MyBB.MyGlobal;

/**
 * Created by romeroj on 11/16/13.
 */
public class OrderAnn implements Comparator<BbAnnouncement> {
    @Override
    public int compare(BbAnnouncement lhs, BbAnnouncement rhs) {
        if(lhs.post_date != null && !lhs.post_date.equals("") && rhs.post_date != null && !rhs.post_date.equals("")){
            Calendar lhscal = MyGlobal.getCalendarFromBuildingBlock(lhs.post_date);
            Calendar rhscal = MyGlobal.getCalendarFromBuildingBlock(rhs.post_date);
            return lhscal.compareTo(rhscal);
        }
        else if((lhs.post_date != null && !lhs.post_date.equals("")) && (rhs.post_date == null || rhs.post_date.equals(""))){
            return 1;
        }
        else if((lhs.post_date == null || lhs.post_date.equals("")) && (rhs.post_date != null && !rhs.post_date.equals(""))){
            return -1;
        }
        else{
            return 0;
        }

    }
}
