package fr.unicaen.thiblef.gpsproject.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.dbmanager.ParcoursDbHandler;
import fr.unicaen.thiblef.gpsproject.fragment.NewParcoursFragment;
import fr.unicaen.thiblef.gpsproject.fragment.ParcoursListFragment;
import fr.unicaen.thiblef.gpsproject.fragment.TrajetsListFragment;
import fr.unicaen.thiblef.gpsproject.fragment.UpdateParcoursFragment;
import fr.unicaen.thiblef.gpsproject.model.Parcours;


public class ParcoursListActivity extends ActionBarActivity
        implements ParcoursListFragment.Callbacks, NewParcoursFragment.NewParcoursListener, UpdateParcoursFragment.UpdateParcoursListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours_list);
    }

    /**
     * Callback method from {@link ParcoursListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Parcours parcours) {
        // In single-pane mode, simply start the detail activity
        // for the selected item ID.
        Intent detailIntent = new Intent(this, TrajetsListActivity.class);
        detailIntent.putExtra(TrajetsListFragment.ARG_PARCOURS_ID, parcours.getId());
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_parcours, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_parcours_new:
                DialogFragment dialog = new NewParcoursFragment();
                dialog.show(getFragmentManager(), "NewParcoursFragment");
                return true;
            case R.id.action_settings:
                //Intent settingsIntent = new Intent(this, SettingsActivity.class);
                //startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNewParcours(DialogFragment dialog, String parcours_name) {
        ParcoursDbHandler db = new ParcoursDbHandler(this);
        Parcours parcours = new Parcours(parcours_name);
        int parcours_id = db.add(parcours);
        parcours.setId(parcours_id);
        onItemSelected(parcours);
    }

    @Override
    public void onUpdateParcours(DialogFragment dialog,int parcours_id, String parcours_name) {
        ParcoursDbHandler db = new ParcoursDbHandler(this);
        Parcours parcours = db.find(parcours_id);
        parcours.setName(parcours_name);
        db.update(parcours);
        ParcoursListFragment fragment = (ParcoursListFragment) getSupportFragmentManager().findFragmentById(R.id.parcours_list);
        fragment.loadParcoursList();
        Toast.makeText(this, "Parcours modifié", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ParcoursListFragment fragment = (ParcoursListFragment) getSupportFragmentManager().findFragmentById(R.id.parcours_list);
        fragment.loadParcoursList();
    }



}
