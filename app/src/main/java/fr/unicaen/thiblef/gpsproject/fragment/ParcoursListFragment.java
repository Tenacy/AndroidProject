package fr.unicaen.thiblef.gpsproject.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.dbmanager.ParcoursDbHandler;
import fr.unicaen.thiblef.gpsproject.model.Parcours;
import fr.unicaen.thiblef.gpsproject.model.ParcoursArrayAdapter;

public class ParcoursListFragment extends ListFragment {

    private Callbacks mCallbacks;

    private ParcoursDbHandler dbParcours;

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Parcours parcours);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbParcours = new ParcoursDbHandler(getActivity());
        loadParcoursList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    public void loadParcoursList() {
        List<Parcours> parcours = dbParcours.findAll();
        Collections.sort(parcours);
        ParcoursArrayAdapter adapter = new ParcoursArrayAdapter(this.getActivity(), R.layout.parcours_list_layout, parcours);
        setListAdapter(adapter);
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
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Parcours parcours = (Parcours) getListView().getAdapter().getItem(position);
        mCallbacks.onItemSelected(parcours);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Parcours parcours = (Parcours) getListView().getAdapter().getItem(info.position);
        menu.setHeaderTitle(getResources().getString(R.string.parcours) + " " + parcours.getName());
        menu.add(0, v.getId(), 0, getResources().getString(R.string.supprimer));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.modif_nom_parcours));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Parcours parcours = (Parcours) getListView().getAdapter().getItem(info.position);
        if (item.getTitle().equals(getResources().getString(R.string.supprimer))) {
            dbParcours.delete(parcours);
            this.loadParcoursList();
            Toast.makeText(this.getActivity(), getResources().getString(R.string.parcours)+ " " +parcours.getName()+ " " +getResources().getString(R.string.supprime), Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals(getResources().getString(R.string.modif_nom_parcours))) {
            DialogFragment dialog = new UpdateParcoursFragment();
            Bundle args = new Bundle();
            args.putInt(UpdateParcoursFragment.ARG_PARCOURS_ID, parcours.getId());
            dialog.setArguments(args);
            dialog.show(getActivity().getFragmentManager(),"UpdateParcoursFragment");
        }
        return super.onContextItemSelected(item);
    }

}
