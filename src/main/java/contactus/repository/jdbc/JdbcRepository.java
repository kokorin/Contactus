package contactus.repository.jdbc;

import contactus.repository.Repository;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class JdbcRepository<T> implements Repository<T> {
    private final Connection connection;

    public JdbcRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(T item) {
        saveAll(Collections.singletonList(item));
    }

    @Override
    @SneakyThrows
    public void saveAll(Collection<T> items) {
        List<Integer> ids = items.stream()
                .map(this::getId)
                .collect(Collectors.toList());

        List<String> ins = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += 500) {
            int start = i;
            int end = Math.min(i + 500, ids.size());
            String idsString = String.join(",", ids.subList(start, end).stream().map(Object::toString).collect(Collectors.toList()));
            ins.add("(" + idsString + ")");
        }
        String existingIdQuery = "SELECT id FROM " + getTableName() + " WHERE id IN " + String.join(" OR IN ", ins);
        Set<Integer> existingIds = loadByQuery(existingIdQuery, this::parseId);

        Map<Boolean, List<T>> itemsByExistence = items.stream()
                .collect(Collectors.groupingBy(t -> existingIds.contains(getId(t))));
        List<T> toInsert = itemsByExistence.getOrDefault(false, Collections.emptyList());
        List<T> toUpdate = itemsByExistence.getOrDefault(true, Collections.emptyList());
        List<String> columnNames = getColumnNames();

        if (!toInsert.isEmpty()) {
            String insertQuery = "INSERT INTO " + getTableName()
                    + " (" + String.join(", ", columnNames) + ") "
                    + " VALUES(" + String.join(",", Collections.nCopies(columnNames.size(), "?")) + ")";

            System.out.println(insertQuery);

            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                for (T item : toInsert) {
                    List<Object> values = getValues(item);
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
            } catch (Exception e) {
                throw e;
            }
        }

        if (!toUpdate.isEmpty()) {
            //In such query column "id" will be mentioned twice:
            // UPDATE Table SET id = ?, ... WHERE id = ?
            String updateQuery = "UPDATE " + getTableName()
                    + " SET " + columnNames.stream()
                    .map(n -> n + " = ?").collect(Collectors.joining(", "))
                    + " WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                for (T item : toUpdate) {
                    List<Object> values = getValues(item);
                    for (int i = 0; i < values.size(); ++i) {
                        statement.setObject(i + 1, values.get(i));
                    }
                    statement.setObject(values.size(), getId(item));
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (Exception e) {
                throw e;
            }
        }
    }

    @Override
    @SneakyThrows
    public void delete(int id) {
        try (Statement statement = connection.createStatement()) {
            String query = "DELETE FROM " + getTableName() + " WHERE id = " + id;
            statement.executeUpdate(query);
            connection.commit();
        }
    }

    @Override
    @SneakyThrows
    public T load(int id) {
        return loadByQueryFirst("SELECT * FROM " + getTableName() + " WHERE id = " + id);
    }

    @Override
    @SneakyThrows
    public Set<T> loadAll() {
        return loadByQuery("SELECT * FROM " + getTableName());
    }

    protected T loadByQueryFirst(String query) {
        return loadByQueryFirst(query, this::parseItem);
    }

    @SneakyThrows
    protected <R> R loadByQueryFirst(String query, Function<ResultSet, R> parser) {
        Set<R> set = loadByQuery(query, parser);
        if (set == null || set.isEmpty()) {
            return null;
        }
        return set.iterator().next();
    }

    public Set<T> loadByQuery(String query) {
        return loadByQuery(query, this::parseItem);
    }

    @SneakyThrows
    public <R> Set<R> loadByQuery(String query, Function<ResultSet, R> parser) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                Set<R> result = new HashSet<>();
                while (resultSet.next()) {
                    result.add(parser.apply(resultSet));
                }
                return result;
            }
        }
    }

    protected abstract int getId(T item);

    protected abstract String getTableName();

    protected abstract List<String> getColumnNames();

    protected abstract List<Object> getValues(T item);

    protected abstract T parseItem(ResultSet resultSet);

    @SneakyThrows
    protected int parseId(ResultSet resultSet) {
        return resultSet.getInt(1);
    }

    protected Connection getConnection() {
        return connection;
    }

    protected static <T, R> R convert(T value, Function<T, R> converter) {
        return value == null ? null : converter.apply(value);
    }
}
