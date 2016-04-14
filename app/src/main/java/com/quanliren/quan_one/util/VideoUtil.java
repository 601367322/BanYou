package com.quanliren.quan_one.util;

import android.content.Context;
import android.content.Intent;

import java.io.File;

//import com.alibaba.sdk.android.AlibabaSDK;
//import com.alibaba.sdk.android.callback.FailureCallback;
//import com.alibaba.sdk.android.callback.InitResultCallback;
//import com.duanqu.qupai.engine.session.VideoSessionCreateInfo;
//import com.duanqu.qupai.sdk.android.QupaiService;
//import com.duanqu.qupaisample.common.Contant;
//import com.duanqu.qupaisample.common.RequestCode;
//import com.duanqu.qupaisample.result.RecordResult;
//import com.duanqu.qupaisample.utils.AppConfig;
//import com.duanqu.qupaisample.utils.AppGlobalSetting;
//import com.google.common.io.Files;
//import com.quanliren.quan_one.activity.BuildConfig;

/**
 * Created by Shen on 2016/1/26.
 */
public class VideoUtil {

    private static VideoUtil instance;

    private Context context;

    public VideoUtil(Context context) {
        this.context = context;
    }

    public static synchronized VideoUtil getInstance(Context context) {
        if (instance == null) {
            instance = new VideoUtil(context.getApplicationContext());
        }
        return instance;
    }


    public void initVideoSdk(final Context context) {
        /*if(BuildConfig.DEBUG) {
            AlibabaSDK.setSecGuardImagePostfix("aaa");
        }
        AlibabaSDK.asyncInit(context, new InitResultCallback() {
            @Override
            public void onSuccess() {
                QupaiService qupaiService = AlibabaSDK
                        .getService(QupaiService.class);

                VideoSessionCreateInfo info = new VideoSessionCreateInfo.Builder()
                        .setOutputDurationLimit(Contant.DEFAULT_DURATION_LIMIT)
                        .setOutputVideoBitrate(Contant.DEFAULT_BITRATE)
                        .setHasImporter(false)
//                        .setWaterMarkPath(Contant.WATER_MARK_PATH)
                        .setWaterMarkPosition(-1)
                        .setHasEditorPage(true)
                        .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)
                        .setBeautyProgress(100)
                        .setBeautySkinOn(true)
                        .build();

                qupaiService.initRecord(info);
                qupaiService.hasMroeMusic(null);

                if (qupaiService != null) {
                    qupaiService.addMusic(0, "Athena", "assets://Qupai/music/Athena");
                    qupaiService.addMusic(1, "Box Clever", "assets://Qupai/music/Box Clever");
                    qupaiService.addMusic(2, "Byebye love", "assets://Qupai/music/Byebye love");
                    qupaiService.addMusic(3, "chuangfeng", "assets://Qupai/music/chuangfeng");
                    qupaiService.addMusic(4, "Early days", "assets://Qupai/music/Early days");
                    qupaiService.addMusic(5, "Faraway", "assets://Qupai/music/Faraway");
                }
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });*/
    }

    public void startRecording(final Context context) {

        /*final AppGlobalSetting sp = new AppGlobalSetting(context.getApplicationContext());
        Boolean isGuideShow = sp.getBooleanGlobalItem(
                AppConfig.PREF_VIDEO_EXIST_USER, true);

        QupaiService qupaiService = AlibabaSDK
                .getService(QupaiService.class);

        qupaiService.showRecordPage((Activity) context, RequestCode.RECORDE_SHOW, isGuideShow,
                new FailureCallback() {
                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(context, "onFailure:" + s + "CODE" + i, Toast.LENGTH_LONG).show();
                    }
                });

        sp.saveGlobalConfigItem(
                AppConfig.PREF_VIDEO_EXIST_USER, false);*/
    }

    public File[] getVideoFiles(Context context, int resultCode, Intent data) {
        /*if (resultCode == Activity.RESULT_OK) {
            RecordResult result = new RecordResult(data);
            //得到视频地址，和缩略图地址的数组，返回十张缩略图
            String videoPath = result.getPath();
            String[] thum = result.getThumbnail();

            File[] files = new File[2];
            try {
                String newFileName = String.valueOf(new Date().getTime());
                String newVideoPath = StaticFactory.APKCardPathChatVideo + newFileName + ".mp4";
                String newThumPath = StaticFactory.APKCardPathChatVideo + newFileName + "_thumb.jpg";
                Files.move(new File(videoPath), files[0] = new File(newVideoPath));
                Files.move(new File(thum[0]), files[1] = new File(newThumPath));

            } catch (IOException e) {
                Toast.makeText(context, "拷贝失败", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            QupaiService qupaiService = AlibabaSDK
                    .getService(QupaiService.class);
            qupaiService.deleteDraft(context.getApplicationContext(), data);

            return files;
        }*/
        return null;
    }
}

