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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;

import static java.util.Calendar.HOUR;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

public class BacHomeFragment extends Fragment{

    //Fields
    public final String APP_TAG = "MyCustomApp";
    public String photoFileName = "photo.jpg";

    //Widgets
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
        String[] quotes = new String[] {"Visste du at du forbrenner ca. 0,15 promille per time",
                "Visste du at shotting og rask drikking gjør at kroppen ikke rekker å registere alkoholen før du tilsetter den mer? Da er risikoen for å miste kontroll stor!",
                "Nyt alkohol med måte, det vil både gjøre din kveld bedre og andres kveld mer hyggelig",
                "Det er ok å ta en shot med venner, men pass på at du ikke tar en for mye",
                "Ca 90 % av alkoholen forbrennes i leveren, resten utskilles i utåndingsluften, urinen og svetten",
                "Evnen til innlæring synker drastisk etter 0.4 i promille",
                "Alkohol gjør at hjernen blir selektiv slik at du lettere irriterer deg over småting",
                "Med promille fra 1.4 og oppover kan du få blackout",
                "Drikker du fort, er risikoen for blackout større",
                "Drikker du sprit, risikerer du å få for høy promille for fort",
                "Det beste rådet mot fyllesyke er å ha drikkevett dagen før",
                "Slutter du å drikke i god tid før du legger deg, minsker sjansen for å bli fyllesyk",
                "Er promillen høy, kan det være farlig å legge seg på stigende promille",
                "Er du ofte på fylla, øker risikoen for overvekt og ernæringsmangler.",
                "Alkohol inneholder mye kalorier og karbohydrater ",
                "Alkohol kan gi dårligere treningseffekt både på kort og lang sikt.",
                "Alkohol gir dårligere prestasjonsevne",
                "Planlegger du på forhånd hvor lite du skal drikke, er det lettere å holde seg til målet",
                "Spiser du et godt måltid før du begynner å drikke, blir promillen jevnere og litt lettere å kontrollere",
                "Alkohol er dehydrerende; drikk vann mellom hvert glass alkohol",
                "Det er ingen skam å tåle minst",
                "Er du syk, stresset, sover dårlig, eller bruker medisiner e.l. tåler du mindre alkohol, enn når du er frisk og uthvilt",
                "Alkohol gjør at du fortere mistforstår andre mennesker og situasjoner"};
        int randomIndex = (int) (Math.random() * quotes.length-1 + 1);
        return quotes[randomIndex];
    }

    private String getRandomWelcomeMessage() {
        String[] welcomeMessages = new String[] {"Hei", "Halla", "Hallo", "Whats up?", "Hallois",
                "Skjer a?", "Skjer?", "God dag", "Ha en fin dag", "Hallo", "Que pasa?", "Morn",
                "Åssen går det?", "Står til?", "Läget?"};
        int randomIndex = (int) (Math.random() * welcomeMessages.length-1 + 1);
        return welcomeMessages[randomIndex];
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
        ivPreview = (ImageView) view.findViewById(R.id.profile_image);
        quoteTextView = (TextView) view.findViewById(R.id.quote_text_view);
    }

    private void fillWidgets() {
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

    private boolean historyIsEmpty() {
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        HistoryDao historyDao = daoSession.getHistoryDao();

        List<History> historyList = historyDao.queryBuilder().list();

        return historyList.size() <= 0;
    }

    private int getTotalCost(List<History> historyList) {
        int totalCost = 0;
        for (History history : historyList) {
            totalCost += history.getSum();
        }
        return totalCost;
    }

    private double getTotalHighestBac(List<History> historyList) {
        double highestBac = 0.0;
        for (History history : historyList) {
            if (history.getHighestBAC() > highestBac) {
                highestBac = history.getHighestBAC();
            }
        }
        return highestBac;
    }

    private double getTotalAverageHighestBac(List<History> historyList) {
        double sum = 0.0;
        for (History history : historyList) {
            sum += history.getHighestBAC();
        }
        return sum / historyList.size();
    }

    private int getLastMonthCost(List<History> historyList) {
        int sum = 0;
        for (History history : historyList) {
            if (dateIsWithin30Days(history.getStartDate())) {
                sum += history.getSum();
            }
        }
        return sum;
    }

    private double getLastMonthHighestBac(List<History> historyList) {
        double highestBac = 0.0;
        for (History history : historyList) {
            if (dateIsWithin30Days(history.getStartDate())) {
                if (history.getHighestBAC() > highestBac) {
                    highestBac = history.getHighestBAC();
                }
            }
        }
        return highestBac;
    }

    private double getLastMonthAverageBac(List<History> historyList) {
        double sum = 0.0;
        int highestBacCount = 0;
        for (History history : historyList) {
            if (dateIsWithin30Days(history.getStartDate())) {
                sum += history.getHighestBAC();
                highestBacCount++;
            }
        }
        return sum / highestBacCount;
    }

    private boolean dateIsWithin30Days(Date dateToTest) {
        Date thirtyDaysAgo = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(thirtyDaysAgo);
        cal.add(Calendar.DATE, -30);

        return cal.getTime().before(dateToTest);
    }

    private List<History> getHistoryList() {
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        HistoryDao historyDao = daoSession.getHistoryDao();

        return historyDao.queryBuilder().list();
    }

    private boolean hasLastMonth() {
        return false;
    }
}


