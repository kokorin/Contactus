package contactus.repository.jdbc.operation;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Update<T> {
    private List<T> items;
    private String tableName;
    private List<String> columnNames;
    private Function<T, List<Object>> getValues;
    private String idField;
    private Function<T, Integer> getId;

    public Update<T> setItems(List<T> items) {
        this.items = items;
        return this;
    }

    public Update<T> setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Update<T> setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public Update<T> setGetValues(Function<T, List<Object>> getValues) {
        this.getValues = getValues;
        return this;
    }

    public Update<T> setIdField(String idField) {
        this.idField = idField;
        return this;
    }

    public Update<T> setGetId(Function<T, Integer> getId) {
        this.getId = getId;
        return this;
    }

    @SneakyThrows
    public void execute(Connection connection) {
        if (items == null || items.isEmpty()) {
            return;
        }
        String updateQuery = "UPDATE " + tableName
                + " SET " + columnNames.stream()
                .map(n -> n + " = ?").collect(Collectors.joining(", "))
                + " WHERE " + idField + " = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            for (T item : items) {
                List<Object> values = getValues.apply(item);
                for (int i = 0; i < values.size(); ++i) {
                    statement.setObject(i + 1, values.get(i));
                }
                statement.setObject(values.size(), getId.apply(item));
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        }
    }
}
