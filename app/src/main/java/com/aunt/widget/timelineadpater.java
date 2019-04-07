package com.aunt.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.aunt.stealen.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chejdj on 2017/5/5.
 */

public class timelineadpater extends BaseAdapter {
    private Context mcontext;
    private List<Map<String, Object>> list;
    private LayoutInflater inflater;
    private Boolean change=false;
    public timelineadpater(Context context, List<Map<String,Object>> list1)
    {
        super();
        this.mcontext=context;
        this.list = list1;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Item item = null;
        if(convertView==null)
        {
            inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.bus_detail_information_item,null);
            item = new Item();
            item.text = (TextView)convertView.findViewById(R.id.station);
            item.viewline=(View)convertView.findViewById(R.id.line_normal);
            item.viewline1=(View)convertView.findViewById(R.id.line_normal1);
            item.time=(TextView)convertView.findViewById(R.id.time);
            convertView.setTag(item);
        }
        else
        {
            item = (Item)convertView.getTag();
        }
        item.text.setText(list.get(position).get("station").toString());
        item.time.setText(list.get(position).get("time").toString());
        item.is_arrived = Integer.parseInt(list.get(position).get("is_arrived").toString());
        if(item.is_arrived==1)
        {
            item.viewline.setBackgroundColor(mcontext.getResources().getColor(R.color.colorAccent));
            item.viewline1.setBackgroundColor(mcontext.getResources().getColor(R.color.colorAccent));
        }
        if(position==0)
            item.viewline.setVisibility(View.INVISIBLE);
        if(position==list.size()-1) {
            item.viewline1.setVisibility(View.INVISIBLE);
            change=false;
        }
        return convertView;
    }
    static  class Item
    {
        public TextView text;
        public int is_arrived;
        public  View viewline;
        public View viewline1;
        public TextView time;
    }
}
