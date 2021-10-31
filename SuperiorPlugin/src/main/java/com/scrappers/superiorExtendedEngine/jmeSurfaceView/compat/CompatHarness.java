package com.scrappers.superiorExtendedEngine.jmeSurfaceView.compat;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.jme3.app.LegacyApplication;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.JoyInput;
import com.jme3.input.android.AndroidSensorJoyInput;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.system.SystemListener;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.system.android.OGLESContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Androidx Migrated Version of {@link com.jme3.app.AndroidHarness}.
 *
 * A factory based class pattern used to wrap an android jme app based on the new
 * androidx API, where the Android Activity along with the {@link android.view.Choreographer} gets linked to the jme life cycle through GL context thread by the help of {@link com.jme3.system.android.OGLESContext}.
 *
 * (Further more : {@link com.jme3.app.LegacyApplication} depends on {@link com.jme3.system.JmeSystemDelegate} to find the righteous Platform and gets its context to the Application, for android the context wrapper is {@link com.jme3.system.android.OGLESContext}).
 * <br/>
 *
 * Major Bug fixes since old android harness :
 *
 * <li>using a determined pattern - the abstract factory pattern.</li>
 * <li>introducing an easy way for passing jme instance either through {@link CompatHarness#getInstance()} implicitly through the Class or explicitly through a direct instance {@link CompatHarness#setAppInstance(LegacyApplication)}.</li>
 * <li>introducing a better way for calculating the screen metrics before rendering using {@link android.util.DisplayMetrics} and {@link AppCompatActivity#getDisplay()}.</li>
 * <li>added more life cycle factory methods for the api {@link CompatHarness#onRendererCompletion(LegacyApplication)}, {@link CompatHarness#onExceptionThrown(Throwable)}.</li>
 * <li>determined a way to destroy the android activity without destroying jme context and retain the jme3 rendering context whenever needed on a new activity instance, see {@link CompatHarness#bindAppState(boolean)}.</li>
 * <li>introducing a better way to store and retain jme3 instance from System delegate whenever the application is destroyed without destroying the context.</li>
 * <li>a way of customizing the activity appearance using {@link Display#switchToGameMode(Activity)}.</li>
 * <li>a way to choose if the rendered gl view would be enclosed in a layout or not based on {@link LayoutManager}.</li>
 *
 * @author pavl_g  -- based on {@link com.jme3.app.AndroidHarness}.
 */
@Singleton(1)
public abstract class CompatHarness extends AppCompatActivity implements SystemListener, TouchListener {
    protected LegacyApplication application;
    //bound state of jme app instance with the current activity instance
    protected boolean boundAppState = true;
    protected static LayoutManager layoutManager = LayoutManager.No_Layout;
    protected static Display display = Display.GAME_MODE;
    protected static Display.Screen screen = Display.Screen.LANDSCAPE;
    protected static final Logger compatLogger = Logger.getLogger(CompatHarness.class.getName());
    private boolean glThreadPaused;
    protected GLSurfaceView glSurfaceView;
    private boolean firstUpdate = true;
    private RelativeLayout layoutHolder;
    private boolean showFlyCamTouchBoundaries;

    /**
     * @deprecated use {@link CompatHarness#preInitializeGlConfig()} to pre-initialize the EGL config, NB : don't use a time utilizing task inside.
     */
    @Deprecated
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get the display Metrics
        compatLogger.log(Level.INFO, "Compat Harness starts with default configurations >>>>>>");
        //reset EGL Config
        EGLConfig.resetEGLConfig();
        //do pre-initializing setup
        Display.useSwitchMode(this);
        //initialize the layout holder
        this.layoutHolder = Display.initializeLayoutHolder(this);
        compatLogger.log(Level.INFO, "Gaming Mode's been activated >>>>>>");
        //call pre-initialize
        preInitializeGlConfig();
    }

    /**
     * @deprecated use {@link CompatHarness#onStopRenderer(LegacyApplication)}.
     */
    @Deprecated
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(application != null){
            destroy();
        }
        compatLogger.log(Level.INFO, "Harness terminates >>>>>>");
    }

    /**
     * @deprecated use {@link CompatHarness#onPauseRenderer(LegacyApplication)}.
     */
    @Deprecated
    @Override
    protected void onPause() {
        super.onPause();
        if(application != null){
            loseFocus();
            compatLogger.log(Level.INFO, "Game goes to the idle mode >>>>>>");
        }
    }

    /**
     * @deprecated don't override, use {@link CompatHarness#onStartRenderer(LegacyApplication)}.
     */
    @Deprecated
    @Override
    protected void onStart() {
        super.onStart();
        try {
            startRenderer(EGLConfig.getEglSurfaceDelay());
        } catch (IllegalAccessException | InstantiationException e) {
            compatLogger.log(Level.WARNING, e.getMessage());
            onExceptionThrown(e);
        }
    }

    /**
     * @deprecated use {@link CompatHarness#onResumeRenderer(LegacyApplication)}.
     */
    @Deprecated
    @Override
    protected void onResume() {
        super.onResume();
        if(application != null){
            gainFocus();
            compatLogger.log(Level.INFO, "Game returns from the idle mode >>>>>>");
        }
    }

    /**
     * @deprecated never use, refer to {@link CompatHarness#onStopRenderer(LegacyApplication)}.
     */
    @Deprecated
    @Override
    protected void onStop() {
        super.onStop();
        if(isAppStateBound()){
            //release the static memory
            GameState.setApplication(null);
            GameState.setOglesContext(null);
        }
        delegateAppInstanceToState();
        //clean-up android views
        layoutHolder.removeAllViews();
        compatLogger.log(Level.INFO, "Game stops, delegating the app instance to the game states >>>>>>");
    }
    private void delegateAppInstanceToState(){
        application = null;
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Display.useSwitchMode(this);
    }
    @Override
    public void onTouch(String name, TouchEvent event, float tpf) {

    }

    @Override
    public void initialize() {
        if(application == null){
            return;
        }
        application.initialize();
        compatLogger.log(Level.INFO, "Game is initializing >>>>>>");
    }

    @Override
    public void reshape(int width, int height) {
        if (application == null) {
            return;
        }
        application.reshape(width , height);
        compatLogger.log(Level.INFO, "Requested Reshaping >>>>>>");
    }

    @Override
    public void update() {
        if (application == null) {
            return;
        }
        application.update();
        //trigger the rendering completed listener on the first update
        if(firstUpdate){
            CompatHarness.this.runOnUiThread(() -> {
                compatLogger.log(Level.FINE, "Rendering Completed >>>>>>");
                onRendererCompletion(application);
            });
            firstUpdate = false;
        }
    }

    @Override
    public void requestClose(boolean esc) {
        if(application == null){
            return;
        }
        application.requestClose(esc);
    }

    @Override
    public void gainFocus() {
        if (application == null || glSurfaceView == null) {
            return;
        }
        glSurfaceView.onResume();
        onResumeRenderer(application);
        /*resume the audio*/
        final AudioRenderer audioRenderer = application.getAudioRenderer();
        if (audioRenderer != null) {
            audioRenderer.resumeAll();
        }
        /*resume the sensors (aka joysticks)*/
        if (application.getContext() != null) {
            final JoyInput joyInput = application.getContext().getJoyInput();
            if (joyInput != null) {
                if (joyInput instanceof AndroidSensorJoyInput) {
                    final AndroidSensorJoyInput androidJoyInput = (AndroidSensorJoyInput) joyInput;
                    androidJoyInput.resumeSensors();
                }
            }
            application.gainFocus();
        }
        setGlThreadPaused(false);
    }

    @Override
    public void loseFocus() {
        if(application == null || glSurfaceView == null){
            return;
        }
        glSurfaceView.onPause();
        onPauseRenderer(application);
        /*pause the audio*/
        application.loseFocus();
        final AudioRenderer audioRenderer = application.getAudioRenderer();
        if (audioRenderer != null) {
            audioRenderer.pauseAll();
        }
        /*pause the sensors (aka joysticks)*/
        if (application.getContext() != null) {
            final JoyInput joyInput = application.getContext().getJoyInput();
            if (joyInput != null) {
                if (joyInput instanceof AndroidSensorJoyInput) {
                    final AndroidSensorJoyInput androidJoyInput = (AndroidSensorJoyInput) joyInput;
                    androidJoyInput.pauseSensors();
                }
            }
        }
        setGlThreadPaused(true);
    }

    @Override
    public void handleError(String errorMsg, Throwable throwable) {
        compatLogger.log(Level.WARNING , throwable.getMessage());
        showErrorDialog(throwable , throwable.getMessage());
        onExceptionThrown(throwable);
    }

    /**
     * @deprecated use {@link CompatHarness#onStopRenderer(LegacyApplication)}.
     */
    @Deprecated
    @Override
    public void destroy() {
        if (application == null || glSurfaceView == null) {
            return;
        }
        application.stop(isGlThreadPaused());
        application.destroy();
        compatLogger.log(Level.INFO, "Game terminates >>>>>>");
        onStopRenderer(application);
    }

    protected static LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * The entry point for rendering a jme app on the current activity.
     * @param delay the user delay, to set this delay invoke a call to {@link EGLConfig#setEglSurfaceDelay(int)} inside {@link CompatHarness#preInitializeGlConfig()}.
     * @throws InstantiationException if the class has no nullary constructor (ie: has a parameterized constructor).
     * @throws IllegalAccessException if the user hasn't settled the jme instance either via {@link CompatHarness#getInstance()} or {@link CompatHarness#setAppInstance(LegacyApplication)}.
     */
    private void startRenderer(final int delay) throws IllegalAccessException, InstantiationException {
        EGLConfig.setEglSurfaceDelay(Math.max(delay, 1));
        //override the global var for the tempInstance
        this.application = getGameInstance();
        if (application != null) {
            OGLESContext oglesContext;
            //check for current game state, resume it if the state isn't null
            if(GameState.getOglesContext() == null || GameState.getApplication() == null) {
                compatLogger.log(Level.INFO, "EGLConfigs have been applied to the Application Settings >>>>>>");
                application.setSettings(EGLConfig.applyGlConfig());
                EGLConfig.getSettings().setResolution(layoutHolder.getLayoutParams().width, layoutHolder.getLayoutParams().height);
                /*start jme game context*/
                application.start();
                //call back for a life cycle class
                onStartRenderer(application);
                oglesContext = (OGLESContext) application.getContext();
                //save both the context state and the jme instance state for future use
                GameState.setApplication(application);
                GameState.setOglesContext(oglesContext);
                compatLogger.log(Level.INFO, "New Game State have been saved for later use >>>>>>");
                configureSplashScreen();
                compatLogger.log(Level.INFO, "Configuring the user splash screen >>>>>>");
            }else{
                application = GameState.getApplication();
                oglesContext = GameState.getOglesContext();
                EGLConfig.setEglSurfaceDelay((int) EGLConfig.DEFAULT_EGL_SURFACE_DELAY.getValue());
                compatLogger.log(Level.INFO, "Old Game State have been retrieved >>>>>>");
            }
            glSurfaceView = oglesContext.createView(this);
            oglesContext.setSystemListener(CompatHarness.this);
            compatLogger.log(Level.INFO, "Rendering now >>>>>>");
            //request build layout
            //build the Layout using a LayoutManager and attach the glSurfaceView along with jme to it.
            final View layout = LayoutManager.buildLayoutManager(layoutHolder, this, glSurfaceView);
            requestPostRenderingActions(layout, delay);
            compatLogger.log(Level.INFO, "Requesting layout build by the CompatHarness.LayoutManager >>>>>>");
        }
    }
    private void configureSplashScreen() {
        if(getSplashScreen() != null) {
            final View splashScreen = getSplashScreen();
            splashScreen.setId('S');
            //override the onClick listener for this child, to terminate the ancestors-children touch input relationship
            splashScreen.setOnClickListener(null);
            splashScreen.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layoutHolder.addView(splashScreen);
        }
    }
    protected void setGlThreadPaused(boolean glThreadPaused) {
        this.glThreadPaused = glThreadPaused;
    }
    protected boolean isGlThreadPaused() {
        return glThreadPaused;
    }

    private void showErrorDialog(Throwable throwable, String message){
        if(Display.isShowErrorDialog()){
            //show error dialog
        }
    }
    /**
     * Used to request build the layout manager during runtime.
     * @param layout the layout created by the layout manager {@link LayoutManager#buildLayoutManager(ViewGroup, Activity, GLSurfaceView)}.
     * @param eglDelay the egl display delay.
     * @return true if the requested action is being executed.
     */
    protected boolean requestPostRenderingActions(final View layout, final int eglDelay){
        return new Handler().postDelayed(new LayoutBuilder(layout), eglDelay);
    }

    /**
     * An alternative way of setting the game instance using a setter method.
     * Benefits : you can instantiate the class of the game yourself on the CompatHarness
     * before passing it here, especailly if it has a parameterized constructor.
     * @param app the instantiated game instance.
     */
    protected void setAppInstance(final LegacyApplication app){
        this.application = app;
    }

    /**
     * A stateful boolean used for dissecting jme app instance from the current Android activity instance.
     * Use this state to tell the CompatHarness whether or not to bind jme app instance to the destruction life cycle of the activity, unbound jme instance can be retained when
     * creating the activity again using {@link JmeAndroidSystem#setView(View)}.
     * @param condition the requested state condition.
     */
    protected void bindAppState(final boolean condition){
        this.boundAppState = condition;
    }

    /**
     * Gets the State of dissection of jme app instance from the current android activity instance.
     * @return true if the jme instance has been unbound from the activity instance, false otherwise, default is true.
     */
    protected boolean isAppStateBound(){
        return boundAppState;
    }

    /**
     * Invoked within {@link CompatHarness#startRenderer(int)}.
     * @throws InstantiationException if the class has no nullary constructor (ie: has a parameterized constructor).
     * @throws IllegalAccessException if the user hasn't settled the jme instance either via {@link CompatHarness#getInstance()} or {@link CompatHarness#setAppInstance(LegacyApplication)}.
     */
    private LegacyApplication getGameInstance() throws InstantiationException, IllegalAccessException {
        //get jme3 app
        LegacyApplication tempInstance;
        if(getInstance() != null) {
            tempInstance = getInstance().newInstance();
        }else{
            tempInstance = application;
        }
        return tempInstance;
    }

    public boolean isShowFlyCamTouchBoundaries() {
        return showFlyCamTouchBoundaries;
    }

    /**
     * To take effect, this have to be called inside {@link CompatHarness#preInitializeGlConfig()}.
     * @param showFlyCamTouchBoundaries true, if you want to debug the boundaries, false otherwise.
     */
    public void setShowFlyCamTouchBoundaries(boolean showFlyCamTouchBoundaries) {
        this.showFlyCamTouchBoundaries = showFlyCamTouchBoundaries;
    }
    /*
     * Abstract Factory method to override when implementing this factory class.
     */

    /**
     * Override to pre-initialize EGL config before being utilized by the jme context.
     */
    protected abstract void preInitializeGlConfig();
    /**
     * Used to get the instance of the jme3 application to link to the GL Renderer and render the Frame buffer.
     *
     * Override this factory method and return your game application class using this syntax :
     * <pre class="prettyprint">
     * protected Class<? extends Application> getInstance(){
     *     return MyGame.class;
     * }
     * </pre>
     * @return the jme3 game application class.
     */
    protected abstract Class<? extends LegacyApplication> getInstance();

    protected abstract View getSplashScreen();

    protected abstract Vector3f getSplashScreenAnimationTranslation();

    protected abstract Vector2f getSplashScreenAnimationScale();

    protected abstract Vector2f getSplashScreenAnimationRotation();

    protected abstract long getSplashScreenAnimationDuration();
    /**
     * Listens for a thrown {@link Exception} or {@link Error} or {@link Throwable}.
     * @param throwable the java throwable instance.
     */
    protected abstract void onExceptionThrown(final Throwable throwable);

    /*
     * GL Renderers listeners.
     */
    /**
     * Listens for a starting GL renderer.
     * @param app the game instance
     */
    protected abstract void onStartRenderer(final LegacyApplication app);

    /**
     * Listens for the completion of the current renderer.
     * @param app the app instance.
     */
    protected abstract void onRendererCompletion(final LegacyApplication app);

    protected abstract void onLayoutDrawn(final LegacyApplication app, final View layout);

    /**
     * Listens for the stoppage of the current renderer.
     * @param app the app instance.
     */
    protected abstract void onStopRenderer(final LegacyApplication app);

    /**
     * Listens for the pause of the current renderer.
     * @param app the app instance.
     */
    protected abstract void onPauseRenderer(final LegacyApplication app);

    protected abstract void onResumeRenderer(final LegacyApplication app);

    /**
     * Used by {@link CompatHarness#requestPostRenderingActions(View, int)} to build the layout manager with a respective delay {@link EGLConfig#eglSurfaceDelay}.
     */
    private class LayoutBuilder implements Runnable{
        private final View layout;
        public LayoutBuilder(final View layout){
            this.layout = layout;
        }
        @Override
        public void run() {
            //handling null events
            Vector3f translationAnimation = getSplashScreenAnimationTranslation();
            Vector2f scaleAnimation = getSplashScreenAnimationScale();
            Vector2f rotationAnimation = getSplashScreenAnimationRotation();
            if(getSplashScreenAnimationTranslation() == null){
                translationAnimation = new Vector3f();
            }
            if(getSplashScreenAnimationScale() == null){
                scaleAnimation = new Vector2f();
            }
            if(getSplashScreenAnimationRotation() == null){
                rotationAnimation = new Vector2f();
            }

            if(layoutHolder.findViewById('S') != null) {
                layoutHolder.findViewById('S')
                            .animate()
                            .translationX(translationAnimation.getX())
                            .translationY(translationAnimation.getY())
                            .translationZ(translationAnimation.getZ())
                            .rotationX(rotationAnimation.getX())
                            .rotationY(rotationAnimation.getY())
                            .scaleX(scaleAnimation.getX())
                            .scaleY(scaleAnimation.getY())
                            .setDuration(getSplashScreenAnimationDuration())
                            .withEndAction(() -> layoutHolder.removeView(layoutHolder.findViewById('S'))).start();
            }
            //inject the fly cam
            if(application instanceof SimpleApplication) {
                if(((SimpleApplication) application).getFlyByCamera().isEnabled()) {
                    final FlyCamAndroidInput flyCamAndroidInput = new FlyCamAndroidInput(CompatHarness.this, (SimpleApplication) application);
                    flyCamAndroidInput.init();
                    layoutHolder.addView(flyCamAndroidInput);
                    flyCamAndroidInput.lateInitOtherEvents();
                    if(isShowFlyCamTouchBoundaries()){
                        flyCamAndroidInput.showTouchBoundaries();
                    }else{
                        flyCamAndroidInput.unShowTouchBoundaries();
                    }
                }
            }
            onLayoutDrawn(application, layout);
            compatLogger.log(Level.INFO, "EGLSurface Delay finishes, layout joining the harness >>>>>>");
        }
    }
    /*
     * Configuration enums used to modify the activity EGL based on the user data and their device.
     */
    protected enum EGLConfig{
        DEFAULT_EGL_SURFACE_DELAY(1),
        DEFAULT_EGL_BITS_PER_PIXEL(24),
        DEFAULT_EGL_ALPHA_BITS(0),
        DEFAULT_EGL_DEPTH_BITS(16),
        DEFAULT_EGL_SAMPLES(0),
        DEFAULT_EGL_STENCIL_BITS(0),
        DEFAULT_EGL_FRAME_RATE(-1),
        DEFAULT_AUDIO_RENDERER(AppSettings.ANDROID_OPENAL_SOFT),
        DEFAULT_KEYS_BEHAVIOUR(true),
        DEFAULT_MOUSE_BEHAVIOUR(true),
        DEFAULT_JOYSTICK_BEHAVIOUR(false),
        DEFAULT_INPUT_BEHAVIOUR(true),
        DEFAULT_MOUSE_INVERT_X_BEHAVIOUR(false),
        DEFAULT_MOUSE_INVERT_Y_BEHAVIOUR(false);

        private static final int TOLERANCE_TIMER = 100;

        private static final AppSettings settings = new AppSettings(false);

        private static int eglSurfaceDelay = (int) DEFAULT_EGL_SURFACE_DELAY.getValue();
        /**
         * Sets the desired RGB size for the surfaceview.  16 = RGB565, 24 = RGB888.
         * (default = 24)
         */
        private static int eglBitsPerPixel = (int) DEFAULT_EGL_BITS_PER_PIXEL.getValue();

        /**
         * Sets the desired number of Alpha bits for the surfaceview.  This affects
         * how the surfaceview is able to display Android views that are located
         * under the surfaceview jME uses to render the scenegraph.
         * 0 = Opaque surfaceview background (fastest)
         * 1-&gt;7 = Transparent surfaceview background
         * 8 or higher = Translucent surfaceview background
         * (default = 0)
         */
        private static int eglAlphaBits = (int) DEFAULT_EGL_ALPHA_BITS.getValue();

        /**
         * The number of depth bits specifies the precision of the depth buffer.
         * (default = 16)
         */
        private static int eglDepthBits = (int) DEFAULT_EGL_DEPTH_BITS.getValue();

        /**
         * Sets the number of samples to use for multisampling.<br>
         * Leave 0 (default) to disable multisampling.<br>
         * Set to 2 or 4 to enable multisampling.
         */
        private static int eglSamples = (int) DEFAULT_EGL_SAMPLES.getValue();

        /**
         * Set the number of stencil bits.
         * (default = 0)
         */
        private static int eglStencilBits = (int) DEFAULT_EGL_STENCIL_BITS.getValue();

        /**
         * Set the desired frame rate.  If frameRate higher than 0, the application
         * will be capped at the desired frame rate.
         * (default = -1, no frame rate cap)
         */
        private static int frameRate = (int) DEFAULT_EGL_FRAME_RATE.getValue();

        /**
         * Sets the type of Audio Renderer to be used.
         * <p>
         * Android MediaPlayer / SoundPool can be used on all
         * supported Android platform versions (2.2+)<br>
         * OpenAL Soft uses an OpenSL backend and is only supported on Android
         * versions 2.3+.
         * <p>
         * Only use ANDROID_ static strings found in AppSettings
         *
         */
        private static String audioRendererType = AppSettings.ANDROID_OPENAL_SOFT;

        /**
         * If true Android Sensors are used as simulated Joysticks. Users can use the
         * Android sensor feedback through the RawInputListener or by registering
         * JoyAxisTriggers.
         */
        private static boolean joystickEventsEnabled = (boolean) DEFAULT_JOYSTICK_BEHAVIOUR.getValue();
        /**
         * If true KeyEvents are generated from TouchEvents
         */
        private static boolean keyEventsEnabled = (boolean) DEFAULT_KEYS_BEHAVIOUR.getValue();
        /**
         * If true MouseEvents are generated from TouchEvents
         */
        private static boolean mouseEventsEnabled = (boolean) DEFAULT_MOUSE_BEHAVIOUR.getValue();
        /**
         * Flip X axis
         */
        private static boolean mouseEventsInvertX = (boolean) DEFAULT_MOUSE_INVERT_X_BEHAVIOUR.getValue();

        private static boolean useInputs = (boolean) DEFAULT_INPUT_BEHAVIOUR.getValue();
        /**
         * Flip Y axis
         */
        private static boolean mouseEventsInvertY = (boolean) DEFAULT_MOUSE_INVERT_Y_BEHAVIOUR.getValue();

        private static boolean requestedRendering = false;

        private final Object value;
        EGLConfig(final Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public static void setEglSurfaceDelay(int eglSurfaceDelay) {
            EGLConfig.eglSurfaceDelay = eglSurfaceDelay;
        }
        public static int getEglSurfaceDelay() {
            return eglSurfaceDelay;
        }

        public static int getEglBitsPerPixel() {
            return eglBitsPerPixel;
        }

        public static void setEglBitsPerPixel(int eglBitsPerPixel) {
            EGLConfig.eglBitsPerPixel = eglBitsPerPixel;
        }

        public static int getEglAlphaBits() {
            return eglAlphaBits;
        }

        public static void setEglAlphaBits(int eglAlphaBits) {
            EGLConfig.eglAlphaBits = eglAlphaBits;
        }

        public static int getEglDepthBits() {
            return eglDepthBits;
        }

        public static void setEglDepthBits(int eglDepthBits) {
            EGLConfig.eglDepthBits = eglDepthBits;
        }

        public static int getEglSamples() {
            return eglSamples;
        }

        public static void setEglSamples(int eglSamples) {
            EGLConfig.eglSamples = eglSamples;
        }

        public static int getEglStencilBits() {
            return eglStencilBits;
        }

        public static void setEglStencilBits(int eglStencilBits) {
            EGLConfig.eglStencilBits = eglStencilBits;
        }

        public static int getFrameRate() {
            return frameRate;
        }

        public static void setFrameRate(int frameRate) {
            EGLConfig.frameRate = frameRate;
        }

        public static String getAudioRendererType() {
            return audioRendererType;
        }

        public static void setAudioRendererType(String audioRendererType) {
            EGLConfig.audioRendererType = audioRendererType;
        }

        public static boolean isJoystickEventsEnabled() {
            return joystickEventsEnabled;
        }

        public static void setJoystickEventsEnabled(boolean joystickEventsEnabled) {
            EGLConfig.joystickEventsEnabled = joystickEventsEnabled;
        }

        public static boolean isKeyEventsEnabled() {
            return keyEventsEnabled;
        }

        public static void setKeyEventsEnabled(boolean keyEventsEnabled) {
            EGLConfig.keyEventsEnabled = keyEventsEnabled;
        }

        public static boolean isMouseEventsEnabled() {
            return mouseEventsEnabled;
        }

        public static void setMouseEventsEnabled(boolean mouseEventsEnabled) {
            EGLConfig.mouseEventsEnabled = mouseEventsEnabled;
        }

        public static boolean isMouseEventsInvertX() {
            return mouseEventsInvertX;
        }

        public static void setMouseEventsInvertX(boolean mouseEventsInvertX) {
            EGLConfig.mouseEventsInvertX = mouseEventsInvertX;
        }

        public static boolean isMouseEventsInvertY() {
            return mouseEventsInvertY;
        }

        public static void setMouseEventsInvertY(boolean mouseEventsInvertY) {
            EGLConfig.mouseEventsInvertY = mouseEventsInvertY;
        }

        private static void setRequestedRendering(boolean requestedRendering) {
            EGLConfig.requestedRendering = requestedRendering;
        }

        public static void setUseInputs(boolean useInputs) {
            EGLConfig.useInputs = useInputs;
        }

        public static boolean isUseInputs() {
            return useInputs;
        }

        private static boolean isRequestedRendering() {
            return requestedRendering;
        }

        public static AppSettings applyGlConfig(){
            settings.setAlphaBits(eglAlphaBits);
            settings.setAudioRenderer(audioRendererType);
            settings.setBitsPerPixel(eglBitsPerPixel);
            settings.setFrameRate(frameRate);
            settings.setDepthBits(eglDepthBits);
            settings.setEmulateKeyboard(keyEventsEnabled);
            settings.setEmulateMouse(mouseEventsEnabled);
            settings.setUseJoysticks(joystickEventsEnabled);
            settings.setUseInput(useInputs);
            settings.setStencilBits(eglStencilBits);
            settings.setSamples(eglSamples);
            settings.setEmulateMouseFlipAxis(mouseEventsInvertX, mouseEventsInvertY);
            return settings;
        }
        public static void resetEGLConfig(){
            EGLConfig.setEglSurfaceDelay((int) EGLConfig.DEFAULT_EGL_SURFACE_DELAY.getValue());
            EGLConfig.setEglStencilBits((int) EGLConfig.DEFAULT_EGL_STENCIL_BITS.getValue());
            EGLConfig.setEglDepthBits((int) EGLConfig.DEFAULT_EGL_DEPTH_BITS.getValue());
            EGLConfig.setEglAlphaBits((int) EGLConfig.DEFAULT_EGL_ALPHA_BITS.getValue());
            EGLConfig.setEglSamples((int) EGLConfig.DEFAULT_EGL_SAMPLES.getValue());
            EGLConfig.setFrameRate((int) EGLConfig.DEFAULT_EGL_FRAME_RATE.getValue());
            EGLConfig.setAudioRendererType((String) EGLConfig.DEFAULT_AUDIO_RENDERER.getValue());
            EGLConfig.setJoystickEventsEnabled((boolean) EGLConfig.DEFAULT_JOYSTICK_BEHAVIOUR.getValue());
            EGLConfig.setMouseEventsEnabled((boolean) EGLConfig.DEFAULT_MOUSE_BEHAVIOUR.getValue());
            EGLConfig.setUseInputs((boolean) EGLConfig.DEFAULT_INPUT_BEHAVIOUR.getValue());
            EGLConfig.setKeyEventsEnabled((boolean) EGLConfig.DEFAULT_KEYS_BEHAVIOUR.getValue());
            EGLConfig.setMouseEventsInvertX((boolean) EGLConfig.DEFAULT_MOUSE_INVERT_X_BEHAVIOUR.getValue());
            EGLConfig.setMouseEventsInvertY((boolean) EGLConfig.DEFAULT_MOUSE_INVERT_Y_BEHAVIOUR.getValue());
        }
        public static AppSettings getSettings() {
            return settings;
        }
    }

    /**
     * Used to get the current display Metrics of the activity.
     */
    protected enum Display{
        GAME_MODE, UI_MODE;

        public enum Screen{
            LANDSCAPE, PORTRAIT, FULL_SENSOR;
            public static void setScreenMode(final Screen screenMode){
                screen = screenMode;
            }
        }

        //used to collect a stateful database for the current display(ex : width, height, density, depth, xdpi, ydpi...etc).
        private static final DisplayMetrics displayMetrics = new DisplayMetrics();
        private static boolean showErrorDialog;

        public static DisplayMetrics getDisplayMetrics(final Activity activity){
                 activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics;
        }
        private static void useSwitchMode(final Activity activity){
            if (display == Display.UI_MODE) {
                switchToUiMode(activity);
            } else if(display == Display.GAME_MODE){
                switchToGameMode(activity);
            }
            if(screen == Screen.LANDSCAPE){
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else if(screen == Screen.PORTRAIT){
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }else if(screen == Screen.FULL_SENSOR){
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        }
        private static void switchToGameMode(final Activity context){
            modifyUiVisibility(context, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        private static void switchToUiMode(final Activity context){
            modifyUiVisibility(context, 0x10000000 | 0x20000000 | 0x00000008 | 0x00008000);
        }
        private static void modifyUiVisibility(final Activity context, final int flags){
            final View decorView = context.getWindow().getDecorView();
            decorView.setSystemUiVisibility(flags);
        }
        public static void setDisplayMode(final Display displayMode){
            display = displayMode;
        }

        private static RelativeLayout initializeLayoutHolder(final Activity activity){
            final RelativeLayout relativeLayout = new RelativeLayout(activity);
            relativeLayout.setLayoutParams(new WindowManager.LayoutParams(Display.getDisplayMetrics(activity).widthPixels, Display.getDisplayMetrics(activity).heightPixels));
            activity.setContentView(relativeLayout);
            return relativeLayout;
        }
        public static void setShowErrorDialog(boolean showErrorDialog) {
            Display.showErrorDialog = showErrorDialog;
        }

        public static boolean isShowErrorDialog() {
            return showErrorDialog;
        }
    }


    /**
     * Use different preconfigured layout configurations.
     * Better be used before gl rendering of this game.
     * Note : Good Android app should handle this Laying outs from its xml layout resources database, but in case you want a fast LayoutManager, this is a good solution.
     */
    protected enum LayoutManager {

        Linear_Layout, Relative_Layout, Frame_Layout, No_Layout, Custom_Layout;

        private static int customLayoutRes;
        /**
         * Used to build a LayoutManager for the activity enclosing the jme app based on some android views criteria.
         * @param activity the activity to use the LayoutManager against.
         * @param glSurfaceView the glSurfaceView used for rendering the current app.
         * @return android view instance representing the LayoutManager.
         */
        protected static View buildLayoutManager(final ViewGroup layoutHolder, final Activity activity, final GLSurfaceView glSurfaceView){
            switch (layoutManager){
                case Frame_Layout:
                    final FrameLayout frameLayout = new FrameLayout(activity);
                    return useEnclosure(layoutHolder, frameLayout, glSurfaceView, 'F');
                case Relative_Layout:
                    final RelativeLayout relativeLayout = new RelativeLayout(activity);
                    return useEnclosure(layoutHolder, relativeLayout, glSurfaceView, 'R');
                case Linear_Layout:
                    final LinearLayout linearLayout = new LinearLayout(activity);
                    return useEnclosure(layoutHolder, linearLayout, glSurfaceView, 'L');
                case Custom_Layout:
                    if(customLayoutRes == 0){
                        throw new IllegalStateException("Cannot use a null resource layout file, please invoke LayoutManager.setCustomLayout(R.layout.custom) in preInitialize method");
                    }
                    final View layout = LayoutInflater.from(activity).inflate(customLayoutRes, null);
                    activity.setContentView(layout);
                    return layout;
                default:
                    return useNoEnclosure(layoutHolder, glSurfaceView);
            }
        }
        private static ViewGroup useEnclosure(final ViewGroup layoutHolder, final ViewGroup viewGroup, final GLSurfaceView glSurfaceView, final int id){
            viewGroup.setId(id);
            viewGroup.setLayoutParams(new RelativeLayout.LayoutParams(layoutHolder.getLayoutParams().width, layoutHolder.getLayoutParams().height));
            glSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(viewGroup.getLayoutParams().width, viewGroup.getLayoutParams().height));
            //set the resolution of the jme3 app based on the first parent
            viewGroup.addView(glSurfaceView);
            layoutHolder.addView(viewGroup);
            return viewGroup;
        }
        private static GLSurfaceView useNoEnclosure(final ViewGroup layoutHolder, final GLSurfaceView glSurfaceView){
            glSurfaceView.setId('G');
            glSurfaceView.setLayoutParams(new WindowManager.LayoutParams(layoutHolder.getLayoutParams().width, layoutHolder.getLayoutParams().height));
            //set the resolution of the jme3 app
            layoutHolder.addView(glSurfaceView);
            return glSurfaceView;
        }
        public static void setLayoutManager(final LayoutManager manager){
            layoutManager = manager;
        }
        public static void setCustomLayout(@LayoutRes final int resId){
            customLayoutRes = resId;
        }
    }

    /**
     * Keeps the jme3 game state alongside with glES context within a runtime memory for future use.
     */
    protected enum GameState{;
        private static LegacyApplication application;
        private static OGLESContext oglesContext;

        protected static LegacyApplication getApplication() {
            return application;
        }

        protected static void setApplication(LegacyApplication application) {
            GameState.application = application;
        }

        protected static OGLESContext getOglesContext() {
            return oglesContext;
        }

        protected static void setOglesContext(OGLESContext oglesContext) {
            GameState.oglesContext = oglesContext;
        }
    }

}
