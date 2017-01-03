package com.flir.flirone;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by txiaozhe on 02/01/2017.
 */

public class ShowImageActivity extends Activity {

    private ImageView showImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimage);

        showImage = (ImageView) findViewById(R.id.id_showImage);

        showImage.setImageBitmap((Bitmap) getIntent().getParcelableExtra("bitmap"));
    }
}
