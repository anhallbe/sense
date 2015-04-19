package eu.hallnet.sense;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

public class WifiChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Log.d("PeriodicAlarmReceiver", "NETWORK CONNECTIVITY CHANGED");
        Log.d("PeriodicAlarmReceiver", "New state: " + intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
        Parcelable newState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

        Intent startServiceIntent = new Intent(ctx, SensePublisherService.class);
        if(newState.equals(SupplicantState.COMPLETED)) {
            Log.d("PeriodicAlarmReceiver", "New Wifi network detected. Should start service.");
            Toast.makeText(ctx, "I joined a new network.", Toast.LENGTH_LONG).show();

            startServiceIntent.putExtra(SensePublisherService.EXTRA_CONNECTED, true);
            ctx.startService(startServiceIntent);
        }
        else if(newState.equals(SupplicantState.DISCONNECTED)) {
            Log.d("PeriodicAlarmReceiver", "Left network?");
            Toast.makeText(ctx, "I probably left network.", Toast.LENGTH_LONG).show();

            startServiceIntent.putExtra(SensePublisherService.EXTRA_CONNECTED, false);
            ctx.startService(startServiceIntent);
        }
    }

}
