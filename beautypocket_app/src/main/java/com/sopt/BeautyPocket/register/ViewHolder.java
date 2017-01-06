package com.sopt.BeautyPocket.register;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sopt.BeautyPocket.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dhlee on 2016-12-28.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    CircleImageView circleImageView;
    TextView name_TextView;
    TextView price_TextView;

    public ViewHolder(View itemView) {
        super(itemView);

        circleImageView = (CircleImageView)itemView.findViewById(R.id.imageview_brand);
        name_TextView = (TextView)itemView.findViewById(R.id.name_TextView);
        price_TextView =(TextView)itemView.findViewById(R.id.price_TextView);
    }
}
