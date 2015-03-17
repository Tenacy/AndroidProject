package fr.unicaen.thiblef.gpsproject.xml;

import android.content.Context;
import android.util.Log;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import fr.unicaen.thiblef.gpsproject.model.Trajet;

/**
 * Created by Maxime on 08/03/2015.
 */
public class GPXReader {

    protected Trajet trajet;
    protected Context context;

    public GPXReader(Context context, Trajet trajet) {
        this.trajet = trajet;
        this.context = context;
    }

    public void parse() {
        String nameFile = GPXWriter.NAME + trajet.getId() + ".gpx";
        String baseFolder = BaseFolder.getBaseFolder(context);
        Log.w("nameFile:", nameFile);
        Log.w("baseFolder:", baseFolder);
        File file = new File(baseFolder + "/" + nameFile);
        DefaultHandler gpxHandler = new GPXHandler(trajet);
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(file, gpxHandler);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
