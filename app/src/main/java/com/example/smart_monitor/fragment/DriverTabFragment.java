/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package com.example.smart_monitor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.smart_monitor.R;
import com.example.smart_monitor.model.Driver;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import zuo.biao.library.base.BaseTabFragment;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.interfaces.OnStopLoadListener;
import zuo.biao.library.util.Log;


/** 使用方法：复制>粘贴>改名>改代码 */
/**带标签的Fragment示例
 * @author Lemon
 * @use new DemoTabFragment(),具体参考.DemoFragmentActivity(initData方法内)
 */
//TODO 重写 布局文件demo_tab_fragment、DemoListActivity、DemoTabFragment、DemoListFragment
public class DriverTabFragment extends BaseTabFragment
        implements CacheCallBack<Driver>,OnClickListener,
        OnHttpResponseListener, OnStopLoadListener, OnRefreshListener, OnLoadmoreListener {
    //	private static final String TAG = "DemoTabFragment";

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**创建一个Fragment实例
     * @return
     */
    public static DriverTabFragment createInstance() {
        DriverTabFragment fragment = new DriverTabFragment();

        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //在initData中调用
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, R.layout.driver_http_fragment);
        //		needReload = true;

        //功能归类分区方法，必须调用<<<<<<<<<<
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
        Log.d("test","lhcAddTop");

        addTopRightButton(newTopRightImageView(context, R.drawable.add_small)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showShortToast("添加");
            }
        });
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

    //改为运输中、运输结束（可全局查找 切换等待中/运输中）
    @Override
    protected String[] getTabNames() {
        return new String[] {"等待中", "运输中"};
    }

    //BaseTabFragment中的selectFragment方法调用了该方法
    //修改，将position放入UserListFragment中，令其与User中的某个属性（运输中）进行比较，返回相同的值(可查 将数据库中获取到的User值放入list中)
    @Override
    protected Fragment getFragment(int position) {
        //示例代码<<<<<<<<<<<<<<<<<<
        showShortToast("getFragment position = " + position);
        return DriverListFragment.createInstance(position);
        //示例代码>>>>>>>>>>>>>>>>>>
    }

    @Override
    public Class<Driver> getCacheClass() {
        return null;
    }

    @Override
    public String getCacheGroup() {
        return null;
    }

    @Override
    public String getCacheId(Driver data) {
        return data == null ? null : "" + data.getDriver_id();
    }

    @Override
    public int getCacheCount() {
        return 0;
    }

    @Override
    public int getFragmentContainerResId(){
        return R.id.flBaseTabFragmentContainer;
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须在onCreate方法内调用
        super.initEvent();
        topTabView.setOnTabSelectedListener(this);
    }
    //tab切换时短显
    //TODO 可能要重写父类的selectFragment，要点在于如何设置需要切换的fragment的内部值

    @Override
    public void onTabSelected(TextView tvTab, int position, int id) {
        super.onTabSelected(tvTab, position, id);
        //示例代码<<<<<<<<<<<<<<<<<<
        showShortToast("onTabSelected position = " + position);
        //示例代码>>>>>>>>>>>>>>>>>>
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

        }
    }


    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private static final int REQUEST_TO_PLACE_PICKER = 10;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
        }
    }



    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //从网络数据库获取数据
    //TODO 可参考BaseHttpListFragment中的onHttpResponse
    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {

    }

    @Override
    public void onStopRefresh() {
    }

    @Override
    public void onStopLoadMore(boolean isHaveMore) {

    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {

    }

    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}