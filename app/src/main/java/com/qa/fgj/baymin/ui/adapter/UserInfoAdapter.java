package com.qa.fgj.baymin.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.model.entity.UserInfoItem;

import java.util.List;

/**
 * 用户个人信息适配器
 * Created by FangGengjia on 2016/11/5.
 */

public class UserInfoAdapter extends ArrayAdapter<UserInfoItem> {

    private int resourceId;

    public UserInfoAdapter(Context context, int resource, List<UserInfoItem> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        UserInfoItem item = getItem(position);
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tip = (TextView) view.findViewById(R.id.tip);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (item != null){
            viewHolder.tip.setText(item.getTip());
            viewHolder.content.setText(item.getContent());
        }
        return view;
    }

    private class ViewHolder{
        private TextView tip;
        private TextView content;
    }
}
