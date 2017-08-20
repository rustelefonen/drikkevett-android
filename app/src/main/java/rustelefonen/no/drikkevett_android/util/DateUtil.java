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

    public static String getMonthShortName(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        String monthString = "";
        switch (month) {
            case 0:
                monthString = "Jan";
                break;
            case 1:
                monthString = "Feb";
                break;
            case 2:
                monthString = "Mar";
                break;
            case 3:
                monthString = "Apr";
                break;
            case 4:
                monthString = "Mai";
                break;
            case 5:
                monthString = "Jun";
                break;
            case 6:
                monthString = "Jul";
                break;
            case 7:
                monthString = "Aug";
                break;
            case 8:
                monthString = "Sep";
                break;
            case 9:
                monthString = "Okt";
                break;
            case 10:
                monthString = "Nov";
                break;
            case 11:
                monthString = "Des";
                break;
        }
        return monthString;
    }

    public static String getYearOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR) + "";
    }

    public static String getMonthOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getMonthName(cal.get(Calendar.MONTH));
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

    public static Date setForgottenNewUnitDate(int minutes, Date startDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MINUTE, minutes);
        startDate = calendar.getTime();
        return startDate;
    }

    public static Date setEndOfSesStamp(int hours){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        date = calendar.getTime();
        return date;
    }

    public static String getNorwegianDayOfWeek(int day) {
        if (day == 1) return "Søndag";
        else if (day == 2) return "Mandag";
        else if (day == 3) return "Tirsdag";
        else if (day == 4) return "Onsdag";
        else if (day == 5) return "Torsdag";
        else if (day == 6) return "Fredag";
        else if (day == 7) return "Lørdag";
        return "";
    }

    public static String getHoursAndMinutes(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String tempTime = "";
        String tempMinute = "";
        String tempHour = "";

        if(calendar.get(Calendar.MINUTE) >= 0 && calendar.get(Calendar.MINUTE) <= 9){
            tempMinute = "0" + calendar.get(Calendar.MINUTE);
        } else {
            tempMinute = "" + calendar.get(Calendar.MINUTE);
        }
        if(calendar.get(Calendar.HOUR_OF_DAY) >= 0 && calendar.get(Calendar.HOUR_OF_DAY) <= 9){
            tempHour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            tempHour = "" + calendar.get(Calendar.HOUR_OF_DAY);
        }
        tempTime = tempHour + ":" + tempMinute;
        return tempTime;
    }

    public static String getTitle(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return DateUtil.getNorwegianDayOfWeek(weekDay) + " " + dayOfMonth + ". " + DateUtil.getMonthName(month) + " - " + year;
    }
}
