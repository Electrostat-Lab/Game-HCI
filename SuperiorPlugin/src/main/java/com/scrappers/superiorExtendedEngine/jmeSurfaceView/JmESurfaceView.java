package com.scrappers.superiorExtendedEngine.jmeSurfaceView;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
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
import com.scrappers.GamePad.R;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.Dialog.OptionPane;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * @author Pavly Gerges aka @pavl_g (A founder of Scrappers-glitch).
 * A CardView Class Holder that holds a #{{@link GLSurfaceView}} using #{{@link OGLESContext}} as a renderer to render
 * a JME game on an android view for custom xmL designs.
 *
 * @apiNote the main idea of #{@link JmESurfaceView} class is to start a jMonkeyEngine application in a SystemListener#{@link SystemListener} context in a GL thread ,
 * then the GLSurfaceView holding the GL thread joins the UI thread with a delay of user's choice using a #{@link Handler} , during the delay , the game runs normally in the GL thread(but without coming up on the UI)
 * and the user has the ability to handle a couple of actions asynchronously as displaying a progress bar #{@link com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen.ProgressEntity} or
 * an image as a splash screen #{@link com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen.ImageEntity} or even play a preface game music of choice #{@link com.scrappers.superiorExtendedEngine.gamePad.GamePadBody.GamePadSoundEffects}.
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
    private boolean ignoreAssertions;
    private final Handler handler=new Handler();
    private GLSurfaceView glSurfaceView;
    private OnRendererCompleted onRendererCompleted;
    private AtomicInteger synthesizedTime=new AtomicInteger();


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
        this.addView(progressBar);
    }
    private void hideProgress(){
        this.removeView(progressBar);
    }
    public void startRenderer(int delayMillis) {
        if ( simpleApplication != null ){
            try {
                /*initialize App Settings & start the Game*/
                appSettings = new AppSettings(true);
                appSettings.setAudioRenderer(audioRendererType);
                appSettings.setResolution(JmESurfaceView.this.getLayoutParams().width, JmESurfaceView.this.getLayoutParams().height);
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
                glSurfaceView = oglesContext.createView(JmESurfaceView.this.getContext());
                /*set the current view as the system engine thread view for future uses*/
                JmeAndroidSystem.setView(JmESurfaceView.this);
                /*set JME system Listener to initialize game , update , requestClose & destroy on closure*/
                oglesContext.setSystemListener(JmESurfaceView.this);
                /* set the glSurfaceView to fit the widget */
                glSurfaceView.setLayoutParams(new LayoutParams(JmESurfaceView.this.getLayoutParams().width, JmESurfaceView.this.getLayoutParams().height));
                /*post delay the renderer join into the UI thread*/
                handler.postDelayed(new RendererThread(),delayMillis);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private class RendererThread implements Runnable{

        @Override
        public void run() {
            JmESurfaceView.this.addView(glSurfaceView);
        }
    }

    @Override
    public void initialize() {
        if(simpleApplication !=null){
            simpleApplication.enqueue(() -> simpleApplication.initialize());
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
        if(simpleApplication ==null){
            return;
        }
        if(!isIgnoreAssertions()){
            if ( glSurfaceView != null ){
                simpleApplication.update();
            }
        }else{
            try {
                if ( glSurfaceView != null ){
                    simpleApplication.update();
                }
            } catch (AssertionError e) {
                e.printStackTrace();
            }
        }
        int timeToPlay=synthesizedTime.addAndGet(1);
        if(timeToPlay==20){
            appCompatActivity.runOnUiThread(() -> {
                if ( onRendererCompleted != null ){
                    onRendererCompleted.onRenderCompletion(simpleApplication);
                }
            });
        }

    }

    @Override
    public void requestClose(boolean esc) {
        if(simpleApplication !=null){
            simpleApplication.enqueue(() -> simpleApplication.requestClose(esc));
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

    public void setIgnoreAssertions(boolean ignoreAssertions) {
        this.ignoreAssertions = ignoreAssertions;
    }

    public boolean isIgnoreAssertions() {
        return ignoreAssertions;
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
    public interface OnRendererCompleted{
        void onRenderCompletion(SimpleApplication application);
    }

    public void setOnRendererCompleted(OnRendererCompleted onRendererCompleted) {
        this.onRendererCompleted = onRendererCompleted;
    }
}