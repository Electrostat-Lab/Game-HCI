package com.myGame.JmEGamePadExample;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.input.ChaseCamera;
import com.jme3.math.FastMath;
import com.scrappers.superiorExtendedEngine.gamePad.GameStickView;
import com.scrappers.superiorExtendedEngine.vehicles.GullWing;
import com.scrappers.superiorExtendedEngine.vehicles.UTurnView;


public class GameStick implements GameStickView.GameStickListeners, GullWing.OnSteering {

    private VehicleControl vehicleControl;
    private final float accelerationForce = FastMath.pow(5, 3.5f);
    private static final float brakeForce = 300f;
    private float accelerationValue = 0;
    private ChaseCamera chaseCamera;
    private UTurnView uTurnView;
    private static float defaultAngle=-(FastMath.PI +FastMath.HALF_PI);

    public float getAccelerationValue() {
        return accelerationValue;
    }

    public float getAccelerationForce() {
        return accelerationForce;
    }


    public void setVehicleControl(VehicleControl vehicleControl) {
        this.vehicleControl = vehicleControl;
    }

    public void setAccelerationValue(float accelerationValue) {
        this.accelerationValue = accelerationValue;
    }

    public void setChaseCamera(ChaseCamera chaseCamera) {
        this.chaseCamera = chaseCamera;
        chaseCamera.setDefaultDistance(-25f);
        chaseCamera.setDefaultVerticalRotation(-FastMath.PI/10);
        chaseCamera.setDefaultHorizontalRotation(-(FastMath.PI + FastMath.HALF_PI));
    }

    @Override
    public void accelerate(final float pulse) {
        accelerationValue+=pulse;
        accelerationValue+=accelerationForce;
        vehicleControl.accelerate(0,accelerationValue);
        vehicleControl.accelerate(1,accelerationValue);
    }

    @Override
    public void reverseTwitch(float pulse) {
        vehicleControl.accelerate(-accelerationForce*2);
        vehicleControl.brake(brakeForce/2);
    }

    @Override
    public void steerRT(float pulse) {
        vehicleControl.steer(pulse/10);
//        if(vehicleControl.getWheel(0).getEngineForce()>0f){
//            chaseCamera.setDefaultHorizontalRotation(chaseCamera.getHorizontalRotation() + (-pulse / 80));
//        }
    }

    @Override
    public void steerLT(float pulse) {
        vehicleControl.steer(pulse/8);
//        if(vehicleControl.getWheel(1).getEngineForce()>0f){
//            chaseCamera.setDefaultHorizontalRotation(chaseCamera.getHorizontalRotation() + (-pulse / 80));
//        }
    }

    @Override
    public void neutralizeState(float pulseX, float pulseY) {
        vehicleControl.accelerate(0);
        vehicleControl.clearForces();
        vehicleControl.steer(0);
    }

    @Override
    public void steerRight(float angle) {
        vehicleControl.steer(-angle/10);
//        if(vehicleControl.getWheel(0).getEngineForce()>0f){
//            chaseCamera.setDefaultHorizontalRotation(chaseCamera.getHorizontalRotation() + (-angle / 80));
//        }
    }

    @Override
    public void steerLeft(float angle) {
        vehicleControl.steer(-angle/8);
//        if(vehicleControl.getWheel(1).getEngineForce()>0f){
//            chaseCamera.setDefaultHorizontalRotation(chaseCamera.getHorizontalRotation() + (-angle / 80));
//        }
    }

    @Override
    public void neutralize(float angle) {
        vehicleControl.steer(angle);
    }
}
