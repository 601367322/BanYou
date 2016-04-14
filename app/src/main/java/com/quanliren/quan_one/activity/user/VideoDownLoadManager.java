package com.quanliren.quan_one.activity.user;

import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.util.StaticFactory;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Shen on 2015/9/17.
 */
public class VideoDownLoadManager {

    private List<PlayVideoFragment.VideoDownBean> downlist = new ArrayList<>();

    private static VideoDownLoadManager instance;

    private Context context;

    private AppClass ac;

    public static synchronized VideoDownLoadManager getInstance(Context context) {
        if (instance == null) {
            instance = new VideoDownLoadManager(context.getApplicationContext());
        }
        return instance;
    }

    public VideoDownLoadManager(Context context) {
        this.context = context;
        this.ac = (AppClass) context.getApplicationContext();
    }

    public void down(PlayVideoFragment.VideoDownBean msg) {
        for (int i = 0; i < downlist.size(); i++) {
            if (downlist.get(i).path.equals(msg.path)) {
                //如果正在下载则不执行
                return;
            }
        }
        downlist.add(msg);
        String url = msg.path;
        ac.finalHttp.get(url, new fileDownload(msg, StaticFactory.APKCardPathChatVoice + msg.path.hashCode()));
    }

    class fileDownload extends FileAsyncHttpResponseHandler {
        PlayVideoFragment.VideoDownBean msg;

        public fileDownload(PlayVideoFragment.VideoDownBean msg, String filePath) {
            super(new File(filePath));
            this.msg = msg;
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            EventBus.getDefault().post(new PlayVideoFragment.VideoDownBean(msg.path, (int) bytesWritten, (int) totalSize));
        }

        public void onStart() {
            File file = new File(StaticFactory.APKCardPathChatVoice);
            if (!file.exists()) {
                file.mkdirs();
            }
            sendBoradcast(msg);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              Throwable throwable, File file) {

            sendBoradcast(msg);
            downlist.remove(msg);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, File file) {
            sendBoradcast(msg);
            downlist.remove(msg);
        }

    }

    public void sendBoradcast(PlayVideoFragment.VideoDownBean bean) {
        Intent i = new Intent(PlayVideoFragment.CHANGESEND);
        i.putExtra("bean", bean);
        context.sendBroadcast(i);
    }

}
