package com.example.smart_monitor.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.driver.driver_view.DriverOrderView;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.model.SaveHouse;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.view.OrderView;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.CacheManager;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.ui.EditTextInfoActivity;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;

public class OrderGoodsActivity extends BaseActivity
        implements OnBottomDragListener, OnHttpResponseListener {
    public static final String TAG = "OrderActivity";

    public static String ORDER_ID = "ORDER_ID";
    public static String ADMIN_ID = "ADMIN_ID";
    public static String DRIVER_ID = "DRIVER_ID";
    public static String USER_FLAG = "USER_FLAG";

    private final int GETORDERGOODS = 1;
    private final int GETDRIVER = 2;
    private final int DELORDER = 3;
    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     *
     * @param context
     * @param order_id
     * @param admin_id
     * @param user_flag
     *          user_flag: true：获取司机信息  false：获取管理员信息
     * @return
     */
    public static Intent createIntent(Context context, long order_id, long admin_id, boolean user_flag) {
        return new Intent(context, OrderGoodsActivity.class).
                putExtra(ADMIN_ID, admin_id).
                putExtra(ORDER_ID, order_id).
                putExtra(USER_FLAG, user_flag);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    private long admin_id = -1;
    private long order_id = -1;
    private boolean user_flag = false;

    private ViewGroup llOrderTopRightButton;
    private ImageView small_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_goods_activity);

        intent = getIntent();
        admin_id = intent.getLongExtra(ADMIN_ID, admin_id);
        order_id = intent.getLongExtra(ORDER_ID, order_id);
        user_flag = intent.getBooleanExtra(USER_FLAG, user_flag);

        initView();
        initData();
        initEvent();
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private OrderView orderView;
    private DriverOrderView driverOrderView;

    private ViewGroup llOrderBusinessCardContainer;//方式三
    @Override
    public void initView() {//必须调用
        llOrderBusinessCardContainer = findView(R.id.llOrderBusinessCardContainer);
        llOrderTopRightButton = findView(R.id.llOrderTabTopRightButtonContainer);

        llOrderBusinessCardContainer.removeAllViews();
        llOrderTopRightButton.removeAllViews();

        small_delete = new ImageView(this);
        small_delete.setImageResource(R.drawable.ic_delete_whiet_24dp);

        if(user_flag){
            orderView = new OrderView(context, null, true);
            llOrderBusinessCardContainer.addView(orderView.createView());
        } else {
            driverOrderView = new DriverOrderView(context, null, true);
            llOrderBusinessCardContainer.addView(driverOrderView.createView());
        }
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

                if (user_flag){
                    orderView.bindView(order);

                    if (order.getOrder_state() != 0 && order.getOrder_state() != 1){
                        llOrderTopRightButton.addView(small_delete);
                    }

                } else {
                    driverOrderView.bindView(order);
                }
            }
        });
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用
        small_delete.setOnClickListener(new DelOnclick());
    }

    private static final String[] TOPBAR_TAG_NAMES = {"确定删除", "取消删除"};

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        if (e != null) {
            showShortToast(R.string.get_failed);
            e.printStackTrace();
            return;
        }

        if (resultJson == null){
            showShortToast(R.string.get_failed);
            return;
        }

        JSONObject result = com.alibaba.fastjson.JSON.parseObject(resultJson);
        int e_state = 0;

        switch (requestCode){
            case DELORDER:
                e_state = result.getInteger("e");
                switch (e_state){
                    case 0:
                        showShortToast("检查网络，也可能存在订单未完结");
                        break;
                    default:
                        showShortToast("删除订单成功");
                        finish();
                        break;
                }
                break;
        }
    }

    private class DelOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO * 完成删除订单
            Log.d(TAG, "order_id:" + order_id);
            toActivity(BottomMenuWindow.createIntent(context, TOPBAR_TAG_NAMES)
                    .putExtra(BottomMenuWindow.INTENT_TITLE, "确认是否删除（若订单仍在运输中，则无法被删除）"), DELORDER, false);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case DELORDER:
                switch (data.getIntExtra(BottomMenuWindow.RESULT_ITEM_ID, 1)) {
                    case 1:
                        //                        showShortToast("货物" + delete_goodsId + "取消删除");
                        break;
                    default:
                        //                        showShortToast("货物" + delete_goodsId + "删除货物");
                        HttpRequest.getInfo(order_id, "deleteSimpleOrder.do", DELORDER, OrderGoodsActivity.this);
                        break;
                }
        }
    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //内部类,
    // 尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //内部类,
    // 尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
