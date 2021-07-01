package com.scrappers.superiorExtendedEngine.tasksUtil.backgroundTask;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.RequiresApi;

public class ServiceBuilder {
    private final Context context;
    private final int requestCode;
    public static final int CONVERT_TO_SECONDS = 1000;
    public static final int CONVERT_TO_MINUTES = CONVERT_TO_SECONDS * 60;
    public static final int CONVERT_TO_HOURS = CONVERT_TO_MINUTES * 60;
    public ServiceBuilder(final Context context, final int requestCode){
        super();
        this.context = context;
        this.requestCode = requestCode;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public JobInfo.Builder startService(Class<? extends BackgroundService> clazz, int minDelay, int maxDelay){
            ComponentName serviceComponent = new ComponentName(context, clazz);
            JobInfo.Builder builder = new JobInfo.Builder(requestCode, serviceComponent);
            builder.setMinimumLatency(minDelay); // wait at least
            builder.setOverrideDeadline(maxDelay); // maximum delay
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            //builder.setRequiresDeviceIdle(true); // device should be idle
            //builder.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
        return builder;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public Intent startServiceICS(Class<? extends ForegroundService> clazz, int delayMillis, int delayDownInterval){
        Intent intent = new Intent(context, clazz);
        if(delayMillis == 0){
            context.startService(intent);
        }else{
            new CountDownTimer(delayMillis, delayDownInterval) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    context.startService(intent);
                    this.cancel();
                }
            }.start();
        }
        return intent;
    }

}
