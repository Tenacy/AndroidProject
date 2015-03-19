package fr.unicaen.thiblef.gpsproject.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import fr.unicaen.thiblef.gpsproject.service.GPSLocation;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.util.Format;
import fr.unicaen.thiblef.gpsproject.xml.GPXWriter;



/**
 * Created by 20900977 on 11/03/15.
 */
public class GPSActivity extends ActionBarActivity {

    public static final String ARG_PARCOURS_ID = "parcours_id";

    protected Trajet trajet;
    private Location last;
    protected boolean isStarted;
    protected long actual_time;
    protected Chronometer chronometer;
    private Parcours parcours;

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
                if(isStarted) {
                    trajet.addLocation(last);
                    Log.i("Location : ", last.toString());
                }
                /*
                 *  Mise à jour de la carte
                 */
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet);
        Log.i("GPSActivity : ", "Création de l'activité");
        last = new Location("");
        trajet = new Trajet();
        chronometer = (Chronometer) findViewById(R.id.chrono);
        parcours = new ParcoursDbHandler(this).find(getIntent().getIntExtra(TrajetsListFragment.ARG_PARCOURS_ID, 1));
        trajet.setDate(new Date().getTime());
        isStarted = false;
        actual_time = 0;

        Intent location_intent = new Intent(getBaseContext(), GPSLocation.class);
        Log.i("GPSLocation : ", "Démarrage du service");
        startService(location_intent);
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

    public void start(View view) {
        Button button = (Button) view;
        Button button_stop = (Button) findViewById(R.id.button_stop);
        button_stop.setEnabled(true);
        if (isStarted) {
            isStarted = false;
            button.setBackgroundResource(R.color.emerald);
            button.setText("START");
            Toast.makeText(this, "PAUSE", Toast.LENGTH_SHORT).show();
            actual_time = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
        } else {
            isStarted = true;
            button.setBackgroundResource(R.color.carrot);
            button.setText("PAUSE");
            Toast.makeText(this, "START", Toast.LENGTH_SHORT).show();
            chronometer.setBase(SystemClock.elapsedRealtime() + actual_time);
            chronometer.start();
        }
    }

    public void stop(View view) {
        isStarted = false;
        chronometer.stop();
        Button button_start_pause = (Button) findViewById(R.id.button_start);
        button_start_pause.setBackgroundResource(R.color.emerald);
        button_start_pause.setText("START");
        Toast.makeText(this, "STOP", Toast.LENGTH_SHORT).show();
        Button button_stop = (Button) view;
        button_stop.setEnabled(false);
        button_start_pause.setEnabled(false);
        int trajet_id = insertInDB();
        generateXml();
        Intent trajet_details = new Intent(this, TrajetDetailActivity.class);
        trajet_details.putExtra(TrajetDetailActivity.ARG_TRAJET_ID, trajet_id);
        startActivity(trajet_details);
    }

    public void majUi(Location location){
        /*TextView traces = (TextView) findViewById(R.id.traces);
        traces.setText(traces.getText().toString()+location.getLatitude()+" " +location.getLongitude()+" "+location.getTime()+" "+location.getSpeed()+ "\n");*/

        TextView speed = (TextView) findViewById(R.id.speed);
        speed.setText(Format.convertToKmH(trajet.averageSpeed()));

        TextView distance = (TextView) findViewById(R.id.distance);
        distance.setText(Format.convertToKm(trajet.getDistance()));
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
    private void generateXml(){
        GPXWriter gpxWriter = new GPXWriter(this, trajet);
        gpxWriter.convertToXml();
    }


}