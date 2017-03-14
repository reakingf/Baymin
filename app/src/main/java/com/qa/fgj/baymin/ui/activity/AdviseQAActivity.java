package com.qa.fgj.baymin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.BaseActivity;
import com.qa.fgj.baymin.presenter.AdviseQAPresenter;
import com.qa.fgj.baymin.ui.view.IAdviseQAView;
import com.qa.fgj.baymin.util.ToastUtil;

import rx.Subscriber;

/**
 * Created by FangGengjia on 2017/3/14.
 */

public class AdviseQAActivity extends BaseActivity implements IAdviseQAView {

    private Toolbar toolbar;
    private EditText editQuestion;
    private EditText editAnswer;
    private Button submitButton;

    private AdviseQAPresenter presenter;

    public static void start(Context context){
        Intent intent = new Intent(context, AdviseQAActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advise_qa);
        initView();
        presenter = new AdviseQAPresenter();
        presenter.attachView(this);
        presenter.onCreate();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        editQuestion = (EditText) findViewById(R.id.edit_question);
        editAnswer = (EditText) findViewById(R.id.edit_answer);
        submitButton = (Button) findViewById(R.id.submit);
        toolbar.setTitle(getString(R.string.app_learning));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    public void submit(){
        if (!validate()){
            return;
        }
        Subscriber<Boolean> subscriber = new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.shortShow("提交失败");
            }

            @Override
            public void onNext(Boolean isSucceed) {
                if (isSucceed){
                    ToastUtil.shortShow("");
                } else {
                    ToastUtil.shortShow("提交失败");
                }
            }
        };
        presenter.submit(editQuestion.getText().toString(), editAnswer.getText().toString(), subscriber);
    }

    public boolean validate(){
        if (editQuestion.getText().toString().trim().equals("") ||
                editAnswer.getText().toString().trim().equals("")){
            ToastUtil.shortShow(getString(R.string.empty_input_tip));
            return false;
        }
        return true;
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void useNightMode(boolean isNight) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter.onDestroy();
    }
}
