package com.quanliren.quan_one.fragment.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.quanliren.quan_one.activity.R;
import com.shen.actionbar.nav.NavActionBarTab;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by Shen on 2015/12/22.
 *
 * 如果fragment是viewpager子元素，并且也是viewpager，并且是有tab形式的，则继承这个
 */
@EFragment
public abstract class BaseViewPagerChildNavFragment extends BaseViewPagerChildFragment implements ViewPager.OnPageChangeListener{

    @ViewById
    protected ViewPager viewpager;

    @ViewById
    public NavActionBarTab actionbar_tab;

    protected List<Fragment> fragments;

    @Override
    public int getConvertViewRes() {
        return R.layout.fragment_nav;
    }

    @Override
    public void lazyInit() {
        super.init();

        fragments = initFragments();

        actionbar_tab.setText(getTabStr());

        viewpager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        actionbar_tab.addViewPager(viewpager);
        viewpager.addOnPageChangeListener(this);

        viewpager.setCurrentItem(getDefaultCurrentItem());
        viewpager.post(new Runnable() {
            @Override
            public void run() {
                onPageSelected(getDefaultCurrentItem());
            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            if (fragments.get(position) instanceof BaseViewPagerChildFragment) {
                ((BaseViewPagerChildFragment) fragments.get(position)).refresh();
            } else if (fragments.get(position) instanceof BaseViewPagerChildListFragment) {
                ((BaseViewPagerChildListFragment) fragments.get(position)).refresh();
            }
            super.setPrimaryItem(container, position, object);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public abstract List<Fragment> initFragments();

    public abstract String getTabStr();

    public int getDefaultCurrentItem(){
        return 0;
    }
}
