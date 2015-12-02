package com.silogood.s_permissions;


import android.graphics.drawable.Drawable;

public class Recycler_item {
    Drawable image;
    String title;
    String packagename;

    Drawable getImage(){
        return this.image;
    }
    String getTitle(){
        return this.title;
    }
    String getPackagename() {return this.packagename;}

    Recycler_item(Drawable image, String title , String packagename){
        this.image=image;
        this.title=title;
        this.packagename=packagename;
    }
}