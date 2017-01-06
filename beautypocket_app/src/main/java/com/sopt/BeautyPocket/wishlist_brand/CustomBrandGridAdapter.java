package com.sopt.BeautyPocket.wishlist_brand;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.main.ItemData;
import com.sopt.BeautyPocket.wishlist.WishlistActivity;

import java.util.ArrayList;

/**
 * Created by samsung on 2017-01-04.
 */
public class CustomBrandGridAdapter extends BaseAdapter {
    //Context 밑 네트워크로 부터 받은 ItemData Set
    private Context context;
    private ArrayList<ItemData> gridValues;
    private ArrayList<ItemData> changeListValues;

    //LongClick 여부에 따라 CheckBox Visible 관리
    Boolean isLong = false;

    public CustomBrandGridAdapter(Context context, ArrayList<ItemData> gridValues) {
        this.context = context;
        this.gridValues = gridValues;
        this.changeListValues = gridValues;
    }

    public void setCustomBrandGridAdapter(ArrayList<ItemData> gridValues) {
        this.changeListValues = gridValues;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        BrandViewHolder holder = null;

        //각각의 GridView를 holder에 등록
        holder= new BrandViewHolder();
        convertView = inflater.inflate(R.layout.grid_brand, parent,false);

        holder.image_brand = (ImageView) convertView.findViewById(R.id.iv_grid_brand);
        holder.tv_brand = (TextView)convertView.findViewById(R.id.tv_grid_brand);
        Glide.with(context)
                .load(changeListValues.get(position).getIcon())
                .into(holder.image_brand);

        holder.tv_brand.setText(changeListValues.get(position).getName());
        Log.i("HJTAG", changeListValues.get(position).getName()+" ");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WishlistActivity.class);
                intent.putExtra("brand_id", changeListValues.get(position).get_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {return changeListValues.size();}

    @Override
    public ItemData getItem(int position) {return changeListValues.get(position);}

    @Override
    public long getItemId(int position) {return 0;}
}