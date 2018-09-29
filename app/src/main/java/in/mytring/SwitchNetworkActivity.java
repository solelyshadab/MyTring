package in.mytring;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by shadabbaig on 18/01/17.
 */

public class SwitchNetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_network);
        getPermissionAndFindMyNetwork();
        getPermissionAndFindAllNetwork();
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

    public void mobileDashboard(View v)
    {
        Intent i = new Intent(SwitchNetworkActivity.this, in.mytring.MainActivity.class);
        startActivity(i);
        finish();
    }

    public void wifiDashboard(View v)
    {
        Intent j = new Intent(SwitchNetworkActivity.this, WifiActivity.class);
        startActivity(j);
        finish();
    }

    public void speedTest(View v)
    {
        Intent j = new Intent(SwitchNetworkActivity.this, SpeedTestActivity.class);
        startActivity(j);
        finish();
    }

    public void settings(View v)
    {
        Intent j = new Intent(SwitchNetworkActivity.this, in.mytring.Settings.class);
        startActivity(j);
        finish();
    }

    private static final int REQUEST_PERMISSIONS = 20;
    ListView lv;

    public void getPermissionAndFindMyNetwork(){
        if (ContextCompat.checkSelfPermission(SwitchNetworkActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(SwitchNetworkActivity.this, android.Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SwitchNetworkActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)||
                    ActivityCompat.shouldShowRequestPermissionRationale(SwitchNetworkActivity.this, android.Manifest.permission.READ_PHONE_STATE)){
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(SwitchNetworkActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(SwitchNetworkActivity.this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE},REQUEST_PERMISSIONS);
            }
        }else {
            //Call whatever you want
            findMyNetwork();
        }
    }



    @TargetApi(23)
    public void findMyNetwork() {
        TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        SubscriptionManager subscriptionManager = SubscriptionManager.from(getBaseContext());
        List<SubscriptionInfo> subscriptions = subscriptionManager.getActiveSubscriptionInfoList();
        String sim1Operator="",sim2Operator="";
        if (subscriptions != null){
            if(subscriptions.size()==1){
                sim1Operator = subscriptions.get(0).getCarrierName().toString();
            }else{
                sim1Operator = subscriptions.get(0).getCarrierName().toString();
                sim2Operator = subscriptions.get(1).getCarrierName().toString();
            }
        }
        String sim1Details = "", sim2Details = "";
        List<CellInfo> listAllCellInfo = tManager.getAllCellInfo();
        if (null != tManager && tManager.getPhoneCount() > 1) {
            for (ListIterator<CellInfo> itr = listAllCellInfo.listIterator(); itr.hasNext(); ) {
                CellInfo currentCellInfo = itr.next();
                if (currentCellInfo.isRegistered()) {
                    if (null == sim1Details || sim1Details.isEmpty()) {
                        sim1Details = sim1Details + "SIM1 Details: "+ System.lineSeparator() + getNetworkType(currentCellInfo) + " | " + sim1Operator + System.lineSeparator() + getSignalStrength(currentCellInfo) + " dBm" + System.lineSeparator() + getSingalQuality(currentCellInfo);
                    } else if (null == sim2Details || sim2Details.isEmpty()) {
                        sim2Details = sim2Details + "SIM2 Details: "+ System.lineSeparator() + getNetworkType(currentCellInfo) + " | " + sim2Operator + System.lineSeparator() + getSignalStrength(currentCellInfo) + " dBm" + System.lineSeparator() + getSingalQuality(currentCellInfo);
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
            return ((CellInfoLte) currentCellInfo).getCellSignalStrength().getDbm();
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

    public void getPermissionAndFindAllNetwork(){
        if (ContextCompat.checkSelfPermission(SwitchNetworkActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(SwitchNetworkActivity.this, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SwitchNetworkActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(SwitchNetworkActivity.this, android.Manifest.permission.ACCESS_NETWORK_STATE)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(SwitchNetworkActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_NETWORK_STATE},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(SwitchNetworkActivity.this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                                                                            android.Manifest.permission.ACCESS_NETWORK_STATE},REQUEST_PERMISSIONS);
            }
        }else {
            //Call whatever you want
            findAllNetwork();
        }
    }

    @TargetApi(22)
    public void findAllNetwork(){
        String allNwDetails = "";
        // Get System TELEPHONY service reference
        TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        SubscriptionManager subscriptionManager = SubscriptionManager.from(getBaseContext());
        List<SubscriptionInfo> subscriptions = subscriptionManager.getActiveSubscriptionInfoList();
        String sim1Operator="",sim2Operator="";
        if (subscriptions != null){
            if(subscriptions.size()==1){
                sim1Operator = subscriptions.get(0).getCarrierName().toString();
            }else{
                sim1Operator = subscriptions.get(0).getCarrierName().toString();
                sim2Operator = subscriptions.get(1).getCarrierName().toString();
            }
        }
        List<CellInfo> listAllCellInfo = tManager.getAllCellInfo();
        int counter = 0;
        String operators="", networkTypes="", strengths="";
        for (ListIterator<CellInfo> itr = listAllCellInfo.listIterator(); itr.hasNext(); ) {
            CellInfo currentCellInfo = itr.next();
            if(currentCellInfo.isRegistered()){
                counter++;
            }
            if (!currentCellInfo.isRegistered()) {
                if(counter==1) {
                    operators = operators + sim1Operator + System.lineSeparator() + System.lineSeparator();
                    networkTypes = networkTypes + getNetworkType(currentCellInfo) + System.lineSeparator() + System.lineSeparator();
                    strengths = strengths + getSignalStrength(currentCellInfo) + System.lineSeparator()+System.lineSeparator();
                } else{
                    operators = operators + sim2Operator + System.lineSeparator() + System.lineSeparator();
                    networkTypes = networkTypes + getNetworkType(currentCellInfo) + System.lineSeparator() + System.lineSeparator();
                    strengths = strengths + getSignalStrength(currentCellInfo) + System.lineSeparator()+System.lineSeparator();
                }
            }
        }
        displayAllNetwork(operators,networkTypes,strengths);
    }


    private void displayAllNetwork(String operatorText, String networkText, String strengthText){
        TextView displayAllOperators = (TextView) findViewById(R.id.scrollView_OperatorText);
        displayAllOperators.setText(operatorText);
        TextView displayAllNw = (TextView) findViewById(R.id.scrollView_NetworkText);
        displayAllNw.setText(networkText);
        TextView displayAllNwStrength = (TextView) findViewById(R.id.scrollView_StrengthText);
        displayAllNwStrength.setText(strengthText);

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

}
