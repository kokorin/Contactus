package contactus.repository.jdbc;

import contactus.model.Contact;
import contactus.repository.ContactRepository;
import contactus.repository.jdbc.operation.Delete;
import contactus.repository.jdbc.operation.Merge;
import contactus.repository.jdbc.operation.Select;
import contactus.repository.jdbc.operation.SetRelations;
import javafx.util.Pair;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Function;

class JdbcContactRepository implements ContactRepository {
    private final Connection connection;

    public JdbcContactRepository(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void save(Contact item) {
        saveAll(Collections.singletonList(item));
    }

    @Override
    @SneakyThrows
    public void saveAll(Collection<Contact> items) {
        boolean autocommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        new Merge<Contact>().setTableName("Contact")
                .setItems(items)
                .setIdField("id")
                .setGetId(Contact::getId)
                .setColumnNames(COLUMN_NAMES)
                .setGetValues(JdbcContactRepository::getValues)
                .execute(connection);

        SetRelations setRelations = new SetRelations().setTableName("Contact_ContactGroup")
                .setOwnerIdField("contactId")
                .setRelationIdField("contactGroupId");
        for (Contact contact : items) {
            setRelations.setRelation(contact.getId(), contact.getGroups());
        }
        setRelations.execute(connection);

        connection.commit();

        connection.setAutoCommit(autocommit);
    }

    @Override
    @SneakyThrows
    public void delete(int id) {
        new Delete().setTableName("Contact")
                .setClause("id = " + id)
                .execute(connection);
        connection.commit();
    }

    @Override
    @SneakyThrows
    public Contact load(int id) {
        Set<Pair<Integer, Integer>> relations = new Select<Pair<Integer, Integer>>()
                .setTableName("Contact_ContactGroup")
                .setClause("contactId = " + id)
                .setParser(JdbcContactRepository::parseContactGroupRelation)
                .execute(connection);

        return new Select<Contact>().setTableName("Contact")
                .setClause("id = " + id)
                .setParser(createContactParser(relations))
                .execute(connection)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public Set<Contact> loadAll() {
        Set<Pair<Integer, Integer>> relations = new Select<Pair<Integer, Integer>>()
                .setTableName("Contact_ContactGroup")
                .setParser(JdbcContactRepository::parseContactGroupRelation)
                .execute(connection);

        return new Select<Contact>().setTableName("Contact")
                .setParser(createContactParser(relations))
                .execute(connection);
    }

    private static final List<String> COLUMN_NAMES = Arrays.asList(
            "id",
            "name",
            "surname",
            "deactivated",
            "hidden",
            "sex",
            "state",
            "nick",
            "avatarUrl",
            "photo100",
            "online"
    );

    private static final List<Object> getValues(Contact item) {
        return Arrays.asList(
                item.getId(),
                item.getName(),
                item.getSurname(),
                item.isDeactivated(),
                item.isHidden(),
                item.getSex() != null ? item.getSex().name() : null,
                item.getState() != null ? item.getState().name() : null,
                item.getNick(),
                item.getAvatarUrl(),
                item.getPhoto100(),
                item.isOnline()
        );
    }

    private static Function<ResultSet, Contact> createContactParser(Set<Pair<Integer, Integer>> relations) {
        Map<Integer, List<Integer>> contactGroupMap = new HashMap<>();
        for (Pair<Integer, Integer> relation : relations) {
            contactGroupMap.computeIfAbsent(relation.getKey(), k -> new ArrayList<>())
                    .add(relation.getValue());
        }

        return new Function<ResultSet, Contact>() {
            @Override
            @SneakyThrows
            public Contact apply(ResultSet resultSet) {
                int contactId = resultSet.getInt("id");
                return Contact.builder()
                        .id(contactId)
                        .name(resultSet.getString("name"))
                        .surname(resultSet.getString("surname"))
                        .groups(contactGroupMap.getOrDefault(contactId, Collections.emptyList()))
                        .deactivated(resultSet.getBoolean("deactivated"))
                        .hidden(resultSet.getBoolean("hidden"))
                        .sex(Contact.Sex.parse(resultSet.getString("sex")))
                        .state(Contact.State.parse(resultSet.getString("state")))
                        .nick(resultSet.getString("nick"))
                        .avatarUrl(resultSet.getString("avatarUrl"))
                        .photo100(resultSet.getString("photo100"))
                        .online(resultSet.getBoolean("online"))
                        .build();
            }
        };
    }

    @SneakyThrows
    private static Pair<Integer, Integer> parseContactGroupRelation(ResultSet resultSet) {
        return new Pair<>(
                resultSet.getInt("contactId"),
                resultSet.getInt("contactGroupId")
        );
    }
}
