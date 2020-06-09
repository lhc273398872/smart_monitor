package com.example.smart_monitor.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import com.example.smart_monitor.R;
import com.example.smart_monitor.fragment.ItemListFragment;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.util.HttpRequest;
import com.example.smart_monitor.util.ItemUtil;
import com.example.smart_monitor.util.MenuUtil;
import com.example.smart_monitor.view.ItemView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.base.BaseModel;
import zuo.biao.library.base.BaseView.OnDataChangedListener;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.CacheManager;
import zuo.biao.library.manager.HttpManager;
import zuo.biao.library.ui.BottomMenuView;
import zuo.biao.library.ui.BottomMenuView.OnBottomMenuItemClickListener;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.ui.EditTextInfoActivity;
import zuo.biao.library.ui.EditTextInfoWindow;
import zuo.biao.library.ui.TextClearSuit;
import zuo.biao.library.util.CommonUtil;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

/**联系人资料界面
 * @author Lemon
 */
 //TODO 重写 布局item_activity、MenuUtil、QRCodeActivity(底端调用)
public class ItemActivity extends BaseActivity implements OnClickListener, OnBottomDragListener,
        OnHttpResponseListener {
    public static final String TAG = "ItemActivity";

    public static String HOUSEID = "House_id";
    public static String ORDERID = "ORDERID";
    public static String ONLYREAD = "ONLYREAD";

    private final int GETGOODS = 1;
    private final int ADDGOODS = 2;
    private final int UPDATEGOODS = 3;
    //启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**获取启动ItemActivity的intent
     * @param context
     * @param goodsId
     * @return
     */

    public static Intent createIntent(Context context, long houseId, long goodsId) {
        return new Intent(context, ItemActivity.class).
                putExtra(INTENT_ID, goodsId).
                putExtra(HOUSEID, houseId);
    }

    public static Intent createIntent(Context context, long orderId, long goodsId, boolean onlyRead) {
        return new Intent(context, ItemActivity.class).
                putExtra(ORDERID, orderId).
                putExtra(INTENT_ID, goodsId).
                putExtra(ONLYREAD, onlyRead);
    }

    //启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private long orderId = -1;
    private long goodsId = -1;
    private long houseId = -1;
    private boolean onlyRead = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_activity, this);

        intent = getIntent();
        orderId = intent.getLongExtra(ORDERID, orderId);
        goodsId = intent.getLongExtra(INTENT_ID, goodsId);
        houseId = intent.getLongExtra(HOUSEID, houseId);

        onlyRead = intent.getBooleanExtra(ONLYREAD, onlyRead);
        //goodsId=0 时，为添加新货物
        if (goodsId < 0) {
            finishWithError("货物不存在！");
            return;
        }

        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();

        if (!onlyRead){
            initEvent();
        }
        //功能归类分区方法，必须调用>>>>>>>>>>
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //	private BaseViewLayout<User> bvlUser;//方式一
    //	private UserViewLayout uvlUser;//方式二

    private static final String[] TOPBAR_TAG_NAMES = {"冷冻运输（-18~-22）", "冷藏运输（0~7）", "恒温运输（18~22）"};

    private ViewGroup llUserBusinessCardContainer;//方式三
    private ViewGroup llUserBottomMenuContainer;
    private ViewGroup llItemTopRightButton;

    private ItemView itemView;

    private TextView etGoodsRemark;
    private TextView tvGoodsTag;
    private TextView tvGoodsWeight;
    private TextView tvGoodsName;
    private TextView tvGoodsNumber;

    private ImageView small_add;
    private TextView small_over;

    @Override
    public void initView() {//必须调用

        //添加用户名片，这些方式都可<<<<<<<<<<<<<<<<<<<<<<
        //		//方式一
        //		bvlUser = findView(R.id.bvlUser);
        //		bvlUser.createView(new UserView(context, getResources()));
        //
        //		//方式二
        //		uvlUser = findView(R.id.uvlUser);

        //TODO 用户名片修改
        //方式三
        llUserBusinessCardContainer = findView(R.id.llUserBusinessCardContainer);
        llUserBusinessCardContainer.removeAllViews();

        //添加用户名片
        itemView = new ItemView(context, null, false);
        llUserBusinessCardContainer.addView(itemView.createView());

        llItemTopRightButton = findView(R.id.llItemTabTopRightButtonContainer);

        etGoodsRemark = findView(R.id.etGoodsRemark);
        tvGoodsTag = findView(R.id.tvGoodsTag);
        tvGoodsWeight = findView(R.id.tvGoodsWeight);
        tvGoodsName = findView(R.id.tvGoodsName);
        tvGoodsNumber = findView(R.id.tvGoodsNumber);

        //右上按钮设置
        //添加按钮设置
        small_add = new ImageView(this);
        small_add.setImageResource(R.drawable.add_small);
        small_add.setOnClickListener(new AddOnclick());

        //修改按钮设置
        small_over = new TextView(this);
        small_over.setText("确认修改");
        small_over.setOnClickListener(new OverOnclick());
        small_over.setVisibility(View.GONE);

        llItemTopRightButton.removeAllViews();
        if (goodsId == 0){
            llItemTopRightButton.addView(small_add);
        } else {
            llItemTopRightButton.addView(small_over);
        }

        //添加底部菜单<<<<<<<<<<<<<<<<<<<<<<
        llUserBottomMenuContainer = findView(R.id.llUserBottomMenuContainer);
        llUserBottomMenuContainer.removeAllViews();

        //添加底部菜单>>>>>>>>>>>>>>>>>>>>>>>

    }

    private Goods goods;
    private Goods old_goods;
    /**显示用户
     * @param goods_
     */
    private void setGoods(Goods goods_) {
        this.goods = goods_;
        if (goods == null) {
            Log.w(TAG, "setGoods  goods == null >> goods = new Goods();");
            goods = new Goods();
        }
        if (old_goods == null){
            old_goods = (Goods) ItemUtil.cloneObjBySerialization(goods);
        }

        runUiThread(new Runnable() {

            @Override
            public void run() {
                //				bvlUser.bindView(user);//方式一
                //				uvlUser.bindView(user);//方式二
                itemView.bindView(goods);//方式三
                etGoodsRemark.setText(StringUtil.getTrimedString(goods.getGoods_remark()));
                //货物类型 0：冷冻运输（-18~-22） 1：冷藏运输（0~7） 2：恒温运输（18~22）
                tvGoodsTag.setText(getStringTag(goods.getGoods_tag()));
                //货物名称
                tvGoodsName.setText(StringUtil.getTrimedString(goods.getGoods_name()));
                //货物单重
                tvGoodsWeight.setText(StringUtil.getTrimedString(goods.getGoods_weight()));
                //货物数量
                tvGoodsNumber.setText(StringUtil.getTrimedString(goods.getGoods_number()));
            }
        });
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    @Override
    public void initData() {//必须调用

        //未知
        //bottomMenuView.bindView(MenuUtil.getMenuList(MenuUtil.USER));
        old_goods = null;
        if(goodsId == 0){
            goods = new Goods();
            goods.setGoods_tag(0);
            //TODO * 此仓库id需改为通过某点击方式来获取
            goods.setHouse_id(houseId);
            setGoods(goods);
        } else {
            runThread(TAG + "initData", new Runnable() {
                @Override
                public void run() {
                    setGoods(CacheManager.getInstance().get(Goods.class, "" + goodsId));//先加载缓存数据，比网络请求快很多
                    if (houseId != -1){
                        HttpRequest.getGood(goodsId, GETGOODS, ItemActivity.this);
                    } else if (orderId != -1 && goodsId != -1){
                        //TODO * 获取订单中的货物信息
                        HttpRequest.getOrderInfo(orderId, goodsId, "querySimpleOrdersGood.do", GETGOODS,ItemActivity.this);
                    }
                }
            });
        }
    }

    private String getStringTag(int position) {
        switch (position){
            case 0:
                return "冷冻运输（-18~-22）";
            case 1:
                return "冷藏运输（0~7）";
            default:
                return "恒温运输（18~22）";
        }
    }

    //设置返回值
    protected void setResult() {
        intent = new Intent()
                .putExtra(RESULT_DATA, "" + goods.getGoods_id());
        setResult(RESULT_OK, intent);
    }
    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    public void initEvent() {//必须调用

        findView(R.id.llGoodsTag).setOnClickListener(this);

        tvGoodsWeight.setOnClickListener(this);
        tvGoodsName.setOnClickListener(this);
        tvGoodsNumber.setOnClickListener(this);

        new TextClearSuit().addClearListener(etGoodsRemark, findView(R.id.ivGoodsRemarkClear));//清空备注按钮点击监听

        itemView.setOnDataChangedListener(new OnDataChangedListener() {

            @Override
            public void onDataChanged() {
                goods = itemView.data;
            }
        });
    }



    //对应HttpRequest.getUser(userId, 0, UserActivity.this); <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        //TODO * 此处的requestCode需要以常量的形式在类开头写明
        if (e != null){
            showShortToast(R.string.get_failed);
            e.printStackTrace();
        }

        //TODO * 此处的添加和修改货物需进行判断

        switch (requestCode){
            case ADDGOODS:
                Log.d(TAG,resultJson);
                showShortToast("添加货物成功");
                setResult();
                finish();
                break;
            case UPDATEGOODS:
                showShortToast("修改货物成功");
                setResult();
                finish();
                break;
            default:
                Goods goods = null;
                try {//（若是https，不确定）如果服务器返回的json一定在最外层有个data，可以用OnHttpResponseListenerImpl解析
                    JSONObject jsonObject = JSON.parseObject(resultJson);
//            JSONObject data = jsonObject == null ? null : jsonObject.getJSONObject("data");
                    goods = JSON.parseObject(jsonObject, Goods.class);
                } catch (Exception e1) {
                    Log.e(TAG, "onHttpResponse  try { user = Json.parseObject(... >>" +
                            " } catch (JSONException e1) {\n" + e1.getMessage());
                }

                if ((goods == null || goods.getGoods_id() < 0) && e != null) {
                    showShortToast(R.string.get_failed);
                } else {
                    showShortToast(goods.toString());
                    setGoods(goods);
                }
                break;
        }

    }

    //系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @Override
    public void onDragBottom(boolean rightToLeft) {
        if (rightToLeft) {

            return;
        }
        goods = old_goods;
        setResult();
        finish();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llGoodsTag:
                //点击选择（三个选项） 0：冷冻运输（-18~-22） 1：冷藏运输（0~7） 2：恒温运输（18~22）
                //此处的GOODS_TAG用来标识返回值
                toActivity(BottomMenuWindow.createIntent(context, TOPBAR_TAG_NAMES)
                        .putExtra(BottomMenuWindow.INTENT_TITLE, "选择标签"), ALTER_GOODS_TAG, false);
                break;
            case R.id.tvGoodsWeight:
                //TODO 实现填写单重
                break;
            case R.id.tvGoodsName:
                //填写姓名
                //参数1：上下文，参数2：填写类型，参数3：key，参数4：value，参数5：包名
                toActivity(EditTextInfoWindow.createIntent(context, EditTextInfoWindow.TYPE_NAME,
                        "货物名称", StringUtil.getTrimedString(goods.getGoods_name()), getPackageName()), ALTER_GOODS_NAME, false);
                break;
            case R.id.tvGoodsNumber:
                toActivity(EditTextInfoWindow.createIntent(context, EditTextInfoWindow.TYPE_NUMBER,
                        "货物数量", StringUtil.getTrimedString(goods.getGoods_number()), getPackageName()), ALTER_GOODS_NUMBER, false);
                break;
            default:
                break;
        }
    }

    private class AddOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HttpRequest.updateInfo(goods, "/insertSimpleGood.do", ADDGOODS, ItemActivity.this);
        }
    }

    private class OverOnclick implements OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO * 更新数据库
            HttpRequest.updateGood(goods, UPDATEGOODS, ItemActivity.this);
        }
    }

    //生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private static final int ALTER_GOODS_TAG = 1;
    private static final int REQUEST_TO_EDIT_TEXT_INFO = 2;
    private static final int ALTER_GOODS_WEIGHT = 3;
    private static final int ALTER_GOODS_NAME = 4;
    private static final int ALTER_GOODS_NUMBER = 5;

    //此处的data中包含返回值，通过EditTextInfoActivity中的常量可获取
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (goods == null) {
            goods = new Goods();
        }
        switch (requestCode) {
            case ALTER_GOODS_TAG:
                //获取标签返回值
                goods.setGoods_tag(data == null ? null : data.getIntExtra(BottomMenuWindow.RESULT_ITEM_ID, 0));
                itemView.bindView(goods);
                setGoods(goods);
                break;
            case REQUEST_TO_EDIT_TEXT_INFO:
                //TODO 此处为备注点击后返回值设定，需修改
                goods.setGoods_remark(data == null ? null : data.getStringExtra(EditTextInfoActivity.RESULT_VALUE)); ;
                itemView.bindView(goods);
                setGoods(goods);
                break;
            case ALTER_GOODS_WEIGHT:
                //TODO 获取单重字符串返回值
                break;
            case ALTER_GOODS_NAME:
                //获取名称返回值
                goods.setGoods_name(data == null ? null : data.getStringExtra(EditTextInfoActivity.RESULT_VALUE));
                itemView.bindView(goods);
                setGoods(goods);
                break;
            case ALTER_GOODS_NUMBER:
                //TODO 获取数量返回值
                goods.setGoods_number(Integer.parseInt(data == null ? null : data.getStringExtra(EditTextInfoActivity.RESULT_VALUE)));
                itemView.bindView(goods);
                setGoods(goods);
                break;
        }
        small_over.setVisibility(View.VISIBLE);
    }


    @Override
    public void finish() {
        super.finish();
        if (goods != null) {
            goods.setGoods_remark(StringUtil.getTrimedString(etGoodsRemark));
            //此处缓存已经更新，需更新ui
            Intent intent = new Intent(ItemActivity.this , ItemListFragment.class);
            setResult(-1, intent);
            CacheManager.getInstance().save(Goods.class, goods, "" + goods.getGoods_id());//更新缓存
        }
    }


    //生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








    //内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



    //内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
