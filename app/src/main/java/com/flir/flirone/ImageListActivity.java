package com.flir.flirone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.flir.flirone.imagehelp.ImageHelp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageListActivity extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private TextView imageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_list);

        imageNumber = (TextView) findViewById(R.id.image_number);

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

        ImageHelp imageHelp = new ImageHelp(GlobalConfig.IMAGE_PATH);
        try{
            File[] files = imageHelp.getFiles();
            Log.i("length", files.length + "");
            imageNumber.setText("共 " + files.length + " 张照片");
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                HashMap<String, Object> map = new HashMap<>();
                map.put("image", file.getPath());
                map.put("name", file.getName());
                map.put("size", imageHelp.getFileOrFilesSize(file));
                map.put("time", imageHelp.getTimeFromName(file));

                arrayList.add(map);
            }

        } catch (Exception e) {

        }

        return arrayList;
    }
}

