package com.scrappers.superiorExtendedEngine.gamePad;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.FastMath;
import com.scrappers.GamePad.R;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

/**
 * A class that creates a Speedometer as UI component or android view that can be used as an xml tag.
 * How to use inside your main_activity.xml layout file do :
 *     <com.scrappers.superiorExtendedEngine.gamePad.Speedometer
 *         android:id="@+id/speedometer"
 *         android:layout_width="140dp"
 *         android:layout_height="140dp"
 *         android:layout_gravity="start"
 *         />
 * as a basic setup in your activity.java :
 *         Speedometer speedometer=appCompatActivity.findViewById(R.id.speedometer);
 *         speedometer.initialize();
 *         speedometer.getSpeedometerDrawable().setStroke(3, ContextCompat.getColor(appCompatActivity,R.color.gold));
 *         gameStick.createSpeedometerLink(speedometer,JmeGame.this,vehicle,1f);
 * @apiNote you can use this UI component inside a jme game {@link com.jme3.app.SimpleApplication} directly w/o having  a runtime exception.
 * @implNote  The class is automatically linked to the {@link InertialListener} when {@link GameStickView#createSpeedometerLink} is called against a {@link GameStickView} instance.
 * @author pavl_g
 * @see Speedometer.InertialListener,BaseAppState,GameStickView#createSpeedometerLink(Speedometer, SimpleApplication, VehicleControl, float).
 */
public class Speedometer extends CardView {
    private static final int SPEEDOMETER_BACKGROUND =R.drawable.speedometer;
    private static final int SPEEDOMETER_IMPOSTOR_GRADIENT =R.drawable.circular_progress;
    private static final int DIGITAL_SCREEN_BACKGROUND=R.drawable.digital_background;
    private static final int CURSOR_BACKGROUND=R.drawable.cursor_base;
    private static final int CURSOR_BLADE_BACKGROUND=R.drawable.cursor_blade;
    private static final int PROGRESS_RANGE=120;
    public static final int PROGRESS_MAX=100;
    private TextView digitalScreen;
    private ProgressBar speedImpostor;
    private CardView speedCursorBase;
    private CardView cursorHolder;
    private ImageView cursorBlade;
    private final ArrayList<ImageView> impostorIndicators=new ArrayList<>();
    private boolean impostorIndicatorEnabled=true;
    private CustomizeSpeedometer customizeSpeedometer;
    private float xOrigin=0.0f;
    private float yOrigin=0.0f;
    private float radius=0.0f;
    /* angles using the pattern = PI * (1/Math.pow(2,n*2)) where n=n*2 starting from n=1; , when angles approaches zero(FROM EITHER LEFT OR RIGHT SIDES)
    , flipping the angle sign would render the angle on the bottom as if it was a mirror*/
    private final double[] angles=new double[]{-Math.PI/2d,-Math.PI/4d,-Math.PI/16d, Math.PI/8d,
                                                -(Math.PI-Math.PI/4d),-(Math.PI-Math.PI/16d),(Math.PI-Math.PI/8d)};

