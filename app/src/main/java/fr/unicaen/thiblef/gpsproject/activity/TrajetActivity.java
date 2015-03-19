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

    public static final String ARG_PARCOURS_ID = "parcours_id";

    private LocationManager locationManager;
    private Trajet trajet;
    private boolean isStarted;
    private long actual_time;
    private Chronometer chronometer;
    private Parcours parcours;
    private int nbLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet);
        parcours = new ParcoursDbHandler(this).find(getIntent().getIntExtra(ARG_PARCOURS_ID, 1));
        trajet = new Trajet();
        trajet.setDate(new Date().getTime());
        isStarted = false;
        actual_time = 0;
        nbLoc = 0;

        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        long minTime = 20 * 1000; //20s
        long minDistance = 25; //25m

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);

        chronometer = (Chronometer) findViewById(R.id.chrono);
        setTitle(getResources().getString(R.string.title_activity_trajet)+" "+parcours.getName() );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trajet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        isStarted = true;
        button_stop.setEnabled(true);
        button.setEnabled(false);
        Toast.makeText(this, getResources().getString(R.string.start), Toast.LENGTH_SHORT).show();
        chronometer.setBase(SystemClock.elapsedRealtime() + actual_time);
        chronometer.start();
        Location first = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(first != null) {
            Date d = new Date();
            first.setTime(d.getTime());
            trajet.addLocation(first);
            majUi(first);
        }
    }

    public void stop(View view) {
        isStarted = false;
        chronometer.stop();
        Button button_start = (Button) findViewById(R.id.button_start);
        Button button_stop = (Button) view;
        button_stop.setEnabled(false);
        button_start.setEnabled(false);
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

    private void majUi(Location location) {
        /*TextView traces = (TextView) findViewById(R.id.traces);
        traces.setText(traces.getText().toString() + location.getLatitude() + " " + location.getLongitude() + " " + location.getTime() + " " + location.getSpeed() + "\n");*/

        nbLoc++;
        TextView speed = (TextView) findViewById(R.id.speed);
        speed.setText(Format.convertToKmH(trajet.averageSpeed()));

        TextView distance = (TextView) findViewById(R.id.distance);
        distance.setText(Format.convertToKm(trajet.getDistance()));

        TextView allure = (TextView) findViewById(R.id.allure);
        allure.setText(Format.convertToMinKm(trajet.averageSpeed()));

        TextView retard = (TextView) findViewById(R.id.retard);
        retard.setText(Format.convertSecondsToMmSs(0));

        TextView nb = (TextView) findViewById(R.id.nbLocations);
        nb.setText(" "+nbLoc);
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
