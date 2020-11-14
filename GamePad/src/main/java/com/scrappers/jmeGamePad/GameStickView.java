package com.scrappers.jmeGamePad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

@SuppressLint("ViewConstructor")
public abstract class GameStickView extends CardView  {

    private ImageView stick;
    private final Activity appCompatActivity;
    private float xOrigin;
    private float yOrigin;
    private Path stickPath;
    private Paint stickBrush;
    private float x;
    private float y;
    private boolean stickPathEnabled;

    /**
     * create a gameStickView & OverRide its abstract methods(gameStickView Listeners).
     * @param appCompatActivity activity instance
     * @apiNote in order to ensure a proper use , extend this class better than using anonymous class instance.
     */
    public GameStickView(Activity appCompatActivity) {
        super(appCompatActivity);
        this.appCompatActivity=appCompatActivity;
    }

    /**
     * @apiNote  Internal use only , don't use it in your game context
     */
    public void initializeGameStickHolder(GamePadView gamePadView, float GAMEPAD_CONFIG, int stickViewBackground){
        /*setting the background of the gameStickView ,elevation,focus behaviour */
        this.setBackground(ContextCompat.getDrawable(appCompatActivity,stickViewBackground));
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            this.setElevation(40.20f);
        }
        this.setFocusable(true);
        /* get the StickViewSize(mainly height) */
        DisplayMetrics deviceDisplayMetrics=new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(deviceDisplayMetrics);
        float stickViewSize=deviceDisplayMetrics.heightPixels * GAMEPAD_CONFIG;

        LayoutParams dimensionalAttrs=new LayoutParams((int)stickViewSize,(int)stickViewSize);
        this.setLayoutParams(dimensionalAttrs);
        /* set the location of the gameStickView into the GameStickView*/
        this.setY(gamePadView.getGamePadHeight()-stickViewSize);
        /* add the gameStickView to the gamePad */
        gamePadView.addView(this);
    }

    /**
     * @apiNote  Internal use only , don't use it in your game context
     */
    public void initializeGameStick(int stickBackground, int stickImage, int stickSize){
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

        /* declare the origin of the gameStick */

        /* by subtracting half of the stickBall size(ball radius) from half of the width of the GameStick Stick(lengthX) to ensure good centerization
        , because if you only get the width half of the gameStickView without subtracting the half os the stickBallSize(radius)
        then you would end up with the stickBall upper Left corner located at the center of the gameStick & not the whole Stick*/

        /* middleXOfBall = lengthX(viewWidth)/2f - radius = lengthX/2f - stickWidth(stickSize)/2f */
        xOrigin=(float)this.getLayoutParams().width/2f - (float) stickSize/2f;
        /* middleYOfBall = lengthY(viewHeight)/2f - radius =lengthY/2f - stickHeight(stickSize)/2f*/
        yOrigin=(float)this.getLayoutParams().height/2f -(float) stickSize/2f;

        stick=new ImageView(appCompatActivity);
        stick.setBackground(ContextCompat.getDrawable(appCompatActivity,stickBackground));
        stick.setImageDrawable(ContextCompat.getDrawable(appCompatActivity,stickImage));
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
        canvas.drawPath(stickPath,stickBrush);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /*Tolerated Motion*/
        float xTolerance = 150f;
        float yTolerance=  150f;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isStickPathEnabled()){
                    simulateStickExtension(event.getX(), event.getY());
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if(event.getX() < (xOrigin- xTolerance) || event.getX()<0){
                    /* due to java pixel coordinate plane */
                    /* get the math absolute of the pulseX to prevent negative numbers because the direction of touch Motion towards zeroX*/
                    /*divide that by 25 that's the 1/4 of the RT side(that's divided by 100) */
                    steerLT(Math.abs(event.getX()/25));
                }else if(event.getX() > xOrigin+ xTolerance ){
                    /* due to java pixel coordinate plane */
                    /* no need get the math absolute of the pulseX to prevent negative numbers because you haven't crossed zeroX*/
                    /*divide that by 100 that's the 4 times of the LT side(that's divided by 25) */
                    steerRT(event.getX()/100);
                }
                if(event.getY() < (yOrigin- yTolerance) || event.getY()<0 ){
                    /* due to java pixel coordinate plane */
                    /* get the math absolute of the pulseY to prevent negative numbers because the direction of touch Motion towards zeroY*/
                    /*divide that by 25 that's the 1/4 of the reverseTwitch side(that's divided by 100) */
                    accelerate(Math.abs(event.getY()/25));
                }else if(event.getY() > (yOrigin + yTolerance)){
                    /* due to java pixel coordinate plane */
                    /* no need to get the math absolute of the pulseY to prevent negative numbers because you haven't crossed zeroY*/
                    /*divide that by 100 that's the 4 times of the accelerate side(that's divided by 25) */
                    reverseTwitch(event.getY()/100);
                }

                if(isStickPathEnabled()){
                    applyMotionOnStickExtension(x, y, event.getX(), event.getY());
                }
                moveStick(event.getX(), event.getY());
                invalidate();

                break;
            default:
                neutralizeStick();
                neutralizeState(xOrigin/100,yOrigin/100);
                invalidate();
        }
        return true;
    }


    private void neutralizeStick() {
        /* reset the path preparing for a new motion path */
        stickPath.reset();
        /* set the default x & y for the Stick */
        stick.setX(xOrigin);
        stick.setY(yOrigin);

    }

    private void moveStick(float x , float y){
       if( y>10 && x>10 && x<this.getWidth()/1.2f && y<this.getHeight()/1.2f ){
           stick.setX(x);
           stick.setY(y);
       }
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
    public abstract void accelerate(float pulse);
    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulse the reverse steering pulse
     */
    public abstract void reverseTwitch(float pulse);
    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulse the right steering pulse
     */
    public abstract void steerRT(float pulse);
    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulse the left steering pulse
     */
    public abstract void steerLT(float pulse);
    /**
     * @apiNote Internal use only- don't call from your class
     * @param pulseX the steady state xPulse of the GameStick
     * @param pulseY the steady state yPulse of the GameStick
     */
    public abstract void neutralizeState(float pulseX,float pulseY);
    }