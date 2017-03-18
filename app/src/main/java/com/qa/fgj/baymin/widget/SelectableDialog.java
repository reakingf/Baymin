package com.qa.fgj.baymin.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.qa.fgj.baymin.R;

/**
 * 二选一对话框
 * Created by FangGengjia on 2017/3/17.
 */

public class SelectableDialog extends BaseDialog {

    private TextView title_tv;
    private TextView firstItem_tv;
    private TextView secondItem_tv;
    //伪装的button
    private TextView cancelButton;

    private String titleText;
    private String firstItemText;
    private String secondItemText;
    private String cancelButtonText;
    private ItemClickListener onFirstItemClick;
    private ItemClickListener onSecondItemClick;
    private ItemClickListener onButtonClickListener;

    public SelectableDialog(Context context) {
        super(context);
    }

    public SelectableDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SelectableDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //放在super前是为了使用基类的属性设置
        setContentView(R.layout.dialog_selectable);
        super.onCreate(savedInstanceState);
        initView();
        setViewText();
        setEvenListener();
    }

    private void setEvenListener() {
        firstItem_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFirstItemClick != null){
                    onFirstItemClick.onClick();
                }
            }
        });

        secondItem_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSecondItemClick != null){
                    onSecondItemClick.onClick();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonClickListener != null){
                    onButtonClickListener.onClick();
                } else {
                    SelectableDialog.this.dismiss();
                }
            }
        });

    }

    private void initView(){
        title_tv = (TextView) findViewById(R.id.title_tip);
        firstItem_tv = (TextView) findViewById(R.id.firstText);
        secondItem_tv = (TextView) findViewById(R.id.secondText);
        cancelButton = (TextView) findViewById(R.id.sigleButton);
    }

    private void setViewText(){
        if (titleText != null){
            title_tv.setText(titleText);
        }

        if (firstItemText != null){
            firstItem_tv.setText(firstItemText);
        }

        if (secondItemText != null){
            secondItem_tv.setText(secondItemText);
        }

        if (cancelButtonText != null){
            cancelButton.setText(cancelButtonText);
        }
    }

    public void setTitleText(String text){
        titleText = text;
    }

    public void setFirstItem(String text, final ItemClickListener listener){
        firstItemText = text;
        onFirstItemClick = listener;
    }

    public void setSecondItem(String text, final ItemClickListener listener){
        secondItemText = text;
        onSecondItemClick = listener;
    }

    public void setButton(String text, final ItemClickListener listener){
        cancelButtonText = text;
        onButtonClickListener = listener;
    }

    public interface ItemClickListener {
        void onClick();
    }

}
