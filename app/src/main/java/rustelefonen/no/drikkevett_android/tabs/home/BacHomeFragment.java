package rustelefonen.no.drikkevett_android.tabs.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.SelectedPageEvent;
import rustelefonen.no.drikkevett_android.goalreached.GoalReachedActivity;
import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.util.DateUtil;
import rustelefonen.no.drikkevett_android.util.ImageUtil;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;

import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getLastMonthAverageBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getLastMonthCost;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getLastMonthHighestBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalAverageHighestBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalCost;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalHighestBac;

public class BacHomeFragment extends Fragment{

    //Fields
    public String photoFileName = "photo.jpg";

    //Widgets
    public TextView quoteTextView;
    public ImageView profileImage;
    public TextView totalCostTextView;
    public TextView totalHighestBac;
    public TextView totalAvgTextView;
    public TextView lastMonthCostTextView;
    public TextView lastMonthHighestBacTextView;
    public TextView lastMonthAvgBacTextView;
    public PieChart goalPieChart;
    public BarChart historyBarChart;
    public TextView graphHomeTextView;
    public TextView totalCountTextView;
    public TextView totalBacCountTextView;
    public TextView avgBacCountTextView;
    public TextView imageTextView;

    public CardView noDataBarChartCardView;
    public CardView historyCardView;
    public CardView noDataPieChart;
    public CardView goalCardView;

    public TextView barChartMonth;
    public TextView barChartYear;

    public FloatingActionButton addProfileImageFab;

    private User user;

    boolean hack = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bac_home_frag, container, false);
        EventBus.getDefault().register(this);
        if (!hack) {
            ((MainActivity)getActivity()).onPageSelected(0);
            hack = true;
        }

        initWidgets(view);
        fetchUser();
        fillWidgets();
        fillPieChart();
        stylePieChart();
        setHasOptionsMenu(true);
        fireGoalDateReachedView();
        return view;
    }

    @Subscribe
    public void getSelectedPage(SelectedPageEvent selectedPageEvent) {
        if (selectedPageEvent.page == 0) {
            ((MainActivity)getActivity()).getFloatingActionMenu().hideMenu(true);
            if (goalPieChart != null) goalPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUser();
        insertProfileImageIfExists();
        insertNicknameIfExists();
    }


    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void fetchUser() {
        user = ((MainActivity)getActivity()).getUser();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.simple_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                NavigationUtil.navigateToContactInformation(getContext());
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getRandomQuote() {
        String[] quotes = getResources().getStringArray(R.array.quotes);
        int randomIndex = (int) (Math.random() * quotes.length-1 + 1);
        return quotes[randomIndex];
    }

    private void initWidgets(View view) {
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
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
        totalCountTextView = (TextView) view.findViewById(R.id.total_count_text_view);
        totalBacCountTextView = (TextView) view.findViewById(R.id.total_bac_count_text_view);
        avgBacCountTextView = (TextView) view.findViewById(R.id.avg_bac_count_text_view);
        imageTextView = (TextView) view.findViewById(R.id.myImageViewText);
        noDataBarChartCardView = (CardView) view.findViewById(R.id.home_no_data_bar_chart);
        historyCardView = (CardView) view.findViewById(R.id.history_card_view);
        noDataPieChart = (CardView) view.findViewById(R.id.home_no_data_pie_chart);
        goalCardView = (CardView) view.findViewById(R.id.goal_card_view);
        barChartYear = (TextView) view.findViewById(R.id.home_bar_chart_year);
        barChartMonth = (TextView) view.findViewById(R.id.home_bar_chart_month);
        addProfileImageFab = (FloatingActionButton) view.findViewById(R.id.add_profile_image_fab);
    }

    private void fillWidgets() {
        quoteTextView.setText(getRandomQuote());


        List<History> historyList = getHistoryList();
        if (historyList.size() > 0) {
            noDataBarChartCardView.setVisibility(View.GONE);
            noDataPieChart.setVisibility(View.GONE);
            historyCardView.setVisibility(View.VISIBLE);
            goalCardView.setVisibility(View.VISIBLE);
            Date now = new Date();
            barChartMonth.setText(DateUtil.getMonthOfDate(now));
            barChartYear.setText(DateUtil.getYearOfDate(now));

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            totalCountTextView.setText(df.format(getTotalCost(historyList)) + ",-");
            totalBacCountTextView.setText(df.format(getTotalHighestBac(historyList)));
            avgBacCountTextView.setText(df.format(getTotalAverageHighestBac(historyList)));

            lastMonthCostTextView.setText(df.format(getLastMonthCost(historyList)) + ",-");
            lastMonthHighestBacTextView.setText(df.format(getLastMonthHighestBac(historyList)));
            lastMonthAvgBacTextView.setText(df.format(getLastMonthAverageBac(historyList)));

            if (user == null) return;
            BarChartController chartController = new BarChartController(historyBarChart, user, getHistoryList());
            chartController.setData();
            chartController.styleBarChart();
        } else {
            noDataBarChartCardView.setVisibility(View.VISIBLE);
            noDataPieChart.setVisibility(View.VISIBLE);
            historyCardView.setVisibility(View.GONE);
            goalCardView.setVisibility(View.GONE);
        }
    }

    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.MONTH);
    }

    private List<History> getHistoryList() {
        SuperDao superDao = new SuperDao(getContext());
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> historyList = historyDao.queryBuilder().list();
        superDao.close();

        List<History> historiesInThisMonth = new ArrayList<>();
        int currentMonth = getCurrentMonth();

        for (History history : historyList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(history.getStartDate());
            if (calendar.get(Calendar.MONTH) == currentMonth) historiesInThisMonth.add(history);
        }
        return historiesInThisMonth;
    }

    private void fillPieChart() {
        List<History> historyList = getHistoryList();

        double goal = ((MainActivity)getActivity()).getUser().getGoalBAC();
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

        dataset.setColors(new int[]{Color.parseColor("#DD7070"), Color.parseColor("#1AC149")});

        ArrayList<String> labels = new ArrayList<>();
        labels.add("");
        labels.add("");

        PieData data = new PieData(labels, dataset); // initialize Piedata
        goalPieChart.setData(data);
    }

    private void stylePieChart() {
        User user = ((MainActivity)getActivity()).getUser();
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

    private void insertProfileImageIfExists() {
        Uri takenPhotoUri = ImageUtil.getPhotoFileUri(photoFileName, getContext());
        if (takenPhotoUri == null) return;
        String path = takenPhotoUri.getPath();
        if (path == null) return;
        if (!new File(path).exists()) return;
        Bitmap takenImage = BitmapFactory.decodeFile(path);
        if (takenImage == null) return;
        if (profileImage == null) return;
        profileImage.setImageBitmap(takenImage);
    }

    private void insertNicknameIfExists() {
        if (user == null) return;
        String nickname = user.getNickname();
        if (nickname == null) return;
        imageTextView.setText(nickname);
    }

    /*
    * GOALDATE REACHED
    * */

    private void fireGoalDateReachedView(){
        Date currentDate = new Date();
        if(currentDate.after(user.getGoalDate())){
            Intent intent = new Intent(getContext(), GoalReachedActivity.class);
            intent.putExtra(GoalReachedActivity.ID, user);
            startActivity(intent);
        }
    }
}