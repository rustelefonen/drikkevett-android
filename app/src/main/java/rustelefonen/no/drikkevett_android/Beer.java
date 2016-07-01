package rustelefonen.no.drikkevett_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Beer {
    public static View getView(Context context, ViewGroup collection) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.bac_calc_frag, collection, false);
    }
}