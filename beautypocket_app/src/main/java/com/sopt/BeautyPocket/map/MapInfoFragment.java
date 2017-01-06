/*
package com.sopt.BeautyPocket.map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.model.Store;

import org.w3c.dom.Text;

import java.util.ArrayList;

*/
/**
 * Created by user on 2016-12-29.
 *//*


public class MapInfoFragment extends Fragment {

    //네트워크 결과값 저장할 변수
    Store nearStore;

    //텍스트뷰
    TextView tv_storename;
    TextView tv_storeaddress;
    TextView tv_storetel;

    //커스텀어댑터
    */
/*MapInfoAdapter adapter;*//*

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //네트맵액티비티으로부터 정보 받아오기
        Bundle bundle = getArguments();*/
/*
        nearStore = (Store) bundle.getSerializable("nearStore");*//*

        nearStore = new Store();


    }
    @Override
    public void onResume() {
        super.onResume();
        nearStore =

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;
       v = inflater.inflate(R.layout.fragment_mapinfo,container,false);
        TextView tv_storename;
        TextView tv_storeaddress;
        TextView tv_storetel;
        tv_storename = (TextView)v.findViewById(R.id.tv_storename);
        tv_storeaddress = (TextView)v.findViewById(R.id.tv_storeaddress);
        tv_storetel = (TextView)v.findViewById(R.id.tv_storetel);

        tv_storename.setText(nearStore.getStore_name());
        tv_storeaddress.setText(nearStore.getStore_address());
        tv_storetel.setText(nearStore.getSotre_tel());




        return v;
    }


}
*/
