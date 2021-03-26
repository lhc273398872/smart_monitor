package com.example.smart_monitor.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.smart_monitor.application.ItemApplication;
import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.model.Order;
import com.example.smart_monitor.model.User;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.HttpManager;
import zuo.biao.library.model.Parameter;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

public class HttpRequest {
    private static final String TAG = "HttpRequest";

    //重写URL_BASE方法，此处需为ip地址，即在SettingUtil中修改URL_SERVER_ADDRESS_NORMAL_HTTPS常量
//    public static final String URL_BASE = SettingUtil.getCurrentServerAddress();
    //http://10.34.10.5
    //http://192.168.137.1    宽带热点ip
    //192.168.43.210       手机热点ip
    //192.168.137.122       舍友宽带ip
    public static final String URL_BASE = "http://192.168.0.7:8001/smart_monitor";
    public static final String PAGE_NUM = "pageNum";
    public static final String JSONSTR = "jsonStr";
    public static final String ListJSONSTR = "listJsonStr";
    public static final String UPDATALISTJSONSTR = "updataListJsonStr";
    public static final String DELETELISTJSONSTR = "deleteListJsonStr";


    //info<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public static final String ID = "id";

    //order<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public static final String ORDER_ID = "order_id";
    public static final String FIREST_ID = "first_id";
    public static final String SECOND_ID = "second_id";

    //goods<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public static final String RANGE = "range";

    public static final String ADMIN_ID = "admin_id";
    public static final String HOUSE_ID = "house_id";
    public static final String GOODS_ID = "goods_id";
    public static final String CURRENT_GOODS_ID = "currentGoods_id";

    //user<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public static final String TEL = "tel";
    public static final String PSWD = "pswd";
    /**
     * 判断用户类型
     * 0：公司管理员
     * 1：司机
     */
    public static final String STATE = "state";

    //account<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    /**
     * 注册（公司管理员和司机）
     * @param phone
     * @param password
     * @param registerState
     *          通过数值来判断用户类型：
     *          0：公司管理员
     *          1：司机
     * @param requestCode
     * @param listener
     */
    public static void register(final String phone, final String password,
                                final int registerState, final int requestCode,
                                final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>();
        request.put(TEL, phone);
        request.put(PSWD, password);
        request.put(STATE, registerState);

        HttpManager.getInstance().post(request, URL_BASE + "/register.do", requestCode, listener);
    }

    /**
     * 登录（公司管理员和司机）
     * @param phone
     * @param password
     *          通过数值来判断用户类型：
     *          0：公司管理员
     *          1：司机
     * @param requestCode
     * @param listener
     */
    public static void login(final String phone, final String password,
                                  final int requestCode, final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>();

        request.put(TEL, phone);
        request.put(PSWD, password);
        HttpManager.getInstance().post(request, URL_BASE + "/login.do", requestCode, listener);
    }

