package fr.unicaen.thiblef.gpsproject.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import fr.unicaen.thiblef.gpsproject.dbmanager.ParcoursDbHandler;
import fr.unicaen.thiblef.gpsproject.fragment.TrajetsListFragment;
import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.model.Parcours;
import fr.unicaen.thiblef.gpsproject.util.Format;


/**
 * An activity representing a single Parcours detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ParcoursListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link fr.unicaen.thiblef.gpsproject.fragment.TrajetsListFragment}.
 */
public class TrajetsListActivity extends ActionBarActivity {

    private Parcours parcours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet_list);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        loadUi();

        Bundle arguments = new Bundle();
        arguments.putInt(TrajetsListFragment.ARG_PARCOURS_ID, parcours.getId());
        TrajetsListFragment fragment = new TrajetsListFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().add(R.id.parcours_detail_container, fragment).commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadUi();
        TrajetsListFragment fragment = (TrajetsListFragment) getSupportFragmentManager().findFragmentById(R.id.parcours_detail_container);
        fragment.loadListView();
    }

    public void loadUi(){
        parcours = new ParcoursDbHandler(this).find(getIntent().getIntExtra(TrajetsListFragment.ARG_PARCOURS_ID, 1));

        Resources r = getApplicationContext().getResources();

        TextView parcours_name = (TextView) findViewById(R.id.parcours_name);
        parcours_name.setText(r.getString(R.string.parcours)+ " " + parcours.getName());

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
        if(parcours.getTrajets().size() == 0) {
            list_label.setText(R.string.list_label_vide);
        } else {
            list_label.setText(R.string.list_label);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trajets, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_trajet_new:
                //Intent trajet_intent = new Intent(this, TrajetActivity.class);
                //trajet_intent.putExtra(TrajetActivity.ARG_PARCOURS_ID, parcours.getId());

                Intent trajet_intent = new Intent(this, GPSActivity.class);
                trajet_intent.putExtra(GPSActivity.ARG_PARCOURS_ID, parcours.getId());
                startActivity(trajet_intent);
                return true;
            case  android.R.id.home:
                navigateUpTo(new Intent(this, ParcoursListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
