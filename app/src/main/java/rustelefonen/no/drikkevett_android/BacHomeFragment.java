package rustelefonen.no.drikkevett_android;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    public BarChart historyBarChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bac_home_frag, container, false);
        insertUser();
        addHistory();
        initWidgets(view);
        fillWidgets();
        fillPieChart();
        stylePieChart();

        BarData data = new BarData(getXAxisValues(), getDataSet());
        historyBarChart.setData(data);
        historyBarChart.setDescription("My Chart");
        historyBarChart.animateXY(2000, 2000);
        historyBarChart.invalidate();



        return view;
    }

    private ArrayList<IBarDataSet> getDataSet() {
        ArrayList<IBarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
        valueSet1.add(v1e6);

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(20.000f, 4); // May
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(80.000f, 5); // Jun
        valueSet2.add(v2e6);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Brand 2");
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        return xAxis;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!this.isVisible()) return;
        if (!isVisibleToUser) return;
        goalPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
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
        User user = getUser();

        if (user == null) return "Tom bruker";

        if (user.getNickname() == null || user.getNickname().isEmpty()) return "Tom bruker";

        return user.getNickname();

    }

    private User getUser() {
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        UserDao userDao = daoSession.getUserDao();

        List<User> userList = userDao.queryBuilder().list();

        if (userList.size() <= 0) {
            return null;
        } else {
            return userList.get(0);
        }
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
        historyBarChart = (BarChart) view.findViewById(R.id.history_bar_chart);
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
        newUser.setGoalBAC(0.4);

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
        history.setHighestBAC(0.4);
        history.setPlannedUnitsCount(10);
        history.setSum(2000);

        historyDao.insert(history);
    }

    private void fillPieChart() {
        List<History> historyList = getHistoryList();

        User user = getUser();

        double goal = user.getGoalBAC();
        double overGoal = 0.0;
        double underGoal = 0.0;

        for (History history : historyList) {
            if (history.getHighestBAC() > goal) {       //tok større enn, uenig med iOS-versjonen
                overGoal++;
            } else {
                underGoal++;
            }

        }


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry((float) overGoal, 0));
        entries.add(new Entry((float) underGoal, 1));

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");

        dataset.setDrawValues(false);

        dataset.setColors(new int[]{Color.parseColor("#C11A1A"), Color.parseColor("#1AC149")});

        ArrayList<String> labels = new ArrayList<>();
        labels.add("");
        labels.add("");

        PieData data = new PieData(labels, dataset); // initialize Piedata
        goalPieChart.setData(data);
    }

    private void stylePieChart() {
        //style

        User user = getUser();
        if (user != null) {
            goalPieChart.setCenterText(user.getGoalBAC() +"");
        }

        //goalPieChart.setCenterText("0.5");
        goalPieChart.setDrawHoleEnabled(true);
        goalPieChart.setHoleRadius(80f);
        goalPieChart.setHoleColor(Color.parseColor("#141414"));
        goalPieChart.setCenterTextRadiusPercent(100f);
        goalPieChart.setTransparentCircleRadius(85f);
        goalPieChart.setDescription("");
        goalPieChart.setBackgroundColor(Color.parseColor("#141414"));
        goalPieChart.setAnimation(new Animation() {
        });
        //goalPieChart.setTransparentCircleColor(Color.tr);
        goalPieChart.setDrawSliceText(false);
        goalPieChart.getLegend().setEnabled(false);
        goalPieChart.setRotationEnabled(false);

        goalPieChart.setDrawSliceText(false);
        //goalPieChart.setDrawValues(false);


        goalPieChart.setCenterTextSize(27.0f);
        goalPieChart.setCenterTextColor(Color.parseColor("#FFFFFF"));
        goalPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        goalPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
}


