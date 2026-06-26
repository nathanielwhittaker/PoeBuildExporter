package poebuildexporter.code.build;

import poebuildexporter.code.ItemData.Stat;

@FunctionalInterface
public interface StatMatchCondition {
    boolean test(String searchKey, String suffix, Stat stat);
}
