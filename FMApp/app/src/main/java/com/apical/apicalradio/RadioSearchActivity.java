package com.apical.apicalradio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fmapp.R;
import com.example.fmutil.FMUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadioSearchActivity extends BaseActivity {

    private ListView mListView;
    private TextView mRadioStatus;
    private List<RadioInfo> mList= new ArrayList<RadioInfo>();

    private static short MIN_CHANNEL = (short) 5310;//87.50
    private static short MAX_CHANNEL = (short) 16290;//108.00

    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_search_log);

        mListView= (ListView) findViewById(R.id.radio_list);
        mRadioStatus= (TextView) findViewById(R.id.status);
        mMyAdapter=new MyAdapter();
        mListView.setAdapter(mMyAdapter);

        mRadioStatus.setText("正在搜索...");

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=RadioSearchActivity.this.getIntent();
                intent.putExtra("setChannel",mList.get(i).frequency);
                RadioSearchActivity.this.setResult(1001,intent);
                RadioSearchActivity.this.finish();
            }
        });

        for (int i=0;i<=5;i++){
            addToList(8950+(i*100),5,"调试数据");
        }

        Log.v("bao","-----------------------------------\n\n\n");

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=MIN_CHANNEL;i<=MAX_CHANNEL;i++){
                    int freq=i;
                    FMUtil.seek(freq);
                    byte[] result=FMUtil.read();
                    Log.v("bao","FMUtil.read : "+ Arrays.toString(result));
                    if(result == null){
                        continue;
                    }
                    //INDEX for [level, usn, wam, offset, bandwidth]
                    final int LEVEL=0;
                    final int USN=1;
                    final int WAM=2;
                    final int OFFSET=3;
                    final int BANDWIDTH=4;

                    final int FMSM_TH = -1;
                    final int IFC = 1;


                    if(FMUtil.compareResult(freq,result)==1){//一般电台
                        addToList(freq,result[LEVEL],"一般电台");
                    }else if(FMUtil.compareResult(freq,result)==2){//99.1特殊电台
                        addToList(freq,result[LEVEL],"99.1特殊电台");
                    }else if(FMUtil.compareResult(freq,result)==3){//94.2特殊电台
                        addToList(freq,result[LEVEL],"94.2特殊电台");
                    }
                }
            }

        }).start();
    }

    private void addToList(int frequency,int level ,String tag){
        Log.v("bao","addToList frequency: "+frequency+" level: "+level+" tag: "+tag);
        for (int i=0;i<mList.size();i++){
            if(mList.get(i).frequency==frequency){
                return;
            }
        }
        RadioInfo info=new RadioInfo();
        info.frequency = frequency;
        info.level = level;
        info.tag = tag;
        mList.add(info);
        mHandler.sendEmptyMessage(1);
        mRadioStatus.setText("搜索列表");
    }


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mMyAdapter.notifyDataSetChanged();
        }
    };

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(view == null){
                holder = new ViewHolder();
                view =LayoutInflater.from(RadioSearchActivity.this).inflate(R.layout.radio_item,null);
                holder.radioName = (TextView) view.findViewById(R.id.radio_name);
                holder.radioFrequency = (TextView) view.findViewById(R.id.radio_frequency);
                holder.radioLevel = (TextView) view.findViewById(R.id.radio_level);
                holder.radioTag = (TextView) view.findViewById(R.id.radio_tag);
                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }
            holder.radioFrequency.setText("频道："+mList.get(i).frequency);
            holder.radioLevel.setText("信号强度："+mList.get(i).level);
            holder.radioTag.setText("备注："+mList.get(i).tag);
            return view;
        }
        class ViewHolder{
            TextView radioName,radioFrequency,radioLevel,radioTag;
        }
    }

    private class RadioInfo{
        private String name;
        private int frequency;
        private int level;
        private String tag;
    }
}
