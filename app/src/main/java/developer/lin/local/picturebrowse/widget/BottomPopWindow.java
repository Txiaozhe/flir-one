package developer.lin.local.picturebrowse.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.flir.flirone.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import developer.lin.local.picturebrowse.adapter.PopwindowAdapter;
import developer.lin.local.picturebrowse.entity.PictureFolderEntity;

/**
 * Created by lin on 2015/10/27.
 */
public class BottomPopWindow extends PopupWindow {

    private ListView mListView;
    private Context mContext;
    private ArrayList<PictureFolderEntity> mList;
    private int width;
    private int height;
    private View convertView;
    private LayoutInflater mInflater;
    private PopwindowAdapter mPopwindowAdapter;

    private void init(Context context) {

    }

    public BottomPopWindow(Context context, ArrayList<PictureFolderEntity> list) {
        super(context);
        this.mContext = context;
        this.mList = list;
        mInflater = LayoutInflater.from(context);
        initLayoutParameters(context);
        initData(list);

        init(context);
    }

    private void initLayoutParameters(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = (int) (displayMetrics.heightPixels * 0.65);
        setWidth(width);
        setHeight(height);
        initConvertView();
        initConfig();
    }

    private void initConvertView() {
        convertView = mInflater.inflate(R.layout.widget_bottom_pop_window, null);
        mListView = (ListView) convertView.findViewById(R.id.list_view);
//        ButterKnife.inject(this, convertView);
        setContentView(convertView);
    }

    private void initConfig() {
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    private void initData(ArrayList<PictureFolderEntity> list) {
        mPopwindowAdapter = new PopwindowAdapter(list, mContext);
        mListView.setAdapter(mPopwindowAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    mListener.changeListener(mList.get(position));
                }
            }
        });
    }


    public interface changeListener {
        void changeListener(PictureFolderEntity entity);
    }

    private changeListener mListener;

    public void setChangeListener(changeListener listener) {
        this.mListener = listener;
    }


}
