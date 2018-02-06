package ist.meic.cmu.locmess.domain.location;

/**
 * Created by lads on 22/04/2017.
 */
public enum CoordinateType {
        GPS("GPS"),
        EMPTY("EMPTY"),
        WIFI("WIFI");


        private String type;

        CoordinateType(String type) {
            this.type = type;
        }

        public String type() {
            return type;
        }
}
