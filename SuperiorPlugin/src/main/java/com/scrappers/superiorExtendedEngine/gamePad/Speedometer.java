package com.scrappers.superiorExtendedEngine.gamePad;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.control.VehicleControl;
import com.scrappers.GamePad.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class Speedometer extends CardView {
    public static final int METER_1=R.drawable.speedometer;
    public static final int CIRCULAR_PROGRESS=R.drawable.circular_progress;
    private static final int PROGRESS_RANGE=120;
    public static final int PROGRESS_MAX=100;
    private TextView digitalScreen;
    private ProgressBar speedCursor;

    public Speedometer(Context context) {
        super(context);
    }

    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Speedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initializeMeter(int progressStyle,int meterBackground){
        ((AppCompatActivity)getContext()).runOnUiThread(()->{
            /*doing the styles*/
            speedCursor =new ProgressBar(getContext(),null, R.style.Widget_AppCompat_ProgressBar_Horizontal);
            speedCursor.setProgressDrawable(ContextCompat.getDrawable(getContext(),progressStyle));
            speedCursor.setBackground(ContextCompat.getDrawable(getContext(),meterBackground));
            /*doing the data related part*/
            speedCursor.setIndeterminate(false);
            speedCursor.setMax(PROGRESS_RANGE);
            speedCursor.setProgress(0);

            digitalScreen=new TextView(getContext());
            digitalScreen.setText(String.valueOf(0));
            digitalScreen.setTextColor(ContextCompat.getColor(getContext(),R.color.greenDegree2));
            digitalScreen.setLayoutParams(new LayoutParams(this.getLayoutParams().width/4,this.getLayoutParams().height/4));
            digitalScreen.setTextSize((float) digitalScreen.getLayoutParams().width/5);
            digitalScreen.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            digitalScreen.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.digital_background));
            digitalScreen.setX((float)this.getLayoutParams().width/2- (float)digitalScreen.getLayoutParams().width/2);
            digitalScreen.setY((float)this.getLayoutParams().height/2-(float)digitalScreen.getLayoutParams().height/2);

            this.setElevation(10);
            this.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.nothing));
            this.addView(speedCursor);
            this.addView(digitalScreen);
        });
    }

    public ProgressBar getSpeedCursor() {
        return speedCursor;
    }

    public TextView getDigitalScreen() {
        return digitalScreen;
    }

    public static class InertialListener extends BaseAppState {
        private final Speedometer speedometer;
        private final AppCompatActivity appCompatActivity;
        private final VehicleControl vehicleControl;
        private float threshold =2;
        public InertialListener(Speedometer speedometer,AppCompatActivity appCompatActivity,VehicleControl vehicleControl){
            this.speedometer=speedometer;
            this.appCompatActivity=appCompatActivity;
            this.vehicleControl=vehicleControl;
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

        public void setThreshold(float threshold) {
            this.threshold = threshold;
        }

        public float getThreshold() {
            return threshold;
        }

        @Override
        public void update(float tpf) {
            appCompatActivity.runOnUiThread(()-> {
                if(Math.abs((int) vehicleControl.getLinearVelocity().getZ() * threshold) < Speedometer.PROGRESS_MAX){
                    speedometer.getSpeedCursor().setProgress(Math.abs((int) (vehicleControl.getLinearVelocity().getZ() * threshold)));
                    speedometer.getDigitalScreen().setText(String.valueOf(new int[]{(int) Math.abs(vehicleControl.getLinearVelocity().getZ() * threshold)}[0]));
                }
            });
        }
    }
}
