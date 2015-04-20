package edu.gvsu.bbmobile.MyBB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by romeroj on 10/17/13.
 */
public class ClickedOnAnnouncement implements View.OnClickListener {

    private Context ctx;
    private String username;
    private String annId;
    private String annLabel;
    private String annDate;
    private String annCrs;
    private DashBoard dbParent;


    public ClickedOnAnnouncement(Context c, String aId, String un, String label, String date, String crs, DashBoard dbparent){
        this.ctx = c;
        this.username =un;
        this.annId = aId;
        this.annLabel = label;
        this.annDate = date;
        this.annCrs = crs;
        this.dbParent = dbparent;
    }


    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", this.username);
        bundle.putString("ann_id", this.annId);
        bundle.putString("ann_label", this.annLabel);
        bundle.putString("ann_crs_name", this.annCrs);
        bundle.putString("ann_date", this.annDate);
        bundle.putBoolean("fromAuth", true);
        Intent intent = new Intent(view.getContext(), ViewAnn.class);
        intent.putExtra("bundle", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}
