package contactus.repository;

public interface RepositoryFactory {
    ContactRepository openUserRepository();
    MessageRepository openMessageRepository();
}
