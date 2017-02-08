package contactus.repository.jdbc.operation;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Select<T> {
    private String tableName;
    private List<String> fields;
    private String clause = null;
    private List<String> groupBy = null;
    private Function<ResultSet, T> parser;

    public Select<T> setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Select<T> setFields(String... fields) {
        this.fields = Arrays.asList(fields);
        return this;
    }

    public Select<T> setFields(List<String> fields) {
        this.fields = fields;
        return this;
    }

    public Select<T> setParser(Function<ResultSet, T> parser) {
        this.parser = parser;
        return this;
    }

    public Select<T> setClause(String clause) {
        this.clause = clause;
        return this;
    }

    public Select<T> setGroupBy(String... groupBy) {
        this.groupBy = Arrays.asList(groupBy);
        return this;
    }

    public Select<T> setGroupBy(List<String> groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    @SneakyThrows
    public Set<T> execute(Connection connection) {
        StringBuilder queryBuilder = new StringBuilder()
                .append("SELECT ");

        if (fields != null && !fields.isEmpty()) {
            queryBuilder.append(String.join(", ", fields));
        } else {
            queryBuilder.append("*");
        }

        queryBuilder.append(" FROM ")
                .append(tableName);

        if (clause != null && !clause.isEmpty()) {
            queryBuilder.append(" WHERE ")
                    .append(clause);
        }
        if (groupBy != null && !groupBy.isEmpty()) {
            queryBuilder.append(" GROUP BY ")
                    .append(String.join(",", groupBy));
        }

        String query = queryBuilder.toString();

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                Set<T> items = new HashSet<>();
                while (resultSet.next()) {
                    items.add(parser.apply(resultSet));
                }
                return items;
            }
        }
    }
}
