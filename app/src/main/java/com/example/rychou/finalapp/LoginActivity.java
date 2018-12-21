package com.example.rychou.finalapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rychou.finalapp.DbSchema.UserTable;

public class LoginActivity extends AppCompatActivity {
    private EditText mUserName;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mSignUpButton;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mContext = getApplicationContext();
        mDatabase = new MySQLiteOpenHelper(mContext).getWritableDatabase();

        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);

        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("".equals(mUserName.getText().toString())&&"".equals(mPassword.getText().toString())){
                    Toast.makeText(LoginActivity.this,"用户名和密码不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    Cursor cursor = queryUser("username=?", new String[]{mUserName.getText().toString()});
                    Log.d("LOGIN", String.valueOf(cursor.getCount()));
                    if(cursor.getCount()>0){
                        cursor.moveToFirst();
                        String password = cursor.getString(cursor.getColumnIndex(UserTable.Cols.PASSWORD));
                        if(password.equals(mPassword.getText().toString())){
                            Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(LoginActivity.this,"密码错误，请重试！",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(LoginActivity.this,"不存在该用户，请先注册！",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mSignUpButton = (Button) findViewById(R.id.link_to_sign_up);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public ContentValues initUsers(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserTable.Cols.USERNAME, "rychou");
        contentValues.put(UserTable.Cols.PASSWORD, "123456");
        return contentValues;
    }

    // 查询用户表
    private Cursor queryUser(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                UserTable.NAME,
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
