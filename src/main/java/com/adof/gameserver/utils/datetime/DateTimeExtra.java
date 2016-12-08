package com.adof.gameserver.utils.datetime;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mukthar on 21/11/16.
 */
public class DateTimeExtra {
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public int getTimeParams(String paramName) {
        Calendar rightNow = Calendar.getInstance();

        if (paramName.equalsIgnoreCase("minute")) {
            return rightNow.get(Calendar.MINUTE);
        }


//        else {
//            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
//
//            int seconds = rightNow.get(Calendar.SECOND);
//            int dateDay = rightNow.get(Calendar.DATE);
//
//
//            Calendar cal = Calendar.getInstance();
//            String dateString = dateFormat.format(cal.getTime());
//
//            System.out.println("Date=" + dateString +
//                    ", Hour=" + hour + ", Minute=" + minute + ", Minute=" + seconds);
//        }

        return 0;
    }


    public static String getCurrentDateTimeAsString(String outputFormat) {
        DateFormat dateFormat = new SimpleDateFormat(outputFormat);
        Calendar cal = Calendar.getInstance();
        String currentDateTime = dateFormat.format(cal);
        System.out.println(currentDateTime); //2016/11/16 12:08:43

        return currentDateTime;
    }


    private static java.sql.Date getCurrentDate() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Date(today.getTime());
    }

    public static java.sql.Timestamp getCurrentTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }

    public static void main(String[] args) {
        System.out.println(getCurrentTimeStamp());
    }
}
