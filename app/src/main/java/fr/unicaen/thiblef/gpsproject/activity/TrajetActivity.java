package fr.unicaen.thiblef.gpsproject.activity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.dbmanager.ParcoursDbHandler;
import fr.unicaen.thiblef.gpsproject.dbmanager.TrajetDbHandler;
import fr.unicaen.thiblef.gpsproject.fragment.TrajetsListFragment;
import fr.unicaen.thiblef.gpsproject.model.Parcours;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.util.Format;
import fr.unicaen.thiblef.gpsproject.xml.GPXWriter;

public class TrajetActivity extends ActionBarActivity implements LocationListener {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_PARCOURS_ID = "parcours_id";
    protected LocationManager locationManager;
    protected Trajet trajet;
    protected boolean isStarted;
    protected boolean firstStart;
    protected long actual_time;
    protected Chronometer chronometer;
    private Parcours parcours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet);
        parcours = new ParcoursDbHandler(this).find(getIntent().getIntExtra(TrajetsListFragment.ARG_PARCOURS_ID, 1));
        trajet = new Trajet();
        trajet.setDate(new Date().getTime());
        firstStart = true;
        isStarted = false;
        actual_time = 0;

        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        long minTime = 20 * 1000; //20s
        long minDistance = 25; //25m

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);

        chronometer = (Chronometer) findViewById(R.id.chrono);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trajet, menu);
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

    public void start(View view) {
        Button button = (Button) view;
        Button button_stop = (Button) findViewById(R.id.button_stop);

        if (isStarted) {
            isStarted = false;
            button.setBackgroundResource(R.color.emerald);
            button.setText("START");
            Toast.makeText(this, "PAUSE", Toast.LENGTH_SHORT).show();
            actual_time = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
        } else {
            isStarted = true;
            button_stop.setEnabled(true);
            button.setBackgroundResource(R.color.carrot);
            button.setText("PAUSE");
            Toast.makeText(this, "START", Toast.LENGTH_SHORT).show();
            chronometer.setBase(SystemClock.elapsedRealtime() + actual_time);
            chronometer.start();
            if(firstStart){
                Location first = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(first != null) {
                    Date d = new Date();
                    first.setTime(d.getTime());
                    trajet.addLocation(first);
                    firstStart = false;
                    majUi(first);
                }
            }
        }
    }

    public void stop(View view) {
        isStarted = false;
        chronometer.stop();
        Button button_start_pause = (Button) findViewById(R.id.button_start_pause);
        button_start_pause.setBackgroundResource(R.color.emerald);
        button_start_pause.setText("START");
        Button button_stop = (Button) view;
        button_stop.setEnabled(false);
        button_start_pause.setEnabled(false);
        Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(last != null){
            last.setTime(new Date().getTime());
            trajet.addLocation(last);
            majUi(last);
        }
        int trajet_id = insertInDB();
        generateXml();
        Intent trajet_details = new Intent(this, TrajetDetailActivity.class);
        trajet_details.putExtra(TrajetDetailActivity.ARG_TRAJET_ID, trajet_id);
        startActivity(trajet_details);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isStarted) {
            trajet.addLocation(location);
            majUi(location);
        }
    }

    public void majUi(Location location) {
        TextView traces = (TextView) findViewById(R.id.traces);
        traces.setText(traces.getText().toString() + location.getLatitude() + " " + location.getLongitude() + " " + location.getTime() + " " + location.getSpeed() + "\n");

        TextView speed = (TextView) findViewById(R.id.speed);
        speed.setText(Format.convertToKmH(trajet.averageSpeed()));

        TextView distance = (TextView) findViewById(R.id.distance);
        distance.setText(Format.convertToKm(trajet.getDistance()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private int insertInDB() {
        TrajetDbHandler trajetDbHandler = new TrajetDbHandler(this);
        int id = trajetDbHandler.add(trajet, parcours);
        trajet.setId(id);
        parcours.addTrajet(trajet);
        if(parcours.getIdTrajetReference() == -1){
            parcours.setIdTrajetReference(id);
        }
        ParcoursDbHandler parcoursDbHandler = new ParcoursDbHandler(this);
        parcoursDbHandler.update(parcours);
        return id;
    }

    private void generateXml() {
        GPXWriter gpxWriter = new GPXWriter(this, trajet);
        gpxWriter.convertToXml();
    }
}