    public static void getLoginUser(String tel, String doUrl,
                                    final int requestCode, final OnHttpResponseListener listener){
        Map<String, Object> request = new HashMap<>();
        request.put(TEL, tel);

        //通过post方法获取数据库中的用户信息
        HttpManager.getInstance().post(request, URL_BASE + "/" + doUrl, requestCode, listener);
    }
    //account>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //更新>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public static <T> void updataInfo(List<T> infoList, String doUrl,
                                   final int requestCode, final OnHttpResponseListener listener){
        Map<String, Object> request = new HashMap<>();
        String JSONList = JSONArray.toJSONString(infoList);
        request.put(JSONSTR, JSONList);
        HttpManager.getInstance().post(request, URL_BASE + "/" + doUrl, requestCode, listener);
    }

    public static <T> void updateInfo(T infoClass, String doUrl,
                                   final int requestCode, final OnHttpResponseListener listener){
        String jsonStr = JSONObject.toJSONString(infoClass);
        Map<String, Object> request = new HashMap<>();
        request.put(JSONSTR, jsonStr);
        HttpManager.getInstance().post(request, URL_BASE + "/" + doUrl, requestCode, listener);
    }

    public static void updateOrderList(List<Goods> orderGoodsList, List<Goods> updataGoodsList, Order order, String doUrl,
                                       final int requestCode, final OnHttpResponseListener listener){
        String goodsListJsonStr = JSONArray.toJSONString(orderGoodsList);
        String updataListJsonStr = JSONArray.toJSONString(updataGoodsList);
        String orderJsonStr = JSONObject.toJSONString(order);

        Map<String, Object> request = new HashMap<>();
        request.put(UPDATALISTJSONSTR, updataListJsonStr);
        request.put(ListJSONSTR, goodsListJsonStr);
        request.put(JSONSTR, orderJsonStr);

        HttpManager.getInstance().post(request, URL_BASE + "/" + doUrl, requestCode, listener);
    }
    //更新>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //get>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public static void getInfo(String doUrl,
                               final int requestCode, final OnHttpResponseListener listener){
        Map<String, Object> request = new HashMap<>();
        HttpManager.getInstance().post(request, URL_BASE + "/" + doUrl, requestCode, listener);
    }

    public static void getInfo(long id, String doUrl,
                               final int requestCode, final OnHttpResponseListener listener){
        Map<String, Object> request = new HashMap<>();
        request.put(ID, id);
        HttpManager.getInstance().post(request, URL_BASE + "/" + doUrl, requestCode, listener);
    }

    public static <T> void getInfo(long id, List<T> infoList, String doUrl,
                                   final int requestCode, final OnHttpResponseListener listener){
        Map<String, Object> request = new HashMap<>();
        String JSONList = JSONArray.toJSONString(infoList);
        request.put(JSONSTR, JSONList);
        request.put(ID, id);
        HttpManager.getInstance().post(request, URL_BASE + "/" + doUrl, requestCode, listener);
    }

    public static <T> void getInfo(long first_id, long second_id,  List<T> infoList, String doUrl,
                                   final int requestCode, final OnHttpResponseListener listener){
        Map<String, Object> request = new HashMap<>();
        String JSONList = JSONArray.toJSONString(infoList);
        request.put(JSONSTR, JSONList);
        request.put(FIREST_ID, first_id);
        request.put(SECOND_ID, second_id);
        HttpManager.getInstance().post(request, URL_BASE + "/" + doUrl, requestCode, listener);
    }

    //通用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //gps>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * 通过百度提供的url来将经纬度（百度标准）转化为地理信息
     * private static final String locationUrl =
     *             "http://api.map.baidu
     *             .com/reverse_geocoding/v3/?ak=1jHLIzMhEkqWGpgbDHOtyvLPZyEEtOGA&" +
     *                     "mcode=2C:77:D9:40:2A:22:ED:BC:D8:6A:F0:01:DB:17:A6:E5:6F:71:73:59;com
     *                     .example.smart_monitor&" +
     *                     "output=json&coordtype=bd09ll&location=";
     * @param latitude
     * @param longitude
     * @param url
     * @param requestCode
     * @param listener
     */
    public static void gpsToAddress(double latitude, double longitude, String url,
                                    final int requestCode, final OnHttpResponseListener listener){
        Map<String, Object> request = new HashMap<>();
        HttpManager.getInstance().get(request, url + latitude + "," + longitude, requestCode, listener);
    }

    //gps>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //order>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 通过first_id和second_id获取货物的信息
     *      货物列表
     *      货物信息
     *      车辆对应gps信息
     * @param first_id
     *      order_id和driver_id
     * @param second_id
     *      admin_id和goods_id
     * @param requestCode
     * @param listener
     */
    public static void getOrderInfo(long first_id, long second_id, String doUrl, final int requestCode, final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>();
        //常量已重写
        request.put(FIREST_ID, first_id);
        request.put(SECOND_ID, second_id);

        //完成eclipse中的应用服务器，通过post方法及goodsId获取数据库中的货物数据
        HttpManager.getInstance().post(request, URL_BASE + "/" +
                doUrl, requestCode, listener);
    }
    //order>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //goods>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //TODO * 完善goods对应用服务器的访问

    /**
     * 获取该公司用户所拥有的仓库信息
     * @param admin_id
     * @param requestCode
     * @param listener
     */
    public static void getHousesList(long admin_id, final int requestCode, final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>();
        //此处通过ADMIN_ID来获取仓库列表
        request.put(ADMIN_ID, admin_id);

        //完成eclipse中的应用服务器，通过post方法及goodsId获取/goods/list中的货物数据
        HttpManager.getInstance().post(request, URL_BASE + "/queryMoreHouses.do", requestCode, listener);
    }


