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
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.util.HttpRequest;

import java.util.List;

import zuo.biao.library.base.BaseHttpListFragment;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.CacheManager;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.ui.EditTextInfoActivity;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;

public class ItemListFragment extends BaseHttpListFragment<Goods, ListView, ItemAdapter>
        implements CacheCallBack<Goods>, OnHttpResponseListener {
    private static final String TAG = "ItemListFragment";


    public static final String HOUSE_ID = "HOUSE_ID";
    public static final String ORDER_ID = "ORDER_ID";
    public static final String ADMIN_ID = "ADMIN_ID";
    public static final String VISIBLE = "VISIBLE";

    public static ItemListFragment createInstance(long house_id) {
        ItemListFragment fragment = new ItemListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(HOUSE_ID, house_id);

        fragment.setArguments(bundle);
        return fragment;
    }

    public static ItemListFragment createInstance(long house_id, boolean checkBoxVisible) {
        ItemListFragment fragment = new ItemListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(HOUSE_ID, house_id);
        bundle.putBoolean(VISIBLE, checkBoxVisible);

        fragment.setArguments(bundle);
        return fragment;
    }

    public static ItemListFragment createInstance(long order_id, long admin_id) {
        ItemListFragment fragment = new ItemListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(ORDER_ID, order_id);
        bundle.putLong(ADMIN_ID, admin_id);

        fragment.setArguments(bundle);
        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    public static final int RANGE_ALL = HttpRequest.ITEM_LIST_RANGE_ALL;
    public static final int RANGE_RECOMMEND = HttpRequest.ITEM_LIST_RANGE_RECOMMEND;
    public static final int HTTP = HttpRequest.ITEM_LIST_HTTP;

    private long house_id = -1;
    private long order_id = -1;
    private long admin_id = -1;
    private boolean checkBoxFlag = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        argument = getArguments();
        if (argument != null) {
            house_id = argument.getLong(HOUSE_ID, house_id);
            order_id = argument.getLong(ORDER_ID, order_id);
            admin_id = argument.getLong(ADMIN_ID, admin_id);
            checkBoxFlag = argument.getBoolean(VISIBLE, checkBoxFlag);
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

    private LinearLayout llCheckBox;
    private LinearLayout llOrderGoodsNumber;

    @Override
    public void initView() {//必须调用
        super.initView();
    }

    @Override
    public void setList(final List<Goods> list) {
        setList(new AdapterCallBack<ItemAdapter>() {

            @Override
            public ItemAdapter createAdapter() {
                ItemAdapter itemAdapter = new ItemAdapter(context,checkBoxFlag);
                return itemAdapter;
            }

            @Override
            public void refreshAdapter() {
                adapter.refresh(list);
            }
        });
    }

    public List<Goods> getGoodsList(){
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
                if (house_id != -1){
                    //需要使用page的负数来作为requestCode
                    HttpRequest.getGoodsList(house_id , -page, ItemListFragment.this);
                } else if (order_id != -1 && admin_id != -1){
                    //通过order_id和admin_id来获取货物列表
                    HttpRequest.getOrderInfo(order_id, admin_id, "queryMoreOrdersGoods.do", -page, ItemListFragment.this);
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
        return "range=" + house_id;
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

        if (house_id != -1 && id > 0) {
            //此处的id为goods_id
            toActivity(ItemActivity.createIntent(context, house_id, id),1);
            Log.d("test","lhcItemClicktest");
        } else if (order_id != -1 && admin_id != -1){
            toActivity(ItemActivity.createIntent(context, order_id, id, true));
        }
    }

    private static final String[] TOPBAR_TAG_NAMES = {"确定删除", "取消删除"};
    private long delete_goodsId;
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        delete_goodsId = id;
        toActivity(BottomMenuWindow.createIntent(context, TOPBAR_TAG_NAMES)
                .putExtra(BottomMenuWindow.INTENT_TITLE, "确认是否删除（若订单中包含该货物，则货物无法被删除）"), DELETE_GOODS, false);

        return true;
    }

    private final static int DELETE_GOODS = 1;
    private final static int ADDGOODS = 9;
    public void runItemActivity(long house_id){
        toActivity(ItemActivity.createIntent(context, house_id,0), ADDGOODS);
    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {

        if (resultJson == null){
            showShortToast(R.string.get_failed);
            return;
        }

        switch (requestCode){
            case DELETE_GOODS:
                JSONObject result = com.alibaba.fastjson.JSON.parseObject(resultJson);
                Integer e_state = result.getInteger("e");
                switch (e_state){
                    case 0:
                        showShortToast("检查网络，也可能存在订单未完结");
                        break;
                    case 1:
                        showShortToast("成功删除货物");
                        refreshList();
                        break;
                }
                break;
            default:
                super.onHttpResponse(requestCode, resultJson, e);
                break;
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
            case DELETE_GOODS:
                switch (data.getIntExtra(BottomMenuWindow.RESULT_ITEM_ID, 1)){
                    case 1:
//                        showShortToast("货物" + delete_goodsId + "取消删除");
                        break;
                    default:
//                        showShortToast("货物" + delete_goodsId + "删除货物");
                        HttpRequest.getInfo(delete_goodsId, "deleteSimpleGoods.do" , DELETE_GOODS, ItemListFragment.this);
                        break;
                }
                break;
            case ADDGOODS:
                List<Goods> goodsList = CacheManager.getInstance().getList(getCacheClass(),getCacheGroup(),0,getCacheCount());
                setList(goodsList);
                refreshList();
                break;
        }
    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}