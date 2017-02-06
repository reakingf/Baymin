package com.qa.fgj.baymin.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.qa.fgj.baymin.R;
import com.qa.fgj.baymin.base.IBasePresenter;
import com.qa.fgj.baymin.base.IBaseView;
import com.qa.fgj.baymin.moduel.entity.MessageBean;
import com.qa.fgj.baymin.presenter.CommunicationPresenter;
import com.qa.fgj.baymin.ui.adapter.MsgAdapter;
import com.qa.fgj.baymin.widget.XListView;

import java.util.List;

import butterknife.BindView;

/**
 * 聊天界面
 * Created by FangGengjia on 2017/1/18.
 */

public class CommunicationFragment extends Fragment implements IBaseView{

    public static final String TAG = CommunicationFragment.class.getSimpleName();

    @BindView(R.id.listContent)
    XListView listView;
    @BindView(R.id.msgButtonBar)
    RelativeLayout inputLayout;
    @BindView(R.id.inputType)
    ImageButton inputType;
    @BindView(R.id.voiceButton)
    ImageButton voiceButton;
    @BindView(R.id.textInput)
    EditText editText;
    @BindView(R.id.sendButton)
    Button sendButton;

    private List<MessageBean> dataList;
    private MsgAdapter adapter;

    private IBasePresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CommunicationPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communication, container, false);
        initView();
        initListView();
        presenter.attachView(this);
        return view;
    }

    private void initListView() {

    }

    private void initView() {

    }

    private void initMessage(){

    }

    private void loadMsg(){

    }

    private void changeInputType(){

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
