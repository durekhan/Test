package com.example.sindoq;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BgService;
import com.example.sindoq.Database.DatabaseHelper;
import com.example.sindoq.adapter.CustomAppListAdapter;
import com.example.sindoq.listener.RecyclerItemClickListener;
import com.example.sindoq.model.AppListMain;
import com.example.sindoq.ui.GridSpacingItemDecoration;
import com.facebook.stetho.Stetho;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.database.Cursor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
public class BlockAppsActivity extends Activity {

    private static final String TAG = "BlockAppsActivity";

    private CustomAppListAdapter customAppListAdapter;
    private ArrayList<AppListMain> appListMainArrayList;
    private AppListMain appListMain;

    DatabaseHelper databaseHelper;
    private RecyclerView rvAppList;
    private PackageManager packageManager;
    public static final int REQUEST_UNINSTALL = 222;
    int selectedPos = 0;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 2323;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Blockk Appps","On create of Block apps called");
        setContentView(R.layout.activity_app_list);




        //////////////////////////////////


        if (hasPermission(this)) {
            new MaterialAlertDialogBuilder(BlockAppsActivity.this, R.style.AlertDialogTheme)
                    .setTitle("Usage Access Permission Required")
                    .setMessage( "\n" + "Sindoq Requires Usage Permission" + "\n" + "\n"  +
                            "\n" + "-" + "Click on Sindoq" + "\n" + "-" + "Enable Usage Tracking " + "\n" + "\n" )
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new
                                    Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                    !Settings.canDrawOverlays(getApplicationContext())) {
                                RequestPermission();
                            }


                        }
                    })
                    .setNeutralButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Toast.makeText(getApplicationContext(), "Permission Required", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(BlockAppsActivity.this,TimerActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();


        }










        /////////////////////////////////////
        databaseHelper = new DatabaseHelper(this);

        Stetho.initializeWithDefaults(this);
        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            startForegroundService(new Intent(this, BgService.class));
        }

        else
        {
            startService(new Intent(this, BgService.class));

        }*/

        loadApps();
        loadListView();

    }

    public void loadApps() {
        try {
            packageManager = getPackageManager();
            appListMainArrayList = new ArrayList<>();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfoList) {
                AppListMain appListMain = new AppListMain();
                appListMain.setAppIcon(resolveInfo.activityInfo.loadIcon(packageManager));
                appListMain.setAppName(resolveInfo.loadLabel(packageManager).toString());
                appListMain.setAppPackage(resolveInfo.activityInfo.packageName);
                appListMain.setAppSelected(false);
                if(!"Sindoq".equals(appListMain.getAppName().toString()))
                {appListMainArrayList.add(appListMain);}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadListView() {
        int mColumnCount = 4;
        rvAppList = findViewById(R.id.rvAppList);
        rvAppList.setLayoutManager(new LinearLayoutManager(this));
        //rvAppList.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        //rvAppList.addItemDecoration(new GridSpacingItemDecoration(10)); // 16px. In practice, you'll want to use getDimensionPixelSize

        Collections.sort(appListMainArrayList, new Comparator<AppListMain>() {
            @Override
            public int compare(AppListMain lhs, AppListMain rhs) {
                return lhs.getAppName().toString().compareTo(rhs.getAppName().toString());
            }

        });
        customAppListAdapter = new CustomAppListAdapter(this, appListMainArrayList);
        rvAppList.setAdapter(customAppListAdapter);

        /////////////////////// load all apps into db initally//////////////
        AppListMain app;

            for (int i = 0; i < appListMainArrayList.size(); i++) {
                app = appListMainArrayList.get(i);
                if(!"Sindoq".equals(app.getAppName().toString()) && !"Settings".equals(app.getAppName().toString())) {
                    if (databaseHelper.CHeckIfAppExistsInUnblocked(app.getAppName().toString()) == false) {
                        if (!databaseHelper.CHeckIfAppExists(app.getAppName().toString())) {

                            databaseHelper.insertapp(app.getAppName().toString(), app.getAppPackage().toString());
                        }
                    }
                }

            }


        //////////////////////////////////////////////////////////////////

        rvAppList.addOnItemTouchListener(new RecyclerItemClickListener(this, rvAppList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                appListMain = appListMainArrayList.get(position);
                if (appListMain != null) {
                    if(!"Sindoq".equals(appListMain.getAppName().toString())  && !"Settings".equals(appListMain.getAppName().toString() )) {
                        if (databaseHelper.CHeckIfAppExists(appListMain.getAppName().toString())) {
                            appListMain.setAppSelected(true);
                            databaseHelper.deleteApp(appListMain.getAppName().toString()); //delete from bloc); //Add in Unblocked Apps
                            Bitmap bitmap = drawableToBitmap(appListMain.getAppIcon());
                            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                            byte[] img = byteArray.toByteArray();
                            databaseHelper.insertUnBlockedApp(appListMain.getAppName().toString(), appListMain.getAppPackage().toString(),img); //Add in Unblocked Apps
                            customAppListAdapter.notifyDataSetChanged();
                        } else {
                            databaseHelper.deleteUnBlockedApp(appListMain.getAppName().toString());
                            //Bitmap bitmap = ((BitmapDrawable)appListMain.getAppIcon()).getBitmap();
                            //Bitmap bitmap = drawableToBitmap(appListMain.getAppIcon());
                            //ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                            //byte[] img = byteArray.toByteArray();
                            databaseHelper.insertapp(appListMain.getAppName().toString(), appListMain.getAppPackage().toString());
                            customAppListAdapter.notifyDataSetChanged();
                        }
                    }

                    }

                    //Intent intent = packageManager.getLaunchIntentForPackage(appListMainArrayList.get(position).getAppPackage().toString());
                    //startActivity(intent);
                }


            @Override
            public void onItemLongClick(View view, int position) {
                appListMain = appListMainArrayList.get(position);
                if (appListMain != null) {
                   /* Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:"+appListMain.getAppPackage()));
                    startActivity(intent);
                    customAppListAdapter.notifyDataSetChanged();*/




                    selectedPos = position;
//                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
//                    intent.setData(Uri.parse("package:" + appListMain.getAppPackage()));
//                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
//                    startActivityForResult(intent, REQUEST_UNINSTALL);

                }
            }
        }));
    }

    @Override
    protected void onResume() {

        super.onResume();
        Log.e("Blockk Appps","On Resume of Block apps called");
        customAppListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UNINSTALL) {
            if (resultCode == RESULT_OK) {
                Log.d("TAG", "onActivityResult: user accepted the (un)install");
//                customAppListAdapter.updateList(appListMainArrayList,selectedPos);
                customAppListAdapter.notifyItemRemoved(selectedPos);
                Toast.makeText(this, "Uninstall successfully!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "onActivityResult: user canceled the (un)install");
                // Toast.makeText(this, "System can't uninstall!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_FIRST_USER) {
                Log.d("TAG", "onActivityResult: failed to (un)install");
            }
        }
    }
    public void Next(View v)
    {
        Intent intent=new Intent(this,ConfirmPage.class);
        startActivity(intent);
    }
    private void RequestPermission()
    {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers

            final String packageName = this.getPackageName();
            new MaterialAlertDialogBuilder(BlockAppsActivity.this, R.style.AlertDialogTheme)
                    .setTitle("Appear on top Permission Required")
                    .setMessage( "\n" + "Sindoq Requires Appear on Top Permission" + "\n" + "\n"  +
                            "\n" + "-" + "Click on Sindoq" + "\n" + "-" + "Enable Appear on Top" + "\n" + "\n" )
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + packageName));
                            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);

                        }
                    })
                    .setNeutralButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Toast.makeText(getApplicationContext(), "Permission Required", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(BlockAppsActivity.this,TimerActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();




        }
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public static boolean hasPermission(@NonNull final Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            return ((AppOpsManager) context.getSystemService(APP_OPS_SERVICE)).checkOpNoThrow("android:get_usage_stats", applicationInfo.uid, applicationInfo.packageName) != 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return true;
        }
    }

}
