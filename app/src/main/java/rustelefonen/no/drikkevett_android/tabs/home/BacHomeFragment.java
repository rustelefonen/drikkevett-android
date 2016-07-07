package rustelefonen.no.drikkevett_android.tabs.home;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;

import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getLastMonthAverageBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getLastMonthCost;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getLastMonthHighestBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalAverageHighestBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalCost;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalHighestBac;
import static rustelefonen.no.drikkevett_android.util.DateUtil.getMonthName;

public class BacHomeFragment extends Fragment{

    //Fields
    public final String APP_TAG = "MyCustomApp";
    public String photoFileName = "photo.jpg";
    private User user;

    //Widgets
    public TextView quoteTextView;
    public ImageView ivPreview;
    public TextView totalCostTextView;
    public TextView totalHighestBac;
    public TextView totalAvgTextView;
    public TextView lastMonthCostTextView;
    public TextView lastMonthHighestBacTextView;
    public TextView lastMonthAvgBacTextView;
    public PieChart goalPieChart;
    public BarChart historyBarChart;
    public TextView graphHomeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bac_home_frag, container, false);
        insertUser();
        user = getUser();
        addHistory();
        initWidgets(view);
        fillWidgets();
        fillPieChart();
        stylePieChart();

        return view;
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
        String[] quotes = getResources().getStringArray(R.array.quotes);
        int randomIndex = (int) (Math.random() * quotes.length-1 + 1);
        return quotes[randomIndex];
    }

    private String getRandomWelcomeMessage() {
        String[] welcomeMessages = getResources().getStringArray(R.array.welcomeMessages);
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
        if (user == null) return "Tom bruker";

        if (user.getNickname() == null || user.getNickname().isEmpty()) return "Tom bruker";

        return user.getNickname();

    }

    private User getUser() {
        SuperDao superDao = new SuperDao(getContext());
        UserDao userDao = superDao.getUserDao();

        List<User> userList = userDao.queryBuilder().list();
        superDao.close();
        if (userList.size() <= 0) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    private void initWidgets(View view) {
        ivPreview = (ImageView) view.findViewById(R.id.profile_image);
        quoteTextView = (TextView) view.findViewById(R.id.quote_text_view);
        totalCostTextView = (TextView) view.findViewById(R.id.total_cost_text_view);
        totalHighestBac = (TextView) view.findViewById(R.id.total_highest_bac);
        totalAvgTextView = (TextView) view.findViewById(R.id.total_avg_text_view);
        lastMonthCostTextView = (TextView) view.findViewById(R.id.last_month_cost_text_view);
        lastMonthHighestBacTextView = (TextView) view.findViewById(R.id.last_month_highest_bac_text_view);
        lastMonthAvgBacTextView = (TextView) view.findViewById(R.id.last_month_avg_bac_text_view);
        graphHomeTextView = (TextView) view.findViewById(R.id.graph_home_text_view);
        goalPieChart = (PieChart) view.findViewById(R.id.goal_pie_chart);
        historyBarChart = (BarChart) view.findViewById(R.id.history_bar_chart);
    }

    private void fillWidgets() {
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

        BarChartController chartController = new BarChartController(historyBarChart, user, getHistoryList());
        chartController.setData();
        chartController.styleBarChart();
    }

    private void insertUser() {
        SuperDao superDao = new SuperDao(getContext());
        UserDao userDao = superDao.getUserDao();

        User newUser = new User();
        newUser.setAge(14);
        newUser.setNickname("fonsim");
        newUser.setGoalBAC(0.4);

        userDao.insert(newUser);
        superDao.close();
    }

    private List<History> getHistoryList() {
        SuperDao superDao = new SuperDao(getContext());
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> historyList = historyDao.queryBuilder().list();
        superDao.close();
        return historyList;
    }

    private void addHistory() {
        SuperDao superDao = new SuperDao(getContext());
        HistoryDao historyDao = superDao.getHistoryDao();

        History history = new History();
        history.setBeerCount(5);
        history.setDrinkCount(6);
        history.setWineCount(7);
        history.setShotCount(8);
        history.setStartDate(new Date());
        history.setHighestBAC(0.6);
        history.setPlannedUnitsCount(10);
        history.setSum(2000);

        historyDao.insert(history);
        superDao.close();
    }

    private void fillPieChart() {
        List<History> historyList = getHistoryList();

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
        if (user != null) goalPieChart.setCenterText(user.getGoalBAC() +"");
        goalPieChart.setDrawHoleEnabled(true);
        goalPieChart.setHoleRadius(80f);
        goalPieChart.setHoleColor(Color.TRANSPARENT);
        goalPieChart.setCenterTextRadiusPercent(100f);
        goalPieChart.setTransparentCircleRadius(85f);
        goalPieChart.setDescription("");
        goalPieChart.setDrawSliceText(false);
        goalPieChart.getLegend().setEnabled(false);
        goalPieChart.setRotationEnabled(false);
        goalPieChart.setDrawSliceText(false);
        goalPieChart.setCenterTextSize(27.0f);
        goalPieChart.setCenterTextColor(Color.parseColor("#FFFFFF"));
        goalPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        goalPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (e.getXIndex() == 0) {
                    graphHomeTextView.setText("Målet ditt er her rødt fordi du har gjort det dårlig!!");
                } else if (e.getXIndex() == 1) {
                    graphHomeTextView.setText("Målet ditt er her grønt fordi du har gjort det bra!");
                }
            }

            @Override
            public void onNothingSelected() {
                graphHomeTextView.setText("Denne grafikken viser hvordan det står til med målet ditt. Ønsker du å vite mer klikk på fargene");
            }
        });
    }
}