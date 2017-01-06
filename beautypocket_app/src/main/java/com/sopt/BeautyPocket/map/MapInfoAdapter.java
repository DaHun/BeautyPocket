/*
package com.sopt.BeautyPocket.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.model.Store;

import org.w3c.dom.Text;



public class MapInfoAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    int layout;

    Store st;

    public MapInfoAdapter(Store st){
        this.st = st;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
//            check
           view = inflater.inflate(R.layout.fragment_mapinfo, viewGroup, false);

        }
        TextView tv_storename = (TextView)view.findViewById(R.id.tv_storename);
        TextView tv_storeaddress = (TextView)view.findViewById(R.id.tv_storeaddress);
        TextView tv_storetel = (TextView)view.findViewById(R.id.tv_storetel);

        tv_storename.setText(st.getStore_name());
        tv_storeaddress.setText(st.getStore_address());
        tv_storetel.setText(st.getSotre_tel());


        return view;
    }
}
*/
