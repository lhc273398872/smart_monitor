package com.example.smart_monitor.driver.driver_fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.HouseActivity;
import com.example.smart_monitor.activity.OrderGoodsActivity;
import com.example.smart_monitor.activity.OrderSelectActivity;
import com.example.smart_monitor.driver.driver_activity.CarInfoActivity;
import com.example.smart_monitor.driver.driver_activity.DriverTabActivity;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.model.Car;
import com.example.smart_monitor.model.OrderGpsToDriver;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.util.overlayutil.DrivingRouteOverlay;

import java.util.ArrayList;
import java.util.List;

import zuo.biao.library.base.BaseFragment;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.ui.TopMenuWindow;

public class CarInfoFragment extends BaseFragment implements OnHttpResponseListener {

    private static final String TAG = "DriverGpsFragment";
    private static final String DRIVER_ID = "DRIVER_ID";
    private static final String LOCATIONURL =
            "http://api.map.baidu.com/reverse_geocoding/v3/?ak=1jHLIzMhEkqWGpgbDHOtyvLPZyEEtOGA&" +
                    "mcode=2C:77:D9:40:2A:22:ED:BC:D8:6A:F0:01:DB:17:A6:E5:6F:71:73:59;com.example.smart_monitor&" +
                    "output=json&coordtype=bd09ll&location=";
    public static DriverTabActivity driverTabActivity;

