package com.quanliren.quan_one.custom.emoji;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.EmoteAdapter;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.custom.emoji.EmoteGridView.EmoticonListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmoticonFragment extends Fragment {

    AtomicBoolean init = new AtomicBoolean(false);

//    @ViewById(R.id.gridview)
    EmoteGridView gridview;

//    @FragmentArg
    ArrayList<String> emoticon;
    int emotionId;

    EmoteAdapter adapter;

    View view;

    EmoticonListener listener;

    public void setListener(EmoticonListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        emoticon=getArguments().getStringArrayList("emoticon");
        emotionId=getArguments().getInt("emotionId");
        if (view == null) {
            view = inflater.inflate(R.layout.emoticon_gridview, null);
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
            if(emotionId==1){
                adapter = new EmoteAdapter(getActivity(), emoticon, AppClass.mEmoticons1Id);
            }else{
                adapter = new EmoteAdapter(getActivity(), emoticon, AppClass.mEmoticons2Id);
            }
            gridview.setListener(listener);
            gridview.setAdapter(adapter);
        }
    }

}
