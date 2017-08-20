package rustelefonen.no.drikkevett_android.tabs.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.Unit;
import rustelefonen.no.drikkevett_android.tabs.home.HistoryUtility;
import rustelefonen.no.drikkevett_android.util.BacUtility;
import rustelefonen.no.drikkevett_android.util.DateUtil;

/**
 * Created by simenfonnes on 12.07.2016.
 */

public class HistoryActivity extends AppCompatActivity {

    public static final String ID = "History";

    private NewHistory history;
    List<Unit> units;

    public LineChart lineChart;

    public TextView beerCountTextView;
    public TextView wineCountTextView;
    public TextView drinkCountTextView;
    public TextView shotCountTextView;

    public TextView historyCostTextView;
    public TextView historyHighestBacTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        insertHistoryIfExists();
        units = HistoryUtility.getHistoryUnits(history, this);
        initWidgets();
        fillWidgets();
        insertToolbar();
    }

    private void initWidgets() {
        lineChart = (LineChart) findViewById(R.id.history_line_chart_view);
        beerCountTextView = (TextView) findViewById(R.id.history_beer_count);
        wineCountTextView = (TextView) findViewById(R.id.history_wine_count);
        drinkCountTextView = (TextView) findViewById(R.id.history_drink_count);
        shotCountTextView = (TextView) findViewById(R.id.history_shot_count);
        historyCostTextView = (TextView) findViewById(R.id.history_cost);
        historyHighestBacTextView = (TextView) findViewById(R.id.history_highest_bac);
    }

    private void fillWidgets() {
        setupLineChart();
        beerCountTextView.setText(history.getBeerPlannedUnitCount() + "");
        wineCountTextView.setText(history.getWinePlannedUnitCount() + "");
        drinkCountTextView.setText(history.getDrinkPlannedUnitCount() + "");
        shotCountTextView.setText(history.getShotPlannedUnitCount() + "");
        historyCostTextView.setText(HistoryUtility.getTotalCost(history, units) + "");
        historyHighestBacTextView.setText(new DecimalFormat("##.00").format(HistoryUtility.getHighestBac(history, units)));
    }

    private void setupLineChart() {
        lineChart.setData(getLineData());
        lineChart.setDescription("");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        int textColor = ContextCompat.getColor(this, R.color.textColor);
        lineChart.getXAxis().setTextColor(textColor);
        lineChart.getAxisLeft().setTextColor(textColor);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setVisibleXRange(3, 3);
        lineChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void insertHistoryIfExists() {
        Object tmpHistory = getIntent().getSerializableExtra(ID);
        if (tmpHistory != null && tmpHistory instanceof NewHistory) history = (NewHistory) tmpHistory;
    }

    private LineData getLineData() {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        double beerUnits = 0.0;
        double wineUnits = 0.0;
        double drinkUnits = 0.0;
        double shotUnits = 0.0;

        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);

            if (unit.getUnitType().equals("Beer")) {beerUnits += 1.0;}
            else if (unit.getUnitType().equals("Wine")) {wineUnits += 1.0;}
            else if (unit.getUnitType().equals("Drink")) {drinkUnits += 1.0;}
            else if (unit.getUnitType().equals("Shot")) {shotUnits += 1.0;}

            long seconds = (unit.getTimestamp().getTime() - history.getBeginDate().getTime()) / 1000;

            double hours = seconds / 3600000.0;

            double bac = BacUtility.calculateBac(beerUnits, wineUnits, drinkUnits, shotUnits, history.getBeerGrams(), history.getWineGrams(), history.getDrinkGrams(), history.getShotGrams(), hours, history.getGender(), history.getWeight());
            entries.add(new Entry((float) bac, i));
            labels.add(DateUtil.getHoursAndMinutes(unit.getTimestamp()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "# of Calls");

        dataSet.setDrawFilled(true);
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setDrawCircleHole(true);
        dataSet.setDrawValues(false);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.textColor));
        dataSet.setFillColor(ContextCompat.getColor(this, R.color.historyLineChartGreen));
        dataSet.setColor(ContextCompat.getColor(this, R.color.historyLineChartGreen));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        return new LineData(labels, dataSet);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle(DateUtil.getTitle(history.getBeginDate()));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasChanged();
            }
        });
    }

    private void hasChanged() {
        super.onBackPressed();
    }
}