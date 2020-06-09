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
import com.example.smart_monitor.R;
import com.example.smart_monitor.driver.driver_fragment.DriverOrderListFragment;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.util.HttpRequest;

import java.util.List;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.util.Log;

public class OrderService extends Service implements OnHttpResponseListener{

    public static boolean runThread = true;
    private long driver_id;
    private int order_number = 0;

    private static final int GETORDER = 1;

    public OrderService() {
    }

    @Override
    public void onCreate() {
        Log.e("OrderService", "onCreate");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("OrderService", "onBind");
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
        mBuilder.setContentTitle("信息通知模块启动")
                //设置内容
                .setContentText("已开启后台信息通知服务")
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

        SendMessage sendMessage = new SendMessage();
        sendMessage.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("OrderService", "onDestroy");
        runThread = false;
        System.exit(0);
        super.onDestroy();
    }

    private int test = 10000;
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
                    .setContentText("消息通知功能，网络连接失败")
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

        switch (requestCode){
            case GETORDER:
                //TODO 获取订单列表，判断订单列表中的货物数量
                List<Order> orderList = JSONObject.parseArray(resultJson, Order.class);

                if (orderList.size() > order_number){
                    //设置标题
                    //设置标题
                    mBuilder.setContentTitle("smart_monitor")
                            //设置内容
                            .setContentText("您有新的信息" + orderList.size())
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
                order_number = orderList.size();

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
                    //TODO * 通过某服务器方法来判断是否有新数据需加载
                    HttpRequest.getDriverOrders(driver_id, 3, GETORDER, OrderService.this);
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            super.run();

        }
    }

}

