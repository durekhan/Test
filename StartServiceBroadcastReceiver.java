package com.example.sindoq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.BgService;

public class StartServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("START ","starting service broadcast");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            context.startForegroundService(new Intent(context, BgService.class));
        }

        else
        {
            context.startService(new Intent(context, BgService.class));
        }
    }
}
