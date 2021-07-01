package com.myGame.backgroundTest;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.common.util.concurrent.ListenableFuture;
import com.myGame.R;
import com.scrappers.superiorExtendedEngine.tasksUtil.backgroundTask.BackgroundNotifier;
import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import androidx.work.impl.utils.futures.SettableFuture;

public class NotificationExample extends ListenableWorker {
    private final Context context;
    public final static String CHANNEL_ID = "SEE Demo Test";
    private final static int REQUEST_ID = 'I' + 'D';
    private SettableFuture<Result> settableFuture;
    public NotificationExample(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        settableFuture = SettableFuture.create();
        getBackgroundExecutor().execute(() -> {
            String dataBinding = getInputData().getString("Notification-Test");
            assert dataBinding != null;
            if(dataBinding.contains(CHANNEL_ID)){
                BackgroundNotifier.Builder builder = new BackgroundNotifier.Builder(context);
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(REQUEST_ID,
                        builder.buildNotification(CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_car_svgrepo_com)
                                .setContentText("TestText " + CHANNEL_ID)
                                .setAutoCancel(true)
                                .setContentIntent(PendingIntent.getService(context, REQUEST_ID, new Intent(context, TestPendingIntent.class), PendingIntent.FLAG_CANCEL_CURRENT))
                                .addAction(R.drawable.ic_car_svgrepo_com, "Start Testing Jme3 Beta", PendingIntent.getService(context, REQUEST_ID, new Intent(context, TestPendingIntent.class), PendingIntent.FLAG_CANCEL_CURRENT))
//                                .setCustomContentView(new RemoteViews(context.getPackageName(), R.layout.dialog_exception))
//                                .setContent(new RemoteViews(context.getPackageName(), R.layout.dialog_exception))
//                                .setCustomBigContentView(new RemoteViews(context.getPackageName(), R.layout.dialog_exception))
//                                .setCustomHeadsUpContentView(new RemoteViews(context.getPackageName(), R.layout.dialog_exception))
                                .build());
                settableFuture.set(Result.success());
            }
        });
        return settableFuture;
    }
}
