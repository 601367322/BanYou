package com.quanliren.quan_one.fragment.date;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.api.base.BaseApi;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildListFragment;
import com.quanliren.quan_one.activity.date.DateDetailActivity_;
import com.quanliren.quan_one.adapter.DateAdapter;
import com.quanliren.quan_one.adapter.DateAdapter.IQuanAdapter;
import com.quanliren.quan_one.adapter.base.BaseAdapter;
import com.quanliren.quan_one.api.QuanListApi;
import com.quanliren.quan_one.bean.BadgeBean;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.custom.ShakeImageView;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildNavFragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

@EFragment
public class DateListFragment extends BaseViewPagerChildListFragment<DateBean> implements
        IQuanAdapter {

    public static final int ONCE = 0;
    public static final int ALL = 1;
    public static final int MY = 2;
    public static final int COLLECT = 6;
    public static final int CARE = 7;

    public static final int PUBLISH_RESULT = 1;
    public static final int CHOSE_LOCTION_RESULT = 2;
    public static final int FILTER_RESULT = 3;

    @FragmentArg
    public String title_str;

    @Override
    public int getConvertViewRes() {
        return R.layout.date_fragment_list;
    }

    @Override
    public void init() {
        super.init();
        switch (type) {
            case CARE:
            case ALL:
                if (maction_bar != null)
                    maction_bar.setVisibility(View.GONE);
                break;
            case ONCE:
                if (TextUtils.isEmpty(title_str)) {
                    setTitleTxt("TA发布的约会");
                } else {
                    setTitleTxt(title_str);
                }
                refresh();
                break;
            case MY:
                setTitleTxt("我发布的");
                refresh();
                break;
            case COLLECT:
                setTitleTxt("我的收藏");
                refresh();
                break;
        }
    }

    @Override
    public Class<?> getClazz() {
        return DateBean.class;
    }

    @FragmentArg
    public int type = ALL;
    @FragmentArg
    public String userId = "";

    @Override
    public BaseAdapter<DateBean> getAdapter() {
        DateAdapter adapter = new DateAdapter(getActivity());
        adapter.setListener(this);
        return adapter;
    }

    @Override
    public BaseApi getApi() {
        return new QuanListApi(getActivity(), type);
    }

    @Override
    public boolean needCache() {
        return true;
    }

    @Override
    public boolean needLocation() {
        switch (type) {
            case ALL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void initParams() {
        super.initParams();
        switch (type) {
            case ONCE:
                api.initParam(userId);
                break;
            default:
                api.initParam();
                break;
        }
    }

    @Override
    public void initListView() {
        super.initListView();
        switch (type) {
            case ALL:
            case CARE:
                ShakeImageView adImg = new ShakeImageView(getActivity());
                adImg.addToListView(listview);
                break;
        }

    }

    @OnActivityResult(PUBLISH_RESULT)
    public void publishResult(int result) {
        if (result == Activity.RESULT_OK) {
            if (type == ALL) {
                super.swipeRefresh();
            }
        }
    }

    @OnActivityResult(FILTER_RESULT)
    public void filterResult(int result) {
        if (result == Activity.RESULT_OK) {
            if (type == ALL) {
                super.swipeRefresh();
            }
        }
    }

    @OnActivityResult(CHOSE_LOCTION_RESULT)
    public void choseLocationResult(int result) {
        if (result == Activity.RESULT_OK) {
            if (type == ALL) {
                super.swipeRefresh();
            }
        }
    }

    @Override
    public void detailClick(DateBean bean) {
        DateDetailActivity_.intent(this).bean(bean).start();
    }

    @Override
    public boolean needTitle() {
        switch (type) {
            case ALL:
            case CARE:
                return false;
        }
        return true;
    }

    @Override
    public String getCacheKey() {
        switch (type) {
            case ALL:
                return super.getCacheKey();
            case ONCE:
                return super.getCacheKey() + userId;
            case MY:
                return super.getCacheKey() + "my" + DBHelper.loginUserDao.getUser().getId();
            case COLLECT:
                return super.getCacheKey() + "collect" + DBHelper.loginUserDao.getUser().getId();
            case CARE:
                return super.getCacheKey() + "care" + DBHelper.loginUserDao.getUser().getId();
        }
        return super.getCacheKey();
    }

    @Override
    public boolean needBack() {
        switch (type) {
            case ALL:
                return false;
            default:
                return true;
        }
    }

    @Override
    public boolean autoRefresh() {
        switch (type) {
            case ALL:
                return false;
        }
        return super.autoRefresh();
    }

    AtomicBoolean init1 = new AtomicBoolean(false);

    @Override
    public void swipeRefresh() {
        if (getActivity() != null && init1.compareAndSet(false, true)) {
            if (type == ALL) {
                checkCity();
            } else {
                super.swipeRefresh();
            }
        }
    }

    void checkCity() {
        if (!ac.cs.getLocation().equals(ac.cs.getChoseLocation())) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage("定位显示你在" + ac.cs.getLocation() + "，是否切换至" + ac.cs.getLocation())
                    .setPositiveButton(
                            "切换",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    try {
                                        ((DateNavFragment) getParentFragment()).setTitleLeftSubTxt(ac.cs.getLocation());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    ac.cs.setChoseLocation(ac.cs.getLocation());
                                    ac.cs.setChoseLocationID(Integer.valueOf(ac.cs.getLocationID()));
                                    checkCityEnd();
                                }
                            })
                    .setNegativeButton(
                            "取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    checkCityEnd();
                                }
                            }).create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            checkCityEnd();
        }
    }

    public void checkCityEnd() {
        super.swipeRefresh();
    }

    @Override
    public void onSuccessCallBack(JSONObject jo) {
        super.onSuccessCallBack(jo);
        if (type == CARE) {
            LoginUser loginUser = ac.getUser();
            BadgeBean badgeBean = DBHelper.badgeDao.getBadge(loginUser.getId());
            if(badgeBean!=null) {
                badgeBean.getBean().setDateBadge(false);
                DBHelper.badgeDao.update(badgeBean);
                ((BaseViewPagerChildNavFragment) getParentFragment()).actionbar_tab.setBadge(1, false);
            }
        }
    }
}
