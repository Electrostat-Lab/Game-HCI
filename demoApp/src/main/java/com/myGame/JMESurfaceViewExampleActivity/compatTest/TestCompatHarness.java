package com.myGame.JMESurfaceViewExampleActivity.compatTest;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.jme3.app.LegacyApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.myGame.JmEGamePadExample.LanLogic;
import com.myGame.JmeEffects.NitroState;
import com.myGame.R;
import com.scrappers.superiorExtendedEngine.gamePad.ControlButtonsView;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.compat.CompatHarness;
import com.scrappers.superiorExtendedEngine.vehicles.GullWing;

/**
 * An Android CompatHarness Migration test.
 * @author pavl_g.
 */
public class TestCompatHarness extends CompatHarness {

    @Override
    protected void preInitializeGlConfig() {
        //TODO define your egl, display and LayoutManager config here
        EGLConfig.setEglSurfaceDelay(2000);
        Display.setDisplayMode(Display.GAME_MODE);
        Display.Screen.setScreenMode(Display.Screen.LANDSCAPE);
        //TODO choose whether to bind the app life to the activity one or not, true if you want them bound, false otherwise
        bindAppState(false);
        LayoutManager.setLayoutManager(LayoutManager.Relative_Layout);
        LayoutManager.setCustomLayout(R.layout.activity_example);
        setShowFlyCamTouchBoundaries(true);

    }

    @Override
    protected Class<? extends LegacyApplication> getInstance() {
        //TODO return your game class here
        return CompatGame.class;
    }

    @Override
    protected View getSplashScreen() {
        //TODO define your splash screen here
        final CardView splashScreen = new CardView(this);
        splashScreen.setBackground(ContextCompat.getDrawable(this, R.mipmap.power1));
        return splashScreen;
    }

    @Override
    protected Vector3f getSplashScreenAnimationTranslation() {
        return new Vector3f();
    }

    @Override
    protected Vector2f getSplashScreenAnimationScale() {
        return new Vector2f(0,0);
    }

    @Override
    protected Vector2f getSplashScreenAnimationRotation() {
        return new Vector2f(40, 90);
    }

    @Override
    protected long getSplashScreenAnimationDuration() {
        return 1000;
    }

    @Override
    protected void onExceptionThrown(Throwable throwable) {
        //TODO catch and deal with exceptions/errors here
    }

    @Override
    protected void onStartRenderer(LegacyApplication app) {
        //TODO do something when the renderer starts
    }

    @Override
    protected void onRendererCompletion(LegacyApplication app) {
        //TODO do something when the renderer completes
    }

    @Override
    protected void onLayoutDrawn(LegacyApplication app, View layout) {
        //TODO do something when layout is drawn
//        final RelativeLayout relativeLayout = (RelativeLayout) layout;
//        final Button button = new Button(this);
//        button.setText("Hello from Compat Harness !");
//        button.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        button.setX(Display.getDisplayMetrics(this).widthPixels / 2f - 30);
//        button.setY(Display.getDisplayMetrics(this).heightPixels / 2f - 30);
//        button.setOnClickListener((view)-> Toast.makeText(TestCompatHarness.this, button.getText().toString(), Toast.LENGTH_SHORT).show());
//        relativeLayout.addView(button);
//
//        final GullWing gullWing = new GullWing(this);
//        gullWing.setLayoutParams(new RelativeLayout.LayoutParams(400, 400));
//        gullWing.setY(Display.getDisplayMetrics(this).heightPixels - (gullWing.getLayoutParams().height + 30));
//        gullWing.setX(40);
//        gullWing.initializeWheel();
//        relativeLayout.addView(gullWing);
//
//        final ControlButtonsView controlButtonsView = new ControlButtonsView(this);
//        controlButtonsView.setLayoutParams(new RelativeLayout.LayoutParams(400, 400));
//        controlButtonsView.setX(Display.getDisplayMetrics(this).widthPixels - (controlButtonsView.getLayoutParams().width + 30));
//        controlButtonsView.setY(Display.getDisplayMetrics(this).heightPixels - (controlButtonsView.getLayoutParams().height + 30));
//        controlButtonsView.addControlButton(ControlButtonsView.ButtonSignature.GAMEPAD_BUTTON_X, ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonIcon.X_BUTTON_ALPHA.ID);
//        controlButtonsView.addControlButton(ControlButtonsView.ButtonSignature.GAMEPAD_BUTTON_Y, ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonIcon.Y_BUTTON_ALPHA.ID);
//        controlButtonsView.addControlButton(ControlButtonsView.ButtonSignature.GAMEPAD_BUTTON_A, ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonIcon.A_BUTTON_ALPHA.ID);
//        controlButtonsView.addControlButton(ControlButtonsView.ButtonSignature.GAMEPAD_BUTTON_B, ControlButtonsView.ButtonStyle.DEFAULT_BUTTONS.STYLE, ControlButtonsView.ButtonIcon.B_BUTTON_ALPHA.ID);
//        relativeLayout.addView(controlButtonsView);
    }

    @Override
    protected void onStopRenderer(LegacyApplication app) {
        //TODO do something when the renderer stops
    }

    @Override
    protected void onPauseRenderer(LegacyApplication app) {
        //TODO do something when the renderer is paused
    }

    @Override
    protected void onResumeRenderer(LegacyApplication app) {
        //TODO do something when the renderer is resumed
    }
}
