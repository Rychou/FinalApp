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

import com.example.rychou.finalapp.DbSchema.CostTable;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
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

        // 遍历cost表
        mDatabase = new MySQLiteOpenHelper(getApplicationContext()).getWritableDatabase();
        Cursor cursor = queryCost(null,null);
        cursor.moveToFirst();
        Log.d("COST_TABLE", String.valueOf(cursor.getCount()));
        while (!cursor.isAfterLast()){
            String time = cursor.getString(cursor.getColumnIndex(CostTable.Cols.TIME));
            Log.d("COST_TABLE", time);
            cursor.moveToNext();
        }
        cursor.close();
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

    public Cursor queryCost(String whereClause, String[] whereArgs) {
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
}
