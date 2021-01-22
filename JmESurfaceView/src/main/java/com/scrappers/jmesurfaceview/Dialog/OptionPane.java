package com.scrappers.jmesurfaceview.Dialog;

import android.app.Activity;
import android.view.View;

import com.jme3.app.AndroidHarness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OptionPane {

    private final AppCompatActivity context;
    private View inflater;
    private AlertDialog alertDialog;

    public OptionPane(@NonNull AppCompatActivity context){
        this.context=context;
    }
    public void showDialog(int designedLayout, int gravity){
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            inflater=context.getLayoutInflater().inflate(designedLayout,null);
            builder.setView(inflater);

            alertDialog=builder.create();
            assert  alertDialog.getWindow() !=null;
            alertDialog.getWindow().setGravity(gravity);
            alertDialog.show();

    }
    @NonNull
    public AlertDialog getAlertDialog() {
        return alertDialog;
    }
    public void setGameMode(){
        View decorView=getAlertDialog().getWindow().getDecorView();
        decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    @NonNull
    public View getInflater() {
        return inflater;
    }
}
