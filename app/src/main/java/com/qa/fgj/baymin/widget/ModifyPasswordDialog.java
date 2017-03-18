package com.qa.fgj.baymin.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qa.fgj.baymin.R;

/**
 * 修改密码对话框
 * Created by FangGengjia on 2017/3/17.
 */

public class ModifyPasswordDialog extends BaseDialog {

    private TextView title;
    private EditText srcPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private TextView negativeButton;
    private TextView positiveButton;

    private String titleText;
    private String negativeText;
    private String positiveText;
    private onNegativeButtonClick negativeClickListener;
    private onPositiveButtonClick positiveClickListener;

    public ModifyPasswordDialog(Context context) {
        super(context);
    }

    public ModifyPasswordDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ModifyPasswordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_modify_password);
        super.onCreate(savedInstanceState);
        initView();
        setViewText();
        setEvenListener();
    }

    private void initView(){
        title = (TextView) findViewById(R.id.title_tip);
        srcPassword = (EditText) findViewById(R.id.src_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        negativeButton = (TextView) findViewById(R.id.cancel_button);
        positiveButton = (TextView) findViewById(R.id.yes_button);
    }

    private void setViewText(){
        if (titleText != null){
            title.setText(titleText);
        }

        if (negativeText != null){
            negativeButton.setText(negativeText);
        }

        if (positiveText != null){
            positiveButton.setText(positiveText);
        }
    }

    private void setEvenListener(){
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

    public void setTitleText(String text){
        titleText = text;
    }

    public String getSrcPassword(){
        if (srcPassword == null){
            return null;
        }
        return srcPassword.getText().toString();
    }

    public void setSrcPasswordError(String errorTip) {
        srcPassword.setError(errorTip);
    }


    public String getNewPassword(){
        if (newPassword == null){
            return null;
        }
        return newPassword.getText().toString();
    }

    public void setNewPasswordError(String errorTip) {
        newPassword.setError(errorTip);
    }

    public String getConfirmPassword(){
        if (confirmPassword == null){
            return null;
        }
        return confirmPassword.getText().toString();
    }

    public void setConfirmPasswordError(String errorTip) {
        confirmPassword.setError(errorTip);
    }


    public void setNegativeButton(String text, final onNegativeButtonClick listener){
        negativeText = text;
        negativeClickListener = listener;
    }
    public void setPositiveButton(String text, final onPositiveButtonClick listener){
        positiveText = text;
        positiveClickListener = listener;
    }

    public interface onNegativeButtonClick{
        void onClick();
    }

    public interface  onPositiveButtonClick{
        void onClick();
    }

}
