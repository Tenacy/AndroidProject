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
public class UpdateParcoursFragment extends DialogFragment {

    public static final String ARG_PARCOURS_ID = "parcours_id";

    private int parcours_id;

     public interface UpdateParcoursListener {
        public void onUpdateParcours(DialogFragment dialog, int parcours_id, String parcours_name);
    }

    private UpdateParcoursListener mListener;
    private EditText text;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (UpdateParcoursListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        parcours_id = getArguments().getInt(ARG_PARCOURS_ID);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        text = new EditText(this.getActivity().getApplicationContext());
        text.setTextColor(getResources().getColor(R.color.black));
        builder.setTitle(getResources().getString(R.string.modification_nom_parcours))
                .setView(text)
                .setPositiveButton(getResources().getString(R.string.modif_nom_parcours), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onUpdateParcours(UpdateParcoursFragment.this, parcours_id, text.getText().toString().trim());
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
