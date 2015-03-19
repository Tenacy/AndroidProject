package fr.unicaen.thiblef.gpsproject.model;

import android.content.Context;
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
public class TrajetArrayAdapter extends ArrayAdapter {

    private int idTrajetReference;

    public TrajetArrayAdapter(Context context, int idLayout, List<Trajet> trajets, int idTrajetReference) {
        super(context, idLayout, trajets);
        this.idTrajetReference = idTrajetReference;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Trajet trajet = (Trajet) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trajet_list_layout, parent, false);
        }

        TextView trajet_date = (TextView) convertView.findViewById(R.id.trajet_date);
        if(trajet.getId() == this.idTrajetReference){
            trajet_date.setText(Format.convertToDate(trajet.getDate()) + " (Référence)");
            trajet_date.setTextColor(convertView.getResources().getColor(R.color.blue));
        } else {
            trajet_date.setText(Format.convertToDate(trajet.getDate()));
        }

        TextView trajet_distance = (TextView) convertView.findViewById(R.id.trajet_distance);
        trajet_distance.setText(Format.convertToKm(trajet.getDistance()));

        TextView trajet_time = (TextView) convertView.findViewById(R.id.trajet_time);
        trajet_time.setText(Format.convertSecondsToHMmSs(trajet.getTemps()));

        TextView trajet_average_speed = (TextView) convertView.findViewById(R.id.trajet_average_speed);
        trajet_average_speed.setText(Format.convertToKmH(trajet.averageSpeed()));

        return convertView;
    }
}
