package com.sopt.BeautyPocket.main.sale;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.model.MainSale;

import java.util.ArrayList;
/**
 * Created by user on 2017-01-04.
 */

class MainSaleAdapter extends ArrayAdapter<MainSale> {
    ArrayList<MainSale> items;
    Context context;

    public MainSaleAdapter(Context context, int resource, ArrayList<MainSale> items) {
        super(context, resource, items);
        this.context=context;
        this.items=items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(v==null){
            LayoutInflater vi=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=vi.inflate(R.layout.sale_row,null);
        }

        MainSale mainSale=items.get(position);
        if(mainSale != null){
            ImageView imageView=(ImageView) v.findViewById(R.id.sale_img);
            //TextView textView_day=(TextView)v.findViewById(R.id.saleday);
            TextView textView_info=(TextView)v.findViewById(R.id.sale_title);

            Glide.with(getContext()).load(mainSale.sale_image).into(imageView);
            //textView_day.setText(mainSale.sale_day);
            textView_info.setText(mainSale.sale_title);

        }

        return v;
    }
}