package edu.gvsu.bbmobile.MyBB.helpers.bbcontent;

import java.util.Calendar;
import java.util.Comparator;

import edu.gvsu.bbmobile.MyBB.MyGlobal;

/**
 * Created by romeroj on 11/16/13.
 */
public class OrderGradeables implements Comparator<BbContent> {
    @Override
    public int compare(BbContent lhs, BbContent rhs) {

        String lhDue = "";
        String rhDue = "";
        if(lhs.dueDate != null && !lhs.dueDate.equals("")){
            lhDue = lhs.dueDate;
        }
        else if(lhs.endDate != null && !lhs.endDate.equals("")){
            lhDue = lhs.endDate;
        }
        if(rhs.dueDate != null && !rhs.dueDate.equals("")){
            rhDue = rhs.dueDate;
        }
        else if(rhs.endDate != null && !rhs.endDate.equals("")){
            rhDue = rhs.endDate;
        }

        if(!lhDue.equals("") && !rhDue.equals("")){
            Calendar lhcal = MyGlobal.getCalendarFromBuildingBlock(lhDue);
            Calendar rhcal = MyGlobal.getCalendarFromBuildingBlock(rhDue);
            if(lhcal != null && rhcal != null){
                return lhcal.compareTo(rhcal);
            }
            else if(lhDue != null && rhDue == null){
                return -1;
            }
            else if(lhDue == null && rhDue != null){
                return 1;
            }
            else{
                return 0;
            }
        }

        else if(!lhDue.equals("") && rhDue.equals("")){
            return 1;
        }
        else if (lhDue.equals("") && !rhDue.equals("")){
            return -1;
        }
        else{
            Calendar lhcal = MyGlobal.getCalendarFromBuildingBlock(lhs.date);
            Calendar rhcal = MyGlobal.getCalendarFromBuildingBlock(rhs.date);
            if(lhcal != null && rhcal != null){
                return lhcal.compareTo(rhcal);
            }
            else if(lhDue != null && rhDue == null){
                return -1;
            }
            else if(lhDue == null && rhDue != null){
                return 1;
            }
            else{
                return 0;
            }
        }
    }
}
