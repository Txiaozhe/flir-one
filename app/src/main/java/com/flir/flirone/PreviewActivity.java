package com.flir.flirone;

//主界面
import com.flir.flirone.dbhelper.DBManager;
import com.flir.flirone.imagehelp.ImageHelp;
import com.flir.flirone.imagehelp.MyImage;
import com.flir.flirone.networkhelp.ConnectivityChangeReceiver;
import com.flir.flirone.networkhelp.NetworkHelp;
import com.flir.flirone.threshold.ThresholdHelp;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import android.content.Context;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flir.flironesdk.Device;
import com.flir.flironesdk.FlirUsbDevice;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;
import com.flir.flironesdk.LoadedFrame;
import com.flir.flironesdk.SimulatedDevice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewActivity extends Activity implements Device.Delegate, FrameProcessor.Delegate, Device.StreamDelegate, ConnectivityChangeReceiver.NetworkStateInteraction {
    ImageView thermalImageView;
    private volatile boolean imageCaptureRequested = false;
    private volatile Socket streamSocket = null;
    private boolean chargeCableIsConnected = true;

    private int deviceRotation = 0;
    private OrientationEventListener orientationEventListener;

    private volatile Device flirOneDevice;
    private FrameProcessor frameProcessor;

    private String lastSavedPath;

    //控制警告是否打开
    private ToggleButton warnButton;

    //播放警告音
    private MediaPlayer mp, mp_strong;

    //查看图片
    private ImageButton showImage;
    private ImageHelp imageHelp;

    //设置阈值
    private Button showDialog;
    ThresholdHelp thresholdHelp;

    //点击屏幕获取温度
    private int width;
    private int height;
    private short[] thermalPixels;

    //nfc
    private TextView showNfcResult;
    private String nfc_result;

    //检测网络状态
    private TextView showNetworkState;
    private NetworkHelp networkHelp;

    private Device.TuningState currentTuningState = Device.TuningState.Unknown;

    //Device.Delegate接口实现的方法，设备已连接
    public void onDeviceConnected(Device device) {
        Log.i("ExampleApp", "Device connected!");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
            }
        });

        flirOneDevice = device;
        flirOneDevice.startFrameStream(this);

        final ToggleButton chargeCableButton = (ToggleButton) findViewById(R.id.chargeCableToggle);
        if (flirOneDevice instanceof SimulatedDevice) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chargeCableButton.setChecked(chargeCableIsConnected);
                    chargeCableButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chargeCableButton.setChecked(chargeCableIsConnected);
                    chargeCableButton.setVisibility(View.INVISIBLE);
                }
            });
        }

        orientationEventListener.enable();
    }

    //Device.Delegate接口实现的方法，设备未连接
    public void onDeviceDisconnected(Device device) {
        Log.i("ExampleApp", "Device disconnected!");

        final ToggleButton chargeCableButton = (ToggleButton) findViewById(R.id.chargeCableToggle);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
                thermalImageView.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8));
                chargeCableButton.setChecked(chargeCableIsConnected);
                chargeCableButton.setVisibility(View.INVISIBLE);
                thermalImageView.clearColorFilter();
                findViewById(R.id.tuningProgressBar).setVisibility(View.GONE);
                findViewById(R.id.tuningTextView).setVisibility(View.GONE);
            }
        });
        flirOneDevice = null;
        orientationEventListener.disable();
    }

    //Device.Delegate接口实现的方法，调节状态改变
    public void onTuningStateChanged(Device.TuningState tuningState) {
        Log.i("ExampleApp", "Tuning state changed changed!");

        currentTuningState = tuningState;
        if (tuningState == Device.TuningState.InProgress) {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    super.run();
                    thermalImageView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.DARKEN);
                    findViewById(R.id.tuningProgressBar).setVisibility(View.VISIBLE);
                    findViewById(R.id.tuningTextView).setVisibility(View.VISIBLE);
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

    //更改热成像视图
    private void updateThermalImageView(final Bitmap frame) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                thermalImageView.setImageBitmap(frame);
            }
        });
    }

    //Device.StreamDelegate实现的方法，处理视图
    public void onFrameReceived(Frame frame) {
        Log.v("ExampleApp", "Frame received!");

        if (currentTuningState != Device.TuningState.InProgress) {
            frameProcessor.processFrame(frame);
        }
    }

    private Bitmap thermalBitmap = null;

    //FrameProcessor.Delegate接口实现的方法，的获取温度，视图处理器授权方法，将访问每次的frame的产生，实时进行扫描
    public void onFrameProcessed(final RenderedImage renderedImage) {
        Log.i("onFrameProcessed", "onFrameProcessed");
        if (renderedImage.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            // Note: this code is not optimized

            thermalPixels = renderedImage.thermalPixelData(); //thermalPixels[76800]
            //每次扫描都会产生这样的一串数组
            // average the center 9 pixels for the spot meter

            width = renderedImage.width();
            height = renderedImage.height();  //width * height = 76800
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

            //扫描全屏温度并进行高温预警
            new Thread(new Runnable() {
                short[] thermalPixels = renderedImage.thermalPixelData();
                int width = renderedImage.width();
                int height = renderedImage.height();


                @Override
                public void run() {
                    double pixelCMax = 0;
                    int pixelTemp;
                    for (int i = 0; i < width * height; i++) {
                        pixelTemp = thermalPixels[i] & 0xffff;
                        //Log.i("everyPixelTemp", pixelTemp + "  " + i);
                        double pixelC = (pixelTemp / 100) - 273.15;
                        pixelCMax = pixelCMax < pixelC ? pixelC : pixelCMax;
                    }
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

            double averageTemp = 0; //平均温度，单位K

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
            //显示温度
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


        //捕获图像
        if (this.imageCaptureRequested) {
            imageCaptureRequested = false;
            final Context context = this;
            new Thread(new Runnable() {
                public void run() {
                    Log.i("lastSavedPath", "Storage:" + GlobalConfig.IMAGE_PATH);

                    String fileName = nfc_result.substring(1) + "-" + getFileName() + ".jpg";
                    Log.i("nfcfilename", fileName);
                    try {
                        lastSavedPath = GlobalConfig.IMAGE_PATH + "/" + fileName;
                        renderedImage.getFrame().save(new File(lastSavedPath), RenderedImage.Palette.Iron, RenderedImage.ImageType.BlendedMSXRGBA8888Image);

                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(GlobalConfig.IMAGE_PATH)));
                        Log.i("lastSavedPath", lastSavedPath);

                        MediaScannerConnection.scanFile(context,
                                new String[]{GlobalConfig.IMAGE_PATH + "/" + fileName}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("lastSavedPath", "Scanned " + GlobalConfig.IMAGE_PATH + ":");
                                        Log.i("lastSavedPath", "-> uri=" + uri);
                                    }

                                });

                    } catch (Exception e) {
                        Log.e("Exp", e.toString());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            thermalImageView.animate().setDuration(50).scaleY(0).withEndAction((new Runnable() {
                                public void run() {
                                    thermalImageView.animate().setDuration(50).scaleY(1);
                                }
                            }));
                        }
                    });
                }
            }).start();
        }

        if (streamSocket != null && streamSocket.isConnected()) {
            try {
                // send PNG file over socket in another thread
                final OutputStream outputStream = streamSocket.getOutputStream();
                // make a output stream so we can get the size of the PNG
                final ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();

                thermalBitmap.compress(Bitmap.CompressFormat.WEBP, 100, bufferStream);
                bufferStream.flush();
                (new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            /*
                             * Header is 6 bytes indicating the length of the image data and rotation
                             * of the device
                             * This could be expanded upon by adding bytes to have more metadata
                             * such as image format
                             */
                            byte[] headerBytes = ByteBuffer.allocate((Integer.SIZE + Short.SIZE) / 8).putInt(bufferStream.size()).putShort((short) deviceRotation).array();
                            synchronized (streamSocket) {
                                outputStream.write(headerBytes);
                                bufferStream.writeTo(outputStream);
                                outputStream.flush();
                            }
                            bufferStream.close();


                        } catch (IOException ex) {
                            Log.e("STREAM", "Error sending frame: " + ex.toString());
                        }
                    }
                }).start();
            } catch (Exception ex) {
                Log.e("STREAM", "Error creating PNG: " + ex.getMessage());

            }

        }


    }

    //捕获图像单击事件
    public void onCaptureImageClicked(View v) {

        // if nothing's connected, let's load an image instead?

        if (flirOneDevice == null && lastSavedPath != null) {
            // load!
            File file = new File(lastSavedPath);

            LoadedFrame frame = new LoadedFrame(file);

            // load the frame
            onFrameReceived(frame);
        } else {
            this.imageCaptureRequested = true;

            try{
                File[] files = imageHelp.getFiles();
                if(files != null && files.length >= 1) {
                    Bitmap thumb = imageHelp.getImageThumbnail(files[files.length - 1].getPath(), 100, 100);
                    showImage.setImageBitmap(thumb);
                }
            } catch (Exception e) {

            }
        }

    }

    //获取文件名
    private String getFileName() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format.format(date);
        int suffix = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
        String fileName = time;
        return fileName;
    }

    public void onSimulatedChargeCableToggleClicked(View v) {
        if (flirOneDevice instanceof SimulatedDevice) {
            chargeCableIsConnected = !chargeCableIsConnected;
            ((SimulatedDevice) flirOneDevice).setChargeCableState(chargeCableIsConnected);
        }
    }

    public void onPaletteListViewClicked(View v) {
        RenderedImage.Palette pal = (RenderedImage.Palette) (((ListView) v).getSelectedItem());
        frameProcessor.setImagePalette(pal);
    }

    //热成像主界面
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

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preview);

        //sqlite
        dbManager = new DBManager(this);
        add();
        query();

        //网络检测
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityChangeReceiver receiver = new ConnectivityChangeReceiver();
        registerReceiver(receiver, intentFilter);
        receiver.setNetWorkStateChangeListener(this);

        //启动时检测网络连接
        networkHelp = new NetworkHelp(PreviewActivity.this);

        //网络状态
        showNetworkState = (TextView) findViewById(R.id.show_network_state);

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

        //阈值
        showDialog = (Button) findViewById(R.id.showDialog);
        thresholdHelp = new ThresholdHelp(PreviewActivity.this, showDialog);
        thresholdHelp.setThreshold();
        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thresholdHelp.showAddDialog();
            }
        });

        //设置默认滤镜
        RenderedImage.ImageType defaultImageType = RenderedImage.ImageType.BlendedMSXRGBA8888Image;
        frameProcessor = new FrameProcessor(this, this, EnumSet.of(defaultImageType, RenderedImage.ImageType.ThermalRadiometricKelvinImage));

        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                deviceRotation = orientation;
            }
        };
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
                Log.d("ZOOM", "zoom ongoing, scale: " + detector.getScaleFactor());
                frameProcessor.setMSXDistance(detector.getScaleFactor());
                return false;
            }
        });

        //查看所有图片按钮设置缩略图
        showImage = (ImageButton) findViewById(R.id.showImage);
        imageHelp = new ImageHelp(GlobalConfig.IMAGE_PATH);
        try{
            File[] files = imageHelp.getFiles();
            if(files != null && files.length >= 1) {
                Bitmap thumb = imageHelp.getImageThumbnail(files[files.length - 1].getPath(), 100, 100);
                showImage.setImageBitmap(thumb);
            }
        } catch (Exception e) {

        }

        //点击查看所有图片按钮进入图片展示页面
        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreviewActivity.this, ImageListActivity.class);
                startActivity(i);
            }
        });

        //获取nfc 数据
        showNfcResult = (TextView) findViewById(R.id.show_nfc_result);
        if(getIntent() != null) {
            nfc_result = getIntent().getStringExtra("nfcresult");
            if(nfc_result == null) {
                nfc_result = "NNFC未识别";
            }
            showNfcResult.setText("当前车厢号：\n" + nfc_result.substring(1));
        }
    }

    //sqlite
    private void add() {
        ArrayList<MyImage> images = new ArrayList<MyImage>();

        MyImage image = new MyImage("dhedhe.jpg", "/pictures", "jpg", "1.1MB", "2017-02-12", "90℃", "90", "76", "88");
        images.add(image);
        dbManager.add(images);
    }

    public void update() {
        MyImage myImage = new MyImage();
        myImage.setName("Jane");
        dbManager.updateAge(myImage);
    }

    public void delete() {
        MyImage myImage = new MyImage();
        //设置删除条件
        dbManager.deleteOldPerson(myImage);
    }

    public void query() {
        List<MyImage> myImages = dbManager.query();
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (MyImage myImage : myImages) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", myImage.getName());
            map.put("path", myImage.getPath());
            list.add(map);
            Log.i("sqlitequery", myImage.getName());
        }
    }

    public void queryTheCursor() {
        Cursor c = dbManager.queryTheCursor();
        startManagingCursor(c); //托付给activity根据自己的生命周期去管理Cursor的生命周期
        CursorWrapper cursorWrapper = new CursorWrapper(c) {
            @Override
            public String getString(int columnIndex) {
                //将简介前加上年龄
                if (getColumnName(columnIndex).equals("name")) {
                    String path = getString(getColumnIndex("path"));
                    return path;
                }
                return super.getString(columnIndex);
            }
        };
    }
    //sqlite

    @Override
    public void onPause() {
        super.onPause();
        if (flirOneDevice != null) {
//            flirOneDevice.stopFrameStream();
        }
        Log.i("activity_info", "pause");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (flirOneDevice != null) {
            flirOneDevice.startFrameStream(this);
        }

        Log.i("activity_info", "resume");
    }

    @Override
    public void onStop() {
        // We must unregister our usb receiver, otherwise we will steal events from other apps
        Log.e("PreviewActivity", "onStop, stopping discovery!");
        Device.stopDiscovery();
        flirOneDevice = null;

        if(mp.isPlaying()) {
            mp.stop();
        }

        Log.i("mpisplaying", mp.isPlaying() + " " + mp_strong.isPlaying());

        if(mp_strong.isPlaying()) {
            mp_strong.stop();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //sqlite
        dbManager.closeDB();
    }

    //网络状态改变时设置提示文字
    @Override
    public void setNetworkState(String state) {
        if(state != null) {
            showNetworkState.setText(state);
            if(state.equals("网络未连接\n将停止上传数据")) {
                networkHelp.setNetwork();
            }
        }
    }
}
