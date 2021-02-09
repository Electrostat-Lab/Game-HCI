package com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.scrappers.GamePad.R;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.JmESurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class ProgressEntity{
    private ProgressBar progressBar;
    private final AppCompatActivity appCompatActivity;
    private final JmESurfaceView jmESurfaceView;
    private CardView splashScreen;
    public ProgressEntity(AppCompatActivity appCompatActivity,JmESurfaceView jmESurfaceView){
        this.appCompatActivity=appCompatActivity;
        this.jmESurfaceView=jmESurfaceView;
    }
    public void displayProgress(){
        progressBar=new ProgressBar(appCompatActivity);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        progressBar.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels/4,displayMetrics.widthPixels/4));

        progressBar.setX((float) (displayMetrics.widthPixels/2 - progressBar.getLayoutParams().width/2));
        progressBar.setY((float)(displayMetrics.heightPixels/2 - progressBar.getLayoutParams().height/2));
        splashScreen=new CardView(appCompatActivity);
        splashScreen.addView(progressBar);
        splashScreen.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        splashScreen.setBackground(ContextCompat.getDrawable(appCompatActivity, R.color.pureWhite));
        jmESurfaceView.addView(splashScreen);
    }
    public void hideProgress(){
        jmESurfaceView.removeView(splashScreen);
    }
    public ProgressBar getProgressBar() {
        return progressBar;
    }
}