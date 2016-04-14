package com.quanliren.quan_one.activity.location;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.model.LatLng;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.Area;
import com.quanliren.quan_one.fragment.date.ChosePositionFragment;
import com.quanliren.quan_one.util.Util;

import java.text.DecimalFormat;
import java.util.List;


public class GDLocation implements AMapLocationListener {

    private ILocationImpl locationListener;
    private boolean isNeedPoi = false;//创建群组手动刷新


    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private AppClass ac;
    private Context context;

    public GDLocation(Context context, ILocationImpl listener, boolean autoStart) {
        this.ac = (AppClass) context.getApplicationContext();
        this.context = context;
        this.locationListener = listener;
        if (autoStart) {
            startLocation();
        }
    }

    public void startLocation() {
        if (Util.isFastLocation() && !isNeedPoi) {
            if (locationListener != null) {
                locationListener.onLocationSuccess();
            }
            return;
        } else {
            locationClient = new AMapLocationClient(context.getApplicationContext());
            locationOption = new AMapLocationClientOption();
            // 设置定位模式为高精度模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置定位监听
            locationClient.setLocationListener(this);
            locationClient.startLocation();
        }
    }


    public void needLocationPoi(boolean isNeed) {
        this.isNeedPoi = isNeed;
    }

    @Override
    public void onLocationChanged(AMapLocation aLocation) {

        deactivate();
        if (aLocation == null
                || aLocation.getErrorCode() != 0) {
            if (locationListener != null)
                locationListener.onLocationFail();
            return;
        }
        DecimalFormat df = new DecimalFormat("#.######");
        if (!df.format(aLocation.getLatitude()).equals("0"))
            ac.cs.setLat(df.format(aLocation.getLatitude()));
        if (!df.format(aLocation.getLongitude()).equals("0"))
            ac.cs.setLng(df.format(aLocation.getLongitude()));
        String city = aLocation.getCity();
        if (city != null) {
            List<Area> areaList = ChosePositionFragment.getAreas();
            Area temp = null;
            for (Area area : areaList) {
                if (city.indexOf(area.name) > -1) {
                    temp = area;
                    break;
                }
            }
            if (temp != null) {
                ac.cs.setLocation(temp.name);
                ac.cs.setLocationID(temp.id);
                ac.cs.setArea(temp.name);
            }
        }
        Util.locationTime = System.currentTimeMillis();
        if (locationListener != null) {
            if (locationListener instanceof ILocationExactly) {
                LatLng latLng = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
                ((ILocationExactly) locationListener).onLocationSuccess(latLng);
            } else {
                locationListener.onLocationSuccess();
            }
        }

    }

    public void deactivate() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    public void destory() {
        deactivate();
    }
}
