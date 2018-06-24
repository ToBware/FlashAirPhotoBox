package com.example.tblume.flashairphotobox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    public final int NUM_PAGES = 3;
    private LayoutInflater layoutInflater;
    public List<String> fileList;

    public ImagePagerAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fileList = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = layoutInflater.inflate(R.layout.image_pager_item, container, false);
        itemView.setTag(fileList.get(position));

        ImageView imageView = itemView.findViewById(R.id.pagerImageView);

        try {
            Picasso.with(context).
                    load("http://flashair/" + fileList.get(position))
                    .resize(480, 360)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView);
        } catch(Exception exc) {

        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(Object object) {

        int position = POSITION_NONE;
        String tag = ((View)object).getTag().toString();
        for (int currPosition = 0; currPosition < fileList.size(); currPosition++) {
            String currFile = fileList.get(currPosition);
            if (tag.equals(currFile)) {
                position = currPosition;
                break;
            }
        }

        return position;
    }
}
