package com.scrappers.superiorExtendedEngine.gamePad;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scrappers.GamePad.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ControlButtonsView extends RelativeLayout {
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


    public ControlButtonsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlButtonsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageView addControlButton(String buttonName,int buttonID, int backgroundDrawable, int drawableIcon){
            ImageView controlButton = new ImageView(this.getContext());
            controlButton.setId(buttonID);
            ViewGroup.LayoutParams layoutParams = new LayoutParams(this.getLayoutParams().width /3, this.getLayoutParams().height/3);
            controlButton.setLayoutParams(layoutParams);
            controlButton.setImageDrawable(ContextCompat.getDrawable(this.getContext(), drawableIcon));
            controlButton.setBackground(ContextCompat.getDrawable(this.getContext(), backgroundDrawable));
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                controlButton.setTooltipText(buttonName);
            }
            switch (buttonID){
                case GAMEPAD_BUTTON_X:
                    controlButton.setX(0f);
                    controlButton.setY(layoutParams.height);
                    break;
                case GAMEPAD_BUTTON_B:
                    controlButton.setX(layoutParams.width*2);
                    controlButton.setY(layoutParams.height);
                    break;

                case GAMEPAD_BUTTON_Y:
                    controlButton.setX(layoutParams.width);
                    controlButton.setY(0f);
                    break;
                case GAMEPAD_BUTTON_A:
                    controlButton.setX(layoutParams.width);
                    controlButton.setY(layoutParams.height*2);
                    break;
            }
            this.addView(controlButton);
        return controlButton;
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
    public void setButtonsStyle(int backgroundDrawable){
        this.setBackground(ContextCompat.getDrawable(this.getContext(), backgroundDrawable));
    }
    public void setButtonsColor(int color){
        this.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public static class GamePadShocker {
        private final Activity appCompatActivity;
        private Vibrator shocker;
        public GamePadShocker(Activity appCompatActivity){
            this.appCompatActivity=appCompatActivity;
        }
        /**
         * initializes the Vibrator Sensor service
         */
        public void initializeGamePadShocker(){
            if(appCompatActivity.getSystemService(Context.VIBRATOR_SERVICE)!=null){
                shocker = (Vibrator) appCompatActivity.getSystemService(Context.VIBRATOR_SERVICE);
            }
        }

        /**
         * Vibrates the physical device
         * @param millis time the vibration waves last
         * @param amplitude vibration waves amplitude(magnitude) - from 1 up to 255 (int values)
         */
        public void shock(long millis,int amplitude){
            if( shocker.hasVibrator()){
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                    shocker.vibrate(VibrationEffect.createOneShot(millis,amplitude));
                }else{
                    shocker.vibrate(millis);
                }
            }
        }

        /**
         * Simulates PS4 shock vibrator system
         * @param numberOfShocks number of shocks to produce per the whole unit time
         * @param millis unit time in millis
         * @param amplitude the magnitude of these shocks
         *                       @RequiresApi(api = Build.VERSION_CODES.O)
         */
        public void generateShocks(int numberOfShocks,long millis,int amplitude){
            if( shocker.hasVibrator()){
                new CountDownTimer(millis, millis/numberOfShocks) {
                    @Override
                    public void onTick(long l) {
                        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                            shocker.vibrate(VibrationEffect.createOneShot(millis/numberOfShocks,amplitude));
                        }else {
                            shocker.vibrate(millis/numberOfShocks);
                        }
                    }

                    @Override
                    public void onFinish() {
                        shocker.cancel();
                    }
                }.start();
            }
        }
        /**
         *
         * Simulate DualShock Effect for a Loser
         * @predefinedShock
         */
        public void shockLoser(){
            if( shocker.hasVibrator()){
                new CountDownTimer(400, 100) {
                    @Override
                    public void onTick(long l) {
                        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                            shocker.vibrate(VibrationEffect.createOneShot(100,80));
                        }else {
                            shocker.vibrate(100);
                        }
                    }

                    @Override
                    public void onFinish() {
                        shocker.cancel();
                    }
                }.start();
            }
        }

        /**
         * Simulate DualShock Effect for a winner
         * @predefinedShock
         */
        public void shockWinner(){
            if( shocker.hasVibrator()){
                new CountDownTimer(200, 50) {
                    @Override
                    public void onTick(long l) {
                        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                            shocker.vibrate(VibrationEffect.createOneShot(50,20));
                        }else {
                            shocker.vibrate(50);
                        }
                    }

                    @Override
                    public void onFinish() {
                        shocker.cancel();
                    }
                }.start();
            }
        }
    }
    public static class GamePadSoundEffects {
        private final Activity activity;
        private MediaPlayer mediaPlayer;
        public static final int WINNER_SOUND=R.raw.winnersound;
        public static final int LOSER_SOUND=R.raw.losersound;

        public GamePadSoundEffects(Activity activity){
            this.activity=activity;
        }
        public void initializeSoundEffects() {
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build());
        }
        public void playEffect(int effect) throws IllegalStateException {
            mediaPlayer=MediaPlayer.create(activity,effect);
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                new CountDownTimer(mediaPlayer.getDuration(), mediaPlayer.getDuration()) {
                    @Override
                    public void onTick(long l) {
                    }

                    @Override
                    public void onFinish() {
                        try {
                            if ( mediaPlayer.isPlaying() ){
                                mediaPlayer.stop();
                                mediaPlayer.release();
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }else{
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;
            }
        }
        public void loopEffect(int numberOfLoops,int effect) throws IllegalStateException {
            mediaPlayer = MediaPlayer.create(activity, effect);
            if ( !mediaPlayer.isPlaying() ){
                new CountDownTimer(mediaPlayer.getDuration() * numberOfLoops, mediaPlayer.getDuration()) {
                    @Override
                    public void onTick(long l) {
                        try {
                            if ( mediaPlayer.isPlaying() ){
                                mediaPlayer.stop();
                                mediaPlayer.release();
                            }
                            mediaPlayer = MediaPlayer.create(activity, effect);
                            mediaPlayer.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        try {
                            if ( mediaPlayer.isPlaying() ){
                                mediaPlayer.stop();
                                mediaPlayer.release();
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }else{
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;
            }
        }

    }
}
