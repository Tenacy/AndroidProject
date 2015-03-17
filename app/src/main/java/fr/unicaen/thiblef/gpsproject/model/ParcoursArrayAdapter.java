package fr.unicaen.thiblef.gpsproject.model;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.unicaen.thiblef.gpsproject.R;
import fr.unicaen.thiblef.gpsproject.util.Format;

/**
 * Created by Thibault on 06/03/2015.
 */
public class ParcoursArrayAdapter extends ArrayAdapter {
    public ParcoursArrayAdapter(Context context, int idLayout, List<Parcours> parcours) {
        super(context, idLayout, parcours);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Parcours parcours = (Parcours) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parcours_list_layout, parent, false);
        }
        Resources r = getContext().getResources();
        TextView parcours_name = (TextView) convertView.findViewById(R.id.parcours_name);
        parcours_name.setText(parcours.getName());
        TextView parcours_distance = (TextView) convertView.findViewById(R.id.parcours_distance);
        parcours_distance.setText(Format.convertToKm(parcours.getDistance()));
        TextView parcours_best_time = (TextView) convertView.findViewById(R.id.parcours_best_time);
        parcours_best_time.setText(r.getString(R.string.best_time) + ": " + Format.convertSecondsToHMmSs(parcours.getBestTime()));

        TextView nb_trajets = (TextView) convertView.findViewById(R.id.nb_trajets);
        nb_trajets.setText(parcours.getTrajets().size()+"");


        return convertView;
    }

}
