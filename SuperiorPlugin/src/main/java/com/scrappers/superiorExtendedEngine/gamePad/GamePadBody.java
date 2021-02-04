package com.scrappers.superiorExtendedEngine.gamePad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;

import com.scrappers.GamePad.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

@SuppressLint("ViewConstructor")
public class GamePadBody extends CardView {
    private final AppCompatActivity activity;
    public GamePadBody(@NonNull AppCompatActivity activity) {
        super(activity);
        this.activity=activity;
    }
    public void initializeGamePadBody(GamePadView gamePadView, GameStickView gameStickView,int translationX){
        LayoutParams layoutParams=new LayoutParams((gamePadView.getGamePadWidth() - (gameStickView.getLayoutParams().width )),gamePadView.getGamePadHeight());
        this.setLayoutParams(layoutParams);
        this.setX(gameStickView.getLayoutParams().width + translationX);
        this.setY(0);
        gamePadView.addView(this);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
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
