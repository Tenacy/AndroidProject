package fr.unicaen.thiblef.gpsproject.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.List;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.dbmanager.ParcoursDbHandler;
import fr.unicaen.thiblef.gpsproject.dbmanager.TrajetDbHandler;
import fr.unicaen.thiblef.gpsproject.model.Parcours;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.model.TrajetArrayAdapter;
import fr.unicaen.thiblef.gpsproject.util.Format;
import fr.unicaen.thiblef.gpsproject.xml.GPXReader;
import fr.unicaen.thiblef.gpsproject.xml.GPXWriter;

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

    /**
     * Chemin vers le dossier Download
     */
    private static final String DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    /**
     * Load list of trajets
     */
    public void loadListView() {
        if (getArguments().containsKey(ARG_PARCOURS_ID)) {
            int idParcours = getArguments().getInt(ARG_PARCOURS_ID);
            List<Trajet> trajets = db.findByParcoursId(idParcours);
            ParcoursDbHandler dbParcours = new ParcoursDbHandler(getActivity());
            int trajetReference = dbParcours.find(idParcours).getIdTrajetReference();
            adapter = new TrajetArrayAdapter(this.getActivity(), R.layout.trajet_list_layout, trajets, trajetReference);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Trajet trajet = (Trajet) getListView().getAdapter().getItem(info.position);
        menu.setHeaderTitle("Trajet du " + Format.convertToDate(trajet.getDate()));
        menu.add(0, v.getId(), 0, "Supprimer");
        menu.add(0, v.getId(), 0, "Définir en trajet de référence");
        menu.add(0, v.getId(), 0, "Télécharger le GPX");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Trajet trajet = (Trajet) getListView().getAdapter().getItem(info.position);
        ParcoursDbHandler parcoursDbHandler = new ParcoursDbHandler(getActivity());
        Parcours parcours = parcoursDbHandler.find(getArguments().getInt(ARG_PARCOURS_ID));
        TrajetDbHandler trajetDbHandler = new TrajetDbHandler(getActivity());

        if (item.getTitle().equals("Supprimer")) {
            trajetDbHandler.delete(trajet.getId());
            if (trajet.getId() == parcoursDbHandler.find(getArguments().getInt(ARG_PARCOURS_ID)).getIdTrajetReference()) {
                List<Trajet> trajets = trajetDbHandler.findByParcoursId(parcours.getId());
                if (!trajets.isEmpty()) {
                    int idNewRef = trajets.get(0).getId();
                    parcours.setIdTrajetReference(trajetDbHandler.findByParcoursId(parcours.getId()).get(0).getId());
                } else {
                    parcours.setIdTrajetReference(-1);
                }
                parcoursDbHandler.update(parcours);
            }
            this.loadListView();
            Toast.makeText(this.getActivity(), "Trajet du " + Format.convertToDate(trajet.getDate()) + " supprimé", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals("Définir en trajet de référence")) {
            parcours.setIdTrajetReference(trajet.getId());
            parcoursDbHandler.update(parcours);
            this.loadListView();
            Toast.makeText(this.getActivity(), "Trajet du " + Format.convertToDate(trajet.getDate()) + " mis en trajet de référence", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals("Télécharger le GPX")) {
            //Récupération des points gpx
            GPXReader gpxReader = new GPXReader(getActivity(), trajet);
            gpxReader.parse();
            //On recréer le fichier dans le répertoire download
            new GPXWriter(getActivity(), trajet, DOWNLOAD_PATH);

        }
        return super.onContextItemSelected(item);
    }
}
