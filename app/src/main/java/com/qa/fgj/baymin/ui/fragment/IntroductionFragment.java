package com.qa.fgj.baymin.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qa.fgj.baymin.R;

/**
 * Created by FangGengjia on 2017/1/18.
 */

public class IntroductionFragment extends Fragment {

    public static final String TAG = IntroductionFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_introduction, container, false);
    }

}
