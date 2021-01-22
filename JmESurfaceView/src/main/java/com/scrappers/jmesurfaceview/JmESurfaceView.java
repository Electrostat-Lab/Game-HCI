package com.scrappers.jmesurfaceview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.JoyInput;
import com.jme3.input.android.AndroidSensorJoyInput;
import com.jme3.system.AppSettings;
import com.jme3.system.SystemListener;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.system.android.OGLESContext;
import com.scrappers.jmesurfaceview.Dialog.OptionPane;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

/**
 * @author Pavly Gerges aka @pavl_g (A founder of Scrappers-glitch).
 * A CardView Class Holder that holds a #{{@link GLSurfaceView}} using #{{@link OGLESContext}} as a renderer to render
 * a JME game on an android view for custom xmL designs.
 */
public class JmESurfaceView extends RelativeLayout implements SystemListener {

    private SimpleApplication simpleApplication;
    protected String audioRendererType = AppSettings.ANDROID_OPENAL_SOFT;
    private AppSettings appSettings;
    private AppCompatActivity appCompatActivity;
    private int eglBitsPerPixel = 24;
    private int eglAlphaBits = 0;
    private int eglDepthBits = 16;
    private int eglSamples = 0;
    private int eglStencilBits = 0;
    private int frameRate = -1;
    private boolean emulateKeyBoard=true;
    private boolean emulateMouse=true;
    private boolean useJoyStickEvents=true;
    private ProgressBar progressBar;
    private boolean isGLThreadPaused;

    public JmESurfaceView(@NonNull Context context) {
        super(context);
    }

