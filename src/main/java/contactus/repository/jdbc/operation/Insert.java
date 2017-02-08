package contactus.repository.jdbc.operation;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Insert<T> {
    private List<T> items;
    private String tableName;
    private List<String> columnNames;
    private Function<T, List<Object>> getValues;

    public Insert<T> setItems(List<T> items) {
        this.items = items;
        return this;
    }

    public Insert<T> setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Insert<T> setColumnNames(String... columnNames) {
        this.columnNames = Arrays.asList(columnNames);
        return this;
    }

    public Insert<T> setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public Insert<T> setGetValues(Function<T, List<Object>> getValues) {
        this.getValues = getValues;
        return this;
    }

    @SneakyThrows
    public void execute(Connection connection) {
        if (!items.isEmpty()) {
            String insertQuery = "INSERT INTO " + tableName
                    + " (" + String.join(", ", columnNames) + ") "
                    + " VALUES(" + String.join(",", Collections.nCopies(columnNames.size(), "?")) + ")";

            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                for (T item : items) {
                    List<Object> values = getValues.apply(item);
                    for (int i = 0; i < values.size(); ++i) {
                        statement.setObject(i + 1, values.get(i));
                    }
                    System.out.println(values.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")));
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            }
        }

    }
}
