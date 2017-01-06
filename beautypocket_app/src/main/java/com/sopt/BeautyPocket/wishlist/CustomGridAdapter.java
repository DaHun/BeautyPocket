package com.sopt.BeautyPocket.wishlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.map.MapActivity;
import com.sopt.BeautyPocket.model.WishList;

import java.util.ArrayList;

/**
 * Created by dhlee on 2016-12-29.
 */

public class CustomGridAdapter extends BaseAdapter {
    //Context 밑 네트워크로 부터 받은 ItemData Set
    private Context context;
    private ArrayList<WishList> gridValues;
    private ArrayList<WishList> changeListValues;

    //LongClick 여부에 따라 CheckBox Visible 관리
    Boolean isLong = false;

    public CustomGridAdapter(Context context, ArrayList<WishList> gridValues) {
        this.context = context;
        this.gridValues = gridValues;
        this.changeListValues = gridValues;
    }

    public void setCustomGridAdpater(ArrayList<WishList> gridValues) {
        this.changeListValues = gridValues;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;

        //각각의 GridView를 holder에 등록
        holder= new ViewHolder();
        convertView = inflater.inflate(R.layout.gridlayout_item, parent,false);

        holder.cb_grid = (CheckBox)convertView.findViewById(R.id.cb_grid);
        holder.img_grid = (ImageView) convertView.findViewById(R.id.img_grid);
        holder.tv_grid_productname= (TextView) convertView.findViewById(R.id.tv_grid_productname);
        holder.tv_grid_price= (TextView) convertView.findViewById(R.id.tv_grid_price);
        holder.btn_gobrandmap = (ImageView)convertView.findViewById(R.id.btn_gobrandmap);

        holder.cb_grid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeListValues.get(position).isCheck = isChecked;
            }
        });
        holder.btn_gobrandmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationController.mapAll = false;
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("brand_id",getItem(position).getBrand_id());
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        //LongClick 여부에 따라 CheckBox Visible 관리
        if(isLong) {
            holder.cb_grid.setVisibility(View.VISIBLE);
        }else {
            changeListValues.get(position).isCheck=false;
            holder.cb_grid.setVisibility(View.GONE);
        }

        holder.cb_grid.setChecked(changeListValues.get(position).isCheck);
//        holder.img_grid.setImageResource(changeListValues.get(position).temp_img);
        Glide.with(context)
                .load(changeListValues.get(position).getWish_image())
                .into(holder.img_grid);
        holder.tv_grid_productname.setText(changeListValues.get(position).getWish_title());
        holder.tv_grid_price.setText(String.valueOf(changeListValues.get(position).getWish_price()));

        return convertView;
    }

    @Override
    public int getCount() {return changeListValues.size();}
    @Override
    public WishList getItem(int position) {return changeListValues.get(position);}
    @Override
    public long getItemId(int position) {return 0;}
}