package contactus.repository.inmem;

import contactus.repository.MessageRepository;
import contactus.repository.RepositoryFactory;
import contactus.repository.UserRepository;

public class InMemoryRepositoryFactory implements RepositoryFactory {
    private final int userId;

    public InMemoryRepositoryFactory(int userId) {
        this.userId = userId;
    }

    public UserRepository openUserRepository() {
        return new InMemoryUserRepository();
    }

    public MessageRepository openMessageRepository() {
        return new InMemoryMessageRepository();
    }
}
