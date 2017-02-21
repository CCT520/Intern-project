package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

import java.util.List;
import java.util.Map;

/**
 * Created by xiangjun on 15/12/22.
 */



public class EditDeviceRemarksNameAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<Map<String, Object>> mData;

    final int TYPE_1 = 0;//静态文本
    final int TYPE_2 = 1;//账号号码
    private ViewHolder1 holder1 = null;
    private ViewHolder2 holder2 = null;
    private String changedText="";//用来获取editText的内容

    public String getChangedText() {
        return changedText;
    }

    public EditDeviceRemarksNameAdapter(Context context,List<Map<String, Object>> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData=mData;
    }

    public ViewHolder2 getHolder2() {
        return holder2;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        int p = position;
        if (p == 0)//静态文本
            return TYPE_1;
        else if (p == 1)//修改备注名的输入框
            return TYPE_2;
        else
            return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        int type = getItemViewType(position);
        if (convertView == null) {
            mInflater = LayoutInflater.from(context);
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:
                    convertView = mInflater.inflate(R.layout.edit_device_remarks_name_item1,
                            parent, false);
                    holder1 = new ViewHolder1();
                    holder1.editDeviceRemarksNameStatic = (TextView) convertView
                            .findViewById(R.id.editDeviceRemarksNameStaticStyle);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = mInflater.inflate(R.layout.edit_device_remarks_name_item2,
                            parent, false);
                    holder2 = new ViewHolder2();
                    holder2.editDeviceRemarksNameText = (EditText) convertView
                            .findViewById(R.id.editDeviceRemarksNameText);
                    convertView.setTag(holder2);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_1:
                    holder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }
        // 设置资源
        switch (type) {
            case TYPE_1://静态信息
                holder1.editDeviceRemarksNameStatic.setText((String) mData.get(position).get("editDeviceRemarksNameStatic"));
                break;
            case TYPE_2:
                holder2.editDeviceRemarksNameText.setText((String) mData.get(position).get("editDeviceRemarksNameText"));
                holder2.editDeviceRemarksNameText.addTextChangedListener(new myTextwatcher());

                //下面这段代码是用于跳转到这个页面后立即弹出软键盘
                holder2.editDeviceRemarksNameText.setFocusable(true);
                holder2.editDeviceRemarksNameText.setFocusableInTouchMode(true);
                holder2.editDeviceRemarksNameText.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager)holder2.editDeviceRemarksNameText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(holder2.editDeviceRemarksNameText, 0);

                break;
        }

        return convertView;
    }

    //自定义holder来装按钮与其他组件
    public final class ViewHolder1 {
        public TextView editDeviceRemarksNameStatic;
    }

    public final class ViewHolder2 {
        public EditText editDeviceRemarksNameText;
    }

    class myTextwatcher implements TextWatcher{


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && !"".equals(s.toString())) {
                changedText=s.toString();
            }

        }
    }

}



