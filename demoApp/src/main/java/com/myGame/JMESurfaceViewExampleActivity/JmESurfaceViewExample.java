package com.myGame.JMESurfaceViewExampleActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.myGame.JmEGamePadExample.JmeGame;
import com.myGame.R;
import com.myGame.SystemVisibilityUI;
import com.myGame.backgroundTest.NotificationExample;
import com.scrappers.superiorExtendedEngine.gamePad.ControlButtonsView;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.JmeSurfaceView;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.dialog.OptionPane;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen.SplashScreen;
import com.scrappers.superiorExtendedEngine.menuStates.UiStateManager;
import com.scrappers.superiorExtendedEngine.vehicles.GullWing;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


public class JmESurfaceViewExample extends AppCompatActivity {

    private JmeSurfaceView jmESurfaceView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        final JmeGame jmeGame=new JmeGame(this);
        jmESurfaceView=findViewById(R.id.jmeSurfaceView);

        SplashScreen splashScreen =new SplashScreen(this,jmESurfaceView);
        splashScreen.setOnSplashScreenDisplayed((splashScreen1)->{
            ControlButtonsView.GamePadSoundEffects gamePadSoundEffects=new ControlButtonsView.GamePadSoundEffects(JmESurfaceViewExample.this);
            gamePadSoundEffects.initializeSoundEffects();
            gamePadSoundEffects.playEffect(R.raw.intro);
        });
        splashScreen.displayProgressedSplash();
        splashScreen.getSplashScreen().setBackground(ContextCompat.getDrawable(this,R.mipmap.power1));

        jmESurfaceView.setOnRendererCompleted((application,settings) -> {
            splashScreen.getSplashScreen().animate().
                    setDuration(1000).
                    rotation(90).
                    withEndAction(splashScreen::hideSplashScreen);

            ((GullWing)findViewById(R.id.steeringWheel)).getHorn().setOnClickListener(v -> {
                ControlButtonsView.GamePadSoundEffects gamePadSoundEffects=new ControlButtonsView.GamePadSoundEffects(JmESurfaceViewExample.this);
                gamePadSoundEffects.initializeSoundEffects();
                gamePadSoundEffects.playEffect(R.raw.horn);
            });
            //test UiStates stacks
            UiStateManager uiStateManager = new UiStateManager(jmESurfaceView);
            LinearLayout menu = (LinearLayout)uiStateManager.fromXML(R.layout.main_menu);
            uiStateManager.attachUiState(menu);
            menu.setId('N');
            uiStateManager.getChildUiStateById('N').findViewById(R.id.start).setOnClickListener(v -> {
                (findViewById(R.id.gameStickView)).setVisibility(View.VISIBLE);
                (findViewById(R.id.speedometer)).setVisibility(View.VISIBLE);
                (findViewById(R.id.gamePadbtns)).setVisibility(View.VISIBLE);
                findViewById(R.id.steeringWheel).setVisibility(View.VISIBLE);
                menu.animate().scaleY(0).scaleX(0).setDuration(1500).withEndAction(()->uiStateManager.detachUiState(menu));
            });
            //test UiPager
            UiTestCase uiTestCase = new UiTestCase(uiStateManager);
            try {
                uiTestCase.testPagerUiStates();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        jmESurfaceView.setLegacyApplication(jmeGame);
        jmESurfaceView.startRenderer(200);

//        ServiceBuilder background = new ServiceBuilder(jmESurfaceView.getContext(), 2021);
//        background.startService(Back.class, 3 * ServiceBuilder.CONVERT_TO_MINUTES, 4 * ServiceBuilder.CONVERT_TO_MINUTES)
//                  .setBackoffCriteria(1, JobInfo.BACKOFF_POLICY_EXPONENTIAL);
        //set work-request flags
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build();
        Data.Builder dataBuilder = new Data.Builder()
                .putString("Notification-Test", NotificationExample.CHANNEL_ID);
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NotificationExample.class, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                .setInitialDelay(Duration.ofMinutes(5))
                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(dataBuilder.build())
                .addTag("Notification-Test")
                .build();

        //register work request to the workManager to be unique, replace itself if it's still running
        WorkManager.getInstance(jmESurfaceView.getContext())
                .enqueueUniquePeriodicWork("Notification-Test", ExistingPeriodicWorkPolicy.REPLACE, workRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
         SystemVisibilityUI systemVisibilityUI=new SystemVisibilityUI(JmESurfaceViewExample.this);
         systemVisibilityUI.setGameMode();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed() {
       final OptionPane optionPane=new OptionPane(JmESurfaceViewExample.this);
        optionPane.showCustomDialog(R.layout.dialog_exception, Gravity.CENTER);
        optionPane.getAlertDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.dialog_exception_background));
        EditText errorContainer=optionPane.getInflater().findViewById(R.id.errorText);
        errorContainer.setText("Are You sure ?");
        ((Button)optionPane.getInflater().findViewById(R.id.closeApp)).setText("yes");
        ((Button)optionPane.getInflater().findViewById(R.id.ignoreError)).setText("no");
        optionPane.getInflater().findViewById(R.id.closeApp).setOnClickListener(
                view -> {
                    optionPane.getAlertDialog().dismiss();
                    jmESurfaceView.getLegacyApplication().stop(jmESurfaceView.isGLThreadPaused());
                    jmESurfaceView.getLegacyApplication().destroy();
                    finish();
        });


        optionPane.getInflater().findViewById(R.id.ignoreError).setOnClickListener(view -> optionPane.getAlertDialog().dismiss());

    }
    public class test1 {
        public void addView(View v){

        }
    }
    public class test2 extends test1{

        public void addView(RelativeLayout v) {
            super.addView(v);
        }
    }

}