package com.octopus.mapmarks;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.octopus.mapmarks.overlay.DrivingRouteOverlay;
import com.octopus.mapmarks.overlay.MLatLonPoint;
import com.octopus.mapmarks.util.AMapUtil;
import com.octopus.mapmarks.util.FileUtils;
import com.octopus.mapmarks.util.ToastUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener {

    private Context mContext;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private RelativeLayout mBottomLayout, mHeadLayout;
    private TextView mRotueTimeDes, mRouteDetailDes;

    boolean isLocationChange = false; //是否定位完毕 默认没有
    boolean isGuihua = false; //是否定位完毕 默认没有

    private AMap aMap;
    private MapView mMapView = null;

    private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，39.942295,116.335891

    private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，39.995576,116.481288

    private ArrayList<MLatLonPoint> mubiaolist = new ArrayList<MLatLonPoint>(); //目的地列表

    private CARRouteSearchListener mRouteSearchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initView();

//        initData();

        String sHA1 = PkagUtils.sHA1(this);

        Log.d("", "sHA1:" + sHA1);

        initamap(savedInstanceState);

        testDeawMarker(); //画标记

        testLuxian();

        testCVIDBI();

    }

    private void testCVIDBI() {

        mubiaolist.clear();

        List<CVID_BJ> cvid_bjs = FileUtils.initCuidJson();

        for (int i = 0; i < cvid_bjs.size(); i++) {
            CVID_BJ cvid_bj = cvid_bjs.get(i);
            mubiaolist.add(new MLatLonPoint(Double.parseDouble(cvid_bj.getMLatitude()), Double.parseDouble(cvid_bj.getMLongitude()), cvid_bj.getName())); //北站

            if (i > 20){
                break;
            }
        }

    }

    private void initData() {

        //测试多点画图

        mubiaolist.clear();

        mubiaolist.add(new MLatLonPoint(39.945966, 116.353272, "北站")); //北站
        mubiaolist.add(new MLatLonPoint(39.866167, 116.377304, "南站")); // 南站
        mubiaolist.add(new MLatLonPoint(39.895148, 116.321343, "西站")); //西站
        mubiaolist.add(new MLatLonPoint(39.901733, 116.484421, "东站")); //东
        mubiaolist.add(new MLatLonPoint(39.90226, 116.426056, "中站")); //中

    }

    private void initView() {

        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mHeadLayout = (RelativeLayout) findViewById(R.id.routemap_header);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        mHeadLayout.setVisibility(View.GONE);

    }

    private void testLuxian() {

        testCar();

    }

    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;

    private void testCar() {

        if (!isLocationChange) {
            return;
        }

        if (isGuihua) {
            return;
        }

        final int ROUTE_TYPE_DRIVE = 2;

        mRouteSearch = new RouteSearch(this);
        mRouteSearchListener = new CARRouteSearchListener();

        mRouteSearch.setRouteSearchListener(mRouteSearchListener); // 注册回调

        if (mStartPoint == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }

        showProgressDialog();


        for (int i = 0; i < mubiaolist.size(); i++) {


            LatLonPoint latLonPoint = mubiaolist.get(i);

            Logger.d(" [" + i + "] - latLonPoint:" + latLonPoint.toString());

            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, latLonPoint);

            //TODO 路径规划的策略，可选，默认为0-速度优先 参见 ：https://developer.amap.com/api/android-sdk/guide/route-plan/drive/#t6
            int mode = RouteSearch.DrivingDefault;

            //第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
            // 异步路径规划驾车模式查询 ,结果在 : onDriveRouteSearched
            mRouteSearch.calculateDriveRouteAsyn(query);
        }

    }

    /**
     * 测试绘标记
     * 点击事件
     */
    private void testDeawMarker() {

        for (int i = 0; i < mubiaolist.size(); i++) {
            MLatLonPoint mLatLonPoint = mubiaolist.get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(mLatLonPoint.getLatitude(), mLatLonPoint.getLongitude())).title(mLatLonPoint.getTitel()).snippet("DefaultMarker");

            aMap.addMarker(markerOptions);
        }


        //动画
