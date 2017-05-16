package ist.meic.cmu.locmess_client.utils;

import android.content.Context;
import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 12/04/2017.
 */

public class CoordinatesUtils {
    String mCoordinates;
    Context context;

    private static final String SEPARATOR = "#";
    private static final String DELIMITER = ", ";
    private static final String GPS_TAG = "GPS";
    private static final String WIFI_TAG = "WIFI";

    public CoordinatesUtils(Context context, String coordinates) {
        mCoordinates = coordinates;
        this.context = context;
    }

    public Coordinates parse() {
        String[] coordinates = mCoordinates.split(SEPARATOR);
        if (WIFI_TAG.equals(coordinates[0])) {
            return new WifiCoordinates(Arrays.copyOfRange(coordinates, 1, coordinates.length));
        } else if (GPS_TAG.equals(coordinates[0])) {
            return new GpsCoordinates(
                    Double.parseDouble(coordinates[1]),
                    Double.parseDouble(coordinates[2]),
                    Double.parseDouble(coordinates[3])
            );
        } else {
            throw new RuntimeException("Incorrectly formatted coordinates string: "+mCoordinates);
        }
    }

    public static String formatGpsToDb(String latitude, String longitude, String radius) {
        return GPS_TAG + SEPARATOR + latitude + SEPARATOR + longitude + SEPARATOR + radius;
    }

    public static String formatGpsToDb(double latitude, double longitude, double radius) {
        return GPS_TAG + SEPARATOR + latitude + SEPARATOR + longitude + SEPARATOR + radius;
    }

    public static String formatWifiToDb(Iterable<String> ssids) {
        return WIFI_TAG + SEPARATOR + TextUtils.join(SEPARATOR, ssids);
    }

    public abstract class Coordinates {
        @Override
        public abstract String toString();
    }
    public class GpsCoordinates extends Coordinates {
        public double latitude;
        public double longitude;
        public double radius;

        private GpsCoordinates() {}

        private GpsCoordinates(double latitude, double longitude, double radius) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
        }

        @Override
        public String toString() {
            DecimalFormat df = new DecimalFormat("#.###");
            df.setRoundingMode(RoundingMode.CEILING);

            return context.getString(R.string.latitude_abrev) +
                    ": " +
                    df.format(latitude) +
                    ", " +
                    context.getString(R.string.longitude_abrev) +
                    ": " +
                    df.format(longitude) +
                    ", " +
                    context.getString(R.string.radius) +
                    ": " +
                    df.format(radius) +
                    context.getString(R.string.distance_unit_abrev);
        }
    }
    public class WifiCoordinates extends Coordinates {
        public List<String> ssids;

        private WifiCoordinates() {}

        private WifiCoordinates(String... ssids) {
            this.ssids = Arrays.asList(ssids);
        }

        @Override
        public String toString() {
            return TextUtils.join(DELIMITER, ssids);
        }
    }
}
