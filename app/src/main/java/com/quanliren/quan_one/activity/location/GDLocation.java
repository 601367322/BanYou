package com.quanliren.quan_one.activity.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.Area;
import com.quanliren.quan_one.fragment.date.ChosePositionFragment;
import com.quanliren.quan_one.util.Util;

import java.text.DecimalFormat;
import java.util.List;


public class GDLocation implements AMapLocationListener {

    private ILocationImpl locationListener;

    public void setLocationListener(ILocationImpl locationListener) {
        this.locationListener = locationListener;
    }

    private LocationManagerProxy mAMapLocationManager;
    private AppClass ac;
    private Context context;

    public GDLocation(Context context, ILocationImpl listener, boolean autoStart) {
        this.ac = (AppClass) context.getApplicationContext();
        this.context = context;
        this.locationListener = listener;
        if (autoStart){
            startLocation();
        }
    }

    public void startLocation() {
        if (Util.isFastLocation()) {
            if (locationListener != null) {
                locationListener.onLocationSuccess();
            }
            return;
        } else {
            mAMapLocationManager = LocationManagerProxy.getInstance(context);
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, -1, 0, this);

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {


    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onLocationChanged(AMapLocation aLocation) {

        deactivate();
        if (aLocation == null
                || aLocation.getAMapException().getErrorCode() != 0) {
            if (locationListener != null)
                locationListener.onLocationFail();
            return;
        }
        DecimalFormat df = new DecimalFormat("#.######");
        if (!df.format(aLocation.getLatitude()).equals("0"))
            ac.cs.setLat(df.format(aLocation.getLatitude()));
        if (!df.format(aLocation.getLongitude()).equals("0"))
            ac.cs.setLng(df.format(aLocation.getLongitude()));
//        if (aLocation.getDistrict() != null)
//            ac.cs.setArea(aLocation.getDistrict());
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
                ac.cs.setLocationArea(aLocation.getPoiName());
            }
        }
        Util.locationTime = System.currentTimeMillis();
        if (locationListener != null)
            locationListener.onLocationSuccess();

    }

    public void deactivate() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }

    public void destory() {
        deactivate();
    }
}
