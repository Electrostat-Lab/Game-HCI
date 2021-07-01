package com.myGame.backgroundTest;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.myGame.R;
import com.scrappers.superiorExtendedEngine.tasksUtil.backgroundTask.BackgroundNotifier;
import com.scrappers.superiorExtendedEngine.tasksUtil.backgroundTask.BackgroundService;
import static android.widget.Toast.LENGTH_LONG;

/**
 * Testing Background services for android 8 0 & above, using JobScheduler Manager of Android System.
 * <br>
 * <b>You must define this subclass in a <code>Service</code> tag </b>
 * @author pavl_g
 */
@SuppressLint("SpecifyJobSchedulerIdRange")
public class Back extends BackgroundService {
    int display=0;
    @SuppressLint("SpecifyJobSchedulerIdRange")
    @Override
    public void listen(Intent intent) {
        display+=1;
        BackgroundNotifier.Builder builder = new BackgroundNotifier.Builder(getApplication());
        ((NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE)).notify(2020,
                builder.buildNotification("2020")
                .setSmallIcon(R.drawable.ic_car_svgrepo_com)
                .setContentText("TestText "+display)
                .setColorized(true)
                .build());
        Toast.makeText(getApplication(), "hassasaasey", LENGTH_LONG).show();
    }
}
