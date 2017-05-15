package ist.meic.cmu.locmess_client.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ist.meic.cmu.locmess_client.network.request_builders.RequestBuilder;
import ist.meic.cmu.locmess_client.sql.LocMessDBContract;

/**
 * Created by Catarina on 12/04/2017.
 */

public class DateUtils {
    private DateUtils(){}

    public static String formatDate(Date date) {
        return SimpleDateFormat.getDateInstance().format(date);
    }

    public static String formatDate(Calendar calendar) {
        return SimpleDateFormat.getDateInstance().format(calendar.getTime());
    }

    public static String formatDateTime(Date date) {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(date);
    }

    public static String formatTime(Date date) {
        return SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    public static String formatTime(Calendar calendar) {
        return SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateTimeLocaleToDb(Calendar calendar) {
        return new SimpleDateFormat(LocMessDBContract.SIMPLE_DATE_FORMAT).format(calendar.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateTimeLocaleToDb(Date date) {
        return new SimpleDateFormat(LocMessDBContract.SIMPLE_DATE_FORMAT).format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateTimeDbToLocale(String dbDate) {
        Date localeDate = null;
        try {
            localeDate = new SimpleDateFormat(LocMessDBContract.SIMPLE_DATE_FORMAT).parse(dbDate);
        } catch (ParseException e) { e.printStackTrace(); }
        return formatDateTime(localeDate);
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateDbToLocale(String dbDate) {
        Date localeDate = null;
        try {
            localeDate = new SimpleDateFormat(LocMessDBContract.SIMPLE_DATE_FORMAT).parse(dbDate);
        } catch (ParseException e) { e.printStackTrace(); }
        return formatDate(localeDate);
    }

    public static String formatDateTimeISO8601(Calendar calendar) {
        return new SimpleDateFormat(RequestBuilder.DATE_FORMAT, Locale.getDefault()).format(calendar.getTime());
    }

    public static String formatDateTimeISO8601(Date date) {
        return new SimpleDateFormat(RequestBuilder.DATE_FORMAT, Locale.getDefault()).format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date parsetDateDbToLocale(String dbDate) {
        Date localeDate = null;
        try {
            localeDate = new SimpleDateFormat(LocMessDBContract.SIMPLE_DATE_FORMAT).parse(dbDate);
        } catch (ParseException e) { e.printStackTrace(); }
        return localeDate;
    }


}
