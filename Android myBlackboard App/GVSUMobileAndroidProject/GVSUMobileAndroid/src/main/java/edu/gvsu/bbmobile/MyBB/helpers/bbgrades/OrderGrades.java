package edu.gvsu.bbmobile.MyBB.helpers.bbgrades;

import java.util.Calendar;
import java.util.Comparator;

import edu.gvsu.bbmobile.MyBB.MyGlobal;

/**
 * Created by romeroj on 11/16/13.
 */
public class OrderGrades implements Comparator<BbGrade> {
    @Override
    public int compare(BbGrade lhs, BbGrade rhs) {

        if((lhs.scoreId != null && !lhs.scoreId.equals(""))  && (rhs.scoreId == null || lhs.equals(""))){
            return -1;
        }
        else if ((lhs.scoreId == null || lhs.scoreId.equals(""))  && (rhs.scoreId != null && !lhs.equals(""))){
            return 1;
        }
        else{
            Calendar lhcal = MyGlobal.getCalendarFromBuildingBlock(lhs.postDate);
            Calendar rhcal = MyGlobal.getCalendarFromBuildingBlock(rhs.postDate);

            if(lhcal != null && rhcal != null){
                return rhcal.compareTo(lhcal);
            }
            else if(lhcal == null && rhcal != null){
                return 1;
            }
            else if(lhcal != null && rhcal == null){
                return -1;
            }
            else{
                return 0;
            }
        }
    }
}
