package fr.unicaen.thiblef.gpsproject.xml;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.unicaen.thiblef.gpsproject.model.Trajet;

/**
 * Created by 20900977 on 06/03/15.
 */
public class GPXWriter {
    public final static String NAME = "Trajet_";
    private final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.9.2\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
    private final String FOOTER = "</gpx>";

    protected Context context;
    protected Trajet trajet;
    protected FileOutputStream fos;
    protected File file;


    public GPXWriter(Context context, Trajet trajet) {
        this.trajet = trajet;
        this.context = context;
        String nameFile = NAME + this.trajet.getId() + ".gpx";
        String baseFolder = BaseFolder.getBaseFolder(context);
        file = new File(baseFolder + nameFile);
        try {
            fos = context.openFileOutput(nameFile, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Fichier introuvable", Toast.LENGTH_LONG);
        }
    }

    public void convertToXml() {
        List<Location> locations = trajet.getLocations();

        String stream = HEADER + "\n";
        stream += "<trk>\n\t<name>" + NAME + this.trajet.getId() + "</name>\n";

        for (Location l : locations) {
            stream += "\t<trkpt lat=\"" + l.getLatitude() + "\" lon=\"" + l.getLongitude() + "\">\n";
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            stream += "\t\t<time>" + df.format(new Date(l.getTime() * 1000)) + "</time>\n";
            stream += "\t</trkpt>\n";
        }
        stream += "</trk>\n";
        stream += FOOTER;
        long size = 0;
        for (Byte b : stream.getBytes()) {
            size += b.longValue();
        }
        Log.i("Bytes : ", Long.toString(size));
        Log.i("Free space :", Long.toString(file.getFreeSpace()));
        if (size >= file.getFreeSpace()) {
            try {
                fos.write(stream.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Erreur lors de l'écriture du fichier", Toast.LENGTH_LONG);
            }
        }
    }


}
