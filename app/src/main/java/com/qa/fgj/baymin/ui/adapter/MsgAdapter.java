package com.qa.fgj.baymin.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.model.entity.UserBean;
import com.qa.fgj.baymin.ui.activity.MainActivity;
import com.qa.fgj.baymin.util.PhotoUtils;
import com.qa.fgj.baymin.util.SystemUtil;

import java.util.List;

/**
 * 聊天列表适配器
 * Created by FangGengjia on 2017/1/17.
 */

public class MsgAdapter extends ArrayAdapter<MessageBean>
        implements View.OnClickListener, View.OnLongClickListener{

    private MainActivity mainActivity;
    private UserBean userBean;
    private int resourceId;
    private MsgItemHolder viewHolder;

    public MsgAdapter(Context context, int resource, List<MessageBean> data, UserBean user) {
        super(context, resource, data);
        mainActivity = (MainActivity) context;
        userBean = user;
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageBean messageBean = getItem(position);
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new MsgItemHolder();
            viewHolder.chatTime = (TextView) view.findViewById(R.id.chat_time);
            viewHolder.receivedContainer = (LinearLayout) view.findViewById(R.id.msg_received_container);
            viewHolder.sendContainer = (LinearLayout) view.findViewById(R.id.msg_send_container);
            viewHolder.receivedText = (TextView) view.findViewById(R.id.recevied_text);
//            viewHolder.sendState = (ImageView) view.findViewById(R.id.msg_state);
            viewHolder.sendText = (TextView) view.findViewById(R.id.send_text);
            viewHolder.userFace = (ImageView) view.findViewById(R.id.user_face_img);
            viewHolder.receivedText.setOnClickListener(this);
//            viewHolder.sendState.setOnClickListener(this);
            viewHolder.userFace.setOnClickListener(this);

            viewHolder.sendText.setOnLongClickListener(this);
            viewHolder.receivedText.setOnLongClickListener(this);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (MsgItemHolder) view.getTag();
        }
        bindData(messageBean);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.msg_state:
                //TODO 重发
//                ToastUtil.show("重发");
//                break;
            case R.id.user_face_img:
                mainActivity.startPersonalActivity();
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        //TODO PopupWindow：复制、删除、查看更多等
        switch (view.getId()){
            case R.id.recevied_text:
                SystemUtil.copyToClipBoard(getContext(), ((TextView)view).getText().toString());
                break;
            case R.id.send_text:
                SystemUtil.copyToClipBoard(getContext(), ((TextView)view).getText().toString());
            break;
        }
        return false;
    }

    private void bindData(MessageBean messageBean) {
        if (messageBean == null){
            return;
        }

        if (messageBean.shouldShowCreateTime){
            viewHolder.chatTime.setVisibility(View.VISIBLE);
            viewHolder.chatTime.setText(String.valueOf(messageBean.getCreateTime()));
        } else {
            viewHolder.chatTime.setVisibility(View.GONE);
        }

        if (messageBean.isSendMsg){
            viewHolder.receivedContainer.setVisibility(View.GONE);
            viewHolder.sendContainer.setVisibility(View.VISIBLE);
            //默认显示发送中，发送成功隐藏该控件，发送失败将原先的圆形进度条动画改为重发图片
//            if (messageBean.isSendSuccessful){
//                viewHolder.sendState.setVisibility(View.GONE);
//            } else if (!messageBean.isSending){
//                viewHolder.sendState.setVisibility(View.VISIBLE);
//                viewHolder.sendState.setImageResource(R.drawable.send_failed);
//            }
//            else {
//                viewHolder.sendState.setVisibility(View.VISIBLE);
//                //todo 带完善
//                viewHolder.sendState.setImageResource(R.anim.rotate_sending);
//            }
            viewHolder.sendText.setText(messageBean.getContent());
        } else {
            viewHolder.sendContainer.setVisibility(View.GONE);
            viewHolder.receivedContainer.setVisibility(View.VISIBLE);
            viewHolder.receivedText.setText(messageBean.getContent());
        }
        if (userBean != null) {
            updateRightFaceImg(userBean.getImagePath());
        }
    }

    /**
     * 修改聊天界面用户头像外部接口
     * @param imgPath 头像路径
     */
    public void updateRightFaceImg(String imgPath){
        if (imgPath != null && viewHolder != null){
            /* 头像大小 */
            int imageWidth = 20;
            int imageHeight = 20;
            Bitmap bitmap = new PhotoUtils(mainActivity).getSpecifiedBitmap(imgPath, imageWidth, imageHeight);
            if (bitmap != null)
                viewHolder.userFace.setImageBitmap(bitmap);
        }
    }

    private class MsgItemHolder{
        TextView chatTime;

        LinearLayout receivedContainer;
        TextView receivedText;

        LinearLayout sendContainer;
//        ImageView sendState;
        TextView sendText;
        ImageView userFace;
    }

}
