package com.example.smart_monitor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.HouseActivity;
import com.example.smart_monitor.activity.OrderSelectActivity;
import com.example.smart_monitor.model.SaveHouse;
import com.example.smart_monitor.util.HttpRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import zuo.biao.library.base.BaseFragment;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.ui.TopMenuWindow;
import zuo.biao.library.util.JSON;

//覆写ItemActivity,HttpRequest,DemoApplication
public class HouseTabFragment extends BaseFragment
        implements OnHttpResponseListener, View.OnClickListener {
    private static final String TAG = "HouseListFragment";

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static final String ARGUMENT_RANGE = "ARGUMENT_RANGE";

    public static HouseTabFragment createInstance(long admin_id) {
        HouseTabFragment fragment = new HouseTabFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("admin_id", admin_id);

        fragment.setArguments(bundle);
        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    List<Fragment> fragmentTab = new ArrayList<>();
    List<String> titleTab = new ArrayList<>();
    List<SaveHouse> houseList = null;
    private long admin_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.house_tab_fragment);

        argument = getArguments();
        admin_id = argument.getLong("admin_id");

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

        return view;
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private LinearLayout llHouseButton;

    @Override
    public void initView() {//必须调用
        tabLayout = findView(R.id.tlHouseTab);
        viewPager2 = findView(R.id.vpHouseView);
        llHouseButton = findView(R.id.llHouseButton);
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final int GETHOUSELIST = 1;
    private static final int REQUEST_TO_TOP_MENU = 2;
    private static final int REFRESH_ITEM_LIST = 3;
    private static final int REFRESH_HOUSE_LIST = 4;

    private int currentPosition = -1;
    @Override
    public void initData() {//必须调用
        //TODO * 设置等待显示
        getHouseList();
    }

    public void getHouseList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HttpRequest.getHousesList(admin_id, GETHOUSELIST, HouseTabFragment.this);
            }
        }, 1000);
    }

    private void setHouseList(){
        titleTab.clear();
        fragmentTab.clear();
        for (SaveHouse saveHouse : houseList){
            titleTab.add(saveHouse.getHouse_name());
            fragmentTab.add(ItemListFragment.createInstance(saveHouse.getHouse_id()));
        }

        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = fragmentTab.get(position);
                return fragment;
            }

            @Override
            public int getItemCount() {
                return fragmentTab.size();
            }
        });

        viewPager2.setOffscreenPageLimit(houseList.size());

        new TabLayoutMediator(tabLayout, viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, final int position) {
                tab.setText(titleTab.get(position));
                tab.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        toActivity(HouseActivity.createIntent(context, admin_id, houseList.get(position).getHouse_id()), REFRESH_HOUSE_LIST);
                        return true;
                    }
                });
            }
        }).attach();
    }


    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initEvent() {//必须调用
    }

    public void runTopMenu(){
        toActivity(TopMenuWindow.createIntent(context, new String[]{"创建新仓库","添加新货物","添加新订单"}), REQUEST_TO_TOP_MENU, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        if (e != null){
            e.printStackTrace();
            showShortToast(R.string.get_failed);
            return;
        }

        houseList = JSON.parseArray(resultJson, SaveHouse.class);

        if (houseList == null || houseList.size() == 0){
            showShortToast("请新建仓库");
        } else {
            setHouseList();
        }
    }


    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            return;
        }

        switch (requestCode) {
            case REFRESH_HOUSE_LIST:
                getHouseList();
                break;
            case REFRESH_ITEM_LIST:
                ItemListFragment itemListFragment = (ItemListFragment) fragmentTab.get(viewPager2.getCurrentItem());
                itemListFragment.refreshList();
                break;
            case REQUEST_TO_TOP_MENU:
                int position = data.getIntExtra(TopMenuWindow.RESULT_POSITION, -1);

                if (position == -1){
                    return;
                }

                switch (position){
                    case 0:
                        //新建仓库
                        toActivity(HouseActivity.createIntent(context, admin_id, (long)0), REFRESH_HOUSE_LIST);
                        break;
                    case 1:
                        //新建货物
                        if (fragmentTab.size() == 0 || houseList == null){
                            showShortToast("请新建仓库，或检查自身网络");
                            return;
                        }

                        itemListFragment = (ItemListFragment) fragmentTab.get(viewPager2.getCurrentItem());

//                        long house_id = houseList.get(viewPager2.getCurrentItem()).getHouse_id();
                        itemListFragment.runItemActivity(houseList.get(viewPager2.getCurrentItem()).getHouse_id());
                        break;
                    case 2:
                        //新建货物
                        if (fragmentTab.size() == 0 || houseList == null){
                            showShortToast("请新建仓库，或检查自身网络");
                            return;
                        }
                        //新建订单
                        toActivity(OrderSelectActivity.createIntent(context, admin_id, houseList.get(viewPager2.getCurrentItem()).getHouse_id()), REFRESH_ITEM_LIST);
                        break;
                }

                break;
        }
    }




    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}