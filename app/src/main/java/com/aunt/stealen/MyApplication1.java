package com.aunt.stealen;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.aunt.service.service_distance;
import com.aunt.tool.GsonService;
import com.aunt.tool.HistoryTrackData;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 洋洋 on 2016/12/7.
 */

public class MyApplication1 extends Application {
    private Context context;
    private String Entityname=null;
    private Trace trace;
    private SharedPreferences shareD=null;
    private OnStartTraceListener startTraceListener;
    private OnStopTraceListener stopTraceListener;
    private LBSTraceClient client;
    private int gatherInterval = 5;
    private int packInterval = 30;
    private int traceType = 2;
    public int trace_route=0;
    public boolean isfistMaintivity=true;
    public boolean isTraceStarted = false;
    private Bitmap own_picture=null;
    List<Map<String,Object>> listItem ;
    public service_distance distance;
    public int target=0;
    public boolean direct=false;
    public String history_track="";
    public String before_bustrack="";
    private  long serviceID =127068;
    private int simplereturn =0;
    private int isProcessed =1;
    private int pageSize = 1000;
    private int pageIndex = 1;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context =getApplicationContext();
        SDKInitializer.initialize(this);
        intitclient();//初始化client
        trace_route=0;
        client.setLocationMode(LocationMode.High_Accuracy);
        client.setInterval(gatherInterval,packInterval);
        client.setProtocolType(traceType);
        shareD =context.getSharedPreferences("Secerct",MODE_PRIVATE);
        listItem = new ArrayList<Map<String,Object>>();
    }
    public OnTrackListener onTrackListener = new OnTrackListener() {
        @Override
        public void onRequestFailedCallback(String s) {
           Log.e("LBS","onstartlistener查询失败");
        }
        @Override
        public void onQueryHistoryTrackCallback(String s) {
            history_track = s;
            before_bustrack=s;
           Log.e("LBS","onQueryHistory"+s);
        }
    };
    public void start_distance(List<LatLng> latLngList)
    {
        distance=new service_distance(latLngList,this.direct);
        distance.calucate();
        target=distance.target;
    }
    public  void saveLoginInfo(String name,String password,String email)
    {
        SharedPreferences.Editor editor = shareD.edit();
        editor.putString("password",password);
        editor.putString("entityName",name);
        editor.putString("email",email);
        editor.apply();
    }
    public SharedPreferences getPreference()
    {
        return shareD;
    }
    public void intitclient(){
        client =new LBSTraceClient(context);
        startTraceListener =new OnStartTraceListener() {
            /*
             （a开启轨迹服务回调接口rg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
             */
            @Override
            public void onTraceCallback(int i, String s) {

            }
            /*
            onTracePushCallback用于接受服务推送消息
             */
            @Override
            public void onTracePushCallback(byte b, String s) {

            }
        };
        stopTraceListener =new OnStopTraceListener() {
            // 轨迹服务停止成功
            @Override
            public void onStopTraceSuccess() {
            }
            //轨迹服务停止失败
            @Override
            public void onStopTraceFailed(int i, String s) {
                Toast.makeText(getApplicationContext(),"轨迹停止失败",Toast.LENGTH_SHORT).show();
            }
        };
    }

    public Bitmap own_picture()
    {
        own_picture= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.foerstzhu" + "/" +"image" +".png");
        if(own_picture!=null)
         return own_picture;
        else
            return null;
    }
    public void queryhistory(String temp){
        long now_time = System.currentTimeMillis()/1000-60*60;//前60分钟就行
        int endTime = (int)(System.currentTimeMillis()/1000);//need_mapmatch=1,transport_mode=3
        client.queryHistoryTrack(serviceID,temp,simplereturn,
                isProcessed,"need_denoise=1",//need_denoise 去噪就可以了
                (int)now_time,endTime,pageSize,pageIndex,onTrackListener);
        while(history_track.isEmpty()){
            if(!history_track.isEmpty())break;}
        HistoryTrackData historyTrackData = GsonService.parseJson(history_track,
                HistoryTrackData.class);
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                List<LatLng> latLngList = new ArrayList<LatLng>();
                latLngList.addAll(historyTrackData.getListPoints());
                start_distance(latLngList);
                history_track="";
            }
        }
    }
}
