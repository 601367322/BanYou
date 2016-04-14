package com.quanliren.quan_one.activity.date;

import android.content.Intent;
import android.view.View;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagBean;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

/**
 * Created by Shen on 2016/2/26.
 */
@EActivity(R.layout.activity_choose_date_content)
public class ChooseContentActivity extends BaseActivity {

    @ViewById
    TagContainerLayout tagcontainerLayout1, tagcontainerLayout2;

    @Extra
    ArrayList<TagBean> list;

    @Override
    public void init() {
        setTitleTxt(R.string.choose_date_content);
        setTitleRightTxt(R.string.ok);

        tagcontainerLayout1.setTags(list);


        ac.finalHttp.post(mContext, URL.GET_DATE_TYPE_LIST, Util.getRequestParams(mContext), new MyJsonHttpResponseHandler(mContext, Util.progress_arr[1]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                final List<TagBean> list3 = Util.jsonToList(jo.getJSONObject(URL.RESPONSE).getString(URL.LIST), TagBean.class);

                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list3.size(); j++) {
                        if (list3.get(j).getId() == list.get(i).getId()) {
                            list3.remove(j);
                            continue;
                        }
                    }
                }
                list3.removeAll(list);

                tagcontainerLayout2.setTags(list3);

                tagcontainerLayout2.setOnTagClickListener(new TagView.OnTagClickListener() {

                    @Override
                    public void onTagClick(int position, String text) {
                        if (tagcontainerLayout1.getTags().size() == 3) {
                            Util.toast(mContext, "最多只能选3个");
                            return;
                        }
                        tagcontainerLayout1.addTag(list3.get(position));
                        tagcontainerLayout2.removeTag(position);
                    }

                    @Override
                    public void onTagLongClick(int position, String text) {

                    }
                });

                tagcontainerLayout1.setOnTagClickListener(new TagView.OnTagClickListener() {
                    @Override
                    public void onTagClick(int position, String text) {
                        tagcontainerLayout2.addTag(tagcontainerLayout1.getTags().get(position));
                        tagcontainerLayout1.removeTag(position);
                    }

                    @Override
                    public void onTagLongClick(int position, String text) {

                    }
                });
            }
        });
    }

    @Override
    public void rightClick(View v) {
        if (tagcontainerLayout1.getTags().size() == 0) {
            Util.toast(mContext, "请至少选择一个标签");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("tags", (ArrayList<TagBean>) tagcontainerLayout1.getTags());
        setResult(RESULT_OK, intent);
        finish();
    }
}
