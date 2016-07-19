package com.example.overapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.com.overapp.model.Menu;
import com.com.overapp.model.Shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class Introduce extends AppCompatActivity {
    private String strmenuName;
    private String menuname = "鸡腿套餐22";
    private List<String> menuName = new ArrayList<String>();
    private String shopad;
    private String price;
    private String shopid;
    private String shopname;
    private TextView tad;
    private TextView t1;
    private Button btn_go;
    private ListView listView;
    private SimpleAdapter simple_adapter;
    private List<Map<String, Object>> dataList;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        //
        listView = (ListView) findViewById(R.id.listView);
        TextView t1 = (TextView) findViewById(R.id.shop_name);
        TextView tad = (TextView) findViewById(R.id.shopaddress);
        // 新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        // 接收店铺ID
        shopid = bundle.getString("shopid");
        shopad = bundle.getString("shopad");
        shopname = bundle.getString("shopname");
        //根据接收的店铺ID搜索menu表
        BmobQuery query = new BmobQuery("Shop");
        query.getObjectByTable(shopid, new QueryListener<Shop>() {
            @Override
            public void done(Shop object, BmobException e) {
                if (e == null) {
                    shopname = object.getShopName();
                } else {
                }
            }
        });

        t1.setText(shopname);
        tad.setText(shopad);

        //Bmob查询menu
        final BmobQuery<Menu> menu = new BmobQuery<Menu>();
        //查询playerName叫“比目”的数据
        menu.addWhereEqualTo("shopid", shopid);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        menu.setLimit(10);
        //执行查询方法
        menu.findObjects(new FindListener<Menu>() {
            @Override
            public void done(List<Menu> list, BmobException e) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(Introduce.this);
                builder1.setTitle("query address");
                if (e == null) {
                    for (Menu menu : list) {
                        menuName.add(menu.getMenuName());
                        strmenuName=menuName.toString();
                        // menuname=menu.getMenuName();
                    }
                }
                builder1.setMessage(menuName.toString());
                builder1.create().show();

            }
        });
        dataList = new ArrayList<Map<String, Object>>();
        simple_adapter = new SimpleAdapter(Introduce.this, getData(), R.layout.list_item,
                new String[]{"pic", "text"}, new int[]{R.id.pic, R.id.text});
        listView.setAdapter(simple_adapter);


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
//                bundle.putDouble("Latitude", mLatitude);
//                bundle.putDouble("Longtitude", mLongtitude);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public List<Map<String, Object>> getData() {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < 6; i++) {
            map.put("pic", R.drawable.ic_launcher);
            map.put("text",menuName.toString());

            dataList.add(map);
        }

        return dataList;
    }

}
