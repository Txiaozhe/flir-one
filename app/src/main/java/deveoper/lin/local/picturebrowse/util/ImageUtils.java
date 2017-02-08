package deveoper.lin.local.picturebrowse.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

/**
 * Created by txiaozhe on 08/02/2017.
 */

public class ImageUtils {

    private String path;

    public ImageUtils(String path) {
        this.path = path;
    }

    //获取本地图片
    public Bitmap getDiskBitmap(String path) {

        Bitmap bitmap = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(path);
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.i("filessssss", e.toString());
        }

        return bitmap;
    }

    public File[] getDiskFiles() {
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
