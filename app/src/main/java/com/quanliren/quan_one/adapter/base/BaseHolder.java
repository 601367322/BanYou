package com.quanliren.quan_one.adapter.base;

import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseHolder<E> {

    public BaseHolder(View view) {

        ButterKnife.bind(this, view);

        view.setTag(this);
    }

    public abstract void bind(E bean, int position);
}