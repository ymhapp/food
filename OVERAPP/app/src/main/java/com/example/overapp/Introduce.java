package com.example.overapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Introduce extends AppCompatActivity {
    private Button btn_go;
    private double mLatitude;
    private double mLongtitude;
    private ListView listView;
    private SimpleAdapter simple_adapter;
    private List<Map<String, Object>> dataList;
    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        // 新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        // 接收经纬度
        double Latitude = bundle.getDouble("Latitude");
        double Longtitude = bundle.getDouble("Longtitude");

        String strLatitude = Double.toString(Latitude);
        String strLongtitude = Double.toString(Longtitude);

        Log.i("获取的纬度是", strLatitude);
        Log.i("获取的经度是", strLongtitude);


        btn_go = (Button) findViewById(R.id.shop_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击button跳转到导航页面
                Intent intent = new Intent();
                intent.setClass(Introduce.this,
                        Routeplan.class);
                // 用Bundle携带数据
                Bundle bundle = new Bundle();
                // 将参数mLatitude传递给Latitude
                bundle.putDouble("Latitude", mLatitude);
                bundle.putDouble("Longtitude", mLongtitude);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //
        listView = (ListView) findViewById(R.id.listView);
        dataList = new ArrayList<Map<String, Object>>();
        simple_adapter = new SimpleAdapter(Introduce.this, getData(), R.layout.list_item,
                new String[]{"pic", "text"}, new int[]{R.id.pic, R.id.text});

        listView.setAdapter(simple_adapter);
    }
    public List<Map<String, Object>> getData() {
        for(int i=0;i<20;i++){
            Map<String,Object>map=new HashMap<String,Object>();
            map.put("pic",R.drawable.ic_launcher);
            map.put("text","鸡腿套餐"+i);
            dataList.add(map);


        }
        return dataList;
    }

}
