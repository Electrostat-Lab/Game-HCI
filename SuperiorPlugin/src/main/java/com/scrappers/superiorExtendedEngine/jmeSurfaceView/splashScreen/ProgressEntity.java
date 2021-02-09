package com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen;

import android.util.DisplayMetrics;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.scrappers.superiorExtendedEngine.jmeSurfaceView.JmESurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProgressEntity{
    private final ProgressBar progressBar;
    private final AppCompatActivity appCompatActivity;
    private final JmESurfaceView jmESurfaceView;
    private final CardView splashScreen;
    public ProgressEntity(AppCompatActivity appCompatActivity,JmESurfaceView jmESurfaceView){
        progressBar=new ProgressBar(appCompatActivity);
        splashScreen=new CardView(appCompatActivity);
        this.appCompatActivity=appCompatActivity;
        this.jmESurfaceView=jmESurfaceView;
    }
    public void displayProgress(){
        DisplayMetrics displayMetrics=new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        splashScreen.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels*2,displayMetrics.heightPixels));
        progressBar.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels/4,displayMetrics.widthPixels/4));

        progressBar.setX((float) (displayMetrics.widthPixels/2 - progressBar.getLayoutParams().width/2));
        progressBar.setY((float) (displayMetrics.heightPixels/2 - progressBar.getLayoutParams().height/2));

        splashScreen.addView(progressBar);
        jmESurfaceView.addView(splashScreen);
    }
    public void hideProgress(){
        jmESurfaceView.removeView(splashScreen);
    }
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public CardView getSplashScreen() {
        return splashScreen;
    }
}