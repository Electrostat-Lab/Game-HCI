package com.myGame;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MotionEvent;

import com.jme3.app.AndroidHarness;

import java.util.logging.Level;
import java.util.logging.LogManager;

public class JmeHarness extends AndroidHarness {

    public static AssetManager assetManager;
    public static JmeHarness jmeHarness;
    public JmeHarness() {

        // Set the desired EGL configuration
        eglBitsPerPixel = 24;
        eglAlphaBits = 0;
        eglDepthBits = 16;
        eglSamples = 0;
        eglStencilBits = 0;

        // Set the maximum framerate
        // (default = -1 for unlimited)
        frameRate = -1;

        // Set main project class (fully qualified path)
        // the class that extends SimpleApplication
        appClass = "com.myGame.JmeGame";


//        // Set input configuration settings
//        joystickEventsEnabled = true;
//        keyEventsEnabled = true;
//        mouseEventsEnabled = true;

        // Set application exit settings
        finishOnAppStop = false;
        handleExitHook = true;
        exitDialogTitle = "Do you want to exit?";
        exitDialogMessage = "Use your home key to bring this app into the background or exit to terminate it.";

        // Set splash screen resource id, if used
        // (default = 0, no splash screen)
        // For example, if the image file name is "splash"...
        //     splashPicID = R.drawable.splash;
        splashPicID = 0;

        // splashPicID = R.drawable.jme_white;

        // Set the default logging level (default=Level.INFO, Level.ALL=All Debug Info)
        LogManager.getLogManager().getLogger("").setLevel(Level.INFO);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager=getAssets();
        jmeHarness=this;


    }


}