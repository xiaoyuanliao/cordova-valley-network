package com.chinavvv.plugin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValleyNetworkManager extends CordovaPlugin {
  private CallbackContext callbackContext;
  String[] permissions = {Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE};

  NetworkUtils networkUtils = null;

  Context context = null;

  JSONArray args = null;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.args = args;
    this.callbackContext = callbackContext;
    context = this.cordova.getActivity().getApplicationContext();
    networkUtils = new NetworkUtils(context);

    if (action.equals("connectWifi")) {
      if (!hasPermisssion()) {
        PermissionHelper.requestPermissions(this, 100, permissions);
      } else {
        JSONObject result = connectWifi();
        Map<String, String> params = new HashMap<String, String>();
        String ssid = args.getString(0);
        params.put("ssid", ssid);
        int minute = this.args.getInt(3);
        if (minute > 0) {
          PollingUtils.startPollingService(context, minute * 60, RemoveWifiService.class, RemoveWifiService.ACTION, "once", params);
        }
        callbackContext.success(result);
      }
    } else if (action.equals("closeWifi")) {
      networkUtils.closeWifi();
    } else if (action.equals("requestPermission")) {
      if (!hasPermisssion()) {
        PermissionHelper.requestPermissions(this, 100, permissions);
      }
    }
    return true;
  }

  public boolean hasPermisssion() {
    for (String p : permissions) {
      if (!PermissionHelper.hasPermission(this, p)) {
        return false;
      }
    }
    return true;
  }

  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException {
    PluginResult result;
    if (callbackContext != null) {
      for (int r : grantResults) {
        if (r == PackageManager.PERMISSION_DENIED) {
          result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
          callbackContext.sendPluginResult(result);
          return;
        }

      }
      if (requestCode == 100) {
        JSONObject returnData = connectWifi();
        callbackContext.success(returnData);
      }
    }
  }

  private JSONObject connectWifi() throws JSONException {
    String ssid = this.args.getString(0);
    String password = this.args.getString(1);
    int type = this.args.getInt(2);
    JSONObject result = networkUtils.connectWifi(networkUtils.createWifiConfig(ssid, password, type));
    return result;
  }

}
