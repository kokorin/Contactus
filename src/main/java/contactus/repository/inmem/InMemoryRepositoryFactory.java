package contactus.repository.inmem;

import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import contactus.repository.RepositoryFactory;

public class InMemoryRepositoryFactory implements RepositoryFactory {
    private final int userId;

    public InMemoryRepositoryFactory(int userId) {
        this.userId = userId;
    }

    public ContactRepository openUserRepository() {
        return new InMemoryContactRepository();
    }

    public MessageRepository openMessageRepository() {
        return new InMemoryMessageRepository();
    }
}
