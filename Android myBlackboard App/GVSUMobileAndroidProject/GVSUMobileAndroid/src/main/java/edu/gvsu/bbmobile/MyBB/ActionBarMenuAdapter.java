package edu.gvsu.bbmobile.MyBB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by romeroj on 10/11/13.
 */
public class ActionBarMenuAdapter extends BaseAdapter{

    private List<String> items;
    private Context context;

    public ActionBarMenuAdapter(Context c, List<String> s){
        this.context = c;
        this.items = s;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_spinner_item, viewGroup, false);
        TextView tvCourseName = (TextView) rowView.findViewById(android.R.id.text1);
        tvCourseName.setText(items.get(i));
        return rowView;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.course_actionbar_menu, viewGroup, false);
        TextView tvCourseName = (TextView) rowView.findViewById(R.id.tv_actionbar_course_name);
        tvCourseName.setText(items.get(i));
        return rowView;
    }

}
