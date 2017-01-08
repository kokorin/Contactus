package contactus.event;

import contactus.model.Contact;
import contactus.repository.ContactRepository;

public class ContactListener implements EventListener<Contact>{
    private final ContactRepository repository;

    public ContactListener(ContactRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onEvent(Contact contact) {
        repository.save(contact);
    }

}
