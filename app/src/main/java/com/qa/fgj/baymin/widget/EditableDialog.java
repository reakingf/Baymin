package com.qa.fgj.baymin.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qa.fgj.baymin.R;

/**
 *
 * Created by FangGengjia on 2017/3/17.
 */

public class EditableDialog extends BaseDialog {

    private TextView title_tv;
    private EditText editText;
    private TextView negativeButton;
    private TextView positiveButton;

    private String titleText;
    private String editHintText;
    private String negativeText;
    private String positiveText;
    private onNegativeButtonClick negativeClickListener;
    private onPositiveButtonClick positiveClickListener;

    public EditableDialog(Context context) {
        super(context);
    }

    public EditableDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected EditableDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_editable);
        super.onCreate(savedInstanceState);
        initView();
        setViewText();
        setEvenListener();
    }

    private void initView(){
        title_tv = (TextView) findViewById(R.id.title_tip);
        editText = (EditText) findViewById(R.id.edit_text);
        negativeButton = (TextView) findViewById(R.id.cancel_button);
        positiveButton = (TextView) findViewById(R.id.yes_button);
    }

    private void setViewText(){
        if (titleText != null){
            title_tv.setText(titleText);
        }

        if (editHintText != null){
            editText.setHint(editHintText);
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
                } else {
                    EditableDialog.this.dismiss();
                }
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveClickListener != null){
                    positiveClickListener.onClick();
                } else {
                    EditableDialog.this.dismiss();
                }
            }
        });
    }

    public void showTitle(){
        title_tv.setVisibility(View.VISIBLE);
    }

    public void hideTitle(){
        title_tv.setVisibility(View.GONE);
    }

    public void showEditText(){
        editText.setVisibility(View.VISIBLE);
    }

    public void hideEditText(){
        editText.setVisibility(View.GONE);
    }

    public void setTitleText(String text){
        titleText = text;
    }

    public void setEditTextHint(String hint){
        editHintText = hint;
    }

    public String getEditText(){
        if (editText == null){
            return null;
        }
        return editText.getText().toString();
    }

    public void setEditTextError(String errorTip) {
        editText.setError(errorTip);
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
