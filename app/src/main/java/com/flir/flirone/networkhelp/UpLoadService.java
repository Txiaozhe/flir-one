package com.flir.flirone.networkhelp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flir.flirone.GlobalConfig;
import com.flir.flirone.imagehelp.ImageHelp;
import com.flir.flirone.imagehelp.ImageInfo;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class UpLoadService extends Service implements ConnectivityChangeReceiver.NetworkStateInteraction {
    public static Integer mInprogressLock = new Integer(10000);
    private static boolean mIsInprogress = false;

    WebServiceCall webServiceCall;
    final int TIME_DELAYED = 30 * 1000;//1000 * 60 * 10; //每10分钟重启一次服务

    private Thread thread_create;
    private Context context = this;
    private boolean isConnected = false;

    //网络监测
    private IntentFilter intentFilter;
    private ConnectivityChangeReceiver receiver;

    public UpLoadService() {
    }

    public static void setUploadInProgress(boolean status) {
        synchronized (mInprogressLock) {
            mIsInprogress = status;
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        webServiceCall = new WebServiceCall(GlobalConfig.NAMESPACE, GlobalConfig.WEBSERVICE_URL,
                GlobalConfig.METHOD_NAME, GlobalConfig.NET_TIMEOUT_MS);

        try {
            synchronized (mInprogressLock) {
                if (!mIsInprogress) {
                    UpLoadService.setUploadInProgress(true);
                    new Thread(new Runnable() {
                        //获取手机串号
                        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        ImageHelp imageHelp = new ImageHelp(GlobalConfig.IMAGE_PATH);
                        File[] files = imageHelp.getFiles();

                        @Override
                        public void run() {
                            if (isConnected & files != null) {
                                for (int i = 0; i < files.length; i++) {
                                    String fileName = files[i].getName();

                                    Log.i("imageindex:", i + "  " + fileName);

                                    if (fileName.indexOf("_UP") < 0) {
                                        byte[] bytes = imageHelp.getFileToByte(files[i]);
                                        try {
                                            boolean isUp = request(webServiceCall, imageHelp, imageHelp.getInfoFromName(fileName), telephonyManager.getDeviceId(), new String(bytes, "UTF-8"));
                                            if (isUp) { //实际中是isSuccess
                                                String newName = imageHelp.renameImage(files[i]);
                                                Log.i("imageindexsuccess", i + "  " + isUp + "  " + fileName + "  newName:" + newName);
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        Thread.sleep(3 * 1000);
                                    } catch (InterruptedException ignored) {
                                    }
                                }
                            }

                            UpLoadService.setUploadInProgress(false);
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
        }

        if (thread_create == null) {
            thread_create = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(TIME_DELAYED);

                            Intent s = new Intent(UpLoadService.this, UpLoadService.class);
                            startService(s);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread_create.start();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new ConnectivityChangeReceiver();
        registerReceiver(receiver, intentFilter);
        receiver.setNetWorkStateChangeListener(this);
    }

    private boolean request(WebServiceCall call, ImageHelp imageHelp, ImageInfo imageInfo, String phoneId, String image) {
        boolean isSuccess = false;
        if (imageInfo.getName().indexOf("_UP") < 0) {
            call.request.addProperty(GlobalConfig.PHONE_TAG, phoneId); //手机串号
            call.request.addProperty(GlobalConfig.NFC_TAG, imageInfo.getNfcCode());
            call.request.addProperty(GlobalConfig.IMAGE, "image");
            call.request.addProperty(GlobalConfig.IMAGE_NAME, imageInfo.getName() + ".jpg");
            call.request.addProperty(GlobalConfig.IMAGE_TIME, imageHelp.getTimeFromName(imageInfo.getName()));
            int maxTempDot = imageInfo.getMaxTemp().indexOf(".") + 2;
            call.request.addProperty(GlobalConfig.MAX_TEMP, imageInfo.getMaxTemp().substring(0, maxTempDot));
            call.request.addProperty(GlobalConfig.MAX_TEMP_X, imageInfo.getMaxTempX());
            call.request.addProperty(GlobalConfig.MAX_TEMP_Y, imageInfo.getMaxTempY());
            int averTempDot = imageInfo.getAverTemp().indexOf(".") + 2;
            call.request.addProperty(GlobalConfig.AVERAGE_TEMP, imageInfo.getAverTemp().substring(0, averTempDot));
            call.request.addProperty(GlobalConfig.TELELONG, "90");
            call.request.addProperty(GlobalConfig.TELELAT, "90");

            String photocount = imageInfo.getName().substring(imageInfo.getName().length() - 1, imageInfo.getName().length());
            Log.i("imageindexname", imageInfo.getName());
            call.request.addProperty(GlobalConfig.IMAGE_INDEX, photocount);

            try {
                String result = call.callWebMethod().toString();
                int result_code = Integer.parseInt(result);
                Log.i("imageindexresult", result);
                if (result_code > 0) {
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    switch (result_code) {
                        case -1: {
                            Log.i("imageerr", result_code + "");
                            break;
                        }
                        case -2: {
                            Log.i("imageerr", result_code + "");
                            break;
                        }
                        case -9: {
                            Log.i("imageerr", result_code + "");
                            break;
                        }
                    }
                }
            } catch (IOException e) {
            } catch (XmlPullParserException e) {
            }
        }
        return isSuccess;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setNetworkState(String state) {
        if (state.indexOf("网络已连接") >= 0) {
            isConnected = true;
        }
    }
}
