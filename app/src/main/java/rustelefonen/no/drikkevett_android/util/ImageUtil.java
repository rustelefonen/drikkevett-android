package rustelefonen.no.drikkevett_android.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by simenfonnes on 27.07.2016.
 */

public class ImageUtil {
    private static final String APP_TAG = "MyCustomApp";

    public static void copyFile(File sourceFile, File destFile) {
        if (!sourceFile.exists()) return;

        FileChannel source;
        FileChannel destination;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();

            if (source != null) {
                destination.transferFrom(source, 0, source.size());
                source.close();
            }
            destination.close();
        } catch (IOException ignored) {}
    }

    public static Uri getPhotoFileUri(String fileName, Context context) {
        if (!isExternalStorageAvailable()) return null;
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    // Returns true if external storage for photos is available
    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


}
