package contactus.repository.jdbc;

import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import contactus.repository.RepositoryFactory;

public class JdbcRepositoryFactory implements RepositoryFactory {
    private final int userId;

    public JdbcRepositoryFactory(int userId) {
        this.userId = userId;
    }

    @Override
    public ContactRepository openContactRepository() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessageRepository openMessageRepository() {
        throw new UnsupportedOperationException();
    }
}
