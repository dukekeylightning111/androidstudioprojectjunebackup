package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private Context context;

    public MyBroadcastReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    onWifiStateChange(true);
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    onCellularDataStateChange(true);
                }
            } else {
                onWifiStateChange(false);
                onCellularDataStateChange(false);
            }
        }
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, intentFilter);
    }

    public void unregister() {
        context.unregisterReceiver(this);
    }

    public void onWifiStateChange(boolean isWifiOn) {
        // Handle Wi-Fi state change here
    }

    public void onCellularDataStateChange(boolean isCellularDataOn) {
        // Handle cellular data state change here
    }
}
