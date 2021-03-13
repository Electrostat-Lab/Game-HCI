package com.scrappers.superiorExtendedEngine.misc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scrappers.GamePad.R;
import com.scrappers.superiorExtendedEngine.gamePad.ControlButtonsView;
import com.scrappers.superiorExtendedEngine.gamePad.GameStickView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

/**
 * An android view class , to create a drivingWheel for cars,vehicles,motorcycles,jets,etc.
 * @author pavl_g
 */
public class DrivingWheelView extends RelativeLayout {
    private int DRIVING_PADS_TINT = R.color.pureWhite;
    private int AXLE_TINT = R.color.pureWhite;
    private int DRIVING_WHEEL = R.drawable.driving_wheel;
    private int DRIVING_WHEEL_TINT = R.color.fireEngineRed;
    private int WHEEL_AXLE = R.drawable.wheel_axle;
    private int DRIVING_PADS = R.drawable.driving_pads;
    private int HORN_HOLDER = R.drawable.driving_handle;
    private int HORN = R.drawable.ic_air_horn;
    private boolean neutralizeWhenLostFocus=true;
    private CardView drivingWheel;
    private CardView hornHolder;
    private ImageView axle;
    private ImageView axle2;
    private ImageView axle3;
    private ImageView horn;
    private CardView drivingPads;
    private CardView drivingWheelEnclosure;

    private float xOrigin;
    private float yOrigin;
    private float toleranceX=50;
    private long drivingWheelAnimationDuration=500;
    public OnSteering onSteering;
    private CustomizeDrivingWheel customizeDrivingWheel;
    private DynamicUTurn dynamicUTurn;

    public DrivingWheelView(@NonNull Context appCompatActivity) {
        super(appCompatActivity);
    }

    public DrivingWheelView(@NonNull Context appCompatActivity, @Nullable AttributeSet attrs) {
        super(appCompatActivity, attrs);
    }

    public DrivingWheelView(@NonNull Context appCompatActivity, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(appCompatActivity, attrs, defStyleAttr);
    }

