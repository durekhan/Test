package com.example.sindoq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.sindoq.screens.BlockPage;

public class BlockBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("From Broadcast Receiver", "RECEIVED");

        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
        //{
          //  Toast.makeText(context, "Android version>10", Toast.LENGTH_LONG).show();


        //}
        //else {
            Intent intent2 = new Intent(context, BlockPage.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        //}
    }
}