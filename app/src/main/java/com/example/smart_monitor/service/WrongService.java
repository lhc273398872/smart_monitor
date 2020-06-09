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

public class WrongService extends Service implements OnHttpResponseListener{

    public static boolean runThread = true;
    private long driver_id;


    public WrongService() {
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

        NotificationManager notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        /**
         *  实例化通知栏构造器
         */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        SendMessage sendMessage = new SendMessage();
        sendMessage.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("WrongService", "onDestroy");
        runThread = false;
        System.exit(0);
        super.onDestroy();
    }

    private int test = 20000;
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
//                    HttpRequest.getInfo("queryDriverOrder.do", 1, WrongService.this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            super.run();

        }
    }

}

