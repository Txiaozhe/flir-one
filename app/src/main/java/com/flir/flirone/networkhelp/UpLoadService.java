package com.flir.flirone.networkhelp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flir.flirone.GlobalConfig;
import com.flir.flirone.imagehelp.ImageHelp;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;

public class UpLoadService extends Service {
    WebServiceCall webServiceCall;

    public UpLoadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("service_oncreated", "service_oncreated");

        //获取手机串号
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        webServiceCall = new WebServiceCall(GlobalConfig.NAMESPACE, GlobalConfig.WEBSERVICE_URL,
                GlobalConfig.METHOD_NAME, GlobalConfig.NET_TIMEOUT_MS);

        webServiceCall.request.addProperty("teleimei", telephonyManager.getDeviceId()); //手机串号
        webServiceCall.request.addProperty("barcode", "rw1234");    // NFC标签

        webServiceCall.request.addProperty("imagename", "edgejdhenjde.jpg"); //图像名
        webServiceCall.request.addProperty("maxtemperature", "90"); //最高温度
        webServiceCall.request.addProperty("maxtemplocalx", "30"); //最高温度位置X坐标
        webServiceCall.request.addProperty("maxtemplocaly", "80"); //最高温度位置Y坐标
        webServiceCall.request.addProperty("meantemperature", "70"); //平均温度
        webServiceCall.request.addProperty("imagetime", "2017-02-13 12：13：14");

//        webServiceCall.request.addProperty("powertype", "软卧1234");  //列车供电类型名称
//        webServiceCall.request.addProperty("worktype", "测温");//作业任务类型

        ImageHelp imageHelp = new ImageHelp(GlobalConfig.IMAGE_PATH);
        File[] files = imageHelp.getFiles();
        Log.i("uploadservice_image", files.length + "");
        try{
            byte[] bytes = imageHelp.getFileToByte(files[0]);
            Log.i("uploadservice_image", files[0].getPath());
            Log.i("uploadservice_image", bytes[0] + "");
            webServiceCall.request.addProperty("heatimage", "bytes");    // 图像
        } catch (Exception e) {
            Log.i("uploadservice_image", e.toString());
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = webServiceCall.callWebMethod().toString();
                    Log.i("uploadservice", result);
                } catch (IOException e) {
                    Log.i("uploadservice_e0", e.toString());
                } catch (XmlPullParserException e) {
                    Log.i("uploadservice_e1", e.toString());
                }
            }
        }).start();


    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
