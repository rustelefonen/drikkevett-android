package rustelefonen.no.drikkevett_android.information;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.InformationCategory;

/**
 * Created by simenfonnes on 18.07.2016.
 */

public class InformationCategoryAdapter extends RecyclerView.Adapter<InformationCategoryAdapter.ViewHolder> {

    private List<InformationCategory> informationCategoryList;
    private Context context;

    public InformationCategoryAdapter(List<InformationCategory> informationCategoryList, Context context) {
        this.informationCategoryList = informationCategoryList;
        this.context = context;
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
            setupCurrentCard(holder.getCardViewLong(), holder.getImageLong(), holder.getTextViewLong(), informationCategoryList.get(position + (position/2)));
        } else {
            setupCurrentCard(holder.getCardViewShortOne(), holder.getImageShortOne(), holder.getTextViewShortOne(), informationCategoryList.get(position + (position/2)));
            setupCurrentCard(holder.getCardViewShortTwo(), holder.getImageShortTwo(), holder.getTextViewShortTwo(), informationCategoryList.get(position + (position/2) + 1));
        }
    }

    private void setupCurrentCard(CardView cardView, ImageView imageView, TextView textView, final InformationCategory informationCategory) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InformationListActivity.class);
                intent.putExtra(InformationListActivity.ID, informationCategory);
                context.startActivity(intent);
            }
        });
        String categoryTitle = informationCategory.getName();
        if (categoryTitle != null) textView.setText(categoryTitle);
        byte[] image = informationCategory.getImage();
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);
            if (bitmap != null) imageView.setImageBitmap(bitmap);
            else System.out.println("bitmap er null");
        }
    }

    @Override
    public int getItemCount() {
        int counter = 0;
        for (int i = 0; i <= informationCategoryList.size(); i++) {
            if (i % 3 != 0) {
                counter++;
            }
        }
        return counter;

        //return informationCategoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardViewLong;
        private final CardView cardViewShortOne;
        private final CardView cardViewShortTwo;

        private final ImageView imageLong;
        private final ImageView imageShortOne;
        private final ImageView imageShortTwo;

        private final TextView textViewLong;
        private final TextView textViewShortOne;
        private final TextView textViewShortTwo;


        public ViewHolder(View v) {
            super(v);
            cardViewLong = (CardView) v.findViewById(R.id.long_row_card);
            cardViewShortOne = (CardView) v.findViewById(R.id.short_row_card_one);
            cardViewShortTwo = (CardView) v.findViewById(R.id.short_row_card_two);

            imageLong = (ImageView) v.findViewById(R.id.long_row_image);
            imageShortOne = (ImageView) v.findViewById(R.id.short_row_image_one);
            imageShortTwo = (ImageView) v.findViewById(R.id.short_row_image_two);

            textViewLong = (TextView) v.findViewById(R.id.long_row_text);
            textViewShortOne = (TextView) v.findViewById(R.id.short_row_text_one);
            textViewShortTwo = (TextView) v.findViewById(R.id.short_row_text_two);
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

        public ImageView getImageLong() {
            return imageLong;
        }

        public ImageView getImageShortOne() {
            return imageShortOne;
        }

        public ImageView getImageShortTwo() {
            return imageShortTwo;
        }

        public TextView getTextViewLong() {
            return textViewLong;
        }

        public TextView getTextViewShortOne() {
            return textViewShortOne;
        }

        public TextView getTextViewShortTwo() {
            return textViewShortTwo;
        }
    }
}