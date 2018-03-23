package com.aunt.stealen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.aunt.view.RefreshableView;
import com.aunt.view.listchangeadapter;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 洋洋 on 2017/4/14.
 */

public class information_bus  extends Activity {
    private Button back;
    private ListView lv;
    LBSTraceClient client;
    long serviceID =127068;
    RefreshableView refreshableView;
    JSONArray entits;
    List<Map<String,Object>> listItem ;
    MyApplication1 trackap;
    listchangeadapter adapter;
    String own_name=null;
    String temp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bus_information);
        back = (Button) findViewById(R.id.back_main);
        lv = (ListView) findViewById(R.id.listview);
        refreshableView = (RefreshableView) findViewById(R.id.refreshableView);
        back.setOnClickListener(new mylistener());
        trackap = (MyApplication1) getApplication();
        own_name = trackap.getPreference().getString("entityName", "");
        listItem = new ArrayList<Map<String,Object>>();
        adapter = new listchangeadapter(getApplicationContext(),listItem);
        lv.setAdapter(adapter);
        init();
        /*
           下拉刷新事件
         */
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() throws InterruptedException {
                Thread.sleep(2000);
                long now_time = System.currentTimeMillis()/1000-60;//前2分钟
                client.queryEntityList(serviceID,null,null,1,(int)now_time,1000,1,new entitylistener());//init 是用来刷新列表的
                Log.e("LBS","执行结束");
                refreshableView.finishRefreshing();
            }
        }, 2);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    temp = entits.get(position).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(temp.charAt(0)=='1')
                    trackap.direct=false;
                else
                    trackap.direct=true;
                trackap.queryhistory(temp);
                Message message =new Message();
                message.what=2;
                hander.sendMessage(message);
            }
        });
    }
    final Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1)
            {
                Log.e("LBS",listItem.size()+"listitem的大小");
                    adapter = new listchangeadapter(getApplicationContext(),listItem);
                    lv.setAdapter(adapter);
            }
            else
            {
                Intent intent = new Intent(information_bus.this,bus_detail_information.class);
                intent.putExtra("bus_user",temp);
                startActivity(intent);
            }
        }
    };
    private List<Map<String, Object>> initlist()//展示列表 公交车的列表
    {
        listItem.clear();
        for(int i=0;i<entits.length();i++)//显示，两辆车
        {
            String temp=null;
            HashMap<String, Object> map = new HashMap<String, Object>();
            try {
                temp = entits.get(i).toString();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("Des",String.valueOf(temp.charAt(0)));
            if(temp.charAt(0)=='1')
                trackap.direct=false;
            else
                trackap.direct=true;
            Log.e("information——bus",String.valueOf(temp.charAt(0)));
            if(temp.charAt(0)=='1')
            {
                map.put("text1","起 07:00");//加入图片
                map.put("text2","教学楼");
                map.put("text3", temp.substring(1,temp.length()));
                map.put("text4", "终 22:00");
                map.put("text5", "学生宿舍");
                map.put("text6", "途径站点：教学楼-第五食堂-学生宿舍");
            }
            else if(temp.charAt(0) =='2') {
                map.put("text1", "起 07:00");//加入图片
                map.put("text2", "学生宿舍");
                map.put("text3", temp.substring(1,temp.length()));
                map.put("text4", "终 22:00");
                map.put("text5", "教学楼");
                map.put("text6", "途径站点：学生宿舍-第五食堂-教学楼");
            }
            listItem.add(map);
        }
        Message message =new Message();
        message.what=1;
        hander.sendMessage(message);
        return listItem;
    }
    public void init()
    {
        client = new LBSTraceClient(getApplicationContext());
        long now_time = System.currentTimeMillis()/1000-60;//前1分钟就行
        client.queryEntityList(serviceID,null,null,1,(int)now_time,1000,1,new entitylistener());
    }

    class entitylistener extends OnEntityListener{
        @Override
        public void onRequestFailedCallback(String s) {
        }
        @Override
        public void onQueryEntityListCallback(String message)
        {
            try {
                entits=null;
                JSONObject jsonObject = new JSONObject(message);
                Log.e("LBS",message);
                entits = jsonObject.getJSONArray("entities");
                initlist();
            }catch (JSONException e){
                Log.e("LBS","解析异常");
                initlist();
                e.printStackTrace();
            }
        }
    }
    class mylistener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(information_bus.this,Main1Activity.class);
            startActivity(intent);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  //对手机的返回键和菜单键的响应写出对应的功能
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(information_bus.this,Main1Activity.class);
            startActivity(intent);
        }
        if(keyCode ==KeyEvent.KEYCODE_MENU)
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}