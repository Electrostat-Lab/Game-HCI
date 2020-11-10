package com.mygame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mygame.basic_android_template.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

@SuppressLint("ViewConstructor")
public class GamePadView extends CardView {
    private Activity appCompatActivity;
    private GameStickView gameStickView;
    private static float GENERAL_PAD_CONFIG = 0.0f;
    public static final float TWO_THIRD_SCREEN = 2 / 3f;
    public static final float ONE_THIRD_SCREEN = 1 / 3f;
    public static final float QUARTER_SCREEN = 1 / 4f;
    public static final float HALF_SCREEN = 1 / 2f;
    public static final float FULL_SCREEN = 1f;

    public static final String GAMEPAD_BUTTON_X = "X";
    public static final String GAMEPAD_BUTTON_Y = "Y";
    public static final String GAMEPAD_BUTTON_A = "A";
    public static final String GAMEPAD_BUTTON_B = "B";
    public static final int CRYSTAL_BUTTONS=R.drawable.crystal_buttons;
    public static final int CRYSTAL_QUADS=R.drawable.crystal_buttons_quad;
    public static final int MATERIALISTIC_BUTTONS=R.drawable.material_buttons;
    public static final int MATERIALISTIC_QUADS=R.drawable.material_buttons_quad;

    public GamePadView(@NonNull Activity appCompatActivity, GameStickView gameStickView) {
        super(appCompatActivity);
        setAppCompatActivity(appCompatActivity);
        setGameStickView(gameStickView);
    }

    public void setGameStickView(GameStickView gameStickView) {
        this.gameStickView = gameStickView;
    }

    public void setAppCompatActivity(Activity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }



    public GamePadView initializeGamePad(int backgroundDrawable, float CONFIG) {
        this.setBackground(ContextCompat.getDrawable(appCompatActivity, backgroundDrawable));
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        this.setEnabled(false);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            this.setFocusedByDefault(false);
        }

        DisplayMetrics deviceDisplayMetrics = new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(deviceDisplayMetrics);

        float height = (float) deviceDisplayMetrics.heightPixels * CONFIG;
        GENERAL_PAD_CONFIG = CONFIG;
        /* set the location of the gamePad on the screen */
        this.setY(deviceDisplayMetrics.heightPixels - height*1.15f);
        this.setX(50);
        /* set the dimensions of the gamePad*/
        LayoutParams layoutParams = new LayoutParams(deviceDisplayMetrics.widthPixels-50, (int) height);
        this.setLayoutParams(layoutParams);

        /*add the gamePad view to the activity jmeGame */
        appCompatActivity.addContentView(this, layoutParams);
        return this;
    }

    public GamePadView initializeGameStickHolder(int stickViewBackground) {

        gameStickView.initializeGameStickHolder(this, GENERAL_PAD_CONFIG, stickViewBackground);
        return this;
    }

    public void initializeGameStick(int stickBackground, int stickImage, int stickSize) {
        gameStickView.initializeGameStick(stickBackground, stickImage, stickSize);
    }

    public void addControlButton(String buttonName,String correspondingGamePadButton, int backgroundDrawable, int drawableIcon, OnClickListener clickListener,OnLongClickListener longClickListener) {
        ImageView controlButton = new ImageView(appCompatActivity);
        float buttonSize=GENERAL_PAD_CONFIG * 300f;
        ViewGroup.LayoutParams layoutParams = new LayoutParams((int) buttonSize, (int)buttonSize);
        controlButton.setLayoutParams(layoutParams);
        controlButton.setBackground(ContextCompat.getDrawable(appCompatActivity, backgroundDrawable));
        controlButton.setImageDrawable(ContextCompat.getDrawable(appCompatActivity, drawableIcon));
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            controlButton.setTooltipText(buttonName);
        }
        controlButton.setOnClickListener(clickListener);
        controlButton.setOnLongClickListener(longClickListener);
        switch (correspondingGamePadButton){
            case GAMEPAD_BUTTON_X:
                controlButton.setX((this.getGamePadWidth() - buttonSize * 4));
                controlButton.setY((this.getGamePadHeight() - buttonSize * 2.25f));
                break;
            case GAMEPAD_BUTTON_B:
                controlButton.setX((this.getGamePadWidth() - buttonSize * 2));
                controlButton.setY((this.getGamePadHeight() - buttonSize * 2.25f));
                break;

            case GAMEPAD_BUTTON_Y:
                controlButton.setX((this.getGamePadWidth() - buttonSize * 3));
                controlButton.setY((this.getGamePadHeight() - buttonSize * 3.25f));
                break;
            case GAMEPAD_BUTTON_A:
                controlButton.setX((this.getGamePadWidth() - buttonSize * 3));
                controlButton.setY((this.getGamePadHeight() - buttonSize * 1.25f));
                break;
        }

        this.addView(controlButton);

    }


    public int getGamePadHeight() {
        return this.getLayoutParams().height;
    }

    public int getGamePadWidth() {
        return this.getLayoutParams().width;
    }

    public void setMotionPathStrokeWidth(int width) {
        gameStickView.setMotionPathStrokeWidth(width);
    }

    public void setMotionPathColor(int color) {
        gameStickView.setMotionPathColor(color);
    }

    public void setStickPathEnabled(boolean stickPathEnabled) {
        gameStickView.setStickPathEnabled(stickPathEnabled);
    }

}
