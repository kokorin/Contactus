package contactus.repository.jdbc.operation;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeleteItems<T> {
    private String tableName;
    private List<T> items;
    private List<String> columnNames;
    private Function<T, List<Object>> getValues;

    public DeleteItems<T> setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DeleteItems<T> setItems(List<T> items) {
        this.items = items;
        return this;
    }

    public DeleteItems<T> setColumnNames(String... columnNames) {
        this.columnNames = Arrays.asList(columnNames);
        return this;
    }

    public DeleteItems<T> setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public DeleteItems<T> setGetValues(Function<T, List<Object>> getValues) {
        this.getValues = getValues;
        return this;
    }

    @SneakyThrows
    public void execute(Connection connection) {
        if (items == null || items.isEmpty()) {
            return;
        }

        String deleteQuery = "DELETE FROM " + tableName
                + " WHERE " + columnNames.stream()
                .map(n -> n + " = ?").collect(Collectors.joining(" AND "));

        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            for (T item : items) {
                List<Object> values = getValues.apply(item);
                for (int i = 0; i < values.size(); ++i) {
                    statement.setObject(i + 1, values.get(i));
                }
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
