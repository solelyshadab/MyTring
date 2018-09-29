package in.mytring;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by shadabbaig on 13/02/17.
 */

public class ListenerService extends Service {
    private ListenerThread listenerThread = null;

    PhoneStateListener myPhoneStateListener;
    TelephonyManager tm;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (listenerThread == null) {
            listenerThread = new ListenerThread(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (listenerThread == null)
            listenerThread = new ListenerThread(this);

        if ((listenerThread.getState() == Thread.State.NEW) || (listenerThread.getState() == Thread.State.TERMINATED)) {
            if (listenerThread.getState() == Thread.State.TERMINATED)
                listenerThread = new ListenerThread(this);

            listenerThread.start();

//            Notification localNotification = new Notification(R.drawable.ic_launcher, "", System.currentTimeMillis());
//            localNotification.setLatestEventInfo(this,AppConstants.NOTIFICATION_NAME,AppConstants.NOTIFICATION_DESCRIPTION, null);
//            localNotification.flags = Notification.FLAG_NO_CLEAR;
//            startForeground(377982, localNotification);

            //myPhoneStateListener = new MyTringPhoneStateListener(this);
//            tm  = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            try {
//                tm.listen(myPhoneStateListener,
//                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//
//
//            } catch (Exception e) {
//
//            }
//            Log.d("ListenerService", "Inside listener onStartCommand");


        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        listenerThread.yield();
//        listenerThread = null;
    }


}
