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
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.smart_monitor.R;
import com.example.smart_monitor.activity.GpsActivity;
import com.example.smart_monitor.driver.driver_fragment.CarInfoFragment;
import com.example.smart_monitor.driver.driver_fragment.DriverOrderListFragment;
import com.example.smart_monitor.driver.driver_view.DriverOrderView;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.util.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;

import static androidx.constraintlayout.widget.Constraints.TAG;

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

    private static final int UPDATAGPS = 1;
    private static final int GETADDRESS = 2;
    private static final int GETRUNORDER = 3;
    private static final int UPDATAORDER = 4;

    private List<Order> order_list;
    private boolean getOrderFlag;
    private boolean getGpsFlag;
//    private List<Long> rfid_id;
//    private List<Integer> tem_list;

    public DriverGpsService() {
    }

    @Override
    public void onCreate() {
        Log.e("WrongService", "onCreate");
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
        Log.e("WrongService", "onBind");
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        driver_id = intent.getLongExtra("driver_id", -1);
        getOrderFlag = false;
        getGpsFlag = false;
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
        notificationManager.notify(test++, mBuilder.build());

        SendTem sendTem = new SendTem();
        sendTem.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("WrongService", "onDestroy");
        runThread = false;
        System.exit(0);
        super.onDestroy();
    }

    private int test = 30000;
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
                    .setContentText("gps信息上传网络失败")
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


        JSONObject result;
        Integer e_state;

        switch (requestCode){
            case UPDATAGPS:
                result = JSON.parseObject(resultJson);
                e_state = result.getInteger("e");
                /**
                 *  设置Builder
                 */
                if(e_state == null || e_state == 0){
                    //设置标题
                    mBuilder.setContentTitle("smart_monitor")
                            //设置内容
                            .setContentText("网络错误，请检查自身网络")
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
                } else {
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
                }
                break;
            case GETADDRESS:
                result = JSONObject.parseObject(resultJson);
                JSONObject result2 = result.getJSONObject("result");
                String test_result = result2.getString("formatted_address");
                latLng.add(test_result);
                HttpRequest.getInfo(driver_id, latLng, "updataDriverGps.do", UPDATAGPS, DriverGpsService.this);
                Log.d("DriverGpsService:", result.toJSONString());
                Log.d("DriverGpsService:", test_result);
                mBuilder.setContentTitle("smart_monitor")
                        //设置内容
                        .setContentText("成功获取poi位置信息" + test_result)
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
            case GETRUNORDER:
                android.util.Log.d(TAG, "onHttpResponse: " + resultJson);
                order_list = JSON.parseArray(resultJson, Order.class);
                android.util.Log.d(TAG, "onHttpResponse: order_list:" + JSON.toJSONString(order_list));
                getOrderFlag = true;
                judgeDirection();
                mBuilder.setContentTitle("smart_monitor")
                        //设置内容
                        .setContentText("成功获取订单位置信息")
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
            case UPDATAORDER:
                result = JSON.parseObject(resultJson);
                e_state = result.getInteger("e");
                if(e_state == null || e_state == 0){
                    //设置标题
                    mBuilder.setContentTitle("smart_monitor")
                            //设置内容
                            .setContentText("网络错误，请检查自身网络")
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
                } else {
                    //设置标题
                    mBuilder.setContentTitle("smart_monitor")
                            //设置内容
                            .setContentText("成功发送订单到达信息，等待管理员确认")
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

    private void judgeDirection(){
        if (getOrderFlag && getGpsFlag){
            for (Order order : order_list){
                if (order.getOrder_state() == 2 || order.getAdmin_sure() == 1){
                    continue;
                }

                android.util.Log.d(TAG, "onHttpResponse: " + result_latitude + result_longitude);
                android.util.Log.d(TAG, "onHttpResponse: " + order.getOrder_latitude());
                android.util.Log.d(TAG, "onHttpResponse: " + order.getOrder_longitude());
                LatLng nowLocation = new LatLng(result_latitude, result_longitude);
                LatLng orderLocation = new LatLng(Double.parseDouble(order.getOrder_latitude()),
                        Double.parseDouble(order.getOrder_longitude()));

                double distance = DistanceUtil.getDistance(nowLocation, orderLocation);

                if (distance < 100){
                    order.setAdmin_sure(1);
                    List<String> orderStateList = new ArrayList<>();
                    orderStateList.add("1");
                    orderStateList.add("" + order.getOrder_id());
                    HttpRequest.updateInfo(orderStateList, "updataSimpleOrderSure.do", UPDATAORDER, DriverGpsService.this);
                }

            }
        }
    }

    private Double result_latitude;
    private Double result_longitude;
    private List<String> latLng;
    public class LocationListener extends BDAbstractLocationListener {

        //用于判断是否第一次打开定位
        private boolean isFirstIn = true;

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            result_latitude = bdLocation.getLatitude();
            result_longitude = bdLocation.getLongitude();
            latLng = new ArrayList<>();
            latLng.add("" + result_latitude);
            latLng.add("" + result_longitude);

            getGpsFlag = true;


            //将此处的经纬度坐标信息转化为poi信息,poi信息获得后，将其数据存储到数据库中
            HttpRequest.gpsToAddress(result_latitude, result_longitude, LOCATIONURL, GETADDRESS, DriverGpsService.this);

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

    private int times = 0;
    class SendTem extends Thread {
        @Override
        public void run() {
            runThread = true;
            while (runThread){
                try {
                    Log.e("runThread", "runThread");
                    Thread.sleep(5000);
                    getOrderFlag = false;
                    HttpRequest.getDriverOrders(driver_id, 0, GETRUNORDER, DriverGpsService.this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            super.run();

        }
    }
}

