package contactus.repository.inmem;

import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import contactus.repository.RepositoryFactory;

public class InMemoryRepositoryFactory implements RepositoryFactory {
    public ContactRepository openContactRepository() {
        return new InMemoryContactRepository();
    }

    public MessageRepository openMessageRepository() {
        return new InMemoryMessageRepository();
    }
}
