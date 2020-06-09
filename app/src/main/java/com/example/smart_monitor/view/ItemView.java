package com.example.smart_monitor.view;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smart_monitor.R;
import com.example.smart_monitor.model.Goods;

import java.util.HashMap;
import java.util.Map;

import zuo.biao.library.base.BaseView;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.ui.EditTextInfoWindow;
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
public class ItemView extends BaseView<Goods>
        implements OnClickListener{
    private static final String TAG = "DemoView";
    private boolean checkBoxFlag = false;
    private static Map<Long, Boolean> selectMap = new HashMap<>();
    private long goods_id = -1;
    private int ALTER_GOODS_NUMBER = 1;
    private int DELETE_GOODS = 2;
//    public boolean selected = false;

    public ItemView(Activity context, ViewGroup parent, boolean checkBoxFlag) {
        super(context, R.layout.item_view, parent);
        this.checkBoxFlag = checkBoxFlag;
    }

    private ImageView ivGoodsViewHead;

    private TextView tvGoodsViewId;
    private TextView tvGoodsViewName;
    private TextView tvGoodsViewNumber;
    private TextView tvGoodsGetNumber;

    private LinearLayout llOrderGoodsNumber;
    private LinearLayout llCheckBox;
    private LinearLayout llItem;

    public CheckBox cbItem;


    @Override
    public View createView() {

        llCheckBox = findView(R.id.llCheckBox);
        llOrderGoodsNumber = findView(R.id.llOrderGoodsNumber);

        if (checkBoxFlag){
            llCheckBox.setVisibility(View.VISIBLE);
            llOrderGoodsNumber.setVisibility(View.VISIBLE);
        } else {
            llCheckBox.setVisibility(View.GONE);
            llOrderGoodsNumber.setVisibility(View.GONE);
        }

        llItem = findView(R.id.llItem);
        tvGoodsGetNumber = findView(R.id.tvGoodsGetNumber);
        ivGoodsViewHead = findView(R.id.ivGoodsViewHead, this);
        tvGoodsViewId = findView(R.id.tvGoodsViewId);
        tvGoodsViewName = findView(R.id.tvGoodsViewName);
        tvGoodsViewNumber = findView(R.id.tvGoodsViewNumber);
        cbItem = findView(R.id.cbItem);

//        evGoodsGetNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                data.setSelect_number(Integer.parseInt(v.getText().toString()));
//                return false;
//            }
//        });
//        evGoodsGetNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d(TAG, "onFocusChange: ");
//            }
//        });
        tvGoodsGetNumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(context);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setText(tvGoodsGetNumber.getText());

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("请输入您的内容");
                dialog.setView(editText);
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int select_number = Integer.parseInt(editText.getText().toString());

                        if (select_number > data.getGoods_number()){
                            showShortToast("订单物品数量不能大于物品总量");
                        } else {
                            tvGoodsGetNumber.setText(StringUtil.getTrimedString(select_number));
                            data.setSelect_number(select_number);
                        }
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });

        cbItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbItem.isChecked()){
                    data.setGoods_select(true);

                    if (data.getSelect_number() == 0){
                        tvGoodsGetNumber.setText(StringUtil.getTrimedString(data.getGoods_number()));
                        data.setSelect_number(data.getGoods_number());
                    }

                } else {
                    data.setGoods_select(false);

                    if (data.getSelect_number() != 0){
                        data.setSelect_number(0);
                        tvGoodsGetNumber.setText("0");
                    }
                }
            }
        });
//        cbItem.setOnCheckedChangeListener(null);


        return super.createView();
    }


    //TODO 修改ListView的赋值，修改为Goods
    @Override
    public void bindView(Goods data_){
        super.bindView(data_ != null ? data_ : new Goods());
        goods_id = data.getGoods_id();
        itemView.setBackgroundResource(selected ? R.drawable.alpha3 : R.drawable.white_to_alpha);

        tvGoodsViewId.setText(StringUtil.getTrimedString(data.getGoods_id()));
        tvGoodsViewName.setText(StringUtil.getTrimedString(data.getGoods_name()));
        tvGoodsViewNumber.setText(StringUtil.getTrimedString(data.getGoods_number()));
        cbItem.setChecked(data.getGoods_select());
        tvGoodsGetNumber.setText(StringUtil.getTrimedString(data.getSelect_number()));

//        if (checkBoxFlag){
//            if (selectMap.containsKey(goods_id)){
//                cbItem.setSelected(selectMap.get(goods_id));
//            } else {
//                cbItem.setSelected(false);
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        if (data == null) {
            return;
        }
        switch (v.getId()) {
            //图片按键反应
            case R.id.ivGoodsViewHead:

//                data.setKey("New " + data.getKey());
                bindView(data);
                if (onDataChangedListener != null) {
                    onDataChangedListener.onDataChanged();
                }
                break;
            default:
                break;
        }
    }


}

