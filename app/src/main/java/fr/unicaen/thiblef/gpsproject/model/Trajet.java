package fr.unicaen.thiblef.gpsproject.model;

import android.location.Location;
import android.location.LocationManager;

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
        calculateDistance();
        calculateTime();
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

    /**
     * Retourne la vitesse moyenne en m/s
     *
     * @return la vitesse moyenne en m/s
     */
    public double averageSpeed() {
        if (distance != 0 && temps != 0) {
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

    public static Location getNormalCoordonate(Location t1, Location t2, Location d1){
        double xa = d1.getLatitude();
        double ya = d1.getLongitude();

        double xb = t1.getLatitude();
        double yb = t1.getLongitude();

        double xc = t2.getLatitude();
        double yc = t2.getLongitude();

        //Calcul du coefficient directeur (m) de la droite (d) passant par t1 et t2
        double m = (xb-xc) / (yb-yc);

        //Détermination de l'ordonnée à l'origine (n)
        double n = yb-m*xb;

        //Calcul du coefficient directeur (m') de la normale à d
        double mp = (-1/m);

        //Détermination de l'ordonnée à l'origine (n')
        double np = ya-mp*xa;

        //Calcul des coordonnées de H(x,y) l'intersection des deux droites
        double y = ((m*m+np+n)/m*m+1);
        double x = y*m+np*m;

        Location h = new Location(LocationManager.GPS_PROVIDER);
        h.setLatitude(x);
        h.setLongitude(y);
        double delta = h.distanceTo(t1)/t1.distanceTo(t2);
        double time = delta * (t2.getTime()-t1.getTime());

        h.setTime((long) time);
        return h;
    }
}
