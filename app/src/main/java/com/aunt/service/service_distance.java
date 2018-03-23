package com.aunt.service;

import android.util.Log;


import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */
public class service_distance {
    private class point{
        public LatLng latLng;
        boolean is_arrived;
        point(LatLng latLng){
            this.latLng=latLng;
            this.is_arrived=false;
        }
        public void setIs_arrived(boolean is_arrived){
            this.is_arrived=is_arrived;
        }
    }
    private LatLng[] temp2;
    private List<LatLng> latLngs;
    public int target=0;   //表示站点
    private boolean direct=false;
     private ArrayList<point> list_point;
    public service_distance(List<LatLng> latLngList, boolean direct)
    {
        this.direct=direct;
        this.latLngs=latLngList;
        this.list_point= new ArrayList<point>();
        temp2=new LatLng[3];
        temp2[0]= new LatLng(39.092065911934,121.82439356637);//0教学区
        temp2[1]= new LatLng(39.090574126707,121.82722507000);//1食堂
        temp2[2]= new LatLng(39.092589133332,121.82773592479002);//2宿舍
        if(!direct) {
            for (int i = 0; i < temp2.length; i++) {
                list_point.add(new point(temp2[i]));   //false   教学区-宿舍
            }
        }
        else{
            for(int i=temp2.length-1;i>=0;i--){
                list_point.add(new point(temp2[i]));     //true  宿舍—教学区
            }
        }
    }
    public void calucate()
    {
         double[] data = new double[latLngs.size()*3];
           for(int i =0;i<list_point.size();i++){
                for(int j=0;j<latLngs.size();j++){
                   data[i*latLngs.size()+j]= DistanceUtil.getDistance
                          (list_point.get(i).latLng,latLngs.get(j));
                    if(data[i*latLngs.size()+j]<80.0){
                        list_point.get(i).setIs_arrived(true);
                    }
                }
               if(!list_point.get(i).is_arrived){
                   target=i-1;
                   break;
               }
               if(list_point.get(i).is_arrived&&i==list_point.size()-1){
                   target=i;
               }
           }
        Log.e("LBS","标志站台为 ："+target);
    }
}
