package fr.unicaen.thiblef.gpsproject.dbmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.unicaen.thiblef.gpsproject.model.Parcours;
import fr.unicaen.thiblef.gpsproject.model.Trajet;


public class TrajetDbHandler {

    public static final String TABLE_NAME = "trajet";

    public static final String COL_ID = "_id";

    public static final String COL_PARCOURS_ID = "parcours_id";

    public static final String COL_DATE = "date";

    public static final String COL_TIME = "time";

    public static final String COL_DISTANCE = "distance";

    private DbOpenHelper dbOpenHelper;

    private Context context;

    public TrajetDbHandler(Context context) {
        this.context = context;
        dbOpenHelper = new DbOpenHelper(context, null);
    }

    /**
     * Find all trajets
     *
     * @return List<Trajet>
     */
    public List<Trajet> findAll() {
        String[] cols = new String[]{COL_ID, COL_PARCOURS_ID, COL_DATE, COL_TIME, COL_DISTANCE};
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, cols, null, null, null, null, null);
        return makeTrajetsList(cursor);
    }

    /**
     * Find all trajets from the parcours id
     *
     * @return List<Trajet>
     */
    public List<Trajet> findByParcoursId(int id) {
        String[] cols = new String[]{COL_ID, COL_PARCOURS_ID, COL_DATE, COL_TIME, COL_DISTANCE};
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, cols, COL_PARCOURS_ID + "=?", new String[]{Integer.toString(id)}, null, null, COL_DATE + " DESC");
        return makeTrajetsList(cursor);
    }

    /**
     * Find a trajets by id
     *
     * @return Trajet
     */
    public Trajet findById(int id) {
        String[] cols = new String[]{COL_ID, COL_PARCOURS_ID, COL_DATE, COL_TIME, COL_DISTANCE};
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, cols, COL_ID + "=?", new String[]{Integer.toString(id)}, null, null, null);
        return makeTrajetsList(cursor).get(0);
    }

    private List<Trajet> makeTrajetsList(Cursor cursor) {
        List<Trajet> trajetsList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Trajet trajet = new Trajet(cursor.getInt(cursor.getColumnIndex(COL_ID)),
                    cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE)),
                    cursor.getLong(cursor.getColumnIndex(COL_TIME)),
                    cursor.getLong(cursor.getColumnIndex(COL_DATE)));
            trajetsList.add(trajet);
        }
        return trajetsList;
    }

    /**
     * Delete a trajet
     *
     * @param id trajet id
     */
    public void delete(int id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL_ID + "=" + id);
    }

    /**
     * Add trajet
     *
     * @param trajet Trajet object
     */
    public int add(Trajet trajet, Parcours parcours) {
        return add(trajet, parcours.getId());
    }

    public int add(Trajet trajet, int parcours_id) {
        ContentValues values = new ContentValues();
        values.put(COL_PARCOURS_ID, parcours_id);
        values.put(COL_DATE, trajet.getDate());
        values.put(COL_TIME, trajet.getTemps());
        values.put(COL_DISTANCE, trajet.getDistance());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        return (int) db.insert(TABLE_NAME, null, values);
    }

    public int getParcoursId(int id_trajet) {
        String[] cols = new String[]{COL_PARCOURS_ID};
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, cols, COL_ID + "=?", new String[]{Integer.toString(id_trajet)}, null, null, null);
        return cursor.moveToFirst()?cursor.getInt(cursor.getColumnIndex(COL_PARCOURS_ID)):-1;
    }

    public int getParcoursId(Trajet trajet){
        return getParcoursId(trajet.getId());
    }

    public boolean isTrajetReference(Trajet trajet){
        int parcours_id = getParcoursId(trajet);
        Parcours parcours = new ParcoursDbHandler(context).find(parcours_id);
        return parcours.getIdTrajetReference() == trajet.getId();
    }

    public Trajet getTrajetRef(Trajet trajet){
        int parcours_id = getParcoursId(trajet);
        Parcours parcours = new ParcoursDbHandler(context).find(parcours_id);
        return findById(parcours.getIdTrajetReference());
    }
}
