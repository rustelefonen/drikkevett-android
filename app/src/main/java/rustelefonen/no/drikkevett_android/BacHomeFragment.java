package rustelefonen.no.drikkevett_android;

import android.database.sqlite.SQLiteDatabase;
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
import java.util.List;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;

public class BacHomeFragment extends Fragment{

    //Fields
    public final String APP_TAG = "MyCustomApp";
    public String photoFileName = "photo.jpg";

    //Widgets
    public TextView tv;
    public TextView quoteTextView;
    public ImageView ivPreview;
    public TextView helloMessageTextView;
    public TextView usernameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bac_home_frag, container, false);
        //insertUser();
        initWidgets(view);
        fillWidgets();
        return view;
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

    private String getRandomWelcomeMessage() {
        String[] quoteArray = new String[] {"Hei", "Halla", "Hallo", "Whats up?", "Hallois",
                "Skjer a?", "Skjer?", "God dag", "Ha en fin dag", "Hallo", "Que pasa?", "Morn",
                "Åssen går det?", "Står til?", "Läget?"};
        int randomIndex = (int) (Math.random() * quoteArray.length-1 + 1);
        return quoteArray[randomIndex];
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

    private String getUsername() {
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        UserDao userDao = daoSession.getUserDao();

        List<User> userList = userDao.queryBuilder().list();

        if (userList.size() <= 0) return "Tom bruker";
        User currentUser = userList.get(0);
        if (currentUser.getNickname() == null || currentUser.getNickname().isEmpty()) return "Tom bruker";

        return currentUser.getNickname();

    }

    private void initWidgets(View view) {
        helloMessageTextView = (TextView) view.findViewById(R.id.hello_message_text_view);
        usernameTextView = (TextView) view.findViewById(R.id.user_name_text_view);
        tv = (TextView) view.findViewById(R.id.textViewHome);
        ivPreview = (ImageView) view.findViewById(R.id.profile_image);
        quoteTextView = (TextView) view.findViewById(R.id.quote_text_view);
    }

    private void fillWidgets() {
        tv.setText("Hjem Skjerm");
        usernameTextView.setText(getUsername());
        helloMessageTextView.setText(getRandomWelcomeMessage());

        quoteTextView.setText(getRandomQuote());
        insertImageIfExists();
    }

    private void insertUser() {
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        UserDao userDao = daoSession.getUserDao();

        User newUser = new User();
        newUser.setAge(14);
        newUser.setNickname("fonsim");

        userDao.insert(newUser);
    }
}


