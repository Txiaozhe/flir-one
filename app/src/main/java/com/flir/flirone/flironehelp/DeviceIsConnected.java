package com.flir.flirone.flironehelp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.flir.flirone.R;
import com.flir.flironesdk.Device;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.SimulatedDevice;

/**
 * Created by txiaozhe on 09/02/2017.
 */

public class DeviceIsConnected implements Device.StreamDelegate, Device.Delegate{

    private View view;
    private Activity activity;
    private boolean chargeIsC;//chargeCableIsConnected;
    private ImageView thermalImageView;
    private OrientationEventListener oe;//orientationEventListener;

    public DeviceIsConnected(Activity activity, int layoutId, boolean chargeIsC, ImageView thermalImageView,  OrientationEventListener oe) {
        this.activity = activity;
        this.chargeIsC = chargeIsC;
        this.thermalImageView = thermalImageView;
        this.oe = oe;

        LayoutInflater factory = LayoutInflater.from(activity);
        view = factory.inflate(layoutId, null);
    }

    @Override
    public void onTuningStateChanged(Device.TuningState tuningState) {

    }

    @Override
    public void onAutomaticTuningChanged(boolean b) {

    }

    @Override
    public void onDeviceConnected(Device device) {
        Log.i("ExampleApp", "Device connected!");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
            }
        });

        device.startFrameStream(this);

        final ToggleButton chargeCableButton = (ToggleButton) view.findViewById(R.id.chargeCableToggle);
        if (device instanceof SimulatedDevice) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chargeCableButton.setChecked(chargeIsC);
                    chargeCableButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chargeCableButton.setChecked(chargeIsC);
                    chargeCableButton.setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.connect_sim_button).setEnabled(false);
                }
            });
        }

        oe.enable();
    }

    @Override
    public void onDeviceDisconnected(Device device) {
        Log.i("ExampleApp", "Device disconnected!");

        final ToggleButton chargeCableButton = (ToggleButton) view.findViewById(R.id.chargeCableToggle);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
                thermalImageView.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8));
                chargeCableButton.setChecked(chargeIsC);
                chargeCableButton.setVisibility(View.INVISIBLE);
                thermalImageView.clearColorFilter();
                view.findViewById(R.id.tuningProgressBar).setVisibility(View.GONE);
                view.findViewById(R.id.tuningTextView).setVisibility(View.GONE);
                view.findViewById(R.id.connect_sim_button).setEnabled(true);
            }
        });
        oe.disable();
    }

    @Override
    public void onFrameReceived(Frame frame) {
//        Log.v("ExampleApp", "Frame received!");
//
//        if (currentTuningState != Device.TuningState.InProgress) {
//            frameProcessor.processFrame(frame);
//        }
    }
}
