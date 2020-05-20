package com.example.smart_monitor.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.fragment.DriverListFragment;
import com.example.smart_monitor.fragment.HouseTabFragment;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.model.SaveHouse;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.util.ItemUtil;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.ui.EditTextInfoActivity;
import zuo.biao.library.ui.EditTextInfoWindow;
import zuo.biao.library.ui.TextClearSuit;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

public class OrderSelectActivity extends BaseActivity
        implements OnClickListener, OnHttpResponseListener, OnBottomDragListener {
    public static final String TAG = "OrderSelectActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final String ADMIN_ID = "admin_id";

    private final int GETHOUSE = 1;
    private final int UPDATA_ORDER = 2;
    private final int GETADDRESS = 3;

    private Driver driver;
    private Order order;

    public static Intent createIntent(Context context, Long admin_id, Long house_id) {
        return new Intent(context, OrderSelectActivity.class).
                putExtra(ADMIN_ID, admin_id).
                putExtra(INTENT_ID, house_id);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long admin_id = -1;
    private long house_id = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_select_activity);

        intent = getIntent();
        admin_id = intent.getLongExtra(ADMIN_ID, admin_id);
        if (admin_id < 0){
            finishWithError("用户不存在");
            return;
        }
        order = new Order();
        order.setAdmin_id(admin_id);
        order.setOrder_state(3);
        house_id = intent.getLongExtra(INTENT_ID, house_id);
        if (house_id < 0){
            finishWithError("仓库不存在！");
            return;
        }

        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private ImageButton ibtnReturnBack;
    private ImageButton ibtnSendOrder;

    private TextView currentHouseName;
    private LinearLayout llDesitination;
    private TextView tvOrderDestinationName;
    private boolean destinationFlag;

    private LinearLayout llOrderDriver;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FrameLayout flFragmentContainer;

    private TextView tvOrderDriverName;
    private TextView tvOrderDriverTel;

    @Override
    public void initView() {//必须调用
        ibtnReturnBack = findView(R.id.ibtnReturnBack);
        ibtnSendOrder = findView(R.id.ibtnSendOrder);

        currentHouseName = findView(R.id.currentHouseName);

        llDesitination = findView(R.id.llDesitination);
        tvOrderDestinationName = findView(R.id.tvOrderDestinationName);
        destinationFlag = false;

        llOrderDriver = findView(R.id.llOrderDriver);
        tvOrderDriverName = findView(R.id.tvOrderDriverName);
        tvOrderDriverTel = findView(R.id.tvOrderDriverTel);

        navigationView = findView(R.id.navigationView);
        drawerLayout = findView(R.id.drawerLayout);
        flFragmentContainer = findView(R.id.flFragmentContainer);
    }

    private SaveHouse house;
    private SaveHouse old_house;
    private void setHouse(final SaveHouse house_){
        this.house = house_;
        if (house == null){
            house = new SaveHouse();
        }
        if (old_house == null){
            old_house = (SaveHouse) ItemUtil.cloneObjBySerialization(house);
        }
        runUiThread(new Runnable() {
            @Override
            public void run() {
                currentHouseName.setText(StringUtil.getTrimedString(house.getHouse_name()));
            }
        });
    }

    private void setLocation(){
        runUiThread(new Runnable() {
            @Override
            public void run() {
                tvOrderDestinationName.setText(order.getOrder_location());
                destinationFlag = true;
            }
        });
    }

    private void setDriver(){

        runUiThread(new Runnable() {
            @Override
            public void run() {
                tvOrderDriverName.setText(driver.getDriver_name());
                tvOrderDriverTel.setText(driver.getTel());
            }
        });

    }
    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private FragmentTransaction ft;
    private ItemListFragment itemListFragment;
    private DriverListFragment driverListFragment;
    @Override
    public void initData() {//必须调用
        ft = fragmentManager.beginTransaction();
        itemListFragment = ItemListFragment.createInstance(house_id, true);
        driverListFragment = DriverListFragment.createInstance(0);
        driverListFragment.setDrawerLayout(drawerLayout);
        ft.add(R.id.flFragmentContainer, driverListFragment);
        ft.add(R.id.lvOrderItemFragment, itemListFragment);
        ft.commitNow();
        old_house = null;
        if (house_id == 0){
            house = new SaveHouse();
            house.setAdmin_id(admin_id);
            setHouse(house);
        } else {
            runThread(TAG + "initData", new Runnable() {
                @Override
                public void run() {
                    HttpRequest.getInfo(house_id, "querySimpleHouse.do", GETHOUSE, OrderSelectActivity.this);
                }
            });
        }

//        if (driverListFragment == null){
//            driverListFragment = DriverListFragment.createInstance(0);
//            driverListFragment.setDrawerLayout(drawerLayout);
//            ft = fragmentManager.beginTransaction();
//            ft.add(R.id.flFragmentContainer, driverListFragment);
//            ft.commitNow();
//        }
    }

    //设置返回值
    protected void setResult() {
        setResult(RESULT_OK, intent);
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用
        llOrderDriver.setOnClickListener(this);
        llDesitination.setOnClickListener(this);
        ibtnSendOrder.setOnClickListener(this);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

                driver = driverListFragment.getLongSelectDriver();

                if (driver == null){
                    showShortToast("未选择司机");
                    return;
                }

                order.setDriver_id(driver.getDriver_id());
                showShortToast("司机选择成功");
                setDriver();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        //TODO * 此处的requestCode需要以常量的形式在类开头写明
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
            case GETHOUSE:
                SaveHouse house = null;
                try {//（若是https，不确定）如果服务器返回的json一定在最外层有个data，可以用OnHttpResponseListenerImpl解析
                    JSONObject jsonObject = JSON.parseObject(resultJson);
                    house = JSON.parseObject(jsonObject, SaveHouse.class);
                } catch (Exception e1) {
                    Log.e(TAG, "onHttpResponse  try { user = Json.parseObject(... >>" +
                            " } catch (JSONException e1) {\n" + e1.getMessage());
                }

                if ((house == null || house.getHouse_id() < 0) && e != null) {
                    showShortToast(R.string.get_failed);
                } else {
                    setHouse(house);
                }
                break;
            case UPDATA_ORDER:
                e_state = result.getInteger("e");
                switch (e_state){
                    case 0:
                        showShortToast(R.string.add_failed);
                        break;
                    case 1:
                        showShortToast("添加订单成功");
                        setResult();
                        finish();
                        break;
                }

                break;
        }

    }

    //系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void onDragBottom(boolean rightToLeft) {
        if (rightToLeft) {

            return;
        }
        finish();
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llOrderDriver:
                drawerLayout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.llDesitination:
                toActivity(GpsActivity.createIntent(context), GETADDRESS);
                break;
            case R.id.ibtnSendOrder:
                List<Goods> goodsList = itemListFragment.getGoodsList();
                List<Goods> goodsSelect = new ArrayList<>();
                List<Goods> updataGoodsList = new ArrayList<>();

                goodsSelect.clear();
                //更新原仓库的货物数量信息
                updataGoodsList.clear();
                Goods updataGoods;

                for (Goods goods : goodsList){
                    if (goods.getGoods_select() && goods.getSelect_number() != 0){
                        goodsSelect.add(goods);
                        android.util.Log.d(TAG, "onClick: " + goods.getGoods_id());


                        updataGoods = (Goods) ItemUtil.cloneObjBySerialization(goods);
                        android.util.Log.d(TAG, "onClick: goods_number:" + goods.getGoods_number());
                        android.util.Log.d(TAG, "onClick: select_number:" + goods.getSelect_number());
                        updataGoods.setGoods_number(goods.getGoods_number() - goods.getSelect_number());
                        android.util.Log.d(TAG, "onClick: updataGoods_number:" + updataGoods.getGoods_number());

                        updataGoodsList.add(updataGoods);
                    }
                }

                if (!destinationFlag){
                    showShortToast("请选择订单目的地");
                    return;
                }

                if (goodsSelect.isEmpty()){
                    showShortToast("请选择货物并编辑货物数量");
                    return;
                }

                if (driver == null){
                    showShortToast("司机未选择");
                    return;
                }

                showShortToast("货物数量为0则默认未选");


                //获取系统时间
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String current_time = dateFormat.format(date);

                android.util.Log.d(TAG, "onClick: current_time: " + current_time);

                order.setOrder_start(current_time);

                //发送货物列表和订单数据到数据库
                android.util.Log.d(TAG, "onClick: goodsSelect: " + JSONArray.toJSONString(goodsList));
                android.util.Log.d(TAG, "onClick: order: " + JSONObject.toJSONString(order));
                android.util.Log.d(TAG, "onClick: updataGoodsList" + JSONArray.toJSONString(updataGoodsList));

                HttpRequest.updateOrderList(goodsSelect, updataGoodsList,
                        order, "insertSimpleOrder.do",
                        UPDATA_ORDER, OrderSelectActivity.this);

                break;
        }
    }
    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //此处的data中包含返回值，通过EditTextInfoActivity中的常量可获取
    //TODO * 获取相应的位置信息
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 1:
                itemListFragment.onActivityResult(requestCode, resultCode, data);
                break;
            case 2:
                break;
            case GETADDRESS:
                String order_location = data.getStringExtra("RESULT_STRING");
                Double order_latitude = data.getDoubleExtra("RESULT_LATITUDE", 0);
                Double order_longitude = data.getDoubleExtra("RESULT_LONGITUDE", 0);

                order.setOrder_location(order_location);
                order.setOrder_latitude(order_latitude.toString());
                order.setOrder_longitude(order_longitude.toString());
                setLocation();

                break;
        }
    }



    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //内部类,
    // 尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //内部类,
    // 尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
