package com.flir.flironeexampleapplication.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flir.flironeexampleapplication.R;

/**
 * Created by txiaozhe on 18/12/2016.
 */

public class GridAdapter extends BaseAdapter {

    private String[] tag;
    private String[] tags;

    private Context mContext;
    private LayoutInflater inflater;

    private class GirdTemp{
        TextView palette_tag;
        TextView palette_text;
    }
    public GridAdapter(Context c){
        mContext = c;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public GridAdapter(Context c, String tag[], String tags[]){
        mContext = c;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.tag = tag;
        this.tags = tags;
    }

    @Override
    public int getCount() {
        return tags.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GirdTemp temp = new GirdTemp();
        if(convertView == null){
            convertView = inflater.inflate(R.layout.palettegridviewitem, null);

            temp.palette_tag = (TextView) convertView.findViewById(R.id.palette_tg);
            temp.palette_text = (TextView) convertView.findViewById(R.id.palette_tag);

            temp.palette_tag.setText(tag[position]);
            temp.palette_text.setText(tags[position]);
            convertView.setTag("tag" + position);
        }else{

        }

        return convertView;
    }

}