    private static final int GETADDRESS = 1;
    private static final int GETDRIVERCAR = 2;
    private static final int GETDRIVERORDERGPS = 3;
    private static final int ADDCAR = 4;
    private static final int UPDATACAR = 5;

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
    private Car car;
    private RoutePlanSearch mSearch;

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
        initSearch();
    }

    private void initSearch(){
        mSearch = RoutePlanSearch.newInstance();
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
                if(drivingRouteResult.getRouteLines().size() > 0){
                    overlay.setData(drivingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        };
        mSearch.setOnGetRoutePlanResultListener(listener);
    }

    private BitmapDescriptor cold_max;
    private BitmapDescriptor warm_max;
    private Bitmap cold_max_bitmap;
    private Bitmap warm_max_bitmap;

    private Bitmap home_bule_bitmap;
    private Bitmap home_red_bitmap;

    private Bitmap order_blue_point;
    private Bitmap order_red_point;
    private void initBitmap(){
        //初始化home图标
        Drawable vectorDrawable = context.getDrawable(R.drawable.ic_home_blue_24dp);
        home_bule_bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(home_bule_bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        vectorDrawable = context.getDrawable(R.drawable.ic_home_red_24dp);
        home_red_bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(home_red_bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        //初始化location图标
        vectorDrawable = context.getDrawable(R.drawable.ic_location_on_blue_24dp);
        order_blue_point = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(order_blue_point);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        vectorDrawable = context.getDrawable(R.drawable.ic_location_on_red_24dp);
        order_red_point = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(order_red_point);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);


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

//    private List<Entry<String, String>> list;
    private List<String> latlng;
    //百度地图SDK
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MyLocationConfiguration myLocationConfiguration;

    List<LatLng> points = new ArrayList<>();
    List<OrderGpsToDriver> order_list;
    private List<OverlayOptions> optionsList;
    private OverlayOptions driverOptions;

    @Override
    public void initData() {//必须在onCreateView方法内调用

        optionsList = new ArrayList<>();
        getDriverGpsList();
        getPointGpsList();
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

    private void getDriverGpsList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过driver_id获取车辆信息
                HttpRequest.getInfo(driver_id, "queryDriverCar.do", GETDRIVERCAR, CarInfoFragment.this);
            }
        }, 1000);
    }

    private void getPointGpsList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //通过driver_id获取订单位置信息
                HttpRequest.getInfo(driver_id, "queryDriverOrderGps.do", GETDRIVERORDERGPS, CarInfoFragment.this);
            }
        }, 1000);
    }

    private void setMapOverlay(){
        mBaiduMap.clear();
        runUiThread(new Runnable() {
            @Override
            public void run() {
                //在地图上批量添加
                mBaiduMap.addOverlays(optionsList);
                mBaiduMap.addOverlay(driverOptions);
            }
        });
    }

    private void setOrderGpsView(final List<OrderGpsToDriver> result_list){
        runUiThread(new Runnable() {
            @Override
            public void run() {

                for (OrderGpsToDriver order : result_list){
                    if (order.getOrder_state() == 4){
                        continue;
                    }
                    LatLng point;
                    BitmapDescriptor bitmapDescriptor;
                    Bundle bundle = new Bundle();
                    switch (order.getOrder_state()){
                        case 1:
                            //运输时，显示相应订单位置
                            point = new LatLng(Double.parseDouble(order.getOrder_latitude()),
                                    Double.parseDouble(order.getOrder_longitude()));
                            bitmapDescriptor = BitmapDescriptorFactory
                                    .fromBitmap(order_blue_point);
                            bundle.putBoolean("isOrder", true);
                            bundle.putString("admin_name", order.getAdmin_name());
                            //TODO
                            break;
                        case 3:
                            //等待司机同意时，显示相应订单位置
                            point = new LatLng(Double.parseDouble(order.getOrder_latitude()),
                                    Double.parseDouble(order.getOrder_longitude()));
                            bitmapDescriptor = BitmapDescriptorFactory
                                    .fromBitmap(order_red_point);

                            bundle.putBoolean("isOrder", true);
                            bundle.putString("admin_name", order.getAdmin_name());

                            LatLng home_point = new LatLng(Double.parseDouble(order.getHouse_latitude()),
                                    Double.parseDouble(order.getHouse_longitude()));;
                            BitmapDescriptor home_bitmapDescriptor = BitmapDescriptorFactory
                                    .fromBitmap(home_red_bitmap);

                            Bundle house_bundle = new Bundle();
                            house_bundle.putString("house_name",order.getHouse_name());
                            house_bundle.putLong("order_id", order.getOrder_id());
                            house_bundle.putLong("admin_id", order.getAdmin_id());
                            house_bundle.putBoolean("isOrder", false);
                            //TODO

                            //构建MarkerOption，用于在地图上添加Marker
                            try {
                                OverlayOptions option = new MarkerOptions()
                                        .position(home_point)
                                        .icon(home_bitmapDescriptor)
                                        .extraInfo(house_bundle);
                                optionsList.add(option);
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            break;
                        default:
                            //等待司机时，显示对应仓库位置
                            point = new LatLng(Double.parseDouble(order.getHouse_latitude()),
                                    Double.parseDouble(order.getHouse_longitude()));
                            bitmapDescriptor = BitmapDescriptorFactory
                                    .fromBitmap(home_bule_bitmap);
                            bundle.putBoolean("isOrder", false);
                            bundle.putString("house_name", order.getHouse_name());
                    }

                    //构建MarkerOption，用于在地图上添加Marker
                    try {
                        bundle.putLong("order_id", order.getOrder_id());
                        bundle.putLong("admin_id", order.getAdmin_id());
                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(bitmapDescriptor)
                                .extraInfo(bundle);
                        optionsList.add(option);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                setMapOverlay();
            }
        });
    }

    private void setDriverGpsView(final LatLng point){
        runUiThread(new Runnable() {
            @Override
            public void run() {
                //构建Marker图标
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                        .fromBitmap(cold_max_bitmap);
                //构建MarkerOption，用于在地图上添加Marker
                try {
                    OverlayOptions gpsOption = new MarkerOptions()
                            .position(point)
                            .icon(bitmapDescriptor);
                    driverOptions = gpsOption;
                    setMapOverlay();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setCarInfo(final Car car){
        runUiThread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                car_id = car.getCar_id();
                tvCarType.setText(car.getCar_type());
                tvCarViewId.setText("" + car.getCar_id());

                switch (car.getCar_tem()){
                    case 0:
                        tvCarTem.setText("温度适宜");
                        tvCarTem.setTextColor(R.color.green);
                        break;
                    case 1:
                        tvCarTem.setText("温度过高");
                        tvCarTem.setTextColor(R.color.red);
                        break;
                    case -1:
                        tvCarTem.setText("温度过低");
                        tvCarTem.setTextColor(R.color.blue_sky);
                }

                tvCarWeight.setText(car.getCar_weight());
            }
        });
    }

    public void refreshCarInfo(){
        //刷新地图信息及车辆信息
        mBaiduMap.clear();
        optionsList.clear();
        getDriverGpsList();
        getPointGpsList();
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
                    toActivity(CarInfoActivity.createIntent(context, driver_id, (long)0), ADDCAR);
                } else {
                    toActivity(CarInfoActivity.createIntent(context, driver_id, car_id), UPDATACAR);
                }
            }
        });

        //地图单击事件捕捉
        final BaiduMap.OnMapClickListener mapClickListener = new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mSearch != null){
                    setMapOverlay();
                }
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                if (mSearch != null){
                    setMapOverlay();
                }
            }
        };
        mBaiduMap.setOnMapClickListener(mapClickListener);
        //Marker点击事件设置
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getExtraInfo() != null){
                    LatLng startNode = new LatLng(Double.parseDouble(latlng.get(0)), Double.parseDouble(latlng.get(1)));
                    PlanNode stNode = PlanNode.withLocation(startNode);
                    PlanNode enNode = PlanNode.withLocation(marker.getPosition());

                    mSearch.drivingSearch((new DrivingRoutePlanOption())
                            .from(stNode)
                            .to(enNode));

                    boolean isOrder = marker.getExtraInfo().getBoolean("isOrder");

                    Button button = new Button(getContext().getApplicationContext());
                    final Bundle bundle = marker.getExtraInfo();

                    if (isOrder){
                        button.setText("管理员:" + bundle.getString("admin_name"));
                    } else {
                        button.setText("仓库:" +bundle.getString("house_name"));
                    }
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long admin_id = bundle.getLong("admin_id");
                            long order_id = bundle.getLong("order_id");
                            toActivity(OrderGoodsActivity.createIntent(context, order_id, admin_id, true));
                        }
                    });

                    InfoWindow mInfoWindow = new InfoWindow(button, marker.getPosition(), -100);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }

                return true;
            }
        });

    }

    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        if (e != null){
            e.printStackTrace();
            showShortToast(R.string.get_failed);
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
                String poi_string = result_poi.getString("formatted_address");
                latlng.add(poi_string);
                tvCarLocation.setText(poi_string);
                break;
            case GETDRIVERCAR:
                car = zuo.biao.library.util.JSON.parseObject(resultJson, Car.class);

                if (car == null){
                    showShortToast("网络错误；或车辆信息为空，请添加车辆");
                    return;
                }

                setCarInfo(car);
                showShortToast("成功获取信息");
                break;
            case GETDRIVERORDERGPS:
                List<OrderGpsToDriver> result_list = JSONObject.parseArray(resultJson, OrderGpsToDriver.class);
                order_list = result_list;
                setOrderGpsView(result_list);
                Log.d(TAG, "CarInfoFragment onHttpResponse: " + order_list);
                break;
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
            case ADDCAR:
            case UPDATACAR:
                refreshCarInfo();
                break;
        }
    }

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

            Double result_latitude = bdLocation.getLatitude();
            Double result_longitude = bdLocation.getLongitude();
            LatLng myLocation = new LatLng(result_latitude, result_longitude);
            latlng = new ArrayList<>();
            latlng.add("" + result_latitude);
            latlng.add("" + result_longitude);

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
            //将此处的经纬度坐标信息转化为poi信息存储到数据库中
            HttpRequest.gpsToAddress(result_latitude, result_longitude, LOCATIONURL, GETADDRESS, CarInfoFragment.this);
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(bdLocation.getRadius())
//                    .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
//                    .longitude(bdLocation.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
        }
    }

    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}