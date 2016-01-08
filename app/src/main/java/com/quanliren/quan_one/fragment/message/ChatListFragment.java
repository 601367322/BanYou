package com.quanliren.quan_one.fragment.message;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.fragment.base.BaseViewPagerChildFragment;
import com.quanliren.quan_one.activity.user.ChatActivity;
import com.quanliren.quan_one.activity.user.ChatActivity_;
import com.quanliren.quan_one.adapter.LeaveMessageAdapter;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.dao.ChatListBeanDao;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.dao.DfMessageDao;
import com.quanliren.quan_one.listener.ICheckBoxInterface;
import com.quanliren.quan_one.pull.swipe.SwipeRefreshLayout;
import com.quanliren.quan_one.util.Util;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment
public class ChatListFragment extends BaseViewPagerChildFragment implements SwipeRefreshLayout.OnRefreshListener, ICheckBoxInterface {

    public static final String TAG = "MyLeaveMessageActivity";
    public static final String REFEREMSGCOUNT = "com.quanliren.quan_one.MyLeaveMessageActivity.REFEREMSGCOUNT";
    public static final String ADDMSG = "com.quanliren.quan_one.MyLeaveMessageActivity.ADDMSG";
    public static final String REMOVE = "com.quanliren.quan_one.MyLeaveMessageActivity.REMOVE";
    public static final String UPDATE = "com.quanliren.quan_one.MyLeaveMessageActivity.UPDATE";
    @ViewById(R.id.listview)
    ListView listview;
    @ViewById(R.id.empty_view)
    View empty_view;
    @ViewById(R.id.layout_option)
    View layOption;
    @ViewById(R.id.checkbox_select_all)
    CheckBox selectAll;
    @ViewById(R.id.swipe_layout)
    SwipeRefreshLayout swipe_layout;
    LeaveMessageAdapter adapter;
    LoginUser user;
    ChatListBeanDao chatListBeanDao;
    DfMessageDao messageDao;

    @Override
    public int getConvertViewRes() {
        return R.layout.my_leavemessage_list;
    }

