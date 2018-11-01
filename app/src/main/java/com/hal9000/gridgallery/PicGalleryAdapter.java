package com.hal9000.gridgallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PicGalleryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Integer> imageIDs;

    public PicGalleryAdapter(Context c, ArrayList<Integer> imageIDs)
    {
        context = c;
        this.imageIDs = imageIDs;
    }

    //---returns the number of images---
    public int getCount() {
        return imageIDs.size();
    }

    //---returns the ID of an item---
    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    //---returns an ImageView view---
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(500,500));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 5, 1, 5);

        } else {
            imageView = (ImageView) convertView;
        }

        // asynchronous image loading
        Picasso.get() //
                .load(imageIDs.get(position)) //
                .fit() //
                .tag(context) //
                .into(imageView);

        //imageView.setImageResource(imageIDs.get(position));   // uses UI thread to load images - makes app kinda laggy
        return imageView;
    }
}
