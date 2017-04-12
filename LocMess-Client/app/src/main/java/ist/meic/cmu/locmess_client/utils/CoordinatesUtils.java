package ist.meic.cmu.locmess_client.utils;

import android.content.Context;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 12/04/2017.
 */

public class CoordinatesUtils {
    String mCoordinates;
    Context context;

    public CoordinatesUtils(Context context, String coordinates) {
        mCoordinates = coordinates;
        this.context = context;
    }

    public Coordinates parse() {
        String[] coordinates = mCoordinates.split("#");
        if (coordinates.length == 1) {
            return new WifiCoordinates(coordinates[0]);
        } else if (coordinates.length == 3) {
            return new GpsCoordinates(
                    Double.parseDouble(coordinates[0]),
                    Double.parseDouble(coordinates[1]),
                    Double.parseDouble(coordinates[2])
            );
        } else {
            throw new RuntimeException("Incorrectly formatted coordinates string");
        }
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
        public String ssid;

        private WifiCoordinates() {}

        private WifiCoordinates(String ssid) {
            this.ssid = ssid;
        }

        @Override
        public String toString() {
            return ssid;
        }
    }
}
