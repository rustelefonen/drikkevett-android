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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;

public class BacHomeFragment extends Fragment{

    //Fields
    public final String APP_TAG = "MyCustomApp";
    public String photoFileName = "photo.jpg";

    //Widgets
    public TextView quoteTextView;
    public ImageView ivPreview;
    public TextView helloMessageTextView;
    public TextView usernameTextView;

    public TextView totalCostTextView;
    public TextView totalHighestBac;
    public TextView totalAvgTextView;

    public TextView lastMonthCostTextView;
    public TextView lastMonthHighestBacTextView;
    public TextView lastMonthAvgBacTextView;

    public PieChart goalPieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bac_home_frag, container, false);
        addHistory();
        initWidgets(view);
        fillWidgets();


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(12f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");

        PieData data = new PieData(labels, dataset); // initialize Piedata
        goalPieChart.setData(data);




        //Styling chart:

        //pieChartDataSet.colors = [UIColor(red: 193/255.0, green: 26/255.0, blue: 26/255.0, alpha: 1.0), UIColor(red:26/255.0, green: 193/255.0, blue: 73/255.0, alpha: 1.3)]

        goalPieChart.setDrawHoleEnabled(true);
        goalPieChart.setHoleRadius((float) 0.80);
        goalPieChart.setHoleColor(1);   //Usikker UIColor(red: 20/255, green: 20/255, blue: 20/255, alpha: 0.0)
        goalPieChart.setCenterTextRadiusPercent((float) 1.0);
        goalPieChart.setTransparentCircleRadius((float) 0.85);
        //goalPieChart.setAnimation();
        goalPieChart.setDescription("");
        goalPieChart.setBackgroundColor(1); //Usikker UIColor(red: 20/255, green: 20/255, blue: 20/255, alpha: 0.0)
        goalPieChart.setTransparentCircleColor(1); //Usikker
        goalPieChart.setDrawSliceText(false);
        goalPieChart.getLegend().setEnabled(false);
        //pieChartView.userInteractionEnabled = false

        goalPieChart.setCenterText("0.5");

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
        totalCostTextView = (TextView) view.findViewById(R.id.total_cost_text_view);
        totalHighestBac = (TextView) view.findViewById(R.id.total_highest_bac);
        totalAvgTextView = (TextView) view.findViewById(R.id.total_avg_text_view);
        lastMonthCostTextView = (TextView) view.findViewById(R.id.last_month_cost_text_view);
        lastMonthHighestBacTextView = (TextView) view.findViewById(R.id.last_month_highest_bac_text_view);
        lastMonthAvgBacTextView = (TextView) view.findViewById(R.id.last_month_avg_bac_text_view);

        goalPieChart = (PieChart) view.findViewById(R.id.goal_pie_chart);
    }

    private void fillWidgets() {
        usernameTextView.setText(getUsername());
        helloMessageTextView.setText(getRandomWelcomeMessage());
        quoteTextView.setText(getRandomQuote());
        insertImageIfExists();

        List<History> historyList = getHistoryList();

        if (historyList.size() > 0) {
            totalCostTextView.setText(getTotalCost(historyList) + ",-\nKostnader");
            totalHighestBac.setText(getTotalHighestBac(historyList) + "\nHøyeste\nPromille");
            totalAvgTextView.setText(getTotalAverageHighestBac(historyList) + "\nGjennomsnitt\nhøyeste promille");

            lastMonthCostTextView.setText(getLastMonthCost(historyList) + ",-\nKostnader");
            lastMonthHighestBacTextView.setText(getLastMonthHighestBac(historyList) + "\nHøyeste\npromille");
            lastMonthAvgBacTextView.setText(getLastMonthAverageBac(historyList) + "\nGjennomsnitt\nhøyeste promille");
        }
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

    private void addHistory() {
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        HistoryDao historyDao = daoSession.getHistoryDao();

        History history = new History();
        history.setBeerCount(5);
        history.setDrinkCount(6);
        history.setWineCount(7);
        history.setShotCount(8);
        history.setStartDate(new Date());
        history.setHighestBAC(0.5);
        history.setPlannedUnitsCount(10);
        history.setSum(2000);

        historyDao.insert(history);
    }
}


