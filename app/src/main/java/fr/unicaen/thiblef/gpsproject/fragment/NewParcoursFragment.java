package fr.unicaen.thiblef.gpsproject.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import fr.unicaen.thiblef.gpsproject.R;

/**
 * Created by Thibault on 10/03/2015.
 */
public class NewParcoursFragment extends DialogFragment {

    public interface NewParcoursListener {
        public void onNewParcours(DialogFragment dialog, String parcours_name);
    }

    // Use this instance of the interface to deliver action events
    private NewParcoursListener mListener;
    private EditText text;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NewParcoursListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        text = new EditText(this.getActivity().getApplicationContext());
        text.setTextColor(getResources().getColor(R.color.black));
        builder.setTitle(getResources().getString(R.string.nouveauParcours))
                .setView(text)
                .setPositiveButton(getResources().getString(R.string.renommer), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onNewParcours(NewParcoursFragment.this, text.getText().toString().trim());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
