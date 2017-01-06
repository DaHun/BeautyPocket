package com.sopt.BeautyPocket.main;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.map.MapActivity;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 2016-12-28.
 */

public class MainWishAdapter extends BaseAdapter{

    LayoutInflater inflater;
    Context context;
    int layout;
    ItemData itemData;
    ArrayList<ItemData> list;
    ImageView imageview_brand;
    TextView textview_brandname;
    ImageView btn_brandmap;

    public MainWishAdapter(Context context, int resource, ArrayList<ItemData> list){
        this.context =context;
        this.layout = resource;
        this.list =list;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
//            check
            convertView = inflater.inflate(R.layout.listview_item_mainwish, parent, false);
        }

        imageview_brand = (ImageView) convertView.findViewById(R.id.imageview_brand);
        textview_brandname = (TextView)convertView.findViewById(R.id.textview_brandname);
        btn_brandmap = (ImageView)convertView.findViewById(R.id.btn_brandmap);


        itemData = list.get(position);

        imageview_brand.setImageResource(itemData.getIcon());
        Glide.with(context)
                .load(itemData.getIcon())
                .bitmapTransform(new CropCircleTransformation(context))
                .into(imageview_brand);
        textview_brandname.setText(itemData.getName());

        btn_brandmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationController.mapAll = false;
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("brand_id",list.get(position).get_id());
                context.startActivity(intent);

            }
        });





        return convertView;
    }
}
