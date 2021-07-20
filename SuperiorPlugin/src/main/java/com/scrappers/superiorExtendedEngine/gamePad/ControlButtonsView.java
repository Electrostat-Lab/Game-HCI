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
    /**
     * Grouping up the ButtonSignatures.
     * @author pavl_g
     */
    public enum ButtonSignature {
        GAMEPAD_BUTTON_X('X', "X"),
        GAMEPAD_BUTTON_Y('Y', "Y"),
        GAMEPAD_BUTTON_A('A', "A"),
        GAMEPAD_BUTTON_B('B', "B");

        public final int ID;
        public String NAME;

        ButtonSignature(final int ID){
            this.ID = ID;
        }
        ButtonSignature(final int ID, final String NAME){
            this.ID = ID;
            this.NAME = NAME;
        }
    }

    /**
     * Grouping up the ButtonIcons.
     * @author pavl_g
     */
    public enum ButtonIcon {
        X_BUTTON_ALPHA(R.drawable.x_button_alpha), X_BUTTON_OUTLINE(R.drawable.x_button_outline),
        Y_BUTTON_ALPHA(R.drawable.y_button_alpha), Y_BUTTON_OUTLINE(R.drawable.y_button_outline),
        A_BUTTON_ALPHA(R.drawable.a_button_alpha), A_BUTTON_OUTLINE(R.drawable.a_button_outline),
        B_BUTTON_ALPHA(R.drawable.b_button_alpha), B_BUTTON_OUTLINE(R.drawable.b_button_outline);
        public final int ID;

        ButtonIcon(final int ID){
            this.ID = ID;
        }
    }

    /**
     * Grouping up the ButtonsStyles.
     * @author pavl_g
     */
    public enum ButtonStyle {
        DEFAULT_GAMEPAD_DOMAIN(R.drawable.gamepad_domain), DEFAULT_COLOR_STICK_DOMAIN(R.drawable.moving_stick_domain),
        FLIPPED_COLOR_STICK_DOMAIN(R.drawable.moving_stick_flipped_domain), OPACIFIED_COLOR_STICK_DOMAIN(R.drawable.opacified_domain),
        NOTHING_IMAGE(R.drawable.nothing), DEFAULT_BUTTONS(R.drawable.moving_stick), CRYSTAL_BUTTONS(R.drawable.crystal_buttons),
        CRYSTAL_QUADS(R.drawable.crystal_buttons_quad), MATERIALISTIC_BUTTONS(R.drawable.material_buttons),
        TEAL_HEXAS(R.drawable.teal_hexagons), TRIS_BUTTONS(R.drawable.tris_buttons), STICK_DASHES(R.drawable.stick_dash);

        public final int STYLE;

        ButtonStyle(final int STYLE){
            this.STYLE = STYLE;
        }
    }

    public ControlButtonsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlButtonsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Adds a GamePad button & apply the UI.
     * @param button the button to add from the enum #{@link ControlButtonsView.ButtonSignature} : eg : #{@link ButtonSignature#GAMEPAD_BUTTON_X},...etc.
     * @param backgroundDrawable the drawable style, you can get some from enum : #{@link ControlButtonsView.ButtonStyle}
     * @param drawableIcon the button icon, you can get some from enum : #{@link ControlButtonsView.ButtonIcon}
     * @return an imageView instance representing the added button.
     */
    public ImageView addControlButton(@NonNull ButtonSignature button, int backgroundDrawable, int drawableIcon){
        ImageView controlButton = new ImageView(this.getContext());
            ((AppCompatActivity)getContext()).runOnUiThread(()->{
                controlButton.setId(button.ID);
                ViewGroup.LayoutParams layoutParams = new LayoutParams(this.getLayoutParams().width /3, this.getLayoutParams().height/3);
                controlButton.setLayoutParams(layoutParams);
                controlButton.setImageDrawable(ContextCompat.getDrawable(this.getContext(), drawableIcon));
                controlButton.setBackground(ContextCompat.getDrawable(this.getContext(), backgroundDrawable));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    controlButton.setTooltipText(button.NAME);
                }

                if (button == ButtonSignature.GAMEPAD_BUTTON_X){
                    controlButton.setX(0f);
                    controlButton.setY(layoutParams.height);
                }else if(button == ButtonSignature.GAMEPAD_BUTTON_B){
                    controlButton.setX(layoutParams.width * 2);
                    controlButton.setY(layoutParams.height);
                }else if(button == ButtonSignature.GAMEPAD_BUTTON_Y){
                    controlButton.setX(layoutParams.width);
                    controlButton.setY(0f);
                }else if(button == ButtonSignature.GAMEPAD_BUTTON_A){
                    controlButton.setX(layoutParams.width);
                    controlButton.setY(layoutParams.height * 2);
                }
                this.addView(controlButton);
            });
        return controlButton;
    }
    public void setButtonListener(@NonNull ButtonSignature button, OnClickListener onClickListener){
        ImageView pushButton=findViewById(button.ID);
        pushButton.setOnClickListener(onClickListener);
    }
    public void setButtonLongClickListener(@NonNull ButtonSignature button, OnLongClickListener onLongClickListener){
        ImageView pushButton=findViewById(button.ID);
        pushButton.setOnLongClickListener(onLongClickListener);
    }
    public void setButtonBackgroundDrawable(@NonNull ButtonSignature button,int drawable){
        (findViewById(button.ID)).setBackground(ContextCompat.getDrawable(getContext(),drawable));
    }
    public void setButtonSrcDrawable(@NonNull ButtonSignature button, int drawable){
        ((ImageView)findViewById(button.ID)).setImageDrawable(ContextCompat.getDrawable(getContext(),drawable));
    }
    public void setButtonSrcBitmap(@NonNull ButtonSignature button, String srcPath){
        ((ImageView)findViewById(button.ID)).setImageBitmap(BitmapFactory.decodeFile(srcPath));
    }
    public void setButtonSrcIcon(@NonNull ButtonSignature button, String srcPath){
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            ((ImageView)findViewById(button.ID)).setImageIcon(Icon.createWithFilePath(srcPath));
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