//        Animation animation = new RotateAnimation(marker.getRotateAngle(), marker.getRotateAngle() + 360, 0, 0, 0);
//        long duration = 1000L;
//        animation.setDuration(duration);
//        animation.setInterpolator(new LinearInterpolator());
//        marker.setAnimation(animation);
//        marker.startAnimation();


        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {

                String markerId = marker.getId();
                String title = marker.getTitle();

                Logger.d("markerId:" + markerId);
                Logger.d("title:" + title);

                return false;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);

        aMap.setOnMyLocationChangeListener(this);

    }


    private void initLoacl(Bundle savedInstanceState) {

        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。

        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。


        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。

//      myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
//      myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。

        //以下三种模式从5.1.0版本开始提供
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。

        myLocationStyle.showMyLocation(true);

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style

        aMap.getUiSettings().setMyLocationButtonEnabled(true); //设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

    }

    private void initamap(Bundle savedInstanceState) {

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.route_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        initLoacl(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    @Override
    public void onMyLocationChange(Location location) {
        Logger.d("onMyLocationChange");
        //TODO 定位回调
        if (location != null) {
            mStartPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());// 经纬度
            Logger.d("onMyLocationChange:" + location.getLatitude() + " lon: " + location.getLongitude());
            isLocationChange = true;
            testCar();
        }
    }


    class CARRouteSearchListener implements RouteSearch.OnRouteSearchListener {

        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {


            handDriveRouteSearched(result, errorCode); //路径规划结果回调

        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }


        //TODO 汽车路径规划 结果
        private void handDriveRouteSearched(DriveRouteResult result, int errorCode) {

            dissmissProgressDialog();
//          aMap.clear();// 清理地图上的所有覆盖物

            if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                if (result != null && result.getPaths() != null) {
                    if (result.getPaths().size() > 0) {
                        mDriveRouteResult = result;
                        final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
                        if (drivePath == null) {
                            return;
                        }

                        isGuihua = true;

                        LatLonPoint targetPos = result.getTargetPos();
                        //根据目的地 找对应的markst

                        Logger.d("targetPos:" + targetPos);

                        MLatLonPoint mLatLonPoint = findlatLonPointBytargetPos(mubiaolist, targetPos);

                        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(mContext, aMap, drivePath, mDriveRouteResult, null ,mLatLonPoint);
                        drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                        drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                        drivingRouteOverlay.removeFromMap();
                        drivingRouteOverlay.addToMap();
                        drivingRouteOverlay.zoomToSpan();

                        mBottomLayout.setVisibility(View.VISIBLE);
                        mBottomLayout.setVisibility(View.GONE);

                        int dis = (int) drivePath.getDistance(); //公里
                        int dur = (int) drivePath.getDuration(); //分钟
                        String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";

                        mRotueTimeDes.setText(des);
                        mRouteDetailDes.setVisibility(View.VISIBLE);

                        int taxiCost = (int) mDriveRouteResult.getTaxiCost();

                        mRouteDetailDes.setText("打车约" + taxiCost + "元");
                        mBottomLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ToastUtil.show("打开详细");

//                                Intent intent = new Intent(mContext, DriveRouteDetailActivity.class);
//                                intent.putExtra("drive_path", drivePath);
//                                intent.putExtra("drive_result", mDriveRouteResult);
//                                startActivity(intent);

                            }
                        });

                    } else if (result != null && result.getPaths() == null) {
                        ToastUtil.show(mContext, R.string.no_result);
                    }

                } else {
                    ToastUtil.show(mContext, R.string.no_result);
                }
            } else {
                ToastUtil.showerror(errorCode);
            }
        }

    }

    /**
     * 根据路径规划返回结果目的地坐标 查找 标记信息
     * 标记信息在之前数据初始化
     *
     * @param targetPos
     * @return
     */
    private MLatLonPoint findlatLonPointBytargetPos(ArrayList<MLatLonPoint> mubiaolist, LatLonPoint targetPos) {
        MLatLonPoint latLonPoint = null;
        if (mubiaolist == null || targetPos == null) {
            return latLonPoint;
        }
        for (int i = 0; i < mubiaolist.size(); i++) {
            MLatLonPoint mLatLonPoint = mubiaolist.get(i);
            if (mLatLonPoint.getLongitude() == targetPos.getLongitude() & mLatLonPoint.getLatitude() == targetPos.getLatitude()) {
                latLonPoint = mLatLonPoint;
                break;
            }
        }
        return latLonPoint;
    }


}