package com.quanliren.quan_one.activity.through;


import android.graphics.Bitmap;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.bean.CustomFilterBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.ThroughImageView;
import com.quanliren.quan_one.dao.DBHelper;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;
import com.quanliren.quan_one.util.http.MyJsonHttpResponseHandler;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@EActivity(R.layout.through_map)
public class ThroughActivity extends BaseActivity implements LocationSource,
        AMapLocationListener, AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnMarkerClickListener {
    public static final LatLng BEIJING = new LatLng(39.908691, 116.397506);// 北京市经纬度

    private static final String MAP_FRAGMENT_TAG = "map";
    AMap amap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private SupportMapFragment map;

    @ViewById(R.id.tv_position)
    TextView tv_position;


    @Override
    public void init() {
        super.init();

        CameraPosition LUJIAZUI = new CameraPosition.Builder().target(BEIJING)
                .zoom(15).bearing(0).tilt(0).build();
        AMapOptions aOptions = new AMapOptions();
        aOptions.camera(LUJIAZUI);
        if (map == null) {
            map = SupportMapFragment.newInstance(aOptions);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.add(R.id.map, map, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();

        }
        Util.umengCustomEvent(mContext, "near_through_btn");
    }

    @Override
    public void rightClick(View v) {

        LatLng mTarget = amap.getCameraPosition().target;
        ThroughListActivity_.intent(this).ll(mTarget).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (amap == null) {
            amap = map.getMap();// amap对象初始化成功
            setUpMap();
        }
        title.setText("会员漫游");
        setTitleRightTxt("列表");
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (locationClient == null) {
            locationClient = new AMapLocationClient(getApplicationContext());
            locationOption = new AMapLocationClientOption();
            // 设置定位模式为高精度模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置定位监听
            locationClient.setLocationListener(this);
            locationClient.startLocation();
        }
    }

    private void setUpMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));
        myLocationStyle.radiusFillColor(0x1902bce4);
        myLocationStyle.strokeColor(0x3302bce4);
        myLocationStyle.strokeWidth(1);
        amap.setMyLocationStyle(myLocationStyle);
        amap.setLocationSource(this);
        amap.setOnMarkerClickListener(this);
        amap.getUiSettings().setMyLocationButtonEnabled(true);
        amap.setMyLocationEnabled(true);
        amap.setOnCameraChangeListener(this);
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
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

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        tv_position.setVisibility(View.GONE);
    }

    private LatLng searchLL = null;

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        LatLng mTarget = amap.getCameraPosition().target;
        searchLL = mTarget;
        LatLonPoint lp = new LatLonPoint(mTarget.latitude, mTarget.longitude);
        RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

        if (!Util.isFastDoubleClick()) {
            getUserList();
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        try {
            if (amap == null || amap.getCameraPosition() == null) {
                return;
            }
            LatLng mTarget = amap.getCameraPosition().target;
            if (mTarget.longitude == searchLL.longitude && mTarget.latitude == searchLL.latitude) {
                if (rCode == 1000) {
                    if (result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null) {
                        String addressName = result.getRegeocodeAddress().getFormatAddress();
                        tv_position.setVisibility(View.VISIBLE);
                        tv_position.setText(addressName);
                    } else {
                        tv_position.setVisibility(View.GONE);
                    }
                } else {
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null && aLocation.getErrorCode() == 0) {
            mListener.onLocationChanged(aLocation);
            isLocationFinish.compareAndSet(false, true);
            onCameraChangeFinish(null);
        }
        deactivate();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        deactivate();
    }

    /**
     * 无用
     *
     * @param geocodeResult
     * @param i
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    List<Marker> markers = new ArrayList<>();
    AtomicBoolean isLoading = new AtomicBoolean(false);
    AtomicBoolean isLocationFinish = new AtomicBoolean(false);

    public void getUserList() {
        if (!isLocationFinish.get()) {
            return;
        }
        if (isLoading.get()) {
            return;
        }
        isLoading.compareAndSet(false, true);
        for (Marker marker : markers) {
            marker.remove();
            marker.destroy();
        }
        markers.clear();

        LatLng mTarget = amap.getCameraPosition().target;
        RequestParams params = getAjaxParams();
        params.put("p", "0");
        List<CustomFilterBean> listCB = DBHelper.customFilterBeanDao.getAllFilter();
        if (listCB != null)
            for (CustomFilterBean cfb : listCB) {
                if ("sex_through".equals(cfb.key)) {
                    params.put("sex", cfb.id + "");
                }
                if ("actime_through".equals(cfb.key)) {
                    params.put("actime", cfb.id + "");
                }
            }
        params.put("longitude", String.valueOf(mTarget.longitude));
        params.put("latitude", String.valueOf(mTarget.latitude));
        params.put("type", 1);
        ac.finalHttp.post(URL.NearUserList, params, callBack);
    }

    MyJsonHttpResponseHandler callBack = new MyJsonHttpResponseHandler() {
        @Override
        public void onSuccessRetCode(JSONObject jo) throws Throwable {
            jo = jo.getJSONObject(URL.RESPONSE);
            List<User> list = new Gson().fromJson(jo.getString(URL.LIST), new TypeToken<ArrayList<User>>() {
            }.getType());
            String loginUserId = ac.getUser().getId();
            for (User user : list) {
                if (!user.getId().equals(loginUserId) && Double.valueOf(user.getLatitude()) != 0 && Double.valueOf(user.getLongitude()) != 0) {
                    addStoreMarket(user);
                }
            }
            isLoading.compareAndSet(true, false);
        }

        @Override
        public void onFailRetCode(JSONObject jo) {
            super.onFailRetCode(jo);
            isLoading.compareAndSet(true, false);
        }

        @Override
        public void onFailure() {
            super.onFailure();
            isLoading.compareAndSet(true, false);
        }

    };

    ThroughImageView tiv;

    public void addStoreMarket(final User userbean) {

        ImageLoader.getInstance().loadImage(userbean.getAvatar() + StaticFactory._160x160, ac.options_userlogo, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    return;
                }
                if (tiv == null) {
                    tiv = new ThroughImageView(ThroughActivity.this);
                }
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(Double.valueOf(userbean.getLatitude()), Double.valueOf(userbean.getLongitude())));
                markerOption.draggable(true);
                tiv.setmBitmap(loadedImage);
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(tiv.getmBitmap()));
                Marker marker = amap.addMarker(markerOption);
                marker.setObject(userbean);
                markers.add(marker);
            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object object = marker.getObject();
        User userbean = object instanceof User ? (User) object : null;
        Util.startUserInfoActivity(this, userbean);
        return true;
    }
}