    /**
     * initialize a steering wheel with its default shape
     *
     */
    public void initializeWheel(){
        ((AppCompatActivity)getContext()).runOnUiThread(()->{
            xOrigin=getLayoutParams().width/2f;
            yOrigin=getLayoutParams().height/2f;

            setBackground(ContextCompat.getDrawable(this.getContext(), ControlButtonsView.NOTHING_IMAGE));

            /*initialization of drivingWheel*/
            drivingWheel =new CardView(getContext());
            drivingWheel.setRotationX((float) Math.toDegrees(Math.PI/10));
            drivingWheel.setBackground(ContextCompat.getDrawable(getContext(), DRIVING_WHEEL));
            drivingWheel.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),DRIVING_WHEEL_TINT)));
            ViewGroup.LayoutParams drivingWheelParams=new RelativeLayout.LayoutParams(getLayoutParams().width,getLayoutParams().height);
            drivingWheel.setLayoutParams(drivingWheelParams);
            drivingWheel.setX(getLayoutParams().width/2f-drivingWheelParams.width/2f);
            drivingWheel.setY(getLayoutParams().height/2f-drivingWheelParams.height/2f);
            addView(drivingWheel);

            /* initialize,define driving wheels axles*/
            axle=new ImageView(getContext());
            axle.setRotationY(60);
            axle.setRotationX(10);
            axle.setBackground(ContextCompat.getDrawable(getContext(),WHEEL_AXLE));
            axle.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),AXLE_TINT)));
            //total distance of the upper half of the circle = half of the width of the view = half the circumference of the circle
            // (because we wanna get the exact width of the axle after a rotation by some degree points in the +ve direction of x-axis
            //general formula = length covered by an angle in unit circle = perimeter of the circle(circle frame length) * (theta/360 or the scaleFactor)
            float radius=drivingWheel.getLayoutParams().width/2f;
            float circumference= (float) (2*Math.PI*radius);
            float scaleFactor=(axle.getRotationX()/360);
            float lengthCoveredByRotationAngle=circumference*scaleFactor;
            int strokeWidth=20;
            //adding the values that have been cut off by the rotationX back to the width of the view
            ViewGroup.LayoutParams axleParams=new RelativeLayout.LayoutParams((int) (getLayoutParams().width/2f+lengthCoveredByRotationAngle)+strokeWidth
                    ,drivingWheelParams.height/5);
            axle.setLayoutParams(axleParams);
            //before the zero by #lengthCoveredByRotationAngle/6
            axle.setX(-lengthCoveredByRotationAngle/6-15);
            axle.setY(drivingWheel.getLayoutParams().height/2f-axleParams.height/2f);
            drivingWheel.addView(axle);
            /*Axle2*/
            axle2=new ImageView(getContext());
            axle2.setRotationY(-60);
            axle2.setRotationX(10);
            axle2.setBackground(ContextCompat.getDrawable(getContext(), WHEEL_AXLE));
            axle2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),AXLE_TINT)));
            ViewGroup.LayoutParams axle2Params=new RelativeLayout.LayoutParams((int) (getLayoutParams().width/2+lengthCoveredByRotationAngle)+strokeWidth
                    ,drivingWheelParams.height/5);
            axle2.setLayoutParams(axle2Params);
            //observatory number, donot know why
            axle2.setX(drivingWheel.getLayoutParams().width/2f-(lengthCoveredByRotationAngle/1.2f));
            axle2.setY(drivingWheel.getLayoutParams().height/2f-axle2Params.height/2f);
            drivingWheel.addView(axle2);
            /*Axle3*/
            axle3=new ImageView(getContext());
            axle3.setBackground(ContextCompat.getDrawable(getContext(),WHEEL_AXLE));
            axle3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),AXLE_TINT)));
            ViewGroup.LayoutParams axle3Params=new RelativeLayout.LayoutParams(getLayoutParams().width,drivingWheelParams.height/5);
            axle3.setLayoutParams(axle3Params);
            axle3.setX(drivingWheel.getLayoutParams().width/2f-axle3Params.width/2f);
            /*before the height of the drivingWheel by a distance stated as the quarter of its height*/
            axle3.setY(drivingWheel.getLayoutParams().height-getLayoutParams().height/4f);
            axle3.setRotation(90);
            axle3.setRotationX(40);
            drivingWheel.addView(axle3);

            /*define the drivingWheelPads*/
            drivingPads=new CardView(getContext());
            drivingPads.setBackground(ContextCompat.getDrawable(getContext(), DRIVING_PADS));
            drivingPads.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),DRIVING_PADS_TINT)));
            ViewGroup.LayoutParams drivingPadsParams=new RelativeLayout.LayoutParams(getLayoutParams().width,getLayoutParams().height);
            drivingPads.setLayoutParams(drivingPadsParams);
            drivingPads.setX(getLayoutParams().width/2f-drivingWheelParams.width/2f);
            drivingPads.setY(getLayoutParams().height/2f-drivingWheelParams.height/2f);
            drivingPads.setRadius(drivingPadsParams.width/2f);
            drivingPads.setRotation(-35);

            /*define the hornHolder view*/
            hornHolder=new CardView(drivingWheel.getContext());
            hornHolder.setBackground(ContextCompat.getDrawable(getContext(),HORN_HOLDER));
            hornHolder.setRotationX(drivingWheel.getRotationX());
            hornHolder.setLayoutParams(new RelativeLayout.LayoutParams(drivingWheelParams.width/3, drivingWheelParams.height/3));
            hornHolder.setX(drivingWheel.getLayoutParams().width/2f-hornHolder.getLayoutParams().width/2f);
            //midpoint y-coordinate lies on 1/3 the height of the hornView
            hornHolder.setY(drivingWheel.getLayoutParams().height/2f-hornHolder.getLayoutParams().height/3f);
            hornHolder.setRadius(hornHolder.getLayoutParams().width/1.5f);
            drivingWheel.addView(hornHolder);

            /*define hornHolder image*/
            horn=new ImageView(hornHolder.getContext());
            horn.setImageDrawable(ContextCompat.getDrawable(getContext(),HORN));
            horn.setLayoutParams(new RelativeLayout.LayoutParams(hornHolder.getLayoutParams().width/2,hornHolder.getLayoutParams().height/2));
            horn.setX(hornHolder.getLayoutParams().width/2f-horn.getLayoutParams().width/2f);
            horn.setY(hornHolder.getLayoutParams().height/2f-horn.getLayoutParams().height/2f);
            hornHolder.addView(horn);

            /* the driving wheel enclosure is the top part part of the driving wheel with same color*/
            drivingWheelEnclosure=new CardView(getContext());
            drivingWheelEnclosure.setBackground(ContextCompat.getDrawable(getContext(), DRIVING_WHEEL));
            drivingWheelEnclosure.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),DRIVING_WHEEL_TINT)));
            drivingWheelEnclosure.setLayoutParams(drivingWheel.getLayoutParams());
            drivingWheelEnclosure.setX(drivingWheel.getX());
            drivingWheelEnclosure.setY(drivingWheel.getY());
            drivingWheel.addView(drivingWheelEnclosure);
            drivingWheelEnclosure.addView(drivingPads);
            if(customizeDrivingWheel!=null){
                customizeDrivingWheel.customize(drivingWheel);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xMotion;
        float yMotion;
        float xStart=0.0f;
        float yStart=0.0f;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xStart=event.getX();
                yStart=event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                final float toleranceX=this.toleranceX;
                //2 is angle scale factor
                final float angleScaleFactor=2f;
                xMotion = event.getX() - xStart;
                yMotion=event.getY()-yStart;
                float angleOfRotation = (float) Math.toDegrees(Math.atan2(yMotion, xMotion)) * angleScaleFactor;
                //creating a new origin point after the center of the view to control the rotation among +ve direction of x-axis
                if(event.getX()>xOrigin+toleranceX){
                    //Right Steering
                    ((AppCompatActivity)getContext()).runOnUiThread(()-> drivingWheel.setRotation(angleOfRotation));
                    if(onSteering!=null){
                        onSteering.steerRight((float) Math.atan2(yMotion, xMotion));
                    }
                    if(dynamicUTurn!=null){
                        dynamicUTurn.animateRight();
                    }
                    //creating a new origin point before the center of the view to control the rotation among -ve direction of x-axis
                }else if(event.getX()<xOrigin-toleranceX){
                    //Left Steering
                    ((AppCompatActivity)getContext()).runOnUiThread(()-> drivingWheel.setRotation(-angleOfRotation));
                    if(onSteering!=null){
                        onSteering.steerLeft(-(float) Math.atan2(yMotion, xMotion));
                    }
                    if(dynamicUTurn!=null){
                        dynamicUTurn.animateLeft();
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(neutralizeWhenLostFocus){
                    if ( drivingWheel.getRotation() != 0 ){
                        ((AppCompatActivity) getContext()).runOnUiThread(() -> {
                            drivingWheel.animate().setDuration(drivingWheelAnimationDuration).rotation(0);
                        });
                    }
                    if(onSteering!=null){
                        onSteering.neutralize(0);
                    }
                    if(dynamicUTurn!=null){
                        dynamicUTurn.hide();
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    public ImageView getHorn() {
        return horn;
    }

    /**
     * declares by how far the driving wheel would respond from the midpoint of the drivingWheelView
     * @apiNote  this number would by added to the xOrigin point in case of +ve x-axis rotation(clock-wise) ,
     * & would be subtracted from the xOrigin in case of -ve x-axis rotation(anti-clockwise).
     * @param toleranceX the float number of the x tolerance , the default value is 50.
     */
    public void setToleranceX(float toleranceX) {
        this.toleranceX = toleranceX;
    }

    public void setDrivingWheelAnimationDuration(long drivingWheelAnimationDuration) {
        this.drivingWheelAnimationDuration = drivingWheelAnimationDuration;
    }

    public int getDRIVING_WHEEL() {
        return DRIVING_WHEEL;
    }

    public void setDRIVING_WHEEL(int DRIVING_WHEEL) {
        this.DRIVING_WHEEL = DRIVING_WHEEL;
    }

    public int getDRIVING_WHEEL_TINT() {
        return DRIVING_WHEEL_TINT;
    }

    public void setDRIVING_WHEEL_TINT(int DRIVING_WHEEL_TINT) {
        this.DRIVING_WHEEL_TINT = DRIVING_WHEEL_TINT;
    }

    public int getWHEEL_AXLE() {
        return WHEEL_AXLE;
    }

    public void setWHEEL_AXLE(int WHEEL_AXLE) {
        this.WHEEL_AXLE = WHEEL_AXLE;
    }

    public int getDRIVING_PADS() {
        return DRIVING_PADS;
    }

    public void setDRIVING_PADS(int DRIVING_PADS) {
        this.DRIVING_PADS = DRIVING_PADS;
    }

    public int getHORN_HOLDER() {
        return HORN_HOLDER;
    }

    public void setHORN_HOLDER(int HORN_HOLDER) {
        this.HORN_HOLDER = HORN_HOLDER;
    }

    public int getHORN() {
        return HORN;
    }

    public void setHORN(int HORN) {
        this.HORN = HORN;
    }

    public void setAXLE_TINT(int AXLE_TINT) {
        this.AXLE_TINT = AXLE_TINT;
    }

    public int getAXLE_TINT() {
        return AXLE_TINT;
    }

    public void setDRIVING_PADS_TINT(int DRIVING_PADS_TINT) {
        this.DRIVING_PADS_TINT = DRIVING_PADS_TINT;
    }

    public int getDRIVING_PADS_TINT() {
        return DRIVING_PADS_TINT;
    }

    public CardView getDrivingWheel() {
        return drivingWheel;
    }

    public CardView getHornHolder() {
        return hornHolder;
    }

    public ImageView getAxle() {
        return axle;
    }

    public ImageView getAxle2() {
        return axle2;
    }

    public ImageView getAxle3() {
        return axle3;
    }

    public CardView getDrivingPads() {
        return drivingPads;
    }

    public CardView getDrivingWheelEnclosure() {
        return drivingWheelEnclosure;
    }

    public long getDrivingWheelAnimationDuration() {
        return drivingWheelAnimationDuration;
    }
    public interface OnSteering{
        void steerRight(float angle);
        void steerLeft(float angle);
        void neutralize(float angle);
    }

    public void setOnSteering(OnSteering onSteering) {
         this.onSteering = onSteering;
    }
    public interface CustomizeDrivingWheel{
         void customize(CardView parentView);
    }

    public void setCustomizeDrivingWheel(CustomizeDrivingWheel customizeDrivingWheel) {
        this.customizeDrivingWheel = customizeDrivingWheel;
    }

    public void setNeutralizeWhenLostFocus(boolean neutralizeWhenLostFocus) {
        this.neutralizeWhenLostFocus = neutralizeWhenLostFocus;
    }

    public boolean isNeutralizeWhenLostFocus() {
        return neutralizeWhenLostFocus;
    }
    public interface DynamicUTurn{
        void animateRight();
        void animateLeft();
        void hide();
    }

    public void setDynamicUTurn(DynamicUTurn dynamicUTurn) {
        this.dynamicUTurn = dynamicUTurn;
    }
}
