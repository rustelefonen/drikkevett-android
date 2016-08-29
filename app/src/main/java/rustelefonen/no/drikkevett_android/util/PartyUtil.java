package rustelefonen.no.drikkevett_android.util;

import android.content.Context;
import android.graphics.Color;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by RUSTELEFONEN on 05.07.2016.
 */

public class PartyUtil {
    // SET GRAM VALUES:
    private static double beerGrams = 23.0;
    private static double wineGrams = 16.0;
    private static double drinkGrams = 16.0;
    private static double shotGrams = 16.0;

    private Context context;

    public PartyUtil(Context context) {
        this.context = context;
    }

    public static double intervalCalc2(double timeDifference, int totalUnits){
        double BACDownPerHour = 0.0;
        double minute = (1.0 / 60.0);

        // 1 MIN
        if(timeDifference >= 0.0 && timeDifference <= minute){
            BACDownPerHour = totalUnits * 0.01; // 50
        }
        // 2 MIN
        if(timeDifference > (minute) && timeDifference <= (minute * 2)){
            BACDownPerHour = totalUnits * 0.02; // 23.5
        }
        // 3 MIN
        if(timeDifference > (minute * 2) && timeDifference <= (minute * 3)){
            BACDownPerHour = totalUnits * 0.03; // 11.5
        }
        // 4 MIN
        if(timeDifference > (minute * 3) && timeDifference <= (minute * 4)){
            BACDownPerHour = totalUnits * 0.04; // 6.8
        }
        // 5 MIN
        if(timeDifference > (minute * 4) && timeDifference <= (minute * 5)){
            BACDownPerHour = totalUnits * 0.05; // 4.8
        }
        // 6 MIN
        if(timeDifference > (minute * 5) && timeDifference <= (minute * 6)){
            BACDownPerHour = totalUnits * 0.06; // 3.5
        }
        // 7 MIN
        if(timeDifference > (minute * 6) && timeDifference <= (minute * 7)){
            BACDownPerHour = totalUnits * 0.07; // 2.55
        }
        // 8 MIN
        if(timeDifference > (minute * 7) && timeDifference <= (minute * 8)){
            BACDownPerHour = totalUnits * 0.08; // 2.0
        }
        // 9 MIN
        if(timeDifference > (minute * 8) && timeDifference <= (minute * 9)){
            BACDownPerHour = totalUnits * 0.09; // 1.5
        }
        // 10 MIN
        if(timeDifference > (minute * 9) && timeDifference <= (minute * 10)){
            BACDownPerHour = totalUnits * 0.10; // 1.15
        }
        // 11 MIN
        if(timeDifference > (minute * 10) && timeDifference <= (minute * 11)){
            BACDownPerHour = totalUnits * 0.11; // 0.85
        }
        // 12 MIN
        if(timeDifference > (minute * 11) && timeDifference <= (minute * 12)){
            BACDownPerHour = totalUnits * 0.12; // 0.53
        }
        // 13 MIN
        if(timeDifference > (minute * 12) && timeDifference <= (minute * 13)){
            BACDownPerHour = totalUnits * 0.13; // 0.33
        }
        // 14 MIN
        if(timeDifference > (minute * 13) && timeDifference <= (minute * 14)){
            BACDownPerHour = totalUnits * 0.14; // 0.28
        }
        // 15 MIN
        if(timeDifference > (minute * 14) && timeDifference <= (minute * 15)){
            BACDownPerHour = totalUnits * 0.15; // 0.20
        }
        return BACDownPerHour;
    }

