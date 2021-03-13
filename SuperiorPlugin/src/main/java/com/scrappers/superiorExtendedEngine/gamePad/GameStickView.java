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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.VehicleControl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class GameStickView extends CardView implements SensorEventListener , View.OnTouchListener {

    private ImageView stick;
    private float xOrigin;
    private float yOrigin;
    private Path stickPath;
    private Paint stickBrush;
    private float x;
    private float y;
    private boolean stickPathEnabled;
    private SensorManager sensorManager;
    private final float[] rotationMatrix=new float[9];
    private final float[] accelerometerValues =new float[3];
    private final float[] magneticFieldValues =new float[3];
    private final float[] orientationResults=new float[3];
    private float xTolerance = 150f;
    private float yTolerance = 150f;
    private float radius=0f;
    private GameStickListeners gameStickListeners;

    public GameStickView(Context context) {
        super(context);
    }

    public GameStickView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameStickView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * initializes game Stick holder
     * @param stickViewBackground background drawable
     */
    public void initializeGameStickHolder(int stickViewBackground){
        ((AppCompatActivity)getContext()).runOnUiThread(()-> {
            /*setting the background of the gameStickView ,elevation,focus behaviour */
            this.setBackground(ContextCompat.getDrawable(getContext(), stickViewBackground));
            this.setElevation(40.20f);
        });
    }

    /**
     * Initializes the gameStick view that would move the vehicle along the way
     * this is a brief way of calculations (for the illustrations) only :->
     * @apiNote <img src='image.png' width=300 height=200/>
     * @param stickBackground background drawable for the game Stick
     * @param stickImage the game Stick image
     * @param stickSize the stick size ( preferred : 100 , 150 , 200 ); in px
     *
     */
    @SuppressLint("ClickableViewAccessibility")
    public void initializeGameStick(int stickBackground, int stickImage, int stickSize){
        ((AppCompatActivity)getContext()).runOnUiThread(()-> {
            /* declare the origin of the gameStick */

            /* by subtracting half of the stickBall size(ball radius) from half of the width of the GameStick Stick(lengthX) to ensure good centerization
            , because if you only get the width half of the gameStickView without subtracting the half of the stickBallSize(radius)
            then you would end up with the stickBall upper Left corner located at the center of the gameStick & not the whole Stick*/

            /* middleXOfBall = lengthX(viewWidth)/2f - radius = lengthX/2f - stickWidth(stickSize)/2f */
            xOrigin = (float) this.getLayoutParams().width / 2f - (float) stickSize / 2f;
            /* middleYOfBall = lengthY(viewHeight)/2f - radius =lengthY/2f - stickHeight(stickSize)/2f*/
            yOrigin = (float) this.getLayoutParams().height / 2f - (float) stickSize / 2f;
            /*get the minimum between width & height...why? because this will handle the rectangular shaped gameStick*/
            radius = Math.min(xOrigin,yOrigin);

            this.setTag(this.getClass().getName());
            this.setOnTouchListener(this);

            stick = new ImageView(getContext());
            stick.setBackground(ContextCompat.getDrawable(getContext(), stickBackground));
            stick.setImageDrawable(ContextCompat.getDrawable(getContext(), stickImage));
            stick.setClipToOutline(true);
            stick.setFocusable(false);
            /* set stick attrs*/
            LayoutParams stickDimensionalAttrs = new LayoutParams(stickSize, stickSize);
            stick.setLayoutParams(stickDimensionalAttrs);
            /* neutralize the stick at the first site set x & y origin */
            neutralizeStick();
            /* add the stick to the stickView CardView */
            this.addView(stick);
        });
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

    /**
     * Creates a Vehicle~GameStick~Speedometer linkage of data & remote method invocations.
     * @param speedometer speedometer instance
     * @param game jmeGame instance
     * @param vehicleControl your vehicle control instance defined in a jme game
     * @param inertialThreshold is basically an float number , multiply this number
     *                          by the Linear velocity of the vehicle in the direction of Z-axis, so as the result is the virtual speed displayed on the speedometer
     *                          calculated from the linear velocity of the car in the direction of Z axis (front of the car/back of the car).
     */
    public void createSpeedometerLink(Speedometer speedometer, SimpleApplication game, VehicleControl vehicleControl,float inertialThreshold){
        /*for more thread safety*/
        Speedometer.InertialListener inertialListener= new Speedometer.InertialListener(speedometer,((AppCompatActivity)getContext()),vehicleControl);
        inertialListener.setScaleFactor(inertialThreshold);
        game.enqueue(()->game.getStateManager().attach(inertialListener));
    }


    private void neutralizeStick() {
        ((AppCompatActivity)getContext()).runOnUiThread(()-> {
            /* reset the path preparing for a new motion path */
            stickPath.reset();
            /* set the default x & y for the Stick */
            stick.setX(xOrigin);
            stick.setY(yOrigin);
        });
    }

    private void moveStick(float x , float y){
        ((AppCompatActivity)getContext()).runOnUiThread(()-> {
            stick.setX(x);
            stick.setY(y);
        });
    }

    private void simulateStickExtension(float x,float y){
        this.x=x;
        this.y=y;
        ((AppCompatActivity)getContext()).runOnUiThread(()-> {
            stickPath.moveTo(x, y);
        });
    }
    private void applyMotionOnStickExtension(float oldX, float oldY, float newX, float newY){
        ((AppCompatActivity)getContext()).runOnUiThread(()-> {
            stickPath.quadTo(oldX, oldY, newX, newY);
        });
    }

    /**
     * updatable via the device sensors
     * @param pulse angle of rotation in degrees converted to pulses
     */
    private void steerUsingDeviceOrientation(float pulse){
        /* doing interval(threshold) to start steering from ; ie ]-12,12[ are kept spare of steer listeners
        note : there are no mathematical formula fo these values , they are captured from physical testing & personal conclusions*/
        if( pulse<-12){
            if(gameStickListeners!=null){
                gameStickListeners.steerRT(pulse / 10);
            }
        } else if(pulse>12){
            if(gameStickListeners!=null){
                gameStickListeners.steerLT(pulse / 10);
            }
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
            sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            if ( sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null && sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null){
                Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                sensorManager.registerListener(this, accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(this, magneticFieldSensor,SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(getContext(), "Please ensure your device is supported !", Toast.LENGTH_LONG).show();
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
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if ( isStickPathEnabled() ){
                        simulateStickExtension(event.getX(), event.getY());
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                        if ( event.getX() < (xOrigin - xTolerance) || event.getX() < 0 ){
                            /* due to java pixel coordinate plane */
                            /* get the math absolute of the pulseX to prevent negative numbers because the direction of touch Motion towards zeroX*/
                            /*divide that by 25 that's the 1/4 of the RT side(that's divided by 100) */
                            if(gameStickListeners!=null){
                                gameStickListeners.steerLT(Math.abs(event.getX() / 25));
                            }
                        } else if ( event.getX() > xOrigin + xTolerance ){
                            /* due to java pixel coordinate plane */
                            /* no need get the math absolute of the pulseX to prevent negative numbers because you haven't crossed zeroX*/
                            /*divide that by 100 that's the 4 times of the LT side(that's divided by 25) */
                            if(gameStickListeners!=null){
                                gameStickListeners.steerRT(-event.getX() / 100);
                            }
                        }
                        if ( event.getY() < (yOrigin - yTolerance) || event.getY() < 0 ){
                            /* due to java pixel coordinate plane */
                            /* get the math absolute of the pulseY to prevent negative numbers because the direction of touch Motion towards zeroY*/
                            /*divide that by 25 that's the 1/4 of the reverseTwitch side(that's divided by 100) */
                            if(gameStickListeners!=null){
                                gameStickListeners.accelerate(Math.abs(event.getY() / 25));
                            }
                        } else if ( event.getY() > (yOrigin + yTolerance) ){
                            /* due to java pixel coordinate plane */
                            /* no need to get the math absolute of the pulseY to prevent negative numbers because you haven't crossed zeroY*/
                            /*divide that by 100 that's the 4 times of the accelerate side(that's divided by 25) */
                            if(gameStickListeners!=null){
                                gameStickListeners.reverseTwitch(event.getY() / 100);
                            }
                        }
                        if ( isStickPathEnabled() ){
                            applyMotionOnStickExtension(x, y, event.getX(), event.getY());
                        }
                    float userDisplacement=(float) Math.sqrt(Math.pow(event.getX()-xOrigin,2)
                            + Math.pow(event.getY()-yOrigin,2));
                    /*check if the user displacement is above or below the radius*/
                    if(userDisplacement<radius){
                        /*move the stick to where the user presses*/
                        moveStick(event.getX(), event.getY());
                    }else {
                        /*if the displacement is outside of the circle(>radius)
                                        -> the approach is to try to
                                        scale the large triangle formed by the userDisplacement into a smaller triangle
                                        created by the circle radius with the same angles of motion.
                        * From the similarity rule of right angled triangles between the displacement tri & the origins tri :
                        * the magnitude of the ratio represents : the ratio between the 2 triangles or what's meant by SCALE FACTOR
                        * which means if we want to scale tri-ABC to be tri-XYZ , then , tri-ABC * SCALE_FACTOR(ratio) = tri-XYZ.
                        * */
                        float scaleFactor= radius /userDisplacement;
                        /*calculates length of parts outside the circle*/
                        /* why mincing the radius from the userX & userY -> because the userX & userY should be inside the circle frame (before radius)*/
                        float offCircleX=event.getX() - radius;
                        float offCircleY=event.getY() - radius;
                        /*(xOrigin,yOrigin) represents the origin point of the user's displacement*/
                        /*user's displacement vector = sqrt(Vx^2 + Vy^2) = sqrt(event.getX()^2 + event.getY()^2);
                        *
                        * so , starting from the user origin point of motion(xOrigin,yOrigin) & moving on to the circle frame
                        *
                        * calculating length from the origin(xOrigin,yOrigin) to the circle frame :
                        *
                        * Vx = xOrigin + modifiedUserTranslationX = xOrigin + userX * scaleFactor = radius + directionX(very low value representing direction in X-axis)
                        * Vy = yOrigin + modifiedUserTranslationY = yOrigin + userY * scaleFactor = radius + directionY(very low value representing direction in Y-axis)
                        *
                        * where , userX = event.getX() - xOrigin = offCircleX
                        *       , userY = event.getY() - yOrigin = offCircleY
                         */
                        moveStick(xOrigin+(offCircleX)*scaleFactor,yOrigin+(offCircleY)*scaleFactor);

                    }
                    invalidate();
                    break;
                default:
                    neutralizeStick();
                    if(gameStickListeners!=null){
                        gameStickListeners.neutralizeState(xOrigin / 100, yOrigin / 100);
                    }
                    invalidate();
            }
            return true;
        }else{
            return false;
        }
    }

    public void setxTolerance(float xTolerance) {
        this.xTolerance = xTolerance;
    }

    public void setyTolerance(float yTolerance) {
        this.yTolerance = yTolerance;
    }

    public interface GameStickListeners{
        void accelerate(float pulse);
        void reverseTwitch(float pulse);
        void steerRT(float pulse);
        void steerLT(float pulse);
        void neutralizeState(float pulseX,float pulseY);
    }

    public void setGameStickListeners(GameStickListeners gameStickListeners) {
        this.gameStickListeners = gameStickListeners;
    }
}