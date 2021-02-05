package com.octopus.mapmarks.util;

import android.content.Context;

import androidx.constraintlayout.solver.widgets.Flow;

import com.alibaba.fastjson.JSON;
import com.octopus.mapmarks.App;
import com.octopus.mapmarks.CVID_BJ;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class FileUtils {


    //读取 请求 填充
    public static  List<CVID_BJ> initCuidJson() {
        Context context = App.getGlobalContext();

        String CVID_BJ = "CVID_BJ.json";
        String assets = getFromAssets(CVID_BJ);

        List<com.octopus.mapmarks.CVID_BJ> cvid_bjs = JSON.parseArray(assets, CVID_BJ.class);

        Logger.d("cvid_bjs:" + cvid_bjs.size());

        for (int i = 0; i < cvid_bjs.size(); i++) {
            CVID_BJ cvid_bj = cvid_bjs.get(i);
            Logger.d("cvid_bjs[" + i + "]:" + cvid_bj.getName());
        }



        return cvid_bjs;

//        String addre = "address";
//        String key = "address";
//        String output = "address";
//
//
//
//        //请求
//        GGWWServices ggwwServices = RetrofitServiceManager.getInstance().create(GGWWServices.class);
//
//        ggwwServices.geocode("","","")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(new Observer<ResponseBody>() {
//
//                    @Override
//                    public void update(Observable o, Object arg) {
//
//                    }
//                });


    }


    public static String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(App.getGlobalContext().getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
