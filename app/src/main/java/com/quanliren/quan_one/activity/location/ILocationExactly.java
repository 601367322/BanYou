package com.quanliren.quan_one.activity.location;

import com.amap.api.maps2d.model.LatLng;

public interface ILocationExactly extends ILocationImpl{
	void onLocationSuccess(LatLng latLng);
}