    public static double intervalCalc(double timeDifference){
        double BACDownPerHour = 0.0;
        double minute = 1 / 60;

        // 1 MIN
        if(timeDifference >= 0.0 && timeDifference <= minute){
            BACDownPerHour = 0.01; // 50
        }
        // 2 MIN
        if(timeDifference > (minute) && timeDifference <= (minute * 2)){
            BACDownPerHour = 0.02; // 23.5
        }
        // 3 MIN
        if(timeDifference > (minute * 2) && timeDifference <= (minute * 3)){
            BACDownPerHour = 0.03; // 11.5
        }
        // 4 MIN
        if(timeDifference > (minute * 3) && timeDifference <= (minute * 4)){
            BACDownPerHour = 0.04; // 6.8
        }
        // 5 MIN
        if(timeDifference > (minute * 4) && timeDifference <= (minute * 5)){
            BACDownPerHour = 0.05; // 4.8
        }
        // 6 MIN
        if(timeDifference > (minute * 5) && timeDifference <= (minute * 6)){
            BACDownPerHour = 0.06; // 3.5
        }
        // 7 MIN
        if(timeDifference > (minute * 6) && timeDifference <= (minute * 7)){
            BACDownPerHour = 0.07; // 2.55
        }
        // 8 MIN
        if(timeDifference > (minute * 7) && timeDifference <= (minute * 8)){
            BACDownPerHour = 0.08; // 2.0
        }
        // 9 MIN
        if(timeDifference > (minute * 8) && timeDifference <= (minute * 9)){
            BACDownPerHour = 0.09; // 1.5
        }
        // 10 MIN
        if(timeDifference > (minute * 9) && timeDifference <= (minute * 10)){
            BACDownPerHour = 0.09; // 1.15
        }
        // 11 MIN
        if(timeDifference > (minute * 10) && timeDifference <= (minute * 11)){
            BACDownPerHour = 0.10; // 0.85
        }
        // 12 MIN
        if(timeDifference > (minute * 11) && timeDifference <= (minute * 12)){
            BACDownPerHour = 0.11; // 0.53
        }
        // 13 MIN
        if(timeDifference > (minute * 12) && timeDifference <= (minute * 13)){
            BACDownPerHour = 0.12; // 0.33
        }
        // 14 MIN
        if(timeDifference > (minute * 13) && timeDifference <= (minute * 14)){
            BACDownPerHour = 0.13; // 0.28
        }
        // 15 MIN
        if(timeDifference > (minute * 14) && timeDifference <= (minute * 15)){
            BACDownPerHour = 0.14; // 0.20
        }
        return BACDownPerHour;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public static double setGenderScore(String gender){
        double genderScore = 0.0;

        if(gender.equals("Mann")){
            genderScore = 0.70;
        }
        if(gender.equals("Kvinne")){
            genderScore = 0.60;
        }

        return genderScore;
    }

    public static Date addMinsToDate(int minutes){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.MINUTE, minutes);

        date = calendar.getTime();
        return date;
    }

    public static double countingGrams(double beerUnits, double wineUnits, double drinkUnits, double shotUnits){
        return (beerUnits * beerGrams) + (wineUnits * wineGrams) + (drinkUnits * drinkGrams) + (shotUnits * shotGrams);
    }

    public static String calculateBAC(String gender, double weight, double grams, double hours) {
        double oppdatertPromille = 0.0;
        double genderScore = setGenderScore(gender);

        if(grams == 0.0){
            oppdatertPromille = 0.0;
        } else {
            oppdatertPromille = grams/(weight * genderScore) - (0.15 * hours);
            if(oppdatertPromille < 0.0){
                oppdatertPromille = 0.0;
            }
        }
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        return numberFormat.format(oppdatertPromille);
    }

    public String textQuote(double bac){
        String output = "";
        if(bac >= 0 && bac < 0.4){
            output = "Kos deg";
        }
        if(bac >= 0.4 && bac < 0.8){
            output = "Lykkepromille";
        }
        if(bac >= 0.8 && bac < 1.0){
            output = "Du blir mer kritikkløs og risikovillig";
        }
        if(bac >= 1.0 && bac < 1.2){
            output = "Balansen blir dårligere";
        }
        if(bac >= 1.2 && bac < 1.4){
            output = "Talen snøvlete og \nkontroll på bevegelser forverres";
        }
        if(bac >= 1.4 && bac < 1.8){
            output = "Man blir trøtt, sløv og \nkan bli kvalm";
        }
        if(bac >= 1.8 && bac < 3.0){
            output = "Hukommelsen sliter";
        }
        if(bac >= 3.0 && bac < 5.0){
            output = "Svært høy promille! \nMan kan bli bevisstløs";
        }
        if(bac >= 5.0){
            output = "Du kan dø ved en så høy promille!";
        }
        return output;
    }

    public int colorQuote(double bac){
        int color = 0;
        if(bac >= 0 && bac < 0.4){
            color = Color.rgb(255, 255, 255);
        }
        if(bac >= 0.4 && bac < 0.8){
            color = Color.rgb(26, 193, 73);
        }
        if(bac >= 0.8 && bac < 1.0){
            color = Color.rgb(255, 180, 10);
        }
        if(bac >= 1.0 && bac < 1.2){
            color = Color.rgb(255, 180, 10);
        }
        if(bac >= 1.2 && bac < 1.4){
            color = Color.rgb(255, 160, 0);
        }
        if(bac >= 1.4 && bac < 1.8){
            color = Color.rgb(255, 160, 0);
        }
        if(bac >= 1.8 && bac < 3.0){
            color = Color.rgb(255, 55, 55);
        }
        if(bac >= 3.0 && bac < 5.0){
            color = Color.rgb(255, 55, 55);
        }
        if(bac >= 5.0){
            color = Color.rgb(255, 0, 0);
        }
        return color;
    }
}
