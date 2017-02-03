package contactus.repository;

public interface RepositoryFactory {
    ContactRepository openContactRepository();
    MessageRepository openMessageRepository();
}
