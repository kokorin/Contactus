package contactus.repository.jdbc;

import contactus.model.Contact;
import contactus.repository.ContactRepository;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

class JdbcContactRepository extends JdbcRepository<Contact> implements ContactRepository {

    public JdbcContactRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected int getId(Contact item) {
        return item.getId();
    }

    @Override
    protected String getTableName() {
        return "Contact";
    }

    @Override
    protected List<String> getColumnNames() {
        return Arrays.asList(
                "id",
                "firstName",
                "lastName",
                "deactivated",
                "hidden",
                "sex",
                "state",
                "screenName",
                "photo50",
                "photo100",
                "online"
        );
    }

    @Override
    protected List<Object> getValues(Contact item) {
        return Arrays.asList(
                item.getId(),
                item.getFirstName(),
                item.getLastName(),
                item.isDeactivated(),
                item.isHidden(),
                convert(item.getSex(), Enum::name),
                convert(item.getState(), Enum::name),
                item.getScreenName(),
                item.getPhoto50(),
                item.getPhoto100(),
                item.isOnline()
        );
    }

    @SneakyThrows
    @Override
    protected Contact parseItem(ResultSet resultSet) {
        return Contact.builder()
                .id(resultSet.getInt("id"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .deactivated(resultSet.getBoolean("deactivated"))
                .hidden(resultSet.getBoolean("hidden"))
                .sex(Contact.Sex.parse(resultSet.getString("sex")))
                .state(Contact.State.parse(resultSet.getString("state")))
                .screenName(resultSet.getString("screenName"))
                .photo50(resultSet.getString("photo50"))
                .photo100(resultSet.getString("photo100"))
                .online(resultSet.getBoolean("online"))
                .build();
    }
}
