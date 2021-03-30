package com.scrappers.superiorExtendedEngine.jmeSurfaceView.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * A class Entity that displays dialogs , custom dialogs using {@link OptionPane#showCustomDialog(int, int)}
 * or basic error dialog using {@link OptionPane#showErrorDialog(Throwable, String)}.
 *
 * @author pavl_g
 */
public class OptionPane implements DialogInterface.OnClickListener {

    private final Activity context;
    private View inflater;
    private AlertDialog alertDialog;

    public OptionPane(@NonNull Activity context){
        this.context=context;
    }
    public void showCustomDialog(int designedLayout, int gravity){
        context.runOnUiThread(()->{
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            inflater=context.getLayoutInflater().inflate(designedLayout,null);
            builder.setView(inflater);
            alertDialog=builder.create();
            assert  alertDialog.getWindow() !=null;
            alertDialog.getWindow().setGravity(gravity);
            alertDialog.show();
        });
    }
    public void showErrorDialog(Throwable throwable,String message){
        context.runOnUiThread(()->{
            AlertDialog alertDialog=new AlertDialog.Builder(context).create();
            alertDialog.setTitle(new StringBuffer(String.valueOf(throwable)));
            alertDialog.setMessage(message);
            alertDialog.setCancelable(true);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"EXIT",this);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"DISMISS", this);
            alertDialog.show();
        });
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                context.finish();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                dialog.dismiss();
                break;
        }
    }
}
