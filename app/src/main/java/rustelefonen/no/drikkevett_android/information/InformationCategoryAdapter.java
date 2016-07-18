package rustelefonen.no.drikkevett_android.information;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.Information;
import rustelefonen.no.drikkevett_android.db.InformationCategory;

/**
 * Created by simenfonnes on 18.07.2016.
 */

public class InformationCategoryAdapter extends RecyclerView.Adapter<InformationCategoryAdapter.ViewHolder> {

    private List<InformationCategory> informationCategoryList;

    public InformationCategoryAdapter(List<InformationCategory> informationCategoryList) {
        this.informationCategoryList = informationCategoryList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if ((viewType % 2) == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.long_row_layout, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.short_row_layout, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.getCardViewLong().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("langt kort");
                }
            });
        } else {
            holder.getCardViewShortOne().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("kort kort 1");
                }
            });
            holder.getCardViewShortTwo().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("kort kort 2");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return informationCategoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardViewLong;
        private final CardView cardViewShortOne;
        private final CardView cardViewShortTwo;


        public ViewHolder(View v) {
            super(v);
            cardViewLong = (CardView) v.findViewById(R.id.long_row_card);
            cardViewShortOne = (CardView) v.findViewById(R.id.short_row_card_one);
            cardViewShortTwo = (CardView) v.findViewById(R.id.short_row_card_two);
        }

        public CardView getCardViewLong() {
            return cardViewLong;
        }

        public CardView getCardViewShortOne() {
            return cardViewShortOne;
        }

        public CardView getCardViewShortTwo() {
            return cardViewShortTwo;
        }
    }
}