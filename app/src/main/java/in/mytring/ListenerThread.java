package in.mytring;

import android.content.Context;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by shadabbaig on 13/02/17.
 */

public class ListenerThread extends Thread {
    PhoneStateListener myPhoneStateListener;
    TelephonyManager tm;
    Context mContext;

    public ListenerThread(Context context) {
        super();
        mContext = context;
    }


    @Override
    public void run() {
        tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Looper.prepare();
        myPhoneStateListener = new MyTringPhoneStateListener(mContext);
        try {
            tm.listen(myPhoneStateListener,
                    MyTringPhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        } catch (Exception e) {

        }
        Log.d("ListenerService", "**************Listener is ON!******************");
        Looper.loop();
    }


}




