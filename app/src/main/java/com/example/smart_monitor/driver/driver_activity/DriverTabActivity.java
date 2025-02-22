package com.example.smart_monitor.driver.driver_activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.LoginActivity;
import com.example.smart_monitor.driver.driver_fragment.AdminListFragment;
import com.example.smart_monitor.driver.driver_fragment.CarTabFragment;
import com.example.smart_monitor.driver.driver_fragment.DriverOrderTabFragment;
import com.example.smart_monitor.driver.driver_service.DriverGpsService;
import com.example.smart_monitor.driver.driver_service.DriverTemService;
import com.example.smart_monitor.driver.driver_service.OrderService;
import com.example.smart_monitor.fragment.DriverTabFragment;
import com.example.smart_monitor.fragment.HouseTabFragment;
import com.example.smart_monitor.fragment.OrderTabFragment;
import com.example.smart_monitor.fragment.SettingFragment;
import com.example.smart_monitor.service.WrongService;

import zuo.biao.library.base.BaseBottomTabActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;

public class DriverTabActivity extends BaseBottomTabActivity
        implements OnBottomDragListener, OnHttpResponseListener{
    private static final String TAG = "AdminTabActivity";

    //登录时将tel传入，在主界面时获取用户信息，便于其余界面获取
    private Long driver_id;
    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context, Long driver_id) {
        Bundle bundle = new Bundle();
        bundle.putLong("driver_id", driver_id);
        return new Intent(context, DriverTabActivity.class).putExtras(bundle);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //调用BaseActivity的onCreate方法，实质上调用FragmentActivity的onCreate方法
        super.onCreate(savedInstanceState);
        //设置main_tab_activity布局
        setContentView(R.layout.main_activity, this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //获取登录界面发送的admin_user
        Intent intent = getIntent();
        driver_id = intent.getLongExtra("driver_id", -1);

        if (driver_id == -1){
            Intent intent_over = new Intent(DriverTabActivity.this, LoginActivity.class);
            showShortToast("登录信息失效，请再次登录");
            startActivity(intent_over);
            finish();
        }

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>
    }



    // UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //导入demoTabFragment
    private ViewGroup topRightButton;
    private TextView[] bottomText;
    private ImageView switch_gps;
    private ImageView small_refresh;
    private OrderTabFragment orderTabFragment;
    private DriverTabFragment driverTabFragment;
//    private ItemListFragment itemListFragment;
    private HouseTabFragment houseTabFragment;
    private CarTabFragment carTabFragment;
    @Override
    public void initView() {// 必须调用
        topRightButton = findView(R.id.llBaseTabTopRightButtonContainer);
        bottomText = new TextView[TAB_NAMES.length];

        small_refresh = new ImageView(this);
        small_refresh.setImageResource(R.drawable.ic_autorenew_black_24dp);
        small_refresh.setOnClickListener(new DriverTabActivity.RefreshOnclick());

        for (int i=0; i<bottomText.length; i++){
            bottomText[i] = findView(getTabSelectIds()[1][i]);
            bottomText[i].setText(TAB_NAMES[i]);
        }

        startGpsUpdataService();
//        startOrderService();
        startTemUpdataService();
        carTabFragment = CarTabFragment.createInstance(driver_id);
        carTabFragment.driverTabActivity = this;

        super.initView();
    }


    @Override
    protected int[] getTabClickIds() {
        return new int[]{R.id.llBottomTabTab0, R.id.llBottomTabTab1, R.id.llBottomTabTab2, R.id.llBottomTabTab3};
    }

    @Override
    protected int[][] getTabSelectIds() {
        return new int[][]{
                new int[]{R.id.ivBottomTabTab0, R.id.ivBottomTabTab1, R.id.ivBottomTabTab2, R.id.ivBottomTabTab3},//顶部图标
                new int[]{R.id.tvBottomTabTab0, R.id.tvBottomTabTab1, R.id.tvBottomTabTab2, R.id.tvBottomTabTab3}//底部文字
        };
    }

    @Override
    public int getFragmentContainerResId() {
        return R.id.flMainTabFragmentContainer;
    }

    @Override
    protected Fragment getFragment(int position) {
        //在车辆货物详情加上温度显示
        /*
         * 0：车辆详情（包括订单列表显示） CarItemListFragment
         * 1：订单通知 OrderNoticeFragment()
         * 2：管理员目录 AdminUserTabFragment
         * 3：设置 SettingFragment
         */
        switch (position) {
            case 1:
                return DriverOrderTabFragment.createInstance(driver_id);
            case 2:
                return AdminListFragment.createInstance(driver_id);
            case 3:
                return SettingFragment.createInstance(getActivity(), driver_id, false);
            default:
                return carTabFragment;
        }
    };

    private static final String[] TAB_NAMES = {"车辆详情", "订单通知", "管理员目录", "设置"};
    @Override
    protected void selectTab(int position) {
        //导致切换时闪屏，建议去掉BottomTabActivity中的topbar，在fragment中显示topbar
        //		rlBottomTabTopbar.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
        tvBaseTitle.setText(TAB_NAMES[position]);



        topRightButton.removeAllViews();

        switch (position){
            case 0:
                topRightButton.addView(small_refresh);
                break;
        }

    }


    // UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    // Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<




    @Override
    public void initData() {// 必须调用
        super.initData();
    }

    // Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    // Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {// 必须调用
        super.initEvent();

    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {

    }

    @Override
    public void onDragBottom(boolean rightToLeft) {

    }



    //双击手机返回键退出<<<<<<<<<<<<<<<<<<<<<
    private long firstTime = 0;//第一次返回按钮计时
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if(secondTime - firstTime > 2000){
                    showShortToast("再按一次退出");
                    firstTime = secondTime;
                } else {//完全退出
                    moveTaskToBack(false);//应用退到后台
                    System.exit(0);
                }
                return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private class RefreshOnclick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (currentPosition) {
                case 0:
                    carTabFragment.refreshCarInfo();
//                    showShortToast("停止服务");
//                    Log.d("DriverTabActivity", "停止服务");
//                    WrongService.runThread = false;
//                    stopService(new Intent(DriverTabActivity.this, WrongService.class));
                    //调用houseTabFragment中的refresh方法
//                    houseTabFragment.getHouseList();
//                    break;
            }
        }
    }

    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
        }
    }

    Intent temUpdataService;
    public void startTemUpdataService(){
        if (temUpdataService == null){
            temUpdataService = new Intent(DriverTabActivity.this, DriverTemService.class);
            temUpdataService.putExtra("driver_id", driver_id);
            temUpdataService.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        }
        startService(temUpdataService);
    }

    public void stopTemUpdataService(){
        if (temUpdataService != null){
            stopService(temUpdataService);
            temUpdataService = null;
        }
    }

    Intent gpsUpdataService;
    public void startGpsUpdataService(){
        if (gpsUpdataService == null){
            gpsUpdataService = new Intent(DriverTabActivity.this, DriverGpsService.class);
            gpsUpdataService.putExtra("driver_id", driver_id);
            gpsUpdataService.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        }
        startService(gpsUpdataService);
    }

    public void stopGpsUpdataService(){
        if (gpsUpdataService != null){
            stopService(gpsUpdataService);
            gpsUpdataService = null;
        }
    }

    Intent orderService;
    public void startOrderService(){
        if (orderService == null){
            orderService = new Intent(DriverTabActivity.this, OrderService.class);
            orderService.putExtra("driver_id", driver_id);
            orderService.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        }
        startService(orderService);
    }

    public void stopOrderService(){
        if (orderService != null){
            stopService(orderService);
            orderService = null;
        }
    }

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    // 内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    // 内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}