package com.flir.flirone.networkhelp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.flir.flirone.R;

/**
 * Created by txiaozhe on 13/02/2017.
 */

public class NetworkHelp {

    private Context context;
    private ConnectivityManager manager;
    private String networkState;

    public NetworkHelp(Context context) {
        this.context = context;
    }

    /**
     * 检测网络是否连接
     * @return
     */
    public boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            setNetwork();
        }

        return flag;
    }


    /**
     * 网络未连接时，调用设置方法
     */
    public void setNetwork(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                context.startActivity(intent);
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

    /**
     * 网络已经连接，然后去判断是wifi连接还是GPRS连接
     * 设置一些自己的逻辑调用
     */
    public String isNetworkAvailable(){

        String networkType = "";
        NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING){
            networkType = "移动数据";
            Toast.makeText(context, "您现在连接的是移动数据", Toast.LENGTH_SHORT).show();
        }
        //判断为wifi状态下才上传图片，如果是GPRS手机网络则不加载！
        if(wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING){
            networkType = "wifi";
            Toast.makeText(context, "您现在连接的是wifi", Toast.LENGTH_SHORT).show();
        }
        return networkType;
    }

    /**
     * 在wifi状态下执行上传操作
     */
    public void upLoad(){

    }
}
