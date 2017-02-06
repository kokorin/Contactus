package contactus.repository.jdbc;

import contactus.model.ContactGroup;
import contactus.repository.ContactGroupRepository;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

class JdbcContactGroupRepository extends JdbcRepository<ContactGroup> implements ContactGroupRepository {
    public JdbcContactGroupRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected int getId(ContactGroup item) {
        return item.getId();
    }

    @Override
    protected String getTableName() {
        return "ContactGroup";
    }

    @Override
    protected List<String> getColumnNames() {
        return Arrays.asList("id", "name");
    }

    @Override
    protected List<Object> getValues(ContactGroup item) {
        return Arrays.asList(item.getId(), item.getName());
    }

    @Override
    @SneakyThrows
    protected ContactGroup parseItem(ResultSet resultSet) {
        return ContactGroup.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
