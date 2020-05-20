package com.example.smart_monitor.application;

import zuo.biao.library.base.BaseApplication;
import zuo.biao.library.util.StringUtil;
import android.util.Log;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.smart_monitor.manager.ItemManager;
import com.example.smart_monitor.model.Goods;

/**Application
 * @author Lemon
 */
 //TODO * 重写ItemManager
public class ItemApplication extends BaseApplication {
    private static final String TAG = "ItemApplication";

    private static ItemApplication context;
    public static ItemApplication getInstance() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    //TODO * 修改为当前用户id，以下同理
    /**获取当前货物id
     * @return
     */
    public long getCurrentGoodsId() {
        currentGoods = getCurrentGoods();
        Log.d(TAG, "getCurrentGoodsId  currentGoodsId = " + (currentGoods == null ? "null" : currentGoods.getGoods_id()));
        return currentGoods == null ? 0 : currentGoods.getGoods_id();
    }

    //TODO 进行适当的修改
//    /**获取当前用户phone
//     * @return
//     */
//    public String getCurrentUserPhone() {
//        currentUser = getCurrentUser();
//        return currentUser == null ? null : currentUser.getPhone();
//    }


    private static Goods currentGoods = null;
    //原为getCurrentUser
    public Goods getCurrentGoods() {
        if (currentGoods == null) {
            currentGoods = ItemManager.getInstance().getCurrentGoods();
        }
        return currentGoods;
    }

    //原为saveCurrentUser
    public void saveCurrentGoods(Goods goods) {
        if (goods == null) {
            Log.e(TAG, "saveCurrentGoods  currentGoods == null >> return;");
            return;
        }
        if (goods.getGoods_id() <= 0 && StringUtil.isNotEmpty(goods.getGoods_name(), true) == false) {
            Log.e(TAG, "saveCurrentGoods  goods.getId() <= 0" +
                    " && StringUtil.isNotEmpty(goods.getName(), true) == false >> return;");
            return;
        }

        currentGoods = goods;
        ItemManager.getInstance().saveCurrentGoods(currentGoods);
    }

    public void logout() {
        currentGoods = null;
        ItemManager.getInstance().saveCurrentGoods(currentGoods);
    }

    /**判断是否为当前用户
     * @param userId
     * @return
     */
    //原为 isCurrentUser
    public boolean isCurrentGoods(long userId) {
        return ItemManager.getInstance().isCurrentGoods(userId);
    }

    public boolean isLoggedIn() {
        return getCurrentGoodsId() > 0;
    }



}
