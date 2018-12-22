package com.example.rychou.finalapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainFragment extends Fragment {
    private static String TAG = "MainFragemnt";
    private SQLiteDatabase mDatabase;
    private TextView mPay;
    private TextView mIncome;
    private RecyclerView mTimeGroupRcv;
    private TimeGroupAdapter mTimeGroupAdapter;
    private int mTotalPay;
    private int mTotalIncome;
    private LinearLayout mDatePicker;
    private TextView mMonth;
    private TextView mYear;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_main, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRecorder = new Intent(getActivity(),RecorderActivity.class);
                startActivity(intentRecorder);
            }
        });

        mPay = (TextView) view.findViewById(R.id.header_pay);
        mIncome = (TextView) view.findViewById(R.id.header_income);
        mDatabase = new MySQLiteOpenHelper(getContext()).getWritableDatabase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mTimeGroupRcv = (RecyclerView) view.findViewById(R.id.time_group_recycler_view);
        mTimeGroupRcv.setLayoutManager(linearLayoutManager);

        mMonth = (TextView) view.findViewById(R.id.header_month);
        mYear = (TextView) view.findViewById(R.id.header_year);
        Calendar calendar = Calendar.getInstance();
        mMonth.setText(String.valueOf(calendar.get(Calendar.MONTH+1)));
        mYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        // 首页头部的日期选择
        mDatePicker= (LinearLayout)view.findViewById(R.id.date_selete);
        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cd = Calendar.getInstance();
                MyDatePickerDialog myDatePickerDialog = new MyDatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new MyDatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                                int month = monthOfYear + 1;
                                mMonth.setText(String.valueOf(month));
                                mYear.setText(String.valueOf(year));

                            }
                        },cd.get(Calendar.YEAR),cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH));
                myDatePickerDialog.myShow();
                // 将对话框的大小按屏幕大小的百分比设置
                WindowManager windowManager = getActivity().getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = myDatePickerDialog.getWindow().getAttributes();
                lp.width = (int)(display.getWidth() * 0.8); //设置宽度
                myDatePickerDialog.getWindow().setAttributes(lp);
                ((ViewGroup)((ViewGroup) myDatePickerDialog.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            }
        });
        updateUI();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }


    private void updateUI(){

        List<String> mTimeGroup = new ArrayList();
        Cursor timeCursor = mDatabase.rawQuery("select time from cost GROUP BY time ORDER BY time DESC", null);
        timeCursor.moveToFirst();

        while (!timeCursor.isAfterLast()){
            String time = timeCursor.getString(0);
            mTimeGroup.add(time);
            Log.d(TAG, time);
            timeCursor.moveToNext();
        }

        mTimeGroupAdapter = new TimeGroupAdapter(mTimeGroup);
        mTimeGroupRcv.setAdapter(mTimeGroupAdapter);
        timeCursor.close();

    }

    public CostAdapter renderCostList(String currentTime){
        List<CostBean> mCostBeans = new ArrayList<CostBean>();;
        Cursor cursor = mDatabase.rawQuery("select * from "+ DbSchema.CostTable.NAME + "  where time=? ",new String[]{ currentTime});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            int id = cursor.getInt(cursor.getColumnIndex(DbSchema.CostTable.Cols.ID));
            String type = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.TYPE));
            String way = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.WAY));
            String fee = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.FEE));
            String budget = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.BUDGET));
            String time = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.TIME));
            String comment = cursor.getString(cursor.getColumnIndex(DbSchema.CostTable.Cols.COMMENT));

            if (budget.equals("支出")){
                mTotalPay += Integer.parseInt(fee);
            }else if(budget.equals("收入")){
                mTotalIncome += Integer.parseInt(fee);
            }
            Log.d(TAG, "ID为——》》"+id);
            Log.d(TAG, "种类——>>>"+budget+" 金额-->>>"+fee);
            mCostBeans.add(new CostBean(id,type,way,Double.parseDouble(fee),time,budget,comment));
            cursor.moveToNext();
        }

        // 更新收入支出总额。
        mPay.setText(String.valueOf(mTotalPay));
        mIncome.setText(String.valueOf(mTotalIncome));
        cursor.close();
        return new CostAdapter(mCostBeans);
    }

    // 时间分组 嵌套列表第一层
    private class TimeGroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTimeGroup;
        private RecyclerView mCostListRcv;
        private CostAdapter mCostAdapter;

        public TimeGroupHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.time_item, parent, false));
            itemView.setOnClickListener(this);
            mTimeGroup = (TextView) itemView.findViewById(R.id.time_group);
        }

        public void bind(String time){
            mTimeGroup.setText(time);
            mCostListRcv = (RecyclerView) itemView.findViewById(R.id.cost_recycler_view);
            mCostListRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCostAdapter = renderCostList(time);

            mCostListRcv.setAdapter(mCostAdapter);
        }

        @Override
        public void onClick(View view) {

        }
    }

    private class TimeGroupAdapter extends RecyclerView.Adapter<TimeGroupHolder>{
        private List<String> mTimeGroup;
        public TimeGroupAdapter(List<String> timeGroup){
            mTimeGroup = timeGroup;
            mTotalPay=0;
            mTotalIncome=0;
        }

        @NonNull
        @Override
        public TimeGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new TimeGroupHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TimeGroupHolder holder, int position) {
            String time = mTimeGroup.get(position);
            holder.bind(time);
        }

        @Override
        public int getItemCount() {
            return mTimeGroup.size();
        }
    }

    // 记账列表 嵌套列表第二层
    private class CostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mCostType;
        private TextView mCostWay;
        private TextView mCostFee;
        private CostBean mCostBean;

        public CostHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.cost_item,parent,false));
            itemView.setOnClickListener(this);
            mCostType = (TextView) itemView.findViewById(R.id.cost_type);
            mCostWay = (TextView) itemView.findViewById(R.id.cost_way);
            mCostFee = (TextView) itemView.findViewById(R.id.cost_fee);
        }
        public void bind(CostBean costBean){
            mCostBean = costBean;
            if(mCostBean.getBudget().equals("支出")){
                mCostFee.setText("-"+String.valueOf(mCostBean.getFee()));
                mCostFee.setTextColor(Color.GREEN);
            }else {
                mCostFee.setText("+"+String.valueOf(mCostBean.getFee()));
                mCostFee.setTextColor(Color.RED);
            }
            mCostType.setText(mCostBean.getType());
            mCostWay.setText(mCostBean.getWay());
        }

        @Override
        public void onClick(View view){
            // 这里点击列表项，查看详情
//            Intent intent = RecorderActivity.newIntent(getActivity(),mCostBean.getId());
//            startActivity(intent);
                Intent intent = new Intent(getActivity().getApplicationContext(),UpdateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id",mCostBean.getId());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
        }
    }

    private class CostAdapter extends RecyclerView.Adapter<CostHolder>{
        private List<CostBean> mCostBeans;
        public CostAdapter(List<CostBean> costBeans){
            mCostBeans = costBeans;
        }

        @Override
        public CostHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CostHolder(layoutInflater, parent);
        }
        @Override
        public void onBindViewHolder(CostHolder holder, int position) {
            CostBean costBean= mCostBeans.get(position);
            holder.bind(costBean);
        }
        @Override
        public int getItemCount() {
            return mCostBeans.size();
        }
    }
}
