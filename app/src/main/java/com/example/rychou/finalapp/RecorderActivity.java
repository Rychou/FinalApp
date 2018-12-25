package com.example.rychou.finalapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

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

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private Uri photoUri;
    public static final int REQUEST_CODE_camera = 2222;
//    public static final String EXTRA_COST_ID = "com.example.rychou.finalapp.cost_id";
//
//    public static Intent newIntent(Context packageContext,int costId){
//        Intent intent = new Intent(packageContext,RecorderActivity.class);
//        intent.putExtra(EXTRA_COST_ID, costId);
//        return intent;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recorder);
        Log.d("info","onCreate");

        mRadioGroup = (RadioGroup) findViewById(R.id.recorder_radioGroup);
        spinner = (Spinner) findViewById(R.id.record_spinner);
        waySpinner = (Spinner) findViewById(R.id.record_spinner_way);
        Confirm = (Button) findViewById(R.id.recorder_confirm);
        Cancel = (Button) findViewById(R.id.recorder_cancel);
        TextTime = (TextView) findViewById(R.id.record_textView_time);
        TextMoney = (EditText) findViewById(R.id.record_textView_money);
        TextComment = (EditText) findViewById(R.id.record_textView_comment);
        timer_chooser = (Button) findViewById(R.id.timer_chooser);

        mPhotoButton = (ImageButton) findViewById(R.id.cost_camera);
        mPhotoView = (ImageView) findViewById(R.id.cost_photo);
        //拍照按钮
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向MediaStore.Images.Media.EXTERNAL_CONTENT_URI插入一个数据，那么返回标识ID。
                //在完成拍照后，新的照片会以此处的photoUri命名.
                ContentValues values = new ContentValues();
                photoUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                //准备intent，并指定新照片的文件名（photoUri）
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                //启动拍照的窗体。并注册回调处理。
                startActivityForResult(intent, REQUEST_CODE_camera);
            }
        });


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
                        if(dayOfMonth < 10)
                        {
                            TextTime.setHint(year + "-" + (month+1) + "-0" + dayOfMonth);
                        }else {
                            TextTime.setHint(year + "-" + (month+1) + "-" + dayOfMonth);
                        }
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


//        //确定按钮
//        Confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCostBean = new CostBean();
//                mCostBean.setType(add_type);
//                mCostBean.setTime(TextTime.getHint().toString());
//                mCostBean.setFee(Double.parseDouble(TextMoney.getText().toString()));
//                mCostBean.setBudget(radioButton_selected);
//                mCostBean.setWay(way_type);
//                mCostBean.setComment(TextComment.getText().toString());
//                WriteData(mCostBean);
//                finish();
//                overridePendingTransition(R.animator.push_up_in,R.animator.push_up_out);
//                Log.i("info", "add_type" + add_type);
//                Log.i("info", "radioButton_selected" + radioButton_selected);
//
//            }
//        });
//
//        //取消按钮
//        Cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                overridePendingTransition(R.animator.push_up_in,R.animator.push_up_out);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Confirm = (Button) findViewById(R.id.recorder_confirm);
        Cancel = (Button) findViewById(R.id.recorder_cancel);
        
        Log.d("info","onActivityResult");
        if (requestCode == REQUEST_CODE_camera) {
            ContentResolver cr = getContentResolver();
            if (photoUri == null)
                return;
            //按刚刚指定的那个文件名，查询数据库，获得更多的 照片信息，比如 图片的物理绝对路径
            Cursor cursor = cr.query(photoUri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String path = cursor.getString(1);
                    //获得图片
                    final Bitmap bp = getBitMapFromPath(path);
                    mPhotoView.setImageBitmap(bp);

                    //写入到数据库
                    //确定按钮
                    Confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCostBean = new CostBean();
                            mCostBean.setType(add_type);
                            mCostBean.setTime(TextTime.getHint().toString());
                            mCostBean.setFee(Double.parseDouble(TextMoney.getText().toString()));
                            mCostBean.setBudget(radioButton_selected);
                            mCostBean.setWay(way_type);
                            mCostBean.setComment(TextComment.getText().toString());
                            WriteData(mCostBean,bp);
                            finish();
                            overridePendingTransition(R.animator.push_up_in,R.animator.push_up_out);
                            Log.i("info","img--->"+bp);
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
                cursor.close();
            }
            photoUri = null;
        }
    }

    public void WriteData(CostBean costBean,Bitmap bmp) {
        sqLiteOpenHelper = new MySQLiteOpenHelper(this);
        mDataBase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);

        values.clear();
        values.put("Type", costBean.getType());
        values.put("Time", costBean.getTime());
        values.put("Fee", costBean.getFee());
        values.put("Budget", costBean.getBudget());
        values.put("Way", costBean.getWay());
        values.put("Comment", costBean.getComment());
        values.put("img", os.toByteArray());

        mDataBase.insert("cost", "Type", values);
        mDataBase.close();
        sqLiteOpenHelper.close();

    }

    private Bitmap getBitMapFromPath(String imageFilePath) {
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        int dw = currentDisplay.getWidth();
        int dh = currentDisplay.getHeight();

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
                / (float) dh);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
                / (float) dw);

        if (heightRatio > 1 && widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }
        bmpFactoryOptions.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
        return bmp;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
