package rustelefonen.no.drikkevett_android.util;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import rustelefonen.no.drikkevett_android.db.Information;
import rustelefonen.no.drikkevett_android.db.InformationDao;
import rustelefonen.no.drikkevett_android.information.InformationActivity;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 21.07.2016.
 */

public class NavigationUtil {

    private static final String CONTACT_INFORMATION_NAME = "Redusert prestasjonsevne";

    public static void navigateToContactInformation(Context context) {
        SuperDao superDao = new SuperDao(context);
        InformationDao informationDao = superDao.getInformationDao();
        List<Information> informationList = informationDao.queryBuilder()
                .where(InformationDao.Properties.Name.eq(CONTACT_INFORMATION_NAME)).list();
        superDao.close();

        Information information = informationList.get(0);
        if (information != null) {
            Intent intent = new Intent(context, InformationActivity.class);
            intent.putExtra(InformationActivity.ID, information);
            context.startActivity(intent);
        }
    }
}
