package com.quanliren.quan_one.activity.user;

import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.VideoDownBean;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.dao.DfMessageDao;
import com.quanliren.quan_one.service.SocketManage;
import com.quanliren.quan_one.util.StaticFactory;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Shen on 2015/9/17.
 */
public class ChatDownLoadManager {

    private List<DfMessage> downlist = new ArrayList<>();

    private static ChatDownLoadManager instance;

    private Context context;

    private AppClass ac;

    private DfMessageDao messageDao;

    public static synchronized ChatDownLoadManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChatDownLoadManager(context.getApplicationContext());
        }
        return instance;
    }

    public ChatDownLoadManager(Context context) {
        this.context = context;
        this.ac = (AppClass) context.getApplicationContext();
        this.messageDao = DBHelper.dfMessageDao;
    }

    public void down(DfMessage msg) {
        for (int i = 0; i < downlist.size(); i++) {
            if (downlist.get(i).getMsgid().equals(msg.getMsgid())) {
                //如果正在下载则不执行
                return;
            }
        }
        downlist.add(msg);
        String url = null;
        switch (msg.getMsgtype()) {
            case DfMessage.VOICE:
                url = msg.getContent();
                break;
        }
        ac.finalHttp.get(url, new fileDownload(msg, StaticFactory.APKCardPathChatVoice + msg.getContent().hashCode()));
    }

    class fileDownload extends FileAsyncHttpResponseHandler {
        DfMessage msg;

        public fileDownload(DfMessage msg, String filePath) {
            super(new File(filePath));
            this.msg = msg;
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            EventBus.getDefault().post(new VideoDownBean(msg, (int) bytesWritten, (int) totalSize));
        }

        public void onStart() {
            File file = new File(StaticFactory.APKCardPathChatVoice);
            if (!file.exists()) {
                file.mkdirs();
            }
            msg.setDownload(SocketManage.D_downloading);
            messageDao.update(msg);
            sendBoradcast(msg);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              Throwable throwable, File file) {

            msg.setDownload(SocketManage.D_destroy);
            messageDao.update(msg);
            sendBoradcast(msg);
            downlist.remove(msg);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, File file) {
            msg.setDownload(SocketManage.D_downloaded);
            switch (msg.getMsgtype()) {
                case DfMessage.VOICE:
                    msg.setContent(file.getPath());
                    break;
            }
            messageDao.update(msg);
            sendBoradcast(msg);
            downlist.remove(msg);
        }

    }

    public void sendBoradcast(DfMessage bean) {
        Intent i = new Intent(ChatActivity.CHANGESEND);
        i.putExtra("bean", bean);
        context.sendBroadcast(i);
    }

}
