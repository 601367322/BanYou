package com.quanliren.quan_one.activity.date;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.fragment.date.ChosePositionFragment;
import com.quanliren.quan_one.fragment.date.ChosePositionFragment_;
import com.quanliren.quan_one.share.CommonShared;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity(R.layout.chose_position_actvitiy)
public class ChoseLocationActivity extends BaseActivity {

    @Extra
    public ChosePositionFragment.FromActivity fromActivity;

    @Override
    public void init() {
        setTitleTxt("选择城市");
        getSupportFragmentManager().beginTransaction().replace(R.id.content, ChosePositionFragment_.builder().fromActivity(fromActivity).build()).commit();

        if (fromActivity == ChosePositionFragment.FromActivity.DateList) {
            ac.cs.setFIRSTCHOSE_LOCATION(CommonShared.CLOSE);
        }
    }
}
