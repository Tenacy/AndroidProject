package fr.unicaen.thiblef.gpsproject.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime.
 */
public class Parcours implements Comparable{
    /**
     * id du parcours
     */
    protected int id;

    /**
     * Nom du parcours
     */
    protected String name;

    /**
     * distance approximative du parcours (en m)
     */
    protected double distance;

    /**
     * Meilleur temps du parcours (en seconde)
     */
    protected long bestTime;

    /**
     * Moyenne des vitesses (m/s) de tous les trajets
     */
    protected double averageSpeed;

    /**
     * Meilleure vitesse (m/s) des trajets
     */
    protected double maxSpeed;

    /**
     * Liste des trajets
     */
    protected List<Trajet> trajets;

    /**
     * Id du trajet de référence
     */
    protected int idTrajetReference;

    public Parcours() {
        this("");
    }

    public Parcours(String name) {
        this(0, name, 0, 0, 0, 0, new ArrayList<Trajet>(),-1);
    }

    public Parcours(int id, String name, double distance, long bestTime, double averageSpeed, double maxSpeed) {
        this(id, name, distance, bestTime, averageSpeed, maxSpeed, new ArrayList<Trajet>(),-1);
    }

    public Parcours(int id, String name, double distance, long bestTime, double averageSpeed, double maxSpeed, List<Trajet> trajets,int idTrajetReference) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.bestTime = bestTime;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
        this.trajets = trajets;
        this.idTrajetReference = idTrajetReference;
    }

    public int getIdTrajetReference() {
        return idTrajetReference;
    }

    public void setIdTrajetReference(int idTrajetReference) {
        this.idTrajetReference = idTrajetReference;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addTrajet(Trajet t) {
        trajets.add(t);
    }

    public void removeTrajet(Trajet t) {
        trajets.remove(t);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public long getBestTime() {
        return bestTime;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public List<Trajet> getTrajets() {
        return trajets;
    }

    private void calcBestTime(){
        if(!trajets.isEmpty()){
            long bestTime = trajets.get(0).getTemps();
            for(Trajet trajet : trajets){
                if(trajet.getTemps() < bestTime){
                    bestTime = trajet.getTemps();
                }
            }
            this.bestTime = bestTime;
        } else {
            this.bestTime = 0;
        }
    }

    private void calcAverageSpeed(){
        long timeSum = 0;
        double distanceSum = 0;
        if(!trajets.isEmpty()) {
            for(Trajet trajet : trajets) {
                distanceSum += trajet.getDistance();
                timeSum += trajet.getTemps();
            }
            if(distanceSum != 0 && timeSum != 0) {
                this.averageSpeed = distanceSum / timeSum;
            } else {
                this.averageSpeed = 0;
            }
        } else{
            this.averageSpeed = 0;
        }
    }

    private void calcMaxSpeed(){
        double maxSpeed = 0;
        if(!trajets.isEmpty()) {
            for(Trajet trajet : trajets) {
                if((trajet.getDistance() / trajet.getTemps()) > maxSpeed){
                    maxSpeed = trajet.getDistance() / trajet.getTemps();
                }
            }
            this.maxSpeed = maxSpeed;
        } else{
            this.maxSpeed = 0;
        }
    }

    private void calcMaxDistance(){
        double maxDistance = 0;
        if(!trajets.isEmpty()) {
            for(Trajet trajet : trajets) {
                if(trajet.getDistance() > maxDistance){
                    maxDistance = trajet.getDistance();
                }
            }
            this.distance = maxDistance;
        } else{
            this.distance = maxDistance;
        }
    }

    public long getAverageTime(){
        long timeSum = 0;
        if(!trajets.isEmpty()) {
            for(Trajet trajet : trajets) {
                timeSum += trajet.getTemps();
            }
            return timeSum / trajets.size();
        } else{
            return 0;
        }
    }

    public void update(){
        calcAverageSpeed();
        calcBestTime();
        calcMaxSpeed();
        calcMaxDistance();
    }

    @Override
    public int compareTo(Object otherParcours) {
        Parcours other = (Parcours) otherParcours;
        long date = getLastTrajetDate();
        long otherDate = other.getLastTrajetDate();
        if (date > otherDate) {
            return -1;
        }
        if (date < otherDate) {
            return 1;
        }
        return 0;
    }

    public long getLastTrajetDate(){
        if(!trajets.isEmpty()) {
            long maxDate = trajets.get(0).getDate();
            for (Trajet trajet : trajets) {
                if(trajet.getDate()>maxDate){
                    maxDate = trajet.getDate();
                }
            }
            return maxDate;
        }
        return 0;
    }
}