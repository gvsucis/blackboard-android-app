package edu.gvsu.bbmobile.MyBB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.gvsu.bbmobile.MyBB.helpers.bbcourses.BbCourse;

/**
 * Created by romeroj on 9/29/13.
 */
public class EmailArrayAdapter extends BaseAdapter {

    private static final String TAG = "GRADE ARRAY ADAPTER";
    private final Context context;
    private final List<BbCourse> bbCourses;
    private String strUserName;


    public EmailArrayAdapter(Context context, List<BbCourse> crs) {
        this.context = context;
        this.bbCourses = crs;

    }

    @Override
    public int getCount() {
        return bbCourses.size();
    }

    @Override
    public Object getItem(int i) {
        return bbCourses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.send_email_list_item, parent, false);
        TextView tvCrsTitle = (TextView) rowView.findViewById(R.id.tv_crs_title);
        tvCrsTitle.setText(bbCourses.get(position).crsName);

        ImageView ivArrow = (ImageView) rowView.findViewById(R.id.imageViewArrow);
        ivArrow.setOnClickListener(new ClickedOnSendEmailCourse(context, strUserName, bbCourses.get(position).id, bbCourses.get(position).crsName));
        tvCrsTitle.setOnClickListener(new ClickedOnSendEmailCourse(context, strUserName, bbCourses.get(position).id, bbCourses.get(position).crsName));
        return rowView;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }
}
