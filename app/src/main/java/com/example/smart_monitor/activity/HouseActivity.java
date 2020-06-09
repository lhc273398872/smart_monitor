package com.example.smart_monitor.activity;


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
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.fragment.HouseTabFragment;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.model.SaveHouse;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.util.ItemUtil;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.ui.EditTextInfoActivity;
import zuo.biao.library.ui.EditTextInfoWindow;
import zuo.biao.library.ui.TextClearSuit;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;


//TODO * 在布局文件中最下方添加gps地图，及光标定位功能
public class HouseActivity extends BaseActivity
        implements OnClickListener, OnHttpResponseListener, OnBottomDragListener {
    public static final String TAG = "HouseActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final String ADMIN_ID = "admin_id";

    private final int GETHOUSE = 1;
    private final int ADDHOUSE = 2;
    private final int UPDATEHOUSE = 3;
    private final int GETADDRESS = 4;
    private final int DELHOUSE = 5;

    public static Intent createIntent(Context context, Long admin_id, Long house_id) {
        return new Intent(context, HouseActivity.class).
                putExtra(ADMIN_ID, admin_id).
                putExtra(INTENT_ID, house_id);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long admin_id = -1;
    private long house_id = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_activity);

        intent = getIntent();
        admin_id = intent.getLongExtra(ADMIN_ID, admin_id);
        if (admin_id < 0){
            finishWithError("用户不存在");
            return;
        }

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

    private EditText etHouseRemark;

    private TextView currentHouseName;
    private TextView tvHouseName;
    private TextView tvHouseSize;
    private TextView tvHouseLocation;

    private ViewGroup llHouseTopRightButton;

    private ImageView small_add;
    private TextView small_over;
    private ImageView small_delete;

    @Override
    public void initView() {//必须调用
        etHouseRemark = findView(R.id.etHouseRemark);

        currentHouseName = findView(R.id.currentHouseName);
        tvHouseName = findView(R.id.tvHouseName);
        tvHouseSize = findView(R.id.tvHouseSize);
        tvHouseLocation = findView(R.id.tvHouseLocation);

        llHouseTopRightButton = findView(R.id.llHouseTabTopRightButtonContainer);

        //右上按钮设置
        //添加按钮设置
        small_add = new ImageView(this);
        small_add.setImageResource(R.drawable.add_small);

        //修改按钮设置
        small_over = new TextView(this);
        small_over.setText("确认修改");
        small_over.setVisibility(View.GONE);

        small_delete = new ImageView(this);
        small_delete.setImageResource(R.drawable.ic_delete_whiet_24dp);

        llHouseTopRightButton.removeAllViews();
        if (house_id == 0){
            llHouseTopRightButton.addView(small_add);
        } else {
            llHouseTopRightButton.addView(small_delete);
            llHouseTopRightButton.addView(small_over);
        }

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
                tvHouseName.setText(StringUtil.getTrimedString(house.getHouse_name()));
                tvHouseSize.setText(StringUtil.getTrimedString(house.getHouse_size()));
                tvHouseLocation.setText(StringUtil.getTrimedString(house.getHouse_location()));
            }
        });
    }
    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initData() {//必须调用

        old_house = null;
        if (house_id == 0){
            house = new SaveHouse();
            house.setAdmin_id(admin_id);
            setHouse(house);
        } else {
            runThread(TAG + "initData", new Runnable() {
                @Override
                public void run() {
                    HttpRequest.getInfo(house_id, "querySimpleHouse.do", GETHOUSE, HouseActivity.this);
                }
            });
        }
    }

    protected void setResult(){
        intent = new Intent()
                .putExtra(RESULT_DATA, "" + house.getHouse_id());
        setResult(RESULT_OK, intent);
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用
        tvHouseName.setOnClickListener(this);
        tvHouseSize.setOnClickListener(this);
        tvHouseLocation.setOnClickListener(this);

        small_add.setOnClickListener(new AddOnclick());
        small_over.setOnClickListener(new OverOnclick());
        small_delete.setOnClickListener(new DelOnclick());

        new TextClearSuit().addClearListener(etHouseRemark, findView(R.id.ivHouseRemarkClear));

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
            case ADDHOUSE:
                e_state = result.getInteger("e");
                switch (e_state){
                    case 0:
                        showShortToast(R.string.add_failed);
                        break;
                    default:
                        showShortToast("添加仓库成功");
                        setResult();
                        finish();
                        break;
                }
                break;
            case DELHOUSE:
                e_state = result.getInteger("e");
                switch (e_state){
                    case 0:
                        showShortToast("检查网络，也可能存在订单未完结");
                        break;
                    default:
                        showShortToast("删除仓库成功");
                        setResult();
                        finish();
                        break;
                }
                break;
            case UPDATEHOUSE:
                e_state = result.getInteger("e");

                switch (e_state){
                    case 0:
                        showShortToast(R.string.add_failed);
                        break;
                    default:
                        showShortToast("修改仓库成功");
                        setResult();
                        finish();
                        break;
                }
                break;
            default:
                SaveHouse house = null;
                try {//（若是https，不确定）如果服务器返回的json一定在最外层有个data，可以用OnHttpResponseListenerImpl解析
                    JSONObject jsonObject = JSON.parseObject(resultJson);
//            JSONObject data = jsonObject == null ? null : jsonObject.getJSONObject("data");
                    house = JSON.parseObject(jsonObject, SaveHouse.class);
                } catch (Exception e1) {
                    Log.e(TAG, "onHttpResponse  try { user = Json.parseObject(... >>" +
                            " } catch (JSONException e1) {\n" + e1.getMessage());
                }

                if ((house == null || house.getHouse_id() < 0) && e != null) {
                    showShortToast(R.string.get_failed);
                } else {
                    showShortToast(house.toString());
                    setHouse(house);
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
        house = old_house;
        setResult();
        finish();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvHouseName:
                //填写姓名
                //参数1：上下文，参数2：填写类型，参数3：key，参数4：value，参数5：包名
                toActivity(EditTextInfoWindow.createIntent(context, EditTextInfoWindow.TYPE_NAME,
                        "仓库名称", StringUtil.getTrimedString(house.getHouse_name()),
                        getPackageName()), ALTER_HOUSE_NAME, false);
                break;
            case R.id.tvHouseSize:
                toActivity(EditTextInfoWindow.createIntent(context, EditTextInfoWindow
                .TYPE_NUMBER,
                        "仓库大小", StringUtil.getTrimedString(house.getHouse_size()),
                        getPackageName()), ALTER_HOUSE_SIZE, false);
                break;
            default:
                toActivity(GpsActivity.createIntent(context), GETADDRESS);
                break;
        }
    }

    private class AddOnclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            //完成添加仓库
            Log.d(TAG, "house:" + house);
            HttpRequest.updateInfo(house, "insertSimpleHouse.do", ADDHOUSE,HouseActivity.this);
        }
    }

    private static final String[] TOPBAR_TAG_NAMES = {"确定删除", "取消删除"};
    private class DelOnclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            //完成删除仓库
            Log.d(TAG, "house:" + house);
            toActivity(BottomMenuWindow.createIntent(context, TOPBAR_TAG_NAMES)
                    .putExtra(BottomMenuWindow.INTENT_TITLE, "确认是否删除（若订单中包含该仓库，则仓库无法被删除）"), DELHOUSE, false);
        }
    }

    private class OverOnclick implements OnClickListener {

        @Override
        public void onClick(View v) {
            //修改仓库信息
            HttpRequest.updateInfo(house, "updateSimpleHouse.do", UPDATEHOUSE, HouseActivity.this);
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
                house.setHouse_name(data == null ? null : data.getStringExtra
                        (EditTextInfoActivity.RESULT_VALUE));
                setHouse(house);
                break;
            case ALTER_HOUSE_SIZE:
                house.setHouse_size(data == null ? null : data.getStringExtra
                        (EditTextInfoActivity.RESULT_VALUE));
                setHouse(house);
                break;
            case GETADDRESS:
                String house_location = data.getStringExtra("RESULT_STRING");
                Double house_latitude = data.getDoubleExtra("RESULT_LATITUDE", -1);
                Double house_longitude = data.getDoubleExtra("RESULT_LONGITUDE", -1);

                house.setHouse_location(house_location);
                house.setHouse_latitude("" + house_latitude);
                house.setHouse_longitude("" + house_longitude);
                setHouse(house);
                break;
            case DELHOUSE:
                switch (data.getIntExtra(BottomMenuWindow.RESULT_ITEM_ID, 1)) {
                    case 1:
                        //                        showShortToast("货物" + delete_goodsId + "取消删除");
                        break;
                    default:
                        //                        showShortToast("货物" + delete_goodsId + "删除货物");
                        HttpRequest.getInfo(house.getHouse_id(), "deleteSimpleHouse.do", DELHOUSE, HouseActivity.this);
                        break;
                }
        }
        small_over.setVisibility(View.VISIBLE);
    }


    @Override
    public void finish() {
        super.finish();
        if (house != null) {
            //TODO * 将仓库的备注传回
            //此处缓存已经更新，需更新ui
            Intent intent = new Intent(HouseActivity.this , HouseTabFragment.class);
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
