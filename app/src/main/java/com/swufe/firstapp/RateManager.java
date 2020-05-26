package com.swufe.firstapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//操作类
public class RateManager {

    private DBHelper dbHelper;
    private String TBNANE;

    public RateManager(Context context){
        dbHelper = new DBHelper(context);
        TBNANE = DBHelper.TB_NAME;
    }

    //插入数据
    public void add(RateItem item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取可写数据库
        ContentValues values = new ContentValues();//创建ContentValues对象
        values.put("curname",item.getCurName());
        values.put("currate",item.getCurRate());
        db.insert(TBNANE,null,values);
        db.close();
    }

    //查询所有数据
    public List<RateItem> listAll(){
        List<RateItem> rateList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();//创建可写数据库
        //查询数据库中所有数据，返回cursor光标对象
        Cursor cursor = db.query(TBNANE,null,null,null,null,null,null);
        //将cursor中数据封装到rateList
        if(cursor != null){
            rateList = new ArrayList<RateItem>();
            while(cursor.moveToNext()){
                RateItem item = new RateItem();
                //按照列名获取每条记录数据，封装成item记录对象
                item.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                item.setCurName(cursor.getString(cursor.getColumnIndex("CURNAME")));
                item.setCurRate(cursor.getString(cursor.getColumnIndex("CURRATE")));
                rateList.add(item);
            }
            cursor.close();
        }
        return  rateList;
    }

    public void deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNANE,null,null);
        db.close();
    }

    public void addAll(List<RateItem> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (RateItem item : list) {
            ContentValues values = new ContentValues();
            values.put("curname", item.getCurName());
            values.put("currate", item.getCurRate());
            db.insert(TBNANE, null, values);
        }
        db.close();
    }


}
