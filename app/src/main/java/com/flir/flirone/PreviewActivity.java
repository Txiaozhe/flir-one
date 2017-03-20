package com.flir.flirone;

//主界面

import com.flir.flirone.imagehelp.ImageHelp;
import com.flir.flirone.networkhelp.ConnectivityChangeReceiver;
import com.flir.flirone.networkhelp.UpLoadService;
import com.flir.flirone.threshold.ThresholdHelp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flir.flironesdk.Device;
import com.flir.flironesdk.FlirUsbDevice;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;
import com.flir.flironesdk.LoadedFrame;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;

public class PreviewActivity extends Activity implements Device.Delegate, FrameProcessor.Delegate, Device.StreamDelegate, ConnectivityChangeReceiver.NetworkStateInteraction {
    private ImageView thermalImageView;
    private volatile boolean imageCaptureRequested = false;
    private volatile Device flirOneDevice;
    private FrameProcessor frameProcessor;
    private String lastSavedPath;
    private ToggleButton warnButton;
    private MediaPlayer mp, mp_strong;
    private SoundPool sp;
    private int sound;
    private ImageButton showImage;
    private ImageHelp imageHelp;
    private Button showDialog;
    ThresholdHelp thresholdHelp;
    private double maxTemp, meantTemp;
    private int maxX, maxY;
    private int width;
    private int height;
    private short[] thermalPixels;
    private TextView showNfcResult;
    private String nfc_result;
    private TextView showNetworkState;
    private TextView showTeleimei;
    private Device.TuningState currentTuningState = Device.TuningState.Unknown;

