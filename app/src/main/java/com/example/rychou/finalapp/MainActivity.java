package com.example.rychou.finalapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.rychou.finalapp.DbSchema.CostTable;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private SQLiteDatabase mDatabase;
    private TextView mPay;
    private TextView mIncome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRecorder = new Intent(MainActivity.this,RecorderActivity.class);
                startActivity(intentRecorder);
            }
        });

        mPay = (TextView) findViewById(R.id.header_pay);
        mIncome = (TextView) findViewById(R.id.header_income);
        mDatabase = new MySQLiteOpenHelper(getApplicationContext()).getWritableDatabase();

        updateHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateHeader();
    }
    // 查询Cost表
    private Cursor queryCost(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CostTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    private void updateHeader(){
        int pay = 0;
        int income = 0;
        Cursor cursor = mDatabase.rawQuery("select * from "+CostTable.NAME,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String budget = cursor.getString(cursor.getColumnIndex(CostTable.Cols.BUDGET));
            String fee = cursor.getString(cursor.getColumnIndex(CostTable.Cols.FEE));
            if (budget.equals("支出")){
                pay += Integer.parseInt(fee);
            }else if(budget.equals("收入")){
                income += Integer.parseInt(fee);
            }
            Log.d("INITHEADER", "种类——>>>"+budget+" 金额-->>>"+fee);
            cursor.moveToNext();
        }
        Log.d("INITHEADER", "支出:"+pay+"收入"+income);
        cursor.close();

        Log.d(TAG, String.valueOf(mPay.getText())+ " " +mIncome.getText());
        mPay.setText(String.valueOf(pay));
        mIncome.setText(String.valueOf(income));
    }


}
