package fr.unicaen.thiblef.gpsproject.xml;

import android.location.Location;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import fr.unicaen.thiblef.gpsproject.model.Trajet;

/**
 * Created by Maxime on 08/03/2015.
 */
public class GPXHandler extends DefaultHandler {
    protected Trajet trajet;
    private double latitude;
    private double longitude;
    private long date;
    private boolean inNode;

    public GPXHandler(Trajet trajet) {
        this.trajet = trajet;
        latitude = 0;
        longitude = 0;
        inNode = false;
    }

    /**
     * This will be called when the tags of the XML starts.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (localName.equals("trkpt")) {
            inNode = true;
            latitude = Double.parseDouble(attributes.getValue("lat"));
            longitude = Double.parseDouble(attributes.getValue("lon"));
        }
        if (localName.equals("time")) {
            inNode = true;
        }
    }

    /**
     * This will be called when the tags of the XML end.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("time")) {
            inNode = false;
        }
        if (localName.equals("trkpt")) {
            Location location = new Location("SaxParser");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setTime(date);
            trajet.addLocation(location);
        }
    }

    /**
     * This is called to get the tags value
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inNode) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                date = df.parse(new String(ch, start, length)).getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

