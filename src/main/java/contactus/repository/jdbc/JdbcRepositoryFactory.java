package contactus.repository.jdbc;

import contactus.repository.ContactGroupRepository;
import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import contactus.repository.RepositoryFactory;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class JdbcRepositoryFactory implements RepositoryFactory {
    private final int userId;
    private Connection connection = null;

    public JdbcRepositoryFactory(int userId) {
        this.userId = userId;
    }

    @Override
    public ContactRepository openContactRepository() {
        initialize();
        return new JdbcContactRepository(connection);
    }

    @Override
    public ContactGroupRepository openContactGroupRepository() {
        initialize();
        return new JdbcContactGroupRepository(connection);
    }

    @Override
    public MessageRepository openMessageRepository() {
        initialize();
        return new JdbcMessageRepository(connection);
    }

    @SneakyThrows
    private void initialize() {
        if (connection != null) {
            return;
        }

        //connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", "sa", "");
        //connection = DriverManager.getConnection("jdbc:h2:~/" + userId, "sa", "");
        connection = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        //connection = DriverManager.getConnection("jdbc:derby:memory:" + userId + ";create=true", "sa", "");

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS ContactGroup ("
                            + "id INT PRIMARY KEY,"
                            + "name VARCHAR(255)"
                            + ")"
            );
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS Contact ("
                            + "id INT PRIMARY KEY,"
                            + "name VARCHAR(255),"
                            + "surname VARCHAR(255),"
                            + "deactivated BOOLEAN,"
                            + "hidden BOOLEAN,"
                            + "sex VARCHAR(255),"
                            + "state VARCHAR(255),"
                            + "nick VARCHAR(255),"
                            + "avatarUrl VARCHAR(255),"
                            + "photo100 VARCHAR(255),"
                            + "online BOOLEAN"
                            + ")"
            );
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS Contact_ContactGroup ("
                            + "contactId INT,"
                            + "contactGroupId INT,"
                            + "FOREIGN KEY (contactId) REFERENCES Contact(id) ON DELETE CASCADE,"
                            + "FOREIGN KEY (contactGroupId) REFERENCES ContactGroup(id) ON DELETE CASCADE"
                            + ")"
            );
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS Message ("
                            + "id INT PRIMARY KEY,"
                            + "contactId INT,"
                            + "randomId INT,"
                            + "date BIGINT,"
                            + "direction VARCHAR(255),"
                            + "important BOOLEAN,"
                            + "deleted BOOLEAN,"
                            + "unread BOOLEAN,"
                            + "title VARCHAR(255),"
                            + "body VARCHAR(4096),"
                            + "FOREIGN KEY (contactId) REFERENCES Contact(id) ON DELETE CASCADE"
                            + ")"
            );
        }
    }
}
