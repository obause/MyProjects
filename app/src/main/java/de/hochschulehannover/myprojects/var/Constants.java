package de.hochschulehannover.myprojects.var;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.text.SimpleDateFormat;

public class Constants {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    public static final SimpleDateFormat CLOCK_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd  HH:mm");

    public static final String USERS_TABLE = "users";
    public static final String PROJECTS_TABLE = "projects";

    public static final String IMAGE = "image";
    public static final String NAME = "name";

    public static final String ASSIGNED_TO = "assignedUsers";

    // TODO: In Constants Klasse packen
    public static int READ_STORAGE_PERMISSION_CODE = 1;
    public static int PICK_IMAGE_REQUEST_CODE = 2;

    public static void showImageChooser(Activity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // TODO: Veraltete Funktion ersetzen
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    }

    //Dateityp zurückgeben
    public static String getFileExtension(Activity activity, Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.getContentResolver().getType(uri));
    }

}
