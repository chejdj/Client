package com.aunt.ui;

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

import com.aunt.AuntApplication;
import com.aunt.R;
import com.aunt.widget.RefreshableView;
import com.aunt.widget.timelineadpater;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chejdj on 2017/5/5.
 */
public class BusDetailActivity extends Activity {
    private ListView list;
    private Button back_bus_information;
    private timelineadpater adapter;
    private AuntApplication tracp;
    private RefreshableView refre;
    private String bus_user;
    private List<Map<String, Object>> list1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bus_detail_information);
        list = findViewById(R.id.bus_detail_list);
        list.setDividerHeight(0);
        tracp = (AuntApplication) getApplicationContext();
        back_bus_information = findViewById(R.id.back_bus_information);
        refre = findViewById(R.id.refresh);
        Intent intent = getIntent();
        bus_user = intent.getStringExtra("bus_user");
        list1 = new ArrayList<Map<String, Object>>();
        initData(tracp.target, tracp.direct, list1);
        adapter = new timelineadpater(this, list1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BusDetailActivity.this, BaiduMapDetailActivity.class);
                intent.putExtra("bus_user", bus_user);
                startActivity(intent);
            }
        });
        back_bus_information.setOnClickListener(new backlistener());
        refre.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() throws InterruptedException {
                Thread.sleep(2000);
                tracp.queryhistory(bus_user);
                Message message = handler.obtainMessage(1);
                handler.sendMessageDelayed(message, 1000);
                refre.finishRefreshing();
            }
        }, 0);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initData(tracp.target, tracp.direct, list1);
            // adapter = new timelineadpater(getApplicationContext(),initData(tracp.target,tracp.direct));
            adapter.notifyDataSetChanged();
            //  list.setAdapter(adapter);
        }
    };

    class backlistener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BusDetailActivity.this, BusNumberActivity.class);
            startActivity(intent);
        }
    }

    private void initData(int target, boolean direct, List<Map<String, Object>> listItem) {
        listItem.clear();
        Calendar calendar = Calendar.getInstance();
        int type = 0;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 8) {
            type = 1;
        } else if (hour >= 8 && hour < 13) {
            type = 2;
        } else if (hour >= 13) {
            type = 3;
        }
        if (direct) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("station", "宿舍");
            if (type == 1) {
                map.put("time", "大约：07:20");
            } else if (type == 2) {
                map.put("time", "大约：11：40");
            } else if (type == 3) {
                map.put("time", "大约：16：40");
            }
            if (target >= 0) {
                map.put("is_arrived", 1);
            } else {
                map.put("is_arrived", 0);
            }
            listItem.add(map);
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("station", "食堂");
            if (type == 1) {
                map1.put("time", "大约：07:33");
            } else if (type == 2) {
                map1.put("time", "大约：11：50");
            } else if (type == 3) {
                map1.put("time", "大约：16:55");
            }
            if (target >= 1) {
                map1.put("is_arrived", 1);
            } else {
                map1.put("is_arrived", 0);
            }
            listItem.add(map1);
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("station", "教学楼");
            if (type == 1) {
                map2.put("time", "大约：07:51");
            } else if (type == 2) {
                map2.put("time", "大约：12：21");
            } else if (type == 3) {
                map2.put("time", "大约：17.20");
            }
            if (target >= 2) {
                map2.put("is_arrived", 1);
            } else {
                map2.put("is_arrived", 0);
            }
            listItem.add(map2);
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("station", "教学楼");
            if (type == 1) {
                map.put("time", "大约：07:20");
            } else if (type == 2) {
                map.put("time", "大约：11：40");
            } else if (type == 3) {
                map.put("time", "大约：16：40");
            }
            if (target >= 0) {
                map.put("is_arrived", 1);
                Log.e("LBS", "教学区点亮");
            } else {
                map.put("is_arrived", 0);
            }
            listItem.add(map);
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("station", "食堂");
            if (type == 1) {
                map1.put("time", "大约：07:33");
            } else if (type == 2) {
                map1.put("time", "大约：11：50");
            } else if (type == 3) {
                map1.put("time", "大约：16:55");
            }
            if (target >= 1) {
                map1.put("is_arrived", 1);
            } else {
                map1.put("is_arrived", 0);
            }
            listItem.add(map1);
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("station", "宿舍");
            if (type == 1) {
                map2.put("time", "大约：07:51");
            } else if (type == 2) {
                map2.put("time", "大约：12：21");
            } else if (type == 3) {
                map2.put("time", "大约：17.20");
            }
            if (target >= 2) {
                map2.put("is_arrived", 1);
            } else {
                map2.put("is_arrived", 0);
            }
            listItem.add(map2);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(BusDetailActivity.this, BusNumberActivity.class);
            startActivity(intent);
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
