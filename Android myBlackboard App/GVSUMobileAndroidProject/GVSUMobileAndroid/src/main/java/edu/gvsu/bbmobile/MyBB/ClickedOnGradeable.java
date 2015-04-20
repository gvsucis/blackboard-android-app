package edu.gvsu.bbmobile.MyBB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by romeroj on 10/17/13.
 */
public class ClickedOnGradeable implements View.OnClickListener {

    private Context ctx;
    private String username;
    private String cntId;
    private String cntLabel;
    private String cntDate;
    private String cntCrs;
    private DashBoard dbParent;


    public ClickedOnGradeable(Context c, String cntId, String un, String label, String date, String crs, DashBoard dbparent){
        this.ctx = c;
        this.username =un;
        this.cntId = cntId;
        this.cntLabel = label;
        this.cntDate = date;
        this.cntCrs = crs;
        this.dbParent = dbparent;
    }


    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", this.username);
        bundle.putString("cnt_id", this.cntId);
        bundle.putString("cnt_label", this.cntLabel);
        bundle.putString("cnt_crs_name", this.cntCrs);
        bundle.putString("cnt_date", this.cntDate);
        bundle.putBoolean("fromAuth", true);
        Intent intent = new Intent(view.getContext(), ViewGradeable.class);
        intent.putExtra("bundle", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}
