package contactus.repository.jdbc.operation;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Merge<T> {
    private String tableName;
    private Collection<T> items;
    private String idField = "id";
    private List<String> columnNames;
    private Function<T, Integer> getId;
    private Function<T, List<Object>> getValues;

    public Merge<T> setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Merge<T> setItems(Collection<T> items) {
        this.items = items;
        return this;
    }

    public Merge<T> setIdField(String idField) {
        this.idField = idField;
        return this;
    }

    public Merge<T> setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public Merge<T> setColumnNames(String... columnNames) {
        this.columnNames = Arrays.asList(columnNames);
        return this;
    }

    public Merge<T> setGetId(Function<T, Integer> getId) {
        this.getId = getId;
        return this;
    }

    public Merge<T> setGetValues(Function<T, List<Object>> getValues) {
        this.getValues = getValues;
        return this;
    }

    public void execute(Connection connection) {
        List<Integer> ids = items.stream()
                .map(getId)
                .collect(Collectors.toList());

        String inclause = Util.createInClauseByIds(ids, idField);
        String existingIdQuery = "SELECT " + idField + " FROM " + tableName
                + " WHERE " + inclause;
        Set<Integer> existingIds = selectIdByQuery(connection, existingIdQuery);

        Map<Boolean, List<T>> itemsByExistence = items.stream()
                .collect(Collectors.groupingBy(t -> existingIds.contains(getId.apply(t))));
        List<T> toInsert = itemsByExistence.getOrDefault(false, Collections.emptyList());
        List<T> toUpdate = itemsByExistence.getOrDefault(true, Collections.emptyList());

        new Insert<T>().setItems(toInsert)
                .setTableName(tableName)
                .setColumnNames(columnNames)
                .setGetValues(getValues)
                .execute(connection);

        new Update<T>().setItems(toUpdate)
                .setTableName(tableName)
                .setColumnNames(columnNames)
                .setGetValues(getValues)
                .setIdField(idField)
                .setGetId(getId)
                .execute(connection);
    }


    @SneakyThrows
    protected Set<Integer> selectIdByQuery(Connection connection, String query) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                Set<Integer> ids = new HashSet<>();
                while (resultSet.next()) {
                    ids.add(resultSet.getInt(idField));
                }
                return ids;
            }
        }
    }
}
