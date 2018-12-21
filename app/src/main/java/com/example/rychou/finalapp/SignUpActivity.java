package com.example.rychou.finalapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirm;
    private Button mSignUpButton;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText) findViewById(R.id.sign_up_username);
        mPassword = (EditText) findViewById(R.id.sign_up_password);
        mConfirm = (EditText) findViewById(R.id.sign_up_confirm);
        mDatabase = new MySQLiteOpenHelper(getApplicationContext()).getWritableDatabase();

        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SIGNUP", mPassword.getText()+" "+mConfirm.getText());
                if (!mPassword.getText().toString().equals(mConfirm.getText().toString())){
                    Toast.makeText(SignUpActivity.this,"两次输入的密码不一样，请重试！",Toast.LENGTH_SHORT).show();
                }else if("".equals(mUsername.getText().toString())&&"".equals(mPassword.getText().toString())&&"".equals(mConfirm.getText().toString())){
                    Toast.makeText(SignUpActivity.this,"用户名和密码不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    mDatabase.insert(DbSchema.UserTable.NAME, null, addUser());
                    Toast.makeText(SignUpActivity.this,"注册成功！请登录吧！",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    public ContentValues addUser(){
        ContentValues values = new ContentValues();

        values.put(DbSchema.UserTable.Cols.USERNAME, mUsername.getText().toString());
        values.put(DbSchema.UserTable.Cols.PASSWORD, mPassword.getText().toString());
        return values;
    }
}
