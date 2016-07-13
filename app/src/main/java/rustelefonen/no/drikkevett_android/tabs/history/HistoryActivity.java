package rustelefonen.no.drikkevett_android.tabs.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 12.07.2016.
 */

public class HistoryActivity extends AppCompatActivity {

    public static final String ID = "History";

    private History history;
    private List<GraphHistory> graphHistories;

    public LineChart lineChart;

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

        //You can use chart.setVisibleXRange(10) to show only 10 values maximum.


        lineChart = (LineChart) findViewById(R.id.history_line_chart_view);
        lineChart.setData(getLineData());
        lineChart.setDescription("");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
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

        for (int i = 0; i < graphHistories.size(); i++) {
            entries.add(new Entry(graphHistories.get(i).getCurrentBAC().floatValue(), i));
            labels.add(getHoursAndMinutes(graphHistories.get(i).getTimestamp()));
        }

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");

        dataset.setDrawFilled(true);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
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
