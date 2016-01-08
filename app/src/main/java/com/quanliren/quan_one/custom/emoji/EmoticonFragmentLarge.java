package com.quanliren.quan_one.custom.emoji;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.EmoteLargeAdapter;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip;
import com.quanliren.quan_one.bean.emoticon.EmoticonActivityListBean.EmoticonZip.EmoticonImageBean;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmoticonFragmentLarge extends Fragment {

    AtomicBoolean init = new AtomicBoolean(false);

//    @ViewById(R.id.gridview)
    EmoteGridView gridview;

//    @FragmentArg
    ArrayList<EmoticonImageBean> emoticon;

    EmoteLargeAdapter adapter;

    View view;

    EmoteGridView.EmoticonListener listener;

    public void setListener(EmoteGridView.EmoticonListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        emoticon=getArguments().getParcelableArrayList("emoticon");
        emoticon= (ArrayList<EmoticonImageBean>) getArguments().getSerializable("emoticon");
        if (view == null) {
            view = inflater.inflate(R.layout.emoticon_gridview_large, null);
        } else {
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
        }
        gridview= (EmoteGridView) view.findViewById(R.id.gridview);
        refresh();
        return view;
    }

    public void refresh() {
        if (getActivity() != null && init.compareAndSet(false, true)) {
            adapter = new EmoteLargeAdapter(getActivity(), emoticon);
            gridview.setListener(listener);
            gridview.setAdapter(adapter);
        }

    }

}
