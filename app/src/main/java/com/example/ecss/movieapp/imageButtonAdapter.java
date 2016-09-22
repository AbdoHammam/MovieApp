package com.example.ecss.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class imageButtonAdapter extends BaseAdapter {
    private Context mContext;
    List<MovieDetail> arr;

    public imageButtonAdapter(Context mContext, List<MovieDetail> arr) {
        this.mContext = mContext;
        this.arr = arr;
    }

    class Holder {
        ImageView imageview;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public MovieDetail getItem(int i) {
        return arr.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
            holder.imageview = (ImageView) convertView.findViewById(R.id.gridItemImage);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w500/" + getItem(position).getPoster_path()).resize(250, 320).into(holder.imageview);



        return convertView;
    }
}
