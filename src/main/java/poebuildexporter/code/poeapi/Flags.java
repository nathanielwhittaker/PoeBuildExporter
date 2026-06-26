package poebuildexporter.code.poeapi;

import java.io.Serializable;

public class Flags implements Serializable {

    private boolean unique;

    public Flags() {

    }

    public boolean getUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
}
