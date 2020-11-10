package com.mygame;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.FastMath;
import com.scrappers.jmeGamePad.GameStickView;


@SuppressLint("ViewConstructor")
public class GameStick extends GameStickView {
    
    private VehicleControl vehicleControl;
    private final SimpleApplication jmeContext;
    private final float accelerationForce = FastMath.pow(5, 3.5f);
    private final float brakeForce = 300f;
    private float steeringValue = 0;
    private float accelerationValue = 0;

    public GameStick(Activity appCompatActivity, SimpleApplication jmeContext) {
        super( appCompatActivity);
        this.jmeContext=jmeContext;

    }

    public void setVehicleControl(VehicleControl vehicleControl) {
        this.vehicleControl = vehicleControl;
    }

    @Override
    public void accelerate(final float pulse) {
        jmeContext.enqueue(new Runnable() {
            @Override
            public void run() {
                accelerationValue+=pulse;
                accelerationValue+=accelerationForce;
                vehicleControl.accelerate(accelerationValue);

            }
        });

    }

    @Override
    public void reverseTwitch(float pulse) {
        jmeContext.enqueue(new Runnable() {
            @Override
            public void run() {
                vehicleControl.accelerate(-accelerationForce*2);
                vehicleControl.brake(brakeForce);
            }
        });

    }

    @Override
    public void steerRT(final float pulse) {
        jmeContext.enqueue(new Runnable() {
            @Override
            public void run() {
                vehicleControl.steer(-pulse/8);

            }
        });

    }

    @Override
    public void steerLT(final float pulse) {
        jmeContext.enqueue(new Runnable() {
            @Override
            public void run() {
                vehicleControl.steer(pulse/8);

            }
        });

    }

    @Override
    public void neutralizeState(float pulseX, float pulseY) {
        jmeContext.enqueue(new Runnable() {
            @Override
            public void run() {
                accelerationValue=0;
                vehicleControl.accelerate(0);
                vehicleControl.clearForces();
                vehicleControl.steer(0);
                vehicleControl.brake(200f);

            }
        });

    }


}
