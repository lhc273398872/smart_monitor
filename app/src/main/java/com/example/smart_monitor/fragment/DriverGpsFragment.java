package com.example.smart_monitor.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.smart_monitor.R;
import com.example.smart_monitor.util.HttpRequest;

import zuo.biao.library.base.BaseFragment;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.model.Entry;
import zuo.biao.library.util.Log;

public class DriverGpsFragment extends BaseFragment implements OnHttpResponseListener {

    private static final String TAG = "DriverGpsFragment";
    private static final String ADMIN_ID = "ADMIN_ID";

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static DriverGpsFragment createInstance(long admin_id) {
        DriverGpsFragment fragment = new DriverGpsFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(ADMIN_ID, admin_id);

        fragment.setArguments(bundle);
        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long admin_id = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.gps_view);

        argument = getArguments();
        assert argument != null;
        admin_id = argument.getLong(ADMIN_ID);

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

        return view;//返回值必须为view
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private MapView mMapView = null;

    @Override
    public void initView() {//必须在onCreateView方法内调用

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}
                    ,0);
        }

        //获取存放百度地图SDK的fragment
        mMapView = findView(R.id.bmapView);

        initBitmap();
    }

    private BitmapDescriptor cold_max;
    private BitmapDescriptor warm_max;
    private void initBitmap(){
        //构建Marker图标
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 4;

        Bitmap car_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car
                ,bitmapOptions);

        Bitmap cold_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cold
                ,bitmapOptions);

        Bitmap warm_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.warm
                ,bitmapOptions);

        Bitmap maxCarCold = maxBitmap(car_bitmap, cold_bitmap);
        Bitmap maxCarWarm = maxBitmap(car_bitmap, warm_bitmap);

        cold_max = BitmapDescriptorFactory
                .fromBitmap(maxCarCold);
        warm_max = BitmapDescriptorFactory
                .fromBitmap(maxCarWarm);
    }

    /**
     * 将car的Bitmap和温度计的bitmap进行拼接
     * @param car
     * @param tem
     * @return
     */
    private Bitmap maxBitmap(Bitmap car, Bitmap tem){
        int width = car.getWidth() + tem.getWidth();
        int height = Math.max(car.getHeight(), tem.getHeight());

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(tem, 0, 0, null);
        canvas.drawBitmap(car, tem.getWidth(), 0, null);
        return result;
    }
    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private List<Entry<String, String>> list;
    //百度地图SDK
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MyLocationConfiguration myLocationConfiguration;
    final List<LatLng> points = new ArrayList<>();

    @Override
    public void initData() {//必须在onCreateView方法内调用

        getDriverGpsList();
        //设置百度地图sdk格式
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //百度地图定位初始化
        mLocationClient = new LocationClient(getContext());

//        //通过LocationClientOption设置LocationClient相关参数
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true);
//        option.setCoorType("bd0911");
//        option.setScanSpan(1000);
//        option.setNeedDeviceDirect(true);
//
//        mLocationClient.setLocOption(option);

        showProgressDialog(R.string.loading);

        //初始化ui数据
        runThread(TAG + "initData", new Runnable() {
            @Override
            public void run() {

                runUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                    }
                });
            }
        });

    }

    private static final int GETDRIVERGPSLIST = 1;
    private void getDriverGpsList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO * 通过admin_id获取运输员列表信息
                HttpRequest.getInfo(admin_id, "queryMoreDriversGps.do", GETDRIVERGPSLIST, DriverGpsFragment.this);
            }
        }, 1000);
    }

    private void setDriverGpsList(final List<Map> gpsList){
        runUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i=0 ; i<gpsList.size(); i++){
                    if (gpsList.get(i).get("driver_latitude") != null &&
                            gpsList.get(i).get("driver_longitude") != null){
                        //        //定义Maker坐标点
                        double latitude =  Double.parseDouble((String) gpsList.get(i).get("driver_latitude"));
                        double longtitude = Double.parseDouble((String) gpsList.get(i).get("driver_longitude"));
                        int tem = (int) gpsList.get(i).get("car_tem");
                        LatLng car_point = new LatLng(latitude, longtitude);

                        OverlayOptions car_options;
                        if (tem <=0){
                            //将car覆盖到百度地图上
                            car_options = new MarkerOptions()
                                    .position(car_point)
                                    .icon(cold_max);
                        } else {
                            car_options = new MarkerOptions()
                                    .position(car_point)
                                    .icon(warm_max);
                        }

                        mBaiduMap.addOverlay(car_options);

                        points.add(car_point);

                    }
                    mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (LatLng p : points){
                                builder = builder.include(p);
                            }

                            LatLngBounds latLngBounds = builder.build();
                            MapStatusUpdate us = MapStatusUpdateFactory.newLatLngBounds(latLngBounds,
                                    (int)(mMapView.getWidth()*0.5), (int)(mMapView.getHeight()*0.5));

                            Log.d("mMapView", "getWidth:" + mMapView.getWidth() + "getHeight:" + mMapView.getHeight());

                            mBaiduMap.setMapStatus(us);
                            Log.d("mBaiduMap", "getMapStatus:" + mBaiduMap.getMapStatus());
                        }
                    });
                }
            }
        });
    }
    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须在onCreateView方法内调用
        //注册LocationListener监听器
//        MylocationListener mylocationListener = new MylocationListener();
//        mLocationClient.registerLocationListener(mylocationListener);
        mLocationClient.start();
    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        if (e != null){
            e.printStackTrace();
            return;
        }
        if (resultJson == null){
            showShortToast(R.string.get_failed);
            return;
        }

        switch (requestCode){
            case GETDRIVERGPSLIST:
                List<Map> driverGpsList = JSONObject.parseArray(resultJson, Map.class);
                Log.d(TAG, driverGpsList.toString());
                setDriverGpsList(driverGpsList);
                break;
        }
    }

    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

//    public class MylocationListener extends BDAbstractLocationListener {
//
//        //用于判断是否第一次打开定位
//        private boolean isFirstIn = true;
//
//        @Override
//        public void onReceiveLocation(BDLocation bdLocation) {
//            if (bdLocation == null || mMapView == null){
//                return;
//            }
//
////            if (isFirstIn) {
////                //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
////                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngBounds();
////                //改变地图状态
////                mBaiduMap.setMapStatus(msu);
////                isFirstIn = false;
////            }
//
//            //TODO * 此处的getRadius,getDirection,getLatitude,getLongitude应为所需的gps信息，需存储到数据库中
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(bdLocation.getRadius())
//                    .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
//                    .longitude(bdLocation.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
//        }
//    }

    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}