package fr.unicaen.thiblef.gpsproject.dbmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.unicaen.thiblef.gpsproject.model.Parcours;
import fr.unicaen.thiblef.gpsproject.model.Trajet;

public class ParcoursDbHandler {

    public static final String TABLE_NAME = "parcours";

    public static final String COL_ID = "_id";

    public static final String COL_NAME = "name";

    public static final String COL_IDREFERENCE = "idReference";

    public static final String COL_DISTANCE = "distance";

    public static final String COL_BEST_TIME = "best_time";

    public static final String COL_AVERAGE_SPEED = "average_speed";

    public static final String COL_MAX_SPEED = "max_speed";

    private DbOpenHelper dbOpenHelper;

    private Context context;

    /**
     * Build ParcoursDbHandler
     *
     * @param context
     */
    public ParcoursDbHandler(Context context) {
        this.context = context;
        dbOpenHelper = new DbOpenHelper(context, null);
    }

    /**
     * Find all parcours
     *
     * @return List<Parcours>
     */
    public List<Parcours> findAll() {
        String[] cols = new String[]{COL_ID, COL_NAME, COL_DISTANCE, COL_BEST_TIME, COL_AVERAGE_SPEED, COL_MAX_SPEED, COL_IDREFERENCE};
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, cols, null, null, null, null, null);
        return makeParcoursList(cursor);
    }

    /**
     * Find a parcours by id
     *
     * @return Parcours
     */
    public Parcours find(int id) {
        String[] cols = new String[]{COL_ID, COL_NAME, COL_DISTANCE, COL_BEST_TIME, COL_AVERAGE_SPEED, COL_MAX_SPEED, COL_IDREFERENCE};
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, cols, COL_ID + "=?", new String[]{Integer.toString(id)}, null, null, null);
        return makeParcoursList(cursor).get(0);
    }

    private List<Parcours> makeParcoursList(Cursor cursor) {
        List<Parcours> parcoursList = new ArrayList<>();
        TrajetDbHandler trajetHandler = new TrajetDbHandler(context);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            Parcours parcours = new Parcours(cursor.getInt(cursor.getColumnIndex(COL_ID)),
                    cursor.getString(cursor.getColumnIndex(COL_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE)),
                    cursor.getLong(cursor.getColumnIndex(COL_BEST_TIME)),
                    cursor.getDouble(cursor.getColumnIndex(COL_AVERAGE_SPEED)),
                    cursor.getDouble(cursor.getColumnIndex(COL_MAX_SPEED)),
                    trajetHandler.findByParcoursId(cursor.getInt(cursor.getColumnIndex(COL_ID))),
                    cursor.getInt(cursor.getColumnIndex(COL_IDREFERENCE)));
            parcoursList.add(parcours);
        }
        return parcoursList;
    }


    /**
     * Delete a parcours
     *
     * @param id parcours id
     */
    public void delete(int id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL_ID + "=" + id);
    }

    /**
     * Delete a parcours and all his trajets
     *
     * @param parcours Parcours object
     */
    public void delete(Parcours parcours) {
        delete(parcours.getId());
        TrajetDbHandler trajetDbHandler = new TrajetDbHandler(context);
        for (Trajet trajet : parcours.getTrajets()) {
            trajetDbHandler.delete(trajet.getId());
        }
    }

    /**
     * Add parcours
     *
     * @param parcours Parcours object
     */
    public int add(Parcours parcours) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        return (int) db.insert(TABLE_NAME, null, getContentValues(parcours));
    }

    /**
     * Update parcours
     *
     * @param parcours Parcours object
     */
    public void update(Parcours parcours) {
        parcours.update();
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.update(TABLE_NAME, getContentValues(parcours), COL_ID + "=?", new String[]{Integer.toString(parcours.getId())});
    }

    private ContentValues getContentValues(Parcours parcours) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, parcours.getName());
        values.put(COL_DISTANCE, parcours.getDistance());
        values.put(COL_BEST_TIME, parcours.getBestTime());
        values.put(COL_AVERAGE_SPEED, parcours.getAverageSpeed());
        values.put(COL_MAX_SPEED, parcours.getMaxSpeed());
        values.put(COL_IDREFERENCE, parcours.getIdTrajetReference());
        return values;
    }

}
