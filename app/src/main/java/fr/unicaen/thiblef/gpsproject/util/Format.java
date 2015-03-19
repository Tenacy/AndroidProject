package fr.unicaen.thiblef.gpsproject.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Thibault on 09/03/2015.
 */
public class Format {

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%02d:%02d'%02d", h, m, s);
    }
    public static String convertSecondsToMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        return String.format("%02d'%02d", m, s);
    }

    public static String convertToDate(long date) {
        return new SimpleDateFormat("dd/MM/yy HH:mm", Locale.FRANCE).format(date);
    }

    public static String convertToKm(double distance) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2); //nb de chiffres apres la virgule
        String s = format.format(distance / 1000);   //donne la chaine representant le double avec 1 chiffres apres la virgules
        return s.replace('.', ',') + "km";
    }

    public static String convertToKmH(double vitesse) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(1); //nb de chiffres apres la virgule
        String s = format.format(vitesse * 3.6);   //donne la chaine representant le double avec 1 chiffres apres la virgules
        return s.replace('.', ',') + "km/h";
    }

    public static String convertToMinKm(double vitesse) {
        if(vitesse == 0){
            return "0:00min/km";
        }
        double minkm = 60 / (vitesse * 3.6);
        int min = (int) minkm;
        double dif = (minkm - min) * 60;
        int sec = (int) dif;
        String secs = Integer.toString(sec);
        if(sec < 10){
            secs = "0"+secs;
        }
        return min + ":" + secs + "min/km";
    }
}
