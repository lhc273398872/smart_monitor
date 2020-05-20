package com.example.smart_monitor.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.util.HttpRequest;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;

public class SignActivity extends BaseActivity
        implements View.OnClickListener, OnHttpResponseListener {
    private final String TAG = "SignActivity";

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign);

        initView();
        initData();
        initEvent();
    }

    private ImageView admin_logo;
    private ImageView car_logo;
    private EditText sign_tel;
    private EditText sign_pswd;
    private EditText sign_pswd1;
    private EditText sign_code;
    private TextView bt_code;
    private Button bt_true;
    private Button bt_false;
    private ImageView tel_over;
    private ImageView pswd_over;
    private ImageView pswd1_over;
    private ImageView code_over;

    @Override
    public void initView() {
        //初始化登录界面各个控件id
        //响应按键式组件
        admin_logo = findView(R.id.admin_logo);
        car_logo = findView(R.id.car_logo);
        bt_code = findView(R.id.bt_code);
        bt_true = findView(R.id.bt_true);
        bt_false = findView(R.id.bt_false);

        //获取数据式组件
        sign_tel = findView(R.id.sign_tel);
        sign_pswd = findView(R.id.sign_pswd);
        sign_pswd1 = findView(R.id.sign_pswd1);
        sign_code = findView(R.id.sign_code);

        //获取图片式组件
        tel_over = findView(R.id.tel_over);
        pswd_over = findView(R.id.pswd_over);
        pswd1_over = findView(R.id.pswd1_over);
        code_over = findView(R.id.code_over);
    }

    @Override
    public void initData() {
        sign_tel.setInputType(EditorInfo.TYPE_CLASS_PHONE);
    }

    @Override
    public void initEvent() {
        //设置按钮式监听事件
        admin_logo.setOnClickListener(new logoSwitch());
        admin_logo.setTag("admin_logo");
        car_logo.setOnClickListener(new logoSwitch());
        car_logo.setTag("car_logo");
        bt_true.setOnClickListener(this);
        bt_true.setTag("bt_true");
        bt_false.setOnClickListener(this);
        bt_false.setTag("bt_false");

        //设置输入框监听事件
        sign_tel.setOnFocusChangeListener(new telOver());
        sign_pswd.setOnFocusChangeListener(new pswdOver());
        sign_pswd1.addTextChangedListener(new pswd1Over());
        //TODO ** 完成验证码功能
    }

    private boolean telFlag = false;
    private String telText;
    //监听手机号输入框
    private class telOver implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                if (sign_tel.getText().length() < 11){
                    Toast.makeText(context, "手机号不规范", Toast.LENGTH_SHORT).show();
                    tel_over.setImageResource(R.drawable.ic_do_not_disturb_red_24dp);
                    telFlag = false;
                } else {
                    tel_over.setImageResource(R.drawable.ic_check_blue_24dp);
                    telText = sign_tel.getText().toString();
                    telFlag = true;
                }
            }
        }
    }

    private boolean pswdFlag = false;
    private String pswdText;
    //监听密码输入框
    private class pswdOver implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (sign_pswd.getText().length() == 0){
                pswd_over.setImageResource(R.drawable.ic_do_not_disturb_red_24dp);
                pswdFlag = false;
            } else {
                pswd_over.setImageResource(R.drawable.ic_check_blue_24dp);
                pswdText = sign_pswd.getText().toString();
                pswdFlag = true;
            }
        }
    }

    private boolean pswd1Flag = false;
    private class pswd1Over implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (sign_pswd1.getText().length() == 0 || !sign_pswd1.getText().toString().equals(sign_pswd.getText().toString())){
                pswd1_over.setImageResource(R.drawable.ic_do_not_disturb_red_24dp);
                pswd1Flag = false;
            } else {
                pswd1_over.setImageResource(R.drawable.ic_check_blue_24dp);
                pswd1Flag = true;
            }
        }
    }

    private int logoState = -1;
    //切换logo状态
    private class logoSwitch implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if ("admin_logo".equals(v.getTag())) {
                if (logoState == 1){
                    car_logo.setImageResource(R.drawable.car_logo_black);
                }
                admin_logo.setImageResource(R.drawable.admin_log);
                logoState = 0;
            } else {
                if (logoState == 0){
                    admin_logo.setImageResource(R.drawable.admin_log_black);
                }
                car_logo.setImageResource(R.drawable.car_logo);
                logoState = 1;
            }
        }
    }

    private final int SIGNUPLOAD = 1;
    @SuppressLint("ShowToast")
    @Override
    public void onClick(View v) {
        if ("bt_true".equals(v.getTag())){
            Boolean flag = telFlag & pswdFlag & pswd1Flag;
            if (flag && logoState != -1){
                //调用HttpRequest中的注册
                HttpRequest.register(telText, pswdText, logoState, SIGNUPLOAD, this);
            } else if (logoState == -1){
                Toast.makeText(getApplicationContext(), "请选择自己的角色", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "请全部填写完毕", Toast.LENGTH_SHORT).show();
            }

        } else {
            finish();
        }
    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {

        if (e != null){
            e.printStackTrace();
            Toast.makeText(this, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, resultJson);
        JSONObject result = JSON.parseObject(resultJson);
        Integer e_state = result.getInteger("e");

        switch (e_state){
            case 0:
                Toast.makeText(this, "手机号已被注册", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                String e_info = result.getString("e_info");
                Toast.makeText(this, e_info, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "成功注册", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("tel", telText);
                setResult(LoginActivity.SIGNRESULT, intent);
                finish();
        }
    }

}

