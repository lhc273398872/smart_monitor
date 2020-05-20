package com.example.smart_monitor.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smart_monitor.R;
import com.example.smart_monitor.driver.driver_activity.DriverTabActivity;

import java.util.Timer;
import java.util.TimerTask;

import zuo.biao.library.util.Log;

public class WelcomeActivity extends Activity {
    private String TAG = "WelcomeActivity";
    private Long id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}
                    ,0);
        }

        /**
         * 读取sharedPreferences中的isLogin来判断之前是否已成功登录过
         *         isLogin为true后 再判断以下可能
         * 读取sharedPreferences中的isCompany来判断之前是否为公司管理员登录
         */
        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);

        boolean isLogin = sp.getBoolean("isLogin",false);
        id = sp.getLong("id", 0);

        if (isLogin && id != null){
            int loginState = sp.getInt("loginState", -1);
            if (loginState == -1){
                startLoginActivity();
            } else if (loginState == 0){
                startCompanyBottomTabActivity();
            } else {
                startDriverBottomTabActivity();
            }
        } else {
            startLoginActivity();
        }
        //TODO * 需判断是否已完善个人信息，不然跳转至完善个人信息界面

    }

    private void startCompanyBottomTabActivity(){
        TimerTask delayTask = new TimerTask() {
            @Override
            public void run() {
                startActivity(AdminTabActivity.createIntent(WelcomeActivity.this, id));
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(delayTask, 2000);
    }

    private void startDriverBottomTabActivity(){
        TimerTask delayTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "调用DriverBottomTabActivity");
                startActivity(DriverTabActivity.createIntent(WelcomeActivity.this, id));
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(delayTask, 2000);
    }

    private void startLoginActivity(){
        TimerTask delayTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(delayTask, 2000);
    }
}
