package rustelefonen.no.drikkevett_android.util;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import rustelefonen.no.drikkevett_android.ContactActivity;
import rustelefonen.no.drikkevett_android.db.Information;
import rustelefonen.no.drikkevett_android.db.InformationDao;
import rustelefonen.no.drikkevett_android.information.InformationActivity;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 21.07.2016.
 */

public class NavigationUtil {

    public static void navigateToContactInformation(Context context) {
        Intent intent = new Intent(context, ContactActivity.class);
        context.startActivity(intent);
    }
}
