package com.example.smart_monitor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.example.smart_monitor.R;
import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.SettingUtil;

/**设置界面Activity
 * @author Lemon
 * @use toActivity(SettingActivity.createIntent(...));
 */
//实现 布局文件setting_activity
public class SettingActivity extends BaseActivity implements OnBottomDragListener {
    private static final String TAG = "SettingActivity";

    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static Intent createIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity, this);

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        initEvent();
        //功能归类分区方法，必须调用>>>>>>>>>>

    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private ImageView[] ivSettings;
    @Override
    public void initView() {//必须调用

        ivSettings = new ImageView[7];
        ivSettings[0] = findView(R.id.ivSettingCache);
        ivSettings[1] = findView(R.id.ivSettingPreload);

        ivSettings[2] = findView(R.id.ivSettingVoice);
        ivSettings[3] = findView(R.id.ivSettingVibrate);
        ivSettings[4] = findView(R.id.ivSettingNoDisturb);

        ivSettings[5] = findView(R.id.ivSettingTestMode);
        ivSettings[6] = findView(R.id.ivSettingFirstStart);

    }

    private boolean[] settings;
    private int[] switchResIds = new int[]{R.drawable.off, R.drawable.on};
    /**设置开关
     * @param which
     * @param isToOn
     */
    private void setSwitch(int which, boolean isToOn) {
        if (ivSettings == null || which < 0 || which >= ivSettings.length) {
            Log.e(TAG, "ivSettings == null || which < 0 || which >= ivSettings.length >> reutrn;");
            return;
        }

        ivSettings[which].setImageResource(switchResIds[isToOn ? 1 : 0]);
        settings[which] = isToOn;
    }





    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initData() {//必须调用

        showProgressDialog(R.string.loading);

        runThread(TAG + "initData", new Runnable() {

            @Override
            public void run() {

                settings = SettingUtil.getAllBooleans(context);
                runUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dismissProgressDialog();
                        if (settings == null || settings.length <= 0) {
                            finish();
                            return;
                        }
                        for (int i = 0; i < settings.length; i++) {
                            setSwitch(i, settings[i]);
                        }
                    }
                });
            }
        });


    }



    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用

        for (int i = 0; i < ivSettings.length; i++) {
            final int which = i;
            ivSettings[which].setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    isSettingChanged = true;
                    setSwitch(which, ! settings[which]);
                }
            });
        }
    }

    @Override
    public void onDragBottom(boolean rightToLeft) {
        if (rightToLeft) {
            SettingUtil.restoreDefault();
            initData();
            return;
        }

        finish();
    }


    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private boolean isSettingChanged = false;
    @Override
    public void finish() {
        if (isSettingChanged) {
            showProgressDialog("正在保存设置，请稍后...");
            runThread(TAG, new Runnable() {

                @Override
                public void run() {

                    SettingUtil.putAllBoolean(settings);
                    isSettingChanged = false;
                    runUiThread(new Runnable() {

                        @Override
                        public void run() {
                            SettingActivity.this.finish();
                        }
                    });
                }
            });
            return;
        }

        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ivSettings = null;
        settings = null;
    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>







    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}

