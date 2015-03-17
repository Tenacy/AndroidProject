package fr.unicaen.thiblef.gpsproject.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import java.util.List;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.dbmanager.TrajetDbHandler;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.model.TrajetArrayAdapter;

/**
 * A fragment representing a single Parcours detail screen.
 * This fragment is either contained in a {@link fr.unicaen.thiblef.gpsproject.activity.ParcoursListActivity}
 * in two-pane mode (on tablets) or a {@link fr.unicaen.thiblef.gpsproject.activity.TrajetsListActivity}
 * on handsets.
 */
public class TrajetsListFragment extends ListFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_PARCOURS_ID = "parcours_id";

    private TrajetDbHandler db;

    private TrajetArrayAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrajetsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TrajetDbHandler(getActivity());
        loadListView();
    }

    /**
     * Load list of trajets
     */
    public void loadListView() {
        if (getArguments().containsKey(ARG_PARCOURS_ID)) {
            int idParcours = getArguments().getInt(ARG_PARCOURS_ID);
            List<Trajet> trajets = db.findByParcoursId(idParcours);
            adapter = new TrajetArrayAdapter(this.getActivity(), R.layout.trajet_list_layout, trajets);
            setListAdapter(adapter);
        }
    }
}
