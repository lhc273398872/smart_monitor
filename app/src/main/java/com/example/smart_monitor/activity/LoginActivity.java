package com.example.smart_monitor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.driver.driver_activity.DriverInfoActivity;
import com.example.smart_monitor.driver.driver_activity.DriverTabActivity;
import com.example.smart_monitor.model.AdminUser;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.util.HttpRequest;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;

public class LoginActivity extends BaseActivity
        implements View.OnClickListener, OnHttpResponseListener {
    private final String TAG = "LoginActivity";

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initView();
        initData();
        initEvent();
    }

    private EditText etAccount;
    private EditText etPwd;
    private ImageView ivPwdSwitch;
    private CheckBox cbRemPwd;
    private TextView tvSign;
    private Button btLogin;
    @Override
    public void initView() {
        //初始化登录界面各个控件id
        etAccount = findView(R.id.etAccount);
        etPwd = findView(R.id.etPwd);
        ivPwdSwitch = findView(R.id.ivPwdSwitch);
        cbRemPwd = findView(R.id.cbRemPwd);
        tvSign = findView(R.id.tvSign);
        btLogin = findView(R.id.btLogin);
    }

    @Override
    public void initData() {

    }

    public static final int SIGNRESULT = 1;
    @Override
    public void initEvent() {
        ivPwdSwitch.setOnClickListener(new pwdSwitch());
        //跳转至注册界面
        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignActivity.class);
                startActivityForResult(intent,SIGNRESULT);
            }
        });
        btLogin.setOnClickListener(this);
    }

    private boolean pwdVisibe = false;
    //切换密码的可视化
    private class pwdSwitch implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            pwdVisibe = !pwdVisibe;
            if (!pwdVisibe){
                ivPwdSwitch.setImageResource(
                        R.drawable.ic_visibility_off_black_24dp
                );
//                etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                ivPwdSwitch.setImageResource(
                        R.drawable.ic_visibility_black_24dp
                );
//                etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
//            etPwd.setTypeface(Typeface.DEFAULT);
        }
    }

    private final int LOGININFO = 1;
    private final int ADMINUSER = 2;
    private final int DRIVERUSER = 3;
    private String tel;
    private AdminUser adminUser;
    private Driver driver;
    @Override
    public void onClick(View v) {
        tel = etAccount.getText().toString();
        Log.d(TAG, "账号：" + tel + "密码:" +  etPwd.getText().toString());
        HttpRequest.login(tel, etPwd.getText().toString(), LOGININFO, this);
    }

    private Integer state;
    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {

        if (e != null){
            e.printStackTrace();
            Toast.makeText(this, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            return;
        }

        if (resultJson == null){
            Toast.makeText(this, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, resultJson);
        if (requestCode == LOGININFO){
            JSONObject result = JSON.parseObject(resultJson);
            Integer e_state = result.getInteger("e");
            state = result.getInteger("state");

            switch (e_state){
                case 0:
                    Toast.makeText(this, "账号不存在", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    String e_info = result.getString("e_info");
                    Toast.makeText(this, e_info, Toast.LENGTH_LONG).show();
                    break;
                default:
                    if (state == 0){
                        HttpRequest.getLoginUser(tel,"queryAdminUser.do", ADMINUSER, this);
                    } else {
                        HttpRequest.getLoginUser(tel, "queryDriverUser.do", DRIVERUSER, this);
                    }
                    break;
            }
        } else if (requestCode == ADMINUSER){
            adminUser = zuo.biao.library.util.JSON.parseObject(resultJson, AdminUser.class);

            if (adminUser == null){
                toActivity(AdminInfoActivity.createIntent(this, (long) 0, tel));
                return;
            } else {
//                showShortToast(adminUser.toString());
                saveAccount(state, cbRemPwd.isChecked());
            }
        } else if (requestCode == DRIVERUSER){
            driver = zuo.biao.library.util.JSON.parseObject(resultJson, Driver.class);

            if (driver == null){

                toActivity(DriverInfoActivity.createIntent(this, (long) 0, tel));
                return;
            } else {
//                showShortToast(driver.toString());
                saveAccount(state, cbRemPwd.isChecked());
            }
        }


//        toActivity(AdminTabActivity.createIntent(this, (AdminUser) null));
    }

    private void saveAccount(int state, boolean flag){
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (flag){
            editor.putBoolean("isLogin", true);
            editor.putInt("loginState", state);
        }

        switch (state){
            case 0:
                editor.putLong("id", adminUser.getAdmin_id());
                editor.apply();
                toActivity(AdminTabActivity.createIntent(this, adminUser.getAdmin_id()));
                break;
            case 1:
                editor.putLong("id", driver.getDriver_id());
                editor.apply();
                toActivity(DriverTabActivity.createIntent(this, driver.getDriver_id()));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNRESULT){
            etAccount.setText(data == null? "" : data.getStringExtra("tel"));
        }
    }
}

