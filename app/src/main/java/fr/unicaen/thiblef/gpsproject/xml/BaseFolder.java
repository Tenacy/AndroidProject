package fr.unicaen.thiblef.gpsproject.xml;

import android.content.Context;
import android.os.Environment;

/**
 * Created by Maxime on 09/03/2015.
 */
public class BaseFolder {
    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getBaseFolder(Context context) {
        String baseFolder;
        //Ajouter option de selection
       /* if (isExternalStorageWritable() && isExternalStorageReadable()) {
            baseFolder = context.getExternalFilesDir(null).getAbsolutePath();
        } else {*/
        baseFolder = context.getFilesDir().getAbsolutePath();
        //}
        return baseFolder;
    }
}
