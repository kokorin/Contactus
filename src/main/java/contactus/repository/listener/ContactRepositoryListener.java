package contactus.repository.listener;

import contactus.event.EventListener;
import contactus.model.Contact;
import contactus.repository.ContactRepository;

public class ContactRepositoryListener implements EventListener<Contact> {
    private final ContactRepository repository;

    public ContactRepositoryListener(ContactRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onEvent(Contact contact) {
        repository.save(contact);
    }

}
