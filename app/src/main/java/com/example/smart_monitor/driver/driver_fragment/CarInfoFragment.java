package com.example.smart_monitor.driver.driver_fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.example.smart_monitor.activity.GpsActivity;
import com.example.smart_monitor.driver.driver_activity.CarInfoActivity;
import com.example.smart_monitor.driver.driver_activity.DriverTabActivity;
import com.example.smart_monitor.service.DriverService;
import com.example.smart_monitor.util.HttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zuo.biao.library.base.BaseFragment;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.model.Entry;

public class CarInfoFragment extends BaseFragment implements OnHttpResponseListener {

    private static final String TAG = "DriverGpsFragment";
    private static final String DRIVER_ID = "DRIVER_ID";
    private static final String LOCATIONURL =
            "http://api.map.baidu.com/reverse_geocoding/v3/?ak=1jHLIzMhEkqWGpgbDHOtyvLPZyEEtOGA&" +
                    "mcode=2C:77:D9:40:2A:22:ED:BC:D8:6A:F0:01:DB:17:A6:E5:6F:71:73:59;com.example.smart_monitor&" +
                    "output=json&coordtype=bd09ll&location=";
    public static DriverTabActivity driverTabActivity;

    //与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static CarInfoFragment createInstance(long driver_id) {
        CarInfoFragment fragment = new CarInfoFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(DRIVER_ID, driver_id);

        fragment.setArguments(bundle);
        return fragment;
    }

    //与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long driver_id = -1;
    private long car_id = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.car_info_fragment);

        argument = getArguments();
        assert argument != null;
        driver_id = argument.getLong(DRIVER_ID);

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

        return view;//返回值必须为view
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private LinearLayout llCar;

    private TextView tvCarType;
    private TextView tvCarViewId;
    private TextView tvCarTem;
    private TextView tvCarWeight;

    private TextView tvCarLocation;

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

        llCar = findView(R.id.llCar);
        tvCarType = findView(R.id.tvCarType);
        tvCarViewId = findView(R.id.tvCarViewId);
        tvCarTem = findView(R.id.tvCarTem);
        tvCarWeight = findView(R.id.tvCarWeight);

        tvCarLocation = findView(R.id.tvCarLocation);

        //获取存放百度地图SDK的fragment
        mMapView = findView(R.id.carMapView);

        initBitmap();
    }

    private BitmapDescriptor cold_max;
    private BitmapDescriptor warm_max;
    private Bitmap cold_max_bitmap;
    private Bitmap warm_max_bitmap;
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

        cold_max_bitmap = maxBitmap(car_bitmap, cold_bitmap);
        warm_max_bitmap = maxBitmap(car_bitmap, warm_bitmap);

        cold_max = BitmapDescriptorFactory
                .fromBitmap(cold_max_bitmap);
        warm_max = BitmapDescriptorFactory
                .fromBitmap(warm_max_bitmap);
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

        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setOpenAutoNotifyMode();
        locationClientOption.setOpenAutoNotifyMode(3000,1,LocationClientOption.LOC_SENSITIVITY_HIGHT);
        mLocationClient.setLocOption(locationClientOption);
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
                //TODO * 通过driver_id获取车辆信息
//                HttpRequest.getInfo(admin_id, "queryMoreDriversGps.do", GETDRIVERGPSLIST, CarInfoFragment.this);
            }
        }, 1000);
    }

    private void setDriverGpsView(LatLng point){
        mBaiduMap.clear();
        //构建Marker图标
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromBitmap(cold_max_bitmap);
        //构建MarkerOption，用于在地图上添加Marker
        try {
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmapDescriptor);
            mBaiduMap.addOverlay(option);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须在onCreateView方法内调用
        //注册LocationListener监听器
        MylocationListener mylocationListener = new MylocationListener();
        mLocationClient.registerLocationListener(mylocationListener);
        mLocationClient.start();

        llCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (car_id == -1){
                    toActivity(CarInfoActivity.createIntent(context, driver_id, (long)0));
                } else {
                    toActivity(CarInfoActivity.createIntent(context, driver_id, car_id));
                }
            }
        });
    }

    private static final int GETADDRESS = 1;
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
            case GETADDRESS:
                JSONObject result_address = JSONObject.parseObject(resultJson);
                JSONObject result_poi = result_address.getJSONObject("result");
                tvCarLocation.setText(result_poi.getString("formatted_address"));
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

    public class MylocationListener extends BDAbstractLocationListener {

        //用于判断是否第一次打开定位
        private boolean isFirstIn = true;

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mMapView == null){
                return;
            }

            LatLng myLocation = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

            if (isFirstIn) {
                //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
                setDriverGpsView(myLocation);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(myLocation);
                //改变地图状态
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;
            }

            Log.d(TAG, "onReceiveLocation: latitude:" + myLocation.latitude + " longitude:" + myLocation.longitude);
            setDriverGpsView(myLocation);
            //TODO * 将此处的经纬度坐标信息转化为poi信息存储到数据库中
            HttpRequest.gpsToAddress(myLocation.latitude, myLocation.longitude, LOCATIONURL, GETADDRESS, CarInfoFragment.this);
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(bdLocation.getRadius())
//                    .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
//                    .longitude(bdLocation.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
        }
    }

    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}