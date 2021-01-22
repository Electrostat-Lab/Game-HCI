package com.scrappers.jmeGamePad;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import com.scrappers.GamePad.R;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class Speedometer extends CardView {
    private final Context context;
    public static final int METER_1=R.drawable.speedometer;
    public static final int CIRCULAR_PROGRESS=R.drawable.circular_progress;
    private static final int PROGRESS_RANGE=120;
    public static final int PROGRESS_MAX=100;
    private TextView digitalScreen;
    private ProgressBar speedCursor;

    public Speedometer(Context context) {
        super(context);
        this.context=context;
    }

    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public Speedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    public void initializeMeter(int progressStyle,int meterBackground){
        /*doing the styles*/
        speedCursor =new ProgressBar(context,null, R.style.Widget_AppCompat_ProgressBar_Horizontal);
        speedCursor.setProgressDrawable(ContextCompat.getDrawable(context,progressStyle));
        speedCursor.setBackground(ContextCompat.getDrawable(context,meterBackground));
        /*doing the data related part*/
        speedCursor.setIndeterminate(false);
        speedCursor.setMax(PROGRESS_RANGE);
        speedCursor.setProgress(0);

        digitalScreen=new TextView(context);
        digitalScreen.setText(String.valueOf(0));
        digitalScreen.setTextColor(ContextCompat.getColor(context,R.color.greenDegree2));
        digitalScreen.setLayoutParams(new LayoutParams(this.getLayoutParams().width/4,this.getLayoutParams().height/4));
        digitalScreen.setTextSize((float) digitalScreen.getLayoutParams().width/5);
        digitalScreen.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        digitalScreen.setBackground(ContextCompat.getDrawable(context,R.drawable.digital_background));
        digitalScreen.setX((float)this.getLayoutParams().width/2- (float)digitalScreen.getLayoutParams().width/2);
        digitalScreen.setY((float)this.getLayoutParams().height/2-(float)digitalScreen.getLayoutParams().height/2);

        this.setElevation(10);
        this.setBackground(ContextCompat.getDrawable(context,R.drawable.nothing));
        this.addView(speedCursor);
        this.addView(digitalScreen);
    }

    public ProgressBar getSpeedCursor() {
        return speedCursor;
    }

    public TextView getDigitalScreen() {
        return digitalScreen;
    }

    public static class InertialListener extends BaseAppState {
        private final Speedometer speedometer;
        private static final float inertialMaxTime=0.3f;
        private float inertialTime=0f;
        public InertialListener(Speedometer speedometer){
            this.speedometer=speedometer;
        }
        @Override
        protected void initialize(Application app) {

        }

        @Override
        protected void cleanup(Application app) {
            speedometer.removeAllViews();
        }

        @Override
        protected void onEnable() {

        }

        @Override
        protected void onDisable() {

        }

        @Override
        public void update(float tpf) {
            inertialTime+=tpf;
            if(inertialTime>=inertialMaxTime){
                if(speedometer==null){
                    return;
                }
                if(speedometer.getSpeedCursor().getProgress() > 0){
                    speedometer.getSpeedCursor().setProgress(speedometer.getSpeedCursor().getProgress() - 5);
                    speedometer.getDigitalScreen().setText(String.valueOf(
                            Math.max((Integer.parseInt(speedometer.getDigitalScreen().getText().toString()) - 5), 0)));
                }
                inertialTime=0f;
            }
        }
    }
}
