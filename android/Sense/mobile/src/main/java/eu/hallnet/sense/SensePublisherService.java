package eu.hallnet.sense;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.UserManager;
import android.util.Log;

import java.lang.reflect.Method;

import sense.jsense.SenseService;
import sense.jsense.SensorPub;

public class SensePublisherService extends Service {

    SenseService sense;
    SSIDSensor ssidSensor = null;
    String lastSSID = "unknown";

    public static final String EXTRA_CONNECTED = "hallnet.eu.sense.wifi.connected";

    private final long INTERVAL_PUBLISH = 30000;

    public SensePublisherService() {
        sense = new SenseService("hallnet.eu", 1337, SenseService.INTERVAL_SLOW, false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean newConnection = intent.getBooleanExtra(EXTRA_CONNECTED, true);
        new PeriodicallyPublishTask().execute(newConnection);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getCurrentSSID() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getSSID();
    }

    private String getPhoneName(String defValue) {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    private class PeriodicallyPublishTask extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... params) {
            boolean connected = params[0];
            if(ssidSensor == null) {
                String phoneName = getPhoneName("androidAnonymous");
                ssidSensor = new SSIDSensor(sense, phoneName);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                String ssid;
                if(connected) {
                    ssid = getCurrentSSID().replaceAll("\"", "");
                    lastSSID = ssid;
                    sense.publish(ssidSensor.getId(), "joined " + ssid);
                } else {
                    ssid = lastSSID;
                    sense.publish(ssidSensor.getId(), "left " + ssid);
                }
                Log.d("SensePublisherService", "Published SSID: " + ssid);

            } catch (Exception e) {
                Log.e("SensePublisherService", "Error when publishing SSID.");
            }

            return null;
        }
    }

    private class SSIDSensor extends SensorPub {

        private String id;

        public SSIDSensor(SenseService service, String username) {
            super("PhoneWifiConnection",
                    "Shows whether my phone just joined or left a WiFi network. Phone name is " + username,
                    SensorPub.TYPE_STRING,
                    "unknown");
            id = service.publish(this);
        }

        public String getId() {
            return id;
        }
    }
}
