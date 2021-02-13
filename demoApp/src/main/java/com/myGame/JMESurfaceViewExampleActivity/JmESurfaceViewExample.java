package com.myGame.JMESurfaceViewExampleActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.myGame.JmEGamePadExample.JmeGame;
import com.myGame.R;
import com.myGame.SystemVisibilityUI;
import com.scrappers.superiorExtendedEngine.gamePad.ControlButtonsView;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.Dialog.OptionPane;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.JmESurfaceView;
import com.scrappers.superiorExtendedEngine.jmeSurfaceView.splashScreen.SplashScreen;


public class JmESurfaceViewExample extends AppCompatActivity {

    private JmESurfaceView jmESurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        final JmeGame jmeGame=new JmeGame(this);
        jmESurfaceView=findViewById(R.id.jmeSurfaceView);
        jmESurfaceView.setIgnoreAssertions(true);
        jmESurfaceView.setEglBitsPerPixel(24);
        jmESurfaceView.setEglAlphaBits(0);
        jmESurfaceView.setEglDepthBits(16);
        jmESurfaceView.setEglSamples(0);
        jmESurfaceView.setEglStencilBits(0);
        jmESurfaceView.setFrameRate(-1);
        jmESurfaceView.setOnExceptionThrown(System.out::println);
        SplashScreen splashScreen =new SplashScreen(this,jmESurfaceView);
        splashScreen.setOnSplashScreenDisplayed((splashScreen1)->{
            ControlButtonsView.GamePadSoundEffects gamePadSoundEffects=new ControlButtonsView.GamePadSoundEffects(JmESurfaceViewExample.this);
            gamePadSoundEffects.initializeSoundEffects();
            gamePadSoundEffects.playEffect(R.raw.intro);
        });
        splashScreen.displayProgressedSplash();
        splashScreen.getSplashScreen().setBackground(ContextCompat.getDrawable(this,R.mipmap.power1));

        jmESurfaceView.setOnRendererCompleted(application -> {
            splashScreen.hideSplashScreen();
            (findViewById(R.id.gameStickView)).setVisibility(View.VISIBLE);
            (findViewById(R.id.speedometer)).setVisibility(View.VISIBLE);
            (findViewById(R.id.gamePadbtns)).setVisibility(View.VISIBLE);
        });
        jmESurfaceView.setJMEGame(jmeGame);
        jmESurfaceView.startRenderer(100);
        ImageView pause=findViewById(R.id.pause);
        pause.setOnClickListener(v -> {
            final OptionPane optionPane=new OptionPane(JmESurfaceViewExample.this);
            optionPane.showDialog(R.layout.dialog_exception, Gravity.CENTER);
            optionPane.getAlertDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dialog_exception_background));
            EditText errorContainer=optionPane.getInflater().findViewById(R.id.errorText);
            errorContainer.setText("Are You sure ?");
            ((Button)optionPane.getInflater().findViewById(R.id.closeApp)).setText("yes");
            ((Button)optionPane.getInflater().findViewById(R.id.ignoreError)).setText("no");
            optionPane.getInflater().findViewById(R.id.closeApp).setOnClickListener(
                    view -> {
                        optionPane.getAlertDialog().dismiss();
                        jmESurfaceView.getSimpleApplication().stop(jmESurfaceView.isGLThreadPaused());
                        jmESurfaceView.getSimpleApplication().destroy();
                        finish();
                    });
            optionPane.getInflater().findViewById(R.id.ignoreError).setOnClickListener(view -> optionPane.getAlertDialog().dismiss());

        });

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
        optionPane.showDialog(R.layout.dialog_exception, Gravity.CENTER);
        optionPane.getAlertDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.dialog_exception_background));
        EditText errorContainer=optionPane.getInflater().findViewById(R.id.errorText);
        errorContainer.setText("Are You sure ?");
        ((Button)optionPane.getInflater().findViewById(R.id.closeApp)).setText("yes");
        ((Button)optionPane.getInflater().findViewById(R.id.ignoreError)).setText("no");
        optionPane.getInflater().findViewById(R.id.closeApp).setOnClickListener(
                view -> {
                    optionPane.getAlertDialog().dismiss();
                    jmESurfaceView.getSimpleApplication().stop(jmESurfaceView.isGLThreadPaused());
                    jmESurfaceView.getSimpleApplication().destroy();
                    finish();
        });


        optionPane.getInflater().findViewById(R.id.ignoreError).setOnClickListener(view -> optionPane.getAlertDialog().dismiss());

    }
}