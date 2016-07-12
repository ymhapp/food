package com.example.overapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Introduce extends AppCompatActivity {
    private Button btn_go;
    private double mLatitude;
    private double mLongtitude;

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
    }
}
