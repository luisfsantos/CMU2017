package ist.meic.cmu.locmess_client.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Catarina on 12/04/2017.
 */

public class DateUtils {

    public static String formatDate(Date date) {
        return SimpleDateFormat.getDateInstance().format(date);
    }

    public static String formatDate(Calendar calendar) {
        return SimpleDateFormat.getDateInstance().format(calendar.getTime());
    }

    public static String formatTime(Date date) {
        return SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    public static String formatTime(Calendar calendar) {
        return SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
    }
}
