package com.sopt.BeautyPocket.calender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.model.CalendarSale;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 2017-01-04.
 */

public class CalendarSaleAdapter extends ArrayAdapter<CalendarSale> {

    ArrayList<CalendarSale> items;
    Context context;

    public CalendarSaleAdapter(Context context, int resource, ArrayList<CalendarSale> objects) {
        super(context, resource, objects);
        this.context = context;
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) { // view가 하나도 없을 경우
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.listview_item_calendar, null);
        }
        CalendarSale c = items.get(position);
        if (c != null) {
            ImageView calendarSale_img = (ImageView) v.findViewById(R.id.calendarSale_img);
            TextView tv_brandname = (TextView)v.findViewById(R.id.tv_brandname);
            TextView tv_sale_day = (TextView) v.findViewById(R.id.sale_day);
            TextView tv_sale_end = (TextView)v.findViewById(R.id.sale_end);

            for(int i=0; i< ApplicationController.itemDataList2.size();i++) {
                if (items.get(position).getBrand_id() == ApplicationController.itemDataList2.get(i).get_id()) {
                    Glide.with(context)
                            .load(ApplicationController.itemDataList2.get(i).getIcon())
                            .bitmapTransform(new CropCircleTransformation(context))
                            .into(calendarSale_img);
                    tv_brandname.setText(ApplicationController.itemDataList.get(i).getName());
                    String m = (items.get(position).getSale_day()).substring(4,6);
                    String d = (items.get(position).getSale_day()).substring(6,8);

                    String m2 = (items.get(position).getSale_end()).substring(4,6);
                    String d2 = (items.get(position).getSale_end()).substring(6,8);

                    tv_sale_day.setText(m+"월 "+d+"일" );
                    tv_sale_end.setText(m2+"월 "+d2+"일");
                }

            }



        }
        return v;
    }
}
