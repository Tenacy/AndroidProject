package fr.unicaen.thiblef.gpsproject.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.activity.TrajetDetailActivity;
import fr.unicaen.thiblef.gpsproject.dbmanager.ParcoursDbHandler;
import fr.unicaen.thiblef.gpsproject.dbmanager.TrajetDbHandler;
import fr.unicaen.thiblef.gpsproject.model.Parcours;
import fr.unicaen.thiblef.gpsproject.model.Trajet;
import fr.unicaen.thiblef.gpsproject.model.TrajetArrayAdapter;
import fr.unicaen.thiblef.gpsproject.util.Format;
import fr.unicaen.thiblef.gpsproject.xml.GPXReader;
import fr.unicaen.thiblef.gpsproject.xml.GPXWriter;


public class TrajetsListFragment extends ListFragment {

    public static final String ARG_PARCOURS_ID = "parcours_id";

    /**
     * Chemin vers le dossier Download
     */
    private static final String DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    private Callbacks mCallbacks;

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Trajet trajet);

    }

    private TrajetDbHandler dbTrajet;

    private ParcoursDbHandler dbParcours;

    private TrajetArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbTrajet = new TrajetDbHandler(getActivity());
        dbParcours = new ParcoursDbHandler(getActivity());
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
            List<Trajet> trajets = dbTrajet.findByParcoursId(idParcours);
            int trajetReference = dbParcours.find(idParcours).getIdTrajetReference();
            adapter = new TrajetArrayAdapter(this.getActivity(), R.layout.trajet_list_layout, trajets, trajetReference);
            setListAdapter(adapter);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Trajet trajet = (Trajet) getListView().getAdapter().getItem(info.position);
        menu.setHeaderTitle("Trajet du " + Format.convertToDate(trajet.getDate()));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.supprimer));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.definir_trajet_ref));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.telecharger_gpx));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Trajet trajet = (Trajet) getListView().getAdapter().getItem(info.position);
        Parcours parcours = dbParcours.find(getArguments().getInt(ARG_PARCOURS_ID));
        if (item.getTitle().equals(getResources().getString(R.string.supprimer))) {
            dbTrajet.delete(trajet.getId());
            if (trajet.getId() == dbParcours.find(getArguments().getInt(ARG_PARCOURS_ID)).getIdTrajetReference()) {
                List<Trajet> trajets = dbTrajet.findByParcoursId(parcours.getId());
                if (!trajets.isEmpty()) {
                    int idNewRef = trajets.get(0).getId();
                    parcours.setIdTrajetReference(dbTrajet.findByParcoursId(parcours.getId()).get(0).getId());
                } else {
                    parcours.setIdTrajetReference(-1);
                }
                dbParcours.update(parcours);
            }
            this.loadListView();
            Toast.makeText(this.getActivity(), getResources().getString(R.string.trajet_du) + " " + Format.convertToDate(trajet.getDate()) + " " + getResources().getString(R.string.supprime), Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals(getResources().getString(R.string.definir_trajet_ref))) {
            parcours.setIdTrajetReference(trajet.getId());
            dbParcours.update(parcours);
            this.loadListView();
            Toast.makeText(this.getActivity(), getResources().getString(R.string.trajet_du) + " " + Format.convertToDate(trajet.getDate()) + " " + getResources().getString(R.string.mis_en_ref) + " ", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals(getResources().getString(R.string.telecharger_gpx))) {
            //Récupération des points gpx
            GPXReader gpxReader = new GPXReader(getActivity(), trajet);
            gpxReader.parse();
            //On recréer le fichier dans le répertoire download
            new GPXWriter(getActivity(), trajet, DOWNLOAD_PATH);

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Trajet trajet = (Trajet) getListView().getAdapter().getItem(position);
        mCallbacks.onItemSelected(trajet);
    }


    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }
}
