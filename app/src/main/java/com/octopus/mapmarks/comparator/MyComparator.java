package com.octopus.mapmarks.comparator;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;

import java.util.Comparator;

public class MyComparator implements Comparator {

    //接口，必须实现的方法
    public int compare(Object o1, Object o2) {


        DriveRouteResult p1 = (DriveRouteResult) o1;
        DriveRouteResult p2 = (DriveRouteResult) o2;

        DrivePath drivePath1 = p1.getPaths().get(0);
        DrivePath drivePath2 = p2.getPaths().get(0);

        int dis1 = (int) drivePath1.getDistance(); //公里
        int dur1 = (int) drivePath1.getDuration(); //分钟

//      String des = AMapUtil.getFriendlyTime(dis1) + "(" + AMapUtil.getFriendlyLength(dis) + ")";

        int dis2 = (int) drivePath2.getDistance(); //公里
        int dur2 = (int) drivePath2.getDuration(); //分钟

        if (dur1 > dur2) {
            return 1;
        } else if (dur1 < dur2) {
            return -1;
        } else {
            return 0;

        }
    }
}