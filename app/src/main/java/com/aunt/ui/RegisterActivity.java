package com.aunt.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aunt.R;
import com.aunt.util.Httpservice;

/**
 * Created by Administrator on 2017/12/4.
 */

public class RegisterActivity extends Activity implements View.OnClickListener {
    private EditText username;
    private EditText password;
    private EditText password1;
    private EditText email;
    private Button btn;
    private Button back;
    private Handler handler;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.re_username);
        password = (EditText) findViewById(R.id.re_password);
        password1 = (EditText) findViewById(R.id.re_repassword);
        email = (EditText) findViewById(R.id.id_mail);
        btn = (Button) findViewById(R.id.re_registbtn);
        back = (Button) findViewById(R.id.re_main);
        back.setOnClickListener(this);
        btn.setOnClickListener(this);
        init();
    }
    private void init(){
        context=this;
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0){
                    Toast.makeText(context,"账号已被注册！",Toast.LENGTH_SHORT).show();
                }else if(msg.what==1){
                    Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_main:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.re_registbtn:
                if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty() || password1.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {
                    Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
                } else if (!email.getText().toString().contains("@")) {
                    Toast.makeText(this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
                } else if (!password.getText().toString().equals(password1.getText().toString())) {
                    Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
                } else {
             new Httpservice("http://api.yangsen.zhuangcloud.cn:8080/register",username.getText().toString(),password.getText().toString(),
                     email.getText().toString(),3,context,handler).start();
                }
                break;
            default:
                Toast.makeText(this, "你按了哪里？", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
