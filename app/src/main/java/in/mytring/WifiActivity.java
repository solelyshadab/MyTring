package in.mytring;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Prem on 2/15/2017.
 */

public class WifiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_layout);
        //lv= (ListView) findViewById(R.id.list);
        //getWifiNetworksList();
        registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        getPermissionAndStartActivity();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void mobileDashboard(View v) {
        Intent i = new Intent(WifiActivity.this, in.mytring.MainActivity.class);
        startActivity(i);
        finish();
    }

    public void switchNetwork(View v) {
        Intent i = new Intent(WifiActivity.this, SwitchNetworkActivity.class);
        startActivity(i);
        finish();
    }


    public void speedTest(View v) {
        Intent j = new Intent(WifiActivity.this, SpeedTestActivity.class);
        startActivity(j);
        finish();
    }

    public void settings(View v)
    {
        Intent j = new Intent(WifiActivity.this, in.mytring.Settings.class);
        startActivity(j);
        finish();
    }

    /*private int size = 0;
    private ListView lv;
    List<ScanResult> scanList;
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    private void getWifiNetworksList(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final WifiManager wifiManager =
                (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        this.adapter = new SimpleAdapter(WifiActivity.this, arraylist, R.layout.activity_list_view_items, new String[]{ITEM_KEY}, new int[]{R.id.textView, R.id.imageView});
        lv.setAdapter(this.adapter);
        registerReceiver(new BroadcastReceiver(){

            @SuppressLint("UseValueOf") @Override
            public void onReceive(Context context, Intent intent) {

                scanList = wifiManager.getScanResults();
                size = scanList.size();

                try {
                    size = size - 1;
                    while (size >= 0) {
                        HashMap<String, String> item = new HashMap<>();
                        item.put(ITEM_KEY, scanList.get(size).SSID + "  " + scanList.get(size).capabilities);

                        arraylist.add(item);
                        size--;
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                }
            }

        },filter);
        wifiManager.startScan();
        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
    }*/

    private static final String TAG = "WiFiActivity";
    WifiManager wifi;
   /* ListView lv;*/
    int size = 0;
    List<ScanResult> results;
    String SSIDs="", networkType="", strengths="";


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        @TargetApi(22)
        public void onReceive(Context c, Intent intent) {
            if(null!=arraylist && !arraylist.isEmpty()){
               arraylist.clear();
            }
            if(null!=results && !results.isEmpty()){
               results.clear();
            }
            results = wifi.getScanResults();
            size = results.size();
            Log.d(TAG, " Size = "+ results.size()+"\n");
            try {
                size = size - 1;
                while (size >= 0) {
                    HashMap<String, String> item = new HashMap<>();
                    Log.d(TAG, "KEY=" + ITEM_KEY+ "; Size= "+ size + "; SSID= " + results.get(size).SSID + "; Signal=  " + results.get(size).level + "\n");
                    if (results.get(size).SSID != null && !results.get(size).SSID.isEmpty() && !results.get(size).SSID.equals(wifi.getConnectionInfo().getSSID().substring(1,wifi.getConnectionInfo().getSSID().length()-1))) {
                        item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).level);

                        arraylist.add(item);
                        //adapter.notifyDataSetChanged();
                        SSIDs = SSIDs + results.get(size).SSID + System.lineSeparator() + System.lineSeparator();
                        if (results.get(size).capabilities.toUpperCase().contains("WEP")
                                || results.get(size).capabilities.toUpperCase().contains("WPA")
                                || results.get(size).capabilities.toUpperCase().contains("WPA2")) {
                            networkType = networkType + "Secured" + System.lineSeparator() + System.lineSeparator();
                        } else {
                            // Open Network
                            networkType = networkType + "Open" + System.lineSeparator() + System.lineSeparator();
                        }
                        strengths = strengths + results.get(size).level + System.lineSeparator() + System.lineSeparator();
                    }
                    size--;
                }
                displayAllWifis(SSIDs,networkType,strengths);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    private static final int REQUEST_PERMISSIONS = 20;

    public void getPermissionAndStartActivity(){
        if (ContextCompat.checkSelfPermission(WifiActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(WifiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(WifiActivity.this, android.Manifest.permission.ACCESS_WIFI_STATE) +
                ContextCompat.checkSelfPermission(WifiActivity.this, android.Manifest.permission.CHANGE_WIFI_STATE) +
                ContextCompat.checkSelfPermission(WifiActivity.this, android.Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(WifiActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)||
                    ActivityCompat.shouldShowRequestPermissionRationale(WifiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(WifiActivity.this, android.Manifest.permission.ACCESS_WIFI_STATE)||
                    ActivityCompat.shouldShowRequestPermissionRationale(WifiActivity.this, android.Manifest.permission.CHANGE_WIFI_STATE)||
                    ActivityCompat.shouldShowRequestPermissionRationale(WifiActivity.this, android.Manifest.permission.INTERNET)){
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(WifiActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                android.Manifest.permission.ACCESS_WIFI_STATE,android.Manifest.permission.CHANGE_WIFI_STATE,android.Manifest.permission.INTERNET}
                                        ,REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(WifiActivity.this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_WIFI_STATE,
                                android.Manifest.permission.CHANGE_WIFI_STATE,android.Manifest.permission.INTERNET}, REQUEST_PERMISSIONS);
            }
        }else {
            //Call whatever you want
            startActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (null!=grantResults && (grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    //Call whatever you want
                    startActivity();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }

    //* Called when the activity is first created. *//*
    @TargetApi(22)
    public void startActivity() {
        /*this.adapter = new SimpleAdapter(WifiActivity.this, arraylist, R.layout.activity_list_view_items, new String[]{ITEM_KEY}, new int[]{R.id.textView, R.id.imageView});
        lv.setAdapter(this.adapter);*/

        WifiInfo connectedWifi = wifi.getConnectionInfo();

        String wifiDetails = "Connected To: " + connectedWifi.getSSID()+System.lineSeparator()+connectedWifi.getLinkSpeed()+"Mbps | "+ getSingalQuality(connectedWifi.getRssi());

        TextView connectedWiFiDetails = (TextView) findViewById(R.id.Wifi_details_text);
        connectedWiFiDetails.setText(wifiDetails);

        arraylist.clear();
        wifi.startScan();
        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
    }

    String getSingalQuality(int signalStrength){
        if(signalStrength>-75){
            return "Excellent";
        }else if(signalStrength>-90){
            return "Good";
        }else if(signalStrength>-100){
            return "Poor";
        }else if(signalStrength>-109){
            return "Very Poor";
        }else{
            return "No Signal";
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
        super.onDestroy();

    }


    public void displayAllWifis(String SSIDs, String networkType, String strengths){
        TextView displayAllSSIDs = (TextView) findViewById(R.id.scrollView_SSIDText);
        displayAllSSIDs.setText(SSIDs);
        TextView displayAllNWTypes = (TextView) findViewById(R.id.scrollView_networkTypeText);
        displayAllNWTypes.setText(networkType);
        TextView displayAllWifiStrength = (TextView) findViewById(R.id.scrollView_StrengthText);
        displayAllWifiStrength.setText(strengths);

    }
}