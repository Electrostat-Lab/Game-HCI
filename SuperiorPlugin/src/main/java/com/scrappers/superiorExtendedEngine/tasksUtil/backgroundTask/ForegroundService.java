package com.scrappers.superiorExtendedEngine.tasksUtil.backgroundTask;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public abstract class ForegroundService extends Service {
    private final Handler handler = new Handler();
    private final Task task = new Task();

    @Override
    public void onDestroy() {
        super.onDestroy();
        synchronized(task){
            handler.removeCallbacks(task);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized(task){
            handler.postAtTime(task, 0);
        }
    }
    public class Task implements Runnable{
        @Override
        public void run() {
            listen(null);
        }
    }
    public abstract void listen(Intent intent);
}
