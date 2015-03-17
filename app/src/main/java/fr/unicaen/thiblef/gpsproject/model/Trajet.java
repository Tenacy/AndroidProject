package fr.unicaen.thiblef.gpsproject.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime on 25/02/2015.
 */
public class Trajet {

    /**
     * Id du trajet
     */
    public int id;
    /**
     * Distance parcourue (en m)
     */
    public double distance;
    /**
     * Temps total du trajet (en sec)
     */
    public long temps;

    /**
     * date du trajet (timestamp)
     */
    public long date;


    protected List<Location> locations;

    public Trajet() {
        this(0, 0, 0, 0, new ArrayList<Location>());
    }

    public Trajet(List<Location> Locations) {
        id = 0;
        this.date = 0; //A CHANGER
        this.locations = Locations;
        calculateDistance();
        calculateTime();
    }

    public Trajet(int id, double distance, long temps, long date) {
        this(id, distance, temps, date, new ArrayList<Location>());
    }

    public Trajet(int id, double distance, long temps, long date, List<Location> locations) {
        this.id = id;
        this.distance = distance;
        this.temps = temps;
        this.date = date;
        this.locations = locations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public long getTemps() {
        return temps;
    }

    public Location getLastPosition() {
        return locations.get(locations.size() - 1);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location l) {
        if (locations.size() == 0) {
            distance += 0;
            locations.add(l);
            temps += 0;
        } else {
            Location last = getLastPosition();
            distance += last.distanceTo(l);
            locations.add(l);
            if (locations.size() == 1) {
                temps = ((l.getTime() / 1000) - (last.getTime() / 1000));
            } else {
                temps += ((l.getTime() / 1000) - (last.getTime() / 1000));
            }
        }
    }

    private void calculateDistance() {
        distance = 0;
        if (locations.size() > 2) {
            for (int i = 0; i < locations.size() - 2; i++) {
                distance += locations.get(i).distanceTo(locations.get(i + 1));
            }
        }
    }

    private void calculateTime() {
        temps = 0;
        if (locations.size() > 2) {
            for (int i = 0; i < locations.size() - 2; i++) {
                temps += (locations.get(i).getTime() / 1000) - ((locations.get(i + 1).getTime() / 1000));
            }
        }
    }
    /*
    * Retourne la vitesse moyenne en m/s
     */
    public double averageSpeed() {
        if(distance != 0 && temps != 0){
            return distance / temps;
        }
        return 0;
    }

    /*
    * Retourne l'allure moyenne en min/km
     */
    public double pace() {
        return averageSpeed() * 16.666666666667;
    }
}
