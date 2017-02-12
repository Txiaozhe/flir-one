package com.flir.flirone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageListActivity extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        listView = (ListView) findViewById(R.id.list_view);
        simpleAdapter = new SimpleAdapter(this, getData(), R.layout.image_list_item,
                new String[]{"image", "name", "size", "time"}, new int[]{R.id.list_item_image,
                R.id.list_item_image_name, R.id.list_item_image_size, R.id.list_item_image_time});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ImageListActivity.this, ShowImageActivity.class);
                intent.putExtra("img_index", position);
                startActivity(intent);
            }
        });

    }

    private ArrayList<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();

        MyImage myImage = getDiskBitmap(GlobalParameter.IMAGE_PATH);
        try{
            File[] files = myImage.files;
            Log.i("length", files.length + "");
            for (int i = 0; i < files.length; i++) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("image", files[i].getPath());
                map.put("name", files[i].getName());
                map.put("size", "1.1MB");
                map.put("time", "2017-01-12 16:05");

                Log.i("imageinfo", files[i].getPath());
                arrayList.add(map);
            }

        } catch (Exception e) {

        }

        return arrayList;
    }

    private void showBigPicture(Context context, String path) {

    }

    class MyImage {
        Bitmap bitmap;
        File[] files;
    }

    //获取本地图片
    private MyImage getDiskBitmap(String pathString) {
        MyImage myImage = new MyImage();

        Bitmap bitmap = null;
        File[] files;
        try {
            File file = new File(pathString);
            files = file.listFiles();
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);

            }

            myImage.bitmap = bitmap;
            myImage.files = files;

        } catch (Exception e) {

        }

        return myImage;
    }
}

