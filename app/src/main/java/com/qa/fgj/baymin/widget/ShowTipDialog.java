package com.qa.fgj.baymin.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qa.fgj.baymin.R;

/**
 * 显示提示信息dialog
 * Created by FangGengjia on 2017/3/17.
 */

public class ShowTipDialog extends BaseDialog {

    private TextView title_tv;
    private TextView content_tv;
    private LinearLayout doubleButtonLayout;
    private TextView negativeButton;
    private TextView positiveButton;
    private TextView singleButton;

    private String titleText;
    private String contentText;
    private String negativeText;
    private String positiveText;
    private String singleText;
    private int gravityPosition = -1;
    private boolean isSingleButton;
    private onNegativeButtonClick negativeClickListener;
    private onPositiveButtonClick positiveClickListener;
    private onSingleButtonClick singleButtonClickListener;

    public ShowTipDialog(Context context) {
        super(context);
    }

    public ShowTipDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ShowTipDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_show_tip);
        super.onCreate(savedInstanceState);
        initView();
        setViewText();
        setEvenListener();
    }

    private void initView(){
        title_tv = (TextView) findViewById(R.id.title);
        content_tv = (TextView) findViewById(R.id.content);
        doubleButtonLayout = (LinearLayout) findViewById(R.id.double_button);
        negativeButton = (TextView) findViewById(R.id.cancel_button);
        positiveButton = (TextView) findViewById(R.id.yes_button);
        singleButton = (TextView) findViewById(R.id.single_button);
        if (gravityPosition > 0){
            content_tv.setGravity(gravityPosition);
        }
        if (isSingleButton){
            doubleButtonLayout.setVisibility(View.GONE);
            singleButton.setVisibility(View.VISIBLE);
        } else {
            doubleButtonLayout.setVisibility(View.VISIBLE);
            singleButton.setVisibility(View.GONE);
        }
    }

    private void setViewText(){
        if (titleText != null){
            title_tv.setText(titleText);
        }

        if (contentText != null){
            content_tv.setText(contentText);
        }

        if (isSingleButton){
            if (singleText != null){
                singleButton.setText(singleText);
            }
        } else {
            if (negativeText != null){
                negativeButton.setText(negativeText);
            }

            if (positiveText != null){
                positiveButton.setText(positiveText);
            }
        }
    }

    private void setEvenListener(){
        if (isSingleButton){
            singleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (singleButtonClickListener != null){
                        singleButtonClickListener.onClick();
                    } else {
                        ShowTipDialog.this.dismiss();
                    }
                }
            });
        } else {
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (negativeClickListener != null){
                        negativeClickListener.onClick();
                    }
                }
            });

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null){
                        positiveClickListener.onClick();
                    }
                }
            });
        }
    }

    public void setContentGravity(int gravityPosition){
        this.gravityPosition = gravityPosition;
    }

    public void setTitleText(String title){
        titleText = title;
    }

    public void setContentText(String content){
        contentText = content;
    }

    public void setSingleButton(boolean isSigle) {
        isSingleButton = isSigle;
    }

    public void setNegativeButton(String text, final onNegativeButtonClick listener){
        negativeText = text;
        negativeClickListener = listener;
    }
    public void setPositiveButton(String text, final onPositiveButtonClick listener){
        positiveText = text;
        positiveClickListener = listener;
    }

    public void setSingleButton(String text, onSingleButtonClick listener){
        singleText = text;
        singleButtonClickListener = listener;
    }

    public interface onNegativeButtonClick{
        void onClick();
    }

    public interface  onPositiveButtonClick{
        void onClick();
    }

    public interface onSingleButtonClick{
        void onClick();
    }

}
