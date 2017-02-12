package developer.lin.local.picturebrowse.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flir.flirone.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import developer.lin.local.picturebrowse.ShowImageActivity;
import developer.lin.local.picturebrowse.entity.GirdViewEntity;
import developer.lin.local.picturebrowse.util.ImageLoader;

import static java.security.AccessController.getContext;

/**
 * Created by lin on 2015/10/27.
 */
public class GirdViewAdapter extends BaseListAdapter<GirdViewEntity> {

    public static final int THREAD_COUNT = 3;
    public Context context;

    public GirdViewAdapter(ArrayList<GirdViewEntity> mList, Context context) {
        super(mList, context);
        this.context = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_gird_view_adapter, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        GirdViewEntity entity = getItem(position);
        ImageLoader.getInstance(THREAD_COUNT, ImageLoader.Type.LIFO).loadImage(entity.getAbsolutePath(), viewHolder.imageView);

        if (entity.isSelected()) {

        } else {
            viewHolder.imageView.clearColorFilter();
        }

        //点选图片预览
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("imageid", "image in " + position);
                Intent i = new Intent(context, ShowImageActivity.class);
                i.putExtra("img_index", position);
                context.startActivity(i);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;

        ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.image_view);
            //ButterKnife.inject(this, view);
        }
    }
}
