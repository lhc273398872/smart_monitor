package com.example.smart_monitor.driver.driver_activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.GpsActivity;
import com.example.smart_monitor.fragment.HouseTabFragment;
import com.example.smart_monitor.fragment.TemListFragment;
import com.example.smart_monitor.model.Car;
import com.example.smart_monitor.model.CarGps;
import com.example.smart_monitor.model.SaveHouse;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.util.ItemUtil;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.ui.EditTextInfoActivity;
import zuo.biao.library.ui.EditTextInfoWindow;
import zuo.biao.library.ui.TextClearSuit;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

public class CarInfoActivity extends BaseActivity
        implements OnClickListener, OnHttpResponseListener, OnBottomDragListener {
    public static final String TAG = "CarInfoActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final String DRIVER_ID = "DRIVER_ID";
    private static final String CAR_ID = "CAR_ID";

    private final int GETCAR = 1;
    private final int ADDCAR = 2;
    private final int UPDATECAR = 3;
    private final int GETADDRESS = 4;

    public static Intent createIntent(Context context, Long driver_id, Long car_id) {
        return new Intent(context, CarInfoActivity.class).
                putExtra(DRIVER_ID, driver_id).
                putExtra(CAR_ID, car_id);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long driver_id = -1;
    private long car_id = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_activity);

        intent = getIntent();
        driver_id = intent.getLongExtra(DRIVER_ID, driver_id);
        if (driver_id < 0){
            finishWithError("用户不存在");
            return;
        }

        car_id = intent.getLongExtra(CAR_ID, car_id);
        if (car_id < 0){
            finishWithError("车辆不存在");
            return;
        }

        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private EditText etCarRemark;

    private TextView tvCarType;
    private TextView tvCarWeight;
    private TextView tvCarTem;

    private ViewGroup llCarTopRightButton;

    private ImageView small_add;
    private TextView small_over;

    @Override
    public void initView() {//必须调用
        etCarRemark = findView(R.id.etCarRemark);

        tvCarType = findView(R.id.tvCarType);
        tvCarWeight = findView(R.id.tvCarWeight);
        tvCarTem = findView(R.id.tvCarTem);

        llCarTopRightButton = findView(R.id.llCarTabTopRightButtonContainer);

        //右上按钮设置
        //添加按钮设置
        small_add = new ImageView(this);
        small_add.setImageResource(R.drawable.add_small);

        //修改按钮设置
        small_over = new TextView(this);
        small_over.setText("确认修改");
        small_over.setVisibility(View.GONE);

        llCarTopRightButton.removeAllViews();
        if (car_id == 0){
            llCarTopRightButton.addView(small_add);
        } else {
            llCarTopRightButton.addView(small_over);
        }

    }

    private Car car;
    private Car old_car;
    private void setCar(final Car car_){
        this.car = car_;
        if (car == null){
            car = new Car();
        }
        if (old_car == null){
            old_car = (Car) ItemUtil.cloneObjBySerialization(car);
        }
        runUiThread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                tvCarType.setText(StringUtil.getTrimedString(car.getCar_type()));
                tvCarWeight.setText(StringUtil.getTrimedString(car.getCar_weight()));
//                tvCarTem.setText(StringUtil.getTrimedString(car.getCar_tem()));
                switch (car.getCar_tem()){
                    case 0:
                        tvCarTem.setText("温度适宜");
                        tvCarTem.setTextColor(R.color.green);
                        break;
                    case 1:
                        tvCarTem.setText("温度过高");
                        tvCarTem.setTextColor(R.color.red);
                        break;
                    case -1:
                        tvCarTem.setText("温度过低");
                        tvCarTem.setTextColor(R.color.blue_sky);
                }
            }
        });
    }

