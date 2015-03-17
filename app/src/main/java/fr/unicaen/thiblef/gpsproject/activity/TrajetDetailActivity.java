package fr.unicaen.thiblef.gpsproject.activity;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.dbmanager.TrajetDbHandler;
import fr.unicaen.thiblef.gpsproject.fragment.TrajetsListFragment;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.util.Format;

public class TrajetDetailActivity extends ActionBarActivity {

    public static final String ARG_TRAJET_ID = "trajet_id";

    private Trajet trajet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet_detail);
        trajet = new TrajetDbHandler(this).findById(getIntent().getIntExtra(ARG_TRAJET_ID, 1));
        majUi();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trajet_detail, menu);
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

    private void majUi(){
        TextView traces = (TextView) findViewById(R.id.time);
        traces.setText(Format.convertSecondsToHMmSs(trajet.getTemps()));

        TextView speed = (TextView) findViewById(R.id.speed);
        speed.setText(Format.convertToKmH(trajet.averageSpeed()));

        TextView distance = (TextView) findViewById(R.id.distance);
        distance.setText(Format.convertToKm(trajet.getDistance()));
    }
}
