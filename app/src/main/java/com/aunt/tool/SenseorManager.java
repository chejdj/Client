package com.aunt.tool;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SenseorManager implements SensorEventListener {

    private SensorManager mSensorManager;  
    private Sensor accmSensor;
    private Sensor manmSensor;
    private Context mContext;
    private float lastX;
    private float[] r;
    private float[] values;
    private OnOrientationListener mOnOrientationListener; 
    public SenseorManager(Context context)  
    {  
        this.mContext=context;  
    }  
    public void start()  
    {  
        mSensorManager= (SensorManager) mContext  
                .getSystemService(Context.SENSOR_SERVICE);
        r=new float[9];
        values=new float[3];
        if(mSensorManager!= null)  
        {  //应该使用加速度传感器和地磁场传感器
            accmSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            manmSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
           // mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            //上面的代码实际上已经过期了，
            //mSensorManager.getOrientation();   由加速度传感器和地磁场传感器组
            mSensorManager.getOrientation(r,values);
        }
        if(accmSensor!=null && manmSensor!=null)
        {  

            mSensorManager.registerListener(this,accmSensor,SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this,manmSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }  
  
  
    }  
    public void stop()  
    {  
        mSensorManager.unregisterListener(this);  
  
    }  
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.sensor.getType()==Sensor.TYPE_ORIENTATION)  
        {  
            float x=arg0.values[SensorManager.DATA_X];
            // values[0] 代表  围绕Z的
            //values[1] 代表 围绕X
            //values[2] 代表围绕Y
            if(Math.abs(x-lastX)>1.0)  
            {  
                
                    mOnOrientationListener.onOrientationChanged(x);  
            }  
            lastX=x;
  
        }  
	}
	public void setOnOrientationListener(OnOrientationListener onOrientationListener)
	{
		this.mOnOrientationListener = onOrientationListener ;
	}
	
	    public interface OnOrientationListener  
	    {  
	        void onOrientationChanged(float x);  
	  
	    }  
	  

}
