package developer.lin.local.picturebrowse;

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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import developer.lin.local.picturebrowse.util.ImageUtils;

/**
 * Created by txiaozhe on 08/02/2017.
 */

public class ShowImageActivity extends Activity {

    private ImageView showImage;

    private TextView showImagePath;
    private TextView showImageSize;
    private TextView showImageTime;

    private static final String IMAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

    private void init() {
        showImage = (ImageView) findViewById(R.id.iv_show_image);

        showImagePath = (TextView) findViewById(R.id.tv_show_image_path);
        showImageSize = (TextView) findViewById(R.id.tv_show_image_size);
        showImageTime = (TextView) findViewById(R.id.tv_show_image_time);
    }

    private void setImageInfo(File file) {
        String name = file.getName();
        String path = file.getPath();

        String time = name.substring(name.indexOf("-") + 1, name.length() - 4);
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);
        String hour = time.substring(8, 10);
        String min = time.substring(10, 12);
        String sec = time.substring(12, time.length());

        showImagePath.setText(path.substring(0, path.indexOf("Pictures/") + 9) + "\n" + name);
        showImageSize.setText(getFileOrFilesSize(file));
        showImageTime.setText(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_image);

        init();

        ImageUtils imageUtils = new ImageUtils(IMAGE_PATH);
        File[] files = imageUtils.getDiskFiles();

        if(getIntent() != null) {
            int img_index = getIntent().getExtras().getInt("img_index");
            Log.i("img_index", "img in " + img_index);
            Bitmap bm = imageUtils.getDiskBitmap(files[img_index].getPath());
            showImage.setImageBitmap(bm);
            setImageInfo(files[img_index]);
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

    private String getFileOrFilesSize(File file){
        long blockSize=0;
        try {
            blockSize = getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小","获取失败!");
        }
        return FormetFileSize(blockSize);
    }

    private long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()){
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        else{
            file.createNewFile();
            Log.e("获取文件大小","文件不存在!");
        }
        return size;
    }

    private String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";
        if(fileS==0){
            return wrongSize;
        }
        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }
        else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }
        else{
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}