    public Speedometer(Context context) {
        super(context);
    }

    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Speedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * initializes the speedometer instance with a speedometer impostor , digital screen , cursor .
     */
    public void initialize(){
        ((Activity)getContext()).runOnUiThread(()->{
            /*doing the styles*/
            speedImpostor =new ProgressBar(getContext(),null, R.style.Widget_AppCompat_ProgressBar_Horizontal);
            speedImpostor.setLayoutParams(getLayoutParams());
            speedImpostor.setProgressDrawable(ContextCompat.getDrawable(getContext(), SPEEDOMETER_IMPOSTOR_GRADIENT));
            speedImpostor.setBackground(ContextCompat.getDrawable(getContext(), SPEEDOMETER_BACKGROUND));
            /*doing the data related part*/
            speedImpostor.setIndeterminate(false);
            speedImpostor.setMax(PROGRESS_RANGE);
            speedImpostor.setProgress(PROGRESS_MAX);


            RelativeLayout speedImpostorHolder=new RelativeLayout(getContext());
            speedImpostorHolder.setLayoutParams(getLayoutParams());
            speedImpostorHolder.setBackground(ContextCompat.getDrawable(getContext(),R.color.transparent));

            digitalScreen=new TextView(getContext());
            digitalScreen.setText(String.valueOf(0));
            digitalScreen.setTextColor(ContextCompat.getColor(getContext(),R.color.greenDegree2));
            digitalScreen.setLayoutParams(new LayoutParams(getLayoutParams().width/4,getLayoutParams().height/4));
            digitalScreen.setTextSize(digitalScreen.getLayoutParams().width/5f);
            digitalScreen.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            digitalScreen.setBackground(ContextCompat.getDrawable(getContext(),DIGITAL_SCREEN_BACKGROUND));
            digitalScreen.setX(getLayoutParams().width/2f-digitalScreen.getLayoutParams().width/2f);
            digitalScreen.setY(getLayoutParams().height/2f-digitalScreen.getLayoutParams().height/2f);

            setElevation(10);
            setBackground(ContextCompat.getDrawable(getContext(),R.drawable.nothing));


            cursorHolder=new CardView(getContext());
            cursorHolder.setLayoutParams(getLayoutParams());
            cursorHolder.setBackground(ContextCompat.getDrawable(getContext(),R.color.transparent));
            cursorHolder.setRotation(0);

            speedCursorBase=new CardView(getContext());
            speedCursorBase.setLayoutParams(new RelativeLayout.LayoutParams(cursorHolder.getLayoutParams().height/4,cursorHolder.getLayoutParams().width/30));
            speedCursorBase.setX(cursorHolder.getLayoutParams().width/2f -speedCursorBase.getLayoutParams().width);
            speedCursorBase.setY(getLayoutParams().height/2f+digitalScreen.getLayoutParams().height/2f);
            speedCursorBase.setRotation(-40);
            speedCursorBase.setBackground(ContextCompat.getDrawable(getContext(),CURSOR_BACKGROUND));

            cursorBlade=new ImageView(getContext());
            cursorBlade.setLayoutParams(new RelativeLayout.LayoutParams(speedCursorBase.getLayoutParams().width/4,speedCursorBase.getLayoutParams().height));
            cursorBlade.setBackground(ContextCompat.getDrawable(getContext(),CURSOR_BLADE_BACKGROUND));
            cursorBlade.setX(0);
            cursorBlade.setY(0);
            speedCursorBase.addView(cursorBlade);

            cursorHolder.addView(speedCursorBase);

            speedImpostorHolder.addView(speedImpostor);
            addView(speedImpostorHolder);
            addView(cursorHolder);
            addView(digitalScreen);

            if(isImpostorIndicatorEnabled()){
                //let's do the impostor degrees
                xOrigin = getLayoutParams().width / 2f;
                yOrigin = getLayoutParams().height / 2f;
                //for oval shaped objects
                radius = Math.max(xOrigin, yOrigin);
                for (double angle : angles) {
                    ImageView impostorIndicator = new ImageView(getContext());
                    impostorIndicator.setBackground(ContextCompat.getDrawable(getContext(), CURSOR_BACKGROUND));
                    impostorIndicator.setLayoutParams(new RelativeLayout.LayoutParams(speedCursorBase.getLayoutParams().width / 2, speedCursorBase.getLayoutParams().height / 2));
                    impostorIndicator.setRotation((float) Math.toDegrees(angle));
                    float xOriginPart = xOrigin - impostorIndicator.getLayoutParams().width / 2f;
                    float yOriginPart = yOrigin - impostorIndicator.getLayoutParams().height / 2f;
                    impostorIndicator.setX((float) (xOriginPart + Math.cos(angle) * radius / 1.5f));
                    impostorIndicator.setY((float) (yOriginPart + Math.sin(angle) * radius / 1.5f));
                    impostorIndicators.add(impostorIndicator);
                    speedImpostorHolder.addView(impostorIndicator);
                }
            }
            if(customizeSpeedometer!=null){
                customizeSpeedometer.customize(Speedometer.this);
            }
        });
    }

    public boolean isImpostorIndicatorEnabled() {
        return impostorIndicatorEnabled;
    }

    public void setImpostorIndicatorEnabled(boolean impostorIndicatorEnabled) {
        this.impostorIndicatorEnabled = impostorIndicatorEnabled;
    }

    public ArrayList<ImageView> getImpostorIndicators() {
        return impostorIndicators;
    }

    /**
     * gets the speedometer drawable xml for customizations purposes.
     * @return a gradient drawable representing the xml file for the speedometer background.
     */
    public GradientDrawable getSpeedometerDrawable(){
        return (GradientDrawable) ContextCompat.getDrawable(getContext(), SPEEDOMETER_BACKGROUND);
    }

