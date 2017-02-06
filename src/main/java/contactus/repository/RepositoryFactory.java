package contactus.repository;

public interface RepositoryFactory {
    ContactRepository openContactRepository();
    ContactGroupRepository openContactGroupRepository();
    MessageRepository openMessageRepository();
}
