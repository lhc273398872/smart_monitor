package com.example.smart_monitor.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.driver.driver_activity.DriverTabActivity;
import com.example.smart_monitor.fragment.HouseTabFragment;
import com.example.smart_monitor.model.AdminUser;
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

public class AdminInfoActivity extends BaseActivity
        implements OnClickListener, OnHttpResponseListener, OnBottomDragListener {
    public static final String TAG = "HouseActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final String ADMIN_ID = "admin_id";
    private static final String TEL = "tel";

    private final int GETADMIN = 1;
    private final int ADDADMIN = 2;
    private final int UPDATEADMIN = 3;

    public static Intent createIntent(Context context, Long admin_id) {
        return new Intent(context, AdminInfoActivity.class).
                putExtra(ADMIN_ID, admin_id);
    }

    public static Intent createIntent(Context context, Long admin_id, String tel) {
        return new Intent(context, AdminInfoActivity.class).
                putExtra(ADMIN_ID, admin_id).
                putExtra(TEL, tel);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long admin_id = -1;
    private String tel = "";
    private boolean new_flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_info_activity);

        intent = getIntent();
        admin_id = intent.getLongExtra(ADMIN_ID, admin_id);
        tel = intent.getStringExtra(TEL);
        if (admin_id < 0){
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
    private TextView tvAdminName;
    private TextView tvAdminTel;
    private ImageButton ibtnAdminInfoCancel;

    private ViewGroup llAdminTopRightButton;

    private ImageView small_add;
    private TextView small_over;

    @Override
    public void initView() {//必须调用
        tvAdminName = findView(R.id.tvAdminName);
        tvAdminTel = findView(R.id.tvAdminTel);
        ibtnAdminInfoCancel = findView(R.id.ibtnAdminInfoCancel);

        llAdminTopRightButton = findView(R.id.llAdminTabTopRightButtonContainer);

        //右上按钮设置
        //添加按钮设置
        small_add = new ImageView(this);
        small_add.setImageResource(R.drawable.add_small);

        //修改按钮设置
        small_over = new TextView(this);
        small_over.setText("确认修改");
        small_over.setVisibility(View.GONE);

        llAdminTopRightButton.removeAllViews();
        if (admin_id == 0){
            new_flag = true;
            ibtnAdminInfoCancel.setVisibility(View.GONE);
            llAdminTopRightButton.addView(small_add);
        } else {
            llAdminTopRightButton.addView(small_over);
        }

    }

    private AdminUser admin;
    private AdminUser old_admin;
    private void setAdmin(final AdminUser admin_){
        this.admin = admin_;
        if (admin == null){
            admin = new AdminUser();
        }
        if (old_admin == null){
            old_admin = (AdminUser) ItemUtil.cloneObjBySerialization(admin);
        }
        runUiThread(new Runnable() {
            @Override
            public void run() {
                tvAdminName.setText(StringUtil.getTrimedString(admin.getAdmin_name()));
                tvAdminTel.setText(StringUtil.getTrimedString(admin.getTel()));
            }
        });
    }
    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initData() {//必须调用

        old_admin = null;
        if (admin_id == 0){
            admin = new AdminUser();
            admin.setAdmin_id(admin_id);
            admin.setTel(tel);
            setAdmin(admin);
        } else {
            runThread(TAG + "initData", new Runnable() {
                @Override
                public void run() {
                    //TODO 改为查询管理员信息
                    HttpRequest.getInfo(admin_id, "querySimpleAdmin.do", GETADMIN, AdminInfoActivity.this);
                }
            });
        }
    }

    protected void setResult(){
        intent = new Intent()
                .putExtra(RESULT_DATA, "" + admin.getAdmin_id());
        setResult(RESULT_OK, intent);
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用
        tvAdminName.setOnClickListener(this);

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
            case ADDADMIN:
                e_state = result.getInteger("e");
                switch (e_state){
                    case 0:
                        showShortToast(R.string.add_failed);
                        break;
                    default:
                        long admin_id = result.getLong("admin_id");
                        toActivity(AdminTabActivity.createIntent(this, admin_id));
                        finish();
                        break;
                }
                break;
            case UPDATEADMIN:
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
                AdminUser admin = null;
                try {//（若是https，不确定）如果服务器返回的json一定在最外层有个data，可以用OnHttpResponseListenerImpl解析
                    JSONObject jsonObject = JSON.parseObject(resultJson);
//            JSONObject data = jsonObject == null ? null : jsonObject.getJSONObject("data");
                    admin = JSON.parseObject(jsonObject, AdminUser.class);
                } catch (Exception e1) {
                    Log.e(TAG, "onHttpResponse  try { user = Json.parseObject(... >>" +
                            " } catch (JSONException e1) {\n" + e1.getMessage());
                }

                if ((admin == null || admin.getAdmin_id() < 0) && e != null) {
                    showShortToast(R.string.get_failed);
                } else {
                    showShortToast(admin.toString());
                    setAdmin(admin);
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
        admin = old_admin;
        setResult();
        finish();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAdminName:
                //填写姓名
                //参数1：上下文，参数2：填写类型，参数3：key，参数4：value，参数5：包名
                toActivity(EditTextInfoWindow.createIntent(context, EditTextInfoWindow.NAME,
                        "管理员名称", StringUtil.getTrimedString(admin.getAdmin_name()),
                        getPackageName()), ALTER_HOUSE_NAME, false);
                break;
        }
    }

    private class AddOnclick implements OnClickListener {
        @Override
        public void onClick(View v) {
            //完成添加仓库
            Log.d(TAG, "admin:" + admin);
            //TODO
            HttpRequest.updateInfo(admin, "insertSimpleAdmin.do", ADDADMIN, AdminInfoActivity.this);
        }
    }

    private class OverOnclick implements OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO
            HttpRequest.updateInfo(admin, "updateSimpleAdmin.do", UPDATEADMIN, AdminInfoActivity.this);
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
                admin.setAdmin_name(data == null ? null : data.getStringExtra
                        (EditTextInfoActivity.RESULT_VALUE));
                setAdmin(admin);
                break;
        }
        small_over.setVisibility(View.VISIBLE);
    }


    @Override
    public void finish() {
        super.finish();
        //TODO 若是新增信息，则跳转至主页面
        if (admin != null) {
            //TODO * 将仓库的备注传回
            //此处缓存已经更新，需更新ui
            Intent intent = new Intent(AdminInfoActivity.this , HouseTabFragment.class);
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
