package com.aunt.widget;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aunt.stealen.R;
import com.aunt.bean.ContentBean;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/9/21.
 */

public class messageadapter extends BaseAdapter{
    private ArrayList<ContentBean> list;
    public messageadapter(ArrayList<ContentBean> list){
        this.list=list;
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
        Item item=null;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.message_item,null);
             item =new Item();
            item.msg=(TextView)convertView.findViewById(R.id.msg);
            item.number=(TextView)convertView.findViewById(R.id.good_number);
            item.tpv=(ThumbUpView)convertView.findViewById(R.id.tpv);
            convertView.setTag(item);
        }else{
            item=(Item)convertView.getTag();
        }
        item.msg.setText(list.get(position).getMessage());
        final int number = (int)(1+Math.random()*100);
        Log.e("Http",number+"");
                item.number.setText(number+"");
        final Item finalItem = item;
        item.tpv.setOnThumbUp(new ThumbUpView.OnThumbUp() {
            @Override
            public void like(boolean like) {
                if(like){
                    finalItem.number.setText(String.valueOf(number+1));
                }else
                {
                    finalItem.number.setText(String.valueOf(number));
                }
            }
        });
        return  convertView;
    }
    static  class Item
    {
        public TextView msg;
        public TextView number;
        public ThumbUpView tpv;
    }
}
