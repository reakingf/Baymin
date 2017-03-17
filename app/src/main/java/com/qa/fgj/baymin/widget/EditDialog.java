package com.qa.fgj.baymin.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.qa.fgj.baymin.R;

/**
 *
 * Created by FangGengjia on 2017/3/17.
 */

public class EditDialog extends Dialog {

    private TextView title_tv;
    private EditText editText;
    private TextView cancelButton;
    private TextView yesButton;

    public EditDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_editable, null);
        setContentView(R.layout.dialog_editable);
        title_tv = (TextView) view.findViewById(R.id.title_tip);
        editText = (EditText) view.findViewById(R.id.edit_text);
        cancelButton = (TextView) view.findViewById(R.id.cancel_button);
        yesButton = (TextView) view.findViewById(R.id.yes_button);
    }

    public EditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected EditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public EditDialog showTitle(){
        title_tv.setVisibility(View.VISIBLE);
        return this;
    }

    public EditDialog hideTitle(){
        title_tv.setVisibility(View.GONE);
        return this;
    }

    public EditDialog showEditText(){
        editText.setVisibility(View.VISIBLE);
        return this;
    }

    public EditDialog hideEditText(){
        editText.setVisibility(View.GONE);
        return this;
    }

    public EditDialog setTitleText(String text){
        title_tv.setText(text);
        return this;
    }

    public EditDialog setTitleText(int resId){
        title_tv.setText(resId);
        return this;
    }

    public String getEditText(){
        if (editText == null){
            return null;
        }
        return editText.getText().toString();
    }

    public EditDialog setListener(View.OnClickListener listener){
        cancelButton.setOnClickListener(listener);
        yesButton.setOnClickListener(listener);
        return this;
    }

}
