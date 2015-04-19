package eu.hallnet.sense;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sense.jsense.SenseRESTClient;
import sense.jsense.SensorPub;


public class MainActivity extends Activity {

    private TextView helloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, SensePublisherService.class));
        Toast.makeText(this, "WiFi Sensor Service started.", Toast.LENGTH_LONG).show();
        finish();
    }

    private class AsyncSearch extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            SenseRESTClient client = new SenseRESTClient("hallnet.eu", 1337);
            List<SensorPub> searchResult = client.search("*");

            String res = "";
            for(SensorPub p : searchResult) {
                res += p.getName() + " " + p.getValue() + "\n";
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            helloTextView.setText(s);
        }
    }
}
