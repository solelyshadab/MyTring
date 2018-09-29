package in.mytring;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by shadabbaig on 12/02/17.
 */

public class MyTringPhoneStateListener extends PhoneStateListener {
    Context mContext;
    private static final String TAG = "SignalChangeIndicator";
    public static final String NEW_SIGNAL_STRENGTH = "outText";
    public static final int DEFAULT_STRENGTH = 0;


    public MyTringPhoneStateListener(Context context) {
        super();
        mContext = context;
    }

    @TargetApi(23)
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);

        TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        List<CellInfo> listAllCellInfo = tManager.getAllCellInfo();

        int strength=0;
        int lastSignalStrength=signalStrength.getGsmSignalStrength();

        for(ListIterator<CellInfo> itr = listAllCellInfo.listIterator(); itr.hasNext();) {
            CellInfo currentCellInfo = itr.next();
            if (currentCellInfo.isRegistered()) {
                strength = getSignalStrength(currentCellInfo);
                Log.d(TAG, ""+strength);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.LOCAL_ACTION);
                broadcastIntent.putExtra(NEW_SIGNAL_STRENGTH, strength);
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                localBroadcastManager.sendBroadcast(broadcastIntent);
                int changeInSignal = strength+lastSignalStrength;
                if(changeInSignal>0){
                    Toast.makeText(mContext, "Your Signal Strength increased by " + changeInSignal + " dBm !!!",
                            Toast.LENGTH_LONG).show();
                }else if(changeInSignal<0){
                    Toast.makeText(mContext, "Your Signal Strength dropped by " + (-changeInSignal) + " dBm !!!",
                            Toast.LENGTH_LONG).show();
                }

            }
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

}


