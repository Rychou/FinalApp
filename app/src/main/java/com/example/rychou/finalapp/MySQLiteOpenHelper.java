package com.example.rychou.finalapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rychou.finalapp.DbSchema.CostTable;
import com.example.rychou.finalapp.DbSchema.UserTable;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private final String TABLE_NAME_COST = "cost";
    private static final String DATABASE_NAME = "finance.db";
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

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    //首次创建时调用 一般用于建库 建表
    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_COST);
        db.execSQL("create table " + CostTable.NAME+"("+
                " _id integer primary key autoincrement, " +
                CostTable.Cols.TYPE+", "+
                CostTable.Cols.TIME+", "+
                CostTable.Cols.FEE+", "+
                CostTable.Cols.BUDGE+","+
                CostTable.Cols.WAY+", "+
                CostTable.Cols.COMMENT+
                ")"
        );
        db.execSQL("create table " + UserTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            UserTable.Cols.USERNAME + ", " +
            UserTable.Cols.PASSWORD +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
