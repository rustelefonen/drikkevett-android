package rustelefonen.no.drikkevett_android.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by simenfonnes on 20.07.2016.
 */

public class NotificationUtil {

    private static final String PREFERENCES = "DrikkevettPrefs";
    private static final String SELECTED_KEY = "selectedKey";

    public static void setSelected(Context context, boolean selected) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(SELECTED_KEY, selected);
        editor.apply();
    }

    public static boolean getSelected(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(SELECTED_KEY, false);
    }
}
