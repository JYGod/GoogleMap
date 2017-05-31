package com.edu.hrbeu.googlemap.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    public final String name;
    public final int profilePhoto;

    public MyItem(double lat, double lng,String name, int pictureResource) {
        mPosition = new LatLng(lat, lng);
        this.name = name;
        profilePhoto = pictureResource;
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }

}
