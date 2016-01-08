package com.quanliren.quan_one.activity.seting;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.CircleImageView;
import com.quanliren.quan_one.custom.UserInfoLayout;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity
public class ShakeActivity extends BaseActivity implements SensorEventListener, Animation.AnimationListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private Animation shark_Annotation = null;
    @ViewById(R.id.img_shark)
    ImageView img_shark;
    @ViewById(R.id.user_ll)
    View user_ll;
    @ViewById(R.id.userlogo)
    CircleImageView userlogo;
    @ViewById(R.id.tv_shake)
    TextView tv_shake;
    @ViewById(R.id.userinfo)
    UserInfoLayout userinfo;
    @ViewById(R.id.distance)
    TextView distance;
    @ViewById(R.id.loading)
    View loading;
    User user = null;
    MediaPlayer mediaPlayer;
    MediaPlayer matchPlayer;
    AudioManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_shake);
        setTitleTxt("摇一摇");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        /**
         * 设置动画效果
         */
        this.shark_Annotation = AnimationUtils.loadAnimation(this, R.anim.shake);
        this.shark_Annotation.setAnimationListener(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.shake_sound_male);
        matchPlayer = MediaPlayer.create(this, R.raw.shake_match);
    }

    @Click(R.id.user_ll)
    public void goDetail(View view) {
        if (user_ll.getVisibility() == View.VISIBLE && user != null) {
            Util.startUserInfoActivity(this, user);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[SensorManager.DATA_X];
        float y = event.values[SensorManager.DATA_Y];
        float z = event.values[SensorManager.DATA_Z];
        if (Math.abs(x) >= 18 || Math.abs(y) >= 18 || Math.abs(z) >= 18) {//判断加速度>14时，这个值是可以修改的。
            img_shark.startAnimation(shark_Annotation);
            user_ll.setVisibility(View.INVISIBLE);
            tv_shake.setVisibility(View.GONE);

            ringerMode = am.getRingerMode();
            switch (ringerMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    break;
            }

            sensorManager.unregisterListener(this);//取消监听加速感应器，如果不取消的话会有问题，同学们可以自己去掉，狂摇试试
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {

        loading.setVisibility(View.GONE);
        if (isCanPost) {
            RequestParams params = getAjaxParams();
            params.put("longitude", ac.cs.getLng());
            params.put("latitude", ac.cs.getLat());
            ac.finalHttp.post(URL.SHAKESWEEP,params, new MyJsonHttpResponseHandler(mContext) {

                @Override
                public void onStart() {
                    loading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccessRetCode(JSONObject jo) throws Throwable {
                    sensorManager.registerListener(ShakeActivity.this, sensor, SensorManager.SENSOR_DELAY_GAME);//完成动画后再次监听加速感应器
                    isCanPost = true;
                    loading.setVisibility(View.GONE);

                    if (matchPlayer != null) {
                        matchPlayer.start();
                    }
                    user = new Gson().fromJson(jo.getString(URL.RESPONSE),
                            new TypeToken<User>() {
                            }.getType());
                    initUser();
                }

                @Override
                public void onFailRetCode(JSONObject jo) {
                    super.onFailRetCode(jo);
                    onFailure();
                }

                @Override
                public void onFailure() {
                    super.onFailure();
                    sensorManager.registerListener(ShakeActivity.this, sensor, SensorManager.SENSOR_DELAY_GAME);//完成动画后再次监听加速感应器
                    isCanPost = true;
                    loading.setVisibility(View.GONE);
                }

            });
            isCanPost = false;
        }

    }

    int ringerMode;

    boolean isCanPost = true;


    public void initUser() {
        if (user != null) {
            user_ll.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(user.getAvatar()+ StaticFactory._160x160, userlogo,ac.options_userlogo);
            userinfo.setUser(user);
//            nickname.setTextColor(getResources().getColor(R.color.enable));
            distance.setText("相距  " + Util.getDistance(Double.valueOf(ac.cs.getLng()),
                    Double.valueOf(ac.cs.getLat()), Double.valueOf(user.getLongitude()), Double.valueOf(user.getLatitude())) + "公里");
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {


    }

    @Override
    public void onAnimationStart(Animation animation) {


    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(ShakeActivity.this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
}
