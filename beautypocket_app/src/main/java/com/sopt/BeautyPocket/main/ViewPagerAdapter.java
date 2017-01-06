package com.sopt.BeautyPocket.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.main.sale.SaleDetailActivity;
import com.sopt.BeautyPocket.model.MainSale;

import java.util.ArrayList;

/**
 * Created by user on 2016-12-28.
 */

public class ViewPagerAdapter extends PagerAdapter{

    ArrayList<MainSale> userImage;
    LayoutInflater inflater;
    Context context;
    String[] brand_name = {"이니스프리", "네이처리퍼블릭", "더샘", "더페이스샵", "미샤", "VDL", "스킨푸드", "아리따움", "어퓨", "에뛰드하우스", "에스쁘아", "올리브영", "잇츠스킨", "토니모리", "홀리카홀리카"};


    public ViewPagerAdapter(Context context, ArrayList<MainSale> userImage) {
        this.context = context;
        this.userImage = userImage;
    }

    @Override
    public int getCount() {
        return userImage.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // TODO Auto-generated method stub
        View view = null;
        inflater = ((Activity)context).getLayoutInflater();
        final MainSale sale = userImage.get(position);
        view= inflater.inflate(R.layout.viewpager_childview, container,false);


        ImageView img= (ImageView)view.findViewById(R.id.img);
        TextView tv = (TextView)view.findViewById(R.id.tv_mainSale);
        Glide.with(context)
                .load(sale.sale_image).
                into(img);
        String title = "["+brand_name[sale.brand_id]+"] "+sale.sale_title;
        tv.setText(title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SaleDetailActivity.class);
                intent.putExtra("brand_id", sale.brand_id);
                intent.putExtra("sale_title", sale.sale_title);
                intent.putExtra("sale_info", sale.sale_info);
                intent.putExtra("sale_image", sale.sale_image);
                context.startActivity(intent);
            }
        });

        container.addView(view);

        return view;

    }
    @Override

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);

    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==object;
    }
}