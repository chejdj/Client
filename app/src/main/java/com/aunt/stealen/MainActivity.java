package com.aunt.stealen;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.aunt.tool.SenseorManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private MapView mv;
    LocationManager map;
    private Button back;
    private Button satellite;
    BaiduMap map2;
    MyApplication1 trackap=null;
    SenseorManager myoritentionlistener;
    AlertDialog dialog;
    double mCurrentLantitude;
    String lantitude;
    String longtitude;
    double mCurrentLongitude;
    MyLocationConfiguration config;
    AlertDialog dialog2;
    MyLocationListener mylocationlistener;
    LocationClient locationclient;
    LBSTraceClient client;
    LocationClientOption option;
    BitmapDescriptor mCurrentMarker;
    int kinds =0;
    private boolean change =false;
    float myCurrentAccary=0;
    private volatile boolean isFristLocation = true;
    boolean gps=true;
    float mXdirecation;
    LocationManager lm;
    private PolylineOptions polyline;
    MarkerOptions  startMarker;
    MarkerOptions  endMarker;
    long serviceID = 127068;
    String own_name=null;
    int simplereturn =0;
    int isProcessed =1;
    int pageSize = 1000;
    int pageIndex = 1;
    JSONArray entits;
    private Context mcontext;
    private static final int BAIDU_READ_PHONE_STATE =1;
    private Overlay bus_icon;
    private String bus_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //对整张地图进行初始化！
        trackap = (MyApplication1) getApplicationContext();
        back =(Button)findViewById(R.id.main);
        satellite=(Button)findViewById(R.id.satellite);
        satellite.setOnClickListener(new changemaplistener());
        back.setOnClickListener(new backlistener());
        own_name=trackap.getPreference().getString("entityName","");
        mcontext = getApplicationContext();
        Intent intent = getIntent();
        bus_user=intent.getStringExtra("bus_user");
        if(Build.VERSION.SDK_INT>=23)//判断是否是6.0
        {
            if(mcontext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
               ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,},BAIDU_READ_PHONE_STATE);
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},BAIDU_READ_PHONE_STATE);
            }
        }
        else
        {
            init();
            initoritention();
            //检查是否打开GPS
            initGPS();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch(requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                      init();
                    initoritention();
                   //检查是否打开GPS
                    initGPS();

                } else{
                   Toast.makeText(getApplicationContext(),"拒绝将导致运用无法使用",Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
    class changemaplistener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(!change)
            {
                map2.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                change = true;}
            else
            {
                map2.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                change = false;
            }
        }
    }
    class backlistener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Intent intent =new Intent(MainActivity.this,bus_detail_information.class);
            intent.putExtra("bus_user",bus_user);
            startActivity(intent);
        }
    }
    public void initoritention()
    {
        myoritentionlistener = new SenseorManager(getApplicationContext());
        myoritentionlistener.setOnOrientationListener(new SenseorManager.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                // TODO Auto-generated method stub
                mXdirecation =  x;
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(myCurrentAccary)//去掉大头针旁边的圆圈
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXdirecation)
                        .latitude(mCurrentLantitude)
                        .longitude(mCurrentLongitude).build();
                // 设置定位数据
                map2.setMyLocationData(locData);
                // 设置自定义图标
                map2.setMyLocationConfigeration(config);
            }
        });
    }
    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(MainActivity.this, "请打开GPS",
                    Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    Toast.makeText(MainActivity.this,"可能导致位置有偏差",Toast.LENGTH_SHORT).show();
                }
            } );
            dialog.show();
        }
    }
    private void init() {
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.main_icon_follow);//为自己的位置上面，添加图标
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mv = (MapView) findViewById(R.id.mv);
        mv.showZoomControls(false);
        map = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        map2 = mv.getMap();
        map2.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        map2.setMyLocationEnabled(true);
        //对百度图标logo进行隐藏！
        View child = mv.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        client = new LBSTraceClient(getApplicationContext());
        //初始化位置管理
        mylocationlistener = new MyLocationListener();//显示自己位置
        locationclient = new LocationClient(getApplicationContext());
        option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(10000);//10秒进行一次定位位置
        option.setIsNeedLocationDescribe(true);
        option.setEnableSimulateGps(true);//虚拟GPS开启
        locationclient.setLocOption(option);
        locationclient.registerLocationListener(mylocationlistener);
        locationclient.start();

        //获取轨迹并且绘制出来。
        //startTime = (int)((System.currentTimeMillis())/1000-30*60);
       new Thread(new Myrunnable()).start();
       initData();
    }
    private void initData()//只是实验
    {
        List<LatLng> points = new ArrayList<LatLng>();
        LatLng temp = new LatLng(39.092065911934,121.82439356637);//教学楼站
        LatLng temp1 = new LatLng(39.090574126707,121.82722507000);//食堂站
        LatLng temp2 = new LatLng(39.092589133332,121.82773592479002);//寝室站
        points.add(temp);
        LatLng temp3 = new LatLng(39.091521734195,121.82448697314);
         points.add(temp3);
         temp3 = new LatLng(39.091444129901,121.82484779098);
        points.add(temp3);
        temp3 = new LatLng(39.091479156303,121.82497341422);
        points.add(temp3);
        temp3 = new LatLng(39.09128170006,121.82506693565);
        points.add(temp3);
        temp3 = new LatLng(39.090904872039,121.82499467005);
        points.add(temp3);
        temp3 = new LatLng(39.090674126707,121.82622507091);
        points.add(temp3);
        points.add(temp1);
        points.add(temp2);
        BitmapDescriptor bmStart = BitmapDescriptorFactory.fromResource(R.drawable.point_station);
        BitmapDescriptor bmStart1 = BitmapDescriptorFactory.fromResource(R.drawable.point_station);
        BitmapDescriptor bmStart2 = BitmapDescriptorFactory.fromResource(R.drawable.point_station);
        startMarker = new MarkerOptions()
                .position(temp).icon(bmStart)
                .zIndex(9).draggable(true);
         map2.addOverlay(startMarker);
         startMarker = new MarkerOptions()
                .position(temp1).icon(bmStart1)
                .zIndex(9).draggable(true);
        map2.addOverlay(startMarker);
        startMarker = new MarkerOptions()
                .position(temp2).icon(bmStart2)
                .zIndex(9).draggable(true);
        map2.addOverlay(startMarker);
        polyline  = new PolylineOptions().width(8).color(R.color.colorLine).points(points);
        map2.addOverlay(polyline);
    }
    private  class Myrunnable implements  Runnable{
        @Override
        public void run() {//就可以直接找到hanler对象，进行处理
            Message message = handler.obtainMessage(1);     // Message,显示公交车的位置
            handler.sendMessageDelayed(message,2000);
        }
    }
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1)
            {
               queryhistorytrack();
            }
        }
    };
    private void queryhistorytrack()
    {
        trackap.queryhistory(bus_user);
        if(!trackap.before_bustrack.isEmpty()){
            try {
                showHistoryTrack(trackap.before_bustrack);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void showHistoryTrack(String historyTrack) throws JSONException {
           JSONObject data = new JSONObject(historyTrack);
           JSONObject  end_point = data.getJSONObject("end_point");
        LatLng latLng =new LatLng
                (end_point.getDouble("latitude"),end_point.getDouble("longitude"));
            drawHistoryTrack(latLng);
        }
    public void drawHistoryTrack(LatLng points) {
        // 绘制新覆盖物前，清空之前的覆盖物
        // map2.clear();
        if(bus_icon!=null) {
            bus_icon.remove();
        }
        if (points == null) {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "当前查询无轨迹点", Toast.LENGTH_SHORT).show();
            Looper.loop();
           // resetMarker();
        } else  {
            BitmapDescriptor bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.map_bus);
              endMarker = new MarkerOptions().position(points)
                    .icon(bmEnd).zIndex(9).draggable(true);
            Toast.makeText(getApplicationContext(),points.toString(),Toast.LENGTH_SHORT).show();
            if (null != endMarker) {
                bus_icon=map2.addOverlay(endMarker);
            }
            Message message = handler.obtainMessage(1);     // Message
            handler.sendMessageDelayed(message,10000);
        }
    }
    private void opennet()
    {
        ConnectivityManager connectivitymanager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
        if(networkinfo!=null)
            return;
        dialog2 = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请打开网络")
                .setPositiveButton("已打开",null)
                .setCancelable(false)
                .create();
        dialog2.setCancelable(false);
        dialog2.show();
        //复写
        dialog2.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ConnectivityManager connectivitymanager2 = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkinfo2 = connectivitymanager2.getActiveNetworkInfo();
                if(networkinfo2!=null)
                {
                    dialog2.dismiss();
                    return ;
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  //对手机的返回键和菜单键的响应写出对应的功能
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           Intent intent=new Intent(MainActivity.this,bus_detail_information.class);
            intent.putExtra("bus_user",bus_user);
            startActivity(intent);
        }
        if(keyCode ==KeyEvent.KEYCODE_MENU)
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public  class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation arg0) {   //BDLocation arg0已经接受位置
            // TODO Auto-generated method stub
            if (arg0 == null)
            {
                opennet();
                return;
            }
            MyLocationData locData = new MyLocationData.Builder().accuracy(0)
                    .direction(mXdirecation).latitude(arg0.getLatitude()).longitude(arg0.getLongitude())
                    .build();
            mCurrentLantitude = arg0.getLatitude();
            mCurrentLongitude = arg0.getLongitude();
            map2.setMyLocationData(locData);
            if(kinds==1){
                kinds=0;
                config = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.COMPASS,true,mCurrentMarker);
                map2.setMyLocationConfigeration(config);
                LatLng ll = new LatLng(arg0.getLatitude(),
                        arg0.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,18f);//设定地图的放大倍数
                map2.animateMapStatus(u);
            }
            if (isFristLocation)
            {
                isFristLocation = false;
                lantitude=String.valueOf(mCurrentLantitude);
                longtitude=String.valueOf(mCurrentLongitude);
                LatLng ll = new LatLng(arg0.getLatitude(),
                        arg0.getLongitude());
                config = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL,true,mCurrentMarker);//mCurrentMarker
                map2.setMyLocationConfigeration(config);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,17f);//设定地图的放大倍数
                map2.animateMapStatus(u);//整张地图以u的位置为中心
                Toast.makeText(getApplicationContext(),arg0.getLocationDescribe(),Toast.LENGTH_SHORT).show();
            }
        }
        public void onConnectHotSpotMessage(String s, int i) {
              Log.e("ss",s);
        }
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        map2.setMyLocationEnabled(true);
        myoritentionlistener.start();
    }
    @Override
    protected void onStop(){
        super.onStop();
        Message msg =handler.obtainMessage(2);
        handler.removeMessages(1);
        handler.sendMessageDelayed(msg,6000);
    }
}