    /**
     * gets the speedometer impostor drawable xml for customizations purposes.
     * @return a layer-list drawable representing the xml file for the speedometer impostor.
     */
    public LayerDrawable getSpeedometerImpostorDrawable(){
        return (LayerDrawable) ContextCompat.getDrawable(getContext(), SPEEDOMETER_IMPOSTOR_GRADIENT);
    }

    /**
     * gets the speedometer digitalScreen(that displays the numbers) drawable xml for customizations purposes.
     * @return a gradient drawable representing the xml file for the speedometer digitalScreen.
     */
    public GradientDrawable getDigitalScreenDrawable(){
        return (GradientDrawable) ContextCompat.getDrawable(getContext(),DIGITAL_SCREEN_BACKGROUND);
    }

    /**
     * gets the speedometer progressBar instance.
     * @return the speedometer impostor representing speedometer analog.
     */
    public ProgressBar getSpeedImpostor() {
        return speedImpostor;
    }

    /**
     * gets the digital screen view instance/
     * @return the text view displaying numbers.
     */
    public TextView getDigitalScreen() {
        return digitalScreen;
    }

    /**
     * gets the speedometerCursor view.
     * @return a cardView instance representing the speedometerCursor.
     */
    public CardView getSpeedCursorBase() {
        return speedCursorBase;
    }
    /**
     * gets the speedometerCursor holder view.
     * @return a cardView instance representing the speedometerCursor holder view.
     */
    public CardView getCursorHolder() {
        return cursorHolder;
    }

    /**
     * gets the speedometer cursor blade view.
     * @return an imageView instance representing the cursor blade view.
     */
    public ImageView getCursorBlade() {
        return cursorBlade;
    }

    public interface CustomizeSpeedometer{
        void customize(Speedometer speedometer);
    }

    /**
     * makes sure that the enclosed block of code would engaged inside the {@link Speedometer#initialize()} method for customizing the speedometer when extending this class.
     * @param customizeSpeedometer the new instance of {@link CustomizeSpeedometer} interface.
     */
    public void setCustomizeSpeedometer(CustomizeSpeedometer customizeSpeedometer) {
        this.customizeSpeedometer = customizeSpeedometer;
    }

    /**
     * get the speedometer impostor gradient.
     * @return id referring to the it.
     */
    public static int getSpeedometerImpostorGradient() {
        return SPEEDOMETER_IMPOSTOR_GRADIENT;
    }

    public static int getSpeedometerBackground() {
        return SPEEDOMETER_BACKGROUND;
    }

    public static int getDigitalScreenBackground() {
        return DIGITAL_SCREEN_BACKGROUND;
    }

    public static int getCursorBackground() {
        return CURSOR_BACKGROUND;
    }

    public static int getCursorBladeBackground() {
        return CURSOR_BLADE_BACKGROUND;
    }

    /**
     * @apiNote Internal use only.
     */
    public static class InertialListener extends BaseAppState {
        private final Speedometer speedometer;
        private final AppCompatActivity appCompatActivity;
        private final VehicleControl vehicleControl;
        private float scaleFactor =2;
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

        public void setScaleFactor(float scaleFactor) {
            this.scaleFactor = scaleFactor;
        }

        public float getScaleFactor() {
            return scaleFactor;
        }

        @Override
        public void update(float tpf) {
            appCompatActivity.runOnUiThread(()-> {
                if(Math.abs((int) vehicleControl.getLinearVelocity().getZ() * scaleFactor) < Speedometer.PROGRESS_MAX){
                    speedometer.getDigitalScreen().setText(String.valueOf(new int[]{(int) Math.abs(vehicleControl.getLinearVelocity().getZ() * scaleFactor)}[0]));
                    speedometer.getCursorHolder().setRotation(FastMath.interpolateLinear(scaleFactor,FastMath.abs(vehicleControl.getLinearVelocity().getZ())
                            ,2*FastMath.abs(vehicleControl.getLinearVelocity().getZ())));
                }
                if(speedometer.getCursorHolder().getRotation()<240){
                    speedometer.getCursorHolder().setRotation(FastMath.interpolateLinear(scaleFactor,FastMath.abs(vehicleControl.getLinearVelocity().getZ()),
                            2*FastMath.abs(vehicleControl.getLinearVelocity().getZ())));
                }
            });
        }
    }
}
