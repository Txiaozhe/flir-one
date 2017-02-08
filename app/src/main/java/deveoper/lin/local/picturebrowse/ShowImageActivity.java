package deveoper.lin.local.picturebrowse;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flir.flirone.R;

import java.io.File;

import deveoper.lin.local.picturebrowse.util.ImageUtils;

/**
 * Created by txiaozhe on 08/02/2017.
 */

public class ShowImageActivity extends Activity {

    private ImageView showImage;
    private TextView showImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_image);

        showImage = (ImageView) findViewById(R.id.iv_show_image);
        showImageName = (TextView) findViewById(R.id.tv_show_image_name);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        ImageUtils imageUtils = new ImageUtils(path);
        File[] files = imageUtils.getDiskFiles();

        if(getIntent() != null) {
            int img_index = getIntent().getExtras().getInt("img_index");
            Log.i("img_index", "img in " + img_index);
            Bitmap bm = imageUtils.getDiskBitmap(files[img_index].getPath());
            showImage.setImageBitmap(bm);
            showImageName.setText(files[img_index].getName());
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

    //获取本地图片
    private Bitmap getDiskBitmap(String pathString) {

        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);

            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.i("filessssss", e.toString());
        }

        return bitmap;
    }

    private File[] getDiskFiles(String path) {
        File[] files = null;
        try {
            File file = new File(path);
            files = file.listFiles();
        } catch (Exception e) {
            Log.i("filessssss", e.toString());
        }
        return files;
    }
}
