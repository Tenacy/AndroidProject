package fr.unicaen.thiblef.gpsproject.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.dbmanager.ParcoursDbHandler;
import fr.unicaen.thiblef.gpsproject.fragment.TrajetsListFragment;
import fr.unicaen.thiblef.gpsproject.model.Parcours;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.util.Format;

public class TrajetsListActivity extends ActionBarActivity implements TrajetsListFragment.Callbacks{

    public static final String ARG_PARCOURS_ID = "parcours_id";

    private Parcours parcours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet_list);
        parcours = new ParcoursDbHandler(this).find(getIntent().getIntExtra(ARG_PARCOURS_ID, 1));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the Up button in the action bar.
        loadUi();
        loadFragment();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadUi();
        TrajetsListFragment fragment = (TrajetsListFragment) getSupportFragmentManager().findFragmentById(R.id.parcours_detail_container);
        fragment.loadListView();
    }

    public void loadUi() {
        Resources r = getApplicationContext().getResources();

        setTitle(r.getString(R.string.parcours) + " " + parcours.getName());

        TextView parcours_name = (TextView) findViewById(R.id.parcours_name);
        parcours_name.setText(r.getString(R.string.allure_moyenne) + ": " + Format.convertToMinKm(parcours.getAverageSpeed()));

        TextView parcours_distance = (TextView) findViewById(R.id.parcours_distance);
        parcours_distance.setText(Format.convertToKm(parcours.getDistance()));

        TextView parcours_best_time = (TextView) findViewById(R.id.parcours_best_time);
        parcours_best_time.setText(r.getString(R.string.best_time) + ": " + Format.convertSecondsToHMmSs(parcours.getBestTime()));

        TextView parcours_average_time = (TextView) findViewById(R.id.parcours_average_time);
        parcours_average_time.setText(r.getString(R.string.average_time) + ": " + Format.convertSecondsToHMmSs(parcours.getAverageTime()));

        TextView parcours_average_speed = (TextView) findViewById(R.id.parcours_average_speed);
        parcours_average_speed.setText(r.getString(R.string.average_speed) + ": " + Format.convertToKmH(parcours.getAverageSpeed()));

        TextView parcours_max_speed = (TextView) findViewById(R.id.parcours_max_speed);
        parcours_max_speed.setText(r.getString(R.string.max_speed) + ": " + Format.convertToKmH(parcours.getMaxSpeed()));

        TextView list_label = (TextView) findViewById(R.id.list_label);
        if (parcours.getTrajets().size() == 0) {
            list_label.setText(R.string.list_label_vide);
        } else {
            list_label.setText(R.string.list_label);
        }
    }

    private void loadFragment(){
        TrajetsListFragment fragment = new TrajetsListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(TrajetsListFragment.ARG_PARCOURS_ID, parcours.getId());
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().add(R.id.parcours_detail_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trajets, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_trajet_new:
                Intent trajet_intent = new Intent(this, TrajetActivity.class);
                trajet_intent.putExtra(TrajetActivity.ARG_PARCOURS_ID, parcours.getId());
                startActivity(trajet_intent);
                return true;
            case android.R.id.home:
                navigateUpTo(new Intent(this, ParcoursListActivity.class));
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, ParametresActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(Trajet trajet) {
        Intent trajet_details = new Intent(this, TrajetDetailActivity.class);
        trajet_details.putExtra(TrajetDetailActivity.ARG_TRAJET_ID, trajet.getId());
        startActivity(trajet_details);
    }
}
