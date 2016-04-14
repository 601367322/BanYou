package com.quanliren.quan_one.activity.jubao;

import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.adapter.JuBaoAdapter;
import com.quanliren.quan_one.bean.DateBean;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.JuBao;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.NoScrollGridView;
import com.quanliren.quan_one.fragment.custom.AddPicFragment;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Shen on 2016/4/6.
 */
@EActivity(R.layout.activity_jubao)
public class JuBaoActivity extends BaseActivity {

    public enum Type {
        JUBAO, JUBAO_AND_BLACK
    }

    @Extra
    User other;
    @Extra
    GroupBean group;
    @Extra
    DateBean date;

    JuBaoAdapter adapter;

    @ViewById
    ScrollView scroll_view;

    @ViewById(R.id.gridview)
    NoScrollGridView gridView;

    @ViewById(R.id.nickname)
    TextView nickname;
    @ViewById(R.id.type_str)
    TextView typeStr;
    @ViewById(R.id.content)
    EditText content;

    @FragmentById(R.id.picFragment)
    AddPicFragment fragment;

    public String drId;

    @Override
    public void init() {
        super.init();

        setTitleTxt("举报");

        ac.finalHttp.post(mContext, URL.JUBAO_LIST, getAjaxParams(), new MyJsonHttpResponseHandler(mContext, Util.progress_arr[1]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                List<JuBao> list = Util.jsonToList(jo.getJSONObject(URL.RESPONSE).getString(URL.LIST), JuBao.class);
                gridView.setAdapter(adapter = new JuBaoAdapter(mContext));
                adapter.setList(list);

                scroll_view.setVisibility(View.VISIBLE);
            }
        });

        nickname.setText(other.getNickname());
        if (date != null) {
            typeStr.setText("的约会：");
        } else if (group != null) {
            typeStr.setText("的群组：");
        } else {
            typeStr.setText("：");
        }
    }

    @Click(R.id.jubao_btn)
    public void jubaoBtnClick(final View view) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < adapter.getList().size(); i++) {
            if (adapter.getList().get(i).isChecked) {
                array.put(adapter.getList().get(i).id);
            }
        }
        if (array.length() == 0) {
            Util.toast(mContext, "至少选择一个举报内容");
            return;
        }

        RequestParams param = getAjaxParams();
        param.put("content", content.getText().toString());
        param.put("type", array.toString());
        String url = URL.JUBAO;

        if (date != null) {
            param.put("otherid", date.getDyid());
            param.put("ptype", 1);
        } else if (group != null) {
            param.put("otherid", group.getId());
            param.put("ptype", 2);
        } else {
            param.put("otherid", other.getId());
            param.put("ptype", 0);
            url = URL.JUBAOANDBLACK;
        }

        view.setEnabled(false);

        ac.finalHttp.post(mContext, url, param, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[3]) {
            @Override
            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                if (fragment.imagePath.size() > 0) {
                    drId = jo.getJSONObject(URL.RESPONSE).getString("rportId");
                    view.setEnabled(false);
                    uploadImg(0);
                } else {
                    Util.toast(mContext, "举报成功");
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                view.setEnabled(true);
            }
        });
    }

    public void uploadImg(int i) {
        try {
            RequestParams ap = getAjaxParams();
            File newFile = new File(StaticFactory.APKCardPath + fragment.imagePath.get(i).toString());
            ImageUtil.downsize(fragment.imagePath.get(i), newFile.getPath(), mContext);
            ap.put("file", newFile);
            ap.put("drId", drId);
            ac.finalHttp.post(URL.JUBAO_IMG, ap, new callBack(i));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class callBack extends MyJsonHttpResponseHandler {

        int index = 0;

        public callBack(int index) {
            super(mContext, "正在上传第" + (index + 1) + "张图片");
            this.index = index;
        }

        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            if (index == (fragment.imagePath.size() - 1)) {
                uploadSuccess();
            } else {
                uploadImg(index + 1);
            }
        }

        private void uploadSuccess() {
            showCustomToast("举报成功");
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onFailRetCode(JSONObject jo) {
            super.onFailRetCode(jo);
            finish();
        }

        @Override
        public void onFailure() {
            super.onFailure();
            finish();
        }
    }
}
