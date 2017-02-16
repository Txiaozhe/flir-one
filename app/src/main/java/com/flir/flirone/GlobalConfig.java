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

    //网络状态全局变量
    public static final String MOBILE_CONNECTED = "网络已连接：移动数据";
    public static final String WIFI_CONNECTED = "网络已连接：WIFI";
    public static final String NO_NETWORK_CONNECTED = "网络未连接，将停止数据传输";

    //网络服务
    public static final String SERVER_IP = "211.143.78.218:7044";
    public static final int NET_TIMEOUT_MS = 6000;
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String WEBSERVICE_URL = "http://211.143.78.218:7044//pcj_cloudtrain_ws//Service1.asmx";
    public static final String METHOD_NAME = "ReceiveHeatImageInfoWithGPS";

    //字段
    public static final String IS_UPLOAD = "isUpload"; //是否已上传
    public static final String PATH = "path"; //手机本地存储地址

    public static final String PHONE_TAG = "teleimei"; //手机串号
    public static final String NFC_TAG = "barcode"; //nfc标签
    public static final String IMAGE = "heatimage"; //图片
    public static final String IMAGE_NAME = "imagename"; //图片名称
    public static final String IMAGE_TIME = "imagetime"; //图片获取时间
    public static final String MAX_TEMP = "maxtemperature"; //最高温度
    public static final String MAX_TEMP_X = "maxtemplocalx"; //最高温度x坐标
    public static final String MAX_TEMP_Y = "maxtemplocaly"; //最高温度y坐标
    public static final String AVERAGE_TEMP = "meantemperature"; //平均温度

    //数据库
    public static final String DB_NAME = "heat_images_info.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_COLUMN_NAMES = "isUpload TEXT, teleimei TEXT, barcode TEXT, " +
            "path TEXT, imagename TEXT, imagetime TEXT, maxtemperature TEXT, " +
            "maxtemplocalx TEXT, maxtemplocaly TEXT, meantemperature TEXT)";
}
