package com.example.rychou.finalapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rychou.finalapp.DbSchema.CostTable;
import com.example.rychou.finalapp.DbSchema.UserTable;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "finance.db";

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    //首次创建时调用 一般用于建库 建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建cost表
        db.execSQL("create table " + CostTable.NAME+"("+
                " _id integer primary key autoincrement, " +
                CostTable.Cols.TYPE+", "+
                CostTable.Cols.TIME+", "+
                CostTable.Cols.FEE+", "+
                CostTable.Cols.BUDGET+","+
                CostTable.Cols.WAY+", "+
                CostTable.Cols.COMMENT+", "+
                CostTable.Cols.IMG_DATA + " blob"+
                ")"
        );
        // 创建users表
        db.execSQL("create table " + UserTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            UserTable.Cols.USERNAME + " unique" + ", " +
            UserTable.Cols.PASSWORD + ", " +
                UserTable.Cols.SALT +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
