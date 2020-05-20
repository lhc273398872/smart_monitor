package com.example.smart_monitor.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.DriverActivity;
import com.example.smart_monitor.adapter.DriverAdapter;
import com.example.smart_monitor.model.Driver;
import com.example.smart_monitor.model.User;
import com.example.smart_monitor.util.HttpRequest;

import zuo.biao.library.base.BaseHttpListFragment;
import zuo.biao.library.base.BaseListFragment;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.JSON;


/** 使用方法：复制>粘贴>改名>改代码 */
/**列表Fragment示例
 * @author Lemon
 * @use new DemoListFragment(),具体参考.DemoTabActivity(getFragment方法内)
 */
//TODO 重写DemoAdapter、布局文件demo_list_fragment、UserActivity
public class DriverListFragment extends BaseHttpListFragment<Driver, ListView, DriverAdapter>
        implements OnHttpResponseListener, CacheCallBack<Driver> {
	private static final String TAG = "DriverListFragment";

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static final String DRIVER_RUNNING = "DRIVER_RUNNING";
    private Driver longSelectDriver = null;
    private DrawerLayout drawerLayout = null;

    /**创建一个Fragment实例
     * @return
     */
    public static DriverListFragment createInstance(int isRunning) {
        DriverListFragment fragment = new DriverListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(DRIVER_RUNNING, isRunning);

        fragment.setArguments(bundle);
        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    public static final int RUNNING = 1;    //运输中
    public static final int NO_RUNNING = 0; //等待中
    private int running = NO_RUNNING;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        argument = getArguments();
        if (argument != null) {
            running = argument.getInt(DRIVER_RUNNING, running);
        }

        initCache(this);

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

        onRefresh();


        return view;//返回值必须为view
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initView() {//必须在onCreateView方法内调用
        super.initView();

    }

    //TODO 单独提取User的部分内容
    @Override
    public void setList(final List<Driver> list) {
        //示例代码<<<<<<<<<<<<<<<
        setList(new AdapterCallBack<DriverAdapter>() {

            @Override
            public void refreshAdapter() {
                adapter.refresh(list);
            }

            @Override
            public DriverAdapter createAdapter() {
                return new DriverAdapter(context);
            }
        });
        //示例代码>>>>>>>>>>>>>>>
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initData() {//必须在onCreateView方法内调用
        super.initData();

    }

    //TODO * 将数据库中获取到的Driver值放入list中
    @Override
    public void getListAsync(final int page) {
        showProgressDialog(R.string.loading);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //此处的getGoodsList从网络获取内容
                //需要使用page的负数来作为requestCode
                HttpRequest.getDriversList(running, -page, DriverListFragment.this);
                //onHttpResponse(-page, page >= 5 ? null : JSON.toJSONString(ItemUtil.getGoodsList(page, getCacheCount())), null);
            }
        }, 1000);
    }

    public List<Driver> parseArray(String json) {
        return JSON.parseArray(json, Driver.class);
    }

    @Override
    public Class<Driver> getCacheClass() {
        return Driver.class;
    }

    @Override
    public String getCacheGroup() {
        return "isRunning=" + running;
    }

    @Override
    public String getCacheId(Driver data) {
        return data == null ? null : "" + data.getDriver_id();
    }

    @Override
    public int getCacheCount() {
        return 100;
    }

    public Driver getLongSelectDriver(){
        return longSelectDriver;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout){
        this.drawerLayout = drawerLayout;
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须在onCreateView方法内调用
        super.initEvent();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //实现单选
        adapter.selectedPosition = adapter.selectedPosition == position ? -1 : position;
        adapter.notifyListDataSetChanged();

        toActivity(DriverActivity.createIntent(context, id));//一般用id，这里position仅用于测试 id));//
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        for (Driver driver : adapter.getList()){
            if (driver.getDriver_id() == id){
                longSelectDriver = driver;
            }
        }

        if (drawerLayout != null){
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }


        return true;
    }

    private final static int ADDDRIVERS = 9;

    public void runDriverActivity(){
        toActivity(DriverActivity.createIntent(context, 0), ADDDRIVERS);
    }



    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHttpResponse(final int requestCode, final String resultJson, final Exception e) {
        runThread(TAG + "onHttpResponse", new Runnable() {

            @Override
            public void run() {
                int page = 0;
                if (requestCode > 0) {
                    Log.w(TAG, "requestCode > 0, 应该用BaseListFragment#getListAsync(int page)中的page的负数作为requestCode!");
                } else {
                    page = - requestCode;
                }

                List<Driver> list = parseArray(resultJson);
                if ((list == null || list.isEmpty()) && e != null) {
                    onLoadFailed(page, e);
                } else {
                    onLoadSucceed(page, list);
                }
            }
        });

    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}
