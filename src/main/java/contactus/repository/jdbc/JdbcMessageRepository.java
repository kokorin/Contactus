package contactus.repository.jdbc;

import contactus.model.Message;
import contactus.repository.MessageRepository;
import contactus.repository.jdbc.operation.Delete;
import contactus.repository.jdbc.operation.Merge;
import contactus.repository.jdbc.operation.Select;
import javafx.util.Pair;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

class JdbcMessageRepository implements MessageRepository {

    private final Connection connection;

    public JdbcMessageRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Message item) {
        saveAll(Collections.singletonList(item));
    }

    @Override
    public void saveAll(Collection<Message> items) {
        new Merge<Message>().setTableName("Message")
                .setItems(items)
                .setColumnNames(COLUMN_NAMES)
                .setGetValues(JdbcMessageRepository::getValues)
                .setIdField("id")
                .setGetId(Message::getId)
                .execute(connection);
    }

    @Override
    public void delete(int id) {
        new Delete().setTableName("Message")
                .setClause("id =" + id)
                .execute(connection);
    }

    @Override
    public Message load(int id) {
        return new Select<Message>().setTableName("Message")
                .setClause("id = " + id)
                .setParser(JdbcMessageRepository::parseMessage)
                .execute(connection)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public Set<Message> loadAll() {
        return new Select<Message>().setTableName("Message")
                .setParser(JdbcMessageRepository::parseMessage)
                .execute(connection);
    }

    @Override
    public Set<Message> loadAll(Integer contactId) {
        return loadAll(contactId, Instant.EPOCH);
    }

    @Override
    public Set<Message> loadAll(Integer contactId, Instant since) {
        return new Select<Message>().setTableName("Message")
                .setClause("contactId = " + contactId
                        + " AND date > " + since.toEpochMilli())
                .setParser(JdbcMessageRepository::parseMessage)
                .execute(connection);
    }

    @Override
    public Set<Message> loadLast() {
        String mostRecentClause = " id IN ("
                + "     SELECT lastId FROM ("
                + "         SELECT contactId, MAX(id) AS lastId"
                + "         FROM Message"
                + "         GROUP BY contactId"
                + "     )"
                + " )";

        return new Select<Message>().setTableName("Message")
                .setClause(mostRecentClause)
                .setParser(JdbcMessageRepository::parseMessage)
                .execute(connection);
    }

    @Override
    @SneakyThrows
    public Map<Integer, Integer> loadUnreadCount() {
        return new Select<Pair<Integer, Integer>>().setTableName("Message")
                .setFields("id", "COUNT(1)")
                .setGroupBy("id")
                .execute(connection)
                .stream()
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public void saveLastPts(int value) {

    }

    @Override
    public int loadLastPts() {
        return 1;
    }

    private static final List<String> COLUMN_NAMES = Arrays.asList(
            "id",
            "contactId",
            "randomId",
            "date",
            "direction",
            "important",
            "deleted",
            "unread",
            "title",
            "body"
    );

    private static List<Object> getValues(Message item) {
        return Arrays.asList(
                item.getId(),
                item.getContactId(),
                item.getRandomId(),
                item.getDate() != null ? item.getDate().toEpochMilli() : null,
                item.getDirection() != null ? item.getDirection().name() : null,
                item.isImportant(),
                item.isDeleted(),
                item.isUnread(),
                item.getTitle(),
                item.getBody()
        );
    }

    @SneakyThrows
    private static Message parseMessage(ResultSet resultSet) {
        return Message.builder()
                .id(resultSet.getInt("id"))
                .contactId(resultSet.getInt("contactId"))
                .randomId(resultSet.getInt("randomId"))
                .date(Instant.ofEpochMilli(resultSet.getLong("date")))
                .direction(Message.Direction.parse(resultSet.getString("direction")))
                .important(resultSet.getBoolean("important"))
                .deleted(resultSet.getBoolean("deleted"))
                .unread(resultSet.getBoolean("unread"))
                .title(resultSet.getString("title"))
                .body(resultSet.getString("body"))
                .build();

    }

    @SneakyThrows
    protected Pair<Integer, Integer> parseContactUnreadCount(ResultSet resultSet) {
        int contactId = resultSet.getInt("contactId");
        int count = resultSet.getInt("count");
        return new Pair<>(contactId, count);
    }
}
