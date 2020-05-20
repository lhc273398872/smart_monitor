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

import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.ItemActivity;
import com.example.smart_monitor.adapter.ItemAdapter;
import com.example.smart_monitor.adapter.OrderAdapter;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.util.ItemUtil;
import com.example.smart_monitor.util.OrderUtil;

import java.util.List;
import java.util.Map;

import zuo.biao.library.base.BaseHttpListFragment;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.manager.CacheManager;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

/**用户列表界面fragment
 * @author Lemon
 * @use new UserListFragment(),详细使用见.DemoFragmentActivity(initData方法内)
 * @must 查看 .HttpManager 中的@must和@warn
 *       查看 .SettingUtil 中的@must和@warn
 */
//重写了
//TODO 覆写OrderUtil, OrderAdapter, OrderView, HttpRequest ,Orders
public class OrderListFragment extends BaseHttpListFragment<Order, ListView, OrderAdapter> implements CacheCallBack<Order> {
    //	private static final String TAG = "OrderListFragment";

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static final String ADMIN_ID = "ADMIN_ID";

    public static OrderListFragment createInstance(long admin_id) {
        OrderListFragment fragment = new OrderListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(ADMIN_ID, admin_id);

        fragment.setArguments(bundle);
        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //TODO * 之后需改
    public static final int RANGE_ALL = HttpRequest.ITEM_LIST_RANGE_ALL;
    public static final int RANGE_RECOMMEND = HttpRequest.ITEM_LIST_RANGE_RECOMMEND;

    private long admin_id = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        argument = getArguments();
        admin_id = argument.getLong(ADMIN_ID, -1);


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
    public void setList(final List<Order> list) {
        //TODO * 修改为order
        setList(new AdapterCallBack<OrderAdapter>() {

            @Override
            public OrderAdapter createAdapter() {
                return new OrderAdapter(context);
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

    @Override
    public void getListAsync(final int page) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                HttpRequest.getInfo(admin_id, "queryMoreOrders.do", -page, OrderListFragment.this);
            }
        }, 1000);
    }

    @Override
    public List<Order> parseArray(String json) {
        return JSON.parseArray(json, Order.class);
    }

    @Override
    public Class<Order> getCacheClass() {
        Log.d("test","lhc:" + Order.class);
        return Order.class;
    }

    @Override
    public String getCacheGroup() {
        return "range=" + admin_id;
    }
    @Override
    public String getCacheId(Order data) {
        return data == null ? null : "" + data.getOrder_id();
    }
    @Override
    public int getCacheCount() {
        //TODO 需要设置为获取数据库中仓库物品总量
        return 10;
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initEvent() {//必须调用
        super.initEvent();

    }

    //TODO * 修改为Orders
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id > 0) {
            showShortToast("点击多选框");
//            toActivity(ItemActivity.createIntent(context, id),1);
//            Log.d("test","lhcItemClicktest");
        }
    }


    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //TODO * 修改为Order
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List map;

        if (resultCode != RESULT_OK){
            return;
        }
        switch (requestCode) {
            case 1:
                List<Order> ordersList = CacheManager.getInstance().getList(getCacheClass(),getCacheGroup(),0,getCacheCount());
                setList(ordersList);
                //TODO 将修改后的数据传到数据库中

                break;
        }


        Log.d("test","lhc_result_itemList:" + CacheManager.getInstance().get(Order.class, data.getStringExtra(RESULT_DATA)));
    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}