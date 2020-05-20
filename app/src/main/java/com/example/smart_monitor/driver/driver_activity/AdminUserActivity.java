package com.example.smart_monitor.driver.driver_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.R;
import com.example.smart_monitor.driver.driver_view.AdminUserView;
import com.example.smart_monitor.model.AdminUser;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.util.MenuUtil;
import com.example.smart_monitor.view.DriverView;

import zuo.biao.library.base.BaseActivity;
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
public class AdminUserActivity extends BaseActivity implements OnClickListener, OnBottomDragListener
        , OnBottomMenuItemClickListener, OnHttpResponseListener {
    public static final String TAG = "AdminUserActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**获取启动UserActivity的intent
     * @param context
     * @param adminId
     * @return
     */
    public static Intent createIntent(Context context, long adminId) {
        return new Intent(context, AdminUserActivity.class).putExtra(INTENT_ID, adminId);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    private long adminId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_activity, this);

        intent = getIntent();
        adminId = intent.getLongExtra(INTENT_ID, adminId);
        if (adminId < 0) {
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
    private AdminUserView adminUserView;

    private EditText etDriverRemark;
    private TextView tvDriverTag;
    private TextView tvDriverRightButton;

    private ViewGroup llDriverBottomMenuContainer;
    private BottomMenuView bottomMenuView;
    @Override
    public void initView() {//必须调用

        llDriverBusinessCardContainer = findView(R.id.llDriverBusinessCardContainer);
        llDriverBusinessCardContainer.removeAllViews();

        adminUserView = new AdminUserView(context, null);
        llDriverBusinessCardContainer.addView(adminUserView.createView());

        etDriverRemark = findView(R.id.etDriverRemark);
        tvDriverTag = findView(R.id.tvDriverTag);
        tvDriverRightButton = findView(R.id.tvDriverTabRight);

        //添加底部菜单<<<<<<<<<<<<<<<<<<<<<<
        llDriverBottomMenuContainer = findView(R.id.llDriverBottomMenuContainer);
        llDriverBottomMenuContainer.removeAllViews();

        bottomMenuView = new BottomMenuView(context, REQUEST_TO_BOTTOM_MENU);
        llDriverBottomMenuContainer.addView(bottomMenuView.createView());
        //添加底部菜单>>>>>>>>>>>>>>>>>>>>>>>

    }

    private AdminUser adminUser;
    /**显示用户
     * @param adminUser_
     */
    private void setAdminUser(AdminUser adminUser_) {
        this.adminUser = adminUser_;
        if (adminUser == null) {
            Log.w(TAG, "setUser  user == null >> user = new User();");
            adminUser = new AdminUser();
        }

        runUiThread(new Runnable() {

            @Override
            public void run() {
                //				bvlUser.bindView(user);//方式一
                //				uvlUser.bindView(user);//方式二
                adminUserView.bindView(adminUser);//方式三

//                etDriverRemark.setText(StringUtil.getTrimedString(driver.getHead()));
                tvDriverTag.setText(StringUtil.getTrimedString(adminUser.getTel()));
            }
        });
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initData() {//必须调用

        bottomMenuView.bindView(MenuUtil.getMenuList(MenuUtil.USER));

        if (adminId == 0){
            adminUser = new AdminUser();
            setAdminUser(adminUser);
        } else {
            runThread(TAG + "initData", new Runnable() {
                @Override
                public void run() {
                    setAdminUser(CacheManager.getInstance().get(AdminUser.class, "" + adminId));//先加载缓存数据，比网络请求快很多
                    //TODO 修改以下请求
                    //通用 HttpRequest.getUser(userId, 0, UserActivity.this);//http请求获取一个User
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

        adminUserView.setOnDataChangedListener(new OnDataChangedListener() {

            @Override
            public void onDataChanged() {
                adminUser = adminUserView.data;
            }
        });
    }

    @Override
    public void onBottomMenuItemClick(int intentCode) {
        if (adminUser == null) {
            Log.e(TAG, "onBottomMenuItemClick  user == null >> return;");
            return;
        }
        switch (intentCode) {
            case MenuUtil.INTENT_CODE_SEND:
                CommonUtil.shareInfo(context, JSON.toJSONString(adminUser));
                break;
            default:
                String phone = StringUtil.getCorrectPhone(adminUser.getTel());
                if (StringUtil.isNotEmpty(phone, true) == false) {
                    return;
                }
                switch (intentCode) {
                    case MenuUtil.INTENT_CODE_SEND_MESSAGE:
                        CommonUtil.toMessageChat(context, adminUser.getTel());
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
            JSONObject data = jsonObject == null ? null : jsonObject.getJSONObject("data");
            driver = JSON.parseObject(data, Driver.class);
        } catch (Exception e1) {
            Log.e(TAG, "onHttpResponse  try { user = Json.parseObject(... >>" +
                    " } catch (JSONException e1) {\n" + e1.getMessage());
        }

        if ((driver == null || driver.getDriver_id() < 0) && e != null) {
            showShortToast(R.string.get_failed);
        } else {
            setAdminUser(adminUser);
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
                if (adminUser == null) {
                    adminUser = new AdminUser(adminId);
                }
//                driver.setTag(data == null ? null : data.getStringExtra(EditTextInfoActivity.RESULT_VALUE));
                setAdminUser(adminUser);
                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        if (adminUser != null) {
//            driver.setHead(StringUtil.getTrimedString(etDriverRemark));
            CacheManager.getInstance().save(AdminUser.class, adminUser, "" + adminUser.getAdmin_id());//更新缓存
        }
    }

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
