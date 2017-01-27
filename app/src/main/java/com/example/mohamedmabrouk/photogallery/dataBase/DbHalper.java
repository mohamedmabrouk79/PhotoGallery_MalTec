package com.example.mohamedmabrouk.photogallery.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mohamedmabrouk.photogallery.dataBase.DBschema.Table;

/**
 * Created by moham on 25/01/2017.
 */

public class DbHalper extends SQLiteOpenHelper {
    private static int VERSION=1;
    private static String DBName="photoGallery.db";
    public DbHalper(Context context) {
        super(context, DBName, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+ Table.NAME+ "(" +
                " _id integer primary key autoincrement, " +
                Table.CLOS.URL +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
