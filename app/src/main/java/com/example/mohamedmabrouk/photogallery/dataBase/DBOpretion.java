package com.example.mohamedmabrouk.photogallery.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mohamedmabrouk.photogallery.dataBase.DBschema.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moham on 25/01/2017.
 */

public class DBOpretion {
    private SQLiteDatabase mDatabase;
    private Context  mContext;

    public DBOpretion(Context context){
        mContext=context;
        mDatabase=new DbHalper(mContext).getWritableDatabase();
    }

    public void deleteItem(String url){
        mDatabase.delete(Table.NAME,Table.CLOS.URL+" = ? ",new String[]{url});
    }

    public boolean insert(String url){
        ContentValues values=new ContentValues();
        values.put(Table.CLOS.URL,url);
      long m=  mDatabase.insert(Table.NAME,null,values);
        if (m>-1){
            return true;
        }else{
            return false;
        }
    }
    public void delete(){
        mDatabase.delete(Table.NAME,null,null);
    }

    public List<String> getList() {
        List<String> list = new ArrayList<>();
        Cursor cursor = mDatabase.query(
                Table.NAME,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null // orderBy
        );
        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {
                String url = cursor.getString(cursor.getColumnIndex(Table.CLOS.URL));
                list.add(url);

                cursor.moveToNext();
            }
            return list;
        }finally {
            cursor.close();
        }
    }




}
