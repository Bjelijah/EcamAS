package com.howell.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.howell.bean.APDeviceDBBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/12/2.
 */

public class ApDeviceDao {

    DBHelper dbHelper;
    SQLiteDatabase db;

    public ApDeviceDao(Context context,String name,int version){
        dbHelper = new DBHelper(context,name,null,version);
    }

    public void insert(APDeviceDBBean bean){
        db = dbHelper.getWritableDatabase();
        String sql = "insert into apcam (username,devicename,ip,port)values(?,?,?,?);";
        db.execSQL(sql,new Object[]{bean.getUserName(),bean.getDeviceName(),bean.getDeviceIP(),bean.getDevicePort()});
    }

    public List<APDeviceDBBean> queryByName(String userName){
        db = dbHelper.getWritableDatabase();
        List<APDeviceDBBean> data = new ArrayList<APDeviceDBBean>();
        String sql = "select * from apcam where username=?;";
        Cursor cursor = db.rawQuery(sql, new String[]{userName+""});
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String device = cursor.getString(cursor.getColumnIndex("devicename"));
            String ip = cursor.getString(cursor.getColumnIndex("ip"));
            int port = cursor.getInt(cursor.getColumnIndex("port"));
            APDeviceDBBean info = new APDeviceDBBean(name,device,ip,port);
            data.add(info);
        }
        return data;
    }

    public void updataByName(APDeviceDBBean bean,String userName,String deviceName){
        db = dbHelper.getWritableDatabase();
        String sql = "update apcam set username=?,devicename=?,ip=?,port=? where username=? AND devicename=?;";
        db.execSQL(sql, new Object[]{bean.getUserName(),bean.getDeviceName(),bean.getDeviceIP(),bean.getDevicePort(),userName,deviceName});
    }


    public List<APDeviceDBBean> queryByName(String userName,String deviceName){
        db = dbHelper.getWritableDatabase();
        List<APDeviceDBBean> data = new ArrayList<APDeviceDBBean>();
        String sql = "select * from apcam where username=? AND devicename=?;";
        Cursor cursor = db.rawQuery(sql,new String[]{userName+"",deviceName+""});
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String device = cursor.getString(cursor.getColumnIndex("devicename"));
            String ip = cursor.getString(cursor.getColumnIndex("ip"));
            int port = cursor.getInt(cursor.getColumnIndex("port"));
            APDeviceDBBean info = new APDeviceDBBean(name,device,ip,port);
            data.add(info);
        }
        return data;
    }

    public void deleteByName(String userName,String deviceName){
        db = dbHelper.getWritableDatabase();
        String sql = "delete from apcam where userName=? AND devicename=?;";
        db.execSQL(sql, new Object[]{userName+"",deviceName+""});
    }

    public void deleteByName(String userName){
        db = dbHelper.getWritableDatabase();
        String sql = "delete from apcam where userName=?;";
        db.execSQL(sql, new Object[]{userName+""});
    }


    public boolean findByName(String userName,String deviceName){
        db = dbHelper.getWritableDatabase();
        String sql = "select * from apcam where username=? AND devicename=?;";
        Cursor cursor = db.rawQuery(sql,new String[]{userName+"",deviceName+""});
        return cursor.moveToNext()?true:false;
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
