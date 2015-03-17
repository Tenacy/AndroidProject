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
    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NewParcoursListener {
        public void onDialogPositiveClick(DialogFragment dialog, String parcours_name);
    }

    // Use this instance of the interface to deliver action events
    NewParcoursListener mListener;
    EditText text;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NewParcoursListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        text = new EditText(this.getActivity().getApplicationContext());
        text.setTextColor(getResources().getColor(R.color.black));
        builder.setTitle("Nom du parcours")
                .setView(text)
                .setPositiveButton("OK"/*R.string.dialog_newHashtag_subscribe*/, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(NewParcoursFragment.this, text.getText().toString().trim());
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
