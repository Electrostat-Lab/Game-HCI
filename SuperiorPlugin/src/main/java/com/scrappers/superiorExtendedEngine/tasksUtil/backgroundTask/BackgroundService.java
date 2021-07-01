package com.scrappers.superiorExtendedEngine.tasksUtil.backgroundTask;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

@SuppressLint("SpecifyJobSchedulerIdRange")
@RequiresApi(api = Build.VERSION_CODES.M)
public abstract class BackgroundService extends JobService {
    private final Task task = new Task();
    private final Handler handler = new Handler();
    @Override
    public boolean onStartJob(JobParameters params) {
        synchronized(task){
            handler.post(task);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        synchronized(task){
            handler.removeCallbacks(task);
        }
        return true;
    }
    public class Task implements Runnable{
        @SuppressLint("SpecifyJobSchedulerIdRange")
        @Override
        public void run() {
            listen(null);
        }
    }
    public abstract void listen(Intent intent);
}
