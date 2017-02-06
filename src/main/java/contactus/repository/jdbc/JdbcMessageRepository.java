package contactus.repository.jdbc;

import contactus.model.Message;
import contactus.repository.MessageRepository;
import javafx.util.Pair;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class JdbcMessageRepository extends JdbcRepository<Message> implements MessageRepository {

    public JdbcMessageRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected int getId(Message item) {
        return item.getId();
    }

    @Override
    protected String getTableName() {
        return "Message";
    }

    @Override
    protected List<String> getColumnNames() {
        return Arrays.asList(
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
    }

    @Override
    protected List<Object> getValues(Message item) {
        return Arrays.asList(
                item.getId(),
                item.getContactId(),
                item.getRandomId(),
                convert(item.getDate(), Instant::toEpochMilli),
                convert(item.getDirection(), Enum::name),
                item.isImportant(),
                item.isDeleted(),
                item.isUnread(),
                item.getTitle(),
                item.getBody()
        );
    }

    @Override
    public Set<Message> loadAll(Integer contactId) {
        return loadAll(contactId, Instant.EPOCH);
    }

    @Override
    public Set<Message> loadAll(Integer contactId, Instant since) {
        return loadByQuery(
                "SELECT * FROM " + getTableName()
                        + " WHERE contactId = " + contactId
                        + " AND date > " + since.toEpochMilli()
        );
    }

    @Override
    public Set<Message> loadLast() {
        return loadByQuery(
                "   SELECT * FROM " + getTableName()
                        + " WHERE id IN ("
                        + "     SELECT lastId FROM ("
                        + "         SELECT contactId, MAX(id) AS lastId"
                        + "         FROM " + getTableName()
                        + "         GROUP BY contactId"
                        + "     )"
                        + " )"
        );
    }

    @Override
    @SneakyThrows
    public Map<Integer, Integer> loadUnreadCount() {
        return loadByQuery(
                "SELECT id, COUNT(1) FROM " + getTableName()
                        + " WHERE unread "
                        + " GROUP BY id",
                this::parseContactUnreadCount)
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

    @Override
    @SneakyThrows
    protected Message parseItem(ResultSet resultSet) {
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
