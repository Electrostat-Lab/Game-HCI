package com.myGame.JMESurfaceViewExampleActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.myGame.JmEGamePadExample.JmeGame;
import com.myGame.R;
import com.myGame.SystemVisibilityUI;
import com.scrappers.superiorExtendedEngine.gamePad.ControlButtonsView;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.JmeSurfaceView;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.dialog.OptionPane;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen.SplashScreen;
import com.scrappers.superiorExtendedEngine.menuStates.UiStateManager;
import com.scrappers.superiorExtendedEngine.menuStates.UiStatesLooper;
import com.scrappers.superiorExtendedEngine.misc.GullWing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class JmESurfaceViewExample extends AppCompatActivity {

    private JmeSurfaceView jmESurfaceView;
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
            UiStateManager uiStateManager = new UiStateManager(jmESurfaceView);
            uiStateManager.attachUiState(uiStateManager.fromXML(R.layout.main_menu)).setId('a');
            uiStateManager.attachUiState(uiStateManager.fromXML(R.layout.main_menu)).setId('b');
            uiStateManager.attachUiState(uiStateManager.fromXML(R.layout.main_menu)).setId('c');
            uiStateManager.forEachUiState((UiStatesLooper.Modifiable.Looper) (currentView, position) -> currentView.findViewById(R.id.start).setOnClickListener(v -> {
                (findViewById(R.id.gameStickView)).setVisibility(View.VISIBLE);
                (findViewById(R.id.speedometer)).setVisibility(View.VISIBLE);
                (findViewById(R.id.gamePadbtns)).setVisibility(View.VISIBLE);
                findViewById(R.id.steeringWheel).setVisibility(View.VISIBLE);
               uiStateManager.getChildUiStateByIndex(position).animate().scaleY(0).scaleX(0).setDuration(1500).withEndAction(()->uiStateManager.detachUiState(currentView));
            }));
            UiTestCase uiTestCase = new UiTestCase(uiStateManager);
            uiTestCase.testPagerUiStates();

        });
        jmESurfaceView.setLegacyApplication(jmeGame);
        jmESurfaceView.startRenderer(200);
//        ImageView pause=findViewById(R.id.pause);
//        pause.setOnClickListener(v -> {
//            final OptionPane optionPane=new OptionPane(JmESurfaceViewExample.this);
//            optionPane.showCustomDialog(R.layout.dialog_exception, Gravity.CENTER);
//            optionPane.getAlertDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dialog_exception_background));
//            EditText errorContainer=optionPane.getInflater().findViewById(R.id.errorText);
//            errorContainer.setText("Are You sure ?");
//            ((Button)optionPane.getInflater().findViewById(R.id.closeApp)).setText("yes");
//            ((Button)optionPane.getInflater().findViewById(R.id.ignoreError)).setText("no");
//            optionPane.getInflater().findViewById(R.id.closeApp).setOnClickListener(
//                    view -> {
//                        optionPane.getAlertDialog().dismiss();
//                        jmESurfaceView.getSimpleApplication().stop(jmESurfaceView.isGLThreadPaused());
//                        jmESurfaceView.getSimpleApplication().destroy();
//                        finish();
//                    });
//            optionPane.getInflater().findViewById(R.id.ignoreError).setOnClickListener(view -> optionPane.getAlertDialog().dismiss());
//
//        });



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
}