    public JmESurfaceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JmESurfaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setJMEGame(SimpleApplication simpleApplication,AppCompatActivity appCompatActivity){
        this.simpleApplication=simpleApplication;
        this.appCompatActivity=appCompatActivity;
        showProgress();
    }
    public void showErrorDialog(String errorMessage){
        OptionPane optionPane=new OptionPane(appCompatActivity);
        optionPane.showDialog(R.layout.dialog_exception, Gravity.CENTER);
        optionPane.getAlertDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this.getContext(),R.drawable.dialog_exception_background));
        EditText errorContainer=optionPane.getInflater().findViewById(R.id.errorText);
        errorContainer.setText(errorMessage);

        optionPane.getInflater().findViewById(R.id.closeApp).setOnClickListener(view -> {
           optionPane.getAlertDialog().dismiss();
           simpleApplication.stop(isGLThreadPaused());
           simpleApplication.destroy();
           appCompatActivity.finish();
        });
        optionPane.getInflater().findViewById(R.id.ignoreError).setOnClickListener(view -> optionPane.getAlertDialog().dismiss());
    }
    private void showProgress(){
        progressBar=new ProgressBar(this.getContext());
        DisplayMetrics displayMetrics=new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        progressBar.setLayoutParams(new LayoutParams(displayMetrics.widthPixels/4,displayMetrics.widthPixels/4));

        progressBar.setX((float) (displayMetrics.widthPixels/2 - progressBar.getLayoutParams().width/2));
        progressBar.setY((float)(displayMetrics.heightPixels/2 - progressBar.getLayoutParams().height/2));
        this.addView(progressBar,0);
    }
    private void hideProgress(){
        this.removeView(progressBar);
    }
    public void startRenderer(){
            if ( simpleApplication != null ){
                try {
                /*initialize App Settings & start the Game*/
                appSettings = new AppSettings(true);
                appSettings.setAudioRenderer(audioRendererType);
                appSettings.setResolution(this.getLayoutParams().width, this.getLayoutParams().height);
                appSettings.setAlphaBits(eglAlphaBits);
                appSettings.setDepthBits(eglDepthBits);
                appSettings.setSamples(eglSamples);
                appSettings.setStencilBits(eglStencilBits);
                appSettings.setBitsPerPixel(eglBitsPerPixel);
                appSettings.setEmulateKeyboard(emulateKeyBoard);
                appSettings.setEmulateMouse(emulateMouse);
                appSettings.setUseJoysticks(useJoyStickEvents);
                simpleApplication.setSettings(appSettings);
                /*start jme game context*/
                simpleApplication.start();
                /*attach the game to JmE OpenGL.Renderer context */
                OGLESContext oglesContext = (OGLESContext) simpleApplication.getContext();
                /*create a glSurfaceView that will hold the renderer thread*/
                GLSurfaceView glSurfaceView = oglesContext.createView(this.getContext());
                /*set the current view as the system engine thread view for future uses*/
                JmeAndroidSystem.setView(this);
                /*set JME system Listener to initialize game , update , requestClose & destroy on closure*/
                oglesContext.setSystemListener(this);
                /* set the glSurfaceView to fit the widget */
                glSurfaceView.setLayoutParams(new LayoutParams(this.getLayoutParams().width, this.getLayoutParams().height));
                /* add the openGL view to the cardView/FrameLayout */
                this.addView(glSurfaceView, 1);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void initialize() {
        if(simpleApplication !=null){
            simpleApplication.initialize();
            hideProgress();
        }

    }

    @Override
    public void reshape(int width, int height) {
        if(simpleApplication !=null){
            simpleApplication.reshape(width, height);
        }
    }

    @Override
    public void update() {
        if(simpleApplication !=null){
            try {
                simpleApplication.update();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestClose(boolean esc) {
        if(simpleApplication !=null){
            simpleApplication.requestClose(esc);
        }
    }

    @Override
    public void gainFocus() {
        if (simpleApplication != null) {
            /*resume the audio*/
            AudioRenderer audioRenderer = simpleApplication.getAudioRenderer();
            if (audioRenderer != null) {
                audioRenderer.resumeAll();
            }
            /*resume the sensors (aka joysticks)*/
            if (simpleApplication.getContext() != null) {
                JoyInput joyInput = simpleApplication.getContext().getJoyInput();
                if (joyInput != null) {
                    if (joyInput instanceof AndroidSensorJoyInput ) {
                        AndroidSensorJoyInput androidJoyInput = (AndroidSensorJoyInput) joyInput;
                        androidJoyInput.resumeSensors();
                    }
                }
                simpleApplication.gainFocus();
            }
        }
        setGLThreadPaused(false);
    }

    @Override
    public void loseFocus() {
        if (simpleApplication != null) {
            /*pause the audio*/
            simpleApplication.loseFocus();
            AudioRenderer audioRenderer = simpleApplication.getAudioRenderer();
            if (audioRenderer != null) {
                audioRenderer.pauseAll();
            }
            /*pause the sensors (aka joysticks)*/
            if (simpleApplication.getContext() != null) {
                JoyInput joyInput = simpleApplication.getContext().getJoyInput();
                if (joyInput != null) {
                    if (joyInput instanceof AndroidSensorJoyInput) {
                        AndroidSensorJoyInput androidJoyInput = (AndroidSensorJoyInput) joyInput;
                        androidJoyInput.pauseSensors();
                    }
                }
            }
        }
        setGLThreadPaused(true);
    }

    @Override
    public void handleError(String errorMsg, Throwable t) {
        System.out.println(errorMsg);
    }

    @Override
    public void destroy() {
        if (simpleApplication != null) {
            simpleApplication.stop(isGLThreadPaused());
            simpleApplication.destroy();
        }
    }

    public SimpleApplication getSimpleApplication() {
        return simpleApplication;
    }

    public void setSimpleApplication(SimpleApplication simpleApplication) {
        this.simpleApplication = simpleApplication;
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    public int getEglBitsPerPixel() {
        return eglBitsPerPixel;
    }

    public void setEglBitsPerPixel(int eglBitsPerPixel) {
        this.eglBitsPerPixel = eglBitsPerPixel;
    }

    public int getEglAlphaBits() {
        return eglAlphaBits;
    }

    public void setEglAlphaBits(int eglAlphaBits) {
        this.eglAlphaBits = eglAlphaBits;
    }

    public int getEglDepthBits() {
        return eglDepthBits;
    }

    public void setEglDepthBits(int eglDepthBits) {
        this.eglDepthBits = eglDepthBits;
    }

    public int getEglSamples() {
        return eglSamples;
    }

    public void setEglSamples(int eglSamples) {
        this.eglSamples = eglSamples;
    }

    public int getEglStencilBits() {
        return eglStencilBits;
    }

    public void setEglStencilBits(int eglStencilBits) {
        this.eglStencilBits = eglStencilBits;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public String getAudioRendererType() {
        return audioRendererType;
    }

    public void setAudioRendererType(String audioRendererType) {
        this.audioRendererType = audioRendererType;
    }

    public boolean isEmulateKeyBoard() {
        return emulateKeyBoard;
    }

    public void setEmulateKeyBoard(boolean emulateKeyBoard) {
        this.emulateKeyBoard = emulateKeyBoard;
    }

    public boolean isEmulateMouse() {
        return emulateMouse;
    }

    public void setEmulateMouse(boolean emulateMouse) {
        this.emulateMouse = emulateMouse;
    }

    public boolean isUseJoyStickEvents() {
        return useJoyStickEvents;
    }

    public void setUseJoyStickEvents(boolean useJoyStickEvents) {
        this.useJoyStickEvents = useJoyStickEvents;
    }

    public boolean isGLThreadPaused() {
        return isGLThreadPaused;
    }

    public void setGLThreadPaused(boolean GLThreadPaused) {
        isGLThreadPaused = GLThreadPaused;
    }
}