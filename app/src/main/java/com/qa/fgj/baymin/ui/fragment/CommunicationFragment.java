package com.qa.fgj.baymin.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
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
import com.qa.fgj.baymin.model.entity.MessageBean;
import com.qa.fgj.baymin.presenter.CommunicationPresenter;
import com.qa.fgj.baymin.ui.activity.ICommunicationView;
import com.qa.fgj.baymin.ui.adapter.MsgAdapter;
import com.qa.fgj.baymin.util.Global;
import com.qa.fgj.baymin.util.ToastUtil;
import com.qa.fgj.baymin.widget.XListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 聊天界面
 * Created by FangGengjia on 2017/1/18.
 */

public class CommunicationFragment extends Fragment implements
        ICommunicationView, View.OnClickListener, XListView.IXListViewListener{

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

    private List<MessageBean> listData;
    private MsgAdapter adapter;
    /* 初始消息id */
    private String msgID = "0";
    private boolean mPullRefreshing = false;

    private CommunicationPresenter presenter;
    private final Scheduler executor = Schedulers.io();
    private final Scheduler notifier = AndroidSchedulers.mainThread();
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
        presenter = new CommunicationPresenter(executor, notifier);
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
        presenter.fetch(msgID, listData.size());
        return view;
    }

    private void initListView() {
        listView.setPullLoadEnable(false);
        listData = new ArrayList<>();
        adapter = new MsgAdapter(getActivity(), R.layout.item_chat_list, listData);
        listView.setAdapter(adapter);
        listView.setXListViewListener(this);
    }

    private void initView() {
        editText.addTextChangedListener(mTextWatcher);
        inputType.setOnClickListener(this);
        voiceButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void initListViewData(List<MessageBean> messageList){
        listData.clear();
        if (Global.isLogin){
            if (messageList != null && messageList.size() > 0){
                for (MessageBean bean : messageList) {
//                setHandleMsgBean(item, 0);
                    listData.add(0, bean);
                }
            } else {
                MessageBean messageBean =new MessageBean(getString(R.string.welcome_tip), false,
                        System.currentTimeMillis());
                listData.add(messageBean);
                presenter.save(messageBean);
            }
        } else{
            listView.setPullRefreshEnable(false);
            MessageBean messageBean =new MessageBean(getString(R.string.welcome_tip), false,
                    System.currentTimeMillis());
            listData.add(messageBean);
        }
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
                handleSendQuestion();
                break;
        }
    }

    /**
     * 更改底部输入面板视图类型
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

    @SuppressWarnings("unchecked")
    private void handleSendQuestion() {
        sendButton.setBackgroundResource(R.drawable.bg_button);
        String question = editText.getText().toString().trim();
        if (TextUtils.isEmpty(question)){
            ToastUtil.shortShow(getString(R.string.null_tips));
        } else if (getString(R.string.close_asr).equals(question)) {
            //todo 关闭语音连续识别
        } else {
            final MessageBean sendMsg = new MessageBean(question, MessageBean.TYPE_SEND, System.currentTimeMillis());
            listData.add(sendMsg);
            adapter.notifyDataSetChanged();
            Subscriber subscriber = new Subscriber<MessageBean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
//                    listData.get(0).isSendSuccessful = false;
//                    listData.get(0).isSending = false;
                    sendMsg.isSending = false;
                    sendMsg.isSendSuccessful = false;
                    listData.remove(listData.size() - 1);
                    listData.add(sendMsg);
                    adapter.notifyDataSetChanged();
                    //todo 小白回复：网络错误，请重试
                    presenter.save(sendMsg);
                }

                @Override
                public void onNext(MessageBean messageBean) {
                    sendMsg.isSending = false;
                    sendMsg.isSendSuccessful = true;
                    listData.remove(listData.size() - 1);
                    listData.add(sendMsg);
                    presenter.save(sendMsg);

                    MessageBean respondMsg = messageBean;
                    listData.add(respondMsg);
                    presenter.save(respondMsg);
                    //todo 语音合成答案
                    adapter.notifyDataSetChanged();
                }
            };
            presenter.getAnswer(question, subscriber);
            editText.setText("");
        }
    }

    private void startSpeechReconDialog() {

    }

    @Override
    public void onRefresh() {
        if (!mPullRefreshing){
            mPullRefreshing = true;
            Subscriber subscriber = new Subscriber<List<MessageBean>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(List<MessageBean> beans) {
                    if (beans != null){
                        for (MessageBean bean : beans){
                            listData.add(0, bean);
                        }
                    }
                    if (beans == null || beans.size() < 20){
                        listView.setPullRefreshEnable(false);
                    }
                    listView.stopRefresh();
                    mPullRefreshing = false;
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                }
            };
            presenter.onRefreshing(msgID, listData.size(), subscriber);
        }
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
