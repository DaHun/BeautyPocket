package com.sopt.BeautyPocket.map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.LocationManager;
import android.util.Log;
import android.widget.RemoteViews;


import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.model.Store;

/**
 * Created by user on 2016-12-29.
 */

public class LocationReceiver extends BroadcastReceiver{
    NotificationManager nm;
    Store st;
    Geocoder geocoder;


    public void onReceive(Context context, Intent intent) {

        boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        if(ApplicationController.nearStore!=null&isEntering) {
            Log.d("리시버","위치가 가까워요!!~!~!");
            Intent intent1 = new Intent(context, MapActivity.class);
            ApplicationController.mapAll =true;
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent content = PendingIntent.getActivity(context, 0, intent1, 0);


           st = ApplicationController.nearStore;




            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.beauty_pic);

            builder.setTicker("근처에 즐겨찾기한 가게가 있어요!");
            builder.setWhen(System.currentTimeMillis());//알람시간에 notification표시
            builder.setNumber(10);
            builder.setContentIntent(content);//notification을 클릭하면 알람 앱 화면 실행
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            builder.setVibrate(new long[]{1000, 1000, 500, 500, 200, 200, 200, 200, 200, 200});
            builder.setLights(Color.BLUE, 500, 500);
            Notification noti = builder.build();

            //커스텀 notification 사용
            RemoteViews contentview = new RemoteViews(context.getPackageName(), R.layout.custom_notiview);
           contentview.setImageViewResource(R.id.image,R.drawable.ic_beautypocket);

            contentview.setTextViewText(R.id.text1, ApplicationController.itemDataList.get(st.getBrand_id()).getName()+"("+st.getStore_name()+")");
            contentview.setTextViewText(R.id.text2, st.getStore_address());
            noti.contentView = contentview;
            noti.flags = noti.flags | noti.FLAG_AUTO_CANCEL;
            nm.notify(1, noti);
        }else
        {
            Log.d("리시버","위치가 너무멀어요!");
        }
    }
}
