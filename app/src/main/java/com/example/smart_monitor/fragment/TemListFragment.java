package com.example.smart_monitor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.ItemActivity;
import com.example.smart_monitor.adapter.ItemAdapter;
import com.example.smart_monitor.adapter.TemAdapter;
import com.example.smart_monitor.model.CarTems;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.util.HttpRequest;

import java.util.List;

import zuo.biao.library.base.BaseHttpListFragment;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.CacheManager;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;

public class TemListFragment extends BaseHttpListFragment<CarTems, ListView, TemAdapter>
        implements CacheCallBack<CarTems>, OnHttpResponseListener {
    private static final String TAG = "TemListFragment";


    public static final String DRIVER_ID = "DRIVER_ID";

    public static TemListFragment createInstance(long driver_id) {
        TemListFragment fragment = new TemListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(DRIVER_ID, driver_id);

        fragment.setArguments(bundle);
        return fragment;
    }


    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    public static final int RANGE_ALL = HttpRequest.ITEM_LIST_RANGE_ALL;
    public static final int RANGE_RECOMMEND = HttpRequest.ITEM_LIST_RANGE_RECOMMEND;
    public static final int HTTP = HttpRequest.ITEM_LIST_HTTP;

    private long driver_id = -1;
    private boolean checkBoxFlag = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        argument = getArguments();
        if (argument != null) {
            driver_id = argument.getLong(DRIVER_ID, driver_id);
        }

        initCache(this);

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

        refreshList();

        return view;
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initView() {//必须调用
        super.initView();
    }

    @Override
    public void setList(final List<CarTems> list) {
        setList(new AdapterCallBack<TemAdapter>() {

            @Override
            public TemAdapter createAdapter() {
                return new TemAdapter(context);
            }

            @Override
            public void refreshAdapter() {
                adapter.refresh(list);
            }
        });
    }

    public List<CarTems> getCarTemsList(){
        return adapter.getList();
    }

    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initData() {//必须调用
        super.initData();

    }

    @Override
    public void getListAsync(final int page) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (driver_id != -1){
                    //需要使用page的负数来作为requestCode
                    //TODO 获取温度列表数据
                    HttpRequest.getInfo(driver_id , "queryMoreDriverTems.do",-page, TemListFragment.this);
                }
            }
        }, 1000);
    }

    @Override
    public List<CarTems> parseArray(String json) {
        return JSON.parseArray(json, CarTems.class);
    }


    @Override
    public Class<CarTems> getCacheClass() {
        return CarTems.class;
    }
    @Override
    public String getCacheGroup() {
        return "carTems=" + driver_id;
    }
    @Override
    public String getCacheId(CarTems data) {
        return data == null ? null : "" + data.getTem_id();
    }
    @Override
    public int getCacheCount() {
        //TODO 需要设置为获取数据库中仓库物品总量
        return 100;
    }

    public void refreshList(){
        srlBaseHttpList.autoRefresh();
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initEvent() {//必须调用
        super.initEvent();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}