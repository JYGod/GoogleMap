package com.edu.hrbeu.googlemap.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.hrbeu.googlemap.R;
import com.edu.hrbeu.googlemap.databinding.ActivityMapsBinding;
import com.edu.hrbeu.googlemap.databinding.NavHeaderMainBinding;
import com.edu.hrbeu.googlemap.pojo.RoutesPOJO;
import com.edu.hrbeu.googlemap.service.IMapQuery;
import com.edu.hrbeu.googlemap.utils.AnimUtil;
import com.edu.hrbeu.googlemap.utils.DrawableUtil;
import com.edu.hrbeu.googlemap.utils.ImgLoadUtil;
import com.edu.hrbeu.googlemap.utils.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.edu.hrbeu.googlemap.service.IMapQuery.retrofit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,GoogleMap.OnMyLocationButtonClickListener ,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationSource.OnLocationChangedListener,GoogleMap.OnCameraMoveListener,GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,GoogleMap.OnCameraMoveCanceledListener{

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
    private Context mContext;
    private final static int ALPHA_ADJUSTMENT = 0x77000000;
    private final double[] longs=new double[]{126.681032,126.682234,126.678178};
    private final double[] lats=new double[]{45.775246,45.774438,45.774168};
    private LatLng centerPoint;
    private LatLng centerPointTemp;
    private PolylineOptions polyLine;
    private Polyline polylines;
    private ImageView ivWheel;
    private LatLngBounds latLngBounds;
    private Marker mCurrentMarker;
    private Marker markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext=this;
        //从保存的实例中获取位置
        if (savedInstanceState!=null){
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosiotion = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mBinding= DataBindingUtil.setContentView(this, com.edu.hrbeu.googlemap.R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initGoogleServices();
        initDrawerLayout();
        clickListener();

    }

    private void initCamera() {
       // mMarker=new MarkerOptions().position(mDefaultLocation).title("我的位置");
        LatLng currentLatLng=null;
        if (mLastKnownLocation!=null){
            currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            //mMap.addMarker(mMarker);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,14));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16),2000,null);
        }else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
       // mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

        updateLocationUI();

        getDeviceLocation();
        initCamera();
        initNearMarkers();

        Log.e(TAG,"onMapReady");

        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

    }

    private void initNearMarkers() {
        BitmapDescriptor  icon = BitmapDescriptorFactory.fromBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.markers),120,130));
        for (int i=0;i<lats.length;i++){
            markers = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lats[i], longs[i]))
                    .icon(icon));
        }
        mMap.setOnMarkerClickListener(this);
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

    @Override
    public void onLocationChanged(Location location) {
        LatLng centerPoint = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        Log.e("中心点经纬度------->",String.valueOf(centerPoint.longitude)+"\n"+String.valueOf(centerPoint.latitude));

        Log.e("当前经纬度------->",String.valueOf(location.getLongitude())+"\n"+String.valueOf(location.getLatitude()));
    }

    @Override
    public void onCameraMove() {
         centerPoint=  mMap.getCameraPosition().target;
       // Log.e("中心点经纬度：  ",String.valueOf(centerPoint.longitude)+"\n"+String.valueOf(centerPoint.latitude));
    }


    /**
     * 绘制路线
     * @param marker
     */
    private void drawPolyLine(Marker marker,DialogPlus dialogPlus) {
        Map<String,String>map=new HashMap<>();
        centerPointTemp=centerPoint;
        if (mBinding.mapCenter.getVisibility()!=View.GONE){
            map.put("origin", String.valueOf(centerPoint.latitude)+","+ String.valueOf(centerPoint.longitude));
        }else {
            map.put("origin", String.valueOf(mCurrentMarker.getPosition().latitude)+","+ String.valueOf(mCurrentMarker.getPosition().longitude));
        }
        map.put("destination", String.valueOf(marker.getPosition().latitude)+
                ","+String.valueOf(marker.getPosition().longitude));
        map.put("sensor","false");
        map.put("mode","walking");
        IMapQuery queryRoutes = retrofit.create(IMapQuery.class);
        Call<RoutesPOJO>call = queryRoutes.getRoutes(map);
        call.enqueue(new Callback<RoutesPOJO>() {
            @Override
            public void onResponse(Call<RoutesPOJO> call, Response<RoutesPOJO> response) {
                clearPolyLines();
                RoutesPOJO routesPOJO=response.body();
                String routeCode = routesPOJO.getRoutes().get(0).getOverview_polyline().getPoints();
                List<LatLng> line= PolyUtil.decode(routeCode);
                polyLine = new PolylineOptions();
                double tolerance = 10; // meters
                List<LatLng> simplifiedLine = PolyUtil.simplify(line, tolerance);
                polylines = mMap.addPolyline(polyLine.addAll(simplifiedLine)
                        .width(20)
                        .color(getResources().getColor(R.color.routeGreen) - ALPHA_ADJUSTMENT));
                ivWheel.clearAnimation();
                dialogPlus.dismiss();
                RoutesPOJO.Route.Bound bounds = routesPOJO.getRoutes().get(0).getBounds();
                latLngBounds=new LatLngBounds(new LatLng(bounds.getSouthwest().getLat(),bounds.getSouthwest().getLng()),
                        new LatLng(bounds.getNortheast().getLat(),bounds.getNortheast().getLng()));
                RoutesPOJO.Route.Leg leg = routesPOJO.getRoutes().get(0).getLegs().get(0);
                moveCameraFixRoute(leg.getDistance().getText(),leg.getDuration().getText());

            }

            @Override
            public void onFailure(Call<RoutesPOJO> call, Throwable t) {
                Log.e("请求失败：","error");
                ivWheel.clearAnimation();
                dialogPlus.dismiss();
            }
        });
    }

    /**
     * 移动镜头适应路径
     */
    private void moveCameraFixRoute(String distance,String duration) {
        BitmapDescriptor  icon = BitmapDescriptorFactory.fromBitmap(DrawableUtil.zoomDrawable(getResources().getDrawable(R.drawable.marker),60,90));
        MarkerOptions markerOptions=null;
       if (mBinding.mapCenter.getVisibility()!=View.GONE){
            markerOptions = new MarkerOptions()
                   .position(centerPointTemp)
                   .icon(icon)
                   .title("距您" + distance + "\n" + "步行" + duration);
           mCurrentMarker = mMap.addMarker(markerOptions);
           Log.e("添加的marker ID：",mCurrentMarker.getId());
           mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,200),1500,null);
        //   mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom),2000,null);
       }
        mBinding.mapCenter.setVisibility(View.GONE);
        mCurrentMarker.showInfoWindow();
     //   mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,200));
        mMap.setOnMarkerClickListener(this);
    }

    private void clearPolyLines() {
        if (polylines!=null){
            polylines.remove();
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("markerId",marker.getId());
        if (mCurrentMarker!=null&&marker.getId().equals(mCurrentMarker.getId())){
            mBinding.mapCenter.setVisibility(View.VISIBLE);
            mCurrentMarker.remove();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPointTemp,mMap.getCameraPosition().zoom));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16),1500,null);
            clearPolyLines();
        }else {
            final DialogPlus dialog = DialogPlus.newDialog(mContext)
                    .setContentHolder(new ViewHolder(R.layout.route_dialog))
                    .setCancelable(false)
                    .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setContentHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setGravity(Gravity.CENTER)
                    .create();
            Animation rotate= AnimationUtils.loadAnimation(this,R.anim.rotate_anim);
            ivWheel=  (ImageView)dialog.getHolderView().findViewById(R.id.iv_wheel);
            ivWheel.setAnimation(rotate);
            ivWheel.startAnimation(rotate);
            dialog.show();
            drawPolyLine(marker,dialog);
        }
        return true;
    }


    @Override
    public void onCameraMoveCanceled() {
        View view=mBinding.mapCenter;
        view.clearAnimation();
        Log.e("Camera canceled","canceled");
        AnimUtil.animateJump(view);
    }

    @Override
    public void onCameraIdle() {
        View view=mBinding.mapCenter;
        Log.e("Camera canceled","canceled");
        AnimUtil.animateJump(view);
    }
}
