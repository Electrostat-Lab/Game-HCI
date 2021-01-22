package com.myGame;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SystemVisibilityUI {
    private final AppCompatActivity appCompatActivity;
    public SystemVisibilityUI(AppCompatActivity appCompatActivity){
        this.appCompatActivity=appCompatActivity;
    }
    public void setGameMode(){
        View decorView=appCompatActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
