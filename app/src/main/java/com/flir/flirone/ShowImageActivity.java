package com.flir.flirone;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flir.flirone.imagehelp.ImageHelp;
import com.flir.flirone.imagehelp.ImageInfo;

import java.io.File;

import static com.flir.flirone.GlobalConfig.IMAGE_PATH;

public class ShowImageActivity extends Activity {

    private ImageView showImage;

    private TextView showImagePath;
    private TextView showImageSize;
    private TextView showImageTime;
    private TextView showTemp;
    private TextView showCoordinate;

    ImageHelp imageHelp;

    private void init() {
        showImage = (ImageView) findViewById(R.id.iv_show_image);

        showImagePath = (TextView) findViewById(R.id.tv_show_image_path);
        showImageSize = (TextView) findViewById(R.id.tv_show_image_size);
        showImageTime = (TextView) findViewById(R.id.tv_show_image_time);

        showTemp = (TextView) findViewById(R.id.show_temp);
        showCoordinate = (TextView) findViewById(R.id.show_coordinate);
    }

    private void setImageInfo(File file) {
        ImageInfo info = imageHelp.getInfoFromName(file.getName());

        showImagePath.setText(info.getName() + ".jpg");
        showImageSize.setText(imageHelp.getFileOrFilesSize(file));
        showImageTime.setText(imageHelp.getTimeFromName(info.getName()));

        showTemp.setText("最高温度：" + info.getMaxTemp() + "℃  平均温度：" + info.getAverTemp() + "℃");
        showCoordinate.setText("最高温度坐标： (" + info.getMaxTempX() + "，" + info.getMaxTempY() + ")");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        imageHelp = new ImageHelp(IMAGE_PATH);

        init();

        if(getIntent() != null) {
            int img_index = getIntent().getExtras().getInt("img_index");
            File file = imageHelp.getFiles()[img_index];
            showImage.setImageBitmap(imageHelp.getBitMap(file.getPath()));

            setImageInfo(file);
        }

        showImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == 0) {
                    finish();
                }
                return false;
            }
        });
    }



}
