package com.aunt.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.aunt.stealen.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/7.
 */

public class listchangeadapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    private LayoutInflater inflater;
    private Context mcontext;
    public listchangeadapter(Context context, List<Map<String,Object>> list1)
    {
        super();
        this.mcontext=context;
        this.list = list1;
        Log.e("List",list.toString());
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
        if (convertView == null) {
            inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.bus_more1, null);
            item = new Item();
            item.text1 = (TextView) convertView.findViewById(R.id.morning);
            item.text2 = (TextView) convertView.findViewById(R.id.starter);
            item.text3 = (TextView) convertView.findViewById(R.id.busnumber);
            item.text4 = (TextView) convertView.findViewById(R.id.evening);
            item.text5 = (TextView) convertView.findViewById(R.id.ending);
            item.text6 = (TextView) convertView.findViewById(R.id.detailed);
            convertView.setTag(item);//设置item
        } else {
            item = (Item) convertView.getTag();
        }
        item.text1.setText(list.get(position).get("text1").toString());
        item.text2.setText(list.get(position).get("text2").toString());
        item.text3.setText(list.get(position).get("text3").toString());
        item.text4.setText(list.get(position).get("text4").toString());
        item.text5.setText(list.get(position).get("text5").toString());
        item.text6.setText(list.get(position).get("text6").toString());
        return convertView;
    }
    private class Item{
        public  TextView text1;
        public TextView text2;
        public TextView text3;
        public TextView text4;
        public TextView text5;
        public TextView text6;
    }
}