package com.zhm.duxiangle.provider;

import android.content.Context;

import com.zhm.duxiangle.DXLApplication;
import com.zhm.duxiangle.utils.ToastUtils;

import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2015/12/2.
 */
public class LocationProvider implements RongIM.LocationProvider{
    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {
        ToastUtils.showToast(context,"地图");
    }
}