    public void onDeviceConnected(Device device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
            }
        });

        flirOneDevice = device;
        flirOneDevice.startFrameStream(this);
    }

    public void onDeviceDisconnected(Device device) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
                thermalImageView.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8));
                thermalImageView.clearColorFilter();
                findViewById(R.id.tuningProgressBar).setVisibility(View.GONE);
                findViewById(R.id.tuningTextView).setVisibility(View.GONE);

            }
        });
        flirOneDevice = null;
    }

    //Device.Delegate接口实现的方法，调节状态改变
    public void onTuningStateChanged(Device.TuningState tuningState) {

        currentTuningState = tuningState;
        if (tuningState == Device.TuningState.InProgress) {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    super.run();
                    thermalImageView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.DARKEN);
                    findViewById(R.id.tuningProgressBar).setVisibility(View.VISIBLE);
                    findViewById(R.id.tuningTextView).setVisibility(View.VISIBLE);

                    loading.setVisibility(View.GONE);
                    spotMeterIcon.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    super.run();
                    thermalImageView.clearColorFilter();
                    findViewById(R.id.tuningProgressBar).setVisibility(View.GONE);
                    findViewById(R.id.tuningTextView).setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onAutomaticTuningChanged(boolean deviceWillTuneAutomatically) {

    }

    private void updateThermalImageView(final Bitmap frame) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                thermalImageView.setImageBitmap(frame);
            }
        });
    }

    public void onFrameReceived(Frame frame) {

        if (currentTuningState != Device.TuningState.InProgress) {
            frameProcessor.processFrame(frame);
        }
    }

    private Bitmap thermalBitmap = null;

    public void onFrameProcessed(final RenderedImage renderedImage) {

        if (renderedImage.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            // Note: this code is not optimized

            thermalPixels = renderedImage.thermalPixelData();

            width = renderedImage.width();
            height = renderedImage.height();
            int centerPixelIndex = width * (height / 2) + (width / 2);
            int[] centerPixelIndexes = new int[]{
                    centerPixelIndex, centerPixelIndex - 1, centerPixelIndex + 1,
                    centerPixelIndex - width,
                    centerPixelIndex - width - 1,
                    centerPixelIndex - width + 1,
                    centerPixelIndex + width,
                    centerPixelIndex + width - 1,
                    centerPixelIndex + width + 1
            };

            new Thread(new Runnable() {
                short[] thermalPixels = renderedImage.thermalPixelData();
                int width = renderedImage.width();
                int height = renderedImage.height();


                @Override
                public void run() {
                    double pixelCMax = 0;
                    double pixelCAll = 0;
                    int maxIndex = 0;
                    int pixelTemp;
                    double[] temp = new double[width * height];
                    for (int i = 0; i < width * height; i++) {
                        pixelTemp = thermalPixels[i] & 0xffff;
                        temp[i] = (pixelTemp / 100) - 273.15;
                        pixelCMax = pixelCMax < temp[i] ? temp[i] : pixelCMax;
                        if (pixelCMax == temp[i]) {
                            maxIndex = i;
                        }
                        pixelCAll += temp[i];
                        meantTemp = pixelCAll / (width * height); //全屏平均温度
                    }
                    maxTemp = pixelCMax; //全屏最高温度
                    maxX = maxIndex % width; //最高温度x坐标
                    maxY = maxIndex / width; //最高温度y坐标
                    mp = MediaPlayer.create(PreviewActivity.this, R.raw.warn);
                    mp_strong = MediaPlayer.create(PreviewActivity.this, R.raw.warn_strong);
                    if (warnButton.isChecked() == true) {
                        if (pixelCMax > thresholdHelp.getThreshold_low() && pixelCMax < thresholdHelp.getThreshold_high()) {
                            mp.start();
                        } else if (pixelCMax > thresholdHelp.getThreshold_high()) {
                            mp.stop();
                            mp_strong.start();
                        }
                    }
                }
            }).start();
            //////

            double averageTemp = 0;

            for (int i = 0; i < centerPixelIndexes.length; i++) {  //centerPixelIndexes.length = 9
                // Remember: all primitives are signed, we want the unsigned value,
                // we could also use renderedImage.thermalPixelValues() instead
                int pixelValue = (thermalPixels[centerPixelIndexes[i]]) & 0xffff;
                averageTemp += (((double) pixelValue) - averageTemp) / ((double) i + 1);
            }
            //Log.i("centerPixelIndex", centerPixelIndexes.length + "");
            double averageC = (averageTemp / 100) - 273.15;
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            final String spotMeterValue = numberFormat.format(averageC) + "ºC";

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.spotMeterValue)).setText(spotMeterValue);
                }
            });

            // if radiometric is the only type, also show the image
            if (frameProcessor.getImageTypes().size() == 1) {
                // example of a custom colorization, maps temperatures 0-100C to 8-bit gray-scale
                byte[] argbPixels = new byte[width * height * 4];
                final byte aPixValue = (byte) 255;
                for (int p = 0; p < thermalPixels.length; p++) {
                    int destP = p * 4;
                    byte pixValue = (byte) (Math.min(0xff, Math.max(0x00, ((int) thermalPixels[p] - 27315) * (255.0 / 10000.0))));

                    argbPixels[destP + 3] = aPixValue;
                    // red pixel
                    argbPixels[destP] = argbPixels[destP + 1] = argbPixels[destP + 2] = pixValue;
                }
                thermalBitmap = Bitmap.createBitmap(width, renderedImage.height(), Bitmap.Config.ARGB_8888);

                thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(argbPixels));

                updateThermalImageView(thermalBitmap);
            }
        } else {
            thermalBitmap = renderedImage.getBitmap();
            updateThermalImageView(thermalBitmap);
        }

        if (this.imageCaptureRequested) {
            imageCaptureRequested = false;
            final Context context = this;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String fileName = nfc_result.substring(1) + "_" + getFileName();

                    try {
                        lastSavedPath = GlobalConfig.IMAGE_PATH + "/" + fileName + "@" + (float) maxTemp + "#" + maxX + "$" + maxY + "%" + (float) meantTemp + ".jpg";
                        Frame frame = renderedImage.getFrame();
                        if (frame != null) {
                            frame.save(new File(lastSavedPath), RenderedImage.Palette.Iron, RenderedImage.ImageType.BlendedMSXRGBA8888Image);
                        } else {
                            Toast.makeText(PreviewActivity.this, "图片获取失败", Toast.LENGTH_SHORT).show();
                        }

                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(GlobalConfig.IMAGE_PATH)));

                        MediaScannerConnection.scanFile(context,
                                new String[]{GlobalConfig.IMAGE_PATH + "/" + fileName}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                    }

                                });
                    } catch (Exception e) {

                    }
                }
            }).start();

        }

    }
    public void onCaptureImageClicked(View v) {

        if (flirOneDevice == null && lastSavedPath != null) {

            //load!
            File file = new File(lastSavedPath);

            LoadedFrame frame = new LoadedFrame(file);

            // load the frame
            onFrameReceived(frame);
        } else {
            if (nfc_result.equals("NNFC未识别")) {
                Toast.makeText(PreviewActivity.this, "NFC未识别，无法进行拍照！", Toast.LENGTH_SHORT).show();
            } else {
                sp.play(sound, 1, 1, 0, 0, 1);
                this.imageCaptureRequested = true;
            }

            setThumb();
        }

    }

    private void setThumb() {
        try {
            File[] files = imageHelp.getFiles();
            if (files != null && files.length >= 1) {
                Bitmap thumb = imageHelp.getImageThumbnail(files[files.length - 1].getPath(), 100, 100);
                showImage.setImageBitmap(thumb);
            }
        } catch (Exception e) {

        }
    }

    private String getFileName() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format.format(date);
        String fileName = time;
        return fileName;
    }

    @Override
    protected void onStart() {
        super.onStart();
        thermalImageView = (ImageView) findViewById(R.id.imageView);
        //若未连接设备，则显示"请连接设备"
        if (Device.getSupportedDeviceClasses(this).contains(FlirUsbDevice.class)) {
            findViewById(R.id.pleaseConnect).setVisibility(View.VISIBLE);
        }
        try {
            Device.startDiscovery(this, this);
        } catch (IllegalStateException e) {
            // it's okay if we've already started discovery
        } catch (SecurityException e) {
            // On some platforms, we need the user to select the app to give us permisison to the USB device.
            Toast.makeText(this, "请插入一个Flir设备并选择" + getString(R.string.app_name_cn), Toast.LENGTH_LONG).show();
            // There is likely a cleaner way to recover, but for now, exit the activity and
            // wait for user to follow the instructions;
            finish();
        }
    }

    ScaleGestureDetector mScaleDetector;

    private ProgressBar loading;
    private ImageView spotMeterIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preview);

        Intent serviceIntent = new Intent(PreviewActivity.this, UpLoadService.class);
        startService(serviceIntent);

        spotMeterIcon = (ImageView) findViewById(R.id.spotMeterIcon);
        loading = (ProgressBar) findViewById(R.id.loading);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityChangeReceiver receiver = new ConnectivityChangeReceiver();
        registerReceiver(receiver, intentFilter);
        receiver.setNetWorkStateChangeListener(this);

        //网络状态
        showNetworkState = (TextView) findViewById(R.id.show_network_state);

        try {
            showTeleimei = (TextView) findViewById(R.id.show_teleimei);
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            showTeleimei.setText("手机串号：\n" + telephonyManager.getDeviceId());
        } catch (Exception e) {
            Toast.makeText(PreviewActivity.this, "权限错误", Toast.LENGTH_SHORT).show();
        }

        //是否开启警报
        warnButton = (ToggleButton) findViewById(R.id.warnButton);
        warnButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    warnButton.setBackgroundResource(R.mipmap.bell_open);
                    Toast.makeText(PreviewActivity.this, "高温警报已开启！", Toast.LENGTH_SHORT).show();
                } else {
                    warnButton.setBackgroundResource(R.mipmap.bell_close);
                    Toast.makeText(PreviewActivity.this, "高温警报已关闭！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        sound = sp.load(this, R.raw.sound, 0);

        showDialog = (Button) findViewById(R.id.showDialog);
        thresholdHelp = new ThresholdHelp(PreviewActivity.this, showDialog);
        thresholdHelp.setThreshold();
        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thresholdHelp.showAddDialog();
            }
        });

        RenderedImage.ImageType defaultImageType = RenderedImage.ImageType.BlendedMSXRGBA8888Image;
        frameProcessor = new FrameProcessor(this, this, EnumSet.of(defaultImageType, RenderedImage.ImageType.ThermalRadiometricKelvinImage));

        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                frameProcessor.setMSXDistance(detector.getScaleFactor());
                return false;
            }
        });

        showImage = (ImageButton) findViewById(R.id.showImage);
        imageHelp = new ImageHelp(GlobalConfig.IMAGE_PATH);
        setThumb();

        imageHelp.checkAllImagesDate();

        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreviewActivity.this, ImageListActivity.class);
                startActivity(i);
            }
        });

        showNfcResult = (TextView) findViewById(R.id.show_nfc_result);
        if (getIntent() != null) {
            nfc_result = getIntent().getStringExtra("nfcresult");
            if (nfc_result == null) {
                nfc_result = "NNFC未识别";
            }
            showNfcResult.setText("当前车厢号：" + nfc_result.substring(1));
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (flirOneDevice != null) {
            //flirOneDevice.stopFrameStream();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (flirOneDevice != null) {
            flirOneDevice.startFrameStream(this);
        }
    }

    @Override
    public void onStop() {
        // We must unregister our usb receiver, otherwise we will steal events from other apps
        Device.stopDiscovery();
        flirOneDevice = null;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setNetworkState(String state) {
        if (state != null) {
            showNetworkState.setText(state);
        }
    }
}
