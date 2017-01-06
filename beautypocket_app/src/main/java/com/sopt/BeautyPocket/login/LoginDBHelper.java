package com.sopt.BeautyPocket.login;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016-12-31.
 */

public class LoginDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "login_db";
    public static final String TABLE_NAME = "login_table";
    public LoginDBHelper(Context context){super(context,DB_NAME,null,1);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        //테이블생성
        String createTable = "create table "+TABLE_NAME+"( _id integer primary key autoincrement, kakao_id text, kakao_name text);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
