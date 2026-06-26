package poebuildexporter.code.poeapi;

public class WeaponBaseStats {
    public final int physMin;
    public final int physMax;
    public final double aps;

    public WeaponBaseStats(int physMin, int physMax, double aps) {
        this.physMin = physMin;
        this.physMax = physMax;
        this.aps = aps;
    }
}
