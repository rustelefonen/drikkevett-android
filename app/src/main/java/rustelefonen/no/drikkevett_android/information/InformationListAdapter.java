package rustelefonen.no.drikkevett_android.information;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.Information;
import rustelefonen.no.drikkevett_android.tabs.history.HistoryActivity;
import rustelefonen.no.drikkevett_android.tabs.history.HistoryAdapter;

/**
 * Created by simenfonnes on 18.07.2016.
 */

public class InformationListAdapter extends RecyclerView.Adapter<InformationListAdapter.ViewHolder> {

    private List<Information> informationList;

    public InformationListAdapter(List<Information> informationList) {
        this.informationList = informationList;
    }

    @Override
    public InformationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.information_list_row_item, parent, false);
        return new ViewHolder(v, v.getContext());
    }

    @Override
    public void onBindViewHolder(InformationListAdapter.ViewHolder holder, final int position) {
        Information currentInformation = informationList.get(position);
        holder.getNameTextView().setText(currentInformation.getName());

        byte[] image = currentInformation.getImage();
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);
            if (bitmap != null) holder.getImageView().setImageBitmap(bitmap);
            else System.out.println("bitmap er null");
        }


        final View view = holder.getView();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = view.getContext();
                Intent intent = new Intent(context, InformationActivity.class);
                intent.putExtra(InformationActivity.ID, informationList.get(position));

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return informationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private View view;
        private final ImageView imageView;

        private Context context;

        public ViewHolder(View v, Context context) {
            super(v);
            this.context = context;
            nameTextView = (TextView) v.findViewById(R.id.information_list_category_name);
            view = v;
            imageView = (ImageView) v.findViewById(R.id.information_list_image);
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public View getView() {
            return view;
        }

        public Context getContext() {
            return context;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}