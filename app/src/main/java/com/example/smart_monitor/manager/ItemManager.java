package com.example.smart_monitor.manager;

import zuo.biao.library.util.JSON;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.smart_monitor.application.ItemApplication;
import com.example.smart_monitor.model.Goods;

/**数据工具类
 * @author Lemon
 */
public class ItemManager {
    private final String TAG = "DataManager";

    private Context context;
    private ItemManager(Context context) {
        this.context = context;
    }

    private static ItemManager instance;
    public static ItemManager getInstance() {
        //TODO 未知用途
        if (instance == null) {
            synchronized (ItemManager.class) {
                if (instance == null) {
                    instance = new ItemManager(ItemApplication.getInstance());
                }
            }
        }
        return instance;
    }

    //用户 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //TODO 需修改，未知用途(大概率为key)
    private String PATH_GOODS = "PATH_GOODS";

    public final String KEY_GOODS = "KEY_GOODS";
    public final String KEY_GOODS_ID = "KEY_GOODS_ID";
    public final String KEY_GOODS_NAME = "KEY_GOODS_NAME";
    public final String KEY_GOODS_PHONE = "KEY_GOODS_PHONE";

    public final String KEY_CURRENT_GOODS_ID = "KEY_CURRENT_GOODS_ID";
    public final String KEY_LAST_GOODS_ID = "KEY_LAST_GOODS_ID";


    /**判断是否为当前用户
     * @param goodsId
     * @return
     */
    public boolean isCurrentGoods(long goodsId) {
        return goodsId > 0 && goodsId == getCurrentGoodsId();
    }

    /**获取当前用户id
     * @return
     */
    public long getCurrentGoodsId() {
        Goods goods = getCurrentGoods();
        return goods == null ? 0 : goods.getGoods_id();
    }

    /**获取当前用户的手机号
     * @return
     */
    //TODO 进行适当的修改
//    public String getCurrentUserPhone() {
//        Goods user = getCurrentGoods();
//        return user == null ? "" : user.getPhone();
//    }

    /**获取当前用户
     * @return
     */
    public Goods getCurrentGoods() {
        SharedPreferences sdf = context.getSharedPreferences(PATH_GOODS, Context.MODE_PRIVATE);
        return sdf == null ? null : getGoods(sdf.getLong(KEY_CURRENT_GOODS_ID, 0));
    }


    /**获取最后一次登录的用户的手机号
     * @return
     */
    //TODO 进行适当的修改
//    public String getLastUserPhone() {
//        Goods user = getLastUser();
//        return user == null ? "" : user.getPhone();
//    }

    //TODO 未知用途
    /**获取最后一次登录的用户
     * @return
     */
    public Goods getLastGoods() {
        SharedPreferences sdf = context.getSharedPreferences(PATH_GOODS, Context.MODE_PRIVATE);
        return sdf == null ? null : getGoods(sdf.getLong(KEY_LAST_GOODS_ID, 0));
    }

    /**获取用户
     * @param goodsId
     * @return
     */
    public Goods getGoods(long goodsId) {
        SharedPreferences sdf = context.getSharedPreferences(PATH_GOODS, Context.MODE_PRIVATE);
        if (sdf == null) {
            Log.e(TAG, "get sdf == null >>  return;");
            return null;
        }
        Log.i(TAG, "getGoods  goodsId = " + goodsId);
        return JSON.parseObject(sdf.getString(StringUtil.getTrimedString(goodsId), null), Goods.class);
    }


    /**保存当前用户,只在登录或注销时调用
     * @param goods  user == null >> user = new User();
     */
    //TODO 未知用途
    public void saveCurrentGoods(Goods goods) {
        SharedPreferences sdf = context.getSharedPreferences(PATH_GOODS, Context.MODE_PRIVATE);
        if (sdf == null) {
            Log.e(TAG, "saveUser sdf == null  >> return;");
            return;
        }
        if (goods == null) {
            Log.w(TAG, "saveUser  user == null >>  user = new User();");
            goods = new Goods();
        }
        SharedPreferences.Editor editor = sdf.edit();
        editor.remove(KEY_LAST_GOODS_ID).putLong(KEY_LAST_GOODS_ID, getCurrentGoodsId());
        editor.remove(KEY_CURRENT_GOODS_ID).putLong(KEY_CURRENT_GOODS_ID, goods.getGoods_id());
        editor.commit();

        saveGoods(sdf, goods);
    }

    /**保存用户
     * @param goods
     */
    public void saveGoods(Goods goods) {
        saveGoods(context.getSharedPreferences(PATH_GOODS, Context.MODE_PRIVATE), goods);
    }
    /**保存用户
     * @param sdf
     * @param goods
     */
    public void saveGoods(SharedPreferences sdf, Goods goods) {
        if (sdf == null || goods == null) {
            Log.e(TAG, "saveGoods sdf == null || goods == null >> return;");
            return;
        }
        String key = StringUtil.getTrimedString(goods.getGoods_id());
        Log.i(TAG, "saveGoods  key = goods.getId() = " + goods.getGoods_id());
        sdf.edit().remove(key).putString(key, JSON.toJSONString(goods)).commit();
    }

    /**删除用户
     * @param sdf
     */
    public void removeGoods(SharedPreferences sdf, long goodsId) {
        if (sdf == null) {
            Log.e(TAG, "removeUser sdf == null  >> return;");
            return;
        }
        sdf.edit().remove(StringUtil.getTrimedString(goodsId)).commit();
    }

    /**设置当前用户手机号
     * @param phone
     */
    //TODO 附加注释，需修改
//    public void setCurrentUserPhone(String phone) {
//        Goods user = getCurrentGoods();
//        if (user == null) {
//            user = new Goods();
//        }
//        user.setPhone(phone);
//        saveUser(user);
//    }

    /**设置当前用户姓名
     * @param name
     */
    public void setCurrentGoodsName(String name) {
        Goods goods = getCurrentGoods();
        if (goods == null) {
            goods = new Goods();
        }
        goods.setGoods_name(name);
        saveGoods(goods);
    }

    //用户 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>




}

