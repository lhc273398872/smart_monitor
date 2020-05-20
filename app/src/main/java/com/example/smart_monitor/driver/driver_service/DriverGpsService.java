package com.example.smart_monitor.driver.driver_service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.GpsActivity;
import com.example.smart_monitor.driver.driver_fragment.CarInfoFragment;
import com.example.smart_monitor.util.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;

public class DriverGpsService extends Service implements OnHttpResponseListener{

    public static boolean runThread = true;
    private long driver_id;
    private LocationClient mLocationClient;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    private static final String LOCATIONURL =
            "http://api.map.baidu.com/reverse_geocoding/v3/?ak=1jHLIzMhEkqWGpgbDHOtyvLPZyEEtOGA&" +
                    "mcode=2C:77:D9:40:2A:22:ED:BC:D8:6A:F0:01:DB:17:A6:E5:6F:71:73:59;com.example.smart_monitor&" +
                    "output=json&coordtype=bd09ll&location=";

    private int UPDATAGPS = 1;
    private static final int GETADDRESS = 2;

    public DriverGpsService() {
    }

    @Override
    public void onCreate() {
        Log.e("DriverService", "onCreate");
        LocationListener listener = new LocationListener();
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setScanSpan(1000);
        locationClientOption.setOpenAutoNotifyMode();
        locationClientOption.setOpenAutoNotifyMode(3000,1,LocationClientOption.LOC_SENSITIVITY_HIGHT);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(listener);
        mLocationClient.setLocOption(locationClientOption);
        mLocationClient.start();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("DriverService", "onBind");
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO * 完成访问数据库逻辑
        driver_id = intent.getLongExtra("driver_id", -1);
        notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        /**
         *  实例化通知栏构造器
         */
        mBuilder = new NotificationCompat.Builder(this);
        /**
         *  设置Builder
         */
        //设置标题
        mBuilder.setContentTitle("gps后台上传")
                //设置内容
                .setContentText("已开启gps后台上传服务")
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                .setDefaults(Notification.DEFAULT_SOUND);
        //发送通知请求
        notificationManager.notify(10, mBuilder.build());

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("DriverService", "onDestroy");
        runThread = false;
        System.exit(0);
        super.onDestroy();
    }

    private int test = 0;
    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        NotificationManager notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        if (e != null){
            e.printStackTrace();
            /**
             *  实例化通知栏构造器
             */
            /**
             *  设置Builder
             */
            //设置标题
            //设置标题
            mBuilder.setContentTitle("smart_monitor")
                    //设置内容
                    .setContentText("网络连接失败" + driver_id)
                    //设置大图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    //设置小图标
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    //设置通知时间
                    .setWhen(System.currentTimeMillis())
                    //首次进入时显示效果
                    .setTicker("smart_monitor")
                    //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                    .setDefaults(Notification.DEFAULT_SOUND);
            //发送通知请求
            notificationManager.notify(test++, mBuilder.build());
            return;
        }

        JSONObject result = JSON.parseObject(resultJson);
        Integer e_state = result.getInteger("e");

        switch (e_state){
            case 1:
                /**
                 *  设置Builder
                 */
                //设置标题
                //设置标题
                mBuilder.setContentTitle("smart_monitor")
                        //设置内容
                        .setContentText("成功上传gps位置信息" + driver_id)
                        //设置大图标
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        //设置小图标
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        //设置通知时间
                        .setWhen(System.currentTimeMillis())
                        //首次进入时显示效果
                        .setTicker("smart_monitor")
                        //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                        .setDefaults(Notification.DEFAULT_SOUND);
                //发送通知请求
                notificationManager.notify(test++, mBuilder.build());
                break;
            case 2:
                result = JSONObject.parseObject(resultJson);
                JSONObject result2 = result.getJSONObject("result");
                result2.getString("formatted_address");
                break;
            default:
                /**
                 *  设置Builder
                 */
                //设置标题
                //设置标题
                mBuilder.setContentTitle("smart_monitor")
                        //设置内容
                        .setContentText("未知错误")
                        //设置大图标
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        //设置小图标
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        //设置通知时间
                        .setWhen(System.currentTimeMillis())
                        //首次进入时显示效果
                        .setTicker("smart_monitor")
                        //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                        .setDefaults(Notification.DEFAULT_SOUND);
                //发送通知请求
                notificationManager.notify(test++, mBuilder.build());
                break;
        }
    }

    public class LocationListener extends BDAbstractLocationListener {

        //用于判断是否第一次打开定位
        private boolean isFirstIn = true;

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            Double result_latitude = bdLocation.getLatitude();
            Double result_longitude = bdLocation.getLongitude();
            List<String> latLng = new ArrayList<>();
            latLng.add("" + result_latitude);
            latLng.add("" + result_longitude);
            //TODO * 将此处的经纬度坐标信息转化为poi信息存储到数据库中
            HttpRequest.gpsToAddress(result_latitude, result_longitude, LOCATIONURL, GETADDRESS, DriverGpsService.this);
//            HttpRequest.getInfo(driver_id, latLng, "updataDriverGps.do", UPDATAGPS, DriverGpsService.this);
            //设置标题
            mBuilder.setContentTitle("smart_monitor")
                    //设置内容
                    .setContentText(test + "坐标信息" + bdLocation.getLatitude() + "longitude:" + bdLocation.getLongitude())
                    //设置大图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    //设置小图标
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    //设置通知时间
                    .setWhen(System.currentTimeMillis())
                    //首次进入时显示效果
                    .setTicker("smart_monitor")
                    //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                    .setDefaults(Notification.DEFAULT_SOUND);
            //发送通知请求
            notificationManager.notify(test++, mBuilder.build());
        }
    }
}

