package org.jfrog.artifactory.client.aql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import static java.util.stream.Collectors.toMap;

import static org.jfrog.artifactory.client.aql.AqlInclude.buildWithElements;

public class AqlQueryBuilder {
    private AqlRootElement root = new AqlRootElement();
    private AqlItem sort;
    private AqlInclude include;
    private boolean isTransitive = false;

    public AqlQueryBuilder item(AqlItem item) {
        root.putAll(item.value());
        return this;
    }

    public AqlQueryBuilder elements(AqlItem... items) {
        if (isNotEmpty(items)) {
            root.putAll(Arrays.stream(items)
                .map(item -> item.value().entrySet())
                .flatMap(Collection::stream)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        return this;
    }

    public AqlQueryBuilder array(String key, AqlItem... items) {
        if (isNotEmpty(items)) {
            root.put(key, items);
        }
        return this;
    }

    public AqlQueryBuilder and(AqlItem... items) {
        if (isNotEmpty(items)) {
            root.putAll(AqlItem.and((Object[]) items).value());
        }
        return this;
    }

    public AqlQueryBuilder and(Collection<AqlItem> items) {
        return and(setToArray(items));
    }

    public AqlQueryBuilder or(AqlItem... items) {
        if (isNotEmpty(items)) {
            root.putAll(AqlItem.or((Object[]) items).value());
        }
        return this;
    }

    public AqlQueryBuilder or(Collection<AqlItem> items) {
        return or(setToArray(items));
    }

    public AqlQueryBuilder include(String... elements) {
        if (isNotEmpty(elements)) {
            include = buildWithElements(elements);
        }
        return this;
    }

    public AqlQueryBuilder asc(String... by) {
        if (isNotEmpty(by)) {
            this.sort = AqlItem.asc(by);
        }
        return this;
    }

    public AqlQueryBuilder desc(String... by) {
        if (isNotEmpty(by)) {
            this.sort = AqlItem.desc(by);
        }
        return this;
    }

    public AqlQueryBuilder transitive() {
        this.isTransitive = true;
        return this;
    }

    public String build() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String aql = "items.find(" + getRootAsString(mapper) + ")" + getIncludeAsString()
                + getSortAsString(mapper);
            if (this.isTransitive) {
                aql += ".transitive()";
            }
            return aql;
        } catch (JsonProcessingException e) {
            throw new AqlBuilderException("Error serializing object to json: ", e);
        }
    }

    private String getSortAsString(ObjectMapper mapper) throws JsonProcessingException {
        return hasSort() ? ".sort(" + mapper.writeValueAsString(sort) + ")" : "";
    }

    private String getIncludeAsString() {
        return hasInclude() ? include.toString() : "";
    }

    private String getRootAsString(ObjectMapper mapper) throws JsonProcessingException {
        return hasRoot() ? mapper.writeValueAsString(root) : "";
    }

    private boolean hasSort() {
        return sort != null && sort.isNotEmpty();
    }

    private boolean hasInclude() {
        return include != null && include.isNotEmpty();
    }

    private boolean hasRoot() {
        return root != null && root.isNotEmpty();
    }

    private AqlItem[] setToArray(Collection<AqlItem> items) {
        return items.toArray(new AqlItem[0]);
    }
}
