package com.example.smart_monitor.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentTransaction;

import com.example.smart_monitor.R;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.view.OrderView;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.manager.CacheManager;

public class OrderGoodsActivity extends BaseActivity implements OnBottomDragListener {
    public static final String TAG = "OrderActivity";

    public static String ORDER_ID = "ORDER_ID";
    public static String ADMIN_ID = "ADMIN_ID";
    public static String DRIVER_ID = "DRIVER_ID";

    private final int GETORDERGOODS = 1;
    private final int GETDRIVER = 2;
    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 获取启动ItemActivity的intent
     *
     * @param context
     * @param order_id
     * @param admin_id
     * @return
     */
    public static Intent createIntent(Context context, long order_id, long admin_id) {
        return new Intent(context, OrderGoodsActivity.class).
                putExtra(ADMIN_ID, admin_id).
                putExtra(ORDER_ID, order_id);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    private long admin_id = -1;
    private long order_id = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_goods_activity);

        intent = getIntent();
        admin_id = intent.getLongExtra(ADMIN_ID, admin_id);
        order_id = intent.getLongExtra(ORDER_ID, order_id);

        initView();
        initData();
        initEvent();
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private OrderView orderView;
    private ViewGroup llOrderBusinessCardContainer;//方式三
    @Override
    public void initView() {//必须调用
        llOrderBusinessCardContainer = findView(R.id.llOrderBusinessCardContainer);
        llOrderBusinessCardContainer.removeAllViews();

        orderView = new OrderView(context, null, true);
        llOrderBusinessCardContainer.addView(orderView.createView());
    }

    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private FragmentTransaction ft;
    @Override
    public void initData() {
        ft = fragmentManager.beginTransaction();
        ft.add(R.id.flOrderGoodsFragmentContainer, ItemListFragment.createInstance(order_id, admin_id));
        ft.commitNow();

        runThread(TAG + "initData", new Runnable() {
            @Override
            public void run() {
                Order order = CacheManager.getInstance().get(Order.class, "" + order_id);//先加载缓存数据，比网络请求快很多
                orderView.bindView(order);
            }
        });
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用

    }


    //对应HttpRequest.getUser(userId, 0, UserActivity.this); <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Override
    public void onDragBottom(boolean rightToLeft) {
        if (rightToLeft) {

            return;
        }
        finish();
    }

    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //内部类,
    // 尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //内部类,
    // 尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
