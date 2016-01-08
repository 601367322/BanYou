package com.quanliren.quan_one.fragment.base;

import com.quanliren.quan_one.activity.impl.LoaderImpl;

/**
 * Created by Shen on 2015/7/6.
 *
 * 如果fragment是放在viewpager里的，则继承这个
 */
public abstract class BaseViewPagerChildFragment extends BaseFragment implements
        LoaderImpl {

    @Override
    public void refresh() {
        if (getActivity() != null && init.compareAndSet(false, true)) {
            lazyInit();
        }
    }

    @Override
    public void init() {
        super.init();
    }

    /**
     * actionbar也会懒加载
     */
    public abstract void lazyInit();

    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    public void onVisible() {
    }

    public void onInvisible() {
    }
}
