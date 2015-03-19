package fr.unicaen.thiblef.gpsproject.activity;


import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.dbmanager.TrajetDbHandler;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.util.Format;
import fr.unicaen.thiblef.gpsproject.xml.GPXReader;

public class TrajetDetailActivity extends ActionBarActivity {

    public static final String ARG_TRAJET_ID = "trajet_id";

    private Trajet trajet;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet_detail);
        trajet = new TrajetDbHandler(this).findById(getIntent().getIntExtra(ARG_TRAJET_ID, 1));
        setTitle(getResources().getString(R.string.trajet_du)+ " " + Format.convertToDate(trajet.getDate()));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        majUi();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // check if we have got the googleMap already
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map_container)).getMap();
            if (googleMap != null) {
                addLines();
            }
        }
    }

    private void addLines() {
        GPXReader gpxReader = new GPXReader(getApplicationContext(), trajet);
        gpxReader.parse();
        List<Location> locations = trajet.getLocations();
        if (!locations.isEmpty()) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(5)
                    .color(Color.BLUE)
                    .geodesic(true);
            LatLng first = new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());
            LatLng last = new LatLng(locations.get(locations.size()-1).getLatitude(), locations.get(locations.size()-1).getLongitude());
            addMarker(first, getResources().getString(R.string.depart));
            addMarker(last, getResources().getString(R.string.arrivee));
            for (Location loc : locations) {
                polylineOptions.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            }
            googleMap.addPolyline(polylineOptions);
            // move camera to zoom on map
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 13));
        }
    }

    private void addMarker(LatLng position, String title){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        googleMap.addMarker(markerOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trajet_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                Intent detailIntent = new Intent(this, TrajetsListActivity.class);
                detailIntent.putExtra(TrajetsListActivity.ARG_PARCOURS_ID, new TrajetDbHandler(this).getParcoursId(trajet));
                navigateUpTo(detailIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void majUi() {
        TextView traces = (TextView) findViewById(R.id.time);
        traces.setText(Format.convertSecondsToHMmSs(trajet.getTemps()));

        TextView speed = (TextView) findViewById(R.id.speed);
        speed.setText(Format.convertToKmH(trajet.averageSpeed()));

        TextView distance = (TextView) findViewById(R.id.distance);
        distance.setText(Format.convertToKm(trajet.getDistance()));
    }
}
