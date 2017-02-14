package com.flir.flirone;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

/**
 * Created by txiaozhe on 12/02/2017.
 */

public class GlobalConfig {

    //图片保存路径
    public static final String IMAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

    //网络状态action
    public static final String NETWORK_CHANGED_ACTION = "network_changed_action";

    //网络状态全局变量
    public static final String MOBILE_CONNECTED = "网络已连接\n移动数据";
    public static final String WIFI_CONNECTED = "网络已连接\nWIFI";
    public static final String NO_NETWORK_CONNECTED = "网络未连接\n将停止上传数据";

    //网络服务
    public static final String SERVER_IP = "211.143.78.218:7044";
    public static final int NET_TIMEOUT_MS = 6000;
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String WEBSERVICE_URL =  "http://211.143.78.218:7044//pcj_cloudtrain_ws//Service1.asmx";
    public static final String METHOD_NAME = "ReceiveInspecteWorkMainWithGPS";

}
