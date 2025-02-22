package com.example.smart_monitor.driver.driver_fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.smart_monitor.R;
import com.example.smart_monitor.driver.driver_activity.DriverTabActivity;
import com.example.smart_monitor.fragment.DriverGpsFragment;
import com.example.smart_monitor.fragment.OrderListFragment;

import zuo.biao.library.base.BaseTabFragment;

public class CarTabFragment extends BaseTabFragment
        implements View.OnClickListener {
    private static final String TAG = "CarTabFragment";
    public static DriverTabActivity driverTabActivity;

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static final String DRIVER_ID = "DRIVER_ID";
    /**创建一个Fragment实例
     * @return
     */
    public static CarTabFragment createInstance(long driver_id) {
        CarTabFragment fragment = new CarTabFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(DRIVER_ID, driver_id);

        fragment.setArguments(bundle);
        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //在initData中调用
    private long driver_id = -1;
    private CarInfoFragment carInfoFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, R.layout.order_http_fragment);

        argument = getArguments();
        driver_id = argument.getLong(DRIVER_ID);

        //功能归类分区方法，必须调用<<<<<<<<<<
        carInfoFragment = CarInfoFragment.createInstance(driver_id);
        carInfoFragment.driverTabActivity = driverTabActivity;

        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>


        return view;
    }



    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Override
    public void initView() {//必须在onCreate方法内调用
        super.initView();

    }


    /**当需要自定义 tab bar layout时，要实现此方法
     */
    @Override
    public int getTopTabViewResId() {
        return R.layout.top_tab_view;
    }

    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initData() {//必须在onCreate方法内调用
        super.initData();
    }


    @Override
    protected String[] getTabNames() {
        return new String[] {"车辆信息", "货物列表"};
    }

    @Override
    protected Fragment getFragment(int position) {

        switch (position){
            case 1:
                return CarItemListFragment.createInstance(driver_id);
            default:
                return carInfoFragment;
        }
    }



    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须在onCreate方法内调用
        super.initEvent();

    }

    public void refreshCarInfo(){
        carInfoFragment.refreshCarInfo();
    }

    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>







    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}
