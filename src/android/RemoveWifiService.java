package com.chinavvv.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RemoveWifiService extends Service {

  public static final String ACTION = "com.chinavvv.plugin.RemoveWifiService";

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(getPackageName(),"服务启动");
    NetworkUtils networkUtils = new NetworkUtils(getApplicationContext());
    String ssid = intent.getStringExtra("ssid");
    networkUtils.removeWifi(ssid);
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

}
