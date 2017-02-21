package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ChooseAccountAsyncTask;

import java.util.List;
import java.util.Map;

/**
 * Created by xiangjun on 16/1/10.
 */
//自己定义的适配器，可以根据所选的按钮来决定跳转
public class ChooseAccountAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ChooseAccountFrame thisChooseAccountFrame;
    private ViewHolder holder = null;
    private List<Map<String, Object>> mData;
    private String token;
    private String accountName;

    public ChooseAccountAdapter(ChooseAccountFrame thisChooseAccountFrame,List<Map<String, Object>> mData,String token,String accountName) {
        this.mInflater = LayoutInflater.from(thisChooseAccountFrame);
        this.thisChooseAccountFrame = thisChooseAccountFrame;
        this.mData=mData;
        this.token=token;
        this.accountName=accountName;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {

            //初始化holder的各个变量
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.account_list_item, null);
            holder.accountImage = (ImageView) convertView.findViewById(R.id.accountImage);
            holder.officialAccountName = (TextView) convertView.findViewById(R.id.officialAccountName);
            holder.officialAccountHint = (TextView) convertView.findViewById(R.id.officialAccountHint);
            holder.enterAccountBtn = (Button) convertView.findViewById(R.id.enterAccountBtn);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        //将之前自定义好的信息显示出来
        holder.accountImage.setImageBitmap((Bitmap) mData.get(position).get("officialAccountImage"));
        holder.officialAccountName.setText((String) mData.get(position).get("officialAccountName"));
        holder.officialAccountHint.setText((String) mData.get(position).get("officialAccountHint"));
        holder.officialAccountIsAuthorized = (boolean) mData.get(position).get("officialAccountIsAuthorized");
        if (holder.officialAccountIsAuthorized == false) {
            holder.enterAccountBtn.setText("已取消授权");
            holder.enterAccountBtn.setBackgroundResource(R.drawable.account_list_isnot_authorized_button_style);
            holder.enterAccountBtn.setEnabled(false);
        }

        holder.enterAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder buider=new AlertDialog.Builder(thisChooseAccountFrame);
                buider.setMessage("正在准备进入主界面，请稍候...");
                AlertDialog isEnteringMainFrameDialog=buider.create();
                isEnteringMainFrameDialog.show();


                //获取选择的账户名，并传入下一个页面
                String chosenOfficialAccount = (String) mData.get(position).get("officialAccountName");
                String chosenOfficialAccountHint = (String) mData.get(position).get("officialAccountHint");
                String chosenOfficialAccountAffliatedCareer = (String) mData.get(position).get("officialAccountAffliatedCareer");
                String chosenOfficialAccountHeadImageURl = (String) mData.get(position).get("officialAccountImageURL");
                int chosenOfficialAccountId = (int) mData.get(position).get("officialAccountId");
                String URL="http://api.wxyaoyao.com/3/user/set_account_to_token?token="+token+"&account_id="+chosenOfficialAccountId;

                //将选择的用户Id绑定到token中
                ChooseAccountAsyncTask chooseAccountAsyncTask=new ChooseAccountAsyncTask(thisChooseAccountFrame,URL,token,chosenOfficialAccount,
                        chosenOfficialAccountHint,chosenOfficialAccountAffliatedCareer,accountName,
                        chosenOfficialAccountHeadImageURl,isEnteringMainFrameDialog);
                chooseAccountAsyncTask.execute();



            }

        });


        return convertView;
    }

    //自定义holder来装按钮与其他组件
    public final class ViewHolder {
        public ImageView accountImage;
        public TextView officialAccountName;
        public TextView officialAccountHint;
        public boolean officialAccountIsAuthorized;
        public Button enterAccountBtn;
    }

}
