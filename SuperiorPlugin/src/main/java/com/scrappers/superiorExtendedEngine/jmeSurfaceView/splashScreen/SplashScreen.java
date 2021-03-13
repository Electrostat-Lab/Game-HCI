package com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen;

import android.util.DisplayMetrics;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.scrappers.superiorExtendedEngine.jmeSurfaceView.JmeSurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SplashScreen {
    private final ProgressBar progressBar;
    private final AppCompatActivity appCompatActivity;
    private final JmeSurfaceView jmESurfaceView;
    private final CardView splashScreen;
    public OnSplashScreenDisplayed onSplashScreenDisplayed;

    /**
     * Display custom splash screen of the user desire , during the renderer delay #{@link JmeSurfaceView#startRenderer(int)}.
     * @see  JmeSurfaceView#startRenderer(int)
     * @param appCompatActivity the androidx activity
     * @param jmESurfaceView the jmeSurfaceView instance to link with.
     */
    public SplashScreen(AppCompatActivity appCompatActivity, JmeSurfaceView jmESurfaceView){
        progressBar=new ProgressBar(appCompatActivity);
        splashScreen=new CardView(appCompatActivity);
        this.appCompatActivity=appCompatActivity;
        this.jmESurfaceView=jmESurfaceView;
    }

    /**
     * display a splash screen with an indeterminate infinite progress bar.
     * @see ProgressBar
     * @see SplashScreen#getProgressBar()
     * @see SplashScreen#getSplashScreen()
     */
    public void displayProgressedSplash(){
        DisplayMetrics displayMetrics=new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        splashScreen.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels*2,displayMetrics.heightPixels));
        progressBar.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels/4,displayMetrics.widthPixels/4));

        progressBar.setX((float) (displayMetrics.widthPixels/2 - progressBar.getLayoutParams().width/2));
        progressBar.setY((float) (displayMetrics.heightPixels/2 - progressBar.getLayoutParams().height/2));

        splashScreen.addView(progressBar);
        jmESurfaceView.addView(splashScreen);
        if(onSplashScreenDisplayed!=null){
            onSplashScreenDisplayed.onSplashed(this);
        }
    }

    /**
     * display the splash screen without a progress.
     * @see SplashScreen#getSplashScreen()
     */
    public void displaySplash(){
        DisplayMetrics displayMetrics=new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        splashScreen.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels*2,displayMetrics.heightPixels));
        jmESurfaceView.addView(splashScreen);
    }

    /**
     * hide the progress without hiding the splash screen
     * @return the splash screen instance for multiple calls.
     */
    public SplashScreen hideProgress(){
        splashScreen.removeView(progressBar);
        return this;
    }
    /**
     * dismiss the whole splash screen including the progress.
     */
    public void hideSplashScreen(){
        jmESurfaceView.removeView(splashScreen);
    }

    /**
     * get the progressBar view component of the splash screen.
     * @return an instance of the progress bar splash screen.
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * gets the current splash screen in a card view form
     * @return cardView representing this splash screen.
     */
    public CardView getSplashScreen() {
        return splashScreen;
    }
    public interface OnSplashScreenDisplayed{
        void onSplashed(SplashScreen splashScreen);
    }

    public void setOnSplashScreenDisplayed(OnSplashScreenDisplayed onSplashScreenDisplayed) {
        this.onSplashScreenDisplayed = onSplashScreenDisplayed;
    }
}