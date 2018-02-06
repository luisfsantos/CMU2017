package ist.meic.cmu.locmess.domain.location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by lads on 28/04/2017.
 */
@DatabaseTable
public class SSID {
    @DatabaseField(generatedId = true)
    long id;
    @DatabaseField
    String name;

    @DatabaseField(foreign = true)
    Coordinate coordinate;

    public SSID() {
    }

    public SSID(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public static SSID fromString(String name) {
        return new SSID(name);
    }

    public static Collection<SSID> fromStringCollection(Collection<String> ssids) {
        return ssids.stream().map(s -> SSID.fromString(s)).collect(Collectors.toList());
    }

    public static Collection<String> toStringCollection(Collection<SSID> wifiSSIDs) {
        return wifiSSIDs.stream().map(s -> SSID.toString(s)).collect(Collectors.toList());
    }

    private static String toString(SSID s) {
        return s.getName();
    }
}
