package com.scrappers.superiorExtendedEngine.misc;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.VehicleControl;
import com.scrappers.GamePad.R;
import com.scrappers.superiorExtendedEngine.gamePad.ControlButtonsView;
import com.scrappers.superiorExtendedEngine.gamePad.GameStickView;
import com.scrappers.superiorExtendedEngine.gamePad.Speedometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * A subclass of the DrivingWheel , that is used to create a GullWing wheel for jets.
 * @author pavl_g
 */
public class GullWing extends DrivingWheelView{
    protected Speedometer speedometer;
    private UTurnView uTurnView;
    public GullWing(@NonNull Context appCompatActivity) {
        super(appCompatActivity);
    }

    public GullWing(@NonNull Context appCompatActivity, @Nullable AttributeSet attrs) {
        super(appCompatActivity, attrs);
    }

    public GullWing(@NonNull Context appCompatActivity, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(appCompatActivity, attrs, defStyleAttr);
    }

    public synchronized void initializeWheel() {
        super.initializeWheel();
        speedometer=new Speedometer(getContext());
        speedometer.setLayoutParams(new LayoutParams(getLayoutParams().width/4,getLayoutParams().height/4));
        speedometer.initialize();
        speedometer.setX(getLayoutParams().width/2f - speedometer.getLayoutParams().width/2f);
        speedometer.setY(speedometer.getLayoutParams().height/2f);
        setCustomizeDrivingWheel(parentView -> {
            setNeutralizeWhenLostFocus(true);
            parentView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.transparent)));
            GullWing.this.getDrivingWheel().setRotationX(0);
            GullWing.this.getDrivingPads().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.gold)));
            GullWing.this.getDrivingWheelEnclosure().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.transparent)));
            GullWing.this.getAxle3().setVisibility(INVISIBLE);
            GullWing.this.getAxle().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.lightBlack)));
            GullWing.this.getAxle2().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.lightBlack)));
            GullWing.this.getHornHolder().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.gold)));
            GullWing.this.getHorn().setImageDrawable(ContextCompat.getDrawable(getContext(),ControlButtonsView.TRIS_BUTTONS));
            GullWing.this.setDrivingWheelAnimationDuration(1000);
            getDrivingWheel().addView(speedometer);

            uTurnView=new UTurnView(getContext());
            uTurnView.setLayoutParams(new LayoutParams(getLayoutParams().width/6,getLayoutParams().height/6));
            uTurnView.setScaleX(0f);
            uTurnView.setScaleY(0f);
            uTurnView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.transparent));
            uTurnView.initialize();
            uTurnView.setRotationY(180);
            addView(uTurnView);

            setDynamicUTurn(new DynamicUTurn() {
                @Override
                public void animateRight() {
                    if(uTurnView!=null){
                        if(uTurnView.getRotationY()==180){
                            uTurnView.setRotationY(0);
                            uTurnView.setX(getLayoutParams().width-uTurnView.getLayoutParams().width);
                        }
                        if(uTurnView.getScaleX()==0 && uTurnView.getScaleY()==0){
                            uTurnView.animate().scaleX(getLayoutParams().width / 500f).scaleY(getLayoutParams().height / 500f).setDuration(200)
                                    .withEndAction(() -> uTurnView.animate().scaleX(0).scaleY(0).setDuration(200).start()).start();
                            uTurnView.invalidate();
                        }
                    }
                }

                @Override
                public void animateLeft() {
                    if(uTurnView!=null){
                        if(uTurnView.getRotationY()==0){
                            uTurnView.setRotationY(180);
                            uTurnView.setX(0);
                        }
                        if(uTurnView.getScaleX()==0 && uTurnView.getScaleY()==0){
                            uTurnView.animate().scaleX(getLayoutParams().width / 500f).scaleY(getLayoutParams().height / 500f).setDuration(200)
                                    .withEndAction(() -> uTurnView.animate().scaleX(0).scaleY(0).setDuration(200).start()).start();
                            uTurnView.invalidate();
                        }
                    }
                }

                @Override
                public void hide() {
                    if(uTurnView!=null){
                        uTurnView.animate().scaleX(0).scaleY(0).setDuration(200).start();
                        uTurnView.invalidate();
                    }
                }
            });
        });
    }

    public UTurnView getuTurnView() {
        return uTurnView;
    }

    public Speedometer getSpeedometer() {
        return speedometer;
    }

    public synchronized void initializeTachometer(GameStickView gameStickView, SimpleApplication jmeGame, VehicleControl vehicle,float inertialThreshold) {
        if(jmeGame==null||gameStickView==null||vehicle==null){
            return;
        }
        gameStickView.createSpeedometerLink(getSpeedometer(),jmeGame,vehicle,inertialThreshold);
    }

}
