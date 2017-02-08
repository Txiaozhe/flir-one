package deveoper.lin.local.picturebrowse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by lin on 2015/10/27.
 */
public class BaseListAdapter<T> extends BaseAdapter {

    public static final int ZERO = 0;

    private ArrayList<T> mList;
    public Context mContext;
    public LayoutInflater mInflater;

    public BaseListAdapter(ArrayList<T> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    protected ArrayList<T> getData() {
        return mList;
    }

    protected boolean isNotEmptyOrNull() {
        return (mList != null && !mList.isEmpty());
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : ZERO;
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }
}
