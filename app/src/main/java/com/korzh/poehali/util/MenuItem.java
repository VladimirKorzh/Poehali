package com.korzh.poehali.util;

/**
 * Created by vladimir on 7/4/2014.
 */
public class MenuItem {
    private int icon;
    private String title;

    public MenuItem(){

    }

    public MenuItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
