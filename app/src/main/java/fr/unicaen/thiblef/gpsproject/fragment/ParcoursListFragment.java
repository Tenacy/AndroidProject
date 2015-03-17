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

/**
 * A list fragment representing a list of Parcours. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link TrajetsListFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ParcoursListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = parcoursCallback;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Parcours parcours);

        //public void onItemLongClick(Parcours parcours);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks parcoursCallback = new Callbacks() {
        @Override
        public void onItemSelected(Parcours parcours) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ParcoursListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadParcoursList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    public void loadParcoursList(){
        ParcoursDbHandler parcoursDbHandler = new ParcoursDbHandler(getActivity());
        List<Parcours> parcours = parcoursDbHandler.findAll();
        Collections.sort(parcours);
        ParcoursArrayAdapter adapter = new ParcoursArrayAdapter(this.getActivity(), R.layout.parcours_list_layout,parcours);
        setListAdapter(adapter);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = parcoursCallback;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Parcours parcours = (Parcours) getListView().getAdapter().getItem(position);
        mCallbacks.onItemSelected(parcours);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Menu Title");
        menu.add(0, v.getId(), 0, "Supprimer");
        menu.add(0, v.getId(), 0, "Modifier nom du parcours");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Parcours parcours = (Parcours) getListView().getAdapter().getItem(info.position);
        ParcoursDbHandler parcoursDbHandler = new ParcoursDbHandler(getActivity());
        if (item.getTitle().equals("Supprimer")) {
            parcoursDbHandler.delete(parcours);
            this.loadParcoursList();
            Toast.makeText(this.getActivity(), "Parcours "+parcours.getName()+" supprimé!", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals("Modifier nom du parcours")) {
            Toast.makeText(this.getActivity(), "MODIF", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

}
