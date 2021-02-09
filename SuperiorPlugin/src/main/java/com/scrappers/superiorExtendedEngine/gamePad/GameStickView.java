package com.scrappers.superiorExtendedEngine.gamePad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jme3.app.SimpleApplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class GameStickView extends CardView implements SensorEventListener , View.OnTouchListener {

    private ImageView stick;
    private AppCompatActivity appCompatActivity;
    private float xOrigin;
    private float yOrigin;
    private Path stickPath;
    private Paint stickBrush;
    private float x;
    private float y;
    private boolean stickPathEnabled;
    private SensorManager sensorManager;
    private  NeutralizeStateLogger neutralizeStateLogger;
    private final float[] rotationMatrix=new float[9];
    private final float[] accelerometerValues =new float[3];
    private final float[] magneticFieldValues =new float[3];
    private final float[] orientationResults=new float[3];
    private Speedometer speedometer;
    private SimpleApplication game;
    private float baseRadius=0f;
    private float radius=0f;

    /**
     * create a gameStickView & OverRide its abstract methods(gameStickView Listeners).
     * @param appCompatActivity activity instance
     * @apiNote in order to ensure a proper use , extend this class better than using anonymous class instance.
     */
    public GameStickView(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
        this.appCompatActivity=appCompatActivity;
    }

    public GameStickView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameStickView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAppCompatActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * @apiNote  Internal use only , don't use it in your game context
     */
    public void initializeGameStickHolder(GamePadView gamePadView, float GAMEPAD_CONFIG, int stickViewBackground){
        /*setting the background of the gameStickView ,elevation,focus behaviour */
        this.setBackground(ContextCompat.getDrawable(appCompatActivity,stickViewBackground));
        this.setClipToOutline(true);
        this.setElevation(40.20f);
        /* get the StickViewSize(mainly height) */
        DisplayMetrics deviceDisplayMetrics=new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(deviceDisplayMetrics);
        float stickViewSize=deviceDisplayMetrics.heightPixels * GAMEPAD_CONFIG;

        LayoutParams dimensionalAttrs=new LayoutParams((int)stickViewSize,(int)stickViewSize);
        this.setLayoutParams(dimensionalAttrs);
        /* set the location of the gameStickView into the GamePadView*/
        this.setY(gamePadView.getGamePadHeight()-stickViewSize);
        /* add the gameStickView to the gamePad */
        gamePadView.addView(this);
    }

    /**
     * @apiNote  use this to initialize GameStickView independent use of the gamePadView
     */
    public void initializeGameStickHolder(int stickViewBackground){
        /*setting the background of the gameStickView ,elevation,focus behaviour */
        this.setBackground(ContextCompat.getDrawable(appCompatActivity,stickViewBackground));
        this.setElevation(40.20f);
    }

    /**
     * @apiNote  Internal use only , don't use it in your game context
     */
    @SuppressLint("ClickableViewAccessibility")
    public void initializeGameStick(int stickBackground, int stickImage, int stickSize){

        /* declare the origin of the gameStick */

        /* by subtracting half of the stickBall size(ball radius) from half of the width of the GameStick Stick(lengthX) to ensure good centerization
        , because if you only get the width half of the gameStickView without subtracting the half os the stickBallSize(radius)
        then you would end up with the stickBall upper Left corner located at the center of the gameStick & not the whole Stick*/

        /* middleXOfBall = lengthX(viewWidth)/2f - radius = lengthX/2f - stickWidth(stickSize)/2f */
        xOrigin=(float)this.getLayoutParams().width/2f - (float) stickSize/2f;
        /* middleYOfBall = lengthY(viewHeight)/2f - radius =lengthY/2f - stickHeight(stickSize)/2f*/
        yOrigin=(float)this.getLayoutParams().height/2f -(float) stickSize/2f;
        radius=(float)Math.min(this.getLayoutParams().width,this.getLayoutParams().height)/2;
        baseRadius=(float) Math.min(this.getLayoutParams().width,this.getLayoutParams().height)/4;

        this.setTag(this.getClass().getName());
        this.setOnTouchListener(this);

        stick=new ImageView(appCompatActivity);
        stick.setBackground(ContextCompat.getDrawable(appCompatActivity,stickBackground));
        stick.setImageDrawable(ContextCompat.getDrawable(appCompatActivity,stickImage));
        stick.setClipToOutline(true);
        stick.setFocusable(false);
        /* set stick attrs*/
        LayoutParams stickDimensionalAttrs=new LayoutParams(stickSize,stickSize);
        stick.setLayoutParams(stickDimensionalAttrs);
        /* neutralize the stick at the first site set x & y origin */
        neutralizeStick();
        /* add the stick to the stickView CardView */
        this.addView(stick);


    }

    /**
     * initializes a drawing pen for the moving stick
     */
    public void initializeStickPath(){
        /*Define the stickPath & stickBrush */
        stickPath=new Path();
        stickBrush=new Paint();
        stickBrush.setAntiAlias(true);
        stickBrush.setDither(true);
        stickBrush.setStyle(Paint.Style.STROKE);
        stickBrush.setStrokeJoin(Paint.Join.ROUND);
        stickBrush.setStrokeCap(Paint.Cap.ROUND);
        stickBrush.setXfermode(null);
        stickBrush.setAlpha(0xff);
    }


    /**
     * @apiNote  Internal use only , don't use it in your game context
     */
    public void setMotionPathStrokeWidth(int width){
        stickBrush.setStrokeWidth(width);
    }

    /**
     * @apiNote  Internal use only , don't use it in your game context
     */
    public void setMotionPathColor(int color){
        stickBrush.setColor(color);
    }

    /**
     * @apiNote  Internal use only , don't use it in your game context
     */
    public void setStickPathEnabled(boolean stickPathEnabled) {
        this.stickPathEnabled = stickPathEnabled;
    }

    private boolean isStickPathEnabled() {
        return stickPathEnabled;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(stickPath !=null && stickBrush !=null){
            canvas.drawPath(stickPath, stickBrush);
        }
    }
    public void createSpeedometerLink(Speedometer speedometer, SimpleApplication game){
        this.speedometer=speedometer;
        this.game=game;
    }
    public void applySpeedometerInertia(){
        /*for more thread safety*/
            game.getStateManager().attach(new Speedometer.InertialListener(speedometer));
    }
    private void incrementSpeedometer(){
        if(speedometer==null){
            return;
        }
        if(speedometer.getSpeedCursor().getProgress() < Speedometer.PROGRESS_MAX){
            speedometer.getSpeedCursor().setProgress(speedometer.getSpeedCursor().getProgress() + 1);
            speedometer.getDigitalScreen().setText(String.valueOf(new int[]{Integer.parseInt(speedometer.getDigitalScreen().getText().toString()) + 1}[0]));
        }
    }



    private void neutralizeStick() {
        /* reset the path preparing for a new motion path */
        stickPath.reset();
        /* set the default x & y for the Stick */
        stick.setX(xOrigin);
        stick.setY(yOrigin);
    }

    private void moveStick(float x , float y){
           stick.setX(x);
           stick.setY(y);
    }

    private void simulateStickExtension(float x,float y){
        this.x=x;
        this.y=y;
        stickPath.moveTo(x,y);
    }
    private void applyMotionOnStickExtension(float oldX, float oldY, float newX, float newY){
        stickPath.quadTo(oldX,oldY,newX,newY);
    }


    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulse the acceleration pulse
     */
    public void accelerate(float pulse){
        /*increment the speedometer*/
        incrementSpeedometer();
    }
    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulse the reverse steering pulse
     */
    public void reverseTwitch(float pulse){
        /*increment the speedometer*/
        incrementSpeedometer();
    }
    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulse the right steering pulse
     */
    public void steerRT(float pulse){
        throw new RuntimeException("OverRide steerRT method to use it !");
    }
    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulse the left steering pulse
     */
    public void steerLT(float pulse){
        throw new RuntimeException("OverRide steerLT method to use it !");
    }
    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulseX the steady state xPulse of the GameStick
     * @param pulseY the steady state yPulse of the GameStick
     */
    public void neutralizeState(float pulseX,float pulseY){
        throw new RuntimeException("OverRide neutralizeState method to use it !");
    }

    /**
     * updatable via the device sensors
     * @param pulse angle of rotation in degrees converted to pulses
     */
    private void steerUsingDeviceOrientation(float pulse){
        /* doing interval(threshold) to start steering from ; ie ]-12,12[ are kept spare of steer listeners
        note : there are no mathematical formula fo these values , they are captured from physical testing & personal conclusions*/
        if( pulse<-12){
            steerRT(pulse/10);
        } else if(pulse>12){
            steerLT(pulse/10);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        /*initialize a sensor combination - accelerometer + magneticField sensor to get the device world coordination orientation angles*/
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            /*copy sensor values */
            System.arraycopy(sensorEvent.values,0, accelerometerValues,0, accelerometerValues.length);
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            /*copy sensor values */
            System.arraycopy(sensorEvent.values,0, magneticFieldValues,0, magneticFieldValues.length);
        }
        /* Computes the inclination matrix(I) , as well as the rotation matrix(R)
        * transforming a vector from the device coordinate system to the
        * world's coordinate system which is defined as a direct orthonormal basis
        * the result is stored in #{rotationMatrix} in 3(columns)*3(rows) matrix & #{inclinationMatrix}if exists*/
        SensorManager.getRotationMatrix(rotationMatrix,null, accelerometerValues, magneticFieldValues);
        /* get the device orientation angles in Radian & stores the result in orientationResults 3(columns)*1(rows) matrix*/
        SensorManager.getOrientation(rotationMatrix,orientationResults);
        /*validate the values to the steeringUsingDeviceOrientationListener*/
        steerUsingDeviceOrientation((float) Math.toDegrees(orientationResults[1]));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * initializes Game sensors for steering RT & LT currently , supports only {@link ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE} no ReverseLandscape
     */
    public void initializeRotationSensor(){
        try {
            sensorManager = (SensorManager) appCompatActivity.getSystemService(Context.SENSOR_SERVICE);
            if ( sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null && sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null){
                Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                sensorManager.registerListener(this, accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(this, magneticFieldSensor,SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(appCompatActivity, "Please ensure your device is supported !", Toast.LENGTH_LONG).show();
            }
        }catch (Exception error){
            error.printStackTrace();
        }

    }
    public void deInitializeSensors(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(String.valueOf(v.getTag()).equals(this.getClass().getName())){
            /*Tolerated Motion*/
            float xTolerance = 150f;
            float yTolerance = 150f;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if ( isStickPathEnabled() ){
                        simulateStickExtension(event.getX(), event.getY());
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float userDisplacement=(float) Math.sqrt(Math.pow(event.getX()-xOrigin,2)
                                                     + Math.pow(event.getY()-yOrigin,2));
                        if ( event.getX() < (xOrigin - xTolerance) || event.getX() < 0 ){
                            /* due to java pixel coordinate plane */
                            /* get the math absolute of the pulseX to prevent negative numbers because the direction of touch Motion towards zeroX*/
                            /*divide that by 25 that's the 1/4 of the RT side(that's divided by 100) */
                            steerLT(Math.abs(event.getX() / 25));
                        } else if ( event.getX() > xOrigin + xTolerance ){
                            /* due to java pixel coordinate plane */
                            /* no need get the math absolute of the pulseX to prevent negative numbers because you haven't crossed zeroX*/
                            /*divide that by 100 that's the 4 times of the LT side(that's divided by 25) */
                            steerRT(-event.getX() / 100);
                        }
                        if ( event.getY() < (yOrigin - yTolerance) || event.getY() < 0 ){
                            /* due to java pixel coordinate plane */
                            /* get the math absolute of the pulseY to prevent negative numbers because the direction of touch Motion towards zeroY*/
                            /*divide that by 25 that's the 1/4 of the reverseTwitch side(that's divided by 100) */
                            accelerate(Math.abs(event.getY() / 25));
                        } else if ( event.getY() > (yOrigin + yTolerance) ){
                            /* due to java pixel coordinate plane */
                            /* no need to get the math absolute of the pulseY to prevent negative numbers because you haven't crossed zeroY*/
                            /*divide that by 100 that's the 4 times of the accelerate side(that's divided by 25) */
                            reverseTwitch(event.getY() / 100);
                        }

                        if ( isStickPathEnabled() ){
                            applyMotionOnStickExtension(x, y, event.getX(), event.getY());
                        }
                    /*check if the user displacement is above or below the radius*/
                    if(userDisplacement<radius){
                        /*move the stick to where the user presses*/
                        moveStick(event.getX(), event.getY());
                    }else {
                        /*if the displacement is outside of the circle(>radius) ->
                        * From the similarity rule of right angled triangles between the displacement tri & the origins tri :
                        *
                        * d/r = (event.getX() - xOrigin)/baseRadius =
                        *
                        * */

                        float ratio=baseRadius/userDisplacement;
                        moveStick(xOrigin + (event.getX() - xOrigin)*ratio,yOrigin+(event.getY()-yOrigin)*ratio);
                    }
                    invalidate();
                    break;
                default:
                    neutralizeStick();
                    neutralizeState(xOrigin / 100, yOrigin / 100);
                    neutralizeStateLogger.getLog(xOrigin / 100, yOrigin / 100);
                    invalidate();
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     * @apiNote Custom listener Interface for Logging & custom callings
     * =>this is not a part of the lib development
     * =>IDK , may be just for training , but you can still use it instead of the neutralize Listener Anonymously
     */
    public interface NeutralizeStateLogger{
        void getLog(float pulseX,float pulseY);
    }

    /**
     * set the custom listener interface for the neutralize state manner
     * =>this is not a part of the lib development
     * =>IDK , may be just for training , but you can still use it instead of the neutralize Listener Anonymously
     * @param neutralizeStateLogger an instance of a class implementing #{{@link NeutralizeStateLogger}} interface or an anonymously created class
     */
    public void setNeutralizeStateLoggerListener(NeutralizeStateLogger neutralizeStateLogger){
        this.neutralizeStateLogger=neutralizeStateLogger;
    }
}