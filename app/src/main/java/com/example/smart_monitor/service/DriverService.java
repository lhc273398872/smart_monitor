package com.example.smart_monitor.service;

import com.alibaba.fastjson.JSON;
import com.example.smart_monitor.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.activity.WelcomeActivity;
import com.example.smart_monitor.util.HttpRequest;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;

public class DriverService extends Service implements OnHttpResponseListener{

    public static boolean runThread = true;
    private long driver_id;

    public DriverService() {
    }

    @Override
    public void onCreate() {
        Log.e("DriverService", "onCreate");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("DriverService", "onBind");
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化
//        messageNotification = new Notification();
//        messageNotification.tickerText = "新消息";
//        messageNotification.defaults = Notification.DEFAULT_SOUND;
//        messageNotification.icon =
//        messageNotificatioManager = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
//
//        messageIntent = new Intent(this, WelcomeActivity.class);
//        messagePendingIntent = PendingIntent.getActivity(this,0,messageIntent,0);
//
        Log.e("DriverService", "开启进程");
        //开启线程
//        messageThread = new MessageThread();
//        messageThread.isRunning = true;
//        messageThread.start();
//        HttpRequest.getInfo(driver_id, "queryDriverOrder.do", 1 , DriverService.this);
        //TODO * 完成访问数据库逻辑
        driver_id = intent.getLongExtra("driver_id", -1);

        NotificationManager notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        /**
         *  实例化通知栏构造器
         */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        /**
         *  设置Builder
         */
        //设置标题
        mBuilder.setContentTitle("我是标题")
                //设置内容
                .setContentText("我是内容")
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                //首次进入时显示效果
                .setTicker("我是测试内容")
                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                .setDefaults(Notification.DEFAULT_SOUND);
        //发送通知请求
        notificationManager.notify(10, mBuilder.build());

        SendMessage sendMessage = new SendMessage();
        sendMessage.start();

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
                        .setContentText("您有新的信息" + driver_id)
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
                notificationManager.notify(test, mBuilder.build());
                break;
        }
    }

    class SendMessage extends Thread {
        @Override
        public void run() {
            runThread = true;
            while (runThread){
                try {
                    Thread.sleep(10000);
                    //TODO * 通过某服务器方法来判断是否有新数据需加载
//                    HttpRequest.getInfo("queryDriverOrder.do", 1, DriverService.this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            super.run();

        }
    }

}

