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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import static android.content.ContentValues.TAG;

public class UpdateActivity extends Activity {

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

    private SQLiteDatabase db;


    //保存类型数据
    private String add_type, radioButton_selected, way_type;

    //将这些数据保存一组data 用Data存入sqlite
    private ArrayList<String> Data = new ArrayList<String>();

    private CostBean mCostBean;
    private RadioButton PayRadio;
    private RadioButton IncomeRadio;

    private CostBean sqliteCostBean;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private Uri photoUri;
    public static final int REQUEST_CODE_camera = 2222;

    private int mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recorder);

        Intent intent  = getIntent();
        mId = intent.getIntExtra("id",0);
        Log.d(TAG, "ID---->>" + mId);

        mRadioGroup = (RadioGroup) findViewById(R.id.recorder_radioGroup);
        PayRadio  = (RadioButton) mRadioGroup.findViewById(R.id.radio_pay);
        IncomeRadio = (RadioButton)mRadioGroup.findViewById(R.id.radio_income);
        spinner = (Spinner) findViewById(R.id.record_spinner);
        waySpinner = (Spinner) findViewById(R.id.record_spinner_way);
        Confirm = (Button) findViewById(R.id.recorder_confirm);
        Cancel = (Button) findViewById(R.id.recorder_cancel);
        TextTime = (TextView) findViewById(R.id.record_textView_time);
        TextMoney = (EditText) findViewById(R.id.record_textView_money);
        TextComment = (EditText) findViewById(R.id.record_textView_comment);
        timer_chooser = (Button) findViewById(R.id.timer_chooser);

        db = new MySQLiteOpenHelper(this).getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from cost where _id=?", new String[]{String.valueOf(mId)});  //创建一个游标
        if(cursor.moveToNext()){  //循环遍历查找数组
            int costid = cursor.getInt(cursor.getColumnIndex(DbSchema.CostTable.Cols.ID));
            String type = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.TYPE));
            String way = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.WAY));
            String fee = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.FEE));
            String budget = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.BUDGET));
            String time = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.TIME));
            String comment = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.COMMENT));
//            byte[] b = cursor.getBlob(cursor.getColumnIndex("img"));
//
//            Log.d("info","img---->"+b);

            sqliteCostBean = new CostBean(costid,type,way,Double.parseDouble(fee),time,budget,comment);
        }

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

        byte[] b = cursor.getBlob(cursor.getColumnIndex("img"));
        if (b == null){
            Log.d("info","没有图片");
        }else{
            List<Bitmap> bpArr = ReadImg(mId);
            ViewGroup gp = (ViewGroup) findViewById(R.id.div);
            gp.removeAllViews();
            for (int i = 0; i < bpArr.size(); i++) {
                ImageView iv = new ImageView(UpdateActivity.this);
                Bitmap bp = bpArr.get(i);
                Log.d("bp------>", String.valueOf(bp));
                Log.d("i----->", String.valueOf(i));
                if (bp != null) {
                    iv.setImageBitmap(bp);
                } else {
                    iv.setImageBitmap(null);
                }
                gp.addView(iv);
            }
        }
        cursor.close();


        TextMoney.setText(Double.toString(sqliteCostBean.getFee()));
        TextMoney.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//设置输入的钱为数字和小数

        //spinner设置属性 设置收入 和 支出的 spinner 利用arrays中的数据
        final ArrayAdapter<CharSequence> spinnerAdapterPay = ArrayAdapter.createFromResource(this,
                R.array.type1, android.R.layout.simple_spinner_item);
        spinnerAdapterPay.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        final ArrayAdapter<CharSequence> spinnerAdapterIncome = ArrayAdapter.createFromResource(this,
                R.array.type2, android.R.layout.simple_spinner_item);

        if (sqliteCostBean.getBudget().equals("支出")){
            PayRadio.setChecked(true);
        }else {
            IncomeRadio.setChecked(true);
        }
