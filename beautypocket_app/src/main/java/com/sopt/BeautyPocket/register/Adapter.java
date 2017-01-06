package com.sopt.BeautyPocket.register;


import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.model.WishList;


import java.util.ArrayList;

/**
 * Created by dhlee on 2016-12-28.
 */

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    ArrayList<WishList> totalDatas; // all datas
    ArrayList<WishList> arSrc; // screen datas
    View.OnClickListener clickEvent;
    ArrayList<Integer> srcList;
    Context context;

    int src=0;


    public Adapter(ArrayList<WishList> itemDatas, View.OnClickListener clickEvent, Context context) {
        arSrc = itemDatas;
        this.totalDatas = new ArrayList<WishList>();
        this.totalDatas.addAll(arSrc);
        this.clickEvent = clickEvent;
        this.context = context;
    }


    public void setAdaper(ArrayList<WishList> wishLists) {
        arSrc = wishLists;
        this.totalDatas = new ArrayList<WishList>();
        this.totalDatas.addAll(arSrc);

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        itemView.setOnClickListener(clickEvent);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,  int position) {
        Glide.with(context)
                .load(arSrc.get(position).getPro_image())
                .into(holder.circleImageView);
        holder.name_TextView.setText(arSrc.get(position).getPro_name());
        holder.price_TextView.setText(String.valueOf(arSrc.get(position).getPro_price()));
    }

    @Override
    public int getItemCount() {
        return (arSrc != null) ? arSrc.size() : 0;
    }

    public void clear(){
        totalDatas.clear();
        arSrc.clear();
    }

    /*** NetWork에 관한 검색 출력 메소드 ***/
    public void PrintMethod()
    {
        for(int i = 0 ; i < totalDatas.size(); i++)
        {
            arSrc.add(totalDatas.get(i));
        }
    }

    //Filter Method
  /*  public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        arSrc.clear();
        srcList = new ArrayList<Integer>();
        if (charText.length() == 0) {
            arSrc.addAll(totalDatas);
        } else {
            for (int i = 0; i < totalDatas.size(); i++) {
                String wp = totalDatas.get(i).getWish_title();
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    srcList.add(i);
                    arSrc.add(totalDatas.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }*/

    public ArrayList<Integer> getResult(){
        srcList = new ArrayList<Integer>();
        for(int i=0; i<arSrc.size(); i++) {
            srcList.add(i);
        }

        return srcList;
    }
}