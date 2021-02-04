package com.scrappers.superiorExtendedEngine.gamePad;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scrappers.GamePad.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class GamePadView extends CardView  {
    private AppCompatActivity appCompatActivity;
    private GameStickView gameStickView;
    private GamePadBody gamePadBody;
    private final int translationX =60;
    private static float GENERAL_PAD_CONFIG = 0.0f;
    public static final float TWO_THIRD_SCREEN = 2 / 3f;
    public static final float ONE_THIRD_SCREEN = 1 / 3f;
    public static final float QUARTER_SCREEN = 1 / 4f;
    public static final float HALF_SCREEN = 1 / 2f;

    public static final int GAMEPAD_BUTTON_X = 'X';
    public static final int GAMEPAD_BUTTON_Y = 'Y';
    public static final int GAMEPAD_BUTTON_A = 'A';
    public static final int GAMEPAD_BUTTON_B = 'B';
    public static final int DEFAULT_GAMEPAD_DOMAIN=R.drawable.gamepad_domain;
    public static final int DEFAULT_COLOR_STICK_DOMAIN=R.drawable.moving_stick_domain;
    public static final int FLIPPED_COLOR_STICK_DOMAIN=R.drawable.moving_stick_flipped_domain;
    public static final int OPACIFIED_COLOR_STICK_DOMAIN=R.drawable.opacified_domain;
    public static final int NOTHING_IMAGE=R.drawable.nothing;
    public static final int DEFAULT_BUTTONS=R.drawable.moving_stick;
    public static final int CRYSTAL_BUTTONS= R.drawable.crystal_buttons;
    public static final int CRYSTAL_QUADS=R.drawable.crystal_buttons_quad;
    public static final int MATERIALISTIC_BUTTONS=R.drawable.material_buttons;
    public static final int MATERIALISTIC_QUADS=R.drawable.material_buttons_quad;
    public static final int TEAL_HEXAS=R.drawable.teal_hexagons;
    public static final int TRIS_BUTTONS=R.drawable.tris_buttons;

    /**
     * Create a gamePadView instance that would hold gameStickView & PadButtons
     * @param appCompatActivity your activity instance #{{@see com.androidx.Activity}}
     * @param gameStickView an instance of a class extending #{{@link GameStickView}}
     */
    public GamePadView(@NonNull AppCompatActivity appCompatActivity, @NonNull GameStickView gameStickView) {
        super(appCompatActivity);
        setAppCompatActivity(appCompatActivity);
        setGameStickView(gameStickView);
    }

    public GamePadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GamePadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setGameStickView(GameStickView gameStickView) {
        this.gameStickView = gameStickView;
    }

    public void setAppCompatActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * Initializes the main GamePadView that would hold the gameStickView & Buttons
     * @param backgroundDrawable the gamePadView background domain
     * @param CONFIG the gamePadView size configuration mode , either #{{@link GamePadView#HALF_SCREEN}} ,
     *                                                                #{{@link GamePadView#ONE_THIRD_SCREEN}},
     *                                                                ,or, #{{@link GamePadView#QUARTER_SCREEN}}.
     * @return #{{@link GamePadView}} for multiple Implementations
     */
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
        this.setX(translationX);
        /* set the dimensions of the gamePad*/
        LayoutParams layoutParams = new LayoutParams(deviceDisplayMetrics.widthPixels-50, (int) height);
        this.setLayoutParams(layoutParams);

        /*add the gamePad view to the activity jmeGame */
        appCompatActivity.addContentView(this, layoutParams);

        return this;
    }
    /**
     * initializes the game Stick holder of the gamePad.
     * @param stickViewBackground the background domain of the game stick
     * @implNote initialize this before the gameStick - #{{@link GamePadView#initializeGameStick(int, int, int)}}
     * @apiNote if you needn't an image or a background domain , set that part to use #{{@link GamePadView#NOTHING_IMAGE}}
     * @return #{{@link GamePadView}} for multiple Implementations
     */
    public GamePadView initializeGameStickHolder(int stickViewBackground) {
        gameStickView.initializeGameStickHolder(this, GENERAL_PAD_CONFIG, stickViewBackground);
        return this;
    }

    /**
     * initializes the game Stick part of the gamePad.
     * @param stickBackground the background domain of the game stick
     * @param stickImage the image of the game stick
     * @param stickSize size of game stick
     * @implNote initialize the gameStick holder before this - #{{@link GamePadView#initializeGameStickHolder(int)}}
     * @apiNote if you needn't an image or a background domain , set that part to use #{{@link GamePadView#NOTHING_IMAGE}}
     */
    public void initializeGameStick(int stickBackground, int stickImage, int stickSize) {
        gameStickView.initializeGameStick(stickBackground, stickImage, stickSize);
    }

    public void addControlButton(String buttonName,int buttonID, int backgroundDrawable, int drawableIcon, OnClickListener clickListener,OnLongClickListener longClickListener) {
        ImageView controlButton = new ImageView(appCompatActivity);
        controlButton.setId(buttonID);
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
        switch (buttonID){
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
    public void setButtonListener(int buttonID,OnClickListener onClickListener){
        ImageView pushButton=findViewById(buttonID);
        pushButton.setOnClickListener(onClickListener);
    }
    public void setButtonLongClickListener(int buttonID,OnLongClickListener onLongClickListener){
        ImageView pushButton=findViewById(buttonID);
        pushButton.setOnLongClickListener(onLongClickListener);
    }
    public void setButtonBackgroundDrawable(int buttonID,int drawable){
        (findViewById(buttonID)).setBackground(ContextCompat.getDrawable(getContext(),drawable));
    }
    public void setButtonSrcDrawable(int buttonID,int drawable){
        ((ImageView)findViewById(buttonID)).setImageDrawable(ContextCompat.getDrawable(getContext(),drawable));
    }
    public void setButtonSrcBitmap(int buttonID,String srcPath){
        ((ImageView)findViewById(buttonID)).setImageBitmap(BitmapFactory.decodeFile(srcPath));
    }
    public void setButtonSrcIcon(int buttonID,String srcPath){
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            ((ImageView)findViewById(buttonID)).setImageIcon(Icon.createWithFilePath(srcPath));
        }
    }
    public ImageView getButtonById(int id){
        return ((ImageView)findViewById(id));
    }
    public void initializeRotationSensor(){
        gameStickView.initializeRotationSensor();
    }
    public void deInitializeSensors(){
        gameStickView.deInitializeSensors();
    }

    /**
     * Internal use only
     *
     */
    public int getGamePadHeight() {
        return this.getLayoutParams().height;
    }

    public int getGamePadWidth() {
        return this.getLayoutParams().width;
    }
    /**
     * set the motion path indicator width
     * @param width stroke size
     */
    public void setMotionPathStrokeWidth(int width) {
        gameStickView.setMotionPathStrokeWidth(width);
    }
    /**
     * set the stick path indicator color
     * @param color Color to specify , DEFAULT is black
     */
    public void setMotionPathColor(int color) {
        gameStickView.setMotionPathColor(color);
    }
    /**
     * enables the stick path indicator
     * @param stickPathEnabled true/false
     */
    public void setStickPathEnabled(boolean stickPathEnabled) {
        gameStickView.setStickPathEnabled(stickPathEnabled);
    }
    public void setButtonsStyle(int backgroundDrawable){
        this.setBackground(ContextCompat.getDrawable(appCompatActivity, backgroundDrawable));
    }
    public void setButtonsColor(int color){
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            this.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }



}
