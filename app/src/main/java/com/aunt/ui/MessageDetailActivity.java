package com.aunt.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aunt.R;
import com.aunt.util.Httpservice;


/**
 * Created by Administrator on 2017/9/25.
 */

public class MessageDetailActivity extends Activity {
    private EditText msg;
    private Button mbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.msg_deliver);
        msg=(EditText)findViewById(R.id.edit_msg);
        mbutton=(Button)findViewById(R.id.deliver_msg);
        mbutton.setOnClickListener(new deliverlistener());
    }
    class deliverlistener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String content = msg.getText().toString();
            if(content.isEmpty()){
                Toast.makeText(getApplicationContext(),
                        "发贴失败",Toast.LENGTH_SHORT).show();
            }else{
                Log.e("Http","开始发送消息");
               new Httpservice("http://47.95.210.46:8080/newMurmur",content,1
                      ,getApplication()).start();
            }
            Intent intent = new Intent(MessageDetailActivity.this, MessageWallActivity.class);
            startActivity(intent);
        }
    }
}
