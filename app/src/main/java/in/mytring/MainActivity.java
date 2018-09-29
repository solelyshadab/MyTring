package in.mytring;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ListenerThread listenerThread = null;

    int sim1LastSignalStrength, sim2LastSignalStrength,sim1NewSignalStrength,sim2NewSignalStrength;
    String sim1Operator="",sim2Operator="",sim1Details = "", sim2Details = "";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive(Context context, Intent intent) {
            sim1NewSignalStrength = intent.getIntExtra(MyTringPhoneStateListener.NEW_SIGNAL_STRENGTH,MyTringPhoneStateListener.DEFAULT_STRENGTH);
            Log.d("MainActivity" ,"Refreshed Signal Stength = " + sim1NewSignalStrength);
            refreshSignalStrength();
        }
    };

    @TargetApi(22)
    public void refreshSignalStrength(){
        TextView sim1TextView = (TextView) findViewById(R.id.Sim1_details_text);
        String[] strings = sim1Details.split(System.lineSeparator());
        if(strings.length>=2) {
            strings[2] = "" + sim1NewSignalStrength + " dBm";
            strings[3] = getSignalQuality(sim1NewSignalStrength);
            sim1Details = strings[0]+ System.lineSeparator() + strings[1] + System.lineSeparator() + strings[2] + System.lineSeparator() + strings[3];
            sim1TextView.setText(sim1Details);
        }
    }

    private String getSignalQuality(int signalStrength){
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
    protected void onResume(){
        super.onResume();
        IntentFilter broadcastFilter = new IntentFilter(LOCAL_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, broadcastFilter);
    }

    public static final String LOCAL_ACTION = "in.mytring.intent_service.ALL_DONE";

    ArrayList<in.mytring.CellTowerData> cellTowerDatas = new ArrayList<in.mytring.CellTowerData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissionAndFindMyNetwork();
        populateCellTowerData();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(this);
        registerReceiver(broadcastReceiver, new IntentFilter(LOCAL_ACTION));
        /*Intent i = new Intent(MainActivity.this, ListenerService.class);
        startService(i);*/
//        if (listenerThread == null) {
//            listenerThread = new ListenerThread(this);
//            listenerThread.start();
//        }
    }

    private void populateCellTowerData() {
        in.mytring.GPSTracker gps = new in.mytring.GPSTracker(this);
        if (gps.canGetLocation()) {
            Double latitude = gps.getLatitude();
            Double longitude = gps.getLongitude();
            // read the CSV
            BufferedReader buffer = null;
            int i = 0;
            try {
                String line;

                InputStream raw = getBaseContext().getResources().openRawResource(R.raw.databangalore_airtel);//getBaseContext().getAssets().open("databangalore.csv");

                buffer = new BufferedReader(new InputStreamReader(raw));
                // How to read file in java line by line?
                while ((line = buffer.readLine()) != null) {
                    i++;
                    String[] parts = line.split(",");
                    if (parts.length < 7 || i % 3 != 0)
                        continue;

                    if ((Math.abs(Double.parseDouble(parts[4]) - longitude) > 0.02 || Math.abs(Double.parseDouble(parts[5]) - latitude) > 0.02))
                        continue;
                    in.mytring.CellTowerData cellTowerData = new in.mytring.CellTowerData(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                            Long.parseLong(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]), parts[6]);
                    cellTowerDatas.add(cellTowerData);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (buffer != null) buffer.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }



    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
            if (listenerThread != null) {
                listenerThread.yield();
            }

        } catch (Exception e) {

        }
        super.onDestroy();

    }

    public void wifiDashboard(View v)
    {
        Intent j = new Intent(MainActivity.this, WifiActivity.class);
        startActivity(j);
        finish();
    }

    public void switchNetwork(View v)
    {
        Intent i = new Intent(MainActivity.this, SwitchNetworkActivity.class);
        startActivity(i);
        finish();
    }


    public void speedTest(View v)
    {
        Intent j = new Intent(MainActivity.this, SpeedTestActivity.class);
        startActivity(j);
        finish();
    }

    public void settings(View v)
    {
        Intent j = new Intent(MainActivity.this, in.mytring.Settings.class);
        startActivity(j);
        finish();
    }

    @Override
    @TargetApi(22)
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        String latitude="",longitude="";
        in.mytring.GPSTracker gps = new in.mytring.GPSTracker(this);
        if(gps.canGetLocation()){
            latitude = Double.toString(gps.getLatitude());
            longitude = Double.toString(gps.getLongitude());
            // \n is for new line
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        LatLng currentLocation = new LatLng(gps.latitude,gps.longitude);

        int pinheight = 130;
        int pinwidth = 70;
        Bitmap mylocationimage = BitmapFactory.decodeResource(getResources(), R.drawable.mylocation1);
        Bitmap mylocationMarker = Bitmap.createScaledBitmap(mylocationimage, pinwidth, pinheight, false);

        mMap.addMarker(new MarkerOptions().position(currentLocation).icon(BitmapDescriptorFactory.fromBitmap(mylocationMarker)));
        //mMap.addMarker(new MarkerOptions().position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14.0f));

        int theight = 150;
        int twidth = 70;
        Bitmap sim1towerimage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_icon1);
        Bitmap sim1towerMarker = Bitmap.createScaledBitmap(sim1towerimage, twidth, theight, false);

        Bitmap sim2towerimage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_icon);
        Bitmap sim2towerMarker = Bitmap.createScaledBitmap(sim2towerimage, twidth, theight, false);

        Bitmap sim1ConnectedTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_connected1);
        Bitmap sim1ConnectedTowerMarker = Bitmap.createScaledBitmap(sim1ConnectedTowerImage, twidth, 200, false);

        Bitmap sim2ConnectedTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_connected);
        Bitmap sim2ConnectedTowerMarker = Bitmap.createScaledBitmap(sim2ConnectedTowerImage, twidth, 200, false);

        CellTowerData sim1ConnectedCell = getNearestTower(currentLocation,sim1Operator);
        CellTowerData sim2ConnectedCell = getNearestTower(currentLocation,sim2Operator);

        if(sim1ConnectedCell!=null){
            LatLng sim1ConnectedTower = new LatLng(sim1ConnectedCell.getLatitude(),sim1ConnectedCell.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sim1ConnectedTower).icon(BitmapDescriptorFactory.fromBitmap(sim1ConnectedTowerMarker)));
            cellTowerDatas.remove(sim1ConnectedCell);
        }
        if(sim2ConnectedCell!=null){
            LatLng sim2ConnectedTower = new LatLng(sim2ConnectedCell.getLatitude(),sim2ConnectedCell.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sim2ConnectedTower).icon(BitmapDescriptorFactory.fromBitmap(sim2ConnectedTowerMarker)));
            cellTowerDatas.remove(sim2ConnectedCell);
        }
        ArrayList<CellTowerData> sim1Cells = filterCells(sim1Operator);
        if(sim1Cells!=null){
            for(in.mytring.CellTowerData celldata: sim1Cells){
                LatLng tower = new LatLng(celldata.getLatitude(),celldata.getLongitude());
                mMap.addMarker(new MarkerOptions().position(tower).icon(BitmapDescriptorFactory.fromBitmap(sim2towerMarker)));
                //mMap.addMarker(new MarkerOptions().position(tower));
            }
        }

        ArrayList<CellTowerData> sim2Cells = filterCells(sim2Operator);
        if(sim2Cells!=null){
            for(in.mytring.CellTowerData celldata: sim2Cells){
                LatLng tower = new LatLng(celldata.getLatitude(),celldata.getLongitude());
                mMap.addMarker(new MarkerOptions().position(tower).icon(BitmapDescriptorFactory.fromBitmap(sim2towerMarker)));
                //mMap.addMarker(new MarkerOptions().position(tower));
            }
        }
    }

    private ArrayList<CellTowerData> filterCells(String operatorName){
        if(operatorName==null){
            return null;
        }
        ArrayList<CellTowerData> filteredCells = new ArrayList<>();
        for(CellTowerData celldata:cellTowerDatas){
            if (operatorName.toLowerCase().contains("jio") && celldata.getMcc()==405 && celldata.getMnc()==10) {
                filteredCells.add(celldata);
            }else if (operatorName.toLowerCase().contains("airtel") && celldata.getMcc()==404 && celldata.getMnc()==45) {
                filteredCells.add(celldata);
            } else if (operatorName.toLowerCase().contains("vodafone") && celldata.getMcc()==404 && celldata.getMnc()==86) {
                filteredCells.add(celldata);
            }
        }
        return filteredCells;
    }

    private CellTowerData getNearestTower (LatLng currentLatLong,String operatorName){
        if(operatorName==null || operatorName.isEmpty()){
            return null;
        }
        Location currentLocation = new Location("Base Location");
        currentLocation.setLatitude(currentLatLong.latitude);
        currentLocation.setLongitude(currentLatLong.longitude);
        float distance= Float.MAX_VALUE;
        ArrayList<CellTowerData> nearestTowers = new ArrayList<>();
        CellTowerData nearestCell =null;
        for(in.mytring.CellTowerData celldata: cellTowerDatas) {
            if (operatorName.toLowerCase().contains("jio") && celldata.getMcc()==405 && celldata.getMnc()==10) {
                Location newLocation = new Location("New Location");
                newLocation.setLatitude(celldata.getLatitude());
                newLocation.setLongitude(celldata.getLongitude());
                if(distance>currentLocation.distanceTo(newLocation)){
                    distance=currentLocation.distanceTo(newLocation);
                    nearestCell = celldata;
                }
            } else if (operatorName.toLowerCase().contains("airtel") && celldata.getMcc()==404 && celldata.getMnc()==45) {
                Location newLocation = new Location("New Location");
                newLocation.setLatitude(celldata.getLatitude());
                newLocation.setLongitude(celldata.getLongitude());
                if(distance>currentLocation.distanceTo(newLocation)){
                    distance=currentLocation.distanceTo(newLocation);
                    nearestCell = celldata;
                }
            } else if (operatorName.toLowerCase().contains("vodafone") && celldata.getMcc()==404 && celldata.getMnc()==86) {
                Location newLocation = new Location("New Location");
                newLocation.setLatitude(celldata.getLatitude());
                newLocation.setLongitude(celldata.getLongitude());
                if(distance>currentLocation.distanceTo(newLocation)){
                    distance=currentLocation.distanceTo(newLocation);
                    nearestCell = celldata;
                }
            }
        }
        return nearestCell;
    }

    private static final int REQUEST_PERMISSIONS = 20;


    public void getPermissionAndFindMyNetwork(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE)){
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE},REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE},REQUEST_PERMISSIONS);
            }
        }else {
            //Call whatever you want
            findMyNetwork();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    //Call whatever you want
                    findMyNetwork();
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

    @TargetApi(23)
    public void findMyNetwork() {
        TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        SubscriptionManager subscriptionManager = SubscriptionManager.from(getBaseContext());
        List<SubscriptionInfo> subscriptions = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptions != null){
            if(subscriptions.size()==1){
                sim1Operator = subscriptions.get(0).getCarrierName().toString();
                sim2Details = "SIM2 Details: "+ System.lineSeparator() + "No Connection";
            }else{
                sim1Operator = subscriptions.get(0).getCarrierName().toString();
                sim2Operator = subscriptions.get(1).getCarrierName().toString();
            }
        }
        List<CellInfo> listAllCellInfo = tManager.getAllCellInfo();
        if (null != tManager && tManager.getPhoneCount() > 1) {
            for (ListIterator<CellInfo> itr = listAllCellInfo.listIterator(); itr.hasNext(); ) {
                CellInfo currentCellInfo = itr.next();
                if (currentCellInfo.isRegistered()) {
                    if (null == sim1Details || sim1Details.isEmpty()) {
                        sim1LastSignalStrength =sim1NewSignalStrength= getSignalStrength(currentCellInfo);
                        sim1Details = sim1Details + "SIM1 Details: "+ System.lineSeparator() + getNetworkType(currentCellInfo) + " | " + sim1Operator + System.lineSeparator() + sim1NewSignalStrength + " dBm" + System.lineSeparator() + getSingalQuality(currentCellInfo);
                    } else if (null == sim2Details || sim2Details.isEmpty()) {
                        sim2LastSignalStrength =sim2NewSignalStrength= getSignalStrength(currentCellInfo);
                        sim2Details = sim2Details + "SIM2 Details: "+ System.lineSeparator() + getNetworkType(currentCellInfo) + " | " + sim2Operator + System.lineSeparator() + sim2NewSignalStrength + " dBm" + System.lineSeparator() + getSingalQuality(currentCellInfo);
                    }
                }
            }
            TextView sim1Text = (TextView) findViewById( R.id.Sim1_details_text);
            sim1Text.setText(sim1Details);
            TextView sim2Text = (TextView) findViewById( R.id.Sim2_details_text);
            sim2Text.setText(sim2Details);
        } else {
            for (ListIterator<CellInfo> itr = listAllCellInfo.listIterator(); itr.hasNext(); ) {
                CellInfo currentCellInfo = itr.next();
                if (currentCellInfo.isRegistered()) {
                    if (null == sim1Details || sim1Details.isEmpty()) {
                        sim1Details = sim1Details +"SIM1 Details: "+ System.lineSeparator() + getNetworkType(currentCellInfo) + " | " + sim1Operator + System.lineSeparator() + getSignalStrength(currentCellInfo) + " dBm" + System.lineSeparator() + getSingalQuality(currentCellInfo);
                    }
                }
            }
            TextView sim1Text = (TextView) findViewById( R.id.Sim1_details_text);
            sim1Text.setText(sim1Details);
            TextView sim2Text = (TextView) findViewById( R.id.Sim2_details_text);
            sim2Text.setText("SIM2 Details: "+ System.lineSeparator() + "No Connection");
        }
    }

    @TargetApi(18)
    int getSignalStrength(CellInfo currentCellInfo){
        if (currentCellInfo instanceof CellInfoWcdma) {
            return ((CellInfoWcdma) currentCellInfo).getCellSignalStrength().getDbm();
        } else if (currentCellInfo instanceof CellInfoLte) {
            Log.d("LTE Parameters:",((CellInfoLte) currentCellInfo).getCellSignalStrength().toString());
            return ((CellInfoLte)   currentCellInfo).getCellSignalStrength().getDbm();
        } else if (currentCellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) currentCellInfo).getCellSignalStrength().getDbm();
        } else {
            return ((CellInfoCdma) currentCellInfo).getCellSignalStrength().getDbm();
        }
    }

    String getNetworkType(CellInfo currentCellInfo){
        if (currentCellInfo instanceof CellInfoWcdma) {
            return "3G";
        } else if (currentCellInfo instanceof CellInfoLte) {
            return "4G";
        } else
            return "2G";
    }


    String getSingalQuality(CellInfo currentCellInfo){
        int signalStrength = getSignalStrength(currentCellInfo);
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

}
