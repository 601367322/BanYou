package com.quanliren.quan_one.activity.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.user.IntroEditActivity_;
import com.quanliren.quan_one.activity.user.NicknameEditActivity_;
import com.quanliren.quan_one.activity.user.UserPicFragment;
import com.quanliren.quan_one.activity.user.UserPicFragment_;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.fragment.message.ChatListFragment;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.activity_edit_group)
public class EditGroupActivity extends BaseActivity {
    public static final String DISSOLVEGROUP = "com.quanliren.quan_one.activity.group.EditGroupActivity.DISSOLVEGROUP";
    private static final int EDIT_NICKNAME = 6;
    private static final int EDIT_INFO = 8;
    @ViewById(R.id.pic_contents)
    View pic_contents;
    @ViewById(R.id.et_group_nickname)
    TextView group_nickname;
    @ViewById(R.id.et_group_intro)
    TextView group_intro;
    @Extra
    public GroupBean group;
    UserPicFragment fragment;

    @Override
    public void init() {
        super.init();
        setTitleTxt(getString(R.string.group_edit));
        if (group == null) {
            return;
        }
        if (fragment == null) {
            fragment = UserPicFragment_.builder().listSource(group.getImglist()).groupId(group.getId()).groupType(group.getType()).isGroup(true).needAddBtn(true).maxLine(2).needPage(false).build();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pic_contents, fragment).commit();
        } else {
            fragment.setList(group.getImglist());
        }
        group_nickname.setText(group.getGroupName());
        group_intro.setText(group.getGroupInt());
    }

    @Click(R.id.group_ll)
    void groupName() {
        NicknameEditActivity_.intent(mContext).str_nickname(group_nickname.getText().toString().trim()).type(1).groupId(group.getId()).startForResult(EDIT_NICKNAME);
    }

    @Click(R.id.intro_ll)
    void groupIntro() {
        IntroEditActivity_.intent(mContext).str_introduce(group_intro.getText().toString().trim()).type(1).groupId(group.getId()).startForResult(EDIT_INFO);
    }

    @OnActivityResult(EDIT_NICKNAME)
    void onResultNickname(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String groupName = data.getStringExtra("nickname");
            group_nickname.setText(groupName);
            setResult(RESULT_OK);
        }
    }

    @OnActivityResult(EDIT_INFO)
    void onResultInfo(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String groupInt = data.getStringExtra("introduce");
            group_intro.setText(groupInt);
            setResult(RESULT_OK);
        }
    }

    @Click(R.id.dissolve_group)
    void dissolveGroup() {
        new AlertDialog.Builder(mContext)
                .setMessage("确定要解散该群？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //解散群组
                        RequestParams params = getAjaxParams();
                        params.put("type", 5);
                        params.put("groupId", group.getId());
                        ac.finalHttp.post(mContext, URL.GROUP_MANAGER_USER, params, new MyJsonHttpResponseHandler(mContext, Util.progress_arr[4]) {
                            @Override
                            public void onSuccessRetCode(JSONObject jo) throws Throwable {
                                //删除群组聊天信息
                                LoginUser user = ac.getUser();
                                DBHelper.chatListBeanDao.deleteChatList(user.getId(), group.getId());
                                DBHelper.dfMessageDao.deleteAllMessageByFriendId(user.getId(), group.getId());
                                Intent chatlist = new Intent(ChatListFragment.REMOVE);
                                chatlist.putExtra("friendId", group.getId());
                                sendBroadcast(chatlist);

                                showCustomToast("该群组已解散");
                                //发广播，关闭群详情，刷新群组列表
                                Intent intent = new Intent(DISSOLVEGROUP);
                                intent.putExtra("group", group);
                                sendBroadcast(intent);
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create().show();

    }
}
