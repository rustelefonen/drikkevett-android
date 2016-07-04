package rustelefonen.no.drikkevett_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class BacHomeFragment extends Fragment{

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bac_home_frag, container, false);

        TextView tv = (TextView) v.findViewById(R.id.textViewHome);
        tv.setText("Hjem Skjerm");


        Uri photoToInsert = getPhotoFileUri(photoFileName);

        File file = new File(photoToInsert.getPath());

        if (file.exists()) {
            if (photoToInsert != null) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoToInsert.getPath());
                System.out.println("bitmap count: " + takenImage.getByteCount());
                //profileImage.setImageBitmap(takenImage);

                ImageView ivPreview = (ImageView) v.findViewById(R.id.profile_image);
                ivPreview.setImageBitmap(takenImage);
                System.out.println("Prøver å sette inn bilde");
            }
        } else {
            System.out.println("Bildet eksisterer ikke");
        }



        return v;
    }

    public Uri getPhotoFileUri(String fileName) {
        if (!isExternalStorageAvailable()) return null;
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}


