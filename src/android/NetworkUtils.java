package com.chinavvv.plugin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NetworkUtils {

  private Context context;

  private ConnectivityManager cm;

  private WifiManager wifiManager;

  private static final int WIFI_CIPHER_NO_PASS = 0;
  private static final int WIFI_CIPHER_WEP = 1;
  private static final int WIFI_CIPHER_WPA = 2;

  public NetworkUtils(Context context) {
    super();
    this.context = context;
    cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
  }

  public JSONObject getCurrentApn() throws JSONException {
    NetworkInfo ni = cm.getActiveNetworkInfo();
    if (ni != null) {
      return processApn(ni);
    } else {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      ni = cm.getActiveNetworkInfo();
      return processApn(ni);
    }
  }

  private JSONObject processApn(NetworkInfo ni) throws JSONException {
    JSONObject result = new JSONObject();
    if (ni != null) {
      result.put("apn", ni.getExtraInfo());
      result.put("typeName", ni.getTypeName());
      result.put("subTypeName", ni.getSubtypeName());
      result.put("type", ni.getType());
    }
    return result;
  }

  public boolean connectWifi(WifiConfiguration wifiConfiguration) {
    openWifi();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
      try {
        // 为了避免程序一直while循环，让它睡个100毫秒检测……
        Thread.sleep(100);
      } catch (InterruptedException ie) {
      }
    }

    int netId = wifiManager.addNetwork(wifiConfiguration);
    boolean enable = wifiManager.enableNetwork(netId, true);
    Log.d(context.getPackageName(), "enable: " + enable);
    boolean reconnect = wifiManager.reconnect();
    Log.d(context.getPackageName(), "reconnect: " + reconnect);
    return reconnect;
  }

  /**
   * 关闭WIFI网络状态
   */
  public boolean closeWifi() {
    Log.v("WIFI_STATE", String.valueOf(wifiManager.getWifiState()));

    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
      boolean result = wifiManager.setWifiEnabled(false);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return result;
    }
    return true;
  }

  public WifiConfiguration createWifiConfig(String ssid, String password, int type) {
    WifiConfiguration config = new WifiConfiguration();
    config.allowedAuthAlgorithms.clear();
    config.allowedGroupCiphers.clear();
    config.allowedKeyManagement.clear();
    config.allowedPairwiseCiphers.clear();
    config.allowedProtocols.clear();
    config.SSID = "\"" + ssid + "\"";
    ;

    WifiConfiguration tempConfig = isExist(ssid);
    if (tempConfig != null) {
      config.SSID = tempConfig.SSID;
      wifiManager.removeNetwork(tempConfig.networkId);
    }

    if (type == WIFI_CIPHER_NO_PASS) {
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    } else if (type == WIFI_CIPHER_WEP) {
      config.hiddenSSID = true;
      config.wepKeys[0] = "\"" + password + "\"";
      config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
      config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      config.wepTxKeyIndex = 0;
    } else if (type == WIFI_CIPHER_WPA) {
      config.preSharedKey = "\"" + password + "\"";
      config.hiddenSSID = true;
      config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
      config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
      config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
      config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
      config.status = WifiConfiguration.Status.ENABLED;
    }

    return config;
  }

  // 打开wifi功能
  private boolean openWifi() {
    boolean bRet = true;
    if (!wifiManager.isWifiEnabled()) {
      bRet = wifiManager.setWifiEnabled(true);
    }
    return bRet;
  }

  public void removeWifi(String targetSsid) {
    List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
    for (WifiConfiguration wifiConfig : wifiConfigs) {
      if (wifiConfig.SSID.equals("\"" + targetSsid + "\"")) {
        wifiManager.disableNetwork(wifiConfig.networkId);
        wifiManager.removeNetwork(wifiConfig.networkId);
        wifiManager.saveConfiguration();
      }
    }

  }

  private WifiConfiguration isExist(String ssid) {
    List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();

    if (configs != null) {
      for (WifiConfiguration config : configs) {
        if (config.SSID.equals("\"" + ssid + "\"")) {
          return config;
        }
      }
    }
    return null;
  }

}
