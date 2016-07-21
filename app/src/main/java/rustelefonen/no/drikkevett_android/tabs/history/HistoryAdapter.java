package rustelefonen.no.drikkevett_android.tabs.history;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.util.DateUtil;

/**
 * Created by simenfonnes on 12.07.2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> historyList;
    private double goalBac;

    public HistoryAdapter(List<History> historyList, double goalBac) {
        this.historyList = historyList;
        this.goalBac = goalBac;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_row, viewGroup, false);
        return new ViewHolder(v, v.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        History history = historyList.get(position);
        viewHolder.getDayTextView().setText(DateUtil.getDayOfMonth(history.getStartDate()) + "");
        viewHolder.getMonthTextView().setText(DateUtil.getMonthShortName(history.getStartDate()) + "");

        String perMille = "\u2030";
        viewHolder.getHighestBacTextView().setText(history.getHighestBAC() + perMille);
        String cashIndicator = ",-";
        viewHolder.getTotalCostTextView().setText(history.getSum() + cashIndicator);

        final View view = viewHolder.getView();

        if (history.getHighestBAC() > goalBac) {
            int red = ContextCompat.getColor(view.getContext(), R.color.historyRed);
            ((GradientDrawable)viewHolder.getShapeView().getBackground())
                    .setColor(red);
            viewHolder.getHighestBacTextView().setTextColor(red);
            viewHolder.getTotalCostTextView().setTextColor(red);
        } else {
            int green = ContextCompat.getColor(view.getContext(), R.color.historyLineChartGreen);
            ((GradientDrawable)viewHolder.getShapeView().getBackground())
                    .setColor(green);
            viewHolder.getHighestBacTextView().setTextColor(green);
            viewHolder.getTotalCostTextView().setTextColor(green);
        }





        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = view.getContext();
                Intent intent = new Intent(context, HistoryActivity.class);
                intent.putExtra(HistoryActivity.ID, historyList.get(position));

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView dayTextView;
        private final TextView monthTextView;
        private final TextView highestBacTextView;
        private final TextView totalCostTextView;

        private final View shapeView;

        private View view;

        private Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            this.context = context;
            view = v;
            dayTextView = (TextView) v.findViewById(R.id.history_row_day);
            monthTextView = (TextView) v.findViewById(R.id.history_row_month);
            highestBacTextView = (TextView) v.findViewById(R.id.history_row_highest_bac);
            totalCostTextView = (TextView) v.findViewById(R.id.history_row_total_cost);

            shapeView = v.findViewById(R.id.history_row_circle);
        }

        public View getView() {
            return view;
        }

        public Context getContext() {
            return context;
        }

        public TextView getDayTextView() {
            return dayTextView;
        }

        public TextView getMonthTextView() {
            return monthTextView;
        }

        public TextView getHighestBacTextView() {
            return highestBacTextView;
        }

        public TextView getTotalCostTextView() {
            return totalCostTextView;
        }

        public View getShapeView() {
            return shapeView;
        }
    }
}
