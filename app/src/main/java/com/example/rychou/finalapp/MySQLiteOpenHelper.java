package com.example.rychou.finalapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    //表名
    private final String TABLE_NAME_COST = "cost";

    //cost字段
    private final String COST_ID = "_id";
    private final String COST_TYPE = "Type";//名称（如用餐、服饰）
    private final String COST_TIME = "Time";
    private final String COST_FEE = "Fee";//金额
    private final String COST_WAY = "Way";//支付方式
    private final String COST_BUDGE = "Budget";//支出还是收入
    private final String COST_COMMENT = "Comment";//文字备注

    //创建cost表
    private final String CREATE_COST = "create table " + TABLE_NAME_COST + "(" +
            COST_ID + " integer primary key autoincrement," +
            COST_TYPE + " varchar(10) ," +
            COST_TIME + " varchar(20)," +
            COST_FEE + " double," +
            COST_BUDGE + " varchar(10),"+
            COST_WAY + " varchar(20),"+
            COST_COMMENT + " varchar(100)" +
            ")";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 1);
    }


    //首次创建时调用 一般用于建库 建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
