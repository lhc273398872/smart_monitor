package com.example.smart_monitor.driver.driver_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.ItemActivity;
import com.example.smart_monitor.adapter.ItemAdapter;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.util.HttpRequest;

import java.util.List;

import zuo.biao.library.base.BaseHttpListFragment;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.JSON;

public class CarItemListFragment extends BaseHttpListFragment<Goods, ListView, ItemAdapter>
        implements CacheCallBack<Goods>, OnHttpResponseListener {
    private static final String TAG = "CarItemListFragment";

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static final String DRIVER_ID = "DRIVER_ID";
    public static final String ORDER_ID = "ORDER_ID";
    public static final String ADMIN_ID = "ADMIN_ID";


    /**
     *
     * @param driver_id
     * @return
     */
    public static CarItemListFragment createInstance(long driver_id) {
        CarItemListFragment fragment = new CarItemListFragment();

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
    private long order_id = -1;
    private long admin_id = -1;
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

        srlBaseHttpList.autoRefresh();

        return view;
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initView() {//必须调用
        super.initView();

    }

    @Override
    public void setList(final List<Goods> list) {
        setList(new AdapterCallBack<ItemAdapter>() {

            @Override
            public ItemAdapter createAdapter() {
                return new ItemAdapter(context, false);
            }

            @Override
            public void refreshAdapter() {
                adapter.refresh(list);
            }
        });
    }



    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initData() {//必须调用
        super.initData();

    }

    private static final int GETGOODSLIST = 1;
    @Override
    public void getListAsync(final int page) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (driver_id != -1){
                    //需要使用page的负数来作为requestCode
                    //TODO * 通过driver_id来获取货物列表
                    HttpRequest.getInfo(driver_id, "queryMoreDriverGoods.do", GETGOODSLIST, CarItemListFragment.this);
                }
            }
        }, 1000);
    }

    @Override
    public List<Goods> parseArray(String json) {
        return JSON.parseArray(json, Goods.class);
    }


    @Override
    public Class<Goods> getCacheClass() {
        return Goods.class;
    }
    @Override
    public String getCacheGroup() {
        return "range=" + driver_id;
    }
    @Override
    public String getCacheId(Goods data) {
        return data == null ? null : "" + data.getGoods_id();
    }
    @Override
    public int getCacheCount() {
        //TODO 需要设置为获取数据库中仓库物品总量
        return 100;
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initEvent() {//必须调用
        super.initEvent();

    }

    private List<Goods> goodsList;
    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {

        if (resultJson == null){
            showShortToast(R.string.get_failed);
            return;
        }

        goodsList = JSON.parseArray(resultJson, Goods.class);

        super.onHttpResponse(requestCode, resultJson, e);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        for (Goods goods : goodsList){
            if (goods.getGoods_id() == id){
                toActivity(ItemActivity.createIntent(context, goods.getOrder_id(), id, true));
            }
        }

    }

    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            return;
        }
        switch (requestCode) {

        }
    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}