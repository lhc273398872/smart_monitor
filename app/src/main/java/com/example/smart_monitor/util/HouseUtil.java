package com.example.smart_monitor.util;

import com.example.smart_monitor.model.Goods;
import com.example.smart_monitor.model.SaveHouse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**仅测试用，图片地址可能会失效
 * @author Lemon
 */
 //TODO * 获取Item列表：货物列表
public class HouseUtil {

    public static List<SaveHouse> getGoodsList() {
        return getGoodsList(0);
    }
    /**
     * @param page 页码
     * @return
     */
    public static List<SaveHouse> getGoodsList(int page) {
        return getGoodsList(page, 10);
    }
    /**
     * @param page 页码
     * @param count 最大一页数量
     * @return
     */
    public static List<SaveHouse> getGoodsList(int page, int count) {
        List<SaveHouse> list = new ArrayList<SaveHouse>();
        long goodsId;
        SaveHouse goods;
        int length = (count <= 0 || count > URLS.length ? URLS.length : count);
        int index;
        for (int i = 0; i < length ; i++) {
            goodsId = i + page*length + 1;
            index = i + page*length;
            while (index >= URLS.length) {
                index -= URLS.length;
            }
            if (index < 0) {
                index = 0;
            }
            //TODO 货物列表赋值
            goods = new SaveHouse();
            goods.setHouse_id(goodsId);
            goods.setHouse_name("Name" + goodsId);
            list.add(goods);
        }
        return list;
    }

    /**
     * 图片地址，仅供测试用
     */
    public static String[] URLS = {
            "http://static.oschina.net/uploads/user/1218/2437072_100.jpg?t=1461076033000",
            "http://common.cnblogs.com/images/icon_weibo_24.png",
            "http://static.oschina.net/uploads/user/585/1170143_50.jpg?t=1390226446000",
            "http://static.oschina.net/uploads/user/1174/2348263_50.png?t=1439773471000",
            "http://common.cnblogs.com/images/wechat.png",
            "http://static.oschina.net/uploads/user/998/1997902_50.jpg?t=1407806577000",
            "http://static.oschina.net/uploads/user/1/3064_50.jpg?t=1449566001000",
            "http://static.oschina.net/uploads/user/51/102723_50.jpg?t=1449212504000",
            "http://static.oschina.net/uploads/user/48/96331_50.jpg",
            "http://static.oschina.net/uploads/user/48/97721_50.jpg?t=1451544779000",
            "http://static.oschina.net/uploads/user/48/96289_50.jpg?t=1452751699000",
            "http://static.oschina.net/uploads/user/19/39085_50.jpg",
            "http://static.oschina.net/uploads/user/1332/2664107_50.jpg?t=1457405500000",
            "http://static.oschina.net/uploads/user/1385/2770216_50.jpg?t=1464405516000",
            "http://static.oschina.net/uploads/user/427/855532_50.jpg?t=1435030876000",
            "http://static.oschina.net/uploads/user/629/1258821_50.jpg?t=1378063141000",
            "http://static.oschina.net/uploads/user/1200/2400261_50.png?t=1439638750000",
            "http://pic.cnblogs.com/face/u373473.jpg?id=07231933",
            "http://pic.cnblogs.com/face/221462/20131226172100.png"
    };

    public static String getPicture(int index) {
        return index < 0 || index >= URLS.length ? null : URLS[index];
    }

    public static void clear(){

    }

    public static Object cloneObjBySerialization(Serializable src)
    {
        Object dest = null;
        try
        {
            ByteArrayOutputStream bos = null;
            ObjectOutputStream oos = null;
            try
            {
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(src);
                oos.flush();
            }
            finally
            {
                oos.close();
            }
            byte[] bytes = bos.toByteArray();
            ObjectInputStream ois = null;
            try
            {
                ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                dest = ois.readObject();
            }
            finally
            {
                ois.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();//克隆失败
        }
        return dest;
    }
}
