package rustelefonen.no.drikkevett_android.tabs.home;

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
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.util.DateUtil;

/**
 * Created by simenfonnes on 07.07.2016.
 */

public class BarChartController {

    private static final int WHITE_COLOR = Color.parseColor("#FFFFFF");
    private static final String NO_DATA_TEXT = "ingen graf.";

    private BarChart barChart;
    private User user;
    private List<History> historyList;

    public BarChartController(BarChart barChart, User user, List<History> historyList) {
        this.barChart = barChart;
        this.user = user;
        this.historyList = historyList;
    }

    public void styleBarChart() {


        barChart.setNoDataText(NO_DATA_TEXT);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisRight().setDrawTopYLabelEntry(false);
        barChart.getAxisLeft().setTextColor(WHITE_COLOR);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setTextColor(WHITE_COLOR);
        barChart.getLegend().setEnabled(false);
        barChart.setDescription("");
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getAxisRight().removeAllLimitLines();
        barChart.animateXY(1000, 1000);
        LimitLine limit = new LimitLine(user.getGoalBAC().floatValue(), "");
        limit.setLineColor(WHITE_COLOR);
        limit.enableDashedLine(8.5f, 8.5f, 6.5f);
        barChart.getAxisRight().addLimitLine(limit);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getXAxis().setDrawAxisLine(false);





        //barChart.se


        //barChart.getBa
        //barChart.getXAxis().setSpaceBetweenLabels(40);

        //barChart.setViewPortOffsets(60f, 0f, 0f, 0f);

        //barChart.getXAxis().setLabelRotationAngle(45f);

        //barChart.getXAxis().label


        //barChart.setX

        //barChart.resetViewPortOffsets();
        //barChart.setViewPortOffsets(0f, 0f, 0f, 0f);
        //barChart.setVisibleXRange(6, 1);



        //barChart.getXAxis().setAvoidFirstLastClipping(true);*/




    }

    private ArrayList<IBarDataSet> getDataSet() {
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();

        List<Integer> colors = new ArrayList<>();

        for (int i = 0; i < historyList.size(); i++) {
            History history = historyList.get(i);
            if (history.getHighestBAC() > user.getGoalBAC()) {
                valueSet1.add(new BarEntry(history.getHighestBAC().floatValue(), i));
                colors.add(Color.rgb(221, 112, 112));
            } else {
                valueSet1.add(new BarEntry(history.getHighestBAC().floatValue(), i));
                colors.add(Color.rgb(0, 155, 0));
            }
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");

        barDataSet1.setColors(colors);

        barDataSet1.setBarSpacePercent(40f);

        //barDataSet1.setColor(Color.rgb(0, 155, 0));
        barDataSet1.setDrawValues(false);
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Brand 2");
        barDataSet2.setColor(Color.rgb(221, 112, 112));
        //barDataSet2.setBarSpacePercent(0f);
        barDataSet2.setDrawValues(false);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        //dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        for (History history : historyList) {
            xAxis.add(DateUtil.getDayOfMonth(history.getStartDate()) /*+ ". " + DateUtil.getMonthShortName(history.getStartDate())*/);
        }
        return xAxis;
    }

    public void setData() {
        BarData data = new BarData(getXAxisValues(), getDataSet());
        barChart.setData(data);
    }
}