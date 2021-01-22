package com.myGame.JmEGamePadExample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.FastMath;
import com.scrappers.jmeGamePad.GameStickView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class GameStick extends GameStickView {
    
    private VehicleControl vehicleControl;
    private final float accelerationForce = FastMath.pow(5, 3.5f);
    private final float brakeForce = 300f;
    private float accelerationValue = 0;

    public float getAccelerationValue() {
        return accelerationValue;
    }

    public float getAccelerationForce() {
        return accelerationForce;
    }

    public GameStick(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    public GameStick(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameStick(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setVehicleControl(VehicleControl vehicleControl) {
        this.vehicleControl = vehicleControl;
    }

    public void setAccelerationValue(float accelerationValue) {
        this.accelerationValue = accelerationValue;
    }

    @Override
    public void accelerate(final float pulse) {
        super.accelerate(pulse);
        accelerationValue+=pulse;
        accelerationValue+=accelerationForce;
        vehicleControl.accelerate(accelerationValue);
    }

    @Override
    public void reverseTwitch(float pulse) {
        super.reverseTwitch(pulse);
        vehicleControl.accelerate(-accelerationForce*2);
        vehicleControl.brake(brakeForce/2);
    }

    @Override
    public void steerRT(final float pulse) {
                vehicleControl.steer(pulse/8);

    }

    @Override
    public void steerLT(final float pulse) {
                vehicleControl.steer(pulse/8);
    }

    @Override
    public void neutralizeState(float pulseX, float pulseY) {
                vehicleControl.accelerate(0);
                vehicleControl.clearForces();
                vehicleControl.steer(0);
    }


}
