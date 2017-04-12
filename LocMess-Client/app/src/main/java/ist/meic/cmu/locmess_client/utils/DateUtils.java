package ist.meic.cmu.locmess_client.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Catarina on 12/04/2017.
 */

public class DateUtils {

    public static String formatDate(Date date) {
        return SimpleDateFormat.getDateInstance().format(date);
    }

    public static Date parseDate(String date) {
        try {
            return SimpleDateFormat.getDateInstance().parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
