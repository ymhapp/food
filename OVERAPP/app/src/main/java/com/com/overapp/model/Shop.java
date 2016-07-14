package com.com.overapp.model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/7/12.
 */
public class Shop  extends BmobObject{

    private String shopName;
    private Double latitude;
    private Double longitude;
    private List<Menu> shopMenu=new ArrayList<Menu>();



    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<Menu> getShopMenu() {
        return shopMenu;
    }

    public void setShopMenu(List<Menu> shopMenu) {
        this.shopMenu = shopMenu;
    }
}
