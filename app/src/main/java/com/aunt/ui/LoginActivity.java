package com.aunt.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aunt.AuntApplication;
import com.aunt.R;
import com.aunt.util.Httpservice;


/**
 * Created by 洋洋 on 2017/4/3.
 */

public class LoginActivity extends Activity {
    private Button btn;
    private AuntApplication trackap = null;
    private EditText car_name;
    private EditText password;
    private SharedPreferences shared = null;
    private TextView reg;
    private Handler handler;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_login);
        trackap = (AuntApplication) getApplicationContext();
        shared = trackap.getPreference();
        if (!(shared.getString("entityName", "").equals("") && shared.getString("password", "").equals(""))) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        car_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.user_number);
        reg = (TextView) findViewById(R.id.regi_ant);
        btn = (Button) findViewById(R.id.login);
        btn.setOnClickListener(new mylistener());
        reg.setOnClickListener(new RegisterListener());
        init();
    }

    private void init() {
        context = this;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    trackap.saveLoginInfo(car_name.getText().toString(), password.getText().toString(), (String) msg.obj);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };

    }

    class mylistener implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {//和服务器相连接
            // TODO Auto-generated method stub
            if (car_name.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "错误填写", Toast.LENGTH_SHORT).show();
            } else {
                //登录信息
                new Httpservice("http://api.yangsen.zhuangcloud.cn:8080/login", car_name.getText().toString(), password.getText().toString(), handler, 4).start();
            }
        }
    }

    class RegisterListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);//回到主页
            startActivity(intent);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
