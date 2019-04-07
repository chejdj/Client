package com.aunt.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aunt.AuntApplication;
import com.aunt.R;
import com.aunt.widget.CircleImageView;


/**
 * Created by 洋洋 on 2017/4/3.
 */

public class MainActivity extends Activity {
    private CircleImageView icon;
    private AuntApplication trackap;
    private LinearLayout layout1;
    private int touchback = 1;
    private LinearLayout layout2;
    private LinearLayout layout4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        trackap = (AuntApplication) getApplicationContext();
        setContentView(R.layout.main1);
        icon = findViewById(R.id.picture);
        icon.setOnClickListener(new picturelistener());
        trackap = (AuntApplication) getApplicationContext();
        layout1 = findViewById(R.id.busline);
        layout2 = findViewById(R.id.download);
        layout4 = findViewById(R.id.noknow);
        layout2.setOnClickListener(new downlistener());
        layout1.setOnClickListener(new buslinelistener());
        layout4.setOnClickListener(new delverlistener());
        if (trackap.own_picture() != null) {
            Bitmap photoBitmap = trackap.own_picture();
            icon.setImageBitmap(photoBitmap);
        } else {
            icon.setImageResource(R.drawable.school_target);
        }
    }

    class delverlistener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, MessageWallActivity.class);
            startActivity(intent);
        }
    }

    class downlistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, OfflineMapActivity.class);
            startActivity(intent);
        }
    }

    class buslinelistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //开始跳转了
            Intent intent = new Intent(MainActivity.this, BusNumberActivity.class);
            startActivity(intent);
        }
    }

    class picturelistener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  //对手机的返回键和菜单键的响应写出对应的功能
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (touchback % 2 == 0) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);//回到主页
                startActivity(intent);
                return true;
            } else {
                touchback++;
                Toast.makeText(getApplicationContext(), "再按一次回到主界面", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

