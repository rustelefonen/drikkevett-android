package rustelefonen.no.drikkevett_android.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import rustelefonen.no.drikkevett_android.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by simenfonnes on 01.07.2016.
 */

public class CameraUtil {

    //Fields
    public final String APP_TAG = "MyCustomApp";
    private Context context;

    public CameraUtil(Context context) {
        this.context = context;
    }

    public Uri getPhotoFileUri(String fileName) {
        if (!isExternalStorageAvailable()) return null;
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    // Returns true if external storage for photos is available
    public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
