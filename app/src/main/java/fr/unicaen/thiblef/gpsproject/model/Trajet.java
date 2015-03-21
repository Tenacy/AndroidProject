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

    protected List<Location> interpolations;

    public Trajet() {
        this(0, 0, 0, 0, new ArrayList<Location>());
    }

    public Trajet(List<Location> Locations) {
        id = 0;
        this.date = 0; //A CHANGER
        this.locations = Locations;
        this.interpolations = new ArrayList<>();
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

    public List<Location> getInterpolations() {
        return interpolations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        calculateDistance();
        calculateTime();
    }

    public void addLocation(Location l) {
        if (locations.size() == 0) {
            distance = 0;
            locations.add(l);
            temps = 0;
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


    public static Location getNormalCoordonate(Location t1, Location t2, Location d1) {
        double xa = d1.getLatitude();
        double ya = d1.getLongitude();

        double xb = t1.getLatitude();
        double yb = t1.getLongitude();

        double xc = t2.getLatitude();
        double yc = t2.getLongitude();

        //Calcul du coefficient directeur (m) de la droite (d) passant par t1 et t2
        double m = (xb - xc) / (yb - yc);

        //Détermination de l'ordonnée à l'origine (n)
        double n = yb - m * xb;

        //Calcul du coefficient directeur (m') de la normale à d
        double mp = (-1 / m);

        //Détermination de l'ordonnée à l'origine (n')
        double np = ya - mp * xa;

        //Calcul des coordonnées de H(x,y) l'intersection des deux droites
        double y = ((m * m + np + n) / m * m + 1);
        double x = y * m + np * m;

        Location h = new Location(LocationManager.GPS_PROVIDER);
        h.setLatitude(x);
        h.setLongitude(y);
        double delta = h.distanceTo(t1) / t1.distanceTo(t2);
        double time = delta * (t2.getTime() - t1.getTime());

        h.setTime((long) time);
        return h;
    }

    public static boolean isOnSegment(Location t1, Location t2, Location h) {
        double latMin = Math.min(t1.getLatitude(), t2.getLatitude());
        double latMax = Math.max(t1.getLatitude(), t2.getLatitude());
        double lonMin = Math.min(t1.getLongitude(), t2.getLongitude());
        double lonMax = Math.max(t1.getLongitude(), t2.getLongitude());
        double lat = h.getLatitude();
        double lon = h.getLongitude();
        return lat >= latMin && lat <= latMax && lon >= lonMin && lon <= lonMax;
    }

    /*public static void calcNearestSegment(Location point,long date, Trajet trajet_ref){
        if(trajet_ref.getLocations().size() < 2){
            return;
        }
        long pointTime = point.getTime() - date;
        Location min1 = trajet_ref.getLocations().get(0);
        Location min2 = trajet_ref.getLocations().get(0);
        double distanceToMin1 = Double.MAX_VALUE;
        double distanceToMin2 = Double.MAX_VALUE;
        long deltaTimeToMin1 = Long.MAX_VALUE;
        long deltaTimeToMin2 = Long.MAX_VALUE;
        for(Location location : trajet_ref.getLocations()){
            long locationTime = location.getTime() - trajet_ref.getDate();
            double distanceToLocation = point.distanceTo(location);
            if(distanceToLocation < distanceToMin1){
                distanceToMin2 = distanceToMin1;
                min2 = min1;
                distanceToMin1 = distanceToLocation;
                min1 = location;
            } else if(distanceToLocation < distanceToMin2){
                distanceToMin2 = distanceToLocation;
                min2 = location;
            }
        }
    }*/

    public void calculateInterpolations(Trajet trajet_ref) {
        int iRef = 0;
        int i = 0;
        int val = 0;
        while (i < locations.size() || iRef < trajet_ref.getLocations().size() - 1) {
            val = calculateInterpolations(trajet_ref, locations.get(i), iRef);
            if (val == -1) {
                i++;
            } else {
                iRef = val;
            }
            i++;
        }
    }

    protected int calculateInterpolations(Trajet trajet_ref, Location newLocation, int reference) {
        int iRef = reference;
        List<Location> refLocations = trajet_ref.getLocations();
        Location t1, t2, h;
        while (iRef < refLocations.size() - 1) {
            t1 = refLocations.get(iRef);
            t2 = refLocations.get(iRef + 1);
            h = getNormalCoordonate(t1, t2, newLocation);
            if (isOnSegment(t1, t2, h)) {
                interpolations.add(h);
                return iRef;
            } else {
                iRef++;
            }
        }
        return -1;
    }

    public List<Location> getCorrespondance(Trajet trajet_ref){
        int iRef = 0;
        int i = 0;
        List<Location> refLocations = trajet_ref.getLocations();
        List<Location> result = new ArrayList<>();
        while(iRef < refLocations.size() && i < locations.size()){
            Location t1 = refLocations.get(iRef);
            Location t2 = refLocations.get(iRef+1);
            Location d1 = locations.get(i);
            Location h = getNormalCoordonate(t1, t2, d1);
            if(isOnSegment(t1, t2, h)){
                result.add(h);
                i++;
            } else {
                iRef++;
            }
        }
        return result;
    }

    public long getRetard(int locationIndice ,Trajet trajet_ref){
        List<Location> correspondance = getCorrespondance(trajet_ref);
        long timeActual = locations.get(locationIndice).getTime() - date;
        long timeRef = correspondance.get(locationIndice).getTime() - trajet_ref.getDate();
        return timeRef - timeActual;
    }

    public long getLiveRetard(Trajet trajet_ref){
        List<Location> correspondance = getCorrespondance(trajet_ref);
        long timeActual = locations.get(locations.size()-1).getTime() - date;
        long timeRef = correspondance.get(locations.size()-1).getTime() - trajet_ref.getDate();
    return timeRef - timeActual;
    }
}
