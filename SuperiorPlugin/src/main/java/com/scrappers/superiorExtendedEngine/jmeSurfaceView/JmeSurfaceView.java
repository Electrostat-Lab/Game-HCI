package com.scrappers.superiorExtendedEngine.jmeSurfaceView;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.JoyInput;
import com.jme3.input.android.AndroidSensorJoyInput;
import com.jme3.system.AppSettings;
import com.jme3.system.SystemListener;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.system.android.OGLESContext;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen.SplashScreen;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Pavly Gerges aka @pavl_g (A founder of Scrappers-glitch).
 * A CardView Class Holder that holds a #{{@link GLSurfaceView}} using #{{@link OGLESContext}} as a renderer to render
 * a JME game on an android view for custom xmL designs.
 *
 * @apiNote the main idea of #{@link JmeSurfaceView} class is to start a jMonkeyEngine application in a SystemListener#{@link SystemListener} context in a GL thread ,
 * then the GLSurfaceView holding the GL thread joins the UI thread with a delay of user's choice using a #{@link Handler} , during the delay , the game runs normally in the GL thread(but without coming up on the UI)
 * and the user has the ability to handle a couple of actions asynchronously as displaying a progress bar #{@link SplashScreen} or
 * an image or even play a preface game music of choice #{@link com.scrappers.superiorExtendedEngine.gamePad.ControlButtonsView.GamePadSoundEffects}.
 */
public class JmeSurfaceView extends RelativeLayout implements SystemListener {

    private SimpleApplication simpleApplication;
    protected String audioRendererType = AppSettings.ANDROID_OPENAL_SOFT;
    private static final Logger jmeSurfaceViewLogger=Logger.getLogger("JmeSurfaceView");
    private AppSettings appSettings;
    private int eglBitsPerPixel = 24;
    private int eglAlphaBits = 0;
    private int eglDepthBits = 16;
    private int eglSamples = 0;
    private int eglStencilBits = 0;
    private int frameRate = -1;
    private boolean emulateKeyBoard=true;
    private boolean emulateMouse=true;
    private boolean useJoyStickEvents=true;
    private boolean isGLThreadPaused;
    private boolean ignoreAssertions;
    private final Handler handler=new Handler();
    private GLSurfaceView glSurfaceView;
    private OnRendererCompleted onRendererCompleted;
    private final AtomicInteger synthesizedTime=new AtomicInteger();
    private OnExceptionThrown onExceptionThrown;
    private int delayMillis=0;
    private static final int TOLERANCE_TIMER=100;

    public JmeSurfaceView(@NonNull Context context) {
        super(context);
    }
    public JmeSurfaceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JmeSurfaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * starts the jmeRenderer on a GlSurfaceView attached to a RelativeLayout
     * @param delayMillis delay of the appearance of jme game on the screen , this doesn't delay the renderer though.
     */
    public synchronized void startRenderer(int delayMillis) {
        this.delayMillis=delayMillis;
        if ( simpleApplication != null ){
            try {
                /*initialize App Settings & start the Game*/
                appSettings = new AppSettings(true);
                appSettings.setAudioRenderer(audioRendererType);
                appSettings.setResolution(JmeSurfaceView.this.getLayoutParams().width, JmeSurfaceView.this.getLayoutParams().height);
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
                glSurfaceView = oglesContext.createView(JmeSurfaceView.this.getContext());
                /*set the current view as the system engine thread view for future uses*/
                JmeAndroidSystem.setView(JmeSurfaceView.this);
                /*set JME system Listener to initialize game , update , requestClose & destroy on closure*/
                oglesContext.setSystemListener(JmeSurfaceView.this);
                /* set the glSurfaceView to fit the widget */
                glSurfaceView.setLayoutParams(new LayoutParams(JmeSurfaceView.this.getLayoutParams().width, JmeSurfaceView.this.getLayoutParams().height));
                /*post delay the renderer join into the UI thread*/
                handler.postDelayed(new RendererThread(),delayMillis);
            } catch (Exception e) {
                if( onExceptionThrown !=null){
                    onExceptionThrown.Throw(e);
                    jmeSurfaceViewLogger.log(Level.WARNING,e.getMessage());
                }
            }
        }
    }

    /**
     * Custom thread that delays the appearance of the display of jme game on the screen for the sake of initial frame pacing & splash screens.
     */
    private class RendererThread implements Runnable{
        @Override
        public synchronized void run() {
            /*jme Renderer joins the UIThread at that point*/
            JmeSurfaceView.this.addView(glSurfaceView);
            jmeSurfaceViewLogger.log(Level.CONFIG,"JmeSurfaceView's joined the UI thread.......");
        }
    }

    @Override
    public synchronized void initialize() {
        if(simpleApplication !=null){
            simpleApplication.enqueue(() -> simpleApplication.initialize());
            /*log for display*/
            jmeSurfaceViewLogger.log(Level.INFO,"JmeGame started in GLThread Asynchronously......");
        }

    }

    @Override
    public void reshape(int width, int height) {
        if(simpleApplication !=null){
            simpleApplication.reshape(width, height);
        }
    }

    @Override
    public synchronized void update() {
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
                if( onExceptionThrown !=null){
                    onExceptionThrown.Throw(e);
                }
            }
        }
        int timeToPlay=synthesizedTime.addAndGet(1);
        if(timeToPlay==(delayMillis>100?(delayMillis-TOLERANCE_TIMER) :delayMillis)){
            ((AppCompatActivity)getContext()).runOnUiThread(() -> {
                if ( onRendererCompleted != null ){
                    jmeSurfaceViewLogger.log(Level.INFO,"SplashScreen Dismissed , User Delay completed with 0 errors.......");
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
    /**
     * ignores assertions.
     * @param ignoreAssertions true if you want to ignore AssertionErrors.
     */
    public void setIgnoreAssertions(boolean ignoreAssertions) {
        this.ignoreAssertions = ignoreAssertions;
    }
    /**
     *
     * @return true if ignored , default -> false.
     */
    public boolean isIgnoreAssertions() {
        return ignoreAssertions;
    }
    /**
     * gets the jme app instance
     * @return simpleApplication instance representing your game enclosure.
     */
    public SimpleApplication getSimpleApplication() {
        return simpleApplication;
    }
    /**
     * sets the jme game instance that will be engaged into the {@link SystemListener}.
     * @param simpleApplication your jme game instance.
     */
    public void setSimpleApplication(SimpleApplication simpleApplication) {
        this.simpleApplication = simpleApplication;
    }
    /**
     * gets the game window settings.
     * @return app settings instance.
     */
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
    public interface OnExceptionThrown{
        void Throw(Throwable e);
    }

    public void setOnExceptionThrown(OnExceptionThrown onExceptionThrown) {
        this.onExceptionThrown = onExceptionThrown;
    }
}