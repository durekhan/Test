package com.example.sindoq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sindoq.screens.BlockPage;

public class TimerActivityBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Timer","TIMER BROADCAST CALLED");
        Intent intent2= new Intent(context, TimerActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
    }
}
