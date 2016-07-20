package rustelefonen.no.drikkevett_android.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by RUSTELEFONEN on 05.07.2016.
 */

public class PartyUtil {
    // SET GRAM VALUES:
    private static double beerGrams = 12.6;
    private static double wineGrams = 14.0;
    private static double drinkGrams = 15.0;
    private static double shotGrams = 16.0;

    public static double intervalCalc(double timeDifference){
        double BACDownPerHour = 0.0;
        double minute = 1 / 60;

        // 1 MIN
        if(timeDifference >= 0.0 && timeDifference <= minute){
            BACDownPerHour = 50.0;
        }
        // 2 MIN
        if(timeDifference > (minute) && timeDifference <= (minute * 2)){
            BACDownPerHour = 23.5;
        }
        // 3 MIN
        if(timeDifference > (minute * 2) && timeDifference <= (minute * 3)){
            BACDownPerHour = 11.5;
        }
        // 4 MIN
        if(timeDifference > (minute * 3) && timeDifference <= (minute * 4)){
            BACDownPerHour = 6.8;
        }
        // 5 MIN
        if(timeDifference > (minute * 4) && timeDifference <= (minute * 5)){
            BACDownPerHour = 4.8;
        }
        // 6 MIN
        if(timeDifference > (minute * 5) && timeDifference <= (minute * 6)){
            BACDownPerHour = 3.5;
        }
        // 7 MIN
        if(timeDifference > (minute * 6) && timeDifference <= (minute * 7)){
            BACDownPerHour = 2.55;
        }
        // 8 MIN
        if(timeDifference > (minute * 7) && timeDifference <= (minute * 8)){
            BACDownPerHour = 2.0;
        }
        // 9 MIN
        if(timeDifference > (minute * 8) && timeDifference <= (minute * 9)){
            BACDownPerHour = 1.5;
        }
        // 10 MIN
        if(timeDifference > (minute * 9) && timeDifference <= (minute * 10)){
            BACDownPerHour = 1.15;
        }
        // 11 MIN
        if(timeDifference > (minute * 10) && timeDifference <= (minute * 11)){
            BACDownPerHour = 0.85;
        }
        // 12 MIN
        if(timeDifference > (minute * 11) && timeDifference <= (minute * 12)){
            BACDownPerHour = 0.53;
        }
        // 13 MIN
        if(timeDifference > (minute * 12) && timeDifference <= (minute * 13)){
            BACDownPerHour = 0.33;
        }
        // 14 MIN
        if(timeDifference > (minute * 13) && timeDifference <= (minute * 14)){
            BACDownPerHour = 0.28;
        }
        // 15 MIN
        if(timeDifference > (minute * 14) && timeDifference <= (minute * 15)){
            BACDownPerHour = 0.20;
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
}
