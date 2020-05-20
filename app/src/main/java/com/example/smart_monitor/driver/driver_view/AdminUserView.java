package com.example.smart_monitor.driver.driver_view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart_monitor.R;
import com.example.smart_monitor.model.AdminUser;
import com.example.smart_monitor.model.Driver;

import zuo.biao.library.base.BaseView;
import zuo.biao.library.util.StringUtil;

/** 使用方法：复制>粘贴>改名>改代码 */

/**自定义View模板，当View比较庞大复杂(解耦效果明显)或使用次数>=2(方便重用)时建议使用
 * @author Lemon
 * @use
 * <br> DemoView demoView = new DemoView(context, resources);
 * <br> adapter中使用:[具体参考.BaseViewAdapter(getView使用自定义View的写法)]
 * <br> convertView = demoView.createView(inflater);
 * <br> demoView.bindView(position, data);
 * <br> 或 其它类中使用:
 * <br> containerView.addView(demoView.createView(inflater));
 * <br> demoView.bindView(data);
 * <br> 然后
 * <br> demoView.setOnDataChangedListener(onDataChangedListener); data = demoView.getData();//非必需
 * <br> demoView.setOnClickListener(onClickListener);//非必需
 * <br> ...
 */
public class AdminUserView extends BaseView<AdminUser> implements OnClickListener {
    private static final String TAG = "AdminUserView";

    public AdminUserView(Activity context, ViewGroup parent) {
        //TODO * 修改布局文件
        super(context, R.layout.driver_view, parent);
    }

    public ImageView ivDemoViewHead;
    public TextView tvDemoViewName;
    public TextView tvDemoViewNumber;
    @Override
    public View createView() {

        ivDemoViewHead = findView(R.id.ivDemoViewHead, this);
        tvDemoViewName = findView(R.id.tvDemoViewName);
        tvDemoViewNumber = findView(R.id.tvDemoViewNumber);

        return super.createView();
    }

    @Override
    public void bindView(AdminUser data_){
        super.bindView(data_ != null ? data_ : new AdminUser());

        itemView.setBackgroundResource(selected ? R.drawable.alpha3 : R.drawable.white_to_alpha);

        tvDemoViewName.setText(StringUtil.getTrimedString(data.getAdmin_name()));
        tvDemoViewNumber.setText(StringUtil.getTrimedString(data_.getTel()));
    }

    @Override
    public void onClick(View v) {
        if (data == null) {
            return;
        }
        switch (v.getId()) {
            //TODO 点击头像逻辑
            case R.id.ivDemoViewHead:
                break;
            default:
                break;
        }
    }


}

