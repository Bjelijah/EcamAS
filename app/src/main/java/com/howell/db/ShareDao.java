package com.howell.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.howell.bean.ShareItem2Bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public class ShareDao {
    DBHelper dbHelper;
    SQLiteDatabase db;

    public ShareDao(Context context){
        dbHelper = new DBHelper(context,"user.db",null,1);
    }

    public void insertAddShare(String shareName,String addTime,String devID,String devName){
        db = dbHelper.getWritableDatabase();
        String sql = "insert into addshare (sharename,addtime,devId,devName)values(?,?,?,?);";
        db.execSQL(sql,new Object[]{shareName,addTime,devID,devName});
    }

    public ArrayList<ShareItem2Bean> queryAllAddShare(@Nullable String devID){
        ArrayList<ShareItem2Bean> l = new ArrayList();
        db = dbHelper.getWritableDatabase();
        String sql = devID==null?"select * from addshare;":"select * from addshare where devId=?;";
        Cursor cursor = db.rawQuery(sql,devID==null?null:new String[]{devID});
        while (cursor.moveToNext()){
            String shareDevName=null,shareDevId=null;
            try{ shareDevName = cursor.getString(cursor.getColumnIndex("devName"));}catch (Exception e){}
            try{ shareDevId = cursor.getString(cursor.getColumnIndex("devId"));}catch (Exception e){}
            String shareName=cursor.getString(cursor.getColumnIndex("sharename"));
            String time = cursor.getString(cursor.getColumnIndex("addtime"));
            l.add(new ShareItem2Bean(shareDevId,shareDevName,shareName,time));
        }
        return l;
    }

    public void delAllAddShare(@Nullable String devID){
        db = dbHelper.getWritableDatabase();
        String sql = devID==null?"delete from addshare;":"delete from addshare where devId=?;";
        db.execSQL(sql,devID==null?new Object[]{}:new Object[]{devID});
    }

    public void close(){
        if(null!=db){
            db.close();
            db = null;
        }
        if (dbHelper!=null) {
            dbHelper.close();
            dbHelper = null;
        }
    }
}
