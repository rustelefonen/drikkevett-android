package rustelefonen.no.drikkevett_android.tabs.home;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.Unit;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.util.DateUtil;

/**
 * Created by simenfonnes on 07.07.2016.
 */

public class BarChartController {

    private BarChart barChart;
    private User user;
    private List<NewHistory> historyList;
    private Context context;

    public BarChartController(BarChart barChart, User user, List<NewHistory> historyList, Context context) {
        this.barChart = barChart;
        this.user = user;
        this.historyList = historyList;
        this.context = context;
    }


}