//    private CarGps carGps;
//    private void setCarGps(final CarGps carGps_){
//        this.carGps = carGps_;
//        if (carGps == null){
//            carGps = new CarGps();
//        }
//        runUiThread(new Runnable() {
//            @Override
//            public void run() {
//                tvHouseName.setText(StringUtil.getTrimedString(house.getHouse_name()));
//                tvHouseSize.setText(StringUtil.getTrimedString(house.getHouse_size()));
//                tvHouseLocation.setText(StringUtil.getTrimedString(house.getHouse_location()));
//            }
//        });
//    }
    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private FragmentTransaction ft;
    private TemListFragment temListFragment;
    @Override
    public void initData() {//必须调用

        ft = fragmentManager.beginTransaction();
        temListFragment = TemListFragment.createInstance(driver_id);
        ft.add(R.id.lvTemFragment, temListFragment);
        ft.commitNow();

        old_car = null;
        if (car_id == 0){
            car = new Car();
            car.setDriver_id(driver_id);
            setCar(car);
        } else {
            runThread(TAG + "initData", new Runnable() {
                @Override
                public void run() {
                    HttpRequest.getInfo(driver_id, "queryDriverCar.do", GETCAR, CarInfoActivity.this);
                }
            });
        }
    }

    protected void setResult(){
        intent = new Intent()
                .putExtra(RESULT_DATA, "" + car.getCar_id());
        setResult(RESULT_OK, intent);
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用
        tvCarType.setOnClickListener(this);
        tvCarWeight.setOnClickListener(this);
        tvCarTem.setOnClickListener(this);
        small_add.setOnClickListener(new AddOnclick());
        small_over.setOnClickListener(new OverOnclick());

        new TextClearSuit().addClearListener(etCarRemark, findView(R.id.ivCarRemarkClear));

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
            case ADDCAR:
                e_state = result.getInteger("e");
                switch (e_state){
                    case 0:
                        showShortToast(R.string.add_failed);
                        break;
                    default:
                        showShortToast("添加车辆信息成功");
                        setResult();
                        finish();
                        break;
                }
                break;
            case UPDATECAR:
                e_state = result.getInteger("e");

                switch (e_state){
                    case 0:
                        showShortToast(R.string.add_failed);
                        break;
                    default:
                        showShortToast("修改车辆信息成功");
                        setResult();
                        finish();
                        break;
                }
                break;
            default:
                Car car = null;
                try {//（若是https，不确定）如果服务器返回的json一定在最外层有个data，可以用OnHttpResponseListenerImpl解析
                    JSONObject jsonObject = JSON.parseObject(resultJson);
//            JSONObject data = jsonObject == null ? null : jsonObject.getJSONObject("data");
                    car = JSON.parseObject(jsonObject, Car.class);
                } catch (Exception e1) {
                    Log.e(TAG, "onHttpResponse  try { user = Json.parseObject(... >>" +
                            " } catch (JSONException e1) {\n" + e1.getMessage());
                }

                if ((car == null || car.getCar_id() < 0)) {
                    showShortToast("网络出现问题或并未存在车辆");
                } else {
                    setCar(car);
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
        car = old_car;
        finish();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCarType:
                //填写姓名
                //参数1：上下文，参数2：填写类型，参数3：key，参数4：value，参数5：包名
                toActivity(EditTextInfoWindow.createIntent(context, EditTextInfoWindow.TYPE_NAME,
                        "车辆型号", StringUtil.getTrimedString(car.getCar_type()),
                        getPackageName()), ALTER_CAR_TYPE, false);
                break;
            case R.id.tvCarWeight:
                toActivity(EditTextInfoWindow.createIntent(context, EditTextInfoWindow
                .TYPE_NUMBER,
                        "车辆载重", StringUtil.getTrimedString(car.getCar_weight()),
                        getPackageName()), ALTER_CAR_WEIGHT, false);
                break;
            default:
                //TODO * 绑定温度传感器
//                toActivity(GpsActivity.createIntent(context), GETADDRESS);
                break;
        }
    }

    private class AddOnclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO * 完成添加车辆
            HttpRequest.updateInfo(car, "insertSimpleCars.do", ADDCAR, CarInfoActivity.this);
        }
    }

    private class OverOnclick implements OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO * 修改车辆信息
            HttpRequest.updateInfo(car, "updateSimpleCars.do", UPDATECAR, CarInfoActivity.this);
        }
    }

    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final int ALTER_CAR_TYPE = 1;
    private static final int ALTER_CAR_WEIGHT = 2;
    private static final int ALTER_CAR_TEM = 3;
    //此处的data中包含返回值，通过EditTextInfoActivity中的常量可获取
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ALTER_CAR_TYPE:
                //获取型号返回值
                car.setCar_type(data == null ? null : data.getStringExtra
                        (EditTextInfoActivity.RESULT_VALUE));
                setCar(car);
                break;
            case ALTER_CAR_WEIGHT:
                //获取载重返回值
                car.setCar_weight(data == null ? null : data.getStringExtra
                        (EditTextInfoActivity.RESULT_VALUE));
                setCar(car);
                break;
            case ALTER_CAR_TEM:
                //TODO * 绑定温度传感器后，获取温度信息
                //可能要写一个额外获取温度的服务
                break;
        }
        small_over.setVisibility(View.VISIBLE);
    }

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //内部类,
    // 尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //内部类,
    // 尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
