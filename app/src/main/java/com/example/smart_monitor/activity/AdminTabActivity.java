package com.example.smart_monitor.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import zuo.biao.library.base.BaseBottomTabActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.ui.TopMenuWindow;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smart_monitor.R;
import com.example.smart_monitor.fragment.HouseTabFragment;
import com.example.smart_monitor.fragment.OrderTabFragment;
import com.example.smart_monitor.fragment.SettingFragment;
import com.example.smart_monitor.fragment.DriverTabFragment;
import com.example.smart_monitor.model.AdminUser;

/** 使用方法：复制>粘贴>改名>改代码 */
/**activity示例
 * @author Lemon
 * @warn 复制到其它工程内使用时务必修改import R文件路径为所在应用包名
 * @use toActivity(DemoActivity.createIntent(...));
 */
/**
 * TODO * 百度地图api需要以下权限
 * 1、访问网络权限 2、获取网络状态 3、读取外置存储 4、写外置存储
 * http://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-map/showmap
 */
public class AdminTabActivity extends BaseBottomTabActivity
        implements OnBottomDragListener, OnHttpResponseListener{
    private static final String TAG = "AdminTabActivity";

    //登录时将tel传入，在主界面时获取用户信息，便于其余界面获取
    private static AdminUser current_admin_user;
    private Long admin_id;
    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context, Long admin_id) {
        Bundle bundle = new Bundle();
        bundle.putLong("admin_id", admin_id);
        return new Intent(context, AdminTabActivity.class).putExtras(bundle);
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

        //初始化admin_user,driver
        this.current_admin_user = null;
        //获取登录界面发送的admin_user
        Intent intent = getIntent();
        admin_id = intent.getLongExtra("admin_id", -1);

        if (admin_id == -1){
            Intent intent_over = new Intent(AdminTabActivity.this, LoginActivity.class);
            //TODO * 弹出窗口（登录信息失效，请再次登录）
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
    private ImageView small_add;
    private ImageView small_refresh;
    private OrderTabFragment orderTabFragment;
    private DriverTabFragment driverTabFragment;
//    private ItemListFragment itemListFragment;
    private HouseTabFragment houseTabFragment;
    @Override
    public void initView() {// 必须调用
        topRightButton = findView(R.id.llBaseTabTopRightButtonContainer);
        //设置添加按钮图像
        small_add = new ImageView(this);
        small_add.setImageResource(R.drawable.add_small);
        small_add.setOnClickListener(new AddOnclick());

        small_refresh = new ImageView(this);
        small_refresh.setImageResource(R.drawable.ic_autorenew_black_24dp);
        small_refresh.setOnClickListener(new RefreshOnclick());

        super.initView();
        exitAnim = R.anim.bottom_push_out;
        driverTabFragment = DriverTabFragment.createInstance();
        orderTabFragment = OrderTabFragment.createInstance(admin_id);
//        itemListFragment = ItemListFragment.createInstance(admin_id);
        houseTabFragment = HouseTabFragment.createInstance(admin_id);
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
        /*
         * 0：仓库 HouseTabFragment
         * 1：订单详情 OrderTabFragment
         * 2：司机目录 DriverTabFragment
         * 3：设置 SettingFragment
         */
        switch (position) {
            case 1:
                //return UserRecyclerFragment.createInstance(UserRecyclerFragment.RANGE_RECOMMEND);
                //调用 切换gps方法 ，通过该方法返回fragment
                //OrderTabFragment和UserTabFragment之间会冲突，已解决
                return orderTabFragment;

            case 2:
                //重写了DemoTabFragment
                return driverTabFragment;
            case 3:
                return SettingFragment.createInstance(getActivity());
            default:
                return houseTabFragment;
        }
    };

    private static final String[] TAB_NAMES = {"仓库", "订单详情", "司机目录", "设置"};
    @Override
    protected void selectTab(int position) {
        //导致切换时闪屏，建议去掉BottomTabActivity中的topbar，在fragment中显示topbar
        //		rlBottomTabTopbar.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
        tvBaseTitle.setText(TAB_NAMES[position]);

        topRightButton.removeAllViews();
        //0为
        switch (position){
            case 0:
                topRightButton.addView(small_refresh);
                topRightButton.addView(small_add);
                break;
            case 1:
                break;
            case 2:
                break;
        }
//        if (position == 2 && position == currentPosition && demoTabFragment != null) {
//            demoTabFragment.selectNext();
//        }
    }


    // UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    // Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<




    @Override
    public void initData() {// 必须调用
        super.initData();
        initUser();
    }

    public AdminUser getCurrent_Admin_user(){
        return current_admin_user;
    }

    public void setCurrent_Admin_user(AdminUser current_admin_user){
        AdminTabActivity.current_admin_user = current_admin_user;
    }

    private static final int GETADMINUSER = 1;
    private void initUser() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO * 获取登录管理员信息
//                HttpRequest.getAdminUser(tel, GETADMINUSER, AdminTabActivity.this);
            }
        }, 1000);
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
        //将Activity的onDragBottom事件传递到Fragment，非必要<<<<<<<<<<<<<<<<<<<<<<<<<<<
        switch (currentPosition) {
            case 2:
                //TODO 附加注释
//                if (demoTabFragment != null) {
//                    if (rightToLeft) {
//                        //demoTabFragment.selectMan();
//                    } else {
//                        //demoTabFragment.selectPlace();
//                    }
//                }
                break;
            default:
                break;
        }
        //将Activity的onDragBottom事件传递到Fragment，非必要>>>>>>>>>>>>>>>>>>>>>>>>>>>
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

    //添加事件按钮捕获
    private class AddOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (currentPosition){
                case 0:
                    houseTabFragment.runTopMenu();
                    break;
            }
        }
    }

    private class RefreshOnclick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (currentPosition) {
                case 0:
                    //调用houseTabFragment中的refresh方法
                    houseTabFragment.getHouseList();
                    break;
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

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    // 内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    // 内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}