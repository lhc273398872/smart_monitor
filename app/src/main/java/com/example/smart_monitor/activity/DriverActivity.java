package com.example.smart_monitor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSONObject;

import com.example.smart_monitor.R;
import com.example.smart_monitor.driver.driver_activity.DriverInfoActivity;
import com.example.smart_monitor.fragment.DriverListFragment;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.fragment.TemListFragment;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.model.User;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.util.MenuUtil;
import com.example.smart_monitor.view.DriverView;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.base.BaseModel;
import zuo.biao.library.base.BaseView.OnDataChangedListener;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.CacheManager;
import zuo.biao.library.ui.BottomMenuView;
import zuo.biao.library.ui.BottomMenuView.OnBottomMenuItemClickListener;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.ui.EditTextInfoActivity;
import zuo.biao.library.ui.TextClearSuit;
import zuo.biao.library.util.CommonUtil;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

/**联系人资料界面
 * @author Lemon
 */
public class DriverActivity extends BaseActivity implements OnClickListener, OnBottomDragListener
        , OnBottomMenuItemClickListener, OnHttpResponseListener {
    public static final String TAG = "DriverActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**获取启动UserActivity的intent
     * @param context
     * @param driverId
     * @return
     */
    public static Intent createIntent(Context context, long driverId) {
        return new Intent(context, DriverActivity.class).putExtra(INTENT_ID, driverId);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private static final int GETDRIVER = 1;

    private long driver_id = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_activity, this);

        intent = getIntent();
        driver_id = intent.getLongExtra(INTENT_ID, driver_id);
        if (driver_id < 0) {
            finishWithError("用户不存在！");
            return;
        }

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //	private BaseViewLayout<User> bvlUser;//方式一
    //	private UserViewLayout uvlUser;//方式二

    private ViewGroup llDriverBusinessCardContainer;//方式三
    private DriverView driverView;

    private EditText etDriverRemark;
    private TextView tvDriverTag;
    private TextView tvDriverRightButton;
    private TextView tvOverOrder;
    private TextView tvWrongOps;

    private ViewGroup llDriverBottomMenuContainer;
    private BottomMenuView bottomMenuView;
    @Override
    public void initView() {//必须调用

        llDriverBusinessCardContainer = findView(R.id.llDriverBusinessCardContainer);
        llDriverBusinessCardContainer.removeAllViews();

        driverView = new DriverView(context, null);
        llDriverBusinessCardContainer.addView(driverView.createView());

        etDriverRemark = findView(R.id.etDriverRemark);
        tvDriverTag = findView(R.id.tvDriverTag);
        tvDriverRightButton = findView(R.id.tvDriverTabRight);
        tvOverOrder = findView(R.id.tvOverOrder);
        tvWrongOps = findView(R.id.tvWrongOps);

        //添加底部菜单<<<<<<<<<<<<<<<<<<<<<<
        llDriverBottomMenuContainer = findView(R.id.llDriverBottomMenuContainer);
        llDriverBottomMenuContainer.removeAllViews();

        bottomMenuView = new BottomMenuView(context, REQUEST_TO_BOTTOM_MENU);
        llDriverBottomMenuContainer.addView(bottomMenuView.createView());
        //添加底部菜单>>>>>>>>>>>>>>>>>>>>>>>
    }

    private Driver driver;
    /**显示用户
     * @param driver_
     */
    private void setDriver(final Driver driver_) {
        this.driver = driver_;
        if (driver == null) {
            Log.w(TAG, "setUser  user == null >> user = new User();");
            driver = new Driver();
        }

        runUiThread(new Runnable() {

            @Override
            public void run() {
                //				bvlUser.bindView(user);//方式一
                //				uvlUser.bindView(user);//方式二
                driverView.bindView(driver);//方式三

//                etDriverRemark.setText(StringUtil.getTrimedString(driver.getHead()));

                tvDriverTag.setText(StringUtil.getTrimedString(driver.getTel()));
                tvOverOrder.setText(StringUtil.getTrimedString(driver.getOver_order()));
                tvWrongOps.setText(StringUtil.getTrimedString(driver.getWrong_ops()));
            }
        });
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private FragmentTransaction ft;
    private TemListFragment temListFragment;
    @Override
    public void initData() {//必须调用

        ft = fragmentManager.beginTransaction();
        temListFragment = TemListFragment.createInstance(driver_id);
        ft.add(R.id.lvTemFragment, temListFragment);
        ft.commitNow();

        bottomMenuView.bindView(MenuUtil.getMenuList(MenuUtil.USER));

        if (driver_id == 0){
            driver = new Driver();
            setDriver(driver);
        } else {
            runThread(TAG + "initData", new Runnable() {
                @Override
                public void run() {
                    HttpRequest.getInfo(driver_id, "querySimpleDriver.do", GETDRIVER, DriverActivity.this);
                    //更方便但对字符串格式有要求 HttpRequest.getUser(userId, 0, new OnHttpResponseListenerImpl(UserActivity.this));
                }
            });
        }
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用

        findView(R.id.llDriverTag).setOnClickListener(this);
        tvDriverRightButton.setOnClickListener(this);

        new TextClearSuit().addClearListener(etDriverRemark, findView(R.id.ivUserRemarkClear));//清空备注按钮点击监听

        bottomMenuView.setOnMenuItemClickListener(this);//底部菜单点击监听

        driverView.setOnDataChangedListener(new OnDataChangedListener() {

            @Override
            public void onDataChanged() {
                driver = driverView.data;
            }
        });
    }

    @Override
    public void onBottomMenuItemClick(int intentCode) {
        if (driver == null) {
            Log.e(TAG, "onBottomMenuItemClick  user == null >> return;");
            return;
        }
        switch (intentCode) {
            case MenuUtil.INTENT_CODE_SEND:
                CommonUtil.shareInfo(context, JSON.toJSONString(driver));
                break;
            default:
                String phone = StringUtil.getCorrectPhone(driver.getTel());
                if (StringUtil.isNotEmpty(phone, true) == false) {
                    return;
                }
                switch (intentCode) {
                    case MenuUtil.INTENT_CODE_SEND_MESSAGE:
                        CommonUtil.toMessageChat(context, driver.getTel());
                        break;
                    case MenuUtil.INTENT_CODE_CALL:
                        CommonUtil.call(context, phone);
                        break;
                    case MenuUtil.INTENT_CODE_SEND_EMAIL:
                        CommonUtil.sendEmail(context, phone + "@qq.com");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    //对应HttpRequest.getUser(userId, 0, UserActivity.this); <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        Driver driver = null;
        try {//如果服务器返回的json一定在最外层有个data，可以用OnHttpResponseListenerImpl解析
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
    }
    //对应HttpRequest.getUser(userId, 0, UserActivity.this); >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //	//对应HttpRequest.getUser(userId, 0, new OnHttpResponseListenerImpl(UserActivity.this)); <<<<<
    //	@Override
    //	public void onHttpSuccess(int requestCode, int resultCode, String resultData) {
    //		setUser(JSON.parseObject(resultData, User.class));
    //	}
    //
    //	@Override
    //	public void onHttpError(int requestCode, Exception e) {
    //		showShortToast(R.string.get_failed);
    //	}
    //	//对应HttpRequest.getUser(userId, 0, new OnHttpResponseListenerImpl(UserActivity.this)); >>>>




    //系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void onDragBottom(boolean rightToLeft) {
        if (rightToLeft) {

            return;
        }

        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDriverTag:
                toActivity(EditTextInfoActivity.createIntent(context, "标签"
                        , StringUtil.getTrimedString(tvDriverTag)), REQUEST_TO_EDIT_TEXT_INFO);
                break;
            case R.id.tvDriverTabRight:
                //TODO 添加用户逻辑
                showShortToast("添加用户成功");
                break;
            default:
                break;
        }
    }


    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final int REQUEST_TO_BOTTOM_MENU = 1;
    private static final int REQUEST_TO_EDIT_TEXT_INFO = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_TO_BOTTOM_MENU:
                if (data != null) {
                    onBottomMenuItemClick(data.getIntExtra(BottomMenuWindow.RESULT_ITEM_ID, -1));
                }
                break;
            case REQUEST_TO_EDIT_TEXT_INFO:
                if (driver == null) {
                    driver = new Driver(driver_id);
                }
//                driver.setTag(data == null ? null : data.getStringExtra(EditTextInfoActivity.RESULT_VALUE));
                setDriver(driver);
                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        if (driver != null) {
//            driver.setHead(StringUtil.getTrimedString(etDriverRemark));
            CacheManager.getInstance().save(Driver.class, driver, "" + driver.getDriver_id());//更新缓存
        }
    }

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
