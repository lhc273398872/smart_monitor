package com.example.smart_monitor.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.AdminInfoActivity;
import com.example.smart_monitor.activity.LoginActivity;
import com.example.smart_monitor.activity.SettingActivity;
import com.example.smart_monitor.driver.driver_activity.DriverInfoActivity;
import com.example.smart_monitor.util.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import zuo.biao.library.base.BaseFragment;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.ui.AlertDialog;
import zuo.biao.library.ui.AlertDialog.OnDialogButtonClickListener;

//重写 布局文件setting_fragment、Setting_Activity
public class SettingFragment extends BaseFragment
        implements OnClickListener, OnDialogButtonClickListener, OnHttpResponseListener {
//	private static final String TAG = "SettingFragment";

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**创建一个Fragment实例
     * @return
     */
    private static final String ID = "ID";
    private static final String USER_FLAG = "USER_FLAG";
    private static final String TAG = "SettingFragment";

    private static Activity activity;
    public static SettingFragment createInstance(Activity activity, Long id, boolean user_flag) {
        SettingFragment.activity = activity;

        SettingFragment fragment = new SettingFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(ID, id);
        bundle.putBoolean(USER_FLAG, user_flag);

        fragment.setArguments(bundle);
        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long id;
    private boolean user_flag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //类相关初始化，必须使用<<<<<<<<<<<<<<<<
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.setting_fragment);
        //类相关初始化，必须使用>>>>>>>>>>>>>>>>

        argument = getArguments();
        assert argument != null;
        id = argument.getLong(ID);
        user_flag = argument.getBoolean(USER_FLAG);

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

        return view;
    }



    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private ImageView ivSettingHead;
    @Override
    public void initView() {//必须调用

        ivSettingHead = findView(R.id.ivSettingHead);
    }




    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initData() {//必须调用

    }


    private void logout() {
        SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("isLogin");
        editor.remove("loginState");
        editor.remove("id");
        editor.apply();

        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        context.finish();

    }


    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用

        ivSettingHead.setOnClickListener(this);

        findView(R.id.llSettingSetting).setOnClickListener(this);
        findView(R.id.llSettingUserInfo).setOnClickListener(this);
        findView(R.id.llSettingLogout).setOnClickListener(this);
    }




    @Override
    public void onDialogButtonClick(int requestCode, boolean isPositive) {
        if (! isPositive) {
            return;
        }

        switch (requestCode) {
            case 0:
                logout();
                break;
            default:
                break;
        }
    }


    private static final int GETTEM = 1;
    @Override
    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.ivSettingHead:
                showShortToast("onClick  ivSettingHead");
                break;
            case R.id.llSettingSetting:
                toActivity(SettingActivity.createIntent(context));

                break;
            case R.id.llSettingUserInfo:
                if (user_flag){
                    toActivity(AdminInfoActivity.createIntent(context, id));
                } else {
                    toActivity(DriverInfoActivity.createIntent(context, id));
                }
                break;
            case R.id.llSettingLogout:
                new AlertDialog(context, "退出登录", "确定退出登录？", true, 0, this).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        if (e != null){
            e.printStackTrace();
            showShortToast("网络错误，请稍后重试");
            return;
        }

        if (resultJson != null){
            Log.d(TAG, "onHttpResponse: " + resultJson);
        }

    }


    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}