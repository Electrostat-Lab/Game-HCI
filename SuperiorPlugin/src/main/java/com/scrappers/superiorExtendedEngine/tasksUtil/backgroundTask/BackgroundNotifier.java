package com.scrappers.superiorExtendedEngine.tasksUtil.backgroundTask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * <b>Code design</b>
 * <ol>
 *<li> <s> BackgroundNotifier does have a builder class to build notification UI & handle the click listeners inside it </s></li>
 *<li> BackgroundNotifier register a notifier to the system with some delay inputs, some data constraints(Binders), System flags</li>
 *<li> Another class for Nominations stacks </li>
 * </ol>
 * @author pavl_g
 */
public class BackgroundNotifier {

    /**
     * A subclass that would inflate notifications with UI-Layouts
     */
    public static class NotificationInflater{
        private final Context context;
        public NotificationInflater(final Context context){
            this.context = context;
        }
        public View inflateNotification(@LayoutRes int resId){
            return LayoutInflater.from(context).inflate(resId, null);
        }
    }

    public static class Builder{
        private final Context context;
        public Builder(final Context context){
            this.context = context;
        }

        /**
         * Build a notiification using custom design
         * @param inflatedView
         * @param constraints
         * @param systemFlags
         * @return
         */
        public NotificationCompat.Builder buildNotification(@Nullable View inflatedView){
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                    NotificationChannel channel = new NotificationChannel(String.valueOf(inflatedView.getId()), String.valueOf(inflatedView.getId()),  NotificationManager.IMPORTANCE_HIGH);
                    channel.enableLights(true);
                    channel.enableVibration(true);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ){
                        channel.setAllowBubbles(true);
                    }
                    notificationManager.createNotificationChannel(channel);
                }
                assert inflatedView != null;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, String.valueOf(inflatedView.getId()));
                if(inflatedView != null){
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), inflatedView.getId());
                    builder.setCustomContentView(remoteViews);
                }
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setAutoCancel(false);
            return builder;
        }

        /**
         * Build regular notification
         */
        public NotificationCompat.Builder buildNotification(String channelID){
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                    NotificationChannel channel = new NotificationChannel(channelID, channelID,  NotificationManager.IMPORTANCE_HIGH);
                    channel.enableLights(true);
                    channel.enableVibration(true);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ){
                        channel.setAllowBubbles(true);
                    }
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setAutoCancel(false);
            return builder;
        }

        public Context getContext() {
            return context;
        }

    }


}
