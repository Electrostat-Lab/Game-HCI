package com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen;

import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.scrappers.superiorExtendedEngine.jmeSurfaceView.JmESurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ImageEntity {
    private ImageView splashImage;
    private final AppCompatActivity appCompatActivity;
    private final JmESurfaceView jmESurfaceView;

    public ImageEntity(AppCompatActivity appCompatActivity,JmESurfaceView jmESurfaceView) {
        this.appCompatActivity = appCompatActivity;
        this.jmESurfaceView = jmESurfaceView;
    }

    public void displayImageSplash(int image){
        splashImage =new ImageView(appCompatActivity);
        splashImage.setImageDrawable(ContextCompat.getDrawable(appCompatActivity,image));
        DisplayMetrics displayMetrics=new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        splashImage.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels,displayMetrics.heightPixels));
        jmESurfaceView.addView(splashImage);
    }

    public ImageView getSplashImage() {
        return splashImage;
    }
    public void hideSplash(){
        jmESurfaceView.removeView(splashImage);
    }
}
