package rustelefonen.no.drikkevett_android.tabs.history;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.Settings;
import rustelefonen.no.drikkevett_android.db.History;

/**
 * Created by simenfonnes on 12.07.2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private static final String TAG = "HistoryAdapter";

    private List<History> historyList;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView totalCostTextView;
        private View view;

        private Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            this.context = context;
            textView = (TextView) v.findViewById(R.id.textView);
            totalCostTextView = (TextView) v.findViewById(R.id.history_total_cost_text_view);
            view = v;
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getTotalCostTextView() {
            return totalCostTextView;
        }

        public View getView() {
            return view;
        }

        public Context getContext() {
            return context;
        }
    }

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(v, v.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        viewHolder.getTextView().setText(historyList.get(position).getHighestBAC() + "");
        viewHolder.getTextView().setText("22000,-");

        final View view = viewHolder.getView();

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
}
