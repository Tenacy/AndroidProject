package fr.unicaen.thiblef.gpsproject.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.service.GPSLocation;


/**
 * Created by 20900977 on 11/03/15.
 */
public class GPSActivity extends Activity {

    protected Trajet trajet;
    private Location last;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                double latitude = bundle.getDouble("latitude", 0);
                double longitude = bundle.getDouble("longitude", 0);
                long time = new Date().getTime();
                last.setTime(time);
                last.setLatitude(latitude);
                last.setLongitude(longitude);
                trajet.addLocation(last);
                Log.i("Location : ", last.toString());
                /*
                 *  Mise à jour de la carte
                 */
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("GPSActivity : ", "Création de l'activité");
        last = new Location("");
        trajet = new Trajet();
        Intent location_indent = new Intent(getBaseContext(), GPSLocation.class);
        Log.i("GPSLocation : ", "Démarrage du service");
        startService(location_indent);
        setContentView(R.layout.activity_gps);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("fr.unicaen.thiblef.GPSProject"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        this.onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_g, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}