    public boolean edit_open() {
        if (getActivity() != null && adapter != null && adapter.getCount() > 0) {
            layOption.setVisibility(View.VISIBLE);
            adapter.setShow(true);
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void edit_close() {
        if (getActivity() != null) {
            layOption.setVisibility(View.GONE);
            title_right_icon.setVisibility(View.VISIBLE);
            adapter.setShow(false);
            for (int i = 0; i < adapter.getCount(); i++) {
                ChatListBean bean = (ChatListBean) adapter.getItem(i);
                bean.setChoosed(false);
            }
            selectAll.setChecked(false);
            adapter.notifyDataSetChanged();
            listId.clear();
        }
    }

    @Override
    public boolean needBack() {
        return false;
    }

    @Override
    public void lazyInit() {
        super.init();

        user = ac.getUser();
        chatListBeanDao = DBHelper.chatListBeanDao;
        messageDao = DBHelper.dfMessageDao;

        initAdapter();
        String[] str = new String[]{REFEREMSGCOUNT, ADDMSG, REMOVE, UPDATE};
        receiveBroadcast(str, broadcast);
    }

    @Override
    public boolean needTitle() {
        return false;
    }

    @Override
    public void init() {
        super.init();
    }

    public void initAdapter() {
        swipe_layout.setOnRefreshListener(this);
        adapter = new LeaveMessageAdapter(getActivity());
        adapter.handler = handler;
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                ChatListBean mlb = (ChatListBean) adapter.getItem(position);
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = mlb;
                msg.sendToTarget();
                return true;
            }
        });
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                ChatListBean bean = adapter.getItem(position);
                User user = new User(bean.getFriendid(), bean.getUserlogo(), bean.getNickname());
                ChatActivity.ChatType type = ChatActivity.ChatType.friend;
                if (bean.getType() == 1) {
                    type = ChatActivity.ChatType.group;
                }
                ChatActivity_.intent(getActivity()).type(type).friend(user).startForResult(1);
            }
        });
        adapter.setCheckBoxInterface(this);
        swipeRefresh();
    }

    @UiThread(delay = 200)
    public void swipeRefresh() {
        swipe_layout.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        findChatListAll();
    }

    @Background
    public void findChatListAll() {
        final List<ChatListBean> list = chatListBeanDao.getAllMyChatList(user.getId());
        if (list != null && list.size() > 0) {
            for (ChatListBean c : list) {
                if (listId.contains(c.getFriendid())) {
                    c.setChoosed(true);
                }
                c.setMsgCount(messageDao.getUnReadMessageCount(user.getId(), c.getFriendid()));
            }
        }
        if (getActivity() != null) {
            notifyData(list);
        }
    }

    @UiThread
    void notifyData(List<ChatListBean> list) {

        if (list != null && list.size() > 0) {
            empty_view.setVisibility(View.GONE);
            adapter.setList(list);
        } else {
            empty_view.setVisibility(View.VISIBLE);
        }

        if (adapter.getList() != null && adapter.getList().size() > 0) {
            empty_view.setVisibility(View.GONE);
        } else {
            empty_view.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        swipe_layout.setRefreshing(false);
    }

    @UiThread
    public void isEmpty() {
        if (adapter.getCount() == 0) {
            empty_view.setVisibility(View.VISIBLE);
        } else {
            empty_view.setVisibility(View.GONE);
        }
    }


    Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            final ChatListBean bean = (ChatListBean) msg.obj;
            switch (msg.what) {
                case 1:
                    new AlertDialog.Builder(getActivity()).setMessage("你确定要删除这条记录吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHelper.chatListBeanDao.delete(bean);
                            DBHelper.dfMessageDao.deleteAllMessageByFriendId(user.getId(), bean.getFriendid());
                            final int position = adapter.getList().indexOf(bean);
                            if (position > -1) {
                                adapter.remove(position);
                                adapter.notifyDataSetChanged();
                                isEmpty();
                            }
                            getActivity().sendBroadcast(new Intent(ChatActivity.ADDMSG));
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    }).create().show();
                    break;
            }
            super.dispatchMessage(msg);
        }

        ;
    };


    Handler broadcast = new Handler() {
        public void dispatchMessage(Message msg) {
            Intent i = (Intent) msg.obj;
            String action = i.getAction();
            if (action.equals(REFEREMSGCOUNT)) {
                List<ChatListBean> list = adapter.getList();
                for (ChatListBean messageListBean : list) {
                    if (messageListBean.getFriendid().equals(i.getStringExtra("id"))) {
                        messageListBean.setMsgCount(messageDao.getUnReadMessageCount(user.getId(), messageListBean.getFriendid()));
                    }
                }
                adapter.notifyDataSetChanged();
            } else if (action.equals(ADDMSG)) {
                ChatListBean bean = (ChatListBean) i.getExtras().getSerializable("bean");
                bean.setMsgCount(messageDao.getUnReadMessageCount(user.getId(), bean.getFriendid()));

                ChatListBean temp = null;
                List<ChatListBean> list = adapter.getList();
                for (ChatListBean messageListBean : list) {
                    if (messageListBean.getFriendid().equals(bean.getFriendid())) {
                        temp = messageListBean;
                    }
                }
                if (temp != null) {
                    adapter.remove(temp);
                }
                adapter.add(0, bean);
                if (listId.contains(bean.getFriendid())) {
                    bean.setChoosed(true);
                    checkChild(0, true);
                } else {
                    bean.setChoosed(false);
                    checkChild(0, false);
                }
                adapter.notifyDataSetChanged();
            } else if (action.equals(REMOVE)) {
                String friendId = i.getStringExtra("friendId");
                List<ChatListBean> list = adapter.getList();
                ChatListBean temp = null;
                for (ChatListBean messageListBean : list) {
                    if (messageListBean.getFriendid().equals(friendId)) {
                        temp = messageListBean;
                    }
                }
                if (temp != null) {
                    adapter.remove(temp);
                }
                adapter.notifyDataSetChanged();
            } else if (action.equals(UPDATE)) {
                ChatListBean bean = (ChatListBean) i.getExtras().getSerializable("bean");
                bean.setMsgCount(messageDao.getUnReadMessageCount(user.getId(), bean.getFriendid()));

                int position = -1;
                List<ChatListBean> list = adapter.getList();
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getFriendid().equals(bean.getFriendid())) {
                        position = j;
                    }
                }
                if (position != -1) {
                    adapter.remove(position);
                }
                adapter.add(position, bean);
                adapter.notifyDataSetChanged();
            }
            isEmpty();
            super.dispatchMessage(msg);
        }
    };

    @Click(R.id.checkbox_select_all)
    void setSelectAll() {
        for (int i = 0; i < adapter.getCount(); i++) {
            ChatListBean bean = (ChatListBean) adapter.getItem(i);
            bean.setChoosed(selectAll.isChecked());
            if (selectAll.isChecked()) {
                add(bean);
            } else {
                remove(bean);
            }
        }
        adapter.notifyDataSetChanged();
    }

    List<String> listId = new ArrayList<String>();

    @Override
    public void checkChild(int position, boolean isChecked) {
        boolean allGroupSameState = true;
        for (int i = 0; i < adapter.getCount(); i++) {
            ChatListBean bean = adapter.getItem(i);
            if (isChecked != bean.isChoosed()) {
                allGroupSameState = false;
                break;
            }
        }
        if (allGroupSameState) {
            selectAll.setChecked(isChecked);
        } else {
            selectAll.setChecked(false);
        }
        ChatListBean bean = adapter.getItem(position);
        if (bean.isChoosed()) {
            add(bean);
        } else {
            remove(bean);
        }
        adapter.notifyDataSetChanged();
    }

    void add(ChatListBean bean) {
        if (listId.contains(bean.getFriendid())) {
            return;
        }
        listId.add(bean.getFriendid());
    }

    void remove(ChatListBean bean) {
        if (listId.contains(bean.getFriendid())) {
            listId.remove(bean.getFriendid());
        }
    }

    @Click(R.id.read_tv)
    void isReaded() {
        if (DBHelper.dfMessageDao.getAllUnReadMessageCount(user.getId()) < 1) {
            showCustomToast("没有未读记录");
            return;
        }
        List<ChatListBean> list = adapter.getList();
        final List<ChatListBean> readlist = new ArrayList<ChatListBean>();
        for (ChatListBean cb : list) {
            if (cb.isChoosed()) {
                readlist.add(cb);
            }
        }
        if (readlist.size() == 0) {
            showCustomToast("请选中未读记录");
            return;
        }
        new AlertDialog.Builder(getActivity()).setMessage("确定将选中的记录标记为已读？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                swipe_layout.setEnabled(false);
                markerMsg(readlist);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        }).create().show();

    }

    ProgressDialog deleteProgress;

    @Click(R.id.delete_tv)
    void isDelete() {
        int count = 0;
        List<ChatListBean> list = adapter.getList();
        final List<ChatListBean> deletelist = new ArrayList<ChatListBean>();
        for (ChatListBean cb : list) {
            if (cb.isChoosed()) {
                count += 1;
                deletelist.add(cb);
            }
        }
        if (count == 0) {
            showCustomToast("请选中记录");
            return;
        }
        new AlertDialog.Builder(getActivity()).setMessage("确定删除选中的记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                swipe_layout.setEnabled(false);
                if (deleteProgress == null) {
                    deleteProgress = Util.progress(getActivity(), "正在删除", false);
                }
                deleteProgress.show();
                deleteMsg(deletelist);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        }).create().show();
    }

    @Background
    void deleteMsg(List<ChatListBean> deletelist) {
        adapter.removeAll(deletelist);
        notifyData();
        for (ChatListBean cb : deletelist) {
            remove(cb);
        }
        isEmpty();
        for (ChatListBean cb : deletelist) {
            DBHelper.chatListBeanDao.delete(cb);
            DBHelper.dfMessageDao.deleteAllMessageByFriendId(user.getId(), cb.getFriendid());
        }
        //更新导航消息数字
        getActivity().sendBroadcast(new Intent(ChatActivity.ADDMSG));
        dimiss();
    }

    @UiThread
    void dimiss() {
        if (deleteProgress != null && deleteProgress.isShowing()) {
            deleteProgress.dismiss();
        }
    }

    @UiThread
    void notifyData() {
        adapter.notifyDataSetChanged();
        swipe_layout.setEnabled(true);
    }

    @Background
    void markerMsg(List<ChatListBean> readlist) {
        if (readlist != null && readlist.size() > 0) {
            for (ChatListBean c : readlist) {
                //未读消息队列
                List<Integer> ids = new ArrayList<Integer>();
                final List<DfMessage> dfMList = DBHelper.dfMessageDao.getMsgList(user.getId(), c.getFriendid(), -1);
                for (DfMessage dfMessage : dfMList) {
                    //将未读变为已读
                    if (dfMessage.getIsRead() == 0) {
                        ids.add(dfMessage.getId());
                        dfMessage.setIsRead(1);
                    }
                }
                //更新未读消息变为已读
                if (ids.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Integer integer : ids) {
                        sb.append(integer + ",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    DBHelper.dfMessageDao.updateMsgReaded(sb.toString());
                }
                c.setMsgCount(0);
                notifyData();
            }
        }
        //更新导航消息数字
        getActivity().sendBroadcast(new Intent(ChatActivity.ADDMSG));
    }
}
