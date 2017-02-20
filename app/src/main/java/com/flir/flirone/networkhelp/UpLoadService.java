package com.flir.flirone.networkhelp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.flir.flirone.GlobalConfig;
import com.flir.flirone.imagehelp.ImageHelp;
import com.flir.flirone.imagehelp.ImageInfo;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class UpLoadService extends Service implements ConnectivityChangeReceiver.NetworkStateInteraction {
    WebServiceCall webServiceCall;
    final int TIME_DELAYED = 1000 * 60 * 10; //每10分钟重启一次服务

    private Thread thread_create;
    private Context context = this;
    private boolean isConnected = false;

    public UpLoadService() {
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        //网络检测
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityChangeReceiver receiver = new ConnectivityChangeReceiver();
        registerReceiver(receiver, intentFilter);
        receiver.setNetWorkStateChangeListener(this);

        webServiceCall = new WebServiceCall(GlobalConfig.NAMESPACE, GlobalConfig.WEBSERVICE_URL,
                GlobalConfig.METHOD_NAME, GlobalConfig.NET_TIMEOUT_MS);

        try {
            new Thread(new Runnable() {
                //获取手机串号
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                ImageHelp imageHelp = new ImageHelp(GlobalConfig.IMAGE_PATH);
                File[] files = imageHelp.getFiles();

                @Override
                public void run() {
                    if (isConnected) {
                        for (int i = 0; i < files.length; i++) {
                            if (files[i].getName().indexOf("_UP") < 0) {
                                byte[] bytes = imageHelp.getFileToByte(files[i]);
                                boolean isSuccess = false;
                                try {
                                    isSuccess = request(webServiceCall, imageHelp, imageHelp.getInfoFromName(files[i].getName()), telephonyManager.getDeviceId(), new String(bytes, "UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                if (!isSuccess) { //实际中是isSuccess
                                    imageHelp.renameImage(files[i]);
                                }
                            }
                        }
                    }
                }
            }).start();

        } catch (Exception e) {
        }

        if (thread_create == null) {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(TIME_DELAYED);
                            startService(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        thread_create = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, UpLoadService.class);
                startService(intent);
            }
        });
        thread_create.start();
    }

    boolean isSuccess;

    private boolean request(WebServiceCall call, ImageHelp imageHelp, ImageInfo imageInfo, String phoneId, String image) {
        if (imageInfo.getName().indexOf("_UP") < 0) {
            call.request.addProperty(GlobalConfig.PHONE_TAG, phoneId); //手机串号
            call.request.addProperty(GlobalConfig.NFC_TAG, imageInfo.getNfcCode());
            call.request.addProperty(GlobalConfig.IMAGE, image);
            call.request.addProperty(GlobalConfig.IMAGE_NAME, imageInfo.getName());
            call.request.addProperty(GlobalConfig.IMAGE_TIME, imageHelp.getTimeFromName(imageInfo.getName()));
            call.request.addProperty(GlobalConfig.MAX_TEMP, imageInfo.getMaxTemp());
            call.request.addProperty(GlobalConfig.MAX_TEMP_X, imageInfo.getMaxTempX());
            call.request.addProperty(GlobalConfig.MAX_TEMP_Y, imageInfo.getMaxTempY());
            call.request.addProperty(GlobalConfig.AVERAGE_TEMP, imageInfo.getAverTemp());

            try {
                String result = webServiceCall.callWebMethod().toString();
                int result_code = Integer.parseInt(result);
                if (result_code > 0) {
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    switch (result_code) {
                        case -1: {

                            break;
                        }
                        case -2: {

                            break;
                        }
                        case -9: {

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
