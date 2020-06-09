package com.example.smart_monitor.view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart_monitor.R;
import com.example.smart_monitor.model.CarTems;
import com.example.smart_monitor.model.Driver;

import zuo.biao.library.base.BaseView;
import zuo.biao.library.util.StringUtil;

public class TemView extends BaseView<CarTems> {
    private static final String TAG = "TemView";

    public TemView(Activity context, ViewGroup parent) {
        super(context, R.layout.tem_view, parent);  //TODO demo_view改为你所需要的layout文件
    }


    private ImageView ivTemViewHead;
    private TextView tvCarTem;
    @Override
    public View createView() {

        ivTemViewHead = findView(R.id.ivTemViewHead);
        tvCarTem = findView(R.id.tvCarTem);

        return super.createView();
    }

    @Override
    public void bindView(CarTems data_){
        super.bindView(data_ != null ? data_ : new CarTems());

        itemView.setBackgroundResource(selected ? R.drawable.alpha3 : R.drawable.white_to_alpha);

        tvCarTem.setText(StringUtil.getTrimedString(data.getTem()));
    }


    //示例代码>>>>>>>>>>>>>>>>


}

