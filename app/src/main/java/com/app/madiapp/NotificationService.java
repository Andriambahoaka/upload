package com.app.madiapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationService {
    private String file_name = "";
    static int ONGOING_NOTIFICATION_ID = 111;
    public NotificationService(FileService fs, Context ct, NotificationManager nt){
        this.fileService = fs;
        this.context = ct;
        _notificationMngr = nt;
    }
    FileService fileService;
    Context context;
    private android.app.NotificationManager _notificationMngr;
    private NotificationCompat.Builder _notificationBuilder;


    private String createNotificationChannelId(){
        String NOTIFICATION_CHANNEL_ID = "madia_app_channel01";
        String channelName = context.getResources().getString(R.string.app_name);
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, android.app.NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        chan.enableLights(true);
        chan.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        chan.enableVibration(true);
        chan.setDescription("Happy channel description");
        this._notificationMngr.createNotificationChannel(chan);
        return NOTIFICATION_CHANNEL_ID;
    }

    public void closeNotif(){
        _notificationMngr.cancel(ONGOING_NOTIFICATION_ID);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _notificationBuilder = (new NotificationCompat.Builder(context, createNotificationChannelId()));
        } else{
            _notificationBuilder = (new NotificationCompat.Builder(context,""));
        }
        _notificationBuilder
                .setContentTitle("completed")
                .setContentText("file : " + file_name + " uploaded to server")
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.FLAG_NO_CLEAR)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setShowWhen(true)
                .setOngoing(true)
                .setContentIntent(pendingIntent).setDeleteIntent(pendingIntent);

        Notification notification = _notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        _notificationMngr.notify(ONGOING_NOTIFICATION_ID, notification);
    }

    public void notifyMadiaapp() {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _notificationBuilder = (new NotificationCompat.Builder(context, createNotificationChannelId()));
        } else{
            _notificationBuilder = (new NotificationCompat.Builder(context,""));
        }
        _notificationBuilder
            .setContentTitle("uploading")
            .setContentText("file : " + file_name)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .setSmallIcon(R.drawable.uplodbarxml).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setDefaults(Notification.FLAG_NO_CLEAR)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(false)
            .setShowWhen(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent).setDeleteIntent(pendingIntent);

            Notification notification = _notificationBuilder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            _notificationMngr.notify(ONGOING_NOTIFICATION_ID, notification);
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }
}
