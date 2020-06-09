package com.example.smart_monitor.driver.driver_fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.DriverActivity;
import com.example.smart_monitor.driver.driver_activity.AdminUserActivity;
import com.example.smart_monitor.driver.driver_adapter.AdminUserAdapter;
import com.example.smart_monitor.model.AdminUser;
import com.example.smart_monitor.util.HttpRequest;

import java.util.List;

import zuo.biao.library.base.BaseHttpListFragment;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;

public class AdminListFragment extends BaseHttpListFragment<AdminUser, ListView, AdminUserAdapter>
        implements OnHttpResponseListener, CacheCallBack<AdminUser> {
	private static final String TAG = "DriverListFragment";

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static final String DRIVER_ID = "DRIVER_ID";
    /**创建一个Fragment实例
     * @return
     */
    public static AdminListFragment createInstance(long driver_id) {
        AdminListFragment fragment = new AdminListFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(DRIVER_ID, driver_id);
        fragment.setArguments(bundle);

        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long driver_id = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        argument = getArguments();
        if (argument != null) {
            driver_id = argument.getLong(DRIVER_ID);
        }

        initCache(this);

        initView();
        initData();
        initEvent();

        onRefresh();
        return view;
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initView() {//必须在onCreateView方法内调用
        super.initView();

    }

    @Override
    public void setList(final List<AdminUser> list) {
        setList(new AdapterCallBack<AdminUserAdapter>() {

            @Override
            public void refreshAdapter() {
                adapter.refresh(list);
            }

            @Override
            public AdminUserAdapter createAdapter() {
                return new AdminUserAdapter(context);
            }
        });
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initData() {//必须在onCreateView方法内调用
        super.initData();

    }

    @Override
    public void getListAsync(final int page) {
        showProgressDialog(R.string.loading);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //此处的getGoodsList从网络获取内容
                //需要使用page的负数来作为requestCode
                HttpRequest.getInfo("queryMoreAdminUser.do", -page, AdminListFragment.this);
                //onHttpResponse(-page, page >= 5 ? null : JSON.toJSONString(ItemUtil.getGoodsList(page, getCacheCount())), null);
            }
        }, 1000);
    }

    public List<AdminUser> parseArray(String json) {
        return JSON.parseArray(json, AdminUser.class);
    }

    @Override
    public Class<AdminUser> getCacheClass() {
        return AdminUser.class;
    }

    @Override
    public String getCacheGroup() {
        return "admin=" + driver_id;
    }

    @Override
    public String getCacheId(AdminUser data) {
        return data == null ? null : "" + data.getAdmin_id();
    }

    @Override
    public int getCacheCount() {
        return 100;
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须在onCreateView方法内调用
        super.initEvent();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.selectedPosition = adapter.selectedPosition == position ? -1 : position;
        adapter.notifyListDataSetChanged();

        toActivity(AdminUserActivity.createIntent(context, id));
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

                List<AdminUser> list = parseArray(resultJson);
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
