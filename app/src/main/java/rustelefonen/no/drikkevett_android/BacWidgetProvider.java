package rustelefonen.no.drikkevett_android;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rustelefonen.no.drikkevett_android.db.DayAfterBAC;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.PlanPartyElements;
import rustelefonen.no.drikkevett_android.db.PlanPartyElementsDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.intro.WelcomeActivity;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.PartyUtil;

import static rustelefonen.no.drikkevett_android.util.PartyUtil.calculateBAC;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.countingGrams;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.getDateDiff;
import static rustelefonen.no.drikkevett_android.util.PartyUtil.setGenderScore;

/**
 * Created by simenfonnes on 21.07.2016.
 */

public class BacWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews views;
            if (isRunning(context)) {
                views = new RemoteViews(context.getPackageName(), R.layout.active_widget_layout);

                views.setTextViewText(R.id.widget_planned_total, getPlannedUnits(context));
                views.setTextViewText(R.id.registered_widget, getRegisteredUnits(context));


                //get user
                views.setTextViewText(R.id.per_mille_widget, "");


                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                views.setOnClickPendingIntent(R.id.widget_complete, pendingIntent);

            } else {
                views = new RemoteViews(context.getPackageName(), R.layout.bac_widget);
                if (hasUser(context)) {
                    Intent intent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    views.setOnClickPendingIntent(R.id.start_evening_button, pendingIntent);
                } else {
                    Intent intent = new Intent(context, WelcomeActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    views.setOnClickPendingIntent(R.id.start_evening_button, pendingIntent);
                }
            }
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    private String getPlannedUnits(Context context) {
        SuperDao superDao = new SuperDao(context);
        PlanPartyElementsDao planPartyElementsDao = superDao.getPlanPartyElementsDao();
        List<PlanPartyElements> partyList = planPartyElementsDao.queryBuilder()
                .where(PlanPartyElementsDao.Properties.Status.eq("RUNNING")).list();
        superDao.close();

        if (partyList.size() > 0) {
            PlanPartyElements planPartyElements = partyList.get(0);
            if (planPartyElements != null) {
                int sum = planPartyElements.getPlannedBeer() + planPartyElements.getPlannedWine()
                        + planPartyElements.getPlannedDrink() + planPartyElements.getPlannedShot();
                return sum + "";
            }
        }
        return "";
    }

    private String getRegisteredUnits(Context context) {
        SuperDao superDao = new SuperDao(context);
        DayAfterBACDao dayAfterBACDao = superDao.getDayAfterBACDao();
        List<DayAfterBAC> dayAfterBACList = dayAfterBACDao.queryBuilder().list();

        int beer = 0;
        int wine = 0;
        int drink = 0;
        int shot = 0;

        for (DayAfterBAC dayAfterBAC : dayAfterBACList) {
            if (dayAfterBAC.getUnit().equals("Beer")) beer++;
            else if (dayAfterBAC.getUnit().equals("Wine")) wine++;
            else if (dayAfterBAC.getUnit().equals("Drink")) drink++;
            else if (dayAfterBAC.getUnit().equals("Shot")) shot++;
        }
        int sum = beer + wine + drink + shot;
        return sum + "";
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

    private User getUser(Context context) {
        SuperDao superDao = new SuperDao(context);
        UserDao userDao = superDao.getUserDao();
        List<User> users = userDao.queryBuilder().list();
        superDao.close();
        if (users.size() > 0) return users.get(0);
        else return null;
    }

    private boolean hasUser(Context context) {
        SuperDao superDao = new SuperDao(context);
        UserDao userDao = superDao.getUserDao();
        List<User> users = userDao.queryBuilder().list();
        superDao.close();
        if (users.size() > 0) return users.get(0) != null;
        else return false;
    }

    private String liveUpdatePromille(double weight, String gender, Date firstUnitAddedTimeStamp, Context context){
        SuperDao superDao = new SuperDao(context);
        double sum = 0.0;

        int b = 0;
        int w = 0;
        int d = 0;
        int s = 0;

        DayAfterBACDao dayAfterDao = superDao.getDayAfterBACDao();

        List<DayAfterBAC> dayAfterBACList = dayAfterDao.queryBuilder().list();

        for (DayAfterBAC dayAfter : dayAfterBACList) {
            if(dayAfter.getUnit().equals("Beer")){
                b++;
            }
            if(dayAfter.getUnit().equals("Wine")){
                w++;
            }
            if(dayAfter.getUnit().equals("Drink")){
                d++;
            }
            if(dayAfter.getUnit().equals("Shot")){
                s++;
            }
            double totalGrams = countingGrams(b, w, d, s);

            Date currentDate = new Date();
            long timeDifference = getDateDiff(firstUnitAddedTimeStamp, currentDate, TimeUnit.MINUTES);
            double newValueDouble = (double)timeDifference;
            double minToHours = newValueDouble / 60;

            // FROM 0 - 4 mins
            if(minToHours < 0.085){
                sum = 0;
            }
            // FROM 5 - 15 MIN
            if(minToHours > 0.085 && minToHours <= 0.25){
                sum = totalGrams/(weight * setGenderScore(gender)) - (PartyUtil.intervalCalc(minToHours) * minToHours);
            }
            if(minToHours > 0.25){
                sum = Double.parseDouble(calculateBAC(gender, weight, totalGrams, minToHours));
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return numberFormat.format(sum);
    }
}
