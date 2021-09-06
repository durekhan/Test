package com.example.sindoq;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.os.Build;

public class MyApp extends Application {
    public static final String CHANNEL_ID = "autoStartServiceChannel";
    public static final String CHANNEL_NAME = "Auto Start Service Channel";
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter("com.example.sindoq.intent.action.ACTION_SHOW_TOAST");

        BlockBroadcastReceiver receiver = new BlockBroadcastReceiver();
        registerReceiver(receiver, filter);

        IntentFilter filter3 = new IntentFilter("com.example.sindoq.intent.action.TimerActivityIntent");
        TimerActivityBroadcastReciever reciver1= new TimerActivityBroadcastReciever();
        registerReceiver(reciver1,filter3);

        IntentFilter filter1 = new IntentFilter("com.example.sindoq.intent.action.stopservice");
        StopServiceBroadcastReceiver sendBroadcastReceiver1 = new StopServiceBroadcastReceiver();
        registerReceiver(sendBroadcastReceiver1, filter1);

        IntentFilter filter2 = new IntentFilter("com.example.sindoq.intent.action.startservice");
        StartServiceBroadcastReceiver sendBroadcastReceiver2 = new StartServiceBroadcastReceiver();
        registerReceiver(sendBroadcastReceiver2, filter2);

        createNotificationChannel();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}