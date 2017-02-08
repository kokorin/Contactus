package contactus.repository.jdbc;

import contactus.model.ContactGroup;
import contactus.repository.ContactGroupRepository;
import contactus.repository.jdbc.operation.Delete;
import contactus.repository.jdbc.operation.Merge;
import contactus.repository.jdbc.operation.Select;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

class JdbcContactGroupRepository implements ContactGroupRepository {
    private final Connection connection;

    public JdbcContactGroupRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(ContactGroup item) {
        saveAll(Collections.singletonList(item));
    }

    @Override
    @SneakyThrows
    public void saveAll(Collection<ContactGroup> items) {
        boolean autocommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        new Merge<ContactGroup>().setTableName("ContactGroup")
                .setItems(items)
                .setIdField("id")
                .setGetId(ContactGroup::getId)
                .setColumnNames("id", "name")
                .setGetValues(group -> Arrays.asList(group.getId(), group.getName()))
                .execute(connection);
        connection.commit();

        connection.setAutoCommit(autocommit);
    }

    @Override
    @SneakyThrows
    public void delete(int id) {
        new Delete().setTableName("ContactGroup")
                .setClause("id = " + id)
                .execute(connection);
        connection.commit();
    }

    @Override
    public ContactGroup load(int id) {
        return new Select<ContactGroup>().setTableName("ContactGroup")
                .setClause("id = " + id)
                .setParser(JdbcContactGroupRepository::parseContactGroup)
                .execute(connection)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public Set<ContactGroup> loadAll() {
        return new Select<ContactGroup>().setTableName("ContactGroup")
                .setParser(JdbcContactGroupRepository::parseContactGroup)
                .execute(connection);
    }

    @SneakyThrows
    private static ContactGroup parseContactGroup(ResultSet resultSet) {
        return ContactGroup.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
