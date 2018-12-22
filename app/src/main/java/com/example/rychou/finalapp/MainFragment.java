package com.example.rychou.finalapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private static String TAG = "MainFragemnt";
    private SQLiteDatabase mDatabase;
    private TextView mPay;
    private TextView mIncome;
    private RecyclerView mRecyclerView;
    private CostAdapter mCostAdapter;

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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.cost_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }


    private void updateUI(){
        int pay = 0;
        int income = 0;
        List<CostBean> mCostBeans = new ArrayList<CostBean>();;

        Cursor cursor = mDatabase.rawQuery("select * from "+ DbSchema.CostTable.NAME,null);
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
                pay += Integer.parseInt(fee);
            }else if(budget.equals("收入")){
                income += Integer.parseInt(fee);
            }
            Log.d(TAG, "ID为——》》"+id);
            Log.d(TAG, "种类——>>>"+budget+" 金额-->>>"+fee);
            mCostBeans.add(new CostBean(id,type,way,Double.parseDouble(fee),time,budget,comment));
            cursor.moveToNext();
        }
        Log.d(TAG, "支出:"+pay+"收入"+income);

        mCostAdapter = new CostAdapter(mCostBeans);
        mRecyclerView.setAdapter(mCostAdapter);

        // 更新收入支出总额。
        mPay.setText(String.valueOf(pay));
        mIncome.setText(String.valueOf(income));
        cursor.close();
    }

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
