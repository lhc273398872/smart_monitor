package com.example.smart_monitor.driver.driver_activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.AdminTabActivity;
import com.example.smart_monitor.fragment.HouseTabFragment;
import com.example.smart_monitor.model.AdminUser;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.util.ItemUtil;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.ui.EditTextInfoActivity;
import zuo.biao.library.ui.EditTextInfoWindow;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

public class DriverInfoActivity extends BaseActivity
        implements OnClickListener, OnHttpResponseListener, OnBottomDragListener {
    public static final String TAG = "HouseActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final String DRIVER_ID = "driver_id";
    private static final String TEL = "tel";

    private final int GETDRIVER = 1;
    private final int ADDDRIVER = 2;
    private final int UPDATEDRIVER = 3;

    public static Intent createIntent(Context context, Long driver_id) {
        return new Intent(context, DriverInfoActivity.class).
                putExtra(DRIVER_ID, driver_id);
    }

    public static Intent createIntent(Context context, Long driver_id, String tel) {
        return new Intent(context, DriverInfoActivity.class).
                putExtra(DRIVER_ID, driver_id).
                putExtra(TEL, tel);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long driver_id = -1;
    private String tel = "";
    private boolean new_flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_info_activity);

        intent = getIntent();
        driver_id = intent.getLongExtra(DRIVER_ID, driver_id);
        tel = intent.getStringExtra(TEL);
        if (driver_id < 0){
            finishWithError("用户不存在");
            return;
        }

        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private TextView tvDriverName;
    private TextView tvDriverTel;
    private ImageButton ibtnDriverInfoCancel;

    private ViewGroup llDriverTopRightButton;

    private ImageView small_add;
    private TextView small_over;

    @Override
    public void initView() {//必须调用
        tvDriverName = findView(R.id.tvDriverName);
        tvDriverTel = findView(R.id.tvDriverTel);
        ibtnDriverInfoCancel = findView(R.id.ibtnDriverInfoCancel);

        llDriverTopRightButton = findView(R.id.llDriverTabTopRightButtonContainer);

        //右上按钮设置
        //添加按钮设置
        small_add = new ImageView(this);
        small_add.setImageResource(R.drawable.add_small);

        //修改按钮设置
        small_over = new TextView(this);
        small_over.setText("确认修改");
        small_over.setVisibility(View.GONE);

        llDriverTopRightButton.removeAllViews();
        if (driver_id == 0){
            new_flag = true;
            ibtnDriverInfoCancel.setVisibility(View.GONE);
            llDriverTopRightButton.addView(small_add);
        } else {
            llDriverTopRightButton.addView(small_over);
        }

    }

    private Driver driver;
    private Driver old_driver;
    private void setDriver(final Driver driver_){
        this.driver = driver_;
        if (driver == null){
            driver = new Driver();
        }
        if (old_driver == null){
            old_driver = (Driver) ItemUtil.cloneObjBySerialization(driver);
        }
        runUiThread(new Runnable() {
            @Override
            public void run() {
                tvDriverName.setText(StringUtil.getTrimedString(driver.getDriver_name()));
                tvDriverTel.setText(StringUtil.getTrimedString(driver.getTel()));
            }
        });
    }
    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initData() {//必须调用

        old_driver = null;
        if (driver_id == 0){
            driver = new Driver();
            driver.setDriver_id(driver_id);
            driver.setTel(tel);
            setDriver(driver);
        } else {
            runThread(TAG + "initData", new Runnable() {
                @Override
                public void run() {
                    HttpRequest.getInfo(driver_id, "querySimpleDriver.do", GETDRIVER, DriverInfoActivity.this);
                }
            });
        }
    }

    protected void setResult(){
        intent = new Intent()
                .putExtra(RESULT_DATA, "" + driver.getDriver_id());
        setResult(RESULT_OK, intent);
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用
        tvDriverName.setOnClickListener(this);

        small_add.setOnClickListener(new AddOnclick());
        small_over.setOnClickListener(new OverOnclick());
    }

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
            case ADDDRIVER:
                e_state = result.getInteger("e");
                switch (e_state){
                    case 0:
                        showShortToast(R.string.add_failed);
                        break;
                    default:
                        long driver_id = result.getLong("driver_id");
                        toActivity(DriverTabActivity.createIntent(this, driver_id));
                        finish();
                        break;
                }
                break;
            case UPDATEDRIVER:
                e_state = result.getInteger("e");

                switch (e_state){
                    case 0:
                        showShortToast(R.string.add_failed);
                        break;
                    default:
                        showShortToast("修改用户信息成功");
                        setResult();
                        finish();
                        break;
                }
                break;
            default:
                Driver driver = null;
                try {//（若是https，不确定）如果服务器返回的json一定在最外层有个data，可以用OnHttpResponseListenerImpl解析
                    JSONObject jsonObject = JSON.parseObject(resultJson);
//            JSONObject data = jsonObject == null ? null : jsonObject.getJSONObject("data");
                    driver = JSON.parseObject(jsonObject, Driver.class);
                } catch (Exception e1) {
                    Log.e(TAG, "onHttpResponse  try { user = Json.parseObject(... >>" +
                            " } catch (JSONException e1) {\n" + e1.getMessage());
                }

                if ((driver == null || driver.getDriver_id() < 0) && e != null) {
                    showShortToast(R.string.get_failed);
                } else {
                    setDriver(driver);
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
        driver = old_driver;
        setResult();
        finish();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDriverName:
                //填写姓名
                //参数1：上下文，参数2：填写类型，参数3：key，参数4：value，参数5：包名
                toActivity(EditTextInfoWindow.createIntent(context, EditTextInfoWindow.NAME,
                        "管理员名称", StringUtil.getTrimedString(driver.getDriver_name()),
                        getPackageName()), ALTER_HOUSE_NAME, false);
                break;
        }
    }

    private class AddOnclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            //完成添加司机
            Log.d(TAG, "driver:" + driver);
            //TODO
            HttpRequest.updateInfo(driver, "insertSimpleDriver.do", ADDDRIVER, DriverInfoActivity.this);
        }
    }

    private class OverOnclick implements OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO
            HttpRequest.updateInfo(driver, "updateSimpleDriver.do", UPDATEDRIVER, DriverInfoActivity.this);
        }
    }

    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final int ALTER_HOUSE_NAME = 1;
    private static final int ALTER_HOUSE_SIZE = 2;
    //此处的data中包含返回值，通过EditTextInfoActivity中的常量可获取
    //TODO * 获取相应的位置信息
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ALTER_HOUSE_NAME:
                //获取名称返回值
                driver.setDriver_name(data == null ? null : data.getStringExtra
                        (EditTextInfoActivity.RESULT_VALUE));
                setDriver(driver);
                break;
        }
        small_over.setVisibility(View.VISIBLE);
    }


    @Override
    public void finish() {
        super.finish();
        //TODO 若是新增信息，则跳转至主页面
        if (driver != null) {
            //TODO * 将仓库的备注传回
            //此处缓存已经更新，需更新ui
            Intent intent = new Intent(DriverInfoActivity.this , HouseTabFragment.class);
            setResult(-1, intent);
            //TODO * 将仓库信息传到Cache数据库中
        }
    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //内部类,
    // 尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //内部类,
    // 尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
