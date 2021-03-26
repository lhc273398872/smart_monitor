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
import com.example.smart_monitor.R;
import com.example.smart_monitor.util.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;

public class DriverTemService extends Service implements OnHttpResponseListener{

    public static boolean runThread = true;
    private long driver_id;
    private LocationClient mLocationClient;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    private static final String LOCATIONURL =
            "http://api.map.baidu.com/reverse_geocoding/v3/?ak=1jHLIzMhEkqWGpgbDHOtyvLPZyEEtOGA&" +
                    "mcode=2C:77:D9:40:2A:22:ED:BC:D8:6A:F0:01:DB:17:A6:E5:6F:71:73:59;com.example.smart_monitor&" +
                    "output=json&coordtype=bd09ll&location=";
    private static final String TAG = "DriverTemService";

    private static final int UPDATATEM = 3;

//    private List<Long> rfid_id;
//    private List<Integer> tem_list;

    public DriverTemService() {
    }

    @Override
    public void onCreate() {
        Log.e("WrongService", "onCreate");
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
        mBuilder.setContentTitle("温度后台上传")
                //设置内容
                .setContentText("已开启温度后台上传服务")
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
                    .setContentText("温度上传网络失败")
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

        switch (requestCode){
            case UPDATATEM:
                /**
                 *  设置Builder
                 */
                if(e_state == null || e_state == 0){
                    //设置标题
                    mBuilder.setContentTitle("smart_monitor")
                            //设置内容
                            .setContentText("网络错误，无法向服务器传输温度信息")
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
                            .setContentText("成功上传温度信息")
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

    private int car_tem = 0;

    private int times = 0;
    class SendTem extends Thread {
        @Override
        public void run() {

            runThread = true;
            int number = 0;
            while(runThread){
                try {
                    android.util.Log.d(TAG, "onClick: 连接socket端口");
                    android.util.Log.d(TAG, "onClick: 获取in数据");

                    Thread.sleep(10000);
                    String getRead = "$GT: 1. 1. 1. 1,22.771629,N,113.881632,E,V,272,273,00,A*0F";

                    mBuilder.setContentTitle("smart_monitor")
                            //设置内容
                            .setContentText(getRead)
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
                    getRead = getRead.replace(" ", "");

                    number%=6;
                    String[] numberList = new String[]{"1", "2", "3", "4", "5", "6"};
                    Long rfid_id = Long.parseLong(getRead.substring(4,10).replace(".", "") + numberList[number++]);

                    String tem_all = getRead.substring(39,46);
                    int first_tem = Integer.parseInt(tem_all.substring(0, 2));
                    int second_tem = Integer.parseInt(tem_all.substring(4,6));

                    android.util.Log.d(TAG, "onClick: 连接成功" + getRead);
                    android.util.Log.d(TAG, "onClick: 连接成功" + rfid_id);
                    android.util.Log.d(TAG, "onClick: 连接成功" + tem_all);
                    android.util.Log.d(TAG, "onClick: 连接成功" + first_tem);
                    android.util.Log.d(TAG, "onClick: 连接成功" + second_tem);
//                        in.close();

                    List<Integer> infoList = new ArrayList<>();
                    infoList.add((first_tem + second_tem)/2);
                    //TODO 上传温度信息，当报错时需提醒用户新建车辆
                    HttpRequest.getInfo(driver_id, rfid_id, infoList, "updateDriverCarTem.do", UPDATATEM, DriverTemService.this);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            /*//可获取socket数据
            while(runThread){
                Socket socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress("study.twdk.net", 50001);
                try {
                    socket.connect(socketAddress, 10000);
                    socket.setSoTimeout(50000);
                    if (socket.isConnected()){
                        android.util.Log.d(TAG, "onClick: 连接socket端口");
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        android.util.Log.d(TAG, "onClick: 获取in数据");

                        Thread.sleep(10000);
                        String getRead = in.readLine();
//                        String getRead = "$GT: 1. 1. 1. 1,22.771629,N,113.881632,E,V,272,273,00,A*0F";

                        getRead = getRead.replace(" ", "");

                        Long rfid_id = Long.parseLong(getRead.substring(4,11).replace(".", ""));
                        String tem_all = getRead.substring(39,46);
                        int first_tem = Integer.parseInt(tem_all.substring(0, 2));
                        int second_tem = Integer.parseInt(tem_all.substring(4, 6));

                        android.util.Log.d(TAG, "onClick: 连接成功" + getRead);
                        android.util.Log.d(TAG, "onClick: 连接成功" + rfid_id);
                        android.util.Log.d(TAG, "onClick: 连接成功" + tem_all);
                        android.util.Log.d(TAG, "onClick: 连接成功" + first_tem);
                        android.util.Log.d(TAG, "onClick: 连接成功" + second_tem);
                        mBuilder.setContentTitle("smart_monitor")
                                //设置内容
                                .setContentText("成功获取温度数据：" + (first_tem + second_tem)/2)
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

                        List<Integer> infoList = new ArrayList<>();
                        infoList.add((first_tem + second_tem)/2);
                        //TODO 上传温度信息，当报错时需提醒用户新建车辆
                        HttpRequest.getInfo(driver_id, rfid_id, infoList, "updateDriverCarTem.do", UPDATATEM, DriverTemService.this);
//                        in.close();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }*/

//            while (runThread){
//                try {
//                    if (times > 3){
//                        car_tem = car_tem == 0 ? 10 : 0;
//                        times = 0;
//                    }
//                    times++;
//                    List<Integer> infoList = new ArrayList<>();
//                    infoList.add(car_tem);
//                    //TODO 上传温度信息，当报错时需提醒用户新建车辆
//                    HttpRequest.getInfo(driver_id, infoList, "queryDriverCarTem.do", UPDATATEM, DriverTemService.this);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
            super.run();

        }
    }
}

