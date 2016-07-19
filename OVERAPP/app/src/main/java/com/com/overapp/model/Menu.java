package com.com.overapp.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/7/12.
 */
public class Menu extends BmobObject {

    private String menuName;
    private String price;
    private String url;
    private String shopId;


    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
