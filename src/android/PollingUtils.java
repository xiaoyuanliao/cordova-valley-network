package com.chinavvv.plugin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Map;


public class PollingUtils {


  /**
   * @param context
   * @param seconds
   * @param cls
   * @param action
   */

  public static void startPollingService(Context context, int seconds, Class<?> cls, String action, String type, Map<String,String> params) {

    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(context, cls);

    intent.setAction(action);
    String ssid = params.get("ssid");
    if(!ssid.equals("") && !ssid.equals("null")){
      intent.putExtra("ssid",ssid);
    }

    PendingIntent pendingIntent = PendingIntent.getService(context, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

    long triggerAtTime = System.currentTimeMillis();

    if(type.equals("once")){
      //只启动一次服务
      triggerAtTime+=seconds * 1000;
      manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent);
    }else{
      manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,seconds * 1000, pendingIntent);
    }
  }

  /**
   * @param context
   * @param cls
   * @param action
   */

  public static void stopPollingService(Context context, Class<?> cls, String action) {

    AlarmManager manager = (AlarmManager) context

      .getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(context, cls);

    intent.setAction(action);

    PendingIntent pendingIntent = PendingIntent.getService(context, 0,

      intent, PendingIntent.FLAG_UPDATE_CURRENT);

    manager.cancel(pendingIntent);

  }

}
