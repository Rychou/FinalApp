package com.example.rychou.finalapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class RecorderActivity extends Activity {

    private RadioGroup mRadioGroup;
    private TextView TextTime;
    private EditText TextMoney;
    private EditText TextComment;
    private Spinner spinner;
    private Spinner waySpinner;
    private Button Confirm, Cancel;

    private MySQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase mDataBase;

    private Button timer_chooser;


    //保存类型数据
    private String add_type, radioButton_selected, way_type;

    //将这些数据保存一组data 用Data存入sqlite
    private ArrayList<String> Data = new ArrayList<String>();

    private CostBean mCostBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recorder);

        mRadioGroup = (RadioGroup) findViewById(R.id.recorder_radioGroup);
        spinner = (Spinner) findViewById(R.id.record_spinner);
        waySpinner = (Spinner) findViewById(R.id.record_spinner_way);
        Confirm = (Button) findViewById(R.id.recorder_confirm);
        Cancel = (Button) findViewById(R.id.recorder_cancel);
        TextTime = (TextView) findViewById(R.id.record_textView_time);
        TextMoney = (EditText) findViewById(R.id.record_textView_money);
        TextComment = (EditText) findViewById(R.id.record_textView_comment);
        timer_chooser = (Button) findViewById(R.id.timer_chooser);

        TextMoney.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//设置输入的钱为数字和小数
        //spinner设置属性 设置收入 和 支出的 spinner 利用arrays中的数据
        final ArrayAdapter<CharSequence> spinnerAdapterPay = ArrayAdapter.createFromResource(this,
                R.array.type1, android.R.layout.simple_spinner_item);
        spinnerAdapterPay.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        final ArrayAdapter<CharSequence> spinnerAdapterIncome = ArrayAdapter.createFromResource(this,
                R.array.type2, android.R.layout.simple_spinner_item);

        spinner.setAdapter(spinnerAdapterPay);//匹配不选情况下默认的pay
        //在radioButton中加入选 支出 还是 收入 的不同情况spinner
        radioButton_selected = "支出";
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();

                if (radioButtonId == R.id.radio_pay) {
                    spinner.setAdapter(spinnerAdapterPay);
                    spinnerAdapterPay.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                } else {
                    spinner.setAdapter(spinnerAdapterIncome);
                    spinnerAdapterIncome.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                }

                //根据ID获取RadioButton的选的是 收入 还是 支出
                RadioButton radioButton = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
                radioButton_selected = radioButton.getText().toString();
            }
        });

        //Spinner获取选中的内容 赋值给add_type
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                add_type = (String) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Calendar cnow = Calendar.getInstance();
        final int year = cnow.get(Calendar.YEAR);
        final int month = cnow.get(Calendar.MONTH)+1;
        final int day = cnow.get(Calendar.DAY_OF_MONTH);
        Log.i("time", "time" + year + month + day);
        TextTime.setHint(year + "-" + month + "-" + day);
        //时间选择的dialog
        timer_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(RecorderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        TextTime.setHint(year + "-" + (month+1) + "-" + dayOfMonth);
                    }
                },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //支付方式
        waySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                way_type = (String) waySpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //确定按钮
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Data.clear();
//                Data.add(add_type);
//                Data.add(TextTime.getHint().toString());
//                Data.add(TextMoney.getText().toString());
//                Data.add(radioButton_selected);
//                Data.add(way_type);
//                Data.add(TextComment.getText().toString());
//                WriteData(Data);
                mCostBean = new CostBean();
                mCostBean.setType(add_type);
                mCostBean.setTime(TextTime.getHint().toString());
                mCostBean.setFee(Double.parseDouble(TextMoney.getText().toString()));
                mCostBean.setBudget(radioButton_selected);
                mCostBean.setWay(way_type);
                mCostBean.setComment(TextComment.getText().toString());
                WriteData(mCostBean);
                finish();
                overridePendingTransition(R.animator.push_up_in,R.animator.push_up_out);
                Log.i("info", "add_type" + add_type);
                Log.i("info", "radioButton_selected" + radioButton_selected);
            }
        });

        //取消按钮
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.animator.push_up_in,R.animator.push_up_out);
            }
        });
    }


    public void WriteData(CostBean costBean) {
        sqLiteOpenHelper = new MySQLiteOpenHelper(this);
        mDataBase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
//        values.put("Type", Data.get(0));
//        values.put("Time", Data.get(1));
//        values.put("Fee", Data.get(2));
//        values.put("Budget", Data.get(3));
//        values.put("Way", Data.get(4));
//        values.put("Comment", Data.get(5));
        values.put("Type", costBean.getType());
        values.put("Time", costBean.getTime());
        values.put("Fee", costBean.getFee());
        values.put("Budget", costBean.getBudget());
        values.put("Way", costBean.getWay());
        values.put("Comment", costBean.getComment());

        mDataBase.insert("cost", "Type", values);
        mDataBase.close();
        sqLiteOpenHelper.close();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
