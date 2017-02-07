package com.qa.fgj.baymin.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.util.ToastUtil;

import java.util.List;

/**
 * 聊天列表适配器
 * Created by FangGengjia on 2017/1/17.
 */

public class MsgAdapter extends ArrayAdapter<MessageBean>
        implements View.OnClickListener, View.OnLongClickListener{

    private int resourceId;
    private MsgItemHolder viewHolder;

    public MsgAdapter(Context context, int resource, List<MessageBean> data) {
        super(context, resource, data);
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
            viewHolder.sendState = (ImageView) view.findViewById(R.id.msg_state);
            viewHolder.sendText = (TextView) view.findViewById(R.id.send_text);
            viewHolder.userFace = (ImageView) view.findViewById(R.id.user_face_img);
            viewHolder.receivedText.setOnClickListener(this);
            viewHolder.sendState.setOnClickListener(this);
            viewHolder.userFace.setOnClickListener(this);

            viewHolder.sendText.setOnLongClickListener(this);

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
            case R.id.msg_state:
                //TODO 重发
                ToastUtil.show("重发");
                break;
            case R.id.user_face_img:
                //TODO 进入用户信息页
                ToastUtil.show("点击用户头像");
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        //TODO PopupWindow：复制、删除、查看更多等
        switch (view.getId()){
            case R.id.recevied_text:
                ToastUtil.show("长按接收文本");
                break;
            case R.id.send_text:
                ToastUtil.show("长按发送文本");
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
            //TODO 发送失败或发送较耗时，显示重发按钮或圆形进度条
            viewHolder.sendText.setText(messageBean.getContent());
        } else {
            viewHolder.sendContainer.setVisibility(View.GONE);
            viewHolder.receivedContainer.setVisibility(View.VISIBLE);
            viewHolder.receivedText.setText(messageBean.getContent());
        }
    }

    private class MsgItemHolder{
        TextView chatTime;

        LinearLayout receivedContainer;
        TextView receivedText;

        LinearLayout sendContainer;
        ImageView sendState;
        TextView sendText;
        ImageView userFace;
    }

}
