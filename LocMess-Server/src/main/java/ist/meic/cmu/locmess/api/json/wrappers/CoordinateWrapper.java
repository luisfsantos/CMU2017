package ist.meic.cmu.locmess.api.json.wrappers;

import ist.meic.cmu.locmess.domain.location.CoordinateType;

/**
 * Created by lads on 22/04/2017.
 */
public class CoordinateWrapper {
    CoordinateType type;

    public CoordinateWrapper() {
    }

    public CoordinateType getType() {
        return type;
    }

    public void setType(CoordinateType type) {
        this.type = type;
    }
}
