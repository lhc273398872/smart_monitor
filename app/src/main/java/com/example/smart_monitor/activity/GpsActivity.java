package com.example.smart_monitor.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.smart_monitor.R;
import com.example.smart_monitor.fragment.DriverGpsFragment;
import com.example.smart_monitor.util.HttpRequest;

import java.util.Map;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.SettingUtil;


//TODO * 结束时将位置信息传回
public class GpsActivity extends BaseActivity
        implements OnBottomDragListener, OnHttpResponseListener {
    private static final String TAG = "SettingActivity";
    private static final String LOCATIONURL =
            "http://api.map.baidu.com/reverse_geocoding/v3/?ak=1jHLIzMhEkqWGpgbDHOtyvLPZyEEtOGA&" +
                    "mcode=2C:77:D9:40:2A:22:ED:BC:D8:6A:F0:01:DB:17:A6:E5:6F:71:73:59;com.example.smart_monitor&" +
                    "output=json&coordtype=bd09ll&location=";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static Intent createIntent(Context context) {
        return new Intent(context, GpsActivity.class);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_view, this);

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private RelativeLayout rlGpsTopInfo;
    private LinearLayout llGpsLocation;

    private TextView tvTabRight;
    private EditText etGpsAddress;

    //地图控件
    private MapView mMapView = null;
    private Bitmap bitmap;

    @Override
    public void initView() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}
                    ,0);
        }

        //布局显示
        rlGpsTopInfo = findView(R.id.rlGpsTopInfo);
        llGpsLocation = findView(R.id.llGpsLocation);
        rlGpsTopInfo.setVisibility(View.VISIBLE);
        llGpsLocation.setVisibility(View.VISIBLE);

        //点击响应事件
        tvTabRight = findView(R.id.tvTabRight);

        //获取gps地图点击位置信息
        etGpsAddress = findView(R.id.etGpsAddress);
//        etGpsAddress.setMovementMethod(ScrollingMovementMethod.getInstance());

        //Gps地图组件
        mMapView = findView(R.id.bmapView);

        //初始化location图标
        Drawable vectorDrawable = context.getDrawable(R.drawable.ic_location_on_blue_24dp);
        bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
    }

    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    //百度地图SDK
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    @Override
    public void initData() {//必须调用
        //设置百度地图sdk格式
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        //设置地图缩放等级
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(15.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        mLocationClient = new LocationClient(this);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd0911");

        mLocationClient.setLocOption(option);

        GpsActivityLocationListener gpsActivityLocationListener = new GpsActivityLocationListener();
        mLocationClient.registerLocationListener(gpsActivityLocationListener);

        mLocationClient.start();
    }

    private void viewAddressPoint(LatLng point){
        mBaiduMap.clear();
        //构建Marker图标
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromBitmap(bitmap);
        //构建MarkerOption，用于在地图上添加Marker
        try {
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmapDescriptor);
            mBaiduMap.addOverlay(option);
        } catch (Exception e){
            e.printStackTrace();
        }
        //在地图上添加Marker，并显示
    }

    public static final String RESULT_STRING = "RESULT_STRING";
    public static final String RESULT_LATITUDE = "RESULT_LATITUDE";
    public static final String RESULT_LONGITUDE = "RESULT_LONGITUDE";

    private double result_latitude;
    private double result_longitude;

    protected void setResult() {
        intent = new Intent();
        intent.putExtra(RESULT_STRING, etGpsAddress.getText().toString());
        intent.putExtra(RESULT_LATITUDE, result_latitude);
        intent.putExtra(RESULT_LONGITUDE, result_longitude);
        setResult(RESULT_OK, intent);
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {
        tvTabRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
                finish();
            }
        });

        BaiduMap.OnMapClickListener onMapClickListener = new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                //改变地图状态
                mBaiduMap.animateMapStatus(msu);
                addressString = "";
                viewAddressPoint(latLng);
                result_latitude = latLng.latitude;
                result_longitude = latLng.longitude;
                HttpRequest.gpsToAddress(latLng.latitude, latLng.longitude, LOCATIONURL, GETADDRESS,GpsActivity.this);
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                LatLng latLng = mapPoi.getPosition();
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                //改变地图状态
                mBaiduMap.animateMapStatus(msu);
                viewAddressPoint(latLng);
                addressString = mapPoi.getName();
                result_latitude = latLng.latitude;
                result_longitude = latLng.longitude;
                HttpRequest.gpsToAddress(latLng.latitude, latLng.longitude, LOCATIONURL, GETADDRESS,GpsActivity.this);
            }
        };
        mBaiduMap.setOnMapClickListener(onMapClickListener);
    }

    @Override
    public void onDragBottom(boolean rightToLeft) {
        if (rightToLeft) {

            return;
        }

        finish();
    }


    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    private static final int GETADDRESS = 1;
    private String addressString = "";
    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        if (e != null){
            e.printStackTrace();
            showShortToast(R.string.get_failed);
            return;
        }

        if (resultJson == null || resultJson.isEmpty()){
            showShortToast(R.string.get_failed);
            return;
        }

        JSONObject result = JSONObject.parseObject(resultJson);
        JSONObject result2 = result.getJSONObject("result");
        etGpsAddress.setText(result2.getString("formatted_address") + addressString);
    }

    public class GpsActivityLocationListener extends BDAbstractLocationListener {
        //用于判断是否第一次打开定位
        private boolean isFirstIn = true;
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }

            if (isFirstIn) {
                result_latitude = location.getLatitude();
                result_longitude = location.getLongitude();
                LatLng nowLocation = new LatLng(result_latitude, result_longitude);
                viewAddressPoint(nowLocation);
                //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(nowLocation);
                //改变地图状态
                mBaiduMap.animateMapStatus(msu);
                HttpRequest.gpsToAddress(result_latitude, result_longitude, LOCATIONURL, GETADDRESS,GpsActivity.this);
                isFirstIn = false;
            }
        }
    }

    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>







    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}

