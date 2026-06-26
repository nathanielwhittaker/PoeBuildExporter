package poebuildexporter.code.build;

import poebuildexporter.code.ItemData.Stat;

public enum StatMatchStrategy {
    EXACT    ((key, sfx, stat) -> stat.getText().equals(key)),
    ENDS_WITH((key, sfx, stat) -> sfx.length() >= 10 && stat.getText().endsWith(sfx)),
    CONTAINS ((key, sfx, stat) -> stat.getText().contains(key)
                                   && (double) key.length() / stat.getText().length() >= 0.6);

    private final StatMatchCondition condition;

    StatMatchStrategy(StatMatchCondition condition) {
        this.condition = condition;
    }

    public boolean test(String key, String sfx, Stat stat) {
        return condition.test(key, sfx, stat);
    }
}
