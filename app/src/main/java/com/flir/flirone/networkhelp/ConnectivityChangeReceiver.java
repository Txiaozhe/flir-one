package com.flir.flirone.networkhelp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.flir.flirone.GlobalConfig;
import com.flir.flirone.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private NetworkStateInteraction networkStateInteraction;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            networkStateInteraction.setNetworkState(GlobalConfig.NO_NETWORK_CONNECTED);
            setNetwork(context);
        } else {
            if(mobNetInfo.isConnected()) {
                networkStateInteraction.setNetworkState(GlobalConfig.MOBILE_CONNECTED);
            } else if(wifiNetInfo.isConnected()) {
                networkStateInteraction.setNetworkState(GlobalConfig.WIFI_CONNECTED);
            }

            //启动上传服务
            Intent serviceIntent = new Intent(context, UpLoadService.class);
            context.startService(serviceIntent);

        }

    }

    /**
     * 网络未连接时，调用设置方法
     */
    public void setNetwork(Context context){
        final Context context1 = context;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context1);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("网络提示信息");
        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context1.startActivity(intent);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    public interface NetworkStateInteraction {
        void setNetworkState(String state);
    }

    public void setNetWorkStateChangeListener(NetworkStateInteraction networkStateInteraction) {
        this.networkStateInteraction = networkStateInteraction;
    }
}
