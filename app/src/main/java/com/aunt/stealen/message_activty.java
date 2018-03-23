package com.aunt.stealen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


import com.aunt.tool.ContentBean;
import com.aunt.tool.Httpservice;
import com.aunt.view.RefreshableView;
import com.aunt.view.messageadapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;


public class message_activty extends Activity {
    private ImageView imageView;
    private Button btn;
    private ListView list;
    private RefreshableView refreshableView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
        imageView=(ImageView)findViewById(R.id.deliver);
        btn=(Button)findViewById(R.id.back_msg);
        refreshableView=(RefreshableView)findViewById(R.id.msg_refer);
        list=(ListView)findViewById(R.id.mesg_lst);
        imageView.setOnClickListener(new deliverlistener());
        btn.setOnClickListener(new backlistener());
         init();
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() throws InterruptedException {
                Thread.sleep(2000);
                Message message = handler.obtainMessage(1);
                handler.sendMessageDelayed(message,1000);
                refreshableView.finishRefreshing();
            }
        },11);
    }
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
                init();
        }
    };
   private void init(){
     Httpservice httpservice= new
             Httpservice("http://47.95.210.46:8080/display","接收到了吗",2,getApplicationContext());
       httpservice.start();
        String data=null ;
       while(data==null){
           data=httpservice.getInformation();
       }
      if(data!=null) {
           //gson解析
          Log.e("Http","开始解析");
          Gson gs = new Gson();
          ArrayList<ContentBean> contentlist = gs.fromJson(data,
                  new TypeToken<ArrayList<ContentBean>>() {
                  }.getType());
          Log.e("Http",contentlist.size()+" ");
          Collections.reverse(contentlist);
          list.setAdapter(new messageadapter(contentlist));
      }
   }
    class deliverlistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
               Intent intent = new Intent(message_activty.this,message_detail.class);
              startActivity(intent);
        }
    }
    class backlistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(message_activty.this,Main1Activity.class);
            startActivity(intent);
        }
    }
}