//    /**
//     * 向数据库中插入货物信息
//     * @param goods
//     * @param requestCode
//     * @param listener
//     */
//    public static void addGood(Goods goods,final int requestCode, final OnHttpResponseListener listener) {
//        String jsonStr = JSONObject.toJSONString(goods);
//        Map<String, Object> request = new HashMap<>();
//        request.put(JSONSTR, jsonStr);
//        HttpManager.getInstance().post(request, URL_BASE + "/insertSimpleGood.do", requestCode, listener);
//    }

    /**
     * 向数据库中更新货物信息
     * @param goods
     * @param requestCode
     * @param listener
     */
    public static void updateGood(Goods goods,final int requestCode, final OnHttpResponseListener listener) {
        String jsonStr = JSONObject.toJSONString(goods);
        Map<String, Object> request = new HashMap<>();
        request.put(JSONSTR, jsonStr);
        HttpManager.getInstance().post(request, URL_BASE + "/updateSimpleGood.do", requestCode, listener);
    }

    /**获取货物
     * @param goodsId
     * @param requestCode
     * @param listener
     */
    public static void getGood(long goodsId, final int requestCode, final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>();
        //常量已重写
        //TODO *** 此处的CURRENT_GOODS_ID暂无用处
//        request.put(CURRENT_GOODS_ID, ItemApplication.getInstance().getCurrentGoodsId());
        request.put(GOODS_ID, goodsId);

        //完成eclipse中的应用服务器，通过post方法及goodsId获取数据库中的货物数据
        HttpManager.getInstance().post(request, URL_BASE + "/querySimpleGood.do", requestCode, listener);
    }

    public static final int ITEM_LIST_RANGE_ALL = 0;
    public static final int ITEM_LIST_RANGE_RECOMMEND = 1;
    public static final int ITEM_LIST_HTTP = 2;

    /**获取货物列表
     * @param house_id
     * 此处的admin_user可通过MainActivity中的admin_user静态变量获取
     * @param requestCode
     * @param listener
     */
    public static void getGoodsList(long house_id, final int requestCode, final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>();
        //此处通过ADMIN_ID来获取货物列表
        //获取一个全局静态变量User来获取该id
        //已重写ADMIN_ID变量
        request.put(HOUSE_ID, house_id);
//        request.put(CURRENT_GOODS_ID, ItemApplication.getInstance().getCurrentGoodsId());
//        request.put(RANGE, range);
//        request.put(PAGE_NUM, pageNum);

        //完成eclipse中的应用服务器，通过post方法及goodsId获取/goods/list中的货物数据
        HttpManager.getInstance().post(request, URL_BASE + "/queryMoreGoods.do", requestCode, listener);
    }


    //goods>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //driver>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //TODO * 完善driver对应用服务器的访问

    /**获取司机列表
     * 此处的admin_user可通过MainActivity中的admin_user静态变量获取
     * @param requestCode
     * @param listener
     */
    public static void getDriversList(int isRunning, final int requestCode, final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>();
        request.put("isRunning",isRunning);

        //完成eclipse中的应用服务器，通过post方法获取数据库中的司机数据
        HttpManager.getInstance().post(request, URL_BASE + "/queryMoreDrivers.do", requestCode, listener);
    }

    public static void getDriverOrders(long id, int order_state, final int requestCode, final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>();
        request.put("id",id);
        request.put("order_state", order_state);
        Log.d("HttpRequest order_state:","HttpRequest order_state:" + order_state);
        //完成eclipse中的应用服务器，通过post方法获取数据库中的司机数据
        HttpManager.getInstance().post(request, URL_BASE + "/queryDriverOrder.do", requestCode, listener);
    }

    //driver>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //adminUser>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    public static void getAdminUser(String tel, final int requestCode, final OnHttpResponseListener listener){
        Map<String, Object> request = new HashMap<>();
        request.put(TEL, tel);

        //通过post方法获取数据库中的用户信息
        HttpManager.getInstance().post(request, URL_BASE + "/queryAdminUser.do", requestCode, listener);
    }

    //adminUser>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //示例代码>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    /**添加请求参数，value为空时不添加，最快在 19.0 删除
     * @param list
     * @param key
     * @param value
     */
    @Deprecated
    public static void addExistParameter(List<Parameter> list, String key, Object value) {
        if (list == null) {
            list = new ArrayList<Parameter>();
        }
        if (StringUtil.isNotEmpty(key, true) && StringUtil.isNotEmpty(value, true) ) {
            list.add(new Parameter(key, value));
        }
    }


}
