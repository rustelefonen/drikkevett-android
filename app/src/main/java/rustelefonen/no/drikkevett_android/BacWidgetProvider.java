package rustelefonen.no.drikkevett_android;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.List;

import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.tabs.dayAfter.BacDayAfterFragment;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.tabs.planParty.BacPlanPartyFragment;

/**
 * Created by simenfonnes on 21.07.2016.
 */

public class BacWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bac_widget);

            if (isRunning(context)) {
                views.setTextViewText(R.id.widget_header, "Kvelden er i gang!");
                views.setTextViewText(R.id.widget_bac, getCurrentBac(context) + "");

                Intent intent = new Intent(context, BacPlanPartyFragment.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);


            } else {
                views.setTextViewText(R.id.widget_header, "Du har ingen kveld i gang for øyeblikket!");
            }
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }

    }

    private boolean isRunning(Context context) {
        SuperDao superDao = new SuperDao(context);
        PlanPartyElementsDao planPartyElementsDao = superDao.getPlanPartyElementsDao();
        List<PlanPartyElements> partyList = planPartyElementsDao.queryBuilder().list();
        superDao.close();
        for (PlanPartyElements party : partyList) {
            if (party.getStatus().equals("RUNNING")) return true;
        }
        return false;
    }

    private double getCurrentBac(Context context) {
        return 0.0;
    }
}
