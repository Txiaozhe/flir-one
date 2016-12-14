package com.flir.flironeexampleapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;
import com.flir.flironesdk.LoadedFrame;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;
import java.util.Map;

/**
 * Example thermal image editor activity
 */
public class EditorActivity extends Activity {

        private static final String LOG_TAG = EditorActivity.class.getSimpleName();

        private ImageView imageView;

        private FrameProcessor frameProcessor;
        volatile RenderedImage msxRenderedImage;
        volatile Bitmap thermalBitmap;
        File frameFile;
        Map<RenderedImage.ImageType, RenderedImage> renderedImageMap;
        private FrameProcessor.Delegate frameReceiver = new FrameProcessor.Delegate() {
            @Override
            public void onFrameProcessed(RenderedImage renderedImage) {
                Log.d(LOG_TAG, "onFrameProcessed");

                if (renderedImage.imageType() == RenderedImage.ImageType.BlendedMSXRGBA8888Image) {
                    msxRenderedImage = renderedImage;
                    thermalBitmap = Bitmap.createBitmap(renderedImage.width(), renderedImage.height(), Bitmap.Config.ARGB_8888);
                    thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(renderedImage.pixelData()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ((ImageView) findViewById(R.id.editorImageView)).setImageBitmap(thermalBitmap);
                        }
                    });
                }else if (renderedImage.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
                    double averageTemp = 0;
                    short[] shortPixels = new short[renderedImage.pixelData().length / 2];
                    ByteBuffer.wrap(renderedImage.pixelData()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortPixels);
                    for (int i = 0; i < shortPixels.length; i++) {
                        averageTemp += (((int)shortPixels[i]) - averageTemp) / ((double) i + 1);
                    }
                    final double averageC = (averageTemp / 100) - 273.15;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Average Temperature = " + averageC + "ºC", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        };

        private static String getRealPathFromUri(Context context, Uri contentUri) {
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                if (cursor == null){
                    return (new File(contentUri.getPath())).getAbsolutePath();
                }
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_editor);
            imageView = (ImageView) findViewById(R.id.imageView);

            Intent intent = getIntent();
            String path;
            Uri data = intent.getData();
            if (data == null){
                // TODO: handle a "share" intent

            }
            if(data != null) {

                frameFile = new File(getRealPathFromUri(getApplicationContext(),data));
                final LoadedFrame frame;
                try {
                    frame = new LoadedFrame(frameFile);
                }catch (final RuntimeException ex){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }


                Log.d(LOG_TAG, "loaded frame: " + frame);
                final Context context = this.getApplicationContext();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        frameProcessor = new FrameProcessor(context, frameReceiver,
                                EnumSet.of(frame.getPreviewImageType(), RenderedImage.ImageType.ThermalRadiometricKelvinImage));
                        frameProcessor.setImagePalette(frame.getPreviewPalette());
                        frameProcessor.processFrame(frame);
                    }
                }).start();


            }


        }
    public void onImageClick(View v){
        if (msxRenderedImage != null) {
            RenderedImage.Palette currentPalette = msxRenderedImage.palette();
            RenderedImage.Palette[] palettes = RenderedImage.Palette.values();
            int nextPaletteOrdinal = (currentPalette.ordinal() + 1) % palettes.length;
            frameProcessor.setImagePalette(palettes[nextPaletteOrdinal]);
            renderedImageMap = frameProcessor.getProcessedFrames(new LoadedFrame(frameFile));
            msxRenderedImage = renderedImageMap.get(RenderedImage.ImageType.BlendedMSXRGBA8888Image);

            thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(msxRenderedImage.pixelData()));
            ((ImageView) findViewById(R.id.editorImageView)).setImageBitmap(thermalBitmap);
        }
    }
}
