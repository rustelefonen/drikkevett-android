package rustelefonen.no.drikkevett_android.tabs.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 12.07.2016.
 */

public class HistoryActivity extends AppCompatActivity {

    public static final String ID = "History";

    private History history;
    private List<GraphHistory> graphHistories;

    public LineChart lineChart;

    public TextView beerCountTextView;
    public TextView wineCountTextView;
    public TextView drinkCountTextView;
    public TextView shotCountTextView;

    public TextView historyCostTextView;
    public TextView historyHighestBacTextView;

    public TextView historyHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setHomeButtonEnabled(true);

        Object tmpHistory = getIntent().getSerializableExtra(ID);

        if (tmpHistory != null && tmpHistory instanceof History) {
            history = (History) tmpHistory;
            System.out.println(history.getHighestBAC());
        }
        /*
        SuperDao superDao = new SuperDao(this);
        GraphHistoryDao graphHistoryDao = superDao.getGraphHistoryDao();

        Date now = new Date();
        GraphHistory graphHistory = new GraphHistory();
        graphHistory.setTimestamp(now);
        graphHistory.setCurrentBAC(0.4);
        graphHistory.setHistoryId(history.getId());

        graphHistoryDao.insert(graphHistory);

        GraphHistory graphHistory2 = new GraphHistory();
        graphHistory.setTimestamp(getDateMinus15Minutes(now));
        graphHistory.setCurrentBAC(0.6);
        graphHistory.setHistoryId(history.getId());

        graphHistoryDao.insert(graphHistory2);

        graphHistories = graphHistoryDao.queryBuilder().where(GraphHistoryDao.Properties.HistoryId.eq(history.getId())).list();

        superDao.close();
        */

        lineChart = (LineChart) findViewById(R.id.history_line_chart_view);
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

        beerCountTextView = (TextView) findViewById(R.id.history_beer_count);
        wineCountTextView = (TextView) findViewById(R.id.history_wine_count);
        drinkCountTextView = (TextView) findViewById(R.id.history_drink_count);
        shotCountTextView = (TextView) findViewById(R.id.history_shot_count);

        beerCountTextView.setText(history.getBeerCount() + "");
        beerCountTextView.setText(history.getWineCount() + "");
        beerCountTextView.setText(history.getDrinkCount() + "");
        beerCountTextView.setText(history.getShotCount() + "");

        historyCostTextView = (TextView) findViewById(R.id.history_cost);
        historyHighestBacTextView = (TextView) findViewById(R.id.history_highest_bac);

        historyCostTextView.setText(history.getSum() + "");
        historyHighestBacTextView.setText(history.getHighestBAC() + "");

        historyHeader = (TextView) findViewById(R.id.history_header);
        historyHeader.setText(getGraphTitle(history.getStartDate()));
    }

    private String getNorwegianDayOfWeek(int day) {
        if (day == 1) return "Søndag";
        else if (day == 2) return "Mandag";
        else if (day == 3) return "Tirsdag";
        else if (day == 4) return "Onsdag";
        else if (day == 5) return "Torsdag";
        else if (day == 6) return "Fredag";
        else if (day == 7) return "Lørdag";
        return "";
    }

    private String getGraphTitle(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return getNorwegianDayOfWeek(weekDay) + " - " + dayOfMonth + "/" + month + "-" + year;
    }

    private Date getDateMinus15Minutes(Date currentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MINUTE, -15);
        return cal.getTime();
    }

    private String getHoursAndMinutes(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

    private LineData getLineData() {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int size = 0;
        if (graphHistories != null) size = graphHistories.size();
        for (int i = 0; i < size; i++) {
            entries.add(new Entry(graphHistories.get(i).getCurrentBAC().floatValue(), i));
            labels.add(getHoursAndMinutes(graphHistories.get(i).getTimestamp()));
        }

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");

        dataset.setDrawFilled(true);
        dataset.setDrawHighlightIndicators(false);
        dataset.setDrawCircleHole(true);
        dataset.setDrawValues(false);
        dataset.setCircleRadius(5f);
        //dataset.setCircleHoleRadius(3f);
        //dataset.setCircleColorHole(Color.RED);
        dataset.setCircleColor(ContextCompat.getColor(this, R.color.textColor));
        dataset.setFillColor(ContextCompat.getColor(this, R.color.historyLineChartGreen));
        dataset.setColor(ContextCompat.getColor(this, R.color.historyLineChartGreen));
        dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        return new LineData(labels, dataset);
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
}
