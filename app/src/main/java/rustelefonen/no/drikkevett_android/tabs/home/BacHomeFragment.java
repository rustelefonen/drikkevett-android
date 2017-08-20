package rustelefonen.no.drikkevett_android.tabs.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.Unit;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.util.DateUtil;
import rustelefonen.no.drikkevett_android.util.ImageUtil;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;

import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalAverageHighestBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalCost;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalHighestBac;

public class BacHomeFragment extends Fragment{

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bac_home_frag, container, false);
        initWidgets(view);
        fillWidgets();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        insertProfileImageIfExists();
        insertNicknameIfExists(((MainActivity)getActivity()).getUser());
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
    }

    private void fillWidgets() {
        quoteTextView.setText(getRandomQuote());

        List<NewHistory> historiesInCurrentMonth = HistoryUtility.getHistoriesInCurrentMonth(getContext());
        List<NewHistory> allCompletedHistories = HistoryUtility.getAllCompletedHistories(getContext());

        if (historiesInCurrentMonth.size() > 0) {
            setViewVisibility(false);

            Date now = new Date();
            barChartMonth.setText(DateUtil.getMonthOfDate(now));
            barChartYear.setText(DateUtil.getYearOfDate(now));

            setTotalCard(allCompletedHistories, getContext());
            setAvgCard(historiesInCurrentMonth, getContext());

            User user = ((MainActivity)getActivity()).getUser();

            setBarChartData(historiesInCurrentMonth, user);
            styleBarChart(user);

            fillPieChart(historiesInCurrentMonth, user);
            stylePieChart(user);
        } else setViewVisibility(true);
    }

    private void setTotalCard(List<NewHistory> entireHistoryList, Context context) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        String formattedTotalCost = df.format(getTotalCost(entireHistoryList, getContext())) + ",-";

        totalCountTextView.setText(formattedTotalCost);
        totalBacCountTextView.setText(df.format(getTotalHighestBac(entireHistoryList, context)));
        avgBacCountTextView.setText(df.format(getTotalAverageHighestBac(entireHistoryList, context)));
    }

    private void setAvgCard(List<NewHistory> historiesInCurrentMonth, Context context) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        String formattedTotalCost = df.format(getTotalCost(historiesInCurrentMonth, context)) + ",-";

        lastMonthCostTextView.setText(formattedTotalCost);
        lastMonthHighestBacTextView.setText(df.format(getTotalHighestBac(historiesInCurrentMonth, context)));
        lastMonthAvgBacTextView.setText(df.format(getTotalAverageHighestBac(historiesInCurrentMonth, context)));
    }

    private void setViewVisibility(boolean historyIsEmpty) {
        if (historyIsEmpty) {
            noDataBarChartCardView.setVisibility(View.VISIBLE);
            noDataPieChart.setVisibility(View.VISIBLE);
            historyCardView.setVisibility(View.GONE);
            goalCardView.setVisibility(View.GONE);
        }
        else {
            noDataBarChartCardView.setVisibility(View.GONE);
            noDataPieChart.setVisibility(View.GONE);
            historyCardView.setVisibility(View.VISIBLE);
            goalCardView.setVisibility(View.VISIBLE);
        }
    }

    private void setBarChartData(List<NewHistory> historiesInCurrentMonth, User user) {
        BarData data = new BarData(getXAxisValues(historiesInCurrentMonth), getDataSet(historiesInCurrentMonth, user));
        historyBarChart.setData(data);
    }

    public void styleBarChart(User user) {
        int whiteColor = Color.parseColor("#FFFFFF");
        historyBarChart.setNoDataText("ingen graf.");
        historyBarChart.getAxisLeft().setDrawGridLines(false);
        historyBarChart.getAxisRight().setDrawGridLines(false);
        historyBarChart.getXAxis().setDrawGridLines(false);
        historyBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        historyBarChart.getAxisRight().setDrawTopYLabelEntry(false);
        historyBarChart.getAxisLeft().setTextColor(whiteColor);
        historyBarChart.getAxisRight().setEnabled(false);
        historyBarChart.getXAxis().setTextColor(whiteColor);
        historyBarChart.getLegend().setEnabled(false);
        historyBarChart.setDescription("");
        historyBarChart.setPinchZoom(false);
        historyBarChart.setDoubleTapToZoomEnabled(false);
        historyBarChart.getAxisRight().removeAllLimitLines();
        historyBarChart.animateXY(1000, 1000);
        LimitLine limit = new LimitLine(user.getGoalBAC().floatValue(), "");
        limit.setLineColor(whiteColor);
        limit.enableDashedLine(8.5f, 8.5f, 6.5f);
        historyBarChart.getAxisRight().addLimitLine(limit);
        historyBarChart.getAxisLeft().setDrawAxisLine(false);
        historyBarChart.getXAxis().setDrawAxisLine(false);
        historyBarChart.setVisibleXRange(8,8);
        historyBarChart.setHighlightPerTapEnabled(false);
        historyBarChart.getAxisLeft().setAxisMinValue(0.0f);
    }

    private ArrayList<IBarDataSet> getDataSet(List<NewHistory> historiesInCurrentMonth, User user) {
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (int i = 0; i < historiesInCurrentMonth.size(); i++) {
            NewHistory history = historiesInCurrentMonth.get(i);
            List<Unit> units = HistoryUtility.getHistoryUnits(history, getContext());

            if (HistoryUtility.getHighestBac(history, units) > user.getGoalBAC()) {
                valueSet1.add(new BarEntry((float) HistoryUtility.getHighestBac(history, units), i));
                colors.add(Color.parseColor("#DD7070"));
            } else {
                valueSet1.add(new BarEntry((float) HistoryUtility.getHighestBac(history, units), i));
                colors.add(Color.parseColor("#1AC149"));
            }
        }
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
        barDataSet1.setColors(colors);
        barDataSet1.setBarSpacePercent(40f);
        barDataSet1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues(List<NewHistory> historiesInCurrentMonth) {
        ArrayList<String> xAxis = new ArrayList<>();
        for (NewHistory history : historiesInCurrentMonth) {
            xAxis.add(DateUtil.getDayOfMonth(history.getBeginDate()));
        }
        return xAxis;
    }

    private void fillPieChart(List<NewHistory> historiesInCurrentMonth, User user) {
        double goal = user.getGoalBAC();
        double overGoal = 0.0;
        double underGoal = 0.0;

        for (NewHistory history : historiesInCurrentMonth) {
            List<Unit> units = HistoryUtility.getHistoryUnits(history, getContext());
            if (HistoryUtility.getHighestBac(history, units) > goal) overGoal++;
            else underGoal++;
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

    private void stylePieChart(User user) {
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

    private void insertProfileImageIfExists() {
        Uri takenPhotoUri = ImageUtil.getPhotoFileUri("photo.jpg", getContext());
        if (takenPhotoUri == null) return;
        if (!new File(takenPhotoUri.getPath()).exists()) return;
        Bitmap takenImage = ImageUtil.decodeSampledBitmapFromResource(getContext(), takenPhotoUri, getWidth(), 175);
        if (takenImage == null) return;
        if (profileImage == null) return;
        profileImage.setImageBitmap(takenImage);
    }

    private void insertNicknameIfExists(User user) {
        if (user == null) return;
        String nickname = user.getNickname();
        if (nickname == null) return;
        imageTextView.setText(nickname);
    }

    private int getWidth() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}