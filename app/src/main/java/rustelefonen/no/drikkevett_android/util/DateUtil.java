package rustelefonen.no.drikkevett_android.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by simenfonnes on 07.07.2016.
 */

public class DateUtil {

    public static boolean dateIsWithin30Days(Date dateToTest) {
        Date thirtyDaysAgo = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(thirtyDaysAgo);
        cal.add(Calendar.DATE, -30);

        return cal.getTime().before(dateToTest);
    }

    public static String getMonthName(int month) {
        switch (month) {
            case 0:
                return "Januar";
            case 1:
                return "Februar";
            case 2:
                return "Mars";
            case 3:
                return "April";
            case 4:
                return "Mai";
            case 5:
                return "Juni";
            case 6:
                return "Juli";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "Oktober";
            case 10:
                return "November";
            case 11:
                return "Desember";
        }
        return "";
    }

    public static String getDateAsStringRepresentation(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH) + " " + getMonthName(cal.get(Calendar.MONTH));
    }

    public static String getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH) + "";
    }

    public static Date setNewUnitDate(int minutes){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        date = calendar.getTime();
        return date;
    }

    public static Date setEndOfSesStamp(int hours){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        date = calendar.getTime();
        return date;
    }
}
