package com.example.smart_monitor.view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart_monitor.R;
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
public class DriverView extends BaseView<Driver> implements OnClickListener {
    private static final String TAG = "DriverView";

    public DriverView(Activity context, ViewGroup parent) {
        super(context, R.layout.driver_view, parent);  //TODO demo_view改为你所需要的layout文件
    }


    //示例代码<<<<<<<<<<<<<<<<
    public ImageView ivDemoViewHead;
    public TextView tvDemoViewName;
    public TextView tvDemoViewNumber;
    //示例代码>>>>>>>>>>>>>>>>
    @Override
    public View createView() {

        //示例代码<<<<<<<<<<<<<<<<
        ivDemoViewHead = findView(R.id.ivDemoViewHead, this);
        tvDemoViewName = findView(R.id.tvDemoViewName);
        tvDemoViewNumber = findView(R.id.tvDemoViewNumber);
        //示例代码>>>>>>>>>>>>>>>>

        return super.createView();
    }

    //TODO 司机目录数据导入
    @Override
    public void bindView(Driver data_){
        //示例代码<<<<<<<<<<<<<<<<
        super.bindView(data_ != null ? data_ : new Driver());

        itemView.setBackgroundResource(selected ? R.drawable.alpha3 : R.drawable.white_to_alpha);

        tvDemoViewName.setText(StringUtil.getTrimedString(data.getDriver_name()));
        tvDemoViewNumber.setText(StringUtil.getTrimedString(data_.getTel()));
        //示例代码>>>>>>>>>>>>>>>>
    }

    //示例代码<<<<<<<<<<<<<<<<
    @Override
    public void onClick(View v) {
        if (data == null) {
            return;
        }
        switch (v.getId()) {
            //TODO 点击头像逻辑
            case R.id.ivDemoViewHead:
//                data.setKey("New " + data.getKey());
//                bindView(data);
//                if (onDataChangedListener != null) {
//                    onDataChangedListener.onDataChanged();
//                }
                break;
            default:
                break;
        }
    }
    //示例代码>>>>>>>>>>>>>>>>


}

