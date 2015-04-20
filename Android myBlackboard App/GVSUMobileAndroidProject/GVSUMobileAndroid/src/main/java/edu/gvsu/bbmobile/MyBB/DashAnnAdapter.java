package edu.gvsu.bbmobile.MyBB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.gvsu.bbmobile.MyBB.helpers.bbannouncements.BbAnnouncement;

/**
 * Created by romeroj on 10/9/13.
 */
public class DashAnnAdapter extends BaseAdapter {

    private static final String TAG = "Course LIST ADAPTER";
    private final Context context;
    private List<BbAnnouncement> listItems;

    public DashAnnAdapter(Context c, List<BbAnnouncement> l){
        this.context = c;
        this.listItems = l;

    }



    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        BbAnnouncement listItem = listItems.get(i);
        View  rowView = inflater.inflate(R.layout.dashboard_ann_item, viewGroup, false);

        TextView tvLabel = (TextView) rowView.findViewById(R.id.dashlist_ann_title);
        tvLabel.setText(listItem.label);

        TextView tvCrs = (TextView) rowView.findViewById(R.id.dashlist_ann_crs);
        tvCrs.setText(listItem.crsName);

        TextView tvDate = (TextView) rowView.findViewById(R.id.dashlist_ann_date);
        tvDate.setText(listItem.post_date);

        return rowView;
    }
}
