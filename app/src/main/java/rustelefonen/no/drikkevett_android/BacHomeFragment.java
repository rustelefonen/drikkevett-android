package rustelefonen.no.drikkevett_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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

    //Widgets
    public TextView tv;
    public TextView quoteTextView;
    public ImageView ivPreview;
    public TextView helloMessageTextView;
    public TextView usernameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bac_home_frag, container, false);

        helloMessageTextView = (TextView) v.findViewById(R.id.hello_message_text_view);
        usernameTextView = (TextView) v.findViewById(R.id.user_name_text_view);
        tv = (TextView) v.findViewById(R.id.textViewHome);
        tv.setText("Hjem Skjerm");

        ivPreview = (ImageView) v.findViewById(R.id.profile_image);

        quoteTextView = (TextView) v.findViewById(R.id.quote_text_view);
        quoteTextView.setText(getRandomQuote());

        insertImageIfExists();

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

    private String getRandomQuote() {
        return "Det er ok å ta en shot med venner, men pass på at du ikke tar en for mye";
    }

    private void insertImageIfExists() {
        Uri photoToInsert = getPhotoFileUri(photoFileName);
        File file = new File(photoToInsert.getPath());

        if (file.exists()) {
            Bitmap takenImage = BitmapFactory.decodeFile(photoToInsert.getPath());
            ivPreview.setImageBitmap(takenImage);
        } else {
            System.out.println("Bildet eksisterer ikke");
        }
    }
}


