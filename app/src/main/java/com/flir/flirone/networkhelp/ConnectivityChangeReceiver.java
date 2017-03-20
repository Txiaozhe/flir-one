package com.flir.flirone.networkhelp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.flir.flirone.GlobalConfig;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private NetworkStateInteraction networkStateInteraction;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            networkStateInteraction.setNetworkState(GlobalConfig.NO_NETWORK_CONNECTED);
        } else {
            if (mobNetInfo.isConnected()) {
                networkStateInteraction.setNetworkState(GlobalConfig.MOBILE_CONNECTED);
            } else if (wifiNetInfo.isConnected()) {
                networkStateInteraction.setNetworkState(GlobalConfig.WIFI_CONNECTED);
            }

            try {
                Intent serviceIntent = new Intent(context, UpLoadService.class);
                context.startService(serviceIntent);
            } catch (Exception e) {

            }
        }
    }

    public interface NetworkStateInteraction {
        void setNetworkState(String state);
    }

    public void setNetWorkStateChangeListener(NetworkStateInteraction networkStateInteraction) {
        this.networkStateInteraction = networkStateInteraction;
    }
}
