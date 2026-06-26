package poebuildexporter.code.poeapi;

import java.util.List;

public interface ApiDeserializationProcess<T> {

    void deserializeIntoJavaObject(List<T> toAddTo, Entry e, Result r);
}
