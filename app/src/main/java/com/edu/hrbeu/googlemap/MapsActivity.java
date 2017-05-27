package com.edu.hrbeu.googlemap;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.edu.hrbeu.googlemap.databinding.ActivityMapsBinding;
import com.edu.hrbeu.googlemap.databinding.NavHeaderMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,GoogleMap.OnMyLocationButtonClickListener ,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String KEY_LOCATION = "location";
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFALT_ZOOM = 16;
    private GoogleMap mMap;
    private ActivityMapsBinding mBinding;
    private NavHeaderMainBinding bind;
    private boolean mPermissionDenied = false;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosiotion;
    private boolean mLocationPermissionGranted;
    private LatLng mDefaultLocation = new LatLng(39.8423, 116.4989955);
    private MarkerOptions mMarker;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext=this;
        //从保存的实例中获取位置
        if (savedInstanceState!=null){
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosiotion = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initGoogleServices();
        initDrawerLayout();
        clickListener();

    }

    private void initMarker() {
       // mMarker=new MarkerOptions().position(mDefaultLocation).title("我的位置");
        LatLng currentLatLng=null;
        if (mLastKnownLocation!=null){
            currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            //mMap.addMarker(mMarker);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,12));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16),2000,null);
        }else {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,12));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16),2000,null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap!=null){
            outState.putParcelable(KEY_CAMERA_POSITION,mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION,mLastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    private void initGoogleServices() {
        if (mGoogleApiClient==null){
         mGoogleApiClient=new GoogleApiClient.Builder(this)
                 .enableAutoManage(this,this)
         .addConnectionCallbacks(this)
         .addOnConnectionFailedListener(this)
         .addApi(LocationServices.API)
                 .addApi(Places.GEO_DATA_API)
                 .addApi(Places.PLACE_DETECTION_API)
         .build();
         }
         mGoogleApiClient.connect();

    }



    private void clickListener() {
        mBinding.include.ivTitleMenu.setOnClickListener(this);
        mBinding.bottom.btnLocate.setOnClickListener(this);
        mBinding.bottom.btnScan.setOnClickListener(this);

    }

    private void initDrawerLayout() {
        mBinding.navView.inflateHeaderView(R.layout.nav_header_main);
        View headerView=mBinding.navView.getHeaderView(0);
        bind=DataBindingUtil.bind(headerView);
        ImgLoadUtil.displayCircle(bind.ivHead,"http://img3.duitang.com/" +
                "uploads/item/201409/22/20140922122621_fxvj8.thumb.224_0.jpeg");

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout)findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        updateLocationUI();

        getDeviceLocation();
        initMarker();
        Log.e(TAG,"onMapReady");

        /**
         *     LatLng beijing = new LatLng(39.860070, 116.467220);
         mMap.addMarker(new MarkerOptions().position(beijing).title("我的位置"));
         mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(beijing,12));
         mMap.animateCamera(CameraUpdateFactory.zoomTo(16),2000,null);
         */

    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted=true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted){
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastKnownLocation != null){
                Log.e("mLastKnownLocation:","经度："+mLastKnownLocation.getLongitude()+"  纬度："+mLastKnownLocation.getLatitude());
            }
        }

        if (mCameraPosiotion != null){
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosiotion));
        } else if (mLastKnownLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),DEFALT_ZOOM)
            );
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFALT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    private void updateLocationUI() {
        if (mMap == null){
            return;
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void showCurrentPlace(){
        if (mMap == null){
            return;
        }
        if (mLocationPermissionGranted){
            if (mLastKnownLocation != null){
                Log.e("定位成功:","经度："+mLastKnownLocation.getLongitude()+"  纬度："+mLastKnownLocation.getLatitude());
                LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
               // mMarker.position(currentLatLng);
                getDeviceLocation();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                // 不退出程序，进入后台
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.iv_title_menu:
                mBinding.drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.btn_locate:
                showCurrentPlace();
                break;
            case R.id.btn_scan:
                intent.setClass(mContext,ScannerActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied){
            showMissingPermissionError();
            mPermissionDenied=false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(),"dialog");
    }

    //连接GoogleService
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //暂停
    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Play services connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }
}
