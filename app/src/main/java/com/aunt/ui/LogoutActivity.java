package com.aunt.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.aunt.AuntApplication;
import com.aunt.R;
import com.aunt.util.ImageUtil;
import com.aunt.widget.CircleImageView;

import java.io.File;


public class LogoutActivity extends Activity {

    private Button logout;
    private Button own_more;
    private Button photograph;
    private Button album;
    private TextView user_name;
    private Button log_cancel;
    private Button back_main;
    private Button email;
    private Button own_route;
    private Uri imagePhotoUri;
    private AuntApplication trackap;
    private CircleImageView circleImageView;
    private PopupWindow mPopwindow;
    private SharedPreferences sharedPreferences;
    private static final int CODE_GALLERY_REQUEST = 0xa0;//本地
    private static final int CODE_CAMERA_REQUEST = 0xa1;//拍照
    private static final int CODE_RESULT_REQUEST = 0xa2;//最终裁剪后的结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {//savedInstanceState用于保存临时数据！！
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.own);
        back_main = (Button) findViewById(R.id.back_main);
        logout = (Button) findViewById(R.id.logout);
        own_more = (Button) findViewById(R.id.own_more);
        email = (Button) findViewById(R.id.out_email);
        circleImageView = (CircleImageView) findViewById(R.id.own_picture);
        own_route = (Button) findViewById(R.id.own_route);
        user_name = (TextView) findViewById(R.id.owner_name);
        back_main.setOnClickListener(new backlistener());
        own_more.setOnClickListener(new morelistener());
        logout.setOnClickListener(new logoutlistener());
        imagePhotoUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/com.foerstzhu", "image.png"));
        circleImageView.setOnClickListener(new picturelistener());
        trackap = (AuntApplication) getApplicationContext();
        sharedPreferences = trackap.getPreference();
        if (!(sharedPreferences.getString("entityName", "").equals(""))) {
            user_name.setText(sharedPreferences.getString("entityName", ""));
            email.setText(sharedPreferences.getString("email", ""));
        } else {
            user_name.setText("X某某");
        }
        if (trackap.own_picture() != null) {
            Bitmap photoBitmap = trackap.own_picture();
            circleImageView.setImageBitmap(photoBitmap);

        } else {
            circleImageView.setImageResource(R.drawable.school_target);
        }
        if (trackap.trace_route == 1)
            own_route.setText("开发区校区-本部");
        if (trackap.trace_route == 2)
            own_route.setText("本部-开发区校区");
    }

    class choice implements View.OnClickListener {
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.album: {
                    mPopwindow.dismiss();
                    choseHeadImageFromGallery();
                }
                break;
                case R.id.photograph: {
                    mPopwindow.dismiss();
                    choseHeadImageFromCameraCapture();
                }
                break;
                case R.id.own_cancel: {
                    mPopwindow.dismiss();
                }
                break;
            }
        }
    }

    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
// 设置文件类型
        intentFromGallery.setType("image/*");//选择图片
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imagePhotoUri);
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {//取消
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent_crop = new Intent("com.android.camera.action.CROP");
        switch (requestCode) {
            case CODE_GALLERY_REQUEST://如果是来自本地的
                Uri originalUri = intent.getData();
                intent_crop.setDataAndType(originalUri, "image/*");
                //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
                intent_crop.putExtra("crop", true);
                // 设置裁剪尺寸
                intent_crop.putExtra("aspectX", 1);
                intent_crop.putExtra("aspectY", 1);
                intent_crop.putExtra("outputX", 320);
                intent_crop.putExtra("outputY", 320);
                intent_crop.putExtra("return-data", true);
                intent_crop.putExtra(MediaStore.EXTRA_OUTPUT, originalUri);
                startActivityForResult(intent_crop, CODE_RESULT_REQUEST);
                break;
            case CODE_CAMERA_REQUEST:
                intent_crop.setDataAndType(imagePhotoUri, "image/*");
                //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
                // 设置裁剪尺寸
                intent_crop.putExtra("crop", "true");
                intent_crop.putExtra("aspectX", 1);
                intent_crop.putExtra("aspectY", 1);
                intent_crop.putExtra("outputX", 320);
                intent_crop.putExtra("outputY", 320);
                intent_crop.putExtra("return-data", true);
                intent_crop.putExtra(MediaStore.EXTRA_OUTPUT, imagePhotoUri);
                startActivityForResult(intent_crop, CODE_RESULT_REQUEST);
                break;
            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    Bundle bundle = intent.getExtras();
                    Bitmap myBitmap = bundle.getParcelable("data");
                    if (myBitmap != null) {
                        //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                        //  Bitmap smallBitmap = ImageUtil.zoomBitmap(myBitmap, myBitmap.getWidth() /5, myBitmap.getHeight() / 5);
                        //释放原始图片占用的内存，防止out of memory异常发生
                        // myBitmap.recycle();
                        circleImageView.setImageBitmap(myBitmap);
                        ImageUtil.savePhotoToSDCard(myBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), "image");
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    class picturelistener implements View.OnClickListener {

        @Override

        public void onClick(View v) {
            showpopwindow();
        }
    }/*
    弹出小框，头像选择！！
    */

    private void showpopwindow() {
        View contentView = LayoutInflater.from(LogoutActivity.this).inflate(R.layout.own_popwindow, null);
        mPopwindow = new PopupWindow(contentView, Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT, true);
        mPopwindow.setContentView(contentView);
        photograph = (Button) contentView.findViewById(R.id.photograph);
        album = (Button) contentView.findViewById(R.id.album);
        log_cancel = (Button) contentView.findViewById(R.id.own_cancel);
        photograph.setOnClickListener(new choice());
        album.setOnClickListener(new choice());
        log_cancel.setOnClickListener(new choice());
        mPopwindow.setBackgroundDrawable(new BitmapDrawable());//设置一个空的Bitmap
        View root_view = LayoutInflater.from(LogoutActivity.this).inflate(R.layout.own, null);
        mPopwindow.setAnimationStyle(R.style.contextMenuAnim);
        mPopwindow.setOutsideTouchable(true);
        mPopwindow.showAtLocation(root_view, Gravity.TOP, 0, 0);
    }

    class logoutlistener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            trackap.saveLoginInfo("", "", "");
            Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    class backlistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent_back = new Intent(LogoutActivity.this, MainActivity.class);
            startActivity(intent_back);
        }
    }

    class morelistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent_information = new Intent(LogoutActivity.this, BusActivity.class);
            startActivity(intent_information);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  //对手机的返回键和菜单键的响应写出对应的功能
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(LogoutActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
