package poebuildexporter.code.poeapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import poebuildexporter.code.ItemData.Stat;

public class PoETradeQuery {

    private List<Stat> stats;
    private String league;
    private String name;
    private String baseType;
    private String category;
    private String status = "online";
    private String priceSort = "asc";
    private final List<QueryStatType<?>> activeFilters = new ArrayList<>();
    private Map<QueryStatType.FilterSection, QueryStatType.FilterSection> sectionRemap = Map.of();

    public PoETradeQuery(String league) {
        this.league = league;
        this.stats = new ArrayList<>();
    }

    public PoETradeQuery filterAll(Map<QueryStat, ?> filters) {
        filters.forEach((stat, val) -> filter(stat, val));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> PoETradeQuery filter(QueryStat stat, T val) {
        // Copy metadata from the enum's template into a fresh instance so the
        // singleton enum constant is never mutated.
        QueryStatType<T> template = (QueryStatType<T>) stat.getType();
        QueryStatType<T> instance = new QueryStatType<>(
                template.getStat(),
                template.getTradeApiStatLabel(),
                template.getFilterSection(),
                template.getMinThreshold()) {};
        instance.setVal(val);
        activeFilters.add(instance);
        return this;
    }

    public PoETradeQuery name(String name) {
        this.name = name;
        return this;
    }

    public PoETradeQuery status(String status) {
        this.status = status;
        return this;
    }

    public PoETradeQuery baseType(String baseType) {
        this.baseType = baseType;
        return this;
    }

    public PoETradeQuery category(String category) {
        this.category = category;
        return this;
    }

    public PoETradeQuery sortPriceAsc() {
        this.priceSort = "asc";
        return this;
    }

    public PoETradeQuery sortPriceDesc() {
        this.priceSort = "desc";
        return this;
    }

    public PoETradeQuery sectionRemap(Map<QueryStatType.FilterSection, QueryStatType.FilterSection> remap) {
        this.sectionRemap = remap;
        return this;
    }

    public PoETradeQuery stats(List<Stat> stats) {
        this.stats.addAll(stats);
        return this;
    }

    public String toJson() {
        // Root: { "query": { ... }, "sort": { "price": "asc" } }
        JsonObjectBuilder queryObj = Json.createObjectBuilder()
            .add("status", Json.createObjectBuilder().add("option", status));

        if (name != null) {
            queryObj.add("name", name);
        }

        if (StringUtils.isNotBlank(baseType)) {
            queryObj.add("type", baseType);
        }

        // "stats": [ { "type": "and", "filters": [ { "id": "...", "value": { "min": 0.0 } } ] } ]
        JsonArrayBuilder statFiltersWrapper = Json.createArrayBuilder();
        if (CollectionUtils.isNotEmpty(stats)) {
            for (Stat s : stats) {
                if (s.getId() == null) continue;
                if (s.getAlternateId() != null) {
                    JsonArrayBuilder filters = Json.createArrayBuilder()
                        .add(Json.createObjectBuilder().add("id", s.getId())
                            .add("value", Json.createObjectBuilder().add("min", s.getRoll())))
                        .add(Json.createObjectBuilder().add("id", s.getAlternateId())
                            .add("value", Json.createObjectBuilder().add("min", s.getRoll())));
                    statFiltersWrapper.add(Json.createObjectBuilder()
                        .add("type", "count")
                        .add("value", Json.createObjectBuilder().add("min", 1))
                        .add("filters", filters));
                } else {
                    JsonObjectBuilder statEntry = Json.createObjectBuilder()
                        .add("id", s.getId())
                        .add("value", Json.createObjectBuilder().add("min", s.getRoll()));
                    statFiltersWrapper.add(Json.createObjectBuilder()
                        .add("type", "and")
                        .add("filters", Json.createArrayBuilder().add(statEntry)));
                }
            }
        }
        queryObj.add("stats", statFiltersWrapper);

        // Group active filters by their destination section (applying any version-specific remaps),
        // then iterate sections in order. Each section produces: "<section-key>": { "filters": { ... } }
        Map<QueryStatType.FilterSection, List<QueryStatType<?>>> bySection = activeFilters.stream()
            .collect(Collectors.groupingBy(
                st -> sectionRemap.getOrDefault(st.getFilterSection(), st.getFilterSection()),
                () -> new EnumMap<>(QueryStatType.FilterSection.class),
                Collectors.toList()));

        JsonObjectBuilder filterJson = Json.createObjectBuilder();

        for (QueryStatType.FilterSection section : QueryStatType.FilterSection.values()) {
            JsonObjectBuilder filtersInner = Json.createObjectBuilder();

            if (section == QueryStatType.FilterSection.TYPE_FILTERS && category != null) {
                filtersInner.add("category", Json.createObjectBuilder().add("option", category));
            }

            for (QueryStatType<?> st : bySection.getOrDefault(section, List.of())) {
                if (st.getVal() == null) continue;
                FilterSerializer s = ValSerializer.BY_TYPE.get(st.getVal().getClass());
                if (s != null) s.apply(filtersInner, st);
            }

            JsonObject filtersObj = filtersInner.build();
            if (!filtersObj.isEmpty()) {
                filterJson.add(section.getJsonKey(), Json.createObjectBuilder().add("filters", filtersObj));
            }
        }

        queryObj.add("filters", filterJson);

        return Json.createObjectBuilder()
            .add("query", queryObj)
            .add("sort", Json.createObjectBuilder().add("price", priceSort))
            .build().toString();
    }

    @FunctionalInterface
    private interface FilterSerializer {
        void apply(JsonObjectBuilder builder, QueryStatType<?> statType);
    }

    private enum ValSerializer implements FilterSerializer {
        STRING {
            @Override
            public void apply(JsonObjectBuilder builder, QueryStatType<?> statType) {
                if (statType.getVal() instanceof String s && StringUtils.isNotBlank(s))
                    builder.add(statType.getTradeApiStatLabel(), s);
            }
        },
        BOOLEAN {
            @Override
            public void apply(JsonObjectBuilder builder, QueryStatType<?> statType) {
                if (statType.getVal() instanceof Boolean b)
                    builder.add(statType.getTradeApiStatLabel(),
                        Json.createObjectBuilder().add("option", b));
            }
        },
        INTEGER {
            @Override
            public void apply(JsonObjectBuilder builder, QueryStatType<?> statType) {
                if (statType.getVal() instanceof Integer i && i >= statType.getMinThreshold())
                    builder.add(statType.getTradeApiStatLabel(),
                        Json.createObjectBuilder().add("min", i));
            }
        };

        static final Map<Class<?>, ValSerializer> BY_TYPE = Map.of(
            String.class,  STRING,
            Boolean.class, BOOLEAN,
            Integer.class, INTEGER
        );
    }
}
