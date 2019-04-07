package com.aunt.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/21.
 */

public class Httpservice extends Thread {
    private String url;
    private String message;
    private int way;
    private String information = null;
    private Context mcontext;
    private Handler handler;
    private String username;
    private String password;
    private String email;

    public Httpservice(String url, String message, int way, Context mcontext) {
        this.url = url;
        this.message = message;
        this.way = way;
        this.mcontext = mcontext;
    }

    public Httpservice(String url, String username, String password, String email, int way, Context mcontext, Handler handler) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.email = email;
        this.way = way;
        this.mcontext = mcontext;
        this.handler = handler;
    }

    public Httpservice(String url, String username, String password, Handler handler, int way) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.handler = handler;
        this.way = way;
    }

    /*
      发送信息！ 1代表发帖的，2代表获取匿名墙信息
                   3代表注册信息，4代表登录验证
     */
    public void run() {
        if (way == 1) {
            Log.e("Http", "执行post_message");
            post_message(message);
        } else if (way == 2) {
            get_message();
        } else if (way == 3) {
            register();
        } else if (way == 4) {
            login();
        }

    }

    public String getInformation() {
        return information;
    }

    private void post_message(String msg) {
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("message", msg)
                .add("good", "0").build();
        Request request = new Request.Builder()
                .url(url).post(body).build();
        Call call = httpClient.newCall(request);
        try {
            Response response = call.execute();
            String data = response.body().toString();
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.get("status") != "true") {
                Log.e("Http", "失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
         接受信息
     */
    public void get_message() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("message", "请求获取数据")
                .build();
        Request request = new Request.Builder()
                .url(url).post(body).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            information = response.body().string();
            if (information.isEmpty()) {
                Toast.makeText(mcontext, "接受信息失败", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("email", email).build();
        Request request = new Request.Builder()
                .url(url).post(body).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String data = response.body().string();
            JSONObject jsonObject = new JSONObject(data);
            if (String.valueOf(jsonObject.get("status")) != "true") {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password).build();
        Request request = new Request.Builder()
                .url(url).post(body).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String data = response.body().string();
            JSONObject jsonObject = new JSONObject(data);
            if (String.valueOf(jsonObject.get("status")) != "true") {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = jsonObject.get("email");
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
