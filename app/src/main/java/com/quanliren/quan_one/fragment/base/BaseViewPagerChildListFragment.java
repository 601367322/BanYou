package com.quanliren.quan_one.fragment.base;

import com.quanliren.quan_one.activity.impl.LoaderImpl;

/**
 * Created by Shen on 2015/7/6.
 *
 * 如果fragment是放在viewpager里的，并且是个list，则继承这个
 */
public abstract class BaseViewPagerChildListFragment<T> extends BaseListFragment<T> implements
        LoaderImpl {

    @Override
    public void refresh() {
        if (getActivity() != null && init.compareAndSet(false, true)) {
            super.init();
        }
    }

    @Override
    public void init() {
    }
}
