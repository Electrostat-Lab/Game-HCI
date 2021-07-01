package com.myGame.backgroundTest;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.myGame.JMESurfaceViewExampleActivity.JmESurfaceViewExample;
import androidx.annotation.Nullable;

import static android.widget.Toast.LENGTH_LONG;

public class TestPendingIntent extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public TestPendingIntent() {
        super("name");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent notificationIntent) {
          //start another explicit intent implicitly from an IntentService which is directed using a PendingIntent from a notification action
        Intent intent = new Intent(getApplication(), JmESurfaceViewExample.class);
        //start an activity from outside the context flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Test Pending Intents", LENGTH_LONG).show();
    }
}
