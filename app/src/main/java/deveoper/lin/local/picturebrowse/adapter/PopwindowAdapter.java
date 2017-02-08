package deveoper.lin.local.picturebrowse.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flir.flirone.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import deveoper.lin.local.picturebrowse.entity.PictureFolderEntity;
import deveoper.lin.local.picturebrowse.util.ImageLoader;

/**
 * Created by lin on 2015/10/27.
 */
public class PopwindowAdapter extends BaseListAdapter<PictureFolderEntity> {

    public static final int THREAD_COUNT = 3;

    public PopwindowAdapter(ArrayList<PictureFolderEntity> mList, Context context) {
        super(mList, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_pop_window_adapter, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PictureFolderEntity entity = getItem(position);
        viewHolder.mTvCounts.setText(entity.getDirPhotoCount()+"");
        viewHolder.mTvName.setText(entity.getDirName());
        ImageLoader.getInstance(THREAD_COUNT, ImageLoader.Type.LIFO).loadImage(entity.getFirstImagePath()
                , viewHolder.mIvLeft);
        return convertView;
    }


    static class ViewHolder {
        ImageView mIvLeft;
        TextView mTvName;
        TextView mTvCounts;
        ImageView mIvChoice;

        ViewHolder(View view) {
            mIvLeft = (ImageView) view.findViewById(R.id.iv_left);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
            mTvCounts = (TextView) view.findViewById(R.id.tv_counts);
            mIvChoice = (ImageView) view.findViewById(R.id.iv_choice);

//            ButterKnife.inject(this, view);
        }
    }
}
