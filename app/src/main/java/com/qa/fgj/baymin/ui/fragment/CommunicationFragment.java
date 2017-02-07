package com.qa.fgj.baymin.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.presenter.CommunicationPresenter;
import com.qa.fgj.baymin.ui.adapter.MsgAdapter;
import com.qa.fgj.baymin.widget.XListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 聊天界面
 * Created by FangGengjia on 2017/1/18.
 */

public class CommunicationFragment extends Fragment implements
        IBaseView, View.OnClickListener, XListView.IXListViewListener{

    public static final String TAG = CommunicationFragment.class.getSimpleName();

    @BindView(R.id.listContent)
    XListView listView;
    @BindView(R.id.msgButtonBar)
    LinearLayout inputLayout;
    @BindView(R.id.inputType)
    ImageButton inputType;
    @BindView(R.id.voiceButton)
    Button voiceButton;
    @BindView(R.id.textInput)
    EditText editText;
    @BindView(R.id.sendButton)
    Button sendButton;

    private List<MessageBean> dataList;
    private MsgAdapter adapter;

    private CommunicationPresenter presenter;
    private InputMethodManager inputMethodManager;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!"".equals(editText.getText().toString().trim())){
                sendButton.setBackgroundResource(R.drawable.bg_button_green);
            } else {
                sendButton.setBackgroundResource(R.drawable.bg_button);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public static CommunicationFragment newInstance(){
        return new CommunicationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CommunicationPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communication, container, false);
        ButterKnife.bind(this, view);
        initListView();
        initView();
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        presenter.attachView(this);
        return view;
    }

    private void initListView() {
        listView.setPullLoadEnable(false);
        dataList = new ArrayList<>();
        adapter = new MsgAdapter(getActivity(), R.layout.item_chat_list, dataList);
        listView.setAdapter(adapter);
        listView.setXListViewListener(this);
        loadMsg();
    }

    private void initView() {
        editText.addTextChangedListener(mTextWatcher);
        inputType.setOnClickListener(this);
        voiceButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
    }

    private void loadMsg(){
        dataList.clear();
        dataList.addAll(presenter.loadDataFromDB());
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inputType:
                changeInputType();
                break;
            case R.id.voiceButton:
                startSpeechReconDialog();
                break;
            case R.id.sendButton:
                sendQuestion();
                break;
        }
    }

    /**
     * 更改底部输入面板视图
     */
    private void changeInputType() {
        if (voiceButton.isShown()) {
            inputType.setImageResource(R.drawable.voice);
            voiceButton.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
            inputMethodManager.showSoftInputFromInputMethod(editText.getWindowToken(), 0);
        } else {
            inputType.setImageResource(R.drawable.edit);
            editText.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
            voiceButton.setVisibility(View.VISIBLE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    private void sendQuestion() {
        sendButton.setBackgroundResource(R.drawable.bg_button);

    }

    private void startSpeechReconDialog() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void useNightMode(boolean isNight) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

}
