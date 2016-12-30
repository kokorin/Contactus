package contactus.repository;

public interface RepositoryFactory {
    UserRepository openUserRepository();
    MessageRepository openMessageRepository();
}
