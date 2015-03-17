package fr.unicaen.thiblef.gpsproject.dbmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpenHelper extends SQLiteOpenHelper {
    // SQL Statement to create a new database.

    private static final String DB_NAME = "gps.db";

    private static final int DB_VERSION = 1;

    private static final String DB_CREATE_PARCOURS = "CREATE TABLE " + ParcoursDbHandler.TABLE_NAME + "( " +
            ParcoursDbHandler.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ParcoursDbHandler.COL_NAME + " TEXT, " +
            ParcoursDbHandler.COL_DISTANCE + " REAL, " +
            ParcoursDbHandler.COL_BEST_TIME + " INTEGER, " +
            ParcoursDbHandler.COL_IDREFERENCE + " INTEGER, " +
            ParcoursDbHandler.COL_AVERAGE_SPEED + " REAL, " +
            ParcoursDbHandler.COL_MAX_SPEED + " REAL " +
            " )";

    private static final String DB_CREATE_TRAJET = "CREATE TABLE " + TrajetDbHandler.TABLE_NAME + "( " +
            TrajetDbHandler.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TrajetDbHandler.COL_PARCOURS_ID + " INTEGER, " +
            TrajetDbHandler.COL_DATE + " INTEGER, " +
            TrajetDbHandler.COL_TIME + " INTEGER, " +
            TrajetDbHandler.COL_DISTANCE + " REAL " +
            " )";

    private static final String DB_DELETE_PARCOURS = "DROP TABLE " + ParcoursDbHandler.TABLE_NAME;

    private static final String DB_DELETE_TRAJET = "DROP TABLE " + TrajetDbHandler.TABLE_NAME;

    public DbOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_PARCOURS);
        db.execSQL(DB_CREATE_TRAJET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // gestion sans états d'âmes : supprimer la table et la recréer
        db.execSQL(DB_DELETE_PARCOURS);
        db.execSQL(DB_DELETE_TRAJET);
        onCreate(db);
    }
}

