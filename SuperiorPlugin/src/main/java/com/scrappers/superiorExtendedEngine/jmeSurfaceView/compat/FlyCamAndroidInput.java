package com.scrappers.superiorExtendedEngine.jmeSurfaceView.compat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.jme3.app.SimpleApplication;
import com.jme3.input.CameraInput;

@SuppressLint("ViewConstructor")
public class FlyCamAndroidInput extends View {
    private final SimpleApplication application;
    private RiseLowerBoundaries touchBoundaries;
    private CurrentCommand command;
    private int displayWidth;
    private int displayHeight;
    final private int debugColor = Color.argb(50, 255, 30, 255);
    final private int transparentColor = Color.argb(0, 255, 30, 255);
    public static final int ID = 'F' + 'L' + 'Y' + 'C' + 'A' + 'M';
    private float oldX = 0f;
    private float oldY = 0f;
    private float analogX = 0f;
    private float analogY = 0f;
    private enum CurrentCommand{
        FORWARD, BACKWARD, LEFT, RIGHT, NEUTRALIZE, RISE, LOWER
    }

    protected FlyCamAndroidInput(@NonNull Context context, final SimpleApplication application) {
        super(context);
        this.application = application;
    }

    @SuppressLint("ResourceType")
    protected void init(){
        setId(ID);

        application.getFlyByCamera().setMoveSpeed(0.5f);
        application.getFlyByCamera().setRotationSpeed(0.4f);

        displayWidth = CompatHarness.Display.getDisplayMetrics((Activity)getContext()).widthPixels;
        displayHeight = CompatHarness.Display.getDisplayMetrics((Activity)getContext()).heightPixels;
        final float xOffset = displayWidth / 50f;
        final float yOffset = displayHeight / 100f;
        //spare quarter the screen width for the flyCam input listener
        this.setLayoutParams(new ViewGroup.LayoutParams(displayWidth / 4, displayHeight));
        this.setX(xOffset);
        this.setY((displayHeight / 2f) - (getLayoutParams().height / 2f) - yOffset);

        this.setBackgroundColor(transparentColor);

    }
    protected void lateInitOtherEvents(){
        final float xOffset = displayWidth / 50f;
        //initialize the Rise, lower touch boundaries
        touchBoundaries = new RiseLowerBoundaries(getContext());
        touchBoundaries.setLayoutParams(getLayoutParams());
        touchBoundaries.setX(displayWidth - touchBoundaries.getLayoutParams().width - xOffset);
        touchBoundaries.setY(getY());
        touchBoundaries.setBackgroundColor(transparentColor);
        ((ViewGroup)getParent()).addView(touchBoundaries);
    }
    protected void showTouchBoundaries(){
        setBackgroundColor(debugColor);
        touchBoundaries.setBackgroundColor(debugColor);
    }
    protected void unShowTouchBoundaries(){
        setBackgroundColor(transparentColor);
        touchBoundaries.setBackgroundColor(transparentColor);
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                analogX = event.getX() - oldX;
                analogY = event.getY() - oldY;
                //store old values for future use
                oldX = event.getX();
                oldY = event.getY();
                processTouchEvent(event.getX(), event.getY(), event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_UP:
                setCommand(CurrentCommand.NEUTRALIZE);
                return true;
            case MotionEvent.ACTION_MOVE:
                processTouchEvent(event.getX(), event.getY(), analogX, analogY);
                return true;
            default:
                return false;
        }
    }
    private void processTouchEvent(final float x, final float y, final float analogX, final float analogY){
        final float xOffset = 80f;
        final float yOffset = 150f;
        final float xCenter = getLayoutParams().width / 2f;
        final float yCenter = getLayoutParams().height / 2f;

        if((x < xCenter - xOffset && analogX < 0) || x < 0) {
            //left
            setCommand(CurrentCommand.LEFT);
            final float analog = (xCenter - analogX);
            onAnalog(analog / displayWidth);
        }else if(x > xCenter + xOffset && analogX > 0){
            //right
            setCommand(CurrentCommand.RIGHT);
            final float analog = analogX;
            onAnalog(analog / displayWidth);
        }

        if((y < yCenter - yOffset && analogY < 0) || y < 0){
            //forward
            setCommand(CurrentCommand.FORWARD);
            final float analog = (yCenter - analogY);
            onAnalog(analog / displayHeight);
        }else if(y > yCenter + yOffset && analogY > 0){
            //backward
            setCommand(CurrentCommand.BACKWARD);
            final float analog = analogY;
            onAnalog(analog / displayHeight);
        }
        setCommand(CurrentCommand.NEUTRALIZE);

    }
    private void setCommand(CurrentCommand command) {
        this.command = command;
    }

    private void onAnalog(final float analog) {
        if(!application.getFlyByCamera().isEnabled()){
            return;
        }
        switch (command){
            case LEFT:
                application.getFlyByCamera().onAnalog(CameraInput.FLYCAM_STRAFELEFT, analog, 0);
                break;
            case RIGHT:
                application.getFlyByCamera().onAnalog(CameraInput.FLYCAM_STRAFERIGHT, analog, 0);
                break;
            case FORWARD:
                application.getFlyByCamera().onAnalog(CameraInput.FLYCAM_FORWARD, analog , 0);
                break;
            case BACKWARD:
                application.getFlyByCamera().onAnalog(CameraInput.FLYCAM_BACKWARD, analog, 0);
                break;
            case RISE:
                application.getFlyByCamera().onAnalog(CameraInput.FLYCAM_RISE, analog, 0);
                break;
            case LOWER:
                application.getFlyByCamera().onAnalog(CameraInput.FLYCAM_LOWER, analog, 0);
                break;
        }
        setCommand(CurrentCommand.NEUTRALIZE);
    }
    private class RiseLowerBoundaries extends View{
        private float analogY;
        private float oldY;
        public RiseLowerBoundaries(Context context) {
            super(context);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    analogY = event.getY() - oldY;
                    //store old values for future use
                    oldY = event.getY();
                    processTouchEvent(event.getY(), event.getY());
                    return true;
                case MotionEvent.ACTION_UP:
                    setCommand(CurrentCommand.NEUTRALIZE);
                    analogY = 0;
                    oldY = 0;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    processTouchEvent(event.getY(), analogY);
                    return true;
                default:
                    return false;
            }
        }
        private void processTouchEvent(final float y, final float analogY) {
            final float yOffset = 80f;
            final float yCenter = getLayoutParams().height / 2f;

            if(y < yCenter - yOffset || analogY < 0){
                //rise
                setCommand(CurrentCommand.RISE);
                final float analog = (yCenter - analogY);
                onAnalog(analog / displayHeight);
            }else if(y > yCenter + yOffset || analogY > 0){
                //lower
                setCommand(CurrentCommand.LOWER);
                final float analog = analogY;
                onAnalog(analog / displayHeight);
            }
        }
    }
}