//        int pos = spinnerAdapterPay.getPosition(sqliteCostBean.getType());
//        spinner.setSelection(pos);


        if (sqliteCostBean.getBudget().equals("支出")){
            PayRadio.setChecked(true);
            spinner.setAdapter(spinnerAdapterPay);
            int pos = spinnerAdapterPay.getPosition(sqliteCostBean.getType());
            spinner.setSelection(pos,true);
            add_type = (String) spinner.getSelectedItem();
        }else {
            IncomeRadio.setChecked(true);
            spinner.setAdapter(spinnerAdapterIncome);
            int pos = spinnerAdapterIncome.getPosition(sqliteCostBean.getType());
            spinner.setSelection(pos,true);
            add_type = (String) spinner.getSelectedItem();
        }
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
        TextTime.setHint(sqliteCostBean.getTime());
        //时间选择的dialog
        timer_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(UpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (dayOfMonth < 10) {
                            TextTime.setHint(year + "-" + (month + 1) + "-0" + dayOfMonth);
                        } else {
                            TextTime.setHint(year + "-" + (month + 1) + "-" + dayOfMonth);
                        }
                    }
                },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //支付方式
        final ArrayAdapter<CharSequence> spinnerAdapterWay = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        spinnerAdapterWay.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        int pos = spinnerAdapterWay.getPosition(sqliteCostBean.getWay());
        waySpinner.setSelection(pos,true);
        way_type = (String) waySpinner.getSelectedItem();

        waySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                way_type = (String) waySpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //文字备注
        TextComment.setText(sqliteCostBean.getComment());

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

                UpdateData(mCostBean,mId);
                mDataBase.close();
                sqLiteOpenHelper.close();
                finish();
                overridePendingTransition(R.animator.push_up_in,R.animator.push_up_out);
                Log.i("info","id---->" + mId);
                Log.i("info", "add_type" + add_type);
                Log.i("info","way_type--->" +way_type);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null){
            Log.d("info","data is null");
        }

//       final int id = data.getIntExtra("id",0);
        Log.d("获得照片 id----->", String.valueOf(mId));

        sqLiteOpenHelper = new MySQLiteOpenHelper(this);
        mDataBase = sqLiteOpenHelper.getWritableDatabase();

        Confirm = (Button) findViewById(R.id.recorder_confirm);
        Cancel = (Button) findViewById(R.id.recorder_cancel);

        Log.d("info","onActivityResult");
        if (requestCode == REQUEST_CODE_camera) {
            ContentResolver cr = getContentResolver();
            Log.d("cr------->", String.valueOf(cr));
            Log.d("uri------->", String.valueOf(photoUri));
            if (photoUri == null)
                return;
            //按刚刚指定的那个文件名，查询数据库，获得更多的 照片信息，比如 图片的物理绝对路径
            Cursor cursor = cr.query(photoUri, null, null, null, null);
            Log.d("cur------->", String.valueOf(cursor));
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String path = cursor.getString(1);
                    //获得图片
                    final Bitmap bp = getBitMapFromPath(path);
                    Log.d("获得图片------->", String.valueOf(bp));
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
                            UpdateData(mCostBean,mId);

                            ContentValues values = new ContentValues();
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            bp.compress(Bitmap.CompressFormat.PNG, 100, os);

                            values.put("img", os.toByteArray());
                            Log.d("img---------->", String.valueOf(os.toByteArray()));
                            mDataBase.update("cost", values, "_id=?", new String[] { String.valueOf(mId) });

                            mDataBase.close();
                            sqLiteOpenHelper.close();

                            finish();
                            overridePendingTransition(R.animator.push_up_in,R.animator.push_up_out);
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


    public void UpdateData(CostBean costBean,int id) {
        sqLiteOpenHelper = new MySQLiteOpenHelper(this);
        mDataBase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.clear();
        values.put("Type", costBean.getType());
        values.put("Time", costBean.getTime());
        values.put("Fee", costBean.getFee());
        values.put("Budget", costBean.getBudget());
        values.put("Way", costBean.getWay());
        values.put("Comment", costBean.getComment());

        mDataBase.update("cost", values, "_id=?", new String[] { String.valueOf(id) });


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

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    //读取
    public List<Bitmap> ReadImg(int mid) {
        db = new MySQLiteOpenHelper(this).getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from cost where _id=?", new String[]{String.valueOf(mid)});
        List<Bitmap> lst = new ArrayList<Bitmap>();
        Log.d("lst-------->", String.valueOf(lst));
        while (cursor.moveToNext()) {
            byte[] in = cursor.getBlob(cursor.getColumnIndex("img"));
            Log.d("in byte-------->", String.valueOf(in));
            lst.add(BitmapFactory.decodeByteArray(in, 0, in.length));
        }
        return lst